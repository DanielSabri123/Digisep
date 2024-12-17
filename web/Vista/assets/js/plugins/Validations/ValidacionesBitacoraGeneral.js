/**
 * @author: Braulio Sorcia
 * @description Js para las validaciones del lado del cliente de bitácora general
 * @since 07 de Mayo 2019
 * =====================================================
 * @author_change:
 * @description_change:
 * @date_change: 
 */
$('[data-toggle="tooltip"]').tooltip({
    trigger: 'hover'
});
$(document).ready(function () {
    $("#lstUsuarios").chosen({width: "100%", disable_search_threshold: 4});
    function LoadTable() {
        $.ajax({
            url: '../Transporte/queryCBitacoraGeneral.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (response) {
                var tbl = response.split("||")[0];
                var lista = response.split("||")[1];
                $('#DivTblBitacora').html(tbl);
                $("#lstUsuarios").find("option").remove().end().html(lista).trigger("chosen:updated");
                initTable();
                $("#tblBitacora_filter input").val("").trigger("keyup");
                setTimeout(function () {
                    $('#mainLoader').fadeOut();
                });
            }, error: function () {

            }
        });
    }
    var tblBitacoraGen;
    function initTable() {
        tblBitacoraGen = $('.js-dataTable-full-pagination-Fixed').dataTable({
            ordering: true,
            paging: true,
            searching: true,
            info: true,
            stateSave: true,
            pagingType: "full_numbers",
            columnDefs: [{orderable: false}],
            pageLength: 25,
            lengthMenu: [[15, 25, 35, 50], [15, 25, 35, 50]]
        });
    }

    $("#btnFiltrarBitacora").on('click', function () {
        var fechaDesde = $("#txtFechaDesde").val();
        var fechaHasta = $("#txtFechaHasta").val();
        var txtIdUsuario = $("#lstUsuarios").val();
        var txtBandera = "";
        if ((txtIdUsuario === '0' || txtIdUsuario === '' || txtIdUsuario === null) && (fechaDesde === "" || fechaHasta === "")) {
            swal('¡Lo sentimos!', '¡Debes llenar al menos uno de los filtros!', 'warning');
        } else {
            $('#loadAction').fadeIn();
            //ONLY DATES
            if ((txtIdUsuario === '0' || txtIdUsuario === '' || txtIdUsuario === null) && (fechaDesde !== "" && fechaHasta !== "")) {
                txtBandera = "1";
                //ONLY USERS
            } else if ((txtIdUsuario !== '0' || txtIdUsuario !== '' || txtIdUsuario !== null) && (fechaDesde === "" && fechaHasta === "")) {
                txtBandera = "2";
                //BOTH OF THEM
            } else if (txtIdUsuario !== '0' && (fechaDesde !== "" && fechaHasta !== "")) {
                txtBandera = "3";
            }

            $.ajax({
                url: '../Transporte/queryCBitacoraGeneral.jsp',
                data: 'txtBandera=2&txtFechaDesde=' + fechaDesde + '&txtFechaHasta=' + fechaHasta + "&txtIdUsuario=" + txtIdUsuario + '&txtTipoFiltro=' + txtBandera,
                type: 'POST',
                success: function (response) {
                    $('#DivTblBitacora').html(response);
                    initTable();
                    $('#loadAction').fadeOut();
                }, error: function () {

                }
            });
        }
    });

    $("#btnLimpiarFiltros").on('click', function () {
        $("#txtFechaDesde").val('').trigger('change');
        $("#txtFechaHasta").val('').trigger('change');
        $("#lstUsuarios").val('0').trigger('chosen:updated');
    });

    $("#btnLimpiarBitacora").on('click', function () {
        swal({
            type: 'warning',
            title: '¿Estás seguro?',
            text: 'Al confirmar, los datos serán eliminados permanentemente!',
            confirmButtonText: 'Sí, ¡Elimínalo!',
            showCancelButton: true,
            cancelButtonText: 'Cancelar',
            cancelButtonColor: '#d33',
            showLoaderOnConfirm: true,
            animation: true,
            allowOutsideClick: false,
            allowEscapeKey: false
        }).then(function () {
            $('#loadAction').fadeIn();
            $.ajax({
                url: '../Transporte/queryCBitacoraGeneral.jsp',
                data: '&txtBandera=3',
                type: 'POST',
                success: function (response) {
                    if (response.includes('success')) {
                        swal('¡Bitácora limpiada!', 'La bitácora fue limpiada', 'success');
                        LoadTable();
                    } else if (response.includes('error')) {
                        swal('¡Upps!', 'Ha ocurrido un error, la acción no fue realizada.', 'error');
                    }
                }, error: function () {
                    swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                }, complete: function (jqXHR, textStatus) {
                    $('#loadAction').fadeOut();
                }
            });
        }, function () {

        });
    })

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
                    if (stringStepFirst.split("¬")[0].includes("1")) {
                        LoadTable();
                        //Permiso de filtrar
                        if (stringStepFirst.split("¬")[5].includes("0")) {
                            $("#divFiltros").remove();
                        }
                        //Permiso de vaciar bitácora
                        if (stringStepFirst.split("¬")[6].includes("0")) {
                            $("#btnLimpiarBitacora").remove();
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


