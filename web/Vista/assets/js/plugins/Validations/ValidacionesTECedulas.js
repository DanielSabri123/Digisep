/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {

    LoadData();
    function LoadData() {
        setTimeout(function () {
            $('#mainLoader').fadeOut();
        }, 300);
        setTimeout(function () {
            $('.js-animation-object').removeClass('animated fadeInLeft').addClass('animated fadeInLeft');
        }, 400);
    }
});
