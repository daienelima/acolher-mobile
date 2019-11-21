package br.com.acolher.Introdução;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;

import br.com.acolher.R;
import br.com.acolher.helper.Helper;
import br.com.acolher.view.Login;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        primeiroAcesso();
        addSlide(AppIntro2Fragment.newInstance("Bem vindo(a)", "Seja bem vindo(a) ao App Acolher.", R.drawable.logo, getResources().getColor(R.color.green_slide_background)));
        addSlide(AppIntroFragment.newInstance("Permissões do App", "Para o funcionamento do App, algumas permissões deverão ser concedidas!", R.drawable.maps, getResources().getColor(R.color.blue_slide_background)));
        addSlide(AppIntroFragment.newInstance("Pronto, Estamos quase lá!", "Agora vamos para um breve tutorial do APP.", R.drawable.tutorial, getResources().getColor(R.color.purple_slide_background)));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro_home_user));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro_agenda));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro_settings));
        addSlide(AppIntro2Fragment.newInstance("Feito!", "Introdução concluída. clique em Done para prosseguir com o APP", R.drawable.check, getResources().getColor(R.color.buttonCadastroProsseguir)));

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 2);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Helper.setSharedPreferences("PRIMEIRO_ACESSO", false, 4, getApplicationContext());
        callLogin();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    public void primeiroAcesso(){
        if(!(Boolean) Helper.getSharedPreferences("PRIMEIRO_ACESSO", true, 4, getApplicationContext())){
            callLogin();
        }
    }

    public void callLogin(){
        Intent login = new Intent(this, Login.class);
        startActivity(login);
        finish();
    }
}
