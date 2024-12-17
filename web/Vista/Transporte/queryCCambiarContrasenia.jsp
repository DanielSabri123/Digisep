<%-- 
    Document   : queryCCambiarContrasenia
    Created on : 01-feb-2019, 11:47:57
    Author     : Braulio Sorcia
--%>
<%@page import="com.ginndex.titulos.control.CCambiarContrasenia"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CCambiarContrasenia" class="com.ginndex.titulos.control.CCambiarContrasenia" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CCambiarContrasenia.setRequest(request);
    out.print(CCambiarContrasenia.establecerAcciones());
%>