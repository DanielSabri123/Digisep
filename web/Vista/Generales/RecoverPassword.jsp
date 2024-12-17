<%-- 
    Document   : RecoverPassword
    Created on : 30-nov-2018, 15:48:59
    Author     : Paulina
--%>

<%
    HttpSession sessionOk = request.getSession();
    String mensajeLogin = "" + sessionOk.getAttribute("mensajeLogin");
    sessionOk.setAttribute("islocked", "no");
    session.invalidate();
    if (mensajeLogin.equals("null")) {
        mensajeLogin = "";
    }
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--[if IE 9]>         <html class="ie9 no-focus" lang="en"> <![endif]-->
<!--[if gt IE 9]><!--> <html class="no-focus" lang="en"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">

        <title>Recuperar Contraseña Digi-SEP</title>

        <meta name="description" content="Digi-SEP - sistema de titulos, cédulas y certificados electrónicos - Recuperar Contraseña"">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <!-- Icons -->
        <!-- The following icons can be replaced with your own, they are used by desktop and mobile browsers -->

        <link rel="shortcut icon" href="../assets/img/Digi-SEP.png"/>


        <link rel="stylesheet" href="../assets/js/plugins/sweetalert/sweetalert2.css">
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

        <!-- You can include a specific file from css/themes/ folder to alter the default color theme of the template. eg: -->
        <!-- <link rel="stylesheet" id="css-theme" href="assets/css/themes/flat.min.css"> -->
        <!-- END Stylesheets -->
        <!-- Estilo del loader de la pagina recoveryPassword -->
        <style type="text/css">
            /*Loader Css*/
            .preloader {
                background-color: #fff;
                bottom: 0;
                height: 100%;
                left: 0;
                position: fixed;
                right: 0;
                top: 0;
                width: 100%;
                z-index: 9999;
                text-align: center;
                font-style:italic;
                color: whitesmoke;
            }
            .imgpreloader {
                margin: 0 auto;
                position: relative;
                top: 40%;
                
                -moz-transform: translateY(-50%);
                -webkit-transform: translateY(-50%);
                transform: translateY(-50%);
                width: 100%;
                text-align: center;
                z-index: 9999;
                -webkit-animation: ball-pulse-sync 2s 0s infinite


            }
            .lds-ellipsis {
                margin: 0 auto;
                position: relative;
                top: 50%;
                -moz-transform: translateY(-50%);
                -webkit-transform: translateY(-50%);
                transform: translateY(-50%);
                width: 100%;
                text-align: center;
                z-index: 9999;
            }
            .lds-ellipsis span {
                display: inline-block;
                width: 15px;
                height: 15px;
                border-radius: 50%;
                background: #00a5b5;
                -webkit-animation: ball-pulse-sync 1s 0s infinite ease-in-out;
                animation: ball-pulse-sync 1s 0s infinite ease-in-out;
            }
            .lds-ellipsis span:nth-child(1) {
                -webkit-animation:ball-pulse-sync 1s -.14s infinite ease-in-out;
                animation:ball-pulse-sync 1s -.14s infinite ease-in-out
            }
            .lds-ellipsis span:nth-child(4) {
                -webkit-animation:ball-pulse-sync 1s -70ms infinite ease-in-out;
                animation:ball-pulse-sync 1s -70ms infinite ease-in-out
            }
            @-webkit-keyframes ball-pulse-sync {
                33% {
                    -webkit-transform:translateY(10px);
                    transform:translateY(10px)
                }
                66% {
                    -webkit-transform:translateY(-10px);
                    transform:translateY(-10px)
                }
                100% {
                    -webkit-transform:translateY(0);
                    transform:translateY(0)
                }
            }
            @keyframes ball-pulse-sync {
                33% {
                    -webkit-transform:translateY(10px);
                    transform:translateY(10px)
                }
                66% {
                    -webkit-transform:translateY(-10px);
                    transform:translateY(-10px)
                }
                100% {
                    -webkit-transform:translateY(0);
                    transform:translateY(0)
                }
            }
        </style>
        <!-- Fin del CSS deñl loader -->



    </head>
    <body>


        <!-- Reminder Content -->
        <div class="bg-white pulldown bg-image" id="divGeneral">
            <div class="content content-boxed overflow-hidden">
                <div class="row">
                    <div class="col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4">
                        <div class="push-30-t push-20 animated fadeIn">
                            <!-- Reminder Title -->
                            <div class="text-center">
                                <i><img src="../assets/img/Digi-SepRPNG_a.png" alt="logo.png" width="150" height="150"></i>
                                <strong> <p class="">No te preocupes, nosotros te enviaremos un correo, especificando los pasos a seguir para entrar a la plataforma.</p></strong>
                            </div>
                            <!-- END Reminder Title -->
                            <!-- Reminder Form -->
                            <!-- jQuery Validation (.js-validation-reminder class is initialized in js/pages/base_pages_reminder.js) -->
                            <!-- For more examples you can check out https://github.com/jzaefferer/jquery-validation -->
                            <form class="js-validation-reminder form-horizontal push-30-t" action="" method="post" name="formRecoverPassword">
                                <input type="text" style="display: none;" id="txtCarpeta" name="txtCarpeta">
                                <div class="form-group">
                                    <div class="col-xs-12">
                                        <div class="form-material form-material-primary floating">
                                            <input class="form-control" type="email" id="email" name="reminder-email" style="width: 100%; background: #d5ebf1; height: 35px; border-radius: 25px; padding-left: 30px; font-weight: bold;" required="true">
                                            <label for="reminder-email">&nbsp;&nbsp;&nbsp;Ingresa tu Email</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-xs-12 col-sm-6 col-sm-offset-3 ">
                                        <button class="btn btn-sm btn-block btn-primary" type="submit" id="btnRecuperarContrasenia" onclick="$('#txtCarpeta').val(getCarpeta())">Recuperar Contraseña</button>
                                    </div>
                                </div>
                            </form>
                            <!-- END Reminder Form -->

                            <!-- Extra Links -->
                            <div class="text-center push-50-t">
                                <strong> <a href="LogIn.jsp">¿Ya tienes una cuenta? Inicia Sesión.</a></strong>
                            </div>
                            <!-- END Extra Links -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- END Reminder Content -->

        <div class="modal fade" tabindex="-1" id="myModal" role="dialog" style="padding-top: 80px; width: 100%;">
            <div class="modal-dialog">
                <!-- Modal content-->
                <div class="modal-content">
                    <div class="preloader" id="divLoaderRP">
                        <div class="imgpreloader" style="padding-bottom: 15px">
                            <span>
                                <img src="https://digisep.com/images/Digi-Sep.png" alt="Digi-SEP" width="100" height="100"></span>
                        </div>
                        <div class="lds-ellipsis">
                            <span></span>
                            <span></span>
                            <span></span>
                            <span></span>
                            <span></span>
                            <span></span>
                            <div style="padding-top: 15px">
                                <strong ><p >Enviando correo.... Espere, porfavor</p></strong>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>




        <!--  Footer -->
        <div class="pulldown push-30-t text-center animated fadeInUp">
            <small class="text-muted">Grupo Inndex</small>
            <br/>
            <small class="text-muted">Digi-Sep :: Versión 1.10:1.032023</small>
        </div>
        <!-- END  Footer -->

        <!-- OneUI Core JS: jQuery, Bootstrap, slimScroll, scrollLock, Appear, CountTo, Placeholder, Cookie and App.js -->
        <script src="../assets/js/core/jquery.min.js"></script>
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
        <script src="../assets/js/pages/base_pages_reminder.js"></script>

        <script src="../assets/js/plugins/Validations/ValidacionesRecuperarContrasenia.js" type="text/javascript"></script>
        <script src="../assets/js/plugins/sweetalert/sweetalert2.js" charset="UTF-8"></script>

        <script>
                                            function getCarpeta() {
                                                var carpeta = location.href.split("/")[3];

                                                return carpeta;
                                            }

                                            function loadBG() {
                                                document.getElementById("divGeneral").style.backgroundImage = "url(../assets/img/FondoCertificadosInstitucionales.png)";
                                                document.getElementById("divGeneral").style.backgroundImage = "url(../assets/img/FondoCertificadosInstitucionales.png)";
                                            }
                                            window.onload = loadBG();
        </script>


    </body>
</html>
