<%-- 
    Document   : Page404
    Created on : 14-ene-2019, 11:54:14
    Author     : Braulio Sorcia
--%>

<%
    String rol = request.getParameter("rol");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error 404 (No encontrado)</title>

        <meta charset="utf-8">

        <meta name="description" content="404 - Page Not Found">
        <meta name="author" content="Grupo Inndex">
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1.0">

        <%
            /**
             * RUTA ABSOLUTA DE LOS HREF Y SRC EN HTML /MÁS_LA_RUTA
             */
        %>
        <!-- Icons -->
        <!-- The following icons can be replaced with your own, they are used by desktop and mobile browsers -->
        <link rel="shortcut icon" href="/Titulos/Vista/assets/img/favicons/favicon.png">

        <link rel="icon" type="image/png" href="/Titulos/Vista/assets/img/favicons/favicon-16x16.png" sizes="16x16">
        <link rel="icon" type="image/png" href="/Titulos/Vista/assets/img/favicons/favicon-32x32.png" sizes="32x32">
        <link rel="icon" type="image/png" href="/Titulos/Vista/assets/img/favicons/favicon-96x96.png" sizes="96x96">
        <link rel="icon" type="image/png" href="/Titulos/Vista/assets/img/favicons/favicon-160x160.png" sizes="160x160">
        <link rel="icon" type="image/png" href="/Titulos/Vista/assets/img/favicons/favicon-192x192.png" sizes="192x192">

        <link rel="apple-touch-icon" sizes="57x57" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-57x57.png">
        <link rel="apple-touch-icon" sizes="60x60" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-60x60.png">
        <link rel="apple-touch-icon" sizes="72x72" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-72x72.png">
        <link rel="apple-touch-icon" sizes="76x76" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-76x76.png">
        <link rel="apple-touch-icon" sizes="114x114" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-114x114.png">
        <link rel="apple-touch-icon" sizes="120x120" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-120x120.png">
        <link rel="apple-touch-icon" sizes="144x144" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-144x144.png">
        <link rel="apple-touch-icon" sizes="152x152" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-152x152.png">
        <link rel="apple-touch-icon" sizes="180x180" href="/Titulos/Vista/assets/img/favicons/apple-touch-icon-180x180.png">
        <!-- END Icons -->

        <!-- Stylesheets -->
        <!-- Web fonts -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400italic,600,700%7COpen+Sans:300,400,400italic,600,700">

        <!-- Bootstrap and OneUI CSS framework -->
        <link rel="stylesheet" href="/Titulos/Vista/assets/css/bootstrap.min.css">
        <link rel="stylesheet" id="css-main" href="/Titulos/Vista/assets/css/oneui.css">

        <!-- You can include a specific file from css/themes/ folder to alter the default color theme of the template. eg: -->
        <!-- <link rel="stylesheet" id="css-theme" href="assets/css/themes/flat.min.css"> -->
        <!-- END Stylesheets -->
    </head>
    <body>
        <!-- Error Content -->
        <div class="content bg-white text-center pulldown overflow-hidden">
            <div class="row">
                <div class="col-sm-6 col-sm-offset-3">
                    <!-- Error Titles -->
                    <h1 class="font-w600 text-city animated flipInX">404</h1>
                    <h1 class="font-w300 text-city animated flipInX">¡Página no encontrada!</h1>
                    <h2 class="h3 font-w300 push-50 animated fadeInUp">Lo sentimos, pero la página que buscas fue removida, cambió de nombre o no está disponible en estos momentos.</h2>
                    <!-- END Error Titles -->

                    <!-- Search Form -->
                    <div class="col-xs-12">
                        <a href=javascript:history.back(-1);>
                            <span id="log" class="text-danger"><i class="fa fa-fort-awesome fa-4x"></i></span>
                            <br>
                            <span class="text-danger"><i>Regresar a la página anterior</i></span>

                        </a>
                    </div>
                    <div class="col-xs-12">
                        &nbsp;
                    </div>
                    <!-- END Search Form -->
                </div>
            </div>
        </div>
        <!-- END Error Content -->

        <!-- Error Footer -->

        <div class="content pulldown text-muted text-center">
            ¿Quieres decirnos que fue lo que pasó?<br>
            <a class="link-effect" href="javascript:void(0)">Déjanos un mensaje</a>
        </div>

        <!-- END Error Footer -->
        <script src="/Titulos/Vista/assets/js/core/jquery.min.js"></script>
    </body>
</html>
