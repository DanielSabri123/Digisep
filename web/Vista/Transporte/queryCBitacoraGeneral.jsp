<%-- 
    Document   : queryCBitacoraGeneral
    Created on : 07-may-2019, 16:18:12
    Author     : Braulio Sorcia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.ginndex.titulos.control.CBitacora"%>
<jsp:useBean id="CBitacora" class="com.ginndex.titulos.control.CBitacora" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CBitacora.setRequest(request);
    out.print(CBitacora.establecerAccionesBitacoraGeneral());
%>