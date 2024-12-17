/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

/**
 *
 * @author Célula de desarrollo.
 */
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Menus;
import com.ginndex.titulos.modelo.Usuario;
import com.ginndex.titulos.modelo.Roles;
import com.ginndex.titulos.modelo.Preferencias_Usuario;
import com.ginndex.titulos.modelo.Persona;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import javax.servlet.http.HttpSession;

public class CLogin {

    HttpServletRequest request;
    HttpServletResponse response;
    String mensaje;
    String pagina;
    Persona persona;
    Usuario usuario;
    Roles roles;
    CConexionPool conector;
    Preferencias_Usuario preferencias_Usuario;
    private List<Menus> lstMenu;
    private List<Menus> lstSubMenu;
    private String menu;
    Bitacora bitacora;
    CBitacora cBitacora;

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public Preferencias_Usuario getPreferencias_Usuario() {
        return preferencias_Usuario;
    }

    public void setPreferencias_Usuario(Preferencias_Usuario preferencias_Usuario) {
        this.preferencias_Usuario = preferencias_Usuario;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public String getPagina() {
        return pagina;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public CLogin() {
        persona = new Persona();
        usuario = new Usuario();
        preferencias_Usuario = new Preferencias_Usuario();
        roles = new Roles();
    }

    //Permite validar la existencia del usuario y su contraseña.
    public void ValidarUsuario() throws SQLException, Exception {
        //System.out.println("Entró al proceso ValidarUsuario");
        //Esta línea permite la codificación de caracteres en español en el servidor.
        request.setCharacterEncoding("UTF-8");
        request.getRequestURI();
        //CConexion conector = new CConexion();
        HttpSession sessionOk = request.getSession();
        //ESte seria la variable dentro de la url
        String user = request.getParameter("txtLogUsuario");
        String pass = request.getParameter("txtLogPass");
        String carpeta = request.getParameter("txtCarpeta");
        ResultSet rs;
        conector = new CConexionPool(request);
        boolean licenciaVencida = esLicenciaVencida();
        //System.out.println("Usuario: "+user);
        if (ExisteUsuario(user, pass)) {
            try {
                if (!licenciaVencida) {
                    //ActualizarUltimoAcceso(user);
                    rs = conector.consulta("select * from persona as p "
                            + "left join usuario_persona_intermedia as upi on upi.id_persona=p.id_persona "
                            + "left join usuario as u on u.id_Usuario=upi.id_usuario "
                            + "left join roles_usuario_intermedia as rui on rui.id_usuario=u.id_usuario "
                            + "left join roles as r on r.id_tiporol=rui.id_tiporol "
                            + " where u.nombreusuario like '" + user + "' and u.Estatus=1");
                    // resulset cuando la base de datos, osea el usuario sea asesor tecnico:
                    if (rs.next()) {
                        usuario.setId_Usuario(rs.getInt("Id_Usuario"));
                        sessionOk.setAttribute("Id_Usuario", usuario.getId_Usuario());
                        usuario.setNombreUsuario(rs.getString("NombreUsuario"));
                        sessionOk.setAttribute("NombreUsuario", usuario.getNombreUsuario());
                        usuario.setContrasena(rs.getString("Contrasena"));
                        sessionOk.setAttribute("Contrasena", usuario.getContrasena());
                        usuario.setId_ConfiguracionInicial((rs.getString("Id_configuracionInicial") == null || rs.getString("Id_configuracionInicial").equalsIgnoreCase("") ? 0 : rs.getInt("Id_configuracionInicial")));
                        sessionOk.setAttribute("Id_configuracionInicial", usuario.getId_ConfiguracionInicial());
                        persona.setId_Persona(rs.getString("Id_Persona"));
                        sessionOk.setAttribute("Id_Persona", persona.getId_Persona());
                        persona.setNombre(rs.getString("Nombre"));
                        sessionOk.setAttribute("Nombre", persona.getNombre());
                        persona.setAPaterno(rs.getString("APaterno"));
                        sessionOk.setAttribute("APaterno", persona.getAPaterno());
                        persona.setAMaterno(rs.getString("AMaterno"));
                        sessionOk.setAttribute("AMaterno", persona.getAMaterno());
                        persona.setEmail(rs.getString("Email"));
                        sessionOk.setAttribute("Email", persona.getEmail());
                        roles.setNombreTipo(rs.getString("NombreTipo"));
                        sessionOk.setAttribute("Rol", roles.getNombreTipo());
                        usuario.setTipo(rs.getString("Tipo").trim());
                        sessionOk.setAttribute("NombreTipo", usuario.getTipo());
                        roles.setNombreTipo(rs.getString("Tipo").trim());
                        roles.setId_TipoRol(rs.getString("Id_TipoRol"));
                        sessionOk.setAttribute("Id_TipoRol", roles.getId_TipoRol());
                        sessionOk.setAttribute("EstatusPredeterminado", rs.getString("Estatus_Predeterminado"));
                        //Variable de sesión con la cual sabemos si el usuario ha cambiado sus preferencias
                        String menus = CrearMenu(rs.getString("NombreTipo"));
                        sessionOk.setAttribute("Menu", menus);
                        sessionOk.setAttribute("Genero", rs.getString("sexo"));
                        sessionOk.setAttribute("txtCarpeta", carpeta + "");
                        String cadenaPermisosAdicionales = consultarPermisosAdicionales();
                        if (!cadenaPermisosAdicionales.equalsIgnoreCase("error")) {
                            String[] arregloPermisosAdicionales = cadenaPermisosAdicionales.split("°");
                            for (String permisoAdicional : arregloPermisosAdicionales) {
                                sessionOk.setAttribute(permisoAdicional.split("¬")[0], permisoAdicional.split("¬")[1]);
                            }
                        }
                    }
                } else {
                    sessionOk.setAttribute("mensajeLogin", "La póliza de uso ha vencido, contacte a su asesor comercial.");
                    this.setPagina("../Generales/LogIn.jsp");
                }

            } catch (Exception ex) {
                //System.out.println("Error en CLogin(ValidarUsuario()): " + ex.getMessage());
            } finally {
                conector.cerrarConexion();
                conector.conexion.close();
                if (conector.rsRecord != null) {
                    conector.rsRecord.close();
                }
                if (conector.smtConsulta != null) {
                    conector.smtConsulta.close();
                }

            }
            if (!licenciaVencida) {
                if (usuario.getId_ConfiguracionInicial() == 0) {
                    this.setPagina("../Administrador/ConfiguracionBasica.jsp");
                } else if (roles.getNombreTipo().equalsIgnoreCase("Soporte")) {
                    this.setPagina("../Soporte/InicioAsesorTecnico.jsp");
                } else if (roles.getNombreTipo().equalsIgnoreCase("Admin")) {
                    this.setPagina("../Administrador/Bienvenida.jsp");
                }
                bitacora = new Bitacora();
                bitacora.setId_Usuario(usuario.getId_Usuario() + "");
                bitacora.setModulo("LogIn");
                bitacora.setMovimiento("Inicio de sesión");
                bitacora.setInformacion("Inicio de sesión al sistema del usuario:" + user + " y contraseña: " + pass);
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            }

        } else {
            sessionOk.setAttribute("mensajeLogin", "Lo sentimos los datos ingresados son incorrectos...");
            this.setPagina("../Generales/LogIn.jsp");
        }
    }

    public boolean ExisteUsuario(String user, String password) {
        //System.out.println("Entró al proceso ExisteUsuario");
        boolean bandera = false;
        PreparedStatement ps = null;
        try {
            conector.conexion = conector.dataSource.getConnection();
            String query = "select * from persona as p "
                    + "left join usuario_persona_intermedia as upi on upi.id_persona=p.id_persona "
                    + "left join usuario as u on u.id_Usuario=upi.id_usuario "
                    + "left join roles_usuario_intermedia as rui on rui.id_usuario=u.id_usuario "
                    + "left join roles as r on r.id_tiporol=rui.id_tiporol "
                    + "where u.nombreusuario like ? and contrasena = ? and u.Estatus = 1;";
            //System.out.println(query);

            ps = conector.conexion.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, password);
            ResultSet rsRecord = ps.executeQuery();
            if (rsRecord.next()) {
                bandera = true;
            } else {
                bandera = false;
            }
        } catch (Exception ex) {
            //System.out.println("Error en CLogin(ExisteUsuario()): " + ex.getMessage());
        } finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(CFirmantes.class.getName()).log(Level.SEVERE, "Error al cerrar el PreparedStatement en CLogin.java", ex);
            }
        }
        return bandera;
    }

    //Crea el menú de la aplicación.
    public String CrearMenu(String rol) throws SQLException, Exception {
        menu = "";
        ResultSet rs = null;
        try {

            if (usuario.getId_ConfiguracionInicial() != 0) {
                rs = conector.consulta("select * from menus as m "
                        + "join menus_roles_intermedia as mri on m.id_menu=mri.id_menu "
                        + "join roles as r on r.id_tiporol=mri.id_tiporol "
                        + "where NombreTipo like '" + rol + "' order by orden;");

                lstMenu = new ArrayList<Menus>();
                lstSubMenu = new ArrayList<Menus>();
                while (rs.next()) {

                    Menus mn = new Menus();
                    mn.setOrden(rs.getInt("orden"));
                    mn.setNombre(rs.getString("nombre"));
                    mn.setURL(rs.getString("URL"));
                    mn.setId_padre(rs.getInt("id_padre"));
                    mn.setIcono(rs.getString("Icono"));
                    mn.setSeccion(rs.getInt("Seccion"));

                    lstMenu.add(mn);
                    lstSubMenu.add(mn);
                }

                for (int i = 0; i <= lstMenu.size() - 1; i++) {
//                if(lstMenu.get(i).getSeccion()==2 && lstMenu.get(i).getId_padre()==77){
//                    banderaFinanciero=1;
//                    menu += "<li class='nav-main-heading'><span class='sidebar-mini-hide'>User Interface</span></li>";
//                }

                    int id_padre = lstMenu.get(i).getId_padre();
                    int orden = lstMenu.get(i).getOrden();
                    int id_secc = lstMenu.get(i).getSeccion();
                    //System.out.println("Control.CLogin.CrearMenu()" + id_secc);
                    if (id_padre == 0) {

                        boolean hasch = haschild(orden);
                        menu += "<li class='click_save_historial'>";
                        menu += "<a " + (hasch ? "class='nav-submenu' data-toggle='nav-submenu'" : "") + "  href='" + lstMenu.get(i).getURL() + "' onclick='window.onbeforeunload = null;' ><i class='" + lstMenu.get(i).getIcono() + "'></i><span class='sidebar-mini-hide'>" + lstMenu.get(i).getNombre() + "</span></a>";
                        ///Revisamos si el menú tiene hijos
                        if (hasch) {

                            menu += "<ul>";
                            for (int is = 0; is <= lstSubMenu.size() - 1; is++) {
                                if (lstSubMenu.get(is).getId_padre() == orden) {
                                    menu += "<li " + (lstSubMenu.get(is).getNombre().equalsIgnoreCase("Mensaje a Soporte") ? " id='liMensajeSoporte' " : lstSubMenu.get(is).getNombre().equalsIgnoreCase("Mensaje a control escolar") ? " class='liMensajeControlEscolar'" : "") + ">";
                                    menu += "<a " + (haschild(lstMenu.get(is).getOrden()) ? "class='nav-submenu' data-toggle='nav-submenu'" : "") + " href='" + lstSubMenu.get(is).getURL() + "' onclick='window.onbeforeunload = null;'>" + "<i class='" + lstSubMenu.get(is).getIcono() + "'></i>" + lstSubMenu.get(is).getNombre() + "</a>";
                                    if (haschild(lstMenu.get(is).getId_menu())) {
                                        menu += "<ul>";

                                        for (int isb = 0; isb <= lstSubMenu.size() - 1; isb++) {
                                            if (lstSubMenu.get(isb).getId_padre() == lstMenu.get(is).getOrden()) {
                                                menu += "<li>";
                                                menu += "<a  href='" + lstSubMenu.get(isb).getURL() + "' onclick='window.onbeforeunload = null;'>" + lstSubMenu.get(isb).getNombre() + "</a>";
                                                menu += "</li>";
                                            }
                                        }

                                        menu += "</ul>";
                                    }
                                    menu += "</li>";
                                }
                            }
                            menu += "</ul>";
                        }
                        ///
                        menu += "</li>";
                    }

                }
                //menu += "<li class='hidden-xs'><a data-toggle='layout' data-action='sidebar_mini_toggle'><i  class='si si-pin'></i><span class='sidebar-mini-hide'>Anclar menú</span></a></li>";
            }
        } catch (Exception ex) {
            String QueryErr = "SET LANGUAGE 'español'; execute Insertar_Bitacora_errores '0','" + ex.getMessage() + "','CLogin','Consultar Menu';";
            conector.consulta(QueryErr);
        }
        return menu;
    }

    public boolean haschild(int id_menu) {
        for (int i = 0; i <= lstSubMenu.size() - 1; i++) {
            if (id_menu == lstMenu.get(i).getId_padre()) {
                return true;
            }
        }
        return false;
    }

    public String consultarPermisosAdicionales() {
        ResultSet rs = null;
        String resp = "";
        try {
            rs = conector.consulta("SELECT Descripcion, Activo FROM Permisos_Adicionales");
            while (rs.next()) {
                resp += rs.getString("Descripcion") + "¬" + rs.getString("Activo") + "°";
            }
        } catch (SQLException ex) {
            Logger.getLogger(CLogin.class.getName()).log(Level.SEVERE, null, ex);
            resp = "error";
        }
        return resp;
    }

    public boolean esLicenciaVencida() {
        boolean RESP = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                RESP = (diasRestantes < 1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CCalificaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return RESP;
    }

}
