$(document).ready(function () {
    
    $("#btn-probar-configuracion-conexion-click").on("click", function(e){
        let form = $('form[name=form-configurar-conexion-click]');
        form.removeData("validator");
        form.off("submit");
        
        $('form[name=form-configurar-conexion-click]').submit(function (e) {
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
                "clave-institucion-click": {
                    required: true
                },
                "usuario-click": {
                    required: true
                },
                "contrasena-click": {
                    required: true
                }
            }, messages: {
                "clave-institucion-click": {
                    required: '¡Por favor ingresa la clave de tu institución!'
                },
                "usuario-click": {
                    required: '¡Por favor ingresa el usuario!'
                },
                "contrasena-click": {
                    required: '¡Por favor ingresa la constraseña del usuario!'
                }
            },
            submitHandler: function (form) {
                let clave = $("#clave-institucion-click").val();
                let usuario = $("#usuario-click").val();
                let contrasena = $("#contrasena-click").val();

                $.ajax({
                    url: '../Transporte/queryCServiceClickEscolar.jsp',
                    data: '&txtBandera=realizarTestConexion'
                            + '&clave=' + clave
                            + '&usuario=' + usuario
                            + '&contrasena=' + contrasena,
                    type: 'GET',
                    beforeSend: function(){
                       $('#loadAction').fadeIn(); 
                    }, success: function (resp) {

                        let resp_split = resp.toString().trim().split("|");

                        if(resp_split[0] == "error"){
                            show_swal("¡Ocurrió un error!", resp_split[1], "error");
                            return;
                        }

                        if(resp_split[0] == "success"){
                            $("#btn-guardar-configuracion-conexion-click").show();
                            $("#nombre-bd-click").val(resp_split[2]).trigger("change");
                            $("#banda-confirmacion-test").html(`
                                <div style="
                                    background-color: #d4edda;
                                    color: #155724;
                                    padding: 15px 20px;
                                    margin: 10px 0;
                                    border: 1px solid #c3e6cb;
                                    border-radius: 5px;
                                    font-weight: bold;
                                    display: flex;
                                    align-items: center;
                                    gap: 10px;
                                    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
                                ">
                                    <i class="fa fa-check-circle" style="font-size: 20px;"></i>
                                    La prueba de conexión se realizó correctamente.
                                </div>
                            `).slideDown(500);// más suave que "fast"

                            setTimeout(() => { 
                                $("#banda-confirmacion-test").slideUp(700); 
                                setTimeout(() => { $("#banda-confirmacion-test").html("");}, 1000);
                            }, 4000);
                            return;
                        }
                    }, error: function (e) {
                        swal('¡Error!', 'Error interno del servidor, contacte a soporte técnico', 'error');
                    }, complete: function () {
                        $('#loadAction').fadeOut();
                    }
                });
            }
        });
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
});