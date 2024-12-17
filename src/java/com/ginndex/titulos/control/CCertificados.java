// VERSION: DIGISEP GENERAL
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Alumno;
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Calificacion;
import com.ginndex.titulos.modelo.Carrera;
import com.ginndex.titulos.modelo.Materia;
import com.ginndex.titulos.modelo.Persona;
import com.ginndex.titulos.modelo.TECertificadoelectronico;
import com.ginndex.titulos.modelo.TEEntidad;
import com.ginndex.titulos.modelo.TEFirmante;
import com.ginndex.titulos.modelo.TETitulosCarreras;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.ssl.Base64;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Paola Alonso
 */
public class CCertificados {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String Bandera;
    private String Id_Usuario;
    private String rutaArchivo;
    private String nombreArchivo;
    private ResultSet rs;
    private String usuario;
    private CConexion conexion;
    Cxml_certificado_electronico certElec;
    private String permisos;
    private CClavesActivacion desencriptador;
    private TEFirmante firmante;
    private List<TEFirmante> lstFirmantes;
    Bitacora bitacora;
    CBitacora cBitacora;
    String NombreInstitucion;
    Workbook libro;
    private final int COLUMNAS_A_LEER_EXCEL = 8;
    private final int FILAS_DESPUES_ENCABEZADOS = 1;

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

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String EstablecerAcciones() throws UnsupportedEncodingException, SQLException, FileUploadException, Exception {
        String RESP = "";

        setBandera(request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");

        HttpSession sessionOk = request.getSession();
        usuario = sessionOk.getAttribute("NombreUsuario").toString();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();

        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        permisos = cPermisos.obtenerPermisos("Certificados");

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

        switch (Bandera) {
            case "1":
                RESP = cargarListaCarreras() + "~" + cargarEntidadFederativa() + "~"
                        + cargarFirmantes() + "~" + cargarNotifyFirmantes();
                break;
            case "2":
                RESP = addCertificado();
                break;
            case "3":
                RESP = updateCertificado();
                break;
            case "4":
                RESP = deleteCertificado();
                break;
            case "5":
                RESP = consultarInfoCertificado();
                break;
            case "6":
                RESP = consultarAlumno();
                break;
            case "7":
                RESP = cargarInfoMaterias();
                break;
            case "8":
                RESP = crearCadenaXML();
                break;
            case "9":
                RESP = descargarXML();
                break;
            case "10":
                RESP = descargarXmlMasivos();
                break;
            case "11":
                RESP = crearCadenaXMLMasivo();
                break;
            case "12":
                RESP = cargarCertificados();
                break;
            case "excel":
                RESP = ImportarCertificadosExcel();
                break;
        }

        return RESP;
    }

    public String getDat(List<FileItem> partes) throws Exception {

        llenarNombreInstitucion();
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

                    if (input_actual.equalsIgnoreCase("fileCertificados")) {

                        try {
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            rutaArchivo = ubicacionarchivo + "\\ArchivoCertificado_" + uploaded.getName();
                            nombreArchivo = "ArchivoCertificado_" + uploaded.getName();
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                nombreArchivo = "";
                            } else {
                                File archivoServer = new File(rutaArchivo);
                                uploaded.write(archivoServer);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Logger.getLogger(CCertificados.class.getName()).log(Level.INFO, rutaArchivo);
                        }
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "OCURRIÓ UN ERROR AL RECUPERAR DESDE EL FORM::---------------->", ex);
            }
        }
        return Bandera;
    }

    private String ImportarCertificadosExcel() throws SQLException {
        //String ruta = System.getProperty("user.home") + "\\Downloads\\PruebasCarreras\\" + nombreArchivo;
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + nombreArchivo;
        String RESP = "";
        String cadenaIdCertificados = "";
        FileInputStream archivo = null;
        CallableStatement cstmt = null;
        Connection con = null;
        rs = null;
        conexion = new CConexion();
        conexion.setRequest(request);

        ArrayList<String[]> data;
        int hojas = 0;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Carga Certificados Masivos");
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
                Logger.getLogger(CCertificados.class.getName()).log(Level.INFO, "FILAS LEÍDAS EN LA HOJA: " + (i + 1) + " ====== " + (rows + 1) + " =====");
                for (int j = 0; j <= rows; j++) {
                    String[] filaActual = new String[COLUMNAS_A_LEER_EXCEL];
                    Row rowActual = hojaActual.getRow(j);
                    if (j >= 3) {
                        for (int k = 0; k < COLUMNAS_A_LEER_EXCEL + 1; k++) {
                            Cell cellActual = rowActual.getCell(k);
                            if (cellActual == null) {
                                filaActual[k] = "";
                            } else {
                                if (k == 5 && j >= 4) {
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
                                                RESP = "formatoInvalido||dateInvalid_<strong>" + ex.getMessage() + "<p>Hoja no. " + (i + 1) + "<p>Fila no. " + (j + 1) + "<strong>";
                                                return RESP;
                                            }
                                        }
                                        if (fecha != null) {
                                            filaActual[k] = sdf.format(fecha);
                                        } else {
                                            filaActual[k] = "";
                                        }
                                    } else {
                                        RESP = "formatoInvalido||dateInvalid_<strong>Celda con formato incorrecto o en blanco<p>Hoja no. " + (i + 1) + "<p>Fila no. " + (j + 1) + "<strong>";
                                        return RESP;

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
                                    }
                                }
                            }
                            if (k == COLUMNAS_A_LEER_EXCEL - 1) {
                                boolean[] bandera = validarInfoCertificado(filaActual);
                                if (!bandera[1]) {
                                    if (bandera[0]) {
                                        data.add(filaActual);
                                        break;
                                    } else {
                                        RESP = "infoCertificadoIncompleta||" + (i + 1);
                                        return RESP;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!data.isEmpty()) {
                    if (data.get(0)[0].equalsIgnoreCase("* Matrícula del alumno")
                            && data.get(0)[1].equalsIgnoreCase("* Id de Carrera")
                            && data.get(0)[2].equalsIgnoreCase("Nombre Carrera")
                            && data.get(0)[3].equalsIgnoreCase("Nombre del Alumno")
                            && data.get(0)[4].equalsIgnoreCase("* Lugar de Expedición")
                            && data.get(0)[5].equalsIgnoreCase("* Fecha Expedición")
                            && data.get(0)[6].equalsIgnoreCase("* CURP Firmante")
                            && data.get(0)[7].equalsIgnoreCase("Nombre Firmante")) {
                        if (data.size() > FILAS_DESPUES_ENCABEZADOS) {
                            for (int q = FILAS_DESPUES_ENCABEZADOS; q < data.size(); q++) {
                                String cadenaFirmantes = validarFirmantes(data.get(q)[6], con);
                                if (cadenaFirmantes.equalsIgnoreCase("noFirmante")) {
                                    return cadenaFirmantes + "||" + (i + 1) + "||" + (q + 4);
                                }
                                Alumno a = new Alumno();
                                TETitulosCarreras ttc = new TETitulosCarreras();

                                TECertificadoelectronico tce = new TECertificadoelectronico();

                                a.setMatricula(data.get(q)[0]);
                                ttc.setId_Carrera_Excel(data.get(q)[1]);
                                ttc.setNombreCarrera(data.get(q)[2]);

                                tce.setID_LugarExpedicion(data.get(q)[4]);
                                tce.setFechaExpedicion(data.get(q)[5]);
                                tce.setID_Firmante(data.get(q)[7]);
                                tce.setVersion("3.0");

                                tce.setAlumno(a);

                                String datosFaltantes = consultarInfoCertificadoImportacionExcel(tce.getAlumno().getMatricula(), ttc.getId_Carrera_Excel(), tce.getID_LugarExpedicion());

                                if (!datosFaltantes.equalsIgnoreCase("noAlumno")
                                        && !datosFaltantes.equalsIgnoreCase("noCarrera")
                                        && !datosFaltantes.equalsIgnoreCase("sinMaterias")
                                        && !datosFaltantes.equalsIgnoreCase("SinCalificaciones")) {

                                    tce.setTotal(datosFaltantes.split("~")[0]);
                                    tce.setAsignadas(datosFaltantes.split("~")[1]);
                                    tce.setPromedio(datosFaltantes.split("~")[2]);
                                    tce.setID_TipoCertificado(datosFaltantes.split("~")[3]);
                                    tce.setTotalCreditos(datosFaltantes.split("~")[4]);
                                    tce.setCreditosObtenidos(datosFaltantes.split("~")[5]);
                                    tce.getAlumno().setId_Alumno(datosFaltantes.split("~")[6]);
                                    tce.setID_Carrera(datosFaltantes.split("~")[7]);
                                    tce.setID_LugarExpedicion(datosFaltantes.split("~")[8]);
                                    int numDecimales = (datosFaltantes.split("~")[9] == null || datosFaltantes.split("~")[9].equalsIgnoreCase("null") ? 2 : Integer.parseInt(datosFaltantes.split("~")[9]));
                                    String Query = "{call Add_CertificadoElectronico (?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                                    con = conexion.GetconexionInSite();
                                    cstmt = con.prepareCall(Query);
                                    cstmt.setString(1, "3.0");
                                    cstmt.setInt(2, Integer.valueOf(tce.getAlumno().getId_Alumno()));
                                    cstmt.setInt(3, Integer.valueOf(tce.getID_Carrera()));
                                    cstmt.setInt(4, Integer.valueOf(cadenaFirmantes));
                                    cstmt.setInt(5, Integer.valueOf(tce.getID_TipoCertificado()));
                                    cstmt.setString(6, tce.getFechaExpedicion());
                                    cstmt.setString(7, tce.getID_LugarExpedicion());
                                    cstmt.setInt(8, Integer.valueOf(tce.getTotal()));
                                    cstmt.setInt(9, Integer.valueOf(tce.getAsignadas()));
                                    cstmt.setFloat(10, Float.valueOf(tce.getPromedio().replace(",", ".")));
                                    cstmt.setInt(11, Integer.valueOf(Id_Usuario));
                                    float crediTotal = Float.valueOf(tce.getTotalCreditos());
                                    cstmt.setString(12, String.format(Locale.US, "%." + numDecimales + "f", crediTotal));
                                    float crediObtain = Float.valueOf(tce.getCreditosObtenidos());
                                    cstmt.setString(13, String.format(Locale.US, "%." + numDecimales + "f", crediObtain));
                                    cstmt.registerOutParameter(14, java.sql.Types.VARCHAR);
                                    cstmt.execute();

                                    RESP = cstmt.getString(14);
                                    if (RESP.contains("success")) {
                                        bitacora.setInformacion("Registro de Certificado||Respuesta Método: " + RESP);
                                        cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral();
                                        String idCertificado = RESP.split("\\|\\|")[1];
                                        cadenaIdCertificados += idCertificado + ",";
                                        RESP = "success";
                                    }
                                } else {
                                    return datosFaltantes + "||" + (i + 1) + "||" + (q + 4);
                                }
                            }
                        } else {
                            RESP = "sinRegistros" + "||" + (i + 1);
                            cadenaIdCertificados = "";
                            break;
                        }
                    } else {
                        RESP = "formatoInvalido";
                        cadenaIdCertificados = "";
                        break;
                    }
                } else {
                    RESP = "formatoInvalido";
                    cadenaIdCertificados = "";
                    break;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.INFO, ex.getMessage());
            RESP = "error|Error FileNotFoundException al realizar la lectura del archivo cargado: " + accion_catch(ex);
            isNotFound = true;
        } catch (SQLException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error SQL al realizar la lectura del archivo cargado: " + accion_catch(ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error InvalidFormatException. El formato no corresponde con el formato: " + accion_catch(ex);
        } catch (IOException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error IOException al realizar la lectura del archivo cargado: " + accion_catch(ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "error|Error EncryptedDocumentException al realizar la lectura del archivo cargado: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP + (!cadenaIdCertificados.trim().equalsIgnoreCase("") ? "||" + cadenaIdCertificados.trim() : "");
    }

    private String cargarListaCarreras() {
        StringBuilder RESP = new StringBuilder();
        PreparedStatement pstmt = null;
        Connection con = null;
        rs = null;
        try {
            conexion = new CConexion();
            List<TETitulosCarreras> lstCarrera = new ArrayList<TETitulosCarreras>();
            String Query = "SELECT Id_Carrera_Excel, nombreCarrera,TipoPeriodo,Id_Carrera FROM TETitulosCarreras TC JOIN TETipoPeriodo TP ON TC.id_TipoPeriodo = TP.id_TipoPeriodo ORDER BY Id_Carrera_Excel";
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);

            rs = pstmt.executeQuery();

            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    TETitulosCarreras tc = new TETitulosCarreras();

                    tc.setID_Carrera(rs.getString(4));
                    tc.setId_Carrera_Excel(rs.getString(1));
                    tc.setNombreCarrera(rs.getString(2));
                    tc.setID_TipoPeriodo(rs.getString(3));

                    lstCarrera.add(tc);
                }
                RESP.append("success|");
                RESP.append("<option value='todos'>Todas las carreras</option>");
                for (int i = 0; i < lstCarrera.size(); i++) {
                    RESP.append("<option value='")
                            .append(lstCarrera.get(i).getID_Carrera())
                            .append("'>")
                            .append(lstCarrera.get(i).getId_Carrera_Excel())
                            .append(" - ")
                            .append(lstCarrera.get(i).getNombreCarrera())
                            .append(" - ")
                            .append(lstCarrera.get(i).getID_TipoPeriodo())
                            .append("</option>");
                }
            } else {
                RESP.append("empty|");
                RESP.append("<option value='empty'>No se encontraron carreras</option>");
            }

        } catch (SQLException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de carreras: " + accion_catch(ex);
        }
        return RESP.toString();
    }

    private String addCertificado() {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        String idCarrera = request.getParameter("idCarrera");
        String idProfesionista = request.getParameter("idProfesionista");
        String idLugarExpedicion = request.getParameter("idLugarExpedicion");
        String fechaExpedicion = request.getParameter("fechaExpedicion");
        String idFirmante = request.getParameter("idFirmante");
        String idTipoCertificado = request.getParameter("idTipoCertificado");
        String total = request.getParameter("total");
        String asignadas = request.getParameter("asignadas");
        String pasadas = request.getParameter("pasadas");
        String promedio = request.getParameter("promedio");
        String totalCreditos = request.getParameter("totalCreditos");
        String creditosObtenidos = request.getParameter("creditosObtenidos");
        int numDecimales = Integer.parseInt(request.getParameter("numDecimales") == null ? "2" : request.getParameter("numDecimales"));
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Certificados");
        bitacora.setMovimiento("Inserción");
        //Date horaServidor = new Date();
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        //fechaExpedicion += " " + sdf.format(horaServidor);
        try {
//            String Query = "SET LANGUAGE 'español'; execute Add_CertificadoElectronico '" + usuario + "','1.0'," + idProfesionista
//                    + "," + idCarrera + "," + idFirmante + "," + idTipoCertificado + ",'" + fechaExpedicion + "','" + fechaInicio + "','" + fechaFin + "','" + idLugarExpedicion + "'," + total + ","
//                    + asignadas + "," + promedio + "";
            String Query = "{call Add_CertificadoElectronico (?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setString(1, "3.0");
            cstmt.setInt(2, Integer.valueOf(idProfesionista));
            cstmt.setInt(3, Integer.valueOf(idCarrera));
            cstmt.setInt(4, Integer.valueOf(idFirmante));
            cstmt.setInt(5, Integer.valueOf(idTipoCertificado));
            cstmt.setString(6, fechaExpedicion);
            cstmt.setString(7, idLugarExpedicion);
            cstmt.setInt(8, Integer.valueOf(total));
            cstmt.setInt(9, Integer.valueOf(asignadas));
            cstmt.setFloat(10, Float.valueOf(promedio.replace(",", ".")));
            cstmt.setInt(11, Integer.valueOf(Id_Usuario));
            float crediTotal = Float.valueOf(totalCreditos);
            cstmt.setString(12, String.format(Locale.US, "%." + numDecimales + "f", crediTotal));
            float crediObtain = Float.valueOf(creditosObtenidos);
            cstmt.setString(13, String.format(Locale.US, "%." + numDecimales + "f", crediObtain));
            cstmt.registerOutParameter(14, java.sql.Types.VARCHAR);
            cstmt.execute();

            RESP = cstmt.getString(14);
            if (RESP.contains("success")) {
                bitacora.setInformacion("Registro de Certificado||Respuesta Método: " + RESP + "||"
                        + "3.0," + idProfesionista + "," + idCarrera + "," + idFirmante + ","
                        + idTipoCertificado + "," + fechaExpedicion + "," + idLugarExpedicion + "," + total + "," + asignadas + "," + promedio
                        + "," + Id_Usuario + "," + totalCreditos + "," + String.format(Locale.US, "%." + numDecimales + "f", crediTotal) + "," + String.format(Locale.US, "%." + numDecimales + "f", crediObtain));
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL insertar el certificado electrónico: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException insertar el certificado electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al insertar el certificado electrónico: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                cstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private String updateCertificado() {
        String RESP = "";
        String idCertificado = request.getParameter("idCertificado");
        String idProfesionista = request.getParameter("idProfesionista");
        String fechaExpedicion = request.getParameter("fechaExpedicion");
        String idLugarExpedicion = request.getParameter("idLugarExpedicion");
        String idFirmante = request.getParameter("idFirmante");
        String idTipoCertificado = request.getParameter("idTipoCertificado");
        String total = request.getParameter("total");
        String asignadas = request.getParameter("asignadas");
        String pasadas = request.getParameter("pasadas");
        String promedio = request.getParameter("promedio");
        String totalCreditos = request.getParameter("totalCreditos");
        String creditosObtenidos = request.getParameter("creditosObtenidos");
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Certificados");
        bitacora.setMovimiento("Actualización");

        //Date horaServidor = new Date();
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        //fechaExpedicion += " " + sdf.format(horaServidor);
        try {
//            String Query = "SET LANGUAGE 'español'; execute Upd_Certificado " + idCertificado + ",'" + idLugarExpedicion + "','"
//                    + fechaInicio + "','" + fechaFin + "'," + idFirmante + "," + total + "," + asignadas + "," + promedio + "," + idTipoCertificado + ","
//                    + idProfesionista + "";
            String Query = "{call Upd_Certificado(?,?,?,?,?,?,?,?,?,?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idCertificado));
            cstmt.setString(2, idLugarExpedicion);
            cstmt.setInt(3, Integer.valueOf(idFirmante));
            cstmt.setInt(4, Integer.valueOf(total));
            cstmt.setInt(5, Integer.valueOf(asignadas));
            cstmt.setFloat(6, Float.valueOf(promedio.replace(",", ".")));
            cstmt.setInt(7, Integer.valueOf(idTipoCertificado));
            cstmt.setInt(8, Integer.valueOf(idProfesionista));
            float crediTotal = Float.valueOf(totalCreditos);
            cstmt.setString(9, crediTotal + "");
            float crediObtain = Float.valueOf(creditosObtenidos);
            cstmt.setString(10, crediObtain + "");
            cstmt.setString(11, fechaExpedicion);

            cstmt.executeUpdate();

            if (cstmt.getResultSet() == null && cstmt.getUpdateCount() != -1) {
                RESP = "success";
                bitacora.setInformacion("Actualización de certificado||Respuesta Método: " + RESP
                        + "||" + idCertificado + "," + idLugarExpedicion + "," + idFirmante + "," + total + "," + asignadas + ","
                        + Float.valueOf(promedio.replace(",", ".")) + "," + idTipoCertificado + ","
                        + idProfesionista + "," + totalCreditos + "," + creditosObtenidos + "," + fechaExpedicion);
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else {
                RESP = "error";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL insertar el certificado electrónico: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException insertar el certificado electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al insertar el certificado electrónico: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                cstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String deleteCertificado() throws SQLException {
        String RESP = "";
        String idCertificado = request.getParameter("idCertificado");
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Certificados");
        bitacora.setMovimiento("Eliminación");

        try {
//            String Query = "SET LANGUAGE 'español'; execute Delt_Certificado " + idCertificado;
            String Query = "{call Delt_Certificado (?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idCertificado));

            cstmt.executeUpdate();

            if (cstmt.getResultSet() == null && cstmt.getUpdateCount() != -1) {
                RESP = "success";
                bitacora.setInformacion("Eliminación de certificado||Certificado:" + idCertificado + "||Respuesta Método: " + RESP);
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else {
                RESP = "error|Error al eliminar el certificado electrónico. El método no devolvió la respuesta esperada. <br><small>Presiona F5 para recargar la página y verificar si el certificado se elimino.</small>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la eliminación del certificado electrónico: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException al realizar la eliminación del certificado electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la eliminación del certificado electrónico: " + accion_catch(ex);
        } finally {
            con.close();
            cstmt.close();
            conexion.GetconexionInSite().close();
        }
        return RESP;
    }

    private String generarCertificadoXML(String idCertificado) throws SQLException {
        String RESP = "";
        String tmp = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        desencriptador = new CClavesActivacion();
        try {
//            String Query = "SET LANGUAGE 'español'; execute Gnrt_Certificado " + idCertificado;
            String Query = "{call Gnrt_Certificado (?,?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idCertificado));
            cstmt.setInt(2, Integer.valueOf(Id_Usuario));
            cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
            cstmt.execute();
            tmp = cstmt.getString(3);

            String[] split = tmp.split("~");
            int totalActual = desencriptador.leeClaveTimbre(split[0]);

            Query = "{call Usar_Timbre (?,?,?)}";
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(split[1]));
            cstmt.setString(2, desencriptador.GenerarClaveTimbres((totalActual - 1) + ""));
            cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
            cstmt.execute();
            RESP = cstmt.getString(3);

        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        } finally {
            con.close();
            cstmt.close();
            conexion.GetconexionInSite().close();
        }
        return RESP;
    }

    private String cargarCertificados() {
        StringBuilder resp = new StringBuilder();
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        TECertificadoelectronico certificado;
        Persona p;
        Alumno a;
        Carrera c;
        Date date;
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");

        List<TECertificadoelectronico> lstCertificados;

        try {
            String idCarrera = request.getParameter("idCarrera");
            String Query = "SELECT C.ID_Certificado, C.folioControl, C.estatus, A.Matricula, "
                    + " P.Nombre, P.APaterno, P.AMaterno, P.Curp, CA.Descripcion AS 'NombreCarrera', "
                    + " C.FechaExpedicion, E.EntidadFederativa, C.Total, C.Asignadas, C.Promedio, CA.Clave,C.IdTipoCertificado"
                    + " FROM TECertificadoElectronico AS C "
                    + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Profesionista "
                    + " JOIN Persona AS P ON P.ID_Persona = A.ID_Persona  "
                    + " JOIN TEEntidad AS E ON E.idEntidadFederativa = C.idLugarExpedicion "
                    + " JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera"
                    + (!idCarrera.equalsIgnoreCase("todos") ? (idCarrera.contains(",") ? " WHERE C.ID_Certificado in ( " + idCarrera.substring(0, idCarrera.length() - 1) + ")" : " WHERE C.Id_Carrera = " + idCarrera) : "");
            //System.out.println(Query);
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);

            rs = pstmt.executeQuery();
            lstCertificados = new ArrayList<>();
            while (rs.next()) {
                certificado = new TECertificadoelectronico();
                p = new Persona();
                a = new Alumno();
                c = new Carrera();

                certificado.setID_Certificado(rs.getString("ID_Certificado"));
                certificado.setFolioControl(rs.getString("folioControl"));
                certificado.setFechaExpedicion(rs.getString("FechaExpedicion"));
                certificado.setID_EntidadFederativa(rs.getString("EntidadFederativa"));
                certificado.setEstatus(rs.getString("estatus"));
                p.setNombre(rs.getString("Nombre"));
                p.setAPaterno(rs.getString("APaterno"));
                a.setMatricula(rs.getString("Matricula"));
                c.setDescripcion(rs.getString("NombreCarrera"));
                c.setClave(rs.getString("Clave"));
                a.setCarrera(c);
                a.setPersona(p);
                certificado.setAlumno(a);
                certificado.setID_TipoCertificado(rs.getString("IdTipoCertificado"));
                lstCertificados.add(certificado);
            }
            resp.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblCertificados'>");
            resp.append("<thead class='bg-primary-dark' style='color: white;'><tr>");
            resp.append("<th class='text-center' style='display:none;'>ID_Certificado</th>");
            resp.append("<th class='text-center hidden-sm hidden-xs'>Folio</th>");
            resp.append("<th class='text-center'>Matrícula</th>");
            resp.append("<th class='text-center'>Nombre(s)</th>");
            resp.append("<th class='text-center hidden-xs'>A. Paterno</th>");
            resp.append("<th class='text-center hidden-xs'>Carrera</th>");
            resp.append("<th class='text-center hidden-xs'>Expedición</th>");
            resp.append("<th class='text-center hidden-md hidden-sm hidden-xs'>Expedido en</th>");
            resp.append("<th class='text-center'>Acciones</th>");
            resp.append("<th class=' text-center bg-primary-dark' style='cursor: pointer; vertical-align:middle;'>");
            resp.append("<label class='css-input css-checkbox css-checkbox-sm css-checkbox-primary remove-margin-t remove-margin-b' style='margin-right: -15px;'>");
            resp.append("<input type='checkbox' class='chtodos'><span></span>");
            resp.append("</label></th></tr></thead>");
            resp.append("<tbody>");
            String cabecera = permisos.split("°")[0];
            if (!cabecera.equalsIgnoreCase("todos")) {
                permisos = permisos.split("°")[1];
            }

            for (int i = 0; i < lstCertificados.size(); i++) {
                String bandera = "";
                String anexoTabla = "";
                date = parseador.parse(lstCertificados.get(i).getFechaExpedicion());
                resp.append("<tr class='").append(lstCertificados.get(i).getID_TipoCertificado().equalsIgnoreCase("79") ? "success" : "danger").append(" '>");
                resp.append("<td class='text-center' style='display:none;' id='IDCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(lstCertificados.get(i).getID_Certificado()).append("</td>");
                resp.append("<td class='hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top' title='").append(lstCertificados.get(i).getFolioControl()).append("' id='FolioControl_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(lstCertificados.get(i).getFolioControl()).append("</td>");
                resp.append("<td id='Matricula_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(lstCertificados.get(i).getAlumno().getMatricula()).append("</td>");
                resp.append("<td id='Nombre_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(lstCertificados.get(i).getAlumno().getPersona().getNombre()).append("</td>");
                resp.append("<td class='hidden-xs' id='APaterno_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(lstCertificados.get(i).getAlumno().getPersona().getAPaterno()).append("</td>");
                resp.append("<td class='hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top' title='").append(lstCertificados.get(i).getAlumno().getCarrera().getDescripcion()).append("' id-carrera='").append(idCarrera).append("' id='Carrera_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(lstCertificados.get(i).getAlumno().getCarrera().getDescripcion()).append("</td>");
                resp.append("<td class='text-center hidden-xs' id='FechaExpedicion_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(formateador.format(date)).append("</td>");
                resp.append("<td data-container='body' data-toggle='tooltip' data-placement='top' title='").append(lstCertificados.get(i).getID_EntidadFederativa()).append("' class='hidden-md hidden-sm hidden-xs' id='LugarExpedicion_").append(lstCertificados.get(i).getID_Certificado()).append("'>").append(lstCertificados.get(i).getID_EntidadFederativa()).append("</td>");
                resp.append("<td class='text-center' id='Acciones_").append(lstCertificados.get(i).getID_Certificado()).append("'>");

                if (cabecera.contains("todos")) {
                    resp.append("<div class='btn-group'>");
                    resp.append("<button class='btn btn-default btn-xs btnConsultarCertificado' data-container='body' type='button' data-toggle='tooltip' id='consultarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='cstCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Consultar'><i class='fa fa-search'></i></button>");
                    if (lstCertificados.get(i).getEstatus().equalsIgnoreCase("0")) {
                        resp.append("<button class='btn btn-default btn-xs btnEditarCertificado' data-container='body' type='button' data-toggle='tooltip' id='editarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='edtCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Editar'><i class='fa fa-pencil'></i></button>");

                        anexoTabla = "<td class='text-center'>";
                        anexoTabla += "<div class='form-group'> ";
                        anexoTabla += "<div class='col-xs-12'>";
                        anexoTabla += "<label class='css-input css-checkbox css-checkbox-warning css-checkbos-sm remove-margin-t remove-margin-b'>";
                        anexoTabla += "<input class='cbxSelectDescarga cbxGenXml' type='checkbox' id='Cer_" + lstCertificados.get(i).getID_Certificado() + "'>" + "<span></span> </label>";
                        anexoTabla += "</div>";
                        anexoTabla += "</div>";
                        anexoTabla += "</td>";
                    } else {
                        resp.append("<button class='btn btn-default btn-xs btnDescargarXML' data-container='body' type='button' data-toggle='tooltip' id='descargarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='dowlCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Descargar'><i class='glyphicon glyphicon-download-alt'></i></button>");

                        anexoTabla = "<td class='text-center'>";
                        anexoTabla += "<div class='form-group'> ";
                        anexoTabla += "<div class='col-xs-12'>";
                        anexoTabla += "<label class='css-input css-checkbox css-checkbox-primary css-checkbos-sm remove-margin-t remove-margin-b'>";
                        anexoTabla += "<input class='cbxSelectDescarga cbxDescXml' type='checkbox' id='Cer_" + lstCertificados.get(i).getID_Certificado() + "'>" + "<span></span> </label>";
                        anexoTabla += "</div>";
                        anexoTabla += "</div>";
                        anexoTabla += "</td>";
                    }
                    resp.append("<button class='js-swal-confirm btn btn-default btn-xs btnCancelarCertificado' data-container='body' type='button' data-toggle='tooltip' id='eliminarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='dltCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Eliminar'><i class='fa fa-times'></i></button>");
                    resp.append("</div>");
                } else if (cabecera.contains("acceso")) {
                    if (permisos.split("¬")[2].equalsIgnoreCase("1") || permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[4].equalsIgnoreCase("1") || permisos.split("¬")[6].equalsIgnoreCase("1") || permisos.split("¬")[5].equalsIgnoreCase("1")) {
                        resp.append("<div class='btn-group'>");

                        if (permisos.split("¬")[6].equalsIgnoreCase("0") && lstCertificados.get(i).getEstatus().equalsIgnoreCase("1")) {
                            anexoTabla = "<td class='text-center'><span class='label label-danger'>Sin permiso</span></td>";
                        } else if (permisos.split("¬")[5].equalsIgnoreCase("0") && lstCertificados.get(i).getEstatus().equalsIgnoreCase("0")) {
                            anexoTabla = "<td class='text-center'><span class='label label-danger'>Sin permiso</span></td>";
                        }
                        //CONSULTAR
                        if (permisos.split("¬")[2].equalsIgnoreCase("1")) {
                            resp.append("<button class='btn btn-default btn-xs btnConsultarCertificado' data-container='body' type='button' data-toggle='tooltip' id='consultarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='cstCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Consultar'><i class='fa fa-search'></i></button>");
                            bandera += "1";
                        }
                        //ELIMINAR
                        if (permisos.split("¬")[4].equalsIgnoreCase("1")) {
                            resp.append("<button class='js-swal-confirm btn btn-default btn-xs btnCancelarCertificado' data-container='body' type='button' data-toggle='tooltip' id='eliminarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='dltCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Eliminar'><i class='fa fa-times'></i></button>");
                            bandera += "1";
                        }

                        //Permiso de Editar[3], Generar XML[5] y Descargar XML[6]
                        if (permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[6].equalsIgnoreCase("1") || permisos.split("¬")[5].equalsIgnoreCase("1")) {
                            if (lstCertificados.get(i).getEstatus().equalsIgnoreCase("0") && permisos.split("¬")[3].equalsIgnoreCase("1")) {
                                resp.append("<button class='btn btn-default btn-xs btnEditarCertificado' data-container='body' type='button' data-toggle='tooltip' id='editarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='edtCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Editar'><i class='fa fa-pencil'></i></button>");
                                bandera += "1";
                            } else if (permisos.split("¬")[6].equalsIgnoreCase("1") && lstCertificados.get(i).getEstatus().equalsIgnoreCase("1")) {
                                resp.append("<button class='btn btn-default btn-xs btnDescargarXML' data-container='body' type='button' data-toggle='tooltip' id='descargarCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' value='dowlCertificado_").append(lstCertificados.get(i).getID_Certificado()).append("' data-original-title='Descargar'><i class='glyphicon glyphicon-download-alt'></i></button>");
                                bandera += "1";
                                anexoTabla = "<td class='text-center'>";
                                anexoTabla += "<div class='form-group'> ";
                                anexoTabla += "<div class='col-xs-12'>";
                                anexoTabla += "<label class='css-input css-checkbox css-checkbox-primary css-checkbos-sm remove-margin-t remove-margin-b'>";
                                anexoTabla += "<input class='cbxSelectDescarga cbxDescXml' type='checkbox' id='Cer_" + lstCertificados.get(i).getID_Certificado() + "'>" + "<span></span> </label>";
                                anexoTabla += "</div>";
                                anexoTabla += "</div>";
                                anexoTabla += "</td>";
                            }
                            if (permisos.split("¬")[5].equalsIgnoreCase("1") && lstCertificados.get(i).getEstatus().equalsIgnoreCase("0")) {
                                anexoTabla = "<td class='text-center'>";
                                anexoTabla += "<div class='form-group'> ";
                                anexoTabla += "<div class='col-xs-12'>";
                                anexoTabla += "<label class='css-input css-checkbox css-checkbox-warning css-checkbos-sm remove-margin-t remove-margin-b'>";
                                anexoTabla += "<input class='cbxSelectDescarga cbxGenXml' type='checkbox' id='Cer_" + lstCertificados.get(i).getID_Certificado() + "'>" + "<span></span> </label>";
                                anexoTabla += "</div>";
                                anexoTabla += "</div>";
                                anexoTabla += "</td>";
                            }
                        }
                        if (bandera.equalsIgnoreCase("")) {
                            resp.append("<span class='label label-danger'>No disponible</span>");
                        }
                        resp.append("</div>");
                    } else {
                        resp.append("<span class='label label-danger'>No disponible</span>");
                        anexoTabla = "<td class='text-center'><span class='label label-danger'>Sin permiso</span></td>";
                    }
                }
                resp.append("</td>");
                resp.append(anexoTabla);
                resp.append("</tr>");
            }

            resp.append("</tbody></table>");

        } catch (SQLException ex) {
            resp = new StringBuilder("error|Error SQL al realizar la consulta de certificados electrónicos: " + accion_catch(ex));
        } catch (ParseException ex) {
            resp = new StringBuilder("error|Error ParseException al realizar la consulta de certificados electrónicos: " + accion_catch(ex));
        } catch (Exception ex) {
            resp = new StringBuilder("error|Error inesperado al realizar la consulta de certificados electrónicos: " + accion_catch(ex));
        } finally {
            try {
                con.close();
                pstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return resp.toString();
    }

    private String consultarAlumno() throws SQLException {
        String RESP = "";
        String matricula = request.getParameter("matricula");
        Alumno a;
        Persona p;
        Carrera c;
        List<Alumno> lstAlumnos;
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            String Query = "SELECT A.ID_Carrera, C.Descripcion, C.Id_Carrera_Excel, A.ID_Alumno, P.Nombre, P.APaterno, P.AMaterno "
                    + " FROM Alumnos AS A "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " JOIN Carrera AS C ON C.ID_Carrera = A.ID_Carrera "
                    + " WHERE A.Matricula LIKE '" + matricula + "' AND A.estatus = 1 ";
//                    + " AND A.ID_Alumno NOT IN ( "
//                    + "     SELECT AL.ID_Alumno "
//                    + "     FROM TECertificadoElectronico AS CE "
//                    + "     JOIN Alumnos AS AL ON AL.ID_Alumno = CE.id_profesionista "
//                    + "     JOIN TETitulosCarreras AS TC ON CE.id_carrera = TC.id_carrera "
//                    + "     WHERE AL.Matricula LIKE '" + matricula + "' "
//                    + ");";

            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstAlumnos = new ArrayList<>();
            while (rs.next()) {
                a = new Alumno();
                p = new Persona();
                c = new Carrera();

                a.setID_Carrera(rs.getString("ID_Carrera"));
                a.setId_Alumno(rs.getString("ID_Alumno"));
                p.setNombre(rs.getString("Nombre"));
                p.setAPaterno(rs.getString("APaterno"));
                p.setAMaterno(rs.getString("AMaterno"));
                c.setDescripcion(rs.getString("Descripcion"));
//                c.setClave(rs.getString("Clave"));//EN LUGAR DE LA CLAVE DE CARRERA VOY A GUARDAR EL ID_CARRERA_EXCEL
                c.setId_Carrera_Excel(rs.getString("Id_Carrera_Excel"));
                a.setPersona(p);
                a.setCarrera(c);
                lstAlumnos.add(a);
            }

            if (lstAlumnos.isEmpty()) {
                RESP = "sinCoincidencias";
//                Query = "SELECT C.id_carrera "
//                        + "     FROM TECertificadoElectronico AS C "
//                        + "     JOIN Alumnos AS A ON A.ID_Alumno = C.id_profesionista "
//                        + "     WHERE A.Matricula LIKE '" + matricula + "'";
//                rs = null;
//                pstmt = con.prepareStatement(Query);
//                rs = pstmt.executeQuery();
//                if (rs.next()) {
//                    RESP = "tieneCertificado";
//                }
            } else {
                if (lstAlumnos.size() == 1) {
                    RESP += "1||";
                } else {
                    RESP += "2||";
                    RESP += "<option></option>";
                }

                for (int i = 0; i < lstAlumnos.size(); i++) {
                    Query = "SELECT * FROM TETitulosCarreras WHERE Id_Carrera_Excel LIKE '" + lstAlumnos.get(i).getCarrera().getId_Carrera_Excel() + "'";
                    rs = null;
                    pstmt = con.prepareStatement(Query);
                    rs = pstmt.executeQuery();
                    rs.next();
                    RESP += "<option value=\"" + rs.getString("id_carrera") + "\" data-idAlumno=\"" + lstAlumnos.get(i).getId_Alumno() + "\" data-idCarrera=\"" + lstAlumnos.get(i).getID_Carrera() + "\" data-Nombre=\"" + lstAlumnos.get(i).getPersona().getNombre() + " " + lstAlumnos.get(i).getPersona().getAPaterno() + (lstAlumnos.get(i).getPersona().getAMaterno() == null ? "" : " " + lstAlumnos.get(i).getPersona().getAMaterno()) + "\">" + lstAlumnos.get(i).getCarrera().getDescripcion() + "</option>";
                }
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del alumno: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del alumno: " + accion_catch(ex);
        } finally {
            con.close();
            pstmt.close();
            conexion.GetconexionInSite().close();
        }
        return RESP;
    }

    private String consultarInfoCertificado() throws SQLException {
        String RESP = "";
        int materiasAprobadas = 0;
        float califAprobatoria = 0;
        String idCertificado = request.getParameter("idCertificado");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        Date d;
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            String Query = "SELECT CE.ID_Certificado,CE.folioControl,CE.id_profesionista,CE.id_carrera,CE.idFirmante, "
                    + " CE.idTipoCertificado,CE.fechaExpedicion,CE.idLugarExpedicion,CE.Total,CE.Asignadas,CE.Promedio, "
                    + " CE.estatus, CE.creditosTotal, CE.creditosObtenidos, "
                    + " A.Matricula, A.ID_Alumno, P.Nombre, P.APaterno, P.AMaterno, "
                    + " C.Descripcion,C.ID_Carrera AS 'CarreraLocal', txml.cadenaOriginal, ISNULL(numeroCiclos,0) numeroCiclos"
                    + " FROM TECertificadoElectronico AS CE "
                    + " JOIN Alumnos AS A ON A.ID_Alumno = CE.id_profesionista "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " JOIN Carrera AS C ON C.ID_Carrera = A.ID_Carrera "
                    + "	LEFT JOIN TExml as txml ON  CE.folioControl = txml.folioControl "
                    + " WHERE ID_Certificado = " + idCertificado;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                RESP += "~";
                RESP += rs.getString("Matricula") + "~";
                RESP += "<option value='" + rs.getString("ID_Carrera") + "' data-idAlumno='" + rs.getString("ID_Alumno") + "' data-idCarrera='" + rs.getString("CarreraLocal") + "'>" + rs.getString("Descripcion") + "</option>" + "~";
                RESP += rs.getString("Nombre") + " " + rs.getString("APaterno") + " " + (rs.getString("AMaterno") == null ? "" : rs.getString("AMaterno")) + "~";
                RESP += rs.getString("idLugarExpedicion") + "~";
                RESP += "0" + "~";
                RESP += "0" + "~";
                RESP += rs.getString("idFirmante") + "~";
                RESP += rs.getString("folioControl") + "~";
                d = parseador.parse(rs.getString("fechaExpedicion"));
                RESP += formateador.format(d) + "~";
                RESP += rs.getString("Total") + "~";
                RESP += rs.getString("Asignadas") + "~";
                RESP += rs.getString("Promedio") + "~";
                RESP += rs.getString("idTipoCertificado") + "~";
                RESP += rs.getString("ID_Certificado") + "~";
                RESP += rs.getString("estatus") + "~";
                RESP += rs.getString("ID_Alumno") + "~";
                RESP += rs.getString("creditosTotal") + "~";
                RESP += rs.getString("creditosObtenidos") + "~";
                RESP += (rs.getString("cadenaOriginal") == null ? "0" : rs.getString("cadenaOriginal")) + "~" + rs.getString("numeroCiclos") + "~";

                Query = "SELECT califMinimaAprobatoria FROM TETitulosCarreras ttc JOIN Carrera c ON ttc.Id_Carrera_Excel = c.Id_Carrera_Excel WHERE c.ID_Carrera = ?";
                //System.out.println(Query.replace("?", rs.getString("ID_Carrera")));
                pstmt = con.prepareStatement(Query);
                pstmt.setString(1, rs.getString("CarreraLocal"));
                ResultSet rset = pstmt.executeQuery();
                if (rset.next()) {
                    califAprobatoria = rset.getFloat(1);
                }

                Query = "SELECT C.ID_Alumno, C.ID_Grupo, C.Calificacion,'' AS CalificacionLetra,"
                        + " CONVERT (date, A.FechaInicioCarrera) AS FechaInicioCarrera, "
                        + " CONVERT (date, A.FechaFinCarrera) AS FechaFinCarrera, "
                        + " CA.TotalCreditos, M.Creditos, M.Tipo,c.ID_Materia"
                        + " FROM Calificaciones AS C "
                        + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno "
                        + " JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera "
                        + " JOIN Materias AS M ON M.ID_Materia = C.ID_Materia "
                        + " WHERE C.ID_Alumno = " + rs.getString("ID_Alumno") + " AND C.ID_Materia "
                        + " IN ("
                        + "  SELECT ID_Materia "
                        + "  FROM Materias AS M "
                        + "  JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso "
                        + "  WHERE CU.ID_Carrera = " + rs.getString("CarreraLocal")
                        + " )"
                        + "UNION\n"
                        + "SELECT C.ID_Alumno, C.ID_Grupo,0,C.CalificacionLetra, "
                        + "CONVERT (date, A.FechaInicioCarrera) AS FechaInicioCarrera,  CONVERT (date, A.FechaFinCarrera) AS FechaFinCarrera,  \n"
                        + "CA.TotalCreditos, M.Creditos, M.Tipo ,c.ID_Materia\n"
                        + "FROM Calificaciones_Letra AS C  \n"
                        + "JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno  \n"
                        + "JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera  \n"
                        + "JOIN Materias AS M ON M.ID_Materia = C.ID_Materia  \n"
                        + "WHERE C.ID_Alumno = " + rs.getString("ID_Alumno") + " AND C.ID_Materia  "
                        + "IN ("
                        + " SELECT ID_Materia   "
                        + " FROM Materias AS M   "
                        + " JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso   "
                        + " WHERE CU.ID_Carrera = " + rs.getString("CarreraLocal") + " "
                        + ");";
                rset = null;
                pstmt = con.prepareStatement(Query);
                rset = pstmt.executeQuery();
                while (rset.next()) {
                    String calLetra = rset.getString("CalificacionLetra");
                    float calif = rset.getFloat("Calificacion");
                    if ((calif >= califAprobatoria) || (calLetra.equalsIgnoreCase("AC") || calLetra.equalsIgnoreCase("ACREDITADA"))) {
                        materiasAprobadas++;
                    }
                }

                RESP += materiasAprobadas + "";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del título electrónico: " + accion_catch(ex);
        } catch (ParseException ex) {
            RESP = "error|Error ParseException al realizar la consulta del título electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del título electrónico: " + accion_catch(ex);
        } finally {
            con.close();
            pstmt.close();
            conexion.GetconexionInSite().close();
        }
        return RESP;
    }

    private String cargarEntidadFederativa() {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        TEEntidad entidad;
        List<TEEntidad> lstEntidades;
        try {
            String Query = "SELECT * FROM TEEntidad";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstEntidades = new ArrayList<>();
            while (rs.next()) {
                entidad = new TEEntidad();

                entidad.setID_EntidadFederativa(rs.getString("IdEntidadFederativa"));
                entidad.setEntidadFederativa(rs.getString("EntidadFederativa"));
                entidad.setAbreviatura(rs.getString("Abreviatura"));
                lstEntidades.add(entidad);
            }

            RESP += "<option></option>";

            for (int i = 0; i < lstEntidades.size(); i++) {
                RESP += "<option value='" + lstEntidades.get(i).getID_EntidadFederativa() + "'>" + lstEntidades.get(i).getEntidadFederativa() + "</option>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de entidades federativas: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de entidades federativas: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                pstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String cargarFirmantes() {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        TEFirmante firmante;
        List<TEFirmante> lstFirmantes;

        try {
            String Query = "SELECT ID_Firmante, Nombre, primerApellido, segundoApellido, Cargo \n"
                    + "FROM TEFirmantes tf JOIN TECargos tc ON tf.idCargo = tc.idCargo WHERE estatus = 1";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstFirmantes = new ArrayList<>();
            while (rs.next()) {
                firmante = new TEFirmante();

                firmante.setID_Firmante(rs.getString("ID_Firmante"));
                firmante.setNombre(rs.getString("Nombre"));
                firmante.setPrimerApellido(rs.getString("primerApellido"));
                firmante.setSegundoApellido(rs.getString("segundoApellido"));
                firmante.setCargo(rs.getString("Cargo"));
                lstFirmantes.add(firmante);
            }

            RESP += "<option></option>";
            for (int i = 0; i < lstFirmantes.size(); i++) {
                RESP += "<option value='" + lstFirmantes.get(i).getID_Firmante() + "'>" + lstFirmantes.get(i).getCargo() + " - " + lstFirmantes.get(i).getNombre() + " " + lstFirmantes.get(i).getPrimerApellido() + " " + lstFirmantes.get(i).getSegundoApellido() + "</option>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de firmantes: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de firmantes: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                pstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String cargarInfoMaterias() throws SQLException {
        String RESP = "";
        float califAprobatoria = 0;
        Alumno a = null;
        Materia m = null;
        String idCarrera = request.getParameter("idCarrera");
        String idAlumno = request.getParameter("idAlumno");
        String idTECarrera = request.getParameter("idTECarrera");
        int idCertificado = 0;
        String infoCertificado = "||";
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        int totalMaterias, materiasAsignadas = 0, materiasAprobadas = 0, materiasCalificadasProm = 0, numDecimales = 0;
        BigDecimal _promedioProv;
        BigDecimal _promedioReal;
        float promedio = 0;
        float creditosCarrera = 0;
        float creditosObtenidos = 0;
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            //Valida que no exista un titulo que no tenga un certificado
            String Query = "SELECT TOP 1 ID_Certificado,idFirmante,idLugarExpedicion, folioControl, estatus, "
                    + "CASE WHEN CE.Total = ce.Asignadas \n"
                    + "THEN '1'\n"
                    + "ELSE '0'\n"
                    + "END AS EsTotal\n"
                    + "FROM TECertificadoElectronico AS CE "
                    + "WHERE id_profesionista = " + idAlumno
                    + "AND id_carrera = " + idTECarrera
                    + " ORDER BY ID_Certificado DESC";
                    con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                idCertificado = rs.getInt(1);
                infoCertificado += rs.getString(2) + "||" + rs.getString(3) + "||" + rs.getString(4) + "||" + rs.getString(5) + "||" + rs.getString(6);
            }

            Query = "SELECT califMinimaAprobatoria FROM TETitulosCarreras ttc JOIN Carrera c ON ttc.Id_Carrera_Excel = c.Id_Carrera_Excel WHERE c.ID_Carrera = ?";
            //System.out.println(Query.replace("?", idTECarrera));
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idCarrera);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                califAprobatoria = rs.getFloat(1);
            }
            Query = "SELECT TotalMaterias,numDecimales FROM CARRERA WHERE ID_CARRERA = " + idCarrera;
//            Query = "SELECT COUNT(*) AS TotalMaterias "
//                    + " FROM Materias AS M "
//                    + " JOIN Curso AS C ON C.ID_Curso = M.ID_Curso "
//                    + " WHERE C.ID_Carrera = " + idCarrera + "AND Tipo <> 266 AND Tipo <> 0";
            rs = null;
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalMaterias = rs.getInt("TotalMaterias");
                numDecimales = rs.getInt("numDecimales");

                Query = "SELECT C.ID_Alumno, C.ID_Grupo, C.Calificacion as Calificacion,'' AS CalificacionLetra , "
                        + "CONVERT (date, A.FechaInicioCarrera) AS FechaInicioCarrera,  CONVERT (date, A.FechaFinCarrera) AS FechaFinCarrera,  \n"
                        + "CA.TotalCreditos, M.Creditos, M.Tipo ,c.ID_Materia\n"
                        + "FROM Calificaciones AS C  \n"
                        + "JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno  \n"
                        + "JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera  \n"
                        + "JOIN Materias AS M ON M.ID_Materia = C.ID_Materia  \n"
                        + "WHERE C.ID_Alumno = " + idAlumno + " AND C.ID_Materia  "
                        + "IN ("
                        + " SELECT ID_Materia   "
                        + " FROM Materias AS M   "
                        + " JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso   "
                        + " WHERE CU.ID_Carrera = " + idCarrera
                        + ")\n"
                        + "UNION\n"
                        + "SELECT C.ID_Alumno, C.ID_Grupo,0,C.CalificacionLetra, "
                        + "CONVERT (date, A.FechaInicioCarrera) AS FechaInicioCarrera,  CONVERT (date, A.FechaFinCarrera) AS FechaFinCarrera,  \n"
                        + "CA.TotalCreditos, M.Creditos, M.Tipo ,c.ID_Materia\n"
                        + "FROM Calificaciones_Letra AS C  \n"
                        + "JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno  \n"
                        + "JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera  \n"
                        + "JOIN Materias AS M ON M.ID_Materia = C.ID_Materia  \n"
                        + "WHERE C.ID_Alumno = " + idAlumno + " AND C.ID_Materia  "
                        + "IN ("
                        + " SELECT ID_Materia   "
                        + " FROM Materias AS M   "
                        + " JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso   "
                        + " WHERE CU.ID_Carrera = " + idCarrera + " "
                        + ");";
                rs = null;
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        String calLetra = rs.getString("CalificacionLetra");
                        a = new Alumno();
                        m = new Materia();
                        float calif = rs.getFloat("Calificacion");
                        //System.out.println(calif);

                        m.setCreditos(String.format(Locale.US, "%." + numDecimales + "f", new Float(rs.getString("Creditos"))));
                        m.setTipo(rs.getString("Tipo"));
                        creditosCarrera = rs.getFloat("TotalCreditos");
                        String creditosCarreraS = String.format(Locale.US, "%." + numDecimales + "f", creditosCarrera);
                        creditosCarrera = new Float(creditosCarreraS);

                        materiasAsignadas++;
                        if (!m.getTipo().equalsIgnoreCase("0") && !m.getTipo().equalsIgnoreCase("266")) {
                            promedio = Float.sum(promedio, calif);
                            //System.out.println("Promedio: " + promedio);
                            if (calLetra.equalsIgnoreCase("")) {//Es calificación númerica
                                materiasCalificadasProm++;
                            }
                            if ((calif >= califAprobatoria) || (calLetra.equalsIgnoreCase("AC") || calLetra.equalsIgnoreCase("ACREDITADA"))) {
                                creditosObtenidos += Float.valueOf(m.getCreditos());
                            }
                        }

                        if ((calif >= califAprobatoria) || (calLetra.equalsIgnoreCase("AC") || calLetra.equalsIgnoreCase("ACREDITADA"))) {
                            materiasAprobadas++;
                        }
                    }
                    /**
                     * Se deben usar BigDecimal para evitar la perdida de
                     * información al realizar las operaciones Es importante
                     * usar el constructor BigDecima(String _string) para que
                     * sea más preciso.
                     */
                    Query = "SELECT  CAST(SUM(CASE WHEN m.tipo <>266  THEN C.Calificacion ELSE 0 END) AS decimal(5,2)) promedio"
                            + " FROM Calificaciones AS C "
                            + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno "
                            + " JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera "
                            + " JOIN Materias AS M ON M.ID_Materia = C.ID_Materia "
                            + " WHERE C.ID_Alumno = " + idAlumno + " AND C.ID_Materia "
                            + " IN ("
                            + "  SELECT ID_Materia "
                            + "  FROM Materias AS M "
                            + "  JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso "
                            + "  WHERE CU.ID_Carrera = " + idCarrera
                            + " );";
                    pstmt = con.prepareStatement(Query);
                    rs = pstmt.executeQuery();
                    String _promedio = "";
                    if (rs.next()) {
                        _promedio = rs.getString("promedio");
                    }
                    _promedioProv = new BigDecimal(String.valueOf(_promedio));
                    _promedioReal = _promedioProv.divide(new BigDecimal(materiasCalificadasProm), 2, BigDecimal.ROUND_FLOOR);

                    //promedio = promedio / materiasCalificadasProm;
                    String promAuxiliar = String.valueOf(_promedioReal);

                    String creditosObtenidosS = String.format(Locale.US, "%." + numDecimales + "f", creditosObtenidos);//aplicado a los cambios de u del prado
                    creditosObtenidos = new Float(creditosObtenidosS);
                    double promedioProv;
                    //28 de Octubre 2020
                    //Existen valores que al realizar la operación * 100 pierde decimales y cambia completamente todo.
                    //Es por eso que vamos a guardar el valor en una cadena y verificar si existen solo dos decimales después del punto, si es así, omitimos el Math.floor

                    if (promAuxiliar.contains(".")) {
                        if (promAuxiliar.split("\\.")[1].length() <= 2) {
                            promedioProv = Double.parseDouble(promAuxiliar);
                        } else {
                            //AQUI NUNCA VA A ENTRAR POR QUE EL DIVIDE SIEMPRE DEVOLVERÁ 2.
                            //Se realiza una multiplicación por 100 y se toma el entero más cercano.
                            //Luego dividimos entre 100 para obtener un entero y dos decimales.
                            promedioProv = Math.floor(promedio * 100) / 100;
                        }
                    } else {
                        //NUNCA VA A ENTRAR POR QUE EL DIVIDE SIEMPRE PONE DOS DECIMALES.
                        //Cuando no contiene decimales
                        promedioProv = Math.floor(promedio * 100) / 100;
                    }

                    RESP += totalMaterias + "~";
                    RESP += materiasAsignadas + "~";
                    if (materiasAsignadas == 0) {
                        RESP += "0~";
                    } else {
//                        Se agrega el LOCALE.US por que si no se especifica regresa una , en lugar de un punto.
                        RESP += String.format(Locale.US, "%.2f", promedioProv) + "~";
                    }
                    RESP += materiasAprobadas + "~";

                    if (creditosCarrera == creditosObtenidos || creditosObtenidos > creditosCarrera) {
                        RESP += "79";
                    } else {
                        RESP += "80";
                    }
//                    if (totalMaterias == materiasAsignadas && totalMaterias == materiasAprobadas) {
//                        RESP += "79";
//                    } else {
//                        RESP += "80";
//                    }

                    RESP += "~~";
                    RESP += "~" + creditosCarrera + "~" + creditosObtenidos;
                    RESP += "~" + idCertificado + infoCertificado;

                    /* --------- Consultamos el número total de ciclos de acuerdo al MEC 3.0 --------- */
                    Query = "SELECT COUNT(numCiclos) AS numCiclos\n"
                            + "FROM (\n"
                            + "	SELECT Ciclo_Escolar as numCiclos FROM Materias AS M \n"
                            + "	JOIN Calificaciones AS C ON C.ID_Materia = M.ID_Materia \n"
                            + "	JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno\n"
                            + "	WHERE A.ID_Alumno = " + idAlumno + " AND C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera =  " + idCarrera + "))\n"
                            + "	UNION\n"
                            + "	SELECT Ciclo_Escolar as numCiclos FROM Materias AS M \n"
                            + "	JOIN Calificaciones_Letra AS C ON C.ID_Materia = M.ID_Materia \n"
                            + "	JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno\n"
                            + "	WHERE A.ID_Alumno = " + idAlumno + " AND C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera =  " + idCarrera + "))\n"
                            + ") AS TBL";
                    pstmt = con.prepareStatement(Query);
                    rs = pstmt.executeQuery();
                    String numCiclos = "";
                    if (rs.next()) {
                        numCiclos = rs.getString("numCiclos");
                    }
                    RESP += "~" + numCiclos + "~" + numDecimales;
                } else {
                    RESP = "SinCalificaciones";
                }
            } else {
                RESP = "SinMaterias";
            }
        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        } finally {
            con.close();
            pstmt.close();
            conexion.GetconexionInSite().close();
        }
        return RESP;
    }

    private String crearCadenaXML() {
        String RESP = "";
        String idCertificado = request.getParameter("idCertificado");
        String cdn_Dec = "";
        String cdn_Ipes = "";
        String cdn_Responsable = "";
        String cdn_Rvoe = "";
        String cdn_Carrera = "";
        String cdn_Alumno = "";
        String cdn_Expedicion = "";
        String cdn_Asignaturas = "";
        String cdn_Asignatura = "";
        String tmp = "";
        String cveActivacion = "";
        String fechaVencimiento = "";
        String configCalif = "";
        int timbresPasados = 0, numDecimales = 2;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date hoy = new Date();
        TECertificadoelectronico ce;
        TETitulosCarreras tc;
        Alumno a;
        Persona p;
        TEFirmante f;
        Calificacion c;
        Materia m;
        List<Calificacion> lstCalificaciones;
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        desencriptador = new CClavesActivacion();
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Certificados");
        bitacora.setMovimiento("Crear Certificado XML");
        try {
            String Query = "SELECT TOP 1 Tmp, Clave FROM Clave_Activacion AS C "
                    + " JOIN Usuario AS U ON U.ID_ConfiguracionInicial = C.ID_ConfiguracionInicial "
                    + " WHERE ID_Usuario = " + Id_Usuario
                    + " ORDER BY ID_ClaveActivacion DESC;";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                tmp = rs.getString(1);
                timbresPasados = desencriptador.leeClaveTimbre(tmp);
                cveActivacion = rs.getString(2);
                cveActivacion = desencriptador.desencripta(cveActivacion);
                cveActivacion = new String(Base64.decodeBase64(cveActivacion.getBytes()));
                fechaVencimiento = cveActivacion.substring(cveActivacion.length() - 8);
                String dd = fechaVencimiento.substring(0, 2);
                String MM = fechaVencimiento.substring(2, 4);
                String yyyy = fechaVencimiento.substring(4);
                fechaVencimiento = dd + "-" + MM + "-" + yyyy;
            }

            if (timbresPasados >= 1) {
                if (!fechaVencimiento.equalsIgnoreCase("") && !hoy.after(sdf.parse(fechaVencimiento))) {
                    Query = "SELECT CE.ID_Certificado, CE.folioControl, CE.id_profesionista, "
                            + " CE.id_carrera_excel, CE.idFirmante, CE.idTipoCertificado, "
                            + " CE.fechaExpedicion, CE.idLugarExpedicion, CE.Total, CE.Asignadas, CE.Promedio, "
                            + " CE.idLugarExpedicion, CE.creditosTotal, CE.creditosObtenidos, "
                            + " A.ID_Alumno, A.ID_Persona, A.Matricula, A.Foto, P.Nombre AS 'NombreAlumno',  "
                            + " P.APaterno, P.AMaterno, P.Curp AS 'CURPAlumno', P.FechaNacimiento, P.Sexo, TC.cveInstitucion , "
                            + " (SELECT CASE (SELECT count(confirmacionNomIInstitucion) FROM Configuracion_Nombre_Institucion) when 0 \n" 
                            + "             THEN (SELECT nombreOficial FROM Configuracion_inicial)\n" 
                            + "         ELSE\n" 
                            + "             CASE cni.confirmacionNomIInstitucion WHEN 1 \n" 
                            + "			THEN (SELECT nombreOficial FROM Configuracion_inicial)\n" 
                            + "             ELSE \n" 
                            + "			CASE cni.confirmacionNomIInstitucion WHEN 0 \n" 
                            + "                     THEN (SELECT nombreInstitucion FROM TETitulosCarreras WHERE Id_carrera = TC.id_carrera) \n" 
                            + "			END\n" 
                            + "             END\n" 
                            + "         END\n" 
                            + " FROM Configuracion_Nombre_Institucion as cni\n" 
                            + " RIGHT JOIN Configuracion_inicial as ci\n" 
                            + " ON ci.ID_ConfiguracionInicial = cni.ID_ConfiguracionInicial) as nombreInstitucion, TC.CveCampus, TC.Campus, TC.cveCarrera, TC.nombreCarrera, TC.numeroRvoe, "
                            + " TC.fechaExpedicionRvoe, CONVERT(varchar(10), TC.id_TipoPeriodo) +  '¬' + TP.TipoPeriodo AS 'Periodo', "
                            + " TC.califMinima, TC.califMaxima, TC.califMinimaAprobatoria, TC.cvePlan,"
                            + " F.idCargo, F.nombre AS 'NombreFirmante', F.noCertificadoResponsable, "
                            + " F.contrasenia, F.llave, F.certificadoCodificado, TC.id_Nivel, NE.nivel, "
                            + " F.primerApellido, F.segundoApellido, F.curp AS 'CURPFirmante', C.Cargo, "
                            + " CONVERT(varchar(10), CE.idEntidadFederativa) + '¬'+  E.EntidadFederativa AS 'EntidadFederativa', "
                            + " CONVERT(varchar(10), CE.idLugarExpedicion)  +'¬'+ EN.EntidadFederativa  AS 'LugarExpedicion',CA.Id_Carrera as Id_Tabla_Carreras,numDecimales "
                            + " FROM TECertificadoElectronico AS CE "
                            + " JOIN Alumnos AS A ON A.ID_Alumno = CE.id_profesionista "
                            + " JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera "
                            + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                            + " JOIN TEFirmantes AS F ON F.id_firmante = CE.idFirmante "
                            + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = CE.id_carrera "
                            + " JOIN TENivelEducativo AS NE ON NE.id_nivel = TC.id_Nivel "
                            + " JOIN TETipoPeriodo AS TP ON TP.id_TipoPeriodo = TC.id_TipoPeriodo "
                            + " JOIN TECargos AS C ON C.idCargo = F.idCargo "
                            + " JOIN TEEntidad AS E ON E.idEntidadFederativa = CE.idEntidadFederativa "
                            + " JOIN TEEntidad AS EN ON EN.idEntidadFederativa = CE.idLugarExpedicion "
                            + " WHERE CE.ID_Certificado = " + idCertificado + ";";
                    //System.out.println(Query);
                    pstmt = con.prepareStatement(Query);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        numDecimales = rs.getInt("numDecimales");
                        ce = new TECertificadoelectronico();
                        a = new Alumno();
                        p = new Persona();
                        tc = new TETitulosCarreras();
                        f = new TEFirmante();

                        //Se agrega un formateo a las fechas que vienen desde base de datos.
                        //Esta validación no viene dentro de las validaciones pero viene marcado dentro del XML que pasó la validación.
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        Date provisionalFechaExpedicion = sdf.parse(rs.getString("fechaExpedicion"));
                        Date provisionalFechaNacimiento = sdf.parse(rs.getString("FechaNacimiento"));
                        Date provisionalFechaExpedicionRvoe = sdf.parse(rs.getString("fechaExpedicionRvoe"));

                        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                        String dftFechaExpedicion = sdf.format(provisionalFechaExpedicion);
                        String dftFechaNacimiento = sdf.format(provisionalFechaNacimiento);
                        String dftFechaExpedicionRvoe = sdf.format(provisionalFechaExpedicionRvoe);

                        ce.setID_Certificado(rs.getString("ID_Certificado"));
                        ce.setFolioControl(rs.getString("folioControl"));
                        ce.setID_Firmante(rs.getString("idFirmante"));
                        ce.setID_TipoCertificado(rs.getString("idTipoCertificado"));
                        ce.setFechaExpedicion(dftFechaExpedicion);
                        ce.setTotal(rs.getString("Total"));
                        ce.setAsignadas(rs.getString("Asignadas"));
                        double dPromedio = Double.parseDouble(rs.getString("Promedio"));
                        String sPromedio = "";
                        if (dPromedio == 10) {
                            sPromedio = String.valueOf((int) dPromedio);
                        } else {
                            sPromedio = String.format(Locale.US, "%.2f", dPromedio);
                        }
                        ce.setPromedio(sPromedio);
                        ce.setID_EntidadFederativa(rs.getString("EntidadFederativa"));
                        ce.setID_LugarExpedicion(rs.getString("LugarExpedicion"));

                        double dTotalCreditos = Double.parseDouble(rs.getString("creditosTotal"));
                        String sTotalCreditos = String.format(Locale.US, "%." + numDecimales + "f", dTotalCreditos);
                        ce.setTotalCreditos(sTotalCreditos);

                        double dCreditosObtenidos = Double.parseDouble(rs.getString("creditosObtenidos"));
                        String sCreditosObtenidos = String.format(Locale.US, "%." + numDecimales + "f", dCreditosObtenidos);
                        ce.setCreditosObtenidos(sCreditosObtenidos);

                        a.setId_Alumno(rs.getString("ID_Alumno"));
                        a.setMatricula(rs.getString("Matricula"));
                        a.setFoto(rs.getString("Foto"));
                        a.setID_Carrera(rs.getString("Id_Tabla_Carreras"));
                        p.setNombre(rs.getString("NombreAlumno"));
                        p.setAPaterno(rs.getString("APaterno"));
                        p.setAMaterno(rs.getString("AMaterno"));
                        p.setFechaNacimiento(dftFechaNacimiento);
                        p.setCurp(rs.getString("CURPAlumno"));
                        p.setSexo(rs.getString("Sexo"));
                        tc.setID_TipoPeriodo(rs.getString("Periodo"));
                        tc.setID_Carrera(rs.getString("id_carrera_excel"));
                        tc.setNombreCarrera(rs.getString("nombreCarrera"));
                        tc.setClaveCarrera(rs.getString("cveCarrera"));
                        tc.setClaveInstitucion(rs.getString("cveInstitucion"));
                        tc.setNombreInstitucion(rs.getString("nombreInstitucion"));
                        tc.setClaveCampus(rs.getString("CveCampus"));
                        tc.setCampus(rs.getString("Campus"));
                        tc.setNumeroRvoe(rs.getString("numeroRvoe"));
                        tc.setFechaExpedicionRvoe(dftFechaExpedicionRvoe);
                        tc.setID_Nivel(rs.getString("id_Nivel") + "¬" + rs.getString("nivel"));
                        tc.setCvePlanCarrera(rs.getString("cvePlan"));

                        double dCalifiMin = Double.parseDouble(rs.getString("califMinima"));
                        int iCalifMin = (int) dCalifiMin;

                        double dCalifMax = Double.parseDouble(rs.getString("califMaxima"));
                        int iCalifMac = (int) dCalifMax;

                        double dCalifMinAprobatoria = Double.parseDouble(rs.getString("califMinimaAprobatoria"));
                        String sCalifMinAprobatoria = "";

                        if (dCalifMinAprobatoria == 10) {
                            int iCalifMinAprob = (int) dCalifMax;
                            sCalifMinAprobatoria = "" + iCalifMinAprob;
                        } else {
                            sCalifMinAprobatoria = String.format(Locale.US, "%.2f", dCalifMinAprobatoria);
                        }

                        configCalif += iCalifMin + "¬";
                        configCalif += iCalifMac + "¬";
                        configCalif += sCalifMinAprobatoria;
                        a.setPersona(p);
                        ce.setAlumno(a);

                        f.setCURP(rs.getString("CURPFirmante"));
                        f.setNombre(rs.getString("NombreFirmante"));
                        f.setPrimerApellido(rs.getString("primerApellido"));
                        f.setSegundoApellido(rs.getString("segundoApellido"));
                        f.setID_Cargo(rs.getString("idCargo"));
                        f.setCargo(rs.getString("Cargo"));
                        f.setCertificadoResponsable(rs.getString("certificadoCodificado"));
                        f.setNoCertificadoResponsable(rs.getString("noCertificadoResponsable"));
                        f.setContrasenia(rs.getString("contrasenia"));
                        f.setLlave(rs.getString("llave"));

//                        Query = "SELECT califMinima, califMaxima, califMinAprobatoria "
//                                + " FROM Configuracion_Inicial AS CI "
//                                + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
//                                + " WHERE Id_Usuario = " + Id_Usuario;
//                        rs = null;
//                        pstmt = con.prepareStatement(Query);
//                        rs = pstmt.executeQuery();
//                        if (rs.next()) {
//                            configCalif += rs.getString("califMinima") + "¬";
//                            configCalif += rs.getString("califMaxima") + "¬";
//                            configCalif += rs.getString("califMinAprobatoria");
//                        }
                        // Cadena del Elemento Dec
                        cdn_Dec += ""
                                + 5 + "¬"
                                + ce.getFolioControl().trim() + "¬"
                                + ce.getID_Firmante().trim() + "¬"
                                + f.getCertificadoResponsable().trim() + "¬"
                                + f.getNoCertificadoResponsable().trim();

                        //Cadena del Elemento Ipes
                        cdn_Ipes += ""
                                + tc.getClaveInstitucion().trim() + "¬"
                                + tc.getNombreInstitucion().trim() + "¬"
                                + tc.getClaveCampus().trim() + "¬"
                                + tc.getCampus().trim() + "¬"
                                + ce.getID_EntidadFederativa().trim();

                        // Cadena del Elemento Responsable (Firmante)
                        cdn_Responsable += ""
                                + f.getCURP().trim() + "¬"
                                + f.getNombre().trim() + "¬"
                                + f.getPrimerApellido().trim() + "¬"
                                + (f.getSegundoApellido().trim().equalsIgnoreCase("") ? "^" : f.getSegundoApellido().trim()) + "¬"
                                + f.getID_Cargo().trim() + "¬"
                                + f.getCargo().trim() + "";
                        // Cadena del Elemento Rvoe
                        cdn_Rvoe += ""
                                + tc.getNumeroRvoe().trim() + "¬"
                                + tc.getFechaExpedicionRvoe().trim();

                        // Cadena del Elemento Carrera
                        cdn_Carrera += "" + tc.getID_Carrera().trim() + "¬"
                                + tc.getClaveCarrera().trim() + "¬"
                                + tc.getNombreCarrera().trim() + "¬"
                                + tc.getID_TipoPeriodo().trim() + "¬"
                                + tc.getCvePlanCarrera().trim() + "¬"
                                + tc.getID_Nivel().trim() + "¬"
                                + configCalif;

                        // Cadena del Elemento Alumno
                        cdn_Alumno += ""
                                + a.getMatricula().trim() + "¬"
                                + p.getCurp().trim() + "¬"
                                + p.getNombre().trim() + "¬"
                                + p.getAPaterno().trim() + "¬"
                                + (p.getAMaterno() == null
                                || p.getAMaterno().equalsIgnoreCase("")
                                || p.getAMaterno().equalsIgnoreCase("null")
                                ? "^" : p.getAMaterno().trim()) + "¬"
                                + p.getSexo().trim() + "¬"
                                + p.getFechaNacimiento().trim() + "¬"
                                + (a.getFoto() == null || a.getFoto().equalsIgnoreCase("null") || a.getFoto().equalsIgnoreCase("") ? "^" : a.getFoto().trim())
                                + "¬^";// Firma Autografa

                        // Cadena del Elemento Expedicion
                        cdn_Expedicion += ""
                                + ce.getID_TipoCertificado().trim() + "¬"
                                + (ce.getID_TipoCertificado().equalsIgnoreCase("80") ? "PARCIAL" : "TOTAL") + "¬"
                                + ce.getFechaExpedicion().trim() + "¬"
                                + ce.getID_LugarExpedicion().trim();

                        // Cadena del Elemento Asignaturas
                        cdn_Asignaturas += ""
                                + ce.getTotal().trim() + "¬"
                                + ce.getAsignadas().trim() + "¬"
                                + ce.getPromedio().trim() + "¬"
                                + ce.getTotalCreditos().trim() + "¬"
                                + ce.getCreditosObtenidos().trim();

                        /* --------- Consultamos el número total de ciclos de acuerdo al MEC 3.0 --------- */
                        Query = "SELECT COUNT(DISTINCT numCiclos) AS numCiclos\n"
                                + "FROM (\n"
                                + "	SELECT Ciclo_Escolar as numCiclos FROM Materias AS M \n"
                                + "	JOIN Calificaciones AS C ON C.ID_Materia = M.ID_Materia \n"
                                + "	JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno\n"
                                + "	WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera = " + a.getID_Carrera() + "))\n"
                                + "	UNION\n"
                                + "	SELECT Ciclo_Escolar as numCiclos FROM Materias AS M \n"
                                + "	JOIN Calificaciones_Letra AS C ON C.ID_Materia = M.ID_Materia \n"
                                + "	JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno\n"
                                + "	WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera = " + a.getID_Carrera() + "))\n"
                                + ") AS TBL";

                        rs = null;
                        pstmt = con.prepareStatement(Query);
                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            cdn_Asignaturas += "¬" + rs.getString("numCiclos");
                        } else {
                            return "numCiclos|No fue posible consulta el número de ciclos del certificado. Intente generar nuevamente el certificado.";
                        }

                        /* --------- Consulta de las calificaciones del alumno --------- */
                        Query = "SELECT M.Folio, M.Clave, M.Descripcion, M.Tipo, M.Creditos,\n"
                                + " C.Calificacion,'' as CalificacionLetra,  C.observaciones,C.Ciclo_Escolar AS 'CicloEscolar' \n"
                                + " FROM Materias AS M \n"
                                + " JOIN Calificaciones AS C ON C.ID_Materia = M.ID_Materia \n"
                                + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno \n"
                                + " WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND \n"
                                + " C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera =  " + a.getID_Carrera() + "))\n"
                                + " UNION\n"
                                + " SELECT M.Folio, M.Clave, M.Descripcion, M.Tipo, M.Creditos,0,\n"
                                + " C.CalificacionLetra, C.observaciones,C.Ciclo_Escolar AS 'CicloEscolar' \n"
                                + " FROM Materias AS M \n"
                                + " JOIN Calificaciones_Letra AS C ON C.ID_Materia = M.ID_Materia \n"
                                + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno \n"
                                + " WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND \n"
                                + " C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera =  " + a.getID_Carrera() + "))\n"
                                + " ORDER BY C.Ciclo_Escolar, folio";
                        rs = null;
                        pstmt = con.prepareStatement(Query);
                        rs = pstmt.executeQuery();
                        lstCalificaciones = new ArrayList<>();
                        while (rs.next()) {
                            String calificacionLetra = rs.getString("CalificacionLetra");
                            m = new Materia();
                            c = new Calificacion();

                            m.setID_Materia(rs.getString("Folio")); //En el campo  IdAsignatura del XML se asigna el folio de ordenamiento del EXCEL
                            //SI LA CLAVE DE LA MATERIA CONTIENE LA PALABRA VACIO O VACIA LA CLAVE DE LA MATERIA ESTA REGISTRADA EN BLANCO ANTE LA SEP.
                            m.setClave(rs.getString("Clave").contains("VACIO") || rs.getString("Clave").contains("VACIA") ? "" : rs.getString("Clave"));
                            m.setDescripcion(rs.getString("Descripcion"));
                            m.setTipo(rs.getString("tipo"));

                            double dCalificacion = Double.parseDouble(rs.getString("Calificacion"));
                            String sCalificacion = "";
                            if (dCalificacion == 10) {
                                sCalificacion = String.format(Locale.US, "%.0f", dCalificacion);
                                c.setCalificacion(sCalificacion);
                            } else {
                                if (dCalificacion == 0.00 && (calificacionLetra.equalsIgnoreCase("AC") || calificacionLetra.equalsIgnoreCase("ACREDITADA"))) {
                                    sCalificacion = calificacionLetra;
                                } else {
                                    sCalificacion = String.format(Locale.US, "%.2f", dCalificacion);
                                }
                                c.setCalificacion(sCalificacion);
                            }

                            double dCreditos = Double.parseDouble(rs.getString("Creditos"));
                            if ((!calificacionLetra.equalsIgnoreCase("AC") && !calificacionLetra.equalsIgnoreCase("ACREDITADA")) && (dCalificacion < dCalifMinAprobatoria)) {
                                dCreditos = 0;
                            }
                            String sCreditos = String.format(Locale.US, "%." + numDecimales + "f", dCreditos);
                            m.setCreditos(sCreditos);

                            c.setObservaciones(rs.getString("observaciones"));
                            c.setID_Ciclo(rs.getString("CicloEscolar"));// Se guarda la descripcion del ciclo escolar
                            c.setMateria(m);
                            lstCalificaciones.add(c);
                        }
                        //Cadena del Elemento Asignatura
                        for (int i = 0; i < lstCalificaciones.size(); i++) {
                            cdn_Asignatura += ""
                                    + lstCalificaciones.get(i).getMateria().getID_Materia().trim() + "¬"
                                    + lstCalificaciones.get(i).getMateria().getClave().trim() + "¬"
                                    + lstCalificaciones.get(i).getMateria().getDescripcion().trim() + "¬"
                                    + lstCalificaciones.get(i).getID_Ciclo().trim() + "¬"
                                    + lstCalificaciones.get(i).getCalificacion().trim() + "¬"
                                    + (lstCalificaciones.get(i).getObservaciones() == null || lstCalificaciones.get(i).getObservaciones().equalsIgnoreCase("") ? "^" : lstCalificaciones.get(i).getObservaciones().trim()) + "¬";
                            Query = "SELECT Descripcion FROM TEObservaciones WHERE Id_Observacion = " + (lstCalificaciones.get(i).getObservaciones() == null ? "0" : lstCalificaciones.get(i).getObservaciones());
                            rs = null;
                            pstmt = con.prepareStatement(Query);
                            rs = pstmt.executeQuery();
                            if (rs.next()) {
                                cdn_Asignatura += rs.getString("Descripcion") + "¬";
                            } else {
                                cdn_Asignatura += "^¬";
                            }
                            cdn_Asignatura += (lstCalificaciones.get(i).getMateria().getTipo().equalsIgnoreCase("0") ? "^" : lstCalificaciones.get(i).getMateria().getTipo().trim()) + "¬";
                            switch (lstCalificaciones.get(i).getMateria().getTipo()) {
                                case "263":
                                    cdn_Asignatura += "OBLIGATORIA";
                                    break;
                                case "264":
                                    cdn_Asignatura += "OPTATIVA";
                                    break;
                                case "265":
                                    cdn_Asignatura += "ADICIONAL";
                                    break;
                                case "266":
                                    cdn_Asignatura += "COMPLEMENTARIA";
                                    break;
                                case "0":
                                    cdn_Asignatura += "^";
                                    break;
                                default:
                                    cdn_Asignatura += "^";
                                    break;
                            }
                            cdn_Asignatura += "¬";
                            cdn_Asignatura += lstCalificaciones.get(i).getMateria().getCreditos().trim();
                            cdn_Asignatura += (i < lstCalificaciones.size() - 1 ? "~" : "");
                        }

                        certElec = new Cxml_certificado_electronico(request);
                        String Respuesta = certElec.crear_xml_certificado_electronico(cdn_Dec, cdn_Ipes, cdn_Responsable, cdn_Rvoe, cdn_Carrera, cdn_Alumno, cdn_Expedicion, cdn_Asignaturas, cdn_Asignatura, Id_Usuario);

                        if (Respuesta.contains("success")) {
                            bitacora.setInformacion("Generación digital de XML||Datos del alumno:" + cdn_Alumno + "||Datos Asignaturas:" + cdn_Asignaturas + "||Datos DEC:" + cdn_Dec);
                            cBitacora = new CBitacora(bitacora);
                            cBitacora.setRequest(request);
                            cBitacora.addBitacoraGeneral();
                            RESP = generarCertificadoXML(idCertificado);
                        } else {
                            if (!Respuesta.equalsIgnoreCase("errorContrasenia")) {
                                RESP = "error|Error al generar el archivo XML.<br>Recarga la página presionando F5.<br><br><small>Si el problema persiste, contacta a soporte técnico.</small>";
                            } else {
                                RESP = Respuesta;
                            }
                        }
                    } else {
                        return "error|La consulta que recopila la información del certificado no ha tenido resultados. Intenta de nuevo.<br><br><small>Si el problema persiste, contacta a soporte técnico.</small>";
                    }

                } else {
                    return "timbresVencidos";
                }
            } else {
                return "sinTimbres";
            }

        } catch (Exception ex) {
            RESP = "error|Error inesperado al crear el certificado electrónico: " + accion_catch(ex);
        } finally {
            try {
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
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

        private String crearCadenaXMLMasivo() throws SQLException {
        String RESP = "";
        String cadenaIdCertificado = request.getParameter("cadenaIdCertificado");
        String[] idCertificados = cadenaIdCertificado.split("¬");
        for (String idCertificado : idCertificados) {
            String cdn_Dec = "";
            String cdn_Ipes = "";
            String cdn_Responsable = "";
            String cdn_Rvoe = "";
            String cdn_Carrera = "";
            String cdn_Alumno = "";
            String cdn_Expedicion = "";
            String cdn_Asignaturas = "";
            String cdn_Asignatura = "";
            String tmp = "";
            String cveActivacion = "";
            String fechaVencimiento = "";
            String configCalif = "";
            int timbresPasados = 0, numDecimales = 2;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date hoy = new Date();
            TECertificadoelectronico ce;
            TETitulosCarreras tc;
            Alumno a;
            Persona p;
            TEFirmante f;
            Calificacion c;
            Materia m;
            List<Calificacion> lstCalificaciones;
            conexion = new CConexion();
            conexion.setRequest(request);
            Connection con = null;
            PreparedStatement pstmt = null;
            desencriptador = new CClavesActivacion();
            bitacora = new Bitacora();
            bitacora.setId_Usuario(Id_Usuario);
            bitacora.setModulo("Certificados");
            bitacora.setMovimiento("Crear Certificado XML");
            try {
                String Query = "SELECT TOP 1 Tmp, Clave FROM Clave_Activacion AS C "
                        + " JOIN Usuario AS U ON U.ID_ConfiguracionInicial = C.ID_ConfiguracionInicial "
                        + " WHERE ID_Usuario = " + Id_Usuario
                        + " ORDER BY ID_ClaveActivacion DESC;";
                con = conexion.GetconexionInSite();
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    tmp = rs.getString(1);
                    timbresPasados = desencriptador.leeClaveTimbre(tmp);
                    cveActivacion = rs.getString(2);
                    cveActivacion = desencriptador.desencripta(cveActivacion);
                    cveActivacion = new String(Base64.decodeBase64(cveActivacion.getBytes()));
                    fechaVencimiento = cveActivacion.substring(cveActivacion.length() - 8);
                    String dd = fechaVencimiento.substring(0, 2);
                    String MM = fechaVencimiento.substring(2, 4);
                    String yyyy = fechaVencimiento.substring(4);
                    fechaVencimiento = dd + "-" + MM + "-" + yyyy;
                }

                if (timbresPasados >= 1) {
                    if (!fechaVencimiento.equalsIgnoreCase("") && !hoy.after(sdf.parse(fechaVencimiento))) {
                        Query = "SELECT CE.ID_Certificado, CE.folioControl, CE.id_profesionista, "
                                + " CE.id_carrera_excel, CE.idFirmante, CE.idTipoCertificado, "
                                + " CE.fechaExpedicion, CE.idLugarExpedicion, CE.Total, CE.Asignadas, CE.Promedio, "
                                + " CE.idLugarExpedicion, CE.creditosTotal, CE.creditosObtenidos, "
                                + " A.ID_Alumno, A.ID_Persona, A.Matricula, A.Foto, P.Nombre AS 'NombreAlumno',  "
                                + " P.APaterno, P.AMaterno, P.Curp AS 'CURPAlumno', P.FechaNacimiento, P.Sexo, TC.cveInstitucion , "
                                + " (SELECT CASE (SELECT count(confirmacionNomIInstitucion) FROM Configuracion_Nombre_Institucion) when 0 \n" 
                                + "             THEN (SELECT nombreOficial FROM Configuracion_inicial)\n" 
                                + "         ELSE\n" 
                                + "             CASE cni.confirmacionNomIInstitucion WHEN 1 \n" 
                                + "			THEN (SELECT nombreOficial FROM Configuracion_inicial)\n" 
                                + "             ELSE \n" 
                                + "			CASE cni.confirmacionNomIInstitucion WHEN 0 \n" 
                                + "                     THEN (SELECT nombreInstitucion FROM TETitulosCarreras WHERE Id_carrera = TC.id_carrera) \n" 
                                + "			END\n" 
                                + "             END\n" 
                                + "         END\n" 
                                + " FROM Configuracion_Nombre_Institucion as cni\n" 
                                + " RIGHT JOIN Configuracion_inicial as ci\n" 
                                + " ON ci.ID_ConfiguracionInicial = cni.ID_ConfiguracionInicial) as nombreInstitucion, TC.CveCampus, TC.Campus, TC.cveCarrera, TC.nombreCarrera, TC.numeroRvoe, "
                                + " TC.fechaExpedicionRvoe, CONVERT(varchar(10), TC.id_TipoPeriodo) +  '¬' + TP.TipoPeriodo AS 'Periodo', "
                                + " TC.califMinima, TC.califMaxima, TC.califMinimaAprobatoria, TC.cvePlan,"
                                + " F.idCargo, F.nombre AS 'NombreFirmante', F.noCertificadoResponsable, "
                                + " F.contrasenia, F.llave, F.certificadoCodificado, TC.id_Nivel, NE.nivel, "
                                + " F.primerApellido, F.segundoApellido, F.curp AS 'CURPFirmante', C.Cargo, "
                                + " CONVERT(varchar(10), CE.idEntidadFederativa) + '¬'+  E.EntidadFederativa AS 'EntidadFederativa', "
                                + " CONVERT(varchar(10), CE.idLugarExpedicion)  +'¬'+ EN.EntidadFederativa  AS 'LugarExpedicion',CA.Id_Carrera as Id_Tabla_Carreras,numDecimales "
                                + " FROM TECertificadoElectronico AS CE "
                                + " JOIN Alumnos AS A ON A.ID_Alumno = CE.id_profesionista "
                                + " JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera "
                                + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                                + " JOIN TEFirmantes AS F ON F.id_firmante = CE.idFirmante "
                                + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = CE.id_carrera "
                                + " JOIN TENivelEducativo AS NE ON NE.id_nivel = TC.id_Nivel "
                                + " JOIN TETipoPeriodo AS TP ON TP.id_TipoPeriodo = TC.id_TipoPeriodo "
                                + " JOIN TECargos AS C ON C.idCargo = F.idCargo "
                                + " JOIN TEEntidad AS E ON E.idEntidadFederativa = CE.idEntidadFederativa "
                                + " JOIN TEEntidad AS EN ON EN.idEntidadFederativa = CE.idLugarExpedicion "
                                + " WHERE CE.ID_Certificado = " + idCertificado + ";";
                        pstmt = con.prepareStatement(Query);
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            numDecimales = rs.getInt("numDecimales");
                            ce = new TECertificadoelectronico();
                            a = new Alumno();
                            p = new Persona();
                            tc = new TETitulosCarreras();
                            f = new TEFirmante();

                            //Se agrega un formateo a las fechas que vienen desde base de datos.
                            //Esta validación no viene dentro de las validaciones pero viene marcado dentro del XML que pasó la validación.
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            Date provisionalFechaExpedicion = sdf.parse(rs.getString("fechaExpedicion"));
                            Date provisionalFechaNacimiento = sdf.parse(rs.getString("FechaNacimiento"));
                            Date provisionalFechaExpedicionRvoe = sdf.parse(rs.getString("fechaExpedicionRvoe"));

                            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                            String dftFechaExpedicion = sdf.format(provisionalFechaExpedicion);
                            String dftFechaNacimiento = sdf.format(provisionalFechaNacimiento);
                            String dftFechaExpedicionRvoe = sdf.format(provisionalFechaExpedicionRvoe);

                            ce.setID_Certificado(rs.getString("ID_Certificado"));
                            ce.setFolioControl(rs.getString("folioControl"));
                            ce.setID_Firmante(rs.getString("idFirmante"));
                            ce.setID_TipoCertificado(rs.getString("idTipoCertificado"));
                            ce.setFechaExpedicion(dftFechaExpedicion);
                            ce.setTotal(rs.getString("Total"));
                            ce.setAsignadas(rs.getString("Asignadas"));
                            double dPromedio = Double.parseDouble(rs.getString("Promedio"));
                            String sPromedio = "";
                            if (dPromedio == 10) {
                                sPromedio = String.valueOf((int) dPromedio);
                            } else {
                                sPromedio = String.format(Locale.US, "%.2f", dPromedio);
                            }
                            ce.setPromedio(sPromedio);
                            ce.setID_EntidadFederativa(rs.getString("EntidadFederativa"));
                            ce.setID_LugarExpedicion(rs.getString("LugarExpedicion"));

                            double dTotalCreditos = Double.parseDouble(rs.getString("creditosTotal"));
                            String sTotalCreditos = String.format(Locale.US, "%." + numDecimales + "f", dTotalCreditos);
                            ce.setTotalCreditos(sTotalCreditos);

                            double dCreditosObtenidos = Double.parseDouble(rs.getString("creditosObtenidos"));
                            String sCreditosObtenidos = String.format(Locale.US, "%." + numDecimales + "f", dCreditosObtenidos);
                            ce.setCreditosObtenidos(sCreditosObtenidos);

                            a.setId_Alumno(rs.getString("ID_Alumno"));
                            a.setMatricula(rs.getString("Matricula"));
                            a.setFoto(rs.getString("Foto"));
                            a.setID_Carrera(rs.getString("Id_Tabla_Carreras"));
                            p.setNombre(rs.getString("NombreAlumno"));
                            p.setAPaterno(rs.getString("APaterno"));
                            p.setAMaterno(rs.getString("AMaterno"));
                            p.setFechaNacimiento(dftFechaNacimiento);
                            p.setCurp(rs.getString("CURPAlumno"));
                            p.setSexo(rs.getString("Sexo"));
                            tc.setID_TipoPeriodo(rs.getString("Periodo"));
                            tc.setID_Carrera(rs.getString("id_carrera_excel"));
                            tc.setNombreCarrera(rs.getString("nombreCarrera"));
                            tc.setClaveCarrera(rs.getString("cveCarrera"));
                            tc.setClaveInstitucion(rs.getString("cveInstitucion"));
                            tc.setNombreInstitucion(rs.getString("nombreInstitucion"));
                            tc.setClaveCampus(rs.getString("CveCampus"));
                            tc.setCampus(rs.getString("Campus"));
                            tc.setNumeroRvoe(rs.getString("numeroRvoe"));
                            tc.setFechaExpedicionRvoe(dftFechaExpedicionRvoe);
                            tc.setID_Nivel(rs.getString("id_Nivel") + "¬" + rs.getString("nivel"));
                            tc.setCvePlanCarrera(rs.getString("cvePlan"));

                            double dCalifiMin = Double.parseDouble(rs.getString("califMinima"));
                            int iCalifMin = (int) dCalifiMin;

                            double dCalifMax = Double.parseDouble(rs.getString("califMaxima"));
                            int iCalifMac = (int) dCalifMax;

                            double dCalifMinAprobatoria = Double.parseDouble(rs.getString("califMinimaAprobatoria"));
                            String sCalifMinAprobatoria = "";

                            if (dCalifMinAprobatoria == 10) {
                                int iCalifMinAprob = (int) dCalifMax;
                                sCalifMinAprobatoria = "" + iCalifMinAprob;
                            } else {
                                sCalifMinAprobatoria = String.format(Locale.US, "%.2f", dCalifMinAprobatoria);
                            }

                            configCalif += iCalifMin + "¬";
                            configCalif += iCalifMac + "¬";
                            configCalif += sCalifMinAprobatoria;
                            a.setPersona(p);
                            ce.setAlumno(a);

                            f.setCURP(rs.getString("CURPFirmante"));
                            f.setNombre(rs.getString("NombreFirmante"));
                            f.setPrimerApellido(rs.getString("primerApellido"));
                            f.setSegundoApellido(rs.getString("segundoApellido"));
                            f.setID_Cargo(rs.getString("idCargo"));
                            f.setCargo(rs.getString("Cargo"));
                            f.setCertificadoResponsable(rs.getString("certificadoCodificado"));
                            f.setNoCertificadoResponsable(rs.getString("noCertificadoResponsable"));
                            f.setContrasenia(rs.getString("contrasenia"));
                            f.setLlave(rs.getString("llave"));

//                        Query = "SELECT califMinima, califMaxima, califMinAprobatoria "
//                                + " FROM Configuracion_Inicial AS CI "
//                                + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
//                                + " WHERE Id_Usuario = " + Id_Usuario;
//                        rs = null;
//                        pstmt = con.prepareStatement(Query);
//                        rs = pstmt.executeQuery();
//                        if (rs.next()) {
//                            configCalif += rs.getString("califMinima") + "¬";
//                            configCalif += rs.getString("califMaxima") + "¬";
//                            configCalif += rs.getString("califMinAprobatoria");
//                        }
                            // Cadena del Elemento Dec
                            cdn_Dec += ""
                                    + 5 + "¬"
                                    + ce.getFolioControl().trim() + "¬"
                                    + ce.getID_Firmante().trim() + "¬"
                                    + f.getCertificadoResponsable().trim() + "¬"
                                    + f.getNoCertificadoResponsable().trim();

                            //Cadena del Elemento Ipes
                            cdn_Ipes += ""
                                    + tc.getClaveInstitucion().trim() + "¬"
                                    + tc.getNombreInstitucion().trim() + "¬"
                                    + tc.getClaveCampus().trim() + "¬"
                                    + tc.getCampus().trim() + "¬"
                                    + ce.getID_EntidadFederativa().trim();

                            // Cadena del Elemento Responsable (Firmante)
                            cdn_Responsable += ""
                                    + f.getCURP().trim() + "¬"
                                    + f.getNombre().trim() + "¬"
                                    + f.getPrimerApellido().trim() + "¬"
                                    + (f.getSegundoApellido().trim().equalsIgnoreCase("") ? "^" : f.getSegundoApellido().trim()) + "¬"
                                    + f.getID_Cargo().trim() + "¬"
                                    + f.getCargo().trim() + "";
                            // Cadena del Elemento Rvoe
                            cdn_Rvoe += ""
                                    + tc.getNumeroRvoe().trim() + "¬"
                                    + tc.getFechaExpedicionRvoe().trim();

                            // Cadena del Elemento Carrera
                            cdn_Carrera += "" + tc.getID_Carrera().trim() + "¬"
                                    + tc.getClaveCarrera().trim() + "¬"
                                    + tc.getNombreCarrera().trim() + "¬"
                                    + tc.getID_TipoPeriodo().trim() + "¬"
                                    + tc.getCvePlanCarrera().trim() + "¬"
                                    + tc.getID_Nivel().trim() + "¬"
                                    + configCalif;

                            // Cadena del Elemento Alumno
                            cdn_Alumno += ""
                                    + a.getMatricula().trim() + "¬"
                                    + p.getCurp().trim() + "¬"
                                    + p.getNombre().trim() + "¬"
                                    + p.getAPaterno().trim() + "¬"
                                    + (p.getAMaterno() == null
                                    || p.getAMaterno().equalsIgnoreCase("")
                                    || p.getAMaterno().equalsIgnoreCase("null")
                                    ? "^" : p.getAMaterno().trim()) + "¬"
                                    + p.getSexo().trim() + "¬"
                                    + p.getFechaNacimiento().trim() + "¬"
                                    + (a.getFoto() == null || a.getFoto().equalsIgnoreCase("null") || a.getFoto().equalsIgnoreCase("") ? "^" : a.getFoto().trim())
                                    + "¬^";// Firma Autografa

                            // Cadena del Elemento Expedicion
                            cdn_Expedicion += ""
                                    + ce.getID_TipoCertificado().trim() + "¬"
                                    + (ce.getID_TipoCertificado().equalsIgnoreCase("80") ? "PARCIAL" : "TOTAL") + "¬"
                                    + ce.getFechaExpedicion().trim() + "¬"
                                    + ce.getID_LugarExpedicion().trim();

                            // Cadena del Elemento Asignaturas
                            cdn_Asignaturas += ""
                                    + ce.getTotal().trim() + "¬"
                                    + ce.getAsignadas().trim() + "¬"
                                    + ce.getPromedio().trim() + "¬"
                                    + ce.getTotalCreditos().trim() + "¬"
                                    + ce.getCreditosObtenidos().trim();

                            /* --------- Consultamos el número total de ciclos de acuerdo al MEC 3.0 --------- */
                            Query = "SELECT COUNT(DISTINCT numCiclos) AS numCiclos\n"
                                    + "FROM (\n"
                                    + "	SELECT Ciclo_Escolar as numCiclos FROM Materias AS M \n"
                                    + "	JOIN Calificaciones AS C ON C.ID_Materia = M.ID_Materia \n"
                                    + "	JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno\n"
                                    + "	WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera = " + a.getID_Carrera() + "))\n"
                                    + "	UNION\n"
                                    + "	SELECT Ciclo_Escolar as numCiclos FROM Materias AS M \n"
                                    + "	JOIN Calificaciones_Letra AS C ON C.ID_Materia = M.ID_Materia \n"
                                    + "	JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno\n"
                                    + "	WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera = " + a.getID_Carrera() + "))\n"
                                    + ") AS TBL";

                            rs = null;
                            pstmt = con.prepareStatement(Query);
                            rs = pstmt.executeQuery();

                            if (rs.next()) {
                                cdn_Asignaturas += "¬" + rs.getString("numCiclos");
                            } else {
                                return "numCiclos|No fue posible consulta el número de ciclos del certificado. Intente generar nuevamente el certificado.";
                            }

                            /* --------- Consulta de las calificaciones del alumno --------- */
                            Query = "SELECT M.Folio, M.Clave, M.Descripcion, M.Tipo, M.Creditos,\n"
                                    + " C.Calificacion,'' as CalificacionLetra,  C.observaciones,C.Ciclo_Escolar AS 'CicloEscolar' \n"
                                    + " FROM Materias AS M \n"
                                    + " JOIN Calificaciones AS C ON C.ID_Materia = M.ID_Materia \n"
                                    + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno \n"
                                    + " WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND \n"
                                    + " C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera =  " + a.getID_Carrera() + "))\n"
                                    + " UNION\n"
                                    + " SELECT M.Folio, M.Clave, M.Descripcion, M.Tipo, M.Creditos,0,\n"
                                    + " C.CalificacionLetra, C.observaciones,C.Ciclo_Escolar AS 'CicloEscolar' \n"
                                    + " FROM Materias AS M \n"
                                    + " JOIN Calificaciones_Letra AS C ON C.ID_Materia = M.ID_Materia \n"
                                    + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno \n"
                                    + " WHERE A.ID_Alumno = " + a.getId_Alumno() + " AND \n"
                                    + " C.Id_Materia IN (SELECT Id_Materia FROM Materias WHERE Id_Curso IN (SELECT Id_Curso FROM Curso WHERE ID_Carrera =  " + a.getID_Carrera() + "))\n"
                                    + " ORDER BY C.Ciclo_Escolar, folio";
                            rs = null;
                            pstmt = con.prepareStatement(Query);
                            rs = pstmt.executeQuery();
                            lstCalificaciones = new ArrayList<>();
                            while (rs.next()) {
                                String calificacionLetra = rs.getString("CalificacionLetra");
                                m = new Materia();
                                c = new Calificacion();

                                m.setID_Materia(rs.getString("Folio")); //En el campo  IdAsignatura del XML se asigna el folio de ordenamiento del EXCEL
                                m.setClave(rs.getString("Clave"));
                                m.setDescripcion(rs.getString("Descripcion"));
                                m.setTipo(rs.getString("tipo"));

                                double dCalificacion = Double.parseDouble(rs.getString("Calificacion"));
                                String sCalificacion = "";
                                if (dCalificacion == 10) {
                                    sCalificacion = String.format(Locale.US, "%.0f", dCalificacion);
                                    c.setCalificacion(sCalificacion);
                                } else {
                                    if (dCalificacion == 0.00 && (calificacionLetra.equalsIgnoreCase("AC") || calificacionLetra.equalsIgnoreCase("ACREDITADA"))) {
                                        sCalificacion = calificacionLetra;
                                    } else {
                                        sCalificacion = String.format(Locale.US, "%.2f", dCalificacion);
                                    }
                                    c.setCalificacion(sCalificacion);
                                }

                                double dCreditos = Double.parseDouble(rs.getString("Creditos"));
                                if ((!calificacionLetra.equalsIgnoreCase("AC") && !calificacionLetra.equalsIgnoreCase("ACREDITADA")) && (dCalificacion < dCalifMinAprobatoria)) {
                                    dCreditos = 0;
                                }
                                String sCreditos = String.format(Locale.US, "%." + numDecimales + "f", dCreditos);
                                m.setCreditos(sCreditos);

                                c.setObservaciones(rs.getString("observaciones"));
                                c.setID_Ciclo(rs.getString("CicloEscolar"));// Se guarda la descripcion del ciclo escolar
                                c.setMateria(m);
                                lstCalificaciones.add(c);
                            }
                            //Cadena del Elemento Asignatura
                            for (int i = 0; i < lstCalificaciones.size(); i++) {
                                cdn_Asignatura += ""
                                        + lstCalificaciones.get(i).getMateria().getID_Materia().trim() + "¬"
                                        + lstCalificaciones.get(i).getMateria().getClave().trim() + "¬"
                                        + lstCalificaciones.get(i).getMateria().getDescripcion().trim() + "¬"
                                        + lstCalificaciones.get(i).getID_Ciclo().trim() + "¬"
                                        + lstCalificaciones.get(i).getCalificacion().trim() + "¬"
                                        + (lstCalificaciones.get(i).getObservaciones() == null || lstCalificaciones.get(i).getObservaciones().equalsIgnoreCase("") ? "^" : lstCalificaciones.get(i).getObservaciones().trim()) + "¬";
                                Query = "SELECT Descripcion FROM TEObservaciones WHERE Id_Observacion = " + (lstCalificaciones.get(i).getObservaciones() == null ? "0" : lstCalificaciones.get(i).getObservaciones());
                                rs = null;
                                pstmt = con.prepareStatement(Query);
                                rs = pstmt.executeQuery();
                                if (rs.next()) {
                                    cdn_Asignatura += rs.getString("Descripcion") + "¬";
                                } else {
                                    cdn_Asignatura += "^¬";
                                }
                                cdn_Asignatura += (lstCalificaciones.get(i).getMateria().getTipo().equalsIgnoreCase("0") ? "^" : lstCalificaciones.get(i).getMateria().getTipo().trim()) + "¬";
                                switch (lstCalificaciones.get(i).getMateria().getTipo()) {
                                    case "263":
                                        cdn_Asignatura += "OBLIGATORIA";
                                        break;
                                    case "264":
                                        cdn_Asignatura += "OPTATIVA";
                                        break;
                                    case "265":
                                        cdn_Asignatura += "ADICIONAL";
                                        break;
                                    case "266":
                                        cdn_Asignatura += "COMPLEMENTARIA";
                                        break;
                                    case "0":
                                        cdn_Asignatura += "^";
                                        break;
                                    default:
                                        cdn_Asignatura += "^";
                                        break;
                                }
                                cdn_Asignatura += "¬";
                                cdn_Asignatura += lstCalificaciones.get(i).getMateria().getCreditos().trim();
                                cdn_Asignatura += (i < lstCalificaciones.size() - 1 ? "~" : "");
                            }

                            certElec = new Cxml_certificado_electronico(request);
                            String Respuesta = certElec.crear_xml_certificado_electronico(cdn_Dec, cdn_Ipes, cdn_Responsable, cdn_Rvoe, cdn_Carrera, cdn_Alumno, cdn_Expedicion, cdn_Asignaturas, cdn_Asignatura, Id_Usuario);

                            if (Respuesta.contains("success")) {
                                bitacora.setInformacion("Generación digital de XML||Datos del alumno:" + cdn_Alumno + "||Datos Asignaturas:" + cdn_Asignaturas + "||Datos DEC:" + cdn_Dec);
                                cBitacora = new CBitacora(bitacora);
                                cBitacora.setRequest(request);
                                cBitacora.addBitacoraGeneral();
                                RESP = generarCertificadoXML(idCertificado);
                            } else {
                                if (!Respuesta.equalsIgnoreCase("errorContrasenia")) {
                                    RESP = "error";
                                } else {
                                    RESP = Respuesta;
                                    break;
                                }
                            }
                        } else {
                            return "error";
                        }

                    } else {
                        return "timbresVencidos";
                    }
                } else {
                    return "sinTimbres";
                }

            } catch (Exception e) {
                e.printStackTrace();
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "Error:------>", e);
                RESP = "error";
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
        }
        return RESP;
    }

    private String descargarXML() throws SQLException {
        String RESP = "";
        String idCertificado = request.getParameter("idCertificado");
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Certificados");
        bitacora.setMovimiento("Descarga");
        try {
            String Query = "SELECT A.Matricula, TC.cveCarrera, CE.folioControl "
                    + " FROM TECertificadoElectronico AS CE "
                    + " JOIN Alumnos AS A ON A.ID_Alumno = CE.id_profesionista "
                    + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = CE.id_carrera "
                    + " WHERE ID_Certificado = " + idCertificado;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                RESP += rs.getString("folioControl") + "_" + rs.getString("Matricula") + "_" + rs.getString("cveCarrera");
                Query = "SELECT xmlTE FROM TExml WHERE folioControl LIKE '" + rs.getString("folioControl") + "'";
                rs = null;
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    RESP += "fpav" + rs.getString("xmlTE");
                    bitacora.setInformacion("Descarga de xml de certificado||Respuesta método: " + RESP);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.setRequest(request);
                    cBitacora.addBitacoraGeneral();
                } else {
                    RESP = "sinCoincidencias";
                }
            } else {
                RESP = "sinCoincidencias";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al descargar el archivo XML: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al descargar el archivo XML: " + accion_catch(ex);
        } finally {
            con.close();
            pstmt.close();
            conexion.GetconexionInSite().close();
        }
        return RESP;
    }

    private String cargarNotifyFirmantes() {
        String RESP = "";
        Connection con = null;
        PreparedStatement pstmt = null;
        rs = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            lstFirmantes = new ArrayList<TEFirmante>();
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            String Query = "SELECT nombre, primerApellido,segundoApellido, fechaCertificadoExpirar FROM TEFIRMANTES WHERE estatus =1";
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                firmante = new TEFirmante();

                firmante.setNombre(rs.getString(1));
                firmante.setPrimerApellido(rs.getString(2));
                firmante.setSegundoApellido(rs.getString(3));
                firmante.setFechaCertificadoExpirar(rs.getString(4));

                lstFirmantes.add(firmante);

            }

            for (TEFirmante firm : lstFirmantes) {
                LocalDate fechaVencimientoCer = LocalDate.parse(firm.getFechaCertificadoExpirar());
                LocalDate fechaActual = LocalDate.parse(sdf.format(today));

                long diasRestantes = ChronoUnit.DAYS.between(fechaActual, fechaVencimientoCer);

                if (diasRestantes <= 0) {
                    RESP += "$.notify({\n"
                            + "	// options\n"
                            + "	icon: 'fa fa-warning',\n"
                            + "	title: 'Vencimiento de e.Firma<br>',\n"
                            + "	message: 'La e.Firma de <b><u>" + firm.getNombre() + " " + firm.getPrimerApellido() + " " + firm.getSegundoApellido() + "</u></b> ha caducado.'\n"
                            + "},{\n"
                            + "	// settings\n"
                            + "	element: 'body',\n"
                            + "	position: null,\n"
                            + "	type: \"danger\",\n"
                            + "	allow_dismiss: true,\n"
                            + "	newest_on_top: false,\n"
                            + "	placement: {\n"
                            + "		from: \"top\",\n"
                            + "		align: \"right\"\n"
                            + "	},\n"
                            + "	offset: 20,\n"
                            + "	spacing: 10,\n"
                            + "	z_index: 1031,\n"
                            + "	delay: 25000,\n"
                            + "	timer: 1000,\n"
                            + "	animate: {\n"
                            + "		enter: 'animated fadeInDown',\n"
                            + "		exit: 'animated fadeOutUp'\n"
                            + "	},\n"
                            + "	icon_type: 'class',\n"
                            + "	template: '<div data-notify=\"container\" class=\"col-xs-11 col-sm-8 alert alert-{0}\" role=\"alert\">' +\n"
                            + "		'<button type=\"button\" aria-hidden=\"true\" class=\"close\" data-notify=\"dismiss\">×</button>' +\n"
                            + "		'<span data-notify=\"icon\"></span> ' +\n"
                            + "		'<span data-notify=\"title\">{1}</span> ' +\n"
                            + "		'<span data-notify=\"message\">{2}</span>' +\n"
                            + "	'</div>' \n"
                            + "});\n";
                } else if (diasRestantes > 0 && diasRestantes < 11) {
                    RESP += "$.notify({\n"
                            + "	// options\n"
                            + "	icon: 'fa fa-warning',\n"
                            + "	title: 'Vencimiento de e.Firma<br>',\n"
                            + "	message: 'La e.Firma de <b><u>" + firm.getNombre() + " " + firm.getPrimerApellido() + " " + firm.getSegundoApellido() + "</u></b> "
                            + "está a punto de caducar.<br><b>Días Restantes: " + diasRestantes + "</b>'\n"
                            + "},{\n"
                            + "	// settings\n"
                            + "	element: 'body',\n"
                            + "	position: null,\n"
                            + "	type: \"danger\",\n"
                            + "	allow_dismiss: true,\n"
                            + "	newest_on_top: false,\n"
                            + "	placement: {\n"
                            + "		from: \"top\",\n"
                            + "		align: \"right\"\n"
                            + "	},\n"
                            + "	offset: 20,\n"
                            + "	spacing: 10,\n"
                            + "	z_index: 1031,\n"
                            + "	delay: 25000,\n"
                            + "	timer: 1000,\n"
                            + "	animate: {\n"
                            + "		enter: 'animated fadeInDown',\n"
                            + "		exit: 'animated fadeOutUp'\n"
                            + "	},\n"
                            + "	icon_type: 'class',\n"
                            + "	template: '<div data-notify=\"container\" class=\"col-xs-11 col-sm-8 alert alert-{0}\" role=\"alert\">' +\n"
                            + "		'<button type=\"button\" aria-hidden=\"true\" class=\"close\" data-notify=\"dismiss\">×</button>' +\n"
                            + "		'<span data-notify=\"icon\"></span> ' +\n"
                            + "		'<span data-notify=\"title\">{1}</span> ' +\n"
                            + "		'<span data-notify=\"message\">{2}</span>' +\n"
                            + "	'</div>' \n"
                            + "});\n";
                } else if (diasRestantes > 10 && diasRestantes < 31) {
                    RESP += "$.notify({\n"
                            + "	// options\n"
                            + "	icon: 'fa fa-warning',\n"
                            + "	title: 'Vencimiento de e.Firma<br>',\n"
                            + "	message: 'La e.Firma de <b><u>" + firm.getNombre() + " " + firm.getPrimerApellido() + " " + firm.getSegundoApellido() + "</u></b> "
                            + "está a punto de caducar.<br><b>Días Restantes: " + diasRestantes + "<b>'\n"
                            + "},{\n"
                            + "	// settings\n"
                            + "	element: 'body',\n"
                            + "	position: null,\n"
                            + "	type: \"warning\",\n"
                            + "	allow_dismiss: true,\n"
                            + "	newest_on_top: false,\n"
                            + "	placement: {\n"
                            + "		from: \"top\",\n"
                            + "		align: \"right\"\n"
                            + "	},\n"
                            + "	offset: 20,\n"
                            + "	spacing: 10,\n"
                            + "	z_index: 1031,\n"
                            + "	delay: 25000,\n"
                            + "	timer: 1000,\n"
                            + "	animate: {\n"
                            + "		enter: 'animated fadeInDown',\n"
                            + "		exit: 'animated fadeOutUp'\n"
                            + "	},\n"
                            + "	icon_type: 'class',\n"
                            + "	template: '<div data-notify=\"container\" class=\"col-xs-11 col-sm-8 alert alert-{0}\" role=\"alert\">' +\n"
                            + "		'<button type=\"button\" aria-hidden=\"true\" class=\"close\" data-notify=\"dismiss\">×</button>' +\n"
                            + "		'<span data-notify=\"icon\"></span> ' +\n"
                            + "		'<span data-notify=\"title\">{1}</span> ' +\n"
                            + "		'<span data-notify=\"message\">{2}</span>' +\n"
                            + "	'</div>' \n"
                            + "});\n";
                }
            }
        } catch (SQLException ex) {
            RESP = "errorNotify|Error SQL al momento de consultar las notificaciones de firmantes: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "errorNotify|Error inesperado al momento de consultar las notificaciones de firmantes: " + accion_catch(ex);
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarNotifyFirmantes() !", ex);
                }
            }
        }
        return RESP;
    }

    public String descargarXmlMasivos() throws SQLException {
        String RESP = "";
        String cadenaIdCer = request.getParameter("cadenaIdCertificado");
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Certificados");
        bitacora.setMovimiento("Descarga Masiva XML");
        try {
            String[] idCertificados = cadenaIdCer.split("¬");
            for (String idCertificado : idCertificados) {
                String Query = "SELECT A.Matricula, TC.cveCarrera, CE.folioControl "
                        + " FROM TECertificadoElectronico AS CE "
                        + " JOIN Alumnos AS A ON A.ID_Alumno = CE.id_profesionista "
                        + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = CE.id_carrera "
                        + " WHERE ID_Certificado = " + idCertificado;
                con = conexion.GetconexionInSite();
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    RESP += rs.getString("folioControl") + "_" + rs.getString("Matricula") + "_" + rs.getString("cveCarrera") + ".xml¬";
                }
            }
            llenarNombreInstitucion();
            String save = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Certificados\\";
            //String save = System.getProperty("user.home") + "\\Downloads\\XML\\Certificados\\";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
            RESP = zip_File("Certiicados_" + sdf.format(new Date()), RESP.substring(0, RESP.length() - 1), save);
            bitacora.setInformacion("Descarga de archivo zip xml de certificados||Respuesta método: " + RESP);
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        } finally {
            con.close();
            pstmt.close();
            conexion.GetconexionInSite().close();
        }
        return "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Certificados\\" + RESP;
    }

    public void llenarNombreInstitucion() {
        PreparedStatement pstmt = null;
        Connection con = null;
        try {
            String Query = "SELECT nombreInstitucion FROM Configuracion_Inicial AS CI "
                    + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                    + " WHERE Id_Usuario = " + Id_Usuario;
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                NombreInstitucion = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @author Braulio Sorcia Función que permite generar un archivo zip que
     * contendrá los archivos a descargar por los clientes.
     * @param zipName Utilizado para el nombre que recibe nuestro archivo zip.
     * @param names Cadena que contiene los nombres de cada archivo que se desea
     * guardar en el zip. IMPORTANTE!!! El caracter para realizar el split puede
     * ser dinámico, mientras tanto será ¬
     * @param mainDirectory Ruta principal que contendrá el archivo zip, Así
     * como la ruta de los arcivos a ser insertados en el zip
     * @since 0.1
     * @return El nombre de nuestro archivo zip para que sea descargado desde el
     * cliente.
     */
    private String zip_File(String zipName, String names, String mainDirectory) {
        ZipOutputStream out = null;
        try {
            FileOutputStream zipFile = new FileOutputStream(new File(mainDirectory + zipName + ".zip"));
            out = new ZipOutputStream(zipFile);
            String[] arrayPaths = names.split("¬");
            for (int i = 0; i < arrayPaths.length; i++) {
                ZipEntry e = new ZipEntry(arrayPaths[i]);
                try {
                    out.putNextEntry(e);
                    FileInputStream pdfFile = new FileInputStream(new File(mainDirectory + arrayPaths[i]));
                    IOUtils.copy(pdfFile, out);  // this method belongs to apache IO Commons lib!
                    pdfFile.close();
                    out.closeEntry();
                } catch (IOException ex) {
                    Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "Ocurrió un error al armar el zip", ex);
                }
            }
            return zipName + ".zip";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "Ocurrió un error al tratar de encontrar el archivo zip", ex);
            return "error";
        } finally {
            try {
                out.finish();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el objeto ZipOutputStream", ex);
            }
        }
    }

    private String accion_catch(Exception ex) {
        String resp = "";
        try {
            resp = ex.toString().replace("'", "\"");
            Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            resp = "<h4 style='color:#e76d6d'>" + resp;
            resp += "</h4>";
        } catch (Exception e) {
            resp += "</h4><small class='text-primary'>No se ha insertado en Bitacora de errores</small>";
        }
        resp += "<br><br><small>Si continua con el problema, comuníquese con soporte técnico.</small> ";
        return resp;
    }

    private boolean[] validarInfoCertificado(String[] filaActual) {
        boolean valido = true;
        boolean filaVacia = false;
        boolean[] validaciones = new boolean[2];
        int contador = 0;
        for (int i = 0; i < filaActual.length; i++) {
            if ((filaActual[i] == null || filaActual[i].trim().equalsIgnoreCase("")) && (i != 2 && i != 3 && i != 7)) {
                valido = false;
                contador++;
            }
        }

        if (contador == filaActual.length - 3) {
            filaVacia = true;
        }

        validaciones[0] = valido;
        validaciones[1] = filaVacia;
        return validaciones;
    }

    private String consultarInfoCertificadoImportacionExcel(String matriculaAlumno, String idCarreraExcel, String lugarExpedicion) {
        String Query = "";
        String idAlumno = "";
        String idCarrera = "";
        PreparedStatement pstmt = null;
        Connection con = null;
        ResultSet rsRecord = null;
        float califAprobatoria = 0;
        int totalMaterias, materiasAsignadas = 0, materiasCalificadasProm = 0, numDecimales = 2;
        BigDecimal _promedioProv;
        BigDecimal _promedioReal;
        float promedio = 0;
        float creditosCarrera = 0;
        float creditosObtenidos = 0;
        Materia m = null;
        String RESP = "";
        try {
            con = conexion.GetconexionInSite();

            Query = "SELECT Id_Alumno FROM Alumnos WHERE matricula = ?";
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, matriculaAlumno);
            rsRecord = pstmt.executeQuery();
            if (rsRecord.next()) {
                idAlumno = rsRecord.getString(1);
            } else {
                return "noAlumno";
            }

            Query = "SELECT Id_Carrera,numDecimales FROM Carrera WHERE Id_Carrera_Excel = ?";
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idCarreraExcel);
            rsRecord = pstmt.executeQuery();
            if (rsRecord.next()) {
                idCarrera = rsRecord.getString(1);
                numDecimales = rsRecord.getInt(2);
            } else {
                return "noCarrera";
            }

            Query = "SELECT califMinimaAprobatoria,C.TotalCreditos "
                    + "FROM TETitulosCarreras ttc JOIN Carrera c ON ttc.Id_Carrera_Excel = c.Id_Carrera_Excel "
                    + "WHERE c.ID_Carrera = ?";
            //System.out.println(Query.replace("?", idTECarrera));
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idCarrera);
            rsRecord = pstmt.executeQuery();
            if (rsRecord.next()) {
                califAprobatoria = rsRecord.getFloat(1);
                creditosCarrera = rsRecord.getFloat("TotalCreditos");
                String creditosCarreraS = String.format(Locale.US, "%." + numDecimales + "f", creditosCarrera);
                creditosCarrera = new Float(creditosCarreraS);
            }
            Query = "SELECT TotalMaterias FROM CARRERA WHERE ID_CARRERA = " + idCarrera;

            rsRecord = null;
            pstmt = con.prepareStatement(Query);
            rsRecord = pstmt.executeQuery();
            if (rsRecord.next()) {
                totalMaterias = rsRecord.getInt("TotalMaterias");

                Query = "SELECT C.Calificacion, M.Creditos, M.Tipo  \n"
                        + "FROM Calificaciones AS C  \n"
                        + "JOIN Alumnos AS A ON A.ID_Alumno = " + idAlumno + "\n"
                        + "JOIN Carrera AS CA ON CA.ID_Carrera = " + idCarrera + "  JOIN Materias AS M ON M.ID_Materia = C.ID_Materia  \n"
                        + "WHERE C.ID_Alumno = " + idAlumno + " AND C.ID_Materia  \n"
                        + "IN (  SELECT ID_Materia   FROM Materias AS M   JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso   WHERE CU.ID_Carrera = " + idCarrera + " );";
                rsRecord = null;
                pstmt = con.prepareStatement(Query);
                rsRecord = pstmt.executeQuery();
                if (rsRecord.isBeforeFirst()) {
                    while (rsRecord.next()) {
                        m = new Materia();
                        float calif = rsRecord.getFloat("Calificacion");
                        m.setCreditos(String.format(Locale.US, "%." + numDecimales + "f", new Float(rsRecord.getString("Creditos"))));
                        m.setTipo(rsRecord.getString("Tipo"));

                        materiasAsignadas++;
                        if (!m.getTipo().equalsIgnoreCase("0") && !m.getTipo().equalsIgnoreCase("266")) {
                            promedio = Float.sum(promedio, calif);
                            materiasCalificadasProm++;
                            if ((calif >= califAprobatoria)) {
                                creditosObtenidos += Float.valueOf(m.getCreditos());
                            }
                        }
                    }
                    Query = "SELECT  CAST(SUM(CASE WHEN m.tipo <>266  THEN C.Calificacion ELSE 0 END) AS decimal(5,2)) promedio"
                            + " FROM Calificaciones AS C "
                            + " JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno "
                            + " JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera "
                            + " JOIN Materias AS M ON M.ID_Materia = C.ID_Materia "
                            + " WHERE C.ID_Alumno = " + idAlumno + " AND C.ID_Materia "
                            + " IN ("
                            + "  SELECT ID_Materia "
                            + "  FROM Materias AS M "
                            + "  JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso "
                            + "  WHERE CU.ID_Carrera = " + idCarrera
                            + " );";
                    pstmt = con.prepareStatement(Query);
                    rsRecord = pstmt.executeQuery();
                    String _promedio = "";
                    if (rsRecord.next()) {
                        _promedio = rsRecord.getString("promedio");
                    }
                    _promedioProv = new BigDecimal(String.valueOf(_promedio));
                    _promedioReal = _promedioProv.divide(new BigDecimal(materiasCalificadasProm), 2, BigDecimal.ROUND_FLOOR);

                    String promAuxiliar = String.valueOf(_promedioReal);

                    String creditosObtenidosS = String.format(Locale.US, "%." + numDecimales + "f", creditosObtenidos);
                    creditosObtenidos = new Float(creditosObtenidosS);
                    double promedioProv;

                    if (promAuxiliar.contains(".")) {
                        if (promAuxiliar.split("\\.")[1].length() <= 2) {
                            promedioProv = Double.parseDouble(promAuxiliar);
                        } else {
                            promedioProv = Math.floor(promedio * 100) / 100;
                        }
                    } else {
                        promedioProv = Math.floor(promedio * 100) / 100;
                    }

                    RESP += totalMaterias + "~";
                    RESP += materiasAsignadas + "~";
                    if (materiasAsignadas == 0) {
                        RESP += "0~";
                    } else {
                        //Se agrega el LOCALE.US por que si no se especifica regresa una , en lugar de un punto.
                        RESP += String.format(Locale.US, "%.2f", promedioProv) + "~";
                    }
                    if (creditosCarrera == creditosObtenidos || creditosObtenidos > creditosCarrera) {
                        RESP += "79";
                    } else {
                        RESP += "80";
                    }
                    Query = "SELECT Id_Carrera FROM TETITULOSCARRERAS WHERE Id_Carrera_Excel = " + idCarreraExcel;
                    pstmt = con.prepareStatement(Query);
                    rsRecord = pstmt.executeQuery();
                    if (rsRecord.next()) {
                        idCarrera = rsRecord.getString(1);
                    } else {
                        return "noCarrera";
                    }

                    RESP += "~" + creditosCarrera + "~" + creditosObtenidos + "~" + idAlumno + "~" + idCarrera;

                    Query = "SELECT idEntidadFederativa FROM TEEntidad WHERE EntidadFederativa = '" + lugarExpedicion + "'";
                    pstmt = con.prepareStatement(Query);
                    rsRecord = pstmt.executeQuery();
                    if (rsRecord.next()) {
                        RESP += "~" + rsRecord.getString(1);
                    }

                } else {
                    return "SinCalificaciones";
                }
            } else {
                return "SinMaterias";
            }
        } catch (SQLException e) {

        } finally {
            try {
                pstmt.close();
                rsRecord.close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP + "~" + numDecimales;
    }

    private String validarFirmantes(String curp, Connection con) {
        //vamos a validar los firmantes ingresados.
        String cadenaFirmantes = "";
        ResultSet rsRecord = null;
        try {
            String query = "SELECT Id_Firmante FROM TEFirmantes WHERE CURP = '" + curp + "'";
            rsRecord = con.createStatement().executeQuery(query);
            if (rsRecord.next()) {
                cadenaFirmantes += rsRecord.getString(1);
            } else {
                cadenaFirmantes = "noFirmante";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return cadenaFirmantes;
    }
}
