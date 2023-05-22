package br.ufc.quixada.calculadora;

import java.io.Serializable;

import com.google.gson.Gson;

/*
 * 
 * Mensagens
	Requisicao
		tipo: (requisicao, resposta)
		codOperacao: (0-Soma, 1-Sub, 2-Mult, 3-Divisao)
		idMensagem: (inteiro, incrementado a cada nova requisicao)
		idCliente (ip, identificador associado no login, ... )
		paramentros entrada: param1, param2

	Resposta
		tipo: (requisicao, resposta)
		codOperacao: (0-Soma, 1-Sub, 2-Mult, 3-Divisao)
		idMensagem: resposta a uma requisicao (idMensagem)
		idCliente: idCliente que fez a requisição
		parametros resposta: resultado 
 *  
 * 
 */
public class Protocolo implements Serializable {
	
	public final static String TIPO_REQUISICAO = "REQUISICAO";
	public final static String TIPO_RESPOSTA = "RESPOSTA";
	
	public final static int OP_SOMA = 1;
	public final static int OP_SUB = 2;
	public final static int OP_MULT = 3;
	public final static int OP_DIV = 4;
	
	String tipo;
	int codOperacao;
	int idMensagem;
	String idCliente;
	float[] parametros;
	
	
	
	
	
	public Protocolo(String tipo, int codOperacao, int idMensagem, String idCliente, float[] parametros) {
		super();
		this.tipo = tipo;
		this.codOperacao = codOperacao;
		this.idMensagem = idMensagem;
		this.idCliente = idCliente;
		this.parametros = parametros;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getCodOperacao() {
		return codOperacao;
	}
	public void setCodOperacao(int codOperacao) {
		this.codOperacao = codOperacao;
	}
	public int getIdMensagem() {
		return idMensagem;
	}
	public void setIdMensagem(int idMensagem) {
		this.idMensagem = idMensagem;
	}
	public String getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}
	public float[] getParametros() {
		return parametros;
	}
	public void setParametros(float[] parametros) {
		this.parametros = parametros;
	}

	public static void main(String[] args) {
		
		Gson gson = new Gson();
		
		float[] params = {3.0f, 4.0f};
		Protocolo requisicao = new Protocolo( TIPO_REQUISICAO, OP_SOMA, 0, "Cliente 01", params );
		
		String requisicaoString = gson.toJson( requisicao );
		System.out.println( requisicaoString );
		//Pronto para ser enviado pela rede
		
		//Recebendo no servidor
		String requisicaoNoServidor = requisicaoString;
		Protocolo requisicaoRecebida = gson.fromJson(requisicaoNoServidor, Protocolo.class );
		
		//Tratamento de envio e resposta pela rede
		
		float[] paramsResposta = { params[0] + params[ 1 ] };
		Protocolo resposta = new Protocolo( TIPO_RESPOSTA, OP_SOMA, 0, "Cliente 01", paramsResposta );
		
	}

}
