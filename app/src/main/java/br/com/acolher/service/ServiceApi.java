package br.com.acolher.service;

import br.com.acolher.model.Instituicao;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceApi {

    @POST("/instituicao")
    Call<Instituicao> cadastroInstituicao(@Body Instituicao instituicao);
}
