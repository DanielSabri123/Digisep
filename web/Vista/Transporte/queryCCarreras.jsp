<%-- 
    Document   : queryCCarreras
    Created on : 10/12/2018, 11:40:45 AM
    Author     : Paola Alonso
--%>

<%@page import="com.ginndex.titulos.control.CCarrerasCarga"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CCarrera" class="com.ginndex.titulos.control.CCarrerasCarga" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CCarrera.setRequest(request);
    out.print(CCarrera.EstablecerAcciones());
%>
