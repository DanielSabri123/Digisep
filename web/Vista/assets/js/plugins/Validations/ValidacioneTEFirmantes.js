/**
 * @author: Braulio Sorcia
 * @description Js para las validaciones del lado del cliente de firmantes
 * @since 16 DE DICIEMBRE 2018
 * =====================================================
 * @author_change:
 * @description_change:
 * @date_change: 
 */

$(document).ready(function () {
    resize();
    $("#lstCargoFirmante").chosen({width: "100%", disable_search_threshold: 4});
    function LoadTable() {
        $.ajax({
            url: '../Transporte/queryCFirmantes.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (response) {
                var tbl = response.split("~")[0];
                var notifys = response.split("~")[1];

                if (tbl.split("|")[0].toString().trim() === "error") {
                    show_swal("¡Upps!", tbl.split('|')[1], "error");
                }

                $('#divTblFirmantes').html(tbl.split('|')[1]);
                $('[data-toggle="tooltip"]').tooltip();
                initTable();
                TableActions();
                $("#tblFirmantes_filter input").val("").trigger("keyup");
                if (notifys.includes("errorNotify")) {
                    show_swal("¡Upps!", notifys.split('|')[1], "error");
                } else {
                    eval(notifys);
                }
                setTimeout(function () {
                    $('#mainLoader').fadeOut();
                });
            }
        });
    }
    $(".notAllowed").keyup(function () {
        let element = $(this);
        element.val(element.val().replace(/[&"'<>;]/g, ""));
    });
    $(".notAllowed").on("paste", function () {
        let element = $(this);
        element.val(element.val().replace(/[&"'<>;]/g, ""));
    });
    var tblFirmantes;
    function initTable() {
        tblFirmantes = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            paging: true,
            searching: true,
            info: true,
            stateSave: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: 6}],
            pageLength: 15,
            lengthMenu: [[5, 10, 15], [5, 10, 15]],
            //Establecemos el tamaño de las columnas de nuestra tabla
            columns: [null, {width: "15%"}, {width: "15%"}, {width: "15%"}, {width: "15%"}, {width: "30%"}, {width: "10%"}]
        });
    }

    $("#btnNuevoFirmante").click(function () {
        $("#modaltitle").html("Nuevo Firmante");
        $("#btnUpdateFirmante").hide();
        $("#btnRegistrarFirmante").show();
        $("#divConfirmPasswordFirmante").show();
        limpiarCampos();
        abrirCerrarLabel(false);
        enaDisInputs(false);
        resetForm();
        $.ajax({
            url: '../Transporte/queryCFirmantes.jsp',
            data: '&txtBandera=2',
            type: 'POST',
            success: function (response) {
                var indicador = response.split("|")[0];
                if (indicador.toString().trim() === "error") {
                    show_swal("¡Upps!", response.split('|')[1], "error");
                    $('#lstCargoFirmante').html(response.split('|')[2]).trigger("chosen:updated");
                } else {
                    $('#lstCargoFirmante').html(response).trigger("chosen:updated");
                }
            }, complete: function () {
                $("#modal-firmantes").modal("show");
            }
        });
    });

    function TableActions() {
        $('#divTblFirmantes').on('click', '.js-swal-confirm', function () {
            var id = $(this).attr("id").split('_')[1];
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'Al confirmar, los datos no podrán retornar fácilmente',
                confirmButtonText: 'Sí, ¡Elimínalo!',
                showCancelButton: true,
                cancelButtonText: 'Cancelar',
                cancelButtonColor: '#d33',
                showLoaderOnConfirm: true,
                animation: true,
                allowOutsideClick: false,
                allowEscapeKey: false
            }).then(function () {
                $.ajax({
                    url: '../Transporte/queryCFirmantes.jsp',
                    data: '&txtBandera=5&txtIdFirmante=' + id,
                    type: 'POST',
                    success: function (response) {
                        if (response.includes('success')) {
                            swal('¡Registro eliminado!', 'El firmante seleccionado ha sido eliminado', 'success');
                            LoadTable();
                            tblFirmantes.$('[data-toggle="tooltip"]').tooltip('hide');
                        } else {
                            show_swal("¡Upps!", response.split('|')[1], "error");
                        }
                    }, complete: function () {
                        tblFirmantes.$('[data-toggle="tooltip"]').tooltip('hide');
                    }
                });
            }, function () {

            });

        });

        $('#divTblFirmantes').on('click', '.btnConsultarFirmante', function () {
            $('#loadAction').fadeIn();
            var id = $(this).attr("id").split('_')[1];
            $("#idFirmante").val(id);
            $.ajax({
                url: '../Transporte/queryCFirmantes.jsp',
                data: '&txtBandera=6&txtIdFirmante=' + id,
                type: 'POST',
                success: function (response) {
                    let split_resp = response.split("|");
                    if (split_resp[0].includes('success')) {
                        $.ajax({
                            url: '../Transporte/queryCFirmantes.jsp',
                            data: '&txtBandera=2',
                            type: 'POST',
                            success: function (response) {
                                resetForm();

                                var indicador = response.split("|")[0];
                                if (indicador.toString().trim() === "error") {
                                    show_swal("¡Upps!", response.split('|')[1], "error");
                                    $('#lstCargoFirmante').html(response.split('|')[2]).trigger("chosen:updated");
                                } else {
                                    $('#lstCargoFirmante').html(response).trigger("chosen:updated");
                                }

                                //Almacenamos los datos del firmante
                                let datosFirmante = split_resp[1].split("¬");
                                $("#txtNombreFirmante").val(datosFirmante[0]);
                                $("#txtApaternoFirmante").val(datosFirmante[1]);
                                $("#txtAmaternoFirmante").val(datosFirmante[2]);
                                $("#txtCurpFirmante").val(datosFirmante[3]);
                                $("#lstCargoFirmante").val(datosFirmante[4]).trigger("chosen:updated");
                                $("#txtAbrevTitulo").val(datosFirmante[5]);
                                //$("#fileLlaveFirmante").val(datosFirmante[6]);
                                //$("#fileCertificadoFirmante").val(datosFirmante[7]);
                                $("#txtNumCertificado").val(datosFirmante[7]);
                                $("#txtPasswordFirmante").val(datosFirmante[8]);
                                $("#txtFechaValidez").val(datosFirmante[10]);
                                $("#txtFechaExpira").val(datosFirmante[11]);

                                //Agregamos la clase open a cada div que contiene un input text
                                abrirCerrarLabel(true);
                                //Deshabilitamos elementos del Modal
                                enaDisInputs(true);

                                //Ocultamos lo que no ocupamos
                                $("#divConfirmPasswordFirmante").hide();
                                $("#btnUpdateFirmante").hide();
                                $("#btnRegistrarFirmante").hide();

                                //Acciones sobre el modal
                                $("#modaltitle").html("Consultar Firmante");
                                $("#modal-firmantes").modal("show");

                            }, complete: function () {
                                tblFirmantes.$('[data-toggle="tooltip"]').tooltip('hide');
                                $('#loadAction').fadeOut();
                            }
                        });
                    } else {
                        show_swal("¡Upps!", response.split('|')[1], "error");
                    }
                }, complete: function () {
                    $('#loadAction').fadeOut();
                }
            });
        });

        $('#divTblFirmantes').on('click', '.btnEditarFirmante', function () {
            var id = $(this).attr("id").split('_')[1];
            tblFirmantes.$('[data-toggle="tooltip"]').tooltip('hide');
            swal({
                title: '¿Estás seguro?',
                text: "¡Al modificar un registro deberás de cargar la llave y el certificado del firmante de nueva cuenta!",
                type: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '¡Si, editar!'
            }).then(function () {
                $('#loadAction').fadeIn();
                resetForm();
                $("#idFirmante").val(id);
                $.ajax({
                    url: '../Transporte/queryCFirmantes.jsp',
                    data: '&txtBandera=6&txtIdFirmante=' + id,
                    type: 'POST',
                    success: function (response) {
                        let split_resp = response.split("|");
                        if (split_resp[0].includes('success')) {
                            $.ajax({
                                url: '../Transporte/queryCFirmantes.jsp',
                                data: '&txtBandera=2',
                                type: 'POST',
                                success: function (response) {
                                    $('#lstCargoFirmante').html(response).trigger("chosen:updated");

                                    //Almacenamos los datos del firmante
                                    let datosFirmante = split_resp[1].split("¬");
                                    $("#txtNombreFirmante").val(datosFirmante[0]);
                                    $("#txtApaternoFirmante").val(datosFirmante[1]);
                                    $("#txtAmaternoFirmante").val(datosFirmante[2]);
                                    $("#txtCurpFirmante").val(datosFirmante[3]);
                                    $("#lstCargoFirmante").val(datosFirmante[4]).trigger("chosen:updated");
                                    $("#txtAbrevTitulo").val(datosFirmante[5]);
                                    $("#txtNumCertificado").val(datosFirmante[7]);
                                    $("#txtPasswordFirmante").val(datosFirmante[8]);
                                    $("#txtFechaValidez").val(datosFirmante[10]);
                                    $("#txtFechaExpira").val(datosFirmante[11]);

                                    //Agregamos la clase open a cada div que contiene un input text
                                    abrirCerrarLabel(true);

                                    //Deshabilitamos elementos del Modal
                                    enaDisInputs(false);
                                    $("#txtNumCertificado").attr("disabled", true);

                                    //Ocultamos lo que no ocupamos
                                    $("#divConfirmPasswordFirmante").show();
                                    $("#btnUpdateFirmante").show();
                                    $("#btnRegistrarFirmante").hide();

                                    //Acciones sobre el modal
                                    $("#modaltitle").html("Modificar Firmante");
                                    $("#modal-firmantes").modal("show");

                                }, error: function () {
                                    swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico');
                                }, complete: function () {
                                    $('#loadAction').fadeOut();
                                }
                            });
                        } else {
                            show_swal("¡Upps!", response.split('|')[1], "error");
                        }
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            });
        });
    }

    $('.btnAccionesFirmantes').click(function () {
        $("form[name='formFirmantes']").submit(function (e) {
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
                elem.closest('.col-xs-12').removeClass('has-error').addClass('has-error');
                elem.closest('.help-block').remove();
            },
            success: function (e) {
                var elem = jQuery(e);
                elem.closest('.col-xs-12').removeClass('has-error');
                elem.closest('.help-block').remove();
            },
            rules: {
                txtNombreFirmante: {
                    required: true
                },
                txtApaternoFirmante: {
                    required: true
                },
                txtCurpFirmante: {
                    required: true
                },
                lstCargoFirmante: {
                    required: true
                },
                txtAbrevTitulo: {
                    required: true
                },
                fileCertificadoFirmante: {
                    required: true,
                    extension: "cer"
                }, fileLlaveFirmante: {
                    required: true,
                    extension: "key"
                },
                txtPasswordFirmante: {
                    required: true,
                    minlength: 5
                },
                txtConfirmPassFirm: {
                    required: true,
                    equalTo: '#txtPasswordFirmante'
                }
            },
            messages: {
                txtNombreFirmante: "¡Este campo es requerido!",
                txtApaternoFirmante: "¡Este campo es requerido!",
                txtCurpFirmante: "¡Este campo es requerido!",
                lstCargoFirmante: "¡Este campo es requerido!",
                txtAbrevTitulo: "¡Este campo es requerido!",
                fileCertificadoFirmante: {
                    required: "¡Por favor selecciona un archivo!",
                    extension: "¡El formato del archivo no es el adecuado!"
                },
                fileLlaveFirmante: {
                    required: "¡Por favor selecciona un archivo!",
                    extension: "¡El formato del archivo no es el adecuado!"
                },
                txtPasswordFirmante: {
                    required: "¡Este campo es requerido!",
                    minlength: "¡La contraseña debe ser al menos de 5 caracteres!"
                },
                txtConfirmPassFirm: {
                    required: "¡Este campo es requerido!",
                    equalTo: "¡Las contraseñas no son iguales!"
                }
            },
            submitHandler: function (form) {
                var bandera = $('#bandera').val();
                $("#noCertificado").val($("#txtNumCertificado").val());
                $("#fechaValidez").val($("#txtFechaValidez").val());
                $("#fechaExpira").val($("#txtFechaExpira").val());
                $.ajax({
                    url: '../Transporte/queryCFirmantes.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    success: function (response) {
                        if (bandera.includes("3")) {
                            if (response.includes("success")) {
                                swal('¡Completado!', '¡El firmante fue registrado con éxito!', 'success');
                                limpiarCampos();
                                $("#modal-firmantes").modal("hide");
                                LoadTable();
                            } else {
                                show_swal("¡Upps!", response.split('|')[1], "error");
                            }
                        } else if (bandera.includes("4")) {
                            if (response.includes("success")) {
                                swal('¡Completado!', '¡El firmante fue actualizado con éxito!', 'success');
                                limpiarCampos();
                                $("#modal-firmantes").modal("hide");
                                LoadTable();
                            } else {
                                show_swal("¡Upps!", response.split('|')[1], "error");
                            }
                        }
                    }
                });
            }
        });
    });


    /**
     * @author Braulio Sorcia
     * @description Habilita o deshabilita los inputs del modal firmantes
     * @param {boolean} e Bandera para habilitar/deshabilitar los inputs del modal firmantes. true para deshabilitar, false para habilitar
     * 
     */
    function enaDisInputs(e) {
        $("#txtNombreFirmante").attr("disabled", e);
        $("#txtApaternoFirmante").attr("disabled", e);
        $("#txtAmaternoFirmante").attr("disabled", e);
        $("#txtCurpFirmante").attr("disabled", e);
        $("#lstCargoFirmante").prop("disabled", e).trigger("chosen:updated");
        $("#txtAbrevTitulo").attr("disabled", e);
        $("#fileLlaveFirmante").attr("disabled", e);
        $("#fileCertificadoFirmante").attr("disabled", e);
        $("#txtPasswordFirmante").attr("disabled", e);
    }


    $("#fileCertificadoFirmante").on("change", function () {
        let pathArchivo = $("#fileCertificadoFirmante").val();
        let nameFile = pathArchivo.replace(/^.*[\\\/]/, '');
        let fileExt = nameFile.split('.').pop();
        if (fileExt.indexOf('cer') >= 0) {
            var formData = new FormData();
            formData.append("bandera", "10");
            let file = document.getElementById("fileCertificadoFirmante");
            formData.append("fileCertificadoFirmante", file.files[0]);
            $("#loadAction").fadeIn();
            $.ajax({
                url: '../Transporte/queryCFirmantes.jsp',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    if (response.split("|")[0].toString().trim() === "error") {
                        show_swal("¡Upps!", response.split("|")[1], 'error');
                    } else {
                        var numCertificado = response.split("||")[0];
                        var fechaValidez = response.split("||")[1];
                        var fechaExpir = response.split("||")[2];


                        $("#divNumCertificadoFirmIn").addClass("open");
                        $("#txtNumCertificado").val(numCertificado);

                        $("#divFechaValidezFirmIn").addClass("open");
                        $("#txtFechaValidez").val(fechaValidez);

                        $("#divFechaExpirFirmIn").addClass("open");
                        $("#txtFechaExpira").val(fechaExpir);
                    }
                }, complete: function () {
                    $("#loadAction").fadeOut();
                }
            });
        }
    });

    /**
     * @author Braulio Sorcia
     * @description Limpia los campos en el formulario firmantes
     */
    function limpiarCampos() {
        $("#txtNombreFirmante").val("");
        $("#txtApaternoFirmante").val("");
        $("#txtAmaternoFirmante").val("");
        $("#txtCurpFirmante").val("");
        $("#lstCargoFirmante").val("").trigger("chosen:updated");
        $("#txtAbrevTitulo").val("");
        $("#txtNumCertificado").val("");
        $("#txtPasswordFirmante").val("");
        $("#txtConfirmPassFirm").val("");
        $("#fileLlaveFirmante").val("");
        $("#fileCertificadoFirmante").val("");
        $("#txtFechaValidez").val("");
        $("#txtFechaExpira").val("");
    }

    /**
     * @author Braulio Sorcia
     * @param {boolean} e true si agrega la clase o false si la remueve 
     * @description Agrega o quita la clase open a los div contenedores de los input del formulario Firmantes
     */
    function abrirCerrarLabel(e) {
        if (e) {
            $("#divNombreFirmanteIn").addClass('open');
            $("#divApaternoFirmanteIn").addClass('open');
            $("#divAmaternoFirmanteIn").addClass('open');
            $("#divCurpFirmanteIn").addClass('open');
            $("#divNumCertificadoFirmIn").addClass('open');
            $("#divPasswordFirmanteIn").addClass('open');
            $("#divAbrTituloIn").addClass('open');
            $("#divFechaValidezFirmIn").addClass('open');
            $("#divFechaExpirFirmIn").addClass('open');
        } else {
            $("#divNombreFirmanteIn").removeClass('open');
            $("#divApaternoFirmanteIn").removeClass('open');
            $("#divAmaternoFirmanteIn").removeClass('open');
            $("#divCurpFirmanteIn").removeClass('open');
            $("#divNumCertificadoFirmIn").removeClass('open');
            $("#divPasswordFirmanteIn").removeClass('open');
            $("#divConfirmPasswordFirmanteIn").removeClass('open');
            $("#divAbrTituloIn").removeClass('open');
            $("#divFechaValidezFirmIn").removeClass('open');
            $("#divFechaExpirFirmIn").removeClass('open');
        }
    }

    /**
     * @author Braulio Sorcia
     * @description Reinicializa el formulario y elimina la clase has-error del mismo.
     */
    function resetForm() {
        $("#formFirmantes div").removeClass("has-error");
        $("#formFirmantes div").remove(".help-block");
    }

    function resize() {
        window.onresize = function () {
            ajustarComponentesModal();
        };

        window.onload = function () {
            ajustarComponentesModal();
        };

        function ajustarComponentesModal() {
            if (window.innerWidth <= 767) {
                $("#divCurpFirmante").css("margin-top", "-15px");
                $("#divCargoFirmante").css("margin-top", "15px");
                $("#divAbrTitulo").css("padding-top", "20px");
                $("#divPasswordFirmante").css("margin-top", "-15px");
            } else {
                $("#divCurpFirmante").css("margin-top", "");
                $("#divCargoFirmante").css("margin-top", "");
                $("#divAbrTitulo").css("padding-top", "");
                $("#divPasswordFirmante").css("margin-top", "");
            }
        }
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
                    LoadTable();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        LoadTable();
                        if (stringStepFirst.split("¬")[1].includes("0")) {
                            $("#btnNuevoFirmante").remove();
                            $("#btnRegistrarFirmante").remove();
                        }
                        if (stringStepFirst.split("¬")[3].includes("0")) {
                            $("#btnUpdateFirmante").remove();
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

    $("#lstCargoFirmante").on('change', function () {
        document.getElementById("divCargoFirmante").className = document.getElementById("divCargoFirmante").className.replace(/(?:^|\s)has-error(?!\S)/g, '');
        document.getElementById("divCargoFirmante").className = document.getElementById("divCargoFirmante").className.replace(/(?:^|\s)form-material-primary(?!\S)/g, '');
        document.getElementById("divCargoFirmante").className += " form-material-primary";
        $('#lstCargoFirmante-error').remove();
    });

});