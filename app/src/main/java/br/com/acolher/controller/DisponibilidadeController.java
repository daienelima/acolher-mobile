package br.com.acolher.controller;

public class DisponibilidadeController {

    public static boolean empty(String s){
        if(s == ""){
            return false;
        }
        return s.trim().length()>0;
    }
}
