<%-- 
    Document   : TituloAutenticados
    Created on : 09-jun-2020, 13:35:10
    Author     : Braulio Sorcia
--%>

<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%
    HttpSession sessionOk = request.getSession();
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
    String usuario = "" + sessionOk.getAttribute("NombreUsuario");
    String NombreC = "" + sessionOk.getAttribute("Nombre") + " " + sessionOk.getAttribute("APaterno");
    String rol = "" + sessionOk.getAttribute("NombreTipo");
    String comminglock = request.getParameter("comminglock");
    String Menu = "" + sessionOk.getAttribute("Menu");
    String genero = "" + sessionOk.getAttribute("Genero");
    String permisoAcceso = (sessionOk.getAttribute("TITULOS_AUTENTICADOS") == null ? "0" : sessionOk.getAttribute("TITULOS_AUTENTICADOS").toString());
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
<html>
    <head>
        <meta charset="utf-8">

        <title>Digi-SEP - Titulos Autenticados</title>

        <meta name="description" content="Digi-SEP - sistema de títulos, cédulas y certificados electrónicos">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <!-- Stylesheets -->
        <!-- Web fonts -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400italic,600,700%7COpen+Sans:300,400,400italic,600,700">


        <!-- END Stylesheets --> 
        <link rel="stylesheet" href="../assets/js/plugins/bootstrap-datepicker/bootstrap-datepicker3.min.css">
        <link rel="stylesheet" href="../assets/js/plugins/sweetalert/sweetalert2.css">
        <link rel="stylesheet" href="../assets/js/plugins/select2/select2.min.css">
        <link rel="stylesheet" href="../assets/js/plugins/select2/select2-bootstrap.min.css">
        <link rel="stylesheet" href="../assets/js/plugins/datatables/jquery.dataTables.min.css">
        <link rel="stylesheet" href="../assets/js/plugins/jquery-tags-input/jquery.tagsinput.min.css">
        <link rel="stylesheet" href="../assets/css/LoadersGeneral.css">

        <!-- Bootstrap and OneUI CSS framework -->
        <link rel="stylesheet" href="../assets/css/bootstrap.min.css">
        <link rel="stylesheet" id="css-main" href="../assets/css/oneui.css">

        <!-- You can include a specific file from css/themes/ folder to alter the default color theme of the template. eg: -->
        <link rel="stylesheet" id="css-theme" href="../assets/css/themes/modern.min.css">

        <!-- END Stylesheets -->
        <style>
            th, td {
                /**width: 16.5%;**/
                border: 1px solid black;
                max-width:250px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                margin:0 5px 0 5px; padding:0;
            }
            table {
                width: 100%;
            }
        </style>
        <!-- Page JS Plugins -->
        <script src="../assets/js/core/jquery.min.js"></script>
        <script src="../assets/js/core/bootstrap.min.js"></script>
        <script src="../assets/js/core/jquery.slimscroll.min.js"></script>
        <script src="../assets/js/core/jquery.scrollLock.min.js"></script>
        <script src="../assets/js/core/jquery.appear.min.js"></script>
        <script src="../assets/js/core/jquery.placeholder.min.js"></script>
        <script src="../assets/js/plugins/chosen/chosen.jquery.js"></script>
        <script src="../assets/js/plugins/jquery-validation/jquery.validate.min.js"></script>
        <script src="../assets/js/plugins/jquery-validation/additional-methods.js"></script>
        <script src="../assets/js/pages/base_forms_validation.js"></script>
        <script src="../assets/js/core/js.cookie.min.js"></script>
        <script src="../assets/js/app.js"></script>

        <!-- Page JS Code -->
        <script src="../assets/js/plugins/jquery-vide/jquery.vide.min.js"></script>
    </head>
    <body style='overflow:hidden;'>
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
                                    <%                                        if (genero.equalsIgnoreCase("250")) {
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
                                    Gestión de Títulos Autenticados
                                </h1>
                                <h3 class="h5 text-white visible-xs text-left"><em>Hola</em></h3>
                                <h2 class="h4 font-w600 text-white push-5 visible-xs text-left"><%=NombreC%></h2>
                            </div>
                            <div class="col-sm-6 block-content  text-center visible-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white">
                                    Gestión de Títulos Autenticados
                                </h1>
                            </div>
                            <div class="col-lg-3 col-sm-3 col-md-3 block-content  hidden-xs" style="min-height: 115px;">

                            </div>
                        </div>

                    </div>
                    <%
                        if (permisoAcceso.equalsIgnoreCase("1")) {

                    %>
                    <!-- Overview -->
                    <div id="fullContent" class="push-20-t">
                        <div class="block"> 
                            <div class="block-header">
                                <div class="xs-12">
                                    <div class="pull-right" id="divAcciones">
                                        <button class="btn btn-success push-5-r btn-sm btn-rounded" id="btnDescargaMasivaPDF" disabled="disabled"><i class="fa fa-file-pdf-o"></i> Generar lote de descarga (PDF)</button>
                                        <button class="btn btn-primary btn-sm btn-rounded" id="btnImportarTitulosZip"><i class="fa fa-cloud-upload"></i> Importar Títulos XML</button>
                                        <button class="btn btn-info btn-sm btn-rounded" id="btnImportarTitulosPdf"><i class="fa fa-cloud-upload"></i> Importar Constancias PDF</button>
                                    </div>
                                </div>
                                <div style="display:none;">
                                    <form class="form-horizontal" id="FormZip" name="FormZip">
                                        <input style="display: none;" type="text" id="txtBandera" name="txtBandera" value="">
                                        <div class="form-group">
                                            <input style="display: none;" class="form-control" type="file" id="fileZipAutenticado" name="fileZipAutenticado" title="Cargar zip autenticado">
                                        </div>
                                        <div class="form-group">
                                            <button class="btnAccionesZip" style="display:none;" type="submit" id="importarArchivoZip" name="importarArchivoZip" onclick="$('#txtBandera').val('zip');"></button>
                                        </div>
                                    </form>
                                </div>

                                <div style="display:none;">
                                    <form class="form-horizontal" id="FormZipConstancias" name="FormZipConstancias">
                                        <input style="display: none;" type="text" id="txtBander2" name="txtBandera2" value="pdf">
                                        <div class="form-group">
                                            <input style="display: none;" class="form-control" type="file" id="fileZipConstancia" name="fileZipConstancia" title="Cargar zip autenticado">
                                        </div>
                                        <div class="form-group">
                                            <button class="btnAccionesZipConstancias" style="display:none;" type="submit" id="importarArchivoZipConstancia" name="importarArchivoZipConstancia"></button>
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
                                                <button class="btn btn-primary btn-sm push-5-t" type="button" id="btnFiltrarTitulos"><i class="fa fa-search"></i></button>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div id="divTblTitulos" style="margin-top:20px;">
                                    <table class="table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed" style="width:100%;"  id="tblTitulosAutenticados">
                                        <thead class="bg-primary-dark" style="color: white;">
                                            <tr>
                                                <th class="text-center" style="display:none;">IdTitulo</th>
                                                <th class="text-center">Folio</th>
                                                <th class="text-center hidden-xs">Matricula</th>
                                                <th class="text-center hidden-md hidden-sm hidden-xs">Nombre(s)</th>
                                                <th class="text-center hidden-xs">A. Paterno</th>
                                                <th class="text-center hidden-xs">Carrera</th>
                                                <th class="text-center" style="width: 10%">Acciones</th>
                                                <th class=' text-center bg-primary-dark' style='cursor: pointer; vertical-align:middle;'></th>
                                            </tr>
                                        </thead>
                                        <tbody id="tblBodyTitulos">
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

                <%                } else {
                %>
                <div class='col-xs-12 push-20-t'>
                    <a class='block block-link-hover2 text-center' href='javascript:void(0)'>
                        <div class='block-content block-content-full border-b'>
                            <i class='fa fa-expeditedssl fa-5x text-danger'></i>
                        </div>
                        <div class='block-content block-content-full block-content-mini bg-gray-lighter'>
                            <strong>¡SIN ACCESO!</strong>
                            <p>Parece ser que el módulo no está activado para la institución.</p>
                            <p>Si necesitas más información <strong>contacta a tu asesor comercial.</strong></p>
                        </div>
                    </a>
                </div>
                <%
                    }
                %>
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

        <!-- MODAL CAMBIO DE CONTRASEÑA -->
        <%
            if (!usuario.equals("null")) {
        %>
        <jsp:include page="../Generales/cambiarContrasenia.jsp">
            <jsp:param name="allow" value="si" /> 
        </jsp:include>
        <%}%>


        <!--========================== JS SECTION=========================================-->
        <script src="../assets/js/plugins/jquery-ui/jquery-ui.min.js"></script>
        <script src="../assets/js/plugins/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
        <script src="../assets/js/plugins/bootstrap-notify/bootstrap-notify.min.js"></script>
        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/select2/select2.full.min.js"></script>
        <script src="../assets/js/plugins/datatables/jquery.dataTables.min.js"></script>
        <script src="../assets/js/pages/base_tables_datatables.js"></script>
        <script src="../assets/js/plugins/jquery-tags-input/jquery.tagsinput.min.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js"></script>
        <script src="../assets/js/plugins/masked-inputs/jquery.maskedinput.min.js"></script>
        <script src="../assets/js/plugins/sessionTimeOut/bootstrap-session-timeout.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesTETitulosAutenticados.js?v=1.1"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>
        <script>
                                                jQuery(function () {
                                                    // Init page helpers (BS Datepicker + BS Datetimepicker + BS Colorpicker + BS Maxlength + Select2 + Masked Input + Range Sliders + Tags Inputs plugins)
                                                    App.initHelpers(['maxlength', 'select2', 'tags-inputs', 'datepicker', 'masked-inputs', 'notify']);
                                                });
        </script>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
        <input id="txtModulo" name="txtModulo" value="Titulos Autenticados" style="display: none" disabled="disabled">
    </body>
</html>