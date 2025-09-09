/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.modelo;

/**
 *
 * @author Tu papi rey
 */
public class CalificacionClickEscolar {
    private String CveMateria;
    private String NombreMateria;
    private String IdObservacion;
    private String Matricula;
    private String NombreAlumno;
    private String CicloEscolar;
    private String IdCarrera;
    private String Calificacion;
    private String FolioOrdenamiento;

    public String getNombreMateria() {
        return NombreMateria;
    }

    public void setNombreMateria(String NombreMateria) {
        this.NombreMateria = NombreMateria;
    }

    public String getNombreAlumno() {
        return NombreAlumno;
    }

    public void setNombreAlumno(String NombreAlumno) {
        this.NombreAlumno = NombreAlumno;
    }

    
    public String getCveMateria() {
        return CveMateria;
    }

    public void setCveMateria(String CveMateria) {
        this.CveMateria = CveMateria;
    }

    public String getIdObservacion() {
        return IdObservacion;
    }

    public void setIdObservacion(String IdObservacion) {
        this.IdObservacion = IdObservacion;
    }

    public String getMatricula() {
        return Matricula;
    }

    public void setMatricula(String Matricula) {
        this.Matricula = Matricula;
    }

    public String getCicloEscolar() {
        return CicloEscolar;
    }

    public void setCicloEscolar(String CicloEscolar) {
        this.CicloEscolar = CicloEscolar;
    }

    public String getIdCarrera() {
        return IdCarrera;
    }

    public void setIdCarrera(String IdCarrera) {
        this.IdCarrera = IdCarrera;
    }

    public String getCalificacion() {
        return Calificacion;
    }

    public void setCalificacion(String Calificacion) {
        this.Calificacion = Calificacion;
    }

    public String getFolioOrdenamiento() {
        return FolioOrdenamiento;
    }

    public void setFolioOrdenamiento(String FolioOrdenamiento) {
        this.FolioOrdenamiento = FolioOrdenamiento;
    }
}
