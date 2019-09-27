package br.com.acolher.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DisponibilidadeController {

    public static boolean empty(String s){
        if(s == ""){
            return false;
        }
        return s.trim().length()>0;
    }

    public static LocalDateTime localDateTime(String dateTime, String hora) {
        String dataHora = dateTime + " " + hora;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return LocalDateTime.parse(dataHora, formatter);
    }
}
