<%-- 
    /*
    * @Titulos Electrónicos 0.1 29/03/17
    * 
    * Copyright 2018 Grupo Inndex. All rights reserved.
    * Grupo Inndex/CONFIDENTIAL Use is subject to license terms.
    */
    /**
    * @author Braulio Sorcia
    * @version 0.1, 30/11/2018
    * @since 0.1
    */
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

    HttpSession sessionOk = request.getSession();
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
    String usuario = "" + sessionOk.getAttribute("NombreUsuario");
    String Nombre = "" + sessionOk.getAttribute("Nombre");
    String NombreC = "" + sessionOk.getAttribute("Nombre") + " " + sessionOk.getAttribute("APaterno");
    String rol = "" + sessionOk.getAttribute("NombreTipo");
    String comminglock = request.getParameter("comminglock");
    String SupportX = "" + sessionOk.getAttribute("BD_Name");
    String Id_SuarioLog = "" + sessionOk.getAttribute("Id_Usuario");
    String Menu = "" + sessionOk.getAttribute("Menu");
    if (comminglock == null) {
        comminglock = "";
    }
    if (usuario.equals("null") || !rol.equalsIgnoreCase("Admin")) {

        session.invalidate();
        String Login = "../Generales/LogIn.jsp";
        response.sendRedirect(Login);
    }
    int timbresDisponibles = 458;
%>
<!DOCTYPE html>
<html class="no-focus" lang="es">
    <head>
        <meta charset="utf-8">

        <title>Digi-SEP - Bienvenida</title>

        <meta name="description" content="Digi-SEP - sistema de titulos, cédulas y certificados electrónicos">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <!-- Stylesheets -->
        <!-- Web fonts -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400italic,600,700%7COpen+Sans:300,400,400italic,600,700">

        <!-- Bootstrap and OneUI CSS framework -->
        <link rel="stylesheet" href="../assets/css/bootstrap.min.css">
        <link rel="stylesheet" id="css-main" href="../assets/css/oneui.css">
        <link rel="stylesheet" id="css-theme" href="../assets/css/themes/modern.min.css">

        <link rel="stylesheet" href="../assets/js/plugins/sweetalert/sweetalert2.css">

        <!-- You can include a specific file from css/themes/ folder to alter the default color theme of the template. eg: -->
        <!-- <link rel="stylesheet" id="css-theme" href="assets/css/themes/flat.min.css"> -->
        <!-- END Stylesheets -->
    </head>
    <body>
        <!-- Page Container -->
        <div id="page-container" class="sidebar-l sidebar-o side-scroll header-navbar-fixed">
            <!-- END Side Overlay -->

            <!-- Sidebar -->
            <nav id="sidebar">
                <!-- Sidebar Scroll Container -->
                <div id="sidebar-scroll">
                    <!-- Sidebar Content -->
                    <!-- Adding .sidebar-mini-hide to an element will hide it when the sidebar is in mini mode -->
                    <div class="sidebar-content">
                        <!-- Side Header -->
                        <div class="side-header side-content bg-white-op">

                            <a class="h5 text-white" href="Bienvenida.jsp">
                                <i><img src="../assets/img/Digi-SEP BLANCO.png" width="35" alt="logo"></i><span> Digi-SEP</span>
                            </a>
                        </div>
                        <!-- END Side Header -->

                        <!-- Side Content -->
                        <div class="side-content">
                            <ul class='nav-main'>

                                <%=Menu%>
                            </ul>
                        </div>
                        <!-- END Side Content -->
                    </div>
                    <!-- Sidebar Content -->
                </div>
                <!-- END Sidebar Scroll Container -->
            </nav>
            <!-- END Sidebar -->

            <!-- Header -->
            <header id="header-navbar" class="content-mini content-mini-full">
                <!-- Header Navigation Right -->
                <ul class="nav-header pull-right">
                    <li>
                        <div class="btn-group">
                            <button class="btn btn-default dropdown-toggle" data-toggle="dropdown" type="button">
                                <i class="fa fa-user"></i> 
                            </button>
                            <ul class="dropdown-menu dropdown-menu-right">
                                <li class="dropdown-header">Acciones</li>
                                <li class="divider"></li>
                                <li>
                                    <a tabindex="-1" data-target="#modal-cambioC" href="#" data-toggle="modal">
                                        <i class="si si-settings pull-right"></i>Cambiar contraseña
                                    </a>
                                </li> 
                                <li>
                                    <a tabindex="-1" href="../Generales/LogOut.jsp">
                                        <i class="si si-logout pull-right"></i>
                                        <span class="badge badge-primary pull-right"></span>Cerrar sesión
                                    </a>
                                </li>                               
                            </ul>
                        </div>
                    </li>
                </ul>
                <!-- END Header Navigation Right -->

                <!-- Header Navigation Left -->
                <ul class="nav-header pull-left">
                    <li class="hidden-md hidden-lg">
                        <!-- Layout API, functionality initialized in App() -> uiLayoutApi() -->
                        <button class="btn btn-default" data-toggle="layout" data-action="sidebar_toggle" type="button" onclick="$('#backdrop').show();">
                            <i class="fa fa-navicon"></i>
                        </button>
                    </li>
                    <%--
                    <li class="hidden-xs hidden-sm">
                        <!-- Layout API, functionality initialized in App() -> uiLayoutApi() -->
                        <button class="btn btn-default" data-toggle="layout" data-action="sidebar_mini_toggle" type="button" style="width: 50px;">
                            <i class="fa fa-ellipsis-v"></i>
                        </button>
                    </li>--%>
                </ul>
                <!-- END Header Navigation Left -->
            </header>
            <!-- END Header -->

            <!-- Main Container -->
            <main id="main-container">
                <!-- Page Content -->
                <div class="content content-boxed" style="margin-top:25px;">

                    <div class="row text-center">
                        <div class="col-lg-4 col-sm-4">
                            <table class="block-table text-center">
                                <tbody>
                                    <tr>
                                        <td style="width: 50%;">
                                            <div class="push-15 push-15-t">
                                                <i class="glyphicon glyphicon-certificate fa-3x text-primary"></i>
                                            </div>
                                        </td>
                                        <td class="bg-gray-lighter" style="width: 50%;">
                                            <div class="h1 font-w700" data-toggle="countTo" id="divTimbresDisponibles"></div>
                                            <div class="h5 text-muted text-uppercase push-5-t">Timbres disponibles</div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div class="col-lg-4 col-sm-4">
                            <table class="block-table text-center">
                                <tbody>
                                    <tr>
                                        <td style="width: 50%;">
                                            <div class="push-15 push-15-t">
                                                <i class="fa fa-file-text-o fa-3x text-primary"></i>
                                            </div>
                                        </td>
                                        <td class="bg-gray-lighter" style="width: 50%;">
                                            <div class="h1 font-w700" data-toggle="countTo" id="divTitulosUsados"></div>
                                            <div class="h5 text-muted text-uppercase push-5-t">Total Títulos</div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div class="col-lg-4 col-sm-4">
                            <table class="block-table text-center">
                                <tbody>
                                    <tr>
                                        <td style="width: 50%;">
                                            <div class="push-15 push-15-t">
                                                <i class="si si-badge fa-3x text-primary"></i>
                                            </div>
                                        </td>
                                        <td class="bg-gray-lighter" style="width: 50%;">
                                            <div class="h1 font-w700" data-toggle="countTo" id="divCertificadosUsados"></div>
                                            <div class="h5 text-muted text-uppercase push-5-t">Total Certificados</div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- Overview -->
                    <div class="block block-opt-refresh-icon4 push-20-t">
                        <div class="block-content block-content-full bg-white-op">
                            <div class="text-center">
                                <img class="" src="../assets/img/Digi-SepRPNG_a.png" style="max-height: 250px;"/>
                            </div>
                        </div>
                    </div>
                    <!-- END Overview -->

                    <div class="row">
                        <div class="col-sm-6 col-md-3">
                            <a class="block block-link-hover3 text-center" href="Titulos.jsp" style="border-radius: 10px;">
                                <div class="block-content block-content-full">
                                    <div class="h1 font-w700 text-flat"><i class="fa fa-file-text-o"></i></div>
                                </div>
                                <div class="block-content block-content-full block-content-mini bg-primary-op text-white" style="border-bottom-left-radius: 10px; border-bottom-right-radius:  10px;">Títulos</div>
                            </a>
                        </div>
                        <div class="col-sm-6 col-md-3">
                            <a class="block block-link-hover3 text-center" href="Certificados.jsp" style="border-radius: 10px;">
                                <div class="block-content block-content-full">
                                    <div class="h1 font-w700 text-city"><i class="si si-badge"></i></div>
                                </div>
                                <div class="block-content block-content-full block-content-mini bg-primary-op text-white" style="border-bottom-left-radius: 10px; border-bottom-right-radius:  10px;">Certificados</div>
                            </a>
                        </div>
                        <div class="col-sm-6 col-md-3">
                            <a class="block block-link-hover3 text-center" href="Firmantes.jsp" style="border-radius: 10px;">
                                <div class="block-content block-content-full">
                                    <div class="h1 font-w700 text-info"><i class="fa fa-users"></i></div>
                                </div>
                                <div class="block-content block-content-full block-content-mini bg-primary-op text-white" style="border-bottom-left-radius: 10px; border-bottom-right-radius:  10px;">Firmantes</div>
                            </a>
                        </div>
                        <div class="col-sm-6 col-md-3">
                            <a class="block block-link-hover3 text-center" href="ConfiguracionBasica.jsp" style="border-radius: 10px;">
                                <div class="block-content block-content-full">
                                    <div class="h1 font-w700 text-flat"><i class="fa fa-cogs"></i></div>
                                </div>
                                <div class="block-content block-content-full block-content-mini bg-primary-op text-white" style="border-bottom-left-radius: 10px; border-bottom-right-radius:  10px;">Configuraciones</div>
                            </a>
                        </div>
                    </div>
                </div>
                <!-- END Page Content -->
            </main>
            <!-- END Main Container -->

            <!-- Footer -->
            <footer id="page-footer" class="content-mini content-mini-full font-s12 bg-gray-lighter clearfix">
                <div class="pull-right">
                    I <i class="fa fa-heart text-city"></i> <a class="font-w600" href="http://www.grupoinndex.com" target="_blank">Grupo Inndex</a>
                </div>
                <div class="pull-left">
                    <a class="font-w600" href="http://www.grupoinndex.com/ControlEscolarWeb.html" target="_blank">Grupo Inndex</a>&copy; <span class="js-year-copy"></span> 
                </div>
            </footer>
            <!-- END Footer -->
        </div>
        <!-- END Page Container -->

        <!-- MODAL CAMBIO DE CONTRASEÑA -->
        <%
            if (!usuario.equals("null")) {
        %>
        <jsp:include page="../Generales/cambiarContrasenia.jsp">
            <jsp:param name="allow" value="si" /> 
        </jsp:include>
        <%}%>
        <!-- OneUI Core JS: jQuery, Bootstrap, slimScroll, scrollLock, Appear, CountTo, Placeholder, Cookie and App.js -->
        <script src="../assets/js/core/jquery.min.js"></script>
        <script src="../assets/js/plugins/jquery-validation/jquery.validate.min.js"></script>
        <script src="../assets/js/plugins/jquery-validation/additional-methods.js"></script>
        <script src="../assets/js/core/bootstrap.min.js"></script>
        <script src="../assets/js/core/jquery.slimscroll.min.js"></script>
        <script src="../assets/js/core/jquery.scrollLock.min.js"></script>
        <script src="../assets/js/core/jquery.appear.min.js"></script>
        <script src="../assets/js/core/jquery.countTo.min.js"></script>
        <script src="../assets/js/core/jquery.placeholder.min.js"></script>
        <script src="../assets/js/core/js.cookie.min.js"></script>
        <script src="../assets/js/app.js"></script>

        <!-- Page JS Plugins -->
        <script src="../assets/js/plugins/bootstrap-notify/bootstrap-notify.min.js"></script>
        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/sessionTimeOut/bootstrap-session-timeout.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesDashBoard.js"></script>
        <script>
                            jQuery(function () {
                                // Init page helpers (Appear + CountTo plugins)
                                App.initHelpers(['appear', 'appear-countTo', 'notify']);
                            });
        </script>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
    </body>
</html>