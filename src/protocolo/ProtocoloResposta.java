package protocolo;

public class ProtocoloResposta {
	 	public final static int TIPO_RESPOSTA = 1;
	 	
	 	public final static int OP_CONECTAR_OK = 0;
	 	public final static int OP_CONECTAR_NOK = 1;
	 	public final static int OP_DESCONECTAR_OK = 2;
	 	public final static int OP_DESCONECTAR_NOK = 3;
	 	public final static int OP_MENSAGEM_INDIVIDUAL_OK = 4;
	 	public final static int OP_MENSAGEM_INDIVIDUAL_NOK = 5;
	 	public final static int OP_MENSAGEM_GRUPO_OK = 6;
	 	public final static int OP_MENSAGEM_GRUPO_NOK = 7;
	 	public final static int OP_CADASTRAR_USUARIO_OK = 8;
	 	public final static int OP_CADASTRAR_USUARIO_NOK = 9;
	 	public final static int OP_LOGAR_USUARIO_OK = 10;
	 	public final static int OP_LOGAR_USUARIO_NOK = 11;
	 	public final static int OP_OBTER_GRUPOS_PERTENCE_OK = 12;
	 	public final static int OP_OBTER_GRUPOS_PERTENCE_NOK = 13;
		public final static int OP_ENTRAR_GRUPO_OK = 14;
	 	public final static int OP_ENTRAR_GRUPO_NOK = 15;
	 	public final static int OP_SAIR_GRUPO_OK = 16;
	 	public final static int OP_SAIR_GRUPO_NOK = 17;
	 	public final static int OP_LISTAR_USUARIOS_ONLINE_OK = 18;
	 	public final static int OP_LISTAR_USUARIOS_ONLINE_NOK = 19;
	 	public final static int OP_OBTER_STATUS_USUARIO_OK = 20;
	 	public final static int OP_OBTER_STATUS_USUARIO_NOK = 21;
	 	public final static int OP_ALTERAR_STATUS_USUARIO_OK = 22;
	 	public final static int OP_ALTERAR_STATUS_USUARIO_NOK = 23;
	 	public final static int OP_AUTORIZACAO_OK = 24;
	 	public final static int OP_AUTORIZACAO_NOK = 25;
	 	public final static int OP_AUTENTICACAO_OK = 26;
	 	public final static int OP_AUTENTICACAO_NOK = 27;
		public final static int OP_OBTER_GRUPOS_NAO_PERTENCE_OK = 28;
	 	public final static int OP_OBTER_GRUPOS_NAO_PERTENCE_NOK = 29;
		
}