function noBlankSpaces() {
    if (event.keyCode == 32) {
        return false;
    }
}
$(document).ready(function () {
    $.sessionTimeout();
    $('#btnCambiarContrasenia').click(function () {
        $("form[name='formCambiarContra']").submit(function (e) {
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
                txtContraseniaAc: {
                    required: true
                },
                txtNuevaContrasenia: {
                    required: true,
                    minlength: 5
                },
                txtNuevaContraseniaConfirm: {
                    required: true,
                    equalTo: '#txtNuevaContrasenia'
                }
            },
            messages: {
                txtContraseniaAc: "¡Este campo es requerido!",
                txtNuevaContrasenia: {
                    required: "¡Este campo es requerido!",
                    minlength: "¡La contraseña debe ser al menos de 5 caracteres!"
                },
                txtNuevaContraseniaConfirm: {
                    required: "¡Este campo es requerido!",
                    equalTo: "¡Las contraseñas no son iguales!"
                }
            },
            submitHandler: function (form) {
                $('#loadAction').fadeIn();
                var txtContraseniaAc = $("#txtContraseniaAc").val();
                var txtNuevaContrasenia = $("#txtNuevaContrasenia").val();
                $.ajax({
                    url: '../Transporte/queryCCambiarContrasenia.jsp',
                    type: 'POST',
                    data: {
                        txtBandera: "1",
                        txtContraseniaActual: txtContraseniaAc,
                        txtNuevaContrasenia: txtNuevaContrasenia
                    },
                    success: function (response) {
                        if (response.includes("success")) {
                            swal('¡Completado!', '¡La contraseña ha sido actualizada!', 'success');
                            limpiarCampos();
                            $("#modal-cambioC").modal("hide");
                        } else if (response.includes("noIgual")) {
                            swal('¡Upps!', 'Las contraseña actual no es la correcta, verifica tu información', 'warning');
                        } else if (response.includes("error")) {
                            swal('¡Error!', 'Error interno del servidor, intente de nuevo o contacte a soporte técnico', 'error');
                        }
                    }, error: function () {
                        swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                    }, complete: function (jqXHR, textStatus) {
                        resetForm();
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });

    /**
     * @author Braulio Sorcia
     * @description Reinicializa el formulario y elimina la clase has-error del mismo.
     */
    function resetForm() {
        $("#formCambiarContra div").removeClass("has-error");
        $("#formCambiarContra div").remove(".help-block");
    }

    /**
     * @author Braulio Sorcia
     * @description Limpia los campos en el formulario firmantes
     */
    function limpiarCampos() {
        $("#txtContraseniaAc").val("");
        $("#txtNuevaContrasenia").val("");
        $("#txtNuevaContraseniaConfirm").val("");
    }
});


