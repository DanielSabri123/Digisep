package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class ClaveActivacion {
    private int ID_ClaveActivacion;
    private String clave;
    private String tmp;
    private int ID_ConfiguracionInicial;

    public int getID_ClaveActivacion() {
        return ID_ClaveActivacion;
    }

    public void setID_ClaveActivacion(int ID_ClaveActivacion) {
        this.ID_ClaveActivacion = ID_ClaveActivacion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public int getID_ConfiguracionInicial() {
        return ID_ConfiguracionInicial;
    }

    public void setID_ConfiguracionInicial(int ID_ConfiguracionInicial) {
        this.ID_ConfiguracionInicial = ID_ConfiguracionInicial;
    }
    
    
}
