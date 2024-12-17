<%-- 
    Document   : Alumnos
    Created on : 4/12/2018, 11:11:42 AM
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
    String Id_SuarioLog = "" + sessionOk.getAttribute("Id_Usuario");
    String Menu = "" + sessionOk.getAttribute("Menu");
    String genero = "" + sessionOk.getAttribute("Genero");
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

        <title>Digi-SEP - Alumnos</title>

        <meta name="description" content="Digi-SEP - sistema de titulos, cédulas y certificados electrónicos">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <!-- Icons -->
        <!-- The following icons can be replaced with your own, they are used by desktop and mobile browsers -->
        <!-- END Icons -->
        <!-- Stylesheets -->
        <!-- Web fonts -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400italic,600,700%7COpen+Sans:300,400,400italic,600,700">

        <!-- Bootstrap and OneUI CSS framework -->
        <link rel="stylesheet" href="../assets/js/plugins/bootstrap-datepicker/bootstrap-datepicker3.min.css">
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
                /**width: 16.67%;**/
                border: 1px solid black;
                max-width:300px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                margin:0 5px 0 5px; padding:0;
                cursor: pointer;
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
                    </li>
                    --%>
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
                            <div class="col-lg-3 col-sm-3 col-md-3 block-content hidden-xs" style="min-height: 115px;">
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
                            <div class="col-lg-6 col-xs-12 col-md-6 block-content text-center hidden-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white push-10-l">
                                    Alumnos
                                </h1>
                                <h3 class="h5 text-white visible-xs text-left"><em>Hola</em></h3>
                                <h2 class="h4 font-w600 text-white push-5 visible-xs text-left"><%=NombreC%></h2>
                            </div>
                            <div class="col-sm-6 block-content text-center visible-sm" style="min-height: 115px;">
                                <h1 class="h1 font-w400 text-white">
                                    Alumnos
                                </h1>
                            </div>
                            <div class="col-lg-3 col-sm-3 col-md-3 block-content hidden-xs" style="min-height: 115px;">

                            </div>
                        </div>
                    </div>
                    <!-- Overview -->

                    <div id="fullContent" class="push-20-t">
                        <div class="col-lg-6 col-xs-12 push-10-t content">
                            <div class="panel-group">
                                <div class="panel panel-primary">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">
                                            <a href="#filtrosTabla">Área de filtros</a>
                                        </h4>
                                    </div>
                                    <div id="filtrosTabla" class="panel">
                                        <div class="panel-body">
                                            <div class="col-lg-10 col-xs-12" id="DivLstCarreras">
                                                <%--
                                                SE DEJA EL -webkit-box pues si se toma el style propio de boostrap se desborda la lista
                                                --%>
                                                <div class="input-group" style="display: -webkit-box !important">
                                                    <div class="form-material form-material-primary" id="DivLstCarrerasIn">
                                                        <select class="form-control" id="lstCarreras" name="lstCarreras">
                                                        </select>
                                                        <label for="lstCarreras"> Carrera</label>
                                                    </div>
                                                    <span class="input-group-btn">
                                                        <button class="btn btn-primary btn-sm" type="button" id="btnFiltrarAlumnos" style="margin-top: 3px;"><i class="fa fa-search"></i></button>
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>                            
                        </div>
                        <div class="col-lg-6 col-xs-12" style="margin-top: 40px;">
                            <!-- Info Alert -->
                            <div class="alert alert-info alert-dismissable">
                                <p>SELECCIONE UNA OPCIÓN DE LA LISTA  DE CARRERAS PARA CARGAR LA INFORMACIÓN</p>
                            </div>
                            <!-- END Info Alert -->
                        </div>
                        <div class="col-xs-12 push-10-t">
                            <div class="pull-right">
                                <ul class="block-options">
                                    <li id="liBtnNuevo">

                                        <button id="btnAgregarAlumno" data-toggle="modal"  type="button" class="btn  text-success"><i class="fa fa-arrow-right"></i> Nuevo</button>
                                    </li>

                                </ul>
                                <br>
                                <br>
                                <div>

                                    <button class="btn btn-primary push-5-r btn-sm btn-rounded" id="btnDescargarFormato"><i class="si si-cloud-download"></i> <a class='text-white' download href="../../Importaciones/ImportarAlumnos.xlsx"> Descargar formato</a></button>
                                    <button class="btn btn-primary btn-sm btn-rounded" id="btnImportarCarrerasExcel"><i class="fa fa-file-excel-o"></i> Importar registros</button>
                                </div>
                            </div>
                        </div>

                        <div style="display:none;">
                            <form class="form-horizontal" id="FormAlumno" name="FormAlumno">
                                <input style="display: none;" type="text" id="txtBandera" name="txtBandera" value="">
                                <div class="form-group">
                                    <input style="display: none;" class="form-control" type="file" id="fileAlumnos" name="fileAlumnos" title="Cargar Alumnos">
                                </div>
                                <div class="form-group">
                                    <button class="btnAccionesAlumno" style="display:none;" type="submit" id="importarArchivo" name="importarArchivo" onclick="$('#txtBandera').val('excel');"></button>
                                </div>
                            </form>
                        </div>
                        <div class="block">
                            <div class="block-content">
                                <div id="DivTblAlumnos">
                                    <table class="table table-bordered table-condensed table-striped js-dataTable-full-pagination-Fixed" style="width:100%;"  id="tblAlumnos">
                                        <thead class="bg-primary-dark" style="color: white;">
                                            <tr>
                                                <th class="text-center" style="display:none;">IdAlumno</th>
                                                <th class="text-center" style="display:none;">Calificaciones</th>
                                                <th class="text-center" style="display:none;">A. Materno</th>
                                                <th class="text-center" style="display:none;">Curp</th>
                                                <th class="text-center" style="display:none;">Generación</th>
                                                <th class="text-center" style="display:none;">Sexo</th>
                                                <th class="text-center" style="display:none;">Fecha inicio</th>
                                                <th class="text-center" style="display:none;">Fecha fin</th>
                                                <th class="text-center" style="display:none;">Correo</th>

                                                <th class="text-center">Matrícula</th>
                                                <th class="text-center">Nombre(s)</th>
                                                <th class="text-center">A. Paterno</th>
                                                <th class="text-center hidden-sm hidden-xs">Id Carrera</th>
                                                <th class="text-center hidden-sm hidden-xs">Carrera</th>
                                                <th class="text-center hidden-sm hidden-xs">F. Nacimiento</th>
                                                <th class="text-center">Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody id="tblBodyAlumnos">
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

            <!-- Inicio Modal Alumnos -->
            <div class="modal fade" id="modal-alumnos" tabindex="-1" role="dialog" aria-hidden="true" style="overflow-x: hidden; overflow-y: auto;">
                <div class="modal-dialog modal-dialog modal-lg modal-dialog-popin mymodal nomini">
                    <div class="modal-content" id="modal-alumnos-draggable">
                        <div class="block block-themed block-transparent remove-margin-b">
                            <div class="block-header bg-primary-dark">
                                <ul class="block-options">
                                    <li>
                                        <button id="btnCerrarModal" type="button"><i class="si si-close resetForm"></i></button>
                                    </li>                                
                                </ul>
                                <h3 class="block-title" id="modaltitle"></h3>
                            </div>
                            <form class=" form-horizontal push-10-t push-10" id="formAlumnos" name="formAlumnos">
                                <input style="display: none;" id="bandera" name="bandera" value=""/>
                                <input style="display: none;" id="ID_Alumno" name="ID_Alumno" value=""/>
                                <input style="display: none;" id="ID_Cambio" name="ID_Cambio" value=""/>
                                <input style="display: none;" id="nombre_select_carrera" name="nombre_select_carrera" value=""/>
                                <select type="hidden" style="display: none;"   id="lstCarreraAlumnoEstatico"></select>
                                <select type="hidden" style="display: none;"   id="lstSexoAlumnoEstatico"><option value=""></option>
                                    <option value="H">Hombre</option>
                                    <option value="M">Mujer</option></select>
                                <div class="block">
                                    <div class="block-content" style="overflow-y:hidden;">
                                        <div class="form-group" style="padding-top: 10px;">
                                            <div class="col-xs-12 col-lg-3 col-md-3 col-sm-6"  id="divMatriculaAlumno">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divMatriculaAlumnoIn">
                                                    <input class="form-control noPaste js-maxlength notAllowed" type="text" id="txtMatriculaAlumno" name="txtMatriculaAlumno" maxlength="20">
                                                    <label for="txtMatriculaAlumno"><span class="text-danger" >&#9642;</span> Matricula</label>
                                                    <span class="input-group-addon hidden-xs"><i class="si si-badge"></i></span>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-lg-3 col-md-3 col-sm-6"  id="divNombreAlumno">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divNombreAlumnoIn">
                                                    <input class="form-control noPaste js-maxlength notAllowed" type="text" id="txtNombreAlumno" name="txtNombreAlumno" maxlength="100">
                                                    <label for="txtNombreAlumno"><span class="text-danger" >&#9642;</span> Nombre(s)</label>
                                                    <span class="input-group-addon hidden-xs"><i class="fa fa-wpforms"></i></span>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-lg-3 col-md-3 col-sm-6"  id="divApaternoAlumno">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divApaternoAlumnoIn">
                                                    <input class="form-control noPaste js-maxlength notAllowed" type="text" id="txtApaternoAlumno" name="txtApaternoAlumno" maxlength="100">
                                                    <label for="txtApaternoAlumno"><span class="text-danger" >&#9642;</span> Apellido Paterno</label>
                                                    <span class="input-group-addon hidden-xs"><i class="fa fa-wpforms"></i></span>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-lg-3 col-md-3 col-sm-6"  id="divAmaternoAlumno">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divAmaternoAlumnoIn">
                                                    <input class="form-control noPaste js-maxlength notAllowed" type="text" id="txtAmaternoAlumno" name="txtAmaternoAlumno" maxlength="100">
                                                    <label for="txtAmaternoAlumno">Apellido Materno</label>
                                                    <span class="input-group-addon hidden-xs"><i class="fa fa-wpforms"></i></span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group">    
                                            <div class="col-xs-12 col-lg-3 col-md-3 col-sm-6" id="divCurpAlumno">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divCurpAlumnoIn">
                                                    <input class="form-control js-maxlength noPaste JustLetters notAllowed" type="text" id="txtCurpAlumno" name="txtCurpAlumno"  maxlength="18">
                                                    <label for="txtCurpFirmante"><span class="text-danger" >&#9642;</span> CURP</label>
                                                    <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-lg-3 col-md-3 col-sm-6" id="divGeneracionAlumno">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divGeneracionAlumnoIn">
                                                    <input class="form-control js-maxlength noPaste JustLetters notAllowed" type="text" id="txtGeneracionAlumno" name="txtGeneracionAlumno"  maxlength="80">
                                                    <label for="txtGeneracionAlumno"><span class="text-danger" >&#9642;</span> Generación</label>
                                                    <span class="input-group-addon hidden-xs"><i class="si si-calendar"></i></span>
                                                </div>
                                            </div>  
                                            <div class="col-xs-12 col-lg-4 col-md-4 col-sm-6" id="divCarreraAlumno">
                                                <div class="form-material form-material-primary " id="divCarreraAlumnoIn">
                                                    <label for="lstCarreraAlumno"><span class="text-danger" >&#9642;</span> Carrera</label>
                                                    <select class="form-control selectOption" id="lstCarreraAlumno" style="width: 100%;" name="lstCarreraAlumno">

                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-lg-2 col-md-2 col-sm-6" id="divSexoAlumno">
                                                <div class="form-material form-material-primary " id="divSexoAlumnoIn">
                                                    <label for="lstSexoAlumno"><span class="text-danger" >&#9642;</span> Sexo</label>
                                                    <select class="form-control selectOption" id="lstSexoAlumno" style="width: 100%;" name="lstSexoAlumno">

                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group">    

                                            <div class="col-xs-12 col-lg-3 col-md-3" id="divFechaInicioCarreraFirm">
                                                <div class="form-material form-material-primary input-group floating" id="divFechaInicioCarreraIn">
                                                    <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaInicioCarrera" name="txtFechaInicioCarrera" type="text" data-date-format="dd-mm-yyyy">
                                                    <label for="txtFechaInicioCarrera"><span class="text-danger ">▪</span> Fecha de inicio de la carrera</label>
                                                    <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                                </div>
                                            </div>

                                            <div class="col-xs-12 col-lg-3 col-md-3" id="divFechaFinCarrera">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divFechaFinCarreraIn">
                                                    <input class="js-datepicker js-masked-date-dash form-control" type="text" id="txtFechaFinCarrera" name="txtFechaFinCarrera" data-date-format="dd-mm-yyyy">
                                                    <label for="txtFechaFinCarrera"><span class="text-danger ">▪</span> Fecha fin de la carrera</label>
                                                    <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
                                                </div>
                                            </div>
                                            <div class="col-xs-12 col-lg-3 col-md-3 col-sm-6" id="divCorreoAlumno">
                                                <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divCorreoAlumnoIn">
                                                    <input class="form-control js-maxlength noPaste JustLetters notAllowed" type="email" id="txtCorreoAlumno" name="txtCorreoAlumno" maxlength="100">
                                                    <label for="txtCorreoAlumno"><span class="text-danger" >&#9642;</span> Correo</label>
                                                    <span class="input-group-addon hidden-xs"><i class="fa fa-envelope"></i></span>
                                                </div>
                                            </div> 

                                            <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12" id="divFechaNac">
                                                <div class="form-material form-material-primary input-group floating" id="divFechaNacIn">
                                                    <input class="js-datepicker js-masked-date-dash form-control" id="txtFechaNac" name="txtFechaNac" type="text" data-date-format="dd-mm-yyyy">
                                                    <label for="txtFechaNac"><span class="text-danger ">▪</span> Fecha de nacimiento</label>
                                                    <span class="input-group-addon"><i class="fa fa-calendar-o"></i></span>
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
                                                <button class="btn btn-sm btn-primary pull-left" type="button" id="btnLimpiarAlumnoModal" name="btnLimpiarAlumnoModal"><i class="glyphicon glyphicon-erase push-5-r"></i>Limpiar</button>
                                            </div>
                                            <div class="col-xs-6">
                                                <button class="btn btn-sm btn-success pull-right btnAccionesAlumnos" type="submit" id="btnRegistrarAlumno" name="btnRegistrarAlumno" onclick="$('#bandera').val('3');"><i class="si si-plus push-5-r"></i>Añadir</button>
                                                <button class="btn btn-sm btn-success pull-right btnAccionesAlumnos" type="submit" id="btnUpdateAlumno" onclick="$('#bandera').val('4');" name="btnUpdateAlumno"><i class="fa fa-save push-5-r"></i>Guardar</button>
                                                <button class="btn btn-sm btn-success pull-right resetForm" style="margin-right: 10px;" type="button" id="btnCerrarAlumno" name="btnCerrarAlumno" onclick="$('#modal-alumnos').modal('hide');"><i class="si si-close push-5-r"></i>Cerrar</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Final Modal Alumnos -->

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

        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>
        <script src="../assets/js/plugins/datatables/jquery.dataTables.min.js"></script>
        <script src="../assets/js/pages/base_tables_datatables.js"></script>        
        <script src="../assets/js/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js"></script>
        <script src="../assets/js/plugins/masked-inputs/jquery.maskedinput.min.js"></script>
        <script src="../assets/js/plugins/sessionTimeOut/bootstrap-session-timeout.js"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesTEAlumnos.js?v=1.2"></script>
        <script src="../assets/js/plugins/Validations/ValidacionesCambiarContrasenia.js"></script>
        <script src="../assets/js/plugins/jquery-ui/jquery-ui.min.js"></script>
        <script src="../assets/js/plugins/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>


        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdrop"></div>
        <div class="modal-backdropMenu fade in " style="display: none; z-index: 1043;" id="backdropside"></div>
        <input id="txtModulo" name="txtModulo" value="Alumnos" style="display: none" disabled="disabled">
        <script>
                                                    jQuery(function () {
                                                        // Init page helpers (BS Datepicker + BS Datetimepicker + BS Colorpicker + BS Maxlength + Select2 + Masked Input + Range Sliders + Tags Inputs plugins)
                                                        App.initHelpers(['maxlength', 'datepicker', 'masked-inputs', 'notify']);
                                                    });
        </script>
    </body>
</html>