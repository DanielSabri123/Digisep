/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Carrera;
import com.ginndex.titulos.modelo.Materia;
import com.ginndex.titulos.modelo.TETitulosCarreras;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * @author Paulina
 */
public class CCarrerasCarga {

    HttpServletRequest request;
    HttpServletResponse response;
    CConexion conexion;
    PreparedStatement pstmt;
    CallableStatement cstmt;
    Connection con;
    String Bandera;
    String Id_Usuario;
    String rutaArchivo;
    String nombreArchivo;
    String NombreInstitucion;
    Workbook libro;
    ResultSet rs;
    private String permisos;
    Bitacora bitacora;
    CBitacora cBitacora;
    private final int FILAS_DESPUES_ENCABEZADOS = 20;

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

    public String getBandera() {
        return Bandera;
    }

    public void setBandera(String Bandera) {
        this.Bandera = Bandera;
    }

    public String getId_Usuario() {
        return Id_Usuario;
    }

    public void setId_Usuario(String Id_Usuario) {
        this.Id_Usuario = Id_Usuario;
    }

    public CCarrerasCarga() {
        this.Bandera = "";
    }

    public String EstablecerAcciones() throws UnsupportedEncodingException, FileUploadException, Exception {
        setBandera(request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
        HttpServletRequest requestProvisional = request;
        HttpSession sessionOk = request.getSession();
        requestProvisional.setCharacterEncoding("UTF-8");
        String RESP = "";
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();

        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        permisos = cPermisos.obtenerPermisos("Carreras");

        if (getBandera().equalsIgnoreCase("0")) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            ServletFileUpload up = new ServletFileUpload(factory);

            boolean isMultipart = ServletFileUpload.isMultipartContent(requestProvisional);
            if (isMultipart) {
                List<FileItem> partes = up.parseRequest(requestProvisional);
                setBandera(getDat(partes));
            }
        }

        if (getBandera().equalsIgnoreCase("excel")) {
            RESP = ImportarCarrerasExcel();
        } else if (getBandera().equalsIgnoreCase("1")) {
            RESP = CargarMaterias();
        } else if (getBandera().equalsIgnoreCase("2")) {
            RESP = EliminarMateria();
        } else if (getBandera().equalsIgnoreCase("3")) {
            RESP = cargarListaCarreras();
        }

        return RESP;
    }

    public String getDat(List<FileItem> partes) throws Exception {
        String Bandera = "";
        pstmt = null;
        con = null;
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
        } finally {
            con.close();
            rs.close();
            pstmt.close();
            conexion.GetconexionInSite().close();
        }

        //String ubicacionarchivo = System.getProperty("user.home") + "\\Downloads\\PruebasCarreras";
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

                    if (input_actual.equalsIgnoreCase("fileCarrera")) {

                        try {
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            rutaArchivo = ubicacionarchivo + "\\ArchivoCarrera_" + uploaded.getName();
                            nombreArchivo = "ArchivoCarrera_" + uploaded.getName();
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                nombreArchivo = "";
                            } else {
                                File archivoServer = new File(rutaArchivo);
                                uploaded.write(archivoServer);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, rutaArchivo);
                        }
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, "OCURRIÓ UN ERROR AL RECUPERAR DESDE EL FORM::---------------->", ex);
            }
        }
        return Bandera;
    }

    private String ImportarCarrerasExcel() throws SQLException {
        //String ruta = System.getProperty("user.home") + "\\Downloads\\PruebasCarreras\\" + nombreArchivo;
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + nombreArchivo;
        String RESP = "";
        String idCurso = "";
        FileInputStream archivo = null;
        cstmt = null;
        con = null;
        rs = null;
        conexion = new CConexion();
        conexion.setRequest(request);

        ArrayList<String[]> data;
        int hojas = 0;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Carga Carreras");
        bitacora.setMovimiento("Inserción");
        boolean isNotFound = false;
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
                int rows = hojaActual.getLastRowNum();
                Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, "FILAS LEÍDAS EN LA HOJA: " + (i + 1) + " ====== " + (rows + 1) + " =====");
                for (int j = 0; j <= rows; j++) {
                    String[] filaActual = new String[5];
                    Row rowActual = hojaActual.getRow(j);
                    if (j >= 4) {
                        int cols = 0;
                        try {
                            cols = rowActual.getLastCellNum();
                        } catch (NullPointerException ex) {
                            break;
                        }
                        for (int k = 0; k <= cols + 1; k++) {
                            Cell cellActual = rowActual.getCell(k);
                            if (k <= 4) {
                                if (cellActual == null) {
                                    filaActual[k] = "";
                                } else {
                                    if ((k == 1 && j == 14)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        sdf.setLenient(false);
                                        if (cellActual.getCellType() != Cell.CELL_TYPE_BLANK
                                                && cellActual.getCellType() != Cell.CELL_TYPE_BOOLEAN
                                                && cellActual.getCellType() != Cell.CELL_TYPE_ERROR
                                                && cellActual.getCellType() != Cell.CELL_TYPE_FORMULA) {
                                            String textoCelda = "";
                                            Date fecha = null;
                                            if (cellActual.getCellType() != Cell.CELL_TYPE_STRING) {
                                                fecha = cellActual.getDateCellValue();
                                            } else {
                                                textoCelda = cellActual.getStringCellValue();
                                                try {
                                                    fecha = sdf.parse(textoCelda);
                                                } catch (ParseException ex) {
                                                    RESP = "formatoInvalido||dateInvalid_" + ex.getMessage() + "||" + (i + 1);
                                                    return RESP;
                                                }
                                            }
                                            if (fecha != null) {
                                                filaActual[k] = sdf.format(fecha);
                                            } else {
                                                filaActual[k] = "";
                                            }
                                        } else {
                                            RESP = "formatoInvalido||dateInvalid_LA CELDA NO TIENE EL FORMATO CORRECTO||" + (i + 1);
                                            return RESP;
                                        }

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
                                        }
                                    }
                                }

                                if (j <= 22 && k == 1) {
                                    if (validarInfoCarrera(filaActual)) {
                                        data.add(filaActual);
                                        break;
                                    } else {
                                        RESP = "infoCarreraIncompleta||" + (i + 1);
                                        return RESP;
                                    }
                                } else if (k == 4) {
                                    boolean[] valido = validarInfoMaterias(filaActual);
                                    if (!valido[1]) {
                                        if (valido[0]) {
                                            data.add(filaActual);
                                            break;
                                        } else {
                                            System.out.println(filaActual[0]);
                                            System.out.println(filaActual[1]);
                                            RESP = "infoMateriaIncompleta||" + (i + 1);
                                            return RESP;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                // Terminamos de guardar los datos de la hoja en cuestión y agregamos los registros a la BD
                /*
                    Primero se agrega la carrera capturada, si ya existe en la BD, se recupera
                    su id y se guarda para agregar las materias, de lo contrario, se inserta la carrera
                    y se sigue el mismo proceso para insertar las materias
                 */
                if (!data.isEmpty()) {
                    if (data.get(0)[0].equalsIgnoreCase("* Id de Carrera")
                            && data.get(1)[0].equalsIgnoreCase("* Clave de Carrera")
                            && data.get(2)[0].equalsIgnoreCase("* Nombre Carrera")
                            && data.get(3)[0].equalsIgnoreCase("* Clave Institución")
                            && data.get(4)[0].equalsIgnoreCase("* Nombre Institución")
                            && data.get(5)[0].equalsIgnoreCase("* Clave Campus")
                            && data.get(6)[0].equalsIgnoreCase("* Campus")
                            && data.get(7)[0].equalsIgnoreCase("* Clave Plan de Carrera")
                            && data.get(8)[0].equalsIgnoreCase("* Autorización Reconocimiento")
                            && data.get(9)[0].equalsIgnoreCase("* RVOE")
                            && data.get(10)[0].equalsIgnoreCase("* Fecha de Expedición Rvoe")
                            && data.get(11)[0].equalsIgnoreCase("* Tipo de Periodo")
                            && data.get(12)[0].equalsIgnoreCase("* Nivel Educativo")
                            && data.get(13)[0].equalsIgnoreCase("* Total Materias")
                            && data.get(14)[0].equalsIgnoreCase("* Total Créditos")
                            && data.get(15)[0].equalsIgnoreCase("* Entidad Federativa")
                            && data.get(16)[0].equalsIgnoreCase("* Calificación Mínima")
                            && data.get(17)[0].equalsIgnoreCase("* Calificación Máxima")
                            && data.get(18)[0].equalsIgnoreCase("* Calificación Aprobatoria")) {
                        if (!data.get(0)[1].trim().equalsIgnoreCase("") && !data.get(1)[1].trim().equalsIgnoreCase("") && !data.get(2)[1].trim().equalsIgnoreCase("")
                                && !data.get(3)[1].trim().equalsIgnoreCase("") && !data.get(4)[1].trim().equalsIgnoreCase("") && !data.get(5)[1].trim().equalsIgnoreCase("")
                                && !data.get(6)[1].trim().equalsIgnoreCase("") && !data.get(7)[1].trim().equalsIgnoreCase("") && !data.get(8)[1].trim().equalsIgnoreCase("")
                                && !data.get(9)[1].trim().equalsIgnoreCase("") && !data.get(10)[1].trim().equalsIgnoreCase("") && !data.get(11)[1].trim().equalsIgnoreCase("")
                                && !data.get(12)[1].trim().equalsIgnoreCase("") && !data.get(13)[1].trim().equalsIgnoreCase("") && !data.get(14)[1].trim().equalsIgnoreCase("")
                                && !data.get(15)[1].trim().equalsIgnoreCase("") && !data.get(16)[1].trim().equalsIgnoreCase("")
                                && !data.get(17)[1].trim().equalsIgnoreCase("") && !data.get(18)[1].trim().equalsIgnoreCase("")) {
                            if (data.size() > FILAS_DESPUES_ENCABEZADOS) {
//                                for (int q = 0; q < 19; q++) {
//                                    System.out.println(data.get(q)[1].trim());
//                                }
//                            String QueryCarrera = "SET LANGUAGE 'español'; execute Add_Carrera_Excel "
//                                    + " '" + data.get(0)[1].trim() + "','" + data.get(1)[1] + "','" + data.get(2)[1].trim() + "','" + data.get(3)[1] + "',"
//                                    + " '" + data.get(4)[1].trim() + "','" + data.get(5)[1] + "',"
//                                    + " '" + data.get(6)[1] + "','" + data.get(7)[1] + "','" + data.get(9)[1] + "','" + data.get(10)[1]
//                                    + "'," + data.get(11)[1] + ",'" + data.get(12)[1] + "','" + data.get(8)[1] + "'";
                                /**
                                 * @idCarreraExcel varchar(50),
                                 * @claveCarrera varchar(50),
                                 * @nombreCarrera varchar(100),
                                 * @claveInstitucion varchar(50),
                                 * @nombreInstitucion varchar(100),
                                 * @claveCampus varchar(50),
                                 * @campus varchar(100),
                                 * @autorizacionReconocimiento varchar(50),
                                 * @rvoe varchar(50),
                                 * @tipoPeriodo varchar(50),
                                 * @nivelEducativo varchar(50),
                                 * @totalMaterias int,
                                 * @EntidadFederativa varchar(50),
                                 * @fechaExpedicionRvoe varchar(15),
                                 * @califMinima float(5),
                                 * @califMaxima float(5),
                                 * @califMinAprobatoria float(5),
                                 * @cvePlanCarrera varchar(30),
                                 * @totalCreditosMateria decimal(18,4),
                                 * @respuesta varchar(20) OUT
                                 */
                                String QueryCarrera = "{call Add_Carrera_Excel (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                                cstmt = con.prepareCall(QueryCarrera);
                                cstmt.setString(1, data.get(0)[1].trim());
                                System.out.println(data.get(0)[1].trim());
                                cstmt.setString(2, data.get(1)[1].trim());
                                System.out.println(data.get(1)[1].trim());
                                cstmt.setString(3, data.get(2)[1]);
                                System.out.println(data.get(2)[1].trim());
                                cstmt.setString(4, data.get(3)[1].trim());
                                System.out.println(data.get(3)[1].trim());
                                cstmt.setString(5, data.get(4)[1]);
                                System.out.println(data.get(4)[1].trim());
                                cstmt.setString(6, data.get(5)[1].trim());
                                System.out.println(data.get(5)[1].trim());
                                cstmt.setString(7, data.get(6)[1]);
                                System.out.println(data.get(6)[1].trim());
                                cstmt.setString(8, data.get(8)[1]);
                                System.out.println(data.get(8)[1].trim());
                                cstmt.setString(9, data.get(9)[1]);
                                System.out.println(data.get(9)[1].trim());
                                cstmt.setString(10, data.get(11)[1]);
                                System.out.println(data.get(11)[1].trim());
                                cstmt.setString(11, data.get(12)[1]);
                                System.out.println(data.get(12)[1].trim());
                                cstmt.setInt(12, Integer.valueOf(data.get(13)[1]));
                                System.out.println(data.get(13)[1].trim());
                                cstmt.setString(13, data.get(15)[1]);
                                System.out.println(data.get(15)[1].trim());
                                cstmt.setString(14, data.get(10)[1]);
                                System.out.println(data.get(10)[1].trim());
                                cstmt.setFloat(15, Float.valueOf(data.get(16)[1]));//CalifMinima
                                System.out.println(data.get(16)[1].trim());
                                cstmt.setFloat(16, Float.valueOf(data.get(17)[1]));//CalifMaxima
                                System.out.println(data.get(17)[1].trim());
                                cstmt.setFloat(17, Float.valueOf(data.get(18)[1]));//califiMinApro
                                System.out.println(data.get(18)[1].trim());
                                cstmt.setString(18, data.get(7)[1]);//CvePlanCarrera
                                System.out.println(data.get(7)[1].trim());
                                cstmt.setFloat(19, Float.valueOf(data.get(14)[1]));//TotalCreditos
                                System.out.println(data.get(14)[1].trim());
                                cstmt.registerOutParameter(20, java.sql.Types.VARCHAR);
                                cstmt.execute();
                                if ((cstmt.getUpdateCount() == -1) && (cstmt.getResultSet() == null)) {
                                    bitacora.setInformacion("Registro Carreras vía Archivo Excel: " + nombreArchivo + "||Hoja: " + i +
                                                            "||Con ID: "+data.get(0)[1].trim()+
                                                             "||Con Nombre: "+data.get(2)[1].trim()+
                                                            "||se registraron un total de materias: "+(data.size()-FILAS_DESPUES_ENCABEZADOS));
                                    cBitacora = new CBitacora(bitacora);
                                    cBitacora.setRequest(request);
                                    cBitacora.addBitacoraGeneral();
                                    idCurso = cstmt.getString(20);
                                    Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, "ID_Curso:------>{0}", idCurso);
                                    // Se insertar las materias capturadas en el documento
                                    for (int j = FILAS_DESPUES_ENCABEZADOS; j < data.size(); j++) {
                                        //String Query = "SET LANGUAGE 'español'; execute Add_Materia_Excel " + idCurso //                                        + ",'" + data.get(j)[0] + "','" + data.get(j)[1] + "','" + data.get(j)[2] + "'";
                                        String QueryMateria = "{call Add_Materia_Excel (?,?,?,?,?,?)}";
                                        int idTipoAsignatura = 0;
                                        switch (data.get(j)[4]) {
                                            case "OBLIGATORIA":
                                                idTipoAsignatura = 263;
                                                break;
                                            case "OPTATIVA":
                                                idTipoAsignatura = 264;
                                                break;
                                            case "ADICIONAL":
                                                idTipoAsignatura = 265;
                                                break;
                                            case "COMPLEMENTARIA":
                                                idTipoAsignatura = 266;
                                                break;
                                            case "":
                                                idTipoAsignatura = 263;//Si se deja en blanco la llenamos como obligatoria
                                                break;
                                            default:
                                                idTipoAsignatura = 263;//Si se deja en blanco la llenamos como obligatoria
                                                break;
                                        }
                                        rs = null;
                                        cstmt = con.prepareCall(QueryMateria);
                                        cstmt.setInt(1, Integer.valueOf(idCurso.trim()));
                                        cstmt.setInt(2, Integer.valueOf(data.get(j)[0]));
                                        cstmt.setString(3, data.get(j)[1].trim());
                                        cstmt.setString(4, data.get(j)[2].trim());
                                        cstmt.setFloat(5, Float.valueOf(data.get(j)[3]));
                                        cstmt.setInt(6, idTipoAsignatura);
//                                    System.out.println(Integer.valueOf(idCurso.trim()) + "-" + Integer.valueOf(data.get(j)[0]) + "-" + data.get(j)[1] + "-" + data.get(j)[2] + "-" + Float.valueOf(data.get(j)[3]) + "-" + idTipoAsignatura);
//                                    System.out.println("ID CARRERA EXCEL: " + data.get(0)[1].trim());
                                        cstmt.executeUpdate();
                                        if (cstmt.getUpdateCount() == -1 && cstmt.getResultSet() == null) {
                                            RESP = "success";
                                        } else {
                                            rs = cstmt.getResultSet();
                                            RESP = "error"
                                                    + "<p>Error al insertar la materia: <b>" + data.get(j)[2] + "</b>.</p><br>"
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
                                            + "<p>Error al insertar la carrera: <b>" + data.get(2)[1] + "</b>.</p><br>"
                                            + "<small>El servidor devolvió el siguiente mensaje de error desde SQL: <b>" + rs.getString("ErrorMessage") + "</b></small>"
                                            + "<br><small>Los registros posteriores no fueron insertados.</small>"
                                            + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
                                }
                            } else {
                                RESP = "sinMaterias";
                                break;
                            }
                        } else {
                            RESP = "infoIncompleta";
                            break;
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
            System.out.println(cstmt.getMoreResults());
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
                if (conexion.GetconexionInSite() != null && !conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String CargarMaterias() {
        StringBuilder RESP = new StringBuilder();
        String claves = "";
        Materia materia;
        Carrera carrera;
        List<Materia> lstMaterias;
        List<Carrera> lstCarreras;
        List<String[]> lstTipoPeriodo;
        String[] tipoPeriodo;
        conexion = new CConexion();
        conexion.setRequest(request);
        cstmt = null;
        pstmt = null;
        con = null;
        rs = null;
        String idCarrera = (request.getParameter("idCarrera") == null ? "" : request.getParameter("idCarrera"));

        try {
            String Query = "SELECT M.ID_Materia, M.Clave AS ClaveMateria,M.Descripcion AS NombreMateria, "
                    + "CA.Descripcion AS NombreCarrera, CA.Id_Carrera_Excel AS ClaveCarrera,folio "
                    + "FROM Materias AS M "
                    + "JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso "
                    + "JOIN Carrera AS CA ON CA.ID_Carrera = CU.ID_Carrera ";
            Query = (!idCarrera.equalsIgnoreCase("todos") && !idCarrera.equalsIgnoreCase("") ? Query + " WHERE CA.Id_Carrera_Excel = ?" : Query);
            Query += "ORDER BY M.ID_Materia DESC;";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            if (!idCarrera.equalsIgnoreCase("todos") && !idCarrera.equalsIgnoreCase("")) {
                pstmt.setString(1, idCarrera);
            }
            rs = pstmt.executeQuery();
            lstMaterias = new ArrayList<>();
            lstCarreras = new ArrayList<>();
            while (rs.next()) {
                materia = new Materia();
                carrera = new Carrera();

                materia.setID_Materia(rs.getString("ID_Materia"));
                materia.setClave(rs.getString("ClaveMateria"));
                materia.setDescripcion(rs.getString("NombreMateria"));
                materia.setFolio(rs.getString("folio"));
                carrera.setDescripcion(rs.getString("NombreCarrera"));
                carrera.setClave(rs.getString("ClaveCarrera"));
                claves += "'" + carrera.getClave() + "',";
                lstMaterias.add(materia);
                lstCarreras.add(carrera);
            }

            claves = (claves.length() != 0 ? claves.substring(0, claves.length() - 1) : "''");
            Query = "SELECT Id_Carrera_Excel, id_TipoPeriodo FROM TETitulosCarreras WHERE Id_Carrera_Excel IN (" + claves + ");";
            rs = null;
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstTipoPeriodo = new ArrayList<>();
            while (rs.next()) {
                tipoPeriodo = new String[2];
                tipoPeriodo[0] = rs.getString("Id_Carrera_Excel");
                tipoPeriodo[1] = rs.getString("id_TipoPeriodo");
                lstTipoPeriodo.add(tipoPeriodo);
            }

            for (int i = 0; i < lstCarreras.size(); i++) {
                for (int j = 0; j < lstTipoPeriodo.size(); j++) {
                    if (lstCarreras.get(i).getClave().equalsIgnoreCase(lstTipoPeriodo.get(j)[0])) {
                        String cTipoPeriodo = "";
                        switch (lstTipoPeriodo.get(j)[1]) {
                            case "91":
                                cTipoPeriodo = "SEMESTRE";
                                break;
                            case "92":
                                cTipoPeriodo = "BIMESTRE";
                                break;
                            case "93":
                                cTipoPeriodo = "CUATRIMESTRE";
                                break;
                            case "94":
                                cTipoPeriodo = "TETRAMESTRE";
                                break;
                            case "260":
                                cTipoPeriodo = "TRIMESTRE";
                                break;
                            case "261":
                                cTipoPeriodo = "MODULAR";
                                break;
                            case "262":
                                cTipoPeriodo = "ANUAL";
                                break;
                        }
                        lstCarreras.get(i).setArea(cTipoPeriodo);
                        break;
                    }
                }
            }

            RESP.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblCarreras'>"
                    + "  <thead class='bg-primary-dark' style='color: white;'>"
                    + "     <tr>"
                    + "         <th class='text-center' style='display:none;'>IdMateria</th>"
                    + "         <th class='text-center'>Clave</th>"
                    + "         <th class='text-center hidden-xs'>Folio Ord.</th>"
                    + "         <th class='text-center'>Materia</th>"
                    + "         <th class='text-center hidden-xs hidden-sm hidden-md'>Carrera</th>"
                    + "         <th class='text-center hidden-xs hidden-sm hidden-md'>Periodo</th>"
                    + "         <th class='text-center'>Acciones</th>"
                    + "     </tr>"
                    + "  </thead>"
                    + "  <tbody>");
            for (int i = 0; i < lstMaterias.size(); i++) {
                RESP.append("<tr>");
                RESP.append("<td class='text-center' id='IdMateria_").append(lstMaterias.get(i).getID_Materia()).append("' style='display:none;'>").append(lstMaterias.get(i).getID_Materia()).append("</td>");
                RESP.append("<td class='text-center' id='ClaveMateria_").append(lstMaterias.get(i).getID_Materia()).append("'>").append(lstMaterias.get(i).getClave()).append("</td>");
                RESP.append("<td class='text-center hidden-xs' id='folio_").append(lstMaterias.get(i).getID_Materia()).append("'>").append(lstMaterias.get(i).getFolio()).append("</td>");
                RESP.append("<td data-container='body' data-toggle='tooltip' data-placement='top' title='").append(lstMaterias.get(i).getDescripcion()).append("' id='NombreMateria_").append(lstMaterias.get(i).getID_Materia()).append("'>").append(lstMaterias.get(i).getDescripcion()).append("</td>");
                RESP.append("<td class='hidden-xs hidden-sm hidden-md'data-container='body' data-toggle='tooltip' data-placement='top' title='").append(lstCarreras.get(i).getDescripcion()).append("' id='NombreCarrera_").append(lstMaterias.get(i).getID_Materia()).append("'>").append(lstCarreras.get(i).getDescripcion()).append("</td>");
                RESP.append("<td class='text-center hidden-xs hidden-sm hidden-md' id='TipoPeriodo_").append(lstMaterias.get(i).getID_Materia()).append("'>").append(lstCarreras.get(i).getArea()).append("</td>");
                RESP.append("<td class='text-center' id='Acciones_").append(lstMaterias.get(i).getID_Materia()).append("'>");
                if (permisos.contains("todos") || (permisos.contains("acceso") && permisos.split("°")[1].split("¬")[4].equalsIgnoreCase("1"))) {
                    RESP.append("<div class='btn-group'><button class='js-swal-confirm btn btn-default btn-xs btnEliminarMateria' data-container='body' type='button' data-toggle='tooltip' id='deleteMateria_").append(lstMaterias.get(i).getID_Materia()).append("' value='dltMateria_").append(lstMaterias.get(i).getID_Materia()).append("' data-original-title='Eliminar'><i class='fa fa-times'></i></button></div>");
                } else {
                    RESP.append("<span class='label label-danger'>No disponible</span>");
                }
                RESP.append("</td></tr>");
            }
            RESP.append("</tbody>"
                    + "</table>");
        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP.setLength(0);
            RESP.append("error|Error SQL al cargar la tabla de materias: ").append(accion_catch(ex));
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP.setLength(0);
            RESP.append("error|Error inesperado cargar la tabla de materias: ").append(accion_catch(ex));
        } finally {
            try {
                con.close();
                //rs.close();
                pstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP.toString();
    }

    private String EliminarMateria() {
        String RESP = "";
        String idMateria = request.getParameter("idMateria");
        String nombreMateria = request.getParameter("nombreMateria");
        String nombreCarrera = request.getParameter("nombreCarrera");
        conexion = new CConexion();
        conexion.setRequest(request);
        cstmt = null;
        con = null;
        rs = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Carreras");
        bitacora.setMovimiento("Eliminación");

        try {
            //String Query = "SET LANGUAGE 'español'; execute Delete_Materia " + idMateria;
            String Query = "{call Delt_Materia (?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idMateria));
            cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
            cstmt.execute();
            RESP = cstmt.getString(2);
            if (!RESP.contains("success")) {
                if (RESP.contains("Calificada")) {
                    RESP = "enUso";
                } else {
                    RESP = "error";
                }
            } else {
                bitacora.setInformacion("Eliminación Materia: " + idMateria + "||Con Nombre: " + nombreMateria + "||En la Carrera: " + nombreCarrera);
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            }
        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la eliminación de la materia: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error inesperado al realizar la eliminación de la materia: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                cstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private boolean validarInfoCarrera(String[] filaActual) {
        boolean valido = true;

        //for (int i = 0; i < filaActual.length; i++) {
        if (filaActual[1] == null || filaActual[1].equalsIgnoreCase("")) {
            valido = false;
            //break;
        }
        //}
        return valido;
    }

    private boolean[] validarInfoMaterias(String[] filaActual) {
        boolean valido = true;
        boolean filaVacia = false;
        boolean[] validaciones = new boolean[2];
        int contador = 0;
        for (int i = 0; i < filaActual.length; i++) {
            if ((filaActual[i] == null || filaActual[i].trim().equalsIgnoreCase("")) && i != 4) {
                valido = false;
                contador++;
            }
        }

        if (contador == filaActual.length - 1) {
            filaVacia = true;
        }

        validaciones[0] = valido;
        validaciones[1] = filaVacia;
        return validaciones;
    }

    private String cargarListaCarreras() {
        StringBuilder RESP = new StringBuilder();
        pstmt = null;
        con = null;
        rs = null;
        try {
            conexion = new CConexion();
            List<TETitulosCarreras> lstCarrera = new ArrayList<TETitulosCarreras>();
            String Query = "SELECT Id_Carrera_Excel, nombreCarrera,TipoPeriodo FROM TETitulosCarreras TC JOIN TETipoPeriodo TP ON TC.id_TipoPeriodo = TP.id_TipoPeriodo ORDER BY Id_Carrera_Excel";
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);

            rs = pstmt.executeQuery();

            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    TETitulosCarreras tc = new TETitulosCarreras();

                    tc.setId_Carrera_Excel(rs.getString(1));
                    tc.setNombreCarrera(rs.getString(2));
                    tc.setID_TipoPeriodo(rs.getString(3));

                    lstCarrera.add(tc);
                }
                RESP.append("success¬");
                RESP.append("<option value='todos'>Todas las carreras</option>");
                for (int i = 0; i < lstCarrera.size(); i++) {
                    RESP.append("<option value='")
                            .append(lstCarrera.get(i).getId_Carrera_Excel())
                            .append("'>")
                            .append(lstCarrera.get(i).getId_Carrera_Excel())
                            .append(" - ")
                            .append(lstCarrera.get(i).getNombreCarrera())
                            .append(" - ")
                            .append(lstCarrera.get(i).getID_TipoPeriodo())
                            .append("</option>");
                }
            } else {
                RESP.append("empty¬");
                RESP.append("<option value='empty'>No se encontraron carreras</option>");
            }

        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de carreras: " + accion_catch(ex);
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
