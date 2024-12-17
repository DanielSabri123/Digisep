/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Alumno;
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Persona;
import com.ginndex.titulos.modelo.TETituloElectronico;
import com.ginndex.titulos.modelo.TETitulosCarreras;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author BSorcia
 */
public class CTitulosAutenticados {

    HttpServletRequest request;
    HttpServletResponse response;
    Connection con;
    PreparedStatement pstmt;
    CallableStatement cstmt;
    ResultSet rs;
    CConexion conexion;
    String Bandera;
    String Id_Usuario;
    String rutaArchivo;
    String nombreArchivo;
    String NombreInstitucion;
    String respCarga;
    String respArchivoIncidencia;
    boolean vinculacionCorrecta = false;
    String nombreLogo;
    private String permisos;

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

    public String EstablecerAcciones() throws FileUploadException, Exception {
        //long startTime = System.nanoTime();
        String RESP = "";
        Bandera = request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera");
        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        HttpSession sessionOk = request.getSession();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        permisos = cPermisos.obtenerPermisos("Titulos Autenticados");
        llenarNombreInstitucion();
        if (Bandera.equalsIgnoreCase("0")) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            ServletFileUpload up = new ServletFileUpload(factory);
            List<FileItem> partes = up.parseRequest(request);
            getDat(partes);
        }
        switch (Bandera) {
            case "1":
                RESP = cargarListaCarreras();
                break;
            case "2":
                RESP = consultarTitulosPorCarrera();
                break;
            case "3":
                RESP = eliminarVinculoTitulo();
                break;
            case "zip":
                RESP = leerZipFile();
                break;
            case "4":
                RESP = llenarFormatoTituloMasivo();
                break;
            case "deleteFile":
                eliminarZipGenerado();
                break;
            case "pdf":
                RESP = leerPdfFile();
                break;
        }
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime);
        //System.out.println("TIEMPO:" + duration);
        return RESP;
    }

    public void getDat(List<FileItem> partes) throws Exception {
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
                } else if (input_actual.equalsIgnoreCase("txtBandera2")) {
                    Bandera = uploaded.getString();
                }
                if (Bandera.equalsIgnoreCase("zip")) {

                    if (input_actual.equalsIgnoreCase("fileZipAutenticado")) {

                        try {
                            rutaArchivo = ubicacionarchivo + "\\ZipAutenticado_" + uploaded.getName();
                            nombreArchivo = "ZipAutenticado_" + uploaded.getName();
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
                } else if (Bandera.equalsIgnoreCase("pdf")) {

                    if (input_actual.equalsIgnoreCase("fileZipConstancia")) {

                        try {
                            rutaArchivo = ubicacionarchivo + "\\" + uploaded.getName();
                            nombreArchivo = uploaded.getName();
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
    }

    private String cargarListaCarreras() throws SQLException {
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
                RESP.append("empty|");
                RESP.append("<option value='empty'>No se encontraron carreras</option>");
            }

        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de carreras: " + accion_catch(ex);
        } finally {
            pstmt.close();
            con.close();
            rs.close();
        }
        return RESP.toString();
    }

    private String consultarTitulosPorCarrera() {
        StringBuilder RESP = new StringBuilder();
        pstmt = null;
        con = null;
        rs = null;
        TETituloElectronico tte;
        Alumno al;
        Persona p;
        TETitulosCarreras ttc;
        String rutaServidor = "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\";
        try {
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            String idCarrera = request.getParameter("idCarrera");
            StringBuilder lastColumn = new StringBuilder();

            String sql = "SELECT Id_Titulo,folioControl, matricula, nombre, apaterno,amaterno,ttc.Id_Carrera_Excel, nombreCarrera,nombreXML, contenidoXML,nombreConstancia\n"
                    + "FROM TitulosAutenticados ta\n"
                    + "JOIN ALUMNOS a ON ta.idProfesionista = a.ID_Alumno\n"
                    + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                    + "JOIN Carrera c ON a.ID_Carrera = c.ID_Carrera\n"
                    + "JOIN TETitulosCarreras ttc ON c.Id_Carrera_Excel = ttc.Id_Carrera_Excel\n"
                    + (idCarrera.equalsIgnoreCase("todos") ? "" : "WHERE ttc.Id_Carrera_Excel = ?")
                    + " ORDER BY Id_Titulo";
            System.out.println(sql);
            pstmt = con.prepareStatement(sql);
            if (!idCarrera.equalsIgnoreCase("todos")) {
                pstmt.setString(1, idCarrera);
            }

            rs = pstmt.executeQuery();
            RESP.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblTitulosAutenticados'>"
                    + "    <thead class='bg-primary-dark' style='color: white;'>"
                    + "        <tr>"
                    + "            <th class='text-center' style='display:none;'>ID_Titulo</th>"
                    + "            <th class='text-center'>Folio</th>"
                    + "            <th class='text-center hidden-xs'>Matrícula</th>"
                    + "            <th class='text-center hidden-md hidden-sm hidden-xs'>Nombre(s)</th>"
                    + "            <th class='text-center hidden-xs'>A. Paterno</th>"
                    + "            <th class='text-center hidden-xs'>Carrera</th>"
                    + "            <th class='text-center'>Acciones</th>"
                    + "            <th class=' text-center bg-primary-dark' style='cursor: pointer; vertical-align:middle;'>"
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

            while (rs.next()) {
                tte = new TETituloElectronico();
                al = new Alumno();
                p = new Persona();
                ttc = new TETitulosCarreras();

                tte.setID_Titulo(rs.getString("Id_Titulo"));
                tte.setFolioControl(rs.getString("folioControl"));
                al.setMatricula(rs.getString("matricula"));
                p.setNombre(rs.getString("nombre"));
                p.setAPaterno(rs.getString("apaterno"));
                p.setAMaterno(rs.getString("amaterno") == null ? "" : rs.getString("amaterno"));
                ttc.setId_Carrera_Excel(rs.getString("Id_Carrera_Excel"));
                ttc.setNombreCarrera(rs.getString("nombreCarrera"));
                String nombreConstancia = (rs.getString("nombreConstancia") == null ? "nulo" : rs.getString("nombreConstancia"));

                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display:none;' id='IDTitulo_").append(tte.getID_Titulo()).append("'>").append(tte.getID_Titulo()).append("</td>");
                RESP.append("<td id='FolioControl_").append(tte.getID_Titulo()).append("'>").append(tte.getFolioControl()).append("</td>");
                RESP.append("<td class='hidden-xs' id='Matricula_").append(tte.getID_Titulo()).append("'>").append(al.getMatricula()).append("</td>");
                RESP.append("<td class='hidden-md hidden-sm hidden-xs' id='Nombre_").append(tte.getID_Titulo()).append("'>").append(p.getNombre()).append("</td>");
                RESP.append("<td class='hidden-xs' id='APaterno_").append(tte.getID_Titulo()).append("'>").append(p.getAPaterno()).append("</td>");
                RESP.append("<td class='hidden-xs' id='Carrera_").append(tte.getID_Titulo()).append("'>").append(ttc.getNombreCarrera()).append("</td>");
                RESP.append("<td class='text-center' id='Acciones_").append(tte.getID_Titulo()).append("'>");
                if (cabecera.contains("todos")) {
                    if (!nombreConstancia.equalsIgnoreCase("nulo")) {
                        RESP.append("<a class='btn btn-default btn-xs btnDescargarConstancia' data-container='body' type='button' data-toggle='tooltip' id='descargarConstancia_")
                                .append(tte.getID_Titulo()).append("'").append("' data-original-title='Descargar Constancia' download href='")
                                .append(rutaServidor).append(nombreConstancia)
                                .append("'><i class='fa fa-file-o'></i></a>");
                    }
                    RESP.append("<div class='btn-group'>");
                    RESP.append("<a class='btn btn-default btn-xs btnDescargarXML' data-container='body' type='button' data-toggle='tooltip' id='descargarXML_")
                            .append(tte.getID_Titulo()).append("'").append("' data-original-title='Descargar XML' download href='")
                            .append(rutaServidor).append(rs.getString("nombreXML"))
                            .append("'><i class='glyphicon glyphicon-download-alt'></i></a>");

                    String nombrePDF = tte.getFolioControl() + "_" + p.getAPaterno().trim() + p.getAMaterno().trim();
                    if (!existePDFGenerado(nombrePDF + ".pdf")) {
                        llenarFormatoTitulo(tte.getID_Titulo(), nombrePDF, rs.getString("contenidoXML"), rs.getString("nombreXML"));
                    }
                    RESP.append("<a class='btn btn-default btn-xs btnDescargarPDF' data-container='body' type='button' data-toggle='tooltip' id='descargarPDF_")
                            .append(tte.getID_Titulo()).append("' data-original-title='Descargar PDF' download href='").append(rutaServidor).append(nombrePDF).append(".pdf")
                            .append("'><i class='fa fa-file-pdf-o'></i></a>");
                    RESP.append("<button class='btn btn-default btn-xs btnEliminarVinculo' data-container='body' type='button' data-toggle='tooltip' id='eliminarTitulo_")
                            .append(tte.getID_Titulo()).append("' data-original-title='Eliminar Titulo' id-carrera='").append(ttc.getId_Carrera_Excel()).append("'><i class='fa fa-times'></i></button>");
                    RESP.append("</div>");

                    lastColumn.append("<div class='col-xs-12'>");
                    lastColumn.append("<label class='css-input css-checkbox css-checkbox-primary css-checkbos-sm remove-margin-t remove-margin-b'>");
                    lastColumn.append("<input class='cbxDesPdfM' type='checkbox' id='Tit_").append(tte.getID_Titulo()).append("'data-nombrepdf='").append(nombrePDF).append("' data-nombrexml='").append(rs.getString("nombreXML")).append("'><span></span> </label>");
                    lastColumn.append("</div></div>");

                } else if (cabecera.contains("acceso")) {
                    if (permisos.split("¬")[4].equalsIgnoreCase("1") || permisos.split("¬")[6].equalsIgnoreCase("1") || permisos.split("¬")[7].equalsIgnoreCase("1")) {
                        if (permisos.split("¬")[6].equalsIgnoreCase("1")) {
                            if (!nombreConstancia.equalsIgnoreCase("nulo")) {
                                RESP.append("<a class='btn btn-default btn-xs btnDescargarConstancia' data-container='body' type='button' data-toggle='tooltip' id='descargarConstancia_")
                                        .append(tte.getID_Titulo()).append("'").append("' data-original-title='Descargar Constancia' download href='")
                                        .append(rutaServidor).append(nombreConstancia)
                                        .append("'><i class='fa fa-file-o'></i></a>");
                            }
                            RESP.append("<a class='btn btn-default btn-xs btnDescargarXML' data-container='body' type='button' data-toggle='tooltip' id='descargarXML_")
                                    .append(tte.getID_Titulo()).append("'").append("' data-original-title='Descargar XML' download href='")
                                    .append(rutaServidor).append(rs.getString("nombreXML"))
                                    .append("'><i class='glyphicon glyphicon-download-alt'></i></a>");
                        }
                        if (permisos.split("¬")[7].equalsIgnoreCase("1")) {
                            String nombrePDF = tte.getFolioControl() + "_" + p.getAPaterno().trim() + p.getAMaterno().trim();
                            if (!existePDFGenerado(nombrePDF + ".pdf")) {
                                llenarFormatoTitulo(tte.getID_Titulo(), nombrePDF, rs.getString("contenidoXML"), rs.getString("nombreXML"));
                            }
                            RESP.append("<a class='btn btn-default btn-xs btnDescargarPDF' data-container='body' type='button' data-toggle='tooltip' id='descargarPDF_")
                                    .append(tte.getID_Titulo()).append("' data-original-title='Descargar PDF' download href='").append(rutaServidor).append(nombrePDF).append(".pdf")
                                    .append("'><i class='fa fa-file-pdf-o'></i></a>");

                            lastColumn.append("<div class='col-xs-12'>");
                            lastColumn.append("<label class='css-input css-checkbox css-checkbox-primary css-checkbos-sm remove-margin-t remove-margin-b'>");
                            lastColumn.append("<input class='cbxDesPdfM' type='checkbox' id='Tit_").append(tte.getID_Titulo()).append("'data-nombrepdf='").append(nombrePDF).append("' data-nombrexml='").append(rs.getString("nombreXML")).append("'><span></span> </label>");
                            lastColumn.append("</div></div>");
                        } else {
                            lastColumn.setLength(0);
                            lastColumn.append("<span class='label label-danger'>No disponible</span>");
                        }
                        if (permisos.split("¬")[4].equalsIgnoreCase("1")) {
                            RESP.append("<button class='btn btn-default btn-xs btnEliminarVinculo' data-container='body' type='button' data-toggle='tooltip' id='eliminarTitulo_")
                                    .append(tte.getID_Titulo()).append("' data-original-title='Eliminar Titulo' id-carrera='").append(ttc.getId_Carrera_Excel()).append("'><i class='fa fa-times'></i></button>");
                            RESP.append("</div>");
                        }
                    } else {
                        RESP.append("<span class='label label-danger'>No disponible</span>");
                        lastColumn.setLength(0);
                        lastColumn.append("<span class='label label-danger'>No disponible</span>");
                    }
                }
                RESP.append("</td>");

                RESP.append("<td class='text-center'>");
                RESP.append(lastColumn);
                RESP.append("</td>");

                RESP.append("</tr>");
                lastColumn.setLength(0);
            }

            RESP.append("</tbody>"
                    + "</table>");

        } catch (SQLException e) {

        }

        return RESP.toString();
    }

    private String eliminarVinculoTitulo() {
        String RESP = "";
        String idTitulo = request.getParameter("idTitulo");
         String folioControl = request.getParameter("folioControl");
        
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        cstmt = null;
        
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("TitulosAutenticados");
        bitacora.setMovimiento("eliminarVinculoTitulo");
        
        try {
//            String Query = "SET LANGUAGE 'español'; EXECUTE Delt_Titulo " + idTitulo;
            String Query = "{call Delt_TitulAutenticado (?)}";

            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idTitulo));
            cstmt.executeUpdate();

            if (cstmt.getResultSet() == null && cstmt.getUpdateCount() != -1) {
                RESP = "success";
                bitacora.setInformacion("Archivo: " + idTitulo + "||Folio Control: "+folioControl);
                    CBitacora cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral(); 
                                      
                                       
            } else {
                RESP = "error|Error al momento de eliminar el título electrónico, intente de nuevo. <br> Si el problema persiste contacte a soporte técnico";
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

    private String leerZipFile() {
        respCarga = "success";
        respArchivoIncidencia = "";
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + nombreArchivo;
        String rutaDescomprimir = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados";

        File dirDescomprimir = new File(rutaDescomprimir);

        if (!dirDescomprimir.exists()) {
            dirDescomprimir.mkdir();
        }
        try {
            if (FilenameUtils.getExtension(nombreArchivo).equalsIgnoreCase("zip")) {
                Charset CP866 = Charset.forName("CP437");
                ZipFile zipFile = new ZipFile(ruta, CP866);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String destPath = rutaDescomprimir + "\\" + entry.getName();

                    InputStream inputStream = zipFile.getInputStream(entry);
                    try (FileOutputStream outputStream = new FileOutputStream(destPath)) {
                        int data = inputStream.read();
                        while (data != -1) {
                            outputStream.write(data);
                            data = inputStream.read();
                        }
                        outputStream.flush();
                    }
                    leerXml(rutaDescomprimir, entry.getName());
                    if (!vinculacionCorrecta) {
                        //File xmlTmp = new File(destPath);
                        //xmlTmp.delete();
                    }
                }
            } else {
                FileUtils.copyFile(new File(ruta), new File(rutaDescomprimir + "\\" + nombreArchivo));
                leerXml(rutaDescomprimir, nombreArchivo);
            }
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException ex) {
            Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            return respCarga = "error";
        } finally {
            File tmp = new File(ruta);
            tmp.delete();
        }
        if (respArchivoIncidencia.equalsIgnoreCase("")) {
            return respCarga + "||noFile";
        } else {
            armarArchivoIncidencias();
            return respCarga + "||" + respArchivoIncidencia;
        }
    }

    private String leerPdfFile() {
        respCarga = "success";
        respArchivoIncidencia = "";
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + nombreArchivo;
        String rutaDescomprimir = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados";

        File dirDescomprimir = new File(rutaDescomprimir);

        if (!dirDescomprimir.exists()) {
            dirDescomprimir.mkdir();
        }
        try {
            if (FilenameUtils.getExtension(nombreArchivo).equalsIgnoreCase("zip")) {
                Charset CP866 = Charset.forName("CP437");
                ZipFile zipFile = new ZipFile(ruta, CP866);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String destPath = rutaDescomprimir + "\\" + entry.getName();

                    InputStream inputStream = zipFile.getInputStream(entry);
                    try (FileOutputStream outputStream = new FileOutputStream(destPath)) {
                        int data = inputStream.read();
                        while (data != -1) {
                            outputStream.write(data);
                            data = inputStream.read();
                        }
                        outputStream.flush();
                    }
                    //Termina de escribir el archivo.
                    vincularConstancia(rutaDescomprimir, entry.getName());
                    if (!vinculacionCorrecta) {
                        //File xmlTmp = new File(destPath);
                        //xmlTmp.delete();
                    }
                }
            } else {
                FileUtils.copyFile(new File(ruta), new File(rutaDescomprimir + "\\" + nombreArchivo));
                vincularConstancia(rutaDescomprimir, nombreArchivo);
                if (!vinculacionCorrecta) {
                    //File xmlTmp = new File(rutaDescomprimir + "\\" + nombreArchivo);
                    //xmlTmp.delete();
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            return respCarga = "error";
        } finally {
            File tmp = new File(ruta);
            tmp.delete();
        }
        if (respArchivoIncidencia.equalsIgnoreCase("")) {
            return respCarga + "||noFile";
        } else {
            armarArchivoIncidenciasPDF();
            return respCarga + "||" + respArchivoIncidencia;
        }
    }

    public String llenarFormatoTituloMasivo() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        String RESP = "";
        try {
            String valRequest = request.getParameter("cadenaIdTitulos");
            String regenerarPDF = request.getParameter("genPDF");
            String[] cadenaIdTitulos = (valRequest == null ? null : valRequest.split("¬"));
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy", new Locale("MX"));
            String fechaGeneracion = sdf.format(new Date());
            StringBuilder nombresPDF = new StringBuilder();
            //String rutaJasper = "C:\\Users\\Paola\\JaspersoftWorkspace\\Plantillas titulos y certificados\\Titulo_P1.jasper";
            String rutaJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\PlantillaAutenticada\\Titulo_P1.jasper";
            String rutaSubJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\PlantillaAutenticada\\";
            String rutaImagen = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\" + nombreLogo;
            String pathContext = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\";
            String save = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\";
            con = conexion.GetconexionInSite();
            for (String Id_Titulo : cadenaIdTitulos) {
                if (regenerarPDF.equalsIgnoreCase("true")) {
                    String xml2String = "";
                    //String nombrePDFyContenido = obtenerNombreTitulo(Id_Titulo.split("~")[0]);
                    //String contenidoXML = toPrettyString(nombrePDFyContenido.split("¬")[1], 3);
                    try {
                        BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                                new FileInputStream(
                                        System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\\\TitulosAutenticados\\" + Id_Titulo.split("~")[2]), "UTF8"));

                        StringBuilder sb = new StringBuilder();
                        String line = bufReader.readLine();
                        while (line != null) {
                            sb.append(line).append("\n");
                            line = bufReader.readLine();
                        }
                        xml2String = sb.toString();
                        bufReader.close();
                    } catch (IOException ex) {
                        System.out.println("1");
                    }

                    //con = conexion.GetconexionInSite();
                    JasperReport reporte = null;
                    Map parametrosReporte = new HashMap();
                    parametrosReporte.put("Id_Titulo", Id_Titulo.split("~")[0]);
                    parametrosReporte.put("xmlContent", xml2String);
                    parametrosReporte.put("rutaImagen", rutaImagen);
                    parametrosReporte.put("pathContext", pathContext);
                    parametrosReporte.put("SUBREPORT_DIR", rutaSubJasper);
                    reporte = (JasperReport) JRLoader.loadObjectFromFile(rutaJasper);
                    JasperPrint jprint = JasperFillManager.fillReport(reporte, parametrosReporte, con);
                    String nameFile = Id_Titulo.split("~")[1];
                    nombreArchivo = nameFile + ".pdf";
                    JasperExportManager.exportReportToPdfFile(jprint, save + nombreArchivo);
                    nombresPDF.append(nombreArchivo).append("¬");
                } else {
                    String nameFile = Id_Titulo.split("~")[1];
                    nombreArchivo = nameFile + ".pdf";
                    nombresPDF.append(nombreArchivo).append("¬");
                }
            }
            con.close();
            con = null;

            //save = "C:\\Users\\Paola\\Desktop\\";
            RESP = zip_File("TitulosFísicos_" + fechaGeneracion, nombresPDF.toString().substring(0, nombresPDF.toString().length() - 1), save);
        } catch (SQLException ex) {
            Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error SQL", ex);
        } catch (JRException ex) {
            Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error JRException", ex);
        }
        return "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\" + RESP;
    }

    private void leerXml(String rutaXML, String nameFile) throws ParserConfigurationException, SAXException,
            IOException, TransformerConfigurationException, TransformerException {
        vinculacionCorrecta = false;
        if (FilenameUtils.getExtension(nameFile).equalsIgnoreCase("xml")) {
            File xml = new File(rutaXML + "\\" + nameFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();
            String folioControl = ((Element) doc.getElementsByTagName("TituloElectronico").item(0)).getAttribute("folioControl");
            NodeList listaNodoAut = doc.getElementsByTagName("Autenticacion");

            if (listaNodoAut.getLength() != 0) {
                TransformerFactory tFact = TransformerFactory.newInstance();
                Transformer trans = tFact.newTransformer();

                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                DOMSource source = new DOMSource(doc);
                trans.transform(source, result);
                String xmlContent = writer.toString();
                System.out.println("longitud: " + listaNodoAut.getLength());
                for (int i = 0; i < listaNodoAut.getLength(); i++) {
                    Node nodoAuten = listaNodoAut.item(i);
                    if (nodoAuten.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) nodoAuten;
                        vincularXMLTitulo(folioControl, element, nameFile, xmlContent);
                    }
                }
            } else {
                respArchivoIncidencia += "noNodo¬" + nameFile + "°";
            }
        }
    }

    private String vincularConstancia(String rutaPrincipal, String nombreArchivo) {
        String RESP = "";
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("TitulosAutenticados");
        bitacora.setMovimiento("VincularConstancia");
        if (FilenameUtils.getExtension(nombreArchivo).equalsIgnoreCase("pdf")) {
            try {
                String nombreSinExtension = FilenameUtils.getBaseName(nombreArchivo);

                pstmt = null;
                con = null;
                rs = null;

                conexion = new CConexion();
                conexion.setRequest(request);
                con = conexion.GetconexionInSite();

                String sql = "UPDATE TitulosAutenticados SET nombreConstancia = ? WHERE folioControl = ?";

                pstmt = con.prepareStatement(sql);

                pstmt.setString(1, nombreArchivo);
                pstmt.setString(2, nombreSinExtension);

                if (pstmt.executeUpdate() != 1) {
                    respArchivoIncidencia += "noVinculado¬" + nombreSinExtension + ".pdf" + "°";
                    vinculacionCorrecta = false;
                } else {
                    vinculacionCorrecta = true;
                    
                    bitacora.setInformacion("Archivo: " + nombreArchivo + "||folioControl: "+nombreSinExtension);
                    CBitacora cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral(); 
                                       
                }

            } catch (SQLException ex) {
                Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
//            File xmlTmp = new File(rutaPrincipal + "\\" + nombreArchivo);
//            xmlTmp.delete();
        }
        return RESP;
    }

    private void vincularXMLTitulo(String folioControl, Element element, String nombreXML, String contenidoXML) {
        cstmt = null;
        con = null;
        rs = null;
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("TitulosAutenticados");
        bitacora.setMovimiento("VincularXML");
        try {
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            String sql = "{call Add_TituloAutenticado (?,?,?,?,?,?,?,?,?,?)}";

            cstmt = con.prepareCall(sql);
            /**
             * @folioControl varchar(40),
             * @versionNodo varchar(100),
             * @folioDigital varchar(800),
             * @fechaAutenticacion varchar(100),
             * @selloTitulo varchar(4000),
             * @noCertificadoAutoridad varchar(100),
             * @selloAutenticacion varchar(4000),
             * @nombreXML varchar(150),
             * @contenidoXML varchar(max),
             * @RESP varchar(100) OUT
             */

            cstmt.setString(1, folioControl);
            cstmt.setString(2, element.getAttribute("version"));
            cstmt.setString(3, element.getAttribute("folioDigital"));
            cstmt.setString(4, element.getAttribute("fechaAutenticacion"));
            cstmt.setString(5, element.getAttribute("selloTitulo"));
            cstmt.setString(6, element.getAttribute("noCertificadoAutoridad"));
            cstmt.setString(7, element.getAttribute("selloAutenticacion"));
            cstmt.setString(8, nombreXML);
            cstmt.setString(9, contenidoXML);
            cstmt.registerOutParameter(10, Types.VARCHAR);

            cstmt.execute();

            String respuesta = cstmt.getString(10);

            if (respuesta.equalsIgnoreCase("No Titulo")) {
                respArchivoIncidencia += "noTitulo¬" + nombreXML + "°";
            } else if (respuesta.contains("error")) {
                respArchivoIncidencia += "errorProcedure¬" + nombreXML + "°";
                Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, respuesta);
            } else {
                vinculacionCorrecta = true;
                bitacora.setInformacion("Registro de Titulo Electronico|| " + folioControl + "||Versión: " + element.getAttribute("version") + "||Folio Digital: " + element.getAttribute("folioDigital")
                                       + "||Fecha Autenticacion: " + element.getAttribute("fechaAutenticacion") +  "||Numero de Certificado Autoridad: " +  element.getAttribute("noCertificadoAutoridad"));
                CBitacora cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            }

        } catch (SQLException e) {
            Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, e);
            respArchivoIncidencia += "errorSQL¬" + nombreXML + "°";
        }
    }

    private void armarArchivoIncidencias() {
        String lineSeparator = System.getProperty("line.separator");
        String[] archivos = respArchivoIncidencia.split("°");
        String encabezadoNoNodo = "============================== DOCUMENTOS SIN NODO AUTENTICACIÓN ==============================" + lineSeparator;
        String encabezadoNoTitulo = "============================== DOCUMENTOS SIN TITULO A VINCULAR ===============================" + lineSeparator;
        String encabezadoError = "============================== DOCUMENTOS SIN PROCESAR POR ERROR ==============================" + lineSeparator;
        for (String valores : archivos) {
            String bandera = valores.split("¬")[0];
            String nombFile = valores.split("¬")[1];

            switch (bandera) {
                case "noNodo":
                    encabezadoNoNodo += "Documento: " + nombFile + lineSeparator;
                    break;
                case "noTitulo":
                    encabezadoNoTitulo += "Documento: " + nombFile + lineSeparator;
                    break;
                case "errorProcedure":
                    encabezadoError += "Documento: " + nombFile + lineSeparator;
                    break;
                case "errorSQL":
                    encabezadoError += "Documento: " + nombFile + lineSeparator;
                    break;
            }
        }

        BufferedWriter bw = null;
        FileWriter write = null;
        try {
            respArchivoIncidencia = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\" + nombreArchivo + "_Incidencias.txt";
            write = new FileWriter(respArchivoIncidencia);
            bw = new BufferedWriter(write);
            bw.write(encabezadoNoNodo + encabezadoNoTitulo + encabezadoError);
            bw.flush();
            bw.close();
            respArchivoIncidencia = "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\" + nombreArchivo + "_Incidencias.txt";
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (write != null) {
                    write.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private void armarArchivoIncidenciasPDF() {
        String lineSeparator = System.getProperty("line.separator");
        String[] archivos = respArchivoIncidencia.split("°");
        String noVinculado = "============================== CONSTANCIA SIN TITULO A VINCULAR ===============================" + lineSeparator;
        for (String valores : archivos) {
            String bandera = valores.split("¬")[0];
            String nombFile = valores.split("¬")[1];

            switch (bandera) {
                case "noVinculado":
                    noVinculado += "Documento: " + nombFile + lineSeparator;
                    break;
            }
        }

        BufferedWriter bw = null;
        FileWriter write = null;
        try {
            respArchivoIncidencia = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\" + nombreArchivo + "_IncidenciasConstancias.txt";
            write = new FileWriter(respArchivoIncidencia);
            bw = new BufferedWriter(write);
            bw.write(noVinculado);
            bw.flush();
            bw.close();
            respArchivoIncidencia = "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\" + nombreArchivo + "_IncidenciasConstancias.txt";
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (write != null) {
                    write.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private void llenarFormatoTitulo(String Id_Titulo, String nombreArchivo, String contenidoXML, String nombreXML) {
        try {
            String xml2String = "";
            try {
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(
                                System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\\\TitulosAutenticados\\" + nombreXML), "UTF8"));

                StringBuilder sb = new StringBuilder();
                String line = bufReader.readLine();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = bufReader.readLine();
                }
                xml2String = sb.toString();
                bufReader.close();
            } catch (IOException ex) {
                //Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error IOException", ex);
                System.out.println("1");
            }

            //contenidoXML = toPrettyString(contenidoXML, 3);
            //String rutaJasper = "C:\\Users\\Paola\\JaspersoftWorkspace\\Plantillas titulos y certificados\\Titulo_P1.jasper";
            String rutaJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\PlantillaAutenticada\\Titulo_P1.jasper";
            String rutaSubJasper = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\PlantillaAutenticada\\";
            String rutaImagen = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\" + nombreLogo;
            String pathContext = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\";
            JasperReport reporte = null;
            String save = "";
            Map parametrosReporte = new HashMap();
            parametrosReporte.put("Id_Titulo", Id_Titulo);
            parametrosReporte.put("xmlContent", xml2String);
            parametrosReporte.put("rutaImagen", rutaImagen);
            parametrosReporte.put("pathContext", pathContext);
            parametrosReporte.put("SUBREPORT_DIR", rutaSubJasper);
            //con = conexion.GetconexionInSite();
            reporte = (JasperReport) JRLoader.loadObjectFromFile(rutaJasper);
            JasperPrint jprint = JasperFillManager.fillReport(reporte, parametrosReporte, con);
            save = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados\\";
            //save = "C:\\Users\\Paola\\Desktop\\";
            nombreArchivo = nombreArchivo + ".pdf";
            JasperExportManager.exportReportToPdfFile(jprint, save + nombreArchivo);
            //} catch (SQLException ex) {
            //Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error SQL", ex);
        } catch (JRException ex) {
            Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, "Error al generar la plantilla, error JRException", ex);
        }
    }

    private boolean existePDFGenerado(String nombreArchivo) {
        String rutaServidor = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados";
        File pdf = new File(rutaServidor + "\\" + nombreArchivo);
        return pdf.exists();
    }

    private static String toPrettyString(String xml, int indent) {
        try {
            // Turn xml string into a document
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            document.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                    document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // Return pretty print xml string
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void llenarNombreInstitucion() {
        try {
            String Query = "SELECT nombreInstitucion,logo FROM Configuracion_Inicial AS CI "
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
            }
        } catch (SQLException ex) {
            Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CTitulosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String obtenerNombreTitulo(String Id_Titulo) {
        String RESP = "";
        try {
            String Query = "SELECT folioControl, apaterno,amaterno, contenidoXML,nombreXML FROM TitulosAutenticados ta "
                    + "JOIN alumnos a ON a.id_Alumno = ta.idProfesionista JOIN Persona p ON a.Id_Persona = p.Id_Persona WHERE Id_Titulo = ? ";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            pstmt.setString(1, Id_Titulo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                RESP += rs.getString(1) + "_" + rs.getString(2).trim() + (rs.getString(3) == null ? "" : rs.getString(3).trim()) + "¬" + rs.getString("contenidoXML") + "¬" + rs.getString("nombreXML");
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
            for (String filePath : arrayPaths) {
                ZipEntry e = new ZipEntry(filePath);
                try {
                    out.putNextEntry(e);
                    try (final FileInputStream pdfFile = new FileInputStream(new File(mainDirectory + filePath))) {
                        IOUtils.copy(pdfFile, out);  // this method belongs to apache IO Commons lib!
                    }
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

    private void eliminarZipGenerado() {
        String nombreZIP = request.getParameter("nombreZIP");
        String rutaDescomprimir = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\TitulosAutenticados";
        File tmp = new File(rutaDescomprimir + "\\" + nombreZIP);
        tmp.delete();
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
