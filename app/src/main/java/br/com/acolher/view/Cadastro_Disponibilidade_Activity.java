package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.controller.DisponibilidadeController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cadastro_Disponibilidade_Activity extends AppCompatActivity {
    //Declarando
    private RetrofitInit retrofitInit = new RetrofitInit();
    public static final String TAG = "api";
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    TextInputLayout inputCodigo;
    TextInputLayout inputNome;
    TextInputLayout inputData;
    ImageButton btnCalendar;
    TextInputLayout inputHora;
    Button concluirCadastro;
    int currentHour;
    int currentMinute;
    String amPm;
    UsuarioController uc;
    DisponibilidadeController dc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro_disponibilidade);

        //Buscar botões em layout
        inputCodigo = (TextInputLayout) findViewById(R.id.inputCodigo);
        inputNome = (TextInputLayout) findViewById(R.id.inputNomeCompleto);
        inputData = (TextInputLayout) findViewById(R.id.inputDataNasc);
        btnCalendar = (ImageButton) findViewById(R.id.btnCalendar);
        inputHora = (TextInputLayout) findViewById(R.id.inputHora);
        concluirCadastro = (Button) findViewById(R.id.buttonConcluirCadastro);


        //Mocando dados para futura integração
        inputCodigo.getEditText().setText("1");
        inputCodigo.setEnabled(false);
        inputNome.getEditText().setText("Alysson Alves");
        inputNome.setEnabled(false);
        inputData.getEditText().setText("18/10/1990");
        inputHora.getEditText().setText("08:00");

        concluirCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uc = new UsuarioController();
                dc = new DisponibilidadeController();

                if ( validateForm() ){
                    Consulta novaConsulta = new Consulta();
                    Endereco endereco = new Endereco();
                    Status statusConsulta;
                    String codigo = inputCodigo.getEditText().getText().toString();
                    String nome = inputNome.getEditText().getText().toString();
                    String hora = inputHora.getEditText().getText().toString();
                    String sData = inputData.getEditText().getText().toString();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
                    LocalDateTime data = LocalDateTime.parse(sData,formatter);


                    novaConsulta.setData(data);
                    novaConsulta.setHora(hora);
                    statusConsulta = Status.DISPONIVEL;
                    novaConsulta.setStatusConsulta(statusConsulta);
                    endereco = enderecoMocado();
                    novaConsulta.setEndereco(endereco);

                    cadastroConsulta(novaConsulta);

                }

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

    //Metodos a serem chamados:

    public void openCalendar(){

        inputData.setErrorEnabled(false);

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(Cadastro_Disponibilidade_Activity.this, new DatePickerDialog.OnDateSetListener() {
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

        timePickerDialog = new TimePickerDialog(Cadastro_Disponibilidade_Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                // Formatar para Am Pm caso nescessario
                /*if (hourOfDay >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }*/
                inputHora.getEditText().setText(String.format("%02d:%02d", hourOfDay, minutes));
            }
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }

    public boolean validateForm(){

        String codigo = inputCodigo.getEditText().getText().toString();
        String nome = inputNome.getEditText().getText().toString();
        String data = inputData.getEditText().getText().toString();
        String hora = inputHora.getEditText().getText().toString();

        if(codigo == null || codigo.isEmpty() || codigo == ""){
            inputCodigo.getEditText().setError("Campo obrigatorio!");
            return false;
        }
        if(uc.validarNome(nome) != ""){
            inputNome.getEditText().setError(uc.validarNome(nome));
            return false;
        }
        if(dc.validarData(data) != ""){
            inputNome.getEditText().setError(dc.validarData(data));
            return false;
        }

        if(dc.validarData(hora) != ""){
            inputNome.getEditText().setError(dc.validarData(hora));
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
                    int status = response.code();
                    Log.d(TAG, String.valueOf(status));
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

    private Endereco enderecoMocado(){
        return new Endereco("51330270", "RUA DAS NINFAS",  "RECIFE",  "PE",  "COHAB", "35",  "0000",  "0000");
    }
}