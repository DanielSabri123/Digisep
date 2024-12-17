/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.soap;

import java.math.BigInteger;
import mx.sep.mec.web.ws.schemas.productivo.AutenticacionType;
import mx.sep.mec.web.ws.schemas.productivo.DescargaTituloElectronicoRequest;
import mx.sep.mec.web.ws.schemas.productivo.DescargaTituloElectronicoResponse;
import mx.sep.mec.web.ws.schemas.productivo.TitulosPortTypeService;

/**
 *
 * @author Paulina
 */
public class DescargaTituloElectronicoProductivo {

    private final String usuario;
    private final String password;
    private final String numLote;
    private byte[] archivoByteArray;
    private String mensaje;
    private DescargaTituloElectronicoRequest dteRequest;
    private DescargaTituloElectronicoResponse dteResponse;

    public DescargaTituloElectronicoProductivo(String usuario, String password,String numLote) {
        this.usuario = usuario;
        this.password = password;
        this.numLote = numLote;
    }

    public byte[] getArchivoByteArray() {
        return archivoByteArray;
    }

    public String getMensaje() {
        return mensaje;
    }

    /**
     * Carga los objetos requeridos para ejecutar el proceso de consumo del WSDL
     *
     * @return String Cadena de respuesta, si todo sale bien devuelve un
     * success. Los métodos getMensaje y getEstatusLote se llenan.
     */
    public String descargarArchivoSOAP() {

        //Declaramos el objeto que lleva la información del proceso de descargar
        dteRequest = new DescargaTituloElectronicoRequest();

        //Declaramos y llenamos el objeto de autenticación necesario para el consumo del servicio SOAP
        AutenticacionType auth = new AutenticacionType();
        auth.setUsuario(usuario);
        auth.setPassword(password);

        dteRequest.setAutenticacion(auth);
        dteRequest.setNumeroLote(new BigInteger(numLote));

        //Este objeto es el encargado de ejecutar el SOAP
        TitulosPortTypeService port = new TitulosPortTypeService();

        /**
         * Ejecutamos el método de descarga. Este nos devuelve el objeto
         * Response con los datos de respuesta
         *
         * @return DescargaTituloElectronicoResponse
         */
        dteResponse = port.getTitulosPortTypeSoap11().descargaTituloElectronico(dteRequest);

        this.mensaje = dteResponse.getMensaje();
        this.archivoByteArray = dteResponse.getTitulosBase64();

        return "success";
    }

}
