package br.com.acolher.service;

import java.util.List;

import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServiceApi {

    @POST("endereco")
    Call<Endereco> cadastroEndereco(@Body Endereco endereco);

    @POST("instituicao")
    Call<Instituicao> cadastroInstituicao(@Body Instituicao instituicao);

    @GET("consulta")
    Call<List<Consulta>> getConsultas();
}
