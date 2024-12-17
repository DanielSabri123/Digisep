<%-- 
    Document   : queryCCertificados
    Created on : 18/12/2018, 09:38:09 AM
    Author     : Paola Alonso
--%>

<%@page import="com.ginndex.titulos.control.CCertificados"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CCertificados" class="com.ginndex.titulos.control.CCertificados" scope="session"/>
<%
    HttpSession sessionOk = request.getSession();
    if (sessionOk.getAttribute("NombreUsuario") == null) {
        out.print("sessionOff");
    } else {
        request.setCharacterEncoding("UTF-8");
        CCertificados.setRequest(request);
        out.print(CCertificados.EstablecerAcciones());
    }
%>