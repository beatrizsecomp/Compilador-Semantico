package Principal;

import Arquivo.Arquivo;
import analiseLexica.Automato;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import analiseSintatica.AnalisadorSintatico;
import analiseLexica.Token;
/**
 *
 * @author user
 */
public class Analise {
       public static void main(String[] args) throws FileNotFoundException, IOException {
        Arquivo arquivo = new Arquivo();
        Automato lexico = new Automato();
        ArrayList<String> codigos = new ArrayList<>();
        codigos = arquivo.lerCodigos();
        AnalisadorSintatico sintatico = new AnalisadorSintatico();
		
        if (codigos.isEmpty()) {
            System.out.println("Coloque o codigo na pasta de Entradas");
            System.exit(0);
        }
        for (String codigo : codigos) {
            lexico = new Automato();
            ArrayList<String> codigoFonte = arquivo.lerCodigoFonte(codigo);
            codigoFonte = arquivo.lerCodigoFonte(codigo);
            System.out.println("Iniciando a analise lexica...");
            lexico.analisadorLexico(codigoFonte, arquivo.getLocalFile());
            
            arquivo.gravaSaida(lexico.getTokens(), lexico.getErros());

            System.out.println("Analise Lexica feita com sucesso!");
            if (lexico.getErros().isEmpty()) {
                System.out.println("Nao foram encontrados erros");
            } else {
                System.out.println("Arquivo de saida contem os erros e lexemas");
            }
            System.out.println(" ");
            
            sintatico = new AnalisadorSintatico();
			ArrayList<Token> listaTokens = lexico.getTokens();
			System.out.println("Iniciando a analise sintatica ... ");
			System.out.println("Analisando: " + arquivo.getLocalFile());
			sintatico.analise(listaTokens);
			arquivo.gravaSaidaSintatico(sintatico.getErros());
			
        }
       }
}
