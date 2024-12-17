/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import static org.apache.commons.ssl.Base64.encodeBase64;
import org.apache.commons.ssl.PKCS8Key;
import org.xml.sax.SAXException;

/**
 *
 * @author DAVID INNDEX
 */
public class Cxml_titulo_electronico {

    /*Variables Globales*/
    CConexionPool conector;
    String respuesta;
    String query;
    CallableStatement cstmt;
    CConexion conexion;
    Connection con;
    private CClavesActivacion desencriptador;
    HttpServletRequest request;
    String idTitulo;

    public void setIdTitulo(String idTitulo) {
        this.idTitulo = idTitulo;
    }

    public Cxml_titulo_electronico(HttpServletRequest request) {
        this.request = request;
    }

    /**
     *
     * @param string_titulo_electronico
     * @param cadena_nodo_firma_responsable
     * @param string_intitucion
     * @param string_carrera
     * @param string_profesionista
     * @param string_expedicion
     * @param string_antecedentes
     * @param id_Usuario
     * @return String con mensaje de la respuesta del proceso en el método.
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws UnsupportedEncodingException
     */
    public String crear_xml_titulo_electronico(
            String string_titulo_electronico,
            String cadena_nodo_firma_responsable,
            String string_intitucion,
            String string_carrera,
            String string_profesionista,
            String string_expedicion,
            String string_antecedentes,
            String id_Usuario
    ) throws TransformerConfigurationException, TransformerException, UnsupportedEncodingException, SQLException, FileNotFoundException, IOException, SAXException, GeneralSecurityException {
        respuesta = "";
        desencriptador = new CClavesActivacion();
        String cadena_original = "";
        String concatena_cadenas_original = "";
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
        int bandera_existen_mas_firma_responsable;
        String[] array_cadena_nodo_firma_responsable;
        /*identificar si la información de la cadena del nodo de firma responsable, cuenta con uno o más responsables.
        cuando se tenga solo uno, se realiza el split de los datos y se asigna al lenght valor de uno; cuando sea mas de uno, 
        se hacae split para separar los datos de cada uno de los firmante y se toma como lenght el valor que de el split.*/
        if (cadena_nodo_firma_responsable.contains("~")) {
            bandera_existen_mas_firma_responsable = 1;
            array_cadena_nodo_firma_responsable = cadena_nodo_firma_responsable.split("~");
        } else {
            bandera_existen_mas_firma_responsable = 0;
            array_cadena_nodo_firma_responsable = cadena_nodo_firma_responsable.split("¬");
        }
        String[] cadena_nodo_institucion = string_intitucion.split("¬");
        String[] cadena_nodo_carrera = string_carrera.split("¬");
        String[] cadena_nodo_profesionista = string_profesionista.split("¬");
        String[] cadena_nodo_expedicion = string_expedicion.split("¬");
        String[] cadena_nodo_antecedentes = string_antecedentes.split("¬");
        //iniciar la construcción del XML:
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            // root element TituloElectronico
            Element rootElement = doc.createElement("TituloElectronico");
            doc.appendChild(rootElement);
            /**
             * NODO TituloElectronico
             */
            //xmlns
            Attr attr_xmlns = doc.createAttribute("xmlns");
            //attr_xmlns.setValue("http://www.w3.org/2001/XMLSchema");
            attr_xmlns.setValue("https://www.siged.sep.gob.mx/titulos/");
            rootElement.setAttributeNode(attr_xmlns);
            //xmlns:xsi
            Attr attr_xmlns_xsi = doc.createAttribute("xmlns:xsi");
            attr_xmlns_xsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttributeNode(attr_xmlns_xsi);
            //version
            Attr attr_version = doc.createAttribute("version");
            attr_version.setValue("1.0");
            rootElement.setAttributeNode(attr_version);
            //cadena_original = "||1.0";
            //folio control
            Attr attr_folioControl = doc.createAttribute("folioControl");
            attr_folioControl.setValue(string_titulo_electronico.trim());
            rootElement.setAttributeNode(attr_folioControl);
            //xsi:schemaLocation
            Attr attr_xsi_schemaLocation = doc.createAttribute("xsi:schemaLocation");
            attr_xsi_schemaLocation.setValue("https://www.siged.sep.gob.mx/titulos/ schema.xsd");
            rootElement.setAttributeNode(attr_xsi_schemaLocation);
            //cadena_original += "|" + string_titulo_electronico.trim();
            /**
             * NODO FirmaResponsable
             */
            // FirmaResponsables element 
            Element FirmaResponsables = doc.createElement("FirmaResponsables");
            rootElement.appendChild(FirmaResponsables);
            int lenght_firma_responsables;
            if (bandera_existen_mas_firma_responsable == 1) {
                lenght_firma_responsables = array_cadena_nodo_firma_responsable.length;
            } else {
                lenght_firma_responsables = 1;
            }
            for (int i = 0; i < lenght_firma_responsables; i++) {
                String[] split_1;
                if (lenght_firma_responsables == 1) {
                    split_1 = array_cadena_nodo_firma_responsable;
                } else {
                    split_1 = array_cadena_nodo_firma_responsable[i].split("¬");
                }
                System.out.println(split_1);
                // FirmaResponsables element of FirmaResponsables
                Element FirmaResponsable = doc.createElement("FirmaResponsable");
                FirmaResponsables.appendChild(FirmaResponsable);
                //nombre
                Attr attr_nombre_de_responsable = doc.createAttribute("nombre");
                attr_nombre_de_responsable.setValue(remplazarCaracteresEspeciales(split_1[0]));
                FirmaResponsable.setAttributeNode(attr_nombre_de_responsable);
                //primerApellido
                Attr attr_primerApellido_de_responsable = doc.createAttribute("primerApellido");
                attr_primerApellido_de_responsable.setValue(remplazarCaracteresEspeciales(split_1[1]));
                FirmaResponsable.setAttributeNode(attr_primerApellido_de_responsable);
                //segundoApellido ----- opcional
                if (!split_1[2].trim().equalsIgnoreCase("^")) {
                    Attr attr_segundoApellido_de_responsable = doc.createAttribute("segundoApellido");
                    attr_segundoApellido_de_responsable.setValue(remplazarCaracteresEspeciales(split_1[2]));
                    FirmaResponsable.setAttributeNode(attr_segundoApellido_de_responsable);
                }
                //curp
                Attr attr_curp_de_responsable = doc.createAttribute("curp");
                attr_curp_de_responsable.setValue(remplazarCaracteresEspeciales(split_1[3]));
                FirmaResponsable.setAttributeNode(attr_curp_de_responsable);
                //cadena_original += "|" + split_1[3].trim();
                //idCargo
                Attr attr_idCargo_de_responsable = doc.createAttribute("idCargo");
                attr_idCargo_de_responsable.setValue(split_1[4].trim());
                FirmaResponsable.setAttributeNode(attr_idCargo_de_responsable);
                //cadena_original += "|" + split_1[4].trim();
                //cargo
                Attr attr_cargo_de_responsable = doc.createAttribute("cargo");
                attr_cargo_de_responsable.setValue(split_1[5].trim());
                FirmaResponsable.setAttributeNode(attr_cargo_de_responsable);
                //cadena_original += "|" + split_1[5].trim();
                //abrTitulo ---- opcional
                if (!split_1[6].trim().equalsIgnoreCase("^")) {
                    Attr attr_abrTitulo_de_responsable = doc.createAttribute("abrTitulo");
                    attr_abrTitulo_de_responsable.setValue(remplazarCaracteresEspeciales(split_1[6].trim()));
                    FirmaResponsable.setAttributeNode(attr_abrTitulo_de_responsable);
                    //cadena_original += "|" + split_1[6].trim();
                } else {
                    //cadena_original += "|";
                }
                //sello, certificadoResponsable y noCertificadoResponsable se consulataran con el id del firmante responsable
                String id_firmante_responsable = split_1[7].trim();
                query = "SELECT * FROM TEFirmantes WHERE id_firmante = " + id_firmante_responsable + " ;";
                rs = conector.consulta(query);
                String sello_firmante = "";
                String certificado_firmante = "";
                String noCertificado = "";
                String contrasenia = "";
                String llave = "";
                while (rs.next()) {
                    //sello_firmante = rs.getString("Sello");
                    certificado_firmante = rs.getString("certificadoResponsable");
                    certificado_firmante = obtenerCertificadoCodificado(certificado_firmante, nombreInstitucion.trim());
                    noCertificado = rs.getString("noCertificadoResponsable");
                    contrasenia = rs.getString("contrasenia");
                    llave = rs.getString("llave");
                }
                //cadena original:
                //nodo TituloElectronico:
                cadena_original = "||1.0|" + string_titulo_electronico.trim();
                //nodo FirmaResponsable:
                cadena_original += "|" + split_1[3].trim() + "|" + split_1[4].trim() + "|" + split_1[5].trim() + "|" + (!split_1[6].trim().equalsIgnoreCase("^") ? split_1[6].trim() : "");
                //nodo Institucion:
                cadena_original += "|" + cadena_nodo_institucion[0].trim() + "|" + remplazarCaracteresEspeciales(cadena_nodo_institucion[1]);
                //nodo Carrera:
                cadena_original += "|" + cadena_nodo_carrera[0].trim() + "|" + remplazarCaracteresEspeciales(cadena_nodo_carrera[1]) + "|" + (!cadena_nodo_carrera[2].trim().equalsIgnoreCase("^") ? cadena_nodo_carrera[2].trim() : "") + "|" + cadena_nodo_carrera[3] + "|" + cadena_nodo_carrera[4].trim() + "|" + cadena_nodo_carrera[5] + "|" + (!cadena_nodo_carrera[6].trim().equalsIgnoreCase("^") ? cadena_nodo_carrera[6].trim() : "");
                //nodo Profesionista:
                cadena_original += "|" + cadena_nodo_profesionista[0] + "|" + remplazarCaracteresEspeciales(cadena_nodo_profesionista[1]) + "|" + remplazarCaracteresEspeciales(cadena_nodo_profesionista[2]) + "|" + (!cadena_nodo_profesionista[3].trim().equalsIgnoreCase("^") ? remplazarCaracteresEspeciales(cadena_nodo_profesionista[3].trim()) : "") + "|" + cadena_nodo_profesionista[4];
                //nodo  Expedicion:
                cadena_original += "|" + cadena_nodo_expedicion[0] + "|" + cadena_nodo_expedicion[1] + "|" + cadena_nodo_expedicion[2] + "|" + (!cadena_nodo_expedicion[3].trim().equalsIgnoreCase("^") ? cadena_nodo_expedicion[3].trim() : "") + "|" + (!cadena_nodo_expedicion[4].trim().equalsIgnoreCase("^") ? cadena_nodo_expedicion[4].trim() : "") + "|" + cadena_nodo_expedicion[5] + "|" + cadena_nodo_expedicion[6] + "|" + cadena_nodo_expedicion[7] + "|" + cadena_nodo_expedicion[8] + "|" + cadena_nodo_expedicion[9];
                //nodo Antecedente:
                cadena_original += "|" + remplazarCaracteresEspeciales(cadena_nodo_antecedentes[0]) + "|" + cadena_nodo_antecedentes[1].trim() + "|" + cadena_nodo_antecedentes[2] + "|" + cadena_nodo_antecedentes[3].trim() + "|" + cadena_nodo_antecedentes[4] + "|" + (!cadena_nodo_antecedentes[5].trim().equalsIgnoreCase("^") ? cadena_nodo_antecedentes[5] : "") + "|" + cadena_nodo_antecedentes[6] + "|" + (!cadena_nodo_antecedentes[7].trim().equalsIgnoreCase("^") ? cadena_nodo_antecedentes[7].trim() : "") + "||";
                System.out.println("cadena original por firmante:" + cadena_original);
                //File key = new File(System.getProperty("user.home")+"\\Downloads\\Llaves\\CSD_Pruebas_CFDI_LAN7008173R5.key");
                File key = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\KEY\\" + llave);
                PrivateKey llavePrivada;
                //llavePrivada = getPrivateKey(key, "12345678a");
                try {
                    llavePrivada = getPrivateKey(key, contrasenia);
                } catch (GeneralSecurityException e) {
                    Logger.getLogger(Cxml_titulo_electronico.class.getName()).log(Level.SEVERE, "Ocurrió un error al leer la llave privada {0}", e);
                    respuesta = "errorContrasenia";
                    return respuesta;
                }
                sello_firmante = generarSelloDigital(llavePrivada, cadena_original);
                System.out.println("sello digital :" + sello_firmante);
                concatena_cadenas_original += "~" + cadena_original;
                //sello
                Attr attr_sello_de_responsable = doc.createAttribute("sello");
                attr_sello_de_responsable.setValue(sello_firmante);
                FirmaResponsable.setAttributeNode(attr_sello_de_responsable);
                //certificadoResponsable
                Attr attr_certificadoResponsable_de_responsable = doc.createAttribute("certificadoResponsable");
                attr_certificadoResponsable_de_responsable.setValue(certificado_firmante);
                FirmaResponsable.setAttributeNode(attr_certificadoResponsable_de_responsable);
                //noCertificadoResponsable
                Attr attr_noCertificadoResponsable_de_responsable = doc.createAttribute("noCertificadoResponsable");
                attr_noCertificadoResponsable_de_responsable.setValue(noCertificado);
                FirmaResponsable.setAttributeNode(attr_noCertificadoResponsable_de_responsable);
                //System.out.println("CADENA ORIGINAL: " + concatena_cadenas_original);
                insertarCadenaYSello(cadena_original, sello_firmante, id_firmante_responsable, idTitulo, id_Usuario);
            }
            /**
             * NODO Institucion
             */
            Element Institucion = doc.createElement("Institucion");
            rootElement.appendChild(Institucion);
            //cveInstitucion
            Attr attr_cveInstitucion_institucion = doc.createAttribute("cveInstitucion");
            attr_cveInstitucion_institucion.setValue(cadena_nodo_institucion[0].trim());
            Institucion.setAttributeNode(attr_cveInstitucion_institucion);
            //cadena_original += "|" + cadena_nodo_institucion[0].trim();
            //nombreInstitucion
            Attr attr_nombreInstitucion_institucion = doc.createAttribute("nombreInstitucion");
            attr_nombreInstitucion_institucion.setValue(cadena_nodo_institucion[1]);
            Institucion.setAttributeNode(attr_nombreInstitucion_institucion);
            //cadena_original += "|" + cadena_nodo_institucion[1];
            /**
             * NODO Carrera
             */
            Element Carrera = doc.createElement("Carrera");
            rootElement.appendChild(Carrera);
            //cveCarrera
            Attr attr_cveCarrera_carrera = doc.createAttribute("cveCarrera");
            attr_cveCarrera_carrera.setValue(cadena_nodo_carrera[0].trim());
            Carrera.setAttributeNode(attr_cveCarrera_carrera);
            //cadena_original += "|" + cadena_nodo_carrera[0].trim();
            //nombreCarrera
            Attr attr_nombreCarrera_carrera = doc.createAttribute("nombreCarrera");
            attr_nombreCarrera_carrera.setValue(cadena_nodo_carrera[1]);
            Carrera.setAttributeNode(attr_nombreCarrera_carrera);
            //cadena_original += "|" + cadena_nodo_carrera[1];
            //fechaInicio --- opcional
            if (!cadena_nodo_carrera[2].trim().equalsIgnoreCase("^")) {
                Attr attr_fechaInicio_carrera = doc.createAttribute("fechaInicio");
                attr_fechaInicio_carrera.setValue(cadena_nodo_carrera[2]);
                Carrera.setAttributeNode(attr_fechaInicio_carrera);
                //cadena_original += "|" + cadena_nodo_carrera[2];
            } else {
                //cadena_original += "|";
            }
            //fechaTerminacion 
            Attr attr_fechaTerminacion_carrera = doc.createAttribute("fechaTerminacion");
            attr_fechaTerminacion_carrera.setValue(cadena_nodo_carrera[3]);
            Carrera.setAttributeNode(attr_fechaTerminacion_carrera);
            //cadena_original += "|" + cadena_nodo_carrera[3];
            //idAutorizacionReconocimiento
            Attr attr_idAutorizacionReconocimiento_carrera = doc.createAttribute("idAutorizacionReconocimiento");
            attr_idAutorizacionReconocimiento_carrera.setValue(cadena_nodo_carrera[4]);
            Carrera.setAttributeNode(attr_idAutorizacionReconocimiento_carrera);
            //cadena_original += "|" + cadena_nodo_carrera[4].trim();
            //autorizacionReconocimiento
            Attr attr_autorizacionReconocimiento_carrera = doc.createAttribute("autorizacionReconocimiento");
            attr_autorizacionReconocimiento_carrera.setValue(cadena_nodo_carrera[5]);
            Carrera.setAttributeNode(attr_autorizacionReconocimiento_carrera);
            //cadena_original += "|" + cadena_nodo_carrera[5];
            //numeroRvoe ----- opcional
            if (!cadena_nodo_carrera[6].trim().equalsIgnoreCase("^")) {
                Attr attr_numeroRvoe_carrera = doc.createAttribute("numeroRvoe");
                attr_numeroRvoe_carrera.setValue(cadena_nodo_carrera[6]);
                Carrera.setAttributeNode(attr_numeroRvoe_carrera);
                //cadena_original += "|" + cadena_nodo_carrera[6];
            } else {
                //cadena_original += "|";
            }
            /**
             * NODO Profesionista
             */
            Element Profesionista = doc.createElement("Profesionista");
            rootElement.appendChild(Profesionista);
            //curp
            Attr attr_curp_profesionista = doc.createAttribute("curp");
            attr_curp_profesionista.setValue(cadena_nodo_profesionista[0]);
            Profesionista.setAttributeNode(attr_curp_profesionista);
            //cadena_original += "|" + cadena_nodo_profesionista[0].trim();
            //nombre
            Attr attr_nombre_profesionista = doc.createAttribute("nombre");
            attr_nombre_profesionista.setValue(cadena_nodo_profesionista[1]);
            Profesionista.setAttributeNode(attr_nombre_profesionista);
            //cadena_original += "|" + cadena_nodo_profesionista[1];
            //primerApellido
            Attr attr_primerApellido_profesionista = doc.createAttribute("primerApellido");
            attr_primerApellido_profesionista.setValue(cadena_nodo_profesionista[2]);
            Profesionista.setAttributeNode(attr_primerApellido_profesionista);
            //cadena_original += "|" + cadena_nodo_profesionista[2];
            //segundoApellido ---- opcional
            if (!cadena_nodo_profesionista[3].trim().equalsIgnoreCase("^")) {
                Attr attr_segundoApellido_profesionista = doc.createAttribute("segundoApellido");
                attr_segundoApellido_profesionista.setValue(cadena_nodo_profesionista[3]);
                Profesionista.setAttributeNode(attr_segundoApellido_profesionista);
                //cadena_original += "|" + cadena_nodo_profesionista[3];
            } else {
                //cadena_original += "|";
            }
            //correoElectronico
            Attr attr_correoElectronico_profesionista = doc.createAttribute("correoElectronico");
            attr_correoElectronico_profesionista.setValue(cadena_nodo_profesionista[4]);
            Profesionista.setAttributeNode(attr_correoElectronico_profesionista);
            //cadena_original += "|" + cadena_nodo_profesionista[4].trim();
            /**
             * NODO Expedicion
             */
            Element Expedicion = doc.createElement("Expedicion");
            rootElement.appendChild(Expedicion);
            //fechaExpedicion
            Attr attr_fechaExpedicion_expedicion = doc.createAttribute("fechaExpedicion");
            attr_fechaExpedicion_expedicion.setValue(cadena_nodo_expedicion[0]);
            Expedicion.setAttributeNode(attr_fechaExpedicion_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[0];
            //idModalidadTitulacion
            Attr attr_idModalidadTitulacion_expedicion = doc.createAttribute("idModalidadTitulacion");
            attr_idModalidadTitulacion_expedicion.setValue(cadena_nodo_expedicion[1]);
            Expedicion.setAttributeNode(attr_idModalidadTitulacion_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[1];
            //modalidadTitulacion
            Attr attr_modalidadTitulacion_expedicion = doc.createAttribute("modalidadTitulacion");
            attr_modalidadTitulacion_expedicion.setValue(cadena_nodo_expedicion[2]);
            Expedicion.setAttributeNode(attr_modalidadTitulacion_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[2];
            //fechaExamenProfesional ------ Opcional
            if (!cadena_nodo_expedicion[3].trim().equalsIgnoreCase("^")) {
                Attr attr_fechaExamenProfesional_expedicion = doc.createAttribute("fechaExamenProfesional");
                attr_fechaExamenProfesional_expedicion.setValue(cadena_nodo_expedicion[3]);
                Expedicion.setAttributeNode(attr_fechaExamenProfesional_expedicion);
                //cadena_original += "|" + cadena_nodo_expedicion[3];
            } else {
                //cadena_original += "|";
            }
            //fechaExencionExamenProfesional -------Opcional
            if (!cadena_nodo_expedicion[4].trim().equalsIgnoreCase("^")) {
                Attr attr_fechaExencionExamenProfesional_expedicion = doc.createAttribute("fechaExencionExamenProfesional");
                attr_fechaExencionExamenProfesional_expedicion.setValue(cadena_nodo_expedicion[4]);
                Expedicion.setAttributeNode(attr_fechaExencionExamenProfesional_expedicion);
                //cadena_original += "|" + cadena_nodo_expedicion[4];
            } else {
                //cadena_original += "|";
            }
            //cumplioServicioSocial
            Attr attr_cumplioServicioSocial_expedicion = doc.createAttribute("cumplioServicioSocial");
            attr_cumplioServicioSocial_expedicion.setValue(cadena_nodo_expedicion[5]);
            Expedicion.setAttributeNode(attr_cumplioServicioSocial_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[5];
            //idFundamentoLegalServicioSocial
            Attr attr_idFundamentoLegalServicioSocial_expedicion = doc.createAttribute("idFundamentoLegalServicioSocial");
            attr_idFundamentoLegalServicioSocial_expedicion.setValue(cadena_nodo_expedicion[6]);
            Expedicion.setAttributeNode(attr_idFundamentoLegalServicioSocial_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[6];
            //fundamentoLegalServicioSocial
            Attr attr_fundamentoLegalServicioSocial_expedicion = doc.createAttribute("fundamentoLegalServicioSocial");
            attr_fundamentoLegalServicioSocial_expedicion.setValue(cadena_nodo_expedicion[7]);
            Expedicion.setAttributeNode(attr_fundamentoLegalServicioSocial_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[7];
            //idEntidadFederativa
            Attr attr_idEntidadFederativa_expedicion = doc.createAttribute("idEntidadFederativa");
            attr_idEntidadFederativa_expedicion.setValue(cadena_nodo_expedicion[8]);
            Expedicion.setAttributeNode(attr_idEntidadFederativa_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[8];
            //entidadFederativa
            Attr attr_entidadFederativa_expedicion = doc.createAttribute("entidadFederativa");
            attr_entidadFederativa_expedicion.setValue(cadena_nodo_expedicion[9]);
            Expedicion.setAttributeNode(attr_entidadFederativa_expedicion);
            //cadena_original += "|" + cadena_nodo_expedicion[9];
            /**
             * NODO Antecedente
             */
            Element Antecedente = doc.createElement("Antecedente");
            rootElement.appendChild(Antecedente);
            //institucionProcedencia
            Attr attr_institucionProcedencia_antecedentes = doc.createAttribute("institucionProcedencia");
            attr_institucionProcedencia_antecedentes.setValue(cadena_nodo_antecedentes[0]);
            Antecedente.setAttributeNode(attr_institucionProcedencia_antecedentes);
            //cadena_original += "|" + cadena_nodo_antecedentes[0];
            //idTipoEstudioAntecedente
            Attr attr_idTipoEstudioAntecedente_antecedentes = doc.createAttribute("idTipoEstudioAntecedente");
            attr_idTipoEstudioAntecedente_antecedentes.setValue(cadena_nodo_antecedentes[1]);
            Antecedente.setAttributeNode(attr_idTipoEstudioAntecedente_antecedentes);
            //cadena_original += "|" + cadena_nodo_antecedentes[1].trim();
            //tipoEstudioAntecedente
            Attr attr_tipoEstudioAntecedente_antecedentes = doc.createAttribute("tipoEstudioAntecedente");
            attr_tipoEstudioAntecedente_antecedentes.setValue(cadena_nodo_antecedentes[2]);
            Antecedente.setAttributeNode(attr_tipoEstudioAntecedente_antecedentes);
            //cadena_original += "|" + cadena_nodo_antecedentes[2];
            //idEntidadFederativa
            Attr attr_idEntidadFederativa_antecedentes = doc.createAttribute("idEntidadFederativa");
            attr_idEntidadFederativa_antecedentes.setValue(cadena_nodo_antecedentes[3]);
            Antecedente.setAttributeNode(attr_idEntidadFederativa_antecedentes);
            //cadena_original += "|" + cadena_nodo_antecedentes[3].trim();
            //entidadFederativa
            Attr attr_entidadFederativa_antecedentes = doc.createAttribute("entidadFederativa");
            attr_entidadFederativa_antecedentes.setValue(cadena_nodo_antecedentes[4]);
            Antecedente.setAttributeNode(attr_entidadFederativa_antecedentes);
            //cadena_original += "|" + cadena_nodo_antecedentes[4];
            //fechaInicio ----- Opcional
            if (!cadena_nodo_antecedentes[5].trim().equalsIgnoreCase("^")) {
                Attr attr_fechaInicio_antecedentes = doc.createAttribute("fechaInicio");
                attr_fechaInicio_antecedentes.setValue(cadena_nodo_antecedentes[5]);
                Antecedente.setAttributeNode(attr_fechaInicio_antecedentes);
                //cadena_original += "|" + cadena_nodo_antecedentes[5];
            } else {
                //cadena_original += "|";
            }
            //fechaTerminacion
            Attr attr_fechaTerminacion_antecedentes = doc.createAttribute("fechaTerminacion");
            attr_fechaTerminacion_antecedentes.setValue(cadena_nodo_antecedentes[6]);
            Antecedente.setAttributeNode(attr_fechaTerminacion_antecedentes);
            //cadena_original += "|" + cadena_nodo_antecedentes[6];
            //noCedula ----- Opcional
            if (!cadena_nodo_antecedentes[7].trim().equalsIgnoreCase("^")) {
                Attr attr_noCedula_antecedentes = doc.createAttribute("noCedula");
                attr_noCedula_antecedentes.setValue(cadena_nodo_antecedentes[7]);
                Antecedente.setAttributeNode(attr_noCedula_antecedentes);
                //cadena_original += "|" + cadena_nodo_antecedentes[7];
            } else {
                //cadena_original += "|";
            }
            //cadena_original += "||";
            //escribir el contenido en archivo xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //IDENTAR
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //IDENTAMOS CON VALOR 4
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount",
                    Integer.toString(4));
            DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(new File(System.getProperty("user.home") + "\\Downloads\\XML\\Titulos\\" + string_titulo_electronico.trim() + "_" + cadena_nodo_profesionista[2] + cadena_nodo_profesionista[3] + ".xml"));
            StreamResult result = new StreamResult(new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\Titulos\\" + string_titulo_electronico.trim() + "_" + cadena_nodo_profesionista[2] + (cadena_nodo_profesionista[3].equalsIgnoreCase("^") ? "" : cadena_nodo_profesionista[3]) + ".xml"));
            transformer.transform(source, result);
            //mensaje consola del xml:
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            //InputStream iStream = new FileInputStream(new File(System.getProperty("user.home") + "\\Downloads\\XML\\Titulos\\" + string_titulo_electronico.trim() + "_" + cadena_nodo_profesionista[2] + cadena_nodo_profesionista[3] + ".xml"));
            InputStream iStream = new FileInputStream(new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\XML\\Titulos\\" + string_titulo_electronico.trim() + "_" + cadena_nodo_profesionista[2] + (cadena_nodo_profesionista[3].equalsIgnoreCase("^") ? "" : cadena_nodo_profesionista[3]) + ".xml"));
            org.w3c.dom.Document document = documentBuilderFactory.newDocumentBuilder().parse(iStream);
            StringWriter stringWriter = new StringWriter();
            Transformer transformer_1 = TransformerFactory.newInstance().newTransformer();
            transformer_1.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer_1.transform(new DOMSource(document), new StreamResult(stringWriter));
            String output = stringWriter.toString();
            System.out.println(output.replaceAll("\n|\r|\\s+", ""));
            //Eliminamos el caracter del inicio ~ ya que no nos sirve
            concatena_cadenas_original = concatena_cadenas_original.substring(1, concatena_cadenas_original.length());
            //registrar el xml en bd:
            //query = "INSERT INTO TExml VALUES ('" + string_titulo_electronico.trim() + "','" + output + "','" + concatena_cadenas_original + "')";
            query = "{call Gnrt_Titulo (?,?,?,?,?)}";
            System.out.println(query);
            //conector.consulta(query);
            conexion = new CConexion();
            conexion.setRequest(request);
            con = conexion.GetconexionInSite();
            cstmt = con.prepareCall(query);
            cstmt.setString(1, string_titulo_electronico.trim());
            cstmt.setString(2, output.trim());
            cstmt.setString(3, concatena_cadenas_original.trim());
            cstmt.setInt(4, Integer.valueOf(id_Usuario));
            cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
            cstmt.execute();
            String RESP = cstmt.getString(5);
            String[] split = RESP.split("~");
            int totalActual = desencriptador.leeClaveTimbre(split[0]);

            query = "{call Usar_Timbre (?,?,?)}";
            cstmt = con.prepareCall(query);
            cstmt.setInt(1, Integer.valueOf(split[1]));
            cstmt.setString(2, desencriptador.GenerarClaveTimbres((totalActual - 1) + ""));
            cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
            cstmt.execute();
            respuesta = cstmt.getString(3);
        } catch (ParserConfigurationException | DOMException e) {
            Logger.getLogger(Cxml_titulo_electronico.class.getName()).log(Level.SEVERE, "Error:--------->", e);
            return e.getMessage();
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }
            if (cstmt != null && !cstmt.isClosed()) {
                cstmt.close();
            }
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                conexion.GetconexionInSite().close();
            }
        }
        return respuesta;
    }

    private PrivateKey getPrivateKey(File keyFile, String password)
            throws java.security.GeneralSecurityException, IOException {
        FileInputStream in = new FileInputStream(keyFile);
        PKCS8Key pkcs8 = new PKCS8Key(in, password.toCharArray());
        byte[] decrypted = pkcs8.getDecryptedBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypted);
        PrivateKey pk = null;
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
        System.out.println(pathFile + "\\" + filename);
        BufferedReader br = new BufferedReader(new FileReader(pathFile + "\\" + filename + ".pem"));
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

    private String remplazarCaracteresEspeciales(String dato) {

        if (dato.contains("&")) {
            dato = dato.replaceAll("&", "&amp;");
        }
        if (dato.contains("\"")) {
            dato = dato.replaceAll("\"", "&quot;");
        }
        if (dato.contains("<")) {
            dato = dato.replaceAll("<", "&lt;");
        }
        if (dato.contains(">")) {
            dato = dato.replaceAll(">", "&gt;");
        }
        //Se comenta este dato, pues el XML lo toma como un caracter válido.
        //&apos; es identificado como ' pero en el MET no hay error.
//        if (dato.contains("’")) {
//            dato = dato.replaceAll("’", "&apos;");
//        }

        return dato;
    }

    private void insertarCadenaYSello(String cadenaOriginal, String selloDigital, String idFirmante, String idTitulo, String IdUsuario) throws SQLException {
        CConexion conAux = new CConexion();
        Connection connAuxiliar = null;
        PreparedStatement pstmt = null;
        Bitacora bitacora = new Bitacora();
        bitacora.setId_Usuario(IdUsuario);
        bitacora.setModulo("Titulos");
        bitacora.setMovimiento("Insertar Cadena y Sello");
        CBitacora controlBitacora;
        try {
            String query = "UPDATE Titulo_Firmante_Intermedia SET cadenaOriginal = ?, sello = ? WHERE ID_Firmante = ? AND ID_Titulo = ?";
            conAux.setRequest(request);
            connAuxiliar = conAux.GetconexionInSite();
            pstmt = connAuxiliar.prepareStatement(query);

            pstmt.setString(1, cadenaOriginal);
            pstmt.setString(2, selloDigital);
            pstmt.setString(3, idFirmante);
            pstmt.setString(4, idTitulo);
            pstmt.executeUpdate();
            bitacora.setInformacion("Cadena y Sello||CadenaOriginal:" + cadenaOriginal + "||SelloDigital:" + selloDigital + "||Firmante:" + idFirmante + "||Titulo:" + idTitulo);
            controlBitacora = new CBitacora(bitacora);
            controlBitacora.setRequest(request);
            controlBitacora.addBitacoraGeneral();
        } catch (SQLException ex) {
            Logger.getLogger(Cxml_titulo_electronico.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pstmt.close();
            connAuxiliar.close();
        }
    }
}
