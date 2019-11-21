package br.com.acolher.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import br.com.acolher.R;
import br.com.acolher.model.Conversa;

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversaAdapter(Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.conversas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if( conversas != null ){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lista_conversas, parent, false);
            TextView nome = (TextView) view.findViewById(R.id.tv_titulo);
            TextView ultimaMensagem = (TextView) view.findViewById(R.id.tv_subtitulo);
            Conversa conversa = conversas.get(position);
            nome.setText( conversa.getNome() );
            ultimaMensagem.setText( conversa.getMensagem() );
        }

        return view;
    }
}
