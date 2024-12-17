/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.ConfiguracionInicial;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.ssl.Base64;

/**
 *
 * @author Paola Alonso
 */
public class CConfiguracionInicial {

    private String Bandera;
    private String NombreArchivo;
    private String rutaArchivo;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String NombreInstitucion;
    private ConfiguracionInicial ci;
    private CClavesActivacion desencriptador;

    private ResultSet rs;
    private CConexion conexion;
    private Connection con;
    private PreparedStatement pstmt;
    private CallableStatement cstmt;

    Bitacora bitacora;
    CBitacora cBitacora;

    private String ID_Usuario;

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

    public CConexion getConexion() {
        return conexion;
    }

    public void setConexion(CConexion conexion) {
        this.conexion = conexion;
    }

    public CConfiguracionInicial() {
        con = null;
        cstmt = null;
        pstmt = null;
        rs = null;
        conexion = null;
    }

    public String EstablecerAcciones() throws UnsupportedEncodingException {
        String RESP = "";
        Bandera = (request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
        HttpSession sessionOk = request.getSession();
        HttpServletRequest requestProvisional = request;
        ID_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        requestProvisional.setCharacterEncoding("UTF-8");

        try {

            if (Bandera.equalsIgnoreCase("0")) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1024);
                ServletFileUpload up = new ServletFileUpload(factory);
                up.setHeaderEncoding("UTF-8");
                boolean isMultipart = ServletFileUpload.isMultipartContent(requestProvisional);
                if (isMultipart) {
                    List<FileItem> partes = up.parseRequest(requestProvisional);
                    Bandera = getDat(partes);
                }
            }

            if (Bandera.equalsIgnoreCase("1")) {
                RESP = cargarConfiguracion();
            } else if (Bandera.equalsIgnoreCase("2")) {
                RESP = addConfiguracion();
            } else if (Bandera.equalsIgnoreCase("3")) {
                RESP = updConfiguracion();
            } else if (Bandera.equalsIgnoreCase("4")) {
                RESP = add_ClaveAutorizacion();
            }
        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        }

        return RESP;
    }

    public String getDat(List<FileItem> partes) {
        ci = new ConfiguracionInicial();
        String input_actual = "";
        String Bandera = "";
        for (FileItem item : partes) {
            try {

                FileItem uploaded = null;
                uploaded = (FileItem) item;
                input_actual = uploaded.getFieldName();
                if (input_actual.equalsIgnoreCase("txtBandera")) {

                    Bandera = uploaded.getString();
                }
                if (Bandera.equalsIgnoreCase("2") || Bandera.equalsIgnoreCase("3")) {

                    if (Bandera.equalsIgnoreCase("2")) {// Insercion
                        if (input_actual.equalsIgnoreCase("txtNombreInstitucion")) {
                            NombreInstitucion = uploaded.getString("UTF-8");
                            ci.setNombreInstitucion(uploaded.getString("UTF-8"));
                            File carpetaInstitucional = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim());
                            carpetaInstitucional.mkdirs();
                            File carpetaArchivosInstitucionales = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim() + "\\ArchivosInstitucionales");
                            carpetaArchivosInstitucionales.mkdirs();
                        }
                    } else if (Bandera.equalsIgnoreCase("3")) {// Modificacion

                        if (input_actual.equalsIgnoreCase("txtIdConfiguracion")) {

                            ci.setID_ConfiguracionInicial(uploaded.getString("UTF-8"));
                        }
                        if (input_actual.equalsIgnoreCase("txtNombreInstitucion2")) {
                            NombreInstitucion = uploaded.getString("UTF-8");
                            ci.setNombreInstitucion(uploaded.getString("UTF-8"));
                        }
                    }

                    if (input_actual.equalsIgnoreCase("txtFileLogo")) {
                        //String ubicacionarchivo = System.getProperty("user.home") + "\\Downloads\\Configuraciones";
                        Logger.getLogger(CConfiguracionInicial.class.getName()).log(Level.INFO, "RUTA" + System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim() + "\\ArchivosInstitucionales");
                        String ubicacionarchivo = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim() + "\\ArchivosInstitucionales";

                        try {
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            rutaArchivo = ubicacionarchivo + "\\LogoInst_" + uploaded.getName();
                            NombreArchivo = "LogoInst_" + uploaded.getName();
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                NombreArchivo = "";
                                ci.setLogo(null);
                            } else {
                                File archivoServer = new File(rutaArchivo);
                                uploaded.write(archivoServer);
                                ci.setLogo(NombreArchivo);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (input_actual.equalsIgnoreCase("txtFileFirma")) {
                        //String ubicacionarchivo = System.getProperty("user.home") + "\\Downloads\\Configuraciones";
                        Logger.getLogger(CConfiguracionInicial.class.getName()).log(Level.INFO, "RUTA" + System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim() + "\\ArchivosInstitucionales");
                        String ubicacionarchivo = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim() + "\\ArchivosInstitucionales";

                        try {
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            rutaArchivo = ubicacionarchivo + "\\LogoInst_" + uploaded.getName();
                            NombreArchivo = "LogoInst_" + uploaded.getName();
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                NombreArchivo = "";
                                ci.setFirmaImagen(null);
                            } else {
                                File archivoServer = new File(rutaArchivo);
                                uploaded.write(archivoServer);
                                ci.setFirmaImagen(NombreArchivo);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (input_actual.equalsIgnoreCase("txtNombreOficial")) {

                        ci.setNombreOficial(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtPrefijoTitulo")) {

                        ci.setPrefijoFolioTitulo(uploaded.getString("UTF-8"));

                    } else if (input_actual.equalsIgnoreCase("txtLongitud")) {

                        ci.setLongitudFolioTItulo(Integer.valueOf(uploaded.getString("UTF-8")));
                    } else if (input_actual.equalsIgnoreCase("txtPrefijoCertificado")) {

                        ci.setPrefijoFolioCertificado(uploaded.getString("UTF-8"));

                    } else if (input_actual.equalsIgnoreCase("txtLongitudCertificados")) {

                        ci.setLongitudFolioCertificado(Integer.valueOf(uploaded.getString("UTF-8")));
                    } else if (input_actual.equalsIgnoreCase("txtUsuarioSEP")) {

                        ci.setUsuarioSEP(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtPasswordSEP")) {

                        ci.setPasswordSEP(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtUsuarioSepProd")) {

                        ci.setUsuarioSepProductivo(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtPasswordSepProd")) {

                        ci.setPasswordSepProductivo(uploaded.getString("UTF-8"));
                    }
                }

            } catch (Exception ex) {
                System.out.println("OCURRIÓ UN ERROR AL RECUPERAR DESDE EL FORM: " + ex);
                Logger.getLogger(CConfiguracionInicial.class.getName()).log(Level.SEVERE, "Ocurrio un error al recuperar el formulario con la variable input_actual: " + input_actual, ex);
            }
        }
        return Bandera;
    }

    private String cargarConfiguracion() throws SQLException {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);

       //Con esta libreria le damos formato a la fecha de vigencia
       SimpleDateFormat formatoFecha= new SimpleDateFormat("dd-MM-yyyy");

        try {
            String Query = "SELECT * FROM Configuracion_Inicial AS CI "
                    + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                    + "JOIN Vigencia_polizas AS VP ON  VP.activo=1"
                    + " WHERE U.Id_Usuario = " + ID_Usuario;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ci = new ConfiguracionInicial();

                ci.setID_ConfiguracionInicial(rs.getString("ID_ConfiguracionInicial"));
                ci.setNombreInstitucion(rs.getString("nombreInstitucion"));
                ci.setPrefijoFolioTitulo(rs.getString("prefijoTitulo"));
                ci.setLongitudFolioTItulo(Integer.valueOf(rs.getString("longitudFolioTitulo")));
                ci.setPrefijoFolioCertificado(rs.getString("prefijoCertificado"));
                ci.setLongitudFolioCertificado(Integer.valueOf(rs.getString("longitudFolioCertificado")));
                ci.setClaveInstitucional(rs.getString("claveInstitucional"));
                ci.setNombreOficial(rs.getString("nombreOficial"));
                ci.setLogo(rs.getString("logo"));
                ci.setUsuarioSEP((rs.getString("usuarioSEP") == null ? "" : rs.getString("usuarioSEP")));
                ci.setPasswordSEP((rs.getString("passwordSEP") == null ? "" : rs.getString("passwordSEP")));
                ci.setUsuarioSepProductivo((rs.getString("usuarioSepProductivo") == null ? "" : rs.getString("usuarioSepProductivo")));
                ci.setPasswordSepProductivo((rs.getString("passwordSepProductivo") == null ? "" : rs.getString("passwordSepProductivo")));
                ci.setFechaInicioVigencia(rs.getDate("fechaInicio"));
                ci.setFechaFinVigencia(rs.getDate("fechaFin"));
            }
            if (ci != null) {
                RESP += ci.getID_ConfiguracionInicial() + "~";
                RESP += ci.getNombreInstitucion() + "~";
                RESP += ci.getPrefijoFolioTitulo() + "~";
                RESP += ci.getLongitudFolioTitulo() + "~";
                RESP += ci.getPrefijoFolioCertificado() + "~";
                RESP += ci.getLongitudFolioCertificado() + "~";
                RESP += ci.getNombreOficial() + "~";
                RESP += ci.getClaveInstitucional() + "~";
                RESP += ci.getLogo() + "~";
                RESP += ci.getUsuarioSEP() + "~";
                RESP += ci.getPasswordSEP() + "~";
                RESP += ci.getUsuarioSepProductivo() + "~";
                RESP += ci.getPasswordSepProductivo() + "~";
                RESP += formatoFecha.format(ci.getFechaInicioVigencia()) + "~";
                RESP += formatoFecha.format(ci.getFechaFinVigencia()) + "~";
            } else {
                RESP = "sinConfiguracion";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al momento de cargar la configuración inicial: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al momento de cargar la configuración inicial: " + accion_catch(ex);
        } finally {
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
        }
        return RESP;
    }

    private String addConfiguracion() throws SQLException {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        String nombreInstitucion = ci.getNombreInstitucion();
        bitacora = new Bitacora();
        bitacora.setId_Usuario(ID_Usuario);
        bitacora.setModulo("Configuración Inicial");
        bitacora.setMovimiento("Inserción");

        try {
            String Query = "{call Add_ConfiguracionInicial (?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setString(1, ci.getNombreInstitucion());
            cstmt.setString(2, ci.getPrefijoFolioTitulo());
            cstmt.setInt(3, ci.getLongitudFolioTitulo());
            cstmt.setString(4, ci.getPrefijoFolioCertificado());
            cstmt.setInt(5, ci.getLongitudFolioCertificado());
            cstmt.setInt(6, Integer.valueOf(ID_Usuario));
            cstmt.setString(7, ci.getNombreOficial());
            cstmt.setString(8, ci.getLogo());
            cstmt.setString(9, ci.getUsuarioSEP());
            cstmt.setString(10, ci.getPasswordSEP());
            cstmt.setString(11, ci.getUsuarioSepProductivo());
            cstmt.setString(12, ci.getPasswordSepProductivo());
            cstmt.setString(13, ci.getFirmaImagen());
            cstmt.registerOutParameter(14, java.sql.Types.VARCHAR);
            cstmt.execute();

            RESP = cstmt.getString(14);

            if (RESP.contains("success")) {
                File carpetaCargarArchivos = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\CargaArchivos");
                carpetaCargarArchivos.mkdirs();
                File carpetaCER = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\CER");
                carpetaCER.mkdirs();
                File carpetaKEY = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\KEY");
                carpetaKEY.mkdirs();
                File carpetaPEM = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\PEM");
                carpetaPEM.mkdirs();
                File carpetaXML = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML");
                carpetaXML.mkdirs();
                File carpetaXMLCertificados = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\Certificados");
                carpetaXMLCertificados.mkdirs();
                File carpetaXMLTitulos = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\Titulos");
                carpetaXMLTitulos.mkdirs();
                File carpetaXMLPlantillas = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\Plantillas");
                carpetaXMLPlantillas.mkdirs();
                File carpetaXMLPlantillasAutenticadas = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\PlantillaAutenticada");
                carpetaXMLPlantillasAutenticadas.mkdir();
                File carpetaXMLTitulosAutenticados = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\TitulosAutenticados");
                carpetaXMLTitulosAutenticados.mkdir();
                File carpetaXMLCertificadosAutenticados = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\CertificadosAutenticados");
                carpetaXMLCertificadosAutenticados.mkdir();

                File plantillaJasperAut = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\PAutenticada\\Titulo_P1.jasper");
                FileUtils.copyFileToDirectory(plantillaJasperAut, carpetaXMLPlantillasAutenticadas);

                File plantillaJasper = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\Titulo_P1.jasper");
                FileUtils.copyFileToDirectory(plantillaJasper, carpetaXMLPlantillas);
                plantillaJasper = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\subReporte.jasper");
                FileUtils.copyFileToDirectory(plantillaJasper, carpetaXMLPlantillas);
                plantillaJasper = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\Titulo_Reverso.jasper");
                FileUtils.copyFileToDirectory(plantillaJasper, carpetaXMLPlantillas);

                bitacora.setInformacion("Registro Configuración Inicial por primera vez");
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else if (cstmt.getMoreResults()) {
                rs = cstmt.getResultSet();
                rs.next();
                RESP = "error|"
                        + "<p>Error al insertar la configuración inicial.</p><br>"
                        + "<small>El servidor devolvió el siguiente número de error desde SQL: <b>" + rs.getString("ErrorNumber") + "</b></small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            } else {
                RESP = "error|"
                        + "<p>Error al insertar la configuración inicial.</p><br>"
                        + "<small>El servidor no devolvió ningun estado de respuesta</small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al registrar la configuración inicial: " + accion_catch(ex);
            File carpetaInstitucional = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim());
            if (carpetaInstitucional.exists()) {
                carpetaInstitucional.delete();
            }
        } catch (IOException ex) {
            RESP = "error|Error IOException al registrar la configuración inicial: " + accion_catch(ex);
            File carpetaInstitucional = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim());
            if (carpetaInstitucional.exists()) {
                carpetaInstitucional.delete();
            }
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException al registrar la configuración inicial: " + accion_catch(ex);
            File carpetaInstitucional = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + ci.getNombreInstitucion().trim());
            if (carpetaInstitucional.exists()) {
                carpetaInstitucional.delete();
            }
        } finally {
            if (cstmt != null && !cstmt.isClosed()) {
                cstmt.close();
            }
            if (con != null && !con.isClosed()) {
                con.close();
            }
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                conexion.GetconexionInSite().close();
            }
        }

        return RESP;
    }

    private String updConfiguracion() throws SQLException {
        String RESP = "";
        conexion = new CConexion();
        conexion.setRequest(request);
        bitacora = new Bitacora();
        bitacora.setId_Usuario(ID_Usuario);
        bitacora.setModulo("Configuración Inicial");
        bitacora.setMovimiento("Actualización");
        try {
            String Query = "{call Upd_ConfiguracionInicial (?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, Integer.valueOf(ci.getID_ConfiguracionInicial()));
            cstmt.setString(2, ci.getPrefijoFolioTitulo());
            cstmt.setString(3, ci.getPrefijoFolioCertificado());
            cstmt.setInt(4, ci.getLongitudFolioTitulo());
            cstmt.setInt(5, ci.getLongitudFolioCertificado());
            cstmt.setString(6, ci.getNombreOficial());
            cstmt.setString(7, ci.getLogo());
            cstmt.setString(8, ci.getUsuarioSEP());
            cstmt.setString(9, ci.getPasswordSEP());
            cstmt.setString(10, ci.getUsuarioSepProductivo());
            cstmt.setString(11, ci.getPasswordSepProductivo());
            cstmt.setString(12, ci.getFirmaImagen());
            cstmt.registerOutParameter(13, java.sql.Types.VARCHAR);
            cstmt.execute();

            RESP = cstmt.getString(13);
            if (RESP.equalsIgnoreCase("success")) {
                bitacora.setInformacion("Actualización de configuración Inicial");
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else if (cstmt.getMoreResults()) {
                rs = cstmt.getResultSet();
                rs.next();
                RESP = "error|"
                        + "<p>Error al actualizar la configuración inicial.</p><br>"
                        + "<small>El servidor devolvió el siguiente número de error desde SQL: <b>" + rs.getString("ErrorNumber") + "</b></small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            } else {
                RESP = "error|"
                        + "<p>Error al actualizar la configuración inicial.</p><br>"
                        + "<small>El servidor no devolvió ningun estado de respuesta</small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al registrar la configuración inicial: " + accion_catch(ex);
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException al registrar la configuración inicial: " + accion_catch(ex);
        } finally {
            if (cstmt != null && !cstmt.isClosed()) {
                cstmt.close();
            }
            if (con != null && !con.isClosed()) {
                con.close();
            }
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                conexion.GetconexionInSite().close();
            }
        }
        return RESP;
    }

    private String add_ClaveAutorizacion() {
        String RESP = "";
        String claveAutorizacion = request.getParameter("claveAutorizacion");
        claveAutorizacion = claveAutorizacion.replace(" ", "+");
        String idConfiguracion = request.getParameter("idConfiguracion");
        String tmp = "";
        int timbresPasados = 0;
        conexion = new CConexion();
        conexion.setRequest(request);
        desencriptador = new CClavesActivacion();
        bitacora = new Bitacora();
        bitacora.setId_Usuario(ID_Usuario);
        bitacora.setModulo("Claves de Activación");
        bitacora.setMovimiento("Inserción");
        try {

            String Query = "SELECT TOP 1 tmp FROM Clave_Activacion AS C "
                    + " JOIN Usuario AS U ON U.ID_ConfiguracionInicial = C.ID_ConfiguracionInicial "
                    + " WHERE ID_Usuario = " + ID_Usuario
                    + " ORDER BY ID_ClaveActivacion DESC;";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                tmp = rs.getString(1);
                timbresPasados = desencriptador.leeClaveTimbre(tmp);
            }

            String claveDesencriptaUno = desencriptador.desencripta(claveAutorizacion.trim());

            RESP = new String(Base64.decodeBase64(claveDesencriptaUno.getBytes()));

            String claveInstitucion = RESP.substring(3, 13);
            String fechaVencimiento = RESP.substring(RESP.length() - 8);
            String numTimbres = RESP.substring(13, RESP.length() - 10);
            timbresPasados += Integer.valueOf(numTimbres);

            tmp = desencriptador.GenerarClaveTimbres(timbresPasados + "");
            if (!tmp.contains("error")) {
                Query = "{call Add_ClaveActivacion (?,?,?,?,?)}";
                cstmt = con.prepareCall(Query);
                cstmt.setString(1, claveAutorizacion);
                cstmt.setString(2, claveInstitucion.trim());
                cstmt.setString(3, tmp.trim());
                cstmt.setInt(4, Integer.valueOf(idConfiguracion));
                cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
                cstmt.execute();
                RESP = cstmt.getString(5);

                if (RESP.contains("success")) {
                    RESP += "||" + numTimbres + "||" + fechaVencimiento.substring(0, 2) + "-" + fechaVencimiento.substring(2, 4) + "-" + fechaVencimiento.substring(4);
                    bitacora.setInformacion("Registro de clave de activación: " + claveAutorizacion + "||Respuesta método:" + RESP);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.setRequest(request);
                    cBitacora.addBitacoraGeneral();
                } else if (!RESP.contains("claveUsada") && !RESP.contains("claveInvalida")) {
                    RESP = "error";
                }
            } else {
                RESP = "error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        }

        return RESP;
    }

    private String accion_catch(Exception ex) {
        String resp = "";
        try {
            resp = ex.toString().replace("'", "\"");
            Logger.getLogger(CConfiguracionInicial.class.getName()).log(Level.SEVERE, null, ex);
            resp = "<h4 style='color:#e76d6d'>" + resp;
            resp += "</h4>";
        } catch (Exception e) {
            resp += "</h4><small class='text-primary'>No se ha insertado en Bitacora de errores</small>";
        }
        resp += "<br><br><small>Si continua con el problema, comuníquese con soporte técnico.</small> ";
        return resp;
    }
}
