package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Alumno;
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Carrera;
import com.ginndex.titulos.modelo.CicloEscolar;
import com.ginndex.titulos.modelo.Curso;
import com.ginndex.titulos.modelo.Generacion;
import com.ginndex.titulos.modelo.Grupo;
import com.ginndex.titulos.modelo.Persona;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellReference;
import org.codehaus.groovy.syntax.Types;

/**
 *
 * @author Paola Alonso
 * @description Clase controladora de alumnos
 * @version 0.1
 * @since 05/12/2018
 */
public class CAlumnos {

    String Bandera;
    String NombreArchivo;
    String NombreInstitucion;
    String rutaArchivo;
    String Id_Usuario;
    HttpServletRequest request;
    HttpServletResponse response;
    Alumno alumno;
    CicloEscolar ciclo;
    Carrera carrera;
    Curso curso;
    Grupo grupo;
    ResultSet rs;
    Workbook libro;
    CConexion conexion;
    private String permisos;
    Bitacora bitacora;
    CBitacora cBitacora;
    private final int COLUMNAS_A_LEER_EXCEL = 13;

    public String getBandera() {
        return Bandera;
    }

    public void setBandera(String Bandera) {
        this.Bandera = Bandera;
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

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public CicloEscolar getCiclo() {
        return ciclo;
    }

    public void setCiclo(CicloEscolar ciclo) {
        this.ciclo = ciclo;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
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

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public Workbook getLibro() {
        return libro;
    }

    public void setLibro(Workbook libro) {
        this.libro = libro;
    }

    public CAlumnos() {
        alumno = new Alumno();
        ciclo = new CicloEscolar();
        carrera = new Carrera();
        curso = new Curso();
        grupo = new Grupo();
    }

    public String EstablecerAcciones() throws FileUploadException, Exception {
        //long startTime = System.nanoTime();
        String RESP = "";
        Bandera = (request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));

        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");
        HttpSession sessionOk = request.getSession();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();

        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        permisos = cPermisos.obtenerPermisos("Alumnos");

        if (Bandera.equalsIgnoreCase("0")) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            ServletFileUpload up = new ServletFileUpload(factory);
            List<FileItem> partes = up.parseRequest(requestProvisional);
            Bandera = getDat(partes);
        }
        conexion = new CConexion();
        conexion.setRequest(request);
        if (Bandera.equalsIgnoreCase("excel")) {
            RESP = ImportarAlumnosExcel();
        } else if (Bandera.equalsIgnoreCase("1")) {
            RESP = CargarAlumnos();
        } else if (Bandera.equalsIgnoreCase("2")) {
            RESP = EliminarAlumno();
        }else if (Bandera.equalsIgnoreCase("3")){
            RESP = AddAlumnos();
        }else if (Bandera.equalsIgnoreCase("4")){
            RESP = UpdateAlumnos();
        }else if (Bandera.equalsIgnoreCase("5")){
            RESP = Consultar_Carreras();
        }else if (Bandera.equalsIgnoreCase("6")){
            RESP = Consultar_Calificaciones();
        }
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime);
        //System.out.println("TIEMPO:" + duration);
        return RESP;
    }

    private String getDat(List<FileItem> partes) throws SQLException {
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
            Logger.getLogger(CAlumnos.class.getName()).log(Level.SEVERE, "ERROR:---------------->", e);
        } finally {

            con.close();
            rs.close();
            conexion.GetconexionInSite().close();
            pstmt.close();
        }

        //String ubicacionarchivo = System.getProperty("user.home") + "\\Downloads\\PruebasAlumnos";
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

                    if (input_actual.equalsIgnoreCase("fileAlumnos")) {

                        try {
                            int rndm = (int) ((Math.random() * 1000) + 1);
                            int rndm2 = (int) ((Math.random() * 1000) + 1);
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            rutaArchivo = ubicacionarchivo + "\\ArchivoAlumnos_" + uploaded.getName().replace(".xlsx", rndm + "" + rndm2 + ".xlsx");
                            NombreArchivo = "ArchivoAlumnos_" + uploaded.getName().replace(".xlsx", rndm + "" + rndm2 + ".xlsx");
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                NombreArchivo = "";
                            } else {
                                File archivoServer = new File(rutaArchivo);
                                uploaded.write(archivoServer);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Logger.getLogger(CAlumnos.class.getName()).log(Level.SEVERE, "ERROR:---------------->", ex);
                        }
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CAlumnos.class.getName()).log(Level.SEVERE, "OCURRIÓ UN ERROR AL RECUPERAR DESDE EL FORM::---------------->", ex);
            }
        }

        return Bandera;
    }

    private String ImportarAlumnosExcel() {
        String ruta = "";
        boolean isNotFound = false;
        //ruta += System.getProperty("user.home") + "\\Downloads\\PruebasAlumnos\\" + NombreArchivo;
        ruta += System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + NombreArchivo;
        String RESP = "";
        Connection con = null;
        CallableStatement cstmt = null;

        ArrayList<String[]> data;
        FileInputStream archivo = null;
        int hojas = 0;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Alumnos");
        bitacora.setMovimiento("Inserción");
        try {
            con = conexion.GetconexionInSite();
            // Para acceder al archivo ingresado
            archivo = new FileInputStream(new File(ruta));
            // Crear el libro
            libro = WorkbookFactory.create(archivo);
            // Traemos el total de hojas de calculo que contiene el archivo
            hojas = libro.getNumberOfSheets();
            // Iniciamos en la primera hoja

            for (int i = 0; i < hojas; i++) {
                data = new ArrayList<>();

                Sheet hojaActual = libro.getSheetAt(i);
                int rows = hojaActual.getLastRowNum();//GET INDEX ROW

                for (int j = 0; j <= rows; j++) {
                    Row rowActual = hojaActual.getRow(j);
                    String[] filaActual = new String[COLUMNAS_A_LEER_EXCEL];
//                    int cols = rowActual.getLastCellNum();
                    if (j >= 4) {
                        for (int k = 0; k < COLUMNAS_A_LEER_EXCEL; k++) { //SE CAMBIA EL LIMITE DEL CICLO A 13 Y NO SE DEJA EN ALGO DINÁMICO, PUES LOS DATOS A INGRESAR SON 12 COLUMNAS.
                            Cell cellActual = rowActual.getCell(k);
                            if (k <= COLUMNAS_A_LEER_EXCEL - 1) {
                                if (cellActual == null) {
                                    filaActual[k] = "";
                                } else {
                                    String columnLetter = CellReference.convertNumToColString(cellActual.getColumnIndex());
                                    if ((k == 9 || k == 10 || k == 12) && (j > 4)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        Date fecha = null;
                                        try {
                                            fecha = cellActual.getDateCellValue();
                                        } catch (Exception e) {
                                            try {
                                                fecha = sdf.parse(cellActual.getStringCellValue());
                                            } catch (Exception ex) {
                                                RESP = "celdaNoValida||" + (j + 1) + "||" + (k + 1);
                                                return RESP;
                                            }
                                        }
                                        if (fecha != null) {
                                            filaActual[k] = sdf.format(fecha);
                                        } else {
                                            filaActual[k] = "";
                                        }
                                    } else {
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
                                        }
                                    }
                                }
                                if (k == 3 && filaActual[3].trim().equalsIgnoreCase("")) {
                                    filaActual[3] = "_";
                                }
                                if (k == COLUMNAS_A_LEER_EXCEL - 1) {
                                    boolean valido[] = validarCampos(filaActual);
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
                boolean[] valido = {};
                if (!data.isEmpty()) {
                    if (data.get(0)[0].equalsIgnoreCase("* Matrícula") && data.get(0)[1].equalsIgnoreCase("* Nombre(s)")) {
                        for (int q = 0; q < data.size(); q++) {
                            valido = validarCampos(data.get(q));
                            if (!valido[0]) {
                                RESP = "infoIncompleta||" + (i + 1);
                                return RESP;
                            }
                        }

                        if (valido[0]) {
                            if (data.size() > 1) {
                                for (int j = 1; j < data.size(); j++) {
                                    if ((data.get(j)[4].trim().length() != 18) && !data.get(j)[4].trim().equalsIgnoreCase("EXTRANJERO")) {
                                        RESP = "curp||" + data.get(j)[0] + "||" + data.get(j)[1] + " " + data.get(j)[2] + " " + data.get(j)[3] + "||" + data.get(j)[4];
                                        return RESP;
                                    }
                                    String fechaNacimiento = "";
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                    sdf.setLenient(false);
                                    if (!data.get(j)[4].trim().equalsIgnoreCase("EXTRANJERO")) {
                                        String aaaa = data.get(j)[4].trim().substring(4, 6);
                                        String mm = data.get(j)[4].trim().substring(6, 8);
                                        String dd = data.get(j)[4].trim().substring(8, 10);
                                        //
                                        //QUERIDO COLABORADOR.
                                        //EL DÍA QUE SE REALIZÓ ESTA CONDICIÓN ESTABAMOS EN EL AÑO 2019, POR CONSECUENTE ERA VÁLIDA
                                        //SI ES NECESARIO MODIFICARLA PORQUE LA CONDICIÓN DEL AÑO YA NO ES SUFICIENTE, PUES ADELANTE :D.
                                        //ME SORPRENDE Y ME DA GUSTO QUE LA HERRAMIENTA SIGA VIGENTE (2030).
                                        //ATTE. UNKNOWN :0
                                        //NUM. DE VECES CAMBIADA: 1
                                        //
                                        if (Integer.parseInt(aaaa) < 30) {
                                            aaaa = "20" + aaaa;
                                        } else {
                                            aaaa = "19" + aaaa;
                                        }
                                        fechaNacimiento = dd + "-" + mm + "-" + aaaa;
                                        try {
                                            sdf.parse(fechaNacimiento);
                                        } catch (ParseException ex) {
                                            RESP = "curp||" + data.get(j)[0] + "||" + data.get(j)[1] + " " + data.get(j)[2] + " " + data.get(j)[3] + "||" + data.get(j)[4];
                                            return RESP;
                                        }
                                    } else {
                                        if (!data.get(j)[12].equalsIgnoreCase("")) {
                                            SimpleDateFormat tmp = new SimpleDateFormat("dd/MM/yyyy");
                                            fechaNacimiento = sdf.format(tmp.parse(data.get(j)[12]));
                                        } else {
                                            return RESP = "fechaExtranjero||" + data.get(j)[0] + "||" + data.get(j)[1] + " " + data.get(j)[2] + " " + data.get(j)[3] + "||" + (data.get(j)[12].equalsIgnoreCase("") ? "CAMPO_VACIO" : data.get(j)[12]);
                                        }
                                    }
                                    System.out.println(fechaNacimiento);
//                                    String Query = "SET LANGUAGE 'español'; execute Add_Alumno_Excel "
//                                            + "'" + data.get(j)[0] + "','" + data.get(j)[1] + "','" + data.get(j)[2] + "','" + data.get(j)[3] + "',"
//                                            + "'" + data.get(j)[4] + "','" + data.get(j)[5] + "','" + data.get(j)[6] + "','" + data.get(j)[8] + "',"
//                                            + "'" + data.get(j)[9] + "','" + data.get(j)[10] + "','" + fechaNacimiento + "'";
                                    String Query = "{call Add_Alumno_Excel(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                                    cstmt = con.prepareCall(Query);
                                    cstmt.setString(1, data.get(j)[0].trim());
                                    //System.out.println(data.get(j)[0]);
                                    cstmt.setString(2, data.get(j)[1].trim());
                                    //System.out.println(data.get(j)[1]);
                                    cstmt.setString(3, data.get(j)[2].trim());
                                    //System.out.println(data.get(j)[2]);
                                    cstmt.setString(4, (data.get(j)[3].equalsIgnoreCase("_") ? null : data.get(j)[3].trim()));
                                    //System.out.println(data.get(j)[3]);
                                    cstmt.setString(5, data.get(j)[4].trim());
                                    //System.out.println(data.get(j)[4]);
                                    cstmt.setString(6, data.get(j)[5].trim());
                                    //System.out.println(data.get(j)[5]);
                                    cstmt.setString(7, data.get(j)[6].trim());
                                    //System.out.println(data.get(j)[6]);
                                    cstmt.setString(8, data.get(j)[8].trim());
                                    //System.out.println(data.get(j)[8]);
                                    cstmt.setString(9, data.get(j)[9].trim());
                                    //System.out.println(data.get(j)[9]);
                                    cstmt.setString(10, data.get(j)[10].trim());
                                    //System.out.println(data.get(j)[10]);
                                    cstmt.setString(11, fechaNacimiento);
                                    //System.out.println(fechaNacimiento);
                                    cstmt.setString(12, data.get(j)[11].trim());
                                    //System.out.println(data.get(j)[11]);
                                    cstmt.registerOutParameter(13, java.sql.Types.VARCHAR);

                                    cstmt.execute();
                                    //rs = conector.consulta(Query);
                                    if ((cstmt.getUpdateCount() != -1) && (cstmt.getResultSet() != null)) {
                                        rs = cstmt.getResultSet();
                                        rs.next();
                                        Logger.getLogger(CAlumnos.class.getName()).log(Level.INFO, "Error Message: ------->{0}", rs.getString("ErrorMessage"));
                                        return "error|"
                                                + "<p>Error al insertar al alumno con la matrícula: <b>" + data.get(j)[0] + "</b>.</p><br>"
                                                + "<small>El servidor devolvió el siguiente mensaje de error desde SQL: <b>" + rs.getString("ErrorMessage") + "</b></small>"
                                                + "<br><small>Los registros posteriores no fueron insertados.</small>"
                                                + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
                                    } else {
                                        RESP = cstmt.getString(13);
                                        if (RESP.equalsIgnoreCase("sinCarrera")) {
                                            RESP = "sinCarrera||" + data.get(j)[0] + "||" + data.get(j)[1] + " " + data.get(j)[2] + " " + data.get(j)[3];
                                            return RESP;
                                        } else if (RESP.equalsIgnoreCase("certificadoActivo")) {
                                            RESP = "certificadoActivo||" + data.get(j)[0] + "||" + data.get(j)[1] + " " + data.get(j)[2] + " " + data.get(j)[3];
                                            return RESP;
                                        } else if (RESP.equalsIgnoreCase("tituloActivo")) {
                                            RESP = "tituloActivo||" + data.get(j)[0] + "||" + data.get(j)[1] + " " + data.get(j)[2] + " " + data.get(j)[3];
                                            return RESP;
                                        } else if (!RESP.equalsIgnoreCase("success")) {
                                            RESP = "error";
                                            return RESP;
                                        }
                                        bitacora.setInformacion("Registro Alumnos Excel: " + NombreArchivo + "||Hoja: " + i + "||Respuesta método: " + RESP);
                                        cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral();
                                    }
                                }
                            } else {
                                RESP = "sinAlumnos||" + (i + 1);
                                return RESP;
                            }
                        }
                    } else {
                        RESP = "formatoInvalido";
                        break;
                    }
                } else {
                    RESP = "formatoInvalido";
                    break;
                }
            }
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
                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }

                if (con != null && !con.isClosed()) {
                    con.close();
                }

                if (conexion.GetconexionInSite() != null && !conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Logger.getLogger(CAlumnos.class.getName()).log(Level.SEVERE, "Error:---------->", e);
            } catch (IOException ex) {
                Logger.getLogger(CAlumnos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private boolean[] validarCampos(String[] infoAlumno) {
        boolean complete = true;
        boolean filaVacia = false;
        boolean[] validaciones = new boolean[2];
        int contador = 0;

        //EVITAMOS LEER LA COLUMNA DE FECHA DE NACIMIENTO
        for (int i = 0; i < infoAlumno.length - 1; i++) {
            if ((infoAlumno[i] == null || infoAlumno[i].trim().equalsIgnoreCase("")) && (i != 3 && i != 12)) {
                complete = false;
                contador++;
            }
        }

        if (contador == infoAlumno.length - 2) {
            filaVacia = true;
        }

        validaciones[0] = complete;
        validaciones[1] = filaVacia;
        return validaciones;
    }

    private String CargarAlumnos() throws SQLException, Exception {
        StringBuilder RESP = new StringBuilder();
        Alumno alumno;
        Persona persona;
        Carrera carrera;
        Generacion generacion;
        List<Alumno> lstAlumnos;
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        rs = null;
        String idCarrera = (request.getParameter("idCarrera") == null ? "" : request.getParameter("idCarrera"));
        try {
            String Query = "SELECT DISTINCT A.ID_Alumno, A.Matricula, A.FechaInicioCarrera, A.FechaFinCarrera,P.Nombre, P.APaterno, P.AMaterno, P.FechaNacimiento, P.Curp, P.Sexo, P.Email, C.ID_Carrera, C.Descripcion, C.Id_Carrera_Excel, G.Generacion "
                    + " FROM Alumnos AS A "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " JOIN Carrera AS C ON A.ID_Carrera = C.ID_Carrera "
                    + " JOIN Generaciones AS G ON G.ID_Generacion = A.ID_Generacion "
                    + " WHERE A.estatus = 1 " + (!idCarrera.equals("todos") && !idCarrera.equals("") ? " AND C.Id_Carrera_Excel = ? " : "")
                    + " ORDER BY A.ID_Alumno DESC;";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            if (!idCarrera.equals("todos") && !idCarrera.equals("")) {
                pstmt.setString(1, idCarrera);
            }
            rs = pstmt.executeQuery();
            lstAlumnos = new ArrayList<>();
            while (rs.next()) {
                alumno = new Alumno();
                persona = new Persona();
                carrera = new Carrera();
                generacion = new Generacion();

                alumno.setId_Alumno(rs.getString("ID_Alumno"));
                alumno.setMatricula(rs.getString("Matricula"));
                persona.setNombre(rs.getString("Nombre"));
                persona.setAPaterno(rs.getString("APaterno"));
                persona.setAMaterno(rs.getString("AMaterno"));
                persona.setEmail(rs.getString("Email"));
                if("250".equals(rs.getString("Sexo"))){
                     persona.setSexo("M");
                }else if("251".equals(rs.getString("Sexo"))){
                 persona.setSexo("H");
                }
                
                persona.setFechaNacimiento(rs.getString("FechaNacimiento"));
                persona.setCurp(rs.getString("Curp"));
                carrera.setID_Carrera(rs.getString("Id_Carrera_Excel"));
                carrera.setId_Carrera_Excel(rs.getString("ID_Carrera"));
                carrera.setDescripcion(rs.getString("Descripcion"));
                generacion.setGeneracion(rs.getString("Generacion"));
                alumno.setPersona(persona);
                alumno.setCarrera(carrera);
                alumno.setGeneracion(generacion);
                alumno.setFechaInicioCarrera(rs.getString("FechaInicioCarrera"));
                alumno.setFechaFinCarrera(rs.getString("FechaFinCarrera"));
                lstAlumnos.add(alumno);
            }

            for (int i = 0; i < lstAlumnos.size(); i++) {
                String QueryAl = "SELECT TOP 1 ID_Materia FROM Calificaciones WHERE ID_Alumno = " + lstAlumnos.get(i).getId_Alumno();
                rs = null;
                pstmt = con.prepareStatement(QueryAl);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    lstAlumnos.get(i).setNotas("Alumno Calificado");
                } else {
                    lstAlumnos.get(i).setNotas("Sin Calificaciones");
                }
            }

            RESP.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblAlumnos'>"
                    + " <thead class='bg-primary-dark' style='color: white;'>"
                    + "     <tr>"
                    + "         <th class='text-center' style='display:none;'>IdAlumno</th>"
                    + "         <th class='text-center' style='display:none;'>Calificaciones</th>"
                    + "         <th class='text-center' style='display:none;'>A. Materno</th>"
                    + "         <th class='text-center' style='display:none;'>CURP</th>"
                    + "         <th class='text-center' style='display:none;'>Generacion</th>"
                    + "         <th class='text-center' style='display:none;'>Sexo</th>"
                    + "         <th class='text-center' style='display:none;'>Fecha inicio</th>"
                    + "         <th class='text-center' style='display:none;'>Fecha fin</th>"
                    + "         <th class='text-center' style='display:none;'>Correo</th>"
                    + "         <th class='text-center'>Matrícula</th>"
                    + "         <th class='text-center hidden-sm hidden-xs'>Nombre(s)</th>"
                    + "         <th class='text-center hidden-xs'>A. Paterno</th>"
                    + "         <th class='text-center'>Id Carrera</th>"
                    + "         <th class='text-center hidden-md hidden-sm hidden-xs'>Carrera</th>"
                    + "         <th class='text-center hidden-sm hidden-xs hidden-md'>F. Nacimiento</th>"
                    + "         <th class='text-center'>Acciones</th>"
                    + "     </tr>"
                    + " </thead>"
                    + " <tbody id='tblBodyAlumnos'>");

            for (int i = 0; i < lstAlumnos.size(); i++) {
                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display: none;' id='IdAlumno_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getId_Alumno()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='Calificaciones_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getNotas()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='AMaterno_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getPersona().getAMaterno()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='Curp_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getPersona().getCurp()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='Generacion_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getGeneracion().getGeneracion()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='Sexo_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getPersona().getSexo()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='FechaInicio_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getFechaInicioCarrera()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='FechaFin_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getFechaFinCarrera()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='Correo_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getPersona().getEmail()).append("</td>");
                RESP.append("<td id='Matricula_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getMatricula()).append("</td>");
                RESP.append("<td class='hidden-sm hidden-xs' id='Nombre_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getPersona().getNombre()).append("</td>");
                RESP.append("<td class='hidden-xs' id='APaterno_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getPersona().getAPaterno()).append("</td>");
               
                RESP.append("<td data-container='body' data-toggle='tooltip' data-placement='top' name='").append(lstAlumnos.get(i).getCarrera().getId_Carrera_Excel()).append("' id='idCarrera_").append(lstAlumnos.get(i).getId_Alumno()).append("' title='").append(lstAlumnos.get(i).getCarrera().getID_Carrera()).append("'>").append(lstAlumnos.get(i).getCarrera().getID_Carrera()).append("</td>");
                RESP.append("<td name='Carrera_").append(lstAlumnos.get(i).getId_Alumno()).append("' id='Carrera_").append(lstAlumnos.get(i).getId_Alumno()).append("' id-carrera='").append(lstAlumnos.get(i).getCarrera().getID_Carrera()).append("' class='hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top' title='").append(lstAlumnos.get(i).getCarrera().getDescripcion()).append("'>").append(lstAlumnos.get(i).getCarrera().getDescripcion()).append("</td>");
                if (lstAlumnos.get(i).getPersona().getFechaNacimiento() == null || lstAlumnos.get(i).getPersona().getFechaNacimiento().equalsIgnoreCase("null")) {
                    RESP.append("<td class='text-center hidden-sm hidden-xs hidden-md' id='FNacimiento_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(lstAlumnos.get(i).getPersona().getFechaNacimiento()).append("</td>");
                } else {
                    SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
                    Date fNacimiento = parseador.parse(lstAlumnos.get(i).getPersona().getFechaNacimiento());
                    RESP.append("<td class='text-center hidden-sm hidden-xs hidden-md' id='FNacimiento_").append(lstAlumnos.get(i).getId_Alumno()).append("'>").append(formateador.format(fNacimiento)).append("</td>");
                }
                RESP.append("<td class='text-center' nowrap id='Acciones_").append(lstAlumnos.get(i).getId_Alumno()).append("'>");
                if (permisos.contains("todos") || (permisos.contains("acceso") && permisos.split("°")[1].split("¬")[4].equalsIgnoreCase("1"))) {
                    RESP.append("<div class='btn-group'>"
                            + "<button class='btn btn-default btn-xs btnMostrarAlumno' data-container='body' type='button' data-toggle='tooltip' id='mostrarAlumno_").append(lstAlumnos.get(i).getId_Alumno()).append("' value='dltMateria_").append(lstAlumnos.get(i).getId_Alumno()).append("' data-original-title='Mostrar'><i class='fa fa-search'></i></button>"
                            + "<button class='btn btn-default btn-xs btnModificarAlumno' data-container='body' type='button' data-toggle='tooltip' id='modificarAlumno_").append(lstAlumnos.get(i).getId_Alumno()).append("' value='dltMateria_").append(lstAlumnos.get(i).getId_Alumno()).append("' data-original-title='Modificar'><i class='fa fa-pencil'></i></button>"
                            + "<button class='js-swal-confirm btn btn-default btn-xs btnEliminarAlumno' data-container='body' type='button' data-toggle='tooltip' id='deleteAlumno_").append(lstAlumnos.get(i).getId_Alumno()).append("' value='dltMateria_").append(lstAlumnos.get(i).getId_Alumno()).append("' data-original-title='Eliminar'><i class='fa fa-times'></i></button>"
                            + "</div>");
                } else {
                    RESP.append("<span class='label label-danger'>No disponible</span>");
                }
                RESP.append("</td></tr>");
            }

            RESP.append(" </tbody>"
                    + "</table>");

        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de alumnos: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de alumnos: " + accion_catch(ex);
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }

            if (pstmt != null && !pstmt.isClosed()) {
                pstmt.close();
            }

            if (rs != null && !rs.isClosed()) {
                rs.close();
            }

            if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                conexion.GetconexionInSite().close();
            }
        }
        return RESP.toString();
    }

    private String EliminarAlumno() {
        String RESP = "";
        String idAlumno = request.getParameter("idAlumno");
        Connection con = null;
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Alumnos");
        bitacora.setMovimiento("Eliminación");
        try {
            //String Query = "SET LANGUAGE 'español'; execute Delete_Alumno " + idAlumno;
            String Query = "{call Delt_Alumno (?,?)}";
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idAlumno));
            cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);

            cstmt.execute();
            RESP = cstmt.getString(2);
            bitacora.setInformacion("Eliminación Alumno: " + idAlumno + "||Respuesta Método: " + RESP);
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la eliminación de la materia: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error inesperado al realizar la eliminación de la materia: " + accion_catch(ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }

                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }

                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }

                if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CAlumnos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }
    
     public String Consultar_Carreras() throws SQLException {

        String RESP = "";
        Connection con = conexion.GetconexionInSite();
        Statement statement = con.createStatement();
        try {
            String Query = "SELECT * FROM Carrera";

            ResultSet rsRecord = statement.executeQuery(Query);
            List<Carrera> lstCarreraSelect = new ArrayList<Carrera>();
            while (rsRecord.next()) {
                Carrera carrera = new Carrera();
                carrera.setId_Carrera_Excel(rsRecord.getString("Id_Carrera_Excel"));
                carrera.setDescripcion(rsRecord.getString("Descripcion"));
                carrera.setID_Carrera(rsRecord.getString("ID_Carrera"));
                lstCarreraSelect.add(carrera);
            }
            RESP += "<option value = ''></option>";
            RESP = lstCarreraSelect.stream().map((car) -> "<option value='" + car.getID_Carrera() + "' id='"+car.getId_Carrera_Excel()+"'>" + car.getId_Carrera_Excel()+ " - "+ car.getDescripcion() + "</option>").reduce(RESP, String::concat);

        } catch (Exception e) {
            RESP = accion_catch(e);
            return "exceptionTry|" + RESP;
        }
        return RESP;

    }
     
     public String Consultar_Calificaciones() throws SQLException {

        String RESP = "NO";
        Connection con = conexion.GetconexionInSite();
        Statement statement = con.createStatement();
        String ID_Alumno = request.getParameter("ID_Alumno");
        String ID_Carrera = request.getParameter("ID_Carrera");
        try {
            //String Query = "SELECT * FROM Calificaciones WHERE ID_Alumno="+ID_Alumno+";";
            String Query = "SELECT * FROM Calificaciones AS C\n" +
                           "INNER JOIN Materias AS M ON M.ID_Materia=C.ID_Materia\n" +
                           "INNER JOIN Alumnos AS A ON A.ID_Alumno=C.ID_Alumno\n" +
                           "INNER JOIN Carrera AS CA ON CA.ID_Carrera=A.ID_Carrera\n" +
                           "WHERE C.ID_Alumno="+ID_Alumno+" AND (SELECT ID_Curso FROM CURSO WHERE ID_Carrera="+ID_Carrera+")=M.ID_Curso;";
            ResultSet rsRecord = statement.executeQuery(Query);
            
            //Verifica si de la consulta devuelve CALIFICACIONES.
        
            if (rsRecord.next()) {
                return "existe";
            }
           
        } catch (Exception e) {
            RESP = accion_catch(e);
            return "exceptionTry|" + RESP;
        }
        return RESP;

    }
     
     public String AddAlumnos() throws SQLException, Exception {
         
        String Respuesta = "";
        Connection conn = conexion.GetconexionInSite();
        Statement statement = conn.createStatement();
        
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Alumnos");
        bitacora.setMovimiento("Inserción");
        
        try {
            
            //Recuperamos los parametros
            String txtMatriculaAlumno = request.getParameter("txtMatriculaAlumno");
            String txtNombreAlumno = request.getParameter("txtNombreAlumno");
            String txtApaternoAlumno = request.getParameter("txtApaternoAlumno");
            String txtAmaternoAlumno = request.getParameter("txtAmaternoAlumno");
            String txtCurpAlumno = request.getParameter("txtCurpAlumno");
            String txtGeneracionAlumno = request.getParameter("txtGeneracionAlumno");
            String txtFechaInicioCarrera = request.getParameter("txtFechaInicioCarrera");
            String txtFechaFinCarrera = request.getParameter("txtFechaFinCarrera");
            String txtCorreoAlumno = request.getParameter("txtCorreoAlumno");
            String lstSexoAlumno = request.getParameter("lstSexoAlumno");
            String lstCarreraAlumno = request.getParameter("lstCarreraAlumno");
            String txtFechaNac = request.getParameter("txtFechaNac");

            //revisar que EL ALUMNO ha registrar, exista ya en un registro:
            String query = "SELECT * FROM alumnos WHERE Matricula LIKE '"+txtMatriculaAlumno+"' AND ID_Carrera=" + lstCarreraAlumno + ";";
            ResultSet rsRecord = statement.executeQuery(query);
            if (rsRecord.next()) {
                return "existe";
            }
            
            //Generamos la consulta, además de pasarle los parametros
            String sql= "EXECUTE SP_Add_Alumno_Individual ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?;";
            CallableStatement cstmt = conn.prepareCall(sql);
            cstmt.setString(1, txtMatriculaAlumno);
            cstmt.setString(2, txtNombreAlumno);
            cstmt.setString(3, txtApaternoAlumno);
            cstmt.setString(4, txtAmaternoAlumno);
            cstmt.setString(5, txtCurpAlumno);
            cstmt.setString(6, txtGeneracionAlumno);
            cstmt.setString(7, lstCarreraAlumno);
            cstmt.setString(8, lstSexoAlumno);
            cstmt.setString(9, txtFechaInicioCarrera);
            cstmt.setString(10, txtFechaFinCarrera);
            cstmt.setString(11, txtFechaNac+" 00:00:00.000");
            cstmt.setString(12, txtCorreoAlumno);
            cstmt.registerOutParameter(13, java.sql.Types.VARCHAR);

            cstmt.executeUpdate();
            
                Respuesta = cstmt.getString(13);
          
                //si la respuesta contiene la palabra success entonces esta se igualara a success
                //pero si no lo tiene, la igualaremos a la excepcion que nos arroja el sql
                if (!Respuesta.contains("success")) {
                    throw new SQLException(Respuesta);
                } else {
                    Respuesta = "success";
                    bitacora.setInformacion("Registro Alumnos: " + txtApaternoAlumno + " " + txtAmaternoAlumno +" "+ txtNombreAlumno + "||Matricula: " + txtMatriculaAlumno
                                            + "||CURP: "+txtCurpAlumno+ "||Correo: "+txtCorreoAlumno);
                                        cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral();
                }
            

        } catch (Exception e) {
            Respuesta = accion_catch(e);
            return "exceptionTry|" + Respuesta;
        } finally {
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }          
        }
        return Respuesta;
    }
     
      public String UpdateAlumnos() throws SQLException, Exception {
          
        String Respuesta = "";
        Connection conn = conexion.GetconexionInSite();
        Statement statement = conn.createStatement();
        
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Alumnos");
        bitacora.setMovimiento("Actualizar");
        
        try {
            
            //Recuperamos los parametros
            String txtMatriculaAlumno = request.getParameter("txtMatriculaAlumno");
            String txtNombreAlumno = request.getParameter("txtNombreAlumno");
            String txtApaternoAlumno = request.getParameter("txtApaternoAlumno");
            String txtAmaternoAlumno = request.getParameter("txtAmaternoAlumno");
            String txtCurpAlumno = request.getParameter("txtCurpAlumno");
            String txtGeneracionAlumno = request.getParameter("txtGeneracionAlumno");
            String txtFechaInicioCarrera = request.getParameter("txtFechaInicioCarrera");
            String txtFechaFinCarrera = request.getParameter("txtFechaFinCarrera");
            String txtCorreoAlumno = request.getParameter("txtCorreoAlumno");
            String lstSexoAlumno = request.getParameter("lstSexoAlumno");
            String lstCarreraAlumno = request.getParameter("lstCarreraAlumno");
            String txtFechaNac = request.getParameter("txtFechaNac");
            String ID_Alumno = request.getParameter("ID_Alumno");
        
            
            //revisar que EL ALUMNO ha registrar, exista ya en un registro:
            String query = "SELECT * FROM alumnos WHERE Matricula LIKE '"+txtMatriculaAlumno+"' AND ID_Carrera=" + lstCarreraAlumno + " AND ID_Alumno<>"+ID_Alumno+";";
            ResultSet rsRecord = statement.executeQuery(query);
            if (rsRecord.next()) {
                return "existe";
            }
            
            //Generamos la consulta, además de pasarle los parametros
            String sql= "EXECUTE SP_Update_Alumno_Individual ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?;";
            CallableStatement cstmt = conn.prepareCall(sql);
            cstmt.setString(1, txtMatriculaAlumno);
            cstmt.setString(2, txtNombreAlumno);
            cstmt.setString(3, txtApaternoAlumno);
            cstmt.setString(4, txtAmaternoAlumno);
            cstmt.setString(5, txtCurpAlumno);
            cstmt.setString(6, txtGeneracionAlumno);
            cstmt.setString(7, lstCarreraAlumno);
            cstmt.setString(8, lstSexoAlumno);
            cstmt.setString(9, txtFechaInicioCarrera);
            cstmt.setString(10, txtFechaFinCarrera);
            cstmt.setString(11, txtFechaNac+" 00:00:00.000");
            cstmt.setString(12, txtCorreoAlumno);
            cstmt.setString(13, ID_Alumno);
            cstmt.registerOutParameter(14, java.sql.Types.VARCHAR);

            cstmt.executeUpdate();

                Respuesta = cstmt.getString(14);
          
                //si la respuesta contiene la palabra success entonces esta se igualara a success
                //pero si no lo tiene, la igualaremos a la excepcion que nos arroja el sql
                if (!Respuesta.contains("success")) {
                    throw new SQLException(Respuesta);
                } else {
                    Respuesta = "success";
                    bitacora.setInformacion("Registro Alumnos: " + txtApaternoAlumno + " " + txtAmaternoAlumno +" "+ txtNombreAlumno + "||Matricula: " + txtMatriculaAlumno
                                            + "||CURP: "+txtCurpAlumno+ "||Correo: "+txtCorreoAlumno);
                                        cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral();
                }
            

        } catch (Exception e) {
            Respuesta = accion_catch(e);
            return "exceptionTry|" + Respuesta;
        } finally {
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        }
        return Respuesta;
    }


    private String accion_catch(Exception ex) {
        String resp = "";
        try {
            resp = ex.toString().replace("'", "\"");
            Logger.getLogger(CAlumnos.class.getName()).log(Level.SEVERE, null, ex);
            resp = "<h4 style='color:#e76d6d'>" + resp;
            resp += "</h4>";
        } catch (Exception e) {
            resp += "</h4><small class='text-primary'>No se ha insertado en Bitacora de errores</small>";
        }
        resp += "<br><br><small>Si continua con el problema, comuníquese con soporte técnico.</small> ";
        return resp;
    }
}