package br.com.acolher.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.acolher.helper.Validacoes;

public class InstituicaoController {

    public String validarNome(String nome){

        if(nome == null || nome.trim().isEmpty() || nome == "") {
            return "Campo obrigatorio!";
        }

        return "";
    }

    public String validarTelefone(String telefone){

        if(telefone == null || telefone.trim().isEmpty() || telefone == "") {
            return "Campo obrigatorio!";
        }

        if(telefone.length() < 11 || telefone.length() > 12){
            return "favor preencher o DDD + 9 Digitos!";
        }

        return "";
    }

    public String validarEmail(String email){


        if(email == null || email.trim().isEmpty() || email == ""){
            return "Campo obrigatório!";
        }

        if(!Validacoes.validaEmail(email)){
            return "formato de email inválido!";
        }

        return "";

    }

    public String validaPassword(String password){

        String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%=+-_]).{4,})";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        if(password == null || password.trim().isEmpty() || password == ""){
            return "Campo obrigatório!";
        }

        if(password.length() < 4){
            return "A senha deve ter no mínimo 4 caracteres!";
        }

        if(!matcher.matches()){
            return "Senha fraca!";
        }

        return "";

    }

    public String validaCnpj(String cnpj){

        if(cnpj == null || cnpj.trim().isEmpty() || cnpj == ""){
            return "Campo obrigatorio!";
        }

        if(cnpj.length() != 14){
            return "CNPJ muito curto!";
        }

        if (!Validacoes.isCNPJ(cnpj)){
            return "CNPJ inválido!";
        }

        return "";

    }


}
