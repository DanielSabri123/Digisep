package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class TEObservaciones {
    
    private String ID_Observacion;
    private String Descripcion;
    private String DescripcionCorta;

    public String getID_Observacion() {
        return ID_Observacion;
    }

    public void setID_Observacion(String ID_Observacion) {
        this.ID_Observacion = ID_Observacion;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getDescripcionCorta() {
        return DescripcionCorta;
    }

    public void setDescripcionCorta(String DescripcionCorta) {
        this.DescripcionCorta = DescripcionCorta;
    }

}