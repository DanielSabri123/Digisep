/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Paulina
 */
public class CCambiarContrasenia {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String Id_Usuario;
    Bitacora bitacora;
    CBitacora cBitacora;

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

    public String getId_Usuario() {
        return Id_Usuario;
    }

    public void setId_Usuario(String Id_Usuario) {
        this.Id_Usuario = Id_Usuario;
    }

    public String establecerAcciones() {
        String RESP = "";
        try {
            String txtBandera = "";
            HttpServletRequest requestProvisional = getRequest();
            requestProvisional.setCharacterEncoding("UTF-8");
            HttpSession sessionOk = request.getSession();
            txtBandera = request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera");
            Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
            if (txtBandera.equalsIgnoreCase("1")) {
                RESP = cambiarContrasenia();
            }

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CCambiarContrasenia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RESP;
    }

    public String cambiarContrasenia() {
        String RESP = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            String Query = "SELECT * FROM USUARIO WHERE ID_USUARIO = ?";
            String contraseniaActual = request.getParameter("txtContraseniaActual");
            String contraseniaNueva = request.getParameter("txtNuevaContrasenia");
            String contrasena = "";
            ps = conn.prepareStatement(Query);
            ps.setString(1, Id_Usuario);
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                contrasena = rs.getString("contrasena");
            }
            if (!contrasena.equalsIgnoreCase("") && contraseniaActual != null && contraseniaNueva != null) {
                if (contrasena.equalsIgnoreCase(contraseniaActual)) {
                    Query = "UPDATE USUARIO SET CONTRASENA= ? WHERE ID_USUARIO = ?";
                    ps = conn.prepareStatement(Query);
                    ps.setString(1, contraseniaNueva.trim());
                    ps.setString(2, Id_Usuario);
                    ps.execute();
                    rs = ps.getResultSet();
                    if (rs == null && ps.getUpdateCount() != -1) {
                        RESP = "success";
                    }
                } else {
                    RESP = "noIgual";
                }
            } else {
                RESP = "error";
            }
            bitacora = new Bitacora();
            bitacora.setId_Usuario(Id_Usuario);
            bitacora.setModulo("Cambio de Contraseña");
            bitacora.setMovimiento("Actualización");
            bitacora.setInformacion("Actualización de contraseña||Respuesta Método: " + RESP);
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error interno en cambiarContrasenia()", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el result set", ex);
                }
            }
            try {
                if (ps != null) {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el prepared statement", ex);
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el objeto conexión", ex);
            }
        }

        return RESP;
    }
}
