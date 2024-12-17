/**
 * @author: Ricardo Reyna
 * @description: Validaciones para restaurar un titulo eliminado
 * @date_change: 23/03/2023
 */

$(document).ready(function () {
    var tblTitulos;
    function initTable() {
        tblTitulos = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            orderable: false,
            paging: true,
            searching: true,
            info: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false, targets: [8]}, {orderable: false, targets: 7, width: "5%"}, {width: "15%", targets: 8}],
            pageLength: 20,
            lengthMenu: [[5, 10, 15, 20, 50], [5, 10, 15, 20, 50]]
        });

        tblTitulos.$('[data-toggle="tooltip"]').tooltip({
            container: 'body',
            animation: false,
            trigger: 'hover'
        });
        $("#tblTitulos_filter input").val("").trigger("keyup");
        if (window.innerWidth <= 545 && window.innerWidth >= 335) {
            $(".js-dataTable-full-pagination-Fixed").find("th:nth-child(10),td:nth-child(10)").show();
        } else if (window.innerWidth <= 334) {
            $(".js-dataTable-full-pagination-Fixed").find("th:nth-child(10),td:nth-child(10)").hide();
        } else {
            $(".js-dataTable-full-pagination-Fixed").find("th:nth-child(10),td:nth-child(10)").show();
        }
    }



    $("#lstCarreras").chosen({width: "100%", disable_search_threshold: 4});
    cargaInicio();
    function cargaInicio() {
        $.ajax({
            url: '../Transporte/queryCTitulosEliminados.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var lstCarreras = resp;

                if (lstCarreras.split("|")[0].toString().trim().includes("error")) {
                    show_swal("¡Upps!", lstCarreras.split('|')[1], "error");
                    return;
                }
                var data = "";
                $('#lstCarreras').html(lstCarreras.split("|")[1]);
                $("#lstCarreras").val("todos").trigger("chosen:updated");
                initTable();

                setTimeout(function () {
                    $('#mainLoader').fadeOut();
                });
            }, complete: function (jqXHR, textStatus) {
                $('#loadAction').fadeOut();
                $('#mainLoader').fadeOut();
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
            $("#main")
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

    function cargarTabla(idCarrera) {

        $("#loadAction").fadeIn();
        $.ajax({
            url: '../Transporte/queryCTitulosEliminados.jsp',
            data: '&txtBandera=2&idCarrera=' + idCarrera,
            type: 'POST',
            success: function (resp) {

                if (resp.includes('success')) {
                    $("#divTblTitulos").html(resp.split("|")[1]);
                    eventosTabla();

                }

            }, complete: function () {
                $("#loadAction").fadeOut();
                $(".li-buttons").attr("disabled", true);
                initTable();
            }
        });
    }

    const eventosTabla = () => {
        $(".span_clickReactivar").on('click', function () {
            $('#loadAction').fadeIn();
            let idTitulo = $(this).attr("data-idTitulo");

            $.ajax({
                url: '../Transporte/queryCTitulosEliminados.jsp',
                data: '&txtBandera=3&idTitulo=' + idTitulo,
                type: 'POST',
                success: function (resp) {

                    resp = resp.trim();
                    let respuesta = resp.split("|")[0];
                    respuesta === "success" ? show_swal("¡Titulo restaurado correctamente!", resp.split("|")[1], "success") : show_swal("¡Upss!", resp.split("|")[1], "error");
                    let idCarrera = $("#lstCarreras").val();
                    cargarTabla(idCarrera);

                }, complete: function (jqXHR, textStatus) {
                    $('#loadAction').fadeOut();
                }
            });
        });
    };



    $("#btnFiltrarTitulos").on("click", function () {
        let idCarrera = $("#lstCarreras").val();
        if (idCarrera.toString().trim() === 'todos') {
            swal({
                type: 'warning',
                title: '¿Estás seguro?',
                text: 'El proceso de carga va demorar en relación a la cantidad de titulos registrados en el sistema',
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
});