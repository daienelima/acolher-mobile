package br.com.acolher.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnderecoController {

    /**
     * Valida se uma String é vazia ou nula
     * @param s
     * @return
     */
    public static boolean empty(String s){
        if(s == null){
            return false;
        }
        return s.trim().length() > 0;
    }

    /**
     * Validação de CEP
     * @param cep
     * @return
     */
    public String validaCep(String cep){

        if(!empty(cep)){
            return "Campo Obrigatório!";
        }

        if(!cep.matches("[0-9]+")){
            return "Insira apenas numeros!";
        }

        if(cep.length() != 8){
            return "O cep está incompleto";
        }

        return "";
    }

    public static String validaUF(String UF){

        if(!empty(UF)){
            return "Campo obrigatorio!";
        }

        if(UF.trim().length()!= 2){
            return "O campo deve conter duas letras";
        }

        return "";
    }

}
