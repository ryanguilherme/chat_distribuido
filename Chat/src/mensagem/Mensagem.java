package mensagem;

public class Mensagem {

	private String tipo;
	private int codOperacao;
	private int idMensagem;
	private String origem;
	private String[] parametros;

	public Mensagem(String tipo, int codOperacao, int idMensagem, String origem, String[] parametros) {
		super();
		this.tipo = tipo;
		this.codOperacao = codOperacao;
		this.idMensagem = idMensagem;
		this.origem = origem;
		this.parametros = parametros;
	}

	public Mensagem() {

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

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String[] getParametros() {
		return parametros;
	}

	public void setParametros(String[] parametros) {
		this.parametros = parametros;
	}

}
