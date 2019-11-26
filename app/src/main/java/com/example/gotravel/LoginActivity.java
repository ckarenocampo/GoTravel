package com.example.gotravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gotravel.Clases.Sesion;
import com.example.gotravel.Clases.Tours;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>,Response.ErrorListener,View.OnFocusChangeListener {


    //Manejo de sesiones
    Sesion _SESION = Sesion.getInstance();

    ImageButton btnRegitrar;
    Button btnIniciarSesion;
    private EditText txtUsuario,txtClave;
    TextView txtRegistrar,lblMensaje;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progeso;
    //Declaracion de la variable para guardar los datos en sesion
    private SharedPreferences preferencias;
    private final String URL_WEB_LOGIN="https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/Login.php?";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btnRegitrar = findViewById(R.id.btnRegistrar);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        txtRegistrar = findViewById(R.id.txtRegistrate);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtClave = findViewById(R.id.txtClave);
        lblMensaje = findViewById(R.id.lblMensaje);
        preferencias=getSharedPreferences("preferencias", MODE_PRIVATE);
        requestQueue= Volley.newRequestQueue(this);

        existe();
        txtRegistrar.setOnClickListener(this);
        btnRegitrar.setOnClickListener(this);
        btnIniciarSesion.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtRegistrate:
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRegistrar:
                Intent intent2=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.btnIniciarSesion:
                LlamarWebServices();
                break;
        }
    }

    private void existe() {
        String Nombre= getUsuario();

        if(!TextUtils.isEmpty(Nombre)){
            txtUsuario.setText(Nombre);
            txtClave.setText("");
        }
        else {

        }
    }
    private String getUsuario() {
        return  preferencias.getString("Nombre", "");
    }

    private void GuardarDatosShared(String Nombre){
        SharedPreferences.Editor editor= preferencias.edit();
        editor.putString("Nombre",Nombre);
        //Guarda los valores en segundo plano
        editor.apply();
    }
    private void LlamarWebServices(){
        progeso=new ProgressDialog(this);
        progeso.setMessage("Cargando...");
        progeso.show();
       // String url="https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/Login.php?Nombre=karen&Clave=karen123";
        String url=URL_WEB_LOGIN+"Nombre="+txtUsuario.getText().toString()+"&Clave="+txtClave.getText().toString()+"";
        url.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()){
            case R.id.txtClave:
            String vtext=txtClave.getText().toString();
            if (b) {
                if(txtClave.getText().toString().contains("Ingrese una contraseña")){
                    txtClave.setText("");
                }else {
                    txtClave.setText(vtext);
                }
                txtClave.setTextColor(Color.BLACK);
            }else{
                if(txtClave.getText().toString().isEmpty()){
                    txtClave.setText("Ingrese una contraseña");
                    txtClave.setTextColor(Color.parseColor("#afafaf"));
                }
            }
            break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progeso.dismiss();
        lblMensaje.setText("Datos inválidos");
    }

    @Override
    public void onResponse(JSONObject response) {
        progeso.dismiss();
        JSONArray json=response.optJSONArray("Usuario");
        JSONObject jsonObject=null;
        try{

            for(int i=0; i<json.length();i++) {
                jsonObject = json.getJSONObject(i);
            }
            if(jsonObject.getString("respuesta").equals("Ok")) {
                _SESION.setIdUsuario(jsonObject.getInt("idUsuario"));
                _SESION.setNombre(jsonObject.getString("Nombre"));
                _SESION.setCorreo(jsonObject.getString("Correo"));
                GuardarDatosShared(_SESION.getNombre());
                Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                lblMensaje.setText("Intente más tarde");
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}