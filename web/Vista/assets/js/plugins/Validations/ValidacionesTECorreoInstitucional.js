/* 
 Creador:  Ricardo Reyna 
 Document: JS para validacion de acciones de correo Institucional
 */

$(document).ready(function () {
    var tblCorreoInstitucionals; //tblFirmantes
    var desabilitar = 0; //Variable global para desabilitar o permitir el boton modificar/añadir 
    var opcion = 0; //Variable global para elegir que metodo utilizar y mandar peticiones    
    primerPaso();

    var permisosTargets;

//Función para añadir un correo nuevo
    $("#btnNuevoCorreoInstitucional").click(function () {

        $('#btnRegistraCorreo').show(); //Mostramos el boton añadir en el modal
        $('#btnRegistraCorreo').prop('disabled', true); //Deshabilitamos el boton mientras no compruebe el correo
        $("#btnModificarCorreo").hide(); //Escondemos el botón modificar en el modal
        desabilitar = 1; //Se declara en uno para quitar el disabled una vez comprobado el correo
        limpiarFormulario();
        $("#modal-CorreoInstitucional").modal("show");
    });

    /* Inicializacion de la DataTable */
    function initTable() {
        tblCorreoInstitucionals = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            paging: true,
            orderable: false,
            searching: true,
            info: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: permisosTargets}], //Los targets deben coincidir con el numero de columnas de la datatable
            pageLength: 15,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]]
                    //Establecemos el tamaño de las columnas de nuestra tabla
                    //columns: [null, {width: "15%"}, {width: "15%"}, {width: "15%"}, {width: "15%"}, {width: "30%"}, {width: "10%"}]
        });
        $('#mainLoader').fadeOut();
    }

    function cargarTabla() {
        $('#loadAction').fadeIn();
        $.ajax({
            url: '../Transporte/queryCCorreoInstitucional.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var indicador = resp.split("|")[0];
                if (indicador.toString().trim() === "success") {
                    $('#divTblCorreoInstitucional').html(resp.split("|")[1]);

                    initTable();
                    $("#tblCorreoInstitucionals_filter input").val("").trigger("keyup");
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function () {

                $('#loadAction').fadeOut();
            }
        });
    }

    //Función para registrat el correo
    $("#btnRegistraCorreo").click(function () {
        validarFormulario(3);
    });

    //Función para Modificar el correo
    $("#btnModificarCorreo").click(function () {
        validarFormulario(4);

    });

    $("#btnComprobarCorreo").click(function () {
        validarFormulario(2);
    });


    function validarFormulario(bandera) {
        opcion = bandera;
        $("form[name='formCorreoInstitucional']").validate({
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
                txtCorreoDestinario: {
                    required: true,
                    email: true,
                    accept: "[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}" //Metodo para verificar que sea de tipo email (.... @.com)
                },
                txtContrasena: {
                    required: true
                },
                txtPuerto: {
                    required: true

                },
                txtHost: {
                    required: true
                },
                txtCorreoInstitucional: {
                    required: true,
                    email: true,
                    accept: "[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}" //Metodo para verificar que sea de tipo email (.... @.com)
                }

            },

            submitHandler: function (e) {
                $('#loadAction').fadeIn();
                //Extraccion de datos de la row seleccionada
                let correoD = $("#txtCorreoDestinario").val();
                let mensaje = $("#txtMensaje").val();
                let correoI = $("#txtCorreoInstitucional").val();
                let contra = $("#txtContrasena").val();
                let puerto = $("#txtPuerto").val();
                let host = $("#txtHost").val();
                let id = $("#idCorreoInstitucional").val();
                //Fin de extracción de datos

                $.ajax({
                    url: '../Transporte/queryCCorreoInstitucional.jsp',
                    data: '&txtBandera=' + opcion +
                            "&correoD=" + correoD +
                            "&mensaje=" + mensaje +
                            "&correoI=" + correoI +
                            "&contra=" + contra +
                            "&puerto=" + puerto +
                            "&id=" + id +
                            "&host=" + host,
                    type: 'POST',
                    success: function (data) {
                        let respuesta = "";
                        respuesta = data.trim();
                        switch (opcion) {
                            case 2: //Comprueba el correo
                                if (respuesta === "success") {
                                    show_swal("Se ha enviado el correo exitosamente", "Ya puedes guardar el correo!", "success");
                                    desabilitar === 1 ? $("#btnRegistraCorreo").removeAttr('disabled') : $("#btnModificarCorreo").removeAttr('disabled');
                                    opcion = 0;
                                } else {
                                    show_swal("¡Upps!", "Hubo un error al enviar el correo", "error");
                                    opcion = 0;
                                }

                                break;
                            case 3: //Inserta un nuevo correo

                                if (respuesta === "success") {
                                    swal('¡Completado!', '¡El Correo fue registrado con éxito!', 'success');
                                    limpiarFormulario();
                                    $("#modal-CorreoInstitucional").modal("hide");
                                    cargarTabla();
                                    opcion = 0;
                                } else {
                                    show_swal("¡Upps!", data.split('|')[1], "error");
                                    opcion = 0;
                                }

                                break;
                            case 4: //Modifica el correo

                                if (respuesta === "success") {
                                    swal('¡Completado!', '¡El Correo fue modificado con éxito!', 'success');
                                    limpiarFormulario();
                                    $("#modal-CorreoInstitucional").modal("hide");
                                    cargarTabla();
                                    opcion = 0;
                                } else {
                                    show_swal("¡Upps!", data.split('|')[1], "error");
                                    opcion = 0;
                                }

                                break;

                            default:

                                opcion = 0;
                        }

                    }, complete: function () {
                        $('#loadAction').fadeOut(); //Se esconde el loader
                    }
                });
            }
        });
    }

    //Función para visualizar los datos en el modal
    $('#divTblCorreoInstitucional').on('click', '.btnEditarCorreo', function () {

        //Extraccion de datos de la row seleccionada
        let id = $(this).attr("id").split('_')[1];
        let correo = $("#correo_" + (id)).text();
        let puerto = $("#Puerto_" + (id)).text();
        let host = $("#Host_" + (id)).text();
        let contra = $("#Contra_" + (id)).text();
        //Fin de extracción de datos

        desabilitar = 2; //Se declara en dos para quitar el disabled una vez comprobado el correo
        $("#modaltitle").html("Modicar Correo Institucional");
        abrirCerrarLabel(true); //Clase para mover los fields por defecto hacia arriba

        //Deposito de datos dentro de los inputs del modal
        $("#idCorreoInstitucional").val(id);
        $("#btnModificarCorreo").show();
        $("#txtCorreoDestinario").val("");
        $("#txtMensaje").val("");
        $("#txtCorreoInstitucional").val(correo);
        $("#txtContrasena").val(contra);
        $("#txtPuerto").val(puerto);
        $("#txtHost").val(host);
        //fin de deposito

        $('#btnRegistraCorreo').hide(); //Escondemos el boton de añadir, ya que vamos a modificar
        $("#modal-CorreoInstitucional").modal("show"); //Abrimos el modal
        $('#btnModificarCorreo').prop('disabled', true); //Agregamos el disabled al boton de modificar para que el usuario no pueda modifciar hasta que compruebe el correo
    });

    //Función para eliminar el correo permanentemente, en esta función no se pudo reutilizar la función de verificarFormulario(), ya que no vamos a verificar el formulario
    $('#divTblCorreoInstitucional').on('click', '.btnEliminarCorreo', function () {

        let id = $(this).attr("id").split('_')[1]; //Obtención de valor del id de la row seleccionada

        swal({
            type: 'warning',
            title: '¿Estás seguro de eliminar este correo?',
            text: '¡Al confirmar, los datos no se podran recuperar! ¿Desea continuar?',
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
                url: '../Transporte/queryCCorreoInstitucional.jsp',
                data: '&txtBandera=5' +
                        "&id=" + id,
                type: 'POST',
                success: function (data) {
                    let respuesta = "";
                    respuesta = data.trim();
                    if (respuesta === "success") {
                        swal('¡Completado!', '¡El Correo fue eliminado con éxito!', 'success');
                        cargarTabla();
                    } else {
                        show_swal("¡Upps!", data.split('|')[1], "error");
                    }
                }
            });
        }).catch(swal.noop); //Se pone el catch ya que no es una función asyncrona. si se lo quitamos tenemos que convertir la fucnion a asyncorna

    });


    //Función para remover o añadir la clase open a los div de los inputs c
    function abrirCerrarLabel(boolean) {
        if (boolean) {
            $("#divCorreoInstitucionalIn").addClass('open');
            $("#divContrasenaIn").addClass('open');
            $("#divPuertoIn").addClass('open');
            $("#divHostIn").addClass('open');
            $("#divCorreoDestinatarioIn").addClass('open');
            $("#divMensajeIn").addClass('open');

        } else {
            $("#divCorreoInstitucionalIn").removeClass('open');
            $("#divContrasenaIn").removeClass('open');
            $("#divPuertoIn").removeClass('open');
            $("#divHostIn").removeClass('open');
            $("#divCorreoDestinatarioIn").removeClass('open');
            $("#divMensajeIn").removeClass('open');

        }
    }


    //funcion para limpiar el formulario una vez presionado el botón
    $("#btnLimpiarForm").click(function () {
        limpiarFormulario();
    });

    //Función para limpiar los campos del formulario
    function limpiarFormulario() {
        $("#txtCorreoDestinario").val("");
        $("#txtMensaje").val("");
        $("#txtCorreoInstitucional").val("");
        $("#txtContrasena").val("");
        $("#txtPuerto").val("");
        $("#txtHost").val("");
    }

    //metodo para que un campo solo admita numeros si es un tipo text
    $(".JustNumbers").keydown(function (e) {
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
                (e.keyCode == 65 && (e.ctrlKey === true || e.metaKey === true)) ||
                (e.keyCode == 67 && (e.ctrlKey === true || e.metaKey === true)) ||
                (e.keyCode == 88 && (e.ctrlKey === true || e.metaKey === true)) ||
                (e.keyCode >= 35 && e.keyCode <= 39)) {
            return;
        }
        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
            e.preventDefault();
        }
    });


    //MENSAJES GENERALES PARA VALIDAR LOS CAMPOS
    jQuery.extend(jQuery.validator.messages, {
        email: "Por favor, escribe una dirección de correo válida",
        required: "Este campo es obligatorio."
    });

    //Función para mostrar alertas al usuario
    function show_swal(titulo, mensaje, tipo) {
        swal({
            allowOutsideClick: false,
            allowEscapeKey: false,
            title: titulo,
            html: mensaje,
            type: tipo
        });
    }





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
                    //cargaInicio();
                    cargarTabla();
                } else if (resp[0].includes("acceso")) {
                    cargarTabla();
                    let stringStepFirst = resp[0].split("°")[1];

                    //Significa que tiene permiso de acceder
                    if (stringStepFirst.split("¬")[0].includes("1")) {

                        stringStepFirst.split("¬")[3].includes("0") && stringStepFirst.split("¬")[4].includes("0") ? permisosTargets = 4 : permisosTargets = 5;

                        stringStepFirst.split("¬")[1].includes("0") ? $("#btnNuevoCorreoInstitucional").remove() : $("#btnNuevoCorreoInstitucional").show();

                        $('#mainLoader').fadeOut();
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
            }, complete: function () {

            }
        });
    }

});



