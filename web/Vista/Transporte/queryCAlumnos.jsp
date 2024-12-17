<%-- 
    Document   : queryCAlumnos
    Created on : 5/12/2018, 03:54:08 PM
    Author     : Paola Alonso
--%>
<%@page import="com.ginndex.titulos.control.CAlumnos"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CAlumno" class="com.ginndex.titulos.control.CAlumnos" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CAlumno.setRequest(request);
    out.print(CAlumno.EstablecerAcciones());
%>