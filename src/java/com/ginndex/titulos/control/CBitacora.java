/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Braulio Sorcia
 */
public class CBitacora {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private CConexion conexion;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private CallableStatement cstmt;
    private Bitacora bitacora;
    private List<Bitacora> lstBitacora;
    private String Id_Usuario;

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

    public CBitacora() {
    }

    public CBitacora(Bitacora b, Connection con) {
        bitacora = b;
        conn = con;
    }

    public CBitacora(Bitacora b) {
        bitacora = b;
    }

    public String establecerAccionesBitacoraGeneral() throws UnsupportedEncodingException, IOException, SQLException {

        String txtBandera = (request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");
        HttpSession sessionOk = request.getSession();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        String RESP = "";
        if (txtBandera.equalsIgnoreCase("1")) {
            RESP = consultarBitacoraGeneral() + "||" + consultarListaUsuario();
        } else if (txtBandera.equalsIgnoreCase("2")) {
            RESP = consultarBitacoraFiltro();
        } else if (txtBandera.equalsIgnoreCase("3")) {
            RESP = limpiarBitacora();
        }
        return RESP;
    }

    public String consultarBitacoraGeneral() {
        String RESP = "";
        try {
            String Query = "SELECT TOP 50 Id_Bitacora,Modulo,Movimiento,FechaMovimiento, Nombre, Apaterno, Amaterno "
                    + "FROM BitacoraGeneral bg JOIN Usuario_Persona_Intermedia upi ON bg.Id_Usuario = upi.Id_Usuario "
                    + "JOIN Persona p ON upi.Id_Persona = p.Id_Persona WHERE CONVERT(date,fechaMovimiento) like CONVERT (date,getDate())"
                    + " order by FechaMovimiento";
            RESP += "<table class=\"table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed\" style=\"width:100%;\"  id=\"tblBitacora\">";
            RESP += "<thead class=\"bg-primary-dark\" style=\"color: white;\">";
            RESP += "<tr>";
            RESP += "<th class=\"text-center\" style=\"display:none;\">IdBitácora</th>";
            RESP += "<th class=\"text-center\">Usuario</th>";
            RESP += "<th class=\"text-center\">Módulo</th>";
            RESP += "<th class=\"text-center\">Movimiento</th>";
            RESP += "<th class=\"text-center hidden-sm hidden-xs\">Fecha y Hora</th>";
            RESP += "</tr>";
            RESP += "</thead>";
            RESP += "<tbody>";
            conexion = new CConexion();
            conexion.setRequest(request);

            conn = conexion.GetconexionInSite();

            pstmt = conn.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.isBeforeFirst()) {
                lstBitacora = new ArrayList<Bitacora>();
                while (rs.next()) {
                    bitacora = new Bitacora();

                    bitacora.setId_Bitacora(rs.getString("Id_Bitacora"));
                    bitacora.setModulo(rs.getString("Modulo"));
                    bitacora.setMovimiento(rs.getString("Movimiento"));
                    bitacora.setFechaMovimiento(rs.getString("FechaMovimiento"));
                    bitacora.setId_Usuario(rs.getString("Nombre") + " " + rs.getString("Apaterno") + " " + (rs.getString("Amaterno") == null ? "" : rs.getString("Amaterno")));

                    lstBitacora.add(bitacora);
                }

                if (!lstBitacora.isEmpty()) {
                    for (Bitacora b : lstBitacora) {
                        RESP += "<tr>";
                        RESP += "<td class='text-center' style='display: none;' id='IdBitacora_'" + b.getId_Bitacora() + "'>" + b.getId_Bitacora() + "</td>";
                        RESP += "<td id='NombreUsuario_" + b.getId_Bitacora() + "'>" + b.getId_Usuario() + "</td>";
                        RESP += "<td id='Modulo_" + b.getId_Bitacora() + "'>" + b.getModulo() + "</td>";
                        RESP += "<td id='Movimiento_" + b.getId_Bitacora() + "'>" + b.getMovimiento() + "</td>";
                        RESP += "<td id='FechaHora_" + b.getId_Bitacora() + "' class='hidden-sm hidden-xs'>" + b.getFechaMovimiento() + "</td>";
                        RESP += "</tr>";
                    }
                }
            }
            RESP += "</tbody>";
            RESP += "</table>";
        } catch (SQLException ex) {

        }
        return RESP;
    }

    public String consultarBitacoraFiltro() {
        String RESP = "";
        try {
            String accion = request.getParameter("txtTipoFiltro");
            String fechaDesde = request.getParameter("txtFechaDesde");
            String fechaHasta = request.getParameter("txtFechaHasta");
            String usuario = request.getParameter("txtIdUsuario");

            RESP += "<table class=\"table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed\" style=\"width:100%;\"  id=\"tblBitacora\">";
            RESP += "<thead class=\"bg-primary-dark\" style=\"color: white;\">";
            RESP += "<tr>";
            RESP += "<th class=\"text-center\" style=\"display:none;\">IdBitácora</th>";
            RESP += "<th class=\"text-center\">Usuario</th>";
            RESP += "<th class=\"text-center\">Módulo</th>";
            RESP += "<th class=\"text-center\">Movimiento</th>";
            RESP += "<th class=\"text-center hidden-sm hidden-xs\">Fecha y Hora</th>";
            RESP += "</tr>";
            RESP += "</thead>";
            RESP += "<tbody>";

            String Query = "SET LANGUAGE 'español';\n SET DATEFORMAT 'dmy'\n";
            if (accion.equalsIgnoreCase("1")) {
                Query += "SELECT Id_Bitacora,Modulo,Movimiento,FechaMovimiento, Nombre, Apaterno, Amaterno "
                        + "FROM BitacoraGeneral bg JOIN Usuario_Persona_Intermedia upi ON bg.Id_Usuario = upi.Id_Usuario "
                        + "JOIN Persona p ON upi.Id_Persona = p.Id_Persona WHERE CAST(FechaMovimiento AS date) BETWEEN '" + fechaDesde + "' AND '" + fechaHasta + "'";
            } else if (accion.equalsIgnoreCase("2")) {
                Query += "SELECT Id_Bitacora,Modulo,Movimiento,FechaMovimiento, Nombre, Apaterno, Amaterno "
                        + "FROM BitacoraGeneral bg JOIN Usuario_Persona_Intermedia upi ON bg.Id_Usuario = upi.Id_Usuario "
                        + "JOIN Persona p ON upi.Id_Persona = p.Id_Persona WHERE bg.Id_Usuario = " + usuario;
            } else if (accion.equalsIgnoreCase("3")) {
                Query += "SELECT Id_Bitacora,Modulo,Movimiento,FechaMovimiento, Nombre, Apaterno, Amaterno "
                        + "FROM BitacoraGeneral bg JOIN Usuario_Persona_Intermedia upi ON bg.Id_Usuario = upi.Id_Usuario "
                        + "JOIN Persona p ON upi.Id_Persona = p.Id_Persona WHERE bg.Id_Usuario= " + usuario + " AND CAST(FechaMovimiento AS date) BETWEEN '" + fechaDesde + "' AND '" + fechaHasta + "'";
            }
            System.out.println(Query);
            conexion = new CConexion();
            conexion.setRequest(request);
            conn = conexion.GetconexionInSite();
            pstmt = conn.prepareStatement(Query);
            rs = pstmt.executeQuery();
            if (rs.isBeforeFirst()) {
                lstBitacora = new ArrayList<Bitacora>();
                while (rs.next()) {
                    bitacora = new Bitacora();

                    bitacora.setId_Bitacora(rs.getString("Id_Bitacora"));
                    bitacora.setModulo(rs.getString("Modulo"));
                    bitacora.setMovimiento(rs.getString("Movimiento"));
                    bitacora.setFechaMovimiento(rs.getString("FechaMovimiento"));
                    bitacora.setId_Usuario(rs.getString("Nombre") + " " + rs.getString("Apaterno") + " " + (rs.getString("Amaterno") == null ? "" : rs.getString("Amaterno")));

                    lstBitacora.add(bitacora);
                }

                if (!lstBitacora.isEmpty()) {
                    for (Bitacora b : lstBitacora) {
                        RESP += "<tr>";
                        RESP += "<td class='text-center' style='display: none;' id='IdBitacora_'" + b.getId_Bitacora() + "'>" + b.getId_Bitacora() + "</td>";
                        RESP += "<td id='NombreUsuario_" + b.getId_Bitacora() + "'>" + b.getId_Usuario() + "</td>";
                        RESP += "<td id='Modulo_" + b.getId_Bitacora() + "'>" + b.getModulo() + "</td>";
                        RESP += "<td id='Movimiento_" + b.getId_Bitacora() + "'>" + b.getMovimiento() + "</td>";
                        RESP += "<td id='FechaHora_" + b.getId_Bitacora() + "' class='hidden-sm hidden-xs'>" + b.getFechaMovimiento() + "</td>";
                        RESP += "</tr>";
                    }
                }
            }
            RESP += "</tbody>";
            RESP += "</table>";
        } catch (SQLException ex) {

        }
        return RESP;
    }

    public String addBitacoraGeneral() throws SQLException {

        String RESP = "";

        try {
            if (request != null && conn == null) {
                conexion = new CConexion();
                conexion.setRequest(request);
                conn = conexion.GetconexionInSite();
            }

            String Query = "{CALL Add_Bitacora ?,?,?,?}";
            cstmt = conn.prepareCall(Query);

            cstmt.setString(1, bitacora.getId_Usuario());
            cstmt.setString(2, bitacora.getModulo());
            cstmt.setString(3, bitacora.getMovimiento());
            cstmt.setString(4, bitacora.getInformacion());
            cstmt.execute();
            rs = cstmt.getResultSet();
            if (rs == null) {
                if (cstmt.getUpdateCount() != -1) {
                    RESP = "success";
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CBitacora.class.getName()).log(Level.SEVERE, "Error al insertar en bitácora general", ex);
            RESP = "error";
        } finally {
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }

            if (cstmt != null) {
                if (!cstmt.isClosed()) {
                    cstmt.close();
                }
            }

            if (rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }

            if (conexion != null) {
                if (!conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            }
        }

        return RESP;
    }

    public String limpiarBitacora() throws SQLException {
        String RESP = "";

        try {
            conexion = new CConexion();
            conexion.setRequest(request);
            conn = conexion.GetconexionInSite();
            String Query = "{CALL Delt_Bitacora ?}";
            cstmt = conn.prepareCall(Query);

            cstmt.setString(1, Id_Usuario);
            cstmt.execute();
            rs = cstmt.getResultSet();
            if (rs == null) {
                if (cstmt.getUpdateCount() != -1) {
                    RESP = "success";
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CBitacora.class.getName()).log(Level.SEVERE, "Error al insertar en bitácora general", ex);
            RESP = "error";
        } finally {
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
            if (cstmt != null) {
                if (!cstmt.isClosed()) {
                    cstmt.close();
                }
            }
            if (rs != null) {
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
            if (conexion != null) {
                if (!conexion.GetconexionInSite().isClosed()) {
                    conexion.GetconexionInSite().close();
                }
            }
        }
        return RESP;
    }

    public String consultarListaUsuario() {
        String RESP = "";
        pstmt = null;
        rs = null;
        conn = null;
        try {
            String Query = "SELECT Id_Usuario,nombreUsuario FROM USUARIO WHERE Estatus = 1";
            conexion = new CConexion();
            conexion.setRequest(request);
            conn = conexion.GetconexionInSite();
            pstmt = conn.prepareStatement(Query);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs.isBeforeFirst()) {
                RESP = "<option></option>";
                while (rs.next()) {
                    RESP += "<option value='" + rs.getString(1) + "'>" + rs.getString(2) + "</option>";
                }
            } else {
                RESP = "<option value='0'>No existen registros</option>";
            }
        } catch (SQLException ex) {

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
            if (conn != null) {
                try {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Objeto de conexión en el método cargarNotifyFirmantes() !", ex);
                }
            }
        }
        return RESP;
    }

}
