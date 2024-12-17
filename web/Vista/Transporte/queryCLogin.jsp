<%@page  contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CLogin" class="com.ginndex.titulos.control.CLogin" scope="session"/>
<jsp:useBean id="CInSite" class="com.ginndex.titulos.control.CConexion" scope="session"/>
<%
    CInSite.setRequest(request);
    CInSite.GetconexionInSite();
    CLogin.setRequest(request);
    CLogin.ValidarUsuario();
    response.sendRedirect(CLogin.getPagina());
%>
