/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.modelo;

/**
 *
 * @author Usuario
 */
public class CorreoInstitucional {

    private int id_Correo;
    private String correo;
    private String puerto;
    private String host;
    private String contrasena;
    private String SSL;

    public int getId_Correo() {
        return id_Correo;
    }

    public void setId_Correo(int id_Correo) {
        this.id_Correo = id_Correo;
    }


    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getSSL() {
        return SSL;
    }

    public void setSSL(String SSL) {
        this.SSL = SSL;
    }
    
    

}
