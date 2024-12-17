$(document).ready(function () {
    $.validator.addMethod('filesize', function (value, element, param) {
        return this.optional(element) || (element.files[0].size <= param)
    }, 'El tamaño del archivo debe ser menor o igual a 2 megas');

    $('#lstComplementoFoliosTitulos').chosen({width: "100%", disable_search_threshold: 6});
    $('#lstComplementoFoliosCertificados').chosen({width: "100%", disable_search_threshold: 6});

    //LoadConfig();
    function LoadConfig() {
        $.ajax({
            url: '../Transporte/queryCConfiguracionInicial.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                if (resp.includes('sinConfiguracion')) {
                    $('#btnAddInitialConfig').show();
                    $('#btnUpdInitialConfig').hide();
                    $('#liBtnNuevo').remove();
                    swal('¡Upps!', 'Para poder comenzar a usar el sistema, debes de establecer algunas configuraciones iniciales', 'info');
                } else if (!resp.includes('error')) {
                    var split = resp.split('~');
                    console.log(split);
                    console.log(resp)
                    $('#btnAddInitialConfig').remove();
                    $('#btnUpdInitialConfig').val(split[0].trim());
                    $('#txtIdConfiguracion').val(split[0].trim());
                    $('#btnAgregarClaveActivacion').val(split[0].trim());
                    $('#txtNombreInstitucion').val(split[1]).trigger('change');
                    $('#txtPrefijoTitulo').val(split[2]).trigger('change');
                    $('#txtLongitud').val(split[3]).trigger('change');
                    $('#txtPrefijoCertificado').val(split[4]).trigger('change');
                    $('#txtLongitudCertificados').val(split[5].trim()).trigger('change');
                    $('#txtNombreOficial').val(split[6].trim()).trigger('change');
                    $('#txtNombreOficial').val(split[6].trim()).trigger('change');   
                    if (split[7].trim().toString() == "null") {
                        $('#txtClaveInstitucional').val('').trigger('change');
                        $('#liBtnNuevo').remove();
                    } else {
                        $('#txtClaveInstitucional').val(split[7].trim()).trigger('change');
                    }
                    //split[8] ----> Logo
                    $('#txtUsuarioSEP').val(split[9].trim()).trigger('change');
                    $('#txtPasswordSEP').val(split[10].trim()).trigger('change');
                    $('#txtUsuarioSepProd').val(split[11].trim()).trigger('change');
                    $('#txtPasswordSepProd').val(split[12].trim()).trigger('change');
                    
                    $('#txtVigenciaInicio').val(split[13]).trigger('change');
                    $('#txtVigenciaFin').val(split[14]).trigger('change');

                    $('#txtNombreInstitucion').attr('disabled', true);
                    $('#btnUpdInitialConfig').show();
                } else {
                    show_swal('¡Upps!', resp.split("|")[1], 'error');
                }
            }, complete: function () {
                $('#mainLoader').fadeOut();
            }
        });
    }

    $('#txtFileLogo').change(function () {
        var file = document.getElementById('txtFileLogo').files[0].name;
        $('#btnFileLogo').html('<i class="fa fa-image"></i> ' + file);
    });

    $('#txtFileFirma').change(function () {
        var file = document.getElementById('txtFileFirma').files[0].name;
        $('#btnFileFirma').html('<i class="fa fa-image"></i> ' + file);
    });

    $('.InitialConfigActions').click(function () {
        $('form[name="FormConfiguracionesIniciales"]').submit(function (e) {
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
                elem.closest('.col-lg-12').removeClass('has-error').addClass('has-error');
                elem.closest('.help-block').remove();
            },
            success: function (e) {
                var elem = jQuery(e);
                elem.closest('.col-xs-12').removeClass('has-error');
                elem.closest('.col-lg-12').removeClass('has-error');
                elem.closest('.help-block').remove();
            },
            rules: {
                txtNombreInstitucion: {
                    required: true,
                    maxlength: 100
                },
                txtNombreOficial: {
                    required: true,
                    maxlength: 100
                },
                txtPrefijoTitulo: {
                    required: true
                },
                txtLongitud: {
                    required: true,
                    min: 5,
                    max: 30
                },
                txtPrefijoCertificado: {
                    required: true
                },
                txtLongitudCertificados: {
                    required: true,
                    min: 5,
                    max: 30
                },
                txtFileLogo: {
                    filesize: 2100000 //poco arriba de 2MB
                },
                txtFileFirma: {
                    filesize: 2100000 //poco arriba de 2MB
                }
            }, messages: {
                txtNombreInstitucion: {
                    required: '¡Por favor completa este campo!',
                    max: 'Solo se permite un máximo de 100 caracteres'
                },
                txtNombreOficial: {
                    required: '¡Por favor completa este campo!',
                    max: 'Solo se permite un máximo de 100 caracteres'
                },
                txtPrefijoTitulo: {
                    required: '¡Por favor completa este campo!'
                },
                txtLongitud: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingresa un valor mayor o igual a 5',
                    max: '¡Por favor ingresa un valor menor o igual a 30'
                },
                txtPrefijoCertificado: {
                    required: '¡Por favor completa este campo!'
                },
                txtLongitudCertificados: {
                    required: '¡Por favor completa este campo!',
                    min: '¡Por favor ingresa un valor mayor o igual a 5',
                    max: '¡Por favor ingresa un valor menor o igual a 30'
                }
            },
            submitHandler: function (form) {
                var bandera = $('#txtBandera').val();

                var prefijoTitulo = $('#txtPrefijoTitulo').val();
                var longitudTitulos = $('#txtLongitud').val();
                var prefijoCertificado = $('#txtPrefijoCertificado').val();
                var longitudCertificados = $('#txtLongitudCertificados').val();

                if (longitudTitulos > prefijoTitulo.length && longitudCertificados > prefijoCertificado.length) {
                    if (prefijoCertificado.toString().trim() !== prefijoTitulo.toString().trim()) {
                        $('#loadAction').fadeIn();
                        $("#txtNombreInstitucion2").val($("#txtNombreInstitucion").val());
                        $.ajax({
                            url: '../Transporte/queryCConfiguracionInicial.jsp',
                            data: new FormData(form),
                            processData: false,
                            contentType: false,
                            type: 'POST',
                            success: function (resp) {
                                if (bandera == '2') {// Inserción
                                    if (resp.includes('success')) {
                                        var split = resp.split('||');
                                        $('#btnAddInitialConfig').remove();
                                        $('#btnUpdInitialConfig').val(split[1].trim());
                                        $('#btnUpdInitialConfig').show();
                                        swal('¡Configuración Agregada!', 'La configuración inicial ha sido establecida, serás redirigido a la página de inicio de sesión para aplicar los cambios.', 'success').then(function () {
                                            location.href = "../Generales/LogOut.jsp";
                                        });
                                    } else if (resp.includes('institucionExistente')) {
                                        swal('¡Upps!', 'Ya existe una institución con este nombre, intenta de nuevo', 'warning');
                                    } else {
                                        show_swal('¡Upps!', resp.split("|")[1], 'error');
                                    }
                                } else if (bandera == '3') {
                                    if (resp.includes('success')) {
                                        swal('Configuración guardada', 'Las configuraciones iniciales han sido actualizadas', 'success');
                                    } else {
                                        show_swal('¡Upps!', resp.split("|")[1], 'error');
                                    }
                                } else {
                                    swal('¡Upps!', 'Acción desconocida', 'warning');
                                }
                            }, complete: function () {
                                $('#loadAction').fadeOut();
                            }
                        });
                    } else {
                        swal('¡Upps!', 'Los prefijos de los documentos no deben ser iguales. Intenta nuevamente', 'warning');
                    }
                } else {
                    swal('¡Upps!', 'Las longitudes de los folios deben ser mayores que el número de caractéres de sus prefijos', 'warning');
                }
            }
        });
    });

    $('.BtnAccionesClaveActivacion').click(function () {
        $('form[name=FormClaveActivacion]').submit(function (e) {
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
                txtClaveActivacion: {
                    required: true
                }
            }, messages: {
                txtClaveActivacion: {
                    required: '¡Por favor ingresa la clave de activación!'
                }
            },
            submitHandler: function (form) {
                var bandera = $('#txtBanderaActivacion').val();
                $('#loadAction').fadeIn();
                var claveActivacion = $('#txtClaveActivacion').val();
                var idConfiguracion = $('#btnAgregarClaveActivacion').val();

                $.ajax({
                    url: '../Transporte/queryCConfiguracionInicial.jsp',
                    data: '&txtBandera=' + bandera +
                            '&claveAutorizacion=' + claveActivacion +
                            '&idConfiguracion=' + idConfiguracion,
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            var split = resp.split('||');
                            swal('¡Timbres adquiridos!', 'Has adquirido ' + split[1] + ' timbres vigentes hasta el ' + split[2], 'success');
                            $('#txtClaveActivacion').val('').trigger('change');
                            $('#modal-ClaveActivacion').modal('hide');
                        } else if (resp.includes('claveInvalida')) {
                            swal('¡Upps!', 'La clave ingresada es inválida, intenta de nuevo o solicita una al personal de Grupo Inndex', 'warning');
                            $('#txtClaveActivacion').val('').trigger('change');
                        } else if (resp.includes('claveUsada')) {
                            swal('¡Upps!', 'La clave ingresada ya fue utilizada en el sistema, si requieres de más timbres, comunícate con el personal de Grupo Inndex', 'warning');
                            $('#txtClaveActivacion').val('').trigger('change');
                        } else if (resp.includes('error')) {
                            swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                            $('#txtClaveActivacion').val('').trigger('change');
                        }
                    }, error: function () {
                        swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });

    $("#lstComplementoFoliosTitulos").on('change', function () {
        document.getElementById("DivLstComplementoFoliosTitulos").className = document.getElementById("DivLstComplementoFoliosTitulos").className.replace(/(?:^|\s)has-error(?!\S)/g, '');
        document.getElementById("DivLstComplementoFoliosTitulos").className = document.getElementById("DivLstComplementoFoliosTitulos").className.replace(/(?:^|\s)form-material-primary(?!\S)/g, '');
        document.getElementById("DivLstComplementoFoliosTitulos").className += " form-material-primary";
        $('#lstComplementoFoliosTitulos-error').remove();
    });
    $("#lstComplementoFoliosCertificados").on('change', function () {
        document.getElementById("DivLstComplementoFoliosCertificados").className = document.getElementById("DivLstComplementoFoliosCertificados").className.replace(/(?:^|\s)has-error(?!\S)/g, '');
        document.getElementById("DivLstComplementoFoliosCertificados").className = document.getElementById("DivLstComplementoFoliosCertificados").className.replace(/(?:^|\s)form-material-primary(?!\S)/g, '');
        document.getElementById("DivLstComplementoFoliosCertificados").className += " form-material-primary";
        $('#lstComplementoFoliosCertificados-error').remove();
    });

    $('#txtLongitud').change(function () {
        var value = parseInt($(this).val());
        if (Number.isInteger(value)) {
            $('#txtLongitud').val(value);
            return false;
        }
    });
    $('#txtLongitudCertificados').change(function () {
        var value = parseInt($(this).val());
        if (Number.isInteger(value)) {
            $('#txtLongitudCertificados').val(value);
            return false;
        }
    });

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
                    LoadConfig();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        LoadConfig();
                        if (stringStepFirst.split("¬")[3].includes("0")) {
                            $("#btnUpdInitialConfig").remove();
                            $("#txtCalifMinAprobatoria").attr("disabled", true);
                            $("#txtPrefijoTitulo").attr("disabled", true);
                            $("#txtLongitud").attr("disabled", true);
                            $("#txtPrefijoCertificado").attr("disabled", true);
                            $("#txtLongitudCertificados").attr("disabled", true);
                            $("#FormConfiguracionesIniciales input").attr("disabled", true);
                        }
                        if(stringStepFirst.split("¬")[5].includes("0")){
                            $("#btnNuevaClave").remove();
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