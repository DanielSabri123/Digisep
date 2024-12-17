<%-- 
    Document   : queryCTitulos
    Created on : 08-ene-2019, 14:49:36
    Author     : Braulio Sorcia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CTitulos" class="com.ginndex.titulos.control.CTitulos" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CTitulos.setRequest(request);
    out.print(CTitulos.EstablecerAcciones());
%>
