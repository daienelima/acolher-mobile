package br.com.acolher.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.DisponibilidadeController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Status;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroDisponibilidade extends AppCompatActivity {

    private RetrofitInit retrofitInit = new RetrofitInit();
    private static final String TAG = "API";
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TextInputLayout inputNome, inputData, inputHora, inputCPR_CRM, inputNumero;
    private ImageButton btnCalendar;
    private Button concluirCadastro, buttonCancelar;
    private int currentHour;
    private int currentMinute;
    private Address address;
    private Double lon;
    private Double lat;
    private Integer codigoEnderecoRecente;
    private Endereco enderecoConsulta = new Endereco();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro_disponibilidade);

        pegaIdCampos();

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0.0);
        lon = intent.getDoubleExtra("long", 0.0);
        codigoEnderecoRecente = intent.getIntExtra("codigoRecente", 0);

        sharedPreferences = getApplicationContext().getSharedPreferences("USERDATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LAT", lat.toString());
        editor.putString("LON", lon.toString());
        editor.apply();

        inputCPR_CRM.getEditText().setText("8454654");
        inputCPR_CRM.setEnabled(false);
        inputNome.getEditText().setText("Medico");
        inputNome.setEnabled(false);

        if(lat != 0.0 && lon != 0.0){
            try {
                address = Validacoes.buscarEndereco(lat, lon, getApplicationContext());
                enderecoConsulta = pegarEndereco(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        concluirCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( validateForm() ){
                    Consulta novaConsulta = new Consulta();
                    enderecoConsulta.setNumero(inputNumero.getEditText().getText().toString());
                    Usuario profissional = new Usuario();
                    profissional.setCodigo(1);
                    String hora = inputHora.getEditText().getText().toString();
                    String sData = inputData.getEditText().getText().toString();

                    novaConsulta.setData(sData);
                    novaConsulta.setHora(hora);
                    novaConsulta.setStatusConsulta(Status.DISPONIVEL);
                    novaConsulta.setProfissional(profissional);

                    if(codigoEnderecoRecente == 0){
                        Call<Endereco> cadastroEndereco = retrofitInit.getService().cadastroEndereco(enderecoConsulta);
                        cadastroEndereco.enqueue(new Callback<Endereco>() {
                            @Override
                            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, String.valueOf(response.code()));
                                    enderecoConsulta = response.body();
                                    editor.putInt("COD_END_RECENT", enderecoConsulta.getCodigo());
                                    editor.apply();
                                    novaConsulta.setEndereco(enderecoConsulta);
                                    cadastroConsulta(novaConsulta);
                                } else {
                                    Log.d(TAG, String.valueOf(response.code()));
                                }

                            }

                            @Override
                            public void onFailure(Call<Endereco> call, Throwable t) {
                                Log.d(TAG, t.getMessage());
                            }
                        });
                    }else{
                        enderecoConsulta.setCodigo(codigoEnderecoRecente);
                        novaConsulta.setEndereco(enderecoConsulta);
                        cadastroConsulta(novaConsulta);
                    }

                }

            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(CadastroDisponibilidade.this, MapsActivity.class);
                startActivity(home);
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTime();
                openCalendar();
            }
        });

        inputData.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputData.getEditText().clearFocus();
                    openCalendar();
                }
            }
        });
        inputHora.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputHora.getEditText().clearFocus();
                    openTime();
                }
            }
        });

    }

    private void pegaIdCampos() {
        inputCPR_CRM = findViewById(R.id.input_CRP_CRM);
        inputNome = findViewById(R.id.inputNomeCompleto);
        inputData = findViewById(R.id.inputDataNasc);
        btnCalendar = findViewById(R.id.btnCalendar);
        inputHora = findViewById(R.id.inputHora);
        buttonCancelar = findViewById(R.id.buttonCancelar);
        concluirCadastro = findViewById(R.id.buttonConcluirCadastro);
        inputNumero = findViewById(R.id.inputNumero);
    }

    public void openCalendar(){

        inputData.setErrorEnabled(false);

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(CadastroDisponibilidade.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                inputData.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void openTime(){
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(CadastroDisponibilidade.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                inputHora.getEditText().setText(String.format("%02d:%02d", hourOfDay, minutes));
            }
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }

    public boolean validateForm(){

        String nome = inputNome.getEditText().getText().toString();
        String data = inputData.getEditText().getText().toString();
        String hora = inputHora.getEditText().getText().toString();

        if(!DisponibilidadeController.empty(nome)){
            inputNome.setError("Nome não dede ficar em branco");
            return false;
        }

        if(!DisponibilidadeController.empty(data)){
            inputData.setError("Campo Obrigatorio");
            return false;
        }

        if(!DisponibilidadeController.empty(hora)){
            inputHora.setError("Campo Obrigatorio");
            return false;
        }


        return true;
    }


    private void cadastroConsulta(Consulta consulta){

        Call<Consulta> cadastro = retrofitInit.getService().cadastroConsulta(consulta);

        cadastro.enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Intent home = new Intent(CadastroDisponibilidade.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(TAG, "erro");
                    Log.d(TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {

            }
        });
    }

    public Endereco pegarEndereco(Address address){
        Endereco endereco = new Endereco();

        endereco.setLongitude(lon.toString());
        endereco.setLatitude(lat.toString());
        endereco.setLogradouro(address.getThoroughfare());
        endereco.setBairro(address.getSubLocality());
        endereco.setCep(Validacoes.cleanCep(address.getPostalCode()));
        endereco.setCidade(address.getSubAdminArea());
        endereco.setUf(Validacoes.deParaEstados(address.getAdminArea()));

        return endereco;
    }
}