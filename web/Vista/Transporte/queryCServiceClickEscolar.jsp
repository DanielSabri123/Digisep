<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="CServiceClickEscolar" class="com.ginndex.titulos.api.ClickEscolarHandlerService" scope="session"/>
<%
    request.setCharacterEncoding("UTF-8");
    CServiceClickEscolar.setRequest(request);
    out.print(CServiceClickEscolar.EstablecerAccionesClickEscolarHandlerService());
%>