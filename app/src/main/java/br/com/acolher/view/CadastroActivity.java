package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.UiAutomation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.MaskWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private String crpCrm;
    private boolean hasCrpCrm;
    public static final String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private Usuario usuario = new Usuario();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        
        hasCrpCrm = false;

        //Configurações da activity
        setContentView(R.layout.activity_cadastro_basico);

        inputDataNasc = (TextInputLayout) findViewById(R.id.inputDataNasc);

        btnCalendar = (ImageButton) findViewById(R.id.btnCalendar);

        continuarCadastro = (Button) findViewById(R.id.buttonContinuarCadastro);

        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);

        inputCpf = (TextInputLayout) findViewById(R.id.inputCPFCad);
        inputCpf.getEditText().addTextChangedListener(MaskWatcher.buildCpf());

        inputCRM_CRP = (TextInputLayout) findViewById(R.id.inputCRM);
        inputCRM_CRP.setVisibility(View.GONE);

        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));

        inputNome = (TextInputLayout) findViewById(R.id.inputNomeCompleto);

        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);

        Intent intent = getIntent();
        if(intent.getStringExtra("perfil") != null){
           if(intent.getStringExtra("perfil").equals("profissional")) {
               hasCrpCrm = true;
               inputCRM_CRP.setVisibility(View.VISIBLE);
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
                    Intent intent = getIntent();
                    Endereco endereco = new Endereco();
                    endereco.setCodigo(intent.getIntExtra("codigoEndereco", 0));

                    usuario.setEndereco(endereco);
                    usuario.setNome_completo(nome);
                    usuario.setData_nascimento(data);
                    usuario.setCpf(cpf);
                    usuario.setTelefone(telefone);
                    usuario.setEmail(email);
                    usuario.setPassword(password);
                    if(hasCrpCrm){
                        usuario.setCrm_crp(crpCrm);
                    }else{
                        usuario.setCrm_crp("");
                    }

                    cadastroUsuario(usuario);
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
        crpCrm = inputCRM_CRP.getEditText().getText().toString();

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

        if(uc.validaCpf(cpf) != ""){
                inputCpf.getEditText().setError(uc.validaCpf(cpf));
                return false;
        }

        if(hasCrpCrm){
            if(uc.validaCRM(crpCrm) != ""){
                inputCRM_CRP.getEditText().setError((uc.validaCRM(crpCrm)));
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

    private void cadastroUsuario(Usuario usuario){
        Call<Usuario> cadastroUsuario = retrofitInit.getService().cadastroUsuario(usuario);
        cadastroUsuario.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Log.d(TAG, response.body().toString());

                    String tipoUsuario = "";
                    if(response.body().getCrm_crp().isEmpty()){
                        tipoUsuario = "paciente";
                    }else{
                        tipoUsuario = "voluntario";
                    }
                    salvarDadosUsuario(response.body().getCodigo(), tipoUsuario);

                    Intent home = new Intent(CadastroActivity.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(TAG, String.valueOf(response.code()));
                    if(response.code() == 403){
                        if(response.errorBody().contentLength() == 18){
                            msgJaCadastrado("CPF");
                        }
                        if(response.errorBody().contentLength() == 21){
                            msgJaCadastrado("E-mail");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    public void msgJaCadastrado(String campo){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CadastroActivity.this);
        alertDialog.setTitle("Atenção");
        alertDialog.setMessage(campo + " " + "já cadastrado.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }

    public void salvarDadosUsuario(Integer codigoUsuario, String tipoUsuario) {
        sharedPreferences = this.getSharedPreferences("USERDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USERCODE", codigoUsuario);
        editor.putString("TYPE", tipoUsuario);
        editor.apply();
    }
}