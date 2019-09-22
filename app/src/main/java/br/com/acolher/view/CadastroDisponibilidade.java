package br.com.acolher.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.DisponibilidadeController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroDisponibilidade extends AppCompatActivity {

    private RetrofitInit retrofitInit = new RetrofitInit();
    private static final String TAG = "api";
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TextInputLayout inputNome, inputData, inputHora, inputCPR_CRM;
    private ImageButton btnCalendar;
    private Button concluirCadastro;
    private int currentHour;
    private int currentMinute;
    private String amPm;
    private UsuarioController uc;
    private DisponibilidadeController dc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro_disponibilidade);

        pegaIdCampos();


        /**
         * Moca dados na tela
         */
        inputCPR_CRM.getEditText().setText("8454654");
        inputCPR_CRM.setEnabled(false);
        inputNome.getEditText().setText("Medico");
        inputNome.setEnabled(false);
        inputData.getEditText().setText("18/10/2019");
        inputHora.getEditText().setText("08:00");

        concluirCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uc = new UsuarioController();
                dc = new DisponibilidadeController();

                if ( validateForm() ){
                    Consulta novaConsulta = new Consulta();
                    Endereco endereco = new Endereco(1,"52000000","R rua","Recife","PE", "Bairro", "150","00000","0000");
                    String hora = inputHora.getEditText().getText().toString();
                    String sData = inputData.getEditText().getText().toString();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
                    LocalDateTime data = LocalDateTime.parse(sData,formatter);

                    novaConsulta.setData(data);
                    novaConsulta.setHora(hora);
                    novaConsulta.setStatusConsulta(Status.DISPONIVEL);
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

    private void pegaIdCampos() {
        inputCPR_CRM = findViewById(R.id.inputCRP_CRM);
        inputNome = findViewById(R.id.inputNomeCompleto);
        inputData = findViewById(R.id.inputDataNasc);
        btnCalendar = findViewById(R.id.btnCalendar);
        inputHora = findViewById(R.id.inputHora);
        concluirCadastro = findViewById(R.id.buttonConcluirCadastro);
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
}