/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.api;

import com.ginndex.titulos.control.CAlumnos;
import com.ginndex.titulos.control.CBitacora;
import com.ginndex.titulos.control.CCarrerasCarga;
import com.ginndex.titulos.control.CConexion;
import com.ginndex.titulos.control.CTitulos;
import com.ginndex.titulos.modelo.AlumnoClickEscolar;
import com.ginndex.titulos.modelo.CalificacionClickEscolar;
import com.ginndex.titulos.modelo.CarreraClickEscolar;
import com.ginndex.titulos.modelo.MateriaClickEscolar;
import com.ginndex.titulos.util.Encriptacion;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ClickEscolarHandlerService {
    
    private HttpServletRequest request;
    private String bandera;
    private String RESP;
    private ClickEscolarService servicio;
    private JsonArray respuestaServicio;
    CConexion conexion;
    String Id_Usuario;
    String NombreInstitucion;
    String nombreLogo;
    String nombreFirma;
    
    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    public String EstablecerAccionesClickEscolarHandlerService() throws SQLException, IOException{
        HttpSession sessionOk = request.getSession();
        bandera = request.getParameter("txtBandera");
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        conexion = new CConexion();
        conexion.setRequest(request);
        
        servicio = new ClickEscolarService();
        servicio.setRequest(request);
        
        llenarNombreInstitucion();
        
        switch(bandera){
            case "realizarTestConexion":
                RESP = realizarTestConexion();
                break;
            case "descargarCarrerasClickEscolar":
                RESP = descargarCarrerasClickEscolar();
                break;
            case "descargarAsignaturasClickEscolar":
                RESP = "";
                break;
            case "descargarAlumnosClickEscolar":
                RESP = descargarAlumnosClickEscolar();
                break;
            case "descargarCalificacionesClickEscolar":
                RESP = descargarCalificacionesClickEscolar();
                break;
            case "guardarCarrerasClickEscolar":
                RESP = guardarCarrerasClickEscolar();
                break;
            case "guardarAsignaturasClickEscolar":
                RESP = "";
                break;
            case "guardarAlumnosClickEscolar":
                RESP = guardarAlumnosClickEscolar();
                break;
            case "guardarCalificacionesClickEscolar":
                RESP = guardarCalificacionesClickEscolar();
                break;    
        }
        
        return RESP;
    }
    
    public String realizarTestConexion(){
        try {
            String clave = request.getParameter("clave");
            String usuario = request.getParameter("usuario");
            String contrasena = request.getParameter("contrasena");
            
            respuestaServicio = servicio.servicioRealizarTestConexion(clave, usuario, contrasena);

            JsonObject respuesta = respuestaServicio.get(0).getAsJsonObject();

            if (respuesta.has("error") && respuesta.get("error").getAsBoolean()) {
                int status = respuesta.has("status") ? respuesta.get("status").getAsInt() : 500;
                String message = respuesta.has("message") ? respuesta.get("message").getAsString() : "Error desconocido";

                return "error|" + message;
            } else {
                String nombreBD = respuesta.get("BD").toString();
                return "success|La prueba de conexión se realizó correctamente.|" + nombreBD.substring(1, nombreBD.length() - 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error|Excepción al procesar la prueba de conexión: " + ex.getMessage();
        }
    }
    
    public String descargarCarrerasClickEscolar(){
        try {
            String idCarrera = request.getParameter("idCarrera");
            Boolean descargarTodo = Boolean.parseBoolean(request.getParameter("descargarTodo"));
            
            if(descargarTodo){
                idCarrera = "";
            }
            
            respuestaServicio = servicio.servicioDescargarCarreras(idCarrera);

            JsonObject respuesta = respuestaServicio.get(0).getAsJsonObject();

            if (respuesta.has("error") && respuesta.get("error").getAsBoolean()) {
                int status = respuesta.has("status") ? respuesta.get("status").getAsInt() : 500;
                String message = respuesta.has("message") ? respuesta.get("message").getAsString() : "Error desconocido";

                return "error|" + message;
            } else if (respuesta.has("info") && respuesta.get("info").getAsBoolean()) {
                int status = respuesta.has("status") ? respuesta.get("status").getAsInt() : 500;
                String message = respuesta.has("message") ? respuesta.get("message").getAsString() : "Error desconocido";

                return "info|" + message;
            } else {
                //return "success|Funcionó todo a la perfeccion jaja";
                String tablaCarreras = armarTablaHtmlCarrerasConMaterias(respuestaServicio);
                
                return "success|" + tablaCarreras;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error|Excepción al procesar la prueba de conexión: " + ex.getMessage();
        }
    }
    
    public String guardarCarrerasClickEscolar() throws SQLException{
        //Variables para la conexion
        Connection con = null;
        CallableStatement cstmt = null;
        con = conexion.GetconexionInSite();
        
        ResultSet rs;
        String errorSP = "";
        
        String jsonString = request.getParameter("json");
        Gson gson = new Gson();
        
        try{
            List<CarreraClickEscolar> filas = gson.fromJson(jsonString, new TypeToken<List<CarreraClickEscolar>>() {}.getType());
            for (CarreraClickEscolar fila : filas) {
                String idCurso = "";
                String QueryCarrera = "{call Add_Carrera_ClickEscolar (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                cstmt = con.prepareCall(QueryCarrera);
                cstmt.setString(1, fila.getId_Carrera_Excel());
                cstmt.setString(2, fila.getCveCarrera());
                cstmt.setString(3, fila.getNombreCarrera());
                cstmt.setString(4, fila.getCveInstitucion());
                cstmt.setString(5, fila.getNombreInstitucion());
                cstmt.setString(6, fila.getCveCampus());
                cstmt.setString(7, fila.getCampus());
                cstmt.setString(8, fila.getAutorizacionReconocimiento());
                cstmt.setString(9, fila.getNumeroRvoe());
                cstmt.setString(10, fila.getTipoPeriodo());
                cstmt.setString(11, fila.getNivelEducativo());
                cstmt.setInt(12, Integer.valueOf(fila.getTotalMaterias()));
                cstmt.setString(13, fila.getEntidadFederativa());
                cstmt.setString(14, fila.getFechaExpedicionRvoe());
                cstmt.setFloat(15, Float.valueOf(fila.getCalifMinima()));
                cstmt.setFloat(16, Float.valueOf(fila.getCalifMaxima()));
                cstmt.setFloat(17, Float.valueOf(fila.getCalifMinimaAprobatoria()));
                cstmt.setString(18, fila.getCvePlan());
                cstmt.setFloat(19, Float.valueOf(fila.getCreditos()));
                cstmt.registerOutParameter(20, java.sql.Types.VARCHAR);
                cstmt.execute();
                
                if ((cstmt.getUpdateCount() == -1) && (cstmt.getResultSet() == null)) {

                    idCurso = cstmt.getString(20);
                    // Se insertar las materias capturadas en el documento
                    for(MateriaClickEscolar materia : fila.getMaterias()){
                        String QueryMateria = "{call Add_Materia_ClickEscolar (?,?,?,?,?,?)}";
                        rs = null;
                        cstmt = con.prepareCall(QueryMateria);
                        cstmt.setInt(1, Integer.parseInt(idCurso));
                        cstmt.setInt(2, Integer.parseInt(materia.getFolioOrdenamiento()));
                        cstmt.setString(3, materia.getClaveSep());
                        cstmt.setString(4, materia.getNombreMat());
                        cstmt.setFloat(5, Float.valueOf(materia.getCreditoMateria()));
                        cstmt.setInt(6, Integer.parseInt(materia.getIdTipoAsignatura()));
                        cstmt.executeUpdate();
                        if (cstmt.getUpdateCount() == -1 && cstmt.getResultSet() == null) {
                            RESP = "success";
                        } else {
                            rs = cstmt.getResultSet();
                            RESP = "error"
                                    + "<p>Error al insertar la materia: <b>" + materia.getNombreMat() + "</b>.</p><br>"
                                    + "<small>El servidor devolvió el siguiente mensaje de error desde SQL: <b>" + rs.getString("ErrorMessage") + "</b></small>"
                                    + "<br><small>Los registros posteriores no fueron insertados.</small>"
                                    + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
                            break;
                        }
                    }
                } else {
                    rs = cstmt.getResultSet();
                    rs.next();
                    Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, "Error Message: ------->{0}", rs.getString("ErrorMessage"));
                    return "error|"
                            + "<p>Error al insertar la carrera: <b>" + fila.getNombreCarrera() + "</b>.</p><br>"
                            + "<small>El servidor devolvió el siguiente mensaje de error desde SQL: <b>" + rs.getString("ErrorMessage") + "</b></small>"
                            + "<br><small>Los registros posteriores no fueron insertados.</small>"
                            + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la lectura de la informacion: " + ex.getMessage();
        } catch (Exception ex) {
            Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error inesperado al cargar la informacion: " + ex.getMessage();
        }
        return RESP;
    }
    
    
    public String descargarAlumnosClickEscolar(){
        try {
            String idCarrera = request.getParameter("idCarrera");
            String matricula = request.getParameter("matricula");
            String tipoBusqueda = request.getParameter("tipoBusqueda");
            
            if(tipoBusqueda.trim().equalsIgnoreCase("individual")){
                respuestaServicio = servicio.servicioDescargarAlumnoIndividual(matricula);
            }
            if(tipoBusqueda.trim().equalsIgnoreCase("porCarrera")){
                respuestaServicio = servicio.servicioDescargarAlumnoPorCarrera(idCarrera);
            }
            
            JsonObject respuesta = respuestaServicio.get(0).getAsJsonObject();
            
            if (respuesta.has("error") && respuesta.get("error").getAsBoolean()) {
                int status = respuesta.has("status") ? respuesta.get("status").getAsInt() : 500;
                String message = respuesta.has("message") ? respuesta.get("message").getAsString() : "Error desconocido";

                return "error|" + message;
            } else if (respuesta.has("info") && respuesta.get("info").getAsBoolean()) {
                int status = respuesta.has("status") ? respuesta.get("status").getAsInt() : 500;
                String message = respuesta.has("message") ? respuesta.get("message").getAsString() : "Error desconocido";

                return "info|" + message;
            } else {
                String tablaAlumnos = armarTablaHtmlAlumnos(respuestaServicio, tipoBusqueda);
                
                return "success|" + tablaAlumnos;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error|Excepción al procesar la prueba de conexión: " + ex.getMessage();
        }
    }
       
    public String guardarAlumnosClickEscolar() throws SQLException, IOException{
        //Variables para la conexion
        Connection con = null;
        CallableStatement cstmt = null;
        con = conexion.GetconexionInSite();
        
        ResultSet rs;
        String errorSP = "";
        
        String jsonString = request.getParameter("json");
        Gson gson = new Gson();
        
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        List<String> errores = new ArrayList<>();

        try{
            List<AlumnoClickEscolar> filas = gson.fromJson(jsonString, new TypeToken<List<AlumnoClickEscolar>>() {}.getType());
            for (AlumnoClickEscolar fila : filas) {
                String Query = "{call Add_Alumno_ClickEscolar(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                cstmt = con.prepareCall(Query);
                cstmt.setString(1, fila.getMatriculaExt());
                cstmt.setString(2, fila.getNombre());
                cstmt.setString(3, fila.getAPaterno());
                cstmt.setString(4, fila.getAMaterno());
                cstmt.setString(5, fila.getCurp());
                cstmt.setString(6, fila.getGeneracion());
                //cstmt.setString(7, String.valueOf(fila.getId_Carrera_Digisep()));
                cstmt.setString(7, "101");
                cstmt.setString(8, fila.getSexo());
                cstmt.setString(9, fila.getFechaInicioCarrera());
                cstmt.setString(10, fila.getFechaFinCarrera());
                cstmt.setString(11, fila.getFechaNacimiento());
                cstmt.setString(12, fila.getEmail());
                cstmt.setString(13, fila.getFechaInicioAntecedente());
                cstmt.setString(14, fila.getFechaFinAntecedente());
                cstmt.setString(15, fila.getTipoEstudioAntecedente());
                cstmt.setString(16, fila.getEstadoAntecedente());
                cstmt.setString(17, fila.getEscuelaOrigen());
                cstmt.registerOutParameter(18, java.sql.Types.VARCHAR);

                cstmt.execute();

                if ((cstmt.getUpdateCount() == -1) && (cstmt.getResultSet() != null)) {
                    rs = cstmt.getResultSet();
                    rs.next();
                    Logger.getLogger(CAlumnos.class.getName()).log(Level.INFO, "Error Message: ------->{0}", rs.getString("ErrorMessage"));
                    return "error|"
                            + "<p>Error al insertar al alumno con la matrícula: <b>" + fila.getMatriculaExt() + "</b>.</p><br>"
                            + "<small>El servidor devolvió el siguiente mensaje de error desde SQL: <b>" + rs.getString("ErrorMessage") + "</b></small>"
                            + "<br><small>Los registros posteriores no fueron insertados.</small>"
                            + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
                } else {
                    errorSP = cstmt.getString(18);
                    
                    if (!errorSP.equalsIgnoreCase("success")) {
                        
                        if(filas.size() > 1){
                            String lineaError = String.format("%-10s %-45s %-100s",
                                    fila.getMatriculaExt(),
                                    fila.getAPaterno() + " " + fila.getAMaterno() + " " + fila.getNombre(),
                                    errorSP);
                            errores.add(lineaError);
                        }else{
                            return "error|"
                                    + "<p>No se pudo registrar al alumno <b>" + fila.getMatriculaExt() + " " + fila.getAPaterno() + " " + fila.getAMaterno() + " " + fila.getNombre() + "</b></p>"
                                    + "<small>El servidor arrojó el siguiente error:</small>"
                                    + "<br><small><b>" + errorSP + "</b></small>"
                                    + "<br><br><small>Los registros posteriores no fueron insertados.</small>"
                                    + "<br><small>Verifica la información. Si el problema persiste contacta a soporte técnico para obtener más información.</small>";
                        }
                        Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, "Error Message: ------->{0}", errorSP);
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la lectura de la información: " + ex.getMessage();
        } catch (Exception ex) {
            Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error inesperado al cargar la información: " + ex.getMessage();
        }
        
        // Si hubo errores, escribirlos a un archivo
        if (!errores.isEmpty()) {
            String rutaGuardado = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\errorresDescargaClickEscolar";
            String nombreArchivo = "errores_alumnos_clickEscolar_" + System.currentTimeMillis() + ".txt";
            String rutaCompleta = rutaGuardado + "\\" + nombreArchivo;
            String rutaDescarga = "Instituciones\\" + NombreInstitucion.trim() + "\\errorresDescargaClickEscolar\\" + nombreArchivo;
            
            try {
                // Crear carpeta si no existe
                File carpeta = new File(rutaGuardado);
                if (!carpeta.exists()) {
                    carpeta.mkdirs(); // Crea todas las carpetas necesarias
                }

                // Escribir archivo
                try (PrintWriter writer = new PrintWriter(new FileWriter(rutaCompleta))) {
                    // Título
                    writer.println("REPORTE DE ERRORES ALUMNOS DIGISEP - CLICK ESCOLAR " + formatter.format(ahora));
                    writer.println("");
                    // Cabecera
                    writer.printf("%-10s %-45s %-100s%n", "Matrícula", "Nombre Alumno", "Mensaje de Error");

                    // Errores
                    for (String err : errores) {
                        writer.println(err);
                    }
                }

            } catch (IOException e) {
                Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, e);
                return "error|Ocurrió un error al generar el archivo: " + e.getMessage();
            }
            return "errorConArchivo|Se realizo el proceso de registro, pero se detectaron algunos errores durante la carga.|" + rutaDescarga;
        }
        
        return "success|Alumnos registrados con éxito";
    }
    
    public String descargarCalificacionesClickEscolar(){
        try {
            String idCarrera = request.getParameter("idCarrera");
            String matricula = request.getParameter("matricula");
            String idAlumno = request.getParameter("idAlumno");
            String tipoBusqueda = "";
            
            if(!matricula.trim().equalsIgnoreCase("")){
                respuestaServicio = servicio.servicioDescargarCalificacionesAlumnoIndividual(matricula, idCarrera);
            }else{
                respuestaServicio = servicio.servicioDescargarCalificacionesAlumnoPorCarrera(idCarrera);
            }
            
            JsonObject respuesta = respuestaServicio.get(0).getAsJsonObject();
            
            if (respuesta.has("error") && respuesta.get("error").getAsBoolean()) {
                int status = respuesta.has("status") ? respuesta.get("status").getAsInt() : 500;
                String message = respuesta.has("message") ? respuesta.get("message").getAsString() : "Error desconocido";

                return "error|" + message;
            } else if (respuesta.has("info") && respuesta.get("info").getAsBoolean()) {
                int status = respuesta.has("status") ? respuesta.get("status").getAsInt() : 500;
                String message = respuesta.has("message") ? respuesta.get("message").getAsString() : "Error desconocido";

                return "info|" + message;
            } else {
                String tablaAlumnos = armarTablaHtmlCalificaciones(respuestaServicio);
                
                return "success|" + tablaAlumnos;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error|Excepción al procesar la prueba de conexión: " + ex.getMessage();
        }
    }
    
    public String guardarCalificacionesClickEscolar() throws SQLException, IOException{
        //Variables para la conexion
        Connection con = null;
        CallableStatement cstmt = null;
        con = conexion.GetconexionInSite();
        
        ResultSet rs;
        String errorSP = "";
        
        String jsonString = request.getParameter("json");
        Gson gson = new Gson();
        
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Map<String, List<String>> erroresPorAlumno = new LinkedHashMap<>();
        
        try{
            List<CalificacionClickEscolar> filas = gson.fromJson(jsonString, new TypeToken<List<CalificacionClickEscolar>>() {}.getType());
            for (int x = 0; x < filas.size(); x++) {
                String Query = "{call Add_Calificacion_ClickEscolar(?,?,?,?,?,?,?)}";
                cstmt = con.prepareCall(Query);
                cstmt.setString(1, filas.get(x).getMatricula());
                cstmt.setString(2, filas.get(x).getIdCarrera());
                cstmt.setString(3, filas.get(x).getCveMateria());
                cstmt.setString(4, filas.get(x).getCalificacion());
                cstmt.setString(5, filas.get(x).getCicloEscolar());
                cstmt.setString(6, filas.get(x).getIdObservacion());
                cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);

                cstmt.execute();
                
                if ((cstmt.getUpdateCount() == -1) && (cstmt.getResultSet() != null)) {
                    rs = cstmt.getResultSet();
                    rs.next();
                    Logger.getLogger(CAlumnos.class.getName()).log(Level.INFO, "Error Message: ------->{0}", rs.getString("ErrorMessage"));
                    return "error|"
                                + "<p>Calificaciones no encontradas <br><b>Matrícula: " + filas.get(x).getMatricula() + "</b>.</p>"
                                + "<small>El servidor devolvió el siguiente mensaje de error desde SQL: <b>" + rs.getString("ErrorMessage") + "</b></small>"
                                + "<br><small>Los registros posteriores no fueron insertados.</small>"
                                + "<br><small>Verifica la información. Si el problema persiste contacta a soporte técnico para obtener más información.</small>";
                } else {
                    errorSP = cstmt.getString(7);
                    
                    if(!errorSP.equalsIgnoreCase("Calificación registrada correctamente")){
                        if(filas.size() > 1){
                            String alumnoKey = filas.get(x).getMatricula() + " - " + filas.get(x).getNombreAlumno();
                            String lineaError = String.format("%-10s %-50s %-6s %-100s",
                                    filas.get(x).getCveMateria(),
                                    filas.get(x).getNombreMateria(),
                                    filas.get(x).getCalificacion(),
                                    errorSP);

                            erroresPorAlumno.computeIfAbsent(alumnoKey, k -> new ArrayList<>()).add(lineaError);
                        }else{
                            return "error|"
                                    + "<p>No se pudo registrar la calificación <b>" + filas.get(x).getCalificacion() + "</b> en la materia <b>" + filas.get(x).getCveMateria() + " " + filas.get(x).getNombreMateria() + "</b> del alumno <b>" + filas.get(x).getMatricula() + " " + filas.get(x).getNombreAlumno() + "</b></p>"
                                    + "<small>El servidor arrojó el siguiente error:</small>"
                                    + "<br><small><b>" + errorSP + "</b></small>"
                                    + "<br><br><small>Los registros posteriores no fueron insertados.</small>"
                                    + "<br><small>Verifica la información. Si el problema persiste contacta a soporte técnico para obtener más información.</small>";
                        }
                    }
                }   
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la lectura de la información: " + ex.getMessage();
        } catch (Exception ex) {
            Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error inesperado al cargar la información: " + ex.getMessage();
        }
        
        // Si hubo errores, escribirlos a un archivo
        if (!erroresPorAlumno.isEmpty()) {
            String rutaGuardado = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\errorresDescargaClickEscolar";
            String nombreArchivo = "errores_calificaciones_clickEscolar_" + System.currentTimeMillis() + ".txt";
            String rutaCompleta = rutaGuardado + "\\" + nombreArchivo;
            String rutaDescarga = "Instituciones\\" + NombreInstitucion.trim() + "\\errorresDescargaClickEscolar\\" + nombreArchivo;
            
            try {
                // Crear carpeta si no existe
                File carpeta = new File(rutaGuardado);
                if (!carpeta.exists()) {
                    carpeta.mkdirs(); // Crea todas las carpetas necesarias
                }

                // Escribir archivo
                try (PrintWriter writer = new PrintWriter(new FileWriter(rutaCompleta))) {
                    writer.println("REPORTE DE ERRORES CALIFICACIONES DIGISEP - CLICK ESCOLAR " + formatter.format(ahora));
                    writer.println();

                    for (Map.Entry<String, List<String>> entry : erroresPorAlumno.entrySet()) {
                        writer.println(entry.getKey());
                        writer.printf("%-10s %-50s %-6s %-100s%n","Clave", "Materia", "Cal.", "Mensaje de Error");
                        for (String linea : entry.getValue()) {
                            writer.println(linea);
                        }
                        writer.println();
                    }
                }
            } catch (IOException e) {
                Logger.getLogger(ClickEscolarHandlerService.class.getName()).log(Level.SEVERE, null, e);
                return "error|Ocurrió un error al generar el archivo: " + e.getMessage();
            }
            return "errorConArchivo|Se realizo el proceso de registro, pero se detectaron algunos errores durante la carga.|" + rutaDescarga;
        }
        
        return "success|La calificaciones fueron guardadas con éxito";
    }
    
    public static String armarTablaHtmlCarrerasConMaterias(JsonArray jsonArray) throws Exception {
        Encriptacion encryp = new Encriptacion();
        StringBuilder htmlTable = new StringBuilder();
        StringBuilder htmlMateriaTable = new StringBuilder();

        htmlTable.append("<table class='table table-bordered table-striped dataTable no-footer tabla-carreras-materias-click-escolar'>\n");

        if (jsonArray.size() > 0) {
            JsonObject firstObj = jsonArray.get(0).getAsJsonObject();
            htmlTable.append("<thead class='bg-primary-dark' style='color: white;'>\n<tr>\n");

            // Crear encabezados, ignorando "Materias"
            for (Map.Entry<String, JsonElement> entry : firstObj.entrySet()) {
                String key = entry.getKey();
                if (!key.equalsIgnoreCase("Materias")) {
                    String formattedKey = key.replace("_", " ");
                    htmlTable.append("<th>").append(formattedKey).append("</th>\n");
                }
            }
            
            htmlTable.append("<th style='display: none;'>Materias</th>\n");
            htmlTable.append("</tr>\n</thead>\n");
        }

        htmlTable.append("<tbody>\n");

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject carrera = jsonArray.get(i).getAsJsonObject();
            
            // Fila principal (carrera)
            htmlTable.append("<tr>\n");
            for (Map.Entry<String, JsonElement> entry : carrera.entrySet()) {
                String key = entry.getKey();
                //idCarrera = encryp.desencriptar(entry.getValue().getAsString());
                if (!key.equalsIgnoreCase("Materias")) {
                    String valor = encryp.desencriptar(entry.getValue().getAsString());
                    htmlTable.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;' id='")
                            .append(key)
                            .append("~")
                            .append(valor)
                            .append("'>")
                            .append(valor)
                            .append("</td>\n");
                }
            }

            // Fila expandible oculta con las materias
            StringBuilder jsonMaterias = new StringBuilder();
            jsonMaterias.append("["); // comienza arreglo general

            if (carrera.has("Materias")) {
                JsonArray materias = carrera.getAsJsonArray("Materias");

                for (int a = 0; a < materias.size(); a++) {
                    JsonObject mat = materias.get(a).getAsJsonObject();

                    // Comenzar objeto de materia
                    jsonMaterias.append("{");
                    jsonMaterias.append("'folioOrdenamiento':'").append(encryp.desencriptar(mat.get("FolioOrdenamiento").getAsString())).append("',");
                    jsonMaterias.append("'claveSep':'").append(encryp.desencriptar(mat.get("ClaveSep").getAsString())).append("',");
                    jsonMaterias.append("'nombreMat':'").append(encryp.desencriptar(mat.get("NombreMat").getAsString())).append("',");
                    jsonMaterias.append("'creditosMateria':'").append(encryp.desencriptar(mat.get("CreditosMateria").getAsString())).append("',");
                    jsonMaterias.append("'idTipoAsignatura':'").append(encryp.desencriptar(mat.get("idTipoAsignatura").getAsString())).append("'");
                    jsonMaterias.append("}");

                    // Agregar coma si no es el último
                    if (a < materias.size() - 1) {
                        jsonMaterias.append(",");
                    }
                }
            }

            jsonMaterias.append("]"); // cierra arreglo
            htmlTable.append("<td style='display: none; padding: 8px; border-bottom: 1px solid #ddd; text-align: center;' id='json-materias'>");
            htmlTable.append(jsonMaterias.toString());
            htmlTable.append("</td>\n");
            htmlTable.append("</tr>\n");
        }

        htmlTable.append("</tbody>\n</table>");

        return htmlTable.toString();
    }

    
    public static String armarTablaHtmlAlumnos(JsonArray jsonArray, String tipoBusqueda) throws Exception {
        Encriptacion encryp = new Encriptacion();
        
        // Crear el StringBuilder para construir la tabla HTML
        StringBuilder htmlTable = new StringBuilder();
        
        // Comenzar la tabla HTML con algunos estilos básicos
        htmlTable.append("<table class='").append(tipoBusqueda.equalsIgnoreCase("individual") ? "tabla-alumnos-servicio-click-escolar" : "tabla-alumnos-por-carrera-servicio-click-escolar").append(" table table-bordered table-condensed table-striped dataTable no-footer'>\n");
        
        // Agregar el encabezado de la tabla si hay elementos
        if (jsonArray.size() > 0) {
            JsonObject firstObj = jsonArray.get(0).getAsJsonObject();
            htmlTable.append("<thead class='bg-primary-dark' style='color: white;'>\n<tr>\n");
            
            // Iterar sobre las entradas del primer objeto para los encabezados
            Set<Map.Entry<String, com.google.gson.JsonElement>> entries = firstObj.entrySet();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : entries) {
                String key = entry.getKey();
                // Formatear el nombre de la columna (removiendo underscores)
                String formattedKey = key.replace("_", " ");
                htmlTable.append("<th style='")
                         .append( (key.equalsIgnoreCase("Id_Alumno") ? " display: none;" : "") )
                         .append("'>")
                         .append(formattedKey)
                         .append("</th>\n");
            }
            
            htmlTable.append("</tr>\n</thead>\n");
        }
        
        // Agregar el cuerpo de la tabla
        htmlTable.append("<tbody>\n");
        
        // Iterar sobre cada objeto en el JsonArray
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject obj = jsonArray.get(i).getAsJsonObject();
            // Alternar colores de fila para mejor legibilidad
            String rowColor = (i % 2 == 0) ? "#ffffff" : "#f9f9f9";
            htmlTable.append("<tr style='background-color: ").append(rowColor).append(";'>\n");
            
            // Iterar sobre cada entrada en el objeto
            Set<Map.Entry<String, com.google.gson.JsonElement>> entries = obj.entrySet();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                htmlTable.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;")
                         .append( (key.equalsIgnoreCase("Id_Alumno") ? " display: none;" : "") )
                         .append("'")
                         .append(" id='")
                         .append(key)
                         .append("~")
                         .append(encryp.desencriptar(value))
                         .append("'>")
                         .append(encryp.desencriptar(value))
                         .append("</td>\n");
            }
            
            htmlTable.append("</tr>\n");
        }
        
        htmlTable.append("</tbody>\n");
        htmlTable.append("</table>");
        
        return htmlTable.toString();
    }

    public static String armarTablaHtmlCalificaciones(JsonArray jsonArray) throws Exception {
        Encriptacion encryp = new Encriptacion();
        
        // Crear el StringBuilder para construir la tabla HTML
        StringBuilder htmlTable = new StringBuilder();
        
        // Comenzar la tabla HTML con algunos estilos básicos
        htmlTable.append("<table class='tabla-calificaciones-servicio-click-escolar table table-bordered table-condensed table-striped dataTable no-footer'>\n");
        
        // Agregar el encabezado de la tabla si hay elementos
        if (jsonArray.size() > 0) {
            JsonObject firstObj = jsonArray.get(0).getAsJsonObject();
            htmlTable.append("<thead class='bg-primary-dark' style='color: white;'>\n<tr>\n");
            
            // Iterar sobre las entradas del primer objeto para los encabezados
            Set<Map.Entry<String, com.google.gson.JsonElement>> entries = firstObj.entrySet();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : entries) {
                String key = entry.getKey();
                // Formatear el nombre de la columna (removiendo underscores)
                String formattedKey = key.replace("_", " ");
                htmlTable.append("<th style='")
                         .append( (key.equalsIgnoreCase("IdCalificacion") ? " display: none;" : "") )
                         .append("'>")
                         .append(formattedKey)
                         .append("</th>\n");
            }
            
            htmlTable.append("</tr>\n</thead>\n");
        }
        
        // Agregar el cuerpo de la tabla
        htmlTable.append("<tbody>\n");
        
        // Iterar sobre cada objeto en el JsonArray
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject obj = jsonArray.get(i).getAsJsonObject();
            // Alternar colores de fila para mejor legibilidad
            String rowColor = (i % 2 == 0) ? "#ffffff" : "#f9f9f9";
            htmlTable.append("<tr style='background-color: ").append(rowColor).append(";'>\n");
            
            // Iterar sobre cada entrada en el objeto
            Set<Map.Entry<String, com.google.gson.JsonElement>> entries = obj.entrySet();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                htmlTable.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;")
                         .append( (key.equalsIgnoreCase("IdCalificacion") ? " display: none;" : "") )
                         .append("'")
                         .append(" id='")
                         .append(key)
                         .append("~")
                         .append(encryp.desencriptar(value))
                         .append("'>")
                         .append(encryp.desencriptar(value))
                         .append("</td>\n");
            }
            
            htmlTable.append("</tr>\n");
        }
        
        htmlTable.append("</tbody>\n");
        htmlTable.append("</table>");
        
        return htmlTable.toString();
    }
    
    public void llenarNombreInstitucion() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        con = conexion.GetconexionInSite();
        ResultSet rs = null;
        try {
            con = conexion.GetconexionInSite();
            String query = "SELECT nombreInstitucion, logo, firmaImagen FROM Configuracion_Inicial AS CI "
                         + "JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                         + "WHERE Id_Usuario = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, Id_Usuario); // Asumiendo que Id_Usuario es un int
            rs = pstmt.executeQuery();
            if (rs.next()) {
                NombreInstitucion = rs.getString(1);
                nombreLogo = rs.getString(2);
                nombreFirma = rs.getString(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
