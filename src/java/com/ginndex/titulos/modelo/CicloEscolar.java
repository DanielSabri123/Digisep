package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class CicloEscolar {
    
    private String ID_Ciclo;
    private String Clave;
    private String Descripcion;
    private String FechaInicio;
    private String FechaFin;
    private String ID_Plan;
    private String UltUsrModif;
    private String FechaUltModif;

    public String getID_Ciclo() {
        return ID_Ciclo;
    }

    public void setID_Ciclo(String ID_Ciclo) {
        this.ID_Ciclo = ID_Ciclo;
    }

    public String getClave() {
        return Clave;
    }

    public void setClave(String Clave) {
        this.Clave = Clave;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getFechaInicio() {
        return FechaInicio;
    }

    public void setFechaInicio(String FechaInicio) {
        this.FechaInicio = FechaInicio;
    }

    public String getFechaFin() {
        return FechaFin;
    }

    public void setFechaFin(String FechaFin) {
        this.FechaFin = FechaFin;
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