<%-- 
    Document   : queryCUsuarios
    Created on : 15-ene-2019, 11:31:56
    Author     : Braulio Sorcia
--%>

<%@page import="com.ginndex.titulos.control.CUsuarios"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CUsuarios" class="com.ginndex.titulos.control.CUsuarios" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CUsuarios.setRequest(request);
    out.print(CUsuarios.establecerAcciones());
%>