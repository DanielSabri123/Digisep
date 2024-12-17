<%-- 
    Document   : queryCReporteDGAIR
    Created on : 17-feb-2023, 12:53:31
    Author     : BSorcia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CReporteDGAIR" class="com.ginndex.titulos.control.CReporteDGAIR" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CReporteDGAIR.setRequest(request);
    out.print(CReporteDGAIR.establecerAcciones());
%>

