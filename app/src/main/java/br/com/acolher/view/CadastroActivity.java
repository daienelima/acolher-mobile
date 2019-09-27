package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.MaskWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.helper.Validacoes;

public class CadastroActivity extends AppCompatActivity{

    Calendar calendar;
    DatePickerDialog datePickerDialog;

    private TextInputLayout inputDataNasc;
    private ImageButton btnCalendar;
    private Button continuarCadastro;
    private TextInputLayout inputPassword;
    private TextInputLayout inputTelefone;
    private TextInputLayout inputCpf;
    private TextInputLayout inputCRM_CRP;
    private TextInputLayout inputNome;
    private TextInputLayout inputEmail;
    private UsuarioController uc;
    private String nome;
    private String data;
    private String email;
    private String password;
    private String cpf;
    private String telefone;
    private String crm;
    private boolean hasCpf;
    private boolean hasCrm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        hasCpf = false;
        hasCrm = false;

        //Configurações da activity
        setContentView(R.layout.activity_cadastro_basico);

        inputDataNasc = (TextInputLayout) findViewById(R.id.inputDataNasc);

        btnCalendar = (ImageButton) findViewById(R.id.btnCalendar);

        continuarCadastro = (Button) findViewById(R.id.buttonContinuarCadastro);

        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);

        inputCpf = (TextInputLayout) findViewById(R.id.inputCPFCad);
        inputCpf.getEditText().addTextChangedListener(MaskWatcher.buildCpf());
        inputCpf.setVisibility(View.GONE);

        inputCRM_CRP = (TextInputLayout) findViewById(R.id.inputCRM);
        inputCRM_CRP.setVisibility(View.GONE);

        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));

        inputNome = (TextInputLayout) findViewById(R.id.inputNomeCompleto);

        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);

        Intent intent = getIntent();
        if(intent.getStringExtra("perfil") != null){
           switch (intent.getStringExtra("perfil")) {
               case "paciente":
                   inputCpf.setVisibility(View.VISIBLE);
                   hasCpf = true;
                   break;
               case "profissional":
                   hasCrm = true;
                   inputCRM_CRP.setVisibility(View.VISIBLE);
                   break;
               default:
                   break;
           }
        }

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendar();
            }
        });

        inputDataNasc.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputDataNasc.getEditText().clearFocus();
                    openCalendar();
                }
            }
        });

        continuarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uc = new UsuarioController();
                if(validateForm()){
                    Intent intentEndereco = new Intent(CadastroActivity.this, CadastroEndereco.class);
                    intentEndereco.putExtra("telaOrigem", "usuario");
                    intentEndereco.putExtra("nomeUsuario",nome);
                    intentEndereco.putExtra("dataUsuario", data);
                    intentEndereco.putExtra("emailUsuario", email);
                    intentEndereco.putExtra("passwordUsuario", password);
                    intentEndereco.putExtra("cpfUsuario", cpf);
                    intentEndereco.putExtra("telefoneUsuario", telefone);
                    startActivity(intentEndereco);
                }
            }
        });

    }

    public void openCalendar(){

        inputDataNasc.setErrorEnabled(false);

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(CadastroActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                inputDataNasc.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public boolean validateForm(){

        nome = inputNome.getEditText().getText().toString();
        data = inputDataNasc.getEditText().getText().toString();
        email = inputEmail.getEditText().getText().toString();
        password = inputPassword.getEditText().getText().toString();
        cpf = Validacoes.cleanCPF(inputCpf.getEditText().getText().toString());
        telefone = Validacoes.cleanTelefone(inputTelefone.getEditText().getText().toString());
        crm = inputCRM_CRP.getEditText().getText().toString();

        if(uc.validarNome(nome) != ""){
            inputNome.getEditText().setError(uc.validarNome(nome));
            return false;
        }

        if(uc.validarDataNasc(data) != ""){
            inputDataNasc.getEditText().setError(uc.validarDataNasc(data));
            return false;
        }

        if(uc.validarEmail(email) != ""){
            inputEmail.getEditText().setError(uc.validarEmail(email));
            return false;
        }

        if(uc.validaPassword(password) != ""){
            inputPassword.getEditText().setError(uc.validaPassword(password));
            return false;
        }

        if(uc.validarTelefone(telefone) != ""){
            inputTelefone.getEditText().setError(uc.validarTelefone(telefone));
            return false;
        }

        if(hasCpf){
            if(uc.validaCpf(cpf) != ""){
                inputCpf.getEditText().setError(uc.validaCpf(cpf));
                return false;
            }
        }

        if(hasCrm){
            if(uc.validaCRM(crm) != ""){
                inputCRM_CRP.getEditText().setError((uc.validaCRM(crm)));
                return false;
            }
        }

        /*if(inputNome.getText().toString().isEmpty()){
            //textInputNome.setErrorTextAppearance();
            inputNome.setError(getString(R.string.error_nome_completo));
            return false;
        }else{
            textInputNome.setErrorEnabled(false);
        }*/

        return true;

    }

}