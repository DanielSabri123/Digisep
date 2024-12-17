<%-- 
    Document   : queryCBienvenida
    Created on : 14/02/2019, 01:07:10 PM
    Author     : Paola Alonso
--%>

<%@page import="com.ginndex.titulos.control.CBienvenida"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CBienvenida" class="com.ginndex.titulos.control.CBienvenida" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CBienvenida.setRequest(request);
    out.print(CBienvenida.EstablecerAcciones());
%>

