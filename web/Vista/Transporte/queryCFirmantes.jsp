<%-- 
    Document   : queryCFirmantes
    Created on : 17-dic-2018, 12:38:45
    Author     : Braulio Sorcia
--%>

<%@page import="com.ginndex.titulos.control.CFirmantes"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CFirmantes" class="com.ginndex.titulos.control.CFirmantes" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CFirmantes.setRequest(request);
    out.print(CFirmantes.establecerAcciones());
%>