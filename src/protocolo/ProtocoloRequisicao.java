package protocolo;

public class ProtocoloRequisicao {

    public final static int TIPO_REQUISICAO = 0;
    
    public final static int OP_CONECTAR = 0;
    public final static int OP_DESCONECTAR = 1;
    public final static int OP_MENSAGEM_INDIVIDUAL = 2;
    public final static int OP_MENSAGEM_GRUPO = 3;
    public final static int OP_CADASTRAR_USUARIO = 4;
    public final static int OP_LOGAR_USUARIO = 5;
    public final static int OP_OBTER_GRUPOS_PERTENCE = 6;
    public final static int OP_ENTRAR_GRUPO = 7;
    public final static int OP_SAIR_GRUPO = 8;
    public final static int OP_LISTAR_USUARIOS_ONLINE = 9;
    public final static int OP_OBTER_STATUS_USUARIO = 10;
    public final static int OP_ALTERAR_STATUS_USUARIO = 11;
    public final static int OP_MENSAGEM_GRUPO_AUTORIZAR = 12;
    public final static int OP_SAIR_GRUPO_AUTORIZAR = 13;
    public final static int OP_LISTAR_USUARIOS_ONLINE_AUTORIZAR = 14;
    public final static int OP_AUTENTICAR_USUARIO = 15;
    public final static int OP_OBTER_GRUPOS_NAO_PERTENCE = 16;



}