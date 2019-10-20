package br.com.acolher.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.acolher.helper.Validacoes;

public class UsuarioController {

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

    public String validarNome(String nome){

        if(!empty(nome)) {
            return "Campo obrigatorio!";
        }

        if(nome.length() < 7){
            return "favor inserir o nome completo!";
        }

        if(nome.indexOf(" ") == -1){
            return "O nome deve conter seu nome e sobrenome";
        }

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


        if(email == null || email.trim().isEmpty() || email == ""){
            return "Campo obrigatório!";
        }

        if(!Validacoes.validaEmail(email)){
            return "formato de email inválido!";
        }

        return "";

    }

    public static boolean validaEmail(String emailValida) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailValida).matches();
    }

    public String validaPassword(String password){

        String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%=+-_]).{4,})";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

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


}
