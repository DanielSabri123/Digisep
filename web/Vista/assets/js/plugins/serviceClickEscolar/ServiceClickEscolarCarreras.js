$(document).ready(function () {
    $("#lst-carrera-servicio-click-escolar").chosen({width: "100%", disable_search_threshold: 4});
   
    var tblCarrerasServicioClickEscolar;
    function initTable() {
        tblCarrerasServicioClickEscolar = $('.tabla-carreras-materias-click-escolar').DataTable({
            order: [],
            responsive: true,
            scrollX: true,
            ordering: true
        });

    }
   
    $("#btn-modal-descargar-carreras-click").on("click", function(){
        $.ajax({
            url: '../Transporte/queryCCarreras.jsp',
            data: '&txtBandera=3' ,
            type: 'POST',
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            }, success: function (resp) {
                
                let split_resp = resp.toString().trim().split("¬");
                $("#btn-confirmar-carrera-servicio-click-escolar").hide();
                
                if(split_resp[0] === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    return;
                }
                if(split_resp[0] === "empty"){
                    $("#lst-carrera-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated");
                    return;
                }
                if(split_resp[0] === "success"){
                    $("#lst-carrera-servicio-click-escolar").html(split_resp[1]).trigger("chosen:updated"); 
                    $('#lst-carrera-servicio-click-escolar').find('option').first().remove().end().trigger("chosen:updated");
                    $("#modal-servicio-click-escolar-carreras").modal("show");
                    return;
                }
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });
    });
    
    $("#check-todos-servicio-click-escolar").on("change", function(){
        if (this.checked) {
            $("#lst-carrera-servicio-click-escolar").attr("disabled", true).trigger("chosen:updated");;
        } else {
            $("#lst-carrera-servicio-click-escolar").attr("disabled", false).trigger("chosen:updated");;
        }
    });
    
    $("#btn-consultar-carrera-servicio-click-escolar").on("click", function(){
        let idCarrera = $("#lst-carrera-servicio-click-escolar").val();
        let descargarTodo = $("#check-todos-servicio-click-escolar").prop("checked");
        
        $.ajax({
            url: '../Transporte/queryCServiceClickEscolar.jsp',
            data: '&txtBandera=descargarCarrerasClickEscolar'
                + '&idCarrera=' + idCarrera
                + '&descargarTodo=' + descargarTodo,
            type: 'POST',
            beforeSend: function(){
               $('#loadAction').fadeIn(); 
            }, success: function (resp) {
                let split_resp = resp.toString().trim().split("|");
                $("#btn-confirmar-carrera-servicio-click-escolar").hide();
                
                if(split_resp[0] === "error"){
                    show_swal("¡Ocurrió un error!", split_resp[1], "error");
                    return;
                }
                if(split_resp[0] === "info"){
                    show_swal("¡Upps!", split_resp[1], "info");
                    return;
                }
                
                if(split_resp[0] === "success"){
                    mostrar_banda("Por favor, verifica la información consultada y posteriormente da click en Confirmar para guardar la información.", "info");
                    $("#div-previsualizar-carrerar-click-escolar").html(split_resp[1]);
                    initTable();
                    $("#btn-confirmar-carrera-servicio-click-escolar").show();
                    return;
                }
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
            }
        });
    });
    
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
                    case 0: fila.Id_Carrera_Excel = parseInt(valor); break; // Primer columna: Id_Alumno
                    case 1: fila.CveCarrera = valor; break;             // Segunda columna: APaterno
                    case 2: fila.NombreCarrera = valor; break;             // Tercera columna: AMaterno
                    case 3: fila.CveInstitucion = valor; break;               // Cuarta columna: Nombre
                    case 4: fila.NombreInstitucion = valor; break;                 // Quinta columna: Curp
                    case 5: fila.CveCampus = valor; break;      // Sexta columna: FechaNacimiento
                    case 6: fila.Campus = valor; break;                 // Séptima columna: Sexo
                    case 7: fila.AutorizacionReconocimiento = valor; break;                // Octava columna: Email
                    case 9: fila.NumeroRvoe = valor; break;         // Décima columna: MatriculaExt
                    case 10: fila.TipoPeriodo = parseInt(valor); break; // Undécima columna: Id_Carrera_Digisep
                    case 12: fila.NivelEducativo = valor; break;  // Duodécima columna: FechaInicioCarrera
                    case 14: fila.TotalMaterias = valor; break;     // Décima tercera columna: FechaFinCarrera
                    case 15: fila.EntidadFederativa = valor; break;          // Décima cuarta columna: Generacion
                    case 17: fila.FechaExpedicionRvoe = valor; break;                // Décima quinta columna: Pais
                    case 18: fila.CalifMinima = valor; break;              // Décima sexta columna: Estado
                    case 19: fila.CalifMaxima = valor; break;              // Décima sexta columna: Estado
                    case 20: fila.CalifMinimaAprobatoria = valor; break;              // Décima sexta columna: Estado
                    case 21: fila.CvePlan = valor; break;              // Décima sexta columna: Estado
                    case 22: fila.Creditos = valor; break;              // Décima sexta columna: Estado
                    case 23: fila.Materias = JSON.parse(valor.replace(/'/g, '"')); break;
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
                txtBandera: 'guardarCarrerasClickEscolar',
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
                if(split_resp[0].toString().trim() === "info"){
                    show_swal("¡Upps!", split_resp[1], "info");
                    return;
                }
                
                if(split_resp[0].toString().trim() === "success"){
                    show_swal("¡Proceso Completado!", "La carrera se guardó exitosamente", "success");
                    limpiarCampos();
                    $("#modal-servicio-click-escolar-carreras").modal("hide");
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
    
    $('#btn-confirmar-carrera-servicio-click-escolar').on('click', function () {
        let filas;
        filas = obtenerFilasComoObjetosDataTable('tabla-carreras-materias-click-escolar');
        
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
                margin: 15px 0;
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
    
    $('#modal-servicio-click-escolar-carreras').on('hidden.bs.modal', function () {
        limpiarCampos();
    });

    function limpiarCampos(){
        $('#check-todos-servicio-click-escolar').prop('checked', false);
        $("#lst-carrera-servicio-click-escolar").val("").attr("disabled", false).trigger("chosen:updated");
        $("#div-previsualizar-carrerar-click-escolar").html("<div class='alert alert-info alert-dismissable text-center' style='padding: 20px;'><i class='fa fa-info-circle fa-4x'></i><h5 class='push-10-t'>Por favor, completa los filtros requeridos para consultar las carreras disponibles.</h5></div>");
        $("#btn-confirmar-carrera-servicio-click-escolar").hide();
    }
    
});

