package br.com.acolher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import br.com.acolher.R;
import br.com.acolher.helper.Helper;
import br.com.acolher.model.Mensagem;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;
    private String idUsuarioRementente;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if( mensagens != null ){
            String typeUser = (String) Helper.getSharedPreferences("TYPE", "", 2, getContext());
            Integer codeUser = (Integer) Helper.getSharedPreferences("USERCODE",  0, 1, getContext());
            if(typeUser.equals("INSTITUICAO")) {
                idUsuarioRementente = "i" + codeUser;
            }else{
                idUsuarioRementente = codeUser.toString();
            }

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            Mensagem mensagem = mensagens.get( position );

            if(idUsuarioRementente.equals( mensagem.getIdUsuario() )  ){
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
            }else {
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }

            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText( mensagem.getMensagem() );
        }

        return view;

    }
}
