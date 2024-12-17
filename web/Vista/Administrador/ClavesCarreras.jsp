<%-- 
    Document   : ClavesCarreras
    Created on : 14/12/2018, 03:03:22 PM
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
        <script src="../assets/js/core/jquery.min.js"></script>

        <meta charset="utf-8">

        <title>Digi-SEP - Claves Carreras</title>

        <meta name="description" content="Digi-SEP - sistema de titulos, cédulas y certificados electrónicos">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <!-- Icons -->
        <!-- The following icons can be replaced with your own, they are used by desktop and mobile browsers -->
        <!-- END Icons -->

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
        <script src="../assets/js/core/jquery.countTo.min.js"></script>
        <script src="../assets/js/core/jquery.placeholder.min.js"></script>
        <script src="../assets/js/core/js.cookie.min.js"></script>
        <script src="../assets/js/app.js"></script>

        <!-- Page JS Code -->
        <script src="../assets/js/plugins/jquery-vide/jquery.vide.min.js"></script>

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
                /**width: 20%;**/
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
                                    Claves Carrera
                                </h1>
                                <h3 class="h5 text-white visible-xs text-left"><em>Hola</em></h3>
                                <h2 class="h4 font-w600 text-white push-5 visible-xs text-left"><%=NombreC%></h2>
                            </div>
                            <div class="col-sm-6 block-content  text-center visible-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white">
                                    Claves Carrera
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
                                    <li id="liBtnNuevo" style="display: none;">
                                        <button id="btnNuevaCarrera" data-toggle="modal" data-target="#modal-claveCarrera" type="button" class="btn  text-success"><i class="fa fa-arrow-right"></i> Nueva</button>
                                    </li>
                                    <li id="liBtnNomInstitucion">
                                        <button id="btnNomInstitucion" onclick="$('#bandera').val('6');" data-toggle="modal" data-target="#modal-NombreInstitucion" type="button" class="btn  text-success">
                                            <i class="si si-settings"></i> Configuración nombre institución
                                        </button>
                                    </li>
                                </ul>
                            </div>
                            <div class="block-content">
                                <div id="DivTblCarreras">
                                    <table class="table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed" style="width:100%;"  id="tblCarreras">
                                        <thead class="bg-primary-dark" style="color: white;">
                                            <tr>
                                                <th class="text-center" style="display:none;">IdCarrera</th>
                                                <th class="text-center">Clave</th>
                                                <th class="text-center">Nombre Carrera</th>
                                                <th class="text-center">Clave Institución</th>
                                                <th class="text-center">Nombre Institución</th>
                                                <th class="text-center">Acciones</th>
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
        <div class="modal fade" id="modal-claveCarrera" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-dialog-popin mymodal nomini modal-md">
                <div class="modal-content" id="modal-claveCarrera-draggable" >
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button data-dismiss="modal" type="button" id="btnCerrarCveCarrera"><i class="si si-close"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitle">NUEVA CLAVE CARRERA</h3>
                        </div>
                        <div class="block-content form-group" style="height: auto; overflow:inherit;">
                            <form class="form-horizontal"  id="FormClaveCarrera" name="FormClaveCarrera"> 
                                <input style="display: none;" id="txtBandera" name="txtBandera" value=""/>
                                <input style="display: none;" id="idCarrera" name="idCarrera" value=""/>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="divTxtIdCarreraExcel">
                                            <input class="form-control notAllowed" type="text" id="txtIdCarreraExcel" name="txtIdCarreraExcel" autoComplete="off">
                                            <label for="txtIdCarreraExcel"><span class="text-danger ">▪</span> Id Carrera Excel</label>
                                            <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtClaveCarrera">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtClaveCarrera" name="txtClaveCarrera" maxlength="7" minlength="5" autoComplete="off">
                                            <label for="txtClaveCarrera"><span class="text-danger ">▪</span> Clave Carrera</label>
                                            <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtNombreCarrera">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtNombreCarrera" name="txtNombreCarrera" maxlength="500" autoComplete="off">
                                            <label for="txtNombreCarrera"><span class="text-danger ">▪</span> Nombre de Carrera</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtClavePlanCarrera">
                                            <input class="form-control notAllowed" type="text" id="txtClavePlanCarrera" name="txtClavePlanCarrera" autoComplete="off">
                                            <label for="txtClavePlanCarrera"><span class="text-danger ">▪</span> Clave Plan Carrera</label>
                                            <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtClaveInstitucion">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtClaveInstitucion" name="txtClaveInstitucion" maxlength="7" minlength="6" autoComplete="off">
                                            <label for="txtClaveInstitucion"><span class="text-danger ">▪</span> Clave Institución</label>
                                            <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtNombreInstitucion">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtNombreInstitucion" name="txtNombreInstitucion" maxlength="500" autoComplete="off">
                                            <label for="txtNombreInstitucion"><span class="text-danger ">▪</span> Nombre de Institución</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtClaveCampus">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtClaveCampus" name="txtClaveCampus" maxlength="50" autoComplete="off">
                                            <label for="txtClaveCampus"><span class="text-danger ">▪</span> Clave Campus</label>
                                            <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtNombreCampus">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtNombreCampus" name="txtNombreCampus" maxlength="500" autoComplete="off">
                                            <label for="txtNombreCampus"><span class="text-danger ">▪</span> Nombre de Campus</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12 col-sm-6 col-lg-4 col-md-4 push-10-t" id="DivLstAutorizaciones">
                                        <div class="form-material form-material-primary" id="DivLstAutorizacionesIn">
                                            <select class="form-control" id="lstAutorizaciones" name="lstAutorizaciones">
                                            </select>
                                            <label for="lstAutorizaciones"><span class="text-danger ">▪</span> Autorización de Reconocimiento</label>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-6 col-md-5 col-lg-4 push-10-t">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtRvoe">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtRvoe" name="txtRvoe" maxlength="100" autoComplete="off">
                                            <label for="txtRvoe" > No. RVOE</label>
                                            <span class="input-group-addon"><i class="fa fa-key"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-4 col-md-3 col-sm-12 col-xs-12 push-10-t">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaExpedicionRvoeIn">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaExpedicionRvoe" name="txtFechaExpedicionRvoe" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaExpedicionRvoe"><span class="text-danger">▪</span> Expedición del Rvoe</label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12 col-sm-6 col-lg-4 col-md-4 push-10-t" id="DivLstTipoPeriodo">
                                        <div class="form-material form-material-primary" id="DivLstTipoPeriodoIn">
                                            <select class="form-control" id="lstTipoPeriodo" name="lstTipoPeriodo">
                                            </select>
                                            <label for="lstTipoPeriodo"><span class="text-danger ">▪</span> Tipo de Periodo</label>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-6 col-lg-4 col-md-4 push-10-t" id="DivLstNivelEducativo">
                                        <div class="form-material form-material-primary" id="DivLstNivelEducativoIn">
                                            <select class="form-control" id="lstNivelEducativo" name="lstNivelEducativo">
                                            </select>
                                            <label for="lstNivelEducativo"><span class="text-danger ">▪</span> Nivel Educativo</label>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-12 col-lg-4 col-md-4 push-10-t" id="DivLstEntidadFederativa">
                                        <div class="form-material form-material-primary" id="DivLstEntidadFederativaIn">
                                            <select class="form-control" id="lstEntidadFederativa" name="lstEntidadFederativa">
                                            </select>
                                            <label for="lstEntidadFederativa"><span class="text-danger ">▪</span> Entidad Federativa</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12 col-sm-6 col-md-4 col-lg-4 push-10-t">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtTotalMaterias">
                                            <input class="form-control js-maxlength" type="number" id="txtTotalMaterias" min="0" name="txtTotalMaterias" maxlength="4" autoComplete="off">
                                            <label for="txtTotalMaterias" ><span class="text-danger">▪</span> Total de Materias</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-6 col-md-4 col-lg-4 push-10-t">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtTotalCreditos">
                                            <input class="form-control js-maxlength" type="number" id="txtTotalCreditos" min="0" name="txtTotalCreditos" maxlength="7" autoComplete="off">
                                            <label for="txtTotalCreditos" ><span class="text-danger">▪</span> Total de Créditos</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-12 col-md-4 col-lg-4 push-10-t">
                                        <div class="form-material form-material-primary input-group floating" id="div-txt-num-decimales">
                                            <input class="form-control" type="number" id="txt-num-decimales" min="0" name="txt-num-decimales" autoComplete="off">
                                            <label for="txt-num-decimales" ><span class="text-danger">▪</span> Decimales en créditos <i style="cursor: pointer" class="fa fa-question-circle" 
                                                                                                                                      data-toggle="popover" title="" 
                                                                                                                                      data-placement="right"
                                                                                                                                      data-html="true"
                                                                                                                                      data-content="Establece el número de decimales a tomar en cuenta para los créditos en los certificados electrónicos.<p><small>El número por defecto es <b>2</b>.</small>" 
                                                                                                                                      data-original-title="Importante"></i></label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12 col-sm-12 col-lg-4 col-md-4 push-10-t" id="DivTxtCalifMin">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtCalifMinIn">
                                            <input class="form-control js-maxlength" type="number" id="txtCalifMin" min="0" maxlength="5" name="txtCalifMin" autoComplete="off">
                                            <label for="txtCalifMin" ><span class="text-danger">▪</span> Calificación Mínima</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-12 col-lg-4 col-md-4 push-10-t" id="DivTxtCalifMax">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtCalifMaxIn">
                                            <input class="form-control js-maxlength" type="number" id="txtCalifMax" min="0" maxlength="6" name="txtCalifMax" autoComplete="off">
                                            <label for="txtCalifMax" ><span class="text-danger">▪</span> Calificación Máxima</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-12 col-lg-4 col-md-4 push-10-t" id="DivTxtCalifMinAprob">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtCalifMinAprobIn">
                                            <input class="form-control js-maxlength" type="number" id="txtCalifMinAprob" min="0" maxlength="5" name="txtCalifMinAprob" autoComplete="off">
                                            <label for="txtCalifMinAprob" ><span class="text-danger">▪</span> Calificación Mínima Aprob.</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12 col-sm-6 col-lg-6 col-md-6 push-10-t" id="DivGradoObtenidoM">
                                        <div class="form-material form-material-primary input-group floating" id="DivGradoObtenidoMIn">
                                            <input class="form-control js-maxlength" type="text" id="txtGradoObtenidoM"  name="txtGradoObtenidoM" maxlength="100" autoComplete="off">
                                            <label for="txtGradoObtenidoM" ><span class="text-danger">▪</span> Grado obtenido al egresar (Masculino)</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 push-10-t" id="DivGradoObtenidoF">
                                        <div class="form-material form-material-primary input-group floating" id="DivGradoObtenidoFIn">
                                            <input class="form-control js-maxlength" type="text" id="txtGradoObtenidoF"  name="txtGradoObtenidoF" maxlength="100" autoComplete="off">
                                            <label for="txtGradoObtenidoF" ><span class="text-danger">▪</span> Grado obtenido al egresar (Femenino)</label>
                                            <span class="input-group-addon"><i class="fa fa-hashtag"></i></span>
                                        </div>
                                    </div>
                                </div>


                                <p class="text-muted font-s12 center-block">

                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <button id="ButtonUpdateCarrera"  class="btn btn-sm btn-success pull-right  BtnAccionesClaveCarrea" onclick="$('#txtBandera').val('3');" type="submit"><i class="fa fa-save push-5-r"></i>Guardar</button>
                                        <button id="ButtonAddCarrera" class="btn btn-sm btn-success pull-right  BtnAccionesClaveCarrea" onclick="$('#txtBandera').val('2');" type="submit"><i class="si si-plus push-5-r"></i> Añadir</button>
                                        <button id="ButtonCerrarCarrera" class="btn btn-sm btn-success pull-right " type="button" onclick="$('#modal-claveCarrera').modal('hide')"><i class="si si-close push-5-r"></i> Cerrar</button>
                                    </div>
                                </div>     
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <!-- MODAL CAMBIO DE CONTRASEÑA -->
        
        <!-- INICIO MODAL CONFIGURACIÓN NOMBRE DE INSTITUCIÓN -->
        <div class="modal fade" id="modal-NombreInstitucion" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-dialog-popin mymodal nomini modal-md">
                <div class="modal-content" id="modal-claveCarrera-draggable" >
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button data-dismiss="modal" type="button" id="btnCerrarNomInstitucion"><i class="si si-close"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitle">CONFIGURACIÓN NOMBRE INSTITUCIÓN</h3>
                        </div>
                        <div class="block-content form-group" style="height: auto; overflow:inherit;">
                            <form class="form-horizontal"  id="FormNombreIntitucion" name="FormNombreInstitucion"> 
                                <input style="display: none;" id="bandera" name="bandera" value=""/>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <p class="text-muted font-s24 center-block">Para la generación de certificados se toma como institución el nombre registrado en la configuración inicial.</p>
                                        <p class="text-muted font-s24 center-block">¿Quiere mantener esa configuración?.<span class="text-danger ">&#9642;</span></p>
                                        <p class="text-muted font-s24 center-block"> <span class="text-danger font-s12">Para cambiar la configuración solo desmarque la casilla.</span></p>
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" value="" id="CBNomInstitucion" checked>
                                            <label class="form-check-label" for="nomInstitucion">
                                              Mantener el nombre de la configuración inicial
                                            </label>
                                        </div>
                                    </div>
                                </div>


                                <p class="text-muted font-s12 center-block">

                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <button id="ButtonGuardarNomInstitucion"  class="btn btn-sm btn-success pull-right  BtnAccionesClaveCarrea" onclick="$('#txtBandera').val('5');" type="submit"><i class="fa fa-save push-5-r"></i>Guardar</button>
                                        <button id="ButtonNombreInstitucion" class="btn btn-sm btn-success pull-right push-5-r" type="button" onclick="$('#modal-NombreInstitucion').modal('hide')"><i class="si si-close push-5-r"></i> Cerrar</button>
                                    </div>
                                </div>       
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- FIN MODAL CONFIGURACIÓN NOMBRE DE INSTITUCIÓN -->
        
        <%
            if (!usuario.equals("null")) {
        %>
        <jsp:include page="../Generales/cambiarContrasenia.jsp">
            <jsp:param name="allow" value="si" /> 
        </jsp:include>
        <%}%>
        <script src="../assets/js/plugins/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
        <script src="../assets/js/plugins/masked-inputs/jquery.maskedinput.min.js"></script>
        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/datatables/jquery.dataTables.min.js"></script>
        <script src="../assets/js/pages/base_tables_datatables.js"></script>        
        <script src="../assets/js/pages/base_ui_activity.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js"></script>
        <script src="../assets/js/plugins/sessionTimeOut/bootstrap-session-timeout.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesTEClavesCarrera.js?v=1.3"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>

        <script>
                                            jQuery(function () {
                                                // Init page helpers (BS Datepicker + BS Datetimepicker + BS Colorpicker + BS Maxlength + Select2 + Masked Input + Range Sliders + Tags Inputs plugins)
                                                App.initHelpers(['maxlength', 'datapicker', 'masked-inputs']);
                                            });
        </script>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
        <input id="txtModulo" name="txtModulo" value="Claves de Carrera" style="display: none" disabled="disabled">
    </body>
</html>
