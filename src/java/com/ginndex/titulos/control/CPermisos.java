/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Braulio Sorcia
 */
public class CPermisos {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private CConexionPool conector;
    private String idUsuario;
    private String Bandera;

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

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getBandera() {
        return Bandera;
    }

    public void setBandera(String Bandera) {
        this.Bandera = Bandera;
    }

    public String establecerAcciones() {
        String RESP = "";
        try {
            HttpServletRequest requestProvisional = getRequest();
            requestProvisional.setCharacterEncoding("UTF-8");
            String nombreModulo = request.getParameter("txtModulo");
            RESP = obtenerPermisos(nombreModulo) + "~" + consultarVigencia();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CPermisos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RESP;
    }

    public String obtenerPermisos(String nombreModulo) {

        String RESP = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            String estPredeterminado = request.getSession().getAttribute("EstatusPredeterminado").toString();
            if (estPredeterminado.equalsIgnoreCase("1")) {
                RESP = "todos°";
            } else {
                CConexion cConexion = new CConexion();
                cConexion.setRequest(request);
                conn = cConexion.GetconexionInSite();
                String Query = "SELECT rpi.* FROM Roles_Permiso_Intermedia rpi \n"
                        + "                  JOIN Catalogo_Permisos cp ON rpi.Id_Permiso = cp.Id_CatalogoP \n"
                        + "                  WHERE (cp.NombrePermiso = ?";
                Query = (nombreModulo.equalsIgnoreCase("usuarios") ? Query + " OR cp.NombrePermiso = 'Roles') AND rpi.Id_TipoRol = ? " : Query + ") AND rpi.Id_TipoRol = ?");
                String idTipoRol = request.getSession().getAttribute("Id_TipoRol").toString();
                ps = conn.prepareStatement(Query);
                ps.setString(1, nombreModulo);
                ps.setString(2, idTipoRol);
                ps.execute();
                rs = ps.getResultSet();
                RESP = "acceso°";
                while (rs.next()) {
                    for (int i = 4; i < 12; i++) {
                        RESP += (rs.getString(i) == null ? "0" : rs.getString(i)) + "¬";
                    }
                }
            }
        } catch (SQLException ex) {
            RESP = "error¬";
            Logger.getLogger(CPermisos.class.getName()).log(Level.SEVERE, "Error al consultar permisos", ex);
        } finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP.substring(0, RESP.length() - 1);
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
            String Query = "SELECT * FROM Vigencia_Polizas WHERE Activo = 1";

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
            Logger.getLogger(CCalificaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RESP;
    }
}
