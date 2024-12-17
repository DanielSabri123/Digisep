<%-- 
    Document   : queryCRecuperarContrasenia
    Created on : 23/02/2023, 10:40:42 AM
    Author     : Ricardo Reyna
--%>

<%@page  contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CRecuperarContrasenia" class="com.ginndex.titulos.control.CRecuperarContrasenia" scope="session"/>
<jsp:useBean id="CInSite" class="com.ginndex.titulos.control.CConexion" scope="session"/>
<%
    System.out.println("REQUEST TRANS "+ request.getParameter("txtCarpeta"));
    CInSite.setRequest(request);
    CInSite.GetconexionInSite();
    CRecuperarContrasenia.setRequest(request);

    out.print(CRecuperarContrasenia.mandarCorreo());

%>


