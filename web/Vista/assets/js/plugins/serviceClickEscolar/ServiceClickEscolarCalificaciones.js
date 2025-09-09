$(document).ready(function () {
    $("#lst-carrera-servicio-click-escolar").chosen({width: "100%", disable_search_threshold: 4});
    $("#lst-alumno-servicio-click-escolar").chosen({width: "100%", disable_search_threshold: 4});
    
    var tblCalificacionesServicioClickEscolar;

    function initTable() {
        tblCalificacionesServicioClickEscolar = $('.tabla-calificaciones-servicio-click-escolar').DataTable({
            order: [],
            responsive: true,
            scrollX: true,
            ordering: true
        });
    }
    
    $("#btn-descargar-calificaciones-click").on("click", function(){
        $.ajax({
            url: '../Transporte/queryCCarreras.jsp',
            data: '&txtBandera=3' ,
            type: 'POST',
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            }, success: function (resp) {
                
                let split_resp = resp.toString().trim().split("¬");
                $("#btn-confirmar-calificaciones-servicio-click-escolar").hide();
                $("#btn-descargar-archivo-incidencias-calificaciones").hide();
                
                if(split_resp[0] === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    return;
                }
                if(split_resp[0] === "empty"){
                    $("#lst-carrera-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated");
                    $("#modal-servicio-click-escolar-calificaciones").modal("show");
                    return;
                }
                if(split_resp[0] === "success"){
                    $("#lst-carrera-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated"); 
                    $("#lst-carrera-servicio-click-escolar").find('option').first().remove().end().trigger("chosen:updated");
                    $("#lst-carrera-servicio-click-escolar").val("").attr("disabled", false).trigger("chosen:updated");
                    $("#modal-servicio-click-escolar-calificaciones").modal("show");
                    return;
                }
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });        
    });
    
    $("#lst-carrera-servicio-click-escolar").on("change", function(){
        let idCarrera = $(this).val();
        
        $.ajax({
            url: '../Transporte/queryCCarreras.jsp',
            data: '&txtBandera=consultarListaAlumnos'
                + '&idCarrera=' + idCarrera,
            type: 'POST',
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            }, success: function (resp) {
                
                let split_resp = resp.toString().trim().split("|");

                if(split_resp[0] === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    return;
                }
                if(split_resp[0] === "empty"){
                    $("#lst-alumno-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated");
                    return;
                }
                
                if(split_resp[0] === "success"){
                    $("#lst-alumno-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated"); 
                    $("#lst-alumno-servicio-click-escolar").val("").attr("disabled", false).trigger("chosen:updated");
                    return;
                }
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });  
    });
    
    $("#btn-consultar-calificaciones-servicio-click-escolar").on("click", function(){
        let idCarrera = $("#lst-carrera-servicio-click-escolar").val();
        let idAlumno = $("#lst-alumno-servicio-click-escolar").val();
        let matriculaAlumno = $("#lst-alumno-servicio-click-escolar option:selected").data('matricula');;
        
        if(idCarrera === "" || idCarrera === null){
            mostrar_banda("Por favor, selecciona una carrera", "info");
            $("#div-previsualizar-alumnos-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar las calificaciones disponibles.</h5></div>");
            $("#btn-confirmar-calificaciones-servicio-click-escolar").hide();
            return;
        }
        
        if(idAlumno === null || idAlumno === undefined){
            idAlumno = "";
        }
        if(matriculaAlumno === null || matriculaAlumno === undefined){
            matriculaAlumno = "";
        }
        
        $.ajax({
            url: '../Transporte/queryCServiceClickEscolar.jsp',
            data: '&txtBandera=descargarCalificacionesClickEscolar'
                + '&idCarrera=' + idCarrera
                + '&matricula=' + matriculaAlumno
                + '&idAlumno=' + idAlumno,
            type: 'POST',
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            }, success: function (resp) {
                let split_resp = resp.toString().trim().split("|");
                $("#btn-confirmar-calificaciones-servicio-click-escolar").hide();
                
                if(split_resp[0] === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    $("#btn-confirmar-alumnos-servicio-click-escolar").hide();
                    $("#div-previsualizar-calificaciones-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar los alumnos disponibles.</h5></div>");
                    return;
                }
                if(split_resp[0] === "info"){
                    show_swal("¡Upps!", split_resp[1], "info");
                    $("#btn-confirmar-alumnos-servicio-click-escolar").hide();
                    $("#div-previsualizar-calificaciones-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar los alumnos disponibles.</h5></div>");
                    return;
                }
                
                if(split_resp[0] === "success"){
                    mostrar_banda("Por favor, verifica la información consultada y posteriormente da click en Confirmar para guardar la información.", "info");
                    $("#div-previsualizar-calificaciones-click-escolar").html(split_resp[1]);
                    $("#btn-confirmar-calificaciones-servicio-click-escolar").show();
                    initTable();
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
                    case 0: fila.CveMateria = valor; break; 
                    case 1: fila.NombreMateria = valor; break;            
                    case 2: fila.IdObservacion = valor; break;           
                    case 3: fila.Matricula = valor; break;               
                    case 4: fila.NombreAlumno = valor; break;                
                    case 5: fila.CicloEscolar = valor; break;      
                    case 6: fila.IdCarrera = valor; break;                 
                    case 7: fila.Calificacion = valor; break;                
                    case 8: fila.FolioOrdenamiento = valor; break;        
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
    
    function enviarFilas(filas) {
        $.ajax({
            url: '../Transporte/queryCServiceClickEscolar.jsp',
            method: 'POST',
            data: {
                txtBandera: 'guardarCalificacionesClickEscolar',
                json: JSON.stringify(filas)
            },
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            },
            success: function (resp) {
                let split_resp = resp.toString().trim().split("|");
                
                if(split_resp[0].toString().trim() === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    return;
                }
                if(split_resp[0].toString().trim() === "errorConArchivo"){
                    $("#modal-servicio-click-escolar-calificaciones").modal("hide");
                    limpiarCampos();
                    show_swal("¡Proceso con incidencias!", split_resp[1], "info");
                    window.open("https://digisep.com/" + split_resp[2], "_blank");
                    return;
                }
                
                if(split_resp[0] === "success"){
                    show_swal("¡Proceso Completado!", split_resp[1], "success");
                    limpiarCampos();
                    $("#modal-servicio-click-escolar-calificaciones").modal("hide");
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
    
    $("#btn-confirmar-calificaciones-servicio-click-escolar").on("click", function(){
        let filas;
        filas = obtenerFilasComoObjetosDataTable('tabla-calificaciones-servicio-click-escolar');
        
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
                margin: 0 0 10px 0;
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
    
    $('#modal-servicio-click-escolar-calificaciones').on('hidden.bs.modal', function () {
        limpiarCampos();
    });

    function limpiarCampos(){
        $("#lst-carrera-servicio-click-escolar").val("").trigger("chosen:updated");
        $("#lst-alumno-servicio-click-escolar").val("").trigger("chosen:updated");
        $("#div-previsualizar-calificaciones-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar los alumnos disponibles.</h5></div>");
    }
    
});
