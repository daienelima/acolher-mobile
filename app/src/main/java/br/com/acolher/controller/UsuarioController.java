package br.com.acolher.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.acolher.helper.Validacoes;

public class UsuarioController {

    public String validarNome(String nome){

        /*String regexNome = "/^[a-zA-ZéúíóáÉÚÍÓÁèùìòàçÇÈÙÌÒÀõãñÕÃÑêûîôâÊÛÎÔÂëÿüïöäËYÜÏÖÄ\\-\\ \\s]+$/";
        Pattern pattern = Pattern.compile(regexNome);
        Matcher matcher = pattern.matcher(nome);*/

        if(nome == null || nome.isEmpty() || nome == "") {
            return "Campo obrigatorio!";
        }

        if(nome.length() < 7){
            return "favor inserir o nome completo!";
        }

        if(nome.indexOf(" ") == -1){
            return "O nome deve conter seu nome e sobrenome";
        }

        /*if(!matcher.matches()){
            return "Insira apenas letras!";
        }*/

        return "";
    }

    public String validarTelefone(String telefone){

        if(telefone == null || telefone.isEmpty() || telefone == "") {
            return "Campo obrigatorio!";
        }

        if(telefone.length() < 10 || telefone.length() > 11){
            return "favor preencher o DDD + 9 Digitos!";
        }

        return "";
    }

    public String validarEmail(String email){

        String regexEmail = "^(.+)@(.+)$";

        if(email == null || email.isEmpty() || email == ""){
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


        //String regexPassword = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{4,20})";

        if(password == null || password.isEmpty() || password == ""){
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

    public String validarDataNasc(String dataNasc){

        if(dataNasc == null || dataNasc == "" || dataNasc.isEmpty()){
            return "Campo obrigatório!";
        }

        return "";

    }

    public String validaCpf(String cpf){

        if(cpf == null || cpf.isEmpty() || cpf == ""){
            return "Campo obrigatorio!";
        }

        if(cpf.length() != 11){
            return "CPF muito curto!";
        }

        if (!Validacoes.isCPF(cpf)){
            return "CPF inválido!";
        }

        return "";

    }

    public String validaCRM(String crm){
        if(crm == null || crm.isEmpty() || crm == ""){
            return "Campo obrigatório";
        }

        return "";
    }


}
