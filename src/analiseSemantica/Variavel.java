/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analiseSemantica;

/**
 *
 * @author user
 */
public class Variavel {
    private String tipo;
    private String nome;
    private int tamanho;
    

    	public String getTipo() {
		return tipo;
	}
        public int getTamanho(){
            return tamanho;
        }
	public String getNome() {
		return nome;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
        public void setTamanho(int tamanho){
            this.tamanho = tamanho;
        }

    
}
