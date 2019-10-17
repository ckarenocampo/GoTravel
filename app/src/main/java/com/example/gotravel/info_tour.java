package com.example.gotravel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gotravel.Clases.Tours;
import com.example.gotravel.Config.Config;
import com.example.gotravel.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import javax.xml.transform.ErrorListener;


public class info_tour extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    //Variables
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    private TextView txtNombretour,txtNombreAgencia,txtDetalle,txtLugar,txtFecha,txtHora,txtPrecio;
    private ImageView imgInfo, imgPerfil;
    ProgressDialog progeso;
    Button btnPagar;
    Tours objT;
    Context a ;
    String amount= "10";

    //URLS de consulta a la bd en webservice
    private final String URL_CONSULTA_ITEM="https://gotravel.webcindario.com/modelos/TourConsultarInfo.php?";

    // Variables de pago
    private static final  int PAYPAL_REQUEST_CODE= 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)// sandox para prueba
            .clientId(Config.PAYPAL_CLIENT_ID);


    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tour);
        txtNombretour = findViewById(R.id.txtTourname);
        txtNombreAgencia = findViewById(R.id.txtNombreAgencia);
        txtDetalle = findViewById(R.id.txtDetalle);
        txtLugar = findViewById(R.id.txtLugar);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        txtPrecio = findViewById(R.id.txtPrecio);
        btnPagar = findViewById(R.id.btnPagar);
        imgInfo = findViewById(R.id.imgInfo);
        imgPerfil = findViewById(R.id.imgProfile);

        //empezar el servicio de paypal
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Iniciamos la solicitud
        requestQueue= Volley.newRequestQueue(this);

        //Obtenemos el objeto de la consulta a Tours
        if(getIntent().getSerializableExtra("objeto")!=null){
            objT= (Tours) getIntent().getSerializableExtra("objeto");
            AsignacionValores(objT.getNombretour(),objT.getAgencia(),objT.getDetalle(),objT.getLugarSalida(),objT.getFecha(),objT.getHora(),objT.getPrecio(),objT.getImgInfo(),objT.getImgPerfil());
            ConsultarItemtour();
        }

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });
    }

    private void ConsultarItemtour() {
        progeso=new ProgressDialog(this);
        progeso.setMessage("Cargando...");
        progeso.show();
        String url=URL_CONSULTA_ITEM+"idTour="+objT.getIdTour();
        url.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null,this,  this);
        requestQueue.add(jsonObjectRequest);
    }

    private void AsignacionValores(String nom, String nomAg, String deta, String Lug, String Fech,String Hora,String Precio, String imgI, String imgP){
        this.txtNombretour.setText(nom);
        this.txtNombreAgencia.setText(nomAg);
        this.txtDetalle.setText(deta);
        this.txtLugar.setText(Lug);
        this.txtFecha.setText(Fech);
        this.txtHora.setText(Hora);
        this.txtPrecio.setText(Precio);
        Picasso.with(a).load(imgI).into(imgInfo);
        Picasso.with(a).load(imgP).into(imgPerfil);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(nomAg);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        progeso.dismiss();
        JSONArray json=response.optJSONArray("Item");
        JSONObject jsonObject=null;
        try{
            for(int i=0; i<json.length();i++){
                jsonObject=json.getJSONObject(i);
            }
            if(jsonObject.getString("respuesta").equals("Ok")){
                AsignacionValores(jsonObject.getString("Nombre"),jsonObject.getString("Detalle"),
                        jsonObject.getString("LugarSalida"),jsonObject.getString("Fecha"),
                        jsonObject.getString("Hora"), jsonObject.getString("Precio"),
                        jsonObject.getString("idAgencia"),jsonObject.getString("imgInfo"), jsonObject.getString("imgPerfil") );
                objT.setNombretour(jsonObject.getString("Nombre"));
                objT.setAgencia(jsonObject.getString("idAgencia"));
                objT.setDetalle(jsonObject.getString("Detalle"));
                objT.setLugarSalida(jsonObject.getString("LugarSalida"));
                objT.setFecha(jsonObject.getString("Fecha"));
                objT.setHora(jsonObject.getString("Hora"));
                objT.setPrecio(jsonObject.getString("Precio"));
                objT.setImgInfo(jsonObject.getString("imgInfo"));
                objT.setImgPerfil(jsonObject.getString("imgPerfil"));


            }else{
                Toast.makeText(getApplicationContext(), "Intente nuevamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processPayment() {
        amount = txtPrecio.getText().toString();
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount),
                "$USD","Abono realizado",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
        startActivityForResult(intent,0);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PagosActivity. class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "Invalido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progeso.dismiss();
        Log.e("Algo fallo", error.getMessage());
        finish();
    }
}
