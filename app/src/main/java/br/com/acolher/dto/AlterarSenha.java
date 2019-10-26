package br.com.acolher.dto;

import java.io.Serializable;

public class AlterarSenha implements Serializable {

    Integer codigo;
    String senhaAntiga;
    String novaSenha;

    public  AlterarSenha(){}

    public AlterarSenha(Integer codigo, String senhaAntiga, String novaSenha) {
        this.codigo = codigo;
        this.senhaAntiga = senhaAntiga;
        this.novaSenha = novaSenha;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getSenhaAntiga() {
        return senhaAntiga;
    }

    public void setSenhaAntiga(String senhaAntiga) {
        this.senhaAntiga = senhaAntiga;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
