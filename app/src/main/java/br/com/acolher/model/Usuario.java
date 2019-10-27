package br.com.acolher.model;

import java.io.Serializable;

public class Usuario implements Serializable {

    private Integer codigo;
    private String nome_completo;
    private String data_nascimento;
    private String email;
    private String password;
    private Endereco endereco;
    private String telefone;
    private String cpf;
    private String crm_crp;
    private boolean ativo;

    public Usuario() {}

    public Usuario(Integer codigo, String nome_completo, String data_nascimento, String email, String password, Endereco endereco, String telefone, String cpf, String crm_crp, boolean ativo) {
        this.codigo = codigo;
        this.nome_completo = nome_completo;
        this.data_nascimento = data_nascimento;
        this.email = email;
        this.password = password;
        this.endereco = endereco;
        this.telefone = telefone;
        this.cpf = cpf;
        this.crm_crp = crm_crp;
        this.ativo = ativo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome_completo() {
        return nome_completo;
    }

    public void setNome_completo(String nome_completo) {
        this.nome_completo = nome_completo;
    }

    public String getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(String data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCrm_crp() {
        return crm_crp;
    }

    public void setCrm_crp(String crm_crp) {
        this.crm_crp = crm_crp;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "codigo=" + codigo +
                ", nome_completo='" + nome_completo + '\'' +
                ", data_nascimento='" + data_nascimento + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", endereco=" + endereco +
                ", telefone='" + telefone + '\'' +
                ", cpf='" + cpf + '\'' +
                ", crm_crp='" + crm_crp + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
