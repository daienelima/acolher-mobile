package br.com.acolher.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;

import br.com.acolher.apiconfig.ConfiguracaoFirebase;

public class ChatApplication extends Application implements Application.ActivityLifecycleCallbacks{
    private DatabaseReference firebase;
    private void setOnline(boolean on){
        String typeUser = Helper.getSharedPreferences(CONSTANTES.TYPE,  "", 2, getApplicationContext()).toString();
        String idUser = Helper.getSharedPreferences(CONSTANTES.USERCODE,  0, 1, getApplicationContext()).toString();
        if(typeUser.equals(CONSTANTES.INSTITUICAO)){
            idUser = "i" + idUser;
        }
        firebase = ConfiguracaoFirebase.getFirebase().child("users");
        firebase.child(idUser).child("online").setValue(on);
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        setOnline(true);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        setOnline(false);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
