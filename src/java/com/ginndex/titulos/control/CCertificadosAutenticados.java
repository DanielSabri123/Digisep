/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Alumno;
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Carrera;
import com.ginndex.titulos.modelo.Persona;
import com.ginndex.titulos.modelo.TECertificadoelectronico;
import com.ginndex.titulos.modelo.TETitulosCarreras;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author BSorcia
 */
public class CCertificadosAutenticados {

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

    public String establecerAccionesCertificadosAutenticados() throws SQLException, FileUploadException, Exception {
        String RESP = "";
        Bandera = request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera");
        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        HttpSession sessionOk = request.getSession();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        permisos = cPermisos.obtenerPermisos("Certificados Autenticados");
        llenarNombreInstitucion();
        if (Bandera.equalsIgnoreCase("0")) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
            ServletFileUpload up = new ServletFileUpload(factory);
//            ProgressListener progressListener = new ProgressListener() {
//                private long megaBytes = -1;
//
//                public void update(long pBytesRead, long pContentLength, int pItems) {
//                    long mBytes = pBytesRead / 1000000;
//                    if (megaBytes == mBytes) {
//                        return;
//                    }
//                    megaBytes = mBytes;
//                    System.out.println("We are currently reading item " + pItems);
//                    if (pContentLength == -1) {
//                        System.out.println("So far, " + pBytesRead + " bytes have been read.");
//                    } else {
//                        System.out.println("So far, " + pBytesRead + " of " + pContentLength
//                                + " bytes have been read.");
//                    }
//                }
//            };
//            up.setProgressListener(progressListener);
            List<FileItem> partes = up.parseRequest(request);
            getDat(partes);
        }
        switch (Bandera) {
            case "1":
                RESP = cargarListaCarreras();
                break;
            case "2":
                RESP = consultarCertificadosPorCarrera();
                break;
            case "3":
                RESP = eliminarVinculoCertificado();
                break;
            case "zip":
                RESP = leerZipFile();
                break;
            case "pdf":
                RESP = leerPdfFile();
                break;
        }
        return RESP;
    }

    public void getDat(List<FileItem> partes) throws Exception {

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
                            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.INFO, rutaArchivo);
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
                            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.INFO, rutaArchivo);
                        }
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, "OCURRIÓ UN ERROR AL RECUPERAR DESDE EL FORM::---------------->", ex);
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
            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de carreras: " + accion_catch(ex);
        } finally {
            pstmt.close();
            con.close();
            rs.close();
        }
        return RESP.toString();
    }

    private String consultarCertificadosPorCarrera() {
        StringBuilder RESP = new StringBuilder();
        pstmt = null;
        con = null;
        rs = null;
        TECertificadoelectronico tce;
        Alumno al;
        Persona p;
        Carrera c;
        String rutaServidor = "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\CertificadosAutenticados\\";
        try {
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            String idCarrera = request.getParameter("idCarrera");
            String lastColumn = "";

            String sql = "SELECT tce.Id_Certificado,c.Id_Carrera_Excel, folioControl,matricula,nombre, apaterno,amaterno, descripcion,nombreXML,nombreConstancia\n"
                    + "FROM TECertificadoElectronico tce\n"
                    + "JOIN Alumnos a ON tce.id_profesionista = a.ID_Alumno\n"
                    + "JOIN Persona p ON a.ID_Persona = p.Id_Persona\n"
                    + "JOIN Carrera c ON c.id_carrera = a.ID_Carrera\n"
                    + "JOIN TEselloDreoe tsd ON tce.ID_Certificado = tsd.id_certificado\n"
                    + "JOIN TESepIpes tsi ON tce.ID_Certificado = tsi.id_certificado\n"
                    + "JOIN CertificadosAutenticados ca ON tce.ID_Certificado = ca.id_certificado\n"
                    + "WHERE tce.estatus = 1 " + (idCarrera.equalsIgnoreCase("todos") ? "" : "AND c.Id_Carrera_Excel = ?");

            pstmt = con.prepareStatement(sql);
            if (!idCarrera.equalsIgnoreCase("todos")) {
                pstmt.setString(1, idCarrera);
            }

            rs = pstmt.executeQuery();
            RESP.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblCertificados'>"
                    + "    <thead class='bg-primary-dark' style='color: white;'>"
                    + "        <tr>"
                    + "            <th class='text-center' style='display:none;'>IdCertificado</th>"
                    + "            <th class='text-center hidden-sm hidden-xs'>Folio</th>"
                    + "            <th class='text-center'>Matrícula</th>"
                    + "            <th class='text-center hidden-xs'>Nombre(s)</th>"
                    + "            <th class='text-center hidden-xs'>A. Paterno</th>"
                    + "            <th class='text-center hidden-xs'>Carrera</th>"
                    + "            <th class='text-center'>Acciones</th>"
                    + "        </tr>"
                    + "    </thead>"
                    + "    <tbody>");

            String cabecera = permisos.split("°")[0];
            if (!cabecera.equalsIgnoreCase("todos")) {
                permisos = permisos.split("°")[1];
            }

            while (rs.next()) {
                tce = new TECertificadoelectronico();
                al = new Alumno();
                p = new Persona();
                c = new Carrera();

                tce.setID_Certificado(rs.getString("Id_Certificado"));
                tce.setFolioControl(rs.getString("folioControl"));

                al.setMatricula(rs.getString("matricula"));
                p.setNombre(rs.getString("nombre"));
                p.setAPaterno(rs.getString("apaterno"));
                p.setAMaterno(rs.getString("amaterno") == null ? "" : rs.getString("amaterno"));

                c.setId_Carrera_Excel(rs.getString("Id_Carrera_Excel"));
                c.setDescripcion(rs.getString("descripcion"));

                String nombreConstancia = (rs.getString("nombreConstancia") == null ? "nulo" : rs.getString("nombreConstancia"));

                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display:none;' id='idCertificado_").append(tce.getID_Certificado()).append("'>").append(tce.getID_Certificado()).append("</td>");
                RESP.append("<td class='hidden-sm hidden-xs' id='FolioControl_").append(tce.getID_Certificado()).append("'>").append(tce.getFolioControl()).append("</td>");
                RESP.append("<td id='Matricula_").append(tce.getID_Certificado()).append("'>").append(al.getMatricula()).append("</td>");
                RESP.append("<td class='hidden-xs' id='Nombre_").append(tce.getID_Certificado()).append("'>").append(p.getNombre()).append("</td>");
                RESP.append("<td class='hidden-xs' id='APaterno_").append(tce.getID_Certificado()).append("'>").append(p.getAPaterno()).append("</td>");
                RESP.append("<td class='hidden-xs' id='Carrera_").append(tce.getID_Certificado()).append("'>").append(c.getDescripcion()).append("</td>");
                RESP.append("<td class='text-center' id='Acciones_").append(tce.getID_Certificado()).append("'>");
                if (cabecera.contains("todos")) {
                    if (!nombreConstancia.equalsIgnoreCase("nulo")) {
                        RESP.append("<a class='btn btn-default btn-xs btnDescargarConstancia' data-container='body' type='button' data-toggle='tooltip' id='descargarConstancia_")
                                .append(tce.getID_Certificado()).append("'").append("' data-original-title='Descargar Constancia' download href='")
                                .append(rutaServidor).append(nombreConstancia)
                                .append("'><i class='fa fa-file-o'></i></a>");
                    }
                    RESP.append("<div class='btn-group'>");
                    RESP.append("<a class='btn btn-default btn-xs btnDescargarXML' data-container='body' type='button' data-toggle='tooltip' id='descargarXML_")
                            .append(nombreConstancia).append("'").append("' data-original-title='Descargar XML' download href='")
                            .append(rutaServidor).append(rs.getString("nombreXML"))
                            .append("'><i class='glyphicon glyphicon-download-alt'></i></a>");

                    RESP.append("<button class='btn btn-default btn-xs btnEliminarVinculo' data-container='body' type='button' data-toggle='tooltip' id='eliminarCer_")
                            .append(tce.getID_Certificado()).append("' data-original-title='Eliminar Certificado' id-carrera='").append(c.getId_Carrera_Excel()).append("'><i class='fa fa-times'></i></button>");
                    RESP.append("</div>");

                } else if (cabecera.contains("acceso")) {
                    System.out.println(permisos);
                    if (permisos.split("¬")[4].equalsIgnoreCase("1") || permisos.split("¬")[6].equalsIgnoreCase("1") || (permisos.split("¬")[7].equalsIgnoreCase("1") && !nombreConstancia.equalsIgnoreCase("nulo"))) {
                        //Descargar XML
                        if (permisos.split("¬")[6].equalsIgnoreCase("1")) {
                            RESP.append("<a class='btn btn-default btn-xs btnDescargarXML' data-container='body' type='button' data-toggle='tooltip' id='descargarXML_")
                                    .append(tce.getID_Certificado()).append("'").append("' data-original-title='Descargar XML' download href='")
                                    .append(rutaServidor).append(rs.getString("nombreXML"))
                                    .append("'><i class='glyphicon glyphicon-download-alt'></i></a>");
                        }
                        if (permisos.split("¬")[7].equalsIgnoreCase("1") && !nombreConstancia.equalsIgnoreCase("nulo")) {
                            RESP.append("<a class='btn btn-default btn-xs btnDescargarConstancia' data-container='body' type='button' data-toggle='tooltip' id='descargarConstancia_")
                                    .append(tce.getID_Certificado()).append("'").append("' data-original-title='Descargar Constancia' download href='")
                                    .append(rutaServidor).append(nombreConstancia)
                                    .append("'><i class='fa fa-file-o'></i></a>");
                        }
                        //ELIMINAR VINCULO
                        if (permisos.split("¬")[4].equalsIgnoreCase("1")) {
                            RESP.append("<button class='btn btn-default btn-xs btnEliminarVinculo' data-container='body' type='button' data-toggle='tooltip' id='eliminarTitulo_")
                                    .append(tce.getID_Certificado()).append("' data-original-title='Eliminar Certificado' id-carrera='").append(c.getId_Carrera_Excel()).append("'><i class='fa fa-times'></i></button>");
                            RESP.append("</div>");
                        }
                    } else {
                        RESP.append("<span class='label label-danger'>No disponible</span>");
                    }
                }
                RESP.append("</td>");
                RESP.append("</tr>");
            }

            RESP.append("</tbody>"
                    + "</table>");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return RESP.toString();
    }

    private String eliminarVinculoCertificado() {
        String RESP = "";
        String idCerificado = request.getParameter("idCerificado");
         String folioControl = request.getParameter("folioControl");
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        cstmt = null;
        
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("CertificadosAutenticados");
        bitacora.setMovimiento("eliminarVinculoCertificado");
        
        try {
//            String Query = "SET LANGUAGE 'español'; EXECUTE Delt_Titulo " + idTitulo;
            String Query = "{call Delt_CertificadoAutenticado (?,?)}";

            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(idCerificado));
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.execute();

            RESP = cstmt.getString(2);

            if (RESP == null) {
                RESP = "success";
                bitacora.setInformacion("Archivo: " + idCerificado + "||Folio Control: "+folioControl);
                    CBitacora cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral(); 
            } else {
                RESP = "error|Error al momento de eliminar el certificado electrónico, intente de nuevo. <br> Si el problema persiste contacte a soporte técnico";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la eliminación del certificado electrónico: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException al realizar la eliminación del certificado electrónico: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la eliminación del certificado electrónico: " + accion_catch(ex);
        }
        return RESP;
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
            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conexion.GetconexionInSite().close();
            } catch (SQLException ex) {
                Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String leerZipFile() {
        respCarga = "success";
        respArchivoIncidencia = "";
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + nombreArchivo;
        String rutaDescomprimir = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\CertificadosAutenticados";

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
//                    if (!vinculacionCorrecta) {
//                        File xmlTmp = new File(destPath);
//                        xmlTmp.delete();
//                    }
                }
            } else {
                FileUtils.copyFile(new File(ruta), new File(rutaDescomprimir + "\\" + nombreArchivo));
                leerXml(rutaDescomprimir, nombreArchivo);
            }
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException ex) {
            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
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

    private void leerXml(String rutaXML, String nameFile) throws ParserConfigurationException, SAXException,
            IOException, TransformerConfigurationException, TransformerException {
        vinculacionCorrecta = false;
        if (FilenameUtils.getExtension(nameFile).equalsIgnoreCase("xml")) {
            File xml = new File(rutaXML + "\\" + nameFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();
            String folioControl = ((Element) doc.getElementsByTagName("Dec").item(0)).getAttribute("folioControl");
            NodeList listaNodoDreoe = doc.getElementsByTagName("Dreoe");
            NodeList listaNodoSepIpes = doc.getElementsByTagName("SepIpes");

            if (listaNodoDreoe.getLength() != 0 && listaNodoSepIpes.getLength() != 0) {

                Node nodoDreoe = listaNodoDreoe.item(0);
                Node nodoSepIpes = listaNodoSepIpes.item(0);

                if (nodoDreoe.getNodeType() == Node.ELEMENT_NODE && nodoSepIpes.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementDreoe = (Element) nodoDreoe;
                    Element elementSepIpes = (Element) nodoSepIpes;
                    vincularXMLCertificado(folioControl, elementDreoe, elementSepIpes, nameFile);
                }

            } else {
                respArchivoIncidencia += "noNodo¬" + nameFile + "°";
            }
        }
    }

    private void vincularXMLCertificado(String folioControl, Element elementDreoe, Element elementSepIpes, String nombreXML) {
        cstmt = null;
        con = null;
        rs = null;
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("CertificadosAutenticados");
        bitacora.setMovimiento("VincularXML");
        try {
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            String sql = "{call Add_CertificadoAutenticado (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

            cstmt = con.prepareCall(sql);
            /**
             * --Dreoe
             *
             * @folioControl varchar(40),
             * @fechaDreoe varchar(30),
             * @selloDec varchar(1000),
             * @noCertificadoDreoe varchar(1000),
             * @curp varchar(1000),
             * @nombreCompleto varchar(1000),
             * @idCargo varchar(1000),
             * @selloDreoe varchar(1000),
             *
             * --SepIpes
             * @version varchar(3),
             * @fechaSepIpes varchar(30),
             * @selloDreoeSepIpes varchar(1000),
             * @noCertificadoSepIpes varchar(1000),
             * @selloSepIpes varchar(1000),
             * @folioDigital varchar(100),
             *
             * --CertificadoAutenticado
             * @nombreXML varchar(100),
             * @RESP varchar(100) OUT
             */

            cstmt.setString(1, folioControl);
            cstmt.setString(2, elementDreoe.getAttribute("fechaDreoe"));
            cstmt.setString(3, elementDreoe.getAttribute("selloDec"));
            cstmt.setString(4, elementDreoe.getAttribute("noCertificadoDreoe"));
            cstmt.setString(5, elementDreoe.getAttribute("curp"));
            cstmt.setString(6, elementDreoe.getAttribute("nombreCompleto"));
            cstmt.setString(7, elementDreoe.getAttribute("idCargo"));
            cstmt.setString(8, elementDreoe.getAttribute("selloDreoe"));
            cstmt.setString(9, elementSepIpes.getAttribute("version"));
            cstmt.setString(10, elementSepIpes.getAttribute("fechaSepIpes"));
            cstmt.setString(11, elementSepIpes.getAttribute("selloDreoe"));
            cstmt.setString(12, elementSepIpes.getAttribute("noCertificadoSepIpes"));
            cstmt.setString(13, elementSepIpes.getAttribute("selloSepIpes"));
            cstmt.setString(14, elementSepIpes.getAttribute("folioDigital"));
            cstmt.setString(15, nombreXML);
            cstmt.registerOutParameter(16, Types.VARCHAR);

            cstmt.execute();

            String respuesta = cstmt.getString(16);

            if (respuesta.equalsIgnoreCase("NO CERTTIFICADO")) {
                respArchivoIncidencia += "noCertificado¬" + nombreXML + "°";
            } else if (respuesta.contains("Error")) {
                respArchivoIncidencia += "errorProcedure¬" + nombreXML + "°";
                Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, respuesta);
            } else {
                vinculacionCorrecta = true;
                 bitacora.setInformacion("Registro de Titulo Electronico|| " + folioControl + "||Fecha Dreoe: " + elementDreoe.getAttribute("fechaDreoe") + "||Numero de Certificado Dreoe: " + elementDreoe.getAttribute("noCertificadoDreoe")
                                        + "||Curp: " + elementDreoe.getAttribute("curp") +  "||Nombre: " +  elementDreoe.getAttribute("nombreCompleto") +  "||ID Cargo: " + elementDreoe.getAttribute("idCargo")
                                        + "||Version: " + elementSepIpes.getAttribute("version") + "||Fecha SepIpes: " + elementSepIpes.getAttribute("fechaSepIpes") 
                                        + "||Numero de Certificado SepIpes: " + elementSepIpes.getAttribute("noCertificadoSepIpes") + "||Folio Digital" +  elementSepIpes.getAttribute("folioDigital"));
                CBitacora cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            }

        } catch (SQLException e) {
            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, e);
            respArchivoIncidencia += "errorSQL¬" + nombreXML + "°";
        }
    }

    private String leerPdfFile() {
        respCarga = "success";
        respArchivoIncidencia = "";
        String ruta = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\cargaArchivos\\" + nombreArchivo;
        String rutaDescomprimir = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\CertificadosAutenticados";

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
//                    if (!vinculacionCorrecta) {
//                        File xmlTmp = new File(destPath);
//                        xmlTmp.delete();
//                    }
                }
            } else {
                FileUtils.copyFile(new File(ruta), new File(rutaDescomprimir + "\\" + nombreArchivo));
                vincularConstancia(rutaDescomprimir, nombreArchivo);
//                if (!vinculacionCorrecta) {
//                    File xmlTmp = new File(rutaDescomprimir + "\\" + nombreArchivo);
//                    xmlTmp.delete();
//                }

            }
        } catch (IOException ex) {
            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
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

    private String vincularConstancia(String rutaPrincipal, String nombreArchivo) {
        String RESP = "";
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("CertificadosAutenticados");
        bitacora.setMovimiento("VincularConstancia");
        if (FilenameUtils.getExtension(nombreArchivo).equalsIgnoreCase("pdf")) {
            try {
                String nombreSinExtension = FilenameUtils.getBaseName(nombreArchivo).split("_")[0];

                cstmt = null;
                con = null;
                rs = null;

                conexion = new CConexion();
                conexion.setRequest(request);
                con = conexion.GetconexionInSite();

                String sql = "call Upd_CertificadoAutenticado (?,?,?)";

                cstmt = con.prepareCall(sql);

                cstmt.setString(1, nombreArchivo);
                cstmt.setString(2, nombreSinExtension);
                cstmt.registerOutParameter(3, Types.VARCHAR);

                cstmt.execute();

                RESP = cstmt.getString(3);

                if (RESP.contains("NO CERTIFICADO") || RESP.contains("Error")) {
                    respArchivoIncidencia += "noVinculado¬" + nombreArchivo + "°";
                    vinculacionCorrecta = false;
                } else {
                    vinculacionCorrecta = true;
                    bitacora.setInformacion("Archivo: " + nombreArchivo + "||folioControl: "+nombreSinExtension);
                    CBitacora cBitacora = new CBitacora(bitacora);
                                        cBitacora.setRequest(request);
                                        cBitacora.addBitacoraGeneral(); 
                                       
                }

            } catch (SQLException ex) {
                Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
//            File xmlTmp = new File(rutaPrincipal + "\\" + nombreArchivo);
//            xmlTmp.delete();
        }
        return RESP;
    }

    private void armarArchivoIncidencias() {
        String lineSeparator = System.getProperty("line.separator");
        String[] archivos = respArchivoIncidencia.split("°");
        String encabezadoNoNodo = "============================== DOCUMENTOS SIN NODO DREOE O NODO SEPIPES ==============================" + lineSeparator;
        String encabezadoNoTitulo = "============================== DOCUMENTOS SIN CERTIFICADO A VINCULAR ===============================" + lineSeparator;
        String encabezadoError = "============================== DOCUMENTOS SIN PROCESAR POR ERROR ==============================" + lineSeparator;
        for (String valores : archivos) {
            String bandera = valores.split("¬")[0];
            String nombFile = valores.split("¬")[1];

            switch (bandera) {
                case "noNodo":
                    encabezadoNoNodo += "Documento: " + nombFile + lineSeparator;
                    break;
                case "noCertificado":
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
            respArchivoIncidencia = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\CertificadosAutenticados\\" + nombreArchivo + "_Incidencias.txt";
            write = new FileWriter(respArchivoIncidencia);
            bw = new BufferedWriter(write);
            bw.write(encabezadoNoNodo + encabezadoNoTitulo + encabezadoError);
            bw.flush();
            bw.close();
            respArchivoIncidencia = "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\CertificadosAutenticados\\" + nombreArchivo + "_Incidencias.txt";
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
        String noVinculado = "============================== CONSTANCIA SIN CERTIFICADO A VINCULAR ===============================" + lineSeparator;
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
            respArchivoIncidencia = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\CertificadosAutenticados\\" + nombreArchivo + "_IncidenciasConstancias.txt";
            write = new FileWriter(respArchivoIncidencia);
            bw = new BufferedWriter(write);
            bw.write(noVinculado);
            bw.flush();
            bw.close();
            respArchivoIncidencia = "..\\..\\..\\Instituciones\\" + NombreInstitucion.trim() + "\\XML\\CertificadosAutenticados\\" + nombreArchivo + "_IncidenciasConstancias.txt";
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

    private String accion_catch(Exception ex) {
        String resp = "";
        try {
            resp = ex.toString().replace("'", "\"");
            Logger.getLogger(CCertificadosAutenticados.class.getName()).log(Level.SEVERE, null, ex);
            resp = "<h4 style='color:#e76d6d'>" + resp;
            resp += "</h4>";
        } catch (Exception e) {
            resp += "</h4><small class='text-primary'>No se ha insertado en Bitacora de errores</small>";
        }
        resp += "<br><br><small>Si continua con el problema, comuníquese con soporte técnico.</small> ";
        return resp;
    }
}
