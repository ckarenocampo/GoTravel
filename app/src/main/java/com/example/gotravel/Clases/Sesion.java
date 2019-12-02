package com.example.gotravel.Clases;

import android.graphics.Bitmap;

public class Sesion  {
    private static Sesion INSTANCE = null ;

    private int idUsuario;
    private String Nombre;
    private String Correo;
    private Sesion() {};

    public static Sesion getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sesion();
        }
        return(INSTANCE);
    }

    public void CerrarSesion(){
        this.idUsuario=0;
        this.Nombre=null;
        this.Correo=null;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
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
