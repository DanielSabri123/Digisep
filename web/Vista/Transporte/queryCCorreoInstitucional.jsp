<%-- 
    Document   : queryCCorreoInstitucional
    Created on : 6/03/2023, 12:53:45 PM
    Author     : Ricardo Reyna
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.ginndex.titulos.control.CCorreoInstitucional" %>
<jsp:useBean id="CCorreo" class="com.ginndex.titulos.control.CCorreoInstitucional" />

<%
    request.setCharacterEncoding("UTF-8");
    CCorreo.setRequest(request);
    out.print(CCorreo.EstablecerAcciones());
%>
