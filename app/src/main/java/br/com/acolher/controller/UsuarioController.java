package br.com.acolher.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.acolher.helper.ValidaCPF;

public class UsuarioController {

    public String validarNome(String nome){

        //String regexNome = @"^[aA-zZ]+((\s[aA-zZ]+)+)?$";
        //Pattern pattern = Pattern.compile(regexNome);
        //Matcher matcher = pattern.matcher(nome);
        //boolean nomeValido = nome.matches("/[A-Z][a-z]* [A-Z][a-z]*/");

        if(nome == null || nome.isEmpty() || nome == "") {
            return "Campo obrigatorio!";
        }

        if(nome.length() < 7){
            return "favor inserir o nome completo!";
        }

        if(nome.indexOf(" ") == -1){
            return "O nome deve conter seu nome e sobrenome";
        }

        /*if(!matcher.find()){
            return "Insira apenas letras!";
        }*/

        return "";
    }

    public String validarTelefone(String telefone){

        if(telefone == null || telefone.isEmpty() || telefone == "") {
            return "Campo obrigatorio!";
        }

        if(telefone.length() > 11){
            return "favor preencher o DDD + 9 Digitos!";
        }

        return "";
    }

    public String validarEmail(String email){

        String regexEmail = "^(.+)@(.+)$";

        if(email == null || email.isEmpty() || email == ""){
            return "Campo obrigatório!";
        }

        if(!email.matches(regexEmail)){
            return "formato de email inválido!";
        }

        return "";

    }

    public String validaPassword(String password){

        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        if(password == null || password.isEmpty() || password == ""){
            return "Campo obrigatório!";
        }

        if(password.length() < 4){
            return "A senha deve ter no mínimo 4 caracteres!";
        }

        if(!password.matches(regexPassword)){
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

        if (!ValidaCPF.isCPF(cpf)){
            return "CPF inválido!";
        }

        return "";

    }


}
