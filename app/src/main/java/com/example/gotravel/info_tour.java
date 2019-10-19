package com.example.gotravel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gotravel.Clases.Tours;
import com.example.gotravel.Config.Config;
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
    //Variables
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    private TextView txtNombre,txtAgencia,txtDetalle,txtLugar,txtFecha,txtHora,txtPrecio;
    private ImageView imgInfo, imgPerfil;
    ProgressDialog progeso;
    Button btnPagar;
    Tours objT;

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
        txtNombre = findViewById(R.id.txtNombre);
        txtAgencia = findViewById(R.id.txtAgencia);
        txtDetalle = findViewById(R.id.txtDetalle);
        txtLugar = findViewById(R.id.txtLugar);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        txtPrecio = findViewById(R.id.txtPrecio);
        btnPagar = findViewById(R.id.btnPagar);
        imgInfo = findViewById(R.id.imgInfo);
        imgPerfil = findViewById(R.id.imgPerfil);

        //empezar el servicio de paypal
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Iniciamos la solicitud
        requestQueue= Volley.newRequestQueue(this);

        //Obtenemos el objeto de la consulta a Tours
        if(getIntent().getSerializableExtra("objeto")!=null){
            objT= (Tours) getIntent().getSerializableExtra("objeto");
            int nu=objT.getIdTour();
            objT.setIdTour(nu);
           // Toast.makeText(this, "ID TOUR"+nu, Toast.LENGTH_SHORT).show();

            ConsultarItemtour();
            AsignacionValores(objT.getNombre(),objT.getDetalle(),objT.getLugarSalida(),objT.getFecha(),objT.getHora(),objT.getPrecio(),objT.getAgencia(),objT.getImgInfo());
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
        URL_CONSULTA_ITEM.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null,this,  this);
        requestQueue.add(jsonObjectRequest);
    }

    private void AsignacionValores(String nom, String deta, String Lug, String Fech,String Hora,String Precio, String nomAg, String imgI){
        this.txtNombre.setText(nom);
        this.txtDetalle.setText(deta);
        this.txtLugar.setText(Lug);
        this.txtFecha.setText(Fech);
        this.txtHora.setText(Hora);
        this.txtPrecio.setText(Precio);
        this.txtAgencia.setText(nomAg);
        Picasso.with(this).load(imgI).into(imgInfo);
    }

    @Override
    public void onResponse(JSONObject response) {
        progeso.dismiss();
        JSONArray json=response.optJSONArray("TourConsultaInfo");
        JSONObject jsonObject=null;

        try{
            for(int i=0; i<json.length();i++){
                jsonObject=json.getJSONObject(i);
            }
            if(jsonObject.getString("respuesta").equals("Ok")){
                objT.setNombre(jsonObject.getString("Nombre"));
                objT.setDetalle(jsonObject.getString("Detalle"));
                objT.setLugarSalida(jsonObject.getString("LugarSalida"));
                objT.setFecha(jsonObject.getString("Fecha"));
                objT.setHora(jsonObject.getString("Hora"));
                objT.setPrecio(jsonObject.getString("Precio"));
                objT.setAgencia(jsonObject.getString("Agencia"));
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
      //  intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        //intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
        //startActivityForResult(intent,PAYPAL_REQUEST_CODE);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PagosActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalido", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progeso.dismiss();
        Log.e("Algo fallo", error.getMessage());
        finish();
    }
}
