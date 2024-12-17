/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.util.MapComparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Workbook;
import org.digisep.reportes.Reportes;

/**
 *
 * @author BSorcia
 */
public class CReportes {

    HttpServletRequest request;
    HttpServletResponse response;
    Connection con;
    PreparedStatement pstmt;
    CallableStatement cstmt;
    String bandera;
    String Id_Usuario;
    private String permisos;
    CConexion conexion;
    String NombreInstitucion;
    List<String> encabezadoMovEliminado;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public CReportes() {
        encabezadoMovEliminado = new ArrayList<>();
        encabezadoMovEliminado.add("Terminación Folio Doc. Electrónico");
        encabezadoMovEliminado.add("Fecha Movimiento");
        encabezadoMovEliminado.add("Hora Movimiento");
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String establecerAcciones() throws UnsupportedEncodingException {
        String RESP = "";
        //long startTime = System.nanoTime();
        bandera = request.getParameter("txtBandera") == null ? "0" : request.getParameter("txtBandera");
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");

        HttpSession sessionOk = request.getSession();
        Id_Usuario = sessionOk.getAttribute("Id_Usuario").toString();
        conexion = new CConexion();
        conexion.setRequest(request);
        CPermisos cPermisos = new CPermisos();
        cPermisos.setRequest(request);
        permisos = cPermisos.obtenerPermisos("Reportes");
        switch (bandera) {
            case "1":
                RESP = consultarLIstaCarreras();
                break;
            case "2":
                RESP = armarReporte();
                break;
        }
        return RESP;
    }

    private String consultarLIstaCarreras() {
        StringBuilder tbl = new StringBuilder();
        try {
            String query = "SELECT id_carrera,Id_Carrera_Excel,nombreCarrera,cveCarrera FROM TETitulosCarreras";
            ResultSet rs;
            con = conexion.GetconexionInSite();
            rs = con.prepareStatement(query).executeQuery();
            List<Map<String, Object>> carreras = resultSetToArrayList(rs);
            if (!carreras.isEmpty()) {
                tbl.append("<option value='0' data-cve=''>Todas</option>");
                carreras.forEach(carrera -> {
                    tbl.append("<option value='")
                            .append(carrera.get("id_carrera")).append("' data-cve='")
                            .append(carrera.get("cveCarrera"))
                            .append("'>").append(carrera.get("Id_Carrera_Excel")).append(" - ")
                            .append(carrera.get("nombreCarrera"))
                            .append("</option>");
                });
            }

        } catch (SQLException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tbl.toString();
    }

    private String armarReporte() {
        try {
            if (permisos.contains("acceso") && !permisos.split("¬")[5].equalsIgnoreCase("1")) {
                return "notAllowed";
            }
            ResultSet rs1 = null;
            ResultSet rs2 = null;
            List lista1 = null;
            List lista2 = null;
            List encabezados = null;
            List encabezados2 = null;
            String query1 = "";
            String query2 = "";
            int opcion = Integer.parseInt(request.getParameter("opt"));
            int consultarEliminados = Integer.parseInt(request.getParameter("deleted"));
            String date1 = request.getParameter("date1");
            String date2 = request.getParameter("date2");
            int carrera = Integer.parseInt(request.getParameter("carrera"));
            String CveCarrera = request.getParameter("cve");
            Reportes rep = new Reportes(opcion);

            switch (opcion) {
                case 1:
                    query1 = "SET DATEFORMAT dmy;SELECT FolioControl as 'Folio de Control',Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "fechaInicio as 'Fecha Inicio Carrera', fechaTerminacion AS 'Fecha Fin Carrera',fechaExpedicion as 'Fecha Expedicion', fechaExtencionExamenProfesional as 'Fecha Exención Examen', \n"
                            + "fechaExamenProfesional as 'Fecha Examen Profesional',\n"
                            + "CASE WHEN tte.estatus = 2 THEN 'CANCELADO EN PRODUCTIVO' ELSE CASE WHEN envioSepProductivo = 1 THEN 'ENVIADO A PRODUCTIVO' ELSE 'SIN ENVIAR A PRODUCTIVO' END end as 'Estatus DigiSep'"
                            + ",'Activo en Sistema'  as SISTEMA\n"
                            + "FROM TETITULOELECTRONICO tte JOIN ALUMNOS a on tte.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tte.id_carrera = ttc.id_carrera\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "' " + (carrera != 0 ? "AND tte.Id_Carrera =" + carrera : "") + "\n";
                    query1 += "UNION\n"
                            + "SELECT FolioControl as 'Folio de Control',Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "fechaInicio as 'Fecha Inicio Carrera', fechaTerminacion AS 'Fecha Fin Carrera',fechaExpedicion as 'Fecha Expedicion', fechaExencionExamenProfesional as 'Fecha Exención Examen', \n"
                            + "fechaExamenProfesional as 'Fecha Examen Profesional',\n"
                            + "CASE WHEN tte.estatus = 2 THEN 'CANCELADO EN PRODUCTIVO' ELSE CASE WHEN envioSepProductivo = 1 THEN 'ENVIADO A PRODUCTIVO' ELSE 'SIN ENVIAR A PRODUCTIVO' END end as 'Estatus DigiSep','Eliminado'  as SISTEMA\n"
                            + "FROM TETituloElectronico_Eliminados tte JOIN ALUMNOS a on tte.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tte.id_carrera = ttc.id_carrera\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "' " + (carrera != 0 ? "AND tte.Id_Carrera =" + carrera : "") + "\n";

                    query1 += "ORDER BY [Folio de Control]";
                    break;
                case 2:
                    query1 = "SET DATEFORMAT dmy;SELECT version as 'Versión DEC',FolioControl as 'Folio de Control',ttc.Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "CAST(fechaExpedicion AS DATE) as 'Fecha de Expedición',\n"
                            + "TOTAL as 'Total Asignaturas', Asignadas as 'Asignaturas Asignadas', Promedio, creditosTotal as 'Total de créditos', "
                            + "creditosObtenidos as 'Creditos Obtenidos',numeroCiclos as 'Número de Ciclos',"
                            + "(CASE WHEN idTipoCertificado = 79 THEN 'COMPLETO' ELSE 'PARCIAL' END) AS Tipo,'Activo en Sistema'  as SISTEMA\n"
                            + "FROM TECERTIFICADOELECTRONICO tce JOIN ALUMNOS a on tce.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tce.Id_Carrera_Excel = ttc.Id_Carrera_Excel\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "'" + (carrera != 0 ? "AND tce.Id_Carrera =" + carrera : "") + "\n"
                            + "UNION\n"
                            + "SELECT version as 'Versión DEC',FolioControl as 'Folio de Control',ttc.Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "CAST(fechaExpedicion AS DATE) as 'Fecha de Expedición',\n"
                            + "TOTAL as 'Total Asignaturas', Asignadas as 'Asignaturas Asignadas', Promedio, creditosTotal as 'Total de créditos', "
                            + "creditosObtenidos as 'Creditos Obtenidos',numeroCiclos as 'Número de Ciclos',"
                            + "(CASE WHEN idTipoCertificado = 79 THEN 'COMPLETO' ELSE 'PARCIAL' END) AS Tipo, 'Eliminado'  as SISTEMA\n"
                            + "FROM TECertificadoElectronico_Eliminados tce JOIN ALUMNOS a on tce.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tce.Id_Carrera_Excel = ttc.Id_Carrera_Excel\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "'" + (carrera != 0 ? "AND tce.Id_Carrera =" + carrera : "") + "\n";
                    query1 += "ORDER BY [Folio de Control]";
                    break;
                default:
                    query1 = "SET DATEFORMAT dmy;SELECT FolioControl as 'Folio de Control',Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "fechaInicio as 'Fecha Inicio Carrera', fechaTerminacion AS 'Fecha Fin Carrera',fechaExpedicion as 'Fecha Expedicion', fechaExtencionExamenProfesional as 'Fecha Exención Examen', \n"
                            + "fechaExamenProfesional as 'Fecha Examen Profesional',\n"
                            + "CASE WHEN tte.estatus = 2 THEN 'CANCELADO EN PRODUCTIVO' ELSE CASE WHEN envioSepProductivo = 1 THEN 'ENVIADO A PRODUCTIVO' ELSE 'SIN ENVIAR A PRODUCTIVO' END end as 'Estatus DigiSep'"
                            + ",'Activo en Sistema'  as SISTEMA\n"
                            + "FROM TETITULOELECTRONICO tte JOIN ALUMNOS a on tte.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tte.id_carrera = ttc.id_carrera\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "' " + (carrera != 0 ? "AND tte.Id_Carrera =" + carrera : "") + "\n";
                    query1 += "UNION\n"
                            + "SELECT FolioControl as 'Folio de Control',Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "fechaInicio as 'Fecha Inicio Carrera', fechaTerminacion AS 'Fecha Fin Carrera',fechaExpedicion as 'Fecha Expedicion', fechaExencionExamenProfesional as 'Fecha Exención Examen', \n"
                            + "fechaExamenProfesional as 'Fecha Examen Profesional',\n"
                            + "CASE WHEN tte.estatus = 2 THEN 'CANCELADO EN PRODUCTIVO' ELSE CASE WHEN envioSepProductivo = 1 THEN 'ENVIADO A PRODUCTIVO' ELSE 'SIN ENVIAR A PRODUCTIVO' END end as 'Estatus DigiSep','Eliminado'  as SISTEMA\n"
                            + "FROM TETituloElectronico_Eliminados tte JOIN ALUMNOS a on tte.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tte.id_carrera = ttc.id_carrera\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "' " + (carrera != 0 ? "AND tte.Id_Carrera =" + carrera : "") + "\n";

                    query1 += "ORDER BY [Folio de Control]";

                    query2 = "SET DATEFORMAT dmy;SELECT version as 'Versión DEC',FolioControl as 'Folio de Control',ttc.Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "CAST(fechaExpedicion AS DATE) as 'Fecha de Expedición',\n"
                            + "TOTAL as 'Total Asignaturas', Asignadas as 'Asignaturas Asignadas', Promedio, creditosTotal as 'Total de créditos',"
                            + " creditosObtenidos as 'Creditos Obtenidos',numeroCiclos as 'Número de Ciclos',"
                            + "(CASE WHEN idTipoCertificado = 79 THEN 'COMPLETO' ELSE 'PARCIAL' END) AS Tipo,'Activo en Sistema'  as SISTEMA\n"
                            + "FROM TECERTIFICADOELECTRONICO tce JOIN ALUMNOS a on tce.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tce.Id_Carrera_Excel = ttc.Id_Carrera_Excel\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "'" + (carrera != 0 ? "AND tce.Id_Carrera =" + carrera : "") + "\n"
                            + "UNION\n"
                            + "SELECT version as 'Versión DEC',FolioControl as 'Folio de Control',ttc.Id_Carrera_Excel AS 'Id de Carrera',\n"
                            + "cveCarrera as 'Clave Carrera' ,nombreCarrera as 'Nombre Carrera',matricula as 'Matricula Alumno', Nombre, Apaterno as 'Apellido Paterno', Amaterno as 'Apellido Materno',\n"
                            + "CAST(fechaExpedicion AS DATE) as 'Fecha de Expedición',\n"
                            + "TOTAL as 'Total Asignaturas', Asignadas as 'Asignaturas Asignadas', Promedio, creditosTotal as 'Total de créditos',"
                            + " creditosObtenidos as 'Creditos Obtenidos',numeroCiclos as 'Número de Ciclos',"
                            + "(CASE WHEN idTipoCertificado = 79 THEN 'COMPLETO' ELSE 'PARCIAL' END) AS Tipo,'Eliminado'  as SISTEMA\n"
                            + "FROM TECertificadoElectronico_Eliminados tce JOIN ALUMNOS a on tce.Id_profesionista = a.id_alumno\n"
                            + "JOIN PERSONA p ON a.ID_Persona = p.Id_Persona\n"
                            + "JOIN TETITULOSCARRERAS ttc ON tce.Id_Carrera_Excel = ttc.Id_Carrera_Excel\n"
                            + "WHERE fechaExpedicion between '" + date1 + "' and '" + date2 + "'" + (carrera != 0 ? "AND tce.Id_Carrera =" + carrera : "") + "\n";
                    query2 += "ORDER BY [Folio de Control]";

                    break;
            }
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            rs1 = con.createStatement().executeQuery(query1);
            ResultSetMetaData rsmd = rs1.getMetaData();
            int columnCount = rsmd.getColumnCount();
            encabezados = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                encabezados.add(rsmd.getColumnName(i));
            }
            lista1 = resultSetToArrayList(rs1);
            rs1.close();

            if (!query2.isEmpty()) {
                rs2 = con.createStatement().executeQuery(query2);
                rsmd = rs2.getMetaData();
                columnCount = rsmd.getColumnCount();
                encabezados2 = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    encabezados2.add(rsmd.getColumnName(i));
                }
                lista2 = resultSetToArrayList(rs2);
                rs2.close();
            }
            if (lista1.isEmpty()) {
                if (lista2 != null && lista2.isEmpty()) {
                    return "empty";
                } else if (lista2 == null) {
                    return "empty";
                }
            }
            switch (opcion) {
                case 1:
                    llenarTitulosEliminadosTeXML(lista1, consultarTitulosTeXML(), date1, date2, CveCarrera);
                    Collections.sort(lista1, new MapComparator("Folio de Control", (short) 1));
                    break;
                case 2:
                    llenarCertificadosEliminadosTeXML(lista1, consultarCertificadosTeXML(), date1, date2, carrera);
                    Collections.sort(lista1, new MapComparator("Folio de Control", (short) 1));
                    break;
                default:
                    llenarTitulosEliminadosTeXML(lista1, consultarTitulosTeXML(), date1, date2, CveCarrera);
                    Collections.sort(lista1, new MapComparator("Folio de Control", (short) 1));
                    llenarCertificadosEliminadosTeXML(lista2, consultarCertificadosTeXML(), date1, date2, carrera);
                    Collections.sort(lista2, new MapComparator("Folio de Control", (short) 1));
                    break;
            }

            switch (opcion) {
                case 1:
                    rep.setListaTitulos(lista1, encabezados);
                    if (consultarEliminados == 1) {
                        rep.setListaMovEliminados(consultarMovEliminados("Titulos"), encabezadoMovEliminado);
                    }
                    break;
                case 2:
                    rep.setListaCertificados(lista1, encabezados);
                    if (consultarEliminados == 1) {
                        rep.setListaCertMovEliminados(consultarMovEliminados("Certificados"), encabezadoMovEliminado);
                    }
                    break;
                default:
                    rep.setListaTitulos(lista1, encabezados);
                    rep.setListaCertificados(lista2, encabezados2);
                    if (consultarEliminados == 1) {
                        rep.setListaMovEliminados(consultarMovEliminados("Titulos"), encabezadoMovEliminado);
                        rep.setListaCertMovEliminados(consultarMovEliminados("Certificados"), encabezadoMovEliminado);
                    }
                    break;
            }
            String fechaGen = sdf.format(new Date());
            String name = (opcion == 1 ? "Títulos" : opcion == 2 ? "Certificados" : "Ambos");
            Workbook excel = rep.generaWorkbook();
            llenarNombreInstitucion();
            String path = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\Reporte_" + name + "_" + fechaGen + ".xlsx";
            FileOutputStream doc = new FileOutputStream(path);
            excel.write(doc);
            doc.close();
            File xlsx = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + NombreInstitucion.trim() + "\\ArchivosInstitucionales\\Reporte_" + name + "_" + fechaGen + ".xlsx");
            if (xlsx.exists()) {
                return "../../../Instituciones/" + NombreInstitucion.trim() + "/ArchivosInstitucionales/Reporte_" + name + "_" + fechaGen + ".xlsx";
            }
        } catch (SQLException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private List resultSetToArrayList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

    private List consultarTitulosTeXML() {
        List lstCadenaOriginal;
        try {
            ResultSet rs;
            Connection conn = this.con;
            String query = "SELECT prefijoTitulo FROM Configuracion_Inicial";
            String prefijoTit = "";
            rs = conn.prepareStatement(query).executeQuery();
            if (rs.next()) {
                prefijoTit = rs.getString("prefijoTitulo");
            }
            rs.close();
            query = "select cadenaOriginal from TExml left JOIN TETITULOELECTRONICO tee ON TExml.folioControl = tee.folioControl\n"
                    + "WHERE TExml.folioControl like '" + prefijoTit + "%' AND tee.id_titulo is null "
                    + "and texml.folioControl NOT IN (SELECT folioControl FROM TETituloElectronico_Eliminados)";
            rs = conn.prepareStatement(query).executeQuery();
            lstCadenaOriginal = resultSetToArrayList(rs);
            return lstCadenaOriginal;
        } catch (SQLException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private List consultarCertificadosTeXML() {
        List lstCadenaOriginal;
        try {
            ResultSet rs;
            Connection conn = this.con;
            String query = "SELECT prefijoCertificado FROM Configuracion_Inicial";
            String prefijoTit = "";
            rs = conn.prepareStatement(query).executeQuery();
            if (rs.next()) {
                prefijoTit = rs.getString("prefijoCertificado");
            }
            rs.close();
            query = "select cadenaOriginal from TExml left JOIN TECERTIFICADOELECTRONICO tce ON TExml.folioControl = tce.folioControl\n"
                    + "WHERE TExml.folioControl like '" + prefijoTit + "%' AND tce.id_certificado is null "
                    + "and texml.folioControl NOT IN (SELECT folioControl FROM TECERTIFICADOELECTRONICO_Eliminados)";
            rs = conn.prepareStatement(query).executeQuery();
            lstCadenaOriginal = resultSetToArrayList(rs);
            return lstCadenaOriginal;
        } catch (SQLException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void llenarTitulosEliminadosTeXML(List titulosActivos, List cadenasOriginales, String date1, String date2, String cveCarrera) {
        if (cadenasOriginales == null) {
            throw new NullPointerException("La lista de cadenas originales no se llenó correctamente");
        }
        try {
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date fechaInicio = sdf.parse(date1);
            Date fechaFin = sdf.parse(date2);
            cadenasOriginales.forEach(cadenaOriginal -> {
                Map<String, Object> row = new HashMap<>();
                String[] datos = cadenaOriginal.toString().split("\\|");
                try {
                    Date fechaExpedicion = new SimpleDateFormat("yyyy-MM-dd").parse(datos[22]);
                    if (!(fechaExpedicion.after(fechaInicio) && fechaExpedicion.before(fechaFin))) {
                        return;
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!cveCarrera.isEmpty() && !cveCarrera.equalsIgnoreCase(datos[10])) {
                    return;
                }
                row.put("Folio de Control", datos[3]);
                row.put("Id de Carrera", "");
                row.put("Clave Carrera", datos[10]);
                row.put("Nombre Carrera", datos[11]);
                row.put("Matricula Alumno", "");
                row.put("Nombre", datos[18]);
                row.put("Apellido Paterno", datos[19]);
                row.put("Apellido Materno", datos[20]);
                row.put("Fecha Inicio Carrera", datos[12]);
                row.put("Fecha Fin Carrera", datos[13]);
                row.put("Fecha Expedicion", datos[22]);
                row.put("Fecha Exención Examen", "");
                row.put("Fecha Examen Profesional", "");
                row.put("Estatus DigiSep", "DESCONOCIDO");
                row.put("SISTEMA", "Eliminado en DigiSEP");
                titulosActivos.add(row);
            });
        } catch (ParseException ex) {

        }
    }

    private void llenarCertificadosEliminadosTeXML(List certificadosActivos, List cadenasOriginales, String date1, String date2, int idCarrera) {
        try {
            if (cadenasOriginales == null) {
                throw new NullPointerException("La lista de cadenas originales no se llenó correctamente");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date fechaInicio = sdf.parse(date1);
            Date fechaFin = sdf.parse(date2);
            cadenasOriginales.forEach(cadenaOriginal -> {
                Map<String, Object> row = new HashMap<>();
                String[] datos = cadenaOriginal.toString().split("\\|");
                try {
                    Date fechaExpedicion = new SimpleDateFormat("yyyy-MM-dd").parse(datos[2].equalsIgnoreCase("2.0") ? datos[28] : datos[26]);
                    if (!(fechaExpedicion.after(fechaInicio) && fechaExpedicion.before(fechaFin))) {
                        return;
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (idCarrera != 0 && idCarrera != Integer.parseInt(datos[11])) {
                    return;
                }

                //||3.0|5|20535|150631|15|GANB770425MDFRVR02|1|20090021|2009-02-11T00:00:00|101|93|2009|81|5|10|6.00|55-15-0467|REMK881021MMCXNR01|KARINA|REA|MONJARAZ|250|1988-10-21T00:00:00|79|2022-01-14T00:00:00|03|54|54|9.59|315.00|315.00|9|1010101|2019-1|10|263|7.00|1010102|2019-1|10|263|5.25|1010103|2019-1|10|263|7.00|1010104|2019-1|9.00|263|5.25|1010105|2019-1|9.00|263|5.25|1010106|2019-1|8.00|263|5.25|1010207|2019-2|10|263|7.00|1010208|2019-2|10|263|5.25|1010209|2019-2|7.00|263|7.00|1010210|2019-2|10|263|5.25|1010211|2019-2|10|263|5.25|1010212|2019-2|10|263|5.25|1010313|2019-3|10|263|7.00|1010314|2019-3|10|263|5.25|1010315|2019-3|9.00|263|7.00|1010316|2019-3|9.00|263|5.25|1010317|2019-3|10|263|5.25|1010318|2019-3|8.00|263|5.25|1010419|2020-1|10|263|7.00|1010420|2020-1|10|263|5.25|1010421|2020-1|10|263|7.00|1010422|2020-1|10|263|5.25|1010423|2020-1|9.00|263|5.25|1010424|2020-1|10|263|5.25|1010525|2020-2|10|263|7.00|1010526|2020-2|10|263|5.25|1010527|2020-2|10|263|7.00|1010528|2020-2|10|263|5.25|1010529|2020-2|10|263|5.25|1010530|2020-2|10|263|5.25|1010631|2020-3|10|263|7.00|1010632|2020-3|10|263|5.25|1010633|2020-3|10|263|7.00|1010634|2020-3|10|263|5.25|1010635|2020-3|10|263|5.25|1010636|2020-3|10|263|5.25|1010737|2021-1|10|263|7.00|1010738|2021-1|10|263|5.25|1010739|2021-1|10|263|7.00|1010740|2021-1|10|263|5.25|1010741|2021-1|10|263|5.25|1010742|2021-1|10|263|5.25|1010843|2021-2|10|263|7.00|1010844|2021-2|9.00|263|5.25|1010845|2021-2|9.00|263|7.00|1010846|2021-2|9.00|263|5.25|1010847|2021-2|10|263|5.25|1010848|2021-2|9.00|263|5.25|1010949|2021-3|10|263|5.25|1010950|2021-3|10|263|5.25|1010951|2021-3|9.00|263|7.00|1010952|2021-3|9.00|263|5.25|1010953|2021-3|10|263|5.25|1010954|2021-3|6.00|263|7.00||
                //||2.0|5|20535|150631|15|GANB770425MDFRVR02|1|20081224|2008-06-18T00:00:00|107|93|2008|81|5|10|6.00|52-14-345|VARE900502MMCRMR03|ERIKA BERENICE|VARGAS|RAMÍREZ|250|1990-05-02T00:00:00|||79|2022-02-10T00:00:00|15|54|54|9.35|315.00|315.00|1070101|2018-1|10|263|7.00|1070102|2018-1|9.00|263|5.25|1070103|2018-1|9.00|263|5.25|1070104|2018-1|10|263|5.25|1070105|2018-1|9.00|263|7.00|1070106|2018-1|9.00|263|5.25|1070207|2018-2|8.00|263|7.00|1070208|2018-2|10|263|5.25|1070209|2018-2|10|263|5.25|1070210|2018-2|8.00|263|7.00|1070211|2018-2|8.00|263|5.25|1070212|2018-2|9.00|263|5.25|1070313|2018-3|9.00|263|7.00|1070314|2018-3|10|263|5.25|1070315|2018-3|10|263|7.00|1070316|2018-3|9.00|263|5.25|1070317|2018-3|10|263|5.25|1070318|2018-3|10|263|5.25|1070419|2019-1|10|263|7.00|1070420|2019-1|9.00|263|5.25|1070421|2019-1|10|263|5.25|1070422|2019-1|10|263|5.25|1070423|2019-1|9.00|263|5.25|1070424|2019-1|10|263|7.00|1070525|2019-2|9.00|263|5.25|1070526|2019-2|9.00|263|7.00|1070527|2019-2|9.00|263|5.25|1070528|2019-2|10|263|5.25|1070529|2019-2|10|263|7.00|1070530|2019-2|9.00|263|5.25|1070631|2019-3|9.00|263|5.25|1070632|2019-3|10|263|7.00|1070633|2019-3|10|263|5.25|1070634|2019-3|10|263|7.00|1070635|2019-3|10|263|5.25|1070636|2019-3|10|263|5.25|1070737|2020-1|9.00|263|5.25|1070738|2020-1|9.00|263|5.25|1070739|2020-1|10|263|7.00|1070740|2020-1|9.00|263|5.25|1070741|2020-1|9.00|263|5.25|1070742|2020-1|10|263|7.00|1070843|2020-2|10|263|7.00|1070844|2020-2|10|263|5.25|1070845|2020-2|7.00|263|5.25|1070846|2020-2|9.00|263|5.25|1070847|2020-2|10|263|5.25|1070848|2020-2|8.00|263|7.00|1070949|2020-3|10|263|5.25|1070950|2020-3|9.00|263|5.25|1070951|2020-3|10|263|5.25|1070952|2020-3|10|263|5.25|1070953|2020-3|6.00|263|7.00|1070954|2020-3|10|263|7.00||
                row.put("Versión DEC", datos[2]);
                row.put("Folio de Control", "");
                row.put("Id de Carrera", datos[11]);
                row.put("Clave Carrera", "");
                row.put("Nombre Carrera", "");
                row.put("Matricula Alumno", datos[18]);
                row.put("Nombre", datos[20]);
                row.put("Apellido Paterno", datos[21]);
                row.put("Apellido Materno", datos[22]);
                row.put("Fecha de Expedición", (datos[2].equalsIgnoreCase("2.0") ? datos[28] : datos[26]).split("T")[0]);
                row.put("Total Asignaturas", datos[2].equalsIgnoreCase("2.0") ? datos[30] : datos[28]);
                row.put("Asignaturas Asignadas", datos[2].equalsIgnoreCase("2.0") ? datos[31] : datos[29]);
                row.put("Promedio", datos[2].equalsIgnoreCase("2.0") ? datos[32] : datos[30]);
                row.put("Total de créditos", datos[2].equalsIgnoreCase("2.0") ? datos[33] : datos[31]);
                row.put("Creditos Obtenidos", datos[2].equalsIgnoreCase("2.0") ? datos[34] : datos[32]);
                row.put("Número de Ciclos", datos[2].equalsIgnoreCase("2.0") ? "" : datos[33]);
                row.put("Tipo", (datos[2].equalsIgnoreCase("2.0") ? datos[27] : datos[25]).equalsIgnoreCase("79") ? "COMPLETO" : "PARCIAL");
                row.put("SISTEMA", "Eliminado en DigiSEP");

                certificadosActivos.add(row);
            });
        } catch (ParseException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void llenarNombreInstitucion() {
        try {
            String Query = "SELECT nombreInstitucion FROM Configuracion_Inicial AS CI "
                    + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                    + " WHERE Id_Usuario = " + Id_Usuario;
            pstmt = con.prepareStatement(Query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                NombreInstitucion = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CTitulos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List consultarMovEliminados(String tbl) {
        List<Map<String, String>> listaMovimientosEliminados = new ArrayList<>();
        try {
            String query = "select InformacionBitacora,CAST(FechaMovimiento AS DATE) 'Fecha',CAST(FECHAMOVIMIENTO AS TIME) 'Hora' from BitacoraGeneral where Modulo = '" + tbl + "' and Movimiento = 'Eliminación'";
            ResultSet rs;
            rs = con.prepareStatement(query).executeQuery();
            List<Map<String, Object>> listResultSetDatosEliminados = resultSetToArrayList(rs);
            listResultSetDatosEliminados.forEach(item -> {
                String cadenaBitacora = item.get("InformacionBitacora").toString();
                String fechaMov = item.get("Fecha").toString();
                String horaMov = item.get("Hora").toString();
                String id = (tbl.equalsIgnoreCase("Titulos") ? cadenaBitacora.split("\\|\\|")[0].split(":")[1].trim() : cadenaBitacora.split("\\|\\|")[1].split(":")[1].trim());
                Map<String, String> row = new HashMap<String, String>();

                row.put(encabezadoMovEliminado.get(0), id);
                row.put(encabezadoMovEliminado.get(1), fechaMov);
                row.put(encabezadoMovEliminado.get(2), horaMov);
                listaMovimientosEliminados.add(row);

            });

        } catch (SQLException ex) {
            Logger.getLogger(CReportes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Collections.sort(listaMovimientosEliminados, new MapComparator(encabezadoMovEliminado.get(0), (short) 2));
        return listaMovimientosEliminados;
    }

}
