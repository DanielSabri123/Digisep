/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import static org.apache.commons.ssl.Base64.encodeBase64;
import org.apache.commons.ssl.PKCS8Key;

/**
 *
 * @author DAVID INNDEX
 */
public class Cxml_certificado_electronico {

    /*Variables Globales*/
    CConexionPool conector;
    HttpServletRequest request;
    HttpServletResponse response;
    String respuesta;
    String query;
    private final String CADENA_ORIGINAL = System.getProperty("user.dir") + "\\webapps\\resources_digisep\\Cadena_Original_MEC.xslt";

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

    public Cxml_certificado_electronico(HttpServletRequest request) {
        this.request = request;
    }

    public String crear_xml_certificado_electronico(
            String cdn_Dec, String cdn_Ipes, String cdn_Responsable, String cdn_Rvoe,
            String cdn_Carrera, String cdn_Alumno, String cdn_Expedicion,
            String cdn_Asignaturas, String cdn_Asignatura,
            String id_Usuario) throws SQLException, Exception {
        respuesta = "";
        String cadena_original = "";
        int bandera_existen_mas_asignaturas;
        conector = new CConexionPool(request);
        query = "SELECT nombreInstitucion FROM Configuracion_Inicial AS CI "
                + " JOIN Usuario AS U ON U.Id_ConfiguracionInicial = CI.ID_ConfiguracionInicial "
                + " WHERE Id_Usuario = " + id_Usuario;
        ResultSet rs = conector.consulta(query);
        String nombreInstitucion = "";
        while (rs.next()) {
            nombreInstitucion = rs.getString("nombreInstitucion");
        }
        rs = null;
        String sello_firmante = "";
        String[] cadena_nodo_dec = cdn_Dec.split("¬");
        String[] cadena_nodo_ipes = cdn_Ipes.split("¬");
        String[] cadena_nodo_responsable = cdn_Responsable.split("¬");
        String[] cadena_nodo_rvoe = cdn_Rvoe.split("¬");
        String[] cadena_nodo_carrera = cdn_Carrera.split("¬");
        String[] cadena_nodo_alumno = cdn_Alumno.split("¬");
        String[] cadena_nodo_expedicion = cdn_Expedicion.split("¬");
        String[] cadena_nodo_asignaturas = cdn_Asignaturas.split("¬");
        String cadena_nodo_asignatura = cdn_Asignatura;
        String[] array_asignatura;
        /**
         * identificar si el nodo de asignatura cuenta más de uno, cuando se
         * tenga solo uno, se realiza el split de los datos y se asigna al
         * lenght valor de uno; cuando sea mas de uno, se hacae split para
         * separar los datos de cada uno de las asignaturas y se toma como
         * lenght el valor que de el split.
         */
        if (cadena_nodo_asignatura.contains("~")) {
            bandera_existen_mas_asignaturas = 1;
            array_asignatura = cadena_nodo_asignatura.split("~");
        } else {
            bandera_existen_mas_asignaturas = 0;
            array_asignatura = cadena_nodo_asignatura.split("¬");
        }
        //iniciar la construcción del xml:
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //STANDALONE YES
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            doc.setXmlStandalone(true);
            //root element DEC
            Element rootElement = doc.createElement("Dec");
            doc.appendChild(rootElement);
            /**
             * NODO Dec
             */
            //version
            Attr attr_version = doc.createAttribute("version");
            attr_version.setValue("3.0");
            rootElement.setAttributeNode(attr_version);
            //tipoCertificado
            Attr attr_tipoCertificado = doc.createAttribute("tipoCertificado");
            attr_tipoCertificado.setValue(cadena_nodo_dec[0]);
            rootElement.setAttributeNode(attr_tipoCertificado);
            //folioControl
            if (!cadena_nodo_dec[1].trim().equalsIgnoreCase("^")) {
                Attr attr_folioControl = doc.createAttribute("folioControl");
                attr_folioControl.setValue(cadena_nodo_dec[1]);
                rootElement.setAttributeNode(attr_folioControl);
            } else {

            }
            //certificado y noCertificado, consultar a la tabla de firmantes:
            String id_firmanteResponsable = cadena_nodo_dec[2].trim();
            //System.out.println(id_firmanteResponsable);
            String certificado_firmante = obtenerCertificadoCodificado(cadena_nodo_dec[3], nombreInstitucion);
            String noCertificado = cadena_nodo_dec[4];

            //llave y contraseña del firmante
            query = "SELECT * FROM TEFirmantes WHERE id_firmante = " + id_firmanteResponsable;
            rs = conector.consulta(query);
            String contrasenia = "";
            String llave = "";
            while (rs.next()) {
                contrasenia = rs.getString("contrasenia");
                llave = rs.getString("llave");
            }

            //certificadoResponsable
            Attr attr_certificadoResponsable_de_responsable = doc.createAttribute("certificadoResponsable");
            attr_certificadoResponsable_de_responsable.setValue(certificado_firmante);
            rootElement.setAttributeNode(attr_certificadoResponsable_de_responsable);
            //noCertificadoResponsable
            Attr attr_noCertificadoResponsable_de_responsable = doc.createAttribute("noCertificadoResponsable");
            attr_noCertificadoResponsable_de_responsable.setValue(noCertificado);
            rootElement.setAttributeNode(attr_noCertificadoResponsable_de_responsable);
            //sello
            //nodo dec
//            cadena_original += "||3.0|" + (!cadena_nodo_dec[0].trim().equalsIgnoreCase("^") ? cadena_nodo_dec[0].trim() : "");
//            //nodo serviciofirmante
//            cadena_original += "|" + cadena_nodo_ipes[0];
//            //nodo ipes
//            cadena_original += "|" + cadena_nodo_ipes[0] + "|" + cadena_nodo_ipes[2] + "|" + cadena_nodo_ipes[4];
//            //nodo responsable
//            cadena_original += "|" + cadena_nodo_responsable[0] + "|" + cadena_nodo_responsable[4];
//            //nodo rvoe
//            cadena_original += "|" + cadena_nodo_rvoe[0] + "|" + cadena_nodo_rvoe[1];
//            //nodo carrera
//            cadena_original += "|" + cadena_nodo_carrera[0] + "|" + cadena_nodo_carrera[3] + "|" + cadena_nodo_carrera[5]
//                    + "|" + cadena_nodo_carrera[6] + "|" + cadena_nodo_carrera[8] + "|" + cadena_nodo_carrera[9] + "|" + cadena_nodo_carrera[10];
//            //nodo alumno
//            //Versión 3.0 se eliminan los espacios para fotografía y firma autografa.
//            cadena_original += "|" + cadena_nodo_alumno[0] + "|" + (!cadena_nodo_alumno[1].trim().equalsIgnoreCase("^") ? cadena_nodo_alumno[1] : "") + "|" + cadena_nodo_alumno[2]
//                    + "|" + cadena_nodo_alumno[3] + "|" + (!cadena_nodo_alumno[4].trim().equalsIgnoreCase("^") ? cadena_nodo_alumno[4] : "") + "|" + cadena_nodo_alumno[5] + "|" + cadena_nodo_alumno[6];
//            //nodo expedicion
//            cadena_original += "|" + cadena_nodo_expedicion[0] + "|" + cadena_nodo_expedicion[2] + "|" + cadena_nodo_expedicion[3];
//            //nodo Asignaturas V 3.0
//            cadena_original += "|" + cadena_nodo_asignaturas[0] + "|" + cadena_nodo_asignaturas[1] + "|" + cadena_nodo_asignaturas[2]
//                    + "|" + cadena_nodo_asignaturas[3] + "|" + cadena_nodo_asignaturas[4] + "|" + cadena_nodo_asignaturas[5];
//            int lenght_asignatura_para_cadena;
//            if (bandera_existen_mas_asignaturas == 1) {
//                lenght_asignatura_para_cadena = array_asignatura.length;
//            } else {
//                lenght_asignatura_para_cadena = 1;
//            }
//            //String[] asignaturas_array = request.getParameter("cadena_nodo_asignatura").split("~");
//            for (int i = 0; i < lenght_asignatura_para_cadena; i++) {
//                String[] split1_asignatura_para_cadena;
//                if (lenght_asignatura_para_cadena == 1) {
//                    split1_asignatura_para_cadena = array_asignatura;
//                } else {
//                    split1_asignatura_para_cadena = array_asignatura[i].split("¬");
//                }
//                cadena_original += "|" + split1_asignatura_para_cadena[0] + "|" + split1_asignatura_para_cadena[3] + "|" + split1_asignatura_para_cadena[4]
//                        + "|" + split1_asignatura_para_cadena[7] + "|" + split1_asignatura_para_cadena[9];
//            }
//            cadena_original += "||";
            //System.out.println("cadena original del nodo DEC" + cadena_original);
            //fakeBreakPoint
            // Key del Firmante
            String keyFile = llave;
            //File key = new File(System.getProperty("user.home") + "\\Downloads\\Llaves\\" + "CSD_Pruebas_CFDI_LAN7008173R5.key");
            File key = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\KEY\\" + keyFile);

            PrivateKey llavePrivada;
            //llavePrivada = getPrivateKey(key, "12345678a");
            try {
                llavePrivada = getPrivateKey(key, contrasenia);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.getLogger(Cxml_certificado_electronico.class.getName()).log(Level.SEVERE, "Error:---------->", e);
                respuesta = "errorContrasenia";
                return respuesta;
            }

            Attr attr_xmlns = doc.createAttribute("xmlns");
            attr_xmlns.setValue("https://www.siged.sep.gob.mx/certificados/");
            rootElement.setAttributeNode(attr_xmlns);
            /**
             * NODO ServiciioFirmante
             */
            Element ServicioFirmante = doc.createElement("ServicioFirmante");
            rootElement.appendChild(ServicioFirmante);
            Attr attr_idEntidad = doc.createAttribute("idEntidad");
            //El atributo “idEntidad” se refiere al identificador MEC (ID_INSTITUCION).
            attr_idEntidad.setValue(cadena_nodo_ipes[0]);//Atributo que contiene el identificador de la Institución que emitirá el Documento Electrónico de Certificación.
            ServicioFirmante.setAttributeNode(attr_idEntidad);
            /**
             * NODO Ipes
             */
            Element Ipes = doc.createElement("Ipes");
            rootElement.appendChild(Ipes);
            //idNombreInstitucion
            Attr attr_idNombreInstitucion_Ipes = doc.createAttribute("idNombreInstitucion");
            attr_idNombreInstitucion_Ipes.setValue(cadena_nodo_ipes[0]);
            Ipes.setAttributeNode(attr_idNombreInstitucion_Ipes);
            //nombreInstitucion
            if (!cadena_nodo_ipes[1].trim().equalsIgnoreCase("^")) {
                Attr attr_nombreInstitucion_Ipes = doc.createAttribute("nombreInstitucion");
                attr_nombreInstitucion_Ipes.setValue(cadena_nodo_ipes[1]);
                Ipes.setAttributeNode(attr_nombreInstitucion_Ipes);
            } else {

            }
            //idCampus
            Attr attr_idCampus_ipes = doc.createAttribute("idCampus");
            attr_idCampus_ipes.setValue(cadena_nodo_ipes[2]);
            Ipes.setAttributeNode(attr_idCampus_ipes);
            //campus
            if (!cadena_nodo_ipes[3].trim().equalsIgnoreCase("^")) {
                Attr attr_campus_ipes = doc.createAttribute("campus");
                attr_campus_ipes.setValue(cadena_nodo_ipes[3]);
                Ipes.setAttributeNode(attr_campus_ipes);
            } else {

            }
            //idEntidadFederativa
            Attr attr_idEntidadFederativa_ipes = doc.createAttribute("idEntidadFederativa");
            attr_idEntidadFederativa_ipes.setValue(cadena_nodo_ipes[4]);
            Ipes.setAttributeNode(attr_idEntidadFederativa_ipes);
            //entidadFederativa
            if (!cadena_nodo_ipes[5].trim().equalsIgnoreCase("^")) {
                Attr attr_entidadFederativa_ipes = doc.createAttribute("entidadFederativa");
                attr_entidadFederativa_ipes.setValue(cadena_nodo_ipes[5]);
                Ipes.setAttributeNode(attr_entidadFederativa_ipes);
            } else {

            }
            /**
             * NODO Responsable Este nodo va dentro del nodo de IPES de acuerdo
             * a la documentación de certificados 2.0
             */
            Element Responsable = doc.createElement("Responsable");
            Ipes.appendChild(Responsable);
            //curp
            Attr attr_curp_Responsable = doc.createAttribute("curp");
            attr_curp_Responsable.setValue(cadena_nodo_responsable[0]);
            Responsable.setAttributeNode(attr_curp_Responsable);
            //nombre
            Attr attr_nombre_Responsable = doc.createAttribute("nombre");
            attr_nombre_Responsable.setValue(cadena_nodo_responsable[1]);
            Responsable.setAttributeNode(attr_nombre_Responsable);
            //primerApellido
            Attr attr_primerApellido_Responsable = doc.createAttribute("primerApellido");
            attr_primerApellido_Responsable.setValue(cadena_nodo_responsable[2]);
            Responsable.setAttributeNode(attr_primerApellido_Responsable);
            //segundoApellido
            if (!cadena_nodo_responsable[3].trim().equalsIgnoreCase("^")) {
                Attr attr_segundoApellido_Responsable = doc.createAttribute("segundoApellido");
                attr_segundoApellido_Responsable.setValue(cadena_nodo_responsable[3]);
                Responsable.setAttributeNode(attr_segundoApellido_Responsable);
            } else {

            }
            //idCargo
            Attr attr_idCargo_Responsable = doc.createAttribute("idCargo");
            attr_idCargo_Responsable.setValue(cadena_nodo_responsable[4]);
            Responsable.setAttributeNode(attr_idCargo_Responsable);
            //cargo
            if (!cadena_nodo_responsable[5].trim().equalsIgnoreCase("^")) {
                Attr attr_cargo_Responsable = doc.createAttribute("cargo");
                attr_cargo_Responsable.setValue(cadena_nodo_responsable[5]);
                Responsable.setAttributeNode(attr_cargo_Responsable);
            } else {

            }
            /**
             * NODO Rvoe
             */
            Element Rvoe = doc.createElement("Rvoe");
            rootElement.appendChild(Rvoe);
            //numero
            Attr attr_numero_Rvoe = doc.createAttribute("numero");
            attr_numero_Rvoe.setValue(cadena_nodo_rvoe[0]);
            Rvoe.setAttributeNode(attr_numero_Rvoe);
            //fechaExpedicion
            Attr attr_fechaExpedicion_Rvoe = doc.createAttribute("fechaExpedicion");
            attr_fechaExpedicion_Rvoe.setValue(cadena_nodo_rvoe[1]);
            Rvoe.setAttributeNode(attr_fechaExpedicion_Rvoe);
            /**
             * NODO Carrera
             */
            Element Carrera = doc.createElement("Carrera");
            rootElement.appendChild(Carrera);
            //idCarrera
            Attr attr_idCarrera_Carrera = doc.createAttribute("idCarrera");
            attr_idCarrera_Carrera.setValue(cadena_nodo_carrera[0]);
            Carrera.setAttributeNode(attr_idCarrera_Carrera);
            //claveCarrera
            if (!cadena_nodo_carrera[1].trim().equalsIgnoreCase("^")) {
                Attr attr_claveCarrera_Carrera = doc.createAttribute("claveCarrera");
                attr_claveCarrera_Carrera.setValue(cadena_nodo_carrera[1]);
                Carrera.setAttributeNode(attr_claveCarrera_Carrera);
            } else {

            }
            //nombreCarrera
//            if (!cadena_nodo_carrera[2].trim().equalsIgnoreCase("^")) {
//                Attr attr_nombreCarrera_Carrera = doc.createAttribute("nombreCarrera");
//                attr_nombreCarrera_Carrera.setValue(cadena_nodo_carrera[2]);
//                Carrera.setAttributeNode(attr_nombreCarrera_Carrera);
//            } else {
//
//            }
            //idTipoPeriodo
            Attr attr_idTipoPeriodo_Carrera = doc.createAttribute("idTipoPeriodo");
            attr_idTipoPeriodo_Carrera.setValue(cadena_nodo_carrera[3]);
            Carrera.setAttributeNode(attr_idTipoPeriodo_Carrera);
            //tipoPeriodo
            if (!cadena_nodo_carrera[4].trim().equalsIgnoreCase("^")) {
                Attr attr_tipoPeriodo_Carrera = doc.createAttribute("tipoPeriodo");
                attr_tipoPeriodo_Carrera.setValue(cadena_nodo_carrera[4]);
                Carrera.setAttributeNode(attr_tipoPeriodo_Carrera);
            } else {

            }
            //clavePlan
            Attr attr_clavePlan_Carrera = doc.createAttribute("clavePlan");
            attr_clavePlan_Carrera.setValue(cadena_nodo_carrera[5]);
            Carrera.setAttributeNode(attr_clavePlan_Carrera);

            //idNivelEstudios
            Attr attr_idNivelEstudios_Carrera = doc.createAttribute("idNivelEstudios");
            attr_idNivelEstudios_Carrera.setValue(cadena_nodo_carrera[6]);
            Carrera.setAttributeNode(attr_idNivelEstudios_Carrera);
            //NivelEstudios
            if (!cadena_nodo_carrera[7].trim().equalsIgnoreCase("^")) {
                Attr attr_NivelEstudios_Carrera = doc.createAttribute("nivelEstudios");
                attr_NivelEstudios_Carrera.setValue(cadena_nodo_carrera[7]);
                Carrera.setAttributeNode(attr_NivelEstudios_Carrera);
            }

            //CalificacionMinima
            Attr attr_califMin_Carrera = doc.createAttribute("calificacionMinima");
            attr_califMin_Carrera.setValue(cadena_nodo_carrera[8]);
            Carrera.setAttributeNode(attr_califMin_Carrera);

            //CalificacionMaxima
            Attr attr_califMax_Carrera = doc.createAttribute("calificacionMaxima");
            attr_califMax_Carrera.setValue(cadena_nodo_carrera[9]);
            Carrera.setAttributeNode(attr_califMax_Carrera);

            //CalificacionMinimaAprobatoria
            Attr attr_califMinAprob_Carrera = doc.createAttribute("calificacionMinimaAprobatoria");
            attr_califMinAprob_Carrera.setValue(cadena_nodo_carrera[10]);
            Carrera.setAttributeNode(attr_califMinAprob_Carrera);

            /**
             * NODO Alumno
             */
            Element Alumno = doc.createElement("Alumno");
            rootElement.appendChild(Alumno);
            //numeroControl
            Attr attr_numeroControl_Alumno = doc.createAttribute("numeroControl");
            attr_numeroControl_Alumno.setValue(cadena_nodo_alumno[0]);
            Alumno.setAttributeNode(attr_numeroControl_Alumno);
            //curp
            if (!cadena_nodo_alumno[1].equalsIgnoreCase("^")) {
                Attr attr_curp_Alumno = doc.createAttribute("curp");
                attr_curp_Alumno.setValue(cadena_nodo_alumno[1]);
                Alumno.setAttributeNode(attr_curp_Alumno);
            } else {

            }
            //nombre
            Attr attr_nombre_Alumno = doc.createAttribute("nombre");
            attr_nombre_Alumno.setValue(cadena_nodo_alumno[2]);
            Alumno.setAttributeNode(attr_nombre_Alumno);
            //primerApellido
            Attr attr_primerApellido_Alumno = doc.createAttribute("primerApellido");
            attr_primerApellido_Alumno.setValue(cadena_nodo_alumno[3]);
            Alumno.setAttributeNode(attr_primerApellido_Alumno);
            //segundoApellido
            if (!cadena_nodo_alumno[4].trim().equalsIgnoreCase("^")) {
                Attr attr_segundoApellido_Alumno = doc.createAttribute("segundoApellido");
                attr_segundoApellido_Alumno.setValue(cadena_nodo_alumno[4]);
                Alumno.setAttributeNode(attr_segundoApellido_Alumno);
            } else {

            }
            //idGenero
            Attr attr_idGenero_Alumno = doc.createAttribute("idGenero");
            attr_idGenero_Alumno.setValue(cadena_nodo_alumno[5]);
            Alumno.setAttributeNode(attr_idGenero_Alumno);
            //fechaNacimiento
            Attr attr_fechaNacimiento_Alumno = doc.createAttribute("fechaNacimiento");
            attr_fechaNacimiento_Alumno.setValue(cadena_nodo_alumno[6]);
            Alumno.setAttributeNode(attr_fechaNacimiento_Alumno);
            /**
             * NODO Expedicion
             */
            Element Expedicion = doc.createElement("Expedicion");
            rootElement.appendChild(Expedicion);
            //idTipoCertificacion
            Attr attr_idTipoCertificacion_Expedicion = doc.createAttribute("idTipoCertificacion");
            attr_idTipoCertificacion_Expedicion.setValue(cadena_nodo_expedicion[0]);
            Expedicion.setAttributeNode(attr_idTipoCertificacion_Expedicion);
            //tipoCertificacion
            if (!cadena_nodo_expedicion[1].trim().equalsIgnoreCase("^")) {
                Attr attr_tipoCertificacion_Expedicion = doc.createAttribute("tipoCertificacion");
                attr_tipoCertificacion_Expedicion.setValue(cadena_nodo_expedicion[1]);
                Expedicion.setAttributeNode(attr_tipoCertificacion_Expedicion);
            } else {

            }
            //fecha
            Attr attr_fecha_Expedicion = doc.createAttribute("fecha");
            attr_fecha_Expedicion.setValue(cadena_nodo_expedicion[2]);
            Expedicion.setAttributeNode(attr_fecha_Expedicion);
            //idLugarExpedicion
            Attr attr_idLugarExpedicion_Expedicion = doc.createAttribute("idLugarExpedicion");
            attr_idLugarExpedicion_Expedicion.setValue(cadena_nodo_expedicion[3]);
            Expedicion.setAttributeNode(attr_idLugarExpedicion_Expedicion);
            //lugarExpedicion
            if (!cadena_nodo_expedicion[4].trim().equalsIgnoreCase("^")) {
                Attr attr_lugarExpedicion_Expedicion = doc.createAttribute("lugarExpedicion");
                attr_lugarExpedicion_Expedicion.setValue(cadena_nodo_expedicion[4]);
                Expedicion.setAttributeNode(attr_lugarExpedicion_Expedicion);
            } else {

            }
            /**
             * NODO Asinaturas
             */
            Element Asignaturas = doc.createElement("Asignaturas");
            rootElement.appendChild(Asignaturas);
            //total
            Attr attr_total_Asignaturas = doc.createAttribute("total");
            attr_total_Asignaturas.setValue(cadena_nodo_asignaturas[0]);
            Asignaturas.setAttributeNode(attr_total_Asignaturas);
            //asignadas
            Attr attr_asignadas_Asignaturas = doc.createAttribute("asignadas");
            attr_asignadas_Asignaturas.setValue(cadena_nodo_asignaturas[1]);
            Asignaturas.setAttributeNode(attr_asignadas_Asignaturas);
            //promedio
            Attr attr_promedio_Asignaturas = doc.createAttribute("promedio");
            attr_promedio_Asignaturas.setValue(cadena_nodo_asignaturas[2]);
            Asignaturas.setAttributeNode(attr_promedio_Asignaturas);
            //totalCreditos
            Attr attr_totalCreditos_Asignaturas = doc.createAttribute("totalCreditos");
            attr_totalCreditos_Asignaturas.setValue(cadena_nodo_asignaturas[3]);
            Asignaturas.setAttributeNode(attr_totalCreditos_Asignaturas);
            //creditosObtenidos
            Attr attr_creditosObtenidos_Asignaturas = doc.createAttribute("creditosObtenidos");
            attr_creditosObtenidos_Asignaturas.setValue(cadena_nodo_asignaturas[4]);
            Asignaturas.setAttributeNode(attr_creditosObtenidos_Asignaturas);
            //numeroCiclos (V 3.0)
            Attr attr_numeroCiclos_Asignaturas = doc.createAttribute("numeroCiclos");
            attr_numeroCiclos_Asignaturas.setValue(cadena_nodo_asignaturas[5]);
            Asignaturas.setAttributeNode(attr_numeroCiclos_Asignaturas);
            /**
             * NODO Asignatura de Asignatura
             */
            int lenght_asignatura;
            if (bandera_existen_mas_asignaturas == 1) {
                lenght_asignatura = array_asignatura.length;
            } else {
                lenght_asignatura = 1;
            }
            //String[] asignaturas_array = request.getParameter("cadena_nodo_asignatura").split("~");
            for (int i = 0; i < lenght_asignatura; i++) {
                String[] split1_asignatura;
                if (lenght_asignatura == 1) {
                    split1_asignatura = array_asignatura;
                } else {
                    split1_asignatura = array_asignatura[i].split("¬");
                }
                Element Asignatura = doc.createElement("Asignatura");
                Asignaturas.appendChild(Asignatura);
                //idAsignatura
                Attr attr_idAsignatura_Asignatura = doc.createAttribute("idAsignatura");
                attr_idAsignatura_Asignatura.setValue(split1_asignatura[0]);
                Asignatura.setAttributeNode(attr_idAsignatura_Asignatura);
                //claveAsignatura
                if (!split1_asignatura[1].trim().equalsIgnoreCase("^")) {
                    Attr attr_claveAsignatura_Asignatura = doc.createAttribute("claveAsignatura");
                    attr_claveAsignatura_Asignatura.setValue(split1_asignatura[1]);
                    Asignatura.setAttributeNode(attr_claveAsignatura_Asignatura);
                } else {

                }
                //nombre
                Attr attr_nombre_Asignatura = doc.createAttribute("nombre");
                attr_nombre_Asignatura.setValue(split1_asignatura[2]);
                Asignatura.setAttributeNode(attr_nombre_Asignatura);
                //ciclo
                Attr attr_ciclo_Asignatura = doc.createAttribute("ciclo");
                attr_ciclo_Asignatura.setValue(split1_asignatura[3]);
                Asignatura.setAttributeNode(attr_ciclo_Asignatura);
                //calificacion
                Attr attr_calificacion_Asignatura = doc.createAttribute("calificacion");
                attr_calificacion_Asignatura.setValue(split1_asignatura[4]);
                Asignatura.setAttributeNode(attr_calificacion_Asignatura);
                //idObservaciones
                if (!split1_asignatura[5].trim().equalsIgnoreCase("^")) {
                    Attr attr_idObservaciones_Asignatura = doc.createAttribute("idObservaciones");
                    attr_idObservaciones_Asignatura.setValue(split1_asignatura[5]);
                    Asignatura.setAttributeNode(attr_idObservaciones_Asignatura);
                } else {

                }
                //observaciones
                if (!split1_asignatura[6].trim().equalsIgnoreCase("^")) {
                    Attr attr_observaciones_Asignatura = doc.createAttribute("observaciones");
                    attr_observaciones_Asignatura.setValue(split1_asignatura[6]);
                    Asignatura.setAttributeNode(attr_observaciones_Asignatura);
                } else {

                }
                //idTipoAsignatura
                if (!split1_asignatura[7].trim().equalsIgnoreCase("^")) {
                    Attr attr_idTipoAsignatura_Asignatura = doc.createAttribute("idTipoAsignatura");
                    attr_idTipoAsignatura_Asignatura.setValue(split1_asignatura[7]);
                    Asignatura.setAttributeNode(attr_idTipoAsignatura_Asignatura);
                }

                //TipoAsignatura
                if (!split1_asignatura[8].trim().equalsIgnoreCase("^")) {
                    Attr attr_idTipoAsignatura_Asignatura = doc.createAttribute("tipoAsignatura");
                    attr_idTipoAsignatura_Asignatura.setValue(split1_asignatura[8]);
                    Asignatura.setAttributeNode(attr_idTipoAsignatura_Asignatura);
                }
                //creditos
                Attr attr_creditos_Asignatura = doc.createAttribute("creditos");
                attr_creditos_Asignatura.setValue(split1_asignatura[9]);
                Asignatura.setAttributeNode(attr_creditos_Asignatura);
            }

            //La cadena original se construye con el archivo XSLT que fue corregido para digisep.
            cadena_original = generarCadenaOriginal(convertDocumentToString(doc));
            sello_firmante = generarSelloDigital(llavePrivada, cadena_original);
            System.out.println("sello digital :" + sello_firmante);
            //sello
            Attr attr_sello_de_dec = doc.createAttribute("sello");
            attr_sello_de_dec.setValue(sello_firmante);
            rootElement.setAttributeNode(attr_sello_de_dec);
            //escribir el contenido en archivo xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //STANDALONE YES
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            //IDENTAR
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //IDENTAMOS CON VALOR 4
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount",
                    Integer.toString(4));
            DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(new File(System.getProperty("user.home") + "\\Downloads\\XML\\Certificados\\" + cadena_nodo_dec[1].trim() + "_" + cadena_nodo_alumno[0].trim() + "_" + cadena_nodo_carrera[1] + ".xml"));
            StreamResult result = new StreamResult(new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\Certificados\\" + cadena_nodo_dec[1].trim() + "_" + cadena_nodo_alumno[0].trim() + "_" + cadena_nodo_carrera[1] + ".xml"));
            transformer.transform(source, result);

            //cadena XML generada del mismo proceso anterior
            result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            String output = result.getWriter().toString();
            //System.out.println(output.replaceAll("\n|\r|\\s+", ""));
            //registrar el xml en bd:
            //query = "INSERT INTO TExml VALUES ('" + cadena_nodo_dec[1] + "','" + output + "','" + cadena_original + "')";
            query = "INSERT INTO TExml VALUES (?,?,?)";
            try (PreparedStatement pstmt = conector.conexion.prepareStatement(query)) {
                pstmt.setString(1, cadena_nodo_dec[1]);
                pstmt.setString(2, output);
                pstmt.setString(3, cadena_original);

                pstmt.execute();
            }

            conector.cerrarConexion();
            respuesta = "success";
        } catch (ParserConfigurationException | DOMException e) {
            e.getMessage();
            e.printStackTrace();
            respuesta = "error";
        }
        return respuesta;
    }

    private PrivateKey getPrivateKey(File keyFile, String password)
            throws java.security.GeneralSecurityException, IOException {

        FileInputStream in = new FileInputStream(keyFile);
        PKCS8Key pkcs8 = new PKCS8Key(in, password.toCharArray());
        byte[] decrypted = pkcs8.getDecryptedBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypted);
        PrivateKey pk;
        if (pkcs8.isDSA()) {
            pk = KeyFactory.getInstance("DSA").generatePrivate(spec);
        } else if (pkcs8.isRSA()) {
            pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
        pk = pkcs8.getPrivateKey();
        return pk;
    }

    private String generarSelloDigital(PrivateKey key, String cadenaOriginal)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
//        Signature sing = Signature.getInstance("SHA256withRSA");
//        sing.initSign(key, new SecureRandom());
//        try {
//            sing.update(cadenaOriginal.getBytes("UTF-8"));
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(CGestionCFDI.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        byte[] signature = sing.sign();
//        return new String(Base64.encode(signature));
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        byte[] bytesCadenaOriginal = cadenaOriginal.getBytes("UTF-8");
        signature.update(bytesCadenaOriginal);
        byte[] bytesSigned = signature.sign();
        //Base64 b64 = new Base64(-1);
        byte[] bytesEncoded = encodeBase64(bytesSigned);
        return new String(bytesEncoded);
    }

    private String obtenerCertificadoCodificado(String filename, String nombreInstitucion) throws IOException {
        String strKeyPEM = "";
        //String pathFile = System.getProperty("user.home") + "\\Downloads\\PEM"; 
        String pathFile = System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\PEM";
        //System.out.println(pathFile + "\\" + filename);
        BufferedReader br = new BufferedReader(new FileReader(pathFile + "\\" + filename));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equalsIgnoreCase("-----BEGIN CERTIFICATE-----") && !line.equalsIgnoreCase("-----END CERTIFICATE-----")) {
                    strKeyPEM += line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            br.close();
        }
        return strKeyPEM;
    }

    private String generarCadenaOriginal(String xml) throws javax.xml.crypto.dsig.TransformException, TransformerConfigurationException, TransformerException, UnsupportedEncodingException {
        String resultado = "";
        StreamSource streamSource = new StreamSource(this.CADENA_ORIGINAL);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer xlsTransformer = transformerFactory.newTransformer(streamSource);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        xlsTransformer.transform(new StreamSource(new StringReader(xml.replaceAll("xmlns=\"https://www.siged.sep.gob.mx/certificados/\"", ""))), new javax.xml.transform.stream.StreamResult(output));
        try {
            resultado = output.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Cxml_certificado_electronico.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }

    private String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
