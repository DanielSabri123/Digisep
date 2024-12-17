$(document).ready(function () {
    $("#lstCarreras").chosen({width: "100%", disable_search_threshold: 4});
    var tblCarreras;
    function initTable() {
        tblCarreras = $(".js-dataTable-full-pagination-Fixed").dataTable({
            ordering: true,
            orderable: false,
            paging: true,
            searching: true,
            info: true,
            stateSave: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: 6}],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]],
            "columns": [
                null,
                {"width": "15%"},
                {"width": "15%"},
                null,
                {"width": "15%"},
                {"width": "15%"},
                {"width": "5%"}
            ]
        });
    }
    initTable();
    function LoadTable() {
        $.ajax({
            url: '../Transporte/queryCCarreras.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var indicador = resp.split("|")[0];
                if (indicador.toString().trim() === "success") {
                    $('#DivTblCarreras').html(resp.split("|")[1]);
                    $('[data-toggle="tooltip"]').tooltip();
                    TableActions();
                    initTable();
                    setTimeout(function () {
                        $('#mainLoader').fadeOut();
                    }, 300);
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }
        });
    }

    $('#btnImportarCarrerasExcel').click(function () {
        $('#fileCarrera').click();
    });

    $('#fileCarrera').on('change', function () {
        var archivo = $(this).val();

        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.xls') || archivo.toString().endsWith('.xlsx')) {
                swal('¡Cargando información!', 'Se están registrando las carreras en el sistema', 'info').then(function () {
                    $('#loadAction').fadeIn();
                });
                $('#importarArchivoCarreras').click();
            } else {
                swal('¡Upps!', 'Debes seleccionar el formato predeterminado con la información de las carreras y sus materias', 'warning');
                $('#fileCarrera').val('');
            }
        }
    });

    $('.btnAccionesCarrera').click(function () {
        $("form[name='FormCarrera']").submit(function (e) {
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
                fileAlumnos: {
                    required: true,
                    extension: "xls|xlsx"
                }
            },
            messages: {
                'fileAlumnos': {
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
                    url: '../Transporte/queryCCarreras.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    success: function (resp) {
                        if (resp.toString().includes('success')) {
                            swal('¡Registros agregados!', 'La importación de las carreras y sus materias se ha completado', 'success');
                            cargarListaCarreras();
                        } else if (resp.toString().includes('infoIncompleta')) {
                            swal('¡Upps!', 'Se encontró una carrera con información incompleta, intente nuevamente', 'warning');
                        } else if (resp.toString().includes('sinMaterias')) {
                            swal('¡Upps!', 'Se encontró una carrera sin materias para registrar, intente nuevamente', 'warning');
                        } else if (resp.toString().includes('formatoInvalido')) {
                            var split = resp.split("||");
                            if (split.length === 1 && split[0].toString().trim() === 'formatoInvalido')
                                swal('¡Upps!', 'El formato del archivo seleccionado es inválido, recuerde que no debe modificar la estructura del archivo', 'warning');
                            else {
                                var mensaje = split[1].split("_")[1];
                                swal('¡Upps!', 'La fecha de expedición del RVOE,  en la hoja ' + split[2] + ', contiene errores: ' + mensaje, 'warning');
                            }
                        } else if (resp.toString().includes('infoCarreraIncompleta')) {
                            var split = resp.split('||');
                            swal('¡Upps!', 'La información de la CARRERA no está completa en la página ' + split[1] + ' por favor revisa el archivo', 'warning');
                        } else if (resp.toString().includes('usoFormulas')) {
                            var split = resp.split('||');
                            swal('¡Upps!', 'No se permite el uso de fórmulas dentro del proceso. Fórmula encontrada en la página <b>' + split[1] + '</b>, fila <b>' + split[2] + '</b>, columna <b>' + split[3] + '</b> por favor revisa el archivo.', 'warning')
                        } else if (resp.toString().includes('infoMateriaIncompleta')) {
                            var split = resp.split('||');
                            swal('¡Upps!', 'La información de las MATERIAS no está completa en la página ' + split[1] + ' por favor revisa el archivo', 'warning');
                        } else if (resp.toString().includes('informacionIncompleta')) {
                            var split = resp.split('||');
                            swal('¡Upps!', 'La información en la página ' + split[1] + ' está incompleta, por favor revisa el archivo', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    },
                    complete: function () {
                        $('#fileCarrera').val('');
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });

    function TableActions() {
        $('#tblCarreras').on('click', '.js-swal-confirm', function () {
            var buttonVal = $(this).val().split('_');

            let nombreMateria = $('#NombreMateria_' + buttonVal[1]).text();
            let nombreCarrera = $('#NombreCarrera_' + buttonVal[1]).text();
            tblCarreras.$("[data-toggle='tooltip']").tooltip("hide");
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
                $("#loadAction").fadeIn();
                $.ajax({
                    url: '../Transporte/queryCCarreras.jsp',
                    data: '&txtBandera=2&idMateria=' + buttonVal[1] + '&nombreMateria=' + nombreMateria + '&nombreCarrera=' + nombreCarrera,
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Registro eliminado!', 'La materia ha sido eliminada con éxito', 'success');
                            LoadTable();
                        } else if (resp.includes('enUso')) {
                            swal('¡Upps!', 'La materia seleccionada está relacionada con una o más calificaciones, no se puede eliminar', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function () {
                        tblCarreras.$("[data-toggle='tooltip']").tooltip("hide");
                        $("#loadAction").fadeOut();
                    }
                });
            }, function () {
                tblCarreras.$("[data-toggle='tooltip']").tooltip("hide");
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
                    cargarListaCarreras();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        cargarListaCarreras();
                        //IMPORTAR REGISTROS
                        if (stringStepFirst.split("¬")[5].includes("0")) {
                            $("#btnImportarCarrerasExcel").remove();
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

    function loadTableEspecifico(e) {
        $("#tblCarreras_filter input").val("").trigger("keyup");
        $.ajax({
            url: '../Transporte/queryCCarreras.jsp',
            data: 'txtBandera=1&idCarrera=' + e,
            type: 'POST',
            success: function (resp) {
                var indicador = resp.split("|")[0];
                if (indicador.toString().trim() === "success") {
                    $('#DivTblCarreras').html(resp.split("|")[1]);
                    $('[data-toggle="tooltip"]').tooltip();
                    TableActions();
                    initTable();
                    setTimeout(function () {
                        $('#mainLoader').fadeOut();
                    }, 300);
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            },
            complete: function (jqXHR, textStatus) {
                $("#loadAction").fadeOut();
            }
        });
    }

    function cargarListaCarreras() {
        $("#loadAction").fadeIn();
        $.ajax({
            url: '../Transporte/queryCCarreras.jsp',
            data: 'txtBandera=3',
            type: 'POST',
            success: function (resp) {
                let serverMsg = resp.split("¬")[0];
                if (serverMsg.toString().trim() === 'success') {
                    let lista = resp.split("¬")[1];
                    $("#lstCarreras").find("option").remove().end().append(lista).trigger("chosen:updated");
                    $("#btnFiltrarMaterias").attr("disabled", false);
                } else if (serverMsg.toString().trim() === 'empty') {
                    let lista = resp.split("¬")[1];
                    $("#lstCarreras").find("option").remove().end().append(lista).trigger("chosen:updated");
                    $("#btnFiltrarMaterias").attr("disabled", true);
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function (jqXHR, textStatus) {
                $('#mainLoader').fadeOut();
                $("#loadAction").fadeOut();
            }
        });
    }

    $("#btnFiltrarMaterias").on("click", function () {
        let idMateria = $("#lstCarreras").val();
        if (idMateria.toString().trim() === 'todos') {
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'El proceso de carga va demorar en relación a la cantidad de materias registradas en el sistema',
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
                loadTableEspecifico(idMateria);
            }, function () {

            });
        } else {
            $("#loadAction").fadeIn();
            loadTableEspecifico(idMateria);
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