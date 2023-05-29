package protocolo;

public class Protocolo {
	
	public final static String TIPO_REQUISICAO = "REQUISICAO";
	public final static String TIPO_RESPOSTA = "RESPOSTA";
	
	public final static int OP_CONECTAR = 0;
	public final static int OP_DESCONECTAR = 1;
	public final static int OP_MENSAGEM_GRUPO = 2;
	public final static int OP_MENSAGEM_INDIVIDUAL = 3;
	public final static int OP_MENSAGEM_INDIVIDUAL_NOK = 4;

}
