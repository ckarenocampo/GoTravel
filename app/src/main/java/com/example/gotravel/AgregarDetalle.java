package com.example.gotravel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gotravel.Clases.Sesion;
import com.example.gotravel.Clases.Tours;

import org.json.JSONObject;

public class AgregarDetalle extends AppCompatActivity implements Response.ErrorListener ,Response.Listener<JSONObject>{

    Sesion _SESION = Sesion.getInstance();
    ProgressDialog progreso;
    private final String URL_ADD_DETALLE="https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/AddDetalle.php?";
    Tours objT;

    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    int id,total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_detalle);
        if(getIntent().getSerializableExtra("objeto")!=null){
            objT= (Tours) getIntent().getSerializableExtra("objeto");
            id=objT.getIdTour();
            objT.setIdTour(id);
            total = (int)getIntent().getSerializableExtra("total");
            objT.setTotal(String.valueOf(total));
            AgregarDetalle();
        }

    }
    public void AgregarDetalle(){
        progreso=new ProgressDialog(this);
        progreso.setMessage("Enviando datos de compra");
        progreso.show();
        String url=URL_ADD_DETALLE+"idTour="+objT.getIdTour()+"&idUsuario="+_SESION.getIdUsuario()+"&Total="+ objT.getTotal();
        URL_ADD_DETALLE.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "error",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onResponse(JSONObject response) {

        Toast.makeText(getApplicationContext(), "Detalle agregado con exito",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
