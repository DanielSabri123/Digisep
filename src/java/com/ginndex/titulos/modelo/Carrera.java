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
public class Carrera {

    private String ID_Carrera;
    private String Id_Carrera_Excel;
    private String Descripcion;
    private String ID_Academia;
    private String Clave;
    private String TotalCreditos;
    private String TotalMaterias;
    private String Area;
    private String UltUsrModif;
    private String FechaUltModif;
    private String CvePlanCarrera;
    private String CalMin;
    private String CalMax;
    private String CalMinAprobatoria;

    public String getID_Carrera() {
        return ID_Carrera;
    }

    public void setID_Carrera(String ID_Carrera) {
        this.ID_Carrera = ID_Carrera;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getID_Academia() {
        return ID_Academia;
    }

    public void setID_Academia(String ID_Academia) {
        this.ID_Academia = ID_Academia;
    }

    public String getClave() {
        return Clave;
    }

    public void setClave(String Clave) {
        this.Clave = Clave;
    }

    public String getTotalCreditos() {
        return TotalCreditos;
    }

    public void setTotalCreditos(String TotalCreditos) {
        this.TotalCreditos = TotalCreditos;
    }

    public String getTotalMaterias() {
        return TotalMaterias;
    }

    public void setTotalMaterias(String TotalMaterias) {
        this.TotalMaterias = TotalMaterias;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String Area) {
        this.Area = Area;
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

    public String getCvePlanCarrera() {
        return CvePlanCarrera;
    }

    public void setCvePlanCarrera(String CvePlanCarrera) {
        this.CvePlanCarrera = CvePlanCarrera;
    }

    public String getId_Carrera_Excel() {
        return Id_Carrera_Excel;
    }

    public void setId_Carrera_Excel(String Id_Carrera_Excel) {
        this.Id_Carrera_Excel = Id_Carrera_Excel;
    }

    public String getCalMin() {
        return CalMin;
    }

    public void setCalMin(String CalMin) {
        this.CalMin = CalMin;
    }

    public String getCalMax() {
        return CalMax;
    }

    public void setCalMax(String CalMax) {
        this.CalMax = CalMax;
    }

    public String getCalMinAprobatoria() {
        return CalMinAprobatoria;
    }

    public void setCalMinAprobatoria(String CalMinAprobatoria) {
        this.CalMinAprobatoria = CalMinAprobatoria;
    }
}
