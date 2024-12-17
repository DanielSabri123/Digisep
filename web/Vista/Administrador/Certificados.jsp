<%-- 
    Document   : Certificados
    Created on : 18/12/2018, 09:32:51 AM
    Author     : Paola Alonso
	Version    : DIGISEP GENERAL
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
    String permisoCargaMasiva = (sessionOk.getAttribute("IMPORTACION_MASIVA_CERTIFICADO_ELECTRONICO") == null ? "0" : sessionOk.getAttribute("IMPORTACION_MASIVA_CERTIFICADO_ELECTRONICO").toString());
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

        <title>Digi-SEP - Certificados</title>

        <meta name="description" content="Digi-SEP - sistema de titulos, cédulas y certificados electrónicos">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">


        <!-- Page JS Plugins -->
        <link rel="stylesheet" href="../assets/js/plugins/bootstrap-datepicker/bootstrap-datepicker3.min.css">
        <link rel="stylesheet" href="../assets/js/plugins/sweetalert/sweetalert2.css">
        <link rel="stylesheet" href="../assets/js/plugins/datatables/jquery.dataTables.min.css">
        <link rel="stylesheet" href="../assets/css/LoadersGeneral.css">

        <script src="../assets/js/core/jquery.min.js"></script>
        <script src="../assets/js/plugins/jquery-validation/jquery.validate.min.js"></script>
        <script src="../assets/js/plugins/jquery-validation/additional-methods.js"></script>
        <script src="../assets/js/plugins/chosen/chosen.jquery.js"></script>
        <script src="../assets/js/pages/base_forms_validation.js"></script>
        <script src="../assets/js/core/bootstrap.min.js"></script>
        <script src="../assets/js/core/jquery.slimscroll.min.js"></script>
        <script src="../assets/js/core/jquery.scrollLock.min.js"></script>
        <script src="../assets/js/core/jquery.appear.min.js"></script>
        <script src="../assets/js/core/jquery.placeholder.min.js"></script>
        <script src="../assets/js/core/js.cookie.min.js"></script>
        <script src="../assets/js/app.js"></script>

        <!-- Stylesheets -->
        <!-- Web fonts -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400italic,600,700%7COpen+Sans:300,400,400italic,600,700">

        <!-- Bootstrap and OneUI CSS framework -->
        <link rel="stylesheet" href="../assets/css/bootstrap.min.css">
        <link rel="stylesheet" id="css-main" href="../assets/css/oneui.css">

        <!-- You can include a specific file from css/themes/ folder to alter the default color theme of the template. eg: -->
        <link rel="stylesheet" id="css-theme" href="../assets/css/themes/modern.min.css">
        <!-- END Stylesheets -->
        <style>
            th, td {
                width: 11.12%;
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
    <body style="overflow: hidden; zoom: 90%">

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
                                    Certificados Electrónicos
                                </h1>
                                <h3 class="h5 text-white visible-xs text-left"><em>Hola</em></h3>
                                <h2 class="h4 font-w600 text-white push-5 visible-xs text-left"><%=NombreC%></h2>
                            </div>
                            <div class="col-sm-6 block-content  text-center visible-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white">
                                    Certificados Elecrónicos
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
                                        <button id="btnNuevoCertificado" data-toggle="modal" data-target="#modal-certificadosElectronicos" type="button" class="btn  text-success"><i class="fa fa-arrow-right"></i> Nuevo</button>
                                    </li>
                                    <li id="liBtnDescargarTodos">
                                        <button id="btnDescargarXmlMasivo" type="button" class="btn text-success" disabled="disabled">
                                            <i class="fa fa-cloud-download"></i> Generar archivo zip
                                        </button>
                                    </li>
                                    <li id="liBtnGenXmlMasivo">
                                        <button id="btnGeneXmlMasivo" type="button" class="btn text-success" disabled="disabled">
                                            <i class="fa fa-file-code-o"></i> Generar archivos xml
                                        </button>
                                    </li>
                                    <li>
                                        <button class="btn text-success" id="btnDescargarZip" style="display: none; color: #999999"><i class="fa fa-file-zip-o"></i></button>
                                    </li>
                                </ul>
                                <div style="display:none;">
                                    <form class="form-horizontal" id="form-importar-certificados-masivos" name="form-importar-certificados-masivos">
                                        <input style="display: none;" type="text" id="txtBandera2" name="txtBandera" value="">
                                        <div class="form-group">
                                            <input style="display: none;" class="form-control" type="file" id="fileCertificados" name="fileCertificados" title="Cargar Certificados">
                                        </div>
                                        <div class="form-group">
                                            <button class="btnAccionesCertificados" style="display:none;" type="submit" id="importarArchivo" name="importarArchivo" onclick="$('#txtBandera2').val('excel');"></button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                            <div class="block-content" style="margin-top: -15px;">
                                <div class="row">
                                    <div class="col-lg-5 col-xs-12" id="DivLstCarreras">
                                        <div class="input-group">
                                            <div class="form-material form-material-primary" id="DivLstCarrerasIn">
                                                <select class="form-control" id="lstCarreras" name="lstCarreras">
                                                </select>
                                                <label for="lstCarreras"> Carrera</label>
                                            </div>
                                            <span class="input-group-btn">
                                                <button class="btn btn-primary btn-sm push-5-t" type="button" id="btnFiltrarCertificados"><i class="fa fa-search"></i></button>
                                            </span>
                                        </div>
                                    </div>
                                    <%
                                        if (permisoCargaMasiva.equalsIgnoreCase("1")) {
                                    %>
                                    <div class="col-lg-5 col-xs-12 push-10-t pull-right">
                                        <div class="pull-right">
                                            <button class="btn btn-primary push-5-r btn-sm btn-rounded" id="btnDescargarFormato"><i class="si si-cloud-download"></i> <a class='text-white' download href="../../Importaciones/ImportarCertificadosElectronicos.xlsx"> Descargar formato</a></button>
                                            <button class="btn btn-primary btn-sm btn-rounded" id="btnImportarMasivo"><i class="fa fa-file-excel-o"></i> Importar registros</button>
                                        </div>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <div class="col-lg-12 col-xs-12 push-10-t pull-right">
                                        <div class="pull-right">
                                            <p style="margin: 0;">Certificados seleccionados : <strong id="contador-seleccionados">0</strong></p>
                                        </div>
                                    </div>
                                    
                                </div>
                                <div id="DivTblCertificados" style="margin-top:20px;">
                                    <table class="table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed" style="width:100%;"  id="tblCertificados">
                                        <thead class="bg-primary-dark" style="color: white;">
                                            <tr>
                                                <th class="text-center" style="display:none;">ID_Certificado</th>
                                                <th class="text-center">Folio</th>
                                                <th class="text-center">Matrícula</th>
                                                <th class="text-center">Nombre(s)</th>
                                                <th class="text-center">A. Paterno</th>
                                                <th class="text-center">Carrera</th>
                                                <th class="text-center">F. Expedición</th>
                                                <th class="text-center">Lugar Expedición</th>
                                                <th class="text-center">Acciones</th>
                                                <th class=' text-center bg-primary-dark' style='cursor: pointer; vertical-align:middle;'></th>
                                            </tr>
                                        </thead>
                                        <tbody id="tblBodyCarreras">
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
        <div class="modal fade" id="modal-certificadosElectronicos" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-dialog-popin mymodal nomini modal-md">
                <div class="modal-content" id="modal-claveCarrera-draggable" >
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button data-dismiss="modal" type="button" id="btnCerrarCveCarrera"><i class="si si-close"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitle">NUEVO CERTIFICADO</h3>
                        </div>
                        <div class="block-content form-group" style="height: auto; overflow:inherit;">
                            <form class="form-horizontal"  id="FormCertificadosElectronicos" name="FormCertificadosElectronicos"> 
                                <input style="display: none;" id="txtBandera" name="txtBandera" value=""/>
                                <input style="display: none;" id="idCertificado" name="idCertificado" value=""/>
                                <input style="display: none;" id="idAlumno" name="idAlumno" value=""/>
                                <input style="display: none;" id="idPersona" name="idPersona" value=""/>
                                <input style="display: none;" id="numDecimales" name="numDecimales" value=""/>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="input-group">
                                            <div class="form-material form-material-primary floating" id="DivTxtMatricula">
                                                <input class="form-control js-maxlength" type="text" id="txtMatricula" name="txtMatricula" maxlength="20" autoComplete="off" onkeypress="return noBlankSpaces()">
                                                <label for="txtMatricula"><span class="text-danger ">▪</span> Matrícula</label>
                                            </div>
                                            <span class="input-group-btn">
                                                <button class="btn btn-primary btn-sm" type="button" id="btnBuscarAlumno"><i class="fa fa-search"></i></button>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-6 col-md-3" id="DivLstCarreraCertificado">
                                        <div class="form-material form-material-primary" id="DivLstCarreraCertificadoIn">
                                            <select class="form-control" id="lstCarreraCertificado" name="lstCarreraCertificado">
                                            </select>
                                            <label for="lstCarreraCertificado"><span class="text-danger ">▪</span> Carrera</label>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" id="DivTxtNombreAlumno">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtNombreAlumnoIn">
                                            <input class="form-control js-maxlength" type="text" id="txtNombreAlumno" name="txtNombreAlumno" maxlength="500" autoComplete="off">
                                            <label for="txtNombreAlumno"><span class="text-danger ">▪</span> Nombre del Alumno</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFolioControl">
                                            <input class="form-control js-maxlength" type="text" id="txtFolioControl" name="txtFolioControl" maxlength="40" autoComplete="off" disabled>
                                            <label for="txtFolioControl"  data-toggle="popover" title="Folio de Control" data-placement="right" data-content="El folio de control es generado automáticamente por el sistema." data-original-title="Right Popover"> Folio de Control</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-8 col-md-8 col-sm-6 col-xs-12" id="DivLstFirmantesCertificados">
                                        <div class="form-material form-material-primary" id="DivLstFirmantesCertificadosIn">
                                            <select class="form-control" id="lstFirmantesCertificados" name="lstFirmantesCertificados">
                                            </select>
                                            <label for="lstFirmantesCertificados"><span class="text-danger ">▪</span> Firmante</label>
                                        </div>
                                    </div>

                                </div>
                                <div class="form-group">
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaExp">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaExpedicion" name="txtFechaExpedicion" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaExpedicion"><span class="text-danger ">▪</span> F.Expedición <i style="cursor: pointer" class="fa fa-question-circle" 
                                                                                                                                data-toggle="popover" title="" 
                                                                                                                                data-placement="right" 
                                                                                                                                data-content="Se recomienda expedir los certificados con fecha de 2 días antes de ser cargados al ambiente de validación SEP (MEC)." 
                                                                                                                                data-original-title="Recomendación"></i></label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-xs-12 col-sm-12 col-md-6" id="DivLstLugarExpedicion">
                                        <div class="form-material form-material-primary" id="DivLstLugarExpedicionIn">
                                            <select class="form-control" id="lstLugarExpedicion" name="lstLugarExpedicion">
                                            </select>
                                            <label for="lstLugarExpedicion"><span class="text-danger ">▪</span> Lugar de Expedición</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group" style="margin-top: -15px;">
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-6">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtTotalFolios">
                                            <input class="form-control js-maxlength" type="text" disabled id="txtTotalMaterias" name="txtTotalMaterias" maxlength="40" autoComplete="off">
                                            <label for="txtTotalMaterias"><span class="text-danger ">▪</span> Total Materias</label>
                                            <span class="input-group-addon "><i class="fa fa-hashtag"></i></span>
                                                <%--<span class="input-group-btn pull-t">
                                                    <button class="btn btn-primary btn-sm" type="button" id="btnEditarTotal" style="margin-top: -8px;">
                                                        <i class="fa fa-pencil"></i>
                                                    </button>
                                                </span>--%>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-6">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtMateriasAsignadas">
                                            <input class="form-control js-maxlength" type="text" disabled id="txtMateriasAsignadas" name="txtMateriasAsignadas" maxlength="40" autoComplete="off">
                                            <label for="txtMateriasAsignadas"><span class="text-danger ">▪</span> Asignadas</label>
                                            <span class="input-group-addon "><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-6">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtMateriasPasadas">
                                            <input class="form-control js-maxlength" type="text" id="txtMateriasPasadas" name="txtMateriasPasadas" maxlength="40" autoComplete="off" disabled>
                                            <label for="txtMateriasPasadas"><span class="text-danger ">▪</span> Aprobadas</label>
                                            <span class="input-group-addon "><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-6">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtPromedio">
                                            <input class="form-control js-maxlength" type="text" id="txtPromedio" name="txtPromedio" maxlength="40" autoComplete="off" disabled>
                                            <label for="txtPromedio"><span class="text-danger ">▪</span> Promedio</label>
                                            <span class="input-group-addon "><i class="fa fa-file-text-o"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtTotalCreditosIn">
                                            <input class="form-control" type="text" id="txtTotalCreditos" name="txtTotalCreditos" disabled>
                                            <label for="txtTotalCreditos"><span class="text-danger">▪</span> Total de créditos</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>                                            
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtCreditosObtenidosIn">
                                            <input class="form-control" type="text" id="txtCreditosObtenidos" name="txtCreditosObtenidos" disabled>
                                            <label for="txtCreditosObtenidos"><span class="text-danger">▪</span> Créditos obtenidos</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>                                            
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="div-txt-total-ciclos">
                                            <input class="form-control" type="text" id="txt-numero-ciclos" name="txt-numero-ciclos" disabled>
                                            <label for="txt-numero-ciclos"><span class="text-danger">▪</span> Número de ciclos</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>                                            
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary" id="DivLstTipoCertificadoIn">
                                            <select class="form-control" id="lstTipoCertificado" name="lstTipoCertificado" disabled>
                                                <option></option>
                                                <option value="79">Total</option>
                                                <option value="80">Parcial</option>
                                            </select>
                                            <label for="lstTipoCertificado"><span class="text-danger ">▪</span> Tipo de Certificado</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary" id="DivLstEstatusCertificadoIn" style="display: none;">
                                            <select class="form-control" id="lstEstatusCertificado" name="lstEstatusCertificado" disabled>
                                                <option></option>
                                                <option value="1">Activo</option>
                                                <option value="0">Inactivo</option>
                                            </select>
                                            <label for="lstEstatusCertificado"><span class="text-danger ">▪</span> Estatus</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12 " id="divTxtACadenaOriginal">
                                        <div class="form-material form-material-primary">
                                            <textarea class="form-control" id="txtACadenaOriginal" name="txtACadenaOriginal" rows="6" disabled="disabled"></textarea>
                                            <label for="txtACadenaOriginal">Cadena Original 
                                                <button type="button" class="btn btn-default btn-xs" data-toggle="tooltip" data-container="body" 
                                                        data-original-title="¡Copiar al portapapeles!" data-placement="right" id="btnCopiarCadena">
                                                    <i class="fa fa-copy"></i>
                                                </button>
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <p class="text-muted font-s12 center-block">
                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <button id="ButtonUpdateCertificado"  class="btn btn-sm btn-success pull-right  BtnAccionesCertificados" onclick="$('#txtBandera').val('3');" type="submit"><i class="fa fa-save push-5-r"></i>Guardar</button>
                                        <button id="ButtonAddCertificado" class="btn btn-sm btn-success pull-right  BtnAccionesCertificados" onclick="$('#txtBandera').val('2');" type="submit"><i class="si si-plus push-5-r"></i> Añadir</button>
                                        <button id="ButtonCerrarCertificado" class="btn btn-sm btn-success pull-right " type="button" onclick="$('#modal-certificadosElectronicos').modal('hide')"><i class="si si-close push-5-r"></i> Cerrar</button>
                                        <button id="ButtonGenerateXML" class="btn btn-sm btn-success pull-right push-5-r" type="button" disabled><i class="si si-plus push-5-r"></i> Generar XML</button>
                                    </div>
                                </div>     
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <!-- MODAL CAMBIO DE CONTRASEÑA -->
        <%
            if (!usuario.equals("null")) {
        %>
        <jsp:include page="../Generales/cambiarContrasenia.jsp">
            <jsp:param name="allow" value="si" /> 
        </jsp:include>
        <%}%>
        <input type="hidden" id="txtEdition" name="txtEdition" value="0">
        <script src="../assets/js/plugins/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
        <script src="../assets/js/plugins/bootstrap-notify/bootstrap-notify.min.js"></script>
        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/datatables/jquery.dataTables.min.js"></script>
        <script src="//cdn.datatables.net/plug-ins/1.10.11/sorting/date-eu.js" type="text/javascript"></script>
        <script src="../assets/js/pages/base_tables_datatables.js"></script>        
        <script src="../assets/js/pages/base_ui_activity.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js"></script>
        <script src="../assets/js/plugins/masked-inputs/jquery.maskedinput.min.js"></script>
        <script src="../assets/js/plugins/jquery-ui/jquery-ui.min.js"></script>
        <script src="../assets/js/plugins/sessionTimeOut/bootstrap-session-timeout.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesTECertificados.js?v=3.4"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>

        <script>
                                            jQuery(function () {
                                                // Init page helpers (BS Datepicker + BS Datetimepicker + BS Colorpicker + BS Maxlength + Select2 + Masked Input + Range Sliders + Tags Inputs plugins)
                                                App.initHelpers(['maxlength', 'datepicker', 'masked-inputs', 'notify']);
                                            });
        </script>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
        <input id="txtModulo" name="txtModulo" value="Certificados" style="display: none" disabled="disabled">
    </body>
</html>