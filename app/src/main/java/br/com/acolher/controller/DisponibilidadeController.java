package br.com.acolher.controller;

public class DisponibilidadeController {

    public String validarData(String data) {

        if (data == null || data.isEmpty() || data == "") {
            return "Campo obrigatorio!";
        }
        return "";
    }

    public String validarHora(String hora) {

        if (hora == null || hora.isEmpty() || hora == "") {
            return "Campo obrigatorio!";
        }
        return "";
    }
}
