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
public class Curso {
    
    private String ID_Curso;
    private String Descripcion;
    private String ID_Carrera;
    private String UltUsrModif;
    private String FechaUltModif;

    public String getID_Curso() {
        return ID_Curso;
    }

    public void setID_Curso(String ID_Curso) {
        this.ID_Curso = ID_Curso;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getID_Carrera() {
        return ID_Carrera;
    }

    public void setID_Carrera(String ID_Carrera) {
        this.ID_Carrera = ID_Carrera;
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