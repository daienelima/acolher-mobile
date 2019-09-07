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
import br.com.acolher.controller.InstituicaoController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;

public class CadastroInstituicao extends AppCompatActivity{

    Button continuarCadastro;
    TextInputLayout inputPassword;
    TextInputLayout inputTelefone;
    TextInputLayout inputCnpj;
    TextInputLayout inputNome;
    TextInputLayout inputEmail;
    InstituicaoController ic;

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
                validateForm();
                //Intent intentEndereco = new Intent(CadastroActivity.this, CadastroEndereco.class);
                //startActivity(intentEndereco);
            }
        });

    }

    public boolean validateForm(){

        String nome = inputNome.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        String cnpj = Validacoes.cleanCNPJ(inputCnpj.getEditText().getText().toString());
        String telefone = Validacoes.cleanTelefone(inputTelefone.getEditText().getText().toString());

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

}