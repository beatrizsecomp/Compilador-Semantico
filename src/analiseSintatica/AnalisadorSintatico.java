package analiseSintatica;

import java.util.ArrayList;
import analiseLexica.Token;

public class AnalisadorSintatico {

    private Token token; // Proximo token da lista
    private ArrayList<Token> tokens;    //lista com os tokens recebidos
    private ArrayList<String> erros;    //lista com os erros encontrados na an�lise.
    private int contTokens = 0;        
    private int aux = 0;
    private int atterror=0;

    public void analise(ArrayList<Token> tokens) {

        this.tokens = tokens; //recebe os tokens vindos do lexico.
        token = proximo();  //recebe o primeiro token da lista
        erros = new ArrayList<>(); //cria a lista de erros

        programa();

        if (!erros.isEmpty()) {
            System.out.println("Ocorreram erros na analise sintatica");
            System.out.println(erros); //imprime os erros na tela
        } else {
            System.out.println("Analise Sintatica feita com sucesso\n");
        }

    }
    // Classe que inicializa o programa, verifica se exite a palavra program, se não existir nem executa o resto das verificacoes
    private void programa() {
        if (token.getLexema().equals("program")) {
            token = proximo();
            blocos();
        } else {
            erroSintatico("Esperava Program");
        }
    }
    // Classe responsavel por chamar os blocos permitidos fora da funcao
    private void blocos() {
        if (token.getLexema().equals("var")) {
            blocoVar();
        }
        if (token.getLexema().equals("const")) {
            blocoConst();
        }
        if (token.getLexema().equals("function")) {
            Funcao();
        } else {
            if (!token.getLexema().equals("EOF")) {
                erroSintatico("Esperava um: bloco de variavel|bloco de constates|bloco de funcao");
                token = proximo();
                if (!token.getLexema().equals("EOF")) {
                    blocos();
                }
            }
        }
    }

    private void erroSintatico(String erro) {
        if (!token.getLexema().equals("EOF")) {
            erros.add("Linha: " + (token.getLinha()) + " " + erro + "\n"); //gera o erro normalizado e adiciona na lista de erros.
        } else {
            erros.add(erro);
        }
    }

    private void terminal(String esperado) {
        if ((!token.getLexema().equals("EOF")) && token.getLexema().equals(esperado)) { //verifica se o token atual e o que era esperado
            token = proximo();
        } else {
            erroSintatico("Falta o token: " + esperado);   //gera o erro se o token nao e o esperado
        }
    }

    private Token proximo() {
        if (contTokens < tokens.size()) { //verifica se ainda possuem tokens para a analise.
            return tokens.get(contTokens++);
        } else {
            return new Token(0, 0, "EOF", "EOF");  //cria um token de fim de arquivo.
        }
    }

    public ArrayList<String> getErros() {
        return this.erros;
    }
    //Bloco Variaveis globais
    private void blocoVar() {
        token = proximo();
        if (token.getLexema().equals("begin")) {
            token = proximo();
            corpoVar();
        } else {
            erroSintatico("Bloco de variavel nao possui begin");
            if (!(token.getLexema().equals("const") || token.getLexema().equals("function") || token.getLexema().equals("end"))) {
                corpoVar();
            }

            if (token.getLexema().equals("end")) {
                token = proximo();
            }
        }
        if (token.getLexema().equals("const")) {
            blocoConst();
        }
        if (token.getLexema().equals("function")) {
            Funcao();
        }
    }

    private void corpoVar() {
        while (!(token.getLexema().equals("end") || token.getLexema().equals("const") || token.getLexema().equals("function"))) {
            if (tipo()) {
                declaracaoVariaveis();
                if (token.getLexema().equals("end") || token.getTipo().equals("Identificador") || token.getTipo().equals(",") || token.getTipo().equals(";")) {
                    break;
                }
            } else {
                erroSintatico("Falta palavra reservada: integer, cadeia, real, boolean, char");
                if (token.getTipo().equals("Identificador")) {
                    padraoVariavel();
                    if (!(pontoVirgula())) {
                        erroSintatico("Faltou ; "); //gera o erro se o tipo do token nao e o esperado
                        if (token.getLexema().equals("end")) {
                            return;
                        }
                        if (tipo()) {
                            declaracaoVariaveis();
                        }
                    }
                } else if (token.getLexema().equals(",")) {
                    erroSintatico("Esperava um Id");
                    token = proximo();
                    padraoVariavel();
                } else {
                    token = proximo();
                    if (token.getTipo().equals("Identificador") || token.getLexema().equals("EOF") || token.getLexema().equals("const") || token.getLexema().equals("function")) {
                        if (token.getTipo().equals("Identificador")) {
                            erroSintatico("Esperava um tipo primitivo" + token.getLexema());
                            padraoVariavel();
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        if (token.getTipo().equals("Identificador")) {
            erroSintatico("Esperava um tipo primitivo");
            declaracaoVariaveis();

        } else if (!token.getLexema().equals("end")) {
            erroSintatico("Bloco de variaveis nao possui end");
        } else {
            token = proximo();
        }

    }   //id(complemento);

    private void declaracaoVariaveis() {
        padraoVariavel();
        // Caso ache um tipo apos um id e pq faltou ;   
        if (tipo()) {
            erroSintatico("Faltou ; ");
            declaracaoVariaveis();
        } else if (!(pontoVirgula())) {
            erroSintatico("Faltou ; "); //gera o erro se o tipo do token nao e o esperado
            if (token.getLexema().equals("end")) {
                return;
            }
            if (tipo()) {
                declaracaoVariaveis();
            } else if (token.getTipo().equals("Identificador")) {
                erroSintatico("Esperava um tipo primitivo aqui");
                padraoVariavel();
            } else {
                erroSintatico("Esperava tipo primitivo " + token.getLexema());
                token = proximo();
            }
        }

    }
    // id ou id,id ou id[1] ou id =1
    private void padraoVariavel() {
        if (identificador("Identificador")) {
            opcVariaveisGlo();
        } else {
            if (token.getLexema().equals("=")) {
                token = proximo();
                igualdadeVarGlo();
            } else {
                if (!token.getLexema().equals(";")) {
                    erroSintatico("Lexema nao esperado: " + token.getLexema());
                    token = proximo();
                }
            }

        }
        if (token.getLexema().equals(",")) {
            token = proximo();
            padraoVariavel();
        } else if (token.getTipo().equals("Identificador")) {
            erroSintatico("Esperava ,");
            padraoVariavel();
        }
    }

    private void opcVariaveisGlo() {
        if (token.getLexema().equals("[")) {
            declaracaoMatriz();
        }
        if (token.getLexema().equals("=")) {
            token = proximo();
            igualdadeVarGlo();
        }
    }

    private void igualdadeVarGlo() {
        if (token.getLexema().equals("[")) {
            atribuicaoMatriz();
        } else if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
            token = proximo();
        } else {
            erroSintatico("Atribuicao incorreta: " + token.getLexema());
            if (!(token.getLexema().equals(",") || token.getLexema().equals(";") || token.getLexema().equals("end"))) {
                token = proximo();
            }
        }

    }

    private void opcVariavel() {
        if (token.getLexema().equals("[")) {
            declaracaoMatriz();
        }
        if (token.getLexema().equals("=")) {
            token = proximo();
            igualdade();
        }  if (token.getLexema().equals(",")) {
            virgulaL();
        } else if (token.getLexema().equals(";")) {

        }
    }

    private void igualdade() {
        if (token.getLexema().equals("[")) {
            atribuicaoMatriz();
        } //Se for uma atribuicao normal
        else if (token.getTipo().equals("Identificador") || token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")||token.getLexema().equals("(")) {
            if (token.getTipo().equals("numero") || token.getTipo().equals("Identificador")||token.getLexema().equals("(")) {
                expLogica(0); 
            } else {
                token = proximo();
                if (!token.getLexema().equals(";")) {
                    erroSintatico("Atribuicao incorreta: " + token.getLexema());
                    while (!(verifica() || token.getLexema().equals(";") || token.getLexema().equals("EOF"))) {
                        erroSintatico("Atribuicao incorreta: " + token.getLexema());
                    }
                }

            }

        } //Se for uma declaracao de variaveis locais pode receber um id e chamada de funcoes
        else {
            erroSintatico("atribuicao de tipo incompativel"); //gera o erro se o tipo do token nao e o esperado
        }

    }

    private boolean pontoVirgula() {
        if (token.getLexema().equals(";")) {
            token = proximo();
            return true;
        } else {
            return false;
        }
    }

    private void blocoConst() {
        token = proximo();
        if (token.getLexema().equals("begin")) {
            token = proximo();
            corpoConst();
        } else {
            erroSintatico("Bloco de constates nao possui begin");
            if (!(token.getLexema().equals("function") || token.getLexema().equals("end"))) {
                corpoConst();
            }

        }
        if (token.getLexema().equals("end")) {
            token = proximo();
        }
        if (token.getLexema().equals("function")) {
            Funcao();
        }
    }

    private void corpoConst() {
        while (!(token.getLexema().equals("end") || token.getLexema().equals("function") || token.getLexema().equals("EOF"))) {
            if (tipo()) {
                declaracao_const();
                if (token.getLexema().equals("end") || token.getTipo().equals("Identificador") || token.getTipo().equals(",") || token.getTipo().equals(";") || token.getLexema().equals("function")) {
                    if (!token.getLexema().equals("end")) {
                        erroSintatico("Esperava end");
                    }
                    break;
                } else {
                    token = proximo();
                }
            } else {
                erroSintatico("Esperava um tipo");
                if (token.getTipo().equals("Identificador")) {
                    declaracao_const();
                } else if (token.getLexema().equals(",")) {
                    erroSintatico("Esperava um id");
                    token = proximo();
                    declaracao_const();
                }
            }
            if (token.getLexema().equals("end")) {
                token = proximo();
            } else {
                erroSintatico("Bloco de constantes nao possui end");
            }
            if (token.getLexema().equals("function")) {
                Funcao();
            }
        }

    }

    private void declaracao_const() {
        padraoConst();
        if (token.getLexema().equals(";")) {
            token = proximo();
            if (tipo()) {
                declaracao_const();
            }
        } else {
            erroSintatico("Esperava ;");
            if (tipo()) {
                declaracao_const();
            }
        }

    }

    private void padraoConst() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            if (token.getLexema().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexema().equals("=")) {
                token = proximo();
                if (token.getLexema().equals("[")) {
                    atribuicaoMatriz();
                } else if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
                    token = proximo();
                } else {
                    erroSintatico("atribuicao de tipo incompativel"); //gera o erro se o tipo do token nao e o esperado
                    if (tipo()) {
                        declaracao_const();
                    } else if (!(token.getLexema().equals("end") || token.getLexema().equals(";"))) {
                        token = proximo();
                        if (token.getLexema().equals(",")) {
                            token = proximo();
                            padraoConst();
                        }
                    }
                }
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    padraoConst();
                }
            } else {
                erroSintatico("Constantes precisam ser atribuidas"); //gera o erro se o tipo do token nao e o esperado
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    padraoConst();
                }
            }

        } else {
            erroSintatico("Esperava Id");
            if (tipo()) {
                declaracao_const();
            }
            if (token.getLexema().equals("=")) {
                erroSintatico("Atribuicao incorreta");
                token = proximo();
                if (!(token.getLexema().equals(",") || token.getLexema().equals(";"))) {
                    token = proximo();
                    if (token.getLexema().equals(",")) {
                        token = proximo();
                        padraoConst();
                    }
                } else {
                    if (token.getLexema().equals(",")) {
                        token = proximo();
                        padraoConst();
                    }
                }
            } else {
                if (!token.getLexema().equals(";")) {
                    token = proximo();
                }
            }
        }

    }

    //Estrutura de uma funcao
    private void blocoFuncao() {
        token = proximo(); // pega o proximo que deveria ser um ID
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            idFuncao();
        } else {
            erroSintatico("A funcao deve ser incializada com um nome");
            if (token.getLexema().equals("(")) {
                idFuncao();
            } else if (token.getLexema().equals(")")) {
                erroSintatico("Esperava (");
                parentesesF();
            } else if (token.getLexema().equals("integer") || token.getLexema().equals("real") || token.getLexema().equals("char") || token.getLexema().equals("string") || token.getLexema().equals("boolean")) {
                erroSintatico("Esperava (");
                parentesesF();
            } else if (token.getLexema().equals("begin")) {
                erroSintatico("Esperava (parametros)");
                fBegin();
            } else if (token.getLexema().equals(":")) {
                erroSintatico("Esperava (parametros)");
                temRetorno();
                if (token.getLexema().equals("begin")) {
                    fBegin();
                } else {
                    erroSintatico("Esperava begin");
                    while (!(token.getLexema().equals("end") || token.getLexema().equals("EOF") || token.getLexema().equals("function"))) {
                        erroSintatico("E necessario inicializar a funcao");
                        token = proximo();
                    }
                }
            } else if (token.getLexema().equals("end")) {
                erroSintatico("Esperava: (parametros)begin ");
                token = proximo();
            } else {
                if (!verifica()) {
                    //token = proximo();
                    if (!(token.getLexema().equals("EOF") || token.getLexema().equals("end"))) {
                        idFuncao();
                    }
                }
            }

        }
    }

    private void idFuncao() {
        if (token.getLexema().equals("(")) {
            token = proximo();
            parentesesF();
        } else {
            erroSintatico("Faltou ( na funcao");
            if (token.getLexema().equals(")")) {
                parentesesF();
            } else if (token.getLexema().equals("integer") || token.getLexema().equals("real") || token.getLexema().equals("char") || token.getLexema().equals("string") || token.getLexema().equals("boolean")) {
                parentesesF();
            } else if (token.getLexema().equals("begin")) {
                erroSintatico("Esperava (parametros)");
                fBegin();
            } else if (token.getLexema().equals(":")) {
                erroSintatico("Esperava (parametros)");
                temRetorno();
                if (token.getLexema().equals("begin")) {
                    fBegin();
                } else {
                    erroSintatico("Esperava begin");
                    while (!(token.getLexema().equals("end") || token.getLexema().equals("EOF") || token.getLexema().equals("function"))) {
                        erroSintatico("E necessario inicializar a funcao");
                        token = proximo();
                    }
                }
            } else if (token.getLexema().equals("end")) {
                erroSintatico("Esperava: (parametros)begin ");
                token = proximo();
            } else {
                if (!verifica()) {
                    token = proximo();
                    if (!(token.getLexema().equals("EOF") || token.getLexema().equals("end"))) {
                        idFuncao();
                    }
                }
            }

        }
    }

    private void parentesesF() {
        if (!token.getLexema().equals(")")) {
            parametroFuncao();
        }
        if (token.getLexema().equals(")")) {
            token = proximo();
            if (token.getLexema().equals("integer") || token.getLexema().equals("real") || token.getLexema().equals("char") || token.getLexema().equals("string") || token.getLexema().equals("boolean")) {
                erroSintatico("Retorno incorreto, faltou :");
                token = proximo();
            }
        } else {
            erroSintatico("Faltou ) na funcao");
        }
        if (token.getLexema().equals(":")) {
            temRetorno();
        }
        if (!(token.getLexema().equals(":") || token.getLexema().equals("begin"))) {
            erroSintatico("Esperava begin ou retorno");
            token = proximo();
        }
        if (token.getLexema().equals("begin")) {
            fBegin();

        } else {
            erroSintatico("Faltou begin na funcao");
        }
    }

    private void fBegin() {
        token = proximo();
        bloco();
        while (!(token.getLexema().equals("EOF") || token.getLexema().equals("end") || token.getLexema().equals("function"))) {
            bloco();
        }
        if (token.getLexema().equals("function")) {
            erroSintatico("Funcoes nao podem ser declaradas dentro de funcoes");
            Funcao();
        }
        if (token.getLexema().equals("end")) {
            token = proximo();
        } else {
            erroSintatico("Faltou end na funcao");
        }
    }
    //Contem tudo que pode estar dentro de uma funcao
    private void bloco() {
        if (token.getLexema().equals("read")) {
            read();
        } else if (token.getLexema().equals("write")) {
            write();
        } else if (token.getTipo().equals("Identificador")) {
            opcId();
        } else if (token.getLexema().equals("if")) {
            ifElse();
        } else if (token.getLexema().equals("while")) {
            _while();
        } else if (tipo()) {
            nvariaveisL();
        }else if(token.getTipo().equals("numero")){
            exp2();
        }else if(token.getLexema().equals("true")||token.getLexema().equals("false")){
            token = proximo();
            if(token.getLexema().equals(";")){
                token = proximo();
            }else{
                erroSintatico("Esperava ;");
            }
        }else if (token.getLexema().equals("function")) {
            erroSintatico("Funcoes nao devem ser inicializadas dentro de funcoes");
            Funcao();
        } else if (token.getLexema().equals("EOF")) {

        } // Esta aceitando bloco vazio
        else if (token.getLexema().equals("end")) {

        } else {
            erroSintatico("Esperava componente do bloco, token: " + token.getLexema());
            token = proximo();
        }
    }
    private void exp2(){
        expLogica(0);
        if (!(pontoVirgula())) {
                        erroSintatico("Faltou ; na atribuicao");
                    }
    }
    private void nvariaveisL() {
        variaveisLocais();
        if (token.getLexema().equals(";")) {
            token = proximo(); 
        } else {
            erroSintatico("Faltou ; ");
        }
    }

    private void variaveisLocais() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            opcVariavel();
        } else {
            erroSintatico("Esperava Id");
        }

    }

    private void virgulaL() {
        
        if (token.getLexema().equals(",")) {
            token = proximo();
            variaveisLocais();
        }
    }

    private void opcId() {
        if (token.getTipo().equals("Identificador")) {
            //token = proximo();
            this.atterror=0;
            expLogica(0);
            if (token.getLexema().equals("[")) {
                declaracaoMatriz();
            if (token.getLexema().equals("=")) {
                if(atterror!=0){
                    erroSintatico("Atribuicao incorreta");
                }    
                token = proximo();
                    if (atId()) {
                    } else {
                        erroSintatico("Atribuicao incorreta");
                    }
                    if (!(pontoVirgula())) {
                        erroSintatico("Faltou ; na atribuicao");
                    }
                }
            }
            if (token.getLexema().equals("(")) {
                callFunction(0);
            } else if (token.getLexema().equals("->")) {
                token = proximo();
                retorno();
            } else if (token.getLexema().equals(";")) {
                token = proximo();
            } else if (token.getLexema().equals("=")) {
                if(atterror!=0){
                    erroSintatico("Atribuicao incorreta");
                }
                token = proximo();
                if (atId()) {
                } else {
                    erroSintatico("Atribuicao incorreta");
                }
                if (!(pontoVirgula())) {
                    erroSintatico("Faltou ; na atribuicao");
                }
            }
        } else {
            erroSintatico("Esperava um Id");
        }
    }
    // a=a[1]; n=1 n=1+1 n="char" 
    private boolean atId() {
        if (token.getLexema().equals("[")) {
            atribuicaoMatrizLocal();
            return true;
        }
        if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals("-")||token.getLexema().equals("!") ||token.getTipo().equals("Identificador") || token.getTipo().equals("numero") || token.getLexema().equals("(")) {
           expLogica(0);
            return true;
        } else {
            return false;
        }
    }
    //Falta parenteses
    private void expID(){
        idLogic();
        if((token.getLexema().equals("||")||token.getLexema().equals("&&"))&&aux==0){
            token = proximo();
            if(!idLogic()){
                erroSintatico("Esperava uma expressao relacional ");
            }
        }
    }
    private boolean idLogic(){
        if(exp()){
            if(operadorRelacional()){
              if(exp()){
                 return true;
               }else{
                erroSintatico("Esperava expressão");
            }   
            }
            return false;
        }
        return false;
    }
    private void atribuicaoMatrizLocal() {
        token = proximo();
        if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true") || token.getTipo().equals("Identificador")) {
            if (token.getTipo().equals("numero") || token.getTipo().equals("Identificador")) {
                expLogica(0);
            } else {
                token = proximo();
            }

            if (token.getLexema().equals(",")) {
                atribuicaoMatrizLocal();
            } else if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
                erroSintatico("Esperava ,");
                token = proximo();
                atribuicaoMatrizLocal();
            } else {
                if (token.getLexema().equals("]")) {
                    token = proximo();
                } else {
                    while (!(token.getLexema().equals("]") || token.getLexema().equals("EOF") || token.getLexema().equals(",") || token.getLexema().equals(";") || token.getLexema().equals("end"))) {
                        erroSintatico("Esperava , ou ]");
                        token = proximo();
                    }
                    if (token.getLexema().equals(";")) {
                        erroSintatico("Esperava ]");
                    }
                    if (token.getLexema().equals("]")) {
                        token = proximo();
                    }
                }
            }
        } else {
            erroSintatico("atribuicao de matriz incorreta");
            if (token.getLexema().equals("]")) {
                token = proximo();
            } else {
                while (!(token.getLexema().equals("]") || token.getLexema().equals("EOF") || token.getLexema().equals(",") || token.getLexema().equals(";"))) {
                    erroSintatico("Esperava ]");
                    token = proximo();
                }
                if (token.getLexema().equals("]")) {
                    token = proximo();
                } else if (token.getLexema().equals(",")) {
                    atribuicaoMatrizLocal();
                }
            }
        }

    }
    
    private void estruturaCondicional() {
        if (token.getLexema().equals("begin")) {
            token = proximo();
            while (!(token.getLexema().equals("EOF") || token.getLexema().equals("end") || token.getLexema().equals("function"))) {
                bloco();
            }
            if (token.getLexema().equals("function")) {
                erroSintatico("Funcoes nao podem ser declaradas dentro de funcoes");
                Funcao();
            }
            if (token.getLexema().equals("end")) {
                token = proximo();
            } else {
                erroSintatico("Esperava end");
            }
        } else {
            erroSintatico("Esperava begin");
            if (verifica()) {
                while (!(token.getLexema().equals("EOF") || token.getLexema().equals("end") || token.getLexema().equals("function"))) {
                    bloco();
                }
               if (token.getLexema().equals("end")) {
                    token = proximo();
                } else {
                    erroSintatico("Esperava end");
                }
            }
        }
    }

    private void _while() {
        if (token.getLexema().equals("while")) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                token = proximo();
                if (token.getLexema().equals(")")) {
                    erroSintatico("Esperava parametro");
                }
                parametrosIf();
                if (token.getLexema().equals(")")) {
                    token = proximo();
                    if (token.getLexema().equals("do")) {
                        token = proximo();
                        estruturaCondicional();
                    } else {
                        erroSintatico("Esperava do");
                        if(token.getLexema().equals("begin")){
                        estruturaCondicional();    
                        }else{
                            token = proximo();
                            estruturaCondicional();
                        }
                         }
                } else {
                    erroSintatico("Esperava )");
                    if(token.getLexema().equals("do")){
                        token = proximo();
                        estruturaCondicional();
                    }else {
                        erroSintatico("Esperava do");
                        if(token.getLexema().equals("begin")){
                        estruturaCondicional();    
                        }else{
                            token = proximo();
                            estruturaCondicional();
                        }
                         }
                }
            } else {
                erroSintatico("Esperava (");
                if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
                    parametrosIf();
                    if (token.getLexema().equals(")")) {
                        token = proximo();
                        if (token.getLexema().equals("do")) {
                            token = proximo();
                            estruturaCondicional();
                        } else {
                            erroSintatico("Esperava do");
                        }
                    } else {
                        erroSintatico("Esperava )");
                    }
                } else if (token.getLexema().equals(")")) {
                    erroSintatico("Esperava parametro");
                    fecP();
                }
            }
        }

    }

    private void fecP() {
        if (token.getLexema().equals(")")) {
            token = proximo();
            if (token.getLexema().equals("do")) {
                token = proximo();
                estruturaCondicional();
            } else {
                erroSintatico("Esperava do");
            }
        } else {
            erroSintatico("Esperava )");
        }
    }

    private void ifElse() {
        _If();
        _Else();

    }

    private void _If() {
        if (token.getLexema().equals("if")) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                token = proximo();
                if (token.getLexema().equals(")")) {
                    erroSintatico("Esperava parametros if");
                }
                parametrosIf();
                fechaPar();
            } else {
                erroSintatico("Esperava (");
                if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
                    parametrosIf();
                    fechaPar();
                } else if (token.getLexema().equals(")")) {
                    erroSintatico("Esperava parametros do if");
                    token = proximo();
                    if (token.getLexema().equals(";")) {
                        token = proximo();
                    } else {
                        erroSintatico("Esperava ;");
                    }
                }
            }
        }

    }

    private void fechaPar() {
        if (token.getLexema().equals(")")) {
            token = proximo();
            if (token.getLexema().equals("then")) {
                token = proximo();
                estruturaCondicional();
            } else {
                erroSintatico("Esperava then");
                if(token.getLexema().equals("begin")){
                  estruturaCondicional();  
                }else{
                    token = proximo();
                    estruturaCondicional();
                }
                
            }

        } else {
            erroSintatico("Esperava ) ");
            if (token.getLexema().equals("then")) {
                token = proximo();
                estruturaCondicional();
            } else {
                erroSintatico("Esperava then");
                estruturaCondicional();
            }
        }
    }

    private void _Else() {
        if (token.getLexema().equals("else")) {
            token = proximo();
            estruturaCondicional();
        }
    }

    //Opcoes de parametros if ou while
    private void parametrosIf() {
        //expRelacional();
        expLogica(1);
    }

    private void parametroFuncao() {
        if (tipoPrimitivo()) {
            token = proximo();
            if (token.getTipo().equals("Identificador")) {
                token = proximo();
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    parametroFuncao();
                } else if (!token.getLexema().equals(")")) {
                    parametroFuncao();
                }
            } else {
                erroSintatico("Parametro de funcao incorreto");
            }
        } else {
            if (token.getTipo().equals("Identificador")) {
                token = proximo();
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    parametroFuncao();
                }
            } else {
                erroSintatico("Parametro incorreto: " + token.getLexema());
                token = proximo();
                if (token.getTipo().equals("Identificador")) {
                    token = proximo();
                    if (token.getLexema().equals(",")) {
                        token = proximo();
                        parametroFuncao();
                    }
                }
                if (!(token.getLexema().equals(":") || token.getLexema().equals(")") || token.getLexema().equals("EOF") || token.getLexema().equals("function"))) {
                    parametroFuncao();
                }
                if (token.getLexema().equals("function")) {
                    Funcao();
                }
            }
        }
    }

    private void temRetorno() {
        token = proximo();
        if (tipoPrimitivo()) {
            token = proximo();
        }
    }

    private void retorno() {
        if (token.getLexema().equals("(")) {
            token = proximo();
            parametroRetorno();

            if (token.getLexema().equals(")")) {
                token = proximo();
            } else {
                erroSintatico("Esperava )");
                
            }

        } else {
            erroSintatico("Esperava (");
            if (token.getTipo().equals("numero") || token.getTipo().equals("Identificador") || token.getTipo().equals("string") || token.getTipo().equals("char") || token.getLexema().equals("true") || token.getLexema().equals("false")) {
                parametroRetorno();
                if (token.getLexema().equals(")")) {
                    token = proximo();
                } else {
                    erroSintatico("Esperava )");
                }
            } else if (token.getLexema().equals(")")) {
                token = proximo();
            }
        }
        if (!token.getLexema().equals(";")) {
            erroSintatico("Esperava ;");
            while (!(verifica() || token.getLexema().equals(";") || token.getLexema().equals(")") | token.getLexema().equals("EOF"))) {
                erroSintatico("Retorno incorreto");
            }
            if (token.getLexema().equals(")")) {
                token = proximo();
                if (!(token.getLexema().equals(";"))) {
                    erroSintatico("Esperava ;");
                } else {
                    token = proximo();
                }

            }
        } else {
            token = proximo();
        }

    }

    private void parametroRetorno() {

        if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero") || token.getTipo().equals("string") || token.getTipo().equals("char") || token.getLexema().equals("true") || token.getLexema().equals("false")) {
           if(token.getTipo().equals("Identificador")||token.getTipo().equals("numero")){
               exp();
           }else{
            token = proximo();   
           }
            if(token.getLexema().equals(",")){
                erroSintatico("Retorno incorreto: "+token.getLexema());
                token = proximo();
                if(!(token.getLexema().equals(")")||token.getLexema().equals(";"))){
               erroSintatico("Retorno incorreto: "+token.getLexema());
               token = proximo();
                }
            }else if(!(token.getLexema().equals(")")||token.getLexema().equals(";"))){
                    erroSintatico("Retorno incorreto: "+token.getLexema());
                    token = proximo();
                }
        } else if (!token.getLexema().equals(")")) {
            erroSintatico("Esperava parametro do retorno ou )");
            verifica();
        }
    }

    private boolean verifica() {
        if (token.getLexema().equals("read") || token.getLexema().equals("write") || token.getLexema().equals("if") || token.getLexema().equals("while") || token.getTipo().equals("Identificador") || tipo()) {
            bloco();
            return true;
        } else {
            token = proximo();
            return false;
        }
    }

    private void Funcao() {
        blocoFuncao();
        if (token.getLexema().equals("function")) {
            Funcao();
        }
    }

    private boolean tipo() {
        switch (token.getLexema()) {
            case "integer":
                terminal("integer");
                return true;

            case "string":
                terminal("string");
                return true;

            case "real":
                terminal("real");
                return true;
            case "boolean":
                terminal("boolean");
                return true;
            case "char":
                terminal("char");
                return true;
            default:
                return false;
        }

    }

    private boolean identificador(String esperado) {
        if (!token.getLexema().equals("EOF") && token.getTipo().equals(esperado)) { //verifica se o tipo do token atual e o que era esperado
            token = proximo();
            return true;
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o tipo do token nao e o esperado
            return false;
        }

    }

    public boolean tipoPrimitivo() {
        switch (token.getLexema()) {
            case "integer":
                return true;
            case "string":
                return true;
            case "real":
                return true;
            case "boolean":
                return true;
            case "char":
                return true;

            default:
                erroSintatico("falta palavra reservada: integer, cadeia, real, boolean, char");
                return false;
        }
    }

    private void declaracaoMatriz() {
        switch (token.getLexema()) {
            case "[":
                token = proximo();
                if (token.getTipo().equals("numero")) {
                    token = proximo();
                } else if (!(token.getTipo().equals("numero"))) {
                    erroSintatico("declaracao de matriz incorreta, esperava numero");
                    if (token.getLexema().equals("[")) {
                        declaracaoMatriz();
                    } else if (token.getLexema().equals("]") || token.getLexema().equals(",") || token.getLexema().equals("=")) {

                    } else if (token.getLexema().equals("Identificador")) {
                        token = proximo();
                    } else {
                        while (!(token.getLexema().equals(";") || token.getLexema().equals("]") || token.getLexema().equals("EOF") || token.getLexema().equals("=") || token.getLexema().equals("end"))) {
                            erroSintatico("parametro matriz incorreto: " + token.getLexema());
                            token = proximo();
                        }
                    }
                }
                if (token.getLexema().equals("]")) {
                    token = proximo();
                } else {
                    erroSintatico("Esperava um ]");
                    if (token.getLexema().equals(",")) {
                        break;
                    } else if (token.getLexema().equals("=")) {
                        break;
                    } else if (token.getLexema().equals("[")) {
                        declaracaoMatriz();
                    } else {
                        while (!(token.getLexema().equals(";") || token.getLexema().equals("]") || token.getLexema().equals("EOF") || token.getLexema().equals("=") || token.getLexema().equals("end"))) {
                            erroSintatico("parametro matriz incorreto: " + token.getLexema());
                            token = proximo();
                        }
                        if (token.getLexema().equals("]")) {
                            token = proximo();
                        }
                    }
                }

                if (token.getLexema().equals("[")) {
                    declaracaoMatriz();
                }
                break;
            default:
                break;
        }
    }

    private void atribuicaoMatriz() {
        token = proximo();
        if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
            token = proximo();
            if (token.getLexema().equals(",")) {
                atribuicaoMatriz();
            } else if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
                erroSintatico("Esperava ,");
                token = proximo();
                atribuicaoMatriz();
            } else {
                if (token.getLexema().equals("]")) {
                    token = proximo();
                } else {
                    while (!(token.getLexema().equals("]") || token.getLexema().equals("EOF") || token.getLexema().equals(",") || token.getLexema().equals(";") || token.getLexema().equals("end"))) {
                        erroSintatico("Esperava , ou ]");
                        token = proximo();
                    }
                    if (token.getLexema().equals(";")) {
                        erroSintatico("Esperava ]");
                    }
                    if (token.getLexema().equals("]")) {
                        token = proximo();
                    }
                }
            }
        } else {
            erroSintatico("atribuicao de matriz incorreta");
            if (token.getLexema().equals("]")) {
                token = proximo();
            } else {
                while (!(token.getLexema().equals("]") || token.getLexema().equals("EOF") || token.getLexema().equals(",") || token.getLexema().equals(";"))) {
                    erroSintatico("Esperava ]");
                    token = proximo();
                }
                if (token.getLexema().equals("]")) {
                    token = proximo();
                } else if (token.getLexema().equals(",")) {
                    atribuicaoMatriz();
                }
            }
        }

    }

    private void write() {
        token = proximo();
        if (token.getLexema().equals("(")) {
            token = proximo();
            if (token.getLexema().equals(")")) {
                erroSintatico("Esperava parametro write");
            }
            parentesesWrite();
            if (token.getLexema().equals(";")) {
                token = proximo();
            }
        } else {
            erroSintatico("Comando write mal formado, esperava (");
            if (token.getTipo().equals("string") || token.getTipo().equals("char") || token.getTipo().equals("numero")|| token.getTipo().equals("Identificador")) {
                if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
                    exp();
                } else {
                    token = proximo();
                }
                if (token.getLexema().equals("(")) {
                    erroSintatico("Esperava ( apos write");
                    token = proximo();
                    parentesesWrite();

                } else if (token.getLexema().equals(",")) {
                    token = proximo();
                    parentesesWrite();
                } else if (token.getLexema().equals(")")) {
                    token = proximo();
                    if (token.getLexema().equals(";")) {
                        token = proximo();
                    } else {
                        erroSintatico("Faltou ;");
                    }
                }

            } else if (token.getLexema().equals(",")) {
                erroSintatico("Esperava: string,char,numero,id ");
                parentesesWrite();
            } else if (token.getLexema().equals(")")) {
                erroSintatico("Esperava: string,char,numero,id ");
                token = proximo();
                if (token.getLexema().equals(";")) {
                    token = proximo();
                } else {
                    erroSintatico("Esperava ;");
                }
            } else if (token.getLexema().equals(";")) {
                token = proximo();
            }
        }

    }

    private void parentesesWrite() {
        parametroWrite();
        if (!(token.getLexema().equals(")") || token.getLexema().equals(";"))) {
            if (!token.getLexema().equals(")")) {
                // adicionar as outras opcoes aqui
                erroSintatico("Comando write mal formado esperava )");

            } else {
                erroSintatico("Comando write mal formado esperava ;");
                bloco();

            }
        } else if (token.getLexema().equals(";")) {
            erroSintatico("expressao write mal formada esperava )");
            token = proximo();
        } else if (token.getLexema().equals(")")) {
            token = proximo();
            if (!token.getLexema().equals(";")) {
                erroSintatico("expressao write mal formada esperava ;");

            } else {
                token = proximo();

            }

        }
    }

    private void parametroWrite() {
        if (token.getTipo().equals("string") || token.getTipo().equals("char") || token.getTipo().equals("numero") || token.getTipo().equals("Identificador")) {
            if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
                exp();
            } else {
                token = proximo();
            }
            if (token.getLexema().equals(",")) {
                token = proximo();
                parametroWrite();
            } else if (token.getTipo().equals("string") || token.getTipo().equals("char") || token.getTipo().equals("numero") || token.getTipo().equals("Identificador")) {
                erroSintatico("Esperava , ou )");
                token = proximo();
            } else if (!token.getLexema().equals(")")) {
                erroSintatico("Esperava , ou ) ");
            }
        } else {
            if (!token.getLexema().equals(")")) {
                erroSintatico("Parametro write incorreto: " + token.getLexema());
            }

            if (token.getLexema().equals(",")) {
                token = proximo();
                parametroWrite();
            } else if (!(token.getLexema().equals(")") || token.getLexema().equals(";"))) {
                token = proximo();
                parametroWrite();
            }
        }
    }

    private void read() {
        token = proximo();
        if (token.getLexema().equals("(")) {
            token = proximo();
            if (token.getLexema().equals(")")) {
                erroSintatico("Esperava parametro read");
            }
            parametroRead();
            readParentesis();
        } else {
            erroSintatico("read nao possui (");
            if (token.getTipo().equals("Identificador")) {
                parametroRead();
                readParentesis();
            }
        }
    }

    private void readParentesis() {
        if (!(token.getLexema().equals(")") || token.getLexema().equals(";") || token.getLexema().equals("EOF"))) {
            if (!token.getLexema().equals(")")) {
                erroSintatico("expressao read mal formada esperava )");
                if (token.getLexema().equals(";")) {
                    token = proximo();
                } else {
                    erroSintatico("Esperava ;");
                }
            }
        } else if (token.getLexema().equals(")")) {
            token = proximo();
            
            if (token.getLexema().equals("end")) {
                erroSintatico("Esperava ;");
            } else if (!token.getLexema().equals(";")) {
                erroSintatico("Esperava ;");
                while (!(token.getLexema().equals(";") || verifica() || token.getLexema().equals("end"))) {
                    erroSintatico("expressao read mal formada esperava ;");
                }
                if (token.getLexema().equals(";")) {
                    token = proximo();
                }
                // bloco();
            } else if (token.getLexema().equals(";")) {
                token = proximo();
            } else {
                token = proximo();
            }
        } else if (token.getLexema().equals(";")) {
            token = proximo();
        }

    }

    private void parametroRead() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            if (token.getLexema().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexema().equals(",")) {
                token = proximo();
                parametroRead();
            }
            if (token.getTipo().equals("Identificador")) {
                erroSintatico("Esperava ,");
                parametroRead();
            } else if (!(token.getLexema().equals(")") || token.getLexema().equals(";"))) {
                erroSintatico("Esperava Id ou ) ");
                token = proximo();
            } else if (token.getLexema().equals(";")) {
                erroSintatico("Esperava )");
            }
        } else {
            while (!(token.getLexema().equals(",") || token.getLexema().equals(")") || token.getLexema().equals(";") || token.getTipo().equals("Identificador") || token.getLexema().equals("EOF"))) {
                erroSintatico("Parametro read incorreto: " +token.getLexema()+" falta Id ");
                token = proximo();
            }
            if (token.getTipo().equals("Identificador")) {
                erroSintatico("Esperava ,");
                parametroRead();
            }
            if (token.getLexema().equals(",")) {
                erroSintatico("Parametro read incorreto: "+token.getLexema()+" falta Id ");
                token = proximo();
                parametroRead();
            }
        }
    }

    private void parameterCallFunction() {
        if (token.getTipo().equals("string") || token.getTipo().equals("char") || token.getLexema().equals("true") || token.getLexema().equals("false") || token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
            if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
                exp();
            } else {
                token = proximo();
            }
            if (token.getLexema().equals(",")) {
                token = proximo();
                parameterCallFunction();
            }
        } else {
            erroSintatico("Parametro invalido :" + token.getLexema());
            if (!verifica()) {
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    parameterCallFunction();
                }
            }
        }
    }

    private void callFunction(int opc1) {
        int opc = opc1;
        if (token.getLexema().equals("(")) {
            token = proximo();
            if (!token.getLexema().equals(")")) {

                parameterCallFunction();
            }
        }
        if (token.getLexema().equals(")")) {
            token = proximo();
            if (!token.getLexema().equals(";") && opc == 0) {
                erroSintatico("falta ;");
            } else {
                if (opc == 0) {
                    token = proximo();
                }
            }

        } else {
            erroSintatico("falta )");
            //colocar if verificando se o atual nao e alguma das outras opcoes
            token = proximo();
        }
    }

    //Expressoes Aritmeticas
    private boolean exp() {
        if (exp_A1()) {
            expAux1();
            return true;
        } else {
            return false;
        }

    }

    private boolean exp_A1() {
        if (valorNumerico()) {
            exp_Aux4();
            return true;
        } else {
            return false;
        }
    }

    private boolean expAux1() {
        if (exp_SomSub()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean exp_SomSub() {
        if (fator()) {
            if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero") || token.getLexema().equals("(")) {
                exp();
            } else {
                erroSintatico("Esperava um Id ou numero");
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean fator() {
        String ch = token.getLexema();
        char n = ch.charAt(0);
        if (n == '-' && token.getTipo().equals("numero")) {
            return true;
        } else if (token.getLexema().equals("+") || token.getLexema().equals("-")) {
            this.atterror++;
            token = proximo();

            return true;
        } else {
            return false;
        }
    }

    private boolean exp_Aux4() {
        if (exp_MultDiv()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean exp_MultDiv() {
        if (termo()) {
            token = proximo();
            if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero") || token.getLexema().equals("(")) {
                exp();
            } else {
                erroSintatico("Esperava um numero ou Id");
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean termo() {
        if (token.getLexema().equals("*") || token.getLexema().equals("/") || token.getLexema().equals("%")) {
            this.atterror++;
            return true;
        }
        return false;
    }

    private boolean valorNumerico() {
        if (token.getLexema().equals("(")) {
            token = proximo();
            exp();
            if (!token.getLexema().equals(")")) {
                erroSintatico("Faltou )");
                return true;
            } else {
                token = proximo();
                return true;
            }
        } else if (idAritmetico()) {
            return true;
        } else if (token.getTipo().equals("Identificador")) {
            verifId();
            return true;
        } else if (token.getTipo().equals("numero")) {
            token = proximo();
            return true;
        } else {
            return false;
        }
    }

    private void verifId() {
        token = proximo();
        if (token.getLexema().equals("(")) {
            callFunction(1);
        } else if (token.getLexema().equals("[")) {
            declaracaoMatriz();
        } else {

        }
    }

    private boolean idAritmetico() {
        if (token.getLexema().equals("-")) {
            token = proximo();
            if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
                token = proximo();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //Expressoes Relacionais
    public boolean operadorRelacional() {
        if (token.getLexema().equals("!=")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals(">")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals("<")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals(">=")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals("<=")) {
            token = proximo();
            return true;
        } else {
            return false;
        }
    }
    private boolean relacionan(int opc) {
        System.out.println("Entrou"+token.getLexema());
        if (exp()) {
            System.out.println("Pos ecp"+token.getLexema());
            System.out.println(" aqui"+aux);
            if(token.getLexema().equals(")")&&aux!=0){
                token = proximo();
                this.aux--;
             }
            System.out.println("Um"+token.getLexema());
                if (token.getLexema().equals("*") || token.getLexema().equals("/") || token.getLexema().equals("%")|| token.getLexema().equals("-")|| token.getLexema().equals("+")) {
                        token = proximo();
                        this.atterror++;
                        exp();
                }
                //
            if (operadorRelacional()) {
                this.atterror++;
                if (exp()) {
                    return true;
                } else {
                    erroSintatico("Esperava uma expressao");
                    return false;
                }
            } else if (token.getLexema().equals("==")) {
                token = proximo();
                if (token.getLexema().equals("true")) {
                    token = proximo();
                    return true;
                } else if (token.getLexema().equals("false")) {
                    token = proximo();
                    return true;
                } else if (exp()) {
                    return true;
                } else {
                    erroSintatico("Esperava expressao");
                    return true;
                }
            } else {
                if(opc==1){
                    System.out.println("Toooo"+token.getLexema());
                erroSintatico("Esperava um operador relacional");
                if (token.getLexema().equals("||") || token.getLexema().equals("&&")) {
                    token = proximo();
                    auxExpLogica(1);
                 return false;   
                }else if(opc==0){
                    return true;
                }
                }
                
            }
        }
        return false;
    }
    private boolean expRelacional(int opc) {
        if (token.getLexema().equals("(")) {
            token = proximo();
            this.aux++;
            relacionan(opc);
            System.out.println("Saiu"+token.getLexema());
            if (token.getLexema().equals(")")) {
                token = proximo();
                this.aux--;
                return true;
            } else {
                 return true;
            }
        } else if (relacionan(opc)) {
            return true;
        } else {
            return false;
        }
    }

    //Expressoes logicas
    public boolean operadorLogico() {
        if (token.getLexema().equals("&&")) {
            token = proximo();
            this.atterror++;
            return true;
        } else if (token.getLexema().equals("||")) {
            token = proximo();
            this.atterror++;
            return true;
        } else {
            return false;
        }
    }
    private boolean auxExpLogica(int opc) {
        if (expRelacional(opc)) {
             if(token.getLexema().equals(")")&&aux!=0){
                token = proximo();
                this.aux--;
             }
            if (token.getLexema().equals("&&") || token.getLexema().equals("||")) {
                this.atterror++;
                token = proximo();
                if (expRelacional(1)) {
                    auxLog();
                    return true;
                } else {
                    erroSintatico("Esperava uma expressao relacional");
                    return false;
                }
            } else {
                return true; // sera so uma relacional
            }
        } else {
            if(opc==1){
              erroSintatico("Esperava expressao relacional");
              
            }
            return false;
        }

    }
    private boolean expLogica(int opc) {
        if (token.getLexema().equals("!")) {
            token = proximo();
            negx(opc);
            auxLog();
            
            validPare();
            return true;
        } else if (token.getLexema().equals("(")) {
            token = proximo();
            this.aux++;
            auxExpLogica(opc);
            if (token.getLexema().equals(")")&&aux!=0) {
                this.aux--;
                token = proximo();
                validPare();
                return true;
            }else if(token.getLexema().equals(")")&&aux==0){
                validPare();
                return true;
            }else {
                validPare();
                return true;
            }
        } else if (expRelacional(opc)) {
            if (token.getLexema().equals("&&") || token.getLexema().equals("||")) {
                token = proximo();
                if (expRelacional(1)) {
                    auxLog();
                    return true;
                } else {
                    erroSintatico("Esperava uma expressao relacional");
                    return false;
                }
            } else {
                return true; // sera so uma relacional
            }
            
        } else {
            return false;
        }
        
    }
    private void validPare(){
        if(aux>0){
            erroSintatico("Esperava )");
        }else if(aux<0){
            erroSintatico("Esperava (");
        }else{
            System.out.println("Parenteses balenceados"+token.getLexema());
        }
    }
    private void auxLog() {
        if (token.getLexema().equals("&&") || token.getLexema().equals("||")) {
            token = proximo();
            if (expLogica(1)) {

            } else {
                erroSintatico("Esperava uma expressao");
            }
        }
    }
    private void negx(int opc) {
        if (token.getLexema().equals("(")) {
            //this.aux++;
            token = proximo();
            expRelacional(opc);
            auxLog();
            if (token.getLexema().equals(")")) {
              //  this.aux--;
                token = proximo();
            } else {
                erroSintatico("Esperava )");
            }
        }
    }
}
