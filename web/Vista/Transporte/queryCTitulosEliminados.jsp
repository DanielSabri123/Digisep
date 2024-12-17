<%-- 
    Document   : queryCTitulosEliminados
    Created on : 22/03/2023, 01:44:54 PM
    Author     : Ricardo Reyna
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CTitulosEliminados" class="com.ginndex.titulos.control.CTitulosEliminados" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CTitulosEliminados.setRequest(request);
    out.print(CTitulosEliminados.EstablecerAcciones());
%>