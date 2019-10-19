package com.example.gotravel.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.gotravel.Clases.Tours;
import com.example.gotravel.MainActivity;
import com.example.gotravel.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ToursAdapter extends BaseAdapter {
    private Context c;
    private ArrayList<Tours> lstTours;
    private ToursAdapter.OnItemClickListener listener;
    private LayoutInflater inflater;

    public ToursAdapter(Context c, ArrayList<Tours> lst, ToursAdapter.OnItemClickListener listener) {
        this.c = c;
        this.lstTours = lst;
        this.listener = listener;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lstTours.size();
    }

    @Override
    public Object getItem(int pos) {
        return lstTours.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.card_item, viewGroup, false);
        }
        TextView nombre = view.findViewById(R.id.txtNombre);
        TextView agencia =  view.findViewById(R.id.txtAgencia);
        ImageView imgInfo =  view.findViewById(R.id.imgInfo);
        ImageView imgPerfil =  view.findViewById(R.id.imgPerfil);
        CardView lstContainer = view.findViewById(R.id.lstContainer);
        //CREAMOS OBJETO DE LA CLASE TOURS
        final Tours obj = lstTours.get(i);
        final int pos = i;
        nombre.setText(obj.getNombre());
        agencia.setText(obj.getAgencia());

        //se supone que con this tiene que ser pero como no funciona
        Picasso.with(c).load(obj.getImgInfo()).into(imgInfo);
        // lo intente asi pero no se si esta bien declarado el contexto
        Picasso.with(c).load(obj.getImgPerfil()).into(imgPerfil);

        lstContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(obj, pos);
            }
        });
        return view;
    }

    public interface OnItemClickListener {
        void onItemClick(Tours objRes, int position);

    }
}
