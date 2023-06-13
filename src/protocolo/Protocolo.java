package protocolo;

public class Protocolo {

    //TIPOS
    public final static String TIPO_REQUISICAO = "REQUISICAO";
    public final static String TIPO_RESPOSTA = "RESPOSTA";

    //OPERAÇÕES
    public final static int OP_CONECTAR = 0;
    public final static int OP_DESCONECTAR = 1;
    public final static int OP_MENSAGEM_GRUPO = 2;
    public final static int OP_MENSAGEM_INDIVIDUAL = 3;
    public final static int OP_MENSAGEM_INDIVIDUAL_NOK = 4;
    public final static int OP_CADASTRAR_USUARIO = 5;
    public final static int OP_LOGAR_USUARIO = 6;
    public final static int OP_BLOQUEAR_USUARIO = 7;
    public final static int OP_DESBLOQUEAR_USUARIO = 8;
    public final static int OP_EXCLUIR_MENSAGEM = 9;
    public final static int OP_ALTERAR_MENSAGEM = 10;
    public final static int OP_PESQUISAR_MENSAGEM = 11;
    public final static int OP_CRIAR_GRUPO = 12;
    public final static int OP_ENTRAR_EM_GRUPO = 13;
    public final static int OP_LISTAR_USUARIOS_ONLINE = 13;
    public final static int OP_OBTER_STATUS_DE_USUARIO = 14;
    public final static int OP_ALTERAR_STATUS_DE_USUARIO = 15;
    public final static int OP_EXCLUIR_GRUPO = 16;

}