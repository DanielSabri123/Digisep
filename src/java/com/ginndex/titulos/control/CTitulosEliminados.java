package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Alumno;
import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.Persona;
import com.ginndex.titulos.modelo.TETituloElectronico;
import com.ginndex.titulos.modelo.TETitulosCarreras;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Ricardo Reyna
 */
public class CTitulosEliminados {

    HttpServletRequest request;
    HttpServletResponse response;
    Connection con;
    PreparedStatement pstmt;
    CallableStatement cstmt;
    String Bandera;
    String Id_Usuario;
    ResultSet rs;
    String usuario;
    Cxml_titulo_electronico titElec;
    CConexion conexion;
    private String permisos;
    String NombreInstitucion;
    String nombreLogo;
    String nombreFirma;
    Bitacora bitacora;
    CBitacora cBitacora;
    String usuarioSEP;
    String passwordSEP;

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

    public CallableStatement getCstmt() {
        return cstmt;
    }

    public void setCstmt(CallableStatement cstmt) {
        this.cstmt = cstmt;
    }

    public CConexion getConexion() {
        return conexion;
    }

    public void setConexion(CConexion conexion) {
        this.conexion = conexion;
    }

    public String EstablecerAcciones() {
        String RESP = "";
        setBandera(request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera"));
        switch (this.Bandera) {
            case "1":
                RESP = cargarListaCarreras();
                break;
            case "2":
                RESP = consultarTablaTitulosEliminados();
                break;
            case "3":
                RESP = activarTitulo();
                break;
            default:
                RESP = "no hay ninguna opcion seleccionada";
                break;
        }

        return RESP;
    }

    private String cargarListaCarreras() {
        StringBuilder RESP = new StringBuilder();
        pstmt = null;
        con = null;
        rs = null;
        try {
            conexion = new CConexion();
            List<TETitulosCarreras> lstCarrera = new ArrayList<TETitulosCarreras>();
            String Query = "select distinct TE.id_carrera, nombreCarrera,TipoPeriodo from  TETituloElectronico_Eliminados te\n"
                    + "JOIN TETitulosCarreras TC   on TC.id_carrera= TE.id_carrera\n"
                    + "JOIN TETipoPeriodo TP ON TC.id_TipoPeriodo = TP.id_TipoPeriodo";
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();

            pstmt = con.prepareStatement(Query);

            rs = pstmt.executeQuery();

            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    TETitulosCarreras tc = new TETitulosCarreras();

                    tc.setID_Carrera(rs.getString(1));

                    tc.setNombreCarrera(rs.getString(2));
                    tc.setID_TipoPeriodo(rs.getString(3));

                    lstCarrera.add(tc);
                }
                RESP.append("success|");
                RESP.append("<option value='todos'>Todas las carreras</option>");
                for (int i = 0; i < lstCarrera.size(); i++) {
                    RESP.append("<option value='")
                            .append(lstCarrera.get(i).getID_Carrera())
                            .append("'>")
                            .append(lstCarrera.get(i).getID_Carrera())
                            .append(" - ")
                            .append(lstCarrera.get(i).getNombreCarrera())
                            .append(" - ")
                            .append(lstCarrera.get(i).getID_TipoPeriodo())
                            .append("</option>");
                }
            } else {
                RESP.append("empty|");
                RESP.append("<option value='empty'>No se encontraron carreras</option>");
            }

        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de carreras: " + accion_catch(ex);
        }
        return RESP.toString();
    }

    private String activarTitulo() {
        String idTitulo = request.getParameter("idTitulo");
        StringBuilder RESP = new StringBuilder();
        try {
            String Query = "{call RestaurarTitulo (?)}";

            conexion = new CConexion();
            conexion.setRequest(request);
            con = null;
            cstmt = null;
            con = conexion.GetconexionInSite();

            cstmt = con.prepareCall(Query);
            cstmt.setString(1, idTitulo);
            cstmt.execute();
            cstmt.close();
            con.close();
            RESP.append("success|Se ha restaurado el titulo ").append(idTitulo).append(" con exito");
        } catch (SQLException ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error SQL al realizar carga de carreras: " + accion_catch(ex);
        } catch (Exception ex) {
            Logger.getLogger(CCarrerasCarga.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Ocurrió un error inesperado al realizar carga de carreras: " + accion_catch(ex);
        }

        return RESP.toString();

    }

    private String consultarTablaTitulosEliminados() {
        String idCarrera = request.getParameter("idCarrera");
        //String RESP = "";
        StringBuilder RESP = new StringBuilder();

        TETituloElectronico te;
        Alumno a;
        Persona p;
        TETitulosCarreras c;
        List<TETituloElectronico> lstTitulos;
        Date date;
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat parseador = new SimpleDateFormat("yyyy-MM-dd");
        conexion = new CConexion();
        conexion.setRequest(request);
        con = null;
        pstmt = null;
        try {
            //Se añadio a la consulta TE.estatusMET, TE.mensajeMET para extraer los datos y poner el icono en la columna MET
            String Query = "SELECT TE.id_titulo, TE.version, TE.folioControl, TE.id_profesionista, "
                    + " TE.estatus, TE.fechaExpedicion, TE.idEntidadFederativa,A.Matricula, P.Nombre, "
                    + " P.APaterno,P.Amaterno, E.EntidadFederativa, TC.nombreCarrera, TE.envioSEP, TE.envioSepProductivo, TE.id_carrera"
                    + " FROM TETituloElectronico_Eliminados AS TE "
                    + " JOIN Alumnos AS A ON A.ID_Alumno = TE.id_profesionista "
                    + " JOIN Persona AS P ON P.Id_Persona = A.ID_Persona "
                    + " JOIN TEEntidad AS E ON E.idEntidadFederativa = TE.idEntidadFederativa "
                    + " JOIN TETitulosCarreras AS TC ON TC.id_carrera = TE.id_carrera " + (!idCarrera.equalsIgnoreCase("todos") ? (!idCarrera.contains(",") ? " AND TE.id_carrera = " + idCarrera : " AND TE.id_titulo in (" + idCarrera.substring(0, idCarrera.length() - 1) + ")") : "");

            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);
            rs = pstmt.executeQuery();

            lstTitulos = new ArrayList<>();
            while (rs.next()) {
                te = new TETituloElectronico();
                a = new Alumno();
                p = new Persona();
                c = new TETitulosCarreras();
                te.setID_Titulo(rs.getString("id_titulo"));
                te.setVersion(rs.getString("version"));
                te.setFolioControl(rs.getString("folioControl"));
                te.setID_Profesionista(rs.getString("id_profesionista"));
                te.setID_EntidadFederativa(rs.getString("idEntidadFederativa"));
                te.setID_EntidadFederativaAntecedente(rs.getString("EntidadFederativa"));// Se guarda el nombre de la entidad de expedición
                te.setEstatus(rs.getString("estatus"));
                te.setFechaExpedicion(rs.getString("fechaExpedicion"));
                te.setEnvioSep(rs.getBoolean("envioSEP"));
                te.setEnvioSepProductivo(rs.getBoolean("envioSepProductivo"));
                a.setId_Alumno(rs.getString("id_profesionista"));
                a.setMatricula(rs.getString("Matricula"));
                c.setID_Carrera(rs.getString("id_carrera"));
                c.setNombreCarrera(rs.getString("nombreCarrera"));
                p.setNombre(rs.getString("Nombre"));
                p.setAPaterno(rs.getString("APaterno"));
                p.setAMaterno(rs.getString("Amaterno"));
                a.setPersona(p);
                te.setCarrera(c);
                te.setAlumno(a);
                lstTitulos.add(te);
            }

            RESP.append("success|<table class='table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed' style='width:100%;'  id='tblTitulos'>"
                    + "    <thead class='bg-primary-dark' style='color: white;'>"
                    + "        <tr>"
                    + "            <th class='text-center' style='display:none;'>ID_Titulo</th>"
                    + "            <th class='text-center hidden-sm hidden-xs'>Folio</th>"
                    + "            <th class='text-center'>Matrícula</th>"
                    + "            <th class='text-center hidden-xs'>Nombre(s)</th>"
                    + "            <th class='text-center hidden-xs'>A. Paterno</th>"
                    + "            <th class='text-center hidden-xs'>Carrera</th>"
                    + "            <th class='text-center hidden-xs'>Expedición</th>"
                    + "            <th class='text-center hidden-md hidden-sm hidden-xs'>Estatus</th>"
                    + "            <th class='text-center'>Acciones</th>"
                    + "        </tr>"
                    + "    </thead>"
                    + "    <tbody>");

            StringBuilder anexoTabla = new StringBuilder();
            for (int i = 0; i < lstTitulos.size(); i++) {
                String bandera = "";
                date = parseador.parse(lstTitulos.get(i).getFechaExpedicion());
                RESP.append("<tr>");
                RESP.append("<td class='text-center' style='display:none;' id='IDTitulo_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getID_Titulo()).append("</td>");
                RESP.append("<td class='hidden-sm hidden-xs' id='FolioControl_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getFolioControl()).append("</td>");
                RESP.append("<td id='Matricula_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getAlumno().getMatricula()).append("</td>");
                RESP.append("<td class='hidden-xs' id='Nombre_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getAlumno().getPersona().getNombre()).append("</td>");
                RESP.append("<td class='hidden-xs' id='APaterno_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getAlumno().getPersona().getAPaterno()).append("</td>");
                RESP.append("<td class='hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top' id-carrera='").append(lstTitulos.get(i).getCarrera().getID_Carrera()).append("' title='").append(lstTitulos.get(i).getCarrera().getNombreCarrera()).append("' id='Carrera_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(lstTitulos.get(i).getCarrera().getNombreCarrera()).append("</td>");
                RESP.append("<td class='text-center hidden-xs' id='FechaExpedicion_").append(lstTitulos.get(i).getID_Titulo()).append("'>").append(formateador.format(date)).append("</td>");

                if (lstTitulos.get(i).getEstatus().equalsIgnoreCase("0")) {
                    RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                            .append("<span class='label label-warning'>XML PENDIENTE.</span>").append("</td>");
                } else {
                    if (lstTitulos.get(i).isEnvioSep() && !lstTitulos.get(i).isEnvioSepProductivo() && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-primary'>ENVIADO A PRUEBAS.</span>").append("</td>");
                    } else if (lstTitulos.get(i).isEnvioSepProductivo() && !lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-success'>ENVIADO A PRODUCTIVO.</span>").append("</td>");
                    } else if (lstTitulos.get(i).getEstatus().equalsIgnoreCase("2")) {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-danger'>CANCELADO EN PRODUCTIVO.</span>").append("</td>");
                    } else {
                        RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' data-container='body' data-toggle='tooltip' data-placement='top'>")
                                .append("<span class='label label-warning'>SIN ENVIAR A SERVICIO.</span>").append("</td>");
                    }

                }
                RESP.append("<td class='text-center hidden-md hidden-sm hidden-xs' id='idTDSpan_").append(lstTitulos.get(i).getFolioControl())
                        .append("'><span class='label span_clickReactivar' tabindex='0' data-toggle='tooltip' title='Reactivar el titulo ").append(lstTitulos.get(i).getFolioControl())
                        .append("' data-idTitulo='").append(lstTitulos.get(i).getID_Titulo()).append("' id='idSpanFolio_").append(lstTitulos.get(i).getFolioControl())
                        .append("'><i class='fa-3x fa fa-undo' style='color:green;'></i>").append("</td>");

                //RESP.append(anexoTabla);
                RESP.append("</tr>");
                //anexoTabla.setLength(0);
            }

            RESP.append("</tbody>"
                    + "</table>");
        } catch (SQLException ex) {
            RESP.setLength(0);
            RESP.append("error|Error SQL al realizar la consulta de títulos electrónicos: ").append(accion_catch(ex));
        } catch (ParseException ex) {
            RESP.setLength(0);
            RESP.append("error|Error ParseException al realizar la consulta de títulos electrónicos: ").append(accion_catch(ex));
        } catch (Exception ex) {
            RESP.setLength(0);
            RESP.append("error|Error inesperado al realizar la consulta de títulos electrónicos: ").append(accion_catch(ex));
        } finally {
            try {
                con.close();
                rs.close();
                pstmt.close();
                conexion = null;
            } catch (SQLException ex) {
                Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return RESP.toString();
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
