// VERSION: DIGISEP GENERAL
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Alumno;
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Carrera;
import com.ginndex.titulos.modelo.Persona;
import com.ginndex.titulos.modelo.TEEntidad;
import com.ginndex.titulos.modelo.TEEstudioAntecedente;
import com.ginndex.titulos.modelo.TEFirmante;
import com.ginndex.titulos.modelo.TEFundamentos;
import com.ginndex.titulos.modelo.TEModalidad;
import com.ginndex.titulos.modelo.TEMotivosCancelacion;
import com.ginndex.titulos.modelo.TETituloElectronico;
import com.ginndex.titulos.modelo.TETitulosCarreras;
import com.ginndex.titulos.soap.CancelaTituloElectronicoProductivo;
import com.ginndex.titulos.soap.CargaTituloElectronico;
import com.ginndex.titulos.soap.CargaTituloElectronicoProductivo;
import com.ginndex.titulos.soap.ConsultaProcesoTituloElectronico;
import com.ginndex.titulos.soap.ConsultaProcesoTituloElectronicoProductivo;
import com.ginndex.titulos.soap.DescargaTituloElectronico;
import com.ginndex.titulos.soap.DescargaTituloElectronicoProductivo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.ssl.Base64;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.xml.sax.SAXException;
//import java.util.Base64;

/**
 *
 * @author Paola Alonso
 */
public class CTitulos {

    HttpServletRequest request;
    HttpServletResponse response;
    Connection con;
    PreparedStatement pstmt;
    CallableStatement cstmt;
    String Bandera;
    String Id_Usuario;
    String rutaArchivo;
    String nombreArchivo;
    ResultSet rs;
    String usuario;
    Cxml_titulo_electronico titElec;
    CConexion conexion;
    private String permisos;
    private CClavesActivacion desencriptador;
    String NombreInstitucion;
    String nombreLogo;
    String nombreFirma;
    private TEFirmante firmante;
    private List<TEFirmante> lstFirmantes;
    Bitacora bitacora;
    CBitacora cBitacora;
    String usuarioSEP;
    String passwordSEP;
    Workbook libro;
    private final int FILAS_DESPUES_ENCABEZADOS = 1;
    private final int COLUMNAS_A_LEER_EXCEL = 21;
    private String estatusMET;
    
    private String mensajeMET;

    public String getMensajeMET() {
        return mensajeMET;
    }

    public void setMensajeMET(String mensajeMET) {
        this.mensajeMET = mensajeMET;
    }
    
    

    public String getEstatusMET() {
        return estatusMET;
    }

    public void setEstatusMET(String estatusMET) {
        this.estatusMET = estatusMET;
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

    public Cxml_titulo_electronico getTitElec() {
        return titElec;
    }

    public void setTitElec(Cxml_titulo_electronico titElec) {
        this.titElec = titElec;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public PreparedStatement getPstmt() {
        return pstmt;
    }

    public void setPstmt(PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }

    public CallableStatement getCstmt() {
        return cstmt;
    }

    public void setCstmt(CallableStatement cstmt) {
        this.cstmt = cstmt;
    }

    public CConexion getConexion() {
        return conexion;
    }

    public void setConexion(CConexion conexion) {
        this.conexion = conexion;
    }

    public String EstablecerAcciones() throws UnsupportedEncodingException, SQLException, FileUploadException, Exception {
        String RESP = "";
        //long startTime = System.nanoTime();
        
        setBandera(request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");

        HttpSession sessionOk = request.getSession();
        usuario = sessionOk.getAttribute("NombreUsuario").toString();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        String folioControl= request.getParameter("folioControl");

        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        permisos = cPermisos.obtenerPermisos("Titulos");
        llenarNombreInstitucion();

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
                RESP = cargarListaCarreras() + "~"
                        + cargarEntidadFederativa() + "~"
                        + cargarFirmantes() + "~"
                        + cargarFundamentoLegal() + "~"
                        + cargarModalidadTitulacion() + "~"
                        + cargarEstudioAntecedente() + "~"
                        + CargarInstitucion() + "~" + cargarNotifyFirmantes() + "~" + cargarMotivosCancelacion();
                break;
            case "2":
                RESP = consultarAlumno();
                break;
            case "3":
                RESP = addTitulo();
                break;
            case "4":
                RESP = updateTitulo();
                break;
            case "5":
                RESP = deleteTitulo();
                break;
            case "6":
                RESP = consultarTitulo();
                break;
            case "7":
                RESP = generarXML();
                break;
            case "8":
                RESP = descargarXML();
                break;
            case "9":
                RESP = descargarZipsMasivos();
                break;
            case "10":
                RESP = generarXmlMasivos();
                break;
            case "11":
                RESP = cargarTitulosWebService(request.getParameter("idTitulo"));
                break;
            case "12":
                RESP = consultarTitulosWebService();
                break;
            case "13":
                RESP = cargarTitulosWebServiceProductivo(request.getParameter("idTitulo"));
                break;
            case "14":
                RESP = consultarTitulosWebServiceProductivo();
                break;
            case "15":
                RESP = cancelarTitulosWebServiceProductivo();
                break;
            case "16":
                RESP = consultarInfoTituloCancelado();
                break;
            case "17":
                RESP = llenarFormatoTituloMasivo();
                break;
            case "18":
                RESP = descargarXmlMasivos();
                break;
            case "19":
                RESP = consultarTablaTitulos();
                break;
            case "20":
                RESP = consultarLibroFoja();
                break;
            case "21":
                RESP = modificarLibroFoja();
                break;
            case "22":
                RESP = cargarTitulosMasivosWebService();
                break;
            case "23":
                RESP = cargarTitulosMasivosWebServiceProductivo();
                break;
            /*case "22":
                RESP = verificarTituloAutenticado();
                break;*/
            case "24":
                RESP = leerZip(folioControl);
                break;
            case "excel":
                RESP = ImportarTitulosExcel();
                break;
        }
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime);
        //System.out.println("TIEMPO:" + duration);
        return RESP;
    }

    public String getDat(List<FileItem> partes) throws Exception {

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

                    if (input_actual.equalsIgnoreCase("fileTitulos")) {

                        try {
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            rutaArchivo = ubicacionarchivo + "\\ArchivoTitulo_" + uploaded.getName();
                            nombreArchivo = "ArchivoTitulo_" + uploaded.getName();
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                nombreArchivo = "";
                            } else {
                                File archivoServer = new File(rutaArchivo);
                                uploaded.write(archivoServer);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Logger.getLogger(CTitulos.class.getName()).log(Level.INFO, rutaArchivo);
                        }
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "OCURRIÓ UN ERROR AL RECUPERAR DESDE EL FORM::---------------->", ex);
            }
        }
        return Bandera;
    }

    private String ImportarTitulosExcel() throws SQLException {
        //String ruta = System.getProperty("user.home") + "\\Downloads\\PruebasCarreras\\" + nombreArchivo;
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + nombreArchivo;
        String RESP = "";
        String cadenaIdTitulos = "";
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
        bitacora.setModulo("Carga Titulos Masivos");
        bitacora.setMovimiento("Inserción");
        boolean isNotFound = false;
        String[] celdaALetra = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U"};
        String[] encabezadosEsperados = {
            "* Matrícula del alumno",
            "* Id de Carrera",
            "Nombre Carrera",
            "Nombre del Alumno",
            "* Modalidad de Titulación",
            "* Lugar de Expedición",
            "* Servicio Social",
            "* Fundamento Legal",
            "* Fecha Expedición",
            "Fecha Examen Profesional",
            "Fecha Exención Examen Profesional",
            "* Institución de Procedencia",
            "* Tipo Estudio Antecedente",
            "* Entidad Antecedente",
            "Fecha Inicio Antecedente",
            "* Fecha Fin Antecedente",
            "No. Cédula",
            "* CURP Firmante",
            "Nombre Firmante",
            "CURP Firmante",
            "Nombre Firmante"
        };
        
        String cadenaCeldasVacias = "";
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
                Logger.getLogger(CTitulos.class.getName()).log(Level.INFO, "FILAS LEÍDAS EN LA HOJA: " + (i + 1) + " ====== " + (rows + 1) + " =====");
                //Si solamente lee 3 filas significa que la hoja esta vacía, no tiene caso continuar
                if(rows == 3){
                    RESP = "sinRegistros||<p style='color: #545454;'>El archivo no contiene registros a importar.</p>"
                            + "<p>Verifica la información ingresada.</p>"
                            + "<p>No. Hoja: " + (i + 1) + "</p>"
                            + "<p>Nombre Hoja: " + libro.getSheetName(i) + "</p>";
                        return RESP;
                }
                for (int j = 3; j <= rows; j++) {
                    String[] filaActual = new String[COLUMNAS_A_LEER_EXCEL];
                    Row rowActual = hojaActual.getRow(j);
                    
                    //Primero validamos que los encabezados esten bien
                    if(j == 3){
                        if(rowActual == null){ //Significa que borro la fila de encabezados
                            RESP = "formatoInvalido||<p style='color: #545454;'>El formato del archivo seleccionado para subir títulos es inválido.</p><p><strong>Nombre Hoja: " + libro.getSheetName(i) + "</strong></p><p><strong>No. Hoja: " + (i+1)  + "</strong></p><p style='color: #545454;'>La <strong>fila 4</strong> correspondiente a los encabezados fue eliminada, Por favor, no modifique la estructura del archivo.<p>";
                            return RESP;
                        }
                        
                        //Metemos los encabezados a un arreglo para posteriormente verificar si esta completo
                        String encabezados[] = new String[COLUMNAS_A_LEER_EXCEL];
                        for (int k = 0; k < COLUMNAS_A_LEER_EXCEL; k++) {
                            Cell cellActual = rowActual.getCell(k);
                            
                            if(cellActual != null){
                                encabezados[k] = cellActual.getStringCellValue();
                            }else{
                                encabezados[k] = "";
                            }
                        }
                        
                        //Verificamos si no se modifico alguna celda del encabezado
                        for (int z = 0; z < encabezadosEsperados.length; z++) {
                            if (!encabezados[z].equalsIgnoreCase(encabezadosEsperados[z])) {
                                RESP = "formatoInvalido||<p style='color: #545454;'>El formato del archivo seleccionado para subir títulos es inválido.</p><p><strong>Nombre Hoja: " + libro.getSheetName(i) + "</strong></p><p><strong>No. Hoja: " + (i+1)  + "</strong></p><p style='color: #545454;'>La <strong>celda " + celdaALetra[z] + "</strong> en la <strong>fila 4</strong> fue modificada. Por favor, no modifique la estructura del archivo.</p>";
                                return RESP;
                            }
                        }
                    }else{
                        //Este else comienza desde la fila 5, es donde comienza la informacion de los titulos
                        
                        //Si la fila esta completamente vacia, simplemente pasamos a la siguiente no tiene caso continuar evaluando esta fila
                        if (rowActual == null) {
                            continue;
                        }
                        
                        //Comenzamos a iterar sobre las celdas de las filas
                        for (int k = 0; k < COLUMNAS_A_LEER_EXCEL; k++) {
                            Cell cellActual = rowActual.getCell(k); //Obtenemos la celda
                            
                            if(cellActual == null){
                                filaActual[k] = "";
                            }else if (((k >= 8 && k < 11) || ((k >= 14 && k <= 15))) && (j >= 4)) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                sdf.setLenient(false);
                                if (cellActual != null
                                        && cellActual.getCellType() != Cell.CELL_TYPE_BLANK
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
                                            RESP = "formatoInvalido||dateInvalid_<strong>" + ex.getMessage() + "<p>Hoja no. " + (i + 1) + " " + libro.getSheetName(i) + " <p>Fila no. " + (j + 1) + "<strong>";
                                            return RESP;
                                        }
                                    }
                                    if (fecha != null) {
                                        filaActual[k] = sdf.format(fecha);
                                    } else {
                                        filaActual[k] = "";
                                    }
                                } else {
                                    if (k == 9 || k == 10 || k == 14 || k == 16) {
                                        if (cellActual == null || cellActual.getCellType() == Cell.CELL_TYPE_BLANK) {
                                            filaActual[k] = "";
                                        }
                                    } else {
                                        RESP = "formatoInvalido||dateInvalid_<strong>Celda con formato incorrecto o en blanco<p>Hoja no. " + (i + 1) + "<p>Fila no. " + (j + 1) + "<strong>";
                                        return RESP;
                                    }
                                }
                            } else {                      

                                switch (cellActual.getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        cellActual.setCellType(1);
                                        filaActual[k] = cellActual.getStringCellValue() + "".trim();
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        filaActual[k] = cellActual.getStringCellValue().trim();
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        filaActual[k] = "".trim();
                                        break;
                                }
                            }
                            
                            if (k == COLUMNAS_A_LEER_EXCEL - 1) {
                                //Se recibe un objeto ( un array con las validaciones y una lista con las celdas que estan vacias )
                                Object[] resultado = validarCampos(filaActual);
                                //Metemos las validaciones en el array
                                /*
                                    Pos 0 - Esta completo
                                    Pos 1 - Fila vacia
                                */
                                boolean[] valido = (boolean[]) resultado[0];
                                //Metemos las lista

                                //Lista con las celdas que estan vacias

                                List<Integer> celdasVacias = (List<Integer>) resultado[1];

                                if(celdasVacias.size() > 0){
                                    for (int l = 0; l < celdasVacias.size(); l++) {
                                        cadenaCeldasVacias += celdaALetra[celdasVacias.get(l)] + ",";
                                    }
                                }

                                //Validacion para verificar si la fila esta vacia
                                if(valido[1]){
                                    RESP = "infoTituloIncompleta||<p style='color: #545454;'>Se detectó que la <strong>fila " + (j + 1) + "</strong> esta vacía.</p><p><strong>Nombre Hoja: " + libro.getSheetName(i) + "</strong></p><p><strong>No. Hoja: " + (i+1)  + "</strong></p><p style='color: #545454;'>Por favor verifica la información y vuelva a intentarlo nuevamente.</p>";
                                    return RESP;
  
                                }

                                //Validacion para verificar si la fila esta completa
                                if(!valido[0]){
                                    RESP = "infoTituloIncompleta||<p style='color: #545454;'>Se detectó que en la <strong>fila " + (j + 1) + "</strong> hay celdas vacías.</p><p><small>Celdas vacías: <strong>" + cadenaCeldasVacias + "</strong></small></p><p><strong>Nombre Hoja: " + libro.getSheetName(i) + "</strong></p><p><strong>No. Hoja: " + (i+1)  + "</strong></p><p style='color: #545454;'>Por favor verifica la información y vuelva a intentarlo nuevamente.</p>";
                                    return RESP;
                                }
                                
                                String validarFechas = validarFechasExamen(filaActual[9], filaActual[10]);
                                
                                if(validarFechas.contains("fechasExamenInvalidas")){
                                    return validarFechas + "<p style='color: #545454;'><strong>Nombre Hoja: </strong>" + libro.getSheetName(i) + "</p>"
                                            + "<p style='color: #545454;'><strong>NO. Hoja: </strong>" + (i+1) + "</p>"
                                            + "<p style='color: #545454;'><strong>Fila: </strong>" + (j + 1) + "</p>";
                                }
                            }
                        }
   
                        //Comenzamos con la insercion del titulo
                        String cadenaFirmantes = validarFirmantes(filaActual[17], filaActual[19], j);
                        if (cadenaFirmantes.contains("noFirmante")) {
                            RESP = "noFirmante||"
                                    + "<p style='color: #545454;'>La CURP ingresada en la <b>hoja " + (i + 1) + "</b> con el <b>nombre " + libro.getSheetName(i) + "</b> en la <b>fila " + (j + 1) + "</b> no corresponde a ningún firmante dentro del sistema. <br>Verifica el archivo e intenta de nuevo.</p>";
                            break;
                        }
                        Alumno a = new Alumno();
                        TETitulosCarreras ttc = new TETitulosCarreras();
                        TETituloElectronico tee = new TETituloElectronico();

                        a.setMatricula(filaActual[0]); 
                        ttc.setId_Carrera_Excel(filaActual[1]);
                        ttc.setNombreCarrera(filaActual[2]);

                        tee.setID_ModalidadTitulacion(filaActual[4]);
                        tee.setID_EntidadFederativa(filaActual[5]);
                        tee.setCumplioServicioSocial(filaActual[6]);
                        tee.setID_FundamentoLegalServicioSocial(filaActual[7]);
                        tee.setFechaExpedicion(filaActual[8]);
                        tee.setFechaExamenProfesional(filaActual[9]);
                        tee.setFechaExtensionExamenProfesional(filaActual[10]);
                        tee.setInstitucionProcedencia(filaActual[11]);
                        tee.setID_TipoEstudioAntecedente(filaActual[12]);
                        tee.setID_EntidadFederativaAntecedente(filaActual[13]);
                        tee.setFechaInicioAntecedente(filaActual[14]);
                        tee.setFechaTerminacionAntecedente(filaActual[15]);
                        tee.setNoCedula(filaActual[16]);
                        tee.setVersion("1.0");

                        tee.setAlumno(a);
                        tee.setCarrera(ttc);
                        
                        //Buscamos las fechas de inicio del alumno
                        conexion = new CConexion();
                        conexion.setRequest(request);
                        con = null;
                        pstmt = null;

                        String QueryAlumno = "SELECT Id_Alumno, FechaInicioCarrera, FechaFinCarrera FROM Alumnos WHERE ID_Carrera = (SELECT ID_Carrera FROM Carrera WHERE Id_Carrera_Excel = " + tee.getCarrera().getId_Carrera_Excel() + ") AND Matricula = '" + tee.getAlumno().getMatricula() + "'";
                        con = conexion.GetconexionInSite();
                        pstmt = con.prepareStatement(QueryAlumno);
                        rs = pstmt.executeQuery();
                        
                        if(!rs.isBeforeFirst()){
                            RESP = "noAlumno||"
                                    + "<p style='color: #545454;'>No se encontraron registros para el alumno ingresado con la <strong>matrícula " + tee.getAlumno().getMatricula() + "</strong>. Por favor verifica que la matrícula y la carrera del alumno coincidan.</p>"
                                    + "<p><strong>Nombre Hoja: </strong>" + libro.getSheetName(i) + "</p>"
                                    + "<p><strong>No. Hoja: </strong>" + (i + 1)  + "</p>"
                                    + "<p style='color: #545454;'><strong>Fila: </strong>" + (j + 1) + "</p>";
                            return RESP;
                        }

                        List<Alumno> lstAlumno = new ArrayList<>();
                        while(rs.next()){
                            Alumno alumno = new Alumno();

                            alumno.setId_Alumno(rs.getString("Id_Alumno"));
                            alumno.setFechaInicioCarrera(rs.getString("FechaInicioCarrera"));
                            alumno.setFechaFinCarrera(rs.getString("FechaFinCarrera"));

                            lstAlumno.add(alumno);
                        }
                        
                        /*
                            * @matriculaAlumno varchar(100),
                            * @idCarreraExcel int,
                            * @version varchar(5),
                            * @fechaInicio varchar(15),
                            * @fechaFin varchar(15),
                            * @modalidadTitulacion varchar(1000),
                            * @fechaExamenProfesional varchar(15),
                            * @fechaExcencionExamenProfesional varchar(15),
                            * @servicioSocial int,
                            * @fundamentoLegalServicio varchar(1000),
                            * @lugarExpedicion varchar(150),
                            * @institucionProcedencia varchar(500),
                            * @tipoEstudioAntecedente varchar(500),
                            * @entidadAntecedente varchar(150),
                            * @fechaInicioAntecedente varchar(15),
                            * @fechaFinAntecedente varchar(15),
                            * @noCedula varchar(8),
                            * @idUsuario int,
                            * @fechaExpedicion varchar(10),
                            * @respuesta varchar(16) OUT
                        */
                        String Query = "{call Add_Titulo_Excel (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                        con = conexion.GetconexionInSite();
                        cstmt = con.prepareCall(Query);
                        cstmt.setString(1, tee.getAlumno().getMatricula());
                        cstmt.setString(2, tee.getCarrera().getId_Carrera_Excel());
                        cstmt.setString(3, "1.0");
                        cstmt.setString(4, lstAlumno.get(0).getFechaInicioCarrera().trim().equalsIgnoreCase("") ? null : lstAlumno.get(0).getFechaInicioCarrera().trim());
                        cstmt.setString(5, lstAlumno.get(0).getFechaFinCarrera().trim());
                        cstmt.setString(6, tee.getID_ModalidadTitulacion());
                        cstmt.setString(7, (tee.getFechaExamenProfesional().trim().equalsIgnoreCase("") ? null : tee.getFechaExamenProfesional().trim()));
                        cstmt.setString(8, (tee.getFechaExtensionExamenProfesional().trim().equalsIgnoreCase("") ? null : tee.getFechaExtensionExamenProfesional().trim()));
                        cstmt.setInt(9, tee.getCumplioServicioSocial().contains("NO") ? 0 : 1);
                        cstmt.setString(10, tee.getID_FundamentoLegalServicioSocial());
                        cstmt.setString(11, tee.getID_EntidadFederativa());
                        cstmt.setString(12, tee.getInstitucionProcedencia());
                        cstmt.setString(13, tee.getID_TipoEstudioAntecedente());
                        cstmt.setString(14, tee.getID_EntidadFederativaAntecedente());
                        cstmt.setString(15, (tee.getFechaInicioAntecedente().trim().equalsIgnoreCase("") ? null : tee.getFechaInicioAntecedente()));
                        cstmt.setString(16, tee.getFechaTerminacionAntecedente());
                        cstmt.setString(17, (tee.getNoCedula().trim().equalsIgnoreCase("") ? null : tee.getNoCedula()));
                        cstmt.setInt(18, Integer.valueOf(Id_Usuario));
                        cstmt.setString(19, tee.getFechaExpedicion());
                        cstmt.registerOutParameter(20, java.sql.Types.VARCHAR);

                        cstmt.execute();

                        RESP = cstmt.getString(20);
                        
                        if(RESP.contains("tituloActivo")){
                            RESP = "tituloActivo||"
                                    + "<p style='color: #545454;'>La información ingresada coincide con un registro existente, puedes editar su información o eliminarlo y generar un nuevo registro.</p>"
                                    + "<p><strong>Nombre Hoja: </strong>" + libro.getSheetName(i) + "</p>"
                                    + "<p><strong>No. Hoja: </strong>" + (i + 1)  + "</p>"
                                    + "<p style='color: #545454;'><strong>Fila: </strong>" + (j + 1) + "</p>";
                            return RESP;
                        }
                        if(RESP.contains("noCarrera")){
                            RESP = "noCarrera||"
                                    + "<p style='color: #545454;'>El id de carrera no coincide con un registro existente para el alumno con matricula: <b>" + tee.getAlumno().getMatricula() + "</b>. <br>Verifica la información ingresada.</p>"
                                    + "<p><strong>Nombre Hoja: </strong>" + libro.getSheetName(i) + "</p>"
                                    + "<p><strong>No. Hoja: </strong>" + (i + 1)  + "</p>"
                                    + "<p style='color: #545454;'><strong>Fila: </strong>" + (j + 1) + "</p>";
                            return RESP;
                        }
                        if(RESP.contains("noAlumno")){
                            RESP = "noAlumno||"
                                    + "<p style='color: #545454;'>No se encontraron registros para el alumno ingresado. Por favor verifica la matrícula y la carrera del alumno coincidan.</p>"
                                    + "<p><strong>Nombre Hoja: </strong>" + libro.getSheetName(i) + "</p>"
                                    + "<p><strong>No. Hoja: </strong>" + (i + 1)  + "</p>"
                                    + "<p style='color: #545454;'><strong>Fila: </strong>" + (j + 1) + "</p>"
                                    + "<p style='color: #545454;'><strong>Matrícula: </strong> " + tee.getAlumno().getMatricula() + "</p>";
                            return RESP;
                        }
                        if(RESP.contains("sinConfiguracion")){
                            RESP = "sinConfiguracion||"
                                    + "<p style='color: #545454;'>No se tiene ingresada la configuración inicial. Por favor realiza la configuración y vuelve a intentarlo.</p>";
                            return RESP;
                        }
                        
                        if(RESP.contains("Error:")){
                            RESP = "error||"
                                    + "<p style='color: #545454;'>Ocurrió un error al registrar el titulo. Verifica la información y vuelve a intentarlo.</p>"
                                    + "<p><strong>Nombre Hoja: </strong>" + libro.getSheetName(i) + "</p>"
                                    + "<p><strong>No. Hoja: </strong>" + (i + 1)  + "</p>"
                                    + "<p style='color: #545454;'><strong>Fila: </strong>" + (j + 1) + "</p>";
                            return RESP;
                        }

                        String[] datosTitulo = RESP.split(",");
                        String[] idFirmante = cadenaFirmantes.split(",");
                        for (int k = 0; k < idFirmante.length; k++) {
                            con.close();
                            cstmt.close();
                            con = conexion.GetconexionInSite();
                            cstmt = null;
                            //Query = "SET LANGUAGE 'español'; EXECUTE Add_FirmanteTitulo " + datosTitulo[0] + "," + idFirmante[i];
                            Query = "{call Add_FirmanteTitulo (?,?)}";
                            cstmt = con.prepareCall(Query);
                            cstmt.setInt(1, Integer.valueOf(datosTitulo[0]));
                            cstmt.setInt(2, Integer.valueOf(idFirmante[k]));
                            cstmt.execute();
                            if (cstmt.getResultSet() == null && cstmt.getUpdateCount() != -1) {
                                RESP = "success";
                                cadenaIdTitulos += datosTitulo[0] + ",";
                            } else {
                                return "errorFirmantes||" + (i + 1) + "||" + (k + 1);
                            }
                        }
                        bitacora.setInformacion("Registro de Titulo||Respuesta Método: " + RESP);
                        cBitacora = new CBitacora(bitacora);
                        cBitacora.setRequest(request);
                        cBitacora.addBitacoraGeneral();
                        
                    }
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
        return RESP + (!cadenaIdTitulos.trim().equalsIgnoreCase("") ? "||" + cadenaIdTitulos.trim() : "");
    }

    private String addTitulo() {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        rs = null;
        PreparedStatement pstmt = null;

        String idAlumno = request.getParameter("idAlumno");
        String idCarreraTitulo = request.getParameter("idCarreraTitulo");
        String idCarreraEscolar = request.getParameter("idCarreraEscolar");
        String idModalidad = request.getParameter("idModalidad");
        String idFundamento = request.getParameter("idFundamento");
        String idLugarExpedicion = request.getParameter("idLugarExpedicion");
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");
        String fechaExpedicion = request.getParameter("fechaExpedicion");
        String servicioSocial = request.getParameter("servicioSocial");
        String fechaExamenProfesional = request.getParameter("fechaExamenProfesional");
        String fechaExencionExamenProfesional = request.getParameter("fechaExencionExamenProfesional");
        String institucionProcedencia = request.getParameter("institucionProcedencia");
        String idTipoEstudioProcedencia = request.getParameter("idTipoEstudioAntecedente");
        String idEntidadEstudioAntecedente = request.getParameter("idEntidadEstudioAntecedente");
        String fechaInicioAntecedente = request.getParameter("fechaInicioAntecedente");
        String fechaFinAntecedente = request.getParameter("fechaFinAntecedente");
        String noCedula = request.getParameter("noCedula");
        String idsFirmantes = request.getParameter("idsFirmantes");
        bitacora = new Bitacora();
        cBitacora = new CBitacora(bitacora);
        cBitacora.setRequest(request);
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Inserción");

        try {
//            String Query = "SET LANGUAGE 'español'; EXECUTE ADD_Titulo " + idAlumno + "," + idCarreraTitulo + ",'" + "1.0"
//                    + "','" + fechaInicio + "','" + fechaFin + "'," + idModalidad + ",'" + fechaExamenProfesional + "','" + fechaExencionExamenProfesional + "',"
//                    + servicioSocial + "," + idFundamento + ",'" + idLugarExpedicion + "','" + institucionProcedencia + "'," + idTipoEstudioProcedencia + ",'"
//                    + idEntidadEstudioAntecedente + "','" + fechaInicioAntecedente + "','" + fechaFinAntecedente + "','" + noCedula + "'";
            String Query = "{call ADD_Titulo (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.parseInt(idAlumno));
            cstmt.setInt(2, Integer.parseInt(idCarreraTitulo));
            cstmt.setString(3, "1.0");
            cstmt.setString(4, (fechaInicio.trim().equalsIgnoreCase("") ? null : fechaInicio));
            cstmt.setString(5, fechaFin);
            cstmt.setInt(6, Integer.parseInt(idModalidad));
            cstmt.setString(7, (fechaExamenProfesional.trim().equalsIgnoreCase("") ? null : fechaExamenProfesional));
            cstmt.setString(8, (fechaExencionExamenProfesional.trim().equalsIgnoreCase("") ? null : fechaExencionExamenProfesional));
            cstmt.setInt(9, Integer.parseInt(servicioSocial));
            cstmt.setInt(10, Integer.parseInt(idFundamento));
            cstmt.setString(11, idLugarExpedicion);
            cstmt.setString(12, institucionProcedencia);
            cstmt.setInt(13, Integer.parseInt(idTipoEstudioProcedencia));
            cstmt.setString(14, idEntidadEstudioAntecedente);
            cstmt.setString(15, (fechaInicioAntecedente.trim().equalsIgnoreCase("") ? null : fechaInicioAntecedente));
            cstmt.setString(16, fechaFinAntecedente);
            cstmt.setString(17, (noCedula.trim().equalsIgnoreCase("") ? null : noCedula));
            cstmt.setInt(18, Integer.valueOf(Id_Usuario));
            cstmt.setString(19, fechaExpedicion);
            cstmt.registerOutParameter(20, java.sql.Types.VARCHAR);

            cstmt.execute();

            RESP = cstmt.getString(20);
            String aux = RESP;
            if (!RESP.contains("tituloActivo") && !RESP.contains("Error:") && !RESP.contains("sinConfiguracion")) {
                bitacora.setInformacion("Registro de Titulo||Respuesta Método: " + RESP + "||"
                        + idAlumno + "," + idCarreraTitulo + ",1.0," + fechaInicio + "," + fechaFin + "," + idModalidad + "," + fechaExamenProfesional
                        + "," + fechaExencionExamenProfesional + "," + servicioSocial + "," + idFundamento + "," + idLugarExpedicion
                        + "," + institucionProcedencia + "," + idTipoEstudioProcedencia + "," + idEntidadEstudioAntecedente + "," + fechaInicioAntecedente
                        + "," + fechaFinAntecedente + "," + noCedula + "," + Id_Usuario + "," + fechaExpedicion);
                cBitacora.addBitacoraGeneral();
                String[] datosTitulo = RESP.split(",");
                String[] idFirmante = idsFirmantes.split(",");
                bitacora = new Bitacora();
                bitacora.setId_Usuario(Id_Usuario);
                bitacora.setModulo("Firmante Titulos");
                bitacora.setMovimiento("Inserción");
                for (int i = 0; i < idFirmante.length; i++) {
                    con.close();
                    cstmt.close();
                    con = conexion.GetconexionInSite();
                    cstmt = null;
                    //Query = "SET LANGUAGE 'español'; EXECUTE Add_FirmanteTitulo " + datosTitulo[0] + "," + idFirmante[i];
                    Query = "{call Add_FirmanteTitulo (?,?)}";
                    cstmt = con.prepareCall(Query);
                    cstmt.setInt(1, Integer.valueOf(datosTitulo[0]));
                    cstmt.setInt(2, Integer.valueOf(idFirmante[i]));
                    cstmt.execute();
                    if (cstmt.getResultSet() == null && cstmt.getUpdateCount() != -1) {
                        RESP = "success";
                        bitacora.setInformacion("Registro de Firmante Titulo||" + datosTitulo[0] + "," + idFirmante[i]);
                        cBitacora = new CBitacora(bitacora);
                        cBitacora.setRequest(request);
                        cBitacora.addBitacoraGeneral();
                    } else {
                        return "errorFirmantes";
                    }
                }
                RESP += "," + aux;

                Query = "SELECT GradoObtenidoM, GradoObtenidoF FROM TETitulosCarreras WHERE ID_Carrera = " + idCarreraTitulo;
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    if (rs.getString(1) != null && rs.getString(2) != null) {
                        RESP += ",1";
                    } else {
                        RESP += ",0";
                    }
                } else {
                    RESP += ",0";
                }
					
											  
														 
				 
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL insertar el título electrónico: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException insertar el título electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al insertar el título electrónico: " + accion_catch(ex);
        } finally {
            try {
                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
                if (pstmt != null && !pstmt.isClosed()) {
                    pstmt.close();
                }
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }

                if (conexion.GetconexionInSite() != null && !conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String updateTitulo() {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        CallableStatement cstmt = null;
        String idTitulo = request.getParameter("idTitulo");
        String idModalidad = request.getParameter("idModalidad");
        String idFundamento = request.getParameter("idFundamento");
        String idLugarExpedicion = request.getParameter("idLugarExpedicion");
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");
        String fechaExpedicion = request.getParameter("fechaExpedicion");
        String servicioSocial = request.getParameter("servicioSocial");
        String fechaExamenProfesional = request.getParameter("fechaExamenProfesional");
        String fechaExencionExamenProfesional = request.getParameter("fechaExencionExamenProfesional");
        String institucionProcedencia = request.getParameter("institucionProcedencia");
        String idTipoEstudioProcedencia = request.getParameter("idTipoEstudioAntecedente");
        String idEntidadEstudioAntecedente = request.getParameter("idEntidadEstudioAntecedente");
        String fechaInicioAntecedente = request.getParameter("fechaInicioAntecedente");
        String fechaFinAntecedente = request.getParameter("fechaFinAntecedente");
        String noCedula = request.getParameter("noCedula");
        String idsFirmantes = request.getParameter("idsFirmantes");
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Actualización");

        try {
//            String Query = "SET LANGUAGE 'español'; EXECUTE Upd_Titulo " + idTitulo + ",'" + "1.0"
//                    + "','" + fechaInicio + "','" + fechaFin + "'," + idModalidad + ",'" + fechaExamenProfesional + "','" + fechaExencionExamenProfesional + "',"
//                    + servicioSocial + "," + idFundamento + ",'" + idLugarExpedicion + "','" + institucionProcedencia + "'," + idTipoEstudioProcedencia + ",'"
//                    + idEntidadEstudioAntecedente + "','" + fechaInicioAntecedente + "','" + fechaFinAntecedente + "','" + noCedula + "'";
            String Query = "{call Upd_Titulo (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.parseInt(idTitulo));
            cstmt.setString(2, "1.0");
            cstmt.setString(3, (fechaInicio.trim().equalsIgnoreCase("") ? null : fechaInicio));
            cstmt.setString(4, fechaFin);
            cstmt.setInt(5, Integer.parseInt(idModalidad));
            cstmt.setString(6, (fechaExamenProfesional.trim().equalsIgnoreCase("") ? null : fechaExamenProfesional));
            cstmt.setString(7, (fechaExencionExamenProfesional.trim().equalsIgnoreCase("") ? null : fechaExencionExamenProfesional));
            cstmt.setInt(8, Integer.parseInt(servicioSocial));
            cstmt.setInt(9, Integer.parseInt(idFundamento));
            cstmt.setString(10, idLugarExpedicion);
            cstmt.setString(11, institucionProcedencia);
            cstmt.setInt(12, Integer.parseInt(idTipoEstudioProcedencia));
            cstmt.setString(13, idEntidadEstudioAntecedente);
            cstmt.setString(14, (fechaInicioAntecedente.trim().equalsIgnoreCase("") ? null : fechaInicioAntecedente));
            cstmt.setString(15, fechaFinAntecedente);
            cstmt.setString(16, (noCedula.trim().equalsIgnoreCase("") ? null : noCedula));
            cstmt.setString(17, fechaExpedicion);
            cstmt.registerOutParameter(18, java.sql.Types.VARCHAR);

            cstmt.execute();

            RESP = cstmt.getString(18);
            if (!RESP.contains("tituloActivo") && !RESP.contains("Error:")) {
                bitacora.setInformacion("Actualización de titulo||Respuesta Método: " + RESP + "||"
                        + idTitulo + "," + ",1.0," + fechaInicio + "," + fechaFin + "," + idModalidad + "," + fechaExamenProfesional + "," + fechaExencionExamenProfesional
                        + "," + servicioSocial + "," + idFundamento + "," + idLugarExpedicion + "," + institucionProcedencia + "," + idTipoEstudioProcedencia + "," + idEntidadEstudioAntecedente
                        + "," + fechaInicioAntecedente + "," + fechaFinAntecedente + "," + noCedula + "," + fechaExpedicion);
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
                String[] idFirmante = idsFirmantes.split(",");
                bitacora = new Bitacora();
                bitacora.setId_Usuario(Id_Usuario);
                bitacora.setModulo("Firmante Titulos");
                bitacora.setMovimiento("Inserción");
                for (int i = 0; i < idFirmante.length; i++) {
                    con.close();
                    cstmt.close();
                    con = conexion.GetconexionInSite();
                    cstmt = null;
                    Query = "{call Add_FirmanteTitulo (?,?)}";
                    cstmt = con.prepareCall(Query);
                    cstmt.setInt(1, Integer.valueOf(idTitulo));
                    cstmt.setInt(2, Integer.valueOf(idFirmante[i]));
                    cstmt.execute();
                    if (cstmt.getResultSet() == null) {
                        RESP = "success";
                        bitacora.setInformacion("Registro de Firmante Titulo||" + idTitulo + "," + idFirmante[i]);
                        cBitacora = new CBitacora(bitacora);
                        cBitacora.setRequest(request);
                        cBitacora.addBitacoraGeneral();
                    } else {
                        return "errorFirmantes";
                    }
                }
            }
//            System.out.println(Query);
//            rs = conector.consulta(Query);
//            if (rs == null || !rs.next()) {
//                String[] idFirmante = idsFirmantes.split(",");
//                for (int i = 0; i < idFirmante.length; i++) {
//                    Query = "SET LANGUAGE 'español'; EXECUTE Add_FirmanteTitulo " + idTitulo + "," + idFirmante[i];
//                    System.out.println(Query);
//                    rs = conector.consulta(Query);
//                    if (rs == null || !rs.next()) {
//                        RESP = "success";
//                    } else {
//                        RESP = "errorFirmantes";
//                    }
//                }
//            } else {
//                RESP = "error";
//            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL insertar el título electrónico: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException insertar el título electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al insertar el título electrónico: " + accion_catch(ex);
        } finally {
            try {
                cstmt.close();
                con.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private String deleteTitulo() {
        String RESP = "";
        String idTitulo = request.getParameter("idTitulo");
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Eliminación");
        try {
//            String Query = "SET LANGUAGE 'español'; EXECUTE Delt_Titulo " + idTitulo;
            String Query = "{call Delt_Titulo (?)}";

            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idTitulo));
            cstmt.executeUpdate();
            System.out.println(cstmt.getMoreResults());
            System.out.println(cstmt.getResultSet());
            System.out.println(cstmt.getUpdateCount());
            if (cstmt.getResultSet() == null && cstmt.getUpdateCount() == -1) {
                RESP = "success";
                bitacora.setInformacion("Eliminación de titulo: " + idTitulo + "||Respuesta Método: " + RESP);
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else {
                ResultSet rsError = cstmt.getResultSet();
                rsError.next();
                //System.out.println(rsError.getString("ErrorMessage"));
                if (rsError.getString("ErrorMessage").contains("fk_LibroFoja_TeTituloElectronico")) {
                    RESP = "error|Error al momento de eliminar el título electrónico. La información relacionada no pudo ser eliminada.<br> <small><strong>Si el problema persiste contacte a soporte técnico.</strong></small>";
                } else if (rsError.getString("ErrorMessage").contains("fk_TitulosAuntenticados_TETitulos")) {
                    RESP = "error|Error al momento de eliminar el título electrónico. <p><strong>El título cuenta con un registro en el módulo de títulos autenticados.</strong></p>";
                } else {
                    RESP = "error|Error al momento de eliminar el título electrónico, intente de nuevo. <br> Si el problema persiste contacte a soporte técnico";
                }
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la eliminación del título electrónico: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException al realizar la eliminación del título electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la eliminación del título electrónico: " + accion_catch(ex);
        }
        return RESP;
    }

    private String consultarTablaTitulos() {
        String idCarrera = request.getParameter("idCarrera");
        //String RESP = "";
        StringBuilder RESP = new StringBuilder();
        String rutaServidor = "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\";
        //String rutaServidor = "C:\\Users\\Paola\\Desktop\\";
        TETituloElectronico te;
        Alumno a;
        Persona p;
        TETitulosCarreras c;
        List<TETituloElectronico> lstTitulos;
        Date date;
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;
        try {
            
            //Se añadio a la consulta TE.estatusMET, TE.mensajeMET para extraer los datos y poner el icono en la columna MET
            String Query = "SELECT TE.id_titulo, TE.version, TE.folioControl, TE.id_profesionista,TE.estatusMET, TE.mensajeMET, "
                    + " TE.estatus, TE.fechaExpedicion, TE.idEntidadFederativa,A.Matricula, P.Nombre, "
                    + " P.APaterno,P.Amaterno, E.EntidadFederativa, TC.nombreCarrera, TE.envioSEP, TE.envioSepProductivo, TE.id_carrera"
                    + " FROM TETituloElectronico AS TE "
                    + " JOIN Alumnos AS A ON A.ID_Alumno = TE.id_profesionista "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " JOIN TEEntidad AS E ON E.idEntidadFederativa = TE.idEntidadFederativa "
                    + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = TE.id_carrera " + (!idCarrera.equalsIgnoreCase("todos") ? (!idCarrera.contains(",") ? " AND TE.id_carrera = " + idCarrera : " AND TE.id_titulo in (" + idCarrera.substring(0, idCarrera.length() - 1) + ")") : "")
                    + " ORDER BY TE.id_titulo DESC";
            System.out.println(Query);
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            boolean existeLibroFoja = existeTabla();
            lstTitulos = new ArrayList<>();
            while (rs.next()) {
                te = new TETituloElectronico();
                a = new Alumno();
                p = new Persona();
                c = new TETitulosCarreras();
                te.setID_Titulo(rs.getString("id_titulo"));
                te.setVersion(rs.getString("version"));
                te.setFolioControl(rs.getString("folioControl"));
                te.setID_Profesionista(rs.getString("id_profesionista"));
                te.setID_EntidadFederativa(rs.getString("idEntidadFederativa"));
                te.setID_EntidadFederativaAntecedente(rs.getString("EntidadFederativa"));// Se guarda el nombre de la entidad de expedición
                te.setEstatus(rs.getString("estatus"));
                te.setFechaExpedicion(rs.getString("fechaExpedicion"));
                te.setEnvioSep(rs.getBoolean("envioSEP"));
                te.setEnvioSepProductivo(rs.getBoolean("envioSepProductivo"));
                a.setId_Alumno(rs.getString("id_profesionista"));
                a.setMatricula(rs.getString("Matricula"));
                c.setID_Carrera(rs.getString("id_carrera"));
                c.setNombreCarrera(rs.getString("nombreCarrera"));
                p.setNombre(rs.getString("Nombre"));
                p.setAPaterno(rs.getString("APaterno"));
                p.setAMaterno(rs.getString("Amaterno"));
                a.setPersona(p);
                te.setCarrera(c);
                te.setAlumno(a);
                te.setEstatusMet(rs.getInt("estatusMET"));
                te.setMensajeMET(rs.getString("mensajeMET"));
                lstTitulos.add(te);
            }

            RESP.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblTitulos'>"
                    + "    <thead class='bg-primary-dark' style='color: white;'>"
                    + "        <tr>"
                    + "            <th class='text-center' style='display:none;'>ID_Titulo</th>"
                    + "            <th class='text-center hidden-sm hidden-xs'>Folio</th>"
                    + "            <th class='text-center'>Matrícula</th>"
                    + "            <th class='text-center hidden-xs'>Nombre(s)</th>"
                    + "            <th class='text-center hidden-xs'>A. Paterno</th>"
                    + "            <th class='text-center hidden-xs'>Carrera</th>"
                    + "            <th class='text-center hidden-xs'>Expedición</th>"
                    + "            <th class='text-center hidden-md hidden-sm hidden-xs'>MET</th>"
                    + "            <th class='text-center hidden-md hidden-sm hidden-xs'>Estatus</th>"
                    + "            <th class='text-center'>Acciones</th>"
                    + "            <th class='cbxSelectDescarga cbxDescXml cbxDesPdf cbxDescXmlM text-center bg-primary-dark' style='cursor: pointer; vertical-align:middle;'>"
                    + "                 <label class='css-input css-checkbox css-checkbox-sm css-checkbox-primary remove-margin-t remove-margin-b' style='margin-right: -15px;'>"
                    + "                     <input type='checkbox' class='chtodos'>"
                    + "                     <span></span>"
                    + "                 </label>"
                    + "             </th>"
                    + "        </tr>"
                    + "    </thead>"
                    + "    <tbody>");

            String cabecera = permisos.split("°")[0];
            if (!cabecera.equalsIgnoreCase("todos")) {
                permisos = permisos.split("°")[1];
            }
            StringBuilder anexoTabla = new StringBuilder();
            for (int i = 0; i < lstTitulos.size(); i++) {
                String bandera = "";
                date = parseador.parse(lstTitulos.get(i).getFechaExpedicion());
                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display:none;' id='IDTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getID_Titulo()).append("</td>");
                RESP.append("<td class='hidden-sm hidden-xs' id='FolioControl_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getFolioControl()).append("</td>");
                RESP.append("<td id='Matricula_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getAlumno().getMatricula()).append("</td>");
                RESP.append("<td class='hidden-xs' id='Nombre_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getAlumno().getPersona().getNombre()).append("</td>");
                RESP.append("<td class='hidden-xs' id='APaterno_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getAlumno().getPersona().getAPaterno()).append("</td>");
                RESP.append("<td class='hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top' id-carrera='").append(lstTitulos.get(i).getCarrera().getID_Carrera()).append("' title='").append(lstTitulos.get(i).getCarrera().getNombreCarrera()).append("' id='Carrera_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getCarrera().getNombreCarrera()).append("</td>");
                RESP.append("<td class='text-center hidden-xs' id='FechaExpedicion_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(formateador.format(date)).append("</td>");
                
                
                switch (lstTitulos.get(i).getEstatusMet()) {
                    case 1:
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' id='idTDSpan_").append(lstTitulos.get(i).getFolioControl()).append("'><span class='label' tabindex='0' data-toggle='tooltip' title='").append(lstTitulos.get(i).getMensajeMET()).append("' data-estatusMet='").append(lstTitulos.get(i).getEstatusMet()).append("' id='idSpanFolio_'").append(lstTitulos.get(i).getFolioControl()).append("'><i class='fa-3x fa fa-check' style='color:green;'></i>").append("</td>");
                        break;
                    case 2:
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' id='idTDSpan_").append(lstTitulos.get(i).getFolioControl()).append("'><span class='label' tabindex='0' data-toggle='tooltip' title='").append(lstTitulos.get(i).getMensajeMET()).append("' data-estatusMet='").append(lstTitulos.get(i).getEstatusMet()).append("' id='idSpanFolio_'").append(lstTitulos.get(i).getFolioControl()).append("'><i class='fa fa-remove fa-3x' style='color:#a70808;'></i>").append("</td>");
                        break;
                    default:
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' id='idTDSpan_").append(lstTitulos.get(i).getFolioControl()).append("'><span class='label' tabindex='0' data-toggle='tooltip' title='").append("No hay datos").append("' data-estatusMet='").append(lstTitulos.get(i).getEstatusMet()).append("' id='idSpanFolio_'").append(lstTitulos.get(i).getFolioControl()).append("'><i class='fa fa-question fa-3x' style='color:#ffb204;'></i>").append("</td>");
                        break;
                }
                if (lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                    RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                            .append("<span class='label label-warning'>XML PENDIENTE.</span>").append("</td>");
                } else {
                    if (lstTitulos.get(i).isEnvioSep() && !lstTitulos.get(i).isEnvioSepProductivo() && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-primary'>ENVIADO A PRUEBAS.</span>").append("</td>");
                    } else if (lstTitulos.get(i).isEnvioSepProductivo() && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-success'>ENVIADO A PRODUCTIVO.</span>").append("</td>");
                    } else if (lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-danger'>CANCELADO EN PRODUCTIVO.</span>").append("</td>");
                    } else {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-warning'>SIN ENVIAR A SERVICIO.</span>").append("</td>");
                    }
                }
                RESP.append("<td class='text-center' id='Acciones_").append(lstTitulos.get(i).getID_Titulo()).append("'>");
                String nombreTitulo = lstTitulos.get(i).getFolioControl() + "_"
                        + lstTitulos.get(i).getAlumno().getPersona().getAPaterno()
                        + (lstTitulos.get(i).getAlumno().getPersona().getAMaterno() == null ? "" : lstTitulos.get(i).getAlumno().getPersona().getAMaterno().trim());
                if (cabecera.contains("todos")) {
                    RESP.append("<div class='btn-group'>");
                    RESP.append("<button class='btn btn-default btn-xs btnConsultarTitulo' data-container='body' type='button' data-toggle='tooltip' id='consultarTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' value='cstTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Consultar'><i class='fa fa-search'></i></button>");
                    if (lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                        RESP.append("<button class='btn btn-default btn-xs btnEditarTitulo' data-container='body' type='button' data-toggle='tooltip' id='editarTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' value='edtTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Editar'><i class='fa fa-pencil'></i></button>");
                        anexoTabla.append("<td class='text-center'>");
                        anexoTabla.append("<div class='form-group'>");
                        anexoTabla.append("<div class='col-xs-12'>");
                        anexoTabla.append("<label class='css-input css-checkbox css-checkbox-warning css-checkbos-sm remove-margin-t remove-margin-b'>");
                        anexoTabla.append("<input class='cbxSelectDescarga cbxGenXml' type='checkbox' id='Tit_").append(lstTitulos.get(i).getID_Titulo()).append("'><span></span> </label>");
                        anexoTabla.append("</div></div></td>");
                    } else {
                        if (existeLibroFoja) {
                            RESP.append("<button class='btn btn-default btn-xs btnLibroFoja' data-container='body' type='button' data-toggle='tooltip' id='libroFoja_").append(lstTitulos.get(i).getID_Titulo()).append("' value='cstTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Registrar Libro y foja'><i class='fa fa-book'></i></button>");
                        }
                        RESP.append("<a class='btn btn-default btn-xs' type='button' data-toggle='tooltip' data-container='body' data-original-title='Descargar Titulo' download href='").append(rutaServidor).append("Titulo_").append(nombreTitulo).append(".zip'><i class='glyphicon glyphicon-download-alt'></i></a>");
                        anexoTabla.append("<td class='text-center'>");
                        anexoTabla.append("<div class='form-group'>");
                        anexoTabla.append("<div class='col-xs-12'>");
                        anexoTabla.append("<label class='css-input css-checkbox css-checkbox-primary css-checkbos-sm remove-margin-t remove-margin-b'>");
                        anexoTabla.append("<input class='cbxSelectDescarga cbxDescXml cbxDesPdf cbxDescXmlM' type='checkbox' id='Tit_").append(lstTitulos.get(i).getID_Titulo()).append("' data-nombrepdf='").append(nombreTitulo).append("'><span></span> </label>");
                        anexoTabla.append("</div></div></td>");
                    }
                    if (!lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                        RESP.append("<button class='js-swal-confirm btn btn-default btn-xs btnCancelarTitulo' data-container='body' type='button' data-toggle='tooltip' data-estatusMet='").append(lstTitulos.get(i).getEstatusMet()).append("' id='eliminarCertificado_").append(lstTitulos.get(i).getID_Titulo()).append("' value='dltTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Eliminar' data-productivo='").append(lstTitulos.get(i).isEnvioSepProductivo() && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("2") ? "1" : "0").append("'><i class='fa fa-times'></i></button>");
                    }
                    if (!lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                        //RESP.append( "     <button class='btn btn-default btn-xs btnCopiarBase64' data-container='body' type='button' data-toggle='tooltip' id='copiarBase64_" + lstTitulos.get(i).getID_Titulo() + "' value='base64_" + archivoXmlABase64(lstTitulos.get(i).getID_Titulo()) + "' data-original-title='Copiar cadena base 64'><i class='fa fa-copy'></i></button>";
                        if (!lstTitulos.get(i).isEnvioSep()) {
                            RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSEP' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAP_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRUEBAS'><i class='fa fa-cloud-upload'></i></button>");
                        } else {
                            RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSEP' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAP_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRUEBAS'><i class='fa fa-cloud-upload'></i></button>");
                            RESP.append("<button class='btn btn-default btn-xs btnConsultarRespuestaServicioSEP' data-container='body' type='button' data-toggle='tooltip' id='consultarRespuesta_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Ver resultados Servicio SEP PRUEBAS'><i class='fa fa-search-plus'></i></button>");
                        }
                        if (!lstTitulos.get(i).isEnvioSepProductivo()) {
                            RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAPPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRODUCTIVO'><i class='fa fa-mortar-board'></i></button>");
                        } else {
                            if (!lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                                RESP.append("<button class='btn btn-default btn-xs btnConsultarRespuestaServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='consultarRespuestaPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Ver resultados Servicio SEP PRODUCTIVO'><i class='fa fa-leanpub'></i></button>");
                            }
                            RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAPPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRODUCTIVO'><i class='fa fa-mortar-board'></i></button>");
                            RESP.append("<button class='btn btn-default btn-xs btnCancelarTituloServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='cancelarTituloSOAPPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cancelar Titulo Servicio SEP PRODUCTIVO'><i class='fa fa-trash'></i></button>");
                        }
                    }
                    RESP.append("</div>");
                } else if (cabecera.contains("acceso")) {
                    if (permisos.split("¬")[2].equalsIgnoreCase("1") || permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[4].equalsIgnoreCase("1") || permisos.split("¬")[6].equalsIgnoreCase("1") || permisos.split("¬")[5].equalsIgnoreCase("1") || permisos.split("¬")[7].equalsIgnoreCase("1")) {
                        RESP.append(" <div class='btn-group'>");

                        if (permisos.split("¬")[6].equalsIgnoreCase("0") && lstTitulos.get(i).getEstatus().equalsIgnoreCase("1")) {
                            anexoTabla.append("<td class='text-center'><span class='label label-danger'>Sin permiso</span></td>");
                        } else if (permisos.split("¬")[5].equalsIgnoreCase("0") && lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                            anexoTabla.append("<td class='text-center'><span class='label label-danger'>Sin permiso</span></td>");
                        }
                        //CONSULTAR
                        if (permisos.split("¬")[2].equalsIgnoreCase("1")) {
                            RESP.append("     <button class='btn btn-default btn-xs btnConsultarTitulo' data-container='body' type='button' data-toggle='tooltip' id='consultarTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' value='cstTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Consultar'><i class='fa fa-search'></i></button>");
                            RESP.append("<button class='btn btn-default btn-xs btnLibroFoja' data-container='body' type='button' data-toggle='tooltip' id='libroFoja_").append(lstTitulos.get(i).getID_Titulo()).append("' value='cstTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Registrar Libro y foja'><i class='fa fa-book'></i></button>");
                            bandera += "1";
                        }
                        //ELIMINAR
                        if (permisos.split("¬")[4].equalsIgnoreCase("1") && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                            RESP.append("     <button class='js-swal-confirm btn btn-default btn-xs btnCancelarTituloZ' data-container='body' type='button' data-toggle='tooltip' data-estatusMet='").append(lstTitulos.get(i).getEstatusMet()).append("' id='eliminarCertificado_").append(lstTitulos.get(i).getID_Titulo()).append("' value='dltTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Eliminar' data-productivo='").append(lstTitulos.get(i).isEnvioSepProductivo() && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("2") ? "1" : "0").append("'><i class='fa fa-times'></i></button>");
                            bandera += "1";
                        }
                        //Permiso de Editar[3], Generar XML[5], Descargar XML[6] y Sellar Titulos[7]
                        if (permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[6].equalsIgnoreCase("1") || permisos.split("¬")[5].equalsIgnoreCase("1") || permisos.split("¬")[7].equalsIgnoreCase("1")) {
                            if (lstTitulos.get(i).getEstatus().equalsIgnoreCase("0") && permisos.split("¬")[3].equalsIgnoreCase("1")) {
                                RESP.append("     <button class='btn btn-default btn-xs btnEditarTitulo' data-container='body' type='button' data-toggle='tooltip' id='editarTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' value='edtTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Editar'><i class='fa fa-pencil'></i></button>");
                                bandera += "1";
                            } else if (permisos.split("¬")[6].equalsIgnoreCase("1") && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                                //RESP.append( "     <button class='btn btn-default btn-xs btnDescargarXML' data-container='body' type='button' data-toggle='tooltip' id='descargarCertificado_" + lstTitulos.get(i).getID_Titulo() + "' value='dowlTitulo_" + lstTitulos.get(i).getID_Titulo() + "' data-original-title='Descargar'><i class='glyphicon glyphicon-download-alt'></i></button>";
                                RESP.append("<a class='btn btn-default btn-xs' type='button' download href='").append(rutaServidor).append("Titulo_").append(nombreTitulo).append(".zip'><i class='glyphicon glyphicon-download-alt'></i></a>");
                                bandera += "1";
                                anexoTabla.append("<td class='text-center'>");
                                anexoTabla.append("<div class='form-group'> ");
                                anexoTabla.append("<div class='col-xs-12'>");
                                anexoTabla.append("<label class='css-input css-checkbox css-checkbox-primary css-checkbos-sm remove-margin-t remove-margin-b'>");
                                anexoTabla.append("<input class='cbxSelectDescarga' type='checkbox' id='Tit_").append(lstTitulos.get(i).getID_Titulo()).append("'data-nombrepdf='").append(nombreTitulo).append("'><span></span> </label>");
                                anexoTabla.append("</div></div></td>");
                            }
                            if (permisos.split("¬")[7].equalsIgnoreCase("1") && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                                if (!lstTitulos.get(i).isEnvioSep()) {
                                    RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSEP' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAP_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRUEBAS'><i class='fa fa-cloud-upload'></i></button>");
                                } else {
                                    RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSEP' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAP_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRUEBAS'><i class='fa fa-cloud-upload'></i></button>");
                                    RESP.append("<button class='btn btn-default btn-xs btnConsultarRespuestaServicioSEP' data-container='body' type='button' data-toggle='tooltip' id='consultarRespuesta_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Ver resultados Servicio SEP PRUEBAS'><i class='fa fa-search-plus'></i></button>");
                                }
                                if (!lstTitulos.get(i).isEnvioSepProductivo()) {
                                    RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAPPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRODUCTIVO'><i class='fa fa-mortar-board'></i></button>");
                                } else {
                                    RESP.append("<button class='btn btn-default btn-xs btnEnviarTituloServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='enviarTituloSOAPPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cargar Titulo Servicio SEP PRODUCTIVO'><i class='fa fa-mortar-board'></i></button>");
                                    if (!lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                                        RESP.append("<button class='btn btn-default btn-xs btnConsultarRespuestaServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='consultarRespuestaPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Ver resultados Servicio SEP PRODUCTIVO'><i class='fa fa-leanpub'></i></button>");
                                    }
                                    RESP.append("<button class='btn btn-default btn-xs btnCancelarTituloServicioSepProductivo' data-container='body' type='button' data-toggle='tooltip' id='cancelarTituloSOAPPRO_").append(lstTitulos.get(i).getID_Titulo()).append("' data-original-title='Cancelar Titulo Servicio SEP PRODUCTIVO'><i class='fa fa-trash'></i></button>");
                                }
                                bandera += "1";
                            }
                            if (permisos.split("¬")[5].equalsIgnoreCase("1") && lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                                anexoTabla.append("<td class='text-center'>");
                                anexoTabla.append("<div class='form-group'> ");
                                anexoTabla.append("<div class='col-xs-12'>");
                                anexoTabla.append("<label class='css-input css-checkbox css-checkbox-warning css-checkbos-sm remove-margin-t remove-margin-b'>");
                                anexoTabla.append("<input class='cbxSelectDescarga cbxGenXml' type='checkbox' id='Tit_").append(lstTitulos.get(i).getID_Titulo()).append("'><span></span> </label>");
                                anexoTabla.append("</div></div></td>");
                            }
                        }
                        if (bandera.equalsIgnoreCase("")) {
                            RESP.append("<span class='label label-danger'>No disponible</span>");
                        }
                        RESP.append(" </div>");
                    } else {
                        RESP.append("<span class='label label-danger'>No disponible</span>");
                        anexoTabla.append("<td class='text-center'><span class='label label-danger'>Sin permiso</span></td>");
                    }
                }
                RESP.append("</td>");
                RESP.append(anexoTabla);
                RESP.append("</tr>");
                anexoTabla.setLength(0);
            }

            RESP.append("</tbody>"
                    + "</table>");
        } catch (SQLException ex) {
            RESP.setLength(0);
            RESP.append("error|Error SQL al realizar la consulta de títulos electrónicos: ").append(accion_catch(ex));
        } catch (ParseException ex) {
            RESP.setLength(0);
            RESP.append("error|Error ParseException al realizar la consulta de títulos electrónicos: ").append(accion_catch(ex));
        } catch (Exception ex) {
            RESP.setLength(0);
            RESP.append("error|Error inesperado al realizar la consulta de títulos electrónicos: ").append(accion_catch(ex));
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP.toString();
    }

    private String consultarAlumno() {
        String RESP = "";
        String matricula = request.getParameter("matricula");
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        Alumno a;
        Persona p;
        Carrera c;
        Date fecha = null;
        List<Alumno> lstAlumnos;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;

        try {
            String Query = "SELECT A.ID_Carrera, C.Descripcion, C.Clave, A.ID_Alumno, P.Nombre, P.APaterno,"
                    + " CONVERT(date, A.FechaInicioCarrera) AS fechaInicioCarrera, "
                    + " CONVERT(date, A.FechaFinCarrera) AS fechaFinCarrera, "
                    + " P.AMaterno, TCT.id_carrera AS 'ID_CarreraTitulo' "
                    + " FROM Alumnos AS A "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " JOIN Carrera AS C ON C.ID_Carrera = A.ID_Carrera "
                    + " JOIN TETitulosCarreras AS TCT ON TCT.Id_Carrera_Excel = C.Id_Carrera_Excel "
                    + " WHERE A.Matricula = ? AND A.estatus = 1 "
                    + " AND A.ID_Alumno NOT IN ( "
                    + "     SELECT TE.id_profesionista "
                    + "     FROM TETituloElectronico AS TE "
                    + "     JOIN TETitulosCarreras AS TC ON TC.id_carrera = TE.id_carrera "
                    + "     JOIN Alumnos AS AL ON AL.ID_Alumno = TE.id_profesionista "
                    + "     JOIN Carrera AS C ON C.ID_Carrera = AL.ID_Carrera "
                    + "     WHERE AL.Matricula = ? AND TE.estatus = 1"
                    + " );";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, matricula);
            pstmt.setString(2, matricula);
            rs = pstmt.executeQuery();
            lstAlumnos = new ArrayList<>();
            while (rs.next()) {
                a = new Alumno();
                c = new Carrera();
                p = new Persona();

                a.setID_Carrera(rs.getString("ID_Carrera"));
                a.setId_Alumno(rs.getString("ID_Alumno"));
                fecha = parseador.parse(rs.getString("FechaInicioCarrera"));
                a.setFechaInicioCarrera(formateador.format(fecha));
                fecha = parseador.parse(rs.getString("FechaFinCarrera"));
                a.setFechaFinCarrera(formateador.format(fecha));
                p.setNombre(rs.getString("Nombre"));
                p.setAPaterno(rs.getString("APaterno"));
                p.setAMaterno(rs.getString("AMaterno"));
                c.setDescripcion(rs.getString("Descripcion"));
                c.setClave(rs.getString("Clave"));
                c.setID_Carrera(rs.getString("ID_CarreraTitulo"));// Id de la carrera de los titulos
                a.setPersona(p);
                a.setCarrera(c);
                lstAlumnos.add(a);
            }

            if (lstAlumnos.isEmpty()) {
                RESP += "sinCoincidencias";
                Query = " SELECT TE.id_profesionista "
                        + " FROM TETituloElectronico AS TE "
                        + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = TE.id_carrera "
                        + " JOIN Alumnos AS AL ON AL.ID_Alumno = TE.id_profesionista "
                        + " JOIN Carrera AS C ON C.ID_Carrera = AL.ID_Carrera "
                        + " WHERE AL.Matricula = ?";
                rs = null;
                pstmt = con.prepareStatement(Query);
                pstmt.setString(1, matricula);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    RESP = "tieneTitulo";
                }
            } else {
                if (lstAlumnos.size() == 1) {
                    RESP += "1||";
                } else {
                    RESP += "2||";
                    RESP += "<option></option>";
                }

                for (int i = 0; i < lstAlumnos.size(); i++) {
                    RESP += "<option value='" + lstAlumnos.get(i).getCarrera().getID_Carrera() + "' data-idAlumno='" + lstAlumnos.get(i).getId_Alumno() + "' data-idCarrera='" + lstAlumnos.get(i).getID_Carrera() + "' data-inicio='" + lstAlumnos.get(i).getFechaInicioCarrera() + "' data-fin='" + lstAlumnos.get(i).getFechaFinCarrera() + "'>" + lstAlumnos.get(i).getCarrera().getDescripcion() + "</option>";
                }

                RESP += "||" + lstAlumnos.get(0).getPersona().getNombre() + " " + lstAlumnos.get(0).getPersona().getAPaterno() + (lstAlumnos.get(0).getPersona().getAMaterno() == null ? "" : " " + lstAlumnos.get(0).getPersona().getAMaterno());
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del alumno: " + accion_catch(ex);
        } catch (ParseException ex) {
            RESP = "error|Error ParseException al realizar la consulta del alumno: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del alumno: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                pstmt.close();
                rs.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String consultarTitulo() {
        String RESP = "";
        String idsFirmantes = "";
        String lstChange = "";
        String tags = "";
        String grados = "";
        String cadenaOriginal = "";
        String idTitulo = request.getParameter("idTitulo");
        TETituloElectronico te = null;
        TEFirmante f = null;
        Alumno a = null;
        Persona p = null;
        List<TEFirmante> lstFirmantes;
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;

        try {
            String Query = "SELECT TE.*, A.Matricula, C.nombreCarrera, "
                    + " P.Nombre, P.APaterno, P.AMaterno, C.GradoObtenidoF, C.GradoObtenidoM, txml.cadenaOriginal "
                    + " FROM TETituloElectronico AS TE "
                    + " JOIN Alumnos AS A ON A.ID_Alumno = TE.id_profesionista "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " JOIN TETitulosCarreras AS C ON C.id_carrera = TE.id_carrera"
                    + "	LEFT JOIN TExml as txml ON  TE.folioControl = txml.folioControl "
                    + " WHERE TE.id_titulo = " + idTitulo;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                te = new TETituloElectronico();
                a = new Alumno();
                p = new Persona();

                te.setID_Titulo(rs.getString("id_titulo"));
                te.setFolioControl(rs.getString("folioControl"));
                te.setID_Profesionista(rs.getString("id_profesionista"));
                te.setID_Carrera(rs.getString("id_carrera"));
                te.setFechaInicio(rs.getString("fechaInicio"));
                te.setFechaTerminacion(rs.getString("fechaTerminacion"));
                te.setFechaExpedicion(rs.getString("fechaExpedicion"));
                te.setID_ModalidadTitulacion(rs.getString("id_modalidadTitulacion"));
                te.setFechaExamenProfesional(rs.getString("fechaExamenProfesional"));
                te.setFechaExtensionExamenProfesional(rs.getString("fechaExtencionExamenProfesional"));
                te.setCumplioServicioSocial(rs.getString("cumplioServicioSocial"));
                te.setID_FundamentoLegalServicioSocial(rs.getString("idfundamentoLegalServicioSocial"));
                te.setID_EntidadFederativa(rs.getString("idEntidadFederativa"));
                te.setInstitucionProcedencia(rs.getString("institucionProcedencia"));
                te.setID_TipoEstudioAntecedente(rs.getString("idTipoEstudioAntecedente"));
                te.setID_EntidadFederativaAntecedente(rs.getString("idEntidadFederativaAntecedente"));
                te.setFechaInicioAntecedente(rs.getString("fechaInicioAntecedente"));
                te.setFechaTerminacionAntecedente(rs.getString("fechaTerminacionAntecedente"));
                te.setNoCedula(rs.getString("noCedula"));
                te.setEstatus(rs.getString("estatus"));
                a.setMatricula(rs.getString("Matricula"));
                a.setID_Carrera(rs.getString("nombreCarrera"));// Se guarda el nombre de la carrera
                p.setAMaterno(rs.getString("AMaterno"));
                p.setNombre(rs.getString("Nombre") + " " + rs.getString("APaterno") + (p.getAMaterno() == null || p.getAMaterno().equalsIgnoreCase("null") ? "" : " " + p.getAMaterno()));
                grados = ((rs.getString("GradoObtenidoF") == null && rs.getString("GradoObtenidoM") == null) || (rs.getString("GradoObtenidoF").equalsIgnoreCase("") && rs.getString("GradoObtenidoM").equalsIgnoreCase("")) ? "0" : "1");
                cadenaOriginal = (rs.getString("cadenaOriginal") == null ? "0" : rs.getString("cadenaOriginal"));

                Query = "SELECT  F.id_firmante, F.nombre + ' '+ F.primerApellido + ' ' + F.segundoApellido AS 'NombreCompleto' "
                        + " FROM Titulo_Firmante_Intermedia AS TF "
                        + " JOIN TEFirmantes AS F ON F.id_firmante = TF.ID_Firmante WHERE TF.ID_Titulo = " + idTitulo;

                rs = null;
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                lstFirmantes = new ArrayList<>();
                while (rs.next()) {
                    f = new TEFirmante();

                    f.setID_Firmante(rs.getString("id_firmante"));
                    f.setNombre(rs.getString("NombreCompleto"));
                    lstFirmantes.add(f);
                }

                for (int i = 0; i < lstFirmantes.size(); i++) {
                    idsFirmantes += lstFirmantes.get(i).getID_Firmante() + (i < lstFirmantes.size() - 1 ? "," : "");
                    tags += "<span class='tag'>"
                            + "<span>" + lstFirmantes.get(i).getNombre() + "&nbsp;</span>"
                            + "</span>";
                }
                lstChange = "$('#lstFirmantesTitulos').val([" + idsFirmantes + "])";

            } else {
                return "sinCoincidencias";
            }

            Date fechaInicio = null;
            if (te.getFechaInicio() != null) {
                fechaInicio = parseador.parse(te.getFechaInicio());
            }
            Date fechaFin = null;
            if (te.getFechaTerminacion() != null) {
                fechaFin = parseador.parse(te.getFechaTerminacion());
            }
            Date fechaExpedicion = null;
            if (te.getFechaExpedicion() != null) {
                fechaExpedicion = parseador.parse(te.getFechaExpedicion());
            }
            Date fechaExamenProfesional = null;
            if (te.getFechaExamenProfesional() != null) {
                fechaExamenProfesional = parseador.parse(te.getFechaExamenProfesional());
            }
            Date fechaExcencionExamen = null;
            if (te.getFechaExtensionExamenProfesional() != null) {
                fechaExcencionExamen = parseador.parse(te.getFechaExtensionExamenProfesional());
            }
            Date fechaInicioAntecedente = null;
            if (te.getFechaInicioAntecedente() != null) {
                fechaInicioAntecedente = parseador.parse(te.getFechaInicioAntecedente());
            }
            Date fechaFinAntecedente = null;
            if (te.getFechaTerminacionAntecedente() != null) {
                fechaFinAntecedente = parseador.parse(te.getFechaTerminacionAntecedente());
            }
            RESP += "~" + lstChange
                    + "~" + tags
                    + "~" + te.getID_Titulo()
                    + "~" + a.getMatricula()
                    + "~" + "<option value='" + te.getID_Carrera() + "'>" + a.getID_Carrera() + "</option>"
                    + "~" + p.getNombre()
                    + "~" + te.getFolioControl()
                    + "~" + te.getID_ModalidadTitulacion()
                    + "~" + te.getID_FundamentoLegalServicioSocial()
                    + "~" + te.getID_EntidadFederativa()
                    + "~" + (fechaInicio == null ? "" : formateador.format(fechaInicio))
                    + "~" + formateador.format(fechaFin)
                    + "~" + formateador.format(fechaExpedicion)
                    + "~" + te.getCumplioServicioSocial()
                    + "~" + (fechaExamenProfesional == null ? "" : formateador.format(fechaExamenProfesional))
                    + "~" + (fechaExcencionExamen == null ? "" : formateador.format(fechaExcencionExamen))
                    + "~" + te.getInstitucionProcedencia()
                    + "~" + te.getID_TipoEstudioAntecedente()
                    + "~" + te.getID_EntidadFederativaAntecedente()
                    + "~" + (fechaInicioAntecedente == null ? "" : formateador.format(fechaInicioAntecedente))
                    + "~" + formateador.format(fechaFinAntecedente)
                    + "~" + (te.getNoCedula() == null ? "" : te.getNoCedula())
                    + "~" + te.getEstatus()
                    + "~" + idsFirmantes
                    + "~" + grados
                    + "¬" + cadenaOriginal.replaceAll("~", "°°");

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del título electrónico: " + accion_catch(ex);
        } catch (ParseException ex) {
            RESP = "error|Error ParseException al realizar la consulta del título electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del título electrónico: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return RESP;
    }

    private String cargarEntidadFederativa() {
        String RESP = "";
        TEEntidad entidad;
        List<TEEntidad> lstEntidades;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;
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

            RESP += "success|<option></option>";

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
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private String cargarFirmantes() {
        String RESP = "";
        String RespAuxIds = "";
        TEFirmante firmante;
        List<TEFirmante> lstFirmantes;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;

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

            RESP += "success|<option value='todosFPAV'>Todos</option>";
            for (int i = 0; i < lstFirmantes.size(); i++) {
                RESP += "<option value='" + lstFirmantes.get(i).getID_Firmante() + "'>" + lstFirmantes.get(i).getCargo() + " - " + lstFirmantes.get(i).getNombre() + " " + lstFirmantes.get(i).getPrimerApellido() + " " + lstFirmantes.get(i).getSegundoApellido() + "</option>";
                RespAuxIds += lstFirmantes.get(i).getID_Firmante();
                RespAuxIds += (i < (lstFirmantes.size() - 1) ? "," : "");
            }

            RESP = RESP.replace("todosFPAV", RespAuxIds);

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de firmantes: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de firmantes: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private String cargarFundamentoLegal() {
        String RESP = "";
        TEFundamentos f;
        List<TEFundamentos> lstFundamentos;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;

        try {
            String Query = "SELECT * FROM TEfundamentos";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstFundamentos = new ArrayList<>();
            while (rs.next()) {
                f = new TEFundamentos();

                f.setID_FundamentoLegalServicioSocial(rs.getString("idFundamentoLegalServicioSocial"));
                f.setFundamentoLegalServicioSocial(rs.getString("fundamentoLegalServicioSocial"));
                lstFundamentos.add(f);
            }
            RESP += "success|<option></option>";
            for (int i = 0; i < lstFundamentos.size(); i++) {
                RESP += "<option value='" + lstFundamentos.get(i).getID_FundamentoLegalServicioSocial() + "'>" + lstFundamentos.get(i).getFundamentoLegalServicioSocial() + "</option>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de fundamentos legales: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de fundamentos legales: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private String cargarModalidadTitulacion() {
        String RESP = "";
        TEModalidad m;
        List<TEModalidad> lstModalidades;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;
        try {
            String Query = "SELECT * FROM TEmodalidad";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstModalidades = new ArrayList<>();
            while (rs.next()) {
                m = new TEModalidad();

                m.setID_ModalidadTitulacion(rs.getString("idModalidadTitulacion"));
                m.setModalidadTitulacion(rs.getString("ModalidadTitulacion"));
                lstModalidades.add(m);
            }

            RESP += "success|<option></option>";
            for (int i = 0; i < lstModalidades.size(); i++) {
                RESP += "<option value='" + lstModalidades.get(i).getID_ModalidadTitulacion() + "'>" + lstModalidades.get(i).getModalidadTitulacion() + "</option>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de modalidades: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de modalidades: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String cargarEstudioAntecedente() {
        String RESP = "";
        TEEstudioAntecedente ante;
        List<TEEstudioAntecedente> lstAntecedentes;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;

        try {
            String Query = "SELECT * FROM TEtipoEstudioAntecedente";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstAntecedentes = new ArrayList<>();
            while (rs.next()) {
                ante = new TEEstudioAntecedente();

                ante.setID_TipoEstudioAntecedente(rs.getString("idTipoEstudioAntecedente"));
                ante.setTipoEstudioAntecedente(rs.getString("tipoEstudioAntecedente"));
                lstAntecedentes.add(ante);
            }

            RESP += "success|<option></option>";
            for (int i = 0; i < lstAntecedentes.size(); i++) {
                RESP += "<option value='" + lstAntecedentes.get(i).getID_TipoEstudioAntecedente() + "'>" + lstAntecedentes.get(i).getTipoEstudioAntecedente() + "</option>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de estudios antecedentes: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de estudios antecedentes: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private String cargarMotivosCancelacion() {
        String RESP = "";
        TEMotivosCancelacion motCan;
        List<TEMotivosCancelacion> lstMotivosCan;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;

        try {
            String Query = "SELECT * FROM TEMotivos_Cancelacion";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            lstMotivosCan = new ArrayList<>();
            while (rs.next()) {
                motCan = new TEMotivosCancelacion();

                motCan.setID_MotivoCancelacion(rs.getString(1));
                motCan.setDescripcion(rs.getString(2));

                lstMotivosCan.add(motCan);
            }

            RESP += "success|<option></option>";
            for (int i = 0; i < lstMotivosCan.size(); i++) {
                RESP += "<option value='" + lstMotivosCan.get(i).getID_MotivoCancelacion() + "'>" + lstMotivosCan.get(i).getID_MotivoCancelacion() + "-" + lstMotivosCan.get(i).getDescripcion() + "</option>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de motivos de cancelación: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de motivos de cancelación: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return RESP;
    }

    private String cargarListaCarreras() {
        StringBuilder RESP = new StringBuilder();
        pstmt = null;
        con = null;
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
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de carreras: " + accion_catch(ex);
        }
        return RESP.toString();
    }

    private String CargarInstitucion() {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        pstmt = null;
        rs = null;
        try {
            String Query = "SELECT nombreOficial FROM Configuracion_Inicial AS CI"
                    + " JOIN Usuario AS U ON U.ID_ConfiguracionInicial = CI.ID_ConfiguracionInicial"
                    + " WHERE U.ID_Usuario = " + Id_Usuario;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                RESP = rs.getString(1);
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del nombre de institución: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del nombre de institución: " + accion_catch(ex);
        }

        return RESP;
    }

    private String generarXML() {
        String RESP = "";
        String idTitulo = request.getParameter("idTitulo");
        TETituloElectronico te;
        Alumno a;
        Persona p;
        TETitulosCarreras tc;
        TEFirmante f;
        List<TEFirmante> lstFirmantes;
        String cdn_TituloElectronico = "", cdn_Firmante = "", cdn_Institucion = "";
        String cdn_Carrera = "", cdn_Profesionista = "", cdn_Expedicion = "", cdn_Antecedentes = "";
        String tmp = "";
        String cveActivacion = "";
        String fechaVencimiento = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date hoy = new Date();
        int timbresPasados = 0;
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;
        desencriptador = new CClavesActivacion();
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Crear Título XML");
        try {

            String Query = "SELECT TOP 1 Tmp,Clave FROM Clave_Activacion AS C "
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

                    Query = "SELECT TE.*,A.ID_Alumno AS 'ID_AlumnoA', A.Matricula, P.Id_Persona AS 'ID_PersonaP', P.Email, "
                            + " P.Nombre, P.APaterno, P.AMaterno, P.Curp, TC.cveCarrera, TC.nombreCarrera, "
                            + " TC.idAutorizacionReconocimiento, TC.numeroRvoe,AU.autorizacionReconocimiento, "
                            + " TC.cveCampus, TC.nombreInstitucion, M.ModalidadTitulacion, F.fundamentoLegalServicioSocial, "
                            + " EN.EntidadFederativa AS 'LugarExpedicion', ENA.EntidadFederativa AS 'EntidadAntecedente', "
                            + " EA.tipoEstudioAntecedente "
                            + " FROM TETituloElectronico AS TE "
                            + " JOIN Alumnos AS A ON A.ID_Alumno = TE.id_profesionista "
                            + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                            + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = TE.id_carrera "
                            + " JOIN TEAutorizaciones AS AU ON AU.idAutorizacionReconocimiento = TC.idAutorizacionReconocimiento "
                            + " JOIN TEmodalidad AS M ON M.idModalidadTitulacion = TE.id_modalidadTitulacion "
                            + " JOIN TEfundamentos AS F ON F.idFundamentoLegalServicioSocial = TE.idFundamentoLegalServicioSocial "
                            + " JOIN TEEntidad AS EN ON EN.idEntidadFederativa = TE.idEntidadFederativa "
                            + " JOIN TEEntidad AS ENA ON ENA.idEntidadFederativa = TE.idEntidadFederativaAntecedente "
                            + " JOIN TEtipoEstudioAntecedente AS EA ON EA.idTipoEstudioAntecedente = TE.idTipoEstudioAntecedente "
                            + " WHERE id_titulo = " + idTitulo;
                    con = conexion.GetconexionInSite();
                    pstmt = con.prepareStatement(Query);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        te = new TETituloElectronico();
                        a = new Alumno();
                        p = new Persona();
                        tc = new TETitulosCarreras();

                        te.setID_Titulo(rs.getString("id_titulo"));
                        te.setVersion(rs.getString("version"));
                        te.setFolioControl(rs.getString("folioControl"));
                        te.setID_Profesionista(rs.getString("id_profesionista"));
                        te.setID_Carrera(rs.getString("id_carrera"));
                        te.setFechaInicio(rs.getString("fechaInicio"));
                        te.setFechaTerminacion(rs.getString("fechaTerminacion"));
                        te.setFechaExpedicion(rs.getString("fechaExpedicion"));
                        te.setID_ModalidadTitulacion(rs.getString("id_modalidadTitulacion") + "¬" + rs.getString("ModalidadTitulacion"));
                        te.setFechaExamenProfesional(rs.getString("fechaExamenProfesional"));
                        te.setFechaExtensionExamenProfesional(rs.getString("fechaExtencionExamenProfesional"));
                        te.setCumplioServicioSocial(rs.getString("cumplioServicioSocial"));
                        te.setID_FundamentoLegalServicioSocial(rs.getString("idFundamentoLegalServicioSocial") + "¬" + rs.getString("fundamentoLegalServicioSocial"));
                        te.setID_EntidadFederativa(rs.getString("idEntidadFederativa") + "¬" + rs.getString("LugarExpedicion"));
                        te.setInstitucionProcedencia(rs.getString("institucionProcedencia"));
                        te.setID_TipoEstudioAntecedente(rs.getString("idTipoEstudioAntecedente") + "¬" + rs.getString("tipoEstudioAntecedente"));
                        te.setID_EntidadFederativaAntecedente(rs.getString("idEntidadFederativaAntecedente") + "¬" + rs.getString("EntidadAntecedente"));
                        te.setFechaInicioAntecedente(rs.getString("fechaInicioAntecedente"));
                        te.setFechaTerminacionAntecedente(rs.getString("fechaTerminacionAntecedente"));
                        te.setNoCedula(rs.getString("noCedula"));

                        a.setId_Alumno(rs.getString("ID_AlumnoA"));
                        a.setMatricula(rs.getString("Matricula"));
                        p.setId_Persona(rs.getString("Id_PersonaP"));
                        p.setNombre(rs.getString("Nombre"));
                        p.setAPaterno(rs.getString("APaterno"));
                        p.setAMaterno(rs.getString("AMaterno"));
                        p.setCurp(rs.getString("Curp"));
                        p.setEmail(rs.getString("Email"));
                        tc.setClaveCarrera(rs.getString("cveCarrera"));
                        tc.setNombreCarrera(rs.getString("nombreCarrera"));
                        tc.setID_AutorizacionReconocimiento(rs.getString("idAutorizacionReconocimiento") + "¬" + rs.getString("autorizacionReconocimiento"));
                        tc.setNumeroRvoe(rs.getString("numeroRvoe"));
                        tc.setClaveInstitucion(rs.getString("cveCampus")); //Los titulos llevan la cla de campus, no institución
                        tc.setNombreInstitucion(rs.getString("nombreInstitucion"));

                        Query = "SELECT F.*, C.Cargo FROM TEFirmantes AS F "
                                + " JOIN Titulo_Firmante_Intermedia AS TFI ON TFI.ID_Firmante = F.id_firmante "
                                + " JOIN TETituloElectronico AS TE ON TE.id_titulo = TFI.ID_Titulo "
                                + " JOIN TECargos AS C ON C.idCargo = F.idCargo "
                                + " WHERE TFI.ID_Titulo = " + idTitulo;
                        rs = null;
                        pstmt = con.prepareStatement(Query);
                        rs = pstmt.executeQuery();
                        lstFirmantes = new ArrayList<>();
                        while (rs.next()) {
                            f = new TEFirmante();

                            f.setID_Firmante(rs.getString("id_firmante"));
                            f.setNombre(rs.getString("Nombre"));
                            f.setPrimerApellido(rs.getString("primerApellido"));
                            f.setSegundoApellido(rs.getString("segundoApellido"));
                            f.setCURP(rs.getString("Curp"));
                            f.setID_Cargo(rs.getString("idCargo") + "¬" + rs.getString("Cargo"));
                            f.setAbrTitulo(rs.getString("abrTitulo"));
                            lstFirmantes.add(f);
                        }

                        cdn_TituloElectronico = te.getFolioControl();
                        for (int i = 0; i < lstFirmantes.size(); i++) {
                            cdn_Firmante += lstFirmantes.get(i).getNombre().trim().toUpperCase()
                                    + "¬" + lstFirmantes.get(i).getPrimerApellido().trim().toUpperCase()
                                    + "¬" + (lstFirmantes.get(i).getSegundoApellido() == null || lstFirmantes.get(i).getSegundoApellido().trim().equalsIgnoreCase("") ? "^" : lstFirmantes.get(i).getSegundoApellido().trim().toUpperCase())
                                    + "¬" + lstFirmantes.get(i).getCURP().trim().toUpperCase()
                                    + "¬" + lstFirmantes.get(i).getID_Cargo()
                                    + "¬" + lstFirmantes.get(i).getAbrTitulo().trim().toUpperCase()
                                    + "¬" + lstFirmantes.get(i).getID_Firmante();
                            cdn_Firmante += (i < lstFirmantes.size() - 1 ? "~" : "");
                        }
                        cdn_Institucion = tc.getClaveInstitucion()
                                + "¬" + tc.getNombreInstitucion().trim().toUpperCase();

                        cdn_Carrera = tc.getClaveCarrera()
                                + "¬" + tc.getNombreCarrera().trim().toUpperCase()
                                + "¬" + (te.getFechaInicio() == null || te.getFechaInicio().trim().equalsIgnoreCase("") ? "^" : te.getFechaInicio())
                                + "¬" + te.getFechaTerminacion()
                                + "¬" + tc.getID_AutorizacionReconocimiento()
                                + "¬" + (tc.getNumeroRvoe() == null || tc.getNumeroRvoe().trim().equalsIgnoreCase("") ? "^" : tc.getNumeroRvoe());

                        cdn_Profesionista = p.getCurp().trim().toUpperCase()
                                + "¬" + p.getNombre().trim().toUpperCase()
                                + "¬" + p.getAPaterno().trim().toUpperCase()
                                + "¬" + (p.getAMaterno() == null || p.getAMaterno().equalsIgnoreCase("") ? "^" : p.getAMaterno().trim().toUpperCase())
                                + "¬" + p.getEmail().trim().toLowerCase();//El correo va en minusculas

                        cdn_Expedicion = te.getFechaExpedicion()
                                + "¬" + te.getID_ModalidadTitulacion()
                                + "¬" + (te.getFechaExamenProfesional() == null || te.getFechaExamenProfesional().trim().equalsIgnoreCase("") ? "^" : te.getFechaExamenProfesional())
                                + "¬" + (te.getFechaExtensionExamenProfesional() == null || te.getFechaExtensionExamenProfesional().equalsIgnoreCase("") ? "^" : te.getFechaExtensionExamenProfesional())
                                + "¬" + te.getCumplioServicioSocial()
                                + "¬" + te.getID_FundamentoLegalServicioSocial()
                                + "¬" + te.getID_EntidadFederativa();

                        cdn_Antecedentes = te.getInstitucionProcedencia().trim().toUpperCase()
                                + "¬" + te.getID_TipoEstudioAntecedente()
                                + "¬" + te.getID_EntidadFederativaAntecedente()
                                + "¬" + (te.getFechaInicioAntecedente() == null || te.getFechaInicioAntecedente().trim().equalsIgnoreCase("") ? "^" : te.getFechaInicioAntecedente())
                                + "¬" + te.getFechaTerminacionAntecedente()
                                + "¬" + (te.getNoCedula() == null || te.getNoCedula().trim().equalsIgnoreCase("") ? "^" : te.getNoCedula());
                    }

                    titElec = new Cxml_titulo_electronico(request);
                    titElec.setIdTitulo(idTitulo);
                    RESP = titElec.crear_xml_titulo_electronico(cdn_TituloElectronico, cdn_Firmante, cdn_Institucion, cdn_Carrera, cdn_Profesionista, cdn_Expedicion, cdn_Antecedentes, Id_Usuario);
                    if (RESP.trim().equalsIgnoreCase("success")) {
                        llenarFormatoTitulo(idTitulo);
                        bitacora.setInformacion("Generación digital de XML||Datos del alumno:" + cdn_Profesionista + "||FolioControl:" + cdn_TituloElectronico);
                        cBitacora = new CBitacora(bitacora);
                        cBitacora.setRequest(request);
                        cBitacora.addBitacoraGeneral();
                    } else if (!RESP.trim().equalsIgnoreCase("success") && !RESP.contains("errorContrasenia")) {
                        RESP = "error|Error al crear el archivo XML. El método no arrojo el mensaje de confirmación. <br><small>Intente de nuevo</small>";
                    }
                } else {
                    return "timbresVencidos";
                }
            } else {
                return "sinTimbres";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQLException al generar el archivo XML: " + accion_catch(ex);
        } catch (TransformerException ex) {
            RESP = "error|Error TransformerException al generar el archivo XML: " + accion_catch(ex);
        } catch (FileNotFoundException ex) {
            RESP = "error|Error FileNotFoundException al generar el archivo XML: " + accion_catch(ex);
        } catch (IOException ex) {
            RESP = "error|Error IOException al generar el archivo XML: " + accion_catch(ex);
        } catch (SAXException ex) {
            RESP = "error|Error SAXException al generar el archivo XML: " + accion_catch(ex);
        } catch (GeneralSecurityException ex) {
            RESP = "error|Error GeneralSecurityException al generar el archivo XML: " + accion_catch(ex);
        } catch (ParseException ex) {
            RESP = "error|Error ParseException al generar el archivo XML: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al generar el archivo XML: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String generarXmlMasivos() throws SQLException {
        String RESP = "";
        String cadenaIdTitulos = request.getParameter("cadenaIdTitulos");
        String[] idTitulos = cadenaIdTitulos.split("¬");
        String msjXml = "<p>Archivos XML seleccionados: <b>" + idTitulos.length + "</b></p>¬";
        int numXlCorrectos = 0;
        for (String idTitulo : idTitulos) {
            TETituloElectronico te;
            Alumno a;
            Persona p;
            TETitulosCarreras tc;
            TEFirmante f;
            List<TEFirmante> lstFirmantes;
            String cdn_TituloElectronico = "", cdn_Firmante = "", cdn_Institucion = "";
            String cdn_Carrera = "", cdn_Profesionista = "", cdn_Expedicion = "", cdn_Antecedentes = "";
            String tmp = "";
            String cveActivacion = "";
            String fechaVencimiento = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date hoy = new Date();
            int timbresPasados = 0;
            conexion = new CConexion();
            conexion.setRequest(request);
            con = null;
            pstmt = null;
            desencriptador = new CClavesActivacion();
            bitacora = new Bitacora();
            bitacora.setId_Usuario(Id_Usuario);
            bitacora.setModulo("Titulos");
            bitacora.setMovimiento("Crear Título XML");
            try {

                String Query = "SELECT TOP 1 Tmp,Clave FROM Clave_Activacion AS C "
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

                        Query = "SELECT TE.*,A.ID_Alumno AS 'ID_AlumnoA', A.Matricula, P.Id_Persona AS 'ID_PersonaP', P.Email, "
                                + " P.Nombre, P.APaterno, P.AMaterno, P.Curp, TC.cveCarrera, TC.nombreCarrera, "
                                + " TC.idAutorizacionReconocimiento, TC.numeroRvoe,AU.autorizacionReconocimiento, "
                                + " TC.cveCampus, TC.nombreInstitucion, M.ModalidadTitulacion, F.fundamentoLegalServicioSocial, "
                                + " EN.EntidadFederativa AS 'LugarExpedicion', ENA.EntidadFederativa AS 'EntidadAntecedente', "
                                + " EA.tipoEstudioAntecedente "
                                + " FROM TETituloElectronico AS TE "
                                + " JOIN Alumnos AS A ON A.ID_Alumno = TE.id_profesionista "
                                + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                                + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = TE.id_carrera "
                                + " JOIN TEAutorizaciones AS AU ON AU.idAutorizacionReconocimiento = TC.idAutorizacionReconocimiento "
                                + " JOIN TEmodalidad AS M ON M.idModalidadTitulacion = TE.id_modalidadTitulacion "
                                + " JOIN TEfundamentos AS F ON F.idFundamentoLegalServicioSocial = TE.idFundamentoLegalServicioSocial "
                                + " JOIN TEEntidad AS EN ON EN.idEntidadFederativa = TE.idEntidadFederativa "
                                + " JOIN TEEntidad AS ENA ON ENA.idEntidadFederativa = TE.idEntidadFederativaAntecedente "
                                + " JOIN TEtipoEstudioAntecedente AS EA ON EA.idTipoEstudioAntecedente = TE.idTipoEstudioAntecedente "
                                + " WHERE id_titulo = " + idTitulo;
                        con = conexion.GetconexionInSite();
                        pstmt = con.prepareStatement(Query);
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            te = new TETituloElectronico();
                            a = new Alumno();
                            p = new Persona();
                            tc = new TETitulosCarreras();

                            te.setID_Titulo(rs.getString("id_titulo"));
                            te.setVersion(rs.getString("version"));
                            te.setFolioControl(rs.getString("folioControl"));
                            te.setID_Profesionista(rs.getString("id_profesionista"));
                            te.setID_Carrera(rs.getString("id_carrera"));
                            te.setFechaInicio(rs.getString("fechaInicio"));
                            te.setFechaTerminacion(rs.getString("fechaTerminacion"));
                            te.setFechaExpedicion(rs.getString("fechaExpedicion"));
                            te.setID_ModalidadTitulacion(rs.getString("id_modalidadTitulacion") + "¬" + rs.getString("ModalidadTitulacion"));
                            te.setFechaExamenProfesional(rs.getString("fechaExamenProfesional"));
                            te.setFechaExtensionExamenProfesional(rs.getString("fechaExtencionExamenProfesional"));
                            te.setCumplioServicioSocial(rs.getString("cumplioServicioSocial"));
                            te.setID_FundamentoLegalServicioSocial(rs.getString("idFundamentoLegalServicioSocial") + "¬" + rs.getString("fundamentoLegalServicioSocial"));
                            te.setID_EntidadFederativa(rs.getString("idEntidadFederativa") + "¬" + rs.getString("LugarExpedicion"));
                            te.setInstitucionProcedencia(rs.getString("institucionProcedencia"));
                            te.setID_TipoEstudioAntecedente(rs.getString("idTipoEstudioAntecedente") + "¬" + rs.getString("tipoEstudioAntecedente"));
                            te.setID_EntidadFederativaAntecedente(rs.getString("idEntidadFederativaAntecedente") + "¬" + rs.getString("EntidadAntecedente"));
                            te.setFechaInicioAntecedente(rs.getString("fechaInicioAntecedente"));
                            te.setFechaTerminacionAntecedente(rs.getString("fechaTerminacionAntecedente"));
                            te.setNoCedula(rs.getString("noCedula"));

                            a.setId_Alumno(rs.getString("ID_AlumnoA"));
                            a.setMatricula(rs.getString("Matricula"));
                            p.setId_Persona(rs.getString("Id_PersonaP"));
                            p.setNombre(rs.getString("Nombre"));
                            p.setAPaterno(rs.getString("APaterno"));
                            p.setAMaterno(rs.getString("AMaterno"));
                            p.setCurp(rs.getString("Curp"));
                            p.setEmail(rs.getString("Email"));
                            tc.setClaveCarrera(rs.getString("cveCarrera"));
                            tc.setNombreCarrera(rs.getString("nombreCarrera"));
                            tc.setID_AutorizacionReconocimiento(rs.getString("idAutorizacionReconocimiento") + "¬" + rs.getString("autorizacionReconocimiento"));
                            tc.setNumeroRvoe(rs.getString("numeroRvoe"));
                            tc.setClaveInstitucion(rs.getString("cveCampus")); //ES CLAVE CAMPUS LA QUE VA EN TITULOS
                            tc.setNombreInstitucion(rs.getString("nombreInstitucion"));

                            Query = "SELECT F.*, C.Cargo FROM TEFirmantes AS F "
                                    + " JOIN Titulo_Firmante_Intermedia AS TFI ON TFI.ID_Firmante = F.id_firmante "
                                    + " JOIN TETituloElectronico AS TE ON TE.id_titulo = TFI.ID_Titulo "
                                    + " JOIN TECargos AS C ON C.idCargo = F.idCargo "
                                    + " WHERE TFI.ID_Titulo = " + idTitulo;
                            rs = null;
                            pstmt = con.prepareStatement(Query);
                            rs = pstmt.executeQuery();
                            lstFirmantes = new ArrayList<>();
                            while (rs.next()) {
                                f = new TEFirmante();

                                f.setID_Firmante(rs.getString("id_firmante"));
                                f.setNombre(rs.getString("Nombre"));
                                f.setPrimerApellido(rs.getString("primerApellido"));
                                f.setSegundoApellido(rs.getString("segundoApellido"));
                                f.setCURP(rs.getString("Curp"));
                                f.setID_Cargo(rs.getString("idCargo") + "¬" + rs.getString("Cargo"));
                                f.setAbrTitulo(rs.getString("abrTitulo"));
                                lstFirmantes.add(f);
                            }

                            cdn_TituloElectronico = te.getFolioControl();
                            for (int i = 0; i < lstFirmantes.size(); i++) {
                                cdn_Firmante += lstFirmantes.get(i).getNombre().trim().toUpperCase()
                                        + "¬" + lstFirmantes.get(i).getPrimerApellido().trim().toUpperCase()
                                        + "¬" + (lstFirmantes.get(i).getSegundoApellido() == null || lstFirmantes.get(i).getSegundoApellido().trim().equalsIgnoreCase("") ? "^" : lstFirmantes.get(i).getSegundoApellido().trim().toUpperCase())
                                        + "¬" + lstFirmantes.get(i).getCURP().trim().toUpperCase()
                                        + "¬" + lstFirmantes.get(i).getID_Cargo()
                                        + "¬" + lstFirmantes.get(i).getAbrTitulo().trim().toUpperCase()
                                        + "¬" + lstFirmantes.get(i).getID_Firmante();
                                cdn_Firmante += (i < lstFirmantes.size() - 1 ? "~" : "");
                            }
                            cdn_Institucion = tc.getClaveInstitucion()
                                    + "¬" + tc.getNombreInstitucion().trim().toUpperCase();

                            cdn_Carrera = tc.getClaveCarrera()
                                    + "¬" + tc.getNombreCarrera().trim().toUpperCase()
                                    + "¬" + (te.getFechaInicio() == null || te.getFechaInicio().trim().equalsIgnoreCase("") ? "^" : te.getFechaInicio())
                                    + "¬" + te.getFechaTerminacion()
                                    + "¬" + tc.getID_AutorizacionReconocimiento()
                                    + "¬" + (tc.getNumeroRvoe() == null || tc.getNumeroRvoe().trim().equalsIgnoreCase("") ? "^" : tc.getNumeroRvoe());

                            cdn_Profesionista = p.getCurp().trim().toUpperCase()
                                    + "¬" + p.getNombre().trim().toUpperCase()
                                    + "¬" + p.getAPaterno().trim().toUpperCase()
                                    + "¬" + (p.getAMaterno() == null || p.getAMaterno().equalsIgnoreCase("") ? "^" : p.getAMaterno().trim().toUpperCase())
                                    + "¬" + p.getEmail().trim().toLowerCase();

                            cdn_Expedicion = te.getFechaExpedicion()
                                    + "¬" + te.getID_ModalidadTitulacion()
                                    + "¬" + (te.getFechaExamenProfesional() == null || te.getFechaExamenProfesional().trim().equalsIgnoreCase("") ? "^" : te.getFechaExamenProfesional())
                                    + "¬" + (te.getFechaExtensionExamenProfesional() == null || te.getFechaExtensionExamenProfesional().equalsIgnoreCase("") ? "^" : te.getFechaExtensionExamenProfesional())
                                    + "¬" + te.getCumplioServicioSocial()
                                    + "¬" + te.getID_FundamentoLegalServicioSocial()
                                    + "¬" + te.getID_EntidadFederativa();

                            cdn_Antecedentes = te.getInstitucionProcedencia().trim().toUpperCase()
                                    + "¬" + te.getID_TipoEstudioAntecedente()
                                    + "¬" + te.getID_EntidadFederativaAntecedente()
                                    + "¬" + (te.getFechaInicioAntecedente() == null || te.getFechaInicioAntecedente().trim().equalsIgnoreCase("") ? "^" : te.getFechaInicioAntecedente())
                                    + "¬" + te.getFechaTerminacionAntecedente()
                                    + "¬" + (te.getNoCedula() == null || te.getNoCedula().trim().equalsIgnoreCase("") ? "^" : te.getNoCedula());
                        }

                        titElec = new Cxml_titulo_electronico(request);
                        titElec.setIdTitulo(idTitulo);
                        RESP = titElec.crear_xml_titulo_electronico(cdn_TituloElectronico, cdn_Firmante, cdn_Institucion, cdn_Carrera, cdn_Profesionista, cdn_Expedicion, cdn_Antecedentes, Id_Usuario);
                        llenarFormatoTitulo(idTitulo);
                        if (RESP.trim().equalsIgnoreCase("success")) {
                            bitacora.setInformacion("Generación digital de XML||Datos del alumno:" + cdn_Profesionista + "||FolioControl:" + cdn_TituloElectronico);
                            cBitacora = new CBitacora(bitacora);
                            cBitacora.setRequest(request);
                            cBitacora.addBitacoraGeneral();
                            numXlCorrectos++;
                        } else if (RESP.contains("errorContrasenia")) {
                            break;
                        } else if (!RESP.trim().equalsIgnoreCase("success") && !RESP.contains("errorContrasenia")) {
                            return "error|Error al crear el archivo XML: " + obtenerNombreTitulo(idTitulo) + ". El método no arrojo el mensaje de confirmación. <br><small>Intente de nuevo</small>";
                        }
                    } else {
                        return "timbresVencidos";
                    }
                } else {
                    return "sinTimbres";
                }

            } catch (SQLException ex) {
                accion_catch(ex);
            } catch (TransformerException ex) {
                accion_catch(ex);
            } catch (FileNotFoundException ex) {
                accion_catch(ex);
            } catch (IOException ex) {
                accion_catch(ex);
            } catch (SAXException ex) {
                accion_catch(ex);
            } catch (GeneralSecurityException ex) {
                accion_catch(ex);
            } catch (ParseException ex) {
                accion_catch(ex);
            } catch (Exception ex) {
                accion_catch(ex);
            } finally {
                con.close();
                rs.close();
                pstmt.close();
                conexion.GetconexionInSite().close();
            }
        }
        msjXml += "<p>El número total de XML generados fue de: <b>" + numXlCorrectos + "</b></p>";
        return RESP + "¬" + msjXml;
    }

    private String descargarXML() throws SQLException {
        String RESP = "";
        String idTitulo = request.getParameter("idTitulo");
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Descarga");

        try {
            String Query = "SELECT TE.folioControl, P.APaterno, P.AMaterno "
                    + " FROM TETituloElectronico AS TE "
                    + " JOIN Alumnos AS A ON A.ID_Alumno = TE.id_profesionista "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " WHERE TE.id_titulo = " + idTitulo;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                RESP += rs.getString("folioControl") + "_" + rs.getString("APaterno") + "_" + rs.getString("AMaterno");
                RESP = RESP.replace("_null", "");
                Query = "SELECT xmlTE FROM TExml WHERE folioControl LIKE '" + rs.getString("folioControl") + "'";
                rs = null;
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    RESP += "fpav" + rs.getString("xmlTE");
                    bitacora.setInformacion("Descarga de xml de titulo||Respuesta método: " + RESP);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.addBitacoraGeneral();
                } else {
                    RESP = "sinCoincidencias";
                }
            } else {
                RESP = "sinCoincidencias";
            }

        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        } finally {
            con.close();
            rs.close();
            pstmt.close();
            conexion = null;
        }

        return RESP;
    }

    public String descargarZipsMasivos() {
        String RESP = "";
        String cadenaIdTit = request.getParameter("cadenaIdTitulos");
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Descarga Masiva XML");
        try {
            String[] idTitulos = cadenaIdTit.split("¬");
            for (String idTitulo : idTitulos) {
                RESP += "Titulo_" + idTitulo.split("~")[1] + ".zip";
                RESP = RESP.replace("_null", "") + "¬";
//                String Query = "SELECT TE.folioControl, P.APaterno, P.AMaterno "
//                        + " FROM TETituloElectronico AS TE "
//                        + " JOIN Alumnos AS A ON A.ID_Alumno = TE.id_profesionista "
//                        + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
//                        + " WHERE TE.id_titulo = " + idTitulo;
//                con = conexion.GetconexionInSite();
//                pstmt = con.prepareStatement(Query);
//                rs = pstmt.executeQuery();
//                if (rs.next()) {
//                    RESP += "Titulo_" + rs.getString("folioControl") + "_" + rs.getString("APaterno").trim() + (rs.getString("AMaterno") == null ? "" : rs.getString("AMaterno").trim()) + ".zip";
//                    RESP = RESP.replace("_null", "") + "¬";
//                }
            }
            llenarNombreInstitucion();
            String save = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\";
            //String save = System.getProperty("user.home") + "\\Downloads\\XML\\Titulos\\";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
            RESP = zip_File("Titulos_" + sdf.format(new Date()), RESP.substring(0, RESP.length() - 1), save);
            bitacora.setInformacion("Descarga de archivo zip xml de titulos||Respuesta método: " + RESP);
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            RESP = "error|Error SQLException al generar el archivo zip con los XML seleccionados: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al generar el archivo zip con los XML seleccionados: " + accion_catch(ex);
        } finally {
//            try {
//                con.close();
//                pstmt.close();
//                conexion.GetconexionInSite().close();
//            } catch (SQLException ex) {
//                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        return "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\" + RESP;
    }

    public String llenarFormatoTitulo(String Id_Titulo) {
        try {
            llenarNombreInstitucion();
            //String rutaJasper = "C:\\Users\\Paola\\JaspersoftWorkspace\\Plantillas titulos y certificados\\Titulo_P1.jasper";
            String rutaJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Plantillas\\Titulo_P1.jasper";
            String rutaSubJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Plantillas\\";
            String rutaImagen = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\" + nombreLogo;
            String pathContext = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\";
            //String pathContext2 = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Plantillas\\";
            String rutaFirma = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\" + nombreFirma;
            JasperReport reporte = null;
            String save = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\";
            Map parametrosReporte = new HashMap();
            parametrosReporte.put("Id_Titulo", Id_Titulo);
            parametrosReporte.put("RutaImagen", rutaImagen);
            parametrosReporte.put("RutaFirma", rutaFirma);
            parametrosReporte.put("pathContext", pathContext);
            //parametrosReporte.put("pathContext2", pathContext);
            parametrosReporte.put("SUBREPORT_DIR", rutaSubJasper);
            con = conexion.GetconexionInSite();
            reporte = (JasperReport) JRLoader.loadObjectFromFile(rutaJasper);
            JasperPrint jprint = JasperFillManager.fillReport(reporte, parametrosReporte, con);
            //save = "C:\\Users\\Paola\\Desktop\\";
            String nameFile = obtenerNombreTitulo(Id_Titulo);
            nombreArchivo = nameFile + ".pdf";
            JasperExportManager.exportReportToPdfFile(jprint, save + nombreArchivo);

            zip_File("Titulo_" + nameFile, nombreArchivo + "¬" + nameFile + ".xml", save);
        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error SQL", ex);
        } catch (JRException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error JRException", ex);
        }
        return null;
    }

    public String llenarFormatoTituloMasivo() {
        String RESP = "";
        try {
            String valRequest = request.getParameter("cadenaIdTitulos");
            String regenerarPDF = request.getParameter("genPDF");
            String[] cadenaIdTitulos = (valRequest == null ? null : valRequest.split("¬"));
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy", new Locale("MX"));
            String fechaGeneracion = sdf.format(new Date());
            llenarNombreInstitucion();
            StringBuilder nombresPDF = new StringBuilder();
            //String rutaJasper = "C:\\Users\\Paola\\JaspersoftWorkspace\\Plantillas titulos y certificados\\Titulo_P1.jasper";
            String rutaJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Plantillas\\Titulo_P1.jasper";
            String rutaSubJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Plantillas\\";
            String rutaImagen = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\" + nombreLogo;
            String pathContext = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\";
            String rutaFirma = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\" + nombreFirma;
            String save = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\";

            for (String Id_Titulo : cadenaIdTitulos) {
                if (regenerarPDF.equalsIgnoreCase("true")) {
                    con = conexion.GetconexionInSite();
                    CGeneracionSello genSello = new CGeneracionSello(con, NombreInstitucion.trim(), Id_Usuario);
                    genSello.verificarCadenaySello(Id_Titulo.split("~")[0]);
                    JasperReport reporte = null;
                    Map parametrosReporte = new HashMap();
                    parametrosReporte.put("Id_Titulo", Id_Titulo.split("~")[0]);
                    parametrosReporte.put("RutaImagen", rutaImagen);
                    parametrosReporte.put("SUBREPORT_DIR", rutaSubJasper);
                    //System.out.println(pathContext);
                    parametrosReporte.put("pathContext", pathContext);
                    parametrosReporte.put("RutaFirma", rutaFirma);
                    reporte = (JasperReport) JRLoader.loadObjectFromFile(rutaJasper);
                    JasperPrint jprint = JasperFillManager.fillReport(reporte, parametrosReporte, con);
                    String nameFile = Id_Titulo.split("~")[1];
                    nombreArchivo = nameFile + ".pdf";
                    JasperExportManager.exportReportToPdfFile(jprint, save + nombreArchivo);
                    zip_File("Titulo_" + nameFile, nombreArchivo + "¬" + nameFile + ".xml", save);
                    nombresPDF.append(nombreArchivo).append("¬");
                    con.close();
                    con = null;
                } else {
                    String nameFile = Id_Titulo.split("~")[1];
                    nombreArchivo = nameFile + ".pdf";
                    nombresPDF.append(nombreArchivo).append("¬");
                }
            }

            //save = "C:\\Users\\Paola\\Desktop\\";
            RESP = zip_File("TitulosFísicos_" + fechaGeneracion, nombresPDF.toString().substring(0, nombresPDF.toString().length() - 1), save);
        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error SQL", ex);
        } catch (JRException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error JRException", ex);
        }
        return "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\" + RESP;
    }

    public String descargarXmlMasivos() {
        String RESP = "";
        String valRequest = request.getParameter("cadenaIdTitulos");
        String[] cadenaIdTitulos = (valRequest == null ? null : valRequest.split("¬"));
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy", new Locale("MX"));
        String fechaGeneracion = sdf.format(new Date());
        llenarNombreInstitucion();
        StringBuilder nombresPDF = new StringBuilder();
        //String rutaJasper = "C:\\Users\\Paola\\JaspersoftWorkspace\\Plantillas titulos y certificados\\Titulo_P1.jasper";
        String save = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\";

        for (String Id_Titulo : cadenaIdTitulos) {
            String nameFile = Id_Titulo.split("~")[1];
            nombreArchivo = nameFile + ".xml";
            nombresPDF.append(nombreArchivo).append("¬");
        }

        //save = "C:\\Users\\Paola\\Desktop\\";
        RESP = zip_File("TitulosElectrónicosXML_" + fechaGeneracion, nombresPDF.toString().substring(0, nombresPDF.toString().length() - 1), save);
        return "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\" + RESP;
    }

    public void llenarNombreInstitucion() {
        try {
            String Query = "SELECT nombreInstitucion,logo,firmaImagen FROM Configuracion_Inicial AS CI "
                    + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                    + " WHERE Id_Usuario = " + Id_Usuario;
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                NombreInstitucion = rs.getString(1);
                nombreLogo = rs.getString(2);
                nombreFirma = rs.getString(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String obtenerNombreTitulo(String Id_Titulo) {
        String RESP = "";
        try {
            String Query = "SELECT folioControl, apaterno,amaterno FROM TETituloElectronico tte "
                    + "JOIN alumnos a ON a.id_Alumno = tte.id_profesionista JOIN Persona p ON a.Id_Persona = p.Id_Persona WHERE Id_Titulo = ? ";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, Id_Titulo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                RESP += rs.getString(1) + "_" + rs.getString(2).trim() + (rs.getString(3) == null ? "" : rs.getString(3).trim());
            }
        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            RESP = "0";
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    private String cargarNotifyFirmantes() {
        String RESP = "";
        pstmt = null;
        rs = null;
        con = null;
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
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al generar las notificaciones de inicio!", ex);
            RESP = "errorNotify";
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarNotifyFirmantes() !", ex);
                }
            }
        }
        return RESP;
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
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "Ocurrió un error al armar el zip", ex);
                }
            }
            return zipName + ".zip";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "Ocurrió un error al tratar de encontrar el archivo zip", ex);
            return "error";
        } finally {
            try {
                out.finish();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el objeto ZipOutputStream", ex);
            }
        }
    }

    public String cargarTitulosWebService(String idTitulo) {

        //String idTitulo = request.getParameter("idTitulo");
        String RESP = "";
        pstmt = null;
        rs = null;
        String nameFile = "";
        File xmlFile = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Carga METqa");
        try {
            llenarNombreInstitucion();
            String Query = "SELECT folioControl, aPaterno, aMaterno FROM TETituloElectronico tte JOIN Alumnos a on tte.id_profesionista = a.ID_Alumno JOIN Persona p ON a.ID_Persona = p.Id_Persona WHERE Id_Titulo = ?";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idTitulo);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs.next()) {
                llenarCredencialesSEP();
                if (usuarioSEP.trim().equalsIgnoreCase("") || passwordSEP.trim().equalsIgnoreCase("")) {
                    RESP = "noCredenciales";
                } else {
                    String folioControl = rs.getString(1).trim();
                    nameFile = folioControl.trim() + "_" + rs.getString(2).trim() + (rs.getString(3) == null ? "" : rs.getString(3).trim()) + ".xml";
                    xmlFile = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\" + folioControl.trim() + "_" + rs.getString(2).trim() + (rs.getString(3) == null ? "" : rs.getString(3).trim()) + ".xml");

                    CargaTituloElectronico cte = new CargaTituloElectronico(usuarioSEP, passwordSEP, nameFile);
                    RESP = cte.fileToArray(xmlFile);
                    if (RESP.equalsIgnoreCase("ok")) {
                        RESP = cte.cargarArchivoSOAP();
                        if (RESP.equalsIgnoreCase("success")) {
                            bitacora.setInformacion("Carga a servicio de pruebas||" + "MensajeQA:" + cte.getMensaje() + ",NumLoteQA:" + cte.getNumLote() + ",FolioControl:" + folioControl.trim());
                            cBitacora = new CBitacora(bitacora);
                            cBitacora.setRequest(request);
                            cBitacora.addBitacoraGeneral();
                            Query = "UPDATE TETituloElectronico SET envioSEP = 1, mensajeSep = ?, numLoteSEP = ? WHERE id_titulo = ?";
                            pstmt = con.prepareStatement(Query);
                            pstmt.setString(1, cte.getMensaje());
                            pstmt.setString(2, cte.getNumLote());
                            pstmt.setString(3, idTitulo);
                            pstmt.execute();
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la carga del título electrónico al servicio SEP: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la carga del título electrónico al servicio SEP: " + accion_catch(ex);
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarNotifyFirmantes() !", ex);
                }
            }
        }
        return RESP;
    }
    public String cargarTitulosMasivosWebService() {
        String cadenaIdTit = request.getParameter("cadenaIdTitulos"); 
        String RESP = "";
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Carga de titulos Masiva SEP Prueba");
        try {
            String[] idTitulos = cadenaIdTit.split("~");
            //Recorremos el arreglo y mandamos a llamar varias veces ña fiuncion individual de carcarTitulosWebSericies con cada id,
            //si manda un error lo devolvemos, los anteriores se registran, pero desde donde dio error hasta los que quedan no se van a subir.
            for (String idTitulo : idTitulos) {
                System.out.println(idTitulo);
                
                 RESP = cargarTitulosWebService(idTitulo);
                 if (RESP.contains("error") || RESP.contains("noCredenciales") || RESP.contains("SQL") || RESP.contains("eSOAP") || RESP.contains("fileNotFound")){
                     return RESP;
                 }
            }

            bitacora.setInformacion("carga masiva de xml de titulos a pruebas||con los id Titulos: " + cadenaIdTit);
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            return "error|Error SQL al realizar la carga del título electrónico al servicio SEP: " + accion_catch(ex);
        }
       return "success";

        
    }
    
    public String cargarTitulosMasivosWebServiceProductivo() {
        String cadenaIdTit = request.getParameter("cadenaIdTitulos"); 
        String RESP = "";
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Carga de titulos Masiva SEP Productivo");
        try {
            String[] idTitulos = cadenaIdTit.split("~");
            //Recorremos el arreglo y mandamos a llamar varias veces ña fiuncion individual de carcarTitulosWebSericies con cada id,
            //si manda un error lo devolvemos, los anteriores se registran, pero desde donde dio error hasta los que quedan no se van a subir.
            for (String idTitulo : idTitulos) {
                System.out.println(idTitulo);
                 RESP =  cargarTitulosWebServiceProductivo(idTitulo);
                 if (RESP.contains("error") || RESP.contains("noCredenciales") || RESP.contains("SQL") || RESP.contains("eSOAP") || RESP.contains("fileNotFound")){
                     return RESP;
                 }
            }

            bitacora.setInformacion("carga masiva de xml de titulos a productivo||con los id Titulos: " + cadenaIdTit);
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            return "error|Error SQL al realizar la carga del título electrónico al servicio SEP: " + accion_catch(ex);
        }
       return "success";

        
    }
    public String consultarTitulosWebService() {
        String idTitulo = request.getParameter("idTitulo");
        String RESP = "";
        pstmt = null;
        rs = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Consulta Resultados METqa");
        try {
            String Query = "SELECT mensajeSep, numLoteSEP,folioControl FROM TETituloElectronico tte WHERE Id_Titulo = ?";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idTitulo);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs.isBeforeFirst()) {
                if (rs.next()) {
                    String folioControl = rs.getString(3);
                    String numLote = rs.getString(2);
                    if (numLote.equalsIgnoreCase("null")) {
                        RESP += "noCargaSEP¬¬" + rs.getString(1) + "¬¬¬" + folioControl;
                    } else {
                        llenarCredencialesSEP();
                        if (usuarioSEP.trim().equalsIgnoreCase("") || passwordSEP.trim().equalsIgnoreCase("")) {
                            RESP = "noCredenciales";
                        } else {
                            ConsultaProcesoTituloElectronico cpte = new ConsultaProcesoTituloElectronico(usuarioSEP, passwordSEP, numLote);
                            RESP = cpte.consultarProcesoSOAP();
                            if (RESP.equalsIgnoreCase("success")) {
                                bitacora.setInformacion("Consulta a servicio de pruebas||" + "MensajeQA:" + cpte.getMensaje() + ",EstatusLoteQA:" + cpte.getEstatusLote() + ",FolioControl:" + folioControl.trim());
                                cBitacora = new CBitacora(bitacora);
                                cBitacora.setRequest(request);
                                cBitacora.addBitacoraGeneral();
                                if (cpte.getEstatusLote().equalsIgnoreCase("1")) {
                                    RESP += "¬" + descargarTitulosWebService(numLote, folioControl) + "¬" + folioControl;
                                } else {
                                    RESP += "¬¬" + cpte.getMensaje() + "¬" + numLote + "¬¬" + folioControl;
                                }
                            } else {
                                RESP = "noConsultaSEP¬¬Ocurrió un error al consultar el estado del título electrónico en el servicio web de la SEP¬" + numLote + "¬¬" + folioControl;
                            }
                        }
                    }
                }
            } else {
                RESP = "noConsultaSQL¬¬Ocurrió un error al consultar en el servidor de base de datos¬¬¬";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del título electrónico en el servicio SEP: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del título electrónico en el servicio SEP: " + accion_catch(ex);
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarNotifyFirmantes() !", ex);
                }
            }
        }
        return RESP;
    }

    public String descargarTitulosWebService(String numLote, String folioControl) {

        String RESP = "";
        DescargaTituloElectronico dte = new DescargaTituloElectronico(usuarioSEP, passwordSEP, numLote);
        RESP = dte.descargarArchivoSOAP();
        if (RESP.equalsIgnoreCase("success")) {
            bitacora = new Bitacora();
            bitacora.setId_Usuario(Id_Usuario);
            bitacora.setModulo("Titulos");
            bitacora.setMovimiento("Descarga Resultados METqa");
            bitacora.setInformacion("Descarga de servicio de pruebas||" + "MensajeQA:" + dte.getMensaje() + ",FolioControl:" + folioControl.trim());
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            try {
                cBitacora.addBitacoraGeneral();
            } catch (SQLException ex) {

            }
            RESP += "¬" + dte.getMensaje() + "¬" + numLote + "¬" + (dte.getArchivoByteArray() == null ? "" : genZipFromBase64(dte.getArchivoByteArray(), folioControl));
        } else {
            RESP = "¬¬Ocurrió un error al intentar descargar la respuesta desde el servicio¬" + numLote + "¬";
        }
        return RESP;
    }

    public String genZipFromBase64(byte[] fileByteArray, String nameFile) {
        String RESP = "";
        try {
            llenarNombreInstitucion();
            File zipFile = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\RT_" + nameFile + ".zip");
            FileUtils.writeByteArrayToFile(zipFile, fileByteArray);
            RESP = "../../../Instituciones/" + NombreInstitucion.trim() + "/XML/Titulos/" + zipFile.getName();
            
            String respuesta = leerZip(nameFile);
            String[] respuesta1= respuesta.split("¬");
      
            String res= respuesta1[0];
            String status= respuesta1[1];

            if (res.equalsIgnoreCase("success")) {
                setEstatusMET(status);             
            } else {
                setEstatusMET("error");    
            }


        } catch (IOException ex) {
            RESP = "IOException";
            ex.printStackTrace();
        }
        return RESP;
    }

    //Metodo para leer el zip en donde se encuentra el excel, en donde encontramos si el titulo se ha verificado exitosamente
    private String leerZip(String name) {
        String respuesta = "";
        try {
            //Ruta en donde se obtendra el zip
            //String rutaLocal="C:/Program Files/Apache Software Foundation/Apache Tomcat 8.0.27/bin/webapps/Instituciones/INSTITUTO KUEPA/XML/Titulos/RT_" + name + ".zip";
            String rutaServidor= System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\RT_" + name + ".zip";
            ZipFile zipFile = new ZipFile(rutaServidor);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
           
            InputStream archivo = null;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream stream = zipFile.getInputStream(entry);

                archivo = stream;
                
            }
       
            String rLeer = leerExcel(archivo, name); //Mandamos el archivo de excel extraido una vez leido el zip y el nombre del archivo
            String[] respuesta1= rLeer.split("¬");

            String res= respuesta1[0];
            String status= respuesta1[1];
            if (res.equalsIgnoreCase("success")) {
                respuesta = "success¬"+status+"¬"+this.mensajeMET;
            } else {
                respuesta = "error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            respuesta = "error";
        }
        return respuesta;

    }
    
    //Metodo para leer el excel y extraer los datos
    private String leerExcel(InputStream archivo, String folioControl) {

        String RESP = "";

        int hojas = 0;
        boolean isNotFound = false;
        int numFilaActual = 0;
        
        try {

            // Para acceder al archivo ingresado
            libro = WorkbookFactory.create(archivo);
            // Traemos el total de hojas de calculo que contiene el archivo
            hojas = libro.getNumberOfSheets();
            // Iniciamos en la primera hoja
            Sheet hojaActual = libro.getSheetAt(0);

            Row rowActual2 = hojaActual.getRow(1); //Es 1 ya que se encuentra en la fila 2, pero empieza desde 0

            Cell cellActual1 = rowActual2.getCell(0); // celda archivo
            Cell cellActual2 = rowActual2.getCell(1); //celda estatus
            Cell cellActual3 = rowActual2.getCell(2); // celda descripcion
            Cell cellActual4 = rowActual2.getCell(3); //celda foliocontrol

            int status = (int) cellActual2.getNumericCellValue(); //Extraemos el estatus y lo convertimos a entero ya que es double
            String descripcion = cellActual3.getStringCellValue();
            status = (descripcion.toUpperCase().contains("FOLIO DE CONTROL PREVIAMENTE REGISTRADO") ? 1 : status);
            System.out.println("Estatus " + status);
            System.out.println("Descripcion " + descripcion);
                            
            archivo.close();

            boolean rAdd = registrarCheckoutMET(folioControl, descripcion, status);
            
            if (rAdd) {
                RESP = "success¬"+status;  //Se hizo de esta manera, solo para tener conocimiento que podemos mandar el estatus de esta manera, sin embargo tambien lo podemos mandar mediante el setEstatusMet
                setMensajeMET(descripcion); //Mandamos el mensaje que extraimos, para ponerlo en el icono como tooltip
            } else {
                RESP = "error";
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, ex.getMessage());
            RESP = "error|Error FileNotFoundException al realizar la lectura del archivo cargado: " + accion_catch(ex);
            isNotFound = true;
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
            System.out.println("RESPUESTA LEER EXCEL FINAL 1 " + RESP);
        }
        System.out.println("RESPUESTA LEER EXCEL FINAL " + RESP);
        return RESP;
    }

    //Guardamos la información en la  tabla de titulo electronico
    private boolean registrarCheckoutMET(String folioControl, String mensaje, int estatus) {
        boolean respuesta = false;
        try {

            String Query = "{CALL Add_MensajeDescripcionMET ?,?,?}";
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            cstmt = con.prepareCall(Query);
            cstmt.setString(1, mensaje);
            cstmt.setInt(2, estatus);
            cstmt.setString(3, folioControl);
            cstmt.execute();
            con.close();
            cstmt.close();

            respuesta = true;

        } catch (Exception e) {
            respuesta = false;
        }

        return respuesta;
    }

    public void llenarCredencialesSEP() {
        ResultSet rsRecord = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {

            con = conexion.GetconexionInSite();

            ps = con.prepareStatement("SELECT usuarioSEP, passwordSEP FROM Configuracion_Inicial CI JOIN Usuario U ON CI.Id_ConfiguracionInicial = U.Id_ConfiguracionInicial WHERE Id_Usuario = ?");

            ps.setString(1, Id_Usuario);

            ps.execute();

            rsRecord = ps.getResultSet();

            if (rsRecord.next()) {
                usuarioSEP = rsRecord.getString(1);
                passwordSEP = rsRecord.getString(2);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rsRecord.close();
                con.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

													   

    public String cargarTitulosWebServiceProductivo(String idTitulo ) {
        String RESP = "";
        pstmt = null;
        rs = null;
        String nameFile = "";
        File xmlFile = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Carga MET");
        try {
            llenarNombreInstitucion();
            String Query = "SELECT folioControl, aPaterno, aMaterno FROM TETituloElectronico tte JOIN Alumnos a on tte.id_profesionista = a.ID_Alumno JOIN Persona p ON a.ID_Persona = p.Id_Persona WHERE Id_Titulo = ?";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idTitulo);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs.next()) {
                llenarCredencialesSepProductivo();
                if (usuarioSEP.trim().equalsIgnoreCase("") || passwordSEP.trim().equalsIgnoreCase("")) {
                    RESP = "noCredenciales";
                } else {
                    String folioControl = rs.getString(1).trim();
                    nameFile = folioControl + "_" + rs.getString(2).trim() + (rs.getString(3) == null ? "" : rs.getString(3).trim()) + ".xml";
                    xmlFile = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\" + folioControl + "_" + rs.getString(2).trim() + (rs.getString(3) == null ? "" : rs.getString(3).trim()) + ".xml");

                    CargaTituloElectronicoProductivo ctep = new CargaTituloElectronicoProductivo(usuarioSEP, passwordSEP, nameFile);
                    RESP = ctep.fileToArray(xmlFile);
                    if (RESP.equalsIgnoreCase("ok")) {
                        RESP = ctep.cargarArchivoSOAP();
                        if (RESP.equalsIgnoreCase("success")) {
                            bitacora.setInformacion("Carga a servicio productivo||" + "MensajeMET:" + ctep.getMensaje() + ",NumLoteMET:" + ctep.getNumLote() + ",FolioControl:" + folioControl.trim());
                            cBitacora = new CBitacora(bitacora);
                            cBitacora.setRequest(request);
                            cBitacora.addBitacoraGeneral();
                            Query = "UPDATE TETituloElectronico SET envioSepProductivo = 1, mensajeSepProductivo = ?, numLoteSepProductivo = ? WHERE id_titulo = ?";
                            pstmt = con.prepareStatement(Query);
                            pstmt.setString(1, ctep.getMensaje());
                            pstmt.setString(2, ctep.getNumLote());
                            pstmt.setString(3, idTitulo);
                            pstmt.execute();
                        }
                    }
                    System.out.println("hola");
                }
            }
        } catch (SQLException ex) {
            RESP = "SQL¬" + ex.getMessage().replace("'", "");
        } catch (Exception e){
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método cargarTitulosWebServiceProductivo() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método cargarTitulosWebServiceProductivo() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarTitulosWebServiceProductivo() !", ex);
                }
            }
        }
        return RESP;
    }

    public String consultarTitulosWebServiceProductivo() {
        String idTitulo = request.getParameter("idTitulo");
        String RESP = "";
        pstmt = null;
        rs = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Consulta Resultados MET");
        try {
            String Query = "SELECT mensajeSepProductivo, numLoteSepProductivo,folioControl FROM TETituloElectronico tte WHERE Id_Titulo = ?";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idTitulo);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs.isBeforeFirst()) {
                if (rs.next()) {
                    String folioControl = rs.getString(3);
                    String numLote = rs.getString(2);
                    if (numLote.equalsIgnoreCase("")) {
                        RESP += "noCargaSEP¬¬" + rs.getString(1) + "¬¬¬" + folioControl;
                    } else {
                        llenarCredencialesSepProductivo();
                        if (usuarioSEP.trim().equalsIgnoreCase("") || passwordSEP.trim().equalsIgnoreCase("")) {
                            RESP = "noCredenciales";
                        } else {
                            ConsultaProcesoTituloElectronicoProductivo cptep = new ConsultaProcesoTituloElectronicoProductivo(usuarioSEP, passwordSEP, numLote);
                            RESP = cptep.consultarProcesoSOAP();
                            if (RESP.equalsIgnoreCase("success")) {
                                bitacora.setInformacion("Consulta a servicio productivo||" + "MensajeMET:" + cptep.getMensaje() + ",EstatusLoteMET:" + cptep.getEstatusLote() + ",FolioControl:" + folioControl.trim());
                                cBitacora = new CBitacora(bitacora);
                                cBitacora.setRequest(request);
                                cBitacora.addBitacoraGeneral();
                                if (cptep.getEstatusLote().equalsIgnoreCase("1")) {
                                    RESP += "¬" + descargarTitulosWebServiceProductivo(numLote, folioControl) + "¬" + folioControl+"¬"+ this.estatusMET+"¬"+this.mensajeMET; //Concatenamos el estatus y el mensaje para refrescarlo en el html                                  
                                } else {
                                    RESP += "¬¬" + cptep.getMensaje() + "¬" + numLote + "¬¬" + folioControl;                                 
                                }
                            } else {
                                RESP = "noConsultaSEP¬¬Ocurrió un error al consultar el estado del título electrónico en el servicio web de la SEP¬" + numLote + "¬¬" + folioControl;
                            }
                        }
                    }
                }
            } else {
                RESP = "noConsultaSQL¬¬Ocurrió un error al consultar en el servidor de base de datos¬¬¬";
            }
        } catch (SQLException ex) {
            RESP = "SQL¬" + ex.getMessage().replace("'", "");
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método consultarTitulosWebServiceProductivo() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método consultarTitulosWebServiceProductivo() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método consultarTitulosWebServiceProductivo() !", ex);
                }
            }
        }       
        return RESP;
    }

    public String descargarTitulosWebServiceProductivo(String numLote, String folioControl) {

        String RESP = "";
        DescargaTituloElectronicoProductivo dtep = new DescargaTituloElectronicoProductivo(usuarioSEP, passwordSEP, numLote);
        RESP = dtep.descargarArchivoSOAP();
        if (RESP.equalsIgnoreCase("success")) {
            bitacora = new Bitacora();
            bitacora.setId_Usuario(Id_Usuario);
            bitacora.setModulo("Titulos");
            bitacora.setMovimiento("Descarga Resultados MET");
            bitacora.setInformacion("Descarga de servicio productivo||" + "MensajeMET:" + dtep.getMensaje() + ",FolioControl:" + folioControl.trim());
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            try {
                cBitacora.addBitacoraGeneral();
            } catch (SQLException ex) {

            }
            RESP += "¬" + dtep.getMensaje() + "¬" + numLote + "¬" + (dtep.getArchivoByteArray() == null ? "" : genZipFromBase64(dtep.getArchivoByteArray(), folioControl));
        } else {
            RESP = "¬¬Ocurrió un error al intentar descargar la respuesta desde el servicio¬" + numLote + "¬";
        }
        return RESP;
    }

    public String cancelarTitulosWebServiceProductivo() {
        String idTitulo = request.getParameter("idTitulo");
        String motCancelacion = request.getParameter("motCancelacion");
        String RESP = "";
        pstmt = null;
        rs = null;
        cstmt = null;
        try {
            String Query = "SELECT mensajeSepProductivo, numLoteSepProductivo,folioControl FROM TETituloElectronico tte WHERE Id_Titulo = ?";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idTitulo);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs.next()) {
                String folioControl = rs.getString(3);
                String numLote = rs.getString(2);
                if (numLote.equalsIgnoreCase("null")) {
                    RESP += "noCargaSEP¬¬" + rs.getString(1) + "¬¬" + folioControl;
                } else {
                    llenarCredencialesSepProductivo();
                    CancelaTituloElectronicoProductivo ctep = new CancelaTituloElectronicoProductivo(folioControl, motCancelacion, usuarioSEP, passwordSEP);
                    RESP = ctep.cancelarTituloElectronicoSOAP();
                    if (RESP.equalsIgnoreCase("success")) {
//                        ctep.setCodigo("0");
//                        ctep.setMensaje("El folio CBGE17028750 fue cancelado exitosamente.");
                        if (ctep.getCodigo().equalsIgnoreCase("0")) {
                            //ACTUALIZAMOS ESTATUS A 2 INDICANDO QUE ESTÁ CANCELADO.
                            Query = "{call Cancelar_Titulo (?,?,?,?)}";
                            cstmt = con.prepareCall(Query);
                            cstmt.setString(1, idTitulo);
                            cstmt.setString(2, motCancelacion);
                            cstmt.setString(3, ctep.getMensaje());
                            cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);
                            cstmt.execute();
                            String idTituloCancelado = cstmt.getString(4);
                            if (idTituloCancelado != null) {
                                RESP += "¬canceladoSQL¬" + ctep.getMensaje() + "¬" + ctep.getMotCancelacion() + "¬" + folioControl;
                                bitacora = new Bitacora();
                                bitacora.setId_Usuario(Id_Usuario);
                                bitacora.setModulo("Titulos");
                                bitacora.setMovimiento("Cancelación");
                                bitacora.setInformacion("Cancelación Título Productivo||" + "MensajeMET:" + ctep.getMensaje() + "MotivoCancelación:" + ctep.getMotCancelacion() + ",FolioControl:" + folioControl.trim());
                                cBitacora = new CBitacora(bitacora);
                                cBitacora.setRequest(request);
                                cBitacora.addBitacoraGeneral();
                            } else {
                                RESP += "¬errorSQL¬" + ctep.getMensaje() + "¬" + ctep.getMotCancelacion() + "¬" + folioControl;
                            }
                        } else {
                            RESP += "¬noCanceladoSOAP¬" + ctep.getMensaje() + "¬" + ctep.getMotCancelacion() + "¬" + folioControl;
                        }
                    } else {
                        RESP = "noConsultaSEP¬¬Ocurrió un error al consultar el estado del título electrónico en el servicio web de la SEP.<br>Número de Lote: <b>" + numLote + "</b>" + "¬" + folioControl;
                    }
                }
            } else {
                RESP = "noConsultaSQL¬¬Ocurrió un error al consultar el título electrónico en la base de datos¬¬";
            }
        } catch (SQLException ex) {
            RESP = "exceptionSQL¬¬" + ex.getMessage().replace("'", "") + "¬¬";
        }
        return RESP;
    }

    public void llenarCredencialesSepProductivo() {
        ResultSet rsRecord = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {

            con = conexion.GetconexionInSite();

            ps = con.prepareStatement("SELECT usuarioSepProductivo, passwordSepProductivo FROM Configuracion_Inicial CI JOIN Usuario U ON CI.Id_ConfiguracionInicial = U.Id_ConfiguracionInicial WHERE Id_Usuario = ?");

            ps.setString(1, Id_Usuario);

            ps.execute();

            rsRecord = ps.getResultSet();

            if (rsRecord.next()) {
                usuarioSEP = rsRecord.getString(1) == null ? "" : rsRecord.getString(1);
                passwordSEP = rsRecord.getString(2) == null ? "" : rsRecord.getString(2);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rsRecord.close();
                con.close();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String consultarInfoTituloCancelado() {
        String Query = "";
        String RESP = "";
        String idTitulo = request.getParameter("idTitulo");
        pstmt = null;
        rs = null;
        try {
																																																						   
            String respuesta = verificarTituloAutenticado(idTitulo);
            if (respuesta == "0") { //No hay titulo autenticado
                Query = "SELECT folioControl, tte.Id_Motivo_Can, mensajeCancelacion FROM TETituloElectronico tte JOIN TEMotivos_Cancelacion tmc ON tte.Id_Motivo_Can = tmc.Id_Motivo_Can WHERE estatus = 2 AND id_titulo = ? ";
                conexion = new CConexion();
                conexion.setRequest(request);
                con = conexion.GetconexionInSite();
                pstmt = con.prepareStatement(Query);
                pstmt.setString(1, idTitulo);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    RESP = "success¬" + rs.getString(1) + "¬" + rs.getString(2) + "¬" + (rs.getString(3) == null ? "" : rs.getString(3));
                } else {
                    RESP = "noCancelado";
                }

            } else if (respuesta == "1") { //Si hay titulo autenticado 
                RESP = "1";
            } else {
                RESP = respuesta;
            }

        } catch (Exception ex) {

        }
        return RESP;
    }

    private String consultarLibroFoja() throws SQLException {
        String Query = "";
        String RESP = "";
        String idTitulo = request.getParameter("idTitulo");
        pstmt = null;
        rs = null;
        try {
            Query = "SELECT matricula, nombre,apaterno,amaterno,descripcion,folioControl,libro,foja\n"
                    + "FROM Alumnos a \n"
                    + "JOIN Persona p ON a.ID_Persona = p.Id_Persona\n"
                    + "JOIN Carrera c ON a.ID_Carrera = c.ID_Carrera\n"
                    + "JOIN TETituloElectronico tte ON a.ID_Alumno = tte.id_profesionista\n"
                    + "LEFT JOIN LibroFoja lf ON tte.id_titulo = lf.Id_Titulo\n"
                    + "WHERE tte.id_titulo = ?";
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idTitulo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                RESP = "success¬" + rs.getString(1) + "¬"
                        + rs.getString(2) + " " + rs.getString(3) + " " + (rs.getString(4) == null ? "" : rs.getString(4)) + "¬"
                        + rs.getString(5) + "¬" + rs.getString(6) + "¬" + (rs.getString(7) == null ? "" : rs.getString(7)) + "¬"
                        + (rs.getString(8) == null ? "" : rs.getString(8));
            } else {
                RESP = "noTitulo";
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return RESP;
    }

    private String modificarLibroFoja() {
        String RESP = "";
        String idTitulo = request.getParameter("idTitulo");
        String libro = request.getParameter("txtLibro");
        String foja = request.getParameter("txtFoja");
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Modificación");
        try {
//            String Query = "SET LANGUAGE 'español'; EXECUTE Delt_Titulo " + idTitulo;
            String Query = "{call Upd_LibroFojaTitulos (?,?,?,?)}";

            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setString(1, libro.trim());
            cstmt.setString(2, foja.trim());
            cstmt.setString(3, idTitulo);
            cstmt.registerOutParameter(4, Types.VARCHAR);
            cstmt.execute();

            if (cstmt.getResultSet() == null && cstmt.getUpdateCount() != -1) {
                RESP = cstmt.getString(4);
                if (RESP != null) {
                    if (!RESP.equalsIgnoreCase("")) {
                        RESP = "success";
                        bitacora.setInformacion("Modificación de libro y foja del título: " + idTitulo + "||Respuesta Método: " + RESP);
                        cBitacora = new CBitacora(bitacora);
                        cBitacora.setRequest(request);
                        cBitacora.addBitacoraGeneral();
                        llenarFormatoTitulo(idTitulo);
                    }
                }
            } else {
                RESP = "error|Error al momento de actualizar el título electrónico, intente de nuevo. <br> Si el problema persiste contacte a soporte técnico";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la actualización del título electrónico (Libro y Foja): " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException al realizar la actualización del título electrónico (Libro y Foja): " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la actiualización del título electrónico (Libro y Foja): " + accion_catch(ex);
        }
        return RESP;
    }

    private boolean existeTabla() throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        String nombreBD = request.getParameter("bdName");
        ResultSet rs = md.getTables(nombreBD, "dbo", "LibroFoja", null);
        return rs.next();
    }

    /**
     *
     * @param infoTitulo fila actual
     * @return boolean [0] = información completa. [1] = es fila vacia.
     */
    private Object[] validarCampos(String[] infoTitulo) {
        boolean complete = true;
        boolean filaVacia = false;
        //Se utiliza un objeto potque no sabemos cuantas filas saldran vacias
        //Y un numero maximo, pero se utilizo una lista
        List<Integer> celdasVacias = new ArrayList<>();

        boolean[] validaciones = new boolean[2];
        int contador = 0;

        for (int i = 0; i < infoTitulo.length - 1; i++) {

            if ((infoTitulo[i] == null || infoTitulo[i].toString().trim().equalsIgnoreCase("")) && (
                    i != 2 && i != 3 
                    && i != 9 
                    && i != 10 
                    && i != 14 
                    && i != 16 
                    && i != 18 
                    && i != 19 
                    && i != 20)) {
                complete = false;
                contador++;
                celdasVacias.add(i);
            }
        }

        if (contador == infoTitulo.length - 9) {
            filaVacia = true;
        }

        validaciones[0] = complete;
        validaciones[1] = filaVacia;

        return new Object[] { validaciones, celdasVacias };
    }
    
    private String validarFechasExamen(String fechaExamen, String fechaExencion){
        String RESP = "";
        if( (fechaExamen == null || fechaExamen.trim().equalsIgnoreCase("")) && (fechaExencion == null || fechaExencion.trim().equalsIgnoreCase("")) ){
            return "fechasExamenInvalidas||<p style='color: #545454;'>Se debe de ingresar una <strong>Fecha de Examen Profesional</strong> o una <strong>Fecha de Exención de Examen Profesional</strong></p>";
        }
        if( (fechaExamen != null && !fechaExamen.trim().equalsIgnoreCase("")) && (fechaExencion != null && !fechaExencion.trim().equalsIgnoreCase("")) ){
            return "fechasExamenInvalidas||<p style='color: #545454;'>Se debe de ingresar solo la <strong>Fecha de Examen Profesional</strong> o la <strong>Fecha de Exención de Examen Profesional</strong>. No se deben de ingresar las dos.</p>";
        }
        
        return "success";
        
    }

    private String validarFirmantes(String curp, String curpNoObligatorio, int fila) {
        //vamos a validar los firmantes ingresados.
        String cadenaFirmantes = "";
        ResultSet rsRecord = null;
        try {
            String query = "SELECT Id_Firmante FROM TEFirmantes WHERE CURP = '" + curp + "'";
            rsRecord = con.createStatement().executeQuery(query);
            if (rsRecord.next()) {
                cadenaFirmantes += rsRecord.getString(1) + ",";
            } else {
                cadenaFirmantes = "noFirmante|" + (fila + 4);
            }
            if (!curpNoObligatorio.trim().equalsIgnoreCase("")) {
                query = "SELECT Id_Firmante FROM TEFirmantes WHERE CURP = '" + curpNoObligatorio + "'";
                rsRecord = con.createStatement().executeQuery(query);
                if (rsRecord.next()) {
                    cadenaFirmantes += rsRecord.getString(1) + ",";
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return cadenaFirmantes;
    }

    //Metodo que verifica si ell titulo esta autenticado para poder eliminarlo, si esta autenticado no lo dejará eliminar hasta que lo elimine de autenticados
    private String verificarTituloAutenticado(String id) {
        String RESP = "";
        String idTitulo = id;
        String idRecuperado = "";

        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;

        try {
            String Query = "select Id_Titulo_Autenticado from TitulosAutenticados where id_titulo=?;";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, idTitulo);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                idRecuperado = rs.getString("Id_Titulo_Autenticado");
            }
            
            if (idRecuperado == "") {
                System.out.println("No hay titulo autenticado");
                RESP = "0";

            } else {
                System.out.println("SI HAY TITULO AUTENTICADO");
                RESP = "1";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del Titulo Autenticado: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del Titulo Autenticado: " + accion_catch(ex);
        } finally {
            try {
                con.close();
                pstmt.close();
                rs.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
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