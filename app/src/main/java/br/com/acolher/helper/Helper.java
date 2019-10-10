package br.com.acolher.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import java.security.Key;

public class Helper {

    public static ProgressDialog progressDialog;

    public static void setSharedPreferences(String key, Object value, Integer type, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("USERDATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (type){
            case 1:
                editor.putInt(key, (Integer) value);
                break;
            case 2:
                editor.putString(key, (String) value);
                break;
            case 3:
                editor.putFloat(key, (Float) value);
                break;
            case 4:
                editor.putBoolean(key, (Boolean) value);
                break;
            case 5:
                editor.putLong(key, (Long) value);
                break;
            default:
                break;
        }

        editor.apply();

    }

    public static Object getSharedPreferences(String key, Object defaultValue, Integer type, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("USERDATA", Context.MODE_PRIVATE);
        switch (type){
            case 1:
                return sharedPreferences.getInt(key, (Integer) defaultValue);
            case 2:
                return sharedPreferences.getString(key, (String) defaultValue);
            case 3:
                return sharedPreferences.getFloat(key, (Float) defaultValue);
            case 4:
                return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
            case 5:
                return sharedPreferences.getLong(key, (Long) defaultValue);
            default:
                return "";
        }
    }

    public static void openProgressDialog(String text, Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(text);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void closeProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}
