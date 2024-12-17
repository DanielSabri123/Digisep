/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.soap;

import mx.sep.mec.web.ws.schemas.productivo.TitulosPortTypeService;
import mx.sep.mec.web.ws.schemas.productivo.AutenticacionType;
import mx.sep.mec.web.ws.schemas.productivo.CancelaTituloElectronicoRequest;
import mx.sep.mec.web.ws.schemas.productivo.CancelaTituloElectronicoResponse;

/**
 *
 * @author Paulina
 */
public class CancelaTituloElectronicoProductivo {

    private String folioControl;
    private String motCancelacion;
    private String usuario;
    private String password;
    private String codigo;
    private String mensaje;
    private CancelaTituloElectronicoRequest cteRequest;
    private CancelaTituloElectronicoResponse cteResponse;

    public CancelaTituloElectronicoProductivo(String folioControl, String motCancelacion, String usuario, String password) {
        this.folioControl = folioControl;
        this.motCancelacion = motCancelacion;
        this.usuario = usuario;
        this.password = password;
    }

    public String getFolioControl() {
        return folioControl;
    }

    public void setFolioControl(String folioControl) {
        this.folioControl = folioControl;
    }

    public String getMotCancelacion() {
        return motCancelacion;
    }

    public void setMotCancelacion(String motCancelacion) {
        this.motCancelacion = motCancelacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public CancelaTituloElectronicoRequest getCteRequest() {
        return cteRequest;
    }

    public void setCteRequest(CancelaTituloElectronicoRequest cteRequest) {
        this.cteRequest = cteRequest;
    }

    public CancelaTituloElectronicoResponse getCteResponse() {
        return cteResponse;
    }

    public void setCteResponse(CancelaTituloElectronicoResponse cteResponse) {
        this.cteResponse = cteResponse;
    }

    public String cancelarTituloElectronicoSOAP() {

        cteRequest = new CancelaTituloElectronicoRequest();

        //Declaramos y llenamos el objeto de autenticación necesario para el consumo del servicio SOAP
        AutenticacionType auth = new AutenticacionType();
        auth.setUsuario(usuario);
        auth.setPassword(password);

        //Llenamos los atributos de la clase instanciada anteriormente
        cteRequest.setFolioControl(folioControl);
        cteRequest.setMotCancelacion(motCancelacion);
        cteRequest.setAutenticacion(auth);

        //Este objeto es el encargado de ejecutar el SOAP
        TitulosPortTypeService port = new TitulosPortTypeService();

        /**
         * Ejecutamos el método de cancelación. Este nos devuelve el objeto
         * Response con los datos de respuesta
         *
         * @return CancelaTituloElectronicoResponse
         */
        cteResponse = port.getTitulosPortTypeSoap11().cancelaTituloElectronico(cteRequest);

        this.codigo = cteResponse.getCodigo() + "";
        this.mensaje = cteResponse.getMensaje();

        return "success";
    }

}
