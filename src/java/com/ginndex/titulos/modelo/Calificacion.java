package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class Calificacion {
    
    private String ID_Grupo;
    private String ID_Alumno;
    private String ID_Materia;
    private String ID_Ciclo;
    private String Periodo;
    private String Calificacion;
    private String Faltas;
    private String CalAsentada;
    private String UltUsrModificacion;
    private String FechaUltModif;
    private String Observaciones;
    private Materia materia;

    public String getID_Grupo() {
        return ID_Grupo;
    }

    public void setID_Grupo(String ID_Grupo) {
        this.ID_Grupo = ID_Grupo;
    }

    public String getID_Alumno() {
        return ID_Alumno;
    }

    public void setID_Alumno(String ID_Alumno) {
        this.ID_Alumno = ID_Alumno;
    }

    public String getID_Materia() {
        return ID_Materia;
    }

    public void setID_Materia(String ID_Materia) {
        this.ID_Materia = ID_Materia;
    }

    public String getID_Ciclo() {
        return ID_Ciclo;
    }

    public void setID_Ciclo(String ID_Ciclo) {
        this.ID_Ciclo = ID_Ciclo;
    }

    public String getPeriodo() {
        return Periodo;
    }

    public void setPeriodo(String Periodo) {
        this.Periodo = Periodo;
    }

    public String getCalificacion() {
        return Calificacion;
    }

    public void setCalificacion(String Calificacion) {
        this.Calificacion = Calificacion;
    }

    public String getFaltas() {
        return Faltas;
    }

    public void setFaltas(String Faltas) {
        this.Faltas = Faltas;
    }

    public String getCalAsentada() {
        return CalAsentada;
    }

    public void setCalAsentada(String CalAsentada) {
        this.CalAsentada = CalAsentada;
    }

    public String getUltUsrModificacion() {
        return UltUsrModificacion;
    }

    public void setUltUsrModificacion(String UltUsrModificacion) {
        this.UltUsrModificacion = UltUsrModificacion;
    }

    public String getFechaUltModif() {
        return FechaUltModif;
    }

    public void setFechaUltModif(String FechaUltModif) {
        this.FechaUltModif = FechaUltModif;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String Observaciones) {
        this.Observaciones = Observaciones;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

}