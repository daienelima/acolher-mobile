package br.com.acolher.model;

public class Usuario {

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
    private Integer codigo;

    public Usuario() {
    }
  
    public Usuario(String nomeCompleto, String telefone, String email, String password, String dtNascimento, boolean ativo, String cpf, String crm_crp, Integer codigo) {
        this.nomeCompleto = nomeCompleto;
        this.telefone = telefone;
        this.email = email;
        this.password = password;
        this.dtNascimento = dtNascimento;
        this.ativo = ativo;
        this.cpf = cpf;
        this.crm_crp = crm_crp;
        this.codigo = codigo;
    }
  
   public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(String dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }
    
}
