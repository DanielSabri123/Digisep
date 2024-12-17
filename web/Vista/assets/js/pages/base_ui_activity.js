/*
 *  Document   : base_ui_activity.js
 *  Author     : pixelcave
 *  Description: Custom JS code used in Activity Page
 */

var BaseUIActivity = function() {
    // Randomize progress bars values
    var barsRandomize = function(){
        jQuery('.js-bar-randomize').on('click', function(){
            jQuery(this)
                .parents('.block')
                .find('.progress-bar')
                .each(function() {
                    var $this   = jQuery(this);
                    var $random = Math.floor((Math.random() * 91) + 10)  + '%';

                    $this.css('width', $random);

                    if ( ! $this.parent().hasClass('progress-mini')) {
                        $this.html($random);
                    }
                });
            });
    };

    // SweetAlert, for more examples you can check out https://github.com/t4t5/sweetalert
    var sweetAlert = function(){
        // Init a simple alert on button click
        jQuery('.js-swal-alert').on('click', function(){
            swal('Hi, this is a simple alert!');
        });
        // Init an success alert on button click, only for update!
        jQuery('.js-swal-success-update').on('click', function(){
            swal('Modifcado', 'Se ha modificado correctamente!', 'success');
        });
        // Init an success alert on button click
        jQuery('.js-swal-success').on('click', function(){
            swal('Agregado', 'Se ha agreado correctamente!', 'success');
        });

        // Init an error alert on button click
        jQuery('.js-swal-error').on('click', function(){
            swal('Oops...', 'Something went wrong!', 'error');
        });

        // Init an example confirm alert on button click
        jQuery('.js-swal-confirm').on('click', function(){
            swal({
                title: '¿Estás seguro?',
                text: 'Al confirmar, los datos no podrán retornar facilmente',
                type: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d26a5c',
                confirmButtonText: 'Si, Eliminalo!',
                closeOnConfirm: false,
                html: false
            }, function () {
                swal('¡Eliminado!', 'La información ha sido eliminada', 'success');
            });
        });
    };

    return {
        init: function() {
            // Init randomize bar values
            barsRandomize();

            // Init SweetAlert
            sweetAlert();
        }
    };
}();

// Initialize when page loads
jQuery(function(){ BaseUIActivity.init(); });