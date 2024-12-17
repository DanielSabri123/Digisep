<%-- 
    Document   : queryCTitulosAutenticados
    Created on : 09-jun-2020, 14:12:36
    Author     : Braulio Sorcia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CTitulosAutenticados" class="com.ginndex.titulos.control.CTitulosAutenticados" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CTitulosAutenticados.setRequest(request);
    out.print(CTitulosAutenticados.EstablecerAcciones());
%>
