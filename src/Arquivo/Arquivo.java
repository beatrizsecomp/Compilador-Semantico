
package Arquivo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import analiseLexica.Token;
    /**
     * Classe utilizada para tratamento de arquivo, responsável por leitura e escrita do mesmo
     */

public class Arquivo {
    	private String localFile;

        	public ArrayList<String> lerCodigos() {

		ArrayList<String> codigos = new ArrayList<>(); 
        File caminho = new File("test/Entrada/");
        for (File f : caminho.listFiles()) {
            codigos.add(f.getName());
        }
        return codigos;
    }
	public ArrayList<String> lerCodigoFonte(String localFile) throws FileNotFoundException {

        Scanner scanner = new Scanner(new FileReader("test/Entrada/" + localFile)); 
        this.localFile = localFile; 
        ArrayList<String> codigo = new ArrayList<String>(); 
        while (scanner.hasNextLine()) { 
        	
        String s = scanner.nextLine();
        
        if (s.length() != 0){
            codigo.add(s);}
            
        }
        scanner.close();
        return codigo;
    }
	public void gravaSaida(ArrayList<Token> tokens, ArrayList<String> erros) throws IOException {

        FileWriter arq = new FileWriter("test/Saida/Lexico/"+"saida"+ this.localFile  , false);
        PrintWriter gravar = new PrintWriter(arq);
        for (Token token : tokens) { 
            gravar.println(" " +token.getLinha() + " " + token.getLexema() + " " + token.getTipo());
        }
        if (erros.isEmpty()) { 
            gravar.printf("\n Nao existem erros lexicos\n");
        } else { 
            gravar.printf("\n Erros \n");
            for (String erro : erros) {
                gravar.println( erro);
            }
        }
        arq.close();
    }
        
	public void gravaSaidaSintatico(ArrayList<String> erros) throws IOException {
        FileWriter arq = new FileWriter("test/Saida/Sintatico/" + this.localFile , false); // Cria o arquivo de saída relacionado ao seu respectivo arquivo de entrada ("mesmo" nome). 
        PrintWriter gravar = new PrintWriter(arq);
        if (erros.isEmpty()) { // Se não houver erros léxicos.
            gravar.printf("\nNao existem erros Sintaticos\n");
        } else { // Se houver erros léxicos, os insere no arquivo de saída.
            for (String erro : erros) {
                gravar.println("Erro: " + erro);
            }
        }
        arq.close();
	}
	
	
	public void gravaSaidaSemantico(ArrayList<String> erros) throws IOException {
        FileWriter arq = new FileWriter("test/Saida/Semantico/" + this.localFile , false); // Cria o arquivo de saída relacionado ao seu respectivo arquivo de entrada ("mesmo" nome). 
        PrintWriter gravar = new PrintWriter(arq);
        if (erros.isEmpty()) { // Se não houver erros léxicos.
            gravar.printf("\nNao existem erros Sintaticos\n");
        } else { // Se houver erros léxicos, os insere no arquivo de saída.
            for (String erro : erros) {
                gravar.println("Erro: " + erro);
            }
        }
        arq.close();
	}
	
	public String getLocalFile(){
		return this.localFile;
	}

}
