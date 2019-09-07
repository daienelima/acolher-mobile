package br.com.acolher.controller;

public class EnderecoController {

    public String validaCep(String cep){

        if(cep.isEmpty() || cep == null || cep == ""){
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
}
