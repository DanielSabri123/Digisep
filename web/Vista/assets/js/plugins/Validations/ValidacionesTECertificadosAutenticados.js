/**
 * @author: Braulio Sorcia
 * @description Js para las validaciones del lado del cliente de certificados autenticados
 * @since 11 DE FEBRERO DE 2021
 * =====================================================
 * @author_change:
 * @description_change:
 * @date_change: 
 */

$(document).ready(function () {
    var tblCertificados;
    function initTable() {
        tblCertificados = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            orderable: false,
            paging: true,
            searching: true,
            info: true,
            stateSave: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: [6]}/*, {width: "5%", targets: 7}*/],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20], [5, 10, 15, 20]],
            columns: [
                null,
                {"width": "12%"},
                {"width": "12%"},
                {"width": "12%"},
                {"width": "12%"},
                {"width": "47%"},
                {"width": "5%"}
            ]
        });
        tblCertificados.$('[data-toggle="tooltip"]').tooltip({
            container: 'body',
            animation: false,
            trigger: 'hover'
        });
        $("#tblCertificados_filter input").val("").trigger("keyup");
    }

    $("#lstCarreras").chosen({width: "100%", disable_search_threshold: 4});
    $("#lstCarreras_chosen").css("display", "grid");

    function cargaInicio() {
        $.ajax({
            url: '../Transporte/queryCCertificadosAutenticados.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var arregloRespuesta = resp.split("~");

                var lstCarreras = arregloRespuesta[0];
                if (lstCarreras.split("|")[0].toString().trim().includes("error")) {
                    show_swal("¡Upps!", lstCarreras.split('|')[1], "error");
                }
                $('#lstCarreras').html(lstCarreras.split("|")[1]);
                $("#lstCarreras").val("todos").trigger("chosen:updated");
                initTable();

            }, complete: function (jqXHR, textStatus) {
                $("#mainLoader").fadeOut();
            }
        });
    }

    $("#btnFiltrarCertificados").on("click", function () {
        let idCarrera = $("#lstCarreras").val();
        if (idCarrera.toString().trim() === 'todos') {
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'El proceso de carga va demorar en relación a la cantidad de certificados registrados en el sistema',
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
                cargarTabla(idCarrera);
            }, function () {

            });
        } else {
            $("#loadAction").fadeIn();
            cargarTabla(idCarrera);
        }
    });

    function cargarTabla(idCarrera) {
        $("#loadAction").fadeIn();
        $.ajax({
            url: '../Transporte/queryCCertificadosAutenticados.jsp',
            data: '&txtBandera=2&idCarrera=' + idCarrera,
            type: 'POST',
            success: function (resp) {
                if (resp.includes('success')) {
                    $("#divTblCertificados").html(resp.split("|")[1]);
                    initTable();
                    TableActions();
                }
            }, complete: function () {
                $("#loadAction").fadeOut();
            }
        });
    }

    $('#btnImportarCertificadosZip').click(function () {
        $('#fileZipAutenticado').click();
    });

    $('#fileZipAutenticado').on('change', function () {
        var archivo = $(this).val();
        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.zip') || archivo.toString().endsWith('.xml')) {
                swal('¡Cargando información!', 'Se están registrando los certificados en el sistema', 'info').then(function () {
                    $('#loadAction').fadeIn();
                    $('#importarArchivoZip').click();
                });
            } else {
                swal('¡Formato inválido!', 'Solo se permiten archivos zip o xml', 'warning');
                $('#fileZipAutenticado').val('');
            }
        }
    });

    $('.btnAccionesZip').click(function () {
        $("form[name='FormZip']").submit(function (e) {
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
                fileZipAutenticado: {
                    required: true,
                    extension: "zip||xml"
                }
            },
            messages: {
                'fileZipAutenticado': {
                    required: function () {
                        swal('¡Upps!', 'Selecciona un archivo zip con los documentos a importar', 'warning');
                    },
                    extension: function () {
                        swal('¡Formato inválido!', 'Solo se permiten archivos en formato zip o xml', 'warning');
                    }
                }
            },
            submitHandler: function (form) {
                $.ajax({
                    url: '../Transporte/queryCCertificadosAutenticados.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        message();
                    },
                    success: function (resp) {
                        let datos = resp.split("||");
                        //Respuesta principal
                        if (datos[0].toString().trim() === "success") {
                            //Respuesta secundaria
                            if (datos[1].toString().trim() === "noFile") {
                                show_swal("¡Proceso completado!", "¡Los archivos fueron vinculados correctamente!<br><small><b>Para ver los cambios reflejados realice una nueva búsqueda de la carrera deseada.</b></small>", "success");
                                $("#masiveTemp").hide();
                            } else {
                                swal({
                                    allowOutsideClick: false,
                                    allowEscapeKey: false,
                                    title: "¡Proceso completado!",
                                    html: "¡El archivo fue procesado correctamente pero se encontraron algunas inconcistencias!<br>Descarga el archivo para verificar la información, dando clic en el botón:<br> <strong>Descargar incidencias</strong>",
                                    type: "warning"
                                }).then(function () {
                                    $("#masiveTemp").remove();
                                    $("<a>")
                                            .attr("id", "masiveTemp")
                                            .attr("class", "btn btn-warning btn-sm btn-rounded")
                                            .attr("href", datos[1])
                                            .attr("download", "")
                                            .attr("style", "color:#fff")
                                            .appendTo("#divAcciones");
                                    $("<i>").attr("class", "fa fa-file-o").appendTo("#masiveTemp");
                                    $("#masiveTemp").append(" Descargar incidencias");
                                });
                            }
                        } else {
                            show_swal("¡Lo sentimos!", "¡Ocurrió un error al procesar la información, intenta de nuevo. <br>Si el problema continua, contacte a soporte técnico!", "error");
                        }
                    }, error: function (jqXHR, textStatus, errorThrown) {

                    }, complete: function () {
                        $('#loadAction').fadeOut();
                        $('#fileZipAutenticado').val('');
                    }
                });
            }
        });
    });

    $('#btnImportarCertificadosPdf').click(function () {
        swal({
            type: 'warning',
            title: '¿Estás seguro?',
            html: 'La carga de constancias debe ser realizada <b>después de</b> cargar los certificados xml autenticados.',
            confirmButtonText: 'Sí, ¡Continuar!',
            showCancelButton: true,
            cancelButtonText: 'Cancelar',
            cancelButtonColor: '#d33',
            showLoaderOnConfirm: true,
            animation: true,
            allowOutsideClick: false,
            allowEscapeKey: false
        }).then(resp => {
            $('#fileZipConstancia').click();
        }, function () {

        });
    });

    $('#fileZipConstancia').on('change', function () {
        var archivo = $(this).val();
        var fileInput = document.getElementById("fileZipConstancia");
        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.zip') || archivo.toString().endsWith('.pdf')) {
                if (fileInput.files[0].size > 90000000) {
                    swal('¡Tamaño no permitido!', 'Solo se permiten archivos no mayores a 90 Mb', 'warning');
                    $('#fileZipConstancia').val('');
                } else {
                    swal('¡Cargando información!', 'Se están registrando las constancias en el sistema', 'info').then(function () {
                        $('#loadAction').fadeIn();
                        $('#importarArchivoZipConstancia').click();
                    });
                }
            } else {
                swal('¡Formato inválido!', 'Solo se permiten archivos zip o pdf', 'warning');
                $('#fileZipConstancia').val('');
            }
        }
    });

    $('.btnAccionesZipConstancias').click(function () {
        $("form[name='FormZipConstancias']").submit(function (e) {
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
                fileZipAutenticado: {
                    required: true,
                    extension: "zip||pdf"
                }
            },
            messages: {
                'fileZipConstancia': {
                    required: function () {
                        swal('¡Upps!', 'Selecciona un archivo zip con los documentos a importar', 'warning');
                    },
                    extension: function () {
                        swal('¡Formato inválido!', 'Solo se permiten archivos en formato zip o pdf', 'warning');
                    }
                }
            },
            submitHandler: function (form) {
                $.ajax({
                    url: '../Transporte/queryCCertificadosAutenticados.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        message();
                    },
                    success: function (resp) {
                        let datos = resp.split("||");
                        //Respuesta principal
                        if (datos[0].toString().trim() === "success") {
                            //Respuesta secundaria
                            if (datos[1].toString().trim() === "noFile") {
                                show_swal("¡Proceso completado!",
                                        "<p>¡Las constancias fueron vinculadas correctamente!</p>\n\
                                <small><b>Para ver los cambios reflejados realice una nueva búsqueda de la carrera deseada.</b></small>"
                                        , "success");
                                $("#masiveTemp").hide();
                            } else {
                                swal({
                                    allowOutsideClick: false,
                                    allowEscapeKey: false,
                                    title: "¡Proceso completado!",
                                    html: "¡El archivo fue procesado correctamente pero se encontraron algunas inconcistencias!<br>Descarga el archivo para verificar la información, dando clic en el botón:<br> <strong>Descargar incidencias</strong>",
                                    type: "warning"
                                }).then(function () {
                                    $("#masiveTemp").remove();
                                    $("<a>")
                                            .attr("id", "masiveTemp")
                                            .attr("class", "btn btn-warning btn-sm btn-rounded")
                                            .attr("href", datos[1])
                                            .attr("download", "")
                                            .attr("style", "color:#fff")
                                            .appendTo("#divAcciones");
                                    $("<i>").attr("class", "fa fa-file-o").appendTo("#masiveTemp");
                                    $("#masiveTemp").append(" Descargar incidencias");
                                });
                            }
                        } else {
                            show_swal("¡Lo sentimos!",
                                    "¡Ocurrió un error al procesar la información! \n\
                                    <br><br><small><b>Verifica que en el archivo zip no haya directorios y sean solamente los archivos a cargar e intenta de nuevo.</b></small>\n\
                                    <br><br>¡Si el problema continua, contacte a soporte técnico!", "error");
                            $('#fileZipConstancia').val('');
                        }
                    }, error: function (jqXHR, textStatus, errorThrown) {
                        $.notify({
                            // options
                            message: 'Ocurrió un error al procesar la solicitud. Presiona la tecla <b>F5</b> y revisa la información importada.'
                        }, {
                            // settings
                            type: 'danger',
                            delay: 15000,
                            placement: {
                                from: "top",
                                align: "right"
                            },
                            z_index: 10310
                        });
                        clearTimeout(timer);
                        clearTimeout(timer2);
                        clearTimeout(timer3);
                    }, complete: function () {
                        clearTimeout(timer);
                        clearTimeout(timer2);
                        clearTimeout(timer3);
                        $('#loadAction').fadeOut();
                        $('#fileZipConstancia').val('');
                    }
                });
            }
        });
    });

    var timer;
    var timer2;
    var timer3;
    var timer4;
    function message() {
        timer = setTimeout(function () {
            $.notify({
                // options
                message: 'El proceso al parecer está demorando más de lo habitual. <b>La tarea sigue en proceso.</b>'
            }, {
                // settings
                type: 'warning',
                delay: 15000,
                placement: {
                    from: "top",
                    align: "right"
                },
                z_index: 10310
            });

            timer2 = setTimeout(function () {
                $.notify({
                    // options
                    message: 'El proceso al parecer está demorando más de lo habitual. <b>La tarea sigue en proceso.</b>'
                }, {
                    // settings
                    type: 'warning',
                    delay: 15000,
                    placement: {
                        from: "top",
                        align: "right"
                    },
                    z_index: 10310
                });

                timer3 = setTimeout(function () {
                    $.notify({
                        // options
                        message: 'El proceso ha demorado más de lo habitual. <b>Si lo desea, puede presionar F5 y consultar la tabla, revisando el contenido cargado.</b>'
                    }, {
                        // settings
                        type: 'danger',
                        delay: 15000,
                        placement: {
                            from: "top",
                            align: "right"
                        },
                        z_index: 10310
                    });
                    timer4 = setTimeout(function () {
                        $.notify({
                            // options
                            message: 'El proceso ha demorado más de lo habitual. <b>Si lo desea, puede presionar F5 y consultar la tabla, revisando el contenido cargado.</b>'
                        }, {
                            // settings
                            type: 'danger',
                            delay: 15000,
                            placement: {
                                from: "top",
                                align: "right"
                            },
                            z_index: 10310
                        });
                    }, 180000);//13 minutos
                }, 180000);//10 minutos
            }, 240000);//aquí son 4 minutos + 3 minutos.
        }, 180000);//3 minutos//1000 = 1 segundo
    }

    function TableActions() {

        $("#tblCertificados").on("click", ".btnEliminarVinculo", function () {
            let idCerificado = $(this).attr("id").split("_")[1];
            var matricula = $("#tblCertificados #Matricula_" + idCerificado).text();
            var folioControl = $("#tblCertificados #FolioControl_" + idCerificado).text();
            var idCarrera = $(this).attr("id-carrera");
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                html: '<p>Al confirmar, los datos no podrán retornar fácilmente<br>Matrícula del alumno: <b>' + matricula.trim() + '</b><br>Folio de control: <b>' + folioControl.trim() + '</p>',
                confirmButtonText: 'Sí, ¡Elimínalo!',
                showCancelButton: true,
                cancelButtonText: 'Cancelar',
                cancelButtonColor: '#d33',
                showLoaderOnConfirm: true,
                animation: true,
                allowOutsideClick: false,
                allowEscapeKey: false
            }).then(function () {
                $("#loadAction").fadeIn();
                $.ajax({
                    url: '../Transporte/queryCCertificadosAutenticados.jsp',
                    data: '&txtBandera=3&idCerificado=' + idCerificado +'&folioControl='+folioControl,
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            show_swal('¡Eliminado!', 'El registro ha sido eliminado', 'success');
                            $("#lstCarreras").val(idCarrera).trigger("chosen:updated");
                            cargarTabla(idCarrera);
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function () {
                        $("#loadAction").fadeIn();
                    }
                });
            }, function () {

            });
        });
    }

    function show_swal(titulo, mensaje, tipo) {
        swal({
            allowOutsideClick: false,
            allowEscapeKey: false,
            title: titulo,
            html: mensaje,
            type: tipo
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
                    cargaInicio();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        cargaInicio();
                        //IMPORTAR REGISTROS
                        if (stringStepFirst.split("¬")[5].includes("0")) {
                            $("#btnImportarCertificadosZip").remove();
                            $("#btnImportarCertificadosPdf").remove();
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
});