package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.ClaveActivacion;
import com.ginndex.titulos.modelo.TEFirmante;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.ssl.Base64;

/**
 *
 * @author Paola Alonso
 */
public class CBienvenida {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String Bandera;
    private String Id_Usuario;
    private ResultSet rs;
    private CConexion conexion;
    private Connection con;
    private PreparedStatement pstmt;
    CClavesActivacion desencriptador;
    private TEFirmante firmante;
    private List<TEFirmante> lstFirmantes;

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

    public TEFirmante getFirmante() {
        return firmante;
    }

    public void setFirmante(TEFirmante firmante) {
        this.firmante = firmante;
    }

    public List<TEFirmante> getLstFirmantes() {
        return lstFirmantes;
    }

    public void setLstFirmantes(List<TEFirmante> lstFirmantes) {
        this.lstFirmantes = lstFirmantes;
    }

    public String EstablecerAcciones() throws UnsupportedEncodingException {
        String RESP = "";

        setBandera(request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");

        HttpSession sessionOk = request.getSession();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();

        if (Bandera.equalsIgnoreCase("1")) {
            RESP = cargarTimbrados() + "~" + cargarNotifyFirmantes() + "~" + consultarVigencia();
        }

        return RESP;
    }

    private String cargarTimbrados() {
        String RESP = "";
        String Id_ClaveActivacion = "";
        String cveActivacion = "";
        String fechaVencimiento = "";
        SimpleDateFormat parseador = new SimpleDateFormat("dd-MM-yyyy");
        Date hoy = new Date();
        conexion = new CConexion();
        conexion.setRequest(request);
        ClaveActivacion ca = null;
        desencriptador = new CClavesActivacion();
        try {
            String Query = "SELECT MAX(ID_ClaveActivacion) AS ID_ClaveActivacion FROM Clave_Activacion AS C "
                    + " JOIN Usuario AS U ON C.ID_ConfiguracionInicial = U.Id_ConfiguracionInicial WHERE U.ID_Usuario = " + Id_Usuario;
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Id_ClaveActivacion = rs.getString(1);
                if (Id_ClaveActivacion != null && !Id_ClaveActivacion.equalsIgnoreCase("null")) {
                    Query = "SELECT Tmp, Clave FROM Clave_Activacion WHERE ID_ClaveActivacion = " + Id_ClaveActivacion.trim();
                    rs = null;
                    pstmt = con.prepareStatement(Query);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        ca = new ClaveActivacion();

                        ca.setTmp(rs.getString(1));
                        ca.setClave(rs.getString(2));
                        cveActivacion = desencriptador.desencripta(ca.getClave());
                        cveActivacion = new String(Base64.decodeBase64(cveActivacion.getBytes()));
                        fechaVencimiento = cveActivacion.substring(cveActivacion.length() - 8);
                        String dd = fechaVencimiento.substring(0, 2);
                        String MM = fechaVencimiento.substring(2, 4);
                        String yyyy = fechaVencimiento.substring(4);
                        fechaVencimiento = dd + "-" + MM + "-" + yyyy;
                    }
                } else {
                    RESP = "0~0~0~noHay";
                    return RESP;
                }
            }

            if (ca != null) {
                RESP += desencriptador.leeClaveTimbre(ca.getTmp());

                Query = "SELECT COUNT(*) AS 'TotalCertificados' FROM TECertificadoElectronico WHERE estatus = 1;";
                rs = null;
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    RESP += "~" + rs.getInt(1);
                }

                Query = "SELECT COUNT(*) AS 'TotalTitulos' FROM TETituloElectronico WHERE estatus <> 0;";
                rs = null;
                pstmt = con.prepareStatement(Query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    RESP += "~" + rs.getInt(1);
                }

                if (hoy.after(parseador.parse(fechaVencimiento))) {
                    RESP += "~vencidos";
                } else {
                    RESP += "~vigentes";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
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
            Logger.getLogger(CBienvenida.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al generar las notificaciones de inicio!", ex);
            RESP = "errorNotify";
        } finally {
            if (pstmt != null) {
                try {
                    if (!pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CBienvenida.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CBienvenida.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Resulset en el método cargarNotifyFirmantes() !", ex);
                }
            }
            if (con != null) {
                try {
                    if (!con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CBienvenida.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarNotifyFirmantes() !", ex);
                }
            }
        }
        return RESP;
    }

    public String consultarVigencia() {
        String RESP = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaInicio = "";
            String fechaFin = "";
            Date today = new Date();
            PreparedStatement ps = null;
            ResultSet rs = null;
            Connection conn = null;
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            String Query = "SET DATEFORMAT YMD;SELECT * FROM Vigencia_Polizas WHERE Activo = 1";

            ps = conn.prepareStatement(Query);

            rs = ps.executeQuery();

            if (rs.next()) {
                fechaFin = rs.getString("fechaFin").split(" ")[0];
                LocalDate fechaVigenciaSistema = LocalDate.parse(fechaFin);
                LocalDate fechaActual = LocalDate.parse(sdf.format(today));
                long diasRestantes = ChronoUnit.DAYS.between(fechaActual, fechaVigenciaSistema);
                if (diasRestantes < 31) {
                    RESP += " swal({\n"
                            + "            allowOutsideClick: false,\n"
                            + "            allowEscapeKey: false,\n"
                            + "            title: 'Vigencia del sistema',\n"
                            + "            html: '<p>La póliza para el uso del sistema está por vencer<br><small><b>Para más información contacte a su asesor comercial</b></small></p><br><b>Días Restantes: " + diasRestantes + "</b>',\n"
                            + "            type: 'info'\n"
                            + "        });";
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(CCambiarContrasenia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RESP;
    }
}
