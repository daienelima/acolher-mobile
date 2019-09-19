package br.com.acolher.model;

import java.time.LocalDateTime;

public class Consulta {

    private Integer codigo;
    private LocalDateTime data;
    private String hora;
    private Endereco endereco;
    private Usuario profissional;
    private Instituicao instituicao;
    private Usuario paciente;
    private Status statusConsulta;

    public Consulta(){super();}

    public Consulta(Integer codigo, LocalDateTime data, String hora, Endereco endereco, Usuario profissional, Instituicao instituicao, Usuario paciente, Status statusConsulta) {
        this.codigo = codigo;
        this.data = data;
        this.hora = hora;
        this.endereco = endereco;
        this.profissional = profissional;
        this.instituicao = instituicao;
        this.paciente = paciente;
        this.statusConsulta = statusConsulta;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Usuario getProfissional() {
        return profissional;
    }

    public void setProfissional(Usuario profissional) {
        this.profissional = profissional;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }

    public Status getStatusConsulta() {
        return statusConsulta;
    }

    public void setStatusConsulta(Status statusConsulta) {
        this.statusConsulta = statusConsulta;
    }

}


