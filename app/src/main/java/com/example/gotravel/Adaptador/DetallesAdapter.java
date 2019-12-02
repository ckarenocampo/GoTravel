package com.example.gotravel.Adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.gotravel.Clases.DetallesTours;
import com.example.gotravel.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetallesAdapter extends BaseAdapter {
    private Context c;
    private ArrayList<DetallesTours> lstDetallesTour;
    private DetallesAdapter.OnItemClickListener listener;
    private LayoutInflater inflater;

    public DetallesAdapter(Context c, ArrayList<DetallesTours> lst, DetallesAdapter.OnItemClickListener listener) {
        this.c = c;
        this.lstDetallesTour = lst;
        this.listener = listener;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lstDetallesTour.size();
    }

    @Override
    public Object getItem(int pos) {
        return lstDetallesTour.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.tour_item, viewGroup, false);
        }
        TextView nombre = view.findViewById(R.id.txtNombre);
        TextView agencia =  view.findViewById(R.id.txtAgencia);
        TextView lugar =  view.findViewById(R.id.txtLugar);
        TextView fecha =  view.findViewById(R.id.txtFecha);
        TextView hora =  view.findViewById(R.id.txtHora);
        TextView precio =  view.findViewById(R.id.txtPrecio);
        TextView total =  view.findViewById(R.id.txtTotal);
        TextView tel =  view.findViewById(R.id.txtTelefono);


        CardView cvlstDetalle = view.findViewById(R.id.cv);


        //CREAMOS OBJETO DE LA CLASE TOURS
        final DetallesTours obj = lstDetallesTour.get(i);
        final int pos = i;
        nombre.setText(obj.getNombreTour());
        agencia.setText(obj.getAgencia());
        lugar.setText(obj.getLugarSalida());
        fecha.setText(obj.getFecha());
        hora.setText(obj.getHora());
        precio.setText(obj.getPrecio());
        total.setText(obj.getTotal());
        tel.setText(obj.getTelefono());



        cvlstDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(obj, pos);
            }
        });
        return view;
    }

    public interface OnItemClickListener {
        void onItemClick(DetallesTours objRes, int position);

    }
}