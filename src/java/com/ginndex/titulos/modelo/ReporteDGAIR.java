/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.modelo;

/**
 *
 * @author BSorcia
 */
public class ReporteDGAIR {
    private String estatus;
    private String anioCicloEscolar;
    private String curp;
    private String promedioGeneral;
    private String tipoDocumento;
    private String fechaExpedicionDocumento;
    private String folioDocumento;

    public ReporteDGAIR() {
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getAnioCicloEscolar() {
        return anioCicloEscolar;
    }

    public void setAnioCicloEscolar(String anioCicloEscolar) {
        this.anioCicloEscolar = anioCicloEscolar;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(String promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFechaExpedicionDocumento() {
        return fechaExpedicionDocumento;
    }

    public void setFechaExpedicionDocumento(String fechaExpedicionDocumento) {
        this.fechaExpedicionDocumento = fechaExpedicionDocumento;
    }

    public String getFolioDocumento() {
        return folioDocumento;
    }

    public void setFolioDocumento(String folioDocumento) {
        this.folioDocumento = folioDocumento;
    }
    
}
