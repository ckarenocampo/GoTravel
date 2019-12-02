package com.example.gotravel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gotravel.Adaptador.ToursAdapter;
import com.example.gotravel.Clases.Sesion;
import com.example.gotravel.Clases.Tours;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, ToursAdapter.OnItemClickListener, Response.ErrorListener{

    Sesion _SESION = Sesion.getInstance();

    final static String URL_LISTA_TOURS = "https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/ListaTours.php";
    //variables a utilizar
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progreso;
    ListView lstTour;
    boolean isFABOpen=false;
    public static ArrayList<Tours> lstTours = new ArrayList<>();
    FloatingActionMenu fab,fabin;
    FloatingActionButton fabLogin,fabLogout,fabInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lstTour = findViewById(R.id.lstTour);
        fab = findViewById(R.id.fab);
        fabin = findViewById(R.id.fabin);
        fabLogin = findViewById(R.id.fabLogin);
        fabLogout = findViewById(R.id.fabLogout);
        fabInfo = findViewById(R.id.fabInfo);
        fab.setOnClickListener(this);
        fabin.setOnClickListener(this);

        fab.setClosedOnTouchOutside(true);
        fabLogin.setOnClickListener(this);
        fabLogout.setOnClickListener(this);
        fabInfo.setOnClickListener(this);



    }

    @Override
    public void onStart() {
        super.onStart();
        if (MainActivity.this != null) {
            progreso = new ProgressDialog(this);
            requestQueue = Volley.newRequestQueue(this);
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            LlamarWebServices();

            if(_SESION.getNombre()==null){
                fabin.hideMenu(true);
                fab.showMenu(true);
            }else{
                fab.hideMenu(true);
                fabin.showMenu(true);
            }
          /*      if (lstTours.size() < 1) {
                    LlamarWebServices();
                } else {
                    ToursAdapter adapter = new ToursAdapter(this, lstTours, this);
                    lstTour.setAdapter(adapter);
                }
          */
        }
    }

    private void LlamarWebServices() {
        progreso = new ProgressDialog(this);
        progreso.setMessage(getString(R.string.MensajeCarga));
        progreso.show();
        URL_LISTA_TOURS.replace(" ", "%20");
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_LISTA_TOURS, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.dismiss();
        Log.e("onError", error.getMessage());
        Toast.makeText(this, "Error al descargar datos de tours", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        progreso.dismiss();
        JSONArray json = response.optJSONArray("Tours");
        JSONObject jsonObject = null;
        lstTours.clear();
        Tours objT;
        try {
            for (int i = 0; i < json.length(); i++) {
                jsonObject = json.getJSONObject(i);
                objT = new Tours();
                objT.setIdTour(jsonObject.getInt("idTour"));
                objT.setNombre(jsonObject.getString("NombreTour"));
                objT.setDetalle(jsonObject.getString("Detalle"));
                objT.setLugarSalida(jsonObject.getString("LugarSalida"));
                objT.setFecha(jsonObject.getString("Fecha"));
                objT.setHora(jsonObject.getString("Hora"));
                objT.setPrecio(jsonObject.getString("Precio"));
                objT.setTelefono(jsonObject.getString("Telefono"));
                objT.setAgencia(jsonObject.getString("Nombre"));
                objT.setImgInfo(jsonObject.getString("imgInfo"));
                objT.setImgPerfil(jsonObject.getString("Imagen"));
                lstTours.add(objT);
            }
            ToursAdapter adapter = new ToursAdapter(MainActivity.this, lstTours, this);
            lstTour.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void onItemClick(Tours objRes, int position) {
        Intent intent = new Intent(this, info_tour.class);
        intent.putExtra("objeto", objRes);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:

            break;
            case R.id.fabLogout:
                CerrarSesion();
                break;
            case R.id.fabLogin:
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
            case R.id.fabInfo:
                startActivity(new Intent(MainActivity.this,detalles_tours.class));

                break;
        }
    }

    private void CerrarSesion(){
        Intent inten= new Intent(this,MainActivity.class);
        inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _SESION.CerrarSesion();
        startActivity(inten);

    }


}
