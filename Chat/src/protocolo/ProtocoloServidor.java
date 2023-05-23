package protocolo;

import mensagem.Mensagem;

public class ProtocoloServidor {

	public Mensagem tratarMensagemRecebida(Mensagem mensagem) {

		Mensagem resposta = new Mensagem();

		if (mensagem.getCodOperacao() == Protocolo.OP_CONECTAR) {
			resposta.setTipo(Protocolo.TIPO_RESPOSTA);
			resposta.setCodOperacao(Protocolo.OP_CONECTAR);
			resposta.setIdMensagem(mensagem.getIdMensagem());
			resposta.setOrigem(mensagem.getOrigem());
			String respostaConexao[] = { "Usuário " + mensagem.getOrigem() + " entrou no chat" };
			resposta.setParametros(respostaConexao);
		} else if (mensagem.getCodOperacao() == Protocolo.OP_DESCONECTAR) {
			resposta.setTipo(Protocolo.TIPO_RESPOSTA);
			resposta.setCodOperacao(Protocolo.OP_DESCONECTAR);
			resposta.setIdMensagem(mensagem.getIdMensagem());
			resposta.setOrigem(mensagem.getOrigem());
			String respostaDesConexao[] = {"Usuário " + mensagem.getOrigem() + " saiu do chat" };
			resposta.setParametros(respostaDesConexao);
		} else if (mensagem.getCodOperacao() == Protocolo.OP_MENSAGEM_INDIVIDUAL) {
			resposta.setTipo(Protocolo.TIPO_RESPOSTA);
			resposta.setCodOperacao(Protocolo.OP_MENSAGEM_INDIVIDUAL);
			resposta.setIdMensagem(mensagem.getIdMensagem());
			resposta.setOrigem(mensagem.getOrigem());
			String respostaMensagemIndividual[] = { "Usuário " + mensagem.getOrigem() + " enviou " + mensagem.getParametros()[1] + " para o " + "Usuário " + mensagem.getParametros()[0] };
			resposta.setParametros(respostaMensagemIndividual);
		} else if (mensagem.getCodOperacao() == Protocolo.OP_MENSAGEM_GRUPO) {
			resposta.setTipo(Protocolo.TIPO_RESPOSTA);
			resposta.setCodOperacao(Protocolo.OP_MENSAGEM_GRUPO);
			resposta.setIdMensagem(mensagem.getIdMensagem());
			resposta.setOrigem(mensagem.getOrigem());
			String respostaMensagemEmGrupo[] = {"Usuário " +mensagem.getOrigem() + " enviou " + mensagem.getParametros()[0] + " para o grupo" };
			resposta.setParametros(respostaMensagemEmGrupo);
		}
		
		return resposta;

	}

}
