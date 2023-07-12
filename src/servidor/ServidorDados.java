package servidor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mensagem.MensagemProtocolBuffers.Mensagem;
import protocolo.ProtocoloRequisicao;
import protocolo.ProtocoloResposta;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ServidorDados {
	// Configurações de conexão com o banco de dados
	private String url = "jdbc:postgresql://localhost/";
	private String username = "postgres";
	private String password = "postgres";
	// Nome do banco de dados a ser criado
	private String databaseName = "usuarios";
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private String[] nomesGrupos = { "cafe_e_conversa", "livros_e_leitura", "cinema_e_tv", "tecnologia_e_inovacao",
			"jogos_e_entretenimento", "sustentabilidade_e_meio_ambiente" };

	public ServidorDados() {
		try {
			// Registrar o driver JDBC do PostgreSQL
			Class.forName("org.postgresql.Driver");

			// Conectar ao servidor PostgreSQL
			connection = DriverManager.getConnection(url, username, password);

			// Criar uma declaração SQL
			statement = connection.createStatement();

			// Verificar se o banco de dados já existe
			String checkDatabaseQuery = "SELECT datname FROM pg_catalog.pg_database WHERE datname = '" + databaseName
					+ "'";
			resultSet = statement.executeQuery(checkDatabaseQuery);

			if (!resultSet.next()) {
				// Criar o banco de dados
				String createDatabaseQuery = "CREATE DATABASE " + databaseName;
				statement.executeUpdate(createDatabaseQuery);
				System.out.println("Banco de dados criado com sucesso!");
			}
			criarTabela();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void FecharConexao() {
		// Fechar conexão e liberar recursos
		try {
			if (resultSet != null)
				resultSet.close();
			if (statement != null)
				statement.close();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean verificarTabelaExistente() {
		try {
			DatabaseMetaData metaData = connection.getMetaData(); // Assuming you have a valid 'connection' object
			ResultSet resultSet = metaData.getTables(null, null, "users", null);
			return resultSet.next(); // Returns true if the table exists, false otherwise
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // Return false in case of any exception
	}

	public void criarTabela() {
		ResultSet resultSet;
		try {
			if (!verificarTabelaExistente()) {
				// Criar a tabela "users"
				StringBuilder createTableQuery = new StringBuilder("CREATE TABLE users (");
				createTableQuery.append("username VARCHAR(255) PRIMARY KEY,");
				createTableQuery.append("password VARCHAR(255) NOT NULL,");
				createTableQuery.append("status VARCHAR(50) NOT NULL");

				// Adicionar colunas para grupos
				for (String grupo : nomesGrupos) {
					String nomeColuna = grupo;
					createTableQuery.append(", ");
					createTableQuery.append("\"");
					createTableQuery.append(nomeColuna);
					createTableQuery.append("\"");
					createTableQuery.append(" BOOLEAN DEFAULT FALSE");
				}

				createTableQuery.append(")");

				Statement statement = connection.createStatement(); // Assuming you have a valid 'connection' object
				statement.executeUpdate(createTableQuery.toString());
			}
			System.out.println("Tabela 'users' criada com sucesso!");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean verificarUsuario(String username) {
		try {
			// Verificar se o usuário existe
			String checkUserQuery = "SELECT username FROM users WHERE username = '" + username + "'";
			ResultSet resultSet = statement.executeQuery(checkUserQuery);
			return resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String adicionarUsuario(String username, String password) {
		try {
			// Verificar se o usuário já existe
			if (verificarUsuario(username)) {
				return "O nome de usuário já existe!";
			} else {
				// Adicionar novo usuário
				String addUserQuery = "INSERT INTO users (username, password, status) VALUES ('" + username + "', '"
						+ password + "', 'offline')";
				statement.executeUpdate(addUserQuery);

				return "Novo usuário " + username + " adicionado com sucesso!";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public boolean verificarUsuarioEmGrupo(String username, String groupName) {
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getColumns(null, null, "users",
					groupName.toLowerCase().replaceAll(" ", "_"));

			if (resultSet.next()) {
				// Verificar se o usuário está no grupo
				String checkUserGroupQuery = "SELECT " + groupName.toLowerCase().replaceAll(" ", "_")
						+ " FROM users WHERE username = '" + username + "'";
				ResultSet resultSet2 = statement.executeQuery(checkUserGroupQuery);

				// Verificar se há um registro retornado
				if (resultSet2.next()) {
					return resultSet2.getBoolean(1);
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String verificarCredenciais(String username, String password) {
		try {
			// Verificar as credenciais do usuário
			String checkCredentialsQuery = "SELECT username FROM users WHERE username = '" + username
					+ "' AND password = '" + password + "'";
			ResultSet resultSet = statement.executeQuery(checkCredentialsQuery);
			if (resultSet.next()) {
				return "Usuário " + username + " logado com sucesso";
			} else {
				return "Usuário e/ou Senha inválidos";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String alterarStatusUsuario(String username, String novoStatus) {
		try {
			// Alterar o status do usuário
			String alterStatusQuery = "UPDATE users SET status = '" + novoStatus + "' WHERE username = '" + username
					+ "'";
			statement.executeUpdate(alterStatusQuery);
			return "Status alterado com sucesso";
		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String conectarDesconectarUsuario(String username, String novoStatus) {
		try {
			// Alterar o status do usuário
			String alterStatusQuery = "UPDATE users SET status = '" + novoStatus + "' WHERE username = '" + username
					+ "'";
			statement.executeUpdate(alterStatusQuery);
			if (novoStatus.equals("online")) {
				return "Conectado com sucesso";
			} else {
				return "Desconectado com sucesso";

			}
		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String transformarNomeGrupo(String nome) {
		String nomeTransformado = nome.replace("_", " ");
		nomeTransformado = nomeTransformado.substring(0, 1).toUpperCase() + nomeTransformado.substring(1);
		return nomeTransformado;
	}

	public String adicionarUsuarioAoGrupo(String username, String groupName) {
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getColumns(null, null, "users", groupName);

			if (resultSet.next()) {
				// Verificar se o usuário já está no grupo
				String checkUserGroupQuery = "SELECT " + groupName.toLowerCase().replaceAll("\\s+", "_")
						+ " FROM users WHERE username = '" + username + "'";
				ResultSet resultSet2 = statement.executeQuery(checkUserGroupQuery);

				if (resultSet2.next() && resultSet2.getBoolean(1)) {
					return "O usuário " + username + " já está no grupo " + groupName;
				} else {
					// Adicionar o usuário ao grupo
					String addGroupQuery = "UPDATE users SET " + groupName.toLowerCase().replaceAll("\\s+", "_")
							+ " = true WHERE username = '" + username + "'";
					statement.executeUpdate(addGroupQuery);
					return "Usuário " + username + " adicionado ao grupo " + transformarNomeGrupo(groupName);
				}
			} else {
				return "Grupo não existe";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String removerUsuarioDoGrupo(String username, String groupName) {
		try {
			// Remover o usuário do grupo
			String removeGroupQuery = "UPDATE users SET " + groupName + " = false WHERE username = '" + username + "'";
			statement.executeUpdate(removeGroupQuery);
			return "Usuário " + username + " removido do grupo " + transformarNomeGrupo(groupName);

		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String verificarEstadoUsuario(String username) {
		try {
			// Verificar se o usuário existe
			if (verificarUsuario(username)) {
				// Obter o estado do usuário
				String getUserStatusQuery = "SELECT status FROM users WHERE username = '" + username + "'";
				ResultSet resultSet = statement.executeQuery(getUserStatusQuery);

				// Verificar se há um registro retornado
				if (resultSet.next()) {
					String status = resultSet.getString("status");
					return "O estado do usuário " + username + " é: " + status;
				} else {
					return "Não foi possível obter o estado do usuário " + username;
				}
			} else {
				return "O usuário " + username + " não existe";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public List<String> obterUsuariosOnlineDoGrupo(String grupo) {
		List<String> usuariosOnline = new ArrayList<>();

		try {
			// Construct the SQL query dynamically
			String columnName = grupo.toLowerCase().replaceAll("\\s+", "_");
			String query = "SELECT username FROM users WHERE status = 'online' AND " + columnName + " = true";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			// Iterate over the result set
			while (resultSet.next()) {
				String username = resultSet.getString("username");
				usuariosOnline.add(username);
			}
		} catch (SQLException e) {
			usuariosOnline.add("Erro");
			usuariosOnline.add(e.getMessage());
		}

		return usuariosOnline;
	}

	public List<String> obterGruposDoUsuario(String nomeUsuario) {
		List<String> gruposDoUsuario = new ArrayList<>();

		try {
			// Consultar os grupos do usuário
			String query = "SELECT ";
			for (String grupo : nomesGrupos) {
				String nomeColuna = grupo;
				query += nomeColuna + ", ";
			}
			query = query.substring(0, query.length() - 2); // Remover a última vírgula e espaço
			query += " FROM users WHERE username = ?";

			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, nomeUsuario);
			ResultSet resultSet = statement.executeQuery();

			// Percorrer o resultado da consulta
			if (resultSet.next()) {
				for (String grupo : nomesGrupos) {
					String nomeColuna = grupo.replaceAll("\\s+", "_").toLowerCase();
					boolean pertenceAoGrupo = resultSet.getBoolean(nomeColuna);
					if (pertenceAoGrupo) {
						gruposDoUsuario.add(grupo);
					}
				}
			}
		} catch (SQLException e) {
			String erro = "Erro";
			e.printStackTrace();
			// Em caso de erro, retorna uma lista com erro
			gruposDoUsuario.add(erro);
			gruposDoUsuario.add(e.getMessage());
			return gruposDoUsuario;
		}

		return gruposDoUsuario;
	}

	public List<String> obterGruposNaoPertenceDoUsuario(String nomeUsuario) {
		List<String> gruposNaoPertenceDoUsuario = new ArrayList<>();

		try {
			// Consultar os grupos do usuário
			String query = "SELECT ";
			for (String grupo : nomesGrupos) {
				String nomeColuna = grupo.replaceAll("\\s+", "_").toLowerCase();
				query += nomeColuna + ", ";
			}
			query = query.substring(0, query.length() - 2); // Remover a última vírgula e espaço
			query += " FROM users WHERE username = ?";

			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, nomeUsuario);
			ResultSet resultSet = statement.executeQuery();

			// Percorrer o resultado da consulta
			if (resultSet.next()) {
				for (String grupo : nomesGrupos) {
					String nomeColuna = grupo.replaceAll("\\s+", "_").toLowerCase();
					boolean pertenceAoGrupo = resultSet.getBoolean(nomeColuna);
					if (!pertenceAoGrupo) {
						gruposNaoPertenceDoUsuario.add(grupo);
					}
				}
			}
		} catch (SQLException e) {
			String erro = "Erro";
			e.printStackTrace();
			// Em caso de erro, retorna uma lista com erro
			gruposNaoPertenceDoUsuario.add(erro);
			gruposNaoPertenceDoUsuario.add(e.getMessage());
			return gruposNaoPertenceDoUsuario;
		}

		return gruposNaoPertenceDoUsuario;
	}

	public Mensagem tratarMensagemRecebidaServidorAutenticacaoAutorizacao(Mensagem Requisicao) {
		Mensagem mensagem = null;
		if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_CADASTRAR_USUARIO) {
			String resultado = adicionarUsuario(Requisicao.getParametros(0), Requisicao.getParametros(1));
			if (resultado.contains("sucesso")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_CADASTRAR_USUARIO_OK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_CADASTRAR_USUARIO_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_LOGAR_USUARIO) {
			System.out.println(Requisicao.getParametros(0));
			System.out.println(Requisicao.getParametros(1));
			String resultado = verificarCredenciais(Requisicao.getParametros(0), Requisicao.getParametros(1));
			if (resultado.contains("sucesso")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_LOGAR_USUARIO_OK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem())
						.addParametros(Requisicao.getParametros(0)).addParametros(Requisicao.getParametros(1))
						.addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_LOGAR_USUARIO_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_AUTENTICAR_USUARIO) {
			String resultado = verificarCredenciais(Requisicao.getNomeUsuarioOrigem(), Requisicao.getParametros(0));
			if (resultado.contains("sucesso")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_AUTENTICACAO_OK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_AUTENTICACAO_NOK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).build();
			}
		} else {
			if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_MENSAGEM_GRUPO_AUTORIZAR
					|| Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_SAIR_GRUPO_AUTORIZAR
					|| Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_LISTAR_USUARIOS_ONLINE_AUTORIZAR) {
				System.out.println(Requisicao.getParametros(1));
				boolean resultado = verificarUsuarioEmGrupo(Requisicao.getNomeUsuarioOrigem(),
						Requisicao.getParametros(1));
				if (resultado) {
					mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
							.setCodOperacao(ProtocoloResposta.OP_AUTORIZACAO_OK)
							.setIdMensagem(Requisicao.getIdMensagem())
							.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).build();
				} else {
					mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
							.setCodOperacao(ProtocoloResposta.OP_AUTORIZACAO_NOK)
							.setIdMensagem(Requisicao.getIdMensagem())
							.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).build();
				}
			}
		}
		return mensagem;
	}

	public Mensagem tratarMensagemRecebidaServidorLogicaNegocios(Mensagem Requisicao) {
		Mensagem mensagem = null;
		if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_CONECTAR) {
			String resultado = conectarDesconectarUsuario(Requisicao.getNomeUsuarioOrigem(), "online");
			if (resultado.contains("sucesso")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_CONECTAR_OK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_CONECTAR_NOK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_DESCONECTAR) {
			String resultado = conectarDesconectarUsuario(Requisicao.getNomeUsuarioOrigem(), "offline");
			if (resultado.contains("sucesso")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_DESCONECTAR_OK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_DESCONECTAR_NOK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_MENSAGEM_INDIVIDUAL) {
			boolean resultado = verificarUsuario(Requisicao.getParametros(1));
			if (resultado) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_OK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).build();
			} else {
				String erro = "Usuário não existe";
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_MENSAGEM_INDIVIDUAL_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(erro).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_OBTER_GRUPOS_PERTENCE) {
			List<String> resultados = obterGruposDoUsuario(Requisicao.getNomeUsuarioOrigem());
			if (resultados.size() == 2 && resultados.get(0).equals("erro")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_OBTER_GRUPOS_PERTENCE_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultados.get(1))
						.build();
			} else {
				Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_OBTER_GRUPOS_PERTENCE_OK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem());
				for (String resultado : resultados) {
					mensagemBuilder.addParametros(resultado);
				}
				mensagem = mensagemBuilder.build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_OBTER_GRUPOS_NAO_PERTENCE) {
			List<String> resultados = obterGruposNaoPertenceDoUsuario(Requisicao.getNomeUsuarioOrigem());
			if (resultados.size() == 2 && resultados.get(0).equals("erro")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_OBTER_GRUPOS_NAO_PERTENCE_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultados.get(1))
						.build();
			} else {
				Mensagem.Builder mensagemBuilder = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_OBTER_GRUPOS_NAO_PERTENCE_OK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem());
				for (String resultado : resultados) {
					mensagemBuilder.addParametros(resultado);
				}
				mensagem = mensagemBuilder.build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_ENTRAR_GRUPO) {
			String resultado = adicionarUsuarioAoGrupo(Requisicao.getNomeUsuarioOrigem(), Requisicao.getParametros(1));
			if (resultado.contains("adicionado")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_ENTRAR_GRUPO_OK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_ENTRAR_GRUPO_NOK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_SAIR_GRUPO) {
			String resultado = removerUsuarioDoGrupo(Requisicao.getNomeUsuarioOrigem(), Requisicao.getParametros(1));
			if (resultado.contains("removido")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_SAIR_GRUPO_OK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem())
						.addParametros(Requisicao.getParametros(1)).addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_SAIR_GRUPO_NOK).setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_LISTAR_USUARIOS_ONLINE) {
			List<String> resultados = obterUsuariosOnlineDoGrupo(Requisicao.getParametros(1));
			if (resultados.size() == 2 && resultados.get(0).equals("erro")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_LISTAR_USUARIOS_ONLINE_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultados.get(1))
						.build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_LISTAR_USUARIOS_ONLINE_OK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addAllParametros(resultados).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_OBTER_STATUS_USUARIO) {
			String resultado = verificarEstadoUsuario(Requisicao.getParametros(1));
			if (resultado.contains("estado")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_OBTER_STATUS_USUARIO_OK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_OBTER_STATUS_USUARIO_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		} else if (Requisicao.getCodOperacao() == ProtocoloRequisicao.OP_ALTERAR_STATUS_USUARIO) {
			String resultado = alterarStatusUsuario(Requisicao.getNomeUsuarioOrigem(), Requisicao.getParametros(1));
			if (resultado.contains("sucesso")) {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_ALTERAR_STATUS_USUARIO_OK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			} else {
				mensagem = Mensagem.newBuilder().setTipo(ProtocoloResposta.TIPO_RESPOSTA)
						.setCodOperacao(ProtocoloResposta.OP_ALTERAR_STATUS_USUARIO_NOK)
						.setIdMensagem(Requisicao.getIdMensagem())
						.setNomeUsuarioOrigem(Requisicao.getNomeUsuarioOrigem()).addParametros(resultado).build();
			}
		}

		return mensagem;

	}

}
