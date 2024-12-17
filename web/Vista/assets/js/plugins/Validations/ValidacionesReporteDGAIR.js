/**
 * @author: Braulio Sorcia
 * @description Js para las validaciones del lado del cliente de reporte dgair
 * @since 17 DE FEBRERO 2023
 * =====================================================
 * @author_change:
 * @description_change:
 * @date_change: 
 */

$(document).ready(function () {
    $("#lst-tipo,#lst-carreras").chosen({width: "100%", disable_search_threshold: 4});
    $.ajaxSetup({
        url: '../Transporte/queryCReporteDGAIR.jsp',
        type: 'POST'
    });



    $("#btnFiltrarBitacora").on("click", function () {
        let date1 = $("#txtFechaDesde").val();
        let date2 = $("#txtFechaHasta").val();
        let showAverage = $("#promedio-general").is(":checked") ? 1 : 0;
        let showFolio = $("#folio-control").is(":checked") ? 1 : 0;
        let opt = $("#lst-tipo").val();
        let carrera = $("#lst-carreras").val();
        if (date1 == "" && date2 == "") {
            swal("Información incompleta", "Debes de seleccionar un intervalo de fechas.", "warning");
            return false;
        }
        $.ajax({
            beforeSend: function (xhr) {
                $("#loadAction").show();
                $.notify({
                    // options
                    message: '<div class="text-center">Tu archivo está generándose.<p>Espera unos momentos.</div>'
                }, {
                    // settings
                    type: 'info',
                    delay: 15000
                });
            },
            data: {
                fi: date1,
                ff: date2,
                opt: opt,
                promedio: showAverage,
                folio: showFolio,
                carrera: carrera,
                txtBandera: 2
            }, success: function (data, textStatus, jqXHR) {
                if (!data.includes("notAllowed") && !data.includes("empty") && !data.includes("error")) {
                    $.notify({
                        // options
                        message: '<div class="text-center">Archivo listo.</div>'
                    }, {
                        // settings
                        type: 'success',
                        delay: 15000
                    });
                    window.open(data);
                } else if (data.includes("empty")) {
                    swal("Sin información", "Lo sentimos, los filtros ingresados no regresan información alguna.", "warning");
                    $.notify({
                        // options
                        message: '<div class="text-center">Sin información.</div>'
                    }, {
                        // settings
                        type: 'warning',
                        delay: 15000
                    });
                } else if (data.includes("notAllowed")) {
                    swal("Sin permisos", "Lo sentimos, no cuentas con permisos para realizar esta acción. <p><b>Consulta a tu administrador</b></p><p><small>La página se recargará.</small></p>", "warning").then(action => {
                        location.reload();
                    }, onexit => {
                        location.reload();
                    });
                }
            }, error: function (jqXHR, textStatus, errorThrown) {
                $.notify({
                    // options
                    message: '<div class="text-center">Error el generar el archivo</div>'
                }, {
                    // settings
                    type: 'danger',
                    delay: 15000
                });
                console.log(jqXHR);
            }, complete: function (jqXHR, textStatus) {
                $("#loadAction").hide();
            }
        })
    });

    function loadList() {
        $.ajax({
            data: {
                txtBandera: 1
            }, success: function (data, textStatus, jqXHR) {
                $("#lst-carreras")
                        .find("option")
                        .remove()
                        .end()
                        .append(data)
                        .trigger("chosen:updated");
            }, error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error al procesar la carrera, refresca el sitio");
            }, complete: function (jqXHR, textStatus) {
                $("#mainLoader").hide();
            }
        })
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
                    loadList();
                } else if (resp[0].includes("acceso")) {
                    let stringStepFirst = resp[0].split("°")[1];
                    if (stringStepFirst != null && stringStepFirst.split("¬")[0].includes("1")) {
                        loadList();
                        if (stringStepFirst.split("¬")[1].includes("0")) {
                            $(".noPermisson").show();
                            $("#fullContent").html("");
                            $('#mainLoader').fadeOut();
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
})