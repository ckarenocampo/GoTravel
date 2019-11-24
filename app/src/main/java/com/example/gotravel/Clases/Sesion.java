package com.example.gotravel.Clases;

import android.graphics.Bitmap;

public class Sesion  {
    private static Sesion INSTANCE = null ;

    private String idUsuario;
    private String Nombre;
    private String Correo;
    private String Clave;
    //private Bitmap foto;
    private Sesion() {};

    public static Sesion getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sesion();
        }
        return(INSTANCE);
    }

    public void CerrarSesion(){
        this.idUsuario="";
        this.Nombre="";
        this.Correo="";
        //this.foto=null;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }
}
