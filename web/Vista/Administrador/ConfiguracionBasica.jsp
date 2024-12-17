<%-- 
    Document   : ConfiguracionesBasicas
    Created on : 22/01/2019, 02:55:43 PM
    Author     : Paola Alonso
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
    String Id_configuracionInicial = "" + sessionOk.getAttribute("Id_configuracionInicial");
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

        <title>Digi-SEP - Configuración Inicial</title>

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
            .btnFile{
                overflow: hidden;
                white-space: nowrap;
                display: block;
                text-overflow: ellipsis;
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

                            <%
                                out.print((Id_configuracionInicial.equalsIgnoreCase("0")) ? "<a class='h5 text-white' href='#'>" : "<a class='h5 text-white' href='Bienvenida.jsp'>");
                            %>
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
                    <div class="block">
                        <!-- Basic Info -->
                        <div class="bg-image push-10-t" style="background-image: url(&quot;../assets/img/flechasDobles.png&quot;);">
                            <div class="block-content bg-primary-op text-center overflow-hidden">
                                <div class="push-30-t push animated fadeInDown">
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
                                <div class="push-30 animated fadeInUp">
                                    <h2 class="h4 font-w600 text-white push-5">Configuraciones Iniciales</h2>
                                    <h3 class="h5 text-white-op"><%=NombreC%></h3>
                                </div>
                            </div>
                        </div>
                        <!-- END Basic Info -->
                    </div>
                    <div id="fullContent">
                        <div class="block push-20-t">
                            <div class="block">
                                <div class="block-header bg-gray-lighter">
                                    <ul class="block-options">
                                        <li id="liBtnNuevo">
                                            <button id="btnNuevaClave" data-toggle="modal" data-target="#modal-ClaveActivacion" type="button" class="btn text-success"><i class="fa fa-plus"></i> Agregar Clave</button>
                                        </li>
                                    </ul>
                                </div>
                                <div class="block-content block-content-full" style="overflow: inherit;">
                                    <form class="form-horizontal" id="FormConfiguracionesIniciales" name="FormConfiguracionesIniciales">
                                        <input class="form-control" type="hidden" id="txtBandera" name="txtBandera">
                                        <input class="form-control" type="hidden" id="txtIdConfiguracion" name="txtIdConfiguracion">
                                        <input class="form-control" type="hidden" id="txtNombreInstitucion2" name="txtNombreInstitucion2">
                                        <div class="form-group">
                                            <div class="col-lg-6 col-xs-12 col-sm-6 col-md-6">
                                                <div class="form-material form-material-primary input-group floating" id="DivTxtNombreInstitucionIn">
                                                    <input class="form-control js-maxlength" type="text" id="txtNombreInstitucion" name="txtNombreInstitucion" maxlength="100" autoComplete="off">
                                                    <label for="txtNombreInstitucion"  data-toggle="popover" title="Nombre comercial" data-placement="right" data-content="El nombre de la institución no se puede modificar una vez agregado." data-original-title="Right Popover"><span class="text-danger ">▪</span> Nombre de la Institución</label>
                                                    <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                                </div>
                                            </div>
                                            <div class="col-lg-6 col-xs-12 col-sm-6 col-md-6">
                                                <div class="form-material form-material-primary input-group floating" id="DivTxtNombreInstitucionIn">
                                                    <input class="form-control js-maxlength" type="text" id="txtNombreOficial" name="txtNombreOficial" maxlength="100" autoComplete="off">
                                                    <label for="txtNombreOficial"  data-toggle="popover" title="Nombre oficial" data-placement="right" data-content="El nombre oficial aparecerá en los certificados electrónicos, por lo que deberá ingresar el nombre correcto" data-original-title="Right Popover"><span class="text-danger ">▪</span> Nombre oficial</label>
                                                    <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-lg-6 col-xs-12">
                                                <div class="form-material form-material-primary input-group floating" id="DivTxtCalifMinAprobatoriaIn">
                                                    <input class="form-control" type="text" id="txtClaveInstitucional" name="txtClaveInstitucional" autoComplete="off" disabled>
                                                    <label for="txtClaveInstitucional" data-toggle="popover" title="Clave Institucional" data-placement="right" data-content="La clave institucional se asigna una vez que se solicitan los timbres para la documentación" data-original-title="Right Popover">Clave Institucional</label>
                                                    <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                                </div>
                                            </div>
                                            <div class="form-group">

                                                <div class="col-lg-3 col-xs-12">
                                                    <input type="file" id="txtFileLogo" name="txtFileLogo" accept="image/png,image/gif,image/jpeg" style="display: none;">
                                                    <button id="btnFileLogo" name="btnFileLogo" class="btn btn-primary btn-block push-10 btnFile" type="button" onclick="$('#txtFileLogo').click();" data-toggle="popover" title="" data-placement="top" data-content="Imagen utilizada en los encabezados de los formatos <b>PDF</b>." data-html="true" data-original-title="Importante"><i class="fa fa-image"></i> Cargar logo institucional</button>
                                                </div>
                                                <div class="col-lg-3 col-xs-12">
                                                    <input type="file" id="txtFileFirma" name="txtFileFirma" accept="image/png,image/gif,image/jpeg" style="display: none;">
                                                    <button id="btnFileFirma" name="btnFileFirma" class="btn btn-warning btn-block push-10 btnFile" type="button" onclick="$('#txtFileFirma').click();" data-toggle="popover" title="" data-placement="top" data-content="Firma en formato digital utilizada para los formatos <b>PDF</b>. <br>Se utiliza en el reverso de los documentos." data-html="true" data-original-title="Importante"><i class="fa fa-image"></i> Cargar firma</button>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="col-lg-6 col-xs-12">
                                                    <div class="form-material form-material-primary input-group floating" id="DivTxtVigenciaInicioIn">
                                                        <input class="form-control" type="text" id="txtVigenciaInicio" name="txtVigenciaInicio" autoComplete="off" disabled>
                                                        <label for="txtVigenciaInicio" data-toggle="popover" title="Inicio de Vigencia" data-placement="right" data-content="Fecha en la cual se dio de alta su renovación del programa " data-original-title="Right Popover">Fecha Inicio Vigencia</label>
                                                        <span class="input-group-addon "><i class="fa fa-calendar"></i></span>
                                                    </div>
                                                </div>
                                                <div class="col-lg-6 col-xs-12">
                                                    <div class="form-material form-material-primary input-group floating" id="DivTxtVigenciaFinIn">
                                                        <input class="form-control" type="text" id="txtVigenciaFin" name="txtVigenciaFin" autoComplete="off" disabled>
                                                        <label for="txtVigenciaFin" data-toggle="popover" title="Fin de Vigencia" data-placement="right" data-content="Fecha en la cual termina su renovación del programa " data-original-title="Right Popover">Fecha Fin Vigencia</label>
                                                        <span class="input-group-addon "><i class="fa fa-calendar"></i></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="col-lg-6 col-xs-12">
                                                    <div class="form-material form-material-primary input-group floating" id="divTxtUsuarioSEPIn">
                                                        <input class="form-control" type="text" id="txtUsuarioSEP" name="txtUsuarioSEP" autoComplete="off">
                                                        <label for="txtUsuarioSEP" data-toggle="tooltip" title="Usuario Servicio SEP" data-placement="right" data-container='body'>Usuario SEP AMBIENTE PRUEBAS</label>
                                                        <span class="input-group-addon "><i class="fa fa-user-secret"></i></span>
                                                    </div>
                                                </div>
                                                <div class="col-lg-6 col-xs-12">
                                                    <div class="form-material form-material-primary input-group floating" id="divTxtPasswordSEPIn">
                                                        <input class="form-control" type="text" id="txtPasswordSEP" name="txtPasswordSEP" autoComplete="off">
                                                        <label for="txtPasswordSEP" data-toggle="tooltip" title="Contraseña Servicio SEP" data-placement="right" data-container='body'>Contraseña SEP AMBIENTE PRUEBAS</label>
                                                        <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <div class="col-lg-6 col-xs-12">
                                                    <div class="form-material form-material-primary input-group floating" id="divTxtUsuarioSepProdIn">
                                                        <input class="form-control" type="text" id="txtUsuarioSepProd" name="txtUsuarioSepProd" autoComplete="off">
                                                        <label for="txtUsuarioSepProd" data-toggle="tooltip" title="Usuario Servicio SEP PRODUCTIVO" data-placement="right" data-container='body'>Usuario SEP AMBIENTE PRODUCTIVO</label>
                                                        <span class="input-group-addon "><i class="fa fa-user-secret"></i></span>
                                                    </div>
                                                </div>
                                                <div class="col-lg-6 col-xs-12">
                                                    <div class="form-material form-material-primary input-group floating" id="divTxtPasswordSepProdIn">
                                                        <input class="form-control" type="text" id="txtPasswordSepProd" name="txtPasswordSepProd" autoComplete="off">
                                                        <label for="txtPasswordSepProd" data-toggle="tooltip" title="Contraseña Servicio SEP PRODUCTIVO" data-placement="right" data-container='body'>Contraseña SEP AMBIENTE PRODUCTIVO</label>
                                                        <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-xs-12">
                                                <div class="panel panel-primary">
                                                    <div class="panel-heading text-center">
                                                        <h5>Folios de Títulos Electrónicos</h5>
                                                    </div>
                                                    <div class="panel-body">
                                                        <div class="form-group">
                                                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                                                <div class="form-material form-material-primary input-group floating" id="DivTxtPrefijoTituloIn">
                                                                    <input class="form-control js-maxlength" type="text" id="txtPrefijoTitulo" name="txtPrefijoTitulo" maxlength="10" autoComplete="off">
                                                                    <label for="txtPrefijoTitulo"><span class="text-danger ">▪</span> Prefijo</label>
                                                                    <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                                                </div>
                                                            </div>
                                                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                                                <div class="form-material form-material-primary input-group floating" id="DivTxtLongitudIn">
                                                                    <input class="form-control" type="number" id="txtLongitud" name="txtLongitud" min="5" max="30" autoComplete="off">
                                                                    <label for="txtLongitud"><span class="text-danger ">▪</span> Longitud</label>
                                                                    <span class="input-group-addon "><i class="fa fa-hashtag"></i></span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </div>
                                            <div class="col-xs-12">
                                                <div class="panel panel-primary">
                                                    <div class="panel-heading text-center">
                                                        <h5>Folios de Certificados Electrónicos</h5>
                                                    </div>
                                                    <div class="panel-body">
                                                        <div class="form-group">
                                                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                                                <div class="form-material form-material-primary input-group floating" id="DivTxtPrefijoCertificadoIn">
                                                                    <input class="form-control js-maxlength" type="text" id="txtPrefijoCertificado" name="txtPrefijoCertificado" maxlength="10" autoComplete="off">
                                                                    <label for="txtPrefijoCertificado"><span class="text-danger ">▪</span> Prefijo</label>
                                                                    <span class="input-group-addon"><i class="fa fa-font"></i></span>
                                                                </div>
                                                            </div>
                                                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                                                <div class="form-material form-material-primary input-group floating" id="DivTxtLongitudCertificadosIn">
                                                                    <input class="form-control" type="number" id="txtLongitudCertificados" name="txtLongitudCertificados" min="5" max="30" step="1" autoComplete="off">
                                                                    <label for="txtLongitudCertificados"><span class="text-danger ">▪</span> Longitud</label>
                                                                    <span class="input-group-addon "><i class="fa fa-hashtag"></i></span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <div class="col-xs-12 col-lg-12 col-md-12 col-sm-12">
                                                    <button id="btnAddInitialConfig" class="btn btn-sm btn-success pull-right InitialConfigActions" type="submit" onclick="$('#txtBandera').val('2')"><i class="si si-plus push-5-r"></i> Añadir configuraciones</button>
                                                    <button id="btnUpdInitialConfig" class="btn btn-sm btn-success pull-right InitialConfigActions" type="submit" onclick="$('#txtBandera').val('3')" hidden><i class="si si-plus push-5-r"></i> Guardar configuraciones</button>
                                                </div>
                                            </div>
                                    </form>
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
        <div class="modal fade push-200-t" id="modal-ClaveActivacion" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-dialog-popin mymodal nomini">
                <div class="modal-content" id="modal-ClaveActivacion-draggable" >
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button data-dismiss="modal" type="button" id="btnCerrarCveActivacion"><i class="si si-close"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitle">NUEVA CLAVE DE ACTIVACIÓN</h3>
                        </div>
                        <div class="block-content form-group" style="height: auto; overflow:inherit;">
                            <form class="form-horizontal"  id="FormClaveActivacion" name="FormClaveActivacion"> 
                                <input style="display: none;" id="txtBanderaActivacion" name="txtBanderaActivacion" value=""/>
                                <input style="display: none;" id="idConfiguracionActivacion" name="idConfiguracionActivacion" value=""/>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtClaveCarrera">
                                            <input class="form-control" type="text" id="txtClaveActivacion" name="txtClaveActivacion" autoComplete="off">
                                            <label for="txtClaveActivacion" data-toggle="popover" title="Clave de activación" data-placement="right" data-content="Ingresa la clave de activación proporcionada por el personal de Grupo Inndex" data-original-title="Right Popover"><span class="text-danger ">▪</span> Clave de Activación</label>
                                            <span class="input-group-addon "><i class="fa fa-key"></i></span>
                                        </div>
                                    </div>
                                </div>

                                <p class="text-muted font-s12 center-block">
                                    Si no cuentas con una clave de activación, comunícate con el personal de Grupo Inndex para solicitar una.</p>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <button id="btnAgregarClaveActivacion" class="btn btn-sm btn-success pull-right BtnAccionesClaveActivacion" onclick="$('#txtBanderaActivacion').val('4');" type="submit"><i class="fa fa-save push-5-r"></i>Guardar</button>
                                    </div>
                                </div>     
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <%
            if (!usuario.equals("null")) {
        %>
        <jsp:include page="../Generales/cambiarContrasenia.jsp">
            <jsp:param name="allow" value="si" /> 
        </jsp:include>
        <%}%>
        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>
        <script src="../assets/js/pages/base_ui_activity.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js"></script>
        <script src="../assets/js/plugins/jquery-ui/jquery-ui.min.js"></script>
        <script src="../assets/js/plugins/sessionTimeOut/bootstrap-session-timeout.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesTEConfiguracionInicial.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>
        <script>
                                            jQuery(function () {
                                                App.initHelpers(['maxlength']);
                                            });

        </script>

        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
        <input id="txtModulo" name="txtModulo" value="Configuración Inicial" style="display: none" disabled="disabled">
    </body>
</html>