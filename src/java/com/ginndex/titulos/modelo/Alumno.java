package com.ginndex.titulos.modelo;

/**
 *
 * @author Paola Alonso
 */
public class Alumno {
    
    private String Id_Alumno;
    private String Matricula;
    private String Foto;
    private String Telefono;
    private String Fax;
    private String Tutor;
    private String Parentesco_Tutor;
    private String FechaAlta;
    private String FechaInscripcion;
    private String ID_Ciclo;
    private String ID_Carrera;
    private String ID_Curso;
    private String ID_Grupo;
    private String UltGradoGrupo;
    private String EstudiosPrevios;
    private String Notas;
    private String ID_Estatus;
    private String ID_Generacion;
    private String Turno;
    private String UltUsrModif;
    private String FechaUltModif;
    private String EscuelaOrigen;
    private String ClavePlantel;
    private String ID_Plantel;
    private String FechaInicioCarrera;
    private String FechaFinCarrera;
    private String ID_Persona;
    private Persona persona;
    private Carrera carrera;
    private Generacion generacion;

    public String getId_Alumno() {
        return Id_Alumno;
    }

    public void setId_Alumno(String Id_Alumno) {
        this.Id_Alumno = Id_Alumno;
    }

    public String getMatricula() {
        return Matricula;
    }

    public void setMatricula(String Matricula) {
        this.Matricula = Matricula;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String Foto) {
        this.Foto = Foto;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String Fax) {
        this.Fax = Fax;
    }

    public String getTutor() {
        return Tutor;
    }

    public void setTutor(String Tutor) {
        this.Tutor = Tutor;
    }

    public String getParentesco_Tutor() {
        return Parentesco_Tutor;
    }

    public void setParentesco_Tutor(String Parentesco_Tutor) {
        this.Parentesco_Tutor = Parentesco_Tutor;
    }

    public String getFechaAlta() {
        return FechaAlta;
    }

    public void setFechaAlta(String FechaAlta) {
        this.FechaAlta = FechaAlta;
    }

    public String getFechaInscripcion() {
        return FechaInscripcion;
    }

    public void setFechaInscripcion(String FechaInscripcion) {
        this.FechaInscripcion = FechaInscripcion;
    }

    public String getID_Ciclo() {
        return ID_Ciclo;
    }

    public void setID_Ciclo(String ID_Ciclo) {
        this.ID_Ciclo = ID_Ciclo;
    }

    public String getID_Carrera() {
        return ID_Carrera;
    }

    public void setID_Carrera(String ID_Carrera) {
        this.ID_Carrera = ID_Carrera;
    }

    public String getID_Curso() {
        return ID_Curso;
    }

    public void setID_Curso(String ID_Curso) {
        this.ID_Curso = ID_Curso;
    }

    public String getID_Grupo() {
        return ID_Grupo;
    }

    public void setID_Grupo(String ID_Grupo) {
        this.ID_Grupo = ID_Grupo;
    }

    public String getUltGradoGrupo() {
        return UltGradoGrupo;
    }

    public void setUltGradoGrupo(String UltGradoGrupo) {
        this.UltGradoGrupo = UltGradoGrupo;
    }

    public String getEstudiosPrevios() {
        return EstudiosPrevios;
    }

    public void setEstudiosPrevios(String EstudiosPrevios) {
        this.EstudiosPrevios = EstudiosPrevios;
    }

    public String getNotas() {
        return Notas;
    }

    public void setNotas(String Notas) {
        this.Notas = Notas;
    }

    public String getID_Estatus() {
        return ID_Estatus;
    }

    public void setID_Estatus(String ID_Estatus) {
        this.ID_Estatus = ID_Estatus;
    }

    public String getID_Generacion() {
        return ID_Generacion;
    }

    public void setID_Generacion(String ID_Generacion) {
        this.ID_Generacion = ID_Generacion;
    }

    public String getTurno() {
        return Turno;
    }

    public void setTurno(String Turno) {
        this.Turno = Turno;
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

    public String getEscuelaOrigen() {
        return EscuelaOrigen;
    }

    public void setEscuelaOrigen(String EscuelaOrigen) {
        this.EscuelaOrigen = EscuelaOrigen;
    }

    public String getClavePlantel() {
        return ClavePlantel;
    }

    public void setClavePlantel(String ClavePlantel) {
        this.ClavePlantel = ClavePlantel;
    }

    public String getID_Plantel() {
        return ID_Plantel;
    }

    public void setID_Plantel(String ID_Plantel) {
        this.ID_Plantel = ID_Plantel;
    }

    public String getFechaInicioCarrera() {
        return FechaInicioCarrera;
    }

    public void setFechaInicioCarrera(String FechaInicioCarrera) {
        this.FechaInicioCarrera = FechaInicioCarrera;
    }

    public String getFechaFinCarrera() {
        return FechaFinCarrera;
    }

    public void setFechaFinCarrera(String FechaFinCarrera) {
        this.FechaFinCarrera = FechaFinCarrera;
    }

    public String getID_Persona() {
        return ID_Persona;
    }

    public void setID_Persona(String ID_Persona) {
        this.ID_Persona = ID_Persona;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Generacion getGeneracion() {
        return generacion;
    }

    public void setGeneracion(Generacion generacion) {
        this.generacion = generacion;
    }

    
}