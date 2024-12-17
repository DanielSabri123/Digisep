/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.ReporteDGAIR;
import com.ginndex.titulos.modelo.TETituloElectronico;
import com.ginndex.titulos.modelo.TETitulosCarreras;
import com.ginndex.titulos.soap.ConsultaProcesoTituloElectronicoProductivo;
import com.ginndex.titulos.soap.DescargaTituloElectronicoProductivo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author BSorcia
 */
public class CReporteDGAIR {

    private HttpServletRequest request;
    private String Id_Usuario;
    private String bandera;
    private CConexion conexion;
    private String permisos;
    private Connection con;
    private List<String> lstEncabezados;
    String NombreInstitucion;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private String passwordSEP;
    private String usuarioSEP;

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String establecerAcciones() throws UnsupportedEncodingException, SQLException {
        String RESP = "";
        //long startTime = System.nanoTime();
        bandera = request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera");
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");

        HttpSession sessionOk = request.getSession();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        conexion = new CConexion();
        conexion.setRequest(request);
        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        permisos = cPermisos.obtenerPermisos("Reporte DGAIR");
        con = conexion.GetconexionInSite();
        switch (bandera) {
            case "1":
                RESP = consultarListaCarreras();
                break;
            case "2":
                RESP = armarReporte();
                break;
        }
        return RESP;
    }

    private String consultarListaCarreras() {
        String htmlListaCarrera = "<option value='0' data-cve=''>Todas</option>";
        List<TETitulosCarreras> lstCarreras = new ArrayList<>();
        try {
            String query = "SELECT id_carrera,Id_Carrera_Excel,nombreCarrera,cveCarrera FROM TETitulosCarreras ORDER BY Id_Carrera_Excel";
            ResultSet rs;
            rs = con.prepareStatement(query).executeQuery();
            while (rs.next()) {
                TETitulosCarreras carrera = new TETitulosCarreras();

                carrera.setID_Carrera(rs.getString("id_carrera"));
                carrera.setId_Carrera_Excel(rs.getString("id_carrera_excel"));
                carrera.setNombreCarrera(rs.getString("nombreCarrera"));
                carrera.setClaveCarrera(rs.getString("cveCarrera"));

                lstCarreras.add(carrera);
            }

            htmlListaCarrera = lstCarreras.stream().map(elemento -> "<option value='" + elemento.getId_Carrera_Excel() + "'>" + elemento.getId_Carrera_Excel() + " - " + elemento.getNombreCarrera() + "</option>")
                    .reduce(htmlListaCarrera, String::concat);

        } catch (SQLException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return htmlListaCarrera;
    }

    private String armarReporte() {
        long startTime = System.currentTimeMillis();
        if (permisos.contains("acceso") && !permisos.split("¬")[1].equalsIgnoreCase("1")) {
            return "notAllowed";
        }
        llenarNombreInstitucion();
        consultarTitulosSinEstatus();
        String resp = "";
        List<ReporteDGAIR> lstDatosReporte = new ArrayList<>();
        ResultSet rs;

        String fechaInicio = request.getParameter("fi");
        String fechaFin = request.getParameter("ff");
        String idCarrera = request.getParameter("carrera");
        String tipoReporte = request.getParameter("opt");
        String mostrarPromedioGeneral = request.getParameter("promedio");
        String mostrarFolioControl = request.getParameter("folio");

        //SE USA TRY CAST DEBIDO A QUE PODEMOS ENCONTRAR VALORES MÁS GRANDES DE LO ESPERADO.
        //CON TRAY CAST SE EVITA ALGUN ERROR.
        //https://learn.microsoft.com/en-us/answers/questions/401547/how-to-avoid-error-arithmetic-overflow-error-conve
        String query = "set dateformat dmy;SELECT 'Certificado de estudios' as Estatus, year(fechaExpedicion) as 'Año del ciclo escolar',\n"
                + "CURP,Promedio as 'Promedio General',(CASE WHEN idTipoCertificado = 79 THEN 'Certificado Total' ELSE 'Certificado Parcial' END) AS 'Tipo de documento',fechaExpedicion,\n"
                + "FORMAT (fechaExpedicion,'yyyyMMdd') AS 'Fecha Expedición del documento',folioControl as 'Folio del documento'\n"
                + "FROM TECERTIFICADOELECTRONICO tce JOIN Alumnos a on tce.id_profesionista = a.ID_Alumno JOIN PERSONA p on a.ID_Persona = p.Id_Persona\n"
                + "WHERE tce.estatus = 1 " + (!idCarrera.equalsIgnoreCase("0") ? " AND tce.Id_Carrera_Excel = " + idCarrera : "") + " AND (fechaExpedicion\n"
                + " between  '" + fechaInicio + "' AND '" + fechaFin + "')\n"
                + "UNION\n"
                + "SELECT 'Título' as Estatus, year(fechaExpedicion) as 'Año del ciclo escolar',\n"
                + "CURP,(SELECT  TRY_CAST(AVG(CASE WHEN m.tipo <>266  THEN C.Calificacion ELSE 0 END) AS decimal(5,2)) promedio\n"
                + "                            FROM Calificaciones AS C \n"
                + "                            JOIN Alumnos AS A ON A.ID_Alumno = C.ID_Alumno \n"
                + "                            JOIN Carrera AS CA ON CA.ID_Carrera = A.ID_Carrera \n"
                + "                            JOIN Materias AS M ON M.ID_Materia = C.ID_Materia \n"
                + "                            WHERE C.ID_Alumno = id_profesionista AND C.ID_Materia \n"
                + "                            IN (\n"
                + "                            SELECT ID_Materia \n"
                + "                            FROM Materias AS M \n"
                + "                            JOIN Curso AS CU ON CU.ID_Curso = M.ID_Curso \n"
                + "                            WHERE CU.ID_Carrera = tte.id_carrera)) as 'Promedio General',\n"
                + "							(CASE WHEN car.Descripcion like '%Licenciatura%' THEN 'Título Profesional' \n"
                + "							   ELSE (CASE WHEN (car.Descripcion like '%Maestría%' OR car.Descripcion like '%Doctorado%') THEN 'Grado' \n"
                + "							      ELSE (CASE WHEN car.Descripcion like 'Especialidad%' THEN 'Diploma' ELSE 'No identificado' END )\n"
                + "							   END) \n"
                + "							END) AS 'Tipo de documento',fechaExpedicion,\n"
                + "FORMAT (fechaExpedicion,'yyyyMMdd') AS 'Fecha Expedición del documento',folioControl as 'Folio del documento'\n"
                + "FROM TETituloElectronico tte JOIN Alumnos a on tte.id_profesionista = a.ID_Alumno JOIN PERSONA p on a.ID_Persona = p.Id_Persona\n"
                + "JOIN TETitulosCarreras ttc ON tte.id_carrera = ttc.id_carrera JOIN Carrera car on ttc.Id_Carrera_Excel = car.Id_Carrera_Excel\n"
                + "WHERE tte.estatus = 1 " + (!idCarrera.equalsIgnoreCase("0") ? " AND ttc.Id_Carrera_Excel = " + idCarrera : "") + " AND (fechaExpedicion\n"
                + " between  '" + fechaInicio + "' AND '" + fechaFin + "')  AND estatusMet = 1 order by folioControl";

        try {
            con = conexion.GetconexionInSite();
            rs = con.prepareStatement(query).executeQuery();

            while (rs.next()) {
                ReporteDGAIR reporte = new ReporteDGAIR();

                reporte.setEstatus(rs.getString("Estatus"));
                reporte.setAnioCicloEscolar(rs.getString("Año del ciclo escolar"));
                reporte.setCurp(rs.getString("CURP"));
                reporte.setPromedioGeneral(rs.getString("Promedio General"));
                reporte.setTipoDocumento(rs.getString("Tipo de documento"));
                reporte.setFechaExpedicionDocumento(rs.getString("Fecha Expedición del documento"));
                reporte.setFolioDocumento(rs.getString("Folio del documento"));

                lstDatosReporte.add(reporte);
            }
            List<ReporteDGAIR> lstFiltro;
            if (tipoReporte.equalsIgnoreCase("1")) {
                //solo titulos
                lstFiltro = lstDatosReporte.stream().filter(datos -> datos.getEstatus().equalsIgnoreCase("Título")).collect(Collectors.toList());
            } else if (tipoReporte.equalsIgnoreCase("2")) {
                //solo certificados
                lstFiltro = lstDatosReporte.stream().filter(datos -> datos.getEstatus().equalsIgnoreCase("Certificado de estudios")).collect(Collectors.toList());
            } else {
                //ambos
                lstFiltro = lstDatosReporte;
            }
            resp = generarExcel(lstFiltro, mostrarPromedioGeneral, mostrarFolioControl);
        } catch (SQLException e) {
            Logger.getLogger(CReporteDGAIR.class.getName()).log(Level.SEVERE, null, e);
            resp = "error";
        }

        long endTime = System.currentTimeMillis();

        System.out.println("That took " + (endTime - startTime) + " milliseconds");
        return resp;
    }

    private String generarExcel(List<ReporteDGAIR> lstFiltro, String mostrarPromedioGeneral, String mostrarFolioControl) {

        lstEncabezados = new ArrayList<>();
        lstEncabezados.add("Estatus");
        lstEncabezados.add("Año del ciclo escolar");
        lstEncabezados.add("CURP");
        lstEncabezados.add("Promedio General");
        lstEncabezados.add("Tipo de documento");
        lstEncabezados.add("Fecha Expedición del documento");
        lstEncabezados.add("Folio del documento");

        Workbook reporte = new XSSFWorkbook();
        Sheet hojaLLenado = reporte.createSheet("Hoja de Llenado");
        CellStyle cs = CReporteDGAIR.getHeaderCellStyle(reporte);
        llenarEncabezadoTitulo(hojaLLenado, cs);
        if (lstFiltro.isEmpty()) {
            return "empty";
        }
        //https://stackoverflow.com/questions/28790784/java-8-preferred-way-to-count-iterations-of-a-lambda
        AtomicInteger i = new AtomicInteger(0);
        lstFiltro.stream().map((row) -> {
            Row fila = hojaLLenado.createRow(i.intValue() + 1);
            i.incrementAndGet();
            XSSFCellStyle style = getBodyCellStyle(reporte, false);
            Cell cell = fila.createCell(0);
            cell.setCellValue(row.getEstatus());
            cell.setCellStyle(style);
            //hojaLLenado.autoSizeColumn(0);
            style = getBodyCellStyle(reporte, true);
            cell = fila.createCell(1);
            cell.setCellValue(row.getAnioCicloEscolar());
            cell.setCellStyle(style);
            //hojaLLenado.autoSizeColumn(1);
            cell = fila.createCell(2);
            cell.setCellValue(row.getCurp());
            cell.setCellStyle(style);
            //hojaLLenado.autoSizeColumn(2);
            style = getBodyCellStyle(reporte, false);
            cell = fila.createCell(3);
            cell.setCellValue(mostrarPromedioGeneral.equalsIgnoreCase("1") ? row.getPromedioGeneral() : "");
            cell.setCellStyle(style);
            //hojaLLenado.autoSizeColumn(3);
            cell = fila.createCell(4);
            cell.setCellValue(row.getTipoDocumento());
            cell.setCellStyle(style);
            //hojaLLenado.autoSizeColumn(4);
            cell = fila.createCell(5);
            cell.setCellValue(row.getFechaExpedicionDocumento());
            cell.setCellStyle(style);
            //hojaLLenado.autoSizeColumn(5);
            cell = fila.createCell(6);
            cell.setCellValue(mostrarFolioControl.equalsIgnoreCase("1") ? row.getFolioDocumento() : "");
            cell.setCellStyle(style);
            return row;
        }).forEachOrdered((_item) -> {
            //hojaLLenado.autoSizeColumn(6);
        });

        //Se omite el proceso anterior pues hacía demasiado pesada la carga.
        for (int j = 0; j < lstEncabezados.size(); j++) {
            hojaLLenado.autoSizeColumn(j);
        }

        String fechaGen = sdf.format(new Date());
        String path = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\REPORTE CERTIFICADOS Y TITULOS SEP FEDERAL " + fechaGen + ".xlsx";
        try (FileOutputStream doc = new FileOutputStream(path)) {
            reporte.write(doc);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CReporteDGAIR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CReporteDGAIR.class.getName()).log(Level.SEVERE, null, ex);
        }
        File xlsx = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\REPORTE CERTIFICADOS Y TITULOS SEP FEDERAL " + fechaGen + ".xlsx");
        if (xlsx.exists()) {
            return "../../../Instituciones/" + NombreInstitucion.trim() + "/ArchivosInstitucionales/REPORTE CERTIFICADOS Y TITULOS SEP FEDERAL " + fechaGen + ".xlsx";
        }
        return "error";
    }

    private static CellStyle getHeaderCellStyle(Workbook excel) {
        byte[] rgb = new byte[3];
        rgb[0] = (byte) 198; // red
        rgb[1] = (byte) 239; // green
        rgb[2] = (byte) 206; // blue
        XSSFColor myColor = new XSSFColor(rgb); // #C6EFCE

        XSSFCellStyle cs = (XSSFCellStyle) excel.createCellStyle();
        cs.setAlignment(CellStyle.ALIGN_CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setFillForegroundColor(myColor);
        cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cs.setFont(getFont(excel));
        return cs;
    }

    private static XSSFCellStyle getBodyCellStyle(Workbook excel, boolean alignCenter) {

        XSSFCellStyle style = (XSSFCellStyle) excel.createCellStyle();
        if (alignCenter) {
            style.setAlignment(CellStyle.ALIGN_CENTER);
        }
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }

    private void llenarEncabezadoTitulo(Sheet sheet, CellStyle cs) {
        Row encabezados = sheet.createRow(0);
        encabezados.setHeightInPoints((float) 19.5);
        for (int a = 0; a < lstEncabezados.size(); a++) {
            Cell celda = encabezados.createCell(a);
            celda.setCellValue(lstEncabezados.get(a));
            celda.setCellStyle(cs);
            sheet.autoSizeColumn(a);
        }
    }

    public void llenarNombreInstitucion() {
        try {
            String Query = "SELECT nombreInstitucion FROM Configuracion_Inicial AS CI "
                    + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                    + " WHERE Id_Usuario = " + Id_Usuario;
            PreparedStatement pstmt = con.prepareStatement(Query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                NombreInstitucion = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Font getFont(Workbook excel) {
        //configuramos la fuente
        Font font = excel.createFont();
        font.setBold(true);
        font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.GREEN.getIndex());
        return font;
    }

    private void consultarTitulosSinEstatus() {
        try {
            llenarCredencialesSepProductivo();

            List<TETituloElectronico> lstTitulos = new ArrayList<>();
            String Query = "SELECT numLoteSepProductivo,folioControl FROM TETITULOELECTRONICO WHERE estatusMet is null AND ESTATUS = 1 AND envioSepProductivo = 1";
            PreparedStatement pstmt = con.prepareStatement(Query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TETituloElectronico titulo = new TETituloElectronico();

                titulo.setNumLote(rs.getString("numLoteSepProductivo"));
                titulo.setFolioControl(rs.getString("folioControl"));

                lstTitulos.add(titulo);
            }
            lstTitulos.stream().forEach(titulo -> consultarTitulosWebServiceProductivo(titulo.getNumLote(), titulo.getFolioControl()));
        } catch (SQLException ex) {
            Logger.getLogger(CReporteDGAIR.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void consultarTitulosWebServiceProductivo(String numLote, String folioControl) {
        String RESP = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Consulta Resultados MET");
        try {

            ConsultaProcesoTituloElectronicoProductivo cptep = new ConsultaProcesoTituloElectronicoProductivo(usuarioSEP, passwordSEP, numLote);
            RESP = cptep.consultarProcesoSOAP();
            if (RESP.equalsIgnoreCase("success")) {
                bitacora.setInformacion("Consulta a servicio productivo||" + "MensajeMET:" + cptep.getMensaje() + ",EstatusLoteMET:" + cptep.getEstatusLote() + ",FolioControl:" + folioControl.trim());
                CBitacora cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
                if (cptep.getEstatusLote().equalsIgnoreCase("1")) {
                    descargarTitulosWebServiceProductivo(numLote, folioControl);
                }
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
    }

    private void descargarTitulosWebServiceProductivo(String numLote, String folioControl) {

        String RESP = "";
        DescargaTituloElectronicoProductivo dtep = new DescargaTituloElectronicoProductivo(usuarioSEP, passwordSEP, numLote);
        RESP = dtep.descargarArchivoSOAP();
        if (RESP.equalsIgnoreCase("success")) {
            Bitacora bitacora = new Bitacora();
            bitacora.setId_Usuario(Id_Usuario);
            bitacora.setModulo("Titulos");
            bitacora.setMovimiento("Descarga Resultados MET");
            bitacora.setInformacion("Descarga de servicio productivo||" + "MensajeMET:" + dtep.getMensaje() + ",FolioControl:" + folioControl.trim());
            CBitacora cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            try {
                cBitacora.addBitacoraGeneral();
            } catch (SQLException ex) {

            }
            if (dtep.getArchivoByteArray() != null) {
                genZipFromBase64(dtep.getArchivoByteArray(), folioControl);
            }
        }

    }

    public void genZipFromBase64(byte[] fileByteArray, String nameFile) {
        try {
            File zipFile = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\RT_" + nameFile + ".zip");
            FileUtils.writeByteArrayToFile(zipFile, fileByteArray);
            leerZip(nameFile);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Metodo para leer el zip en donde se encuentra el excel, en donde encontramos si el titulo se ha verificado exitosamente
    private void leerZip(String name) {
        try {
            //Ruta en donde se obtendra el zip
            //String rutaLocal="C:/Program Files/Apache Software Foundation/Apache Tomcat 8.0.27/bin/webapps/Instituciones/INSTITUTO KUEPA/XML/Titulos/RT_" + name + ".zip";
            String rutaServidor = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\Titulos\\RT_" + name + ".zip";
            ZipFile zipFile = new ZipFile(rutaServidor);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            InputStream archivo = null;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream stream = zipFile.getInputStream(entry);
                archivo = stream;
            }

            leerExcel(archivo, name); //Mandamos el archivo de excel extraido una vez leido el zip y el nombre del archivo
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Metodo para leer el excel y extraer los datos
    private void leerExcel(InputStream archivo, String folioControl) {

        String RESP = "";

        try {

            // Para acceder al archivo ingresado
            Workbook libro = WorkbookFactory.create(archivo);

            // Iniciamos en la primera hoja
            Sheet hojaActual = libro.getSheetAt(0);

            Row rowActual2 = hojaActual.getRow(1); //Es 1 ya que se encuentra en la fila 2, pero empieza desde 0

            Cell cellActual1 = rowActual2.getCell(0); // celda archivo
            Cell cellActual2 = rowActual2.getCell(1); //celda estatus
            Cell cellActual3 = rowActual2.getCell(2); // celda descripcion
            Cell cellActual4 = rowActual2.getCell(3); //celda foliocontrol

            int status = (int) cellActual2.getNumericCellValue(); //Extraemos el estatus y lo convertimos a entero ya que es double
            String descripcion = cellActual3.getStringCellValue();
            System.out.println("Estatus " + status);
            System.out.println("Descripcion " + descripcion);

            archivo.close();

            registrarCheckoutMET(folioControl, descripcion, status);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.INFO, ex.getMessage());
            RESP = "error|Error FileNotFoundException al realizar la lectura del archivo cargado: " + accion_catch(ex);
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
    }

    //Guardamos la información en la  tabla de titulo electronico
    private boolean registrarCheckoutMET(String folioControl, String mensaje, int estatus) {
        boolean respuesta = false;
        try {

            String Query = "{CALL Add_MensajeDescripcionMET ?,?,?}";
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            CallableStatement cstmt = con.prepareCall(Query);
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
