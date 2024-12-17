/**
 * @author: Braulio Sorcia
 * @description Js para las validaciones del lado del cliente de titulos autenticados
 * @since 09 DE JUNIO DE 2020
 * =====================================================
 * @author_change:
 * @description_change:
 * @date_change: 
 */

$(document).ready(function () {
    //$("#mainLoader").hide();
    //cargaInicio();
    var tblTitulos;
    function initTable() {
        tblTitulos = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            orderable: false,
            paging: true,
            searching: true,
            info: true,
            stateSave: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: [6, 7]}/*, {width: "5%", targets: 7}*/],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20], [5, 10, 15, 20]],
            columns: [
                null,
                {"width": "15%"},
                {"width": "15%"},
                {"width": "15%"},
                {"width": "15%"},
                {"width": "35%"},
                {"width": "15%"},
                {"width": "5%"}
            ]
        });
        tblTitulos.$('[data-toggle="tooltip"]').tooltip({
            container: 'body',
            animation: false,
            trigger: 'hover'
        });
        $("#tblTitulosAutenticados_filter input").val("").trigger("keyup");
    }

    $("#lstCarreras").chosen({width: "100%", disable_search_threshold: 4});
    $("#lstCarreras_chosen").css("display", "grid");

    function cargaInicio() {
        $.ajax({
            url: '../Transporte/queryCTitulosAutenticados.jsp',
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

    $('#btnImportarTitulosZip').click(function () {
        $('#fileZipAutenticado').click();
    });

    $('#fileZipAutenticado').on('change', function () {
        var archivo = $(this).val();
        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.zip') || archivo.toString().endsWith('.xml')) {
                swal('¡Cargando información!', 'Se están registrando los titulos en el sistema', 'info').then(function () {
                    $('#loadAction').fadeIn();
                    $('#importarArchivoZip').click();
                });
            } else {
                swal('¡Formato inválido!', 'Solo se permiten archivos zip o xml', 'warning');
                $('#fileZipAutenticado').val('');
            }
        }
    });

    $('#btnImportarTitulosPdf').click(function () {
        swal({
            type: 'warning',
            title: '¿Estás seguro?',
            html: 'La carga de constancias debe ser realizada <b>después de</b> cargar los títulos xml autenticados.',
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
        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.zip') || archivo.toString().endsWith('.pdf')) {
                swal('¡Cargando información!', 'Se están registrando las constancias en el sistema', 'info').then(function () {
                    $('#loadAction').fadeIn();
                    $('#importarArchivoZipConstancia').click();
                });
            } else {
                swal('¡Formato inválido!', 'Solo se permiten archivos zip o pdf', 'warning');
                $('#fileZipConstancia').val('');
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
                    url: '../Transporte/queryCTitulosAutenticados.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    success: function (resp) {
                        let datos = resp.split("||");
                        //Respuesta principal
                        if (datos[0].toString().trim() === "success") {
                            //Respuesta secundaria
                            if (datos[1].toString().trim() === "noFile") {
                                show_swal("¡Proceso completado!", "¡Los archivos fueron vinculados correctamente!", "success");
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
                    url: '../Transporte/queryCTitulosAutenticados.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
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
                                    <br><br><small><b>Verifica que en el archivo zip no haya directorios y sean solamento los archivos a cargar e intenta de nuevo.</b></small>\n\
                                    <br><br>¡Si el problema continua, contacte a soporte técnico!", "error");
                            $('#fileZipConstancia').val('');
                        }
                    }, error: function (jqXHR, textStatus, errorThrown) {

                    }, complete: function () {
                        $('#loadAction').fadeOut();
                        $('#fileZipConstancia').val('');
                    }
                });
            }
        });
    });


    $("#btnFiltrarTitulos").on("click", function () {
        let idCarrera = $("#lstCarreras").val();
        if (idCarrera.toString().trim() === 'todos') {
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'El proceso de carga va demorar en relación a la cantidad de titulos registrados en el sistema',
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
            url: '../Transporte/queryCTitulosAutenticados.jsp',
            data: '&txtBandera=2&idCarrera=' + idCarrera,
            type: 'POST',
            success: function (resp) {
                if (resp.includes('success')) {
                    $("#divTblTitulos").html(resp.split("|")[1]);
                    initTable();
                    TableActions();
                }
            }, complete: function () {
                $("#loadAction").fadeOut();
            }
        });
    }
    var $table, $row;
    function TableActions() {

        $("#tblTitulosAutenticados").on("click", ".btnEliminarVinculo", function () {
            $table = $(".js-dataTable-full-pagination-Fixed").DataTable();
            $row = $(this).parents("tr");
            let idTitulo = $(this).attr("id").split("_")[1];
            var matricula = $("#tblTitulosAutenticados #Matricula_" + idTitulo).text();
            var folioControl = $("#tblTitulosAutenticados #FolioControl_" + idTitulo).text();
            var idCarrera = $(this).attr("id-carrera");
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                html: '<p>Al confirmar, los datos no podrán retornar fácilmente<br>Matrícula del profesionista: <b>' + matricula.trim() + '</b><br>Folio de control: <b>' + folioControl.trim() + '</p>',
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
                    url: '../Transporte/queryCTitulosAutenticados.jsp',
                    data: '&txtBandera=3&idTitulo=' + idTitulo + "&folioControl="+folioControl,
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Eliminado!', 'El registro ha sido eliminado', 'success');
                            //$("#lstCarreras").val(idCarrera).trigger("chosen:updated");
                            //cargarTabla(idCarrera);
                            $table.row($row).remove().draw();
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function () {
                        $("#loadAction").fadeOut();
                    }
                });
            }, function () {

            });


        });

        $("#tblTitulosAutenticados").on("change", ".chtodos", function () {
            if ($(this).is(":checked")) {
                tblTitulos.$(".cbxDesPdfM").prop("checked", true);
                if (tblTitulos.$(".cbxDesPdfM").length > 0)
                    $("#btnDescargaMasivaPDF").prop("disabled", false);
            } else {
                tblTitulos.$(".cbxDesPdfM").prop("checked", false);
                $("#btnDescargaMasivaPDF").prop("disabled", true);
            }
        });
    }

    $("body").on("change", ".cbxDesPdfM", function () {
        var conteo_cadena_validacion = 0;
        tblTitulos.$(".cbxDesPdfM").each(function () {
            if ($(this).is(":checked")) {
                ++conteo_cadena_validacion;
            }
        });
        if (conteo_cadena_validacion > 0) {
            $("#btnDescargaMasivaPDF").prop("disabled", false);
        } else {
            $("#btnDescargaMasivaPDF").prop("disabled", true);
        }
        if (conteo_cadena_validacion !== tblTitulos.$(".cbxDesPdfM").length) {
            $(".chtodos").prop("checked", false);
        } else {
            $(".chtodos").prop("checked", true);
        }
    });
    var time;
    $("#btnDescargaMasivaPDF").click(function () {
        var cadenaIdTitulos = "";

        swal({
            type: 'warning',
            title: '¿Generar Zip?',
            html: '<p>Se generara un archivo zip con los títulos seleccionados</p>',
            confirmButtonText: 'Sí, ¡Generar!',
            showCancelButton: true,
            cancelButtonText: 'Cancelar',
            cancelButtonColor: '#d33',
            showLoaderOnConfirm: true,
            animation: true,
            allowOutsideClick: false,
            allowEscapeKey: false,
            input: "checkbox",
            inputClass: "css-input css-checkbox css-checkbox-primary",
            inputPlaceholder: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;¿Regenerar PDF creados?'
        }).then(resp => {
            $('#loadAction').fadeIn();
            tblTitulos.$(".cbxDesPdfM").map(function (index, element) {
                if ($(element).is(":checked")) {
                    let id = $(element).attr("id").split("_")[1];
                    let nombre = $(this).attr("data-nombrepdf");
                    let nombrexml = $(this).attr("data-nombrexml");
                    cadenaIdTitulos += id + "~" + nombre + "~" + nombrexml + "¬";
                }
            }).promise().done(function () {
                let regenerar = $("#swal2-checkbox").is(":checked");
                $.ajax({
                    url: '../Transporte/queryCTitulosAutenticados.jsp',
                    data: '&txtBandera=4&cadenaIdTitulos=' + cadenaIdTitulos + '&genPDF=' + regenerar,
                    type: 'POST',
                    success: function (resp) {
                        $("#divAcciones #masivePDFTemp").remove();
                        if (!resp.includes("error")) {
                            $.notify({
                                // options
                                message: '<div class="text-center">Tu archivo está listo para descargar<br><strong>El archivo dejará de estar disponible en 3 minutos</strong></div>'
                            }, {
                                // settings
                                type: 'success',
                                delay: 15000
                            });
                            clearTimeout(time);
                            $("<a>")
                                    .attr("id", "masivePDFTemp")
                                    .attr("class", "btn btn-danger btn-sm btn-rounded")
                                    .attr("href", resp.trim())
                                    .attr("download", "")
                                    .attr("style", "color:#fff")
                                    .appendTo("#divAcciones");
                            $("<i>").attr("class", "fa fa-file-zip-o").appendTo("#masivePDFTemp");
                            $("#masivePDFTemp").append(" Descargar lote de titulos pdf").attr("data-toggle", "tooltip").attr("title", "Descargar lote de titulos pdf");
                            $('[data-toggle="tooltip"]').tooltip();
                            $("#masiveTemp").hide();
                            time = setTimeout(function () {
                                $("#masivePDFTemp").tooltip("destroy");
                                $("#divAcciones #masivePDFTemp").remove();
                            }, 180000);
                        } else {
                            $.notify({
                                // options
                                message: '<div class="text-center">Ocurrió un error al generar tu archivo, intenta de nuevo</div>'
                            }, {
                                // settings
                                type: 'danger'
                            });

                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            });
        }, function () {

        });
        $(".swal2-checkbox").find("span").remove();
        $(".swal2-checkbox").append("<span></span>¿Regenerar PDF creados?");
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
                            $("#btnImportarTitulosZip").remove();
                            $("#btnImportarTitulosPdf").remove();
                        }
                        //NO DESCARGAS
                        if (stringStepFirst.split("¬")[7].includes("0")) {
                            $("#btnDescargaMasivaPDF").remove();
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