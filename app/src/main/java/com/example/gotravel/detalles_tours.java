package com.example.gotravel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gotravel.Adaptador.DetallesAdapter;
import com.example.gotravel.Adaptador.ToursAdapter;
import com.example.gotravel.Clases.DetallesTours;
import com.example.gotravel.Clases.Sesion;
import com.example.gotravel.Clases.Tours;
import com.example.gotravel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class detalles_tours extends AppCompatActivity implements Response.Listener<JSONObject>, DetallesAdapter.OnItemClickListener, Response.ErrorListener {

    final static String URL_LISTA_TOURS = "https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/vistaDetalle.php?";
    //variables a utilizar
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progreso;
    ListView lstDetalles;
    Sesion _SESION = Sesion.getInstance();
    Tours objTo;
    DetallesTours objT;

    public static ArrayList<DetallesTours> lstDetalleTour = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tours);
        lstDetalles = findViewById(R.id.lstDetallesTour);


    }
    @Override
    public void onStart() {
        super.onStart();
        if (detalles_tours.this != null) {
            progreso = new ProgressDialog(this);
            requestQueue = Volley.newRequestQueue(this);
            VerDetalleTour();
        }
    }
    private void VerDetalleTour() {
        progreso = new ProgressDialog(this);
        progreso.setMessage(getString(R.string.MensajeCarga));
        progreso.show();
        String url=URL_LISTA_TOURS+"idUsuario="+_SESION.getIdUsuario();
        URL_LISTA_TOURS.replace(" ", "%20");
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.dismiss();
        Log.e("onError", error.getMessage());
        Toast.makeText(this, "No hay tours cancelados", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        progreso.dismiss();
        JSONArray json = response.optJSONArray("vistaDetalle");
        JSONObject jsonObject = null;
        lstDetalleTour.clear();
        DetallesTours objT;
        try {
            for (int i = 0; i < json.length(); i++) {
                jsonObject = json.getJSONObject(i);
                objT = new DetallesTours();
                objT.setIdDetalle(jsonObject.getInt("idDetalle"));
                objT.setNombreTour(jsonObject.getString("NombreTour"));
                objT.setAgencia(jsonObject.getString("Nombre"));
                objT.setLugarSalida(jsonObject.getString("LugarSalida"));
                objT.setFecha(jsonObject.getString("Fecha"));
                objT.setHora(jsonObject.getString("Hora"));
                objT.setPrecio(jsonObject.getString("Precio"));
                objT.setTotal(jsonObject.getString("Total"));
                objT.setTelefono(jsonObject.getString("Telefono"));
                lstDetalleTour.add(objT);
            }
            DetallesAdapter adapter = new DetallesAdapter(this, lstDetalleTour, this);
            lstDetalles.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(DetallesTours objRes, int position) {

    }
}
