package br.com.acolher.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.controller.InstituicaoController;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

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