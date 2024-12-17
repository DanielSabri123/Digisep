package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class TETituloElectronico {

    private String ID_Titulo;
    private String Version;
    private String FolioControl;
    private String ID_Profesionista;
    private String ID_Carrera;
    private String FechaInicio;
    private String FechaTerminacion;
    private String FechaExpedicion;
    private String ID_ModalidadTitulacion;
    private String FechaExamenProfesional;
    private String FechaExtensionExamenProfesional;
    private String CumplioServicioSocial;
    private String ID_FundamentoLegalServicioSocial;
    private String ID_EntidadFederativa;
    private String InstitucionProcedencia;
    private String ID_TipoEstudioAntecedente;
    private String ID_EntidadFederativaAntecedente;
    private String FechaInicioAntecedente;
    private String FechaTerminacionAntecedente;
    private String NoCedula;
    private Alumno Alumno;
    private String Estatus;
    private TETitulosCarreras Carrera;
    private boolean EnvioSep;
    private boolean EnvioSepProductivo;
    private String numLote;
    private int estatusMet;
    private String mensajeMET;

    public int getEstatusMet() {
        return estatusMet;
    }

    public void setEstatusMet(int estatusMet) {
        this.estatusMet = estatusMet;
    }

    public String getMensajeMET() {
        return mensajeMET;
    }

    public void setMensajeMET(String mensajeMET) {
        this.mensajeMET = mensajeMET;
    }

    public String getID_Titulo() {
        return ID_Titulo;
    }

    public void setID_Titulo(String ID_Titulo) {
        this.ID_Titulo = ID_Titulo;
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

    public String getID_Profesionista() {
        return ID_Profesionista;
    }

    public void setID_Profesionista(String ID_Profesionista) {
        this.ID_Profesionista = ID_Profesionista;
    }

    public String getID_Carrera() {
        return ID_Carrera;
    }

    public void setID_Carrera(String ID_Carrera) {
        this.ID_Carrera = ID_Carrera;
    }

    public String getFechaInicio() {
        return FechaInicio;
    }

    public void setFechaInicio(String FechaInicio) {
        this.FechaInicio = FechaInicio;
    }

    public String getFechaTerminacion() {
        return FechaTerminacion;
    }

    public void setFechaTerminacion(String FechaTerminacion) {
        this.FechaTerminacion = FechaTerminacion;
    }

    public String getFechaExpedicion() {
        return FechaExpedicion;
    }

    public void setFechaExpedicion(String FechaExpedicion) {
        this.FechaExpedicion = FechaExpedicion;
    }

    public String getID_ModalidadTitulacion() {
        return ID_ModalidadTitulacion;
    }

    public void setID_ModalidadTitulacion(String ID_ModalidadTitulacion) {
        this.ID_ModalidadTitulacion = ID_ModalidadTitulacion;
    }

    public String getFechaExamenProfesional() {
        return FechaExamenProfesional;
    }

    public void setFechaExamenProfesional(String FechaExamenProfesional) {
        this.FechaExamenProfesional = FechaExamenProfesional;
    }

    public String getFechaExtensionExamenProfesional() {
        return FechaExtensionExamenProfesional;
    }

    public void setFechaExtensionExamenProfesional(String FechaExtensionExamenProfesional) {
        this.FechaExtensionExamenProfesional = FechaExtensionExamenProfesional;
    }

    public String getCumplioServicioSocial() {
        return CumplioServicioSocial;
    }

    public void setCumplioServicioSocial(String CumplioServicioSocial) {
        this.CumplioServicioSocial = CumplioServicioSocial;
    }

    public String getID_FundamentoLegalServicioSocial() {
        return ID_FundamentoLegalServicioSocial;
    }

    public void setID_FundamentoLegalServicioSocial(String ID_FundamentoLegalServicioSocial) {
        this.ID_FundamentoLegalServicioSocial = ID_FundamentoLegalServicioSocial;
    }

    public String getID_EntidadFederativa() {
        return ID_EntidadFederativa;
    }

    public void setID_EntidadFederativa(String ID_EntidadFederativa) {
        this.ID_EntidadFederativa = ID_EntidadFederativa;
    }

    public String getInstitucionProcedencia() {
        return InstitucionProcedencia;
    }

    public void setInstitucionProcedencia(String InstitucionProcedencia) {
        this.InstitucionProcedencia = InstitucionProcedencia;
    }

    public String getID_TipoEstudioAntecedente() {
        return ID_TipoEstudioAntecedente;
    }

    public void setID_TipoEstudioAntecedente(String ID_TipoEstudioAntecedente) {
        this.ID_TipoEstudioAntecedente = ID_TipoEstudioAntecedente;
    }

    public String getID_EntidadFederativaAntecedente() {
        return ID_EntidadFederativaAntecedente;
    }

    public void setID_EntidadFederativaAntecedente(String ID_EntidadFederativaAntecedente) {
        this.ID_EntidadFederativaAntecedente = ID_EntidadFederativaAntecedente;
    }

    public String getFechaInicioAntecedente() {
        return FechaInicioAntecedente;
    }

    public void setFechaInicioAntecedente(String FechaInicioAntecedente) {
        this.FechaInicioAntecedente = FechaInicioAntecedente;
    }

    public String getFechaTerminacionAntecedente() {
        return FechaTerminacionAntecedente;
    }

    public void setFechaTerminacionAntecedente(String FechaTerminacionAntecedente) {
        this.FechaTerminacionAntecedente = FechaTerminacionAntecedente;
    }

    public String getNoCedula() {
        return NoCedula;
    }

    public void setNoCedula(String NoCedula) {
        this.NoCedula = NoCedula;
    }

    public Alumno getAlumno() {
        return Alumno;
    }

    public void setAlumno(Alumno Alumno) {
        this.Alumno = Alumno;
    }

    public String getEstatus() {
        return Estatus;
    }

    public void setEstatus(String Estatus) {
        this.Estatus = Estatus;
    }

    public TETitulosCarreras getCarrera() {
        return Carrera;
    }

    public void setCarrera(TETitulosCarreras Carrera) {
        this.Carrera = Carrera;
    }

    public boolean isEnvioSep() {
        return EnvioSep;
    }

    public void setEnvioSep(boolean EnvioSep) {
        this.EnvioSep = EnvioSep;
    }

    public boolean isEnvioSepProductivo() {
        return EnvioSepProductivo;
    }

    public void setEnvioSepProductivo(boolean EnvioSepProductivo) {
        this.EnvioSepProductivo = EnvioSepProductivo;
    }

    public String getNumLote() {
        return numLote;
    }

    public void setNumLote(String numLote) {
        this.numLote = numLote;
    }
}
