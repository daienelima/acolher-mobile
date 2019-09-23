package br.com.acolher.service;

import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceApi {

    @POST("endereco")
    Call<Endereco> cadastroEndereco(@Body Endereco endereco);

    @POST("instituicao")
    Call<Instituicao> cadastroInstituicao(@Body Instituicao instituicao);

    @POST("usuario")
    Call<Usuario> cadastroUsuario(@Body Usuario usuario);
  
    @POST("consulta")
    Call<Consulta> cadastroConsulta (@Body Consulta consulta);
}
