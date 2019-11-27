package br.com.acolher.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import br.com.acolher.R;
import br.com.acolher.adapters.ConversaAdapter;
import br.com.acolher.apiconfig.ConfiguracaoFirebase;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.helper.ChatApplication;
import br.com.acolher.helper.Helper;
import br.com.acolher.model.Conversa;
import br.com.acolher.model.Token;

public class ChatFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter<Conversa> adapter;
    private ArrayList<Conversa> conversas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversas;
    private TextView labelNenhumaConversa;
    private ProgressDialog progressDialogConversas;

    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chat, null);
        String typeUser = Helper.getSharedPreferences(CONSTANTES.TYPE,  "", 2, getContext()).toString();
        String idUser = Helper.getSharedPreferences(CONSTANTES.USERCODE,  0, 1, getContext()).toString();
        if(typeUser.equals(CONSTANTES.INSTITUICAO)){
            idUser = "i" + idUser;
        }

        ChatApplication application = (ChatApplication) getActivity().getApplication();

        getActivity().getApplication().registerActivityLifecycleCallbacks(application);

        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("token: " + token);


        Token t = new Token();
        t.setToken(token);
        t.setId(idUser);
        t.setOnline(true);

        firebase = ConfiguracaoFirebase.getFirebase().child("users");

        firebase.child(idUser)
                .setValue(t);

        conversas = new ArrayList<>();
        listView = mView.findViewById(R.id.lv_conversas);
        adapter = new ConversaAdapter(getContext(), conversas );
        listView.setAdapter(adapter);
        labelNenhumaConversa = mView.findViewById(R.id.labelNenhumaConversa);

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("conversas")
                .child(idUser);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                conversas.clear();
                for ( DataSnapshot dados: dataSnapshot.getChildren() ){
                    Conversa conversa = dados.getValue( Conversa.class );
                    conversas.add(conversa);
                }
                adapter.notifyDataSetChanged();

                if(progressDialogConversas.isShowing()){
                    progressDialogConversas.dismiss();
                }

                if(conversas.size() == 0){
                    labelNenhumaConversa.setVisibility(View.VISIBLE);
                    progressDialogConversas.dismiss();
                }
                progressDialogConversas.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Conversa conversa = conversas.get(position);
            Intent intent = new Intent(getActivity(), ConversaActivity.class );

            intent.putExtra("idDestinatario", conversa.getIdUsuario());
            intent.putExtra("nomeDestinatario", conversa.getNome());

            startActivity(intent);

        });

        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();
        progressDialogConversas = new ProgressDialog(mView.getContext());
        progressDialogConversas.setMessage("Carregando...");
        progressDialogConversas.setCancelable(false);
        progressDialogConversas.show();
        firebase.addValueEventListener(valueEventListenerConversas);
        progressDialogConversas.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }
}
