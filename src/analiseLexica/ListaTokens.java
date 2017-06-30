package analiseLexica;

import java.util.ArrayList;

public class ListaTokens {

    private final ArrayList<String> palavrasReservadas = new ArrayList<>();
    private final ArrayList<Character> operadoresAritmeticos = new ArrayList<>();
    private final ArrayList<Character> operadoresRelacionais = new ArrayList<>();
    private final ArrayList<Character> operadoresLogicos = new ArrayList<>();
    private final ArrayList<Character> delimitadores = new ArrayList<>();
    private final ArrayList<Character> simbolos = new ArrayList<>();
    private final ArrayList<Character> letras = new ArrayList<>();
    private final ArrayList<Character> naoSimbolo = new ArrayList<>();

    public ListaTokens() {
        palavrasReservadas.add("program");
        palavrasReservadas.add("const");
        palavrasReservadas.add("var");
        palavrasReservadas.add("function");
        palavrasReservadas.add("begin");
        palavrasReservadas.add("end");
        palavrasReservadas.add("if");
        palavrasReservadas.add("then");
        palavrasReservadas.add("else");
        palavrasReservadas.add("while");
        palavrasReservadas.add("do");
        palavrasReservadas.add("read");
        palavrasReservadas.add("write");
        palavrasReservadas.add("integer");
        palavrasReservadas.add("real");
        palavrasReservadas.add("boolean");
        palavrasReservadas.add("true");
        palavrasReservadas.add("false");
        palavrasReservadas.add("string");
        palavrasReservadas.add("char");

        operadoresAritmeticos.add('+');
        operadoresAritmeticos.add('-');
        operadoresAritmeticos.add('*');
        operadoresAritmeticos.add('/');
        operadoresAritmeticos.add('%');

        operadoresRelacionais.add('<');
        operadoresRelacionais.add('=');
        operadoresRelacionais.add('>');

        operadoresLogicos.add('!');
        operadoresLogicos.add('&');
        operadoresLogicos.add('|');

        
        delimitadores.add(':');
        delimitadores.add(';');
        delimitadores.add(',');
        delimitadores.add('(');
        delimitadores.add(')');
        delimitadores.add('[');
        delimitadores.add(']');
        for (int i = 1; i <= 254; i++) {
            if (i < 32 || i > 126) {
                this.naoSimbolo.add((char) i);
            }
        }
        for (int i = 32; i <= 126; i++) {
            if (i != 34 || i != 92) {
                this.simbolos.add((char) i);
            }
        }

        for (char i = 'a'; i <= 'z'; i++) {
            this.letras.add((char) i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            this.letras.add((char) i);
        }

    }

    public boolean isnotSimbolo(char c) {
        return this.naoSimbolo.contains(c);
    }

    public boolean isPalavraResevada(String s) {
        return this.palavrasReservadas.contains(s);
    }

    public boolean isSimbolo(char c) {
        return this.simbolos.contains(c);
    }

    public boolean isLetra(char c) {
        return this.letras.contains(c);
    }

    public boolean isDelimitador(char c) {
        return this.delimitadores.contains(c);
    }

    public boolean isOperador(char c) {
        if (this.operadoresAritmeticos.contains(c) || this.operadoresRelacionais.contains(c) || this.operadoresLogicos.contains(c)) {
            return true;
        }
        return false;
    }

    public boolean isOperadorLogico(Character c) {
        return this.operadoresLogicos.contains(c);
    }

    public boolean isSpace(char ch) {
        return (Character.isSpaceChar(ch) || ch == 9);
    }

}
