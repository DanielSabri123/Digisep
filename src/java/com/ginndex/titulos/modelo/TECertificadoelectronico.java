package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class TECertificadoelectronico {
    
    private String ID_Certificado;
    private String Version;
    private String FolioControl;
    private String Id_Profesionista;
    private String ID_Carrera;
    private String ID_EntidadFederativa;
    private String ID_Firmante;
    private String ID_TipoCertificado;
    private String FechaExpedicion;
    private String ID_LugarExpedicion;
    private String Total;
    private String Asignadas;
    private String Promedio;
    private String estatus;
    private Alumno alumno;
    private String totalCreditos;
    private String creditosObtenidos;
    private String numeroCiclos;

    public String getID_Certificado() {
        return ID_Certificado;
    }

    public void setID_Certificado(String ID_Certificado) {
        this.ID_Certificado = ID_Certificado;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public String getFolioControl() {
        return FolioControl;
    }

    public void setFolioControl(String FolioControl) {
        this.FolioControl = FolioControl;
    }

    public String getId_Profesionista() {
        return Id_Profesionista;
    }

    public void setId_Profesionista(String Id_Profesionista) {
        this.Id_Profesionista = Id_Profesionista;
    }

    public String getID_Carrera() {
        return ID_Carrera;
    }

    public void setID_Carrera(String ID_Carrera) {
        this.ID_Carrera = ID_Carrera;
    }

    public String getID_EntidadFederativa() {
        return ID_EntidadFederativa;
    }

    public void setID_EntidadFederativa(String ID_EntidadFederativa) {
        this.ID_EntidadFederativa = ID_EntidadFederativa;
    }

    public String getID_Firmante() {
        return ID_Firmante;
    }

    public void setID_Firmante(String ID_Firmante) {
        this.ID_Firmante = ID_Firmante;
    }

    public String getID_TipoCertificado() {
        return ID_TipoCertificado;
    }

    public void setID_TipoCertificado(String ID_TipoCertificado) {
        this.ID_TipoCertificado = ID_TipoCertificado;
    }

    public String getFechaExpedicion() {
        return FechaExpedicion;
    }

    public void setFechaExpedicion(String FechaExpedicion) {
        this.FechaExpedicion = FechaExpedicion;
    }

    public String getID_LugarExpedicion() {
        return ID_LugarExpedicion;
    }

    public void setID_LugarExpedicion(String ID_LugarExpedicion) {
        this.ID_LugarExpedicion = ID_LugarExpedicion;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String Total) {
        this.Total = Total;
    }

    public String getAsignadas() {
        return Asignadas;
    }

    public void setAsignadas(String Asignadas) {
        this.Asignadas = Asignadas;
    }

    public String getPromedio() {
        return Promedio;
    }

    public void setPromedio(String Promedio) {
        this.Promedio = Promedio;
    }    

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getTotalCreditos() {
        return totalCreditos;
    }

    public void setTotalCreditos(String totalCreditos) {
        this.totalCreditos = totalCreditos;
    }

    public String getCreditosObtenidos() {
        return creditosObtenidos;
    }

    public void setCreditosObtenidos(String creditosObtenidos) {
        this.creditosObtenidos = creditosObtenidos;
    }

    public String getNumeroCiclos() {
        return numeroCiclos;
    }

    public void setNumeroCiclos(String numeroCiclos) {
        this.numeroCiclos = numeroCiclos;
    }
}