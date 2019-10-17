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
        TextView nombretour = view.findViewById(R.id.txtTourname);
        ImageView img =  view.findViewById(R.id.card_background);
        TextView agencia =  view.findViewById(R.id.txtProfilename);
        CardView lstContainer = view.findViewById(R.id.lstContainer);
        final Tours obj = lstTours.get(i);
        final int pos = i;
        nombretour.setText(obj.getNombretour());
        agencia.setText(obj.getAgencia());

        Context c = this.c;
        Picasso.with(c).load(obj.getImgInfo()).into(img);

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
