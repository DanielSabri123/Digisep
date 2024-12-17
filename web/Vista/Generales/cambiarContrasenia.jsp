<%-- 
    Document   : cambiarContrasenia
    Created on : 01-feb-2019, 10:32:12
    Author     : Braulio Sorcia
--%>

<%
    if (request.getParameter("allow") != null) {
%>
<div class="modal fade" id="modal-cambioC" tabindex="-1" role="dialog" aria-hidden="true" style="overflow-x: hidden; overflow-y: auto;">
    <div class="modal-dialog modal-dialog modal-sm modal-dialog-popin mymodal nomini">
        <div class="modal-content" id="modal-Contrasenia-draggable">
            <div class="block block-themed block-transparent remove-margin-b">
                <div class="block-header bg-primary-dark">
                    <ul class="block-options">
                        <li>
                            <button onclick="$('#modal-cambioC').modal('hide')" id="btnCerrarModal" type="button"><i class="si si-close resetForm"></i></button>
                        </li>                                
                    </ul>
                    <h3 class="block-title" id="modaltitleContrasenia">Cambiar contraseña</h3>
                </div>
                <form class="form-horizontal" name="formCambiarContra" id="formCambiarContra">
                    <div class="block">
                        <div class="block-content">
                            <div class="form-group">
                                <div class="col-xs-12"  id="divTipoContraseniaActual">
                                    <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divTipoContraseniaActualIn">
                                        <input class="form-control js-maxlength" type="password" id="txtContraseniaAc" name="txtContraseniaAc" maxlength="50" onkeypress="return noBlankSpaces()">
                                        <label for="txtContraseniaAc"><span class="text-danger" >&#9642;</span> Contraseña actual</label>
                                        <span class="input-group-addon hidden-xs"><i class="fa fa-user-secret"></i></span>
                                    </div>
                                </div>
                                <div class="col-xs-12">
                                    <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divNuevaContraseniaIn">
                                        <input class="form-control js-maxlength" id="txtNuevaContrasenia" name="txtNuevaContrasenia" maxlength="50" type="password" onkeypress="return noBlankSpaces()">
                                        <label for="txtNuevaContrasenia"><span class="text-danger" >&#9642;</span> Nueva contraseña</label>
                                        <span class="input-group-addon hidden-xs"><i class="fa fa-user-secret"></i></span>
                                    </div>
                                </div>
                                <div class="col-xs-12">
                                    <div class="form-material input-group form-material-primary floating" style="width: 100%;" id="divNuevaContraseniaConfirmIn">
                                        <input class="form-control js-maxlength" id="txtNuevaContraseniaConfirm" name="txtNuevaContraseniaConfirm" maxlength="50" type="password" onkeypress="return noBlankSpaces()">
                                        <label for="txtNuevaContraseniaConfirm"><span class="text-danger" >&#9642;</span> Confirmar nueva contraseña</label>
                                        <span class="input-group-addon hidden-xs"><i class="fa fa-user-secret"></i></span>
                                    </div>
                                </div>
                            </div>
                            <p class="text-muted font-s12 center-block">
                                Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.
                            </p>
                        </div>
                    </div>
                    <div class="block-content" style="overflow-x:inherit; margin-top: -25px;">
                        <div class="form-horizontal">
                            <div class="form-group">
                                <div class="col-xs-12">
                                    <button class="btn btn-sm btn-success pull-right" type="submit" id="btnCambiarContrasenia" name="btnCambiarContrasenia" onclick="$('#bandera').val('1');"><i class="si si-plus push-5-r"></i>Realizar cambio</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%
    } else {
        response.sendRedirect("../Administrador/Bienvenida.jsp");
    }
%>