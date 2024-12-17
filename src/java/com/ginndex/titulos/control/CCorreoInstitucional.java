/*
    Document   : Controler de Correo Institucional
    Created on : 1/03/2023, 03:59:30 PM
    Author     : Ricardo Reyna

 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.CorreoInstitucional;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Usuario
 */
public class CCorreoInstitucional {

    HttpServletRequest request;
    HttpServletResponse response;
    CorreoInstitucional correoI;
    ResultSet rs;
    CConexion conexion;
    Bitacora bitacora;
    private String permisos;
    private String carpeta;

    public String getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(String carpeta) {
        this.carpeta = carpeta;
    }
    

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    public CConexionPool getConector() {
        return conector;
    }

    public void setConector(CConexionPool conector) {
        this.conector = conector;
    }

    public String getId_Usuario() {
        return Id_Usuario;
    }

    public void setId_Usuario(String Id_Usuario) {
        this.Id_Usuario = Id_Usuario;
    }
    CBitacora cBitacora;
    private CConexionPool conector;

    private String Id_Usuario;
    private String respuesta;
    private String opcion;

    public CorreoInstitucional getCorreoI() {
        return correoI;
    }

    public void setCorreoI(CorreoInstitucional correoI) {
        this.correoI = correoI;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
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

    public Bitacora getBitacora() {
        return bitacora;
    }

    public void setBitacora(Bitacora bitacora) {
        this.bitacora = bitacora;
    }

    public CBitacora getcBitacora() {
        return cBitacora;
    }

    public void setcBitacora(CBitacora cBitacora) {
        this.cBitacora = cBitacora;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String EstablecerAcciones() throws Exception {

        HttpSession sessionOk = request.getSession();
        this.Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        this.carpeta=  sessionOk.getAttribute("txtCarpeta").toString();
        
        String carp = getRequest().getSession().getAttribute("txtCarpeta") + "";
        
        System.out.println("CARPETA1 "+ carpeta);
        System.out.println("CARPETA2 "+carp);

        this.conector = new CConexionPool(request);
        this.conector.conexion = this.conector.dataSource.getConnection();

        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        setPermisos(cPermisos.obtenerPermisos("Correo Institucional"));

        try {
            this.opcion = request.getParameter("txtBandera");

            switch (opcion) {

                case "1": { //Consulta de correos
                    System.out.println("Usted eligió la opcion 1.");
                    this.respuesta = cargarCorreos();
                    break;
                }
                case "2": { //Comprobar Correo
                    System.out.println("Usted eligió la opcion 2.");
                    boolean si = enviarCorreo();
                    if (si) {
                        System.out.println("SE HA ENVIADO EL CORREO");
                        this.respuesta = ("success");
                    } else {
                        System.out.println("NO SE HA ENVIADO EL CORREO");
                        this.respuesta = ("error");
                    }
                    break;
                }

                case "3": { //Guardar Correo
                    System.out.println("Usted eligió la opcion 3.");
                    this.respuesta = insertarCorreoInstitucional();
                    break;
                }
                case "4": { //Modificar Correo
                    System.out.println("Usted eligió la opcion 4.");
                    this.respuesta = modificarCorreoInstitucional();
                    break;
                }
                case "5": { //Eliminar Correo
                    System.out.println("Usted eligió la opcion 5.");
                    this.respuesta = eliminarCorreoInstitucional();
                    break;
                }

                default: {
                    System.out.println("Opcion incorrecta");
                    this.respuesta = "";

                }
            }
            this.opcion = "0";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return respuesta;
    }

    private String cargarCorreos() throws SQLException, Exception {
        StringBuilder RESP = new StringBuilder();

        List<CorreoInstitucional> lstCorreos;
        conexion = new CConexion();
        conexion.setRequest(request);
        Connection con = null;
        PreparedStatement pstmt = null;
        rs = null;

        try {
            String Query = "SELECT * FROM CORREOS;";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);

            rs = pstmt.executeQuery();
            lstCorreos = new ArrayList<>();
            while (rs.next()) {
                correoI = new CorreoInstitucional();

                correoI.setId_Correo(rs.getInt("Id_Correo"));
                correoI.setCorreo(rs.getString("Correo"));
                correoI.setPuerto(rs.getString("Puerto"));
                correoI.setHost(rs.getString("Host"));
                correoI.setContrasena(rs.getString("Contrasena"));

                lstCorreos.add(correoI);
            }

            String cabecera = permisos.split("°")[0];
            if (!cabecera.equalsIgnoreCase("todos")) {
                permisos = permisos.split("°")[1];
            }

            RESP.append("success| <table class=\"table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed\" style=\"width:100%;\"  id=\"tblCorreoInstitucionals\">"
                    + "                                        <thead class=\"bg-primary-dark\" style=\"color: white;\">"
                    + "                                            <tr>"
                    + "                                                <th class=\"text-center\" style=\"display:none;\">IdCorreoInstitucional</th>"
                    + "                                                <th class=\"text-center\">Correo</th>"
                    + "                                                <th class=\"text-center\">Puerto</th>"
                    + "                                                <th class=\"text-center hidden-sm hidden-xs\">Host</th>"
                    + "                                                <th class=\"text-center hidden-sm hidden-xs\">Contraseña</th>");
            if (cabecera.equalsIgnoreCase("todos") || (permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[4].equalsIgnoreCase("1"))) {
                RESP.append("                                                <th class=\"text-center\" style=\"width: 10%\">Acciones</th>");
            }
            RESP.append("                                            </tr>"
                    + "                                        </thead>"
                    + "                                        <tbody id=\"tblBodyCorreoInstitucional\">");

            for (int i = 0; i < lstCorreos.size(); i++) {
                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display: none;' id='IdCorreo_").append(lstCorreos.get(i).getId_Correo()).append("'>").append(lstCorreos.get(i).getId_Correo()).append("</td>");
                RESP.append("<td class='text-center' id='correo_").append(lstCorreos.get(i).getId_Correo()).append("'>").append(lstCorreos.get(i).getCorreo()).append("</td>");
                RESP.append("<td class='text-center' id='Puerto_").append(lstCorreos.get(i).getId_Correo()).append("'>").append(lstCorreos.get(i).getPuerto()).append("</td>");
                RESP.append("<td class='text-center' id='Host_").append(lstCorreos.get(i).getId_Correo()).append("'>").append(lstCorreos.get(i).getHost()).append("</td>");
                RESP.append("<td class='text-center' id='Contra_").append(lstCorreos.get(i).getId_Correo()).append("'>").append(lstCorreos.get(i).getContrasena()).append("</td>");

                if (cabecera.equalsIgnoreCase("todos") || (permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[4].equalsIgnoreCase("1"))) {
                    RESP.append("<td class='text-center' id='Acciones_").append(lstCorreos.get(i).getId_Correo()).append("'>");

                    RESP.append("<div class='btn-group'>");

                    //Tiene acceso a modificar
                    if (cabecera.equalsIgnoreCase("todos") || (permisos.split("¬")[3].equalsIgnoreCase("1"))) {
                        RESP.append("<button class='btn btn-default btn-xs btnEditarCorreo' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Editar Correo' id='modificarCorreo_")
                                .append(lstCorreos.get(i).getId_Correo())
                                .append("'><i class='fa fa-pencil'></i></button>");
                    }
                    //tiene acceso a eliminar
                    if (cabecera.equalsIgnoreCase("todos") || (permisos.split("¬")[4].equalsIgnoreCase("1"))) {
                        RESP.append("<button class='js-swal-confirm btn btn-default btn-xs btnEliminarCorreo' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Eliminar' id='borrarCorreo_")
                                .append(lstCorreos.get(i).getId_Correo())
                                .append("'><i class='fa fa-times'></i></button>");
                    }
                    RESP.append("</div></td>");
                }

                RESP.append("</tr>");

            }

            RESP.append("</tbody>"
                    + "</table>");

        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de Correos Institucionales: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga los correos Institucionales: " + accion_catch(ex);
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

    public String insertarCorreoInstitucional() {
        String RESP = "";
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Enviar Correos");
        bitacora.setMovimiento("Inserción");
        try {

            String correoIns = request.getParameter("correoI");
            String contI = request.getParameter("contra");
            String port = request.getParameter("puerto");
            String Host = request.getParameter("host");

            String Query = "EXEC Add_CorreoInstitucional ?,?,?,?,?";
            cstmt = getConector().conexion.prepareCall(Query);
            cstmt.setString(1, correoIns);
            cstmt.setString(2, port);
            cstmt.setString(3, Host);
            cstmt.setString(4, contI);

            cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
            cstmt.execute();

            String respuestaSP = cstmt.getString(5);

            ResultSet rsRecord = null;
            //Si no devuelve nada el procedimiento almacenado, todo fue bien :D
            if (!cstmt.getMoreResults() && cstmt.getUpdateCount() != 1) {
                RESP = "success";
                bitacora.setInformacion("Registro de Correo");
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else if (cstmt.getMoreResults()) {
                rsRecord = cstmt.getResultSet();
                rsRecord.next();
                RESP = "error|"
                        + "<p>Error al insertar el Correo.</p><br>"
                        + "<small>El servidor devolvió el siguiente número de error desde SQL: <b>" + rsRecord.getString("ErrorNumber") + "</b></small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            } else {
                RESP = "error|"
                        + "<p>Error al insertar el Correo.</p><br>"
                        + "<small>El servidor no devolvió ningun estado de respuesta</small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar el registro del Correo: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar el registro del Correo: " + accion_catch(ex);
        } finally {
            try {
                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CCorreoInstitucional.class.getName()).log(Level.SEVERE, "Error al cerrar el PreparedStatement", ex);
            }
        }
        return RESP;
    }

    public String modificarCorreoInstitucional() {
        String RESP = "";
        CallableStatement cstmt = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Enviar Correos");
        bitacora.setMovimiento("Modificación");
        try {

            String correoIns = request.getParameter("correoI");
            String contI = request.getParameter("contra");
            String port = request.getParameter("puerto");
            String Host = request.getParameter("host");
            String IdCorreo = request.getParameter("id");

            String Query = "EXEC Upd_CorreoInstitucional ?,?,?,?,?,?";
            cstmt = getConector().conexion.prepareCall(Query);
            cstmt.setString(1, IdCorreo);
            cstmt.setString(2, correoIns);
            cstmt.setString(3, port);
            cstmt.setString(4, Host);
            cstmt.setString(5, contI);

            cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);
            cstmt.execute();

            String respuestaSP = cstmt.getString(6);

            ResultSet rsRecord = null;
            //Si no devuelve nada el procedimiento almacenado, todo fue bien :D
            if (!cstmt.getMoreResults() && cstmt.getUpdateCount() != 1) {
                RESP = "success";
                bitacora.setInformacion("Modificación de Correo");
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else if (cstmt.getMoreResults()) {
                rsRecord = cstmt.getResultSet();
                rsRecord.next();
                RESP = "error|"
                        + "<p>Error al modificar el Correo.</p><br>"
                        + "<small>El servidor devolvió el siguiente número de error desde SQL: <b>" + rsRecord.getString("ErrorNumber") + "</b></small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            } else {
                RESP = "error|"
                        + "<p>Error al modificar el Correo.</p><br>"
                        + "<small>El servidor no devolvió ningun estado de respuesta</small>"
                        + "<br><small>Contacta a soporte técnico para obtener más información.</small>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la modificación del Correo: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la modificación del Correo: " + accion_catch(ex);
        } finally {
            try {
                if (cstmt != null && !cstmt.isClosed()) {
                    cstmt.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CCorreoInstitucional.class.getName()).log(Level.SEVERE, "Error al cerrar el PreparedStatement", ex);
            }
        }
        return RESP;
    }

    public String eliminarCorreoInstitucional() {

        String IdCorreo = request.getParameter("id");

        String RESP = "error";
        CallableStatement cst = null;
        ResultSet rs = null;
        Connection conn = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Usuarios");
        bitacora.setMovimiento("Eliminación");
        try {
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            String Query = "EXEC Delt_CorreoInstitucional ?";
            cst = conn.prepareCall(Query);
            cst.setString(1, IdCorreo);
            cst.execute();
            rs = cst.getResultSet();
            if (rs == null) {
                if (cst.getUpdateCount() != -1) {
                    RESP = "success";
                    bitacora.setInformacion("Eliminación de correo: " + IdCorreo + "||Respuesta Método: " + RESP);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.setRequest(request);
                    cBitacora.addBitacoraGeneral();
                }
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la modificación del Correo: " + accion_catch(ex);
            Logger.getLogger(CCorreoInstitucional.class.getName()).log(Level.SEVERE, "¡Ocurrió un error interno en eliminarCorreoInstitucional()", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    RESP = "error|Error SQL al realizar la modificación del Correo: " + accion_catch(ex);
                    Logger.getLogger(CCorreoInstitucional.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el result set", ex);
                }
            }
            try {
                if (cst != null) {
                    if (!cst.isClosed()) {
                        cst.close();
                    }
                }
            } catch (SQLException ex) {
                RESP = "error|Error inesperado al realizar la modificación del Correo: " + accion_catch(ex);
                Logger.getLogger(CCorreoInstitucional.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el callable statement", ex);
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                RESP = "error|Error inesperado al realizar la modificación del Correo: " + accion_catch(ex);
                Logger.getLogger(CCorreoInstitucional.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    public boolean enviarCorreo() throws Exception {
        boolean respuesta = false;

        try {
            String destinatario = request.getParameter("correoD").trim();
            String correoIns = request.getParameter("correoI").trim();
            String contI = request.getParameter("contra".trim());
            String port = request.getParameter("puerto").trim();
            String Host = request.getParameter("host").trim();
            String mensajeRequest = request.getParameter("mensaje");

            String mensaje = "<body link=\"#00a5b5\" vlink=\"#00a5b5\" alink=\"#00a5b5\">\n"
                    + "	<table class=\" main contenttable\" align=\"center\" style=\"font-weight: normal;border-collapse: collapse;border: 0;margin-left: auto;margin-right: auto;padding: 0;font-family: Arial, sans-serif;color: #555559;background-color: white;font-size: 16px;line-height: 26px;width: 450px;\">\n"
                    + "		<tr>\n"
                    + "			<td class=\"border\" style=\"border-collapse: collapse;border: 1px solid #eeeff0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\">\n"
                    + "				<table style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;\">\n"
                    + "					<tr>\n"
                    + "						<td colspan=\"4\" valign=\"top\" class=\"image-section\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;background-color: #fff;border-bottom: 4px solid #00a5b5\">\n"
                    + "							<a href=\"https://digisep.com/"+carpeta+"/Vista/Generales/LogIn.jsp\"><img class=\"top-image\" src=\"https://digisep.com/images/Digi-Sep.png;\" alt=\"digi sep\" width=\"100\" height=\"100\"></a>\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td valign=\"top\" class=\"side title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 20px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;vertical-align: top;background-color: white;border-top: none;\">\n"
                    + "							<table style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;\">\n"
                    + "								<tr>\n"
                    + "									<td class=\"head-title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 28px;line-height: 34px;font-weight: bold; text-align: center;\">\n"
                    + "										<div class=\"mktEditable\" id=\"main_title\">\n"
                    + "											Envio de correo exitoso!\n"
                    + "										</div>\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"sub-title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;padding-top:5px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 18px;line-height: 29px;font-weight: bold;text-align: center;\">\n"
                    + "										<div class=\"mktEditable\" id=\"intro_title\">\n"
                    + "											Ya puede registrar su email para envios de correos!\n"
                    + "										</div>\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"top-padding\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 5px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\"></td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"grey-block\" style=\"border-collapse: collapse;border: 0;margin: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;background-color: #fff; text-align:center;\">\n"
                    + "										<div class=\"mktEditable\" id=\"cta\">\n"
                    + "											<img class=\"top-image\" src=\"https://tituloelectronicosep.com/wp-content/uploads/2019/04/proceso-para-titulos-electronicos-Digi-sep-1400x633.png\" width=\"180\" height=\"100\" /><br><br>\n"
                    + "                                                                                  <strong>Mensaje: </strong><p>" + mensajeRequest + "</p>"
                    + "										</div>\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"top-padding\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 15px 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 21px;\">\n"
                    + "										<hr size=\"1\" color=\"#eeeff0\">\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"text\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\">\n"
                    + "										<div class=\"mktEditable\" id=\"main_text\">\n"
                    + "											Mensaje informativo.\n"
                    + "										</div>\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "							</table>\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr bgcolor=\"#fff\" style=\"border-top: 4px solid #00a5b5;\">\n"
                    + "					</tr>\n"
                    + "				</table>\n"
                    + "</body>\n"
                    + "<style type='text/css'>"
                    + "@media only screen and (max-width: 600px) {\n"
                    + "		.main {\n"
                    + "			width: 320px !important;\n"
                    + "		}\n"
                    + "		.inside-footer {\n"
                    + "			width: 320px !important;\n"
                    + "		}\n"
                    + "		table[class=\"contenttable\"] { \n"
                    + "            width: 320px !important;\n"
                    + "            text-align: left !important;\n"
                    + "        }\n"
                    + "        td[class=\"force-col\"] {\n"
                    + "	        display: block !important;\n"
                    + "	    }\n"
                    + "	     td[class=\"rm-col\"] {\n"
                    + "	        display: none !important;\n"
                    + "	    }\n"
                    + "		.mt {\n"
                    + "			margin-top: 15px !important;\n"
                    + "		}\n"
                    + "		*[class].width300 {width: 255px !important;}\n"
                    + "		*[class].block {display:block !important;}\n"
                    + "		*[class].blockcol {display:none !important;}\n"
                    + "		.emailButton{\n"
                    + "            width: 100% !important;\n"
                    + "        }\n"
                    + "        .emailButton a {\n"
                    + "            display:block !important;\n"
                    + "            font-size:18px !important;\n"
                    + "        }\n"
                    + "	}"
                    + " </style>";

            Properties props = new Properties();

            props.put(
                    "mail.smtp.auth", "true");
            props.put(
                    "mail.smtp.starttls.enable", "true");
            props.put(
                    "mail.smtp.host", Host);
            props.put(
                    "mail.smtp.port", port);

            props.put("mail.smtp.ssl.trust", Host);

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoIns, contI);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(correoIns));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destinatario));
            message.setSubject("DIGI-SEP Validación de envio de correos");
            message.setContent(mensaje, "text/html; charset=utf-8");
            Transport.send(message);

            respuesta = true;
            //****************************************************** Fin metodo enviar correo *********************************************************
        } catch (Exception e) {
            respuesta = false;
            System.out.println("Excepcion en Enviar correo completo");
            throw new RuntimeException(e);
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
