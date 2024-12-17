<%-- 
    Document   : queryCPermisos
    Created on : 29-ene-2019, 9:51:42
    Author     : Braulio Sorcia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CPermisos" class="com.ginndex.titulos.control.CPermisos" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CPermisos.setRequest(request);
    out.print(CPermisos.establecerAcciones());
%>
