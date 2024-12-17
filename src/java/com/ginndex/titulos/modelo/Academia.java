/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class Academia {
    
    private String ID_Academia;
    private String Descripcion;
    private String ID_Plan;
    private String UltUsrModif;
    private String FechaUltModif;

    public String getID_Academia() {
        return ID_Academia;
    }

    public void setID_Academia(String ID_Academia) {
        this.ID_Academia = ID_Academia;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getID_Plan() {
        return ID_Plan;
    }

    public void setID_Plan(String ID_Plan) {
        this.ID_Plan = ID_Plan;
    }

    public String getUltUsrModif() {
        return UltUsrModif;
    }

    public void setUltUsrModif(String UltUsrModif) {
        this.UltUsrModif = UltUsrModif;
    }

    public String getFechaUltModif() {
        return FechaUltModif;
    }

    public void setFechaUltModif(String FechaUltModif) {
        this.FechaUltModif = FechaUltModif;
    }

}