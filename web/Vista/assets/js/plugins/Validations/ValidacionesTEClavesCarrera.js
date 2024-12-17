/* Version	: DIGISEP GENERAL */
$(document).ready(function () {
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
            columnDefs: [{orderable: false, targets: 5}],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]],
            "columns": [
                null,
                {"width": "10%"},
                {"width": "35%"},
                {"width": "15%"},
                {"width": "30%"},
                {"width": "10%"}
            ]
        });
        $('[data-toggle="tooltip"]').tooltip({
            container: 'body',
            animation: false,
            trigger: 'hover'
        });
        $("#tblCarreras_filter").children().children().children().keyup(function (e) {
            $('.tooltip').remove();
        });
    }

    $("#lstAutorizaciones").chosen({width: "100%", disable_search_threshold: 4});
    $("#lstTipoPeriodo").chosen({width: "100%", disable_search_threshold: 4});
    $("#lstNivelEducativo").chosen({width: "100%", disable_search_threshold: 4});
    $("#lstEntidadFederativa").chosen({width: "100%", disable_search_threshold: 4});

//    $(".notAllowed").keyup(function () {
//        let element = $(this);
//        element.val(element.val().replace(/[&"'<>;]/g, ""));
//    });
//    $(".notAllowed").on("paste", function () {
//        let element = $(this);
//        element.val(element.val().replace(/[&"'<>;]/g, ""));
//    });

    function LoadTable() {
        $.ajax({
            url: '../Transporte/queryCClavesCarrera.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var tabla = resp.split('~')[0];
                var lstAutorizaciones = resp.split('~')[1];
                var lstTiposPeriodo = resp.split('~')[2];
                var lstNivelEducativo = resp.split('~')[3];
                var lstEntidadFederativa = resp.split('~')[4];

                if (tabla.split("|")[0].toString().trim() === 'error') {
                    show_swal('¡Upps!', tabla.split("|")[1], 'error');
                } else if (lstAutorizaciones.split("|")[0].toString().trim() === 'error') {
                    show_swal('¡Upps!', lstAutorizaciones.split("|")[1], 'error');
                } else if (lstTiposPeriodo.split("|")[0].toString().trim() === 'error') {
                    show_swal('¡Upps!', lstTiposPeriodo.split("|")[1], 'error');
                } else if (lstNivelEducativo.split("|")[0].toString().trim() === 'error') {
                    show_swal('¡Upps!', lstNivelEducativo.split("|")[1], 'error');
                } else if (lstEntidadFederativa.split("|")[0].toString().trim() === 'error') {
                    show_swal('¡Upps!', lstEntidadFederativa.split("|")[1], 'error');
                }

                $('#DivTblCarreras').html(tabla.split("|")[1]);
                $('#lstAutorizaciones').html(lstAutorizaciones.split("|")[1]).trigger('chosen:updated');
                $('#lstTipoPeriodo').html(lstTiposPeriodo.split("|")[1]).trigger('chosen:updated');
                $('#lstNivelEducativo').html(lstNivelEducativo.split("|")[1]).trigger('chosen:updated');
                $('#lstEntidadFederativa').html(lstEntidadFederativa.split("|")[1]).trigger('chosen:updated');
                $('[data-toggle="tooltip"]').tooltip();
                TableActions();
                initTable();
                $("#tblCarreras_filter input").val("").trigger("keyup");
                setTimeout(function () {
                    $('#mainLoader').fadeOut();
                });
            }
        });
    }

    $('#btnNuevaCarrera').click(function () {
        $('#ButtonUpdateCarrera').hide();
        $('#ButtonAddCarrera').show();
        $('#ButtonCerrarCarrera').hide();
        $('#modaltitle').text('NUEVA CLAVE CARRERA');
        BlockCampos(false);
        CleanCampos();
    });

    $('.BtnAccionesClaveCarrea').click(function () {
        $("form[name='FormClaveCarrera']").submit(function (e) {
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
                elem.closest('.col-lg-6').removeClass('has-error').addClass('has-error');
                elem.closest('.col-lg-4').removeClass('has-error').addClass('has-error');
                elem.closest('.col-lg-8').removeClass('has-error').addClass('has-error');
                elem.closest('.col-xs-12').removeClass('has-error').addClass('has-error');
                elem.closest('.help-block').remove();
            },
            success: function (e) {
                var elem = jQuery(e);
                elem.closest('.col-lg-6').removeClass('has-error');
                elem.closest('.col-lg-4').removeClass('has-error');
                elem.closest('.col-lg-8').removeClass('has-error');
                elem.closest('.col-xs-12').removeClass('has-error');
                elem.closest('.help-block').remove();
            },
            rules: {
                txtIdCarreraExcel: {
                    required: true
                },
                txtClaveCarrera: {
                    required: true,
                    minlength: 5,
                    maxlength: 7
                },
                txtNombreCarrera: {
                    required: true
                },
                txtClavePlanCarrera: {
                    required: true
                },
                txtClaveInstitucion: {
                    required: true,
                    minlength: 5,
                    maxlength: 7
                },
                txtNombreInstitucion: {
                    required: true
                },
                txtClaveCampus: {
                    required: true
                },
                txtNombreCampus: {
                    required: true
                },
                lstAutorizaciones: {
                    required: true
                },
                txtRvoe: {
                    maxlength: 100
                },
                lstTipoPeriodo: {
                    required: true
                },
                lstNivelEducativo: {
                    required: true
                },
                txtTotalMaterias: {
                    required: true,
                    min: 0
                },
                txtTotalCreditos: {
                    required: true,
                    min: 0
                },
                lstEntidadFederativa: {
                    required: true
                },
                txtFechaExpedicionRvoe: {
                    required: true
                },
                txtCalifMin: {
                    required: true,
                    min: 0,
                    maxlength: 5
                },
                txtCalifMax: {
                    required: true,
                    min: 0,
                    maxlength: 6
                },
                txtCalifMinAprob: {
                    required: true,
                    min: 0,
                    maxlength: 5
                },
                txtGradoObtenidoM: {
                    required: true
                },
                txtGradoObtenidoF: {
                    required: true
                },
                'txt-num-decimales': {
                    required: true,
                    min: 1
                }
            },
            messages: {
                txtIdCarreraExcel: {
                    required: '¡Por favor completa este campo!'
                },
                txtClaveCarrera: {
                    required: '¡Por favor completa este campo!',
                    minlength: '¡Por favor ingresa al menos 5 caracteres!',
                    maxlength: '¡Ingresa máximo 7 caracteres!'
                },
                txtNombreCarrera: {
                    required: '¡Por favor completa este campo!'
                },
                txtClavePlanCarrera: {
                    required: '¡Por favor completa este campo!'
                },
                txtClaveInstitucion: {
                    required: '¡Por favor completa este campo!',
                    minlength: '¡Por favor ingresa al menos 5 caracteres!',
                    maxlength: '¡Ingresa máximo 7 caracteres!'
                },
                txtNombreInstitucion: {
                    required: '¡Por favor completa este campo!'
                },
                txtClaveCampus: {
                    required: '¡Por favor completa este campo!'
                },
                txtNombreCampus: {
                    requireD: '¡Por favor completa este campo!'
                },
                lstAutorizaciones: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtRvoe: {
                    maxlength: '¡Ingresa máximo 100 caracteres!'
                },
                lstTipoPeriodo: {
                    required: '¡Por favor selecciona una opción!'
                },
                lstNivelEducativo: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtTotalMaterias: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 4 caracteres!'
                },
                txtTotalCreditos: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 7 caracteres!'
                },
                lstEntidadFederativa: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtFechaExpedicionRvoe: {
                    required: '¡Por favor selecciona una fecha!'
                },
                txtCalifMin: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 1 caracter!'
                },
                txtCalifMax: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 1 caracter!'
                },
                txtCalifMinAprob: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 4 caracteres!'
                },
                txtGradoObtenidoM: '¡Por favor completa este campo!',
                txtGradoObtenidoF: '¡Por favor completa este campo!',
                'txt-num-decimales': {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                }
            },
            submitHandler: function (form) {
                $('#loadAction').fadeIn();
                var bandera = $('#txtBandera').val();
                var claveCarrera = $('#txtClaveCarrera').val();
                var nombreCarrera = $('#txtNombreCarrera').val();
                var claveInstitucion = $('#txtClaveInstitucion').val();
                var nombreInstitucion = $('#txtNombreInstitucion').val();
                var claveCampus = $('#txtClaveCampus').val();
                var nombreCampus = $('#txtNombreCampus').val();
                var idAutorizacion = $('#lstAutorizaciones').val();
                var noRvoe = $('#txtRvoe').val();
                var idTipoPeriodo = $('#lstTipoPeriodo').val();
                var idNivelEducativo = $('#lstNivelEducativo').val();
                var totalMaterias = $('#txtTotalMaterias').val();
                var idEntidadFederativa = $('#lstEntidadFederativa').val();
                var fechaExpedicionRvoe = $('#txtFechaExpedicionRvoe').val();
                var idCarrera = $('#idCarrera').val();
                var gradoObtenidoM = $("#txtGradoObtenidoM").val();
                var gradoObtenidoF = $("#txtGradoObtenidoF").val();
                var txtIdCarreraExcel = $("#txtIdCarreraExcel").val();
                var txtCalifMin = $('#txtCalifMin').val();
                var txtCalifMax = $('#txtCalifMax').val();
                var txtCalifMinAprob = $('#txtCalifMinAprob').val();
                var txtCvePlanCarrera = $('#txtClavePlanCarrera').val();
                var txtTotalCreditos = $("#txtTotalCreditos").val();
                var txtNumDecimales = $("#txt-num-decimales").val();
                $.ajax({
                    url: '../Transporte/queryCClavesCarrera.jsp',
                    data: {
                        txtBandera: bandera,
                        claveCarrera: claveCarrera,
                        nombreCarrera: nombreCarrera,
                        claveInstitucion: claveInstitucion,
                        nombreInstitucion: nombreInstitucion,
                        claveCampus: claveCampus,
                        nombreCampus: nombreCampus,
                        idAutorizacion: idAutorizacion,
                        noRvoe: noRvoe,
                        idTipoPeriodo: idTipoPeriodo,
                        idNivel: idNivelEducativo,
                        totalMaterias: totalMaterias,
                        idEntidadFederativa: idEntidadFederativa,
                        fechaExpedicionRvoe: fechaExpedicionRvoe,
                        idCarrera: idCarrera,
                        gradoObtenidoM: gradoObtenidoM,
                        gradoObtenidoF: gradoObtenidoF,
                        idCarreraExcel: txtIdCarreraExcel,
                        califMin: txtCalifMin,
                        califMax: txtCalifMax,
                        califMinAprobatoria: txtCalifMinAprob,
                        cvePlanCarrera: txtCvePlanCarrera,
                        totalCreditos: txtTotalCreditos,
                        numDecimales: txtNumDecimales
                    },
                    type: 'POST',
                    success: function (resp) {
                        if (bandera == 2) {
                            if (resp.includes('success')) {
                                LoadTable();
                                $('#modal-claveCarrera').modal('hide');
                                swal('¡Clave Agregada!', 'La clave de la carrera ha sido agregada', 'success');
                            } else if (resp.includes('claveExistente')) {
                                swal('¡Upps!', 'La clave ingresada ya existe en el sistema, intente de nuevo', 'warning');
                            } else {
                                swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                            }
                        } else if (bandera == 3) {
                            if (resp.includes('success')) {
                                LoadTable();
                                $('#modal-claveCarrera').modal('hide');
                                swal('¡Clave guardada!', 'La clave de la carrera ha sido modificada con éxito', 'success');
                            } else if (resp.includes('claveExistente')) {
                                swal('¡Upps!', 'El Id de carrera ingresado ya está en uso, intenta nuevamente', 'warning');
                            } else {
                                show_swal('¡Upps!', resp.split("|")[1], 'error')
                            }
                        }
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });


    function TableActions() {
        $('#tblCarreras').on('click', '.btnConsultarCarrera', function () {
            $("#loadAction").fadeIn();
            $('#ButtonUpdateCarrera').hide();
            $('#ButtonAddCarrera').hide();
            $('#ButtonCerrarCarrera').show();
            $('#modaltitle').text('CONSULTAR CLAVE CARRERA');
            CleanCampos();
            var buttonVal = $(this).val().split('_');
            $.ajax({
                url: '../Transporte/queryCClavesCarrera.jsp',
                data: '&txtBandera=4&idCarrera=' + buttonVal[1],
                type: 'POST',
                success: function (resp) {
                    if (resp.split("|")[0].toString().trim() === 'error') {
                        show_swal('¡Upps!', resp.split("|")[1], 'error');
                    } else {
                        var split = resp.split('~');

                        $('#idCarrera').val(split[1]);
                        $('#txtClaveInstitucion').val(split[2]);
                        $('#txtNombreInstitucion').val(split[3]);
                        $('#txtClaveCarrera').val(split[4]);
                        $('#txtNombreCarrera').val(split[5]);
                        $('#lstAutorizaciones').val(split[6]).trigger('chosen:updated');
                        $('#txtRvoe').val(split[7]);
                        $('#txtClaveCampus').val(split[8]);
                        $('#txtNombreCampus').val(split[9]);
                        $('#lstTipoPeriodo').val(split[10]).trigger('chosen:updated');
                        $('#lstNivelEducativo').val(split[11]).trigger('chosen:updated');
                        $('#lstEntidadFederativa').val(split[12]).trigger('chosen:updated');
                        $('#txtFechaExpedicionRvoe').datepicker('setDate', split[13]);
                        $('#txtTotalMaterias').val(split[14]);
                        $('#txtTotalCreditos').val(split[15]).trigger("change");
                        $('#txtGradoObtenidoM').val(split[16]);
                        $('#txtGradoObtenidoF').val(split[17]);
                        $('#txtIdCarreraExcel').val(split[18]);
                        $('#txtCalifMin').val(split[19]);
                        $('#txtCalifMax').val(split[20]);
                        $('#txtCalifMinAprob').val(split[21]);
                        $("#txtClavePlanCarrera").val(split[22]);
                        $("#txt-num-decimales").val(split[23]).trigger("change");
                        BlockCampos(true);
                        $('#modal-claveCarrera').modal('show');
                    }
                }, complete: function (jqXHR, textStatus) {
                    tblCarreras.$("[data-toggle='tooltip']").tooltip("hide");
                    $("#loadAction").fadeOut();
                }
            });
        });


        $('#tblCarreras').on('click', '.btnEditarCarrera', function () {
            $('#loadAction').fadeIn();
            $('#ButtonUpdateCarrera').show();
            $('#ButtonAddCarrera').hide();
            $('#ButtonCerrarCarrera').hide();
            $('#modaltitle').text('EDITAR CLAVE CARRERA');
            CleanCampos();
            var buttonVal = $(this).val().split('_');
            $.ajax({
                url: '../Transporte/queryCClavesCarrera.jsp',
                data: '&txtBandera=4&idCarrera=' + buttonVal[1],
                type: 'POST',
                success: function (resp) {
                    if (resp.split("|")[0].toString().trim() === 'error') {
                        show_swal('¡Upps!', resp.split("|")[1], 'error');
                    } else {
                        var split = resp.split('~');
                        $('#idCarrera').val(split[1]);
                        $('#txtClaveInstitucion').val(split[2]);
                        $('#txtNombreInstitucion').val(split[3]);
                        $('#txtClaveCarrera').val(split[4]);
                        $('#txtNombreCarrera').val(split[5]);
                        $('#lstAutorizaciones').val(split[6]).trigger('chosen:updated');
                        $('#txtRvoe').val(split[7]);
                        $('#txtClaveCampus').val(split[8]);
                        $('#txtNombreCampus').val(split[9]);
                        $('#lstTipoPeriodo').val(split[10]).trigger('chosen:updated');
                        $('#lstNivelEducativo').val(split[11]).trigger('chosen:updated');
                        $('#lstEntidadFederativa').val(split[12]).trigger('chosen:updated');
                        $('#txtFechaExpedicionRvoe').datepicker('setDate', split[13]);
                        $('#txtTotalMaterias').val(split[14]);
                        $('#txtTotalCreditos').val(split[15]).trigger("change");
                        $('#txtGradoObtenidoM').val(split[16]);
                        $('#txtGradoObtenidoF').val(split[17]);
                        $('#txtIdCarreraExcel').val(split[18]);
                        $('#txtCalifMin').val(split[19]);
                        $('#txtCalifMax').val(split[20]);
                        $('#txtCalifMinAprob').val(split[21]);
                        $("#txtClavePlanCarrera").val(split[22]);
                        $("#txt-num-decimales").val(split[23]).trigger("change");
                        BlockCampos(false);
                        $('#modal-claveCarrera').modal('show');
                    }
                }, complete: function (jqXHR, textStatus) {
                    tblCarreras.$("[data-toggle='tooltip']").tooltip("hide");
                    $('#loadAction').fadeOut();
                }
            });
        });
    }

    /*$('#CBNomInstitucion').click(function () {
     if ($('input#CBNomInstitucion').is(':checked')) {
     $('#ButtonGuardarNomInstitucion').removeAttr("disabled");
     $('#ButtonGuardarNomInstitucion').attr('enabled', 'enabled');
     } else {
     $('#ButtonGuardarNomInstitucion').removeAttr("enabled");
     $('#ButtonGuardarNomInstitucion').attr('disabled', 'disabled');
     }  
     });*/
    $('#btnNomInstitucion').click(function () {
        var bandera = $('#txtBandera').val();
        $.ajax({
            url: '../Transporte/queryCClavesCarrera.jsp',
            data: {
                txtBandera: 6
            },
            type: 'POST',
            success: function (resp) {
                if (bandera == 6) {
                    if (resp.includes('success')) {
                        if (resp.includes('0')) {
                            $('#CBNomInstitucion').removeAttr("checked");
                        }
                    } else {
                        show_swal('¡Upps!', resp.split("|")[1], 'error')
                    }
                }
                console.log("funcion 1");
            }, complete: function () {
                $('#loadAction').fadeOut();
            }
        });

    });



    $('.BtnAccionesClaveCarrea').click(function () {
        $("form[name='FormClaveCarrera']").submit(function (e) {
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
                elem.closest('.col-lg-6').removeClass('has-error').addClass('has-error');
                elem.closest('.col-lg-4').removeClass('has-error').addClass('has-error');
                elem.closest('.col-lg-8').removeClass('has-error').addClass('has-error');
                elem.closest('.col-xs-12').removeClass('has-error').addClass('has-error');
                elem.closest('.help-block').remove();
            },
            success: function (e) {
                var elem = jQuery(e);
                elem.closest('.col-lg-6').removeClass('has-error');
                elem.closest('.col-lg-4').removeClass('has-error');
                elem.closest('.col-lg-8').removeClass('has-error');
                elem.closest('.col-xs-12').removeClass('has-error');
                elem.closest('.help-block').remove();
            },
            rules: {
                txtIdCarreraExcel: {
                    required: true
                },
                txtClaveCarrera: {
                    required: true,
                    minlength: 5,
                    maxlength: 7
                },
                txtNombreCarrera: {
                    required: true
                },
                txtClavePlanCarrera: {
                    required: true
                },
                txtClaveInstitucion: {
                    required: true,
                    minlength: 5,
                    maxlength: 7
                },
                txtNombreInstitucion: {
                    required: true
                },
                txtClaveCampus: {
                    required: true
                },
                txtNombreCampus: {
                    required: true
                },
                lstAutorizaciones: {
                    required: true
                },
                txtRvoe: {
                    maxlength: 100
                },
                lstTipoPeriodo: {
                    required: true
                },
                lstNivelEducativo: {
                    required: true
                },
                txtTotalMaterias: {
                    required: true,
                    min: 0
                },
                txtTotalCreditos: {
                    required: true,
                    min: 0
                },
                lstEntidadFederativa: {
                    required: true
                },
                txtFechaExpedicionRvoe: {
                    required: true
                },
                txtCalifMin: {
                    required: true,
                    min: 0,
                    maxlength: 2
                },
                txtCalifMax: {
                    required: true,
                    min: 0,
                    maxlength: 2
                },
                txtCalifMinAprob: {
                    required: true,
                    min: 0,
                    maxlength: 4
                },
                txtGradoObtenidoM: {
                    required: true
                },
                txtGradoObtenidoF: {
                    required: true
                },
                'txt-num-decimales': {
                    required: true,
                    min: 1
                }
            },
            messages: {
                txtIdCarreraExcel: {
                    required: '¡Por favor completa este campo!'
                },
                txtClaveCarrera: {
                    required: '¡Por favor completa este campo!',
                    minlength: '¡Por favor ingresa al menos 5 caracteres!',
                    maxlength: '¡Ingresa máximo 7 caracteres!'
                },
                txtNombreCarrera: {
                    required: '¡Por favor completa este campo!'
                },
                txtClavePlanCarrera: {
                    required: '¡Por favor completa este campo!'
                },
                txtClaveInstitucion: {
                    required: '¡Por favor completa este campo!',
                    minlength: '¡Por favor ingresa al menos 5 caracteres!',
                    maxlength: '¡Ingresa máximo 7 caracteres!'
                },
                txtNombreInstitucion: {
                    required: '¡Por favor completa este campo!'
                },
                txtClaveCampus: {
                    required: '¡Por favor completa este campo!'
                },
                txtNombreCampus: {
                    requireD: '¡Por favor completa este campo!'
                },
                lstAutorizaciones: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtRvoe: {
                    maxlength: '¡Ingresa máximo 100 caracteres!'
                },
                lstTipoPeriodo: {
                    required: '¡Por favor selecciona una opción!'
                },
                lstNivelEducativo: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtTotalMaterias: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 4 caracteres!'
                },
                txtTotalCreditos: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 7 caracteres!'
                },
                lstEntidadFederativa: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtFechaExpedicionRvoe: {
                    required: '¡Por favor selecciona una fecha!'
                },
                txtCalifMin: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 1 caracter!'
                },
                txtCalifMax: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 1 caracter!'
                },
                txtCalifMinAprob: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                    maxlength: '¡Ingresa máximo 4 caracteres!'
                },
                txtGradoObtenidoM: '¡Por favor completa este campo!',
                txtGradoObtenidoF: '¡Por favor completa este campo!',
                'txt-num-decimales': {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingrese un número mayor o igual a 0!',
                }
            },
            submitHandler: function (form) {
                $('#loadAction').fadeIn();
                var bandera = $('#txtBandera').val();
                var claveCarrera = $('#txtClaveCarrera').val();
                var nombreCarrera = $('#txtNombreCarrera').val();
                var claveInstitucion = $('#txtClaveInstitucion').val();
                var nombreInstitucion = $('#txtNombreInstitucion').val();
                var claveCampus = $('#txtClaveCampus').val();
                var nombreCampus = $('#txtNombreCampus').val();
                var idAutorizacion = $('#lstAutorizaciones').val();
                var noRvoe = $('#txtRvoe').val();
                var idTipoPeriodo = $('#lstTipoPeriodo').val();
                var idNivelEducativo = $('#lstNivelEducativo').val();
                var totalMaterias = $('#txtTotalMaterias').val();
                var idEntidadFederativa = $('#lstEntidadFederativa').val();
                var fechaExpedicionRvoe = $('#txtFechaExpedicionRvoe').val();
                var idCarrera = $('#idCarrera').val();
                var gradoObtenidoM = $("#txtGradoObtenidoM").val();
                var gradoObtenidoF = $("#txtGradoObtenidoF").val();
                var txtIdCarreraExcel = $("#txtIdCarreraExcel").val();
                var txtCalifMin = $('#txtCalifMin').val();
                var txtCalifMax = $('#txtCalifMax').val();
                var txtCalifMinAprob = $('#txtCalifMinAprob').val();
                var txtCvePlanCarrera = $('#txtClavePlanCarrera').val();
                var txtTotalCreditos = $("#txtTotalCreditos").val();
                var txtNumDecimales = $("#txt-num-decimales").val();
                $.ajax({
                    url: '../Transporte/queryCClavesCarrera.jsp',
                    data: {
                        txtBandera: bandera,
                        claveCarrera: claveCarrera,
                        nombreCarrera: nombreCarrera,
                        claveInstitucion: claveInstitucion,
                        nombreInstitucion: nombreInstitucion,
                        claveCampus: claveCampus,
                        nombreCampus: nombreCampus,
                        idAutorizacion: idAutorizacion,
                        noRvoe: noRvoe,
                        idTipoPeriodo: idTipoPeriodo,
                        idNivel: idNivelEducativo,
                        totalMaterias: totalMaterias,
                        idEntidadFederativa: idEntidadFederativa,
                        fechaExpedicionRvoe: fechaExpedicionRvoe,
                        idCarrera: idCarrera,
                        gradoObtenidoM: gradoObtenidoM,
                        gradoObtenidoF: gradoObtenidoF,
                        idCarreraExcel: txtIdCarreraExcel,
                        califMin: txtCalifMin,
                        califMax: txtCalifMax,
                        califMinAprobatoria: txtCalifMinAprob,
                        cvePlanCarrera: txtCvePlanCarrera,
                        totalCreditos: txtTotalCreditos,
                        numDecimales: txtNumDecimales
                    },
                    type: 'POST',
                    success: function (resp) {
                        if (bandera == 2) {
                            if (resp.includes('success')) {
                                LoadTable();
                                $('#modal-claveCarrera').modal('hide');
                                swal('¡Clave Agregada!', 'La clave de la carrera ha sido agregada', 'success');
                            } else if (resp.includes('claveExistente')) {
                                swal('¡Upps!', 'La clave ingresada ya existe en el sistema, intente de nuevo', 'warning');
                            } else {
                                swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                            }
                        } else if (bandera == 3) {
                            if (resp.includes('success')) {
                                LoadTable();
                                $('#modal-claveCarrera').modal('hide');
                                swal('¡Clave guardada!', 'La clave de la carrera ha sido modificada con éxito', 'success');
                            } else if (resp.includes('claveExistente')) {
                                swal('¡Upps!', 'El Id de carrera ingresado ya está en uso, intenta nuevamente', 'warning');
                            } else {
                                show_swal('¡Upps!', resp.split("|")[1], 'error')
                            }
                        }
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });

    function CleanCampos() {
        $('#txtBandera').val('0');
        $('#txtClaveCarrera').val('');
        $('#txtNombreCarrera').val('');
        $('#txtClaveInstitucion').val('');
        $('#txtNombreInstitucion').val('');
        $('#txtClaveCampus').val('');
        $('#txtNombreCampus').val('');
        $('#lstAutorizaciones').val('').trigger('chosen:updated');
        $('#txtRvoe').val('');
        $('#txtFechaExpedicionRvoe').datepicker('setDate', null);
        $('#lstTipoPeriodo').val('').trigger('chosen:updated');
        $('#lstNivelEducativo').val('').trigger('chosen:updated');
        $('#lstEntidadFederativa').val('').trigger('chosen:updated');
        $('#txtTotalMaterias').val('');
        $("#txtTotalCreditos").val('').trigger("change");
        $('#txtGradoObtenidoM').val('');
        $('#txtGradoObtenidoF').val('');
        $('#txtCalifMin').val('');
        $('#txtCalifMax').val('');
        $('#txtCalifMinAprob').val('');
        $("#txtClavePlanCarrera").val('');
        $('.has-error').removeClass('has-error');
        $('.help-block').remove();
        $('#DivTxtClaveCarrera').removeClass('open');
        $('#DivTxtNombreCarrera').removeClass('open');
        $('#DivTxtClaveInstitucion').removeClass('open');
        $('#DivTxtNombreInstitucion').removeClass('open');
        $('#DivTxtClaveCampus').removeClass('open');
        $('#DivTxtNombreCampus').removeClass('open');
        $('#DivTxtRvoe').removeClass('open');
        $('#DivTxtTotalMaterias').removeClass('open');
        $("#DivGradoObtenidoMIn").removeClass('open');
        $("#DivGradoObtenidoFIn").removeClass('open');
        $('#DivTxtCalifMinIn').removeClass('open');
        $('#DivTxtCalifMaxIn').removeClass('open');
        $('#DivTxtCalifMinAprobIn').removeClass('open');
        $('#DivTxtClavePlanCarrera').removeClass('open');
    }

    function BlockCampos(Cond) {
        $('#txtClaveCarrera').attr('disabled', Cond);
        $('#txtNombreCarrera').attr('disabled', Cond);
        $('#txtClaveInstitucion').attr('disabled', Cond);
        $('#txtNombreInstitucion').attr('disabled', Cond);
        $('#txtClaveCampus').attr('disabled', Cond);
        $('#txtNombreCampus').attr('disabled', Cond);
        $('#txtRvoe').attr('disabled', Cond);
        $('#txtTotalMaterias').attr('disabled', Cond);
        $('#lstAutorizaciones').prop('disabled', Cond).trigger('chosen:updated');
        $('#lstTipoPeriodo').prop('disabled', Cond).trigger('chosen:updated');
        $('#lstNivelEducativo').prop('disabled', Cond).trigger('chosen:updated');
        $('#lstEntidadFederativa').prop('disabled', Cond).trigger('chosen:updated');
        $('#txtGradoObtenidoM').prop('disabled', Cond);
        $('#txtGradoObtenidoF').prop('disabled', Cond);
        $('#txtIdCarreraExcel').prop('disabled', Cond);
        $('#txtCalifMin').prop('disabled', Cond);
        $('#txtCalifMax').prop('disabled', Cond);
        $('#txtCalifMinAprob').prop('disabled', Cond);
        $('#txtClavePlanCarrera').prop('disabled', Cond);
        $("#txtTotalCreditos").prop('disabled', Cond);
        $("#txt-num-decimales").prop('disabled', Cond);

        $('#DivTxtClaveCarrera').removeClass('open').addClass('open');
        $('#DivTxtNombreCarrera').removeClass('open').addClass('open');
        $('#DivTxtClaveInstitucion').removeClass('open').addClass('open');
        $('#DivTxtNombreInstitucion').removeClass('open').addClass('open');
        $('#DivTxtClaveCampus').removeClass('open').addClass('open');
        $('#DivTxtNombreCampus').removeClass('open').addClass('open');
        $('#DivTxtRvoe').removeClass('open').addClass('open');
        $('#DivTxtTotalMaterias').removeClass('open').addClass('open');
        $("#DivGradoObtenidoMIn").removeClass('open').addClass('open');
        $("#DivGradoObtenidoFIn").removeClass('open').addClass('open');
        $("#divTxtIdCarreraExcel").removeClass('open').addClass('open');
        $('#DivTxtCalifMinIn').removeClass('open').addClass('open');
        $('#DivTxtCalifMaxIn').removeClass('open').addClass('open');
        $('#DivTxtCalifMinAprobIn').removeClass('open').addClass('open');
        $('#DivTxtClavePlanCarrera').removeClass('open').addClass('open');
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
                    console.log(stringStepFirst);
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        LoadTable();
                        if (stringStepFirst.split("¬")[1].includes("0")) {
                            $("#btnNuevaCarrera").remove();
                            $("#ButtonAddCarrera").remove();
                            $("#btnNomInstitucion").remove();
                        }
                        if (stringStepFirst.split("¬")[3].includes("0")) {
                            $("#ButtonUpdateCarrera").remove();
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
});