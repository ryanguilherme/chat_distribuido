package chat;

import java.net.*;
import protocolo.Protocolo;
import java.io.*;
import java.util.UUID;

import mensagem.Mensagem;

import com.google.gson.Gson;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatCliente {
	public static void main(String args[]) {
		Socket s = null;
		AtomicBoolean isRunning = new AtomicBoolean(true);
		try {
			int serverPort = 7879;
			s = new Socket("localhost", serverPort);
			System.out.println("Conexão estabelecida com o servidor");
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			Thread leituraThread = new Thread(() -> {
				try {
					while (isRunning.get()) {
						String mensagem = in.readUTF();
						System.out.println(mensagem);
					}
				} catch (IOException e) {
					isRunning.set(false);
				}
			});
			leituraThread.start();
			UUID uuid = UUID.randomUUID();
			String idCliente = uuid.toString();
			System.out.println("Bem vindo, usuário " + idCliente);
			int contadorMensagem = 0;
			String parametrosConexao[] = { "Tentando conectar" };
			Mensagem requisicaoConexao = new Mensagem(Protocolo.TIPO_REQUISICAO, Protocolo.OP_CONECTAR,
					contadorMensagem, idCliente, parametrosConexao);
			contadorMensagem++;
			Gson gson = new Gson();
			String requisicaoConexaoString = gson.toJson(requisicaoConexao);
			out.writeUTF(requisicaoConexaoString);
			System.out.println(
					"Escolha uma opção:\n1-Enviar mensagem em grupo\n2-Enviar mensagem individual\n3-Desconectar");
			while (true) {
				String msg = reader.readLine();
				if (msg.equals("1")) {
					System.out.println("Digite uma mensagem para o grupo");
					String conteudo = reader.readLine();
					String params[] = { conteudo };
					Mensagem msgGrupo = new Mensagem(Protocolo.TIPO_REQUISICAO, Protocolo.OP_MENSAGEM_GRUPO,
							contadorMensagem, idCliente, params);
					contadorMensagem++;
					String msgGrupoString = gson.toJson(msgGrupo);
					msgGrupoString+="\n";
					out.writeUTF(msgGrupoString);
				} else if (msg.equals("2")) {
					System.out.println("Informe o ID do usuário");
					String id = reader.readLine();
					System.out.println("Digite uma mensagem para esse usuário");
					String cnt = reader.readLine();
					String params[] = { id, cnt };
					Mensagem msgIndividual = new Mensagem(Protocolo.TIPO_REQUISICAO, Protocolo.OP_MENSAGEM_INDIVIDUAL,
							contadorMensagem, idCliente, params);
					contadorMensagem++;
					String msgIndividualString = gson.toJson(msgIndividual);
					msgIndividualString+="\n";
					out.writeUTF(msgIndividualString);
				} else if (msg.equals("3")) {
					String parameters[] = { "Tentando desconectar" };
					Mensagem desconectarRequisicao = new Mensagem(Protocolo.TIPO_REQUISICAO, Protocolo.OP_DESCONECTAR,
							contadorMensagem, idCliente, parameters);
					contadorMensagem++;
					String desconectarRequisicaoString = gson.toJson(desconectarRequisicao);
					desconectarRequisicaoString+="\n";
					out.writeUTF(desconectarRequisicaoString);
					break;
				}
			}
		} catch (IOException e) {
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException e) {
					System.out.println("close:" + e.getMessage());
				}
		}
	}
}