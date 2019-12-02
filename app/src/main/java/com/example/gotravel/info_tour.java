package com.example.gotravel;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gotravel.Clases.Sesion;
import com.example.gotravel.Clases.Tours;
import com.github.clans.fab.FloatingActionButton;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.PaymentMethodActivity;
import com.squareup.picasso.Picasso;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


public class info_tour extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    Sesion _SESION = Sesion.getInstance();
    //Variables
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    int Flag=0;


    private TextView txtNombre,txtAgencia,txtDetalle,txtLugar,txtFecha,txtHora,txtPrecio,txtCupos,txtTotal,txtTelefono;
    private ImageView imgInfo, imgPerfil;
    ProgressDialog progeso;
    Button btnPagar;
    ImageButton btnDisminuir,btnIncrementar,btnLlamar;
    Tours objT;
    int cupos = 1,precio=0,total=0;
    String amount,tel="";

    //URLS de consulta a la bd en webservice
    private final String URL_CONSULTA_ITEM="https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/TourConsultarInfo.php?";
    // Variables de pago
    private static final String PAYPAL_CLIENT_ID = "AQeyW3gXjTVnI2ljt1Q-dh02N2mPE7MkL6D06uYgrGw-6E2jJQZBw-vRoimA9h_I3GLVR-Dhpw0St-CQ";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final  int REQUEST_CODE_PAYMENT= 1;

    ProgressDialog progreso;
    private final String URL_ADD_DETALLE="https://gotravelsapp.000webhostapp.com/gotravel/web/modelos/AddDetalle.php?";

    PayPalPayment thingToBuy;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)// sandox para prueba
            .clientId(PAYPAL_CLIENT_ID);



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tour);
        txtNombre = findViewById(R.id.txtNombre);
        txtAgencia = findViewById(R.id.txtAgencia);
        txtDetalle = findViewById(R.id.txtDetalle);
        txtLugar = findViewById(R.id.txtLugar);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtTelefono = findViewById(R.id.txtTelefono);
        btnPagar = findViewById(R.id.btnPagar);
        imgInfo = findViewById(R.id.imgInfo);
        imgPerfil = findViewById(R.id.imgPerfil);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnDisminuir = findViewById(R.id.btnDisminuir);
        btnIncrementar = findViewById(R.id.btnIncrementar);
        txtCupos = findViewById(R.id.txtCupos);
        txtTotal = findViewById(R.id.txtTotal);

        //cuenta de cupos
        cupos = Integer.parseInt(txtCupos.getText().toString());

        btnIncrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cupos++;
                txtCupos.setText(String.valueOf(cupos));
                total = cupos * precio;
                txtTotal.setText("$ "+ String.valueOf(total));

            }
        });

        btnDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cupos>1){
                    cupos--;
                    txtCupos.setText(String.valueOf(cupos));
                    total = cupos * precio;
                    txtTotal.setText("$ "+ String.valueOf(total));

                }
            }
        });

        //empezar el servicio de paypal
        final Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Iniciamos la solicitud
        requestQueue= Volley.newRequestQueue(this);

        //Obtenemos el objeto de la consulta a Tours
        if(getIntent().getSerializableExtra("objeto")!=null){
            objT= (Tours) getIntent().getSerializableExtra("objeto");
            int nu=objT.getIdTour();
            objT.setIdTour(nu);
            ConsultarItemtour();
            AsignacionValores(objT.getNombre(),objT.getDetalle(),objT.getLugarSalida(),objT.getFecha(),objT.getHora(),objT.getPrecio(),objT.getTelefono(),objT.getAgencia(),objT.getImgInfo());
            precio = Integer.parseInt(objT.getPrecio());
            txtTotal.setText("$ "+ String.valueOf(precio));
            total = precio;
            tel = objT.getTelefono();
            txtTelefono.setText(String.valueOf(tel));
        }

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_SESION.getNombre()!=null){
                    processPayment();
                }else{
                    Intent intent=new Intent(info_tour.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_DIAL,
                 Uri.fromParts("tel",tel,null));
                startActivity(intent1);
            }
        });
    }

    private void ConsultarItemtour() {
        Flag = 1;
        progeso=new ProgressDialog(this);
        progeso.setMessage("Cargando...");
        progeso.show();
        String url=URL_CONSULTA_ITEM+"idTour="+objT.getIdTour();
        URL_CONSULTA_ITEM.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null,this,  this);
        requestQueue.add(jsonObjectRequest);
    }
    public void AgregarDetalle(){
        Flag = 2;
        /*progreso=new ProgressDialog(this);
        progreso.setMessage("Enviando datos de compra");
        progreso.show();*/
        String url=URL_ADD_DETALLE+"idTour="+objT.getIdTour()+"&idUsuario="+_SESION.getIdUsuario()+"&Total="+ objT.getTotal();
        URL_ADD_DETALLE.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        requestQueue.add(jsonObjectRequest);
    }


    private void AsignacionValores(String nom, String deta, String Lug, String Fech,String Hora,String Precio,String Telefono, String nomAg, String imgI){
        this.txtNombre.setText(nom);
        this.txtDetalle.setText(deta);
        this.txtLugar.setText(Lug);
        this.txtFecha.setText(Fech);
        this.txtHora.setText(Hora);
        this.txtPrecio.setText(Precio);
        this.txtTelefono.setText(Telefono);
        this.txtAgencia.setText(nomAg);
        Picasso.with(this).load(imgI).into(imgInfo);

    }

    @Override
    public void onResponse(JSONObject response) {
        if(Flag==1){
        progeso.dismiss();
        JSONArray json=response.optJSONArray("TourConsultaInfo");
        JSONObject jsonObject=null;

        try{
            for(int i=0; i<json.length();i++){
                jsonObject=json.getJSONObject(i);
            }
            if(jsonObject.getString("respuesta").equals("Ok")){
                objT.setNombre(jsonObject.getString("NombreTour"));
                objT.setDetalle(jsonObject.getString("Detalle"));
                objT.setLugarSalida(jsonObject.getString("LugarSalida"));
                objT.setFecha(jsonObject.getString("Fecha"));
                objT.setHora(jsonObject.getString("Hora"));
                objT.setPrecio(jsonObject.getString("Precio"));
                objT.setIdAgencia(jsonObject.getInt("idAgencia"));
                objT.setTelefono(jsonObject.getString("Telefono"));
                objT.setAgencia(jsonObject.getString("Nombre"));
                objT.setImgInfo(jsonObject.getString("imgInfo"));
                objT.setImgPerfil(jsonObject.getString("Imagen"));

            }else{
                Toast.makeText(getApplicationContext(), "Intente nuevamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        }
        if(Flag==2){
            Toast.makeText(getApplicationContext(), "Detalle agregado con exito",Toast.LENGTH_SHORT).show();
        }
    }

    private void processPayment(){

        objT.setTotal(String.valueOf(total));

        amount = String.valueOf(total);
        thingToBuy = new PayPalPayment(new BigDecimal(amount),
                "USD","Abono a realizar",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(info_tour.this, PaymentActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,thingToBuy);
        startActivityForResult(intent,REQUEST_CODE_PAYMENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirmation = data
                    .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirmation != null) {
                try {

                    System.out.println(confirmation.toJSONObject().toString(4));
                    System.out.println(confirmation.getPayment().toJSONObject()
                            .toString(4));

                    Toast.makeText(getApplicationContext(), "Orden procesada correctamente",
                            Toast.LENGTH_LONG).show();
                    AgregarDetalle();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "El pago no se realizó", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progeso.dismiss();
        Log.e("Algo fallo", error.getMessage());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
