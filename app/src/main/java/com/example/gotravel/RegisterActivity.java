package com.example.gotravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import org.json.JSONObject;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, Response.Listener<JSONObject>,Response.ErrorListener {

    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progeso;

    ImageButton btnIniciarSesion;
    EditText txtUsuario,txtCorreo,txtClave,txtConfirmar;
    TextView lblMensaje;
    Button btnRegistrar;
    private final String URL_REGISTRO = "https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/Registro.php?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        btnIniciarSesion = findViewById(R.id.btnInicio);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtClave = findViewById(R.id.txtClave);
        txtConfirmar = findViewById(R.id.txtConfirmar);
        lblMensaje = findViewById(R.id.lblMensaje);
        btnRegistrar = findViewById(R.id.btnRegistrar);

       /* txtConfirmar.setText("Confirmar contraseña");
        txtConfirmar.setTextColor(Color.parseColor("#afafaf"));
        txtClave.setText("Ingrese una contraseña");
        txtClave.setTextColor(Color.parseColor("#afafaf"));
*/
        requestQueue= Volley.newRequestQueue(this);

        txtClave.setOnFocusChangeListener( this);
        txtConfirmar.setOnFocusChangeListener( this);
        btnRegistrar.setOnClickListener( this);
        btnIniciarSesion.setOnClickListener( this);
    }

    private void LlamarWebServices(){
        progeso=new ProgressDialog(this);
        progeso.setMessage("Enviando datos al servidor");
        progeso.show();
        String url=URL_REGISTRO+"Nombre="+txtUsuario.getText().toString()+"&Correo="+txtCorreo.getText().toString()+"&Clave="+txtClave.getText().toString()+"";
        url.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnRegistrar:
                    if (txtClave.getText().toString().equals(txtConfirmar.getText().toString())) {
                        LlamarWebServices();
                    } else {
                        lblMensaje.setText("Las contraseñas no coinciden");
                    }

                break;
            case R.id.btnInicio:
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void onFocusChange(View v, boolean b){
        switch (v.getId()){
                case R.id.txtClave:{
                    String vtext=txtClave.getText().toString();
                    if (b) {
                        if(txtClave.getText().toString().contains("Ingrese una contraseña")){
                            txtClave.setText("");
                        }
                        else{
                            txtClave.setText(vtext);
                        }
                        txtClave.setTextColor(Color.BLACK);
                    }
                    else{
                        if(txtClave.getText().toString().isEmpty()){
                            txtClave.setText("Ingrese una contraseña");
                            txtClave.setTextColor(Color.parseColor("#afafaf"));
                        }
                    }
                }
                break;
            case R.id.txtConfirmar: {
                    String text = txtConfirmar.getText().toString();
                    if (b) {
                        if (txtConfirmar.getText().toString().contains(("Confirmar contraseña"))) {
                            txtConfirmar.setText("");
                        } else {
                            txtConfirmar.setText(text);
                        }
                        txtConfirmar.setTextColor(Color.BLACK);
                    } else {
                        if (txtConfirmar.getText().toString().isEmpty()) {
                            txtConfirmar.setText("Confirmar contraseña");
                            txtConfirmar.setTextColor(Color.parseColor("#afafaf"));
                        }
                    }
                }
                break;
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progeso.hide();
        lblMensaje.setText("No se pudo registrar");
    }

    @Override
    public void onResponse(JSONObject response) {
        progeso.hide();
        Toast.makeText(getApplicationContext(), "Registrado con exito",Toast.LENGTH_SHORT).show();
        finish();
    }
}