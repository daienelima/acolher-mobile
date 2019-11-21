package br.com.acolher.model;

import com.google.gson.annotations.SerializedName;

public class Notificacao {

    @SerializedName("to") //  "to" changed to token
    private String token;

    @SerializedName("notification")
    private SendNotificationModel sendNotificationModel;

    public SendNotificationModel getSendNotificationModel() {
        return sendNotificationModel;
    }

    public void setSendNotificationModel(SendNotificationModel sendNotificationModel) {
        this.sendNotificationModel = sendNotificationModel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /*private String hashDestinatario;
    private String titulo;
    private String body;

    public Notificacao(){}

    public Notificacao(String hashDestinatario, String titulo, String body) {
        this.hashDestinatario = hashDestinatario;
        this.titulo = titulo;
        this.body = body;
    }

    public String getHashDestinatario() {
        return hashDestinatario;
    }

    public void setHashDestinatario(String hashDestinatario) {
        this.hashDestinatario = hashDestinatario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    @Override
    public String toString() {
        return "Notificacao{" +
                "hashDestinatario='" + hashDestinatario + '\'' +
                ", titulo='" + titulo + '\'' +
                ", body='" + body + '\'' +
                '}';
    }*/
}
