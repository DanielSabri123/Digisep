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
public class Preferencias_Usuario {

    private int Id_Usuario_Preferencias;
    private int Id_Usuario;
    private String Tema;
    private int Zoom;
    private String foto;
    private String attMenu;

    public String getAttMenu() {
        return attMenu;
    }

    public void setAttMenu(String attMenu) {
        this.attMenu = attMenu;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getZoom() {
        return Zoom;
    }

    public void setZoom(int Zoom) {
        this.Zoom = Zoom;
    }

    public int getId_Usuario_Preferencias() {
        return Id_Usuario_Preferencias;
    }

    public void setId_Usuario_Preferencias(int Id_Usuario_Preferencias) {
        this.Id_Usuario_Preferencias = Id_Usuario_Preferencias;
    }

    public int getId_Usuario() {
        return Id_Usuario;
    }

    public void setId_Usuario(int Id_Usuario) {
        this.Id_Usuario = Id_Usuario;
    }

    public String getTema() {
        return Tema;
    }

    public void setTema(String Tema) {
        this.Tema = Tema;
    }

}
