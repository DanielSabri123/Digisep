/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.soap;

import java.math.BigInteger;
import mx.sep.mec.web.ws.schemas.AutenticacionType;
import mx.sep.mec.web.ws.schemas.ConsultaProcesoTituloElectronicoRequest;
import mx.sep.mec.web.ws.schemas.ConsultaProcesoTituloElectronicoResponse;
import mx.sep.mec.web.ws.schemas.TitulosPortTypeService;

/**
 *
 * @author Paulina
 */
public class ConsultaProcesoTituloElectronico {

    private final String usuario;
    private final String password;
    private final String numLote;
    String mensaje;
    String estatusLote;
    ConsultaProcesoTituloElectronicoRequest cpteRequest;
    ConsultaProcesoTituloElectronicoResponse cpteResponse;

    public ConsultaProcesoTituloElectronico(String usuario, String password, String numLote) {
        this.usuario = usuario;
        this.password = password;
        this.numLote = numLote;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getEstatusLote() {
        return estatusLote;
    }

    /**
     * Carga los objetos requeridos para ejecutar el proceso de consumo del WSDL
     *
     * @return String Cadena de respuesta, si todo sale bien devuelve un
     * success. Los métodos getMensaje y getEstatusLote se llenan.
     */
    public String consultarProcesoSOAP() {

        //Declaramos el objeto que lleva la información del proceso de consultar
        cpteRequest = new ConsultaProcesoTituloElectronicoRequest();

        //Declaramos y llenamos el objeto de autenticación necesario para el consumo del servicio SOAP
        AutenticacionType auth = new AutenticacionType();
        auth.setUsuario(usuario);
        auth.setPassword(password);

        cpteRequest.setAutenticacion(auth);
        cpteRequest.setNumeroLote(new BigInteger(numLote));

        //Este objeto es el encargado de ejecutar el SOAP
        TitulosPortTypeService port = new TitulosPortTypeService();

        /**
         * Ejecutamos el método de consulta. Este nos devuelve el objeto
         * Response con los datos de respuesta
         *
         * @return ConsultaProcesoTituloElectronicoResponse
         */
        cpteResponse = port.getTitulosPortTypeSoap11().consultaProcesoTituloElectronico(cpteRequest);

        this.mensaje = cpteResponse.getMensaje();
        this.estatusLote = cpteResponse.getEstatusLote() + "";

        return "success";
    }

}
