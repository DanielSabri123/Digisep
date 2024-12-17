<%-- 
    Document   : queryCReportes
    Created on : 17-may-2022, 11:24:55
    Author     : BSorcia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CReportes" class="com.ginndex.titulos.control.CReportes" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CReportes.setRequest(request);
    out.print(CReportes.establecerAcciones());
%>
