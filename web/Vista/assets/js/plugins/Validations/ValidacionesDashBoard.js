/**
 * @author: Paola Alonso
 * @description Js para las validaciones del Dash-Board
 * @since 13 de Febrero del 2019
 */
$(document).ready(function () {

    LoadData();
    function LoadData() {
        $.ajax({
            url: '../Transporte/queryCBienvenida.jsp',
            data: '&txtBandera=1',
            type: 'POST',
            success: function (resp) {
                var split = resp.split('~');
                $('#divTimbresDisponibles').countTo({from: 0, to: split[0]});
                $('#divCertificadosUsados').countTo({from: 0, to: split[1]});
                $('#divTitulosUsados').countTo({from: 0, to: split[2]});
                if (split[3].includes('vencidos')) {
                    swal('¡Upps!', 'Los timbres disponibles ya no son vigentes, para corregir esta situación, contacte a soporte técnico', 'info');
                }
                var notifys = split[4];
                if (notifys !== "") {
                    eval(notifys);
                } else if (notifys.includes("errorNotify")) {
                    swal('¡Upps!', 'Ocurrió un error al generar las notificaciones de firmantes, consulte a soporte técnico.', 'error');
                }
                var mensajeVigencia = split[5];
                if (mensajeVigencia !== "") {
                    eval(mensajeVigencia);
                }

            },
            error: function () {
                swal('¡Upps!', 'Error interno del servidor, contacte a soporte técnico', 'error');
            }
        });
    }
});

