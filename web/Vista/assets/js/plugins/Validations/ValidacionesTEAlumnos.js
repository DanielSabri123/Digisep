//CETT and my ponies ._.
$(document).ready(function () {
    $("#lstCarreras").chosen({width: "100%", disable_search_threshold: 4});
    resize();
    var tblAlumnos;
    function initTable() {
        tblAlumnos = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            orderable: false,
            paging: true,
            searching: true,
            //Se queda en la pagian actual donde esta el usuario.
            //Por ejemplo si inserta normalmente la tabla te pondra en la primera pagina,
            //pero con esta funcion activada te dejara en la pagina en donde se encuentre el usuario.
            //Esto tambien afecta a la hora de buscar, ya que te pondra en la pagina actual.
            stateSave: true,
            info: true,
            //numeros de abajo de la tabla, se muestra todo los numeros
            pagingType: "full_numbers",
            //que no se puede ordenar la columna 15
            columnDefs: [{orderable: false, targets: 15}],
            //Numero de filas que se muestran
            pageLength: 15,
            //la selección de paginacion (mostrar 5, 10, 15, 20, 50 filas)
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]],
            //ancho de las columnas
            "columns": [
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                {"width": "20%"},
                {"width": "20%"},
                {"width": "20%"},
                {"width": "5%"},
                {"width": "20%"},
                {"width": "10%"},
                {"width": "5%"}
            ]
        });
    }
    initTable();
    function LoadTable() {
        $(".tooltip").hide();
        $.ajax({
            url: '../Transporte/queryCAlumnos.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var indicador = resp.split("|")[0];
                if (indicador.toString().trim() === "success") {
                    $('#DivTblAlumnos').html(resp.split("|")[1]);
                    $('[data-toggle="tooltip"]').tooltip();
                    TableActions();
                    initTable();
                    validatePermits();
                    setTimeout(function () {
                        $('#mainLoader').fadeOut();
                    });
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function () {
                $(".tooltip").hide();
            }
        });
    }
    
    
    var banderaPermiso = [];
    function validatePermits() {
        /**
         * dentro de la función se edita conforme a las acciones que el módulo lo requiera, se requiere revisar la tabla Catalogo_Permisos.
         */
        /*Consultar*/
        (banderaPermiso[2] === '0' ? $("#DivTblAlumnos").find(".btnMostrarAlumno").remove() : "");
        /*Editar*/
        (banderaPermiso[3] === '0' ? $("#DivTblAlumnos").find(".btnModificarAlumno").remove() : "");
        
//        if (banderaPermiso[1] === '0' && banderaPermiso[3] === '0'
//                && banderaPermiso[4] === '0' && banderaPermiso[5] === '0'
//                && banderaPermiso[6] === '0' && banderaPermiso[7] === '0') {
//            $("#tblAlumno tbody tr").map(function (index, element) {
//                $(element).find("td").last().children().remove();
//                $(element).find("td").last().append("<span class='label label-danger'>Sin permisos</span>");
//            });
//        }

    }

    $('#btnImportarCarrerasExcel').click(function () {
        $('#fileAlumnos').click();
    });

    $('#fileAlumnos').on('change', function () {
        var archivo = $(this).val();

        if (archivo != null && archivo.toString() != "undefined") {
            if (archivo.toString().endsWith('.xls') || archivo.toString().endsWith('.xlsx')) {
                swal('¡Cargando información!', 'Se están registrando los alumnos en el sistema', 'info').then(function () {
                    $('#loadAction').fadeIn();
                });
                $('#importarArchivo').click();
            } else {
                swal('¡Upps!', 'Debes seleccionar el formato predeterminado con la información de los alumnos', 'warning');
                $('#fileAlumnos').val('');
            }
        }
    });

    $('.btnAccionesAlumno').click(function () {
        $("form[name='FormAlumno']").submit(function (e) {
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
                    url: '../Transporte/queryCAlumnos.jsp',
                    type: 'POST',
                    data: new FormData(form),
                    processData: false,
                    contentType: false,
                    success: function (resp) {
                        if (resp.toString().includes('success')) {
                            swal('¡Alumnos agregados!', 'La importación de los alumnos se ha completado con éxito', 'success');
                            cargarListaCarreras();
                        } else if (resp.toString().includes('infoIncompleta')) {
                            if (resp.includes('||')) {
                                var split = resp.split("||");
                                swal('¡Upps!', 'La información de los alumnos en la página ' + split[1] + ' está incompleta, por favor revise el archivo e intente nuevamente. Recuerde que solamente el apellido materno es opcional', 'warning');
                            } else {
                                swal('¡Upps!', 'La información de los alumnos está incompleta, por favor revise el archivo e intente nuevamente. Recuerde que solamente el apellido materno es opcional', 'warning');
                            }

                        } else if (resp.toString().includes('sinAlumnos')) {
                            var split = resp.split('||');
                            swal('¡Upps!', 'No se encontraron alumnos para registar en la página ' + split[1] + '. Los registros posteriores NO fueron realizados', 'warning');
                        } else if (resp.toString().includes('formatoInvalido')) {
                            swal('¡Upps!', 'El archivo seleccionado no cumple con el formato requerido, recuerde que no debe modificar la estructura del formato', 'warning');
                        } else if (resp.toString().includes('sinCarrera')) {
                            var split = resp.split('||');
                            swal('¡Upps!',
                                    'El id de la carrera asignada al alumno ' + split[2] + " con matrícula " + split[1] +
                                    " no está registrada en el sistema, por favor rectifique la clave o registre la carrera para continuar. \n" +
                                    "Los registros posteriores a este alumno no se realizaron", 'error');
                            cargarListaCarreras();
                        } else if (resp.toString().includes('certificadoActivo')) {
                            var split = resp.split('||');
                            swal('¡Upps!', 'El alumno(a) ' + split[2] + ' con matrícula ' + split[1] +
                                    ' tiene un certificado electrónico activo, por lo que no es posible modificar su información. Los registros posteriores no fueron realizados', 'warning');
                            cargarListaCarreras();
                        } else if (resp.toString().includes('tituloActivo')) {
                            var split = resp.split('||');
                            swal('¡Upps!', 'El alumno(a) ' + split[2] + ' con matrícula ' + split[1] +
                                    ' tiene un título electrónico activo, por lo que no es posible modificar su información. Los registros posteriores no fueron realizados', 'warning');
                            cargarListaCarreras();
                        } else if (resp.toString().includes('curp')) {
                            var split = resp.split('||');
                            show_swal('¡Upps!', 'El alumno(a) ' + split[2] + ' con matrícula ' + split[1] +
                                    ' tiene una CURP (<b>' + split[3] + '</b>) que no es válida.<br><small>Los registros posteriores no fueron procesados.</small>', 'warning');
                            cargarListaCarreras();
                        } else if (resp.toString().includes('fechaExtranjero')) {
                            var split = resp.split('||');
                            show_swal('¡Upps!', 'El alumno(a) ' + split[2] + ' con matrícula ' + split[1] +
                                    ' no tiene una fecha de nacimiento: (<b>' + split[3] + '</b>) teniendo CURP como extranjero.<br><small>Los registros posteriores no fueron procesados.</small>', 'warning');
                        } else if (resp.toString().includes('celdaNoValida')) {
                            var split = resp.split('||');
                            show_swal('¡Upps!', 'El sistema encontró una celda con información no válida.<br> Fila: ' + split[1] + '. Columna: ' + split[2], 'warning');
                        } else if (resp.toString().includes('usoFormulas')) {
                            var split = resp.split('||');
                            show_swal('¡Upps!', 'No se permite el uso de fórmulas dentro del proceso. Fórmula encontrada en la página <b>' + split[1] + '</b>, fila <b>' + split[2] + '</b>, columna <b>' + split[3] + '</b> por favor revisa el archivo.', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function () {
                        $('#fileAlumnos').val('');
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
    });

    function TableActions() {
        //al hacerle click a un boton con la clase btnMostrarAlumno, se ejecutara el siguiente codigo:
        $("#DivTblAlumnos").on("click", ".btnMostrarAlumno", function () {
            //se cambiara el titulo del modal
            $("#modaltitle").html('Consultar Alumno');
            //Se repociciona el modal a su lugar original
            $("#modal-alumnos-draggable").removeAttr("style");
            $("#modal-alumnos").removeAttr('style');
            $('#modal-alumnos').modal('show');
            //se actualiza para que esten vacias las listas
            $("#lstPlantelGrupoE").val('').trigger("chosen:updated");
            $("#lstSexoAlumno").val('').trigger("chosen:updated");
            //se esconden los botones de agregar, modificar y limpiar;
            //se muestra el boton de cerrar
            $('#btnRegistrarAlumno').hide();
            $('#btnUpdateAlumno').hide();
            $('#btnLimpiarAlumnoModal').hide();
            $('#btnCerrarAlumno').show();
            var buttonval = this.value.split('_');

            //llenamos las variables para obtener la informacion de los td
            var Matricula = $("#Matricula_" + buttonval[1]).text();
            var Nombre = $("#Nombre_" + buttonval[1]).text();
            var APaterno = $("#APaterno_" + buttonval[1]).text();
            var AMaterno = $("#AMaterno_" + buttonval[1]).text();
            var Curp = $("#Curp_" + buttonval[1]).text();
            var IDCarrera = $("#idCarrera_" + buttonval[1]).text();
            var Generacion = $("#Generacion_" + buttonval[1]).text();
            var Carrera = $("[name='Carrera_" + buttonval[1] + "']").text();
            var Sexo = $("#Sexo_" + buttonval[1]).text();
            var FInicio = $("#FechaInicio_" + buttonval[1]).text();
            var FFin = $("#FechaFin_" + buttonval[1]).text();
            var Correo = $("#Correo_" + buttonval[1]).text();
            var FNacimiento = $("#FNacimiento_" + buttonval[1]).text();

            //Condicional para verificar que si es H, entonces en Sexo se mostrara la palabra Hombre y igualmente con el sexo femenino
            if (Sexo === "H") {
                Sexo = "Hombre";
            } else if (Sexo === "M") {
                Sexo = "Mujer";
            }
            //añadimos las clases para que el label se vea arriba y además desactivamos la escritura en los campos
            $("#divMatriculaAlumnoIn").addClass('open');
            $("#txtMatriculaAlumno").val(Matricula).attr('disabled', true);
            $("#divNombreAlumnoIn").addClass('open');
            $("#txtNombreAlumno").val(Nombre).attr('disabled', true);
            $("#divApaternoAlumnoIn").addClass('open');
            $("#txtApaternoAlumno").val(APaterno).attr('disabled', true);
            $("#divAmaternoAlumnoIn").addClass('open');
            $("#txtAmaternoAlumno").val(AMaterno).attr('disabled', true);
            $("#divCurpAlumnoIn").addClass('open');
            $("#txtCurpAlumno").val(Curp).attr('disabled', true);
            $("#divGeneracionAlumnoIn").addClass('open');
            $("#txtGeneracionAlumno").val(Generacion).attr('disabled', true);
            $('#lstCarreraAlumno').find('option').remove().end().append("<option value = '0'>" + IDCarrera + " - " + Carrera + "</option>");
            $('#lstCarreraAlumno').attr('disabled', true).trigger("chosen:updated");
            $('#lstSexoAlumno').find('option').remove().end().append("<option value = '0'>" + Sexo + "</option>");
            $('#lstSexoAlumno').attr('disabled', true).trigger("chosen:updated");
            $("#txtCorreoAlumno").val(Correo).attr('disabled', true);
            $("#divCorreoAlumnoIn").addClass('open');
            //Reacomodamos la fecha de nacimiento, pues se recibe con cierto formato,
            // pero se necesita que este en cierto formato para darle valor a la etiqueta tipo 
            var ArrayFNac = FNacimiento.split('/');
            var arrayFInicio = FInicio.split('-');
            var arrayFFin = FFin.split('-');
            var fechaNueva = ArrayFNac[0] + "-" + ArrayFNac[1] + "-" + ArrayFNac[2];
            var fechaNuevaInicio = arrayFInicio[2] + "-" + arrayFInicio[1] + "-" + arrayFInicio[0];
            var fechaNuevaFin = arrayFFin[2] + "-" + arrayFFin[1] + "-" + arrayFFin[0];
            $("#txtFechaNac").val(fechaNueva).attr('disabled', true);
            $("#divFechaNacIn").addClass('open');
            $("#txtFechaInicioCarrera").val(fechaNuevaInicio).attr('disabled', true);
            $("#divFechaInicioCarreraIn").addClass('open');
            $("#txtFechaFinCarrera").val(fechaNuevaFin).attr('disabled', true);
            $("#divFechaFinCarreraIn").addClass('open');
        });

        //Modificar Alumnos individualmente
        $("#DivTblAlumnos").on("click", ".btnModificarAlumno", function () {
            //se cambiara el titulo del modal
            $("#modaltitle").html('Modificar Alumno');
            //Se repociciona el modal a su lugar original
            $("#modal-alumnos-draggable").removeAttr("style");
            $("#modal-alumnos").removeAttr('style');
            $('#modal-alumnos').modal('show');
            //se esconden los botones de agregar, modificar y limpiar;
            //se muestra el boton de cerrar
            $('#btnRegistrarAlumno').hide();
            $('#btnUpdateAlumno').show();
            $('#btnLimpiarAlumnoModal').hide();
            $('#btnCerrarAlumno').hide();

            //llenamos las listas, por si estan vacias
            $("#lstCarreraAlumno").html($("#lstCarreraAlumnoEstatico").html()).trigger("chosen:updated").change();
            $("#lstSexoAlumno").html($("#lstSexoAlumnoEstatico").html()).trigger("chosen:updated").change();

            //Se dividen los value del button para encontrar el numero de id del grupo
            var buttonval = this.value.split('_');

            //se recupera el texto de la tabla mediante los id
            var Matricula = $("#Matricula_" + buttonval[1]).text();
            var Nombre = $("#Nombre_" + buttonval[1]).text();
            var APaterno = $("#APaterno_" + buttonval[1]).text();
            var AMaterno = $("#AMaterno_" + buttonval[1]).text();
            var Curp = $("#Curp_" + buttonval[1]).text();
            var IDCarrera = $("#idCarrera_" + buttonval[1]).text();
            var Generacion = $("#Generacion_" + buttonval[1]).text();
            var Carrera = $("[name='Carrera_" + buttonval[1] + "']").text();
            var IDExcelCarrera = $('#idCarrera_' + buttonval[1]).attr('name');
            var Sexo = $("#Sexo_" + buttonval[1]).text();
            var FInicio = $("#FechaInicio_" + buttonval[1]).text();
            var FFin = $("#FechaFin_" + buttonval[1]).text();
            var Correo = $("#Correo_" + buttonval[1]).text();
            var FNacimiento = $("#FNacimiento_" + buttonval[1]).text();
            $('#txtIdGrupo').val(buttonval[1]);
            var ID_Alumno = $("#IdAlumno_" + buttonval[1]).text();
            //añadimos las clases para que el label se vea arriba y además desactivamos la escritura en los campos
            $("#ID_Alumno").val(ID_Alumno);
            $("#divMatriculaAlumnoIn").addClass('open');
            $("#txtMatriculaAlumno").val(Matricula).attr('disabled', false);
            $("#divNombreAlumnoIn").addClass('open');
            $("#txtNombreAlumno").val(Nombre).attr('disabled', false);
            $("#divApaternoAlumnoIn").addClass('open');
            $("#txtApaternoAlumno").val(APaterno).attr('disabled', false);
            $("#divAmaternoAlumnoIn").addClass('open');
            $("#txtAmaternoAlumno").val(AMaterno).attr('disabled', false);
            $("#divCurpAlumnoIn").addClass('open');
            $("#txtCurpAlumno").val(Curp).attr('disabled', false);
            $("#divGeneracionAlumnoIn").addClass('open');
            $("#txtGeneracionAlumno").val(Generacion).attr('disabled', false);
            //$("lstCarreraAlumno option[id='"+IDCarrera+"']").attr("selected", "selected");

            $("#ID_Cambio").val(IDExcelCarrera);
            $("#lstCarreraAlumno").val(IDExcelCarrera).trigger("chosen:updated").change();
            //$('#lstCarreraAlumno').find('option').remove().end().append("<option value = '0'>" + IDCarrera + " - " + Carrera + "</option>");
            $('#lstCarreraAlumno').attr('disabled', false).trigger("chosen:updated");
            var ArrayNombreCarrera = $("#lstCarreraAlumno :selected").text().split('-');
            $("#nombre_select_carrera").val(ArrayNombreCarrera[1]);
            //$('#lstSexoAlumno').find('option').remove().end().append("<option value = '0'>" + Sexo + "</option>");
            $("#lstSexoAlumno").val(Sexo).trigger("chosen:updated").change();
            $('#lstSexoAlumno').attr('disabled', false).trigger("chosen:updated");
            $("#txtCorreoAlumno").val(Correo).attr('disabled', false);
            $("#divCorreoAlumnoIn").addClass('open');
            //Reacomodamos la fecha de nacimiento, pues se recibe con cierto formato,
            // pero se necesita que este en cierto formato para darle valor a la rtiqueta tipo date
            var ArrayFNac = FNacimiento.split('/');
            var arrayFInicio = FInicio.split('-');
            var arrayFFin = FFin.split('-');
            var fechaNueva = ArrayFNac[0] + "-" + ArrayFNac[1] + "-" + ArrayFNac[2];
            var fechaNuevaInicio = arrayFInicio[2] + "-" + arrayFInicio[1] + "-" + arrayFInicio[0];
            var fechaNuevaFin = arrayFFin[2] + "-" + arrayFFin[1] + "-" + arrayFFin[0];
            $("#txtFechaNac").val(fechaNueva).attr('disabled', false);
            $("#divFechaNacIn").addClass('open');
            $("#txtFechaInicioCarrera").val(fechaNuevaInicio).attr('disabled', false);
            $("#divFechaInicioCarreraIn").addClass('open');
            $("#txtFechaFinCarrera").val(fechaNuevaFin).attr('disabled', false);
            $("#divFechaFinCarreraIn").addClass('open');
            //se que es redundate darle valor a la bandera, cuando hay un onclick en el boton
            //de btnUpdateAlumno en el jsp de Alumnos, donde se le da el mismo valor,
            //pero es necesario para que no haya confuciones en la funcion de $('#lstCarreraAlumno').on('change', function ()
            $('#bandera').val(4);

        });

    }

    $('#DivTblAlumnos').on('click', '.js-swal-confirm', function () {
        var buttonVal = $(this).val().split('_');
        var calif = $('#Calificaciones_' + buttonVal[1]).text();
        var idCarrera = $("#Carrera_" + buttonVal[1]).attr("id-carrera");
        tblAlumnos.$("[data-toggle='tooltip']").tooltip("hide");
        if (calif.includes('Alumno Calificado')) {
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: '¡Este alumno ya cuenta con calificaciones asignadas, éstas serán eliminadas de igual forma y los datos no podrán retornar! ¿Desea continuar?',
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
                    url: '../Transporte/queryCAlumnos.jsp',
                    data: '&txtBandera=2&idAlumno=' + buttonVal[1],
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Registro eliminado!', 'El alumno(a) seleccionado ha sido eliminado', 'success');
                            $(".tooltip").hide();
                            LoadTableEspecifico(idCarrera);
                        } else if (resp.includes('TituloActivo')) {
                            swal('¡Upps!', 'El alumno(a) seleccionado cuenta con titulos sin generar, no es posible eliminarlo', 'warning');
                        } else if (resp.includes('CertificadoActivo')) {
                            swal('¡Upps!', 'El alumno(a) seleccionado cuenta con certificados sin generar, no es posible eliminarlo', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function (jqXHR, textStatus) {
                        tblAlumnos.$("[data-toggle='tooltip']").tooltip("hide");
                    }
                });
            }, function () {
                tblAlumnos.$("[data-toggle='tooltip']").tooltip("hide");
            });
        } else {
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
                    url: '../Transporte/queryCAlumnos.jsp',
                    data: '&txtBandera=2&idAlumno=' + buttonVal[1],
                    type: 'POST',
                    success: function (resp) {
                        if (resp.includes('success')) {
                            swal('¡Registro eliminado!', 'El alumno(a) seleccionado ha sido eliminado', 'success');
                            $(".tooltip").hide();
                            LoadTableEspecifico(idCarrera);
                        } else if (resp.includes('TituloActivo')) {
                            swal('¡Upps!', 'El alumno(a) seleccionado cuenta con titulos sin generar, no es posible eliminarlo', 'warning');
                        } else if (resp.includes('CertificadoActivo')) {
                            swal('¡Upps!', 'El alumno(a) seleccionado cuenta con certificados sin generar, no es posible eliminarlo', 'warning');
                        } else {
                            show_swal("¡Upps!", resp.split('|')[1], "error");
                        }
                    }, complete: function (jqXHR, textStatus) {
                        tblAlumnos.$("[data-toggle='tooltip']").tooltip("hide");
                    }
                });
            }, function () {
                tblAlumnos.$("[data-toggle='tooltip']").tooltip("hide");
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
            if (window.innerWidth <= 371) {
                $("#btnImportarCarrerasExcel").css("margin-top", "15px");
            } else {
                $("#btnImportarCarrerasExcel").css("margin-top", "");
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
                    cargarListaCarreras();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        cargarListaCarreras();
                        //IMPORTAR REGISTROS
                        //verificamos que tenga permitido el rol ver ese boton, si no lo eliminamos
                        //normalmente la pocision 1 es para agregar/insertar
                        if (stringStepFirst.split("¬")[1].includes("0")) {
                            $("#btnAgregarAlumno").remove();
                        }
                        banderaPermiso = stringStepFirst.toString().trim().split("¬");
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

    function LoadTableEspecifico(e) {
        $("#tblAlumnos_filter input").val("").trigger("keyup");
        $.ajax({
            url: '../Transporte/queryCAlumnos.jsp',
            data: 'txtBandera=1&idCarrera=' + e,
            type: 'POST',
            success: function (resp) {
                var indicador = resp.split("|")[0];
                if (indicador.toString().trim() === "success") {
                    $('#DivTblAlumnos').html(resp.split("|")[1]);
                    $('[data-toggle="tooltip"]').tooltip();
                    TableActions();
                    initTable();
                    validatePermits();
                    setTimeout(function () {
                        $('#mainLoader').fadeOut();
                    });
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function (jqXHR, textStatus) {
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
                    $("#btnFiltrarAlumnos").attr("disabled", false);
                } else if (serverMsg.toString().trim() === 'empty') {
                    let lista = resp.split("¬")[1];
                    $("#lstCarreras").find("option").remove().end().append(lista).trigger("chosen:updated");
                    $("#btnFiltrarAlumnos").attr("disabled", true);
                } else {
                    show_swal("¡Upps!", resp.split('|')[1], "error");
                }
            }, complete: function (jqXHR, textStatus) {
                $('#mainLoader').fadeOut();
                $("#loadAction").fadeOut();
            }
        });
    }

    $("#btnFiltrarAlumnos").on("click", function () {
        let idMateria = $("#lstCarreras").val();
        if (idMateria.toString().trim() === 'todos') {
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'El proceso de carga va demorar en relación a la cantidad de alumnos registrados en el sistema',
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
                LoadTableEspecifico(idMateria);
            }, function () {

            });
        } else {
            $("#loadAction").fadeIn();
            LoadTableEspecifico(idMateria);
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


    //onclick para mostrar el modal para agregar nuevo alumno indiviudal
    $("#btnAgregarAlumno").click(function () {
        $('#bandera').val(3);
        $("#modaltitle").html("Nuevo Alumno");
        $("#btnUpdateAlumno").hide();
        $("#btnRegistrarAlumno").show();
        $("#btnLimpiarAlumnoModal").show();
        $("#btnCerrarAlumno").hide();
        limpiarCampos();
        $("#modal-alumnos").modal("show");

        $("#lstCarreraAlumno").attr('disabled', false);

        $("#lstSexoAlumno").attr('disabled', false);
        $("#txtMatriculaAlumno").attr('disabled', false);
        $("#txtNombreAlumno").attr('disabled', false);
        $("#txtApaternoAlumno").attr('disabled', false);
        $("#txtAmaternoAlumno").attr('disabled', false);
        $("#txtCurpAlumno").attr('disabled', false);
        $("#txtGeneracionAlumno").attr('disabled', false);
        $("#txtFechaInicioCarrera").attr('disabled', false);
        $("#txtFechaFinCarrera").attr('disabled', false);
        $("#txtCorreoAlumno").attr('disabled', false);
        $("#txtFechaNac").attr('disabled', false);


        $("#lstCarreraAlumno").html($("#lstCarreraAlumnoEstatico").html()).trigger("chosen:updated").change();
        $("#lstSexoAlumno").html($("#lstSexoAlumnoEstatico").html()).trigger("chosen:updated").change();




    });
    consultarCarreras();
    function consultarCarreras() {
        $("#loadAction").fadeIn();
        $.ajax({
            url: '../Transporte/queryCAlumnos.jsp',
            data: '&txtBandera=5',
            type: 'POST',
            success: function (resp) {


                //$("#lstCarreraAlumno").html(resp);
                $("#lstCarreraAlumnoEstatico").html(resp);

                if (resp.toString().trim().includes("exceptionTry")) {
                    var error = resp.split('|');
                    show_swal("¡Upps!", "Error al cargar lista de de Carreras: <h4 style='color:#e76d6d'><b>" + error[1] + "</b></h4><br><br><small>vuelve a cargar la página con F5, si continua con el problema, comuníquese con soporte técnico.</small>", "error");
                    return false;
                }
            }, complete: function () {
                $("#loadAction").fadeOut();
            }
        });
    }

    $('#btnCerrarModal').click(function () {
        $('#modal-alumnos').modal('hide');
        limpiarCampos();
    });


    //Validacion para verificar si es un correo electronico valido
      $.validator.addMethod('isEmail', function (value) {
                var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
                if (!regex.test(value)) {
                        return false;
                } else {
                        return true;
                }
        }, '¡Por favor ingrese un email válido!');

    //vaciar los campos del modal alumno individual
    function limpiarCampos() {
        $("#txtMatriculaAlumno").val("");
        $('#divMatriculaAlumnoIn').removeClass('open');
        $("#txtNombreAlumno").val("");
        $('#divNombreAlumnoIn').removeClass('open');

        $("#txtApaternoAlumno").val("");
        $('#divApaternoAlumnoIn').removeClass('open');
        $("#txtAmaternoAlumno").val("");
        $('#divAmaternoAlumnoIn').removeClass('open');
        $("#txtCurpAlumno").val("");
        $('#divCurpAlumnoIn').removeClass('open');
        $("#txtGeneracionAlumno").val("");
        $('#divGeneracionAlumnoIn').removeClass('open');
        $("#lstSexoAlumno").val("").trigger("chosen:updated");
        $("#lstCarreraAlumno").val("").trigger("chosen:updated");
        $("#txtFechaInicioCarrera").val("");
        $('#divFechaInicioCarreraIn').removeClass('open');
        $("#txtFechaFinCarrera").val("");
        $('#divFechaFinCarreraIn').removeClass('open');
        $("#txtCorreoAlumno").val("");
        $('#divCorreoAlumnoIn').removeClass('open');
        $("#txtFechaNac").val("");
        $('#divFechaNacIn').removeClass('open');
        $('.has-error').removeClass('has-error');
        $('.help-block').remove();
    }
    $(".selectOption").chosen({width: "100%", disable_search_threshold: 4});
    $(".JustNumbers").keydown(function (e) {
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13]) !== -1 ||
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

    $('#btnLimpiarAlumnoModal').click(function () {
        limpiarCampos();
    });

    $('.btnAccionesAlumnos').click(function () {
        $("form[name='formAlumnos']").submit(function (e) {
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
                elem.closest('.col-xs-5').removeClass('has-error').addClass('has-error');
                elem.closest('.col-xs-6').removeClass('has-error').addClass('has-error');
                elem.closest('.col-xs-7').removeClass('has-error').addClass('has-error');
                elem.closest('.col-xs-12').removeClass('has-error').addClass('has-error');
                elem.closest('.help-block').remove();
            },
            success: function (e) {
                var elem = jQuery(e);
                elem.closest('.col-xs-5').removeClass('has-error');
                elem.closest('.col-xs-6').removeClass('has-error');
                elem.closest('.col-xs-7').removeClass('has-error');
                elem.closest('.col-xs-12').removeClass('has-error');
                elem.closest('.help-block').remove();
            },
            rules: {
                'txtMatriculaAlumno': {
                    required: true
                },
                'txtNombreAlumno': {
                    required: true
                },
                'txtApaternoAlumno': {
                    required: true
                },
                'txtCurpAlumno': {
                    required: true
                },
                'txtGeneracionAlumno': {
                    required: true
                },
                'lstCarreraAlumno': {
                    required: true
                },
                'lstSexoAlumno': {
                    required: true
                },
                'txtFechaInicioCarrera': {
                    required: true
                },
                'txtFechaFinCarrera': {
                    required: true
                },
                'txtCorreoAlumno': {
                    required: true,
                    isEmail: true
                },
                'txtFechaNac': {
                    required: true
                }
            },
            messages: {
                'txtMatriculaAlumno': '¡Por favor completa este campo!',
                'txtNombreAlumno': '¡Por favor completa este campo!',
                'txtApaternoAlumno': '¡Por favor completa este campo!',
                'txtCurpAlumno': '¡Por favor completa este campo!',
                'txtGeneracionAlumno': '¡Por favor completa este campo!',
                'txtFechaInicioCarrera': '¡Por favor completa este campo!',
                'txtFechaFinCarrera': '¡Por favor completa este campo!',
                'txtCorreoAlumno': {
                    required: '¡Por favor completa este campo!',
                    email: 'Por favor ingresa un correo válido'
                },
                'lstCarreraAlumno': '¡Por favor selecciona una opción!',
                'txtFechaNac': '¡Por favor completa este campo!',
                'lstSexoAlumno': '¡Por favor selecciona una opción!'

            },
            submitHandler: function (form) {
                $('#loadAction').fadeIn();
                var bandera = document.getElementById('bandera').value;
                var txtMatriculaAlumno = $('#txtMatriculaAlumno').val();
                var txtNombreAlumno = $('#txtNombreAlumno').val();
                var txtApaternoAlumno = $('#txtApaternoAlumno').val();
                var txtAmaternoAlumno = $('#txtAmaternoAlumno').val();
                var txtCurpAlumno = $('#txtCurpAlumno').val().toUpperCase();
                var txtGeneracionAlumno = $('#txtGeneracionAlumno').val();
                var txtFechaInicioCarrera = $('#txtFechaInicioCarrera').val();
                var txtFechaFinCarrera = $('#txtFechaFinCarrera').val();
                var txtCorreoAlumno = $('#txtCorreoAlumno').val();
                var lstSexoAlumno = $('#lstSexoAlumno').val();
                var lstCarreraAlumno = $('#lstCarreraAlumno').val();
                var txtFechaNac = $('#txtFechaNac').val();
                var ID_Alumno = $('#ID_Alumno').val();
                
                //las fechas las convertimos a tipo date, con el motivo de poder compararlas
                var arrayFInicio = txtFechaInicioCarrera.split('-');
                var fInicio = new Date(arrayFInicio[2], arrayFInicio[1] - 1, arrayFInicio[0]);
                var arrayFFin = txtFechaFinCarrera.split('-');
                var fFin = new Date(arrayFFin[2], arrayFFin[1] - 1, arrayFFin[0]);
                var arrayFNac = txtFechaNac.split('-');
                var fNac = new Date(arrayFNac[2], arrayFNac[1] - 1, arrayFNac[0]);
                
                if (fInicio > fNac) {
                    if (fFin > fInicio) {
                        $.ajax({
                            url: '../Transporte/queryCAlumnos.jsp',
                            data: '&txtMatriculaAlumno=' + txtMatriculaAlumno
                                    + '&txtNombreAlumno=' + txtNombreAlumno
                                    + '&txtApaternoAlumno=' + txtApaternoAlumno
                                    + '&txtAmaternoAlumno=' + txtAmaternoAlumno
                                    + '&txtCurpAlumno=' + txtCurpAlumno
                                    + '&txtGeneracionAlumno=' + txtGeneracionAlumno
                                    + '&txtFechaInicioCarrera=' + txtFechaInicioCarrera
                                    + '&txtFechaFinCarrera=' + txtFechaFinCarrera
                                    + '&txtCorreoAlumno=' + txtCorreoAlumno
                                    + '&lstSexoAlumno=' + lstSexoAlumno
                                    + '&lstCarreraAlumno=' + lstCarreraAlumno
                                    + '&txtFechaNac=' + txtFechaNac
                                    + '&ID_Alumno=' + ID_Alumno
                                    + '&txtBandera=' + bandera,
                            type: 'POST',
                            success: function (resp) {
                                if (bandera === '3') {
                                    if (resp.toString().trim() === 'existe') {
                                        show_swal("¡Upps!", "El alumno ya existe.", "info");
                                        return false;
                                    }

                                    /* Apartado donde se manda la función agregar  con su determinado alert.*/
                                    if (resp.includes("success")) {
                                        show_swal("¡Agregado!", "El alumno se ha agregado correctamente", "success");
                                        $('#modal-alumnos').modal('hide');
                                        LoadTableEspecifico($('#lstCarreraAlumno option:selected').attr('id'));
                                        $('#lstCarreras').val($('#lstCarreraAlumno option:selected').attr('id')).trigger("chosen:updated").change();

                                        return false;
                                    } else if (resp.toString().trim().includes('exceptionTry')) {
                                        var error = resp.split('|');
                                        show_swal("¡Upps!", "Error al agregar alumno: <h4 style='color:#e76d6d'><b>" + error[1] + "</b></h4><br><br><small>vuelve a cargar la página con F5, si continua con el problema, comuníquese con soporte técnico.</small>", "error");
                                        return false;
                                    }
                                } else if (bandera === '4') {
                                    if (resp.toString().trim() === 'existe') {
                                        show_swal("¡Upps!", "La matricula del alumno y su carrera ya existe.", "info");
                                        return false;
                                    }
                                    /* Apartado donde se manda la función modificar con su determinado alert.*/
                                    if (resp.includes("success")) {
                                        $('#modal-alumnos').modal('hide');
                                        show_swal("¡Modificado!", "El registro del alumno se ha modificado correctamente", "success");
                                        LoadTableEspecifico($('#lstCarreraAlumno option:selected').attr('id'));
                                        $('#lstCarreras').val($('#lstCarreraAlumno option:selected').attr('id')).trigger("chosen:updated").change();

                                        return false;
                                    } else if (resp.toString().trim().includes('exceptionTry')) {
                                        var error = resp.split('|');
                                        show_swal("¡Upps!", "Error al modificar alumno: <h4 style='color:#e76d6d'><b>" + error[1] + "</b></h4><br><br><small>vuelve a cargar la página con F5, si continua con el problema, comuníquese con soporte técnico.</small>", "error");
                                        return false;
                                    }
                                }
                            }, complete: function () {
                                $('#loadAction').fadeOut();
                            }
                        });
                        return false;
                    } else {
                        $('#loadAction').fadeOut();
                        show_swal("¡Upps!", "Verifique la fecha inicio y fin de la carrera.", "info");
                    }
                } else {
                    $('#loadAction').fadeOut();
                    show_swal("¡Upps!", "Verifique la fecha de nacimiento.", "info");
                }
            }
        });
    });


    //Al cambiar la carrera del alumno, cuando se modifique, se consultara
    //si tiene calificaciones dentro de la carrera
    $('#lstCarreraAlumno').on('change', function () {
        //los inputs bandera, id_alumno y nombre_select_carrera se les da valor en table actions a la hora de hacer click
        //en los botones con clase btnModificarAlumno.
        var bandera = $('#bandera').val();
        var ID_Alumno = $('#ID_Alumno').val();
        var ID_Carrera = $('#ID_Cambio').val();

        //se verifica que se este en modificar alumno
        if (bandera === '4') {
            $("#loadAction").fadeIn();
            $.ajax({
                url: '../Transporte/queryCAlumnos.jsp',
                data: '&txtBandera=6'
                        + '&ID_Alumno=' + ID_Alumno
                        + '&ID_Carrera=' + ID_Carrera,
                type: 'POST',
                success: function (resp) {
                    //si existe calificaciones, se verifica que id_cambio sea diferente a lo de la lista
                    //esto con el motivo de que al iniciar el modal no muestre el modal,
                    //además que no haya problemas(cuando es el misma carrera entre alumnos y al iniciar el modal, 
                    //puede que se se muestre la alerta, por ende esta validacion es necesaria para que el ususario no tenga complicaciones)
                    //al checar las demás calificaciones de los alumnos.
                    if (resp.toString().trim() === 'existe') {
                        var id_cambio = $("#ID_Cambio").val();

                        if (id_cambio !== $("#lstCarreraAlumno").val()) {
                            swal({
                                type: 'warning',
                                title: '¿Estás seguro?',
                                text: 'El alumno a modificar cuenta con calificaciones en la carrera: ' + $("#nombre_select_carrera").val(),
                                confirmButtonText: 'Sí, ¡Permitir!',
                                showCancelButton: true,
                                cancelButtonText: 'Cancelar',
                                cancelButtonColor: '#d33',
                                showLoaderOnConfirm: true,
                                animation: true,
                                allowOutsideClick: false,
                                allowEscapeKey: false
                            }).then(function () {
                                //si quiere cambiar la carrera, solo se borrara los value de los inputs bandera y id cambio para
                                //que no le vuelva mostrar esta alerta de cambio de carrera
                                $("#ID_Cambio").val("");
                                $('#bandera').val("");
                            }, function () {
                                //si NO quiere cambiar la carrera, se pondra la carrera que tenia antes,

                                $("#lstCarreraAlumno").val(id_cambio).trigger("chosen:updated").change();
                            });
                        }
                        return false;
                    }

                    if (resp.toString().trim().includes("exceptionTry")) {
                        var error = resp.split('|');
                        show_swal("¡Upps!", "Error al cargar lista de de Carreras: <h4 style='color:#e76d6d'><b>" + error[1] + "</b></h4><br><br><small>vuelve a cargar la página con F5, si continua con el problema, comuníquese con soporte técnico.</small>", "error");

                        return false;
                    }
                }, complete: function () {
                    $("#loadAction").fadeOut();
                }
            });
        }

    });

});