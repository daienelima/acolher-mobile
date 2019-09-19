package br.com.acolher.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.InstituicaoController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Instituicao;
import retrofit2.Call;

public class CadastroInstituicao extends AppCompatActivity{

    Button continuarCadastro;
    TextInputLayout inputPassword;
    TextInputLayout inputTelefone;
    TextInputLayout inputCnpj;
    TextInputLayout inputNome;
    TextInputLayout inputEmail;
    InstituicaoController ic;
    private RetrofitInit retrofitInit;
    private Instituicao instituicao = new Instituicao();
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
        setContentView(R.layout.cadastro_instituicao_activity);

        continuarCadastro = (Button) findViewById(R.id.buttonContinuarCadastro);

        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);

        inputCnpj = (TextInputLayout) findViewById(R.id.inputCnpj);
        inputCnpj.getEditText().addTextChangedListener(MaskWatcher.buildCnpj());

        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) ####-####"));

        inputNome = (TextInputLayout) findViewById(R.id.inputNome);

        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);

        continuarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ic = new InstituicaoController();
                if(validateForm()){
                    montarInstituicao();
                }
                //Intent intentEndereco = new Intent(CadastroActivity.this, CadastroEndereco.class);
                //startActivity(intentEndereco);
            }
        });

    }

    private void montarInstituicao() {
        instituicao.setAtivo(true);
        instituicao.setNome(nome);
        instituicao.setCnpj(cnpj);
        instituicao.setEmail(email);
        instituicao.setSenha(password);
        instituicao.setTelefone(telefone);
    }

    public boolean validateForm(){

        nome = inputNome.getEditText().getText().toString();
        email = inputEmail.getEditText().getText().toString();
        password = inputPassword.getEditText().getText().toString();
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

        if(ic.validaPassword(password) != ""){
            inputPassword.getEditText().setError(ic.validaPassword(password));
            return false;
        }

        if(ic.validarTelefone(telefone) != ""){
            inputTelefone.getEditText().setError(ic.validarTelefone(telefone));
            return false;
        }
        return true;

    }

    private void cadastroInstituicao(Instituicao instituicao){
        //Instituicao cadastro = new RetrofitInit().getService().cadastroInstituicao(instituicao);

    }


}