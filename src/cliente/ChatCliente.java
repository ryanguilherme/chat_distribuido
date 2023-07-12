package cliente;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Scanner;
import protocolo.ProtocoloRequisicao;
import protocolo.ProtocoloResposta;
import mensagem.MensagemProtocolBuffers.Mensagem;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolStringList;
import javax.crypto.KeyGenerator;
import java.util.Map;
import java.util.HashMap;

public class ChatCliente {

	private String canalRequisicao = "requisicao";
	private static int contadorMensagem = 0;
	private Scanner scanner;
	private BinaryJedis jedis;

	public ChatCliente(Scanner scanner, BinaryJedis jedis) {
		this.scanner = scanner;
		this.jedis = jedis;
	}

	public void exibirMenuLogin() {
		System.out.println("+----+-------------------+");
		System.out.println("| OP |      INICIO       |");
		System.out.println("+----+-------------------+");
		System.out.println("|  1 | CADASTRAR USUÁRIO |");
		System.out.println("|  2 | LOGIN             |");
		System.out.println("|  3 | SAIR              |");
		System.out.println("+----+-------------------+");
		System.out.print("ESCOLHA UMA OPÇÃO: ");

	}

	public void exibirMenuChat() {
		System.out.println("+----+---------------------------+");
		System.out.println("| OP |           MENU            |");
		System.out.println("+----+---------------------------+");
		System.out.println("|  1 | ACESSAR GRUPO             |");
		System.out.println("|  2 | MENSAGEM INDIVIDUAL       |");
		System.out.println("|  3 | MENSAGEM PARA GRUPO       |");
		System.out.println("|  4 | ENTRAR EM GRUPO           |");
		System.out.println("|  5 | SAIR DO GRUPO             |");
		System.out.println("|  6 | LISTAR USUÁRIOS ONLINE    |");
		System.out.println("|  7 | OBTER STATUS DE USUÁRIO   |");
		System.out.println("|  8 | ALTERAR STATUS DE USUÁRIO |");
		System.out.println("|  9 | SAIR                      |");
		System.out.println("+----+---------------------------+");
		System.out.print("ESCOLHA UMA OPÇÃO: ");
	}

	public void cadastrarUsuario(String nomeUsuario) {
		// Solicita nome de usuário e senha
		System.out.print("Nome de Usuário: ");
		String username = scanner.next();
		System.out.print("Senha: ");
		String password = scanner.next();
		System.out.print("Repita a Senha: ");
		String password2 = scanner.next();

		while (!password.equals(password2)) {
			System.out.println("Senhas diferentes! Tente novamente!");
			System.out.print("Senha: ");
			password = scanner.next();
			System.out.print("Repita a Senha: ");
			password2 = scanner.next();
		}

		String parametrosCadastro[] = { username.toLowerCase(), password };

		// Cria uma instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_CADASTRAR_USUARIO).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		for (String parametro : parametrosCadastro) {
			mensagemBuilder.addParametros(parametro);
		}

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem ao servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal

	}

	public void realizarLogin(String nomeUsuario) {
		// Solicita nome de usuário e senha
		System.out.print("Nome de Usuário: ");
		String username = scanner.next();
		System.out.print("Senha: ");
		String password = scanner.next();

		String[] parametrosLogin = { username.toLowerCase(), password };
		// Cria uma instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_LOGAR_USUARIO).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		for (String parametro : parametrosLogin) {
			mensagemBuilder.addParametros(parametro);
		}

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem ao servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal

	}

	public void obterGruposPertence(String nomeUsuario, String senhaUsuario) {

		String[] parametrosAcessarGrupo = { senhaUsuario };

		// Cria uma instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_OBTER_GRUPOS_PERTENCE).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		for (String parametro : parametrosAcessarGrupo) {
			mensagemBuilder.addParametros(parametro);
		}

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem criptografada ao servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal
	}

	public String acessarGrupo(String[] nomesDosGrupos) {

		System.out.println(" OP |            MENU              |");
		System.out.println("+----+------------------------------+");
		for (int i = 1; i <= nomesDosGrupos.length; i++) {
			System.out.printf("| %2d  | %-30s|\n", i, transformarNomeGrupo(nomesDosGrupos[--i]));
			i++;
		}
		System.out.println("+----+------------------------------+");

		System.out.print("Escolha qual grupo você quer acessar:");
		int opcao = scanner.nextInt();

		if (opcao >= 1 && opcao <= nomesDosGrupos.length) {
			return transformarNomeGrupo(nomesDosGrupos[--opcao]);
		} else {
			System.out.println("Opção Inválida! Certifique-se de selecionar um grupo válido");
			return null;
		}

	}

	public void conectar(String nomeUsuario, String senha) {
		System.out.println("Tentando conectar");

		// Cria uma instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_CONECTAR).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		mensagemBuilder.addParametros(senha);

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem criptografada ao servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal

	}

	public void enviarMensagemIndividual(String nomeUsuario, String senha) {

		// Solicitando nome de usuário de destino e mensagem
		System.out.println("Digite o nome de usuário do destinatário:");
		String nomeUsuarioDestino = scanner.next();
		scanner.nextLine(); // limpando o buffer do scanner
		System.out.println("Digite a mensagem:");
		String mensagemIndividual = scanner.nextLine();

		// Cria uma instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_MENSAGEM_INDIVIDUAL).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		mensagemBuilder.addParametros(senha);
		mensagemBuilder.addParametros(nomeUsuarioDestino);
		mensagemBuilder.addParametros(mensagemIndividual);

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem para o servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal

	}

	public boolean enviarMensagemParaGrupo(String nomeUsuario, String senha, String grupoAtual) {

		if (grupoAtual == null) {
			System.out.println("Você precisa acessar um grupo primeiro");
			return true;
		} else {
			// Solicitando a mensagem
			System.out.println("Digite a mensagem para o grupo:");
			String mensagemGrupo = scanner.nextLine();

			// Parâmetros da mensagem em grupo
			String[] parametrosMensagemGrupo = { senha, grupoAtual, mensagemGrupo };

			// Cria uma instância da mensagem Mensagem
			Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
					.setCodOperacao(ProtocoloRequisicao.OP_MENSAGEM_GRUPO).setIdMensagem(contadorMensagem++)
					.setNomeUsuarioOrigem(nomeUsuario);

			mensagemBuilder.addParametros(senha);
			mensagemBuilder.addParametros(grupoAtual);
			mensagemBuilder.addParametros(mensagemGrupo);

			Mensagem mensagem = mensagemBuilder.build();

			// Serializa a mensagem em bytes
			byte[] mensagemSerializada = mensagem.toByteArray();

			// Envia a mensagem para o servidor
			jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal

			return false;
		}
	}

	public void obterGruposNaoPertence(String nomeUsuario, String senhaUsuario) {

		String[] parametrosObterGruposNaoPertence = { senhaUsuario };

		// Cria uma instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_OBTER_GRUPOS_NAO_PERTENCE).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		mensagemBuilder.addParametros(senhaUsuario);

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem ao servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal

	}

	public static String transformarNomeGrupo(String nome) {
		String nomeTransformado = nome.replace("_", " ");
		nomeTransformado = nomeTransformado.substring(0, 1).toUpperCase() + nomeTransformado.substring(1);
		return nomeTransformado;
	}

	public boolean entrarEmGrupo(String nomeUsuario, String senhaUsuario, String[] nomesDosGrupos) {

		if (nomesDosGrupos.length == 0) {
			System.out.println("Você já está em todos os grupos");
			return true;
		} else {

			System.out.println("+----+------------------------------+");
			System.out.println("| OP |            MENU              |");
			System.out.println("+----+------------------------------+");

			for (int i = 1; i <= nomesDosGrupos.length; i++) {
			    System.out.printf("| %2d | %-28s |\n", i, transformarNomeGrupo(nomesDosGrupos[i-1]));
			    System.out.println("+----+------------------------------+");
			}
			
			System.out.print("Escolha em qual grupo você quer entrar:");
			int opcao = scanner.nextInt();

			if (opcao >= 1 && opcao <= nomesDosGrupos.length) {
				String grupoSelecionado = nomesDosGrupos[--opcao];

				String[] parametrosEntrarGrupo = { senhaUsuario,
						grupoSelecionado.toLowerCase().replaceAll("\\s+", "_") };

				// Cria uma instância da mensagem Mensagem
				Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
						.setCodOperacao(ProtocoloRequisicao.OP_ENTRAR_GRUPO).setIdMensagem(contadorMensagem++)
						.setNomeUsuarioOrigem(nomeUsuario);

				mensagemBuilder.addParametros(senhaUsuario);
				mensagemBuilder.addParametros(grupoSelecionado.toLowerCase().replaceAll("\\s+", "_"));

				Mensagem mensagem = mensagemBuilder.build();

				// Serializa a mensagem em bytes
				byte[] mensagemSerializada = mensagem.toByteArray();

				jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal
				return false;
			} else {
				System.out.println("Opção Inválida! Certifique-se de selecionar um grupo válido");
				return true;

			}
		}
	}

	public boolean sairDoGrupo(String nomeUsuario, String senhaUsuario, String[] nomesDosGrupos) {

		if (nomesDosGrupos.length == 0) {
			System.out.println("Você não está em nenhum grupo");
			return true;
		} else {
			System.out.println("+----+------------------------------+");
			System.out.println("| OP |            MENU              |");
			System.out.println("+----+------------------------------+");

			for (int i = 0; i < nomesDosGrupos.length; i++) {
			    System.out.printf("| %2d | %-28s |\n", (i + 1), transformarNomeGrupo(nomesDosGrupos[i]));
			    System.out.println("+----+------------------------------+");
			}

			System.out.print("Escolha de qual grupo você quer sair:");
			int opcao = scanner.nextInt();

			if (opcao >= 1 && opcao <= nomesDosGrupos.length) {
				String grupoSelecionado = nomesDosGrupos[--opcao];

				String[] parametrosSairGrupo = { senhaUsuario, grupoSelecionado.toLowerCase().replaceAll("\\s+", "_") };

				// Cria uma instância da mensagem Mensagem
				Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
						.setCodOperacao(ProtocoloRequisicao.OP_SAIR_GRUPO).setIdMensagem(contadorMensagem++)
						.setNomeUsuarioOrigem(nomeUsuario);

				mensagemBuilder.addParametros(senhaUsuario);
				mensagemBuilder.addParametros(grupoSelecionado.toLowerCase().replaceAll("\\s+", "_"));

				Mensagem mensagem = mensagemBuilder.build();

				// Serializa a mensagem em bytes
				byte[] mensagemSerializada = mensagem.toByteArray();

				jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal
				return false;
			} else {
				System.out.println("Opção Inválida! Certifique-se de selecionar um grupo válido");
				return true;
			}
		}
	}

	public boolean listarUsuariosOnline(String nomeUsuario, String senhaUsuario, String grupoAtual) {

		if (grupoAtual == null) {
			System.out.println("Você precisa acessar um grupo primeiro");
			return true;
		} else {
			// Cria uma instância da mensagem Mensagem
			Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
					.setCodOperacao(ProtocoloRequisicao.OP_LISTAR_USUARIOS_ONLINE).setIdMensagem(contadorMensagem++)
					.setNomeUsuarioOrigem(nomeUsuario);

			mensagemBuilder.addParametros(senhaUsuario);
			mensagemBuilder.addParametros(grupoAtual);

			Mensagem mensagem = mensagemBuilder.build();

			// Serializa a mensagem em bytes
			byte[] mensagemSerializada = mensagem.toByteArray();

			// Envia a mensagem ao servidor
			jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal
			return false;
		}
	}

	public void obterStatusDoUsuario(String nomeUsuario, String senhaUsuario) {
		System.out.print("Digite o Nome do Usuário:");
		String nomeUsuarioDestino = scanner.next().toLowerCase();

		String[] parametrosObterStatusDoUsuario = { senhaUsuario, nomeUsuarioDestino };
		// Cria uma instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_OBTER_STATUS_USUARIO).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		mensagemBuilder.addParametros(senhaUsuario);
		mensagemBuilder.addParametros(nomeUsuarioDestino);

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem ao servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal
	}

	public boolean alterarStatusDoUsuario(String nomeUsuario, String senhaUsuario, String estado) {

		String novoEstado;
		if (estado.equals("online")) {
			System.out.println("Você quer alterar seu status para ocupado?s/n");
			novoEstado = "ocupado";
		} else {
			System.out.println("Você quer alterar seu status para online?s/n");
			novoEstado = "online";
		}

		String resposta = scanner.next();

		if (resposta.toLowerCase().contains("s")) {
			String[] parametrosAlterarStatusUsuario = { senhaUsuario, novoEstado };

			// Cria uma instância da mensagem Mensagem
			Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
					.setCodOperacao(ProtocoloRequisicao.OP_ALTERAR_STATUS_USUARIO).setIdMensagem(contadorMensagem++)
					.setNomeUsuarioOrigem(nomeUsuario);

			mensagemBuilder.addParametros(senhaUsuario);
			mensagemBuilder.addParametros(novoEstado);

			Mensagem mensagem = mensagemBuilder.build();

			// Serializa a mensagem em bytes
			byte[] mensagemSerializada = mensagem.toByteArray();

			// Envia a mensagem ao servidor
			jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal
			return false;
		}
		return true;
	}

	public void desconectar(String nomeUsuario, String senhaUsuario) {

		// Criando a instância da mensagem Mensagem
		Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
				.setCodOperacao(ProtocoloRequisicao.OP_DESCONECTAR).setIdMensagem(contadorMensagem++)
				.setNomeUsuarioOrigem(nomeUsuario);

		mensagemBuilder.addParametros(senhaUsuario);

		Mensagem mensagem = mensagemBuilder.build();

		// Serializa a mensagem em bytes
		byte[] mensagemSerializada = mensagem.toByteArray();

		// Envia a mensagem criptografada ao servidor
		jedis.publish(canalRequisicao.getBytes(), mensagemSerializada); // Publicando uma mensagem no canal
	}

}
