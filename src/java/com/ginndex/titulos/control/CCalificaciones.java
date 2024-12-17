/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Alumno;
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Calificacion;
import com.ginndex.titulos.modelo.CicloEscolar;
import com.ginndex.titulos.modelo.Grupo;
import com.ginndex.titulos.modelo.Materia;
import com.ginndex.titulos.modelo.Persona;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

/**
 *
 * @author Paola Alonso
 */
public class CCalificaciones {

    Calificacion calificacion;
    Grupo grupo;
    Alumno alumno;
    Materia materia;
    CicloEscolar ciclo;
    String Id_Usuario;
    String Bandera;
    String NombreArchivo;
    String rutaArchivo;
    String NombreInstitucion;
    HttpServletRequest request;
    HttpServletResponse response;
    CConexion conexion;
    ResultSet rs;
    Workbook libro;
    private String permisos;
    Bitacora bitacora;
    CBitacora cBitacora;

    public Calificacion getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Calificacion calificacion) {
        this.calificacion = calificacion;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public CicloEscolar getCiclo() {
        return ciclo;
    }

    public void setCiclo(CicloEscolar ciclo) {
        this.ciclo = ciclo;
    }

    public String getBandera() {
        return Bandera;
    }

    public void setBandera(String Bandera) {
        this.Bandera = Bandera;
    }

    public String getNombreArchivo() {
        return NombreArchivo;
    }

    public void setNombreArchivo(String NombreArchivo) {
        this.NombreArchivo = NombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public CCalificaciones() {
        calificacion = new Calificacion();
        grupo = new Grupo();
        alumno = new Alumno();
        materia = new Materia();
        ciclo = new CicloEscolar();
    }

    public String EstablecerAccionesCalificaciones() throws UnsupportedEncodingException, FileUploadException {
        //long startTime = System.nanoTime();
        String RESP = "";

        try {
            Bandera = (request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));

            HttpServletRequest requestProvisional = request;
            requestProvisional.setCharacterEncoding("UTF-8");
            HttpSession sessionOk = request.getSession();
            Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();

            CPermisos cPermisos = new CPermisos();
            cPermisos.setRequest(request);
            permisos = cPermisos.obtenerPermisos("Calificaciones");

            if (Bandera.equalsIgnoreCase("0")) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1024);
                ServletFileUpload up = new ServletFileUpload(factory);
                List<FileItem> partes = up.parseRequest(requestProvisional);
                Bandera = getDat(partes);
            }
            if (Bandera.equalsIgnoreCase("excel")) {
                RESP = ImportarCalificacionesExcel();
            } else if (Bandera.equalsIgnoreCase("1")) {
                RESP = CargarCalificaciones();
            } else if (Bandera.equalsIgnoreCase("2")) {
                RESP = EliminarCalificacion();
            } else if (Bandera.equalsIgnoreCase("3")) {
                RESP = cargarListaAlumnos();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(CCalificaciones.class.getName()).log(Level.SEVERE, "Error:----->", e);
        }
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime);
        //System.out.println("TIEMPO:" + duration);
        return RESP;
    }

    private String getDat(List<FileItem> partes) {
        String Bandera = "";
        PreparedStatement pstmt = null;
        Connection con = null;
        rs = null;
        conexion = new CConexion();
        conexion.setRequest(request);
        try {
            String Query = "SELECT nombreInstitucion FROM Configuracion_Inicial AS CI "
                    + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                    + " WHERE Id_Usuario = " + Id_Usuario;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                NombreInstitucion = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //String ubicacionarchivo = System.getProperty("user.home") + "\\Downloads\\PruebasCalificaciones";
        String ubicacionarchivo = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos";
        String input_actual = "";
        for (FileItem item : partes) {
            try {

                FileItem uploaded = null;
                uploaded = (FileItem) item;
                input_actual = uploaded.getFieldName();
                if (input_actual.equalsIgnoreCase("txtBandera")) {

                    Bandera = uploaded.getString();
                }
                if (Bandera.equalsIgnoreCase("excel")) {

                    if (input_actual.equalsIgnoreCase("fileCalificaciones")) {

                        try {
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            rutaArchivo = ubicacionarchivo + "\\ArchivoCalificaciones_" + uploaded.getName();
                            NombreArchivo = "ArchivoCalificaciones_" + uploaded.getName();
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                NombreArchivo = "";
                            } else {
                                File archivoServer = new File(rutaArchivo);
                                uploaded.write(archivoServer);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CCalificaciones.class.getName()).log(Level.SEVERE, "OCURRIÓ UN ERROR AL RECUPERAR DESDE EL FORM::---------------->", ex);
            }
        }

        return Bandera;
    }

    private String ImportarCalificacionesExcel() {
        //String ruta = System.getProperty("user.home") + "\\Downloads\\PruebasCalificaciones\\" + NombreArchivo;
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + NombreArchivo;
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        rs = null;
        FileInputStream archivo = null;
        ArrayList<String[]> data;
        int hojas = 0;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Calificaciones");
        bitacora.setMovimiento("Inserción");
        boolean isNotFound = false;
        int numFilaActual = 0;
        try {
            con = conexion.GetconexionInSite();
            // Para acceder al archivo ingresado
            archivo = new FileInputStream(new File(ruta));

            libro = WorkbookFactory.create(archivo);
            // Traemos el total de hojas de calculo que contiene el archivo
            hojas = libro.getNumberOfSheets();
            // Iniciamos en la primera hoja

            for (int i = 0; i < hojas; i++) {
                data = new ArrayList();

                Sheet hojaActual = libro.getSheetAt(i);
                System.out.println(hojaActual.getSheetName());
                int rows = hojaActual.getLastRowNum();
                System.out.println("Número de filas: " + rows);
                for (int j = 0; j <= rows; j++) {
                    Row rowActual = hojaActual.getRow(j);
                    if (rowActual == null) {
                        if (data.size() > 5) {
                            break;
                        }
                        return "sinCalificaciones||" + hojaActual.getSheetName();
                    }
                    //Designamos el espacio de acuerdo a las columnas del archivo excel (5 columnas)
                    String[] filaActual = new String[5];
                    int cols = rowActual.getLastCellNum();
                    //EMPEZAMOS A REVISAR LOS DATOS UBICADOS EN LA 5 FILA DEL ARCHIVO EXCEL
                    if (j >= 4) {
                        for (int k = 0; k < cols + 1; k++) {
                            Cell cellActual = rowActual.getCell(k);
                            //LEEMOS LA INFORMACIÓN DE LAS COLUMNAS QUE TIENEN INFORMACIÓN A SER INSERTADA (SON 5, recordando el indice de los arreglos iniciando en 0)
                            if (k <= 4) {
                                if (cellActual == null) {
                                    filaActual[k] = "";
                                } else {
                                    String columnLetter = CellReference.convertNumToColString(cellActual.getColumnIndex());
                                    switch (cellActual.getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            cellActual.setCellType(1);
                                            filaActual[k] = cellActual.getStringCellValue() + "";
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            filaActual[k] = cellActual.getStringCellValue();
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            filaActual[k] = "";
                                            break;
                                        case Cell.CELL_TYPE_FORMULA:
                                            return "usoFormulas||" + (i + 1) + "||" + (j + 1) + "||" + columnLetter;
                                        default:
                                            filaActual[k] = "";
                                            break;
                                    }
                                }
                                //Si la lectura del acrhivo termino de leer la información del alumno se inserta a la lista de datos.
                                if (j < 8 && k == 1) {
                                    data.add(filaActual);
                                    break;
                                    //Si la lectura del archivo es igual a las 5 (indice 4) columnas procedemos a validar la información
                                } else if (k == 4) {
                                    boolean[] valido = validarCamposCalif(filaActual);
                                    if (!valido[1]) {
                                        data.add(filaActual);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                // Terminamos de guardar los datos de la hoja en cuestión y agregamos los registros a la BD
                try {//

                    if (!data.isEmpty()) {
                        if (data.get(0)[0].trim().equalsIgnoreCase("* MATRÍCULA") && data.get(1)[0].trim().equalsIgnoreCase("NOMBRE")) {
                            if (data.size() > 5) {
                                for (int j = 0; j < data.size(); j++) {
                                    boolean[] valido = {};
                                    boolean valid;

                                    if (j <= 3) { // Validamos que los datos generales del alumno estén completos
                                        valid = validarCamposAlu(data.get(j));
                                        if (!valid && j != 1 && j != 3) {
                                            RESP = "infoAlumnoIncompleta";
                                            return RESP;
                                        }
                                    } else if (j >= 4) { // Validamos que los datos de las calificaciones estén completos
                                        valido = validarCamposCalif(data.get(j));
                                        if (!valido[0]) {
                                            RESP = "infoCalifIncompleta||" + hojaActual.getSheetName();
                                            return RESP;
                                        }
                                    }
                                }

                                if (!RESP.contains("infoAlumnoIncompleta") && !RESP.contains("infoCalifIncompleta")) {

                                    bitacora.setInformacion("Registro Califcaciones Excel: " + NombreArchivo + "||Hoja: " + i + "||Respuesta método: " + RESP
                                            + "||Con Matricula: " + data.get(0)[1].trim()
                                            + "||Con Nombre: " + data.get(1)[1].trim()
                                            + "||se registraron un total de calificaciones: " + (data.size() - 5));
                                    cBitacora = new CBitacora(bitacora);
                                    cBitacora.setRequest(request);
                                    cBitacora.addBitacoraGeneral();
                                    for (int j = 5; j < data.size(); j++) {
                                        numFilaActual = (j + 5);
//                                        String Query = "SET LANGUAGE 'español'; execute Add_Calificacion "
//                                                + "'" + data.get(0)[1]//Matricula del alumno
//                                                + "','" + data.get(2)[1]//Clave de la carrera
//                                                + "','" + data.get(j)[0]//Clave de la materia
//                                                + "'," + data.get(j)[2]// Calificacion
//                                                + "," + data.get(j)[3];// Observacion
                                        //System.out.println(Query);
                                        /**
                                         * @matricula varchar(25),
                                         * @idCarreraExcel varchar(25),
                                         * @claveMateria varchar(25),
                                         * @calificacion varchar(5),
                                         * @CICLO_ESCOLAR varchar(15),
                                         * @idObservacion int,
                                         * @respuesta varchar(250) OUTPUT
                                         */
                                        String Query = "";
                                        try {
                                            Query = "{call Add_Calificacion (?,?,?,?,?,?,?)}";
                                            float calif = Float.valueOf(data.get(j)[2]);
                                        } catch (NumberFormatException ex) {
                                            if (!data.get(j)[2].equalsIgnoreCase("AC") && !data.get(j)[2].equalsIgnoreCase("ACREDITADA")) {
                                                return "wrongStatement||" + (i + 1) + "||" + numFilaActual;
                                            }
                                            Query = "{call Add_Calificacion_Letra (?,?,?,?,?,?,?)}";
                                        }
                                        cstmt = con.prepareCall(Query);
                                        cstmt.setString(1, data.get(0)[1].trim());
                                        cstmt.setString(2, data.get(2)[1].trim());
                                        cstmt.setString(3, data.get(j)[0].trim());
                                        cstmt.setString(4, data.get(j)[2].trim());
                                        String cadenaCicloEscolar = data.get(j)[3].trim();
                                        String[] partesCicloEscolar = cadenaCicloEscolar.split("-");
                                       
                                            System.out.println(partesCicloEscolar[0].toString());
                                        

                                        if (partesCicloEscolar[0].length() == 4 && (partesCicloEscolar[1].length() == 1 || partesCicloEscolar[1].length() == 2)) {
                                            cstmt.setString(5, data.get(j)[3].trim());
                                        } else {
                                            return "ErrorCicloEscolar ||" + (j + 5);
                                        }

                                        cstmt.setInt(6, (data.get(j)[4] != null && !data.get(j)[4].equalsIgnoreCase("") ? Integer.valueOf(data.get(j)[4]) : 0));
                                        cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);

                                        cstmt.execute();
                                        RESP = cstmt.getString(7);

                                        if (!RESP.contains("sinCarrera") && !RESP.contains("sinAlumno") && !RESP.contains("sinMateria") && !RESP.contains("certificadoActivo") && !RESP.contains("success")) {
                                            RESP = "error";
                                            return RESP;
                                        } else {
                                            if (RESP.contains("sinCarrera")) {
                                                RESP += "||" + data.get(0)[1] + "||" + data.get(2)[1] + "||" + data.get(1)[1];
                                            } else if (RESP.contains("sinAlumno")) {
                                                RESP += "||" + data.get(0)[1];
                                            } else if (RESP.contains("sinMateria")) {
                                                RESP += "||" + data.get(0)[1] + "||" + data.get(j)[0] + "||" + data.get(1)[1];
                                            } else if (RESP.contains("certificadoActivo")) {
                                                RESP += "||" + data.get(0)[1] + "||" + data.get(1)[1];
                                            }
                                        }

                                        if (!RESP.contains("success")) {
                                            return RESP;
                                        }
                                    }
                                } else {
                                    return RESP;
                                }
                            } else {
                                RESP = "sinCalificaciones||" + (i + 1);
                                return RESP;
                            }
                        } else {
                            RESP = "formatoInvalido";
                            return RESP;
                        }
                    } else {
                        RESP = "formatoInvalido";
                        return RESP;
                    }
                } catch (SQLException e) {
                    RESP = "error";
                } catch (NumberFormatException e) {
                    RESP = "errorFormatNumber||" + (i + 1) + "||" + (numFilaActual);
                    return RESP;
                }
            }

            archivo.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, ex.getMessage());
            RESP = "error|Error FileNotFoundException al realizar la lectura del archivo cargado: " + accion_catch(ex);
            isNotFound = true;
        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la lectura del archivo cargado: " + accion_catch(ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error InvalidFormatException. El formato no corresponde con el formato: " + accion_catch(ex);
        } catch (IOException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error IOException al realizar la lectura del archivo cargado: " + accion_catch(ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error EncryptedDocumentException al realizar la lectura del archivo cargado: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error inesperado al leer el archivo cargado: " + accion_catch(ex);
        } finally {
            try {
                if (!isNotFound) {
                    archivo.close();
                    libro = null;
                    File archivoServer = new File(rutaArchivo);
                    archivoServer.delete();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }

                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }

                if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CCalificaciones.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CCalificaciones.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String CargarCalificaciones() {
        StringBuilder RESP = new StringBuilder();
        Alumno alumno;
        Persona persona;
        Calificacion calificacion;
        Materia materia;
        List<Alumno> lstAlumnos;
        List<Calificacion> lstCalificaciones;
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        rs = null;
        String idAlumno = (request.getParameter("idAlumno") == null ? "" : request.getParameter("idAlumno"));
        try {
            String Query = "SELECT TOP 2000 A.ID_Alumno, A.Matricula, P.Nombre, P.APaterno, M.clave,M.Descripcion, GR.Calificacion,'' as CalificacionLetra, M.ID_Materia ,m.folio "
                    + " FROM Persona AS P JOIN Alumnos AS A ON A.ID_Persona = P.Id_Persona "
                    + " JOIN Calificaciones AS GR ON GR.ID_Alumno = A.ID_Alumno "
                    + " JOIN Materias AS M ON M.ID_Materia = GR.ID_Materia WHERE A.estatus = 1 "
                    + (!idAlumno.equalsIgnoreCase("todos") && !idAlumno.equalsIgnoreCase("") ? " AND A.Id_Alumno = ? " : "") + ""
                    + "UNION\n"
                    + "SELECT TOP 500 A.ID_Alumno, A.Matricula, P.Nombre, P.APaterno, M.clave,M.Descripcion,0, GR.CalificacionLetra,  M.ID_Materia,m.folio\n"
                    + "FROM Persona AS P JOIN Alumnos AS A ON A.ID_Persona = P.Id_Persona  JOIN Calificaciones_Letra AS GR ON GR.ID_Alumno = A.ID_Alumno  \n"
                    + "JOIN Materias AS M ON M.ID_Materia = GR.ID_Materia WHERE A.estatus = 1 "
                    + (!idAlumno.equalsIgnoreCase("todos") && !idAlumno.equalsIgnoreCase("") ? " AND A.Id_Alumno = ? " : "") + ""
                    + "ORDER BY M.folio;";

            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            if (!idAlumno.equalsIgnoreCase("todos") && !idAlumno.equalsIgnoreCase("")) {
                pstmt.setString(1, idAlumno);
                pstmt.setString(2, idAlumno);
            }
            rs = pstmt.executeQuery();
            lstAlumnos = new ArrayList<>();
            lstCalificaciones = new ArrayList<>();
            while (rs.next()) {
                alumno = new Alumno();
                persona = new Persona();
                calificacion = new Calificacion();
                materia = new Materia();

                alumno.setId_Alumno(rs.getString("ID_Alumno"));
                alumno.setMatricula(rs.getString("Matricula"));
                persona.setNombre(rs.getString("Nombre"));
                persona.setAPaterno(rs.getString("APaterno"));
                materia.setID_Materia(rs.getString("ID_Materia"));
                materia.setClave(rs.getString("clave"));
                materia.setDescripcion(rs.getString("Descripcion"));
                if (rs.getString("Calificacion").equalsIgnoreCase("0.0") && rs.getString("CalificacionLetra").equalsIgnoreCase("AC") || rs.getString("CalificacionLetra").equalsIgnoreCase("ACREDITADA")) {
                    calificacion.setCalificacion(rs.getString("CalificacionLetra"));
                } else {
                    calificacion.setCalificacion(rs.getString("Calificacion"));
                }
                alumno.setPersona(persona);
                calificacion.setMateria(materia);
                lstAlumnos.add(alumno);
                lstCalificaciones.add(calificacion);
            }

            RESP.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblCalificaciones'>"
                    + " <thead class='bg-primary-dark' style='color: white;'>"
                    + "     <tr>\n"
                    + "         <th class='text-center' style='display:none;'>IdAlumno</th>\n"
                    + "         <th class='text-center hidden-xs'>Matrícula</th>\n"
                    + "         <th class='text-center hidden-xs hidden-sm hidden-md'>Nombre(s)</th>\n"
                    + "         <th class='text-center hidden-xs hidden-sm'>A. Paterno</th>\n"
                    + "         <th class='text-center'>Cve. Materia</th>\n"
                    + "         <th class='text-center hidden-md hidden-xs'>Materia</th>\n"
                    + "         <th class='text-center'>Calificación</th>\n"
                    + "         <th class='text-center'>Acciones</th>\n"
                    + "     </tr>\n"
                    + " </thead>\n"
                    + " <tbody id='tblBodyCalificaciones'>\n");
            for (int i = 0; i < lstAlumnos.size(); i++) {
                RESP.append("<tr>");
                RESP.append("<td style='display: none;' id='IdAlumno_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>").append(lstAlumnos.get(i).getId_Alumno()).append("</td>");
                RESP.append("<td class='hidden-xs' id='Matricula_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>").append(lstAlumnos.get(i).getMatricula()).append("</td>");
                RESP.append("<td class='hidden-xs hidden-sm hidden-md' id='Nombre_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>").append(lstAlumnos.get(i).getPersona().getNombre()).append("</td>");
                RESP.append("<td class='hidden-xs hidden-sm' id='APaterno_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>").append(lstAlumnos.get(i).getPersona().getAPaterno()).append("</td>");
                RESP.append("<td id='CveMateria_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>").append(lstCalificaciones.get(i).getMateria().getClave()).append("</td>");
                RESP.append("<td class='hidden-md hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top' title='").append(lstCalificaciones.get(i).getMateria().getDescripcion()).append("' id='Materia_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("' data-idMateria='").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>").append(lstCalificaciones.get(i).getMateria().getDescripcion()).append("</td>");
                RESP.append("<td class='text-right' id='Calificacion_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>").append(lstCalificaciones.get(i).getCalificacion()).append("</td>");
                RESP.append("<td class='text-center' id='Acciones_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("'>");
                if (permisos.contains("todos") || (permisos.contains("acceso") && permisos.split("°")[1].split("¬")[4].equalsIgnoreCase("1"))) {
                    RESP.append("<div class='btn-group'><button class='js-swal-confirm btn btn-default btn-xs btnEliminarCalificacion' data-container='body' type='button' data-toggle='tooltip' id='deleteCalificacion_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("' value='dltMateria_").append(lstAlumnos.get(i).getId_Alumno()).append("_").append(lstCalificaciones.get(i).getMateria().getID_Materia()).append("' data-original-title='Eliminar'><i class='fa fa-times'></i></button></div>");
                } else {
                    RESP.append("<span class='label label-danger'>No disponible</span>");
                }
                RESP.append("</td></tr>");
            }
            RESP.append("   </tbody>\n"
                    + "</table>");
        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP.setLength(0);
            RESP.append("error|Error SQL al cargar la tabla de calificaciones: ").append(accion_catch(ex));
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP.setLength(0);
            RESP.append("error|Error inesperado cargar la tabla de calificaciones: ").append(accion_catch(ex));
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }

                if (pstmt != null && !pstmt.isClosed()) {
                    pstmt.close();
                }

                if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CCalificaciones.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP.toString();
    }

    private String EliminarCalificacion() throws SQLException, Exception {
        String RESP = "";
        String idAlumno = request.getParameter("idAlumno");
        String idMateria = request.getParameter("idMateria");
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Calificaciones");
        bitacora.setMovimiento("Eliminación");
        try {
            //String Query = "SET LANGUAGE 'español'; execute Delt_Calificacion " + idAlumno + "," + idMateria;
            String Query = "{call Delt_Calificacion (?,?,?)}";

            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idAlumno));
            cstmt.setInt(2, Integer.valueOf(idMateria));
            cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);

            cstmt.execute();
            RESP = cstmt.getString(3);
            bitacora.setInformacion("Eliminación Calificacion Alumno: " + idAlumno + "||Materia: " + idMateria + "||Respuesta Método: " + RESP);
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la eliminación de la calificación del  alumno: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error inesperado al realizar la eliminación de la calificación del  alumno: " + accion_catch(ex);
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }
            if (cstmt != null && !cstmt.isClosed()) {
                cstmt.close();
            }
            if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                conexion.GetconexionInSite().close();
            }
        }

        return RESP;
    }

    /**
     * PENDIENTE REALIZAR DESCRIPCIÓN
     *
     * @param infoCalificaciones
     * @return
     */
    private boolean[] validarCamposCalif(String[] infoCalificaciones) {
        boolean complete = true;
        boolean filaVacia = false;
        boolean[] validaciones = new boolean[2];
        int contador = 0;

        for (int i = 0; i < infoCalificaciones.length; i++) {
            //Se evita verificar la columna 2 (Nombre Materia) y 5 (Id_Observación) de la fila analizada del excel recordando el indice de arreglos empezando en 0
            if ((infoCalificaciones[i] == null || infoCalificaciones[i].trim().equalsIgnoreCase("")) && i != 1 && i != 4) {
                complete = false;
                contador++;
            }
        }

        if (contador == infoCalificaciones.length - 2) {
            filaVacia = true;
        }

        validaciones[0] = complete;
        validaciones[1] = filaVacia;
        return validaciones;
    }

    private boolean validarCamposAlu(String[] infoAlumno) {
        boolean complete = true;

        //Restamos tres al tamaño de arreglo (5) recordando que verificamos la info del alumno la cual se compone de 2 columnas.
        for (int i = 0; i < infoAlumno.length - 3; i++) {
            if (infoAlumno[i].trim().equalsIgnoreCase("")) {
                complete = false;
                break;
            }
        }
        return complete;
    }

    private String cargarListaAlumnos() {
        StringBuilder RESP = new StringBuilder();
        PreparedStatement pstmt = null;
        Connection con = null;
        rs = null;
        try {
            conexion = new CConexion();
            List<Alumno> lstAlumnos = new ArrayList<Alumno>();
            String Query = "SELECT al.Id_Alumno, al.matricula, p.Nombre, p.APaterno, p.AMaterno\n"
                    + " FROM Alumnos al JOIN Persona p ON al.ID_Persona = p.Id_Persona\n"
                    + " WHERE al.estatus = 1 ORDER BY p.APaterno, p.AMaterno,p.Nombre, al.matricula";
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);

            rs = pstmt.executeQuery();

            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    Alumno al = new Alumno();
                    Persona p = new Persona();

                    al.setId_Alumno(rs.getString(1));
                    al.setMatricula(rs.getString(2));
                    p.setNombre(rs.getString(3));
                    p.setAPaterno(rs.getString(4));
                    p.setAMaterno((rs.getString(5) == null || rs.getString(5).equalsIgnoreCase("") ? "" : rs.getString(5)));

                    al.setPersona(p);
                    lstAlumnos.add(al);
                }
                RESP.append("success¬");
                RESP.append("<option value='todos'>Todos los alumnos</option>");
                for (int i = 0; i < lstAlumnos.size(); i++) {
                    RESP.append("<option value='")
                            .append(lstAlumnos.get(i).getId_Alumno())
                            .append("'>")
                            .append(lstAlumnos.get(i).getMatricula())
                            .append(" - ")
                            .append(lstAlumnos.get(i).getPersona().getNombre())
                            .append(" ")
                            .append(lstAlumnos.get(i).getPersona().getAPaterno())
                            .append(" ")
                            .append(lstAlumnos.get(i).getPersona().getAMaterno())
                            .append("</option>");
                }
            } else {
                RESP.append("empty¬");
                RESP.append("<option value='empty'>No se encontraron alumnos</option>");
            }

        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de alumnos: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de alumnos: " + accion_catch(ex);
        }
        return RESP.toString();
    }

    private String accion_catch(Exception ex) {
        String resp = "";
        try {
            resp = ex.toString().replace("'", "\"");
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            resp = "<h4 style='color:#e76d6d'>" + resp;
            resp += "</h4>";
        } catch (Exception e) {
            resp += "</h4><small class='text-primary'>No se ha insertado en Bitacora de errores</small>";
        }
        resp += "<br><br><small>Si continua con el problema, comuníquese con soporte técnico.</small> ";
        return resp;
    }
}
