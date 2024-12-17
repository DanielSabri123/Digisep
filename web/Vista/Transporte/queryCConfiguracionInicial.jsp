<%-- 
    Document   : queryCConfiguracionInicial
    Created on : 23/01/2019, 09:45:02 AM
    Author     : Paola Alonso
--%>

<%@page import="com.ginndex.titulos.control.CAlumnos"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CConfiguracionInicial" class="com.ginndex.titulos.control.CConfiguracionInicial" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CConfiguracionInicial.setRequest(request);
    out.print(CConfiguracionInicial.EstablecerAcciones());
%>
