<%-- 
    Document   : keep-alive
    Created on : 01-mar-2022, 17:23:27
    Author     : BSorcia
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="KeepAliveSession" class="com.ginndex.titulos.util.KeepAliveSession" scope="session"/>
<%
    HttpSession sessionOk = request.getSession();
    if (sessionOk.getAttribute("NombreUsuario") == null) {
        out.print("sessionOff");
    } else {
        request.setCharacterEncoding("UTF-8");
        KeepAliveSession.setRequest(request);
        KeepAliveSession.keepAliveSession();
    }
%>
