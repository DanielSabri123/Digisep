package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Carrera;
import com.ginndex.titulos.modelo.TEAutorizaciones;
import com.ginndex.titulos.modelo.TEEntidad;
import com.ginndex.titulos.modelo.TENivelEducativo;
import com.ginndex.titulos.modelo.TETipoPeriodo;
import com.ginndex.titulos.modelo.TETitulosCarreras;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Paola Alonso
 */
public class CClavesCarrera {

    HttpServletRequest request;
    HttpServletResponse response;
    CConexionPool conector;
    Connection con;
    CallableStatement cstmt;
    String Bandera;
    String Id_Usuario;
    String id_ConfInicial;
    String rutaArchivo;
    String nombreArchivo;
    String confirmacionNomInstitucion;
    ResultSet rs;
    String usuario;
    private String permisos;
    Bitacora bitacora;
    CBitacora cBitacora;
    private TETitulosCarreras ttc;
    private Carrera c;

    private CConexion conexion;
    private Connection conn;
    private PreparedStatement pstmt;

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

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String EstablecerAcciones() throws UnsupportedEncodingException, Exception {
        setBandera(request.getParameter("txtBandera") == null ? request.getParameter("bandera") : request.getParameter("txtBandera"));
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");
        String RESP = "";

        HttpSession sessionOk = request.getSession();
        usuario = sessionOk.getAttribute("NombreUsuario").toString();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        id_ConfInicial = sessionOk.getAttribute("Id_configuracionInicial").toString();

        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        permisos = cPermisos.obtenerPermisos("Claves de Carrera");

        conector = new CConexionPool(request);
        if (Bandera.equalsIgnoreCase("1")) {
            RESP = CargarCarreras() + "~"
                    + CargarAutorizacionesReconocimiento() + "~"
                    + CargarTiposPeriodo() + "~"
                    + CargarNivelEducativo() + "~"
                    + CargarEntidadFederativa();
        } else if (Bandera.equalsIgnoreCase("2")) {
            RESP = AddClaveCarrera();
        } else if (Bandera.equalsIgnoreCase("3")) {
            RESP += UpdateClaveCarrera();
        } else if (Bandera.equalsIgnoreCase("4")) {
            RESP += ConsultarCarrera();
        } else if (Bandera.equalsIgnoreCase("5")) {
            RESP += guardarConfiguracionNombreInstitucion();
        } else if (Bandera.equalsIgnoreCase("6")) {
            RESP += consultarConfiguracionNomInstitucion();
        }
        if (!Bandera.equalsIgnoreCase("5") && !Bandera.equalsIgnoreCase("6")) {
            conector.cerrarConexion();
        }

        return RESP;
    }

    private String consultarConfiguracionNomInstitucion() {
        String RESP = "";

        try {
            String Query = "SELECT * FROM Configuracion_Nombre_Institucion WHERE ID_ConfiguracionInicial = " + id_ConfInicial;

            con = conector.dataSource.getConnection();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();
            RESP = "success";
            if (rs.next()) {
                confirmacionNomInstitucion = rs.getString(2);
                RESP += confirmacionNomInstitucion;
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la inserción: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la inserción: " + accion_catch(ex);
        }

        return RESP;
    }

    private String guardarConfiguracionNombreInstitucion() {
        String RESP = "";
        int confNombreInstitucion = Integer.parseInt(request.getParameter("nombreInstitucion"));
        int idConfiguracionInicial = Integer.parseInt(id_ConfInicial);

        try {
            String Query = "{call Upd_Configuracion_Nombre_Institucion (?,?)}";
            System.out.println(conector);
            System.out.println(conector.dataSource);
            System.out.println(conector.dataSource.getConnection());
            con = conector.dataSource.getConnection();
            cstmt = con.prepareCall(Query);
            cstmt.setInt(1, idConfiguracionInicial);
            cstmt.setInt(2, confNombreInstitucion);
            cstmt.execute();

            RESP = "success";
            bitacora = new Bitacora();
            bitacora.setInformacion("Registro de configuración para nombre de la institución");
            bitacora.setId_Usuario(Id_Usuario);
            bitacora.setModulo("Claves de Carrera");
            bitacora.setMovimiento("Inserción");
            cBitacora = new CBitacora(bitacora);
            cBitacora.setRequest(request);
            cBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la inserción: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la inserción: " + accion_catch(ex);
        }
        return RESP;
    }

    private String AddClaveCarrera() {
        String RESP = "";
        String claveCarrera = request.getParameter("claveCarrera");
        String nombreCarrera = request.getParameter("nombreCarrera");
        String claveInstitucion = request.getParameter("claveInstitucion");
        String nombreInstitucion = request.getParameter("nombreInstitucion");
        String claveCampus = request.getParameter("claveCampus");
        String nombreCampus = request.getParameter("nombreCampus");
        String idAutorizacion = request.getParameter("idAutorizacion");
        String noRvoe = request.getParameter("noRvoe");
        String idTipoPeriodo = request.getParameter("idTipoPeriodo");
        String idNivelEducativo = request.getParameter("idNivel");
        String totalMaterias = request.getParameter("totalMaterias");
        String idEntidadFederativa = request.getParameter("idEntidadFederativa");
        String fechaExpedicionRvoe = request.getParameter("fechaExpedicionRvoe");
        String gradoObtenidoM = request.getParameter("gradoObtenidoM");
        String gradoObtenidoF = request.getParameter("gradoObtenidoF");
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Claves de Carrera");
        bitacora.setMovimiento("Inserción");

        try {
            String Query = "SET LANGUAGE 'español'; execute Add_Carrera '" + usuario + "','" + claveCarrera + "','" + nombreCarrera + "','"
                    + claveInstitucion + "','" + nombreInstitucion + "','" + claveCampus + "','" + nombreCampus + "'," + idAutorizacion + ",'"
                    + noRvoe + "'," + idTipoPeriodo + "," + idNivelEducativo + "," + totalMaterias + ",'" + idEntidadFederativa + "','" + fechaExpedicionRvoe + "','"
                    + gradoObtenidoM + "','" + gradoObtenidoF + "';";

            rs = conector.consulta(Query);
            if (rs == null || !rs.next()) {
                RESP = "success";
                bitacora.setInformacion("Registro Carreras vía Claves de carrera");
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else {
                String res = rs.getString(1);
                if (res.equalsIgnoreCase("claveExistente")) {
                    RESP = res;
                } else {
                    RESP = "error";
                }
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la inserción de la carrera: " + accion_catch(ex);
        }
        return RESP;
    }

    private String UpdateClaveCarrera() {
        String RESP = "";
        ttc = new TETitulosCarreras();
        c = new Carrera();
        String idCarrera = request.getParameter("idCarrera");
        ttc.setID_Carrera(request.getParameter("idCarrera"));
        String claveCarrera = request.getParameter("claveCarrera");
        ttc.setClaveCarrera(request.getParameter("claveCarrera"));
        String nombreCarrera = request.getParameter("nombreCarrera");
        ttc.setNombreCarrera(request.getParameter("nombreCarrera"));
        String claveInstitucion = request.getParameter("claveInstitucion");
        ttc.setClaveInstitucion(request.getParameter("claveInstitucion"));
        String nombreInstitucion = request.getParameter("nombreInstitucion");
        ttc.setNombreInstitucion(request.getParameter("nombreInstitucion"));
        String claveCampus = request.getParameter("claveCampus");
        ttc.setClaveCampus(request.getParameter("claveCampus"));
        String nombreCampus = request.getParameter("nombreCampus");
        ttc.setCampus(request.getParameter("nombreCampus"));
        String idAutorizacion = request.getParameter("idAutorizacion");
        ttc.setID_AutorizacionReconocimiento(request.getParameter("idAutorizacion"));
        String noRvoe = request.getParameter("noRvoe");
        ttc.setNumeroRvoe(request.getParameter("noRvoe"));
        String idTipoPeriodo = request.getParameter("idTipoPeriodo");
        ttc.setID_TipoPeriodo(request.getParameter("idTipoPeriodo"));
        String idNivelEducativo = request.getParameter("idNivel");
        ttc.setID_Nivel(request.getParameter("idNivel"));
        String totalMaterias = request.getParameter("totalMaterias");
        c.setTotalMaterias(request.getParameter("totalMaterias"));
        String idEntidadFederativa = request.getParameter("idEntidadFederativa");
        String fechaExpedicionRvoe = request.getParameter("fechaExpedicionRvoe");
        ttc.setFechaExpedicionRvoe(request.getParameter("fechaExpedicionRvoe"));
        String gradoObtenidoM = request.getParameter("gradoObtenidoM");
        ttc.setGradoObtenidoM(request.getParameter("gradoObtenidoM"));
        String gradoObtenidoF = request.getParameter("gradoObtenidoF");
        ttc.setGradoObtenidoF(request.getParameter("gradoObtenidoF"));
        String IdCarreraExcel = request.getParameter("idCarreraExcel");
        ttc.setId_Carrera_Excel(request.getParameter("idCarreraExcel"));
        String califMin = request.getParameter("califMin");
        c.setCalMin(request.getParameter("califMin"));
        String califMax = request.getParameter("califMax");
        c.setCalMax(request.getParameter("califMax"));
        String califMinAprobatoria = request.getParameter("califMinAprobatoria");
        c.setCalMinAprobatoria(request.getParameter("califMinAprobatoria"));
        String cvePlanCarrera = request.getParameter("cvePlanCarrera");
        c.setCvePlanCarrera(request.getParameter("cvePlanCarrera"));
        String totalCreditos = request.getParameter("totalCreditos");
        c.setTotalCreditos(request.getParameter("totalCreditos"));
        String numDecimales = request.getParameter("numDecimales");
        bitacora = new Bitacora();
        bitacora.setId_Usuario(Id_Usuario);
        bitacora.setModulo("Claves de Carrera");
        bitacora.setMovimiento("Actualización");

        try {
            String Query = "SET LANGUAGE 'español'; execute Upd_Carrera '" + usuario + "'," + ttc.getID_Carrera() + ",'" + ttc.getClaveCarrera() + "','"
                    + ttc.getNombreCarrera() + "','" + ttc.getClaveInstitucion() + "','" + ttc.getNombreInstitucion() + "','" + ttc.getClaveCampus() + "','" + ttc.getCampus() + "',"
                    + ttc.getID_AutorizacionReconocimiento() + ",'" + ttc.getNumeroRvoe() + "'," + ttc.getID_TipoPeriodo() + "," + ttc.getID_Nivel() + "," + c.getTotalMaterias() + ",'" + idEntidadFederativa + "','"
                    + ttc.getFechaExpedicionRvoe() + "','" + ttc.getGradoObtenidoM() + "','" + ttc.getGradoObtenidoF() + "'," + ttc.getId_Carrera_Excel() + "," + c.getCalMin() + "," + c.getCalMax() + "," + c.getCalMinAprobatoria() + ",'"
                    + c.getCvePlanCarrera() + "'," + c.getTotalCreditos() + "," + numDecimales + ";";
            System.out.println(Query);
            rs = conector.consulta(Query);
            if ((rs == null || !rs.next()) && conector.getEx() == null) {
                RESP = "success";
                bitacora.setInformacion("Actualización de  Carreras vía Claves de carrera ||" + Query);
                cBitacora = new CBitacora(bitacora);
                cBitacora.setRequest(request);
                cBitacora.addBitacoraGeneral();
            } else {
                if (conector.getEx() == null) {
                    String rsp = rs.getString(1);
                    if (rsp.equalsIgnoreCase("claveExistente")) {
                        RESP = "claveExistente";
                    } else {
                        if (rs.getMetaData().getColumnCount() == 5) {
                            RESP = "error|Error al momento de actualizar, el servidor devolvió la siguiente respuesta:<br><b>" + rs.getString(5) + "</b>";
                        } else {
                            RESP = "error|Error al momento de actualizar, el método no devolvió una respuesta identificada.<br><small><b>Actualiza la página y verifica que el cambio haya sido enfectuado. Si no, intenta de nuevo.</b></small>";
                        }
                    }
                } else {
                    if (conector.getEx() instanceof SQLException) {
                        throw new SQLException(conector.getEx());
                    } else {
                        throw new Exception(conector.getEx());
                    }
                }
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la actualizacón de la carrera: "
                    + "El servidor devolvió el siguiente código de error <b>" + (conector.getErrorCode() != 0 ? conector.getErrorCode() : ex.getErrorCode()) + ".</b><p><small>Contacte a soporte técnico.</small>";
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la actualizacón de la carrera: " + accion_catch(ex);
        }

        return RESP;
    }

    private String CargarCarreras() {
        String RESP = "";
        TETitulosCarreras teCarreras;
        List<TETitulosCarreras> lstCarreras;
        try {
            String Query = "SELECT * FROM TETitulosCarreras";
            rs = conector.consulta(Query);
            lstCarreras = new ArrayList<>();
            while (rs.next()) {
                teCarreras = new TETitulosCarreras();

                teCarreras.setID_Carrera(rs.getString("ID_Carrera"));
                teCarreras.setId_Carrera_Excel(rs.getString("ID_Carrera_Excel"));
                teCarreras.setClaveInstitucion(rs.getString("cveInstitucion"));
                teCarreras.setNombreInstitucion(rs.getString("nombreInstitucion"));
                teCarreras.setClaveCarrera(rs.getString("cveCarrera"));
                teCarreras.setNombreCarrera(rs.getString("nombreCarrera"));
                teCarreras.setID_AutorizacionReconocimiento(rs.getString("idAutorizacionReconocimiento"));
                teCarreras.setNumeroRvoe(rs.getString("numeroRvoe"));
                teCarreras.setClaveCampus(rs.getString("CveCampus"));
                teCarreras.setCampus(rs.getString("Campus"));
                teCarreras.setID_TipoPeriodo(rs.getString("id_TipoPeriodo"));
                teCarreras.setID_Nivel(rs.getString("id_Nivel"));

                lstCarreras.add(teCarreras);
            }

            RESP += "success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblCarreras'>"
                    + " <thead class='bg-primary-dark' style='color: white;'>"
                    + "     <tr>"
                    + "         <th class='text-center' style='display:none;'>IdCarrera</th>"
                    + "         <th class='text-center'>Id Carrera</th>"
                    + "         <th class='text-center'>Nombre Carrera</th>"
                    + "         <th class='text-center hidden-xs hidden-sm'>Clave Institución</th>"
                    + "         <th class='text-center hidden-xs'>Nombre Institución</th>"
                    + "         <th class='text-center'>Acciones</th>"
                    + "     </tr>"
                    + " </thead>"
                    + " <tbody id='tblBodyCarreras'>";

            String cabecera = permisos.split("°")[0];
            if (!cabecera.equalsIgnoreCase("todos")) {
                permisos = permisos.split("°")[1];
            }
            for (int i = 0; i < lstCarreras.size(); i++) {
                RESP += "<tr>";
                RESP += "<td style='display: none;' id='IdCarrea_" + lstCarreras.get(i).getID_Carrera() + "'>" + lstCarreras.get(i).getID_Carrera() + "</td>";
                RESP += "<td class='text-center' id='IdCarreaExcel_" + lstCarreras.get(i).getID_Carrera() + "'>" + lstCarreras.get(i).getId_Carrera_Excel() + "</td>";
                RESP += "<td data-container='body' data-toggle='tooltip' data-placement='top' title='" + lstCarreras.get(i).getNombreCarrera() + "' class='text-left' id='Descripcion_" + lstCarreras.get(i).getID_Carrera() + "'>" + lstCarreras.get(i).getNombreCarrera() + "</td>";
                RESP += "<td class='text-center hidden-xs hidden-sm' id='ClaveInstitucion_" + lstCarreras.get(i).getID_Carrera() + "'>" + lstCarreras.get(i).getClaveInstitucion() + "</td>";
                RESP += "<td data-container='body' data-toggle='tooltip' data-placement='top' title='" + lstCarreras.get(i).getNombreInstitucion() + "' class='text-left hidden-xs' id='NombreInstitucion_" + lstCarreras.get(i).getID_Carrera() + "'>" + lstCarreras.get(i).getNombreInstitucion() + "</td>";
                RESP += "<td class='text-center' id='Acciones_" + lstCarreras.get(i).getID_Carrera() + "'>";
                if (cabecera.contains("todos")) {
                    RESP += "<div class='btn-group'>"
                            + "<button class='btn btn-default btn-xs btnConsultarCarrera' data-container='body' type='button' data-toggle='tooltip' id='consultarCarrera_" + lstCarreras.get(i).getID_Carrera() + "' value='cstCarrera_" + lstCarreras.get(i).getID_Carrera() + "' data-original-title='Consultar'><i class='fa fa-search'></i></button>"
                            + "<button class='btn btn-default btn-xs btnEditarCarrera' data-container='body' type='button' data-toggle='tooltip' id='editarCarrera_" + lstCarreras.get(i).getID_Carrera() + "' value='edtCarrera_" + lstCarreras.get(i).getID_Carrera() + "' data-original-title='Editar'><i class='fa fa-pencil'></i></button>"
                            + "</div>";
                } else if (cabecera.contains("acceso")) {
                    if (permisos.split("¬")[2].equalsIgnoreCase("1") || permisos.split("¬")[3].equalsIgnoreCase("1")) {
                        RESP += "<div class='btn-group'>";
                        if (permisos.split("¬")[2].equalsIgnoreCase("1")) {
                            RESP += "<button class='btn btn-default btn-xs btnConsultarCarrera' data-container='body' type='button' data-toggle='tooltip' id='consultarCarrera_" + lstCarreras.get(i).getID_Carrera() + "' value='cstCarrera_" + lstCarreras.get(i).getID_Carrera() + "' data-original-title='Consultar'><i class='fa fa-search'></i></button>";
                        }
                        if (permisos.split("¬")[3].equalsIgnoreCase("1")) {
                            RESP += "<button class='btn btn-default btn-xs btnEditarCarrera' data-container='body' type='button' data-toggle='tooltip' id='editarCarrera_" + lstCarreras.get(i).getID_Carrera() + "' value='edtCarrera_" + lstCarreras.get(i).getID_Carrera() + "' data-original-title='Editar'><i class='fa fa-pencil'></i></button>";
                        }
                        RESP += "</div>";
                    } else {
                        RESP += "<span class='label label-danger'>No disponible</span>";
                    }
                }

                RESP += "</td></tr>";
            }

            RESP += "    </tbody>"
                    + "</table>";

        } catch (SQLException ex) {
            RESP = "error|Error SQL al realizar la carga de la tabla de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al realizar la carga de la tabla de carreras: " + accion_catch(ex);
        }
        return RESP;
    }

    private String ConsultarCarrera() {
        String RESP = "";
        String anexoRESP = "";
        String idCarrera = request.getParameter("idCarrera");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        String idCarreraExcel = "";
        String numDecimales = "";
        try {
            String Query = "SELECT * FROM TETitulosCarreras WHERE id_carrera=" + idCarrera;
            rs = conector.consulta(Query);
            if (rs.next()) {
                idCarreraExcel = rs.getString("id_Carrera_Excel");
                Date expedicionRvoe = null;
                RESP += "~" + rs.getString("id_Carrera");
                RESP += "~" + rs.getString("cveInstitucion");
                RESP += "~" + rs.getString("nombreInstitucion");
                RESP += "~" + rs.getString("cveCarrera");
                RESP += "~" + rs.getString("nombreCarrera");
                RESP += "~" + rs.getString("idAutorizacionReconocimiento");
                RESP += "~" + rs.getString("numeroRvoe");
                RESP += "~" + rs.getString("cveCampus");
                RESP += "~" + rs.getString("Campus");
                RESP += "~" + rs.getString("id_TipoPeriodo");
                RESP += "~" + rs.getString("id_nivel");
                RESP += "~" + rs.getString("id_EntidadFederativa");
                expedicionRvoe = parseador.parse(rs.getString("FechaExpedicionRvoe"));
                RESP += "~" + formateador.format(expedicionRvoe);
                anexoRESP += "~" + (rs.getString("gradoObtenidoM") == null || rs.getString("gradoObtenidoM").equalsIgnoreCase("") ? "No registrado" : rs.getString("gradoObtenidoM"));
                anexoRESP += "~" + (rs.getString("gradoObtenidoF") == null || rs.getString("gradoObtenidoF").equalsIgnoreCase("") ? "No registrado" : rs.getString("gradoObtenidoF"));
                anexoRESP += "~" + idCarreraExcel;

                double dCalifiMin = Double.parseDouble(rs.getString("califMinima"));
                //int iCalifMin = (int) dCalifiMin;

                double dCalifMax = Double.parseDouble(rs.getString("califMaxima"));
                //int iCalifMax = (int) dCalifMax;

                anexoRESP += "~" + dCalifiMin;
                anexoRESP += "~" + dCalifMax;

                double dCalifMinAprobatoria = Double.parseDouble(rs.getString("califMinimaAprobatoria"));
                if (dCalifMinAprobatoria == 10) {
                    int iCalifMinAprob = (int) dCalifMax;
                    anexoRESP += "~" + iCalifMinAprob;
                } else {
                    String sCalifMinAprobatoria = String.format(Locale.US, "%.2f", dCalifMinAprobatoria);
                    anexoRESP += "~" + sCalifMinAprobatoria;
                }
                anexoRESP += "~" + rs.getString("cvePlan");
                Query = "SELECT TotalMaterias,TotalCreditos,numDecimales FROM Carrera WHERE Id_Carrera_Excel = " + idCarreraExcel;
                rs = conector.consulta(Query);
                if (rs.next()) {
                    RESP += "~" + rs.getString("TotalMaterias") + "~" + rs.getString("TotalCreditos");
                    numDecimales = rs.getString("numDecimales");
                } else {
                    RESP += "~0~0";
                    numDecimales = "2";
                }
                RESP += anexoRESP + "~" + numDecimales;
            }
        } catch (NumberFormatException ex) {
            RESP = "error|Error NumberFormatException al momento de consultar la carrera: " + accion_catch(ex);
        } catch (SQLException ex) {
            RESP = "error|Error SQLException al momento de consultar la carrera: " + accion_catch(ex);
        } catch (ParseException ex) {
            RESP = "error|Error ParseException al momento de consultar la carrera: " + accion_catch(ex);
        } catch (Exception ex) {
            RESP = "error|Error inesperado al momento de consultar la carrera: " + accion_catch(ex);
        }
        return RESP;
    }

    private String CargarAutorizacionesReconocimiento() {
        String RESP = "";
        TEAutorizaciones autorizacion;
        List<TEAutorizaciones> lstAutorizaciones;

        try {
            String Query = "SELECT * FROM TEAutorizaciones";
            rs = conector.consulta(Query);
            lstAutorizaciones = new ArrayList<>();
            while (rs.next()) {
                autorizacion = new TEAutorizaciones();

                autorizacion.setID_AutorizacionReconcimiento(rs.getString("idAutorizacionReconocimiento"));
                autorizacion.setAutorizacionReconocimiento(rs.getString("autorizacionReconocimiento"));
                lstAutorizaciones.add(autorizacion);
            }

            RESP += "success|<option></option>";

            for (int i = 0; i < lstAutorizaciones.size(); i++) {
                RESP += "<option value='" + lstAutorizaciones.get(i).getID_AutorizacionReconcimiento() + "'>" + lstAutorizaciones.get(i).getAutorizacionReconocimiento() + "</option>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al cargar la lista de autorización reconocimiento: " + accion_catch(ex) + "|<option>error SQL</option>";
        } catch (Exception ex) {
            RESP = "error|Error inesperado al cargar la lista de autorización reconocimiento: " + accion_catch(ex) + "|<option>error</option>";
        }
        return RESP;
    }

    private String CargarTiposPeriodo() {
        String RESP = "";
        TETipoPeriodo tipoPeriodo;
        List<TETipoPeriodo> lstTiposPeriodos;
        try {
            String Query = "SELECT * FROM TETipoPeriodo;";
            rs = conector.consulta(Query);
            lstTiposPeriodos = new ArrayList<>();
            while (rs.next()) {
                tipoPeriodo = new TETipoPeriodo();

                tipoPeriodo.setID_TipoPEriodo(rs.getString("id_TipoPeriodo"));
                tipoPeriodo.setTipoPeriodo(rs.getString("TipoPeriodo"));
                lstTiposPeriodos.add(tipoPeriodo);
            }

            RESP += "success|<option></option>";

            for (int i = 0; i < lstTiposPeriodos.size(); i++) {
                RESP += "<option value='" + lstTiposPeriodos.get(i).getID_TipoPEriodo() + "'>" + lstTiposPeriodos.get(i).getTipoPeriodo() + "</option>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al cargar la lista de tipos de periodo: " + accion_catch(ex) + "|<option>error SQL</option>";
        } catch (Exception ex) {
            RESP = "error|Error inesperado al cargar la lista de tipos de periodo: " + accion_catch(ex) + "|<option>error</option>";
        }

        return RESP;
    }

    private String CargarNivelEducativo() {
        String RESP = "";
        TENivelEducativo nivel;
        List<TENivelEducativo> lstNiveles;

        try {
            String Query = "SELECT * FROM TENivelEducativo;";
            rs = conector.consulta(Query);
            lstNiveles = new ArrayList<>();
            while (rs.next()) {
                nivel = new TENivelEducativo();

                nivel.setID_Nivel(rs.getString("ID_Nivel"));
                nivel.setNivel(rs.getString("Nivel"));
                lstNiveles.add(nivel);
            }

            RESP += "|success<option></option>";
            for (int i = 0; i < lstNiveles.size(); i++) {
                RESP += "<option value='" + lstNiveles.get(i).getID_Nivel() + "'>" + lstNiveles.get(i).getNivel() + "</option>";
            }
        } catch (SQLException ex) {
            RESP = "error|Error SQL al cargar la lista de niveles educativos: " + accion_catch(ex) + "|<option>error SQL</option>";
        } catch (Exception ex) {
            RESP = "error|Error inesperado al cargar la lista de niveles educativos: " + accion_catch(ex) + "|<option>error</option>";
        }

        return RESP;
    }

    private String CargarEntidadFederativa() {
        String RESP = "";
        TEEntidad entidadFederativa;
        List<TEEntidad> lstEntidades;

        try {
            String Query = "SELECT * FROM TEEntidad;";
            rs = conector.consulta(Query);
            lstEntidades = new ArrayList<>();
            while (rs.next()) {
                entidadFederativa = new TEEntidad();

                entidadFederativa.setID_EntidadFederativa(rs.getString("idEntidadFederativa"));
                entidadFederativa.setEntidadFederativa(rs.getString("EntidadFederativa"));
                entidadFederativa.setAbreviatura(rs.getString("Abreviatura"));
                lstEntidades.add(entidadFederativa);
            }

            RESP += "success|<option></option>";
            for (int i = 0; i < lstEntidades.size(); i++) {
                RESP += "<option value='" + lstEntidades.get(i).getID_EntidadFederativa() + "'>" + lstEntidades.get(i).getEntidadFederativa() + "</option>";
            }

        } catch (SQLException ex) {
            RESP = "error|Error SQL al cargar la lista de entidades federativas: " + accion_catch(ex) + "|<option>error SQL</option>";
        } catch (Exception ex) {
            RESP = "error|Error inesperado al cargar la lista de entidades federativas: " + accion_catch(ex) + "|<option>error</option>";
        }

        return RESP;
    }

    private String accion_catch(Exception ex) {
        String resp = "";
        try {
            resp = ex.toString().replace("'", "\"");
            Logger.getLogger(CClavesCarrera.class.getName()).log(Level.SEVERE, null, ex);
            resp = "<h4 style='color:#e76d6d'>" + resp;
            resp += "</h4>";
        } catch (Exception e) {
            resp += "</h4><small class='text-primary'>No se ha insertado en Bitacora de errores</small>";
        }
        resp += "<br><br><small>Si continua con el problema, comuníquese con soporte técnico.</small> ";
        return resp;
    }

}
