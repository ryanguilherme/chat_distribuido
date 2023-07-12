package servidor;

import protocolo.ProtocoloRequisicao;
import protocolo.ProtocoloResposta;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolStringList;

import mensagem.MensagemProtocolBuffers.Mensagem;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import redis.clients.jedis.*;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.io.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.CertificateException;

public class ServidorLogicaDadosMain {

	public static void main(String[] args) {
		ServerSocket listenSocket = null;
		Socket socket2 = null;
		int serverPort = 6780;

		try {
			// Configure the SSLContext
			SSLContext sslContext;
			try {
				sslContext = SSLContext.getInstance("TLS");
				TrustManagerFactory trustManagerFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				try {
					trustManagerFactory.init((KeyStore) null);
					TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
					sslContext.init(null, trustManagers, null);
				} catch (KeyStoreException e) {
					e.printStackTrace();
				}

			} catch (NoSuchAlgorithmException | KeyManagementException e) {
				e.printStackTrace();
				return;
			}

			// Connect to Redis server using Jedis
			BinaryJedis jedis = null;
			try {
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMinIdle(10);
                poolConfig.setMaxIdle(50);

    			// Configure the JedisPool connection using SSL/TLS
    			JedisPool jedisPool = new JedisPool(poolConfig, "172.25.240.241", 6379, 2000, 0, 0, null, 0, null, false,
    					sslContext.getSocketFactory(), null, null);
    			jedis = jedisPool.getResource();

				// Start SSL handshake with server
				socket2 = new Socket("localhost", 6770);

				// Server listening for client connections
				listenSocket = new ServerSocket(serverPort);

				while (true) {
					System.out.println("Waiting for client connection...");
					Socket socket = listenSocket.accept();
					System.out.println("Client connected.");

                    ThreadServidorLD threadCliente = new ThreadServidorLD(socket, socket2, jedis);
                    threadCliente.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		} finally {
			try {
				if (listenSocket != null) {
					listenSocket.close();
				}
				if (socket2 != null) {
					socket2.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ThreadServidorLD extends Thread {
	InputStream in;
	InputStream in2;
	OutputStream out2;
	Socket serverSocket;
	Socket clientSocket;
	String canalResposta = "resposta";
	BinaryJedis jedis;

	boolean running = true;

	public ThreadServidorLD(Socket serverSocket, Socket clientSocket, BinaryJedis jedis) {
		try {
			this.serverSocket = serverSocket;
			this.clientSocket = clientSocket;

			in = serverSocket.getInputStream();
			in2 = clientSocket.getInputStream();
			out2 = clientSocket.getOutputStream();
			this.jedis = jedis;
			running = true;
		} catch (IOException e) {
			System.out.println("Connection: " + e.getMessage());
		}
	}

	public void run() {
		try {
			while (running) {
				byte[] mensagemBytes = new byte[4096];
				int qtdbytes = in.read(mensagemBytes);

				// Create a new byte array with the exact size of the data read
				byte[] mensagem = Arrays.copyOf(mensagemBytes, qtdbytes);

				Mensagem mensagemConvertida = Mensagem.parseFrom(mensagem);

				ProtocolStringList parametrosList = mensagemConvertida.getParametrosList();
				String[] parametros = parametrosList.toArray(new String[parametrosList.size()]);
				List<String> parametrosLista = Arrays.asList(parametros);

				if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_CONECTAR
						|| mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_DESCONECTAR) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_MENSAGEM_INDIVIDUAL) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					Mensagem respostaConvertida = Mensagem.parseFrom(Arrays.copyOf(resposta, bytesRead));

					if (respostaConvertida.getCodOperacao() == ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_NOK) {
						jedis.publish(canalResposta.getBytes(), respostaConvertida.toByteArray());
					} else {
						Mensagem.Builder mensagemBuilder = Mensagem.newBuilder()
								.setTipo(ProtocoloResposta.TIPO_RESPOSTA)
								.setCodOperacao(ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_OK)
								.setIdMensagem(mensagemConvertida.getIdMensagem())
								.setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem());

						mensagemBuilder.addParametros(parametrosLista.get(1));
						mensagemBuilder.addParametros(parametrosLista.get(2));

						Mensagem mensagemIndividual = mensagemBuilder.build();
						jedis.publish(canalResposta.getBytes(), mensagemIndividual.toByteArray());

					}
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_MENSAGEM_GRUPO) {
					Mensagem.Builder mensagemGrupoBuilder = Mensagem.newBuilder()
							.setTipo(ProtocoloResposta.TIPO_RESPOSTA)
							.setCodOperacao(ProtocoloResposta.OP_MENSAGEM_GRUPO_OK)
							.setIdMensagem(mensagemConvertida.getIdMensagem())
							.setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem());

					mensagemGrupoBuilder.addParametros(parametrosLista.get(1));
					mensagemGrupoBuilder.addParametros(parametrosLista.get(2));

					Mensagem mensagemGrupo = mensagemGrupoBuilder.build();
					jedis.publish(canalResposta.getBytes(), mensagemGrupo.toByteArray());
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_OBTER_GRUPOS_PERTENCE) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_OBTER_GRUPOS_NAO_PERTENCE) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_ENTRAR_GRUPO) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_SAIR_GRUPO) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_LISTAR_USUARIOS_ONLINE) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_OBTER_STATUS_USUARIO) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				} else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_ALTERAR_STATUS_USUARIO) {
					out2.write(mensagemConvertida.toByteArray());
					out2.flush();
					byte[] resposta = new byte[4096];
					int bytesRead = in2.read(resposta);
					jedis.publish(canalResposta.getBytes(), Arrays.copyOf(resposta, bytesRead));
				}
			}

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} catch (IOException e) {
			running = false;
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
