<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%
    HttpSession sessionOk = request.getSession();
    String mensajeLogin = "" + sessionOk.getAttribute("mensajeLogin");
    sessionOk.setAttribute("islocked", "no");
    session.invalidate();
    if (mensajeLogin.equals("null")) {
        mensajeLogin = "";
    }
%>

<%-- 
    Document   : LogIn
    Created on : 29-nov-2018, 12:00:00
    Author     : Célula de desarrollo
--%>

<!DOCTYPE html>
<html class="no-focus" lang="es">
    <head>
        <script src="../assets/js/core/jquery.min.js"></script>

        <meta charset="utf-8">

        <title>Digi-SEP</title>

        <meta name="description" content="Digi-SEP - sistema de titulos, cédulas y certificados electrónicos">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <!-- Icons -->
        <!-- The following icons can be replaced with your own, they are used by desktop and mobile browsers -->
        <link rel="shortcut icon" href="assets/img/favicons/favicon.png">

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

        <script src="../assets/js/core/bootstrap.min.js"></script>
        <script src="../assets/js/core/jquery.slimscroll.min.js"></script>
        <script src="../assets/js/core/jquery.scrollLock.min.js"></script>
        <script src="../assets/js/core/jquery.appear.min.js"></script>
        <script src="../assets/js/core/jquery.countTo.min.js"></script>
        <script src="../assets/js/core/jquery.placeholder.min.js"></script>
        <script src="../assets/js/core/js.cookie.min.js"></script>
        <script src="../assets/js/app.js"></script>

        <!-- Page JS Plugins -->
        <script src="../assets/js/plugins/jquery-validation/jquery.validate.min.js"></script>

        <!-- Page JS Code -->
        <script src="../assets/js/pages/base_pages_login.js"></script>

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
    </head>
    <body>
        <!-- Login Content -->
        <div id="divGeneral" class="bg-white pulldown bg-image">
            <div class="content content-boxed overflow-hidden">
                <div class="row">
                    <div class="col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4">
                        <div class="push-30-t push-50 animated fadeIn">
                            <!-- Login Title -->
                            <div class="text-center">
                                <img src="../assets/img/Digi-SepRPNG_a.png" alt="digi-sep" width="200">
                                <!--<i class="fa fa-2x fa-circle-o-notch text-primary"></i>
                                <p class="text-muted push-15-t">Titulos Electrónicos</p>-->
                            </div>
                            <!-- END Login Title -->

                            <!-- Login Form -->
                            <!-- jQuery Validation (.js-validation-login class is initialized in js/pages/base_pages_login.js) -->
                            <!-- For more examples you can check out https://github.com/jzaefferer/jquery-validation -->
                            <form class="js-validation-login form-horizontal" action="../Transporte/queryCLogin.jsp" method="post" autocomplete="off" id="formLogIn">
                                <input type="text" style="display: none;" id="txtCarpeta" name="txtCarpeta">
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <div class="form-material form-material-primary floating">
                                            <input class="form-control"  type="text" id="txtLogUsuario" name="txtLogUsuario" style="width: 100%; background: #d5ebf1; height: 35px; border-radius: 25px; padding-left: 30px;">
                                            <label for="txtLogUsuario" style="color:black">&nbsp;&nbsp;&nbsp;Usuario</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <div class="form-material form-material-primary floating">
                                            <input class="form-control" type="password" id="txtLogPass" name="txtLogPass" style="width: 100%; background: #d5ebf1; height: 35px; border-radius: 25px; padding-left: 30px;">
                                            <label for="txtLogPass" style="color: black">&nbsp;&nbsp;&nbsp;Contraseña</label>
                                        </div>
                                    </div>
                                </div>
                                <div id="txtClaveGen-error" class="help-block text-right animated fadeInDown" style="color: #ff2000;"><%=mensajeLogin%></div>
                                
                                     <div class="form-group">
                                     <div class="col-xs-12">
                                     <div class="font-s13 text-center push-5-t">
                                     <a href="RecoverPassword.jsp">¿Olvidaste
                                     tu contraseña?</a>
                                      </div>
                                     </div>
                                      </div>
                                     
                                
                                <div class="form-group push-30-t">
                                    <div class="col-xs-12 col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-4">
                                        <button class="btn btn-sm btn-block btn-primary" type="submit" onclick="$('#txtCarpeta').val(getCarpeta())">Iniciar Sesión</button>
                                    </div>
                                </div>
                            </form>
                            <!-- END Login Form -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- END Login Content -->

        <!-- Login Footer -->
        <div class="pulldown push-30-t text-center animated fadeInUp">
            <small class="text-muted">Grupo Inndex</small>
            <br/>
            <small class="text-muted">Digi-Sep :: Versión 1.10:1.032023</small>
        </div>
        <!-- END Login Footer -->

        <script>
            function getCarpeta() {
                var carpeta = location.href.split("/")[3];
                return carpeta;
            }
            function loadBG() {
                document.getElementById("divGeneral").style.backgroundImage = "url(../assets/img/FondoCertificadosInstitucionales.png)";
            }
            window.onload = loadBG();
        </script>
    </body>
</html>