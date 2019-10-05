package br.com.acolher.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.InstituicaoController;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Instituicao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeusDadosInstituicaoActivity extends AppCompatActivity{

    Button continuarAlteracao;
    Button alterarSenha;
    TextInputLayout inputTelefone;
    TextInputLayout inputCnpj;
    TextInputLayout inputNome;
    TextInputLayout inputEmail;
    InstituicaoController ic;
    private String nome;
    private String email;
    private String password;
    private String cnpj;
    private String telefone;
    Instituicao instituicao;
    Call<Instituicao> call;
    private RetrofitInit retrofitInit = new RetrofitInit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();


        // buscar do banco
        call = retrofitInit.getService().consultaInstituicao(1);

        call.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                instituicao = response.body();

                //incluindo dados em tela
                inputNome.getEditText().setText(instituicao.getNome());
                inputEmail.getEditText().setText(instituicao.getEmail());
                inputTelefone.getEditText().setText(instituicao.getTelefone());
                inputCnpj.getEditText().setText(instituicao.getCnpj());
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });

        //Configurações da activity
        setContentView(R.layout.activity_meus_dados_instituicao);

        continuarAlteracao = (Button) findViewById(R.id.buttonContinuarAlteracao);
        alterarSenha = (Button) findViewById(R.id.buttonAlterarSenha);

        inputCnpj = (TextInputLayout) findViewById(R.id.inputCnpj);
        inputCnpj.getEditText().addTextChangedListener(MaskWatcher.buildCnpj());

        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));
        inputNome = (TextInputLayout) findViewById(R.id.inputNome);

        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);

        TextView voltar = (TextView) findViewById(R.id.labelRetornarHome) ;

        continuarAlteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ic = new InstituicaoController();
                if(validateForm()){
                    Intent intentEndereco = new Intent(MeusDadosInstituicaoActivity.this, CadastroEndereco.class);
                    intentEndereco.putExtra("telaOrigem", "instituicao");
                    intentEndereco.putExtra("nomeInstituicao", nome);
                    intentEndereco.putExtra("cnpjInstituicao", cnpj);
                    intentEndereco.putExtra("telefoneInstituicao", telefone);
                    intentEndereco.putExtra("emailInstituicao", email);
                    intentEndereco.putExtra("passwordInstituicao", password);
                    startActivity(intentEndereco);
                }
            }
        });


        alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentAlterarSenha = new Intent(MeusDadosInstituicaoActivity.this, AlterarSenha.class);
                startActivity(intentAlterarSenha);

            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

    public boolean validateForm(){

        nome = inputNome.getEditText().getText().toString();
        email = inputEmail.getEditText().getText().toString();
        cnpj = Validacoes.cleanCNPJ(inputCnpj.getEditText().getText().toString());
        telefone = Validacoes.cleanTelefone(inputTelefone.getEditText().getText().toString());

        if(ic.validarNome(nome) != ""){
            inputNome.getEditText().setError(ic.validarNome(nome));
            return false;
        }

        if(ic.validaCnpj(cnpj) != ""){
            inputCnpj.getEditText().setError(ic.validaCnpj(cnpj));
            return false;
        }
        if(ic.validarEmail(email) != ""){
            inputEmail.getEditText().setError(ic.validarEmail(email));
            return false;
        }


        if(ic.validarTelefone(telefone) != ""){
            inputTelefone.getEditText().setError(ic.validarTelefone(telefone));
            return false;
        }
        return true;

    }

}