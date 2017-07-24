
package analiseSemantica;

import java.util.ArrayList;

public class Funcao {
	
	//private final String retorno;
	private String nome;
	private String tipoRetorno;
	private ArrayList<Variavel> parametros = new ArrayList<>();
        
        public Funcao(String nome, String tipoRetorno, ArrayList<Variavel> parametros) {
		this.nome = nome;
		this.tipoRetorno = tipoRetorno;
		// Isso ta certo ?
                this.parametros= parametros;
	}
	/**
	 * @return the parametro
	 */
	public ArrayList<Variavel> getParametros() {
		return parametros;
	}

	/**
	 * @param parametro the parametro to set
	 */
	public void setParametros(ArrayList<Variavel> parametro) {
		this.parametros = parametro;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @param tipoRetorno the tipoRetorno to set
	 */
	public void setTipoRetorno(String tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @return the tipoRetorno
	 */
	public String getTipoRetorno() {
		return tipoRetorno;
	}
	

}

