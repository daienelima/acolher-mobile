package br.com.acolher.model;

import java.io.Serializable;

public class Instituicao implements Serializable {

    private Integer codigo;
    private String nome;
    private String cnpj;
    private Endereco endereco;
    private String telefone;
    private String email;
    private String senha;
    private boolean ativo;

    public Instituicao() {
    }

    public Instituicao(Integer codigo, String nome, String cnpj, Endereco endereco, String telefone, String email, String senha, boolean ativo) {
        this.codigo = codigo;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Instituicao{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", endereco=" + endereco +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
