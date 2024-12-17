/**
 * @author: Braulio Sorcia
 * @description Js para las validaciones del lado del cliente de Usuarios
 * @since 15 DE ENERO DE 2019
 * =====================================================
 * @author_change:
 * @description_change:
 * @date_change: 
 */

$(document).ready(function () {
    resize();
    $('#lstSexoUsuario').chosen({width: "100%", disable_search_threshold: 4});
    $('#lstTipoUsuario').chosen({width: "100%", disable_search_threshold: 4});



    var cadena_rol_seleccionado = "";
    var tblUsuarios;
    var tblRoles;
    function initTable() {
        tblUsuarios = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            paging: true,
            searching: true,
            info: true,
            stateSave: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: 6}],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]]
        });
        tblRoles = $('.js-dataTable-full-pagination-Fixed1').dataTable({
            ordering: true,
            paging: true,
            searching: true,
            stateSave: true,
            info: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: 2}],
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]]
        });
        $('[data-toggle="tooltip"]').tooltip();
    }



    function LoadTable() {
        $.ajax({
            url: '../Transporte/queryCUsuarios.jsp',
            data: 'txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var tblUsuario = resp.split("°")[0];
                var tblRol = resp.split("°")[1];
                $('#divTblUsuarios').html(tblUsuario);
                $('#divTblRol').html(tblRol);
                initTable();
                $("#tblUsuarios_filter input").val("").trigger("keyup");
                $("#tblRoles_filter input").val("").trigger("keyup");
                TableActions();
                setTimeout(function () {
                    $('#mainLoader').fadeOut();
                });
            }, error: function () {

            }
        });
    }

    $("#btnNuevoUsuario").click(function () {
        $('#loadAction').fadeIn();
        $.ajax({
            url: '../Transporte/queryCUsuarios.jsp',
            data: 'txtBandera=6',
            type: 'POST',
            success: function (data, textStatus, jqXHR) {
                limpiarFormulario();
                $("#btnRegistrarUsuario").attr("disabled", false);
                deshabilitarInputs(false);
                abrirCerrarLabel(false);
                resetForm();
                $("#modaltitle").html("AGREGAR USUARIO");
                $("#btnUpdateUsuario").hide();
                $("#btnRegistrarUsuario").show();
                $("#lstTipoUsuario").html(data).trigger("chosen:updated");
                $("#divConfirmPasswordUsuario").show();
                $("#modal-usuarios").modal("show");
            }, error: function (jqXHR, textStatus, errorThrown) {
                swal('¡Upps!', 'Ha ocurrido un error, al llenar la lista de roles', 'error');
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });
    });

    $("#btnNuevoRol").click(function () {
        $('#loadAction').fadeIn();
        $.ajax({
            url: '../Transporte/queryCUsuarios.jsp',
            data: 'txtBandera=11',
            type: 'POST',
            success: function (data, textStatus, jqXHR) {
                $("#divTipoRolIn").removeClass("open");
                $("#divDescripRolIn").removeClass("open");
                $("#txtNombreRol").val("");
                $("#txtDescripcionRol").val("");
                $("#txtNombreRol").attr("disabled", false);
                $("#txtDescripcionRol").attr("disabled", false);
                $("#modaltitleRol").html("AGREGAR ROL");
                $("#btnUpdateRol").hide();
                $("#btnRegistrarRol").show();
                $("#marcarTodos").prop("checked", false);
                $(".permisos").attr("disabled", false);
                $("#marcarTodos").attr("disabled", false);
                $("#panel-permisos").html(data);
                $("#modal-roles").modal("show");
            }, error: function (jqXHR, textStatus, errorThrown) {
                swal('¡Upps!', 'Ha ocurrido un error, al llenar la lista de permisos', 'error');
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });
    });


    function TableActions() {
        $('#divTblUsuarios').on('click', '.js-swal-confirm', function () {
            var buttonVal = $(this).attr("id").split('_');
            tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
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
                    url: '../Transporte/queryCUsuarios.jsp',
                    data: '&txtBandera=2&IdUsuario=' + buttonVal[1],
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Registro eliminado!', 'El usuario seleccionado ha sido eliminado', 'success');
                            LoadTable();
                            tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
                        } else if (resp.includes('error')) {
                            swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                            tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
                        }
                    }, error: function () {
                        swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                    }
                });
            }, function () {
                tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
            });
        });
        $('#divTblUsuarios').on('click', '.btnConsultarUsuario', function () {
            var buttonVal = $(this).attr("id").split('_');
            limpiarFormulario();
            $('#loadAction').fadeIn();
            $.ajax({
                url: '../Transporte/queryCUsuarios.jsp',
                data: 'txtBandera=6',
                type: 'POST',
                success: function (data, textStatus, jqXHR) {
                    $("#lstTipoUsuario").html(data).trigger("chosen:updated");
                }, error: function (jqXHR, textStatus, errorThrown) {
                    swal('¡Upps!', 'Ha ocurrido un error, al llenar la lista de roles', 'error');
                }, complete: function (jqXHR, textStatus) {
                    $.ajax({
                        url: '../Transporte/queryCUsuarios.jsp',
                        data: '&txtBandera=3&IdUsuario=' + buttonVal[1],
                        type: 'POST',
                        success: function (resp) {
                            var bandera = resp.split("°")[0];
                            if (bandera.includes('success')) {
                                var datos_modal = resp.split("°")[1].split("¬");

                                $("#IdPersona").val(datos_modal[0]);
                                $("#txtNombreUsuario").val(datos_modal[1]);
                                $("#txtApaternoUsuario").val(datos_modal[2]);
                                $("#txtAmaternoUsuario").val(datos_modal[3]);
                                $("#txCorreoUsuario").val(datos_modal[4]);
                                $("#lstSexoUsuario").val(datos_modal[5]).trigger("chosen:updated");

                                $("#IdUsuario").val(datos_modal[6]);
                                $("#txtUsuario").val(datos_modal[7]);
                                $("#txtPasswordUsuario").val(datos_modal[8]);

                                $("#lstTipoUsuario").val(datos_modal[9]).trigger("chosen:updated");


                                //OCULTAMOS LOS BOTONES DEL MODAL QUE NO SON NECESARIOS
                                $(".btnAccionesUsuarios").hide();
                                $("#divConfirmPasswordUsuario").hide();

                                abrirCerrarLabel(true);
                                deshabilitarInputs(true);
                                $("#modaltitle").html("CONSULTAR USUARIO");
                                $("#modal-usuarios").modal("show");


                            } else if (bandera.includes('error')) {
                                swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                                tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
                            }
                        }, error: function () {
                            swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                        }, complete: function (jqXHR, textStatus) {
                            $('#loadAction').fadeOut();
                            tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
                        }
                    });
                }
            });
        });
        $('#divTblUsuarios').on('click', '.btnEditarUsuario', function () {
            var buttonVal = $(this).attr("id").split('_');
            limpiarFormulario();
            $('#loadAction').fadeIn();
            $.ajax({
                url: '../Transporte/queryCUsuarios.jsp',
                data: 'txtBandera=6',
                type: 'POST',
                success: function (data, textStatus, jqXHR) {
                    $("#lstTipoUsuario").html(data).trigger("chosen:updated");
                }, error: function (jqXHR, textStatus, errorThrown) {
                    swal('¡Upps!', 'Ha ocurrido un error, al llenar la lista de roles', 'error');
                }, complete: function (jqXHR, textStatus) {
                    $.ajax({
                        url: '../Transporte/queryCUsuarios.jsp',
                        data: '&txtBandera=3&IdUsuario=' + buttonVal[1],
                        type: 'POST',
                        success: function (resp) {
                            var bandera = resp.split("°")[0];
                            if (bandera.includes('success')) {
                                var datos_modal = resp.split("°")[1].split("¬");

                                $("#IdPersona").val(datos_modal[0]);
                                $("#txtNombreUsuario").val(datos_modal[1]);
                                $("#txtApaternoUsuario").val(datos_modal[2]);
                                $("#txtAmaternoUsuario").val(datos_modal[3]);
                                $("#txCorreoUsuario").val(datos_modal[4]);
                                $("#lstSexoUsuario").val(datos_modal[5]).trigger("chosen:updated");

                                $("#IdUsuario").val(datos_modal[6]);
                                $("#txtUsuario").val(datos_modal[7]);
                                $("#txtPasswordUsuario").val(datos_modal[8]);

                                $("#lstTipoUsuario").val(datos_modal[9]).trigger("chosen:updated");


                                //OCULTAMOS LOS BOTONES DEL MODAL QUE NO SON NECESARIOS
                                $("#btnRegistrarUsuario").hide();
                                $("#btnRegistrarUsuario").attr("disabled", true);
                                $("#btnUpdateUsuario").show();
                                $("#divConfirmPasswordUsuario").show();

                                abrirCerrarLabel(true);
                                deshabilitarInputs(false);
                                $("#modaltitle").html("MODIFICAR USUARIO");
                                $("#modal-usuarios").modal("show");


                            } else if (bandera.includes('error')) {
                                swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                                tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
                            }
                        }, error: function () {
                            swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                        }, complete: function (jqXHR, textStatus) {
                            $('#loadAction').fadeOut();
                            tblUsuarios.$('[data-toggle="tooltip"]').tooltip('hide');
                        }
                    });
                }
            });
        });

        $('#divTblRol').on('click', '.js-swal-confirm', function () {
            var buttonVal = $(this).attr("id").split('_');
            tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
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
                    url: '../Transporte/queryCUsuarios.jsp',
                    data: '&txtBandera=9&idRol=' + buttonVal[1],
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Registro eliminado!', 'El rol seleccionado ha sido eliminado', 'success');
                            LoadTable();
                            tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                        } else if (resp.includes('error')) {
                            swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                            tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                        } else if (resp.includes("link")) {
                            swal('¡Upps!', 'El rol que intentas eliminar tiene usuarios relacionados', 'warning');
                            tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                        }
                    }, error: function () {
                        swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                        tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                    }
                });
            }, function () {
                tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
            });
        });
        $('#divTblRol').on('click', '.btnConsultarRol', function () {
            var buttonVal = $(this).attr("id").split('_');
            $('#loadAction').fadeIn();
            $.ajax({
                url: '../Transporte/queryCUsuarios.jsp',
                data: '&txtBandera=10&idRol=' + buttonVal[1],
                type: 'POST',
                success: function (resp) {
                    var bandera = resp.split("°")[1];
                    if (bandera.includes('success')) {
                        var datos_modal = resp.split("°")[2].split("¬");
                        var catalogo_permisos = resp.split("°")[0];
                        $("#idRol").val(datos_modal[0]);
                        $("#txtNombreRol").val(datos_modal[1]);
                        $("#txtDescripcionRol").val(datos_modal[2]);
                        $("#txtNombreRol").attr("disabled", true);
                        $("#txtDescripcionRol").attr("disabled", true);
                        //OCULTAMOS LOS BOTONES DEL MODAL QUE NO SON NECESARIOS
                        $(".btnAccionesRoles").hide();
                        $("#divTipoRolIn").addClass("open");
                        $("#divDescripRolIn").addClass("open");
                        $("#panel-permisos").html(catalogo_permisos);
                        $(".permisos").attr("disabled", true);
                        $("#marcarTodos").attr("disabled", true);
                        $(".checkPanel").attr("disabled", true);
                        $("#modaltitleRol").html("CONSULTAR ROL");
                        tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                        $("#modal-roles").modal("show");
                    } else if (bandera.includes('error')) {
                        swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                        tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                    }
                }, error: function () {
                    swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                    tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                }, complete: function (jqXHR, textStatus) {
                    $('#loadAction').fadeOut();
                    tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                }
            });
        });
        $('#divTblRol').on('click', '.btnEditarRol', function () {
            var buttonVal = $(this).attr("id").split('_');
            $('#loadAction').fadeIn();
            $.ajax({
                url: '../Transporte/queryCUsuarios.jsp',
                data: '&txtBandera=10&idRol=' + buttonVal[1],
                type: 'POST',
                success: function (resp) {
                    var bandera = resp.split("°")[1];
                    if (bandera.includes('success')) {
                        var datos_modal = resp.split("°")[2].split("¬");
                        var catalogo_permisos = resp.split("°")[0];

                        $("#idRol").val(datos_modal[0]);
                        $("#txtNombreRol").val(datos_modal[1]);
                        $("#txtDescripcionRol").val(datos_modal[2]);
                        $("#txtNombreRol").attr("disabled", false);
                        $("#txtDescripcionRol").attr("disabled", false);

                        //OCULTAMOS LOS BOTONES DEL MODAL QUE NO SON NECESARIOS
                        $("#btnRegistrarRol").hide();
                        $("#btnUpdateRol").show();
                        $("#divTipoRolIn").addClass("open");
                        $("#divDescripRolIn").addClass("open");

                        $("#panel-permisos").html(catalogo_permisos);
                        $(".permisos").attr("disabled", false);
                        $("#marcarTodos").attr("disabled", false);
                        $(".checkPanel").attr("disabled", false);

                        $("#modaltitleRol").html("MODIFICAR ROL");
                        $("#modal-roles").modal("show");

                        tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                    } else if (bandera.includes('error')) {
                        swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada', 'error');
                        tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                    }
                }, error: function () {
                    swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                }, complete: function (jqXHR, textStatus) {
                    $('#loadAction').fadeOut();
                    tblRoles.$('[data-toggle="tooltip"]').tooltip('hide');
                }
            });
        });
    }


    function limpiarFormulario() {
        $("#IdUsuario").val("");
        $("#txtNombreUsuario").val("");
        $("#txtApaternoUsuario").val("");
        $("#txtAmaternoUsuario").val("");
        $("#txCorreoUsuario").val("");
        $("#lstSexoUsuario").val("").trigger("chosen:updated");
        $("#lstTipoUsuario").val("").trigger("chosen:updated");
        $("#txtUsuario").val("");
        $("#txtPasswordUsuario").val("");
        $("#txtConfirmPassUsuario").val("");
    }

    /**
     * @author Braulio Sorcia
     * @param {boolean} e true si agrega la clase o false si la remueve 
     * @description Agrega o quita la clase open a los div contenedores de los input del formulario Firmantes
     */
    function abrirCerrarLabel(e) {
        if (e) {
            $("#divNombreUsuarioIn").addClass('open');
            $("#divApaternoUsuarioIn").addClass('open');
            $("#divAmaternoUsuarioIn").addClass('open');
            $("#divCorreoUsuarioIn").addClass('open');
            $("#divUsuarioIn").addClass('open');
            $("#divPasswordUsuarioIn").addClass('open');
        } else {
            $("#divNombreUsuarioIn").removeClass('open');
            $("#divApaternoUsuarioIn").removeClass('open');
            $("#divAmaternoUsuarioIn").removeClass('open');
            $("#divCorreoUsuarioIn").removeClass('open');
            $("#divUsuarioIn").removeClass('open');
            $("#divPasswordUsuarioIn").removeClass('open');
        }
    }

    /**
     * @author Braulio Sorcia
     * @description Habilita o deshabilita los inputs del modal usuarios
     * @param {boolean} e Bandera para habilitar/deshabilitar los inputs del modal firmantes. true para deshabilitar, false para habilitar
     * 
     */
    function deshabilitarInputs(e) {
        $("#txtNombreUsuario").attr("disabled", e);
        $("#txtApaternoUsuario").attr("disabled", e);
        $("#txtAmaternoUsuario").attr("disabled", e);
        $("#txCorreoUsuario").attr("disabled", e);
        $("#lstSexoUsuario").prop("disabled", e).trigger("chosen:updated");
        $("#lstTipoUsuario").prop("disabled", e).trigger("chosen:updated");
        $("#txtUsuario").attr("disabled", e);
        $("#txtPasswordUsuario").attr("disabled", e);
    }

    $('.btnAccionesUsuarios').click(function () {
        $("form[name='formUsuarios']").submit(function (e) {
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
                txtNombreUsuario: {
                    required: true
                },
                txtApaternoUsuario: {
                    required: true
                },
                txCorreoUsuario: {
                    required: true,
                    email: true
                },
                lstSexoUsuario: {
                    required: true
                },
                lstTipoUsuario: {
                    required: true
                },
                txtUsuario: {
                    required: true,
                    minlength: 5
                },
                txtPasswordUsuario: {
                    required: true,
                    minlength: 5
                },
                txtConfirmPassUsuario: {
                    required: true,
                    equalTo: '#txtPasswordUsuario'
                }
            },
            messages: {
                txtNombreUsuario: "¡Este campo es requerido!",
                txtApaternoUsuario: "¡Este campo es requerido!",
                lstSexoUsuario: "¡Este campo es requerido!",
                lstTipoUsuario: "¡Este campo es requerido!",
                txtUsuario: {
                    required: "¡Este campo es requerido!",
                    minlength: "¡El nombre de usuario debe ser al menos de 5 caracteres!"
                },
                txCorreoUsuario: {
                    required: "¡Este campo es requerido!",
                    email: "¡El formato del correo no es el adecuado!"
                },
                txtPasswordUsuario: {
                    required: "¡Este campo es requerido!",
                    minlength: "¡La contraseña debe ser al menos de 5 caracteres!"
                },
                txtConfirmPassUsuario: {
                    required: "¡Este campo es requerido!",
                    equalTo: "¡Las contraseñas no son iguales!"
                }
            },
            submitHandler: function (form) {
                var bandera = $('#bandera').val();
                $.ajax({
                    url: '../Transporte/queryCUsuarios.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    success: function (response) {
                        if (bandera.includes("4")) {
                            if (response.includes("success")) {
                                swal('¡Completado!', '¡El usuario fue registrado con éxito!', 'success');
                                limpiarFormulario();
                                $("#modal-usuarios").modal("hide");
                                LoadTable();
                            } else if (response.includes("error")) {
                                swal('¡Error!', 'Error interno del servidor, intente de nuevo o contacte a soporte técnico.', 'error');
                            } else if (response.includes("email")) {
                                swal('¡Upps!', 'El correo ingresado ya está registrado dentro del sistema, pruebe con otro.', 'warning');
                            } else if (response.includes("usuario")) {
                                swal('¡Upps!', 'El usuario ingresado ya está registrado dentro del sistema, pruebe con otro.', 'warning');
                            }
                        } else if (bandera.includes("5")) {
                            if (response.includes("success")) {
                                swal('¡Completado!', '¡El usuario fue actualizado con éxito!', 'success');
                                limpiarFormulario();
                                $("#modal-usuarios").modal("hide");
                                LoadTable();
                            } else if (response.includes("error")) {
                                swal('¡Error!', 'Error interno del servidor, intente de nuevo o contacte a soporte técnico', 'error');
                            } else if (response.includes("email")) {
                                swal('¡Upps!', 'El correo ingresado ya está registrado dentro del sistema, pruebe con otro.', 'warning');
                            } else if (response.includes("usuario")) {
                                swal('¡Upps!', 'El usuario ingresado ya está registrado dentro del sistema, pruebe con otro.', 'warning');
                            }
                        }
                    }, error: function () {
                        swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                    }
                });
            }
        });
    });


    $('.btnAccionesRoles').click(function () {
        $("form[name='formRoles']").submit(function (e) {
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
                txtNombreRol: {
                    required: true
                }
            },
            messages: {
                txtNombreRol: "¡Este campo es requerido!"
            },
            submitHandler: function (form) {
                var bandera = $('#bandera').val();
                var txtNombreRol = $("#txtNombreRol").val();
                var idRol = $("#idRol").val();
                var descripRol = $("#txtDescripcionRol").val();
                if (esRolesSeleccionados()) {
                    $('#loadAction').fadeIn();
                    $.ajax({
                        url: '../Transporte/queryCUsuarios.jsp',
                        type: 'POST',
                        data: "txtBandera=" + bandera + "&idRol=" + idRol + "&txtNombreRol=" + txtNombreRol + "&txtCadenaIdRoles=" + cadena_rol_seleccionado + "&txtDescripRol=" + descripRol,
                        success: function (response) {
                            if (bandera.includes("7")) {
                                if (response.includes("success")) {
                                    swal('¡Completado!', '¡El rol fue agregado correctamente!', 'success');
                                    $("#modal-roles").modal("hide");
                                    LoadTable();
                                    cadena_rol_seleccionado = "";
                                } else if (response.includes("rol")) {
                                    swal('¡Upps!', '¡El rol que intentas agregar ya existe!', 'warning');
                                    cadena_rol_seleccionado = "";
                                }
                            } else if (bandera.includes("8")) {
                                if (response.includes("success")) {
                                    swal('¡Completado!', '¡El rol fue modificado correctamente!', 'success');
                                    $("#modal-roles").modal("hide");
                                    LoadTable();
                                    cadena_rol_seleccionado = "";
                                } else if (response.includes("rol")) {
                                    swal('¡Upps!', '¡El rol que intentas modificar ya existe!', 'warning');
                                    cadena_rol_seleccionado = "";
                                }
                            }
                            $('#loadAction').fadeOut();
                        }, error: function () {
                            swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                            $('#loadAction').fadeOut();
                            cadena_rol_seleccionado = "";
                        }
                    });
                } else {
                    swal('¡Error!', 'Debes seleccionar los permisos relacionados al rol', 'error');
                }
            }
        });
    });

    /**
     * @author Braulio Sorcia
     * @description Reinicializa el formulario y elimina la clase has-error del mismo.
     */
    function resetForm() {
        $("#formUsuarios div").removeClass("has-error");
        $("#formUsuarios div").remove(".help-block");
    }

    $("#marcarTodos").on("click", function () {
        if ($("#marcarTodos").is(":checked")) {
            $("div .permisos").prop("checked", true);
            $(".checkPanel").prop("checked", true);
        } else {
            $("div .permisos").prop("checked", false);
            $(".checkPanel").prop("checked", false);
        }
    });

    function esRolesSeleccionados() {
        let isChecked = false;
        cadena_rol_seleccionado = "";
        $(".panel-body").each(function () {
            var idPermiso = $(this).attr("id");
            cadena_rol_seleccionado += idPermiso.split("_")[1] + "^";
            $("#" + idPermiso + " .permisos").each(function () {
                var input = $(this);
                if (input.is(":checked")) {
                    isChecked = true;
                    cadena_rol_seleccionado += input.attr("id").split("_")[1] + "°1¬";
                } else {
                    cadena_rol_seleccionado += input.attr("id").split("_")[1] + "°0¬";
                }
            });
            cadena_rol_seleccionado = cadena_rol_seleccionado.substring(0, cadena_rol_seleccionado.length - 1);
            cadena_rol_seleccionado += "*";
        });
        //console.log(cadena_rol_seleccionado = cadena_rol_seleccionado.substring(0, cadena_rol_seleccionado.length - 1));
        return isChecked;
    }


    $("#panel-permisos").on("click", ".checkPanel", function () {
        var eId = $(this).attr("id");
        if ($("#" + eId).is(":checked")) {
            $("#collapse" + eId.split("_")[1] + " input").each(function () {
                $(this).prop("checked", true);
            });
        } else {
            $("#collapse" + eId.split("_")[1] + " input").each(function () {
                $(this).prop("checked", false);
            });
        }
    });

    function resize() {
        window.onresize = function () {
            ajustarComponentesModal();
        };

        window.onload = function () {
            ajustarComponentesModal();
        };

        function ajustarComponentesModal() {
            if (window.innerWidth <= 991 && window.innerWidth > 767) {
                $("#divSexoUsuario").css("margin-top", "15px");
                $("#divTipoUsuario").css("margin-top", "15px");
            } else if (window.innerWidth <= 767) {
                $("#divCorreoUsuario").css("margin-top", "-15px");
                $("#divSexoUsuario").css("margin-top", "15px");
                $("#divTipoUsuario").css("margin-top", "15px");
                $("#divUsuario").css("margin-top", "-15px");
            } else {
                $("#divCorreoUsuario").css("margin-top", "");
                $("#divSexoUsuario").css("margin-top", "");
                $("#divUsuario").css("margin-top", "");
                $("#divTipoUsuario").css("margin-top", "");
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
                    if (stringStepFirst.split("¬")[0].includes("1") || stringStepFirst.split("¬")[8].includes("1")) {
                        LoadTable();
                        if (stringStepFirst.split("¬")[0].includes("0")) {
                            $("#btabs-static-justified-usuarios #noPermissonU").siblings("div").remove();
                            $("#btabs-static-justified-usuarios #noPermissonU").show();
                        } else if (stringStepFirst.split("¬")[8].includes("0")) {
                            $("#btabs-static-justified-roles_permisos #noPermissonR").siblings("div").remove();
                            $("#btabs-static-justified-roles_permisos #noPermissonR").show();
                        }
                        if (stringStepFirst.split("¬")[1].includes("0")) {
                            $("#btnNuevoUsuario").remove();
                            $("#btnRegistrarUsuario").remove();
                        }
                        if (stringStepFirst.split("¬")[9].includes("0")) {
                            $("#btnNuevoRol").remove();
                            $("#btnRegistrarRol").remove();
                        }
                        if (stringStepFirst.split("¬")[3].includes("0")) {
                            $("#btnUpdateUsuario").remove();
                        }
                        if (stringStepFirst.split("¬")[11].includes("0")) {
                            $("#btnUpdateRol").remove();
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