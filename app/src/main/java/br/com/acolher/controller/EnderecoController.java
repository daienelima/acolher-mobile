package br.com.acolher.controller;

import br.com.acolher.helper.Validacoes;

public class EnderecoController {

    public String validaCep(String cep){

        if(!Validacoes.hasString(cep)){
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

    public String validaNumero(String numero){

        if(!Validacoes.hasString(numero)){
            return "Campo obrigatorio!";
        }

        if(!numero.matches("[0-9]+")){
            return "Insira apenas numeros!";
        }

        return "";
    }

    public String validaRua(String rua){

        if(!Validacoes.hasString(rua)){
            return "Campo Obrigatorio";
        }

        return "";
    }

    public String validaCidade(String cidade){

        if(!Validacoes.hasString(cidade)){
            return "Campo Obrigatorio";
        }

        return "";
    }

    public String validaEstado(String estado){

        if(!Validacoes.hasString(estado)){
            return "Campo Obrigatorio";
        }

        return "";
    }

    public String validaBairro(String bairro){

        if(!Validacoes.hasString(bairro)){
            return "Campo Obrigatorio";
        }

        return "";
    }

}
