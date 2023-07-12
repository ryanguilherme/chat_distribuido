package cliente;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

import protocolo.ProtocoloRequisicao;
import protocolo.ProtocoloResposta;
import mensagem.MensagemProtocolBuffers.Mensagem;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import protocolo.ProtocoloResposta;
import mensagem.MensagemProtocolBuffers.Mensagem;
import com.google.protobuf.ProtocolStringList;

import com.google.protobuf.ProtocolStringList;

public class ChatClienteMain {

	private static String canalResposta = "resposta";
	static UUID uuid = UUID.randomUUID();

	private String nomeUsuario = uuid.toString().replace("-", "");;
	private String senhaUsuario;
	private String grupoAtual = null;
	private String estado = "offline";
	private boolean sairGrupo = false;
	private boolean executando = true;

	// Add a Semaphore for synchronization
	private Object messageLock = new Object();

	public static void main(String[] args) {
		ChatClienteMain chatClienteMain = new ChatClienteMain();
		chatClienteMain.run();
	}

	public void run() {

		Scanner scanner = new Scanner(System.in);
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			try {
				trustManagerFactory.init((KeyStore) null);
				TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
				sslContext.init(null, trustManagers, null);
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
			scanner.close();
			return;
		}

		// Create a custom JedisPoolConfig
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMinIdle(10);
		poolConfig.setMaxIdle(50);

		// Configure JedisPool with SSL/TLS
		JedisPool jedisPool = new JedisPool(poolConfig, "172.25.240.241", 6379, 2000, 0, 0, null, 0, null, false,
				sslContext.getSocketFactory(), null, null);

		BinaryJedis jedis = jedisPool.getResource();
		BinaryJedis jedis2 = jedisPool.getResource();

		ChatCliente chatCliente = new ChatCliente(scanner, jedis2);

		// Implementando o JedisPubSub para lidar com as mensagens recebidas
		BinaryJedisPubSub jedisPubSub = new BinaryJedisPubSub() {

			@Override
			public void onMessage(byte[] channel, byte[] message) {

				if (message != null && message.length > 0) {
					boolean desbloquear = true;
					byte[] mensagemSerializada = message;

					// Descriptografando a mensagem
					try {
						Mensagem mensagemDesserializada = Mensagem.parseFrom(mensagemSerializada);
						if ((mensagemDesserializada.getNomeUsuarioOrigem().equals(nomeUsuario) && mensagemDesserializada
								.getCodOperacao() != ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_OK)
								|| (mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_MENSAGEM_GRUPO_OK
										&& mensagemDesserializada.getParametros(0).equals(grupoAtual))
								|| (mensagemDesserializada
										.getCodOperacao() == ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_OK
										&& mensagemDesserializada.getParametros(0).equals(nomeUsuario))) {
							if (mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_NOK
									|| mensagemDesserializada
											.getCodOperacao() == ProtocoloResposta.OP_MENSAGEM_GRUPO_NOK
									|| mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_CONECTAR_NOK
									|| mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_DESCONECTAR_NOK
									|| mensagemDesserializada
											.getCodOperacao() == ProtocoloResposta.OP_CADASTRAR_USUARIO_NOK
									|| mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_LOGAR_USUARIO_NOK
									|| mensagemDesserializada
											.getCodOperacao() == ProtocoloResposta.OP_OBTER_GRUPOS_PERTENCE_NOK
									|| mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_ENTRAR_GRUPO_NOK
									|| mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_SAIR_GRUPO_NOK
									|| mensagemDesserializada
											.getCodOperacao() == ProtocoloResposta.OP_LISTAR_USUARIOS_ONLINE_NOK
									|| mensagemDesserializada
											.getCodOperacao() == ProtocoloResposta.OP_OBTER_STATUS_USUARIO_NOK
									|| mensagemDesserializada
											.getCodOperacao() == ProtocoloResposta.OP_ALTERAR_STATUS_USUARIO_NOK
									|| mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_AUTORIZACAO_NOK
									|| mensagemDesserializada.getCodOperacao() == ProtocoloResposta.OP_AUTENTICACAO_NOK
									|| mensagemDesserializada
											.getCodOperacao() == ProtocoloResposta.OP_OBTER_GRUPOS_NAO_PERTENCE_NOK) {
								String resposta = mensagemDesserializada.getParametros(0);
								System.out.println(resposta);
							} else {
								int codOperacao = mensagemDesserializada.getCodOperacao();

								if (codOperacao == ProtocoloResposta.OP_CONECTAR_OK) {
									String resposta = mensagemDesserializada.getParametros(0);
									System.out.println(resposta);
									executando = false;
									estado = "online";
								} else if (codOperacao == ProtocoloResposta.OP_DESCONECTAR_OK) {
									String resposta = mensagemDesserializada.getParametros(0);
									System.out.println(resposta);
									estado = "offline";
									executando = false;
								} else if (codOperacao == ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_OK) {
									String mensagemIndividual = mensagemDesserializada.getParametros(1);
									System.out.println(mensagemDesserializada.getNomeUsuarioOrigem()
											+ " enviou para você: " + mensagemIndividual);

								} else if (codOperacao == ProtocoloResposta.OP_MENSAGEM_GRUPO_OK) {
									String destino = mensagemDesserializada.getParametros(0);
									String mensagemGrupo = mensagemDesserializada.getParametros(1);
									System.out.println(mensagemDesserializada.getNomeUsuarioOrigem()
											+ " enviou para o grupo " + destino + ": " + mensagemGrupo);

								} else if (codOperacao == ProtocoloResposta.OP_CADASTRAR_USUARIO_OK) {
									String resposta = mensagemDesserializada.getParametros(0);
									System.out.println(resposta);
								} else if (codOperacao == ProtocoloResposta.OP_LOGAR_USUARIO_OK) {
									String nomeDeUsuario = mensagemDesserializada.getParametros(0);
									String senha = mensagemDesserializada.getParametros(1);
									String resposta = mensagemDesserializada.getParametros(2);
									nomeUsuario = nomeDeUsuario;
									senhaUsuario = senha;
									System.out.println(resposta);
									chatCliente.conectar(nomeDeUsuario, senha);
								} else if (codOperacao == ProtocoloResposta.OP_OBTER_GRUPOS_PERTENCE_OK) {
									ProtocolStringList parametrosList = mensagemDesserializada.getParametrosList();
									String[] nomeGrupos = parametrosList.toArray(new String[parametrosList.size()]);
									if (nomeGrupos.length == 0) {
										System.out.println("Você não está em nenhum grupo");
									} else {
										if (sairGrupo) {
											boolean retorno = chatCliente.sairDoGrupo(nomeUsuario, senhaUsuario,
													nomeGrupos);
											desbloquear = retorno;
											sairGrupo = false;
										} else {
											grupoAtual = chatCliente.acessarGrupo(nomeGrupos);
											System.out.println("Você acessou o grupo " + grupoAtual);
										}
									}
								} else if (codOperacao == ProtocoloResposta.OP_ENTRAR_GRUPO_OK) {
									String resposta = mensagemDesserializada.getParametros(0);
									System.out.println(resposta);
								} else if (codOperacao == ProtocoloResposta.OP_SAIR_GRUPO_OK) {
									String nomeGrupo = mensagemDesserializada.getParametros(0);
									String resposta = mensagemDesserializada.getParametros(1);
									System.out.println(resposta);
									if (grupoAtual != null) {
										if (nomeGrupo.equals(grupoAtual.toLowerCase().replaceAll(" ", "_"))) {
											grupoAtual = null;
										}
									}
								} else if (codOperacao == ProtocoloResposta.OP_LISTAR_USUARIOS_ONLINE_OK) {
									ProtocolStringList parametrosList = mensagemDesserializada.getParametrosList();
									String[] nomeUsuarios = parametrosList.toArray(new String[parametrosList.size()]);
									for (String usuario : nomeUsuarios) {
										System.out.println(usuario);
									}
									if(nomeUsuarios.length == 0) {
										System.out.println("Não existem usuários online");
									}
								} else if (codOperacao == ProtocoloResposta.OP_OBTER_STATUS_USUARIO_OK) {
									String resposta = mensagemDesserializada.getParametros(0);
									System.out.println(resposta);
								} else if (codOperacao == ProtocoloResposta.OP_ALTERAR_STATUS_USUARIO_OK) {
									String resposta = mensagemDesserializada.getParametros(0);
									System.out.println(resposta);
									if (estado.equals(("online"))) {
										estado = "ocupado";
									} else {
										estado = "online";
									}
								} else if (codOperacao == ProtocoloResposta.OP_OBTER_GRUPOS_NAO_PERTENCE_OK) {
									ProtocolStringList parametrosList = mensagemDesserializada.getParametrosList();
									String[] nomeGrupos = parametrosList.toArray(new String[parametrosList.size()]);
									boolean retorno = chatCliente.entrarEmGrupo(nomeUsuario, senhaUsuario, nomeGrupos);
									desbloquear = retorno;
								}
							}
							if (mensagemDesserializada.getCodOperacao() != ProtocoloResposta.OP_LOGAR_USUARIO_OK
									&& desbloquear) {
								// Notify the waiting thread when a message is received
								synchronized (messageLock) {
									messageLock.notify();
								}
							}
						} else if (mensagemDesserializada
								.getCodOperacao() == ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_OK
								&& mensagemDesserializada.getNomeUsuarioOrigem().equals(nomeUsuario)) {
							System.out.println("Mensagem enviada com sucesso");
							// Notify the waiting thread when a message is received
							synchronized (messageLock) {
								messageLock.notify();
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		Thread leituraThread = new Thread(() -> {
			// Iniciando a assinatura do canal
			jedis.subscribe(jedisPubSub, canalResposta.getBytes());
		});
		leituraThread.start();

		while (executando) {
			boolean opcaoInvalida = false;
			chatCliente.exibirMenuLogin();
			int opcao = scanner.nextInt();
			scanner.nextLine(); // Limpar o buffer do scanner

			switch (opcao) {
			case 1:
				chatCliente.cadastrarUsuario(nomeUsuario);
				break;
			case 2:
				chatCliente.realizarLogin(nomeUsuario);
				break;
			case 3:
				System.out.println("Encerrando o programa...");
				return;
			default:
				opcaoInvalida = true;
				System.out.println("Opção inválida. Tente novamente.");
			}
			if (!opcaoInvalida) {
				synchronized (messageLock) {
					try {
						messageLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!executando) {
					break;
				}
			}

		}

		executando = true;
		while (executando) {
			boolean exibirMenu = false;
			chatCliente.exibirMenuChat();
			int opcao = scanner.nextInt();
			scanner.nextLine(); // Limpar o buffer do scanner
			// Processar a opção escolhida
			switch (opcao) {
			case 1:
				chatCliente.obterGruposPertence(nomeUsuario, senhaUsuario);
				break;
			case 2:
				chatCliente.enviarMensagemIndividual(nomeUsuario, senhaUsuario);
				break;
			case 3:
				exibirMenu = chatCliente.enviarMensagemParaGrupo(nomeUsuario, senhaUsuario, grupoAtual);
				break;
			case 4:
				chatCliente.obterGruposNaoPertence(nomeUsuario, senhaUsuario);
				break;
			case 5:
				sairGrupo = true;
				chatCliente.obterGruposPertence(nomeUsuario, senhaUsuario);
				break;
			case 6:
				exibirMenu = chatCliente.listarUsuariosOnline(nomeUsuario, senhaUsuario, grupoAtual);
				break;
			case 7:
				chatCliente.obterStatusDoUsuario(nomeUsuario, senhaUsuario);
				break;
			case 8:
				exibirMenu = chatCliente.alterarStatusDoUsuario(nomeUsuario, senhaUsuario, estado);
				break;
			case 9:
				chatCliente.desconectar(nomeUsuario, senhaUsuario);
				break;
			default:
				exibirMenu = true;
				System.out.println("Opção inválida! Tente novamente!");

			}

			if (!exibirMenu) {
				synchronized (messageLock) {
					try {
						messageLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!executando) {
					break;
				}
			}
		}

		scanner.close();

		// Cancelando a assinatura do canal
		jedisPubSub.unsubscribe(canalResposta.getBytes());

		// Fechando a conexão
		jedis.close();

//		jedisPool.close();
	}
}