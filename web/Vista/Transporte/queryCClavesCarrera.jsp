<%-- 
    Document   : queryCClavesCarrera
    Created on : 14/12/2018, 03:09:32 PM
    Author     : Paola Alonso
--%>
<%@page import="com.ginndex.titulos.control.CClavesCarrera"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CClavesCarrera" class="com.ginndex.titulos.control.CClavesCarrera" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CClavesCarrera.setRequest(request);
    out.print(CClavesCarrera.EstablecerAcciones());
%>