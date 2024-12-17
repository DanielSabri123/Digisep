/*
 * @ClickEscolar 0.1 19/10/16
 * 
 * Copyright 2016 Grupo Inndex. All rights reserved.
 * Grupo Inndex/CONFIDENTIAL Use is subject to license terms.
 */
/**
 
 * @author David Pérez Negrete
 * @version 0.1, 19/10/16
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
            //alert(e.target.id);
            if (e.target.id == 'ToggleMenu' || e.target.id == 'ToggleMenui') {
                //alert('dysplay');
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

    var path = (location.pathname.toString()).split('/');
    var lenght = path.length - 1;

    if (path[lenght] == ('Encuesta.jsp')) {
        $(document).ready(function () {
            scaleGeneralEncuesta();
        });

        $(window).on('resize', function () {
            scaleGeneralEncuesta();
        });

        function scaleGeneralEncuesta() {
            var height = $('#TablaGeneralEncuesta').width();

            if (parseInt(height) > 947) {
                $('#THAccionEncuesta').html('ACCION');
                $('#lblFechaInicioEncuesta').html('Fecha de inicio');
                $('#lblFechaFinEncuesta').html('Fecha de cierre');
            } else if (parseInt(height) == 298 || parseInt(height) < 298) {
                $('#lblFechaInicioEncuesta').html('F.inicio');
                $('#lblFechaFinEncuesta').html('F.cierre');
                $('#THAccionEncuesta').html('ACCIO');
            } else if (parseInt(height) > 298 && parseInt(height) < 947) {
                $('#THAccionEncuesta').html('ACCION');
                $('#lblFechaInicioEncuesta').html('Fecha de inicio');
                $('#lblFechaFinEncuesta').html('Fecha de cierre');
            }

        }
    }



    /*
     * nos aseguramos mediante la función validate() que los datos introducidos por el usuario sean los correctos
     * de ser así realizamos la inserción en la base de datos o de lo contrario la avisamos al usuario que la in-
     * formación es incorrcta.
     * También checamos las acciones de cada button del JSP para su correcta funcionalidad
     */




   



 



    /* 
     * @author David Pérez Negrete
     * @targetdocument Encuesta.jsp
     * @version 0.1, 11/11/16
     * @description funciones para validar la correcta entrada de datos y funciones que interactuen con el jsp.
     */

    $('#BtnNuevoEncuesta').click(function () {
        $('#modal-encuesta').modal('show');
        $('#TitleModalEncuesta').html('Nueva Encuesta');
    });

    $('#FormEncuesta').mouseup(function () {
        var TxtNombreEncuesta = $('#txtNomEncuesta').val();
        var nPreguntas = $('#txtNumeroPEncuesta').val();
        if (TxtNombreEncuesta == '') {

            $('#DivTxtNomEncuesta').removeClass('form-material-primary');
            $('#DivTxtNomEncuesta').addClass('form-material-danger');

            $('#AlerttxtNomEncuesta').html('Por favor completa este campo!');
            $("#DivTxtNomEncuesta").addClass('open');
            $("#txtNomEncuesta").focus();
            return false;
        } else {
            $('#DivTxtNomEncuesta').removeClass('form-material-danger');
            $('#DivTxtNomEncuesta').addClass('form-material-primary');
            $('#AlerttxtNomEncuesta').html('');
        }

        if (nPreguntas == '') {
            $('#DivTxtNumeroPEncuesta').removeClass('form-material-primary');
            $('#DivTxtNumeroPEncuesta').addClass('form-material-danger');

            $('#AlerttxtNumeroPEncuesta').html('Por favor completa este campo!');
            $("#DivTxtNumeroPEncuesta").addClass('open');
            $("#txtNumeroPEncuesta").focus();
            return false;
        } else {
            $('#DivTxtNumeroPEncuesta').removeClass('form-material-danger');
            $('#DivTxtNumeroPEncuesta').addClass('form-material-primary');
            $('#AlerttxtNumeroPEncuesta').html('');
        }
    });

    //Clase fuctión que al dar click en botones con esta clase realizara los procesos indicados.
    $('.ClassAccionesBtnEncuesta').click(function () {



        //Recolecta datos de form de uan nueva encuesta.
        var formTa2 = "";

        var TxtNombreEncuesta = $('#txtNomEncuesta').val();
        var nPreguntas = $('#txtNumeroPEncuesta').val();
        var TxtFechaInicioEncuesta = $('#txtFechaInicioEncuesta').val();
        var TxtFechaFinEncuesta = $('#txtFechaFinEncuesta').val();

        var RadioValor = $('input[name="RadioValEncuesta"]:checked', '#FormEncuesta').val();

        var numpages = Math.ceil(nPreguntas / 3);// se obtiene el total de preguntas definidas y se realiza una división entre 5
        // que es el numero de preguntas por cada pagina de la paginación.




        /* Validar que datos sean correcto o no se encuentren vacios */

        if (TxtNombreEncuesta == '') {

            $('#DivTxtNomEncuesta').removeClass('form-material-primary');
            $('#DivTxtNomEncuesta').addClass('form-material-danger');

            $('#AlerttxtNomEncuesta').html('Por favor completa este campo!');
            $("#DivTxtNomEncuesta").addClass('open');
            $("#txtNomEncuesta").focus();
            return false;
        } else {
            $('#DivTxtNomEncuesta').removeClass('has-error');
            $('#DivTxtNomEncuesta').addClass('form-material-primary');

            //Bloqueo de tab en tiempo necesario.
            $('#Tab2').addClass('active in');
            $('#Tab1').removeClass('active in');
            $('#li1').removeClass('active');
            $('#li1').addClass('inactive');
            $('#li2').removeClass('inactive');
            $('#li2').addClass('active');

            for (var i = 1; i <= nPreguntas; i++) {
                formTa2 += "<div id='DivR"+i+"'><ul class='nav nav-tabs' data-toggle='tabs' id='DivPre"+i+"'>                                                    <li class=''>                                                        <a href='#btabs-animated-slideup-home"+i+"'>Pregunta Abierta</a>                                                    </li>                                                    <li class='active'>                                                        <a href='#btabs-animated-slideup-profile"+i+"'>Pregunta Multiple</a>                                                    </li>                                                    <li class='pull-right '>                                                        <span class='label label-primary push-5-t'>Pregunta "+i+"</span>                                                    </li>                                                </ul>                                                <div class='block-content tab-content'>                                                    <div class='tab-pane fade fade-up' id='btabs-animated-slideup-home"+i+"'>                                                        <div class='col-sm-12'>                                                            <div class='form-material input-group form-material-primary floating open' id='DivTxtPreguntaAbierta"+i+"' style='margin-top: -1px;'>                                                                <input class='form-control' type='text' id='txtPreguntaAbierta"+i+"' name='txtPreguntaAbierta"+i+"'>                                                                <label for='txtPreguntaAbierta"+i+"' id='lblPreguntaAbierta"+i+"'>Pregunta "+i+"</label>	                                                                <span class='input-group-addon'><i class='fa fa-question hidden-xs'></i></span> 	                                                            </div>                                                         </div>                                                    </div>                                                    <div class='tab-pane fade fade-up active in' id='btabs-animated-slideup-profile"+i+"'>                                                        <div class='row'>                                                            <div class='col-sm-6'>                                                                <div class='block'>                                                                    <div class='block-content'>                                                                        <div class='form-material input-group form-material-primary floating' id='DivTxtPreguntaMulti"+i+"'>                                                                            <input class='form-control' type='text' id='txtPreguntaMulti"+i+"' name='txtPreguntaMulti"+i+"'>                                                                            <label for='txtPreguntaMulti"+i+"' id='lblPreguntaMulti"+i+"'>Pregunta "+i+" </label>                                                                            <span class='input-group-addon'><i class='fa fa-question hidden-xs'></i></span>                                                                        </div>                                                                     </div>                                                                </div>                                                            </div>                                                            <div class='col-sm-6'>                                                                <div class='block'>                                                                    <div class='block-content'>                                                                        <div class='input-group'>                                                                            <div class='form-material form-material-primary '>                                                                                <label class='lblChosen'>N° Res</label>                                                                                <input class='form-control' type='text' id='txtNRespuestasPre_"+i+"' name='txtNRespuestasPre_"+i+"'>                                                                            </div>                                                                            <span class='input-group-btn ClassDoubleDowRespuesta' >                                                                                <button class='btn btn-primary ' type='button' value='btnGenerarR_"+i+"' id='BtnDow' name='BtnDow'  ><i class='fa fa-angle-double-down '></i></button>                                                                            </span>                                                                        </div>                                                                     </div>                                                                </div>                                                            </div>                                                        </div>                                                        <div class='row' id='DivGenerarRespuesta_"+i+"'>                                                        </div>                                                        <ul id='pagination-demo2' class='pagination-sm'>                                                        </ul>                                                    </div>                                                    <div class='tab-pane fade fade-up' id='btabs-animated-slideup-settings'>                                                        <h4 class='font-w300 push-15'>Settings Tab</h4>                                                        <p>Content slides up..</p>                                                    </div>                                                </div></div>";
                //formTa2 += "<div class='form-group' id='DivPre"+i+"'>   <div class='col-xs-12 col-lg-12 col-md-12'>      <div class='form-material input-group form-material-primary floating open' id='DivTxtPreguntaAbierta"+i+"' style='margin-top: -1px;' >		<input class='form-control' type='text' id='txtPreguntaAbierta"+i+"' name='txtPreguntaAbierta"+i+"'>		<label for='txtPreguntaAbierta"+i+"' id='lblPreguntaAbierta"+i+"'>Pregunta "+i+"</label>		<span class='input-group-addon'><i class='fa fa-question'></i></span> 	  </div>   </div></div>";
            }
            $('#divprueba').html(formTa2);
            formTa2 = "";

            $('#pagination-demo').twbsPagination({//fuction para crear la paginación.
                totalPages: numpages,
                visiblePages: 3,
                onPageClick: function (event, page) {// la var page, es el numero de pagina que se selecciona.
                    
                    for (var i = 1; i <= nPreguntas; i++) {
                        $('#DivR'+i+'').hide();
                    }
                    
                    for (var i = (3 * ((page - 1))) + 1; i <= (3 * (page)); i++) {
                        $('#DivR'+i+'').show();
                    }
                }
            });

        }

    });
    
   
    
    $('.ClassDoubleDowRespuesta').click(function (){
        alert('Hi!');
        var buttonval = this.value.split('_');
        
        var DivGRespuestas = "";
        var nRespuestas = "";
        
        nRespuestas = $("#txtNRespuestasPre_" + buttonval[1]).val();
        $('#DivGenerarRespuesta_'+ buttonval[1]).html("");
        for(var i = 1; i <= nRespuestas; i++){
            DivGRespuestas += "<div class='col-xs-12 col-lg-12 col-md-12' id='DivRes"+i+"'>                                                    <div class='form-material input-group form-material-primary floating open' id='DivTxtRespuestaEncuesta"+i+"'>                                                        <input class='form-control' type='text' id='txtRespuestaEncuesta"+i+"' name='txtRespuestaEncuesta"+i+"'>                                                        <label for='txtRespuestaEncuesta"+i+"' id='lblRespuestaEncuesta"+i+"'>Respuesta "+i+" </label>                                                        <span class='input-group-addon '><i class='fa fa-font hidden-xs'></i></span>                                                    </div>                                                </div>";
        }
        $('#DivGenerarRespuesta_'+ buttonval[1]).html(DivGRespuestas);
        DivGRespuestas = "";
        var numpages = Math.ceil(nRespuestas / 5);
        
        
        if($('#pagination-demo2').data("twbs-pagination")){
            $('.pagination').twbsPagination('destroy');
        }

        $('#pagination-demo2').twbsPagination({//fuction para crear la paginación.
                totalPages: numpages,
                visiblePages: 5,
                onPageClick: function (event, page) {// la var page, es el numero de pagina que se selecciona.
                    
                    for (var i = 1; i <= nRespuestas; i++) {
                        $('#DivRes'+i+'').hide();
                    }
                    
                    for (var i = (5 * ((page - 1))) + 1; i <= (5 * (page)); i++) {
                        $('#DivRes'+i+'').show();
                    }
                }
            });
            
    });
    
    


    /* ////////////////////////Fin Encuesta.jsp ////////////////////////////  */

});
