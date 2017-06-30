package analiseLexica;
public class Token {
	private final int linha;
	private final int aux;
	private final String tipo;
	private final String lexema;

        public Token(int linha, int aux, String tipo, String lexema) {
		this.tipo = tipo;
		this.linha = linha;
		this.aux = aux;
		this.lexema = lexema;
	}

	public String getTipo() {
		return tipo;
	}
	public int getLinha() {
		return linha;
	}
	public int getEspaco() {
		return aux;
	}
	public String getLexema() {
		return lexema;
	}

}
