$(document).ready(function () {
    $("#lstAlumnos").chosen({width: "100%", disable_search_threshold: 4});
    function initTable() {
        var tblCalificaciones = $(".js-dataTable-full-pagination-Fixed").dataTable({
            ordering: true,
            orderable: false,
            paging: true,
            searching: true,
            info: true,
            stateSave: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: 7}],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]],
            order: [[1, 'desc']],
            "columns": [
                null,
                {"width": "15%"},
                {"width": "20%"},
                {"width": "20%"},
                {"width": "15%"},
                {"width": "20%"},
                {"width": "5%"},
                {"width": "5%"}
            ]
        });
    }
    initTable();
    function LoadTable() {
        $.ajax({
            url: '../Transporte/queryCCalificaciones.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var indicador = resp.split("|")[0];
                if (indicador.toString().trim() === "success") {
                    $('#DivTblCalificaciones').html(resp.split("|")[1]);
                    $('[data-toggle="tooltip"]').tooltip({
                        container: 'body',
                        animation: false,
                        trigger: 'hover'
                    });
                    TableActions();
                    initTable();
                    setTimeout(function () {
                        $('#mainLoader').fadeOut();
                    });
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function () {
            }
        });
    }

    $('#btnImportarCalifsExcel').click(function () {
        $('#fileCalificaciones').click();
    });

    $('#fileCalificaciones').on('change', function () {
        var archivo = $(this).val();

        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.xls') || archivo.toString().endsWith('.xlsx')) {
                swal('¡Cargando información!', 'Se están registrando las calificaciones en el sistema', 'info').then(function () {
                    $('#loadAction').fadeIn();
                });
                $('#importarArchivoCalif').click();
            } else {
                swal('¡Upps!', 'Debes seleccionar el formato predeterminado con las calificaciones', 'warning');
                $('#fileCalificaciones').val('');
            }
        }
    });

    $('.btnAccionesCalificaciones').click(function () {
        $("form[name='FormCalificaciones']").submit(function (e) {
            e.preventDefault();
        }).validate({
            ignore: [],
            errorClass: 'help-block text-right animated fadeInDown',
            errorElement: 'div',
            errorPlacement: function (error, e) {
                jQuery(e).parents('.form-group > div').append(error);
            },
            highlight: function (e) {
                var elem = jQuery(e);
                elem.closest('.col-xs-6').removeClass('has-error').addClass('has-error');
                elem.closest('.help-block').remove();
            },
            success: function (e) {
                var elem = jQuery(e);
                elem.closest('.col-xs-6').removeClass('has-error');
                elem.closest('.help-block').remove();
            },
            rules: {
                fileCalificaciones: {
                    required: true,
                    extension: "xls|xlsx"
                }
            },
            messages: {
                'fileCalificaciones': {
                    required: function () {
                        swal('¡Upps!', 'Selecciona el archivo predeterminado con la información de los alumnos', 'warning');
                    },
                    extension: function () {
                        swal('¡Upps!', 'Selecciona el archivo excel con la información de los alumnos', 'warning');
                    }
                }
            },
            submitHandler: function (form) {

                var bandera = $('#txtBandera').val();
                $.ajax({
                    url: '../Transporte/queryCCalificaciones.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    success: function (resp) {
                        if (resp.toString().includes('success')) {
                            swal('¡Calificaciones agregadas!', 'La importación de las calificaciones se ha completado', 'success');
                            cargarListaAlumnos();
                        } else if (resp.toString().includes('sinCarrera')) {
                            var respSplit = resp.split('||');
                            swal('¡Upps!', "Al alumno " + respSplit[3] + " con matrícula " + respSplit[1] + " no se le pudieron asignar calificaciones ya que el id de la carrera '" + respSplit[2] + "' no existe en el sistema, verifique la información. Los registros posteriores NO fueron realizados", "error");
                        } else if (resp.toString().includes('sinAlumno')) {
                            var respSplit = resp.split('||');
                            swal('¡Upps!', 'No se encontró el alumno con la matrícula ' + respSplit[1] + ' intente nuevamente. Los registros posteriores NO fueron realizados', 'error');
                        } else if (resp.toString().includes('sinMateria')) {
                            var respSplit = resp.split('||');
                            swal('¡Upps!', 'No se encontró la materia con la clave ' + respSplit[2] + ' para el alumno ' + respSplit[3] + ' con matrícula ' + respSplit[1] + ', intente nuevamente. Los registros posteriores NO fueron realizados', 'error');
                        } else if (resp.toString().includes('formatoInvalido')) {
                            swal('¡Upps!', 'El archivo seleccionado no cumple con el formato requerido, recuerde que no debe modificar la estructura del formato', 'warning');
                        } else if (resp.toString().includes('sinCalificaciones')) {
                            var split = resp.split("||");
                            swal('¡Upps!', 'No se encontraron calificaciones para registar en la página ' + split[1] + ', revise que la información esté completa. Los registros posteriores NO fueron realizados', 'warning');
                        } else if (resp.toString().includes('infoCalifIncompleta')) {
                            let mensaje = resp.split("||")[1];
                            swal('¡Upps!', 'La información de las calificaciones está incompleta, revise los datos de la hoja: <b>' + mensaje, 'warning');
                        } else if (resp.toString().includes('infoAlumnoIncompleta')) {
                            swal('¡Upps!', 'La información del alumno está incompleta, revise los datos del archivo', 'warning');
                        } else if (resp.toString().includes('certificadoActivo')) {
                            var respSplit = resp.split('||');
                            swal('¡Upps!', 'El alumno ' + respSplit[2] + ' con matrícula ' + respSplit[1] + ' cuenta con certificados registrados, no es posible actualizar sus calificaciones', 'warning');
                        } else if (resp.toString().includes('errorFormatNumber')) {
                            var respSplit = resp.split('||');
                            show_swal('¡Upps!', 'Se encontró un error al leer el archivo de calificaciones en la hoja <b>' + respSplit[1] + '</b>, fila <b>' + respSplit[2] + '.</b><br><small>Se esperaba leer un dato tipo númerico en la celda <b>E</b><br><strong>¡Verifique la información e intente nuevamenete. Los registros posteriores NO fueron realizados!</strong></small>', 'error');
                        } else if (resp.toString().includes('wrongStatement')) {
                            var respSplit = resp.split('||');
                            show_swal('¡Upps!', 'Se encontró un error al leer el archivo de calificaciones en la hoja <b>' + respSplit[1] + '</b>, fila <b>' + respSplit[2] + '.</b><br><small>Se esperaba leer <b>"AC" O "ACREDITADA"</b>.<br><strong>¡Verifique la información e intente nuevamenete. Los registros posteriores NO fueron realizados!</strong></small>', 'error');
                        } else if (resp.toString().includes('usoFormulas')) {
                            var split = resp.split('||');
                            show_swal('¡Upps!', 'No se permite el uso de fórmulas dentro del proceso. Fórmula encontrada en la página <b>' + split[1] + '</b>, fila <b>' + split[2] + '</b>, columna <b>' + split[3] + '</b> por favor revisa el archivo.', 'warning');
                        }else if (resp.toString().includes('ErrorCicloEscolar')) {
                            var split = resp.split('||');
                            show_swal('¡Upps!', 'En la columna Ciclo Escolar esta mal el formato en la fila <b>' + split[1] + '</b>, los datos anteriores a esa fila se guardaron, por favor revisa el archivo.', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function () {
                        $('#fileCalificaciones').val('');
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });

    function TableActions() {
        $('#tblCalificaciones').on('click', '.js-swal-confirm', function () {
            var $table = $(".js-dataTable-full-pagination-Fixed").DataTable();
            var $row = $(this).parents("tr");
            var buttonVal = $(this).val().split('_');
            var idMateria = buttonVal[2];
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'Al confirmar, los datos no podrán retornar fácilmente',
                confirmButtonText: 'Sí, ¡Elimínala!',
                showCancelButton: true,
                cancelButtonText: 'Cancelar',
                cancelButtonColor: '#d33',
                showLoaderOnConfirm: true,
                animation: true,
                allowOutsideClick: false,
                allowEscapeKey: false
            }).then(function () {
                $.ajax({
                    url: '../Transporte/queryCCalificaciones.jsp',
                    data: '&txtBandera=2&idAlumno=' + buttonVal[1] + '&idMateria=' + idMateria,
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Registro Eliminado!', 'La calificación seleccionada ha sido eliminada', 'success');
                            $table.row($row).remove().draw();
                            //LoadTable();
                        } else if (resp.includes('certificadoActivo')) {
                            swal('¡Upps!', 'El alumno al que le pertenece esta calificación ya cuenta con certificados registrados, por lo que no se puede eliminar la calificación seleccionada', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }

                    }, complete: function () {
                    }
                });
            });
        });
    }

    primerPaso();
    function primerPaso() {
        let txtModulo = $("#txtModulo").val();
        $.ajax({
            url: '../Transporte/queryCPermisos.jsp',
            data: 'txtModulo=' + txtModulo,
            type: 'POST',
            success: function (data, textStatus, jqXHR) {
                var resp = data.split("~");
                let mensajeVigencia = resp[1];
                if (mensajeVigencia !== "") {
                    eval(mensajeVigencia);
                }
                if (resp[0].includes("todos")) {
                    cargarListaAlumnos();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        cargarListaAlumnos();
                        //IMPORTAR REGISTROS
                        if (stringStepFirst.split("¬")[5].includes("0")) {
                            $("#btnImportarCalifsExcel").remove();
                        }
                    } else {
                        $(".noPermisson").show();
                        $("#fullContent").html("");
                        $('#mainLoader').fadeOut();
                    }
                } else if (resp[0].includes("error")) {
                    $(".noPermisson").show();
                    $("#fullContent").html("");
                    $('#mainLoader').fadeOut();
                }
            }
        });
    }

    function LoadTableEspecifico(e) {
        $("#tblCalificaciones_filter input").val("").trigger("keyup");
        $.ajax({
            url: '../Transporte/queryCCalificaciones.jsp',
            data: 'txtBandera=1&idAlumno=' + e,
            type: 'POST',
            success: function (resp) {
                var indicador = resp.split("|")[0];
                if (indicador.toString().trim() === "success") {
                    $('#DivTblCalificaciones').html(resp.split("|")[1]);
                    $('[data-toggle="tooltip"]').tooltip({
                        container: 'body',
                        animation: false,
                        trigger: 'hover'
                    });
                    TableActions();
                    initTable();
                    setTimeout(function () {
                        $('#mainLoader').fadeOut();
                    });
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function (jqXHR, textStatus) {
                $("#loadAction").fadeOut();
            }
        });
    }

    function cargarListaAlumnos() {
        $("#loadAction").fadeIn();
        $.ajax({
            url: '../Transporte/queryCCalificaciones.jsp',
            data: 'txtBandera=3',
            type: 'POST',
            success: function (resp) {
                let serverMsg = resp.split("¬")[0];
                if (serverMsg.toString().trim() === 'success') {
                    let lista = resp.split("¬")[1];
                    $("#lstAlumnos").find("option").remove().end().append(lista).trigger("chosen:updated");
                    $("#btnFiltrarCalificaciones").attr("disabled", false);
                } else if (serverMsg.toString().trim() === 'empty') {
                    let lista = resp.split("¬")[1];
                    $("#lstAlumnos").find("option").remove().end().append(lista).trigger("chosen:updated");
                    $("#btnFiltrarCalificaciones").attr("disabled", true);
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function (jqXHR, textStatus) {
                $('#mainLoader').fadeOut();
                $("#loadAction").fadeOut();
            }
        });
    }

    $("#btnFiltrarCalificaciones").on("click", function () {
        let idAlumno = $("#lstAlumnos").val();
        if (idAlumno.toString().trim() === 'todos') {
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'El proceso de carga va demorar en relación a la cantidad de calificaciones registradas en el sistema',
                confirmButtonText: 'Sí, ¡Consultar!',
                showCancelButton: true,
                cancelButtonText: 'Cancelar',
                cancelButtonColor: '#d33',
                showLoaderOnConfirm: true,
                animation: true,
                allowOutsideClick: false,
                allowEscapeKey: false
            }).then(function () {
                $("#loadAction").fadeIn();
                LoadTableEspecifico(idAlumno);
            }, function () {

            });
        } else {
            $("#loadAction").fadeIn();
            LoadTableEspecifico(idAlumno);
        }
    });


    /**
     * Función global para manejo de errores con ajax
     */
    $.ajaxSetup({
        beforeSend: function (jqXHR, settings) {
            jqXHR.url = settings.url;
            jqXHR.url = jqXHR.url.substring(jqXHR.url.lastIndexOf('/') + 1);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            var msjError = '';
            if (jqXHR.status === 0) {
                //Not connect: Verify Network
                msjError += 'Sin conexión, verifica la red.';

            } else if (jqXHR.status == 404) {
                //Requested page not found [404]
                msjError += '[404] Elemento <b>"' + jqXHR.url + '"</b> no encontrado';

            } else if (jqXHR.status == 500) {
                msjError += '[500] Error interno del servidor';

            } else if (textStatus === 'parsererror') {

                msjError += 'Requested JSON parse failed.';

            } else if (textStatus === 'timeout') {
                msjError += 'Time out error.';

            } else if (textStatus === 'abort') {
                msjError += 'Ajax request aborted.';

            } else {
                msjError: '(' + jqXHR + ' <b>|</b> ' + textStatus + ' <b>|</b> ' + errorThrown + ')';
            }

            msjError += "<br><small>vuelve a cargar la página con F5, si continua con el problema, comuníquese con soporte técnico.</small>";
            show_swal('Error interno POST', msjError, 'error');
        }
    });

    function show_swal(titulo, mensaje, tipo) {
        swal({
            allowOutsideClick: false,
            allowEscapeKey: false,
            title: titulo,
            html: mensaje,
            type: tipo
        });
    }

});