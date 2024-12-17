// VERSION: DIGISEP GENERAL

function noBlankSpaces() {
    if (event.keyCode == 32) {
        return false;
    }
}

$(document).ready(function () {
    resize();
    var xml = 0;
    var tblCertificados;
    function initTable() {
        tblCertificados = $(".js-dataTable-full-pagination-Fixed").dataTable({
            ordering: true,
            orderable: false,
            paging: true,
            searching: true,
            stateSave: true,
            info: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: [8, 9]}, {targets: [6], type: "date-eu"}],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]],
            order: []
        });
        //tblCertificados.$('[data-toggle="tooltip"]').tooltip({trigger: 'hover'});
    }

    $('#lstCarreraCertificado').chosen({width: "100%", disable_search_threshold: 4});
    $('#lstLugarExpedicion').chosen({width: "100%", disable_search_threshold: 4});
    $('#lstFirmantesCertificados').chosen({width: "100%", disable_search_threshold: 4});
    $('#lstTipoCertificado').chosen({width: "100%", disable_search_threshold: 4});
    $("#lstCarreras").chosen({width: "100%", disable_search_threshold: 4});
    $("#lstCarreras_chosen").css("display", "grid");

    function cargarInicio() {
        $.ajax({
            url: '../Transporte/queryCCertificados.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var arregloRespuesta = resp.split("~");

                var lstCarreras = arregloRespuesta[0];
                var lstEstados = arregloRespuesta[1];
                var lstFirmantes = arregloRespuesta[2];
                var notificacionEFirma = arregloRespuesta[3];

                if (lstCarreras.split("|")[0].toString().trim().includes("error")) {
                    show_swal("¡Upps!", lstCarreras.split('|')[1], "error");
                }

                if (lstEstados.split("|")[0].toString().trim().includes("error")) {
                    show_swal("¡Upps!", lstEstados.split('|')[1], "error");
                }
                if (lstFirmantes.split("|")[0].toString().trim().includes("error")) {
                    show_swal("¡Upps!", lstFirmantes.split('|')[1], "error");
                }

                $("#lstCarreras").html(lstCarreras).trigger('chosen:updated');
                $('#lstLugarExpedicion').html(lstEstados).trigger('chosen:updated');
                $('#lstFirmantesCertificados').html(lstFirmantes).trigger('chosen:updated');
                initTable();
                var notifys = notificacionEFirma;
                if (notifys.includes("errorNotify")) {
                    show_swal("¡Upps!", notifys.split('|')[1], "error");
                } else {
                    eval(notifys);
                }
                setTimeout(function () {
                    $('#mainLoader').fadeOut();
                }, 200);

            }, complete: function () {
            }
        });
    }

//    function LoadTable() {
//        $.ajax({
//            url: '../Transporte/queryCCertificados.jsp',
//            data: '&txtBandera=1',
//            type: 'POST',
//            success: function (resp) {
//                var arregloRespuesta = resp.split("~");
//                var tabla = arregloRespuesta[0];
//                var lstEstados = arregloRespuesta[1];
//                var lstFirmantes = arregloRespuesta[2];
//                var notificacionEFirma = arregloRespuesta[3];
//
//                if (tabla.split("|")[0].toString().trim().includes("error")) {
//                    show_swal("¡Upps!", tabla.split('|')[1], "error");
//                }
//                if (lstEstados.split("|")[0].toString().trim().includes("error")) {
//                    show_swal("¡Upps!", lstEstados.split('|')[1], "error");
//                }
//                if (lstFirmantes.split("|")[0].toString().trim().includes("error")) {
//                    show_swal("¡Upps!", lstFirmantes.split('|')[1], "error");
//                }
//
//                $('#DivTblCertificados').html(tabla);
//                $('[data-toggle="tooltip"]').tooltip();
//                $('#lstLugarExpedicion').html(lstEstados).trigger('chosen:updated');
//                $('#lstFirmantesCertificados').html(lstFirmantes).trigger('chosen:updated');
//                var notifys = notificacionEFirma;
//                if (notifys.includes("errorNotify")) {
//                    show_swal("¡Upps!", notifys.split('|')[1], "error");
//                } else {
//                    eval(notifys);
//                }
//                TableActions();
//                initTable();
//                setTimeout(function () {
//                    $('#mainLoader').fadeOut();
//                }, 200);
//
//            }, complete: function () {
//            }
//        });
//    }

    $('#btnNuevoCertificado').click(function () {
        //$("#btnEditarTotal").attr("disabled", true);
        resetForm();
        $('#ButtonUpdateCertificado').hide();
        $('#ButtonAddCertificado').show();
        $('#ButtonGenerateXML').show();
        $('#ButtonCerrarCertificado').hide();
        $('#DivLstEstatusCertificadoIn').hide();
        $('#modaltitle').text('NUEVO CERTIFICADO');
        $('#ButtonGenerateXML').prop('disabled', true);
        $('#txtEdition').val('0');
        $("#divTxtACadenaOriginal").hide();
        CleanCampos();
        BlockCampos(false);
        $('#txtNombreAlumno').prop('disabled', true);
    });

    $('#btnBuscarAlumno').click(function () {
        var matricula = $('#txtMatricula').val();
        if (matricula != "") {
            $('#loadAction').fadeIn();
            $.ajax({
                url: '../Transporte/queryCCertificados.jsp',
                data: '&txtBandera=6&matricula=' + matricula,
                type: 'POST',
                success: function (resp) {
                    if (!resp.includes('error')) {
                        if (resp.includes('sinCoincidencias')) {
                            swal('¡Upps!', 'No se encontraron alumnos relacionados a la matrícula ingresada', 'warning');
                        } else if (resp.includes('tieneCertificado')) {
                            swal('¡Upps!', 'Este alumno ya cuenta con certificados registrados', 'info');
                        } else {
                            var split = resp.split('||');

                            $('#lstCarreraCertificado').html(split[1]).trigger('chosen:updated');
                            //HABILITAMOS EL BOTON EDITAR TOTAL DE MATERIAS HASTA QUE SE CARGUE INFORMACIÓN
                            //$("#btnEditarTotal").attr("disabled", false);
                            if (split[0].trim() == '1') {
                                $('#lstCarreraCertificado').change();
                            } else if (split[0].trim() == '2') {
                                swal('¡Wow!', 'Se encontró más de un registro relacionado a la matrícula ingresada, selecciona la carrera que estás buscando', 'info');
                            }
                        }
                    } else {
                        show_swal('¡Upps!', resp.split("|")[1], 'error');
                    }
                }, complete: function () {
                    $('#loadAction').fadeOut();
                }
            });
        } else {
            swal('¡Upps!', 'Ingresa una matrícula para continuar', 'warning');
        }
    });

    $('#lstCarreraCertificado').change(function () {
        $('#loadAction').fadeIn();
        var idCarrera = $('#lstCarreraCertificado option:selected').data('idcarrera');
        var idTECarrera = $('#lstCarreraCertificado').val();
        var idAlumno = $('#lstCarreraCertificado option:selected').data('idalumno');
        var nombreAlumno = $('#lstCarreraCertificado option:selected').data('nombre');
        $('#txtNombreAlumno').val(nombreAlumno).trigger('change');
        $('#idAlumno').val(idAlumno);
        $.ajax({
            url: '../Transporte/queryCCertificados.jsp',
            data: '&txtBandera=7&idCarrera=' + idCarrera + '&idAlumno=' + idAlumno + '&idTECarrera=' + idTECarrera,
            type: 'POST',
            success: function (resp) {
                if (!resp.includes('error') && !resp.includes('SinMaterias') && !resp.includes('SinCalificaciones')) {
                    var split = resp.split('~');
                    var splitCert = split[9].split('||');
                    
                    if(splitCert[4] === "1" && splitCert[5] === "1"){
                        swal('¡Alumno con Certificado!', 'El alumno ya cuenta con un <strong>certificado total</strong> para la carrera.<br>No es posible generar mas de un certificado total para el alumno en la misma carrera.', 'warning');
                        $("#ButtonAddCertificado").prop("disabled", true);
                        return;
                    }
                    
                    console.log($('#txtEdition').val())
                    if (splitCert[0] !== '0' && $('#txtEdition').val() !== "1") {
                        swal({
                            type: 'info',
                            title: '¡Alumno con Certificado!',
                            html: 'Se encontró un certificado del alumno en esta carrera que aún no está activo, ¿deseas seguir editandolo o crear uno nuevo?',
                            showCancelButton: true,
                            confirmButtonText: 'Sí, editar el certificado',
                            confirmButtonClass: 'btn btn-success',
                            cancelButtonText: 'Crear un nuevo certificado',
                            cancelButtonClass: 'btn btn-warning',
                            allowOutsideClick: false,
                            allowEscapeKey: false,
                            cancelButtonColor: null,
                            confirmButtonColor: null
                        }).then((result) => {
                            if (result) {
                                // Editar certificado
                                $('#idCertificado').val(splitCert[0]).trigger('change');
                                $('#ButtonUpdateCertificado').val(splitCert[0]).trigger('change');
                                $('#ButtonGenerateXML').val(splitCert[0]).trigger('change');
                                $('#lstFirmantesCertificados').val(splitCert[1]).trigger('chosen:updated');
                                $('#lstLugarExpedicion').val(splitCert[2]).trigger('chosen:updated');
                                $('#txtFolioControl').val(splitCert[3]).trigger('change');
                                $('#ButtonAddCertificado').hide();
                                $('#ButtonUpdateCertificado').show();
                                $('#ButtonGenerateXML').prop('disabled', false);
                                $('#modaltitle').html("EDITAR CERTIFICADO");
                                $('#txtEdition').val('1');
                            } else if (!result) {
                                // Crear nuevo certificado
                                $('#idCertificado').val('0').trigger('change');
                                $('#ButtonUpdateCertificado').val('0').trigger('change');
                                $('#ButtonGenerateXML').val('0').trigger('change');
                            }
                        });
                    }

                    $('#txtTotalMaterias').val(split[0]).trigger('change');
                    $('#txtMateriasAsignadas').val(split[1]).trigger('change');
                    $('#txtPromedio').val(split[2]).trigger('change');
                    $('#txtMateriasPasadas').val(split[3]).trigger('change');
                    $('#lstTipoCertificado').prop('disabled', false).trigger('chosen:updated');
                    $('#lstTipoCertificado').val(split[4].trim()).trigger('chosen:updated');
                    if (split[4].trim() == 79) {
                        $('#lstTipoCertificado_chosen .chosen-single').children().css("color", "darkgreen").css("font-weight", "bolder").css("font-size", "initial");
                    } else {
                        $('#lstTipoCertificado_chosen .chosen-single').children().css("color", "darkred").css("font-weight", "bolder").css("font-size", "initial");
                    }
                    $('#lstTipoCertificado').prop('disabled', true).trigger('chosen:updated');
                    $('#txtTotalCreditos').val(split[7]).trigger('change').prop('disabled', true);
                    $('#txtCreditosObtenidos').val(split[8]).trigger('change').prop('disabled', true);
                    $('#txt-numero-ciclos').val(split[10]).trigger('change').prop('disabled', true);
                    $('#numDecimales').val(split[11]).trigger('change').prop('disabled', true);
                    $("#ButtonAddCertificado").prop("disabled", false);
                } else if (resp.includes('SinMaterias')) {
                    swal('¡Upps!', 'La carrera seleccionada no cuenta con materias relacionadas', 'warning');
                    $("#ButtonAddCertificado").prop("disabled", true);
                } else if (resp.includes('SinCalificaciones')) {
                    swal('¡Upps!', 'La carrera seleccionada no cuenta con calificaciones relacionadas', 'warning');
                    $("#ButtonAddCertificado").prop("disabled", true);
                } else {
                    swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                }
            }, error: function () {
                swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
            }, complete: function () {
                $('#loadAction').fadeOut();
            }
        });
    });

    $('#ButtonGenerateXML').click(function () {
        var buttonVal = $(this).val();
        var calificadas = $('#txtMateriasAsignadas').val();
        var txtEdition = $('#txtEdition').val();
        var idCarrera = $("#lstCarreraCertificado").val();
        if (calificadas > 0) {
            // Primero guardamos cualquier cambio que el usuario haya
            // hecho en caso de que esté editando el registro
            if (txtEdition == 1) {
                xml = 1;
                $('#ButtonUpdateCertificado').click();
            } else {// Cuando es un registro nuevo
                $('#loadAction').fadeIn();
                $.ajax({
                    url: '../Transporte/queryCCertificados.jsp',
                    data: '&txtBandera=8&idCertificado=' + buttonVal,
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            show_swal('¡Certificado generado!', 'El archivo XML ha sido generado con éxito, ya se puede descargar', 'success');
                            cargarTabla(idCarrera);
                            $('#modal-certificadosElectronicos').modal('hide');
                        } else if (resp.includes('errorContrasenia')) {
                            show_swal('¡Upps!', 'Parece que la contraseña del firmante es incorrecta', 'error');
                        } else if (resp.includes('sinTimbres')) {
                            show_swal('¡Upps!', 'Se han agotado los timbres disponibles para la generación de certificados, adquiere más para continuar', 'warning');
                            cargarTabla(idCarrera);
                        } else if (resp.includes('timbresVencidos')) {
                            show_swal('¡Upps!', 'Los timbres disponibles ya no son vigentes, contacte a soporte técnico para continuar', 'warning');
                        } else {
                            show_swal('¡Upps!', resp.split("|")[1], 'error');
                        }
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            }
        } else {
            show_swal('¡Upps', 'Este alumno no tiene materias asignadas aún, no es posible generar un certificado electrónico', 'warning');
            $('#ButtonGenerateXML').prop('disabled', true);
        }
    });

    $('.BtnAccionesCertificados').click(function () {
        $("form[name='FormCertificadosElectronicos']").submit(function (e) {
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
                txtMatricula: {
                    required: true
                },
                lstCarreraCertificado: {
                    required: true
                },
                txtNombreAlumno: {
                    required: true
                },
                lstLugarExpedicion: {
                    required: true
                },
                lstFirmantesCertificados: {
                    required: true
                },
                txtFolioControl: {
                    required: true
                },
                txtFechaExpedicion: {
                    required: true
                },
                txtTotalMaterias: {
                    required: true
                },
                txtMateriasAsignadas: {
                    required: true
                },
                txtMateriasPasadas: {
                    required: true
                },
                txtPromedio: {
                    required: true
                },
                lstTipoCertificado: {
                    required: true
                },
                txtTotalCreditos: {
                    required: true
                },
                txtCreditosObtenidos: {
                    required: true
                }
            },
            messages: {
                txtMatricula: {
                    required: '¡Por favor completa este campo!'
                },
                lstCarreraCertificado: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtNombreAlumno: {
                    required: '¡Por favor completa este campo!'
                },
                lstLugarExpedicion: {
                    required: '¡Por favor selecciona una opción!'
                },
                lstFirmantesCertificados: {
                    required: '¡Por favor selecciona una opción!'
                },
                txtFolioControl: {
                    required: '¡Por favor completa este campo!'
                },
                txtFechaExpedicion: {
                    required: '¡Por favor selecciona una fecha!'
                },
                txtTotalMaterias: {
                    required: '¡Por favor selecciona una carrera!'
                },
                txtMateriasAsignadas: {
                    required: '¡Por favor selecciona una carrera!'
                },
                txtMateriasPasadas: {
                    required: '¡Por favor selecciona una carrera!'
                },
                txtPromedio: {
                    required: '¡Por favor selecciona una carrera!'
                },
                lstTipoCertificado: {
                    required: '¡Por favor selecciona una carrera!'
                },
                txtTotalCreditos: {
                    required: '¡Por favor selecciona un alumno para llenar este campo!'
                },
                txtCreditosObtenidos: {
                    required: '¡Por favor selecciona un alumno para llenar este campo!'
                }
            },
            submitHandler: function (form) {

                var bandera = $('#txtBandera').val();
                var idCarrera = $('#lstCarreraCertificado').val();
                var idProfesionista = $('#idAlumno').val();
                var idLugarExpedicion = $('#lstLugarExpedicion').val();
                var idFirmante = $('#lstFirmantesCertificados').val();
                var idTipoCertificado = $('#lstTipoCertificado').val();
                var fechaExpedicion = $('#txtFechaExpedicion').val();
                var total = $('#txtTotalMaterias').val();
                var asignadas = $('#txtMateriasAsignadas').val();
                var promedio = $('#txtPromedio').val();
                var pasadas = $('#txtMateriasPasadas').val();
                var totalCreditos = $('#txtTotalCreditos').val();
                var creditosObtenidos = $('#txtCreditosObtenidos').val();
                var idCertificado = $('#idCertificado').val();
                $.ajax({
                    url: '../Transporte/queryCCertificados.jsp',
                    data: {
                        txtBandera: bandera,
                        idCarrera: idCarrera,
                        idProfesionista: idProfesionista,
                        idLugarExpedicion: idLugarExpedicion,
                        idFirmante: idFirmante,
                        idTipoCertificado: idTipoCertificado,
                        fechaExpedicion: fechaExpedicion,
                        total: total,
                        asignadas: asignadas,
                        pasadas: pasadas,
                        promedio: promedio,
                        totalCreditos: totalCreditos,
                        creditosObtenidos: creditosObtenidos,
                        idCertificado: idCertificado,
                        numDecimales: $('#numDecimales').val()
                    },
                    type: 'POST',
                    success: function (resp) {
                        if (bandera == 2) {
                            if (resp.includes('success')) {
                                cargarTabla(idCarrera);
                                var split = resp.split('||');
                                $('#ButtonGenerateXML').prop('disabled', false);
                                $('#ButtonGenerateXML').val(split[1]);
                                $('#txtFolioControl').val(split[2]).trigger('change');
                                swal('¡Registro guardado!', 'La información del certificado se ha guardado exitósamente, ya puede generar el archivo XML', 'success');
                                setTimeout(function () {
                                    $('#lstLugarExpedicion').val(idLugarExpedicion).trigger('chosen:updated');
                                    $('#lstFirmantesCertificados').val(idFirmante).trigger('chosen:updated');
                                }, 500);
                            } else if (resp.includes('certificadoExistente')) {
                                swal('¡Upps!', 'La información ingresada coincide con un certificado existente, intente de nuevo', 'warning');
                            } else if (resp.includes('sinConfiguracion')) {
                                swal('¡Upps!', 'Parece que no has establecido las configuraciones iniciales, para continuar, primero completa la información inicial', 'warning');
                            } else {
                                show_swal("¡Upps!", resp.split('|')[1], "error");
                            }
                        } else if (bandera == 3) {
                            if (xml === 0) {// Si el usuario dio clic en el botón guardar
                                if (resp.includes('success')) {
                                    cargarTabla(idCarrera);
                                    swal('¡Registro actualizado!', 'La información del certificado se ha guardado exitósamente, ya puede generar el archivo XML', 'success');
                                    setTimeout(function () {
                                        $('#lstLugarExpedicion').val(idLugarExpedicion).trigger('chosen:updated');
                                        $('#lstFirmantesCertificados').val(idFirmante).trigger('chosen:updated');
                                    }, 500);
                                } else {
                                    show_swal("¡Upps!", resp.split('|')[1], "error");
                                }
                            } else if (xml === 1) {// Si se está haciendo un guardado antes de generar el XML
                                var buttonVal = $('#ButtonGenerateXML').val();
                                $('#loadAction').fadeIn();
                                $.ajax({
                                    url: '../Transporte/queryCCertificados.jsp',
                                    data: '&txtBandera=8&idCertificado=' + buttonVal,
                                    type: 'POST',
                                    success: function (resp) {
                                        if (resp.includes('success')) {
                                            swal('¡Certificado generado!', 'El archivo XML ha sido generado con éxito, ya se puede descargar', 'success');
                                            cargarTabla(idCarrera);
                                            $('#modal-certificadosElectronicos').modal('hide');
                                        } else if (resp.includes('errorContrasenia')) {
                                            swal('¡Upps!', 'Parece que la contraseña del firmante es incorrecta', 'error');
                                        } else if (resp.includes('timbresVencidos')) {
                                            swal('¡Upps!', 'Los timbres disponibles ya no son vigentes, contacte a soporte técnico para continuar', 'warning');
                                        } else if (resp.includes('sinTimbres')) {
                                            swal('¡Upps!', 'Se han agotado los timbres disponibles para la generación de certificados, adquiere más para continuar', 'warning');
                                        } else {
                                            show_swal("¡Upps!", resp.split('|')[1], "error");
                                        }
                                    }, complete: function () {
                                        $('#loadAction').fadeOut();
                                    }
                                });
                                xml = 0;
                            }
                            $('#txtEdition').val('0');
                        }
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });
    var $table, $row;
    function TableActions() {
        $("#tblCertificados").on('click', '.btnConsultarCertificado', function () {
            $('#loadAction').fadeIn();
            resetForm();
            $('#txtEdition').val('0');
            var buttonVal = $(this).val().split('_');
            $('#ButtonUpdateCertificado').hide();
            $('#ButtonAddCertificado').hide();
            $('#ButtonGenerateXML').hide();
            $('#ButtonCerrarCertificado').show();
            $('#DivLstEstatusCertificadoIn').show();
            $('#modaltitle').text('CONSULTAR CERTIFICADO');
            $.ajax({
                url: '../Transporte/queryCCertificados.jsp',
                data: '&txtBandera=5&idCertificado=' + buttonVal[1],
                type: 'POST',
                success: function (resp) {
                    if (resp.includes('error')) {
                        show_swal('¡Upps!', resp.split("|")[1], 'error');
                    } else {
                        var split = resp.split('~');
                        $('#txtMatricula').val(split[1]).trigger('change');
                        $('#idAlumno').val(split[16]);
                        $('#lstCarreraCertificado').html(split[2]).trigger('chosen:updated');
                        $('#txtNombreAlumno').val(split[3]).trigger('change');
                        $('#lstLugarExpedicion').val(split[4]).trigger('chosen:updated');
                        $('#lstFirmantesCertificados').val(split[7]).trigger('chosen:updated');
                        $('#txtFolioControl').val(split[8]).trigger('change');
                        $('#txtFechaExpedicion').prop('disabled', false);
                        $('#txtFechaExpedicion').val(split[9]).trigger('change');
                        $('#txtFechaExpedicion').prop('disabled', true);
                        $('#txtTotalMaterias').val(split[10]).trigger('change');
                        $('#txtMateriasAsignadas').val(split[11]).trigger('change');
                        $('#txtMateriasPasadas').val(split[21]).trigger('change');
                        $('#txtPromedio').val(split[12]).trigger('change');
                        $('#lstTipoCertificado').val(split[13]).trigger('chosen:updated');
                        if (split[13].trim() == 79) {
                            $('#lstTipoCertificado_chosen .chosen-single').children().css("color", "darkgreen").css("font-weight", "bolder").css("font-size", "initial");
                        } else {
                            $('#lstTipoCertificado_chosen .chosen-single').children().css("color", "darkred").css("font-weight", "bolder").css("font-size", "initial");
                        }
                        $('#idCertificado').val(split[14]);
                        $('#ButtonGenerateXML').val(split[14]);
                        $('#lstEstatusCertificado').val(split[15]).trigger('chosen:updated');
                        $('#txtTotalCreditos').val(split[17]).trigger('change');
                        $('#txtCreditosObtenidos').val(split[18]).trigger('change');
                        var cadenaOriginal = split[19];
                        if (cadenaOriginal != 0) {
                            $('#txtACadenaOriginal').val(cadenaOriginal);
                            $("#divTxtACadenaOriginal").show();
                        } else {
                            $("#divTxtACadenaOriginal").hide();
                        }
                        $('#txt-numero-ciclos').val(split[20]).trigger('change');
                        BlockCampos(true);
                        if (split[15] == "0") {
                            $('#ButtonGenerateXML').prop('disabled', false);
                        }
                        $('#modal-certificadosElectronicos').modal('show');
                    }
                }, complete: function () {
                    $('#loadAction').fadeOut();
                    tblCertificados.$("[data-toggle='tooltip']").tooltip("hide");
                }
            });
        });
        $("#tblCertificados").on('click', '.btnEditarCertificado', function () {
            $('#loadAction').fadeIn();
            $('#txtEdition').val('1');
            CleanCampos();
            resetForm();
            var buttonVal = $(this).val().split('_');
            $('#ButtonUpdateCertificado').show();
            $('#ButtonGenerateXML').show();
            $('#ButtonAddCertificado').hide();
            $('#ButtonCerrarCertificado').hide();
            $('#DivLstEstatusCertificadoIn').show();
            $('#modaltitle').text('EDITAR CERTIFICADO');
            $.ajax({
                url: '../Transporte/queryCCertificados.jsp',
                data: '&txtBandera=5&idCertificado=' + buttonVal[1],
                type: 'POST',
                success: function (resp) {
                    if (resp.includes('error')) {
                        show_swal('¡Upps!', resp.split("|")[1], 'error');
                    } else {
                        var split = resp.split('~');

                        $('#txtMatricula').val(split[1]).trigger('change');
                        $('#lstCarreraCertificado').html(split[2]).trigger('chosen:updated').trigger('change');
                        $('#txtNombreAlumno').val(split[3]).trigger('change');
                        $('#lstLugarExpedicion').val(split[4]).trigger('chosen:updated');
                        $('#lstFirmantesCertificados').val(split[7]).trigger('chosen:updated');
                        $('#txtFolioControl').val(split[8]).trigger('change');
                        $('#txtFechaExpedicion').prop('disabled', false);
                        $('#txtFechaExpedicion').datepicker().datepicker('setDate', split[9]);
                        //$('#txtFechaExpedicion').prop('disabled', true);
//                        $('#txtTotalMaterias').val(split[10]).trigger('change');
//                        $('#txtMateriasAsignadas').val(split[11]).trigger('change');
//                        $('#txtMateriasPasadas').val('').trigger('change');
//                        $('#txtPromedio').val(split[12]).trigger('change');
//                        $('#lstTipoCertificado').val(split[13]).trigger('chosen:updated');
                        $('#idCertificado').val(split[14]);
                        $('#ButtonGenerateXML').val(split[14]);
                        $('#lstEstatusCertificado').val(split[15]).trigger('chosen:updated');
                        $('#idAlumno').val(split[16]);
                        BlockCampos(false);
                        $('#txtMatricula').prop('disabled', true);
                        $('#lstCarreraCertificado').prop('disabled', true).trigger('chosen:updated');
                        $('#txtNombreAlumno').prop('disabled', true);
                        $('#btnBuscarAlumno').prop('disabled', true);
                        $("#divTxtACadenaOriginal").hide();
                        if (split[15] == "0") {
                            $('#ButtonGenerateXML').prop('disabled', false);
                        }
                        $('#modal-certificadosElectronicos').modal('show');
                    }
                }, complete: function () {
                    //$('#loadAction').fadeOut();
                    tblCertificados.$("[data-toggle='tooltip']").tooltip("hide");
                }
            });
        });
        $("#tblCertificados").on('click', '.js-swal-confirm', function () {
            $table = $(".js-dataTable-full-pagination-Fixed").DataTable();
            $row = $(this).parents("tr");
            var buttonval = this.value.split('_');
            var matricula = $("#tblCertificados #Matricula_" + buttonval[1]).text();
            var folioControl = $("#tblCertificados #FolioControl_" + buttonval[1]).text();
            var idCarrera = $("#tblCertificados #Carrera_" + buttonval[1]).attr("id-carrera");
            tblCertificados.$("[data-toggle='tooltip']").tooltip("hide");
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
                    url: '../Transporte/queryCCertificados.jsp',
                    data: '&txtBandera=4&idCertificado=' + buttonval[1],
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Eliminado!', 'El registro ha sido eliminado', 'success');
                            $("#lstCarreras").val(idCarrera).trigger("chosen:updated");
                            //cargarTabla(idCarrera);
                            $table.row($row).remove().draw();
                        } else {
                            show_swal('¡Upps!', resp.split("|")[1], 'error');
                        }
                    }, complete: function () {
                        $("#loadAction").fadeOut();
                        $('.chtodos').trigger('change');
                    }
                });
            }, function () {
                tblCertificados.$("[data-toggle='tooltip']").tooltip("hide");
            });
        });

        $("#tblCertificados").on('click', '.btnDescargarXML', function () {
            $('#loadAction').fadeIn();
            var buttonval = this.value.split('_');
            $(this).tooltip('hide');
            $.ajax({
                url: '../Transporte/queryCCertificados.jsp',
                data: '&txtBandera=9&idCertificado=' + buttonval[1],
                type: 'POST',
                success: function (resp) {
                    if (resp.includes('error') || resp.includes('sinCoincidencias')) {
                        if (resp.includes('error')) {
                            show_swal('¡Upps!', resp.split("|")[1], 'error');
                        } else {
                            swal('¡Upps!', 'No se ha encontrado el archivo XML, contacta a soporte técnico', 'error');
                        }
                    } else {
                        var split = resp.split('fpav');
                        var xml = [];
                        xml.push(split[1]);
                        descargarArchivo(new Blob(xml, {
                            type: 'application/xml'
                        }), split[0].trim());
                    }
                }, complete: function () {
                    $('#loadAction').fadeOut();
                    tblCertificados.$("[data-toggle='tooltip']").tooltip("hide");
                }
            });
        });
        
        $("#tblCertificados").on("change", ".chtodos", function () {
            if ($(this).is(":checked")) {
                tblCertificados.$(".cbxSelectDescarga").prop("checked", true);
                if (tblCertificados.$(".cbxGenXml").length > 0)
                    $("#btnGeneXmlMasivo").prop("disabled", false);
                if (tblCertificados.$(".cbxDescXml").length > 0)
                    $("#btnDescargarXmlMasivo").prop("disabled", false);

            } else {
                tblCertificados.$(".cbxSelectDescarga").prop("checked", false);
                $("#btnDescargarXmlMasivo").prop("disabled", true);
                $("#btnGeneXmlMasivo").prop("disabled", true);
            }
        });
        
        $("body").on("change", ".cbxDescXml", function () {
            var conteo_cadena_validacion = 0;
            tblCertificados.$(".cbxDescXml").each(function () {
                if ($(this).is(":checked")) {
                    ++conteo_cadena_validacion;
                }
            });
            if (conteo_cadena_validacion > 0) {
                $("#btnDescargarXmlMasivo").prop("disabled", false);
            } else {
                $("#btnDescargarXmlMasivo").prop("disabled", true);
            }
            if (conteo_cadena_validacion !== tblCertificados.$(".cbxDescXml").length) {
                $(".chtodos").prop("checked", false);
            } else {
                $(".chtodos").prop("checked", true);
            }
        });

        $("body").on("change", ".cbxGenXml", function () {
            var conteo_cadena_validacion = 0;
            tblCertificados.$(".cbxGenXml").each(function () {
                if ($(this).is(":checked")) {
                    ++conteo_cadena_validacion;
                }
            });
            if (conteo_cadena_validacion > 0) {
                $("#btnGeneXmlMasivo").prop("disabled", false);
            } else {
                $("#btnGeneXmlMasivo").prop("disabled", true);
            }
            if (conteo_cadena_validacion !== tblCertificados.$(".cbxGenXml").length) {
                $(".chtodos").prop("checked", false);
            } else {
                $(".chtodos").prop("checked", true);
            }
        });
        
        $("#tblCertificados").on("change", ".cbxDescXml, .cbxSelectDescarga", function () {
            var contador_seleccionados = 0;
            tblCertificados.$(".cbxDescXml, .cbxSelectDescarga").each(function () {
                if ($(this).is(":checked")) {
                    ++contador_seleccionados;
                }
            });
            $("#contador-seleccionados").html(contador_seleccionados);
        });

        $("#tblCertificados").on("change", ".chtodos", function () {
            var contador_seleccionados = 0;
            tblCertificados.$(".cbxDescXml, .cbxSelectDescarga").each(function () {
                if ($(this).is(":checked")) {
                    ++contador_seleccionados;
                }
            });
            $("#contador-seleccionados").html(contador_seleccionados);
        });
        
    }
    
    
        
    
    
    var time;
    $("#btnDescargarXmlMasivo").on("click", function () {
        $('#loadAction').fadeIn();
        var cadenaIdCertificados = "";
        tblCertificados.$(".cbxDescXml").each(function () {
            if ($(this).is(":checked")) {
                var id = this.id.split("_")[1];
                cadenaIdCertificados += id + "¬";
            }
        });
        $.ajax({
            url: '../Transporte/queryCCertificados.jsp',
            data: '&txtBandera=10&cadenaIdCertificado=' + cadenaIdCertificados,
            type: 'POST',
            success: function (resp) {
                $("#btnDescargarZip").children("a").remove();
                $("#btnDescargarZip").hide();
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
                            .attr("href", resp.trim())
                            .attr("download", "")
                            .attr("style", "color:#999999")
                            .text("Descargar archivo certificados")
                            .appendTo("#btnDescargarZip");
                    $("#btnDescargarZip").show();
                    time = setTimeout(function () {
                        $("#btnDescargarZip").children("a").remove();
                        $("#btnDescargarZip").hide();
                    }, 180000);
                } else {
                    $.notify({
                        // options
                        message: '<div class="text-center">Ocurrió un error al generar tu archivo, intenta de nuevo</div>'
                    }, {
                        // settings
                        type: 'danger'
                    });
                }
            }, error: function () {
                swal('¡Error!', 'Error interno del servidor, contacte a soporte técncio', 'error');
            }, complete: function () {
                $('#loadAction').fadeOut();
                tblCertificados.$("[data-toggle='tooltip']").tooltip("hide");
            }
        });
    });

    $("#btnGeneXmlMasivo").on("click", function () {
        var cadenaIdCertificados = "";
        tblCertificados.$(".cbxGenXml").each(function () {
            if ($(this).is(":checked")) {
                var id = this.id.split("_")[1];
                cadenaIdCertificados += id + "¬";
            }
        });
//        if (calificadas > 0) {
        $('#loadAction').fadeIn();
        $.ajax({
            url: '../Transporte/queryCCertificados.jsp',
            data: '&txtBandera=11&cadenaIdCertificado=' + cadenaIdCertificados,
            type: 'POST',
            success: function (resp) {
                if (resp.includes('success')) {
                    swal('¡Certificado generado!', 'El archivo XML ha sido generado con éxito, ya se puede descargar', 'success');
                    $("#btnGeneXmlMasivo").prop("disabled", true);
                    $("#btnDescargarXmlMasivo").prop("disabled", true);
                    var idCarrera = $("#lstCarreras").val();
                    cargarTabla(idCarrera);
                } else if (resp.includes('errorContrasenia')) {
                    swal('¡Upps!', 'Parece que la contraseña del firmante es incorrecta', 'error');
                } else if (resp.includes('error')) {
                    swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                } else if (resp.includes('sinTimbres')) {
                    swal('¡Upps!', 'Se han agotado los timbres disponibles para la generación de certificados, adquiere más para continuar', 'warning');
                    var idCarrera = $("#lstCarreras").val();
                    cargarTabla(idCarrera);
                } else if (resp.includes('timbresVencidos')) {
                    swal('¡Upps!', 'Los timbres disponibles ya no son vigentes, contacte a soporte técnico para continuar', 'warning');
                } else {
                    swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                }
            }, error: function () {
                swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
            }, complete: function () {
                $('#loadAction').fadeOut();
            }
        });

//        } else {
//            swal('¡Upps', 'Este alumno no tiene materias asignadas aún, no es posible generar un certificado electrónico', 'warning');
//            $('#ButtonGenerateXML').prop('disabled', true);
//        }

    });



    function descargarArchivo(contenidoEnBlob, nombreArchivo) {
        var reader = new FileReader();
        reader.onload = function (event) {
            var save = document.createElement('a');
            save.href = event.target.result;
            save.target = '_blank';
            save.download = nombreArchivo;
            var clicEvent = new MouseEvent('click', {
                'view': window,
                'bubbles': true,
                'cancelable': true
            });
            save.dispatchEvent(clicEvent);
            (window.URL || window.webkitURL).revokeObjectURL(save.href);
        };
        reader.readAsDataURL(contenidoEnBlob);
    }

    function BlockCampos(Cond) {
        $('#txtMatricula').prop('disabled', Cond);
        $('#lstCarreraCertificado').prop('disabled', Cond).trigger('chosen:updated');
        $('#txtNombreAlumno').prop('disabled', Cond);
        $('#lstLugarExpedicion').prop('disabled', Cond).trigger('chosen:updated');
        $('#lstFirmantesCertificados').prop('disabled', Cond).trigger('chosen:updated');
        $('#btnBuscarAlumno').prop('disabled', Cond);
    }

    function CleanCampos() {
        $('#txtMatricula').val('').trigger('change');
        $('#lstCarreraCertificado').html('').trigger('chosen:updated');
        $('#txtNombreAlumno').val('').trigger('change');
        $('#lstLugarExpedicion').val('').trigger('chosen:updated');
        $('#lstFirmantesCertificados').val('').trigger('chosen:updated');
        $('#txtFolioControl').val('').trigger('change');
        $('#txtFechaExpedicion').prop('disabled', false);
        $('#txtFechaExpedicion').datepicker().datepicker('setDate', fecha()).trigger('change');
        //$('#txtFechaExpedicion').val(Fecha()).trigger('change');
        //$('#txtFechaExpedicion').prop('disabled', true);
        $('#txtTotalMaterias').val('').trigger('change');
        $('#txtMateriasAsignadas').val('').trigger('change');
        $('#txtMateriasPasadas').val('').trigger('change');
        $('#txtPromedio').val('').trigger('change');
        $('#lstTipoCertificado').val('').trigger('chosen:updated');
        $('#txtTotalCreditos').val('').prop('disabled', true).trigger('change');
        $('#txtCreditosObtenidos').val('').prop('disabled', true).trigger('change');
        $('#txt-numero-ciclos').val('').prop('disabled', true).trigger('change');
        $('#idCertificado').val('').trigger('change');
        $('#idAlumno').val('').trigger('change');
        $('#ButtonUpdateCertificado').val('').trigger('change');
        $('#ButtonGenerateXML').val('').trigger('change');
    }

    $("#lstCarreraCertificado").on('change', function () {
        document.getElementById("DivLstCarreraCertificado").className = document.getElementById("DivLstCarreraCertificado").className.replace(/(?:^|\s)has-error(?!\S)/g, '');
        document.getElementById("DivLstCarreraCertificado").className = document.getElementById("DivLstCarreraCertificado").className.replace(/(?:^|\s)form-material-primary(?!\S)/g, '');
        document.getElementById("DivLstCarreraCertificado").className += " form-material-primary";
        $('#lstCarreraCertificado-error').remove();
    });
    $("#lstLugarExpedicion").on('change', function () {
        document.getElementById("DivLstLugarExpedicion").className = document.getElementById("DivLstLugarExpedicion").className.replace(/(?:^|\s)has-error(?!\S)/g, '');
        document.getElementById("DivLstLugarExpedicion").className = document.getElementById("DivLstLugarExpedicion").className.replace(/(?:^|\s)form-material-primary(?!\S)/g, '');
        document.getElementById("DivLstLugarExpedicion").className += " form-material-primary";
        $('#lstLugarExpedicion-error').remove();
    });
    $("#lstFirmantesCertificados").on('change', function () {
        document.getElementById("DivLstFirmantesCertificados").className = document.getElementById("DivLstFirmantesCertificados").className.replace(/(?:^|\s)has-error(?!\S)/g, '');
        document.getElementById("DivLstFirmantesCertificados").className = document.getElementById("DivLstFirmantesCertificados").className.replace(/(?:^|\s)form-material-primary(?!\S)/g, '');
        document.getElementById("DivLstFirmantesCertificados").className += " form-material-primary";
        $('#lstFirmantesCertificados-error').remove();
    });
    $("#txtNombreAlumno").on('change', function () {
        document.getElementById("DivTxtNombreAlumno").className = document.getElementById("DivTxtNombreAlumno").className.replace(/(?:^|\s)has-error(?!\S)/g, '');
        document.getElementById("DivTxtNombreAlumno").className = document.getElementById("DivTxtNombreAlumno").className.replace(/(?:^|\s)form-material-primary(?!\S)/g, '');
        document.getElementById("DivTxtNombreAlumno").className += " form-material-primary";
        $('#txtNombreAlumno-error').remove();
    });

    /**
     * @author Braulio Sorcia
     * @description Reinicializa el formulario y elimina la clase has-error del mismo.
     */
    function resetForm() {
        $("#FormCertificadosElectronicos div").removeClass("has-error");
        $("#FormCertificadosElectronicos div").remove(".help-block");
        $('#lstTipoCertificado_chosen .chosen-single').children().removeAttr("style");
    }

    function resize() {
        window.onresize = function () {
            ajustarComponentesModal();
        };

        window.onload = function () {
            ajustarComponentesModal();
        };

        function ajustarComponentesModal() {
            if (window.innerWidth <= 991 && window.innerWidth > 767) {
                $("#DivTxtFechaExp").parent("div").css("margin-top", "15px");
                $("#DivTxtNombreAlumno").css("margin-top", "15px");
                $("#DivLstCarreraCertificado").css("margin-top", "");
                $("#DivTxtFolioControl").parent("div").css("margin-top", "");
                $("#DivTxtTotalFolios").parent().parent().children().children().children().children("i").removeClass("hidden-xs");
            } else if (window.innerWidth <= 767 && window.innerWidth > 376) {
                $("#DivLstCarreraCertificado").css("margin-top", "15px");
                $("#DivTxtFolioControl").parent("div").css("margin-top", "15px");
                $("#DivTxtFechaExp").parent("div").css("margin-top", "15px");
                $("#DivTxtTotalFolios").parent().parent().children().children().children().children("i").removeClass("hidden-xs");
                $("#DivLstEstatusCertificadoIn").parent("div").css("margin-top", "15px");
            } else if (window.innerWidth <= 376) {
                $("#DivTxtTotalFolios").parent().parent().children().children().children().children("i").addClass("hidden-xs");
                $("#DivLstCarreraCertificado").css("margin-top", "15px");
                $("#DivTxtFolioControl").parent("div").css("margin-top", "15px");
            } else {
                $("#DivTxtFechaExp").parent("div").css("margin-top", "");
                $("#DivTxtNombreAlumno").css("margin-top", "");
                $("#DivLstCarreraCertificado").css("margin-top", "");
                $("#DivTxtFolioControl").parent("div").css("margin-top", "");
                $("#DivTxtTotalFolios").parent().parent().children().children().children().children("i").removeClass("hidden-xs");
                $("#DivLstEstatusCertificadoIn").parent("div").css("margin-top", "");
            }
        }
    }

    $("#btnCopiarCadena").click(function () {
        $("#txtACadenaOriginal").attr("disabled", false);
        $("#txtACadenaOriginal").select();
        document.execCommand("copy");
        $("#txtACadenaOriginal").attr("disabled", true);
        $.notify({
            // options
            message: '¡Copiado al portapapeles!'
        }, {
            // settings
            type: 'info',
            delay: 3000,
            placement: {
                from: "bottom",
                align: "left"
            },
            z_index: 10310
        });
    });

    $("#txtMatricula").keypress(function (event) {
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if (keycode == '13') {
            $("#btnBuscarAlumno").click();
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
                    cargarInicio();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        cargarInicio();
                        //IMPORTAR REGISTROS
                        if (stringStepFirst.split("¬")[1].includes("0")) {
                            $("#btnNuevoCertificado").remove();
                            $("#btnDescargarFormato").parent("div").parent("div").remove();
                        }
                        if (stringStepFirst.split("¬")[5].includes("0")) {
                            $("#ButtonGenerateXML").remove();
                            $("#btnGeneXmlMasivo").remove();
                        }
                        //NO DESCARGAS
                        if (stringStepFirst.split("¬")[6].includes("0")) {
                            $("#btnDescargarXmlMasivo").remove();
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
        $("#tblCertificados_filter input").val("").trigger("keyup");
        $("#loadAction").fadeIn();
        $.ajax({
            url: '../Transporte/queryCCertificados.jsp',
            data: '&txtBandera=12&idCarrera=' + idCarrera,
            type: 'POST',
            success: function (resp) {
                if (resp.includes("sessionOff")) {
                    alert("Sesión Expirada");
                    window.location.replace("../Generales/LogOut.jsp");
                } else if (resp.includes('success')) {
                    $("#DivTblCertificados").html(resp.split("|")[1]);
                }
            }, complete: function () {
                $("#loadAction").fadeOut();
                //$(".li-buttons").attr("disabled", true);
                initTable();
                TableActions();
                $("#contador-seleccionados").html("0");
            }
        });
    }

    $("#btnImportarMasivo").click(function () {
        $("#fileCertificados").click();
    });

    $('#fileCertificados').on('change', function () {
        var archivo = $(this).val();

        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.xls') || archivo.toString().endsWith('.xlsx')) {
                swal('¡Cargando información!', 'Se están registrando los certificados en el sistema', 'info').then(function () {
                    $('#loadAction').fadeIn();
                });
                $('#importarArchivo').click();
            } else {
                show_swal('¡Upps!', 'Debes seleccionar el formato predeterminado con la información de los certificados', 'warning');
                $('#fileCertificados').val('');
            }
        }
    });

    $('.btnAccionesCertificados').click(function () {
        $("form[name='form-importar-certificados-masivos']").submit(function (e) {
            e.preventDefault();
        }).validate({
            ignore: [],
            errorClass: 'help-block text-right animated fadeInDown',
            errorElement: 'div',
            rules: {
                fileCertificados: {
                    required: true,
                    extension: "xls|xlsx"
                }
            },
            messages: {
                'fileCertificados': {
                    required: function () {
                        show_swal('¡Upps!', 'Selecciona el archivo predeterminado con la información de los alumnos', 'warning');
                    },
                    extension: function () {
                        show_swal('¡Upps!', 'Selecciona el archivo excel con la información de los alumnos', 'warning');
                    }
                }
            },
            submitHandler: function (form) {
                $.ajax({
                    url: '../Transporte/queryCCertificados.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    success: function (resp) {
                        if (resp.toString().includes('success')) {
                            var split = resp.split("||");
                            show_swal('¡Proceso completado!', 'La importación de los certificados se ha completado con éxito.', 'success');
                            //Procedemos a cargar los certificados recien importados.
                            cargarTabla(split[1]);
                            $.notify({
                                // options
                                message: 'Se cargaran en la tabla los certificados importados...'
                            }, {
                                // settings
                                type: 'success',
                                delay: 7000,
                                placement: {
                                    from: "top",
                                    align: "right"
                                },
                                z_index: 10310
                            });
                        } else if (resp.toString().includes('sinRegistros')) {
                            var split = resp.split('||');
                            show_swal('¡Archivo en blanco!', 'El archivo no contiene registros a importar. <br>Verifica la información ingresada.<br>Hoja: '
                                    + split[1] + '. <br><strong>Los registros posteriores NO fueron realizados.</strong>', 'warning');
                        } else if (resp.toString().includes('infoCertificadoIncompleta')) {
                            if (resp.includes('||')) {
                                var split = resp.split("||");
                                show_swal('¡Información incompleta!', 'La información del certificado en la <strong>hoja ' + split[1] + ' y fila ' + split[2] + '</strong> está incompleta, por favor revise el archivo e intente nuevamente. <p><strong>Los registros posteriores NO fueron procesados.', 'warning');
                            }
                        } else if (resp.toString().includes('formatoInvalido')) {
                            var split = resp.split("||");
                            if (split.length === 1 && split[0].toString().trim() === 'formatoInvalido')
                                show_swal('¡Formato incorrecto!', 'El formato del archivo seleccionado para subir certificados es inválido, recuerde que no debe modificar la estructura del archivo.', 'warning');
                            else {
                                var mensaje = split[1].split("_")[1];
                                show_swal('¡Formato con inconsistencias!', 'El formato presenta algunas inconsistencias.<p>Detalle: ' + mensaje + "</p><p><strong>Los registros posteriores NO fueron procesados.", 'warning');
                            }
                        } else if (resp.toString().includes('noFirmante')) {
                            var split = resp.split('||');
                            show_swal('¡Firmante no encontrado!', 'El CURP del firmante ingresado en la <strong>hoja ' + split[1] + ' y fila ' + split[2] + '</strong> no corresponde a ningún firmante dentro del sistema. <br>Verifica el archivo e intenta de nuevo. <p><strong>Los registros posteriores NO fueron realizados.', 'warning');
                        } else if (resp.toString().includes('errorFirmantes')) {
                            var split = resp.split('||');
                            show_swal('¡Error interno!', 'Ocurrió un error al registrar los firmantes con el título de la hoja '
                                    + split[1] + '. <br><p>Registro Firmante: '
                                    + split[2] + "<br><strong><small>Contacta a soporte técnico</small></strong>", 'error');
                        } else if (resp.includes('noAlumno')) {
                            var split = resp.split('||');
                            show_swal('¡Alumno no encontrado!', 'La matrícula ingresada no coincide con un registro existente en la carrera establecida. <br>Verifica la información ingresada.<br>Certificado en la <b>hoja: '
                                    + split[1] + ' y fila ' + split[2] + '</b>. <br><strong>Los registros posteriores NO fueron realizados.</strong>', 'warning');
                        } else if (resp.includes('noCarrera')) {
                            var split = resp.split('||');
                            show_swal('¡Carrera no encontrada!', 'El id de carrera no coincide con un registro existente. <br>Verifica la información ingresada.<br>Certificado en la <b>hoja: '
                                    + split[1] + ' y fila: ' + split[2] + '</b>. <br><strong>Los registros posteriores NO fueron realizados.</strong>', 'warning');
                        } else if (resp.includes('SinMaterias')) {
                            show_swal('¡Sin materias!', 'La carrera seleccionada no cuenta con materias relacionadas.<br>Verifica la información ingresada.<br>Certificado en la <b>hoja: '
                                    + split[1] + ' y fila ' + split[2] + '</b>. <br><strong>Los registros posteriores NO fueron realizados.</strong>', 'warning');
                        } else if (resp.includes('SinCalificaciones')) {
                            var split = resp.split('||');
                            show_swal('¡Sin calificaciones!', 'El alumno no cuenta con calificaciones relacionadas<br>Verifica la información ingresada.<br>Certificado en la <b>hoja: '
                                    + split[1] + ' y fila ' + split[2] + '</b>. <br><strong>Los registros posteriores NO fueron realizados.</strong>', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function () {
                        $('#fileCertificados').val('');
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });

    function fecha() {
        var today = new Date();
        var yesterday = new Date(today.setDate(today.getDate() - 2));
        var dd, mm, yyyy;
        dd = yesterday.getDate();
        mm = yesterday.getMonth() + 1;
        yyyy = yesterday.getFullYear();

        if (dd < 10) {
            dd = '0' + dd;
        }
        if (mm < 10) {
            mm = '0' + mm;
        }
        return dd + "-" + mm + "-" + yyyy;
    }
});