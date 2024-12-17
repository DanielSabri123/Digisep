/*
 * @ClickEscolar 0.1 20/09/16
 *  
 * Copyright 2016 Grupo Inndex. All rights reserved.
 * Grupo Inndex/CONFIDENTIAL Use is subject to license terms.
 */
/**
 
 * @author Arturo Sanchez
 * @author David Pérez Negrete
 * @version 0.1, 29/09/16
 * @since 0.1
 */
$(document).ready(function () { 
    /**
     * @function efecto de arrastre
     * @author Arturo Sánchez
     * @version 0.1, 30/10/16
     * @since 0.1
     * @description funcion draggable para los modals se llaman dos parametros el 
     * elemento e (que es el botón al que se da click para arrastrar) para arrastrar y el elemento div al que se le aplicará el efecto 
     * IMPORTANTE!!! los botones de nuevo, editar y mostrar tienen que restablecer la posición original 
     * del modal para evitar problemas en el posicionamiento
     */
    var tipoGrafica = "bar";
    var barChart;

    color();
    function handle_mousedown(e, div) {
        window.drag = {};
        drag.pageX0 = e.pageX;
        drag.pageY0 = e.pageY;
        drag.elem = div;
        drag.offset0 = $(div).offset();
        function handle_dragging(e) {
            var left = drag.offset0.left + (e.pageX - drag.pageX0);
            var top = drag.offset0.top + (e.pageY - drag.pageY0);
            $(drag.elem).offset({top: top, left: left});
        }
        function handle_mouseup(e) {
            $('body').off('mousemove', handle_dragging).off('mouseup', handle_mouseup);
        }
        $('body').on('mouseup', handle_mouseup).on('mousemove', handle_dragging);

    }
    /**
     * @function para cerrar sidebar
     * @author Arturo Sánchez
     * @version 0.1, 10/11/16
     * @since 0.1
     * @description funcion para cerrar el sidebar cuando se se da click fuera del menu
     */
    $(document).mouseup(function (e)
    {
        var container = $("#sidebar");

        if (!container.is(e.target) // if the target of the click isn't the container...
                && container.has(e.target).length === 0) // ... nor a descendant of the container
        {

            if (e.target.id == 'ToggleMenu' || e.target.id == 'ToggleMenui') {

                $('#backdrop').removeAttr('style');
                $('#backdrop').attr('style', 'z-index: 1042;');
            } else {
                if (e.target.id == 'backdrop') {
                    var $windowW = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;

                    if ($windowW > 991) {
                        $('#page-container').removeClass('sidebar-o');
                    } else {
                        $('#page-container').removeClass('sidebar-o-xs');
                    }
                    $('#backdrop').attr('style', 'display: none; z-index: 1042;');
                }
            }
        }
    });

    LoadComponents();
    function LoadComponents() {
        $.ajax({
            url: '../Transporte/queryCDash.jsp',
            data: '&txtBandera=1',
            type: 'post',
            success: function (resp) {
                if (resp.includes("sessionOff")) {
                    window.location.replace("../Generales/LogIn.jsp");
                }
                var RespSp = resp.split('||');
                $('#ulAvisos').html(RespSp[0] + "<br class='hidden-xs'>");
                $('#ulBitacoras').html(RespSp[1]);
                $('#ulEventos').html(RespSp[2]);
                $('#lblSolicitudes').html("<span class='h2 text-muted'></span>"+RespSp[3]);
                // Agregar la gráfica relacionada con financiero
                crearGrafica();
                initSlider();
                $('#blockModalAV').hide();
                $('#blockModalFN').hide();
                setTimeout(function () {
                    accionesWindow();
                }, 500);
            }
        });
    }

    function initSlider() {
        $('.js-slider').slick('unslick');
        $('.js-slider').slick({
            dots: true,
            autoplay: true,
            autoplaySpeed: '5000',
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: false
        });
    }

    $("#ulAvisos").on("click", ".Aviso", function () {

        swal($(this).find('.labels').text(), $(this).find('input').val(), "info");
        $('.note-video-clip').attr('width', '440');
        $('.note-video-clip').attr('height', '300');
    });

    $('#ulEventos').on('click', '.Evento', function () {
        var foto = $(this).find('.foto').text();
        if (foto != 'null' && foto != null && foto != '') {
            foto = "<img class='img-responsive' src='fakepath/" + $(this).find('.foto').text() + "' />";
        } else {
            foto = "";
        }

        swal({
            title: $(this).find('.labels').text(),
            html: "<div class='push-5-t'><h2 style='color: " + $(this).find('.color').text() + "'><span><i class='fa fa-calendar fa-3x'></i></span></h2>\n\
                    <div class='swal2-content'>\n\
                    <div class='block-content text-center'>\n\
                    <p style='font-size: 20px;'>" + $(this).find('input').val() + "</p>" + foto +
                    "</div>\n\
                    <p> Del " + $(this).find('.fechaInicio').text() + " al " + $(this).find('.fechaFin').text() + "</p>\n\
                    </div></div>",
            animation: true
        });
        $('.note-video-clip').attr('width', '440');
        $('.note-video-clip').attr('height', '300');
    });


    setInterval(function () {
        $('.Iload').removeClass('fa-spin').addClass('fa-spin');
        LoadComponents();
        setTimeout(function () {
            $('.Iload').removeClass('fa-spin');
        }, 2000);
    }, 600000);


    /**
     * Comment
     */
    function crearGrafica() {
        var hoy = new Date();
        var anio = hoy.getFullYear();
        var graficaIngresos = document.getElementById("graficaIngresos");

        Chart.defaults.global.defaultFontFamily = "Arial";
        Chart.defaults.global.defaultFontSize = 18;

        var graficaData = {
            data: [2544, 2507, 3540, 1427, 2243, 3514, 3133, 1326, 0],
            backgroundColor: [
                'rgba(110, 197, 79,0.50)',
                'rgba(243, 183, 96,0.50)',
                'rgba(255, 108, 157, 0.50)',
                'rgba(110, 197, 79,0.50)',
                'rgba(243, 183, 96,0.50)',
                'rgba(255, 108, 157, 0.50)',
                'rgba(110, 197, 79,0.50)',
                'rgba(243, 183, 96,0.50)',
                'rgba(255, 108, 157, 0.50)'],
            borderWidth: 2,
            hoverBorderWidth: 0
        };

        var chartOptions = {
            scales: {
                yAxes: [{
                        barPercentage: 0.5
                    }]
            },
            elements: {
                rectangle: {
                    borderSkipped: 'left'
                }
            },
            responsive: true
        };

        barChart = new Chart(graficaIngresos, {
            type: tipoGrafica,
            data: {
                labels: ["Abr", "May", "Jun", "Jul", "Ago", "Sept", "Oct", "Nov", "Dic"],
                datasets: [graficaData]
            },
            options: {
                chartOptions,
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    position: "bottom",
                    text: anio
                }
            }
        });
    }

    function accionesWindow() {
        $(window).resize(function () {
            var ancho = $(window).width();
            var altoCont = $('#grfFinanzas').height();
            barChart.destroy();
            if (altoCont >= 442) {
                $('#densityChart').attr('height', '430');
            } else {
                $('#densityChart').attr('height', '440');
            }

            if (ancho <= 768) {
                $('#blockGraficaFin').attr('style', 'min-height: 400px;');
            } else {
                $('#blockGraficaFin').attr('style', 'min-height: 540px;');
            }
            crearGrafica();
        });

        $(window).on('load', function () {
            var ancho = $(window).width();
            if (ancho <= 768) {
                $('#blockGraficaFin').attr('style', 'min-height: 400px;');
            } else {
                $('#blockGraficaFin').attr('style', 'min-height: 540px;');
            }
        });
    }

    $('.ClassPlusZoom').attr('disabled', false);
    $('.ClassMinusZoom').attr('disabled', false);
    function color() {
        var tema = $('#theme').val();
        switch (tema.toString().replace(".min.css", "")) {
            case "default":
                $('.finanzas').attr("style", "border-left: 3px solid #5c90d2;");
                break;
            case "amethysy":
                $('.finanzas').attr("style", "border-left: 3px solid #a48ad4;");
                break;
            case "city":
                $('.finanzas').attr("style", "border-left: 3px solid #ff6b6b;");
                break;
            case "flat":
                $('.finanzas').attr("style", "border-left: 3px solid #44b4a6;");
                break;
            case "modern":
                $('.finanzas').attr("style", "border-left: 3px solid #14adc4;");
                break;
            case "smooth":
                $('.finanzas').attr("style", "border-left: 3px solid #ff6c9d;");
                break;
        }
    }
    /*-------------------------------------------------------------------*/
});
