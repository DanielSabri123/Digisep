/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.TECargos;
import com.ginndex.titulos.modelo.TEFirmante;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Braulio Sorcia
 */
public class CFirmantes {

    private String Bandera;
    private String Id_Usuario;
    private String nombreArchivo;
    private String rutaArchivo;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private CConexionPool conector;
    private CConexion conexion;
    private TEFirmante firmante;
    private String noCertificado;
    private String NombreInstitucion;
    private String permisos;
    private String fechaValidez;
    private String fechaExpir;
    private List<TEFirmante> lstFirmantesNoti;
    Bitacora bitacora;
    CBitacora cBitacora;

    public String getBandera() {
        return Bandera;
    }

    public void setBandera(String Bandera) {
        this.Bandera = Bandera;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
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

    public CConexionPool getConector() {
        return conector;
    }

    public void setConector(CConexionPool conector) {
        this.conector = conector;
    }

    public TEFirmante getFirmante() {
        return firmante;
    }

    public void setFirmante(TEFirmante firmante) {
        this.firmante = firmante;
    }

    public String getNoCertificado() {
        return noCertificado;
    }

    public void setNoCertificado(String noCertificado) {
        this.noCertificado = noCertificado;
    }

    public String getFechaValidez() {
        return fechaValidez;
    }

    public void setFechaValidez(String fechaValidez) {
        this.fechaValidez = fechaValidez;
    }

    public String getFechaExpir() {
        return fechaExpir;
    }

    public void setFechaExpir(String fechaExpir) {
        this.fechaExpir = fechaExpir;
    }

    public CFirmantes() {
        Bandera = "";
        nombreArchivo = "";
        noCertificado = "";
        firmante = new TEFirmante();
    }

    public String establecerAcciones() throws SQLException {
        String RESP = "";
        try {
            HttpServletRequest requestProvisional = getRequest();
            setBandera(request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
            requestProvisional.setCharacterEncoding("UTF-8");
            HttpSession sessionOk = request.getSession();
            Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
            this.conector = new CConexionPool(request);
            this.conector.conexion = this.conector.dataSource.getConnection();

            CPermisos cPermisos = new CPermisos();
            cPermisos.setRequest(request);
            permisos = cPermisos.obtenerPermisos("Firmantes");

            if (getBandera().equalsIgnoreCase("0")) {
                obtenerNombreInstitucion();
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1024);
                ServletFileUpload up = new ServletFileUpload(factory);
                up.setHeaderEncoding("UTF-8");
                boolean isMultipart = ServletFileUpload.isMultipartContent(requestProvisional);
                if (isMultipart) {
                    List<FileItem> partes = up.parseRequest(requestProvisional);
                    setBandera(getDat(partes));
                }
            }
            if (getBandera().equalsIgnoreCase("1")) {
                RESP = consultaTblFirmantes() + "~" + cargarNotifyFirmantes();
            } else if (getBandera().equalsIgnoreCase("2")) {
                RESP = cargarListaCargos();
            } else //Inserción de Firmante
            if (getBandera().equalsIgnoreCase("3")) {
                RESP = insertarFirmante();
            } else //Actualización de Firmante
            if (getBandera().equalsIgnoreCase("4")) {
                RESP = actualizarFirmante();
            } else //Eliminación de Firmante
            if (getBandera().equalsIgnoreCase("5")) {
                String idFirmante = requestProvisional.getParameter("txtIdFirmante");
                RESP = eliminarFirmante(idFirmante);
            } else //Consultamos un firmante mediante su Id
            if (getBandera().equalsIgnoreCase("6")) {
                String idFirmante = requestProvisional.getParameter("txtIdFirmante");
                RESP = consultarFirmanteEspecifico(idFirmante);
            } else //Leemos el archivo de certificado
            if (getBandera().equalsIgnoreCase("10")) {
                RESP = getNoCertificado() + "||" + getFechaValidez() + "||" + getFechaExpir();
            } else if (getBandera().split("¬")[0].equalsIgnoreCase("formException")) {
                RESP = "error|Error al recuperar la información del formulario: " + getBandera().split("¬")[1];
            } else if (getBandera().split("¬")[0].equalsIgnoreCase("certException")) {
                RESP = "error|Error al realizar la lectura del certificado o llave: " + getBandera().split("¬")[1];
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Ocurrió un error al establecer el formato UTF-8.", ex);
        } catch (FileUploadException ex) {
            Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Ocurrió un error al subir el archivo.", ex);
        } catch (IOException ex) {

        }

        if (getConector().conexion != null || !getConector().conexion.isClosed()) {
            getConector().cerrarConexion();
        }
        return RESP;
    }

    public String getDat(List<FileItem> partes) {
        String Bandera = "";
        //String ubicacionarchivo = System.getProperty("user.home") + "\\Downloads\\Archivos_Firmantes";
        String rutaLlave = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\KEY";
        //String rutaLlave = System.getProperty("user.home") + "\\Downloads\\Archivos_Firmantes";
        String rutaCER = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\CER";
        //String rutaCER = System.getProperty("user.home") + "\\Downloads\\Archivos_Firmantes";
        String input_actual = "";
        Logger.getLogger(CFirmantes.class.getName()).log(Level.INFO, "RUTA KEY:-------->", rutaLlave);
        Logger.getLogger(CFirmantes.class.getName()).log(Level.INFO, "RUTA CER:-------->", rutaCER);
        for (FileItem item : partes) {
            try {
                FileItem uploaded = null;
                uploaded = (FileItem) item;
                input_actual = uploaded.getFieldName();
                if (input_actual.equalsIgnoreCase("bandera")) {
                    Bandera = uploaded.getString("UTF-8");
                } //Seteamos la información en el objeto firmantes    INSERTAR
                else if (Bandera.equalsIgnoreCase("3")) {
                    if (input_actual.equalsIgnoreCase("txtNombreFirmante")) {
                        getFirmante().setNombre(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtApaternoFirmante")) {
                        getFirmante().setPrimerApellido(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtAmaternoFirmante")) {
                        getFirmante().setSegundoApellido(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtCurpFirmante")) {
                        getFirmante().setCURP(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("lstCargoFirmante")) {
                        getFirmante().setID_Cargo(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtAbrevTitulo")) {
                        getFirmante().setAbrTitulo(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("noCertificado")) {
                        getFirmante().setNoCertificadoResponsable(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("fechaValidez")) {
                        getFirmante().setFechaCertificadoValidez(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("fechaExpira")) {
                        getFirmante().setFechaCertificadoExpirar(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtPasswordFirmante")) {
                        getFirmante().setContrasenia(uploaded.getString("UTF-8"));
                        break;
                    } else if (input_actual.equalsIgnoreCase("fileCertificadoFirmante")) {
                        try {
                            //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                            setRutaArchivo(rutaCER + "\\" + uploaded.getName());
                            setNombreArchivo(uploaded.getName());
                            if (uploaded.getName().equalsIgnoreCase("")) {
                                setNombreArchivo("");
                            } else {
                                File archivoServer = new File(getRutaArchivo());
                                uploaded.write(archivoServer);
                                getFirmante().setCertificadoResponsable(getNombreArchivo());
                            }
                        } catch (Exception ex) {
                        }
                    } else if (input_actual.equalsIgnoreCase("fileLlaveFirmante")) {
                        //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                        setRutaArchivo(rutaLlave + "\\" + uploaded.getName());
                        setNombreArchivo(uploaded.getName());
                        if (uploaded.getName().equalsIgnoreCase("")) {
                            setNombreArchivo("");
                        } else {
                            File archivoServer = new File(getRutaArchivo());
                            uploaded.write(archivoServer);
                            getFirmante().setLlave(getNombreArchivo());
                        }
                    }
                } else if (Bandera.equalsIgnoreCase("4")) {
                    if (input_actual.equalsIgnoreCase("idFirmante")) {
                        getFirmante().setID_Firmante(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtNombreFirmante")) {
                        getFirmante().setNombre(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtApaternoFirmante")) {
                        getFirmante().setPrimerApellido(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtAmaternoFirmante")) {
                        getFirmante().setSegundoApellido(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtCurpFirmante")) {
                        getFirmante().setCURP(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("lstCargoFirmante")) {
                        getFirmante().setID_Cargo(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtAbrevTitulo")) {
                        getFirmante().setAbrTitulo(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("noCertificado")) {
                        getFirmante().setNoCertificadoResponsable(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("fechaValidez")) {
                        getFirmante().setFechaCertificadoValidez(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("fechaExpira")) {
                        getFirmante().setFechaCertificadoExpirar(uploaded.getString("UTF-8"));
                    } else if (input_actual.equalsIgnoreCase("txtPasswordFirmante")) {
                        getFirmante().setContrasenia(uploaded.getString("UTF-8"));
                        break;
                    } else if (input_actual.equalsIgnoreCase("fileCertificadoFirmante")) {
                        //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                        setRutaArchivo(rutaCER + "\\" + uploaded.getName());
                        setNombreArchivo(uploaded.getName());
                        if (uploaded.getName().equalsIgnoreCase("")) {
                            setNombreArchivo("");
                        } else {
                            File archivoServer = new File(getRutaArchivo());
                            uploaded.write(archivoServer);
                            getFirmante().setCertificadoResponsable(getNombreArchivo());
                        }
                    } else if (input_actual.equalsIgnoreCase("fileLlaveFirmante")) {
                        //String extension = uploaded.getName().replace(".", "#").split("#")[ uploaded.getName().replace(".", "#").split("#").length - 1];
                        setRutaArchivo(rutaLlave + "\\" + uploaded.getName());
                        setNombreArchivo(uploaded.getName());
                        if (uploaded.getName().equalsIgnoreCase("")) {
                            setNombreArchivo("");
                        } else {
                            File archivoServer = new File(getRutaArchivo());
                            uploaded.write(archivoServer);
                            getFirmante().setLlave(getNombreArchivo());
                        }
                    }

                } else if (Bandera.equalsIgnoreCase("10")) {
                    if (input_actual.equalsIgnoreCase("fileCertificadoFirmante")) {
                        setRutaArchivo(rutaCER + "\\" + uploaded.getName());
                        setNombreArchivo(uploaded.getName());
                        if (uploaded.getName().equalsIgnoreCase("")) {
                            setNombreArchivo("");
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            X509Certificate certificado = null;
                            File archivoServer = new File(getRutaArchivo());
                            uploaded.write(archivoServer);
                            InputStream is = new FileInputStream(archivoServer);
                            CertificateFactory cf = CertificateFactory.getInstance("X.509");
                            certificado = (X509Certificate) cf.generateCertificate(is);
                            //System.out.println("EL NOMBRE DEL MORRO ES: " + certificado.getSubjectX500Principal().getName());
                            byte[] byteArray = certificado.getSerialNumber().toByteArray();
                            String aux = new String(byteArray);
                            setNoCertificado(aux);

                            Date fechaValidezCert = certificado.getNotBefore();
                            String cadenaFechaValidez = sdf.format(fechaValidezCert);
                            setFechaValidez(cadenaFechaValidez);

                            Date fechaExpirCert = certificado.getNotAfter();
                            String cadenaFechaExpirar = sdf.format(fechaExpirCert);
                            setFechaExpir(cadenaFechaExpirar);

                            generatePemFile(archivoServer.getName(), certificado);
                            is.close();
                        }
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                Bandera = "formException¬" + accion_catch(ex);
            } catch (Exception ex) {
                Bandera = "certException¬" + accion_catch(ex);
            }
        }
        return Bandera;
    }

    public String consultaTblFirmantes() throws SQLException {
        String RESP = "";
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rsRecord = null;
        try {
            conexion = new CConexion();
            conexion.setRequest(request);
            conn = conexion.GetconexionInSite();

            RESP += "success|<table class=\"table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed\" style=\"width:100%;\"  id=\"tblFirmantes\">";
            RESP += "<thead class=\"bg-primary-dark\" style=\"color: white;\">";
            RESP += "<tr>";
            RESP += "<th class=\"text-center\" style=\"display:none;\">IdFirmante</th>";
            RESP += "<th class=\"text-center\">Nombre(s)</th>";
            RESP += "<th class=\"text-center\">A. Paterno</th>";
            RESP += "<th class=\"text-center hidden-sm hidden-xs\">Cargo</th>";
            RESP += "<th class=\"text-center hidden-sm hidden-xs\">Abrev. Titulo</th>";
            RESP += "<th class=\"text-center hidden-sm hidden-xs\">No. Certificado Responsable</th>";
            RESP += "<th class=\"text-center thAcciones\">Acciones</th>";
            RESP += "</tr>";
            RESP += "</thead>";
            RESP += "<tbody>";

            String Query = "SELECT id_firmante, nombre, primerApellido, cargo, abrTitulo,noCertificadoResponsable FROM TEFirmantes f JOIN TECARGOS c ON f.idCargo = c.IdCargo WHERE f.estatus<>0";
            List<TEFirmante> lstFirmantes = new ArrayList<TEFirmante>();

            ps = conn.prepareStatement(Query);
            rsRecord = ps.executeQuery();

            while (rsRecord.next()) {
                TEFirmante teFirmante = new TEFirmante();

                teFirmante.setID_Firmante(rsRecord.getString("id_firmante"));
                teFirmante.setNombre(rsRecord.getString("nombre"));
                teFirmante.setPrimerApellido(rsRecord.getString("primerApellido"));
                teFirmante.setCargo(rsRecord.getString("cargo"));
                teFirmante.setAbrTitulo(rsRecord.getString("abrTitulo"));
                teFirmante.setNoCertificadoResponsable(rsRecord.getString("noCertificadoResponsable"));

                lstFirmantes.add(teFirmante);
            }

            String cabecera = permisos.split("°")[0];
            if (!cabecera.equalsIgnoreCase("todos")) {
                permisos = permisos.split("°")[1];
            }
            if (lstFirmantes.size() > 0) {
                for (TEFirmante c : lstFirmantes) {
                    RESP += "<tr>";
                    RESP += "<td class='text-center' style='display: none;' id='IdFirmante_'" + c.getID_Firmante() + "'>" + c.getID_Firmante() + "</td>";
                    RESP += "<td id='Nombre_" + c.getID_Firmante() + "'>" + c.getNombre() + "</td>";
                    RESP += "<td id='Apaterno_" + c.getID_Firmante() + "'>" + c.getPrimerApellido() + "</td>";
                    RESP += "<td id='Cargo_" + c.getID_Firmante() + "'     class='hidden-sm hidden-xs'>" + c.getCargo() + "</td>";
                    RESP += "<td id='AbrTitulo_" + c.getID_Firmante() + "' class='hidden-sm hidden-xs'>" + c.getAbrTitulo() + "</td>";
                    RESP += "<td id='No.Cert_" + c.getID_Firmante() + "'   class='hidden-sm hidden-xs'>" + c.getNoCertificadoResponsable() + "</td>";
                    RESP += "<td class='text-center thAcciones' id='Acciones_" + c.getID_Firmante() + "'>";
                    if (cabecera.contains("todos")) {
                        RESP += "<div class='btn-group'>"
                                + "<button class='btn btn-default btn-xs btnConsultarFirmante' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Consultar' id='lookFirmante_" + c.getID_Firmante() + "'><i class='fa fa-search'></i></button>"
                                + "<button class='btn btn-default btn-xs btnEditarFirmante' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Editar' id='modifyFirmante_" + c.getID_Firmante() + "'><i class='fa fa-pencil'></i></button>"
                                + "<button class='js-swal-confirm btn btn-default btn-xs btnEliminarFirmante' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Eliminar' id='deleteFirmante_" + c.getID_Firmante() + "'><i class='fa fa-times'></i></button>"
                                + "</div>";
                    } else if (cabecera.contains("acceso")) {
                        if (permisos.split("¬")[2].equalsIgnoreCase("1") || permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[4].equalsIgnoreCase("1")) {
                            RESP += "<div class='btn-group'>";
                            if (permisos.split("¬")[2].equalsIgnoreCase("1")) {
                                RESP += "<button class='btn btn-default btn-xs btnConsultarFirmante' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Consultar' id='lookFirmante_" + c.getID_Firmante() + "'><i class='fa fa-search'></i></button>";
                            }
                            if (permisos.split("¬")[3].equalsIgnoreCase("1")) {
                                RESP += "<button class='btn btn-default btn-xs btnEditarFirmante' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Editar' id='modifyFirmante_" + c.getID_Firmante() + "'><i class='fa fa-pencil'></i></button>";
                            }
                            if (permisos.split("¬")[4].equalsIgnoreCase("1")) {
                                RESP += "<button class='js-swal-confirm btn btn-default btn-xs btnEliminarFirmante' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Eliminar' id='deleteFirmante_" + c.getID_Firmante() + "'><i class='fa fa-times'></i></button>";
                            }
                            RESP += "</div>";
                        } else {
                            RESP += "<span class='label label-danger'>No disponible</span>";
                        }
                    }
                    RESP += "</td></tr>";
                }
            }
            RESP += "</tbody>";
            RESP += "</table>";
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar el armado de la tabla de firmantes: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar el armado de la tabla de firmantes: " + accion_catch(ex);
        } finally {
            if (ps != null) {
                if (!ps.isClosed()) {
                    ps.close();
                }
            }

            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        }
        return RESP;
    }

    public String cargarListaCargos() {
        String RESP = "";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            String Query = "SELECT * FROM TECARGOS --WHERE Cargo NOT IN('SECRETARIO GENERAL','AUTORIDAD LOCAL','AUTORIDAD FEDERAL','DIRECTOR GENERAL');";
            conexion = new CConexion();
            conexion.setRequest(request);
            conn = conexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);
            ps.executeQuery();
            ResultSet rsRecord = ps.getResultSet();
            List<TECargos> lstCargos = new ArrayList<TECargos>();

            while (rsRecord.next()) {
                TECargos cargos = new TECargos();

                cargos.setID_Cargos(rsRecord.getString("IdCargo"));
                cargos.setCargo(rsRecord.getString("cargo"));
                lstCargos.add(cargos);
            }
            RESP += "<option></option>";
            for (TECargos c : lstCargos) {
                RESP += "<option value='" + c.getID_Cargos() + "'>" + c.getCargo()
                        + (c.getCargo().equalsIgnoreCase("SECRETARIO GENERAL")
                        || c.getCargo().equalsIgnoreCase("AUTORIDAD LOCAL")
                        || c.getCargo().equalsIgnoreCase("AUTORIDAD FEDERAL")
                        || c.getCargo().equalsIgnoreCase("DIRECTOR GENERAL") ? " - Cargo SEP" : "") + "</option>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta de cargos: " + accion_catch(ex) + "|<option>ERROR SQL</option>";
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta de cargos: " + accion_catch(ex) + "|<option>ERROR INESPERADO</option>";
        } finally {
            try {
                if (ps != null) {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                }

                if (conn != null) {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Error al cerrar el PreparedStatement", ex);
            }
        }
        return RESP;
    }

    public String insertarFirmante() {
        String RESP = "";
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Firmantes");
        bitacora.setMovimiento("Inserción");
        try {
            String Query = "EXEC Add_Firmante ?,?,?,?,?,?,?,?,?,?,?,?,?,?";
            cstmt = getConector().conexion.prepareCall(Query);
            cstmt.setString(1, getFirmante().getNombre());
            cstmt.setString(2, getFirmante().getPrimerApellido());
            cstmt.setString(3, getFirmante().getSegundoApellido());
            cstmt.setString(4, getFirmante().getCURP());
            cstmt.setString(5, getFirmante().getID_Cargo());
            cstmt.setString(6, getFirmante().getAbrTitulo());
            cstmt.setString(7, getFirmante().getCertificadoResponsable());
            cstmt.setString(8, getFirmante().getNoCertificadoResponsable());
            cstmt.setString(9, getFirmante().getContrasenia());
            cstmt.setString(10, getFirmante().getLlave());
            cstmt.setString(11, getFirmante().getCertificadoPem());
            cstmt.setString(12, getFirmante().getFechaCertificadoValidez());
            cstmt.setString(13, getFirmante().getFechaCertificadoExpirar());
            cstmt.registerOutParameter(14, java.sql.Types.VARCHAR);
            cstmt.execute();
            String idFirmante = cstmt.getString(14);
            ResultSet rsRecord = null;
            //Si no devuelve nada el procedimiento almacenado, todo fue bien :D
            if (!cstmt.getMoreResults() && cstmt.getUpdateCount() != 1) {
                RESP = "success";
                bitacora.setInformacion("Registro de firmante");
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else if (cstmt.getMoreResults()) {
                rsRecord = cstmt.getResultSet();
                rsRecord.next();
                RESP = "error|"
                        + "<p>Error al insertar el firmante.</p><br>"
                        + "<small>El servidor devolvió el siguiente número de error desde SQL: <b>" + rsRecord.getString("ErrorNumber") + "</b></small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            } else {
                RESP = "error|"
                        + "<p>Error al insertar el firmante.</p><br>"
                        + "<small>El servidor no devolvió ningun estado de respuesta</small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar el registro del firmante: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar el registro del firmante: " + accion_catch(ex);
        } finally {
            try {
                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Error al cerrar el PreparedStatement", ex);
            }
        }
        return RESP;
    }

    public String eliminarFirmante(String idFirmante) {
        String RESP = "";
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Firmantes");
        bitacora.setMovimiento("Eliminación");
        try {
            String Query = "EXEC Delt_Firmante ?";
            cstmt = getConector().conexion.prepareCall(Query);
            cstmt.setString(1, idFirmante);
            cstmt.execute();
            ResultSet rsRecord = cstmt.getResultSet();
            //Si no devuelve nada el procedimiento almacenado, todo fue bien :D
            if (rsRecord == null) {
                if (cstmt.getMoreResults()) {
                    rsRecord = cstmt.getResultSet();
                    rsRecord.next();
                    RESP = "error|"
                            + "<p>Error al eliminar el firmante.</p><br>"
                            + "<small>El servidor devolvió el siguiente número de error desde SQL: <b>" + rsRecord.getString("ErrorNumber") + "</b></small>"
                            + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
                } else {
                    RESP = "success";
                    bitacora.setInformacion("Eliminación de firmante: " + idFirmante);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.setRequest(request);
                    cBitacora.addBitacoraGeneral();
                }
            } else {
                RESP = "error|El procedimiento fue ejecutado, pero no devolvió la respuesta esperada.<br><small>Presione F5 para refrescar la página y verificar si el firmante fue eliminado.</small>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la eliminación del firmante: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la eliminación del firmante: " + accion_catch(ex);
        } finally {
            try {
                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Error al cerrar el CallableStatement", ex);
            }
        }
        return RESP;
    }

    public String actualizarFirmante() {
        String RESP = "";
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Firmantes");
        bitacora.setMovimiento("Actualización");
        try {
            String Query = "EXEC Upd_Firmante ?,?,?,?,?,?,?,?,?,?,?,?,?,?";
            cstmt = getConector().conexion.prepareCall(Query);
            cstmt.setString(1, getFirmante().getID_Firmante());
            cstmt.setString(2, getFirmante().getNombre());
            cstmt.setString(3, getFirmante().getPrimerApellido());
            cstmt.setString(4, getFirmante().getSegundoApellido());
            cstmt.setString(5, getFirmante().getCURP());
            cstmt.setString(6, getFirmante().getID_Cargo());
            cstmt.setString(7, getFirmante().getAbrTitulo());
            cstmt.setString(8, getFirmante().getCertificadoResponsable());
            cstmt.setString(9, getFirmante().getNoCertificadoResponsable());
            cstmt.setString(10, getFirmante().getContrasenia());
            cstmt.setString(11, getFirmante().getLlave());
            cstmt.setString(12, getFirmante().getCertificadoPem());
            cstmt.setString(13, getFirmante().getFechaCertificadoValidez());
            cstmt.setString(14, getFirmante().getFechaCertificadoExpirar());
            cstmt.execute();
            //Si no devuelve nada el procedimiento almacenado, todo fue bien :D
            if (!cstmt.getMoreResults() && cstmt.getUpdateCount() != 1) {
                RESP = "success";
                bitacora.setInformacion("Actualización de firmante");
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else if (cstmt.getMoreResults()) {
                ResultSet rsRecord = cstmt.getResultSet();
                rsRecord.next();
                RESP = "error|"
                        + "<p>Error al actualizar el firmante.</p><br>"
                        + "<small>El servidor devolvió el siguiente número de error desde SQL: <b>" + rsRecord.getString("ErrorNumber") + "</b></small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            } else {
                RESP = "error|"
                        + "<p>Error al actualizar el firmante.</p><br>"
                        + "<small>El servidor no devolvió ningun estado de respuesta</small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la actualización del firmante: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la actualización del firmante: " + accion_catch(ex);
        } finally {
            try {
                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Error al cerrar el PreparedStatement", ex);
            }
        }
        return RESP;
    }

    public String consultarFirmanteEspecifico(String idFirmante) {
        String RESP = "";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            String Query = "SELECT * FROM TEFIRMANTES WHERE id_Firmante = ?";
            ps = conn.prepareStatement(Query);
            ps.setString(1, idFirmante);
            ResultSet rsRecord = ps.executeQuery();
            if (rsRecord != null) {
                RESP = "success|";
                if (rsRecord.next()) {
                    RESP += rsRecord.getString("nombre") + "¬" + rsRecord.getString("primerApellido") + "¬"
                            + rsRecord.getString("segundoApellido") + "¬" + rsRecord.getString("curp") + "¬" + rsRecord.getString("idCargo") + "¬"
                            + rsRecord.getString("abrTitulo") + "¬" + rsRecord.getString("certificadoResponsable") + "¬" + rsRecord.getString("noCertificadoResponsable") + "¬"
                            + "" + "¬" + rsRecord.getString("llave") + "¬"
                            + rsRecord.getString("fechaCertificadoValidez") + "¬" + rsRecord.getString("fechaCertificadoExpirar");
                }
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la consulta del certificado: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la consulta del certificado: " + accion_catch(ex);
        } finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
                if (conn != null) {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Error al cerrar el PreparedStatement", ex);
            }
        }
        return RESP;
    }

    public void generatePemFile(String nameFile, X509Certificate cert) {
        FileWriter writer = null;
        try {
            //String ubicacionarchivo = System.getProperty("user.home") + "\\Downloads\\Archivos_Firmantes";
            String ubicacionarchivo = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion + "\\PEM";
            Logger.getLogger(CFirmantes.class.getName()).log(Level.INFO, "RUTA PEM:-------->", ubicacionarchivo);
            writer = new FileWriter(ubicacionarchivo + "\\" + nameFile + ".pem", false);
            String pemContent = "";
            pemContent += "-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator");
            pemContent += Base64.getEncoder().encodeToString(cert.getEncoded()) + System.getProperty("line.separator");
            pemContent += "-----END CERTIFICATE-----";
            writer.write(pemContent);
            getFirmante().setCertificadoPem(nameFile + ".pem");
        } catch (IOException e) {
            Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al escribir el documento PEM!", e);
        } catch (CertificateEncodingException e) {
            Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al codificar el certificado!", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el FileWritter!", e);
            }
        }
    }

    public String obtenerNombreInstitucion() throws SQLException {
        String RESP = "";
        Connection con = null;
        conexion = new CConexion();
        conexion.setRequest(request);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String Query = "SELECT nombreInstitucion FROM Configuracion_Inicial AS CI "
                    + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                    + " WHERE Id_Usuario = " + Id_Usuario;

            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                NombreInstitucion = rs.getString(1).trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Error:--------->", e);
        } finally {
            con.close();
            rs.close();
            pstmt.close();
            conexion.GetconexionInSite().close();
        }

        return RESP;
    }

    private String cargarNotifyFirmantes() {
        String RESP = "";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            lstFirmantesNoti = new ArrayList<TEFirmante>();
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

                lstFirmantesNoti.add(firmante);

            }

            for (TEFirmante firm : lstFirmantesNoti) {
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
            Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al generar las notificaciones de inicio!", ex);
            RESP = "errorNotify|Ocurrió un error SQL al generar las notificaciones de firmantes: " + accion_catch(ex);
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarNotifyFirmantes() !", ex);
                }
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
