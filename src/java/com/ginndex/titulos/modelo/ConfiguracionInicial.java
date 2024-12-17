/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.modelo;

import java.util.Date;

/**
 *
 * @author Paola Alonso
 * @since 23/01/2019
 */
public class ConfiguracionInicial {

    private String ID_ConfiguracionInicial;
    private String nombreInstitucion;
    private String prefijoFolioTitulo;
    private int complementoFolioTitulo;
    private int longitudFolioTitulo;
    private String prefijoFolioCertificado;
    private int complementoFolioCertificado;
    private int longitudFolioCertificado;
    private float califMinima;
    private float califMaxima;
    private float califMinAprobatoria;
    private String claveInstitucional;
    private String nombreOficial;
    private String logo;
    private String firmaImagen;
    private String usuarioSEP;
    private String passwordSEP;
    private String usuarioSepProductivo;
    private String passwordSepProductivo;

    private Date fechaInicioVigencia;
    private Date fechaFinVigencia;

    public Date getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(Date fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public Date getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(Date fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }
    
    public String getID_ConfiguracionInicial() {
        return ID_ConfiguracionInicial;
    }

    public void setID_ConfiguracionInicial(String ID_ConfiguracionInicial) {
        this.ID_ConfiguracionInicial = ID_ConfiguracionInicial;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getPrefijoFolioTitulo() {
        return prefijoFolioTitulo;
    }

    public void setPrefijoFolioTitulo(String prefijoFolioTitulo) {
        this.prefijoFolioTitulo = prefijoFolioTitulo;
    }

    public int getComplementoFolioTitulo() {
        return complementoFolioTitulo;
    }

    public void setComplementoFolioTitulo(int complementoFolioTitulo) {
        this.complementoFolioTitulo = complementoFolioTitulo;
    }

    public int getLongitudFolioTitulo() {
        return longitudFolioTitulo;
    }

    public void setLongitudFolioTItulo(int longitudFolioTitulo) {
        this.longitudFolioTitulo = longitudFolioTitulo;
    }

    public String getPrefijoFolioCertificado() {
        return prefijoFolioCertificado;
    }

    public void setPrefijoFolioCertificado(String prefijoFolioCertificado) {
        this.prefijoFolioCertificado = prefijoFolioCertificado;
    }

    public int getComplementoFolioCertificado() {
        return complementoFolioCertificado;
    }

    public void setComplementoFolioCertificado(int complementoFolioCertificado) {
        this.complementoFolioCertificado = complementoFolioCertificado;
    }

    public int getLongitudFolioCertificado() {
        return longitudFolioCertificado;
    }

    public void setLongitudFolioCertificado(int longitudFolioCertificado) {
        this.longitudFolioCertificado = longitudFolioCertificado;
    }

    public float getCalifMinAprobatoria() {
        return califMinAprobatoria;
    }

    public void setCalifMinAprobatoria(float califMinAprobatoria) {
        this.califMinAprobatoria = califMinAprobatoria;
    }

    public String getClaveInstitucional() {
        return claveInstitucional;
    }

    public void setClaveInstitucional(String claveInstitucional) {
        this.claveInstitucional = claveInstitucional;
    }

    public String getNombreOficial() {
        return nombreOficial;
    }

    public void setNombreOficial(String nombreOficial) {
        this.nombreOficial = nombreOficial;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public float getCalifMinima() {
        return califMinima;
    }

    public void setCalifMinima(float califMinima) {
        this.califMinima = califMinima;
    }

    public float getCalifMaxima() {
        return califMaxima;
    }

    public void setCalifMaxima(float califMaxima) {
        this.califMaxima = califMaxima;
    }

    public String getUsuarioSEP() {
        return usuarioSEP;
    }

    public void setUsuarioSEP(String usuarioSEP) {
        this.usuarioSEP = usuarioSEP;
    }

    public String getPasswordSEP() {
        return passwordSEP;
    }

    public void setPasswordSEP(String passwordSEP) {
        this.passwordSEP = passwordSEP;
    }

    public String getUsuarioSepProductivo() {
        return usuarioSepProductivo;
    }

    public void setUsuarioSepProductivo(String usuarioSepProductivo) {
        this.usuarioSepProductivo = usuarioSepProductivo;
    }

    public String getPasswordSepProductivo() {
        return passwordSepProductivo;
    }

    public void setPasswordSepProductivo(String passwordSepProductivo) {
        this.passwordSepProductivo = passwordSepProductivo;
    }

    public String getFirmaImagen() {
        return firmaImagen;
    }

    public void setFirmaImagen(String firmaImagen) {
        this.firmaImagen = firmaImagen;
    }

    
}
