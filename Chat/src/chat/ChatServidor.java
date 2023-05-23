package chat;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;

import mensagem.Mensagem;
import protocolo.Protocolo;
import protocolo.ProtocoloServidor;

public class ChatServidor {
	public static void main(String args[]) {
		ArrayList<ThreadServidor> listaClientes = new ArrayList<ThreadServidor>();
		ServerSocket listenSocket = null;
		try {
			int serverPort = 7879;
			listenSocket = new ServerSocket(serverPort);
			System.out.println("Servidor esperando na porta " + serverPort);
			while (true) {
				Socket clientSocket = listenSocket.accept();
				ThreadServidor threadCliente = new ThreadServidor(clientSocket, listaClientes);
				listaClientes.add(threadCliente);
			}
		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		} finally {
			try {
				listenSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class ThreadServidor extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	ArrayList<ThreadServidor> listaClientes;
	String idCliente;
	boolean running;

	public ThreadServidor(Socket ClientSocket, ArrayList<ThreadServidor> listaClientes) {
		try {
			this.clientSocket = ClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.listaClientes = listaClientes;
			running = true;
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	public void run() {
		try {
			int count = 0;
			ProtocoloServidor protocoloServidor = new ProtocoloServidor();
			while (running) {
				String mensagem = in.readUTF();
				Gson gson = new Gson();
				Mensagem requisicaoRecebida = gson.fromJson(mensagem, Mensagem.class);
				this.setIdCliente(requisicaoRecebida.getOrigem());
				if (count == 0) {
					System.out.println("Conexão estabelecida com o cliente " + requisicaoRecebida.getOrigem());
					count++;
				}
				System.out.println(mensagem);
				Mensagem resposta = protocoloServidor.tratarMensagemRecebida(requisicaoRecebida);

				String resp = gson.toJson(resposta);
				if (resposta.getCodOperacao() == Protocolo.OP_CONECTAR) {
					for (ThreadServidor threadCliente : listaClientes) {
						threadCliente.escreverMensagem(resp);
					}
					for (ThreadServidor threadCliente : listaClientes) {
						if (threadCliente.getIdCliente().equals(requisicaoRecebida.getOrigem())) {
							threadCliente.escreverMensagem("Usuários no chat:");
							for (ThreadServidor threadC : listaClientes) {
								threadCliente.escreverMensagem(threadC.getIdCliente());
							}

						}
					}
				} else if (resposta.getCodOperacao() == Protocolo.OP_DESCONECTAR) {
					for (ThreadServidor threadCliente : listaClientes) {
						threadCliente.escreverMensagem(resp);
					}
					running = false;
					for (ThreadServidor threadCliente : listaClientes) {
						if (threadCliente.getIdCliente().equals(requisicaoRecebida.getOrigem())) {
							listaClientes.remove(threadCliente);
						}
					}
				}

				else if (resposta.getCodOperacao() == Protocolo.OP_MENSAGEM_GRUPO) {
					for (ThreadServidor threadCliente : listaClientes) {
						threadCliente.escreverMensagem(resp);
					}
				} else if (resposta.getCodOperacao() == Protocolo.OP_MENSAGEM_INDIVIDUAL) {
					int cnt = 0;
					for (ThreadServidor threadCl : listaClientes) {
						if (threadCl.getIdCliente().equals(requisicaoRecebida.getParametros()[0])) {
							cnt++;
							break;
						}
					}
					for (ThreadServidor threadCliente : listaClientes) {
						if (threadCliente.getIdCliente().equals(requisicaoRecebida.getOrigem())
								|| threadCliente.getIdCliente().equals(requisicaoRecebida.getParametros()[0])) {
							if (cnt == 1)
								threadCliente.escreverMensagem(resp);
							else {
								if (threadCliente.getIdCliente().equals(requisicaoRecebida.getOrigem())) {
									String p[] = { "ID inválido" };
									Mensagem erro = new Mensagem(Protocolo.TIPO_RESPOSTA,
											Protocolo.OP_MENSAGEM_INDIVIDUAL_NOK, requisicaoRecebida.getIdMensagem(),
											requisicaoRecebida.getOrigem(), p);
									String erroString = gson.toJson(erro);
									threadCliente.escreverMensagem(erroString);
								}
							}
						}
					}

				}
			}
		}

		catch (IOException e) {
			running = false;
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void escreverMensagem(String msg) {
		try {
			out.writeUTF(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}