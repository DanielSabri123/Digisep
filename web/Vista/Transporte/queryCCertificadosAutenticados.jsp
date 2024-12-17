<%-- 
    Document   : queryCCertificadosAutenticados
    Created on : 11-feb-2021, 14:12:36
    Author     : Braulio Sorcia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CCertificadosAutenticados" class="com.ginndex.titulos.control.CCertificadosAutenticados" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CCertificadosAutenticados.setRequest(request);
    out.print(CCertificadosAutenticados.establecerAccionesCertificadosAutenticados());
%>
