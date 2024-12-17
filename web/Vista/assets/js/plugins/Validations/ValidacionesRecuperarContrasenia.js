/* 
 * JAVASCRIPT PARA RECUPERAR LA CONTRASEÑA
 
 Creador:  Ricardo Reyna 
 Document: JS para validacion de acciones de correo Institucional
 
 */
$(document).ready(function (e) {

    $("#myModal").modal("hide");
    $('#btnRecuperarContrasenia').click(function (event) {
        event.preventDefault();

        var email = $("#email").val();
        var carp = $("#txtCarpeta").val();
        if (verificarCorreo(email) === true) {

            $("#myModal").modal("show");
            $.ajax({
                url: '../Transporte/queryCRecuperarContrasenia.jsp',
                data: 'correo=' + email +
                        '&txtCarpeta=' + carp, //Se pone & para separar los datos que mandamos
                type: 'POST',
                success: function (data) {
                    let respuesta = data.trim(); //Se hace un trim(), ya quje el retorno de la respuesta manda datos separados, ya así quitamos todos los espacios

                    if (respuesta === '0') {
                        swal('Contraseña establecida correctamente', 'Se ha enviado un correo con especificaciones para entrar a la plataforma ', 'success');
                        //Se regresa a la pagina principal en 3 segundos para que ingrese a la plataforma
                        setTimeout(function () {
                            window.location.href = "../../Vista/Generales/LogIn.jsp";
                        }, 3000);
                    } else if (respuesta === "1") {
                        swal('¡Upps!', 'No hemos encontrado el email ingresado', 'error');
                    } else {
                        swal('¡Upps!', 'No se ha podido enviar el correo', 'error');
                    }

                }, complete: function () {
                    $("#myModal").modal("hide");
                }

            });
        } else {
            console.log("no es correo");
        }


    });
    //Funcion para verificar si es un correo
    function verificarCorreo(email) {
        var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
        if (!regex.test(email)) {
            return false;
        } else {
            return true;
        }
    }

//MENSAJE PARA VALIDAR EL EMAIL
    jQuery.extend(jQuery.validator.messages, {
        email: "Por favor, escribe una dirección de correo válida"
    });
});



