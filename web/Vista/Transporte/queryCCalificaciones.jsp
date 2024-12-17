<%-- 
    Document   : queryCCalificaciones
    Created on : 7/12/2018, 03:32:27 PM
    Author     : Paola Alonso
--%>
<%@page import="com.ginndex.titulos.control.CCalificaciones"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CCalificaciones" class="com.ginndex.titulos.control.CCalificaciones" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CCalificaciones.setRequest(request);
    out.print(CCalificaciones.EstablecerAccionesCalificaciones());
%>