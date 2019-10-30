package br.com.acolher.service;


import java.util.List;

import br.com.acolher.dto.AlterarSenha;
import br.com.acolher.dto.Login;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import br.com.acolher.model.ViaCep;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServiceApi {

    @POST("endereco")
    Call<Endereco> cadastroEndereco(@Body Endereco endereco);

    @PUT("endereco")
    Call<Endereco> atualizarEndereco(@Body Endereco endereco);

    @POST("instituicao")
    Call<Instituicao> cadastroInstituicao(@Body Instituicao instituicao);

    @PUT("instituicao")
    Call<Instituicao> atualizarInstituicao(@Body Instituicao instituicao);

    @GET("instituicao/{codigo}")
    Call<Instituicao> consultaInstituicao(@Path("codigo") Integer codigo);

    @GET("consulta/disponiveis")
    Call<List<Consulta>> getConsultas();

    @POST("usuario")
    Call<Usuario> cadastroUsuario(@Body Usuario usuario);

    @PUT("usuario")
    Call<Usuario> alterarUsuario(@Body Usuario usuario);

    @GET("usuario/{codigo}")
    Call<Usuario>getUsuario(@Path("codigo")Integer codigo);

    @POST("consulta")
    Call<Consulta> cadastroConsulta (@Body Consulta consulta);

    @PUT("consulta/confirmar")
    Call<Consulta> confirmarConsulta(@Body Consulta consulta);

    @GET("consulta/disponiveis/{codigo}")
    Call<Consulta> getConsultasPorPaciente(@Path("codigo") Integer codigo);

    @GET("{cep}/json")
    Call<ViaCep> buscarCEP(@Path("cep") String cep);

    @GET("consulta/paciente/{codigo}")
    Call<List<Consulta>> getConsultasPorPacientes(@Path("codigo") int codigo);

    @GET("consulta/voluntario/{codigo}")
    Call<List<Consulta>> getConsultasPorVoluntario(@Path("codigo") int codigo);

    @GET("consulta/instituicao/{codigo}")
    Call<List<Consulta>> getConsultasPorInstituicao(@Path("codigo") int codigo);

    @PUT("consulta/cancelar")
    Call<Consulta> cancelarConsulta(@Body Consulta consulta);

    @PUT("consulta/cancelarpaciente")
    Call<Consulta> cancelarConsultaPaciente(@Body Consulta consulta);

    @POST("usuario/login")
    Call<Usuario> validarLoginUsuario(@Body Login login);

    @POST("instituicao/login")
    Call<Instituicao> validarLoginInstituicao(@Body Login login);

    @PUT("usuario/senha")
    Call<Usuario> alterarSenhaUsuario(@Body AlterarSenha alterarSenha);

    @PUT("instituicao/senha")
    Call<Instituicao> alterarSenhaInstituicao(@Body AlterarSenha alterarSenha);

    @GET("usuario/desativar/{codigo}")
    Call<Usuario> desativarUsuario(@Path("codigo") Integer codigo);

    @GET("instituicao/desativar/{codigo}")
    Call<Instituicao> desativarInstituicao(@Path("codigo") Integer codigo);

    @POST("endereco/getByParameters")
    Call<Endereco> getAddressByParameters(@Body Endereco endereco);
}
