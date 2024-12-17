<%-- 
    Document   : Firmantes
    Created on : 14-dic-2018, 12:32:42
    Author     : Braulio Sorcia
--%>

<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
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
    String genero = "" + sessionOk.getAttribute("Genero");
    if (comminglock == null) {
        comminglock = "";
    }
    if (usuario.equals("null") || !rol.equalsIgnoreCase("Admin")) {

        session.invalidate();
        String Login = "../Generales/LogIn.jsp";
        response.sendRedirect(Login);
    }
%>
<!DOCTYPE html>
<html class="no-focus" lang="es">
    <head>
        <meta charset="utf-8">

        <title>Digi-SEP - Firmantes</title>

        <meta name="description" content="Digi-SEP - sistema de titulos, cédulas y certificados electrónicos">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <!-- Icons -->
        <!-- The following icons can be replaced with your own, they are used by desktop and mobile browsers -->
        <link rel="shortcut icon" href="../assets/img/favicons/favicon.png">

        <link rel="icon" type="image/png" href="../assets/img/favicons/favicon-16x16.png" sizes="16x16">
        <link rel="icon" type="image/png" href="../assets/img/favicons/favicon-32x32.png" sizes="32x32">
        <link rel="icon" type="image/png" href="../assets/img/favicons/favicon-96x96.png" sizes="96x96">
        <link rel="icon" type="image/png" href="../assets/img/favicons/favicon-160x160.png" sizes="160x160">
        <link rel="icon" type="image/png" href="../assets/img/favicons/favicon-192x192.png" sizes="192x192">

        <link rel="apple-touch-icon" sizes="57x57" href="../assets/img/favicons/apple-touch-icon-57x57.png">
        <link rel="apple-touch-icon" sizes="60x60" href="../assets/img/favicons/apple-touch-icon-60x60.png">
        <link rel="apple-touch-icon" sizes="72x72" href="../assets/img/favicons/apple-touch-icon-72x72.png">
        <link rel="apple-touch-icon" sizes="76x76" href="../assets/img/favicons/apple-touch-icon-76x76.png">
        <link rel="apple-touch-icon" sizes="114x114" href="../assets/img/favicons/apple-touch-icon-114x114.png">
        <link rel="apple-touch-icon" sizes="120x120" href="../assets/img/favicons/apple-touch-icon-120x120.png">
        <link rel="apple-touch-icon" sizes="144x144" href="../assets/img/favicons/apple-touch-icon-144x144.png">
        <link rel="apple-touch-icon" sizes="152x152" href="../assets/img/favicons/apple-touch-icon-152x152.png">
        <link rel="apple-touch-icon" sizes="180x180" href="../assets/img/favicons/apple-touch-icon-180x180.png">
        <!-- END Icons -->
        <!-- Stylesheets -->
        <!-- Web fonts -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400italic,600,700%7COpen+Sans:300,400,400italic,600,700">

        <!-- Bootstrap and OneUI CSS framework -->
        <link rel="stylesheet" href="../assets/css/bootstrap.min.css">
        <link rel="stylesheet" id="css-main" href="../assets/css/oneui.css">

        <!-- You can include a specific file from css/themes/ folder to alter the default color theme of the template. eg: -->
        <link rel="stylesheet" id="css-theme" href="../assets/css/themes/modern.min.css">
        <!-- END Stylesheets --> 
        <link rel="stylesheet" href="../assets/js/plugins/sweetalert/sweetalert2.css">
        <link rel="stylesheet" href="../assets/js/plugins/datatables/jquery.dataTables.min.css">
        <link rel="stylesheet" href="../assets/css/LoadersGeneral.css">

        <!-- END Stylesheets -->

        <!-- Page JS Plugins -->
        <script src="../assets/js/core/jquery.min.js"></script>
        <script src="../assets/js/plugins/jquery-validation/jquery.validate.min.js"></script>
        <script src="../assets/js/plugins/jquery-validation/additional-methods.js"></script>
        <script src="../assets/js/pages/base_forms_validation.js"></script>
        <script src="../assets/js/core/bootstrap.min.js"></script>
        <script src="../assets/js/core/jquery.slimscroll.min.js"></script>
        <script src="../assets/js/core/jquery.scrollLock.min.js"></script>
        <script src="../assets/js/core/jquery.appear.min.js"></script>
        <script src="../assets/js/core/jquery.placeholder.min.js"></script>
        <script src="../assets/js/plugins/chosen/chosen.jquery.js"></script>
        <script src="../assets/js/core/js.cookie.min.js"></script>
        <script src="../assets/js/app.js"></script>

        <!-- Page JS Code -->
        <script src="../assets/js/plugins/jquery-vide/jquery.vide.min.js"></script>
        <style>
            th, td {
                width: 17%;
                border: 1px solid black;
                max-width:100px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                margin:0 5px 0 5px; padding:0;
            }
            table {
                width: 100%;
            }
        </style>
    </head>
    <body style="overflow: hidden;">

        <div class="loader loader-double is-active" id='mainLoader'></div>

        <div class="loaderExt" id="loadAction" style="display: none;">
            <div class="loaderExt-content">
                <div class="sk-circle">
                    <div class="sk-circle1 sk-child"></div>
                    <div class="sk-circle2 sk-child"></div>
                    <div class="sk-circle3 sk-child"></div>
                    <div class="sk-circle4 sk-child"></div>
                    <div class="sk-circle5 sk-child"></div>
                    <div class="sk-circle6 sk-child"></div>
                    <div class="sk-circle7 sk-child"></div>
                    <div class="sk-circle8 sk-child"></div>
                    <div class="sk-circle9 sk-child"></div>
                    <div class="sk-circle10 sk-child"></div>
                    <div class="sk-circle11 sk-child"></div>
                    <div class="sk-circle12 sk-child"></div>
                </div>
            </div>
        </div>
        <div id="page-container" class="sidebar-l sidebar-o side-scroll header-navbar-fixed">
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
                <div class="content">
                    <div class="bg-image remove-margin-b push-20-t" style="background-image: url(&quot;../assets/img/flechas.png&quot;);">
                        <div class="row push-20 bg-primary-op remove-margin" style="padding-left: 15px; padding-right: 15px;">
                            <div class="col-lg-3 col-sm-3 col-md-3 block-content  hidden-xs" style="min-height: 115px;">
                                <div class="pull-left push push-10-l hidden-sm hidden-md">
                                    <%
                                        if (genero.equalsIgnoreCase("250")) {
                                    %>
                                    <img class="img-avatar img-avatar-thumb" src="../assets/img/avatars/Mujer.png" alt="logo">
                                    <%
                                        } else if (genero.equalsIgnoreCase("251")) {
                                            out.print("<img class='img-avatar img-avatar-thumb' src='../assets/img/avatars/Hombre.png' alt='logo'>");
                                        } else {
                                            out.print("<img class='img-avatar img-avatar-thumb' src='../assets/img/avatars/avatar10.jpg' alt='logo'>");
                                        }
                                    %>
                                </div>
                                <div class="pull-left push-15-t push-15-l">
                                    <h3 class="h5 text-white"><em>Hola</em></h3>
                                    <h2 class="h4 font-w600 text-white push-5"><%=NombreC%></h2>
                                </div>
                            </div>
                            <div class="col-lg-6 col-xs-12 col-md-6 block-content  text-center hidden-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white push-10-l">
                                    Firmantes
                                </h1>
                                <h3 class="h5 text-white visible-xs text-left"><em>Hola</em></h3>
                                <h2 class="h4 font-w600 text-white push-5 visible-xs text-left"><%=NombreC%></h2>
                            </div>
                            <div class="col-sm-6 block-content  text-center visible-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white">
                                    Firmantes
                                </h1>
                            </div>
                            <div class="col-lg-3 col-sm-3 col-md-3 block-content  hidden-xs" style="min-height: 115px;">

                            </div>
                        </div>

                    </div>
                    <!-- Overview -->
                    <div id="fullContent" class="push-20-t">

                        <div class="block">
                            <div class="block-header">
                                <ul class="block-options">
                                    <li id="liBtnNuevo">
                                        <button id="btnNuevoFirmante" type="button" class="btn  text-success"><i class="fa fa-arrow-right"></i> Nuevo</button>
                                    </li>
                                </ul>
                            </div>
                            <div class="block-content">
                                <div id="divTblFirmantes">
                                    <table class="table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed" style="width:100%;"  id="tblFirmantes">
                                        <thead class="bg-primary-dark" style="color: white;">
                                            <tr>
                                                <th class="text-center" style="display:none;">IdFirmante</th>
                                                <th class="text-center">Nombre(s)</th>
                                                <th class="text-center">A. Paterno</th>
                                                <th class="text-center hidden-sm hidden-xs">Cargo</th>
                                                <th class="text-center hidden-sm hidden-xs" style="width: 10%">Abrev. Titulo</th>
                                                <th class="text-center hidden-sm hidden-xs">No. Certificado Responsable</th>
                                                <th class="text-center" style="width: 10%">Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody id="tblBodyFirmantes">
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="noPermisson col-xs-12" style="display: none;">
                        <div class="alert alert-warning alert-dismissable">
                            <h3 class="font-w300 push-15">¡Lo sentimos!</h3>
                            <p>No cuentas con los permisos suficentes <a class="alert-link" href="javascript:void(0)"><strong>¡Contacta a tu administrador!</strong></a></p>
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


        <!-- Inicio Modal Firmantes -->
        <div class="modal fade" id="modal-firmantes" tabindex="-1" role="dialog" aria-hidden="true" style="overflow-x: hidden; overflow-y: auto;">
            <div class="modal-dialog modal-dialog modal-lg modal-dialog-popin mymodal nomini">
                <div class="modal-content" id="modal-Firmante-draggable">
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button onclick="$('#modal-firmantes').modal('hide')" id="btnCerrarModal" type="button"><i class="si si-close resetForm"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitle"></h3>
                        </div>
                        <form class=" form-horizontal push-10-t push-10" id="formFirmantes" name="formFirmantes">
                            <input style="display: none;" id="bandera" name="bandera" value=""/>
                            <input style="display: none;" id="noCertificado" name="noCertificado" value=""/>
                            <input style="display: none;" id="fechaValidez" name="fechaValidez" value=""/>
                            <input style="display: none;" id="fechaExpira" name="fechaExpira" value=""/>
                            <input style="display: none;" id="idFirmante" name="idFirmante" value=""/>
                            <div class="block">
                                <div class="block-content">
                                    <div class="form-group" style="padding-top: 10px;">
                                        <div class="col-xs-12 col-lg-4 col-md-4 col-sm-6"  id="divNombreFirmante">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divNombreFirmanteIn">
                                                <input class="form-control noPaste js-maxlength notAllowed" type="text" id="txtNombreFirmante" name="txtNombreFirmante" maxlength="50">
                                                <label for="txtNombreFirmante"><span class="text-danger" >&#9642;</span> Nombre(s)</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-wpforms"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-4 col-md-4 col-sm-6"  id="divApaternoFirmante">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divApaternoFirmanteIn">
                                                <input class="form-control noPaste js-maxlength notAllowed" type="text" id="txtApaternoFirmante" name="txtApaternoFirmante" maxlength="50">
                                                <label for="txtApaternoFirmante"><span class="text-danger" >&#9642;</span> Apellido Paterno</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-wpforms"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-4 col-md-4 col-sm-12"  id="divAmaternoFirmante">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divAmaternoFirmanteIn">
                                                <input class="form-control noPaste js-maxlength notAllowed" type="text" id="txtAmaternoFirmante" name="txtAmaternoFirmante" maxlength="50">
                                                <label for="txtAmaternoFirmante">Apellido Materno</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-wpforms"></i></span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group">    
                                        <div class="col-xs-12 col-lg-4 col-md-4 col-sm-4" id="divCurpFirmante">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divCurpFirmanteIn">
                                                <input class="form-control js-maxlength noPaste JustLetters notAllowed" type="text" id="txtCurpFirmante" name="txtCurpFirmante"  maxlength="50">
                                                <label for="txtCurpFirmante"><span class="text-danger" >&#9642;</span> CURP</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-4 col-md-4 col-sm-4" id="divCargoFirmante">
                                            <div class="form-material form-material-primary " id="divCargoFirmanteIn">
                                                <label for="lstCargoFirmante"><span class="text-danger" >&#9642;</span> Cargo</label>
                                                <select class="form-control" id="lstCargoFirmante" style="width: 100%;" name="lstCargoFirmante">
                                                </select>

                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-4 col-md-4 col-sm-4" id="divAbrTitulo">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divAbrTituloIn">
                                                <input class="form-control js-maxlength noPaste JustLetters notAllowed" type="text" id="txtAbrevTitulo" name="txtAbrevTitulo"  maxlength="50">
                                                <label for="txtAbrevTitulo"><span class="text-danger" >&#9642;</span> Abrev. Titulo</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>
                                            </div>
                                        </div>  
                                    </div>
                                    <div class="form-group">    
                                        <div class="col-xs-12 col-lg-6 col-md-6" id="divLlaveFirmante">
                                            <label for="fileLlaveFirmante"><span class="text-danger" >&#9642;</span> Carga de Llave</label>
                                            <div class="form-material input-group form-material-primary floating " style="width: 100%;">
                                                <input style="display: none;" id="txtLlaveFirmUpd" name="txtLlaveFirmUpd" value=""/>
                                                <input class="form-control" type="file" id="fileLlaveFirmante" name="fileLlaveFirmante" title="Llave">
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-paperclip"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-6 col-md-6" id="divCertificadoFirmante">
                                            <label for="fileCertificadoFirmante"><span class="text-danger" >&#9642;</span> Carga de Certificado</label>
                                            <div class="form-material input-group form-material-primary floating " style="width: 100%;">
                                                <input style="display: none;" id="txtCertificadoUpd" name="txtCertificadoUpd" value=""/>
                                                <input class="form-control" type="file" id="fileCertificadoFirmante" name="fileCertificadoFirmante" title="Certificado">
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-paperclip"></i></span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-xs-12 col-lg-4 col-md-4" id="divNumCertificadoFirm">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divNumCertificadoFirmIn">
                                                <input class="form-control js-maxlength noPaste JustLetters" type="text" id="txtNumCertificado" name="txtNumCertificado"  maxlength="50" disabled="disabled">
                                                <label for="txtNumCertificado"><span class="text-danger" >&#9642;</span> Número de Certificado</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-4 col-md-4" id="divFechaValidezFirm">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divFechaValidezFirmIn">
                                                <input class="form-control js-maxlength noPaste JustLetters" type="text" id="txtFechaValidez" name="txtFechaValidez"  maxlength="50" disabled="disabled">
                                                <label for="txtFechaValidez"><span class="text-danger" >&#9642;</span> Fecha inicio validez</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-calendar-o"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-4 col-md-4" id="divFechaExpirFirm">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divFechaExpirFirmIn">
                                                <input class="form-control js-maxlength noPaste JustLetters" type="text" id="txtFechaExpira" name="txtFechaExpira"  maxlength="50" disabled="disabled">
                                                <label for="txtFechaExpira"><span class="text-danger" >&#9642;</span> Fecha de expiración</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-calendar-o"></i></span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-xs-12 col-lg-6 col-md-6" id="divPasswordFirmante">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divPasswordFirmanteIn">
                                                <input class="form-control js-maxlength noPaste JustLetters" type="password" id="txtPasswordFirmante" name="txtPasswordFirmante"  maxlength="50">
                                                <label for="txtPasswordFirmante"><span class="text-danger" >&#9642;</span> Contraseña</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-user-secret"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-xs-12 col-lg-6 col-md-6" id="divConfirmPasswordFirmante">
                                            <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divConfirmPasswordFirmanteIn">
                                                <input class="form-control js-maxlength noPaste JustLetters" type="password" id="txtConfirmPassFirm" name="txtConfirmPassFirm"  maxlength="50">
                                                <label for="txtConfirmPassFirm"><span class="text-danger" >&#9642;</span> Confirmar Contraseña</label>
                                                <span class="input-group-addon hidden-xs"><i class="fa fa-user-secret"></i></span>
                                            </div>
                                        </div>  
                                    </div>
                                    <p class="text-muted font-s12 center-block">
                                        Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>
                                </div>
                            </div>

                            <div class="block-content" style="overflow-x:inherit;">
                                <div class="form-horizontal">
                                    <div class="form-group">
                                        <div class="col-xs-6">
                                        </div>
                                        <div class="col-xs-12">
                                            <button class="btn btn-sm btn-success pull-right btnAccionesFirmantes" type="submit" id="btnRegistrarFirmante" name="btnRegistraAlumno" onclick="$('#bandera').val('3');"><i class="si si-plus push-5-r"></i>Añadir</button>
                                            <button class="btn btn-sm btn-success pull-right btnAccionesFirmantes" type="submit" id="btnUpdateFirmante" onclick="$('#bandera').val('4');" name="btnUpdateAlumno"><i class="fa fa-save push-5-r"></i>Guardar</button>
                                            <button class="btn btn-sm btn-success pull-right resetForm" style="margin-right: 10px;" type="button" id="btnAcceptAlumno" name="btnAcceptAlumno" onclick="$('#modal-firmantes').modal('hide');"><i class="si si-close push-5-r"></i>Cerrar</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <!-- Final Modal Firmantes -->

        <!-- MODAL CAMBIO DE CONTRASEÑA -->
        <%
            if (!usuario.equals("null")) {
        %>
        <jsp:include page="../Generales/cambiarContrasenia.jsp">
            <jsp:param name="allow" value="si" /> 
        </jsp:include>
        <%}%>

        <!--========================== JS SECTION=========================================-->
        <script src="../assets/js/plugins/bootstrap-notify/bootstrap-notify.min.js"></script>
        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/datatables/jquery.dataTables.min.js"></script>
        <script src="../assets/js/pages/base_tables_datatables.js"></script>        
        <script src="../assets/js/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js"></script>
        <script src="../assets/js/plugins/sessionTimeOut/bootstrap-session-timeout.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacioneTEFirmantes.js?v=1.1"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>
        <script>
                                                jQuery(function () {
                                                    // Init page helpers (BS Datepicker + BS Datetimepicker + BS Colorpicker + BS Maxlength + Select2 + Masked Input + Range Sliders + Tags Inputs plugins)
                                                    App.initHelpers(['maxlength', 'notify']);
                                                });
        </script>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
        <input id="txtModulo" name="txtModulo" value="Firmantes" style="display: none" disabled="disabled">
    </body>
</html>
