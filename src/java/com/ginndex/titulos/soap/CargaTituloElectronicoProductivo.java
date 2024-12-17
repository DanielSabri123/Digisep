/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.soap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.sep.mec.web.ws.schemas.productivo.AutenticacionType;
import mx.sep.mec.web.ws.schemas.productivo.CargaTituloElectronicoRequest;
import mx.sep.mec.web.ws.schemas.productivo.CargaTituloElectronicoResponse;
import mx.sep.mec.web.ws.schemas.productivo.TitulosPortTypeService;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Braulio Sorcia
 */
public class CargaTituloElectronicoProductivo {

    private final String usuario;
    private final String password;
    private final String nombreArchivo;
    private byte[] archivoByteArray;
    private String mensaje;
    private String numLote;
    private CargaTituloElectronicoRequest cteRequest;
    private CargaTituloElectronicoResponse cteResponse;

    public CargaTituloElectronicoProductivo(String usuario, String password, String nombreArchivo) {
        this.usuario = usuario;
        this.password = password;
        this.nombreArchivo = nombreArchivo;
    }

    public byte[] getArchivoByteArray() {
        return archivoByteArray;
    }

    public void setArchivoByteArray(byte[] archivoByteArray) {
        this.archivoByteArray = archivoByteArray;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getNumLote() {
        return numLote;
    }

    /**
     * Carga los objetos requeridos para ejecutar el proceso de consumo del WSDL
     *
     * @return String Cadena de respuesta, si todo sale bien devuelve un
     * success. Los métodos getMensaje y getNumLote se llenan.
     */
    public String cargarArchivoSOAP() {

        //Declaramos el objeto que lleva la información del proceso de cargar
        cteRequest = new CargaTituloElectronicoRequest();

        //Declaramos y llenamos el objeto de autenticación necesario para el consumo del servicio SOAP
        AutenticacionType auth = new AutenticacionType();
        auth.setUsuario(usuario);
        auth.setPassword(password);

        //Llenamos los atributos de la clase instanciada anteriormente
        cteRequest.setArchivoBase64(archivoByteArray);
        cteRequest.setNombreArchivo(nombreArchivo);
        cteRequest.setAutenticacion(auth);

        //Este objeto es el encargado de ejecutar el SOAP
        TitulosPortTypeService port = new TitulosPortTypeService();

        /**
         * Ejecutamos el método de carga. Este nos devuelve el objeto Response
         * con los datos de respuesta
         *
         * @return CargaTituloElectronicoResponse
         */
        
        //victor pruebas desde aqui
        cteResponse = port.getTitulosPortTypeSoap11().cargaTituloElectronico(cteRequest);

        this.mensaje = cteResponse.getMensaje();
        this.numLote = cteResponse.getNumeroLote() + "";
        if (this.numLote.equalsIgnoreCase("null") || this.numLote.equalsIgnoreCase("")) {
            return "eSOAP¬" + this.mensaje;
        } else {
            return "success";
        }
    }

    public String fileToArray(File file) {
        try {
            archivoByteArray = FileUtils.readFileToByteArray(file);
            return "ok";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaTituloElectronicoProductivo.class.getName()).log(Level.SEVERE, null, ex);
            return "fileNotFound";
        } catch (IOException ex) {
            Logger.getLogger(CargaTituloElectronicoProductivo.class.getName()).log(Level.SEVERE, null, ex);
            return "ioException";
        }
    }
}
