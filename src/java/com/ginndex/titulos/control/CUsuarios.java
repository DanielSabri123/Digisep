/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Usuario;
import com.ginndex.titulos.modelo.Persona;
import com.ginndex.titulos.modelo.Roles;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Braulio Sorcia
 */
public class CUsuarios {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Usuario usuarios;
    private Persona persona;
    private Roles roles;
    private String Id_Usuario;
    private String Bandera;
    private List<Usuario> lstUsuario;
    private List<Persona> lstPersona;
    private List<Roles> lstRoles;
    private String permisos;
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

    public Usuario getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuario usuarios) {
        this.usuarios = usuarios;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public String getId_Usuario() {
        return Id_Usuario;
    }

    public void setId_Usuario(String Id_Usuario) {
        this.Id_Usuario = Id_Usuario;
    }

    public String getBandera() {
        return Bandera;
    }

    public void setBandera(String Bandera) {
        this.Bandera = Bandera;
    }

    public List<Usuario> getLstUsuario() {
        return lstUsuario;
    }

    public void setLstUsuario(List<Usuario> lstUsuario) {
        this.lstUsuario = lstUsuario;
    }

    public List<Persona> getLstPersona() {
        return lstPersona;
    }

    public void setLstPersona(List<Persona> lstPersona) {
        this.lstPersona = lstPersona;
    }

    public List<Roles> getLstRoles() {
        return lstRoles;
    }

    public void setLstRoles(List<Roles> lstRoles) {
        this.lstRoles = lstRoles;
    }

    public CUsuarios() {
        persona = new Persona();
        roles = new Roles();
        usuarios = new Usuario();
    }

    public String establecerAcciones() {
        String RESP = "";
        try {
            HttpServletRequest requestProvisional = getRequest();
            setBandera(request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
            requestProvisional.setCharacterEncoding("UTF-8");
            HttpSession sessionOk = request.getSession();
            Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();

            CPermisos cPermisos = new CPermisos();
            cPermisos.setRequest(request);
            permisos = cPermisos.obtenerPermisos("Usuarios");

            if (getBandera().equalsIgnoreCase("0")) {
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
                RESP = consultarTablaUsuarios() + "°" + consultarTablaRoles();
            } else if (getBandera().equalsIgnoreCase("2")) {
                String IdUsuario = requestProvisional.getParameter("IdUsuario");
                RESP = eliminarUsuario(IdUsuario);
            } else if (getBandera().equalsIgnoreCase("3")) {
                String IdUsuario = requestProvisional.getParameter("IdUsuario");
                RESP = consultarUsuarioEspecifico(IdUsuario);
            } else if (getBandera().equalsIgnoreCase("4")) {
                RESP = insertarUsuario();
            } else if (getBandera().equalsIgnoreCase("5")) {
                RESP = actualizarUsuario();
            } else if (getBandera().equalsIgnoreCase("6")) {
                RESP = consultarListaRoles();
            } else if (getBandera().equalsIgnoreCase("7")) {
                String nombreRol = requestProvisional.getParameter("txtNombreRol");
                String descripRol = requestProvisional.getParameter("txtDescripRol");
                getRoles().setNombreTipo(nombreRol);
                getRoles().setDescripcion(descripRol);
                RESP = insertarRol();
            } else if (getBandera().equalsIgnoreCase("8")) {
                String idRol = requestProvisional.getParameter("idRol");
                String nombreRol = requestProvisional.getParameter("txtNombreRol");
                String descripRol = requestProvisional.getParameter("txtDescripRol");
                getRoles().setId_TipoRol(idRol);
                getRoles().setNombreTipo(nombreRol);
                getRoles().setDescripcion(descripRol);
                RESP = actualizarRol();
            } else if (getBandera().equalsIgnoreCase("9")) {
                String idRol = requestProvisional.getParameter("idRol");
                getRoles().setId_TipoRol(idRol);
                RESP = eliminarRol();
            } else if (getBandera().equalsIgnoreCase("10")) {
                String idRol = requestProvisional.getParameter("idRol");
                getRoles().setId_TipoRol(idRol);
                RESP = consultarRolEspecifico();
            } else if (getBandera().equalsIgnoreCase("11")) {
                RESP = consultarCatalogoRoles();
            }
        } catch (Exception e) {

        }
        return RESP;
    }

    public String getDat(List<FileItem> partes) throws Exception {
        String Bandera = "";
        String input_actual = "";
        for (FileItem item : partes) {
            try {
                FileItem uploaded = null;
                uploaded = (FileItem) item;
                input_actual = uploaded.getFieldName();
                if (input_actual.equalsIgnoreCase("bandera")) {
                    Bandera = uploaded.getString("UTF-8");
                } else if (input_actual.equalsIgnoreCase("IdUsuario")) {
                    int aux = (uploaded.getString("UTF-8").equalsIgnoreCase("") || uploaded.getString("UTF-8") == null ? 0 : Integer.parseInt(uploaded.getString("UTF-8")));
                    getUsuarios().setId_Usuario(aux);
                } else if (input_actual.equalsIgnoreCase("IdPersona")) {
                    getPersona().setId_Persona(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("txtNombreUsuario")) {
                    getPersona().setNombre(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("txtApaternoUsuario")) {
                    getPersona().setAPaterno(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("txtAmaternoUsuario")) {
                    getPersona().setAMaterno(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("txCorreoUsuario")) {
                    getPersona().setEmail(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("lstSexoUsuario")) {
                    getPersona().setSexo(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("lstTipoUsuario")) {
                    getRoles().setId_TipoRol(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("txtUsuario")) {
                    getUsuarios().setNombreUsuario(uploaded.getString("UTF-8"));
                } else if (input_actual.equalsIgnoreCase("txtPasswordUsuario")) {
                    getUsuarios().setContrasena(uploaded.getString("UTF-8"));
                    break;
                }

            } catch (UnsupportedEncodingException | NumberFormatException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al momento de recuperar la información desde el form!", ex);
            }
        }
        return Bandera;
    }

    public String consultarTablaUsuarios() {

        StringBuilder RESP = new StringBuilder();
        ResultSet rsRecord = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            String Query = " SELECT Usu.Id_Usuario,\n"
                    + "Per.Id_Persona,\n"
                    + "Rol.Id_TipoRol,\n"
                    + "Per.Nombre,\n"
                    + "Per.APaterno,\n"
                    + "Per.AMaterno,\n"
                    + "Usu.NombreUsuario,\n"
                    + "Usu.Contrasena,\n"
                    + "Usu.Estatus_Predeterminado,\n"
                    + "Rol.NombreTipo,\n"
                    + "Usu.Estatus FROM Persona AS Per\n"
                    + "join Usuario_Persona_Intermedia AS UPI ON Per.Id_Persona = UPI.Id_Persona\n"
                    + "join Usuario AS Usu ON Usu.Id_Usuario = UPI.Id_Usuario\n"
                    + "join Roles AS Rol ON Rol.Id_TipoRol = (SELECT Id_TipoRol FROM Roles_Usuario_Intermedia WHERE Id_Usuario = Usu.Id_Usuario )\n"
                    + "WHERE Usu.Estatus = 1 and Usu.Tipo = 'Admin' and Rol.NombreTipo <> 'Admin' and Usu.Estatus_Predeterminado = 0 ";

            lstPersona = new ArrayList<Persona>();
            lstUsuario = new ArrayList<Usuario>();
            lstRoles = new ArrayList<Roles>();

            ps = conn.prepareStatement(Query);
            ps.execute();
            rsRecord = ps.getResultSet();

            //ENCABEZADOS DE LA TABLA
            RESP.append("<table class=\"table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed\" style=\"width:100%;\"  id=\"tblUsuarios\">");
            RESP.append("<thead class=\"bg-primary-dark\" style=\"color: white;\">");
            RESP.append("<tr>");
            RESP.append("<th class=\"text-center\" style=\"display:none;\">IdUsuario</th>");
            RESP.append("<th class=\"text-center\" style=\"display:none;\">IdPersona</th>");
            RESP.append("<th class=\"text-center\" style=\"display:none;\">IdTipoRol</th>");
            RESP.append("<th class=\"text-center\">Usuario</th>");
            RESP.append("<th class=\"text-center\">Rol</th>");
            RESP.append("<th class=\"text-center hidden-xs \">Nombre del personal</th>");
            RESP.append("<th class=\"text-center thAcciones\">Acciones</th>");
            RESP.append("</tr>");
            RESP.append("</thead>");
            RESP.append("<tbody>");

            while (rsRecord.next()) {
                persona = new Persona();
                usuarios = new Usuario();
                roles = new Roles();

                //LLENAMOS A PERSONA
                persona.setId_Persona(rsRecord.getString("Id_Persona"));
                persona.setNombre(rsRecord.getString("Nombre"));
                persona.setAPaterno(rsRecord.getString("APaterno"));
                persona.setAMaterno(rsRecord.getString("AMaterno"));

                //LLENAMOS A USUARIO
                usuarios.setId_Usuario(rsRecord.getInt("Id_Usuario"));
                usuarios.setNombreUsuario(rsRecord.getString("NombreUsuario"));
                usuarios.setContrasena(rsRecord.getString("Contrasena"));
                usuarios.setEstatus(rsRecord.getString("Estatus"));

                //LLENAMOS A ROLES
                roles.setId_TipoRol(rsRecord.getString("Id_TipoRol"));
                roles.setNombreTipo(rsRecord.getString("NombreTipo"));

                lstPersona.add(persona);
                lstUsuario.add(usuarios);
                lstRoles.add(roles);

            }
            String cabecera = permisos.split("°")[0];
            if (!cabecera.equalsIgnoreCase("todos")) {
                permisos = permisos.split("°")[1];
            }
            for (int i = 0; i < lstUsuario.size(); i++) {
                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display: none;' id='IdUsuario_'").append(lstUsuario.get(i).getId_Usuario()).append("'>").append(lstUsuario.get(i).getId_Usuario()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='IdPersona_'").append(lstUsuario.get(i).getId_Usuario()).append("'>").append(lstPersona.get(i).getId_Persona()).append("</td>");
                RESP.append("<td class='text-center' style='display: none;' id='IdTipoRol_'").append(lstUsuario.get(i).getId_Usuario()).append("'>").append(lstRoles.get(i).getId_TipoRol()).append("</td>");
                RESP.append("<td class='text-center' id='NombreUsuario_'").append(lstUsuario.get(i).getId_Usuario()).append("'>").append(lstUsuario.get(i).getNombreUsuario()).append("</td>");
                RESP.append("<td class='text-center' id='TipoRol_'").append(lstUsuario.get(i).getId_Usuario()).append("'>").append(lstRoles.get(i).getNombreTipo()).append("</td>");
                RESP.append("<td class='text-center hidden-xs' id='NombrePersona_'").append(lstUsuario.get(i).getId_Usuario()).append("'>")
                        .append(lstPersona.get(i).getNombre())
                        .append(" ")
                        .append(lstPersona.get(i).getAPaterno())
                        .append(" ")
                        .append(lstPersona.get(i).getAMaterno())
                        .append("</td>");
                RESP.append("<td class='text-center thAcciones' id='Acciones_").append(lstUsuario.get(i).getId_Usuario()).append("'>");
                if (cabecera.contains("todos")) {
                    RESP.append("<div class='btn-group'>");
                    RESP.append("<button class='btn btn-default btn-xs btnConsultarUsuario' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Consultar' id='lookUsuario_")
                            .append(lstUsuario.get(i).getId_Usuario())
                            .append("'><i class='fa fa-search'></i></button><button class='btn btn-default btn-xs btnEditarUsuario' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Editar' id='modifyUsuario_")
                            .append(lstUsuario.get(i).getId_Usuario())
                            .append("'><i class='fa fa-pencil'></i></button><button class='js-swal-confirm btn btn-default btn-xs btnEliminarUsuario' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Eliminar' id='deleteUsuario_")
                            .append(lstUsuario.get(i).getId_Usuario())
                            .append("'><i class='fa fa-times'></i></button></div>");
                } else if (cabecera.contains("acceso")) {
                    if (permisos.split("¬")[2].equalsIgnoreCase("1") || permisos.split("¬")[3].equalsIgnoreCase("1") || permisos.split("¬")[4].equalsIgnoreCase("1")) {
                        RESP.append("<div class='btn-group'>");
                        if (permisos.split("¬")[2].equalsIgnoreCase("1")) {
                            RESP.append("<button class='btn btn-default btn-xs btnConsultarUsuario' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Consultar' id='lookUsuario_")
                                    .append(lstUsuario.get(i).getId_Usuario())
                                    .append("'><i class='fa fa-search'></i></button>");
                        }
                        if (permisos.split("¬")[3].equalsIgnoreCase("1")) {
                            RESP.append("<button class='btn btn-default btn-xs btnEditarUsuario' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Editar' id='modifyUsuario_")
                                    .append(lstUsuario.get(i).getId_Usuario())
                                    .append("'><i class='fa fa-pencil'></i></button>");
                        }
                        if (permisos.split("¬")[4].equalsIgnoreCase("1")) {
                            RESP.append("<button class='js-swal-confirm btn btn-default btn-xs btnEliminarUsuario' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Eliminar' id='deleteUsuario_")
                                    .append(lstUsuario.get(i).getId_Usuario())
                                    .append("'><i class='fa fa-times'></i></button>");
                        }
                        RESP.append("</div>");
                    } else {
                        RESP.append("<span class='label label-danger'>No disponible</span>");
                    }
                }
                RESP.append("</td></tr>");
            }
            RESP.append("</tbody>");
            RESP.append("</table>");

        } catch (SQLException e) {

        } finally {
            try {
                if (rsRecord != null && !rsRecord.isClosed()) {
                    rsRecord.close();
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

        return RESP.toString();
    }

    public String eliminarUsuario(String idUsuario) {

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
            String Query = "EXEC Delt_Usuario ?";
            cst = conn.prepareCall(Query);
            cst.setString(1, idUsuario);
            cst.execute();
            rs = cst.getResultSet();
            if (rs == null) {
                if (cst.getUpdateCount() != -1) {
                    RESP = "success";
                    bitacora.setInformacion("Eliminación de usuario: " + idUsuario + "||Respuesta Método: " + RESP);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.setRequest(request);
                    cBitacora.addBitacoraGeneral();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error interno en eliminarUsuario()", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el result set", ex);
                }
            }
            try {
                if (cst != null) {
                    if (!cst.isClosed()) {
                        cst.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el callable statement", ex);
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    public String consultarUsuarioEspecifico(String IdUsuario) {

        StringBuilder RESP = new StringBuilder();
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            String Query = "";

            Query = "SELECT p.Id_Persona, p.Nombre, p.APaterno, p.AMaterno, p.Email, p.Sexo, u.Id_Usuario,u.NombreUsuario, u.Contrasena, r.Id_TipoRol\n"
                    + "FROM Persona p JOIN Usuario_Persona_Intermedia upi ON upi.Id_Persona = p.Id_Persona \n"
                    + "               JOIN  Usuario u ON upi.Id_Usuario = u.Id_Usuario\n"
                    + "               JOIN Roles_Usuario_Intermedia rui ON u.Id_Usuario = rui.Id_Usuario\n"
                    + "               JOIN Roles r ON rui.Id_TipoRol = r.Id_TipoRol\n"
                    + "WHERE u.Id_Usuario = ?";

            ps = conn.prepareStatement(Query);
            ps.setString(1, IdUsuario);
            ps.execute();
            rs = ps.getResultSet();

            if (rs.next()) {
                RESP.append("success").append("°");
                RESP.append(rs.getString("Id_Persona")).append("¬");
                RESP.append(rs.getString("nombre")).append("¬");
                RESP.append(rs.getString("aPaterno")).append("¬");
                RESP.append(rs.getString("aMaterno")).append("¬");
                RESP.append(rs.getString("Email")).append("¬");
                RESP.append(rs.getString("sexo")).append("¬");
                RESP.append(rs.getString("Id_Usuario")).append("¬");
                RESP.append(rs.getString("NombreUsuario")).append("¬");
                RESP.append(rs.getString("Contrasena")).append("¬");
                RESP.append(rs.getString("Id_TipoRol"));
            } else {
                RESP.append("error");
            }

        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al consultar el usuario!", ex);
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en consultarUsuarioEspecifico()", ex);
                }
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

        return RESP.toString();
    }

    public String insertarUsuario() {
        String RESP = "error";
        CallableStatement cst = null;
        Connection conn = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Usuarios");
        bitacora.setMovimiento("Inserción");
        try {
            if (!validarCorreoElectronico(false)) {
                if (!validarUserNameExisencia(false)) {
                    CConexion cConexion = new CConexion();
                    cConexion.setRequest(request);
                    conn = cConexion.GetconexionInSite();
                    String Query = "EXEC Add_Usuario ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
                    cst = conn.prepareCall(Query);
                    cst.setString(1, getPersona().getNombre());
                    cst.setString(2, getPersona().getAPaterno());
                    cst.setString(3, getPersona().getAMaterno());
                    cst.setString(4, getPersona().getEmail());
                    cst.setString(5, getPersona().getSexo());
                    cst.setString(6, getRoles().getId_TipoRol());
                    cst.setString(7, getUsuarios().getNombreUsuario());
                    cst.setString(8, getUsuarios().getContrasena());
                    cst.setInt(9, Integer.valueOf(Id_Usuario));
                    cst.registerOutParameter(10, java.sql.Types.VARCHAR);
                    cst.execute();
                    String aux = cst.getString(10);

                    if (aux != null) {
                        RESP = "success";
                        bitacora.setInformacion("Inserción de usuario: " + aux + "||Respuesta Método: " + RESP);
                        cBitacora = new CBitacora(bitacora);
                        cBitacora.setRequest(request);
                        cBitacora.addBitacoraGeneral();
                    }
                } else {
                    RESP = "usuario";
                }
            } else {
                RESP = "email";
            }

        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al ejecutar el Callable statment en insertarUsuario()", ex);
        } finally {
            try {
                if (cst != null) {
                    if (!cst.isClosed()) {
                        cst.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el callable statement", ex);
            }

            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    public String actualizarUsuario() {
        String RESP = "error";
        CallableStatement cst = null;
        Connection conn = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Usuarios");
        bitacora.setMovimiento("Actualizar");
        try {
            if (!validarCorreoElectronico(true)) {
                if (!validarUserNameExisencia(true)) {
                    CConexion cConexion = new CConexion();
                    cConexion.setRequest(request);
                    conn = cConexion.GetconexionInSite();
                    String Query = "EXEC Upd_Usuario ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
                    cst = conn.prepareCall(Query);
                    cst.setString(1, getPersona().getId_Persona());
                    cst.setInt(2, getUsuarios().getId_Usuario());
                    cst.setString(3, getPersona().getNombre());
                    cst.setString(4, getPersona().getAPaterno());
                    cst.setString(5, getPersona().getAMaterno());
                    cst.setString(6, getPersona().getEmail());
                    cst.setString(7, getPersona().getSexo());
                    cst.setString(8, getRoles().getId_TipoRol());
                    cst.setString(9, getUsuarios().getNombreUsuario());
                    cst.setString(10, getUsuarios().getContrasena());
                    cst.execute();
                    if (cst.getResultSet() == null) {
                        if (cst.getUpdateCount() != -1) {
                            RESP = "success";
                            bitacora.setInformacion("Actualización de usuario||Respuesta Método: " + RESP);
                            cBitacora = new CBitacora(bitacora);
                            cBitacora.setRequest(request);
                            cBitacora.addBitacoraGeneral();
                        }
                    }
                } else {
                    RESP = "usuario";
                }
            } else {
                RESP = "email";
            }

        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al ejecutar el Callable statment en actualizarUsuario()", ex);
        } finally {
            try {
                if (cst != null) {
                    if (!cst.isClosed()) {
                        cst.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el callable statement en actualizarUsuario()", ex);
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    public String consultarListaRoles() {
        StringBuilder RESP = new StringBuilder();
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            String Query = "SELECT * FROM ROLES WHERE NombreTipo<>'Admin' AND estatus = 1";
            RESP.append("<option value=''></option>");
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                RESP.append("<option value='").append(rs.getString("Id_TipoRol")).append("'>").append(rs.getString("nombreTipo")).append("</option>");
            }
        } catch (Exception ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al consultar los roles!", ex);
            RESP.append("<option value=''>¡Ocurrió un error!</option>");
            String consultarListaRoles = consultarListaRoles();
            return consultarListaRoles;
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en consultarListaRoles()", ex);
                }
            }
            if (ps != null) {
                try {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en consultarListaRoles()", ex);
                }
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP.toString();
    }

    public String consultarTablaRoles() {
        StringBuilder RESP = new StringBuilder();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        lstRoles = new ArrayList<Roles>();
        try {
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            String Query = "SELECT * FROM ROLES WHERE NombreTipo<>'Admin' AND estatus = 1";
            RESP.append("<table class=\"table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed1\" style=\"width:100%;\"  id=\"tblRoles\">");
            RESP.append("<thead class=\"bg-primary-dark\" style=\"color: white;\">");
            RESP.append("<tr>");
            RESP.append("<th class=\"text-center\" style=\"display:none;\">IdRol</th>");
            RESP.append("<th class=\"text-center\">Tipo Rol</th>");
            RESP.append("<th class=\"text-center hidden-xs\">Descripción</th>");
            RESP.append("<th class=\"text-center thAcciones\">Acciones</th>");
            RESP.append("</tr>");
            RESP.append("</thead>");
            RESP.append("<tbody>");
            ps = conn.prepareStatement(Query);
            ps.execute();
            rs = ps.getResultSet();
            while (rs.next()) {
                Roles r = new Roles();

                r.setId_TipoRol(rs.getString("id_TipoRol"));
                r.setNombreTipo(rs.getString("NombreTipo"));
                r.setDescripcion(rs.getString("descripcion"));
                lstRoles.add(r);
            }

            for (Roles r : lstRoles) {
                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display: none;' id='IdRol_'").append(r.getId_TipoRol()).append("'>").append(r.getId_TipoRol()).append("</td>");
                RESP.append("<td class='text-center' id='NombreRol_'").append(r.getId_TipoRol()).append("'>").append(r.getNombreTipo()).append("</td>");
                RESP.append("<td class='text-center hidden-xs' id='DescripRol_'").append(r.getId_TipoRol()).append("'>").append(r.getDescripcion()).append("</td>");
                RESP.append("<td class='text-center thAcciones' id='Acciones_").append(r.getId_TipoRol()).append("'>");
                if (permisos.contains("todos")) {
                    RESP.append("<div class='btn-group'>");
                    RESP.append("<button class='btn btn-default btn-xs btnConsultarRol' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Consultar' id='lookRol_")
                            .append(r.getId_TipoRol())
                            .append("'><i class='fa fa-search'></i></button><button class='btn btn-default btn-xs btnEditarRol' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Editar' id='modifyRol_")
                            .append(r.getId_TipoRol())
                            .append("'><i class='fa fa-pencil'></i></button><button class='js-swal-confirm btn btn-default btn-xs btnEliminarRol' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Eliminar' id='deleteRol_")
                            .append(r.getId_TipoRol())
                            .append("'><i class='fa fa-times'></i></button></div>");
                } else if (permisos.split("¬")[10].equalsIgnoreCase("1") || permisos.split("¬")[11].equalsIgnoreCase("1") || permisos.split("¬")[12].equalsIgnoreCase("1")) {
                    RESP.append("<div class='btn-group'>");
                    if (permisos.split("¬")[10].equalsIgnoreCase("1")) {
                        RESP.append("<button class='btn btn-default btn-xs btnConsultarRol' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Consultar' id='lookRol_")
                                .append(r.getId_TipoRol())
                                .append("'><i class='fa fa-search'></i></button>");
                    }
                    if (permisos.split("¬")[11].equalsIgnoreCase("1")) {
                        RESP.append("<button class='btn btn-default btn-xs btnEditarRol' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Editar' id='modifyRol_")
                                .append(r.getId_TipoRol())
                                .append("'><i class='fa fa-pencil'></i></button>");
                    }
                    if (permisos.split("¬")[12].equalsIgnoreCase("1")) {
                        RESP.append("<button class='js-swal-confirm btn btn-default btn-xs btnEliminarRol' type='button'  data-container='body' type='button' data-toggle='tooltip' data-original-title='Eliminar' id='deleteRol_")
                                .append(r.getId_TipoRol())
                                .append("'><i class='fa fa-times'></i></button>");
                    }
                    RESP.append("</div>");
                } else {
                    RESP.append("<span class='label label-danger'>No disponible</span>");
                }
                RESP.append("</td></tr>");
            }
            RESP.append("</tbody>");
            RESP.append("</table>");

        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al momento de consultar la tabla de roles!", ex);
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en consultarTablaRoles()", ex);
                }
            }
            if (ps != null) {
                try {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en consultarTablaRoles()", ex);
                }
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el obj Connection en consultarTablaRoles()", ex);
            }
        }

        return RESP.toString();
    }

    public String insertarRol() {
        String RESP = "error";
        CallableStatement cst = null;
        PreparedStatement ps = null;
        Connection conn = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Roles");
        bitacora.setMovimiento("Inserción");
        try {
            if (!validarTipoRolExistencia(false)) {
                String Query = "{call Add_Rol ?,?,?}";
                CConexion cConexion = new CConexion();
                cConexion.setRequest(request);
                conn = cConexion.GetconexionInSite();
                cst = conn.prepareCall(Query);
                cst.setString(1, getRoles().getNombreTipo());
                cst.setString(2, getRoles().getDescripcion());
                cst.registerOutParameter(3, java.sql.Types.VARCHAR);
                cst.execute();
                String idRol = cst.getString(3);
                if (idRol != null) {
                    String cadena_rol_seleccion = request.getParameter("txtCadenaIdRoles");
                    String[] cadenaIds = cadena_rol_seleccion.split("\\*");
                    for (String cadenaId : cadenaIds) {
                        Query = "INSERT INTO Roles_Permiso_Intermedia VALUES (?,?,?,?,?,?,?,?,?,?)";
                        ps = conn.prepareStatement(Query);
                        ps.setString(1, idRol);
                        String ids = cadenaId.split("\\^")[0];
                        ps.setString(2, ids);
                        String[] permisos = cadenaId.split("\\^")[1].split("¬");
                        int aux = 0;
                        for (int i = 1; i < 9; i++) {
                            if (aux != permisos.length) {
                                if (("Permiso" + i).equalsIgnoreCase("Permiso" + permisos[aux].split("°")[0])) {
                                    ps.setInt((i + 2), Integer.parseInt(permisos[aux].split("°")[1]));
                                    aux++;
                                } else {
                                    ps.setInt((i + 2), 0);
                                }
                            } else {
                                ps.setInt((i + 2), 0);
                            }
                        }
                        ps.execute();
                    }
                    RESP = "success";
                    Query = "{call Upd_Rol ?,?,?}";
                    cst = conn.prepareCall(Query);
                    cst.setString(1, idRol);
                    cst.setString(2, getRoles().getNombreTipo());
                    cst.setString(3, getRoles().getDescripcion());
                    cst.execute();
                    bitacora.setInformacion("Inserción de rol||Respuesta Método: " + RESP);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.setRequest(request);
                    cBitacora.addBitacoraGeneral();
                }
            } else {
                RESP = "rol";
            }
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al momento ejecutar el procedmiento add_rol!", ex);
        } finally {
            try {
                if (cst != null) {
                    if (!cst.isClosed()) {
                        cst.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el callable statement", ex);
            }

            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el objeto de conexión", ex);
            }
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el prepared statement", ex);
            }
        }
        return RESP;
    }

    public String actualizarRol() {
        String RESP = "error";
        CallableStatement cst = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Roles");
        bitacora.setMovimiento("Actualización");
        try {
            if (!validarTipoRolExistencia(true)) {
                CConexion cConexion = new CConexion();
                cConexion.setRequest(request);
                conn = cConexion.GetconexionInSite();
                //if (cst.getResultSet() == null) {
                //if (cst.getUpdateCount() != -1) {
                String Query = "DELETE FROM Roles_Permiso_Intermedia WHERE Id_TipoRol = ?";
                ps = conn.prepareStatement(Query);
                ps.setString(1, getRoles().getId_TipoRol());
                ps.execute();
                rs = ps.getResultSet();
                if (rs == null) {
                    String cadena_rol_seleccion = request.getParameter("txtCadenaIdRoles");
                    String[] cadenaIds = cadena_rol_seleccion.split("\\*");
                    for (String cadenaId : cadenaIds) {
                        Query = "INSERT INTO Roles_Permiso_Intermedia VALUES (?,?,?,?,?,?,?,?,?,?)";
                        ps = conn.prepareStatement(Query);
                        ps.setString(1, getRoles().getId_TipoRol());
                        String ids = cadenaId.split("\\^")[0];
                        ps.setString(2, ids);
                        String[] permisos = cadenaId.split("\\^")[1].split("¬");
                        int aux = 0;
                        for (int i = 1; i < 9; i++) {
                            if (aux != permisos.length) {
                                if (("Permiso" + i).equalsIgnoreCase("Permiso" + permisos[aux].split("°")[0])) {
                                    ps.setInt((i + 2), Integer.parseInt(permisos[aux].split("°")[1]));
                                    aux++;
                                } else {
                                    ps.setInt((i + 2), 0);
                                }
                            } else {
                                ps.setInt((i + 2), 0);
                            }
                        }
                        ps.execute();
                    }
                    Query = "{call Upd_Rol ?,?,?}";
                    cst = conn.prepareCall(Query);
                    cst.setString(1, getRoles().getId_TipoRol());
                    cst.setString(2, getRoles().getNombreTipo());
                    cst.setString(3, getRoles().getDescripcion());
                    cst.execute();
                    if (cst.getResultSet() == null && cst.getUpdateCount() == -1) {
                        RESP = "success";
                    }
                    bitacora.setInformacion("Actualización de rol: " + getRoles().getId_TipoRol() + "||Respuesta Método: " + RESP);
                    cBitacora = new CBitacora(bitacora);
                    cBitacora.setRequest(request);
                    cBitacora.addBitacoraGeneral();
                }
                //}
                //}
            } else {
                RESP = "rol";
            }
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al momento ejecutar el procedmiento Upd_Rol!", ex);
        } finally {
            try {
                if (cst != null) {
                    if (!cst.isClosed()) {
                        cst.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el callable statement", ex);
            }

            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el prepared statement", ex);
            }
        }
        return RESP;

    }

    public String eliminarRol() {
        String RESP = "error";
        CallableStatement cst = null;
        ResultSet rs = null;
        Connection conn = null;
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Roles");
        bitacora.setMovimiento("Eliminación");
        try {
            if (!validarRolUsuario()) {
                CConexion cConexion = new CConexion();
                cConexion.setRequest(request);
                conn = cConexion.GetconexionInSite();
                cst = conn.prepareCall("{call Delt_Rol ?}");
                cst.setString(1, getRoles().getId_TipoRol());
                cst.execute();

                rs = cst.getResultSet();

                if (rs == null) {
                    if (cst.getUpdateCount() != -1) {
                        RESP = "success";
                        bitacora.setInformacion("Eliminación de rol: " + getRoles().getId_TipoRol() + "||Respuesta Método: " + RESP);
                        cBitacora = new CBitacora(bitacora);
                        cBitacora.setRequest(request);
                        cBitacora.addBitacoraGeneral();
                    }
                }
            } else {
                RESP = "link";
            }
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error interno en eliminarRol()", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el result set", ex);
                }
            }
            try {
                if (cst != null) {
                    if (!cst.isClosed()) {
                        cst.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "Ocurrió un error al cerrar el callable statement", ex);
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP;
    }

    public String consultarRolEspecifico() {
        StringBuilder RESP = new StringBuilder();
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String Query = "SELECT  r.Id_TipoRol,r.NombreTipo,\n"
                    + "		cp.Id_CatalogoP, cp.NombrePermiso, \n"
                    + "		cp.Permiso1, rpi.Permiso1,\n"
                    + "		cp.Permiso2, rpi.Permiso2,\n"
                    + "		cp.Permiso3, rpi.Permiso3,\n"
                    + "		cp.Permiso4, rpi.Permiso4,\n"
                    + "		cp.Permiso5, rpi.Permiso5,\n"
                    + "		cp.Permiso6, rpi.Permiso6,\n"
                    + "		cp.Permiso7, rpi.Permiso7,\n"
                    + "		cp.Permiso8, rpi.Permiso8,\n"
                    + "         r.Descripcion \n"
                    + "FROM Catalogo_Permisos cp LEFT JOIN Roles_Permiso_Intermedia rpi \n"
                    + "ON cp.Id_CatalogoP = rpi.Id_Permiso AND rpi.Id_TipoRol = ? LEFT JOIN roles r \n"
                    + "ON r.Id_TipoRol = rpi.Id_TipoRol";
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);
            ps.setString(1, getRoles().getId_TipoRol());

            ps.execute();
            rs = ps.getResultSet();

            String idTipoRol = "";
            String nombreTipo = "";
            String descripRol = "";
            List<String[]> lstCatalogoDatos = new ArrayList<String[]>();
            List<String[]> lstRolesPI = new ArrayList<String[]>();
            while (rs.next()) {
                String[] datos = new String[10];
                if (idTipoRol.isEmpty()) {
                    idTipoRol = rs.getString(1);
                    nombreTipo = rs.getString(2);
                    descripRol = rs.getString(21);
                }
                datos[0] = rs.getString(3);
                datos[1] = rs.getString(4);
                datos[2] = rs.getString(5);
                datos[3] = rs.getString(7);
                datos[4] = rs.getString(9);
                datos[5] = rs.getString(11);
                datos[6] = rs.getString(13);
                datos[7] = rs.getString(15);
                datos[8] = rs.getString(17);
                datos[9] = rs.getString(19);
                lstCatalogoDatos.add(datos);

                datos = new String[8];
                datos[0] = rs.getString(6);
                datos[1] = rs.getString(8);
                datos[2] = rs.getString(10);
                datos[3] = rs.getString(12);
                datos[4] = rs.getString(14);
                datos[5] = rs.getString(16);
                datos[6] = rs.getString(18);
                datos[7] = rs.getString(20);

                lstRolesPI.add(datos);
            }

            for (int i = 0; i < lstRolesPI.size(); i++) {
                RESP.append("<div class='panel panel-default'>");
                RESP.append("<div class='panel-heading'>");
                RESP.append("<h4 class='panel-title'>");
                RESP.append("<div class='row'>").append("<div class='col-xs-6 col-lg-9 col-md-9 col-sm-7'>");
                RESP.append("<a data-toggle='collapse' href ='#collapse").append(lstCatalogoDatos.get(i)[0]).append("'>").append(lstCatalogoDatos.get(i)[1]).append("</a>").append("</div>");
                RESP.append("<div class='col-xs-6 col-lg-3 col-md-3 col-sm-5' style='text-align: right'>");
                RESP.append("<label class='css-input css-checkbox css-checkbox-primary pull-t' data-toggle='popover' data-container='body' title='' data-placement='right' data-content='Al marcar esta opción se habilitan los permisos sobre el control total del sistema' data-original-title='¡Importante!'>");
                RESP.append("<input class='checkPanel' type='checkbox' autocomplete='off' id='checkPanel_").append(lstCatalogoDatos.get(i)[0]).append("'><span></span> Habilitar todo").append("</label></div></div>");
                RESP.append("</h4>");
                RESP.append("</div>");
                RESP.append("<div id='collapse").append(lstCatalogoDatos.get(i)[0]).append("' class='panel-collapse collapse in'>");
                RESP.append("<div class='panel-body' ").append("id='IdPermiso_").append(lstCatalogoDatos.get(i)[0]).append("'>");
                for (int j = 0; j < 8; j++) {
                    if (lstCatalogoDatos.get(i)[(j + 2)] != null) {
                        RESP.append("<div class='col-xs-6 col-lg-4 col-sm-4 col-md-4'>");
                        RESP.append("<label class='css-input css-checkbox css-checkbox-primary'").append(">");
                        String checked = (lstRolesPI.get(i)[j] != null && lstRolesPI.get(i)[j].equalsIgnoreCase("1") ? "checked='checked' " : "");
                        RESP.append("<input class='permisos' type='checkbox' ").append(checked).append("id='Permiso_").append((1 + j)).append("_").append(lstCatalogoDatos.get(i)[0]).append("'>");
                        RESP.append("<span></span>").append(lstCatalogoDatos.get(i)[(j + 2)]);
                        RESP.append("</label></div>");
                    }
                }
                RESP.append("</div>");
                RESP.append("</div>");
                RESP.append("</div>");
            }
            RESP.append("°success").append("°");
            RESP.append(idTipoRol).append("¬").append(nombreTipo).append("¬").append(descripRol);
//            if (rs.next()) {
//                RESP.append("success").append("°");
//                RESP.append(rs.getString(1)).append("¬");
//                RESP.append(rs.getString(2));
//            }
//
//            while (rs.next()) {
//                RESP.append("<div class='panel panel-default'>");
//                RESP.append("<div class='panel-heading'>");
//                RESP.append("<h4 class='panel-title'>");
//                RESP.append("<a data-toggle='collapse' href ='#collapse").append(rs.getString("Id_CatalogoP")).append("'>").append(rs.getString("NombrePermiso")).append("</a>");
//                RESP.append("</h4>");
//                RESP.append("</div>");
//                RESP.append("<div id='collapse").append(rs.getString("Id_CatalogoP")).append("' class='panel-collapse collapse in'>");
//                RESP.append("<div class='panel-body' ").append("id='IdPermiso_").append(rs.getString("Id_CatalogoP")).append("'>");
//                for (int i = 0; i < 8; i++) {
//                    if (rs.getString("Permiso" + (1 + i)) != null) {
//                        RESP.append("<div class='col-xs-12 col-lg-4 col-sm-12 col-md-4'>");
//                        RESP.append("<label class='css-input css-checkbox css-checkbox-primary' ").append("id='check_").append("'>");
//                        RESP.append("<input class='permisos' type='checkbox' id='Permiso_").append((1 + i)).append("_").append(rs.getString("Id_CatalogoP")).append("'>");
//                        RESP.append("<span></span>").append(rs.getString("Permiso" + (1 + i)));
//                        RESP.append("</label></div>");
//                    }
//                }
//                RESP.append("</div>");
//                RESP.append("</div>");
//                RESP.append("</div>");
//            }
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al consultar el rol!", ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al recorrer los arreglos!", ex);
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en consultarRolEspecifico()", ex);
                }
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
        return RESP.toString();
    }

    public String consultarCatalogoRoles() {
        StringBuilder RESP = new StringBuilder();
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String Query = "SELECT * FROM Catalogo_Permisos";
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);

            ps.execute();
            rs = ps.getResultSet();
            boolean flag = true;
            while (rs.next()) {
                RESP.append("<div class='panel panel-default'>");
                RESP.append("<div class='panel-heading'>");
                RESP.append("<h4 class='panel-title'>");
                RESP.append("<div class='row'>").append("<div class='col-xs-6 col-lg-9 col-md-9 col-sm-7'>");
                RESP.append("<a data-toggle='collapse' href ='#collapse").append(rs.getString("Id_CatalogoP")).append("'>").append(rs.getString("NombrePermiso")).append("</a>").append("</div>");
                RESP.append("<div class='col-xs-6 col-lg-3 col-md-3 col-sm-5' style='text-align: right'>");
                RESP.append("<label class='css-input css-checkbox css-checkbox-primary pull-t'>");
                RESP.append("<input type='checkbox' autocomplete='off' class='checkPanel' id='checkPanel_").append(rs.getString("Id_CatalogoP")).append("'><span></span> Habilitar todo").append("</label></div></div>");
                RESP.append("</h4>");
                RESP.append("</div>");
                RESP.append("<div id='collapse").append(rs.getString("Id_CatalogoP")).append("' class='panel-collapse collapse ");
                RESP.append((flag ? "in'>" : "'>"));
                RESP.append("<div class='panel-body' ").append("id='IdPermiso_").append(rs.getString("Id_CatalogoP")).append("'>");
                for (int i = 0; i < 8; i++) {
                    if (rs.getString("Permiso" + (1 + i)) != null) {
                        RESP.append("<div class='col-xs-6 col-lg-4 col-sm-4 col-md-4'>");
                        RESP.append("<label class='css-input css-checkbox css-checkbox-primary' ").append("id='check_").append("'>");
                        RESP.append("<input class='permisos' type='checkbox' id='Permiso_").append((1 + i)).append("_").append(rs.getString("Id_CatalogoP")).append("'>");
                        RESP.append("<span></span>").append(rs.getString("Permiso" + (1 + i)));
                        RESP.append("</label></div>");
                    }
                }
                RESP.append("</div>");
                RESP.append("</div>");
                RESP.append("</div>");
                flag = false;
            }

        } catch (Exception ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al consultar los datos!", ex);
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en consultarRolEspecifico()", ex);
                }
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

        return RESP.toString();
    }

    //<editor-fold defaultstate="collapsed" desc="Métodos para validaciones internas">
    /**
     * Check if an email is already in DB
     *
     * @param isUpd Indicate if is an update or insert true for update false if
     * not
     * @return true if email exist or false if not
     */
    public boolean validarCorreoElectronico(boolean isUpd) {
        String RESP = "";
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bandera = false;
        try {
            String Query = "SELECT * FROM PERSONA WHERE EMAIL = ?";
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);
            ps.setString(1, getPersona().getEmail());
            ps.execute();
            rs = ps.getResultSet();
            /**
             * If there is an update we verify if the email is like the one in
             * DB If is yes we return a false statement because is our email.
             */
            if (isUpd) {
                if (rs.next()) {
                    bandera = rs.getString("email").equalsIgnoreCase(getPersona().getEmail());
                    return !bandera;
                } else {
                    return false;
                }
            } else {
                return (rs == null ? false : rs.next());
            }
        } catch (Exception ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al ejecutar el result set en validarCorreoElectronico()", ex);
            return false;
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en validarCorreoElectronico()", ex);
                }
            }
            if (ps != null) {
                try {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en validarCorreoElectronico()", ex);
                }
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Check if a username is already registered in DB
     *
     * @param isUpd Indicate if is an update or insert true for update false if
     * @return true if username exist or false if not
     */
    public boolean validarUserNameExisencia(boolean isUpd) {
        String RESP = "";
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bandera = false;
        try {
            String Query = "SELECT * FROM usuario WHERE NombreUsuario = ?";
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);
            ps.setString(1, getUsuarios().getNombreUsuario());
            ps.execute();
            rs = ps.getResultSet();
            /**
             * If there is an update we verify if the username is like the one
             * in DB If is yes we return a false statement because is our
             * username.
             */
            if (isUpd) {
                if (rs.next()) {
                    bandera = rs.getString("nombreUsuario").equalsIgnoreCase(getUsuarios().getNombreUsuario());
                    return !bandera;
                } else {
                    return false;
                }
            } else {
                return (rs == null ? false : rs.next());
            }
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al ejecutar el result set en validarUserNameExisencia()", ex);
            return false;
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en validarUserNameExisencia()", ex);
                }
            }
            if (ps != null) {
                try {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en consultarListaRoles()", ex);
                }
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Check if a rolname is already registered in DB
     *
     * @param isUpd Indicate if is an update or insert true for update false for
     * insert
     * @return true if rolname exist or false if not
     */
    public boolean validarTipoRolExistencia(boolean isUpd) {
        String RESP = "";
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bandera = false;
        try {
            String Query = "SELECT * FROM Roles WHERE NombreTipo = ?";
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);
            ps.setString(1, getRoles().getNombreTipo());
            ps.execute();
            rs = ps.getResultSet();
            /**
             * If there is an update we verify if the rolanem is like the one in
             * DB If is yes we return a false statement because is our rolname.
             */
            if (isUpd) {
                if (rs.next()) {
                    bandera = rs.getString("nombreTipo").equalsIgnoreCase(getRoles().getNombreTipo());
                    return !bandera;
                } else {
                    return false;
                }
            } else {
                return (rs == null ? false : rs.next());
            }
        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al ejecutar el result set en validarTipoRolExistencia()", ex);
            return false;
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en validarTipoRolExistencia()", ex);
                }
            }
            if (ps != null) {
                try {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en validarTipoRolExistencia()", ex);
                }
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Check if a is linnked with a user.
     *
     * @return true if exists a relation between rol and user false if not
     */
    public boolean validarRolUsuario() {
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        boolean bandera = false;
        try {
            String Query = "SELECT * FROM Roles_Usuario_Intermedia RUI JOIN ROLES R ON RUI.ID_TIPOROL = R.Id_TipoRol JOIN USUARIO U ON RUI.Id_Usuario = U.Id_Usuario \n"
                    + "WHERE U.Estatus = 1 AND RUI.Id_TipoRol = ?";
            CConexion cConexion = new CConexion();
            cConexion.setRequest(request);
            conn = cConexion.GetconexionInSite();
            ps = conn.prepareStatement(Query);
            ps.setString(1, getRoles().getId_TipoRol());
            ps.execute();
            rs = ps.getResultSet();
            if (rs.next()) {
                bandera = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al ejecutar el prepared statement!", ex);
            bandera = true;
        } finally {
            if (rs != null) {
                try {
                    if (!rs.isClosed()) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el result set en validarRolUsuario()", ex);
                }
            }
            if (ps != null) {
                try {
                    if (!ps.isClosed()) {
                        ps.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, "¡Ocurrió un error al cerrar el Prepared Statement en validarRolUsuario()", ex);
                }
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CUsuarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return bandera;
    }
    // </editor-fold>

}
