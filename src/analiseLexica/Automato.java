package analiseLexica;

import java.util.ArrayList;
import Arquivo.Arquivo;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Automato {

    private ArrayList<String> codigoFonte = new ArrayList<>();
    private ArrayList<Token> tokens = new ArrayList<>();
    private ArrayList<String> erros = new ArrayList<>();
    private static final char EOF = '\0';
    private final ListaTokens Token = new ListaTokens();
    private int linha = 0;
    private int aux = 0;
    private boolean linhaVazia = false;
    private boolean nAnts = false;
    public boolean paratensesAberto = false;
    public int linhap;
    public int linhac;
    public boolean chavesAberta = false;

    public void analisadorLexico(ArrayList<String> codigo, String nomeDoArquivo) {
        this.codigoFonte = codigo;
        char c = proximo();
        while (c != EOF) {
            testaCaractere(c);
            c = this.proximo();
        }
        
    }

    private void testaCaractere(char c) {
        String lexema;
        if (!this.linhaVazia) {
            lexema = "";

            if (Token.isSpace(c)) {
                aux++;
            } else if (Token.isLetra(c)) {
                letras(lexema, c);
            } else if (Character.isDigit(c)) {
                this.digito(lexema, c);
            } else if (c == '/') {
                this.comentario(lexema, c);
            } else if (Token.isOperador(c)) {
                this.operador(lexema, c);
            } else if (Token.isDelimitador(c)) {
                this.delimitador(lexema, c);
            } else if (c == '\'') {
                this.caractere(lexema, c);
            } else if (c == '"') {
                cadeiaDeCaracteres(lexema, c);
            } else {
                this.palavraInvalida(lexema, c);
            }

        } else {
            linhaVazia = false;
            linha++;
        }
    }

    private void palavraInvalida(String lexema, char ch) {
        int linhaInicial = this.linha;
        int auxI = this.aux;

        while (!(ch == EOF || Character.isSpaceChar(ch) || Token.isOperador(ch) || Token.isDelimitador(ch) || ch == '\'' || ch == '"')) {
            lexema = lexema + ch;
            this.aux++;
            ch = this.proximo();
        }

        this.addErro("Palavra_invalida", lexema, linhaInicial);
    }

    private void cadeiaDeCaracteres(String lexema, char ch) {
        int linhaInicial = this.linha;
        int naoSimbolo = 0;
        int auxI = this.aux;
        boolean erro = false;
        char barra = ((char) 47);
        lexema = lexema + ch;
        this.aux++;
        ch = this.proximo();

        if (!(Character.isLetter(ch))) {
            erro = true;
        }

        while (ch != '"' && linha == linhaInicial && ch != EOF) {
            if (ch == barra) {
                this.aux++;
                lexema = lexema + ch;
                ch = this.proximo();
                if (ch == '"') {
                    lexema = lexema + ch;
                    ch = this.proximo();
                    this.aux++;
                }
            } else if (Token.isnotSimbolo(ch)) {
                this.aux++;
                lexema = lexema + ch;
                ch = this.proximo();
                erro = true;
                naoSimbolo = 1;
            } else if (Character.isLetterOrDigit(ch) || Token.isSimbolo(ch)) {
                this.aux++;
                lexema = lexema + ch;
                ch = this.proximo();
            } else {
                this.aux++;
                lexema = lexema + ch;
                ch = this.proximo();
                erro = true;
            }
        }

        if (ch == '"' && linhaInicial == this.linha) {
            lexema += ch;
            this.aux++;
        }

        if (!erro && linhaInicial == this.linha) {
            Token token;
            token = new Token(linhaInicial + 1, auxI + 1, "string", lexema);
            this.tokens.add(token);
        } else if (naoSimbolo == 1) {
            this.addErro("Cadeia_de_caracteres_mal_formada", lexema, linhaInicial);
        } else {
            this.addErro("Cadeia_de_caracteres_mal_formada", lexema, linhaInicial);
        }
    }

    private void comentario(String lexema, char ch) {
        int linhaInicial = this.linha;
        int auxI = this.aux;

        lexema = lexema + ch;
        this.aux++;
        ch = this.proximo();

        if (ch == '/') {
            lexema = lexema + ch;
            Token token = new Token(linhaInicial + 1, auxI + 1, "Comentario", lexema);
            this.tokens.add(token);
            this.aux--;
            linha++;
            return;
        } else if (ch == '*') {
            lexema = lexema + ch;
            ch = this.proximo();
            this.aux++;

            lexema = lexema + ch;
            ch = this.proximo();
            this.aux++;

            do {
                while (ch != '*' && ch != EOF) {
                    ch = this.proximo();
                    this.aux++;
                }
            } while (ch != EOF && ch != '*');

            ch = this.proximo();
            this.aux++;

            if (ch == EOF) {
                this.addErro("Comentario_mal_formado", "Faltou */", linhaInicial);
                return;
            }
            if (ch != '/') {
                this.addErro("Comentario_mal_formado", "Faltou /", linhaInicial);
                lexema = lexema + ch;
            }
            if (ch == '/') {
                lexema = lexema + ch;
                Token token = new Token(linhaInicial + 1, auxI + 1, "Comentario", lexema);
                this.tokens.add(token);

            }

        } else {
            Token token = new Token(linhaInicial + 1, auxI + 1, "Operador_Arimetico", lexema);
            this.tokens.add(token);
        }

    }

    private void delimitador(String lexema, char ch) {

        int linhaInicial = this.linha;
        int auxI = this.aux;

        lexema = lexema + ch;
        this.aux++;

        Token token = new Token(linhaInicial + 1, auxI + 1, "delimitador", lexema);
        this.tokens.add(token);

    }

    private void caractere(String lexema, char ch) {
        int linhaInicial = this.linha;
        int auxI = this.aux;
        boolean erro = false;
        char barra = ((char) 47);

        lexema = lexema + ch;
        this.aux++;
        ch = this.proximo();
        if (Token.isLetra(ch) || Token.isSpace(ch) || Character.isDigit(ch)) {
            lexema = lexema + ch;
            this.aux++;
            ch = this.proximo();
        }
        if(ch == '\''){
            lexema = lexema + ch;
        }

        
        if (!erro) {
            Token token;
            token = new Token(linhaInicial + 1, auxI + 1, "char", lexema);
            tokens.add(token);
            this.aux++;
        } else {
            if (linhaInicial == this.linha) {
                lexema = lexema + ch;
                this.aux++;
            }

            this.addErro("Caractere_Invalido", lexema, linhaInicial);
        }
    }

    private void operador(String lexema, char ch) {
        int linhaInicial = this.linha;
        int auxI = this.aux;
        boolean retorno = false;
        boolean aritmetico = false;
        boolean logico = false;
        int espaco = 0;
        lexema = lexema + ch;
        this.aux++;

        if (ch == '+' || ch == '%' || ch == '*') {
            aritmetico = true;
            ch = this.proximo();
            if (ch == '-') {

            }
        } else if (ch == '-' && !(nAnts)) {
            aritmetico = true;
            ch = this.proximo();
            while (Token.isSpace(ch)) {
                ch = this.proximo();
                this.aux++;
                espaco = 1;
            }
            if (espaco == 1) {
                this.aux--;
            }
            if (Character.isDigit(ch)) {
                this.digito(lexema, ch);
                return;
            }
            else if(ch == '>'){
                lexema = lexema + ch;
                this.aux++;
                retorno = true;
            }

        } else if (ch == '<') {
            ch = this.proximo();
            if (ch == '=' || ch == '>') {
                lexema = lexema + ch;
                this.aux++;
            }
        } else if (ch == '>') {
            ch = this.proximo();
            if (ch == '=') {
                lexema = lexema + ch;
                this.aux++;

            }
        } else if (ch == '!') {
            ch = this.proximo();
            if (ch == '=') {
                lexema = lexema + ch;
                this.aux++;
            } else {
                
                logico = true;
            }
        } else if (ch == '&') {
            ch = this.proximo();
            if (ch == '&') {
                lexema = lexema + ch;
                logico = true;
                this.aux++;
            } else {
                this.addErro("Operador_logico_mal_formado", lexema, linhaInicial);
                return;
            }
        } else if (ch == '|') {
            ch = this.proximo();
            if (ch == '|') {
                lexema = lexema + ch;
                logico = true;
                this.aux++;
            } else {
                this.addErro("Operador_logico_mal_formado", lexema, linhaInicial);
                return;
            }
        }
        else if (ch == '='){
            ch = this.proximo();
            if(ch == '='){
                lexema = lexema + ch;
                this.aux ++;
            }
        }

        Token token;
        if(retorno){
            token = new Token(linhaInicial + 1, auxI + 1, "retorno", lexema);
        }
         else if(aritmetico) {
            token = new Token(linhaInicial + 1, auxI + 1, "operador aritmetico", lexema);
        }
         else if (logico) {
            token = new Token(linhaInicial + 1, auxI + 1, "operador logico", lexema);
        }
        else {
            token = new Token(linhaInicial + 1, auxI + 1, "operador relacional", lexema);
            
        }

        tokens.add(token);

    }

    private void digito(String lexema, char ch) {
        int linhaInicial = linha;
        int auxI = aux;
        boolean isPonto = false;
        boolean erro = false;
        int neg=0;
        
        
        lexema = lexema + ch;
        this.aux++;
        ch = this.proximo();
        while (!(ch == EOF || Character.isSpaceChar(ch) || Token.isOperador(ch) || Token.isDelimitador(ch) || ch == '\'' || ch == '"' )) {

            if (!(Character.isDigit(ch)) && ch != '.') {
                erro = true;
                lexema = lexema + ch;
                aux++;
                ch = this.proximo();
            } else if (Character.isDigit(ch)) {
                lexema = lexema + ch;
                aux++;
                ch = this.proximo();
            } else if (ch == '.' && isPonto == false) {
                lexema = lexema + ch;
                aux++;
                isPonto = true;
                ch = this.proximo();
                if (!(Character.isDigit(ch))) {
                    erro = true;
                }
            } else {
                erro = true;
                lexema = lexema + ch;
                aux++;
                ch = this.proximo();
            }
        }
        if (!erro) {
            Token token;
             if (lexema.length() > 1) {
                token = new Token(linhaInicial + 1, auxI + 1, "numero", lexema);
            } else {
                token = new Token(linhaInicial + 1, auxI + 1, "numero", lexema);
            }
            tokens.add(token);
        } else {
            addErro("Numero_Invalido", lexema, linhaInicial);
        }
    }

    private void letras(String lexema, char ch) {
        int linhaInicial = linha;
        int auxI = aux;
        boolean erro = false;

        lexema = lexema + ch;
        this.aux++;
        ch = this.proximo();

        while (!(ch == EOF || Character.isSpaceChar(ch) || Token.isDelimitador(ch) || Token.isOperador(ch) || ch == '\'' || ch == '"' )) {

            if (ch == '�' || ch == '�') {
                erro = true;
            }
            if (!(ch == '_' || Token.isLetra(ch) || Character.isDigit(ch))) {
                erro = true;
            }
            lexema = lexema + ch;
            aux++;
            ch = this.proximo();
        }
            
        if (!erro) {
            Token token;
            if (Token.isPalavraResevada(lexema)) {
                token = new Token(linhaInicial + 1, auxI + 1, "palavra reservada", lexema);
            } else {
                token = new Token(linhaInicial + 1, auxI + 1, "Identificador", lexema);
            }
            tokens.add(token);
        } else {
            this.addErro("Identificador_incorreto", lexema, linhaInicial);
        }
    }

    private void addErro(String tipo, String erro, int linha) {
        erros.add((linha + 1) + "  " + erro + "  " + tipo + "  ");
    }

    private char proximo() {
        if (!codigoFonte.isEmpty()) {
            char c[] = codigoFonte.get(linha).toCharArray();
            if (c.length == aux) {
                linhaVazia = false;
                return ' ';
            } else if (c.length > aux) {
                linhaVazia = false;
                return c[aux];
            } else if (codigoFonte.size() > (linha + 1)) {
                linha++;
                c = codigoFonte.get(linha).toCharArray();
                aux = 0;
                if (c.length == 0) {
                    this.linhaVazia = true;
                    return ' ';
                }
                return c[aux];
            } else {
                return EOF;
            }
        } else {
            return EOF;
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<String> getErros() {
        return erros;
    }
    public void Semantico(){
        
    }
  

}
