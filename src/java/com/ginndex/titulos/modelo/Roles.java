/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.modelo;

/**
 *
 * @author Célula de desarrollo
 */
public class Roles {

    private String Id_TipoRol;
    private String NombreTipo;
    private String Descripcion;

    public String getId_TipoRol() {
        return Id_TipoRol;
    }

    public void setId_TipoRol(String Id_TipoRol) {
        this.Id_TipoRol = Id_TipoRol;
    }

    public String getNombreTipo() {
        return NombreTipo;
    }

    public void setNombreTipo(String NombreTipo) {
        this.NombreTipo = NombreTipo;
    }
    
    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
}
