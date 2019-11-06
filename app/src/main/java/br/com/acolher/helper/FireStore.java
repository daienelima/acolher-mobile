package br.com.acolher.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FireStore {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "FIRESTORE";
    public static String hashAtual;

    public static void insertUserId(Integer codigo, String nome, String hash){
        Map<String, Object> user = new HashMap<>();
        user.put("name", nome);
        user.put("codigo", codigo);
        user.put("hash", hash);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public static String getUserId(Integer codigo){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.get("codigo") == codigo){
                                    hashAtual = (String) document.get("hash");
                                }
                            }
                        }else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return hashAtual;

    }

    public static void enviarNotificacaoVoluntario(String hash){
        try{l
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("title", "askdjaosijdoiasjd");
            jData.put("body", "foi visse, firma...");
            jGcmData.put("to", hash);
            jGcmData.put("notification", jData);

            URL url;

            url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key="+CONSTANTES.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            InputStream inputStream = conn.getInputStream();

            Log.d("not", inputStream.toString());
            Log.d("not", "Enviou, visse firma...");

        }catch (MalformedURLException ex){
            ex.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException json){
            json.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
