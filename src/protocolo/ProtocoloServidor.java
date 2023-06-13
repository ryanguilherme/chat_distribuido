package protocolo;

import mensagem.Mensagem;

public class ProtocoloServidor {

    public Mensagem tratarMensagemRecebida(Mensagem mensagem) {

        Mensagem resposta = new Mensagem();

        //TRATAMENTO DE MENSAGENS DE REQUISIÇÃO
        if (mensagem.getCodOperacao() == Protocolo.OP_CONECTAR)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_CONECTAR);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaConexao = { "Usuário " + mensagem.getOrigem() + " entrou no chat" };
            resposta.setParametros(respostaConexao);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_DESCONECTAR)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_DESCONECTAR);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaDesConexao = {"Usuário " + mensagem.getOrigem() + " saiu do chat" };
            resposta.setParametros(respostaDesConexao);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_MENSAGEM_INDIVIDUAL)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_MENSAGEM_INDIVIDUAL);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaMensagemIndividual = { "Usuário " + mensagem.getOrigem() + " enviou " + mensagem.getParametros()[1] + " para o " + "Usuário " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaMensagemIndividual);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_MENSAGEM_GRUPO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_MENSAGEM_GRUPO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaMensagemEmGrupo = {"Usuário " +mensagem.getOrigem() + " enviou " + mensagem.getParametros()[1] + " para o grupo" + mensagem.getParametros()[0]};
            resposta.setParametros(respostaMensagemEmGrupo);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_CADASTRAR_USUARIO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_CADASTRAR_USUARIO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaCadastroUsuario = {"Usuário " + mensagem.getOrigem() + " cadastrou o usuário " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaCadastroUsuario);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_LOGAR_USUARIO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_LOGAR_USUARIO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaLogarUsuario = {"Usuário " + mensagem.getOrigem() + " logou no sistema" };
            resposta.setParametros(respostaLogarUsuario);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_BLOQUEAR_USUARIO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_BLOQUEAR_USUARIO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaBloquearUsuario = {"Usuário " + mensagem.getOrigem() + " bloqueou o usuário " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaBloquearUsuario);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_DESBLOQUEAR_USUARIO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_DESBLOQUEAR_USUARIO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaDesbloquearUsuario = {"Usuário " + mensagem.getOrigem() + " desbloqueou o usuário " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaDesbloquearUsuario);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_EXCLUIR_MENSAGEM)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_EXCLUIR_MENSAGEM);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaExcluirMensagem = {"Usuário " + mensagem.getOrigem() + " excluiu a mensagem " + mensagem.getParametros()[1] };
            resposta.setParametros(respostaExcluirMensagem);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_ALTERAR_MENSAGEM)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_ALTERAR_MENSAGEM);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaAlterarMensagem = {"Usuário " + mensagem.getOrigem() + " alterou a mensagem " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaAlterarMensagem);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_PESQUISAR_MENSAGEM)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_PESQUISAR_MENSAGEM);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaPesquisarMensagem = {"Usuário " + mensagem.getOrigem() + " pesquisou a mensagem " + mensagem.getParametros()[0] };
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_CRIAR_GRUPO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_CRIAR_GRUPO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaCriarGrupo = {"Usuário " + mensagem.getOrigem() + " criou o grupo " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaCriarGrupo);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_ENTRAR_EM_GRUPO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_ENTRAR_EM_GRUPO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaEntrarEmGrupo = {"Usuário " + mensagem.getOrigem() + " entrou no grupo " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaEntrarEmGrupo);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_LISTAR_USUARIOS_ONLINE)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_LISTAR_USUARIOS_ONLINE);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaListarUsuariosOnline = {"Usuário " + mensagem.getOrigem() + " listou os usuários online" };
            resposta.setParametros(respostaListarUsuariosOnline);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_OBTER_STATUS_DE_USUARIO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_OBTER_STATUS_DE_USUARIO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaObterStatusDeUsuario = {"Usuário " + mensagem.getOrigem() + " obteve o status do usuário " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaObterStatusDeUsuario);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_ALTERAR_STATUS_DE_USUARIO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_ALTERAR_STATUS_DE_USUARIO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaAlterarStatusDeUsuario = {"Usuário " + mensagem.getOrigem() + " alterou o status de usuário " };
            resposta.setParametros(respostaAlterarStatusDeUsuario);
        }
        else if (mensagem.getCodOperacao() == Protocolo.OP_EXCLUIR_GRUPO)
        {
            resposta.setTipo(Protocolo.TIPO_RESPOSTA);
            resposta.setCodOperacao(Protocolo.OP_EXCLUIR_GRUPO);
            resposta.setIdMensagem(mensagem.getIdMensagem());
            resposta.setOrigem(mensagem.getOrigem());
            String[] respostaExcluirGrupo = {"Usuário " + mensagem.getOrigem() + " excluiu o grupo " + mensagem.getParametros()[0] };
            resposta.setParametros(respostaExcluirGrupo);
        }

        return resposta;

    }

}