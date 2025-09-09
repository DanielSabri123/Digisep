$(document).ready(function () {
    $("#lst-carrera-alumnos-servicio-click-escolar").chosen({width: "100%", disable_search_threshold: 4});

    var tblAlumnosServicioClickEscolar;
    var tblAlumnosCarreraServicioClickEscolar;
    function initTable(tipo) {
        switch(tipo){
            case 'individual':
                tblAlumnosServicioClickEscolar = $('.tabla-alumnos-servicio-click-escolar').DataTable({
                    order: [],
                    responsive: true,
                    scrollX: true,
                    ordering: true
                });
                break;
            case 'porCarrera':
                tblAlumnosServicioClickEscolar = $('.tabla-alumnos-por-carrera-servicio-click-escolar').DataTable({
                    order: [],
                    responsive: true,
                    scrollX: true,
                    ordering: true
                });
        }
    }

    $("#btn-modal-descargar-alumnos-click").on("click", function(){
        $.ajax({
            url: '../Transporte/queryCCarreras.jsp',
            data: '&txtBandera=3' ,
            type: 'POST',
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            }, success: function (resp) {
                
                let split_resp = resp.toString().trim().split("¬");
                $("#btn-confirmar-alumnos-servicio-click-escolar").hide();
                $("#btn-descargar-archivo-incidencias-alumno-carrera").hide();
                
                if(split_resp[0] === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    return;
                }
                if(split_resp[0] === "empty"){
                    $("#lst-carrera-alumnos-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated");
                    $("#modal-servicio-click-escolar-alumnos").modal("show");
                    return;
                }
                if(split_resp[0] === "success"){
                    $("#lst-carrera-alumnos-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated"); 
                    $("#lst-carrera-alumnos-servicio-click-escolar").find('option').first().remove().end().trigger("chosen:updated");
                    $("#lst-carrera-alumnos-servicio-click-escolar").val("").trigger("chosen:updated");
                    $("#modal-servicio-click-escolar-alumnos").modal("show");
                    return;
                }
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });        
    });
    
    $("#opcion-busqueda-servicio-click-escolar").on("change", function(){
        let tipoBusqueda = $(this).val();
        if(tipoBusqueda === "individual"){
            if ($("#div-previsualizar-alumnos-click-escolar").find(".tabla-alumnos-servicio-click-escolar").length > 0) {
                $("#btn-confirmar-alumnos-servicio-click-escolar").show();
            } else {
                $("#btn-confirmar-alumnos-servicio-click-escolar").hide();
            }
        }else{
            if ($("#div-previsualizar-carrera-alumnos-click-escolar").find(".tabla-alumnos-por-carrera-servicio-click-escolar").length > 0) {
                $("#btn-confirmar-alumnos-servicio-click-escolar").show();
            } else {
                $("#btn-confirmar-alumnos-servicio-click-escolar").hide();
            }
        }
    });
    
    $("#btn-consultar-alumnos-servicio-click-escolar").on("click", function(){
        let idCarrera;
        let matricula;
        let tipoBusqueda = $("#opcion-busqueda-servicio-click-escolar").val();
        
        if(tipoBusqueda === "individual"){
            matricula= $("#txt-matricula-servicio-click-escolar").val().trim();
            idCarrera = "";
            if(matricula === ""){
                mostrar_banda("Por favor, ingresa una matrícula para consultar el alumno", "info");
                return;
            }
            $("#div-previsualizar-alumnos-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar los alumnos disponibles.</h5></div>");
        }else{
            idCarrera = $("#lst-carrera-alumnos-servicio-click-escolar").val();
            matricula = "";
            if(idCarrera === "" || idCarrera === null){
                mostrar_banda("Por favor, seleccionar una carrera para consultar los alumno", "info");
                return;
            }
            $("#div-previsualizar-carrera-alumnos-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar los alumnos disponibles.</h5></div>");
        }
        
        $.ajax({
            url: '../Transporte/queryCServiceClickEscolar.jsp',
            data: '&txtBandera=descargarAlumnosClickEscolar'
                + '&idCarrera=' + idCarrera
                + '&matricula=' + matricula
                + '&tipoBusqueda=' + tipoBusqueda,
            type: 'POST',
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            }, success: function (resp) {
                let split_resp = resp.toString().trim().split("|");
                $("#btn-confirmar-carrera-servicio-click-escolar").hide();
                
                if(split_resp[0] === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    $("#btn-confirmar-alumnos-servicio-click-escolar").hide();
                    return;
                }
                if(split_resp[0] === "info"){
                    show_swal("¡Upps!", split_resp[1], "info");
                    $("#btn-confirmar-alumnos-servicio-click-escolar").hide();
                    return;
                }
                
                if(split_resp[0] === "success"){
                    mostrar_banda("Por favor, verifica la información consultada y posteriormente da click en Confirmar para guardar la información.", "info");
                    if(tipoBusqueda === "individual"){
                        $("#div-previsualizar-alumnos-click-escolar").html(split_resp[1]);
                        initTable('individual');
                    }else{
                        $("#div-previsualizar-carrera-alumnos-click-escolar").html(split_resp[1]);
                        initTable('porCarrera');
                    }
                    $("#btn-confirmar-alumnos-servicio-click-escolar").show();
                    return;
                }
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });
    });
    
    // Función para obtener todas las filas como objetos, incluidos los datos no visibles (sin importar la paginación)
    function obtenerFilasComoObjetosDataTable(tablaClass) {
        const filas = [];
        const tabla = $('.' + tablaClass).DataTable();  // Obtener la instancia del DataTable

        // Recorrer todas las filas de la tabla, sin importar si están visibles o no
        tabla.rows({ search: 'applied' }).every(function () {
            const data = this.data();  // Obtener los datos de la fila

            // Crear el objeto para la fila con los datos correspondientes
            const fila = {};

            // Mapear los datos según los identificadores de las celdas
            data.forEach((valor, index) => {
                switch (index) {
                    case 0: fila.Id_Alumno = parseInt(valor); break; // Primer columna: Id_Alumno
                    case 1: fila.APaterno = valor; break;             // Segunda columna: APaterno
                    case 2: fila.AMaterno = valor; break;             // Tercera columna: AMaterno
                    case 3: fila.Nombre = valor; break;               // Cuarta columna: Nombre
                    case 4: fila.Curp = valor; break;                 // Quinta columna: Curp
                    case 5: fila.FechaNacimiento = valor; break;      // Sexta columna: FechaNacimiento
                    case 6: fila.Sexo = valor; break;                 // Séptima columna: Sexo
                    case 7: fila.Email = valor; break;                // Octava columna: Email
                    case 8: fila.MatriculaExt = valor; break;         // Décima columna: MatriculaExt
                    case 9: fila.Id_Carrera_Digisep = parseInt(valor); break; // Undécima columna: Id_Carrera_Digisep
                    case 10: fila.FechaInicioCarrera = valor; break;  // Duodécima columna: FechaInicioCarrera
                    case 11: fila.FechaFinCarrera = valor; break;     // Décima tercera columna: FechaFinCarrera
                    case 12: fila.Generacion = valor; break;          // Décima cuarta columna: Generacion
                    case 13: fila.Pais = valor; break;                // Décima quinta columna: Pais
                    case 14: fila.Estado = valor; break;              // Décima sexta columna: Estado
                    case 15: fila.FechaInicioAntecedente = valor; break;
                    case 16: fila.FechaFinAntecedente = valor; break;
                    case 17: fila.TipoEstudioAntecedente = valor; break;
                    case 18: fila.EstadoAntecedente = valor; break;
                    case 19: fila.EscuelaOrigen = valor; break;
                    default: break; // Si alguna columna no coincide con la estructura esperada, se ignora
                }
            });

            // Solo agregar la fila si tiene valores asignados
            if (Object.keys(fila).length > 0) {
                filas.push(fila);
            }
        });

        return filas;
    }

    // Función para enviar las filas en lotes
    function enviarFilas(filas, tamanoLote = 100) {
        $.ajax({
            url: '../Transporte/queryCServiceClickEscolar.jsp',
            method: 'POST',
            data: {
                txtBandera: 'guardarAlumnosClickEscolar',
                json: JSON.stringify(filas)
            },
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            },
            success: function (resp) {
                let split_resp = resp.toString().trim().split("|");
                //$("#btn-confirmar-carrera-servicio-click-escolar").hide();
                
                if(split_resp[0].toString().trim() === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    return;
                }
                if(split_resp[0].toString().trim() === "errorConArchivo"){
                    limpiarCampos();
                    $("#modal-servicio-click-escolar-alumnos").modal("hide");
                    show_swal("¡Proceso con incidencias!", split_resp[1], "info");
                    window.open("https://digisep.com/" + split_resp[2], "_blank");
                    return;
                }
                
                if(split_resp[0].toString().trim() === "success"){
                    show_swal("¡Proceso Completado!", split_resp[1], "success");
                    limpiarCampos();
                    $("#modal-servicio-click-escolar-alumnos").modal("hide");
                }
            },
            error: function (err) {
                show_swal("¡Ocurrió un error!", "Error interno del servidor", "error");
            },
            complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });
    }


    // Al hacer clic en el botón de confirmación, obtener las filas y enviarlas por lotes
    $('#btn-confirmar-alumnos-servicio-click-escolar').on('click', function () {
        let filas;
        let tipo = $("#opcion-busqueda-servicio-click-escolar").val();
        if(tipo === "individual"){
            filas = obtenerFilasComoObjetosDataTable('tabla-alumnos-servicio-click-escolar');
        }else{
            filas = obtenerFilasComoObjetosDataTable('tabla-alumnos-por-carrera-servicio-click-escolar');
        }
        
        if (filas.length === 0) {
            show_swal("¡Sin información!", "No se encontró información en la tabla", "info")
        } else {
            enviarFilas(filas);
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
    
    function mostrar_banda(mensaje, tipo) {
        let bg_color;
        let border_color;
        let text_color;
        let icon;

        // Definición según tipo
        if (tipo === "info") {
            bg_color = "#d1ecf1";
            border_color = "#bee5eb";
            text_color = "#0c5460";
            icon = "fa fa-info-circle";
        } else if (tipo === "error") {
            bg_color = "#f8d7da";
            border_color = "#f5c6cb";
            text_color = "#721c24";
            icon = "fa fa-exclamation-triangle";
        } else if (tipo === "success") {
            bg_color = "#d4edda";
            border_color = "#c3e6cb";
            text_color = "#155724";
            icon = "fa fa-check-circle";
        } else {
            // valores por defecto si el tipo no es válido
            bg_color = "#ffffff";
            border_color = "#cccccc";
            text_color = "#333333";
            icon = "fa fa-info-circle";
        }

        $("#banda-confirmacion-error").html(`
            <div style="
                background-color: ${bg_color};
                color: ${text_color};
                padding: 15px 20px;
                margin: 10px 0;
                border: 1px solid ${border_color};
                border-radius: 5px;
                font-weight: bold;
                display: flex;
                align-items: center;
                gap: 10px;
                box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            ">
                <i class="${icon}" style="font-size: 20px;"></i>
                ${mensaje}
            </div>
        `).slideDown(500);

        setTimeout(() => {
            $("#banda-confirmacion-error").slideUp(700);
            setTimeout(() => {
                $("#banda-confirmacion-error").html("");
            }, 1000);
        }, 6000);
    }
    
    $('#modal-servicio-click-escolar-alumnos').on('hidden.bs.modal', function () {
        limpiarCampos();
    });

    function limpiarCampos(){
        $("#txt-matricula-servicio-click-escolar").val("");
        $("#lst-carrera-alumnos-servicio-click-escolar").val("").trigger("chosen:updated");
        $("#div-previsualizar-alumnos-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar los alumnos disponibles.</h5></div>");
        $("#div-previsualizar-carrera-alumnos-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar los alumnos disponibles.</h5></div>");
        $("#btn-descargar-archivo-incidencias-alumno-carrera").hide();
    }

});