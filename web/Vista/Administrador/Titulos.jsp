<%-- 
    Document   : Titulos
    Created on : 08-ene-2019, 13:11:10
    Author     : Braulio Sorcia
	Versión	   : DIGISEP-GENERAL
--%>

<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%
    HttpSession sessionOk = request.getSession();
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
    String usuario = "" + sessionOk.getAttribute("NombreUsuario");
    String Nombre = "" + sessionOk.getAttribute("Nombre");
    String NombreC = "" + sessionOk.getAttribute("Nombre") + " " + sessionOk.getAttribute("APaterno");
    String Tema = "" + sessionOk.getAttribute("Tema");
    String rol = "" + sessionOk.getAttribute("NombreTipo");
    String comminglock = request.getParameter("comminglock");
    String SupportX = "" + sessionOk.getAttribute("BD_Name");
    String Id_SuarioLog = "" + sessionOk.getAttribute("Id_Usuario");
    String Menu = "" + sessionOk.getAttribute("Menu");
    String genero = "" + sessionOk.getAttribute("Genero");
    String permisoCargaMasiva = (sessionOk.getAttribute("IMPORTACION_MASIVA_TITULO_ELECTRONICO") == null ? "0" : sessionOk.getAttribute("IMPORTACION_MASIVA_TITULO_ELECTRONICO").toString());
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

        <title>Digi-SEP - Titulos</title>

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
                /**width: 12.5%;**/
                border: 1px solid black;
                max-width:100px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                margin:0 5px 0 5px; padding:0;
                cursor: pointer;
            }
            table {
                width: 100%;
            }
            .btnDescargas{
                text-align: justify;
                width: 100%;
                overflow: hidden;
                white-space: nowrap;
                display: block;
                text-overflow: ellipsis;
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
    <body style='overflow:hidden; zoom: 90%'>
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
                                    Títulos Electrónicos
                                </h1>
                                <h3 class="h5 text-white visible-xs text-left"><em>Hola</em></h3>
                                <h2 class="h4 font-w600 text-white push-5 visible-xs text-left"><%=NombreC%></h2>
                            </div>
                            <div class="col-sm-6 block-content  text-center visible-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white">
                                    Títulos Electrónicos
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
                                <div class="dropdown block-options hidden-xs">
                                    <button id="dropdownMenuButton" class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-fw fa-cog"></i> Acciones Extra</button>
                                    <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                                        <li id="liBtnCargasMasivasTitulosSEP">
                                            <button id="btnliBtnCargasMasivasTitulosSEP" type="button" class="btn text-info li-buttons" disabled="disabled">
                                                <i class="si si-cloud-upload"></i> Generar carga de lote de titulos en pruebas
                                            </button>
                                        </li>
                                        <li id="liBtnCargasMasivasTitulosSEPProductivo">
                                            <button id="btnliBtnCargasMasivasTitulosSEPProductivo" type="button" class="btn text-success li-buttons" disabled="disabled">
                                                <i class="si si-cloud-upload"></i> Generar carga de lote de titulos en productivo
                                            </button>
                                        </li>
                                    </ul>
                                </div>
                                <div class="push-15-t col-xs-12 pull-right">
                                    <div class="xs-12">
                                        <ul class="block-options">
                                            <li id="liBtnNuevo">
                                                <button id="btnNuevoTitulo" data-toggle="modal" data-target="#modal-titulosElectronicos" type="button" class="btn  text-success"><i class="fa fa-arrow-right"></i> Nuevo</button>
                                            </li>
                                            <li id="liBtnGenXmlMasivo">
                                                <button id="btnGeneXmlMasivo" type="button" class="btn text-success li-buttons" disabled="disabled">
                                                    <i class="fa fa-file-code-o"></i> Generar archivos xml
                                                </button>
                                            </li>
                                            <li id="liBtnDescargarTodos">
                                                <button id="btnDescargarXmlMasivo" type="button" class="btn text-success li-buttons" disabled="disabled">
                                                    <i class="fa fa-cloud-download"></i> Generar archivo zip
                                                </button>
                                            </li>
                                            <li id="liBtnDescargarPDF">
                                                <button id="btnDescargarPDFMasivo" type="button" class="btn text-danger li-buttons" disabled="disabled">

                                                    <i class="fa fa-file-pdf-o"></i> Generar lote de descarga zip (solo pdf)
                                                </button>
                                            </li>
                                            <li id="liBtnDescargarXMLMasivo">
                                                <button id="btnDescargarXMLMasivo" type="button" class="btn text-warning li-buttons" disabled="disabled">
                                                    <i class="fa fa-file-code-o"></i> Generar lote de descarga zip (solo xml)
                                                </button>
                                            </li>
                                        </ul>
                                        <div style="display:none;">
                                            <form class="form-horizontal" id="form-importar-titulos-masivos" name="form-importar-titulos-masivos">
                                                <input style="display: none;" type="text" id="txtBandera2" name="txtBandera" value="">
                                                <div class="form-group">
                                                    <input style="display: none;" class="form-control" type="file" id="fileTitulos" name="fileTitulos" title="Cargar Titulos">
                                                </div>
                                                <div class="form-group">
                                                    <button class="btnAccionesTitulos" style="display:none;" type="submit" id="importarArchivo" name="importarArchivo" onclick="$('#txtBandera2').val('excel');"></button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                    <div id="divBotonesDescarga" class="col-xs-12 col-sm-12 col-lg-12 push-30-t" style="display: none;">
                                        <div class="col-xs-12 col-sm-4 col-md-4 col-lg-4" id="divDescargaZipMasivo" style="display: none;">
                                        </div>
                                        <div class="col-xs-12 col-sm-4 col-md-4 col-lg-4" style="margin-left: -20px; display: none;" id="divDescargaPdfMasivo">
                                        </div>
                                        <div class="col-xs-12 col-sm-4 col-md-4 col-lg-4" id="divDescargaXmlMasivo" style="display: none;">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <br>
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
                                    <%
                                        if (permisoCargaMasiva.equalsIgnoreCase("1")) {
                                    %>
                                    <div class="col-lg-5 col-xs-12 push-10-t pull-right">
                                        <div class="pull-right">
                                            <button class="btn btn-primary push-5-r btn-sm btn-rounded" id="btnDescargarFormato"><i class="si si-cloud-download"></i> <a class='text-white' download href="../../Importaciones/ImportarTitulosElectronicos.xlsx"> Descargar formato</a></button>
                                            <button class="btn btn-primary btn-sm btn-rounded" id="btnImportarMasivo"><i class="fa fa-file-excel-o"></i> Importar registros</button>
                                        </div>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <div class="col-lg-12 col-xs-12 push-10-t pull-right">
                                        <div class="pull-right">
                                            <p style="margin: 0;">Títulos seleccionados : <strong id="contador-seleccionados">0</strong></p>
                                        </div>
                                    </div>
                                </div>
                                <div id="divTblTitulos" style="margin-top:20px;">
                                    <table class="table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed" style="width:100%;"  id="tblTitulos">
                                        <thead class="bg-primary-dark" style="color: white;">
                                            <tr>
                                                <th class="text-center" style="display:none;">IdTitulo</th>
                                                <th class="text-center hidden-sm hidden-xs">Folio</th>
                                                <th class="text-center">Matricula</th>
                                                <th class="text-center hidden-xs">Nombre(s)</th>
                                                <th class="text-center hidden-xs">A. Paterno</th>
                                                <th class="text-center hidden-xs">Carrera</th>
                                                <th class='text-center hidden-xs'>Expedición</th>
                                                <th class='text-center hidden-md hidden-sm hidden-xs'>MET</th>
                                                <th class='text-center hidden-md hidden-sm hidden-xs'>Estatus</th>
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


        <!-- Inicio Modal Titulos Electronicos -->
        <div class="modal fade" id="modal-titulosElectronicos" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-dialog-popin mymodal nomini modal-lg">
                <div class="modal-content" id="modal-claveCarrera-draggable" >
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button data-dismiss="modal" type="button" id="btnCerrarTitulo"><i class="si si-close"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitle">NUEVO TÍTULO</h3>
                        </div>
                        <div class="block-content form-group" style="height: auto; overflow:inherit;">
                            <form class="form-horizontal"  id="FormTitulosElectronicos" name="FormTitulosElectronicos"> 
                                <input style="display: none;" id="txtBandera" name="txtBandera" value=""/>
                                <input style="display: none;" id="idTitulo" name="idTitulo" value=""/>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="input-group">
                                            <div class="form-material form-material-primary floating" id="DivTxtMatricula">
                                                <input class="form-control js-maxlength" type="text" id="txtMatricula" name="txtMatricula" maxlength="20" autoComplete="off">
                                                <label for="txtMatricula"><span class="text-danger ">▪</span> Matrícula</label>
                                            </div>
                                            <span class="input-group-btn">
                                                <button class="btn btn-primary btn-sm" type="button" id="btnBuscarAlumno"><i class="fa fa-search"></i></button>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-6 col-md-3" id="DivLstCarreraTitulo">
                                        <div class="form-material form-material-primary" id="DivLstCarreraTituloIn">
                                            <select class="form-control" id="lstCarreraTitulo" name="lstCarreraTitulo">
                                            </select>
                                            <label for="lstCarreraTitulo"><span class="text-danger ">▪</span> Carrera</label>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" id="DivTxtNombreAlumno">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtNombreAlumnoIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtNombreAlumno" name="txtNombreAlumno" maxlength="500" autoComplete="off">
                                            <label for="txtNombreAlumno"><span class="text-danger ">▪</span> Nombre del Alumno</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-6 col-sm-12 col-xs-12 push-10-t" id="DivTxtFolioControl">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFolioControlIn">
                                            <input class="form-control js-maxlength" type="text" id="txtFolioControl" name="txtFolioControl" maxlength="500" autoComplete="off" disabled>
                                            <label for="txtFolioControl" data-toggle="popover" title="Folio de Control" data-placement="right" data-content="El folio de control es generado automáticamente por el sistema." data-original-title="Right Popover"> Folio de Control</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-12 col-md-6 push-10-t" id="DivLstModalidadTitulacion">
                                        <div class="form-material form-material-primary" id="DivLstModalidadTitulacionIn">
                                            <select class="form-control" id="lstModalidadTitulacion" name="lstModalidadTitulacion">
                                            </select>
                                            <label for="lstModalidadTitulacion"><span class="text-danger ">▪</span> Modalidad de Titulación</label>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-12 col-md-6 push-10-t" id="DivLstFundamentoLegal">
                                        <div class="form-material form-material-primary" id="DivLstFundamentoLegalIn">
                                            <select class="form-control" id="lstFundamentoLegal" name="lstFundamentoLegal">
                                            </select>
                                            <label for="lstFundamentoLegal"><span class="text-danger ">▪</span> Fundamento Legal</label>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-12 col-md-6 push-10-t" id="DivLstLugarExpedicion">
                                        <div class="form-material form-material-primary" id="DivLstLugarExpedicionIn">
                                            <select class="form-control" id="lstLugarExpedicion" name="lstLugarExpedicion">
                                            </select>
                                            <label for="lstLugarExpedicion"><span class="text-danger ">▪</span> Lugar de Expedición</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaInicio">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaInicio" name="txtFechaInicio" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaInicio">Fecha Inicio</label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaFin">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaFin" name="txtFechaFin" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaFin"><span class="text-danger ">▪</span> Fecha Fin</label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12" id='DivTxtFechaExpedicion'>
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaExpedicionIn">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaExpedicion" name="txtFechaExpedicion" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaExpedicion">Fecha Expedición <i style="cursor: pointer" class="fa fa-question-circle" 
                                                                                                data-toggle="popover" title="" 
                                                                                                data-placement="right" 
                                                                                                data-content="Se recomienda expedir los títulos con fecha del día anterior a ser cargados al ambiente de validación SEP." 
                                                                                                data-original-title="Recomendación"></i></label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-6 col-md-6" id="DivLstCumplioServicio">
                                        <div class="form-material form-material-primary" id="DivLstCumplioServicioIn">
                                            <select class="form-control" id="lstCumplioServicio" name="lstCumplioServicio">
                                                <option></option>
                                                <option value='1'>Sí cumplió</option>
                                                <option value='0'>No cumplió</option>
                                            </select>
                                            <label for="lstCumplioServicio"><span class="text-danger ">▪</span> Servicio Social</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12" id='DivTxtFechaExamenProfesional'>
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaExamenProfesionalIn">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaExamenProfesional" name="txtFechaExamenProfesional" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaExamenProfesional">F.Examen Profesional</label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12" id='DivTxtFechaExencionExamenProfesional'>
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaExencionExamenProfesionalIn">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaExencionExamenProfesional" name="txtFechaExencionExamenProfesional" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaExencionExamenProfesional">F. Exención Examen Prof.</label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" id="DivTxtInstitucionProcedencia">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtInstitucionProcedenciaIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtInstitucionProcedencia" name="txtInstitucionProcedencia" maxlength="500" autoComplete="off">
                                            <label for="txtInstitucionProcedencia"><span class="text-danger ">▪</span> Institución de Procedencia</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class="col-lg-3 col-xs-12 col-sm-6 col-md-6" id="DivLstEstudioAntecedente">
                                        <div class="form-material form-material-primary" id="DivLstEstudioAntecedenteIn">
                                            <select class="form-control" id="lstEstudioAntecedente" name="lstEstudioAntecedente">
                                            </select>
                                            <label for="lstEstudioAntecedente"><span class="text-danger ">▪</span> Tipo estudio antecedente</label>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-6 col-md-6" id="DivLstEntidadAntecedente">
                                        <div class="form-material form-material-primary" id="DivLstEntidadAntecedenteIn">
                                            <select class="form-control" id="lstEntidadAntecedente" name="lstEntidadAntecedente">
                                            </select>
                                            <label for="lstEntidadAntecedente"><span class="text-danger ">▪</span> Entidad antecedente</label>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12" id='DivTxtFechaInicioAntecedente'>
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaInicioAntecedenteIn">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaInicioAntecedente" name="txtFechaInicioAntecedente" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaInicioAntecedente">F. Inicio antecedente (opcional)</label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12" id='DivTxtFechaFinAntecedente'>
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtFechaFinAntecedenteIn">
                                            <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaFinAntecedente" name="txtFechaFinAntecedente" type="text" data-date-format="dd-mm-yyyy">
                                            <label for="txtFechaFinAntecedente"><span class="text-danger ">▪</span> F. Fin antecedente</label>
                                            <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class='form-group'>
                                    <div class="col-lg-6 col-md-4 col-sm-12 col-xs-12" id="DivLstFirmantesTitulos">
                                        <div class="form-material form-material-primary" id="DivLstFirmantesTitulosIn">
                                            <label class="lblChosen"><span class="text-danger">▪</span> Firmante(s)</label>                                            
                                            <input class="js-tags-input form-control" type="text" id="txtFirmantes" name="txtFirmantes" value="" data-tagsinput-init="true" style="display: none;">
                                            <div id="tagsPerfil" class="tagsinput" style="width: 100%; min-height: 36px; height: 36px;">
                                                <span class="tag"><span> - Seleccione los firmantes responsables - &nbsp;</span></span>
                                            </div> 
                                            <input style="display: none;" id="txtIdsFirmantes" name="txtIdsFirmantes" value=""/>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-md-6 col-sm-6 col-xs-12" id="DivTxtNoCedula">
                                        <div class="form-material form-material-primary input-group floating" id="DivTxtNoCedulaIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtNoCedula" name="txtNoCedula" maxlength="8" autoComplete="off">
                                            <label for="txtNoCedula">No. Cédula</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-3 col-xs-12 col-sm-12 col-md-6" id="DivLstEstatusTitulo">
                                        <div class="form-material form-material-primary" id="DivLstEstatusTituloIn">
                                            <select class="form-control" id="lstEstatusTitulo" name="lstEstatusTitulo" disabled>
                                                <option></option>
                                                <option value="0">Inactivo</option>
                                                <option value="1">Activo</option>
                                                <option value="2">Cancelado</option>
                                            </select>
                                            <label for="lstEstatusTitulo">Estatus</label>
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
                                <p class="text-muted font-s24 center-block">
                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>
                                <p class="text-muted font-s12 center-block">
                                    TOME EN CUENTA QUE DEPENDIENDO DE LA MODALIDAD DE TITULACIÓN QUE SE MANEJE, DEBE DE LLENAR CAMPOS EXTRA (fechas de exámenes profesionales,etc.).</p>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <button id="ButtonUpdateTitulo"  class="btn btn-sm btn-success pull-right  BtnAccionesTitulos" onclick="$('#txtBandera').val('4');" type="submit"><i class="fa fa-save push-5-r"></i>Guardar</button>
                                        <button id="ButtonAddTitulo" class="btn btn-sm btn-success pull-right  BtnAccionesTitulos" onclick="$('#txtBandera').val('3');" type="submit"><i class="si si-plus push-5-r"></i> Añadir</button>
                                        <button id="ButtonCerrarTitulo" class="btn btn-sm btn-success pull-right " type="button" onclick="$('#modal-titulosElectronicos').modal('hide')"><i class="si si-close push-5-r"></i> Cerrar</button>
                                        <button id="ButtonGenerateXML" class="btn btn-sm btn-success pull-right push-5-r" type="button" disabled><i class="si si-plus push-5-r"></i> Generar XML</button>
                                    </div>
                                </div>     
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <!-- Fin Modal Titulos Electronicos -->

        <!-- INICIO MODAL FIRMANTES -->
        <div class="modal fade" id="modal-listFirmantes" tabindex="-1" role='dialog' aria-hidden='true' style="padding-top: 80px; ">
            <div class="modal-dialog modal-dialog-popin mymodal nomini modal-md"  style="-moz-box-shadow: 0 0 5px #888; -webkit-box-shadow: 0 0 5px #888; box-shadow: 0 0 50px #888;">
                <div class="modal-content" id="modal-listFirmantes-draggable">
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary">
                            <ul class="block-options">
                                <li>
                                    <button class="cerrarFirmantes" data-dismiss="modal" type="button"><i class="si si-close"></i></button>
                                </li>
                            </ul>
                            <h3 class="block-title" id="modaltitle">Añadir Firmantes</h3>
                        </div>
                        <div class="block-content form-group" style="height: auto; overflow:inherit;">
                            <form class="form-horizontal"  id="FormListFirmante" name="FormListFirmante">
                                <div class="form-group">
                                    <div class="col-xs-12 col-lg-12 col-sm-12">
                                        <div class="form-material form-material-primary" id="DivListaPlantelModal">
                                            <label>Seleccione los firmantes responsables</label>
                                            <select class="js-select2 form-control select2-hidden-accessible" id="lstFirmantesTitulos" name="lstFirmantesTitulos" style="width: 100%;" data-placeholder="Firmante..." multiple="" tabindex="-1" aria-hidden="true">
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <button id="btnAgregarFirmantes" class="btn btn-sm  btn-primary pull-right" type="button"><i class="fa fa-exchange push-5-r"></i>Añadir</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- FIN MODAL FIRMANTES -->

        <!-- MODAL CAMBIO DE CONTRASEÑA -->
        <%
            if (!usuario.equals("null")) {
        %>
        <jsp:include page="../Generales/cambiarContrasenia.jsp">
            <jsp:param name="allow" value="si" /> 
        </jsp:include>
        <%}%>


        <!-- Inicio Modal Consulta Resultados PRUEBAS-->
        <div class="modal fade" id="modal-consultaTituloSEP" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-dialog-popin mymodal nomini modal-sm">
                <div class="modal-content">
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button data-dismiss="modal" type="button"><i class="si si-close"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitle">Consulta Proceso de Titulos</h3>
                        </div>
                        <div class="block-content">
                            <div class="col-xs-12">
                                <div class="form-material form-material-primary input-group floating open">
                                    <input class="form-control" type="text" id="txtFolioRespuesta" name="txtFolioRespuesta" autoComplete="off" style="width: 100%" disabled="disabled">
                                    <label for="txtFolioRespuesta"> Folio Titulo</label>
                                    <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                </div>
                            </div>
                            <div class="col-xs-12 push-15-t">
                                <div class="form-material form-material-primary input-group floating open">
                                    <input class="form-control" type="text" id="txtNumLote" name="txtNumLote" autoComplete="off" style="width: 100%" disabled="disabled">
                                    <label for="txtNumLote"> Número de Lote</label>
                                    <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                </div>
                            </div>
                            <div class="col-xs-12 push-15-t">
                                <div class="form-material form-material-primary">
                                    <textarea class="form-control" id="txtMensajeSEP" name="txtMensajeSEP" rows="3" disabled="disabled"></textarea>
                                    <label for="txtMensajeSEP">Mensaje
                                    </label>
                                </div>
                            </div>
                            <div class="col-xs-12 push-15t">
                                <button class="btn btn-block btn-sm btn-success btnDecargaRespuestaSEP" style="margin-bottom: 15px;"><i class="fa fa-cloud-download"></i> Descargar Respuesta</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Fin Modal Consulta Resultados PRUEBAS-->

        <!-- Modal Cancelar título electrónico -->
        <div class="modal fade" id="modal-cancelarT" tabindex="-1" role="dialog" aria-hidden="true" style="overflow-x: hidden; overflow-y: auto;">
            <div class="modal-dialog modal-dialog modal-dialog-popin mymodal nomini">
                <div class="modal-content" id="modal-Cancelacion-draggable">
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button onclick="$('#modal-cancelarT').modal('hide')" id="btnCerrarModal" type="button"><i class="si si-close resetForm"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitleCancelarT"></h3>
                        </div>
                        <form class="form-horizontal" name="formCancelarTitulo" id="formCancelarTitulo">
                            <div class="block-content" style="overflow: inherit;">
                                <div class="form-group">
                                    <div class="col-lg-3 col-xs-12"  id="divFolioControl">
                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divFolioControlIn">
                                            <input class="form-control js-maxlength" type="text" id="txtFolioControlCancelar" name="txtFolioControlCancelar" maxlength="50" disabled="disabled">
                                            <label for="txtFolioControlCancelar"><span class="text-danger" >&#9642;</span> Folio Control</label>
                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-9 col-xs-12">
                                        <div class="form-material form-material-primary">
                                            <select class="form-control" id="lstMotivosCan" name="lstMotivosCan" disabled>
                                                <option></option>
                                            </select>
                                            <label for="lstMotivosCan"><span class="text-danger" >&#9642;</span> Motivo Cancelación</label>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 push-15-t">
                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">
                                            <textarea class="form-control" id="txtMensajeCancelacion" name="txtMensajeCancelacion" rows="3" disabled="disabled"></textarea>
                                            <label for="txtMensajeCancelacion">Mensaje de respuesta</label>
                                        </div>
                                    </div>
                                </div>
                                <p class="text-muted font-s12 center-block">
                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.
                                </p>
                            </div>

                            <div class="block-content" style="overflow-x:inherit; margin-top: -25px;">
                                <div class="form-horizontal">
                                    <div class="form-group">
                                        <div class="col-xs-12">
                                            <button class="btn btn-sm btn-success pull-right" type="button" id="btnCancelarTituloElectronico" name="btnCancelarTituloElectronico"><i class="si si-plus push-5-r"></i>Enviar solicitud de cancelación</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <!-- FIN Modal Cancelar título electrónico -->


        <!-- Modal Cancelar título electrónico -->
        <div class="modal fade" id="modal-llenarLibroFoja" tabindex="-1" role="dialog" aria-hidden="true" style="overflow-x: hidden; overflow-y: auto;">
            <div class="modal-dialog modal-dialog modal-md modal-dialog-popin mymodal nomini">
                <div class="modal-content" id="modal-LibroFoja-draggable">
                    <div class="block block-themed block-transparent remove-margin-b">
                        <div class="block-header bg-primary-dark">
                            <ul class="block-options">
                                <li>
                                    <button onclick="$('#modal-llenarLibroFoja').modal('hide')" id="btnCerrarModal2" type="button"><i class="si si-close resetForm"></i></button>
                                </li>                                
                            </ul>
                            <h3 class="block-title" id="modaltitleFojaLibro">Libro y Foja del Título</h3>
                        </div>
                        <form class="form-horizontal" name="formLibroFoja" id="formLibroFoja">
                            <div class="block-content" style="overflow: inherit;">
                                <div class="form-group">
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" id="divTxtMatriculaLF">
                                        <div class="form-material form-material-primary input-group floating" id="divTxtMatriculaLFIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtMatriculaLF" name="txtMatriculaLF" maxlength="20" autoComplete="off">
                                            <label for="txtMatriculaLF">Matrícula del Alumno</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" id="divTxtNombreLF">
                                        <div class="form-material form-material-primary input-group floating" id="divTxtNombreLFIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtNombreLF" name="txtNombreLF" maxlength="300" autoComplete="off">
                                            <label for="txtNombreLF">Nombre del Alumno</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-8 col-md-8 col-sm-12 col-xs-12" id="divTxtCarreraALF">
                                        <div class="form-material form-material-primary input-group floating" id="divTxtCarreraALFIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtCarreraALF" name="txtCarreraALF" maxlength="500" autoComplete="off">
                                            <label for="txtCarreraALF">Carrera</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12" id="divTxtFolioLF">
                                        <div class="form-material form-material-primary input-group floating" id="divTxtFolioLFIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtFolioLF" name="txtFolioLF" maxlength="40" autoComplete="off">
                                            <label for="txtNombreLF">Folio Título</label>
                                            <span class="input-group-addon "><i class="fa fa-font"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" id="divTxtLibroTitulo">
                                        <div class="form-material form-material-primary input-group floating" id="divTxtLibroTituloIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtLibroTitulo" name="txtLibroTitulo" maxlength="50" autoComplete="off">
                                            <label for="txtLibroTitulo"><span class="text-danger ">▪</span> Libro</label>
                                            <span class="input-group-addon "><i class="fa fa-book"></i></span>
                                        </div>
                                    </div>
                                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12" id="divTxtFojaTitulo">
                                        <div class="form-material form-material-primary input-group floating" id="divTxtFojaTituloIn">
                                            <input class="form-control js-maxlength notAllowed" type="text" id="txtFojaTitulo" name="txtFojaTitulo" maxlength="50" autoComplete="off">
                                            <label for="txtFojaTitulo"><span class="text-danger ">▪</span> Foja</label>
                                            <span class="input-group-addon "><i class="fa fa-file-text-o"></i></span>
                                        </div>
                                    </div>
                                </div>
                                <p class="text-muted font-s12 center-block">
                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.
                                </p>
                            </div>

                            <div class="block-content" style="overflow-x:inherit; margin-top: -25px;">
                                <div class="form-horizontal">
                                    <div class="form-group">
                                        <div class="col-xs-12">
                                            <button class="btn btn-sm btn-success pull-right" type="button" id="btnLlenarLibroFoja" name="btnLlenarLibroFoja"><i class="fa fa-save push-5-r"></i>Guardar información</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <!-- FIN Modal Cancelar título electrónico -->

        <input type="hidden" value="0" id="txtEdition">
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
        <script src="../assets/js/plugins/Validations/ValidacionesTETitulos.js?v=1.9"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>
        <script>

                                        jQuery(function () {
                                            // Init page helpers (BS Datepicker + BS Datetimepicker + BS Colorpicker + BS Maxlength + Select2 + Masked Input + Range Sliders + Tags Inputs plugins)
                                            App.initHelpers(['maxlength', 'select2', 'tags-inputs', 'datepicker', 'masked-inputs', 'notify']);
                                        });
        </script>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
        <input id="txtModulo" name="txtModulo" value="Titulos" style="display: none" disabled="disabled">
        <input id="txtTituloCancelar" name="txtTituloCancelar" value="" style="display: none" disabled="disabled">
        <!--<textarea id="txtBase64" name="txtBase64" style="display: block"></textarea>-->
    </body>
</html>