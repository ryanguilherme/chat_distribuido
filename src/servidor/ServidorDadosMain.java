package servidor;

import protocolo.ProtocoloRequisicao;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import mensagem.MensagemProtocolBuffers.Mensagem;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class ServidorDadosMain {

	public static void main(String[] args) {
		int serverPort = 6770;
		ServerSocket listenSocket = null;
		ServidorDados servidorDados = null;
		try {

			listenSocket = new ServerSocket(serverPort);

			servidorDados = new ServidorDados();
			while (true) {
				System.out.println("Waiting for client connection...");
				Socket socket = listenSocket.accept();
				System.out.println("Client connected.");

				ThreadServidor threadCliente = new ThreadServidor(socket, servidorDados);
				threadCliente.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (listenSocket != null)
					listenSocket.close();
				if (servidorDados != null)
					servidorDados.FecharConexao();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ThreadServidor extends Thread {
	InputStream in;
	OutputStream out;
	Socket clientSocket;
	ServidorDados servidorDados;

	boolean running;

	public ThreadServidor(Socket clientSocket, ServidorDados servidorDados) {
		try {
			this.clientSocket = clientSocket;
			in = clientSocket.getInputStream();
			out = clientSocket.getOutputStream();
			this.servidorDados = servidorDados;
			running = true;
			System.out.println("Aqui");
		} catch (IOException e) {
			System.out.println("Connection: " + e.getMessage());
		}
	}

	public void run() {
		try {
			while (running) {
				// Read the data from the input stream into the byte array
				byte[] mensagemBytes = new byte[4096];
				int bytesRead = in.read(mensagemBytes);

				// Create a new byte array with the exact size of the data read
				byte[] mensagemData = Arrays.copyOf(mensagemBytes, bytesRead);

				// Now you can parse the message from the mensagemData array
				Mensagem mensagem = Mensagem.parseFrom(mensagemData);
				int codOperacao = mensagem.getCodOperacao();

				Mensagem resposta;

				if (codOperacao == ProtocoloRequisicao.OP_CADASTRAR_USUARIO
						|| codOperacao == ProtocoloRequisicao.OP_LOGAR_USUARIO
						|| codOperacao == ProtocoloRequisicao.OP_AUTENTICAR_USUARIO
						|| codOperacao == ProtocoloRequisicao.OP_MENSAGEM_GRUPO_AUTORIZAR
						|| codOperacao == ProtocoloRequisicao.OP_SAIR_GRUPO_AUTORIZAR
						|| codOperacao == ProtocoloRequisicao.OP_LISTAR_USUARIOS_ONLINE_AUTORIZAR) {
					resposta = servidorDados.tratarMensagemRecebidaServidorAutenticacaoAutorizacao(mensagem);
				} else {
					resposta = servidorDados.tratarMensagemRecebidaServidorLogicaNegocios(mensagem);
				}

				byte[] respostaBytes = resposta.toByteArray();
				out.write(respostaBytes);
				out.flush();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
