/**
 * @Author Paulina Muñoz
 * @Version 0.1
 * Creación de los nodos y acciones de los mismos
 */
(function ($) {
    $.fn.EasyTree = function (options) {
//        var PertenenciaNivel= "";
        var defaults = {
            selectable: true,
            deletable: false,
            editable: false,
            addable: false,
            i18n: {
                deleteNull: 'Seleccione un elemento para eliminar',
                deleteConfirmation: 'Delete this node?',
                confirmButtonLabel: 'Okay',
                editNull: 'Select a node to edit',
                editMultiple: 'Only one node can be edited at one time',
                addMultiple: 'Select a node to add a new node',
                collapseTip: 'collapse',
                expandTip: 'expand',
                selectTip: 'No seleccionado',
                unselectTip: 'Seleccionado',
                editTip: 'Modificar elemento',
                addTip: 'Agregar un nuevo elemento',
                deleteTip: 'Eliminar elemento',
                cancelButtonLabel: 'cancle'
                
            }
        };
        
        

        var warningAlert = $('<div class="alert alert-warning alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button><strong></strong><span class="alert-content"></span> </div> ');
        var dangerAlert = $('<div class="alert alert-danger alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button><strong></strong><span class="alert-content"></span> </div> ');
        var SeleNivelAlert = $('<div class="alert alert-warning alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button><strong></strong><span class="alert-content"></span> </div> ');
var SeleNodoPrincipalAlert = $('<div class="alert alert-info alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button><strong></strong><span class="alert-content"></span> </div> ');

        var createModalidadComponent = $(' <div class="form-group">                                         <div class="col-xs-12 col-lg-12 col-sm-12" id="DivtxtNombreModalidadEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtNombreModalidadEst" name="txtNombreModalidadEst" >                                             <label for="txtNombreModalidadEst"><span class="text-danger ">&#9642;</span> Nombre de la modalidad</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                </div>                                <p class="text-muted font-s12 center-block">                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>                                <div class="block-content">                                    <div class="form-horizontal">                                        <div class="form-group">                                            <div class="pull-left">                                                 <button class="btn btn-sm btn-primary push-5-r push-10" type="button" id="btnCleanEstModalidad" name="btnCleanEstModalidad"><i class="glyphicon glyphicon-erase push-5-r"></i>Limpiar</button>                                            </div>                                            <div class="pull-right">                                                <button class="btn btn-sm btn-success push-5-r push-10 confirm" id="btnAgregarModalidad"><i class="fa fa-plus"></i> Agregar</button>                                                <button class="btn btn-sm btn-danger push-5-r push-10 cancel" id="btnCancelarEstModalidad"><i class="fa fa-ban"></i> Cancelar</button>                                            </div>                                        </div>                                    </div>                                </div>');
        var createNivelEducativoComponent = $('<div class="form-group">                                     <div class="col-xs-12" id="DivtxtNombreNivelEducativoEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtNombreNivelEducativoEst" name="txtNombreNivelEducativoEst" >                                             <label for="txtNombreNivelEducativoEst"><span class="text-danger ">&#9642;</span> Nombre</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                                                   </div>				<div class="form-group">                                       <div class="col-xs-6" id="DivModalidadPerteneciente">                                                    <div class="form-material form-material-primary " id="DivModalidadPertenecienteIn" >                                                        <label for="lstDivModalidadPerteneciente"><span class="text-danger ">&#9642;</span> Modalidad</label>                                                        <select class="form-control" id="lstDivModalidadPerteneciente" style="width: 100%;" name="lstDivModalidadPerteneciente" >                                                            <option value="3">Anual</option>                                                            <option value="2">Semestral</option>                                                            <option value="0">Cuatrimestral</option>                                                        </select>                                                    </div>                                                </div>                                   <div class="col-xs-6 " id="DivtxtClaveNivelEducativoEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtClaveNivelEducativoEst" name="txtClaveNivelEducativoEst" >                                             <label for="txtClaveNivelEducativoEst"><span class="text-danger ">&#9642;</span> Clave</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                                                  </div>                                                                <div class="form-group">                                     <div class="col-xs-6" id="DivtxtTotalCreditosNivelEducativoEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtTotalCreditosNivelEducativoEst" name="txtTotalCreditosNivelEducativoEst" >                                             <label for="txtTotalCreditosNivelEducativoEst"><span class="text-danger ">&#9642;</span> Total de Créditos</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-hashtag"></i></span>                                        </div>                                     </div>                                                                      <div class="col-xs-6" id="DivtxtTotalMateriasNivelEducativoEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtTotalMateriasNivelEducativoEst" name="txtTotalMateriasNivelEducativoEst" >                                             <label for="txtTotalMateriasNivelEducativoEst"><span class="text-danger ">&#9642;</span> Total de Materias</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-hashtag"></i></span>                                        </div>                                     </div>                                </div>                                <p class="text-muted font-s12 center-block">                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>                                <div class="block-content">                                    <div class="form-horizontal">                                        <div class="form-group">                                            <div class="pull-left">                                                 <button class="btn btn-sm btn-primary push-5-r push-10" type="button" id="btnCleanEstNivelEducativo" name="btnCleanEstNivelEducativo"><i class="glyphicon glyphicon-erase push-5-r"></i>Limpiar</button>                                            </div>                                            <div class="pull-right">                                                <button class="btn btn-sm btn-success push-5-r push-10 confirm" id="btnAgregarEstNivelEducativo" ><i class="fa fa-plus"></i> Agregar</button>                                                <button class="btn btn-sm btn-danger push-5-r push-10 cancel" id="btnCancelarEstNivelEducativo"><i class="fa fa-ban"></i> Cancelar</button>                                            </div>                                        </div>                                    </div>                                </div>');
        var createCursoComponent = $(' <div class="form-group">    								 <div class="col-xs-6" id="DivtxtNombreCursoEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtNombreCursoEst" name="txtNombreCursoEst" >                                             <label for="txtNombreCursoEst"><span class="text-danger ">&#9642;</span> Nombre</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                   <div class="col-xs-6" id="DivNivelEdPerteneciente">                                                    <div class="form-material form-material-primary " id="DivNivelEdPertenecienteIn" >                                                        <label for="lstDivNivelEdPerteneciente"><span class="text-danger ">&#9642;</span>Especialidad</label>                                                        <select class="form-control" id="lstDivNivelEdPerteneciente" style="width: 100%;" name="lstDivNivelEdPerteneciente" >                                                            <option value="3">Astronomía</option>                                                            <option value="2">Economía</option>                                                            <option value="0">Letras</option>                                                        </select>                                                    </div>                                                </div>                                                                                                     </div>								 <p class="text-muted font-s12 center-block">                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>                                <div class="block-content">                                    <div class="form-horizontal">                                        <div class="form-group">                                            <div class="pull-left">                                                 <button class="btn btn-sm btn-primary push-5-r push-10" type="button" id="btnCleanEstCurso" name="btnCleanEstNivelEducativo"><i class="glyphicon glyphicon-erase push-5-r"></i>Limpiar</button>                                            </div>                                            <div class="pull-right">                                                <button class="btn btn-sm btn-success push-5-r push-10 confirm" id="btnAgregarEstCurso" ><i class="fa fa-plus"></i> Agregar</button>                                                <button class="btn btn-sm btn-danger push-5-r push-10 cancel" id="btnCancelarEstCurso"><i class="fa fa-ban"></i> Cancelar</button>                                            </div>                                        </div>                                    </div>                                </div>');
        var createMateriaComponent = $('<div class="form-group">                                       <div class="col-xs-12 col-lg-6" id="DivtxtNombreMateriaEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtNombreMateriaEst" name="txtNombreMateriaEst" >                                             <label for="txtNombreMateriaEst"><span class="text-danger ">&#9642;</span> Nombre</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                        <div class="col-xs-12 col-lg-6" id="DivDescripcionMateria">                                                    <div class="form-material form-material-primary" id="DivDesMatIn">                                                        <textarea class="form-control js-maxlength" id="txtaDescMat" name="txtaDescMat" rows="1" placeholder="Ingrese todo lo referente a la materia" maxlength="130"></textarea>                                                        <label for="txtaDescMat">Descripción de la Materia</label>                                                    </div>                                                </div>                                                                                                                                    </div>								<div class="form-group">                                        <div class="col-xs-6" id="DivCursoPerteneciente">                                        <div class="form-material form-material-primary " id="DivCursoPertenecienteIn" >                                            <label for="lstDivCursoPerteneciente"><span class="text-danger ">&#9642;</span> Curso</label>                                            <select class="form-control" id="lstDivCursoPerteneciente" style="width: 100%;" name="lstDivCursoPerteneciente" >                                                <option value="3">Primer Cuatrimestre</option>                                                <option value="2">Segundo Cuatrimestre</option>                                                <option value="0">Tercer Cuatrimestre</option>                                            </select>                                        </div>                                    </div>                                    <div class="col-xs-6" id="DivEquivalenciaPerteneciente">                                        <div class="form-material form-material-primary " id="DivEquivalenciaPertenecienteIn" >                                            <label for="lstDivEquivalenciaPerteneciente"><span class="text-danger ">&#9642;</span> Equivalencia</label>                                            <select class="form-control" id="lstDivEquivalenciaPerteneciente" style="width: 100%;" name="lstDivEquivalenciaPerteneciente" >                                                <option value="3">10 - A</option>                                                <option value="2">9 - B</option>                                                <option value="0">8 - C</option>                                            </select>                                        </div>                                    </div>                                </div>                                <div class="form-group">                                      <div class="col-xs-4" id="DivtxtClaveIntEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtClaveIntEst" name="txtClaveIntEst" >                                             <label for="txtClaveIntEst"><span class="text-danger ">&#9642;</span> Clave Int.</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                   <div class="col-xs-4" id="DivtxtClaveSEPEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtClaveSEPEst" name="txtClaveSEPEst" >                                             <label for="txtClaveSEPEst"><span class="text-danger ">&#9642;</span> Clave SEP</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                   <div class="col-xs-4" id="DivtxtClaveDGTIEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtClaveDGTIEst" name="txtClaveDGTIEst" >                                             <label for="txtClaveDGTIEst"><span class="text-danger ">&#9642;</span> Clave DGTI</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-font"></i></span>                                        </div>                                     </div>                                                                  </div>                                                                                                <div class="form-group">                                      <div class="col-xs-12 col-lg-4" id="DivtxtHrsMatEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtHrsSemanaMatEst" name="txtHrsSemanaMatEst" >                                             <label for="txtHrsSemanaMatEst"><span class="text-danger ">&#9642;</span> Hrs.Semana</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-hashtag"></i></span>                                        </div>                                     </div>                                   <div class="col-xs-12 col-lg-4" id="DivtxtHrsTeoriaMatEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtHrsTeoriaMatEst" name="txtHrsTeoriaMatEst" >                                             <label for="txtHrsTeoriaMatEst"><span class="text-danger ">&#9642;</span> Hrs.Teoría</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-hashtag"></i></span>                                        </div>                                     </div>                                   <div class="col-xs-12 col-lg-4" id="DivtxtHrsPracticaEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtHrsPracticaEst" name="txtHrsPracticaEst" >                                             <label for="txtHrsPracticaEst"><span class="text-danger ">&#9642;</span> Hrs.Práctica</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-hashtag"></i></span>                                        </div>                                     </div>                                                                  </div>                                <div class="form-group">                                        <div class="col-xs-6 col-lg-4" id="DivcheckSeguimientoEst">                                        <label class="css-input css-checkbox css-checkbox-sm css-checkbox-primary">                                                <input type="checkbox" checked="" id="checkSeguimientoEst"><span></span> Con Seguimiento                                            </label>                                    </div>                                    <div class="col-xs-6 col-lg-4" id="DivcheckSegMateriaEst">                                        <label class="css-input css-checkbox css-checkbox-sm css-checkbox-primary">                                                <input type="checkbox" checked="" id="checkSeMateriaEst"><span></span> Seguimiento Materia                                            </label>                                    </div>                                                                                  <div class=" col-xs-6 col-lg-4" id="DivcheckParaescolarEst">                                        <label class="css-input css-checkbox css-checkbox-sm css-checkbox-primary">                                                <input type="checkbox" checked="" id="checkParaescolarEst"><span></span> Paraescolar                                            </label>                                    </div>                                   <div class=" col-xs-6 col-lg-4" id="DivcheckCompartidaEst">                                        <label class="css-input css-checkbox css-checkbox-sm css-checkbox-primary">                                                <input type="checkbox" checked="" id="checkCompartidaEst"><span></span> Compartida                                            </label>                                    </div>                                                                       <div class=" col-xs-6 col-lg-4" id="DivcheckPonderarEst">                                        <label class="css-input css-checkbox css-checkbox-sm css-checkbox-primary">                                                <input type="checkbox" checked="" id="checkPonderarEst"><span></span> Ponderar                                            </label>                                    </div>                                    <div class="col-xs-6 col-lg-4" id="DivcheckANAEst">                                        <label class="css-input css-checkbox css-checkbox-sm css-checkbox-primary">                                                <input type="checkbox" checked="" id="checkANAEst"><span></span> A/NA                                            </label>                                    </div>                                                                  </div>                                 <div class="form-group">                                      <div class="col-xs-4" id="DivtxtNombreCursoEst">                                        <div class="form-material form-material-primary " id="DivNivelEdPertenecienteIn" >                                                        <label for="lstDivNivelEdPerteneciente"><span class="text-danger ">&#9642;</span> Tipo</label>                                                        <select class="form-control" id="lstDivNivelEdPerteneciente" style="width: 100%;" name="lstDivNivelEdPerteneciente" >                                                            <option value="3">Tronco Común</option>                                                            <option value="2">Propedéutica</option>                                                            <option value="0">Letras</option>                                                        </select>                                                    </div>                                    </div>                                                                     <div class="col-xs-4" id="DivtxtTeoriaPorcEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtTeoriaPorcEst" name="txtTeoriaPorcEst" >                                             <label for="txtTeoriaPorcEst"><span class="text-danger ">&#9642;</span> Teoría</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-percent"></i></span>                                        </div>                                     </div>                                   <div class="col-xs-4" id="DivtxtPracticaEst">                                        <div class="form-material input-group form-material-primary floating" style="width: 100%;">                                            <input class="form-control" type="text" id="txtPracticaEst" name="txtPracticaEst" >                                             <label for="txtPracticaEst"><span class="text-danger ">&#9642;</span> Práctica</label>                                            <span class="input-group-addon hidden-xs"><i class="fa fa-percent"></i></span>                                        </div>                                     </div>                                                                                                       </div>                                <p class="text-muted font-s12 center-block">                                    Los datos marcados con <span class="text-danger ">&#9642;</span> son obligatorios.</p>                                <div class="block-content">                                    <div class="form-horizontal">                                        <div class="form-group">                                            <div class="pull-left">                                                 <button class="btn btn-sm btn-primary push-5-r push-10" type="button" id="btnCleanEstMateria" name="btnCleanEstNivelEducativo"><i class="glyphicon glyphicon-erase push-5-r"></i>Limpiar</button>                                            </div>                                            <div class="pull-right">                                                <button class="btn btn-sm btn-success push-5-r push-10 confirm" id="btnAgregarEstMateria" ><i class="fa fa-plus"></i> Agregar</button>                                                <button class="btn btn-sm btn-danger push-5-r push-10 cancel" id="btnCancelarEstMateria"><i class="fa fa-ban"></i> Cancelar</button>                                            </div>                                        </div>                                    </div>                                </div>');

        options = $.extend(defaults, options);

        this.each(function () {
            var easyTree = $(this);
            $.each($(easyTree).find('ul > li'), function() {
                var text;
                if($(this).is('li:has(ul)')) {
                    var children = $(this).find(' > ul');
                    $(children).remove();
                    text = $(this).text();
                    $(this).html('<span><span class="glyphicon"></span><a href="javascript: void(0);"></a> </span>');
                    $(this).find(' > span > span').addClass('glyphicon-folder-open');
                    $(this).find(' > span > a').text(text);
                    $(this).append(children);
                }
                else {
                    text = $(this).text();
                    $(this).html('<span><span class="glyphicon"></span><a href="javascript: void(0);"></a> </span>');
                    $(this).find(' > span > span').addClass('glyphicon-education  fa-lg');
                    $(this).find(' > span > a').text(text);
                }
            });

            $(easyTree).find('li:has(ul)').addClass('parent_li').find(' > span').attr('title', options.i18n.collapseTip);

            // add easy tree toolbar dom
            if (options.deletable || options.editable || options.addable) {
                $(easyTree).prepend('<div class="easy-tree-toolbar"></div> ');
            }

            // addable
            if (options.addable) {
                $(easyTree).find('.easy-tree-toolbar').append('<div class="create"><button class="btn btn-primary push-5-r push-10"><span class="glyphicon glyphicon-plus"></span></button></div> ');
                $(easyTree).find('.easy-tree-toolbar .create > button').attr('title', options.i18n.addTip).click(function () {
                    var createBlock = $(easyTree).find('.easy-tree-toolbar .create');
                    var selected = getSelectedItems();
                       if (selected.length <= 0) {
                            //$(easyTree).find(' > ul').append($(item));
                            
                     
                            $(easyTree).prepend(SeleNivelAlert);
                             $(easyTree).find('.alert .alert-content').html("Por favor, seleccione un nivel");
                        }
//                      PertenenciaNivel= selected.attr('class');
//                       alert(PertenenciaNivel);
                     if($(selected).hasClass('Modalidad')){
                           $(createBlock).append(createModalidadComponent);
                    $(createModalidadComponent).find('input').focus();
                    //$(createModalidadComponent).find('.confirm').text(options.i18n.confirmButtonLabel);
                      
                        $("#titleEst").html('Modalidad');
                        $("#DivContenedorNivel").empty();
                       $("#DivContenedorNivel").append(createModalidadComponent);
                    $(createModalidadComponent).find('.confirm').click(function () {
                     
                        if ($(createModalidadComponent).find('input').val() === '')
                            
                            return;
                             
//                        var selected = getSelectedItems();
//                       alert(selected.attr('id'));
                     
                    
                        var item = $('<li class="NivelEducativo Mod"><span><span class="fa fa-connectdevelop fa-lg"></span><a href="javascript: void(0);">' +'         '+  $(createModalidadComponent).find('input').val() + '</a> </span></li>');
                        $(item).find(' > span > span').attr('title', options.i18n.collapseTip);
                        $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                        
                          if (selected.length > 1) {
                            $(easyTree).prepend(warningAlert);
                            $(easyTree).find('.alert .alert-content').text(options.i18n.addMultiple);
                        } else {
                            if ($(selected).hasClass('parent_li')) {
                                $(selected).find(' > ul').append(item);
                                
                            } else {
                                $(selected).addClass('parent_li').find(' > span > span').addClass('fa-connectdevelop fa-lg').removeClass('fa-connectdevelop fa-lg');
                                $(selected).append($('<ul></ul>')).find(' > ul').append(item);
                            }
                        }
                        $(createModalidadComponent).find('input').val('');
                        if (options.selectable) {
                            $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                            $(item).find(' > span > a').click(function (e) {
                                var li = $(this).parent().parent();
                                if (li.hasClass('li_selected')) {
                                    $(this).attr('title', options.i18n.selectTip);
                                    $(li).removeClass('li_selected');
                                }
                                else {
                                    $(easyTree).find('li.li_selected').removeClass('li_selected');
                                    $(this).attr('title', options.i18n.unselectTip);
                                    $(li).addClass('li_selected');
                                }

                                if (options.deletable || options.editable || options.addable) {
                                    var selected = getSelectedItems();
                                    if (options.editable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').removeClass('disabled');
                                    }

                                    if (options.deletable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').removeClass('disabled');
                                    }

                                }

                                e.stopPropagation();

                            });
                        }
                        $(createModalidadComponent).remove();
                        $("#titleEst").html('Acciones');
                         
                    });
                         
                     }else if ($(selected).hasClass('NivelEducativo')) {
                           $(createBlock).append(createNivelEducativoComponent);
                    $(createNivelEducativoComponent).find('input').focus();
                    //$(createModalidadComponent).find('.confirm').text(options.i18n.confirmButtonLabel);
                      
                        $("#titleEst").html('Nivel Educativo');
                         $("#DivContenedorNivel").empty();
                       $("#DivContenedorNivel").append(createNivelEducativoComponent);
                    $(createNivelEducativoComponent).find('.confirm').click(function () {
                     
                        if ($(createNivelEducativoComponent).find('input').val() === '')
                            
                            return;
                             
//                        var selected = getSelectedItems();
//                       alert(selected.attr('id'));
                     
                    
                    
                        var item = $('<li class="Curso NivE"><span><span class="fa fa-th-large fa-lg "></span><a href="javascript: void(0);">' +'         '+ $(createNivelEducativoComponent).find('input').val() + '</a> </span></li>');
                        $(item).find(' > span > span').attr('title', options.i18n.collapseTip);
                        $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                        
                        if (selected.length <= 0) {
                            $(easyTree).prepend(SeleNivelAlert);
                             $(easyTree).find('.alert .alert-content').html("Por favor seleccione un nivel");
                        } else if (selected.length > 1) {
                            $(easyTree).prepend(warningAlert);
                            $(easyTree).find('.alert .alert-content').text(options.i18n.addMultiple);
                        } else {
                            if ($(selected).hasClass('parent_li')) {
                                $(selected).find(' > ul').append(item);
                            } else {
                                $(selected).addClass('parent_li').find(' > span > span').addClass('fa-th-large fa-lg').removeClass('fa-th-large fa-lg');
                                $(selected).append($('<ul></ul>')).find(' > ul').append(item);
                            }
                        }
                        $(createNivelEducativoComponent).find('input').val('');
                        if (options.selectable) {
                            $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                            $(item).find(' > span > a').click(function (e) {
                                var li = $(this).parent().parent();
                                if (li.hasClass('li_selected')) {
                                    $(this).attr('title', options.i18n.selectTip);
                                    $(li).removeClass('li_selected');
                                }
                                else {
                                    $(easyTree).find('li.li_selected').removeClass('li_selected');
                                    $(this).attr('title', options.i18n.unselectTip);
                                    $(li).addClass('li_selected');
                                }

                                if (options.deletable || options.editable || options.addable) {
                                    var selected = getSelectedItems();
                                    if (options.editable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').removeClass('disabled');
                                    }

                                    if (options.deletable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').removeClass('disabled');
                                    }

                                }

                                e.stopPropagation();

                            });
                        }
                        $(createNivelEducativoComponent).remove();
                        $("#titleEst").html('Acciones');
                         
                    });
                     }else if ($(selected).hasClass('Curso')) {
                             $(createBlock).append(createCursoComponent);
                    $(createCursoComponent).find('input').focus();
                    //$(createModalidadComponent).find('.confirm').text(options.i18n.confirmButtonLabel);
                      
                        $("#titleEst").html('Curso');
                         $("#DivContenedorNivel").empty();
                       $("#DivContenedorNivel").append(createCursoComponent);
                    $(createCursoComponent).find('.confirm').click(function () {
                     
                        if ($(createCursoComponent).find('input').val() === '')
                            
                            return;
                             
//                        var selected = getSelectedItems();
//                       alert(selected.attr('id'));
                     
                    
                    
                        var item = $('<li class="Materia Cur"><span><span class="fa fa-cube fa-lg"></span><a href="javascript: void(0);">' +'         '+ $(createCursoComponent).find('input').val() + '</a> </span></li>');
                        $(item).find(' > span > span').attr('title', options.i18n.collapseTip);
                        $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                        
                        if (selected.length <= 0) {
                            $(easyTree).prepend(SeleNivelAlert);
                             $(easyTree).find('.alert .alert-content').html("Por favor seleccione un nivel");
                        } else if (selected.length > 1) {
                            $(easyTree).prepend(warningAlert);
                            $(easyTree).find('.alert .alert-content').text(options.i18n.addMultiple);
                        } else {
                            if ($(selected).hasClass('parent_li')) {
                                $(selected).find(' > ul').append(item);
                            } else {
                                $(selected).addClass('parent_li').find(' > span > span').addClass('fa-cube fa-lg').removeClass('fa-cube fa-lg');
                                $(selected).append($('<ul></ul>')).find(' > ul').append(item);
                            }
                        }
                        $(createCursoComponent).find('input').val('');
                        if (options.selectable) {
                            $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                            $(item).find(' > span > a').click(function (e) {
                                var li = $(this).parent().parent();
                                if (li.hasClass('li_selected')) {
                                    $(this).attr('title', options.i18n.selectTip);
                                    $(li).removeClass('li_selected');
                                }
                                else {
                                    $(easyTree).find('li.li_selected').removeClass('li_selected');
                                    $(this).attr('title', options.i18n.unselectTip);
                                    $(li).addClass('li_selected');
                                }

                                if (options.deletable || options.editable || options.addable) {
                                    var selected = getSelectedItems();
                                    if (options.editable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').removeClass('disabled');
                                    }

                                    if (options.deletable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').removeClass('disabled');
                                    }

                                }

                                e.stopPropagation();

                            });
                        }
                        $(createCursoComponent).remove();
                        $("#titleEst").html('Acciones');
                          
                    });
                     }else if ($(selected).hasClass('Materia')) {
                           $(createBlock).append(createMateriaComponent);
                    $(createMateriaComponent).find('input').focus();
                    //$(createModalidadComponent).find('.confirm').text(options.i18n.confirmButtonLabel);
                      
                        $("#titleEst").html('Materia');
                         $("#DivContenedorNivel").empty();
                       $("#DivContenedorNivel").append(createMateriaComponent);
                    $(createMateriaComponent).find('.confirm').click(function () {
                     
                        if ($(createMateriaComponent).find('input').val() === '')
                            
                            return;
                             
//                        var selected = getSelectedItems();
//                       alert(selected.attr('id'));
                     
                    
                    
                        var item = $('<li class="Mat"><span><span class="fa fa-cubes fa-lg"></span><a href="javascript: void(0);">' +'         '+ $(createMateriaComponent).find('input').val() + '</a> </span></li>');
                        $(item).find(' > span > span').attr('title', options.i18n.collapseTip);
                        $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                        
                        if (selected.length <= 0) {
                            //$(easyTree).find(' > ul').append($(item));
                            
                            alert("holis");
                            $(easyTree).prepend(SeleNivelAlert);
                             $(easyTree).find('.alert .alert-content').html("Por favor seleccione un nivel");
                        } else if (selected.length > 1) {
                            $(easyTree).prepend(warningAlert);
                            $(easyTree).find('.alert .alert-content').text(options.i18n.addMultiple);
                        } else {
                            if ($(selected).hasClass('parent_li')) {
                                $(selected).find(' > ul').append(item);
                            } else {
                                $(selected).addClass('parent_li').find(' > span > span').addClass('fa-cubes fa-lg').removeClass('fa-cubes fa-lg');
                                $(selected).append($('<ul></ul>')).find(' > ul').append(item);
                            }
                        }
                        $(createMateriaComponent).find('input').val('');
                        if (options.selectable) {
                            $(item).find(' > span > a').attr('title', options.i18n.selectTip);
                            $(item).find(' > span > a').click(function (e) {
                                var li = $(this).parent().parent();
                                if (li.hasClass('li_selected')) {
                                    $(this).attr('title', options.i18n.selectTip);
                                    $(li).removeClass('li_selected');
                                }
                                else {
                                    $(easyTree).find('li.li_selected').removeClass('li_selected');
                                    $(this).attr('title', options.i18n.unselectTip);
                                    $(li).addClass('li_selected');
                                }

                                if (options.deletable || options.editable || options.addable) {
                                    var selected = getSelectedItems();
                                    if (options.editable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .edit > button').removeClass('disabled');
                                    }

                                    if (options.deletable) {
                                        if (selected.length <= 0 || selected.length > 1)
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').addClass('disabled');
                                        else
                                            $(easyTree).find('.easy-tree-toolbar .remove > button').removeClass('disabled');
                                    }

                                }

                                e.stopPropagation();

                            });
                        }
                        $(createMateriaComponent).remove();
                        $("#titleEst").html('Acciones');
                          
                    });
                     }
                  
                    //$(createModalidadComponent).find('.cancel').text(options.i18n.cancelButtonLabel);
                    $(createModalidadComponent).find('.cancel').click(function () {
                        $(createModalidadComponent).remove();
                      
                           $("#titleEst").html('Acciones');
                        
                    });
                });
            }

            // editable
            if (options.editable) {
                $(easyTree).find('.easy-tree-toolbar').append('<div class="edit"><button class="btn btn-primary push-5-r push-10 disabled"><span class="glyphicon glyphicon-edit"></span></button></div> ');
                $(easyTree).find('.easy-tree-toolbar .edit > button').attr('title', options.i18n.editTip).click(function () {
                    $(easyTree).find('input.easy-tree-editor').remove();
                    $(easyTree).find('li > span > a:hidden').show();
                    var selected = getSelectedItems();
                    if (selected.length <= 0) {
                        $(easyTree).prepend(warningAlert);
                        $(easyTree).find('.alert .alert-content').html(options.i18n.editNull);
                    }
                    else if (selected.length > 1) {
                        $(easyTree).prepend(warningAlert);
                        $(easyTree).find('.alert .alert-content').html(options.i18n.editMultiple);
                    }
                    else {
                        
                        if ($(selected).hasClass('Mod')) {
                            $("#titleEst").html('Modificar Modalidad');
                            $("#DivContenedorNivel").empty();
                            $("#DivContenedorNivel").append(createModalidadComponent);
                        }else if ($(selected).hasClass('NivE')){
                             $("#titleEst").html('Modificar Nivel Educativo');
                            $("#DivContenedorNivel").empty();
                            $("#DivContenedorNivel").append(createNivelEducativoComponent);
                        }else if ($(selected).hasClass('Cur')){
                             $("#titleEst").html('Modificar Curso');
                            $("#DivContenedorNivel").empty();
                            $("#DivContenedorNivel").append(createCursoComponent);
                        }else if ($(selected).hasClass('Mat')){
                             $("#titleEst").html('Modificar Materia');
                            $("#DivContenedorNivel").empty();
                            $("#DivContenedorNivel").append(createMateriaComponent);
                        }
                       
                    }
                });
            }

            // deletable
            if (options.deletable) {
                $(easyTree).find('.easy-tree-toolbar').append('<div class="remove"><button class="btn btn-danger push-5-r push-10 disabled"><span class="glyphicon glyphicon-remove"></span></button></div> ');
                $(easyTree).find('.easy-tree-toolbar .remove > button').attr('title', options.i18n.deleteTip).click(function () {
                    var selected = getSelectedItems();
                    if($(selected).hasClass('NodoPrincipal')){
                         $(easyTree).prepend(SeleNodoPrincipalAlert);
                             $(easyTree).find('.alert .alert-content').html("Oupss...Este es el nodo principal, no podemos realizar dicha acción");
                    }
                    else if (selected.length <= 0) {
                        $(easyTree).prepend(warningAlert);
                        $(easyTree).find('.alert .alert-content').html(options.i18n.deleteNull);
                    } else {
                        $(easyTree).prepend(dangerAlert);
                        $(easyTree).find('.alert .alert-content').html(options.i18n.deleteConfirmation)
                            .append('<a style="margin-left: 10px;" class="btn btn-default btn-danger confirm"></a>')
                            .find('.confirm').html(options.i18n.confirmButtonLabel);
                        $(easyTree).find('.alert .alert-content .confirm').on('click', function () {
                            $(selected).find(' ul ').remove();
                            if($(selected).parent('ul').find(' > li').length <= 1) {
                                $(selected).parents('li').removeClass('parent_li').find(' > span > span').removeClass('glyphicon-folder-open').addClass('glyphicon-education  fa-lg');
                                $(selected).parent('ul').remove();
                            }
                            $(selected).remove();
                            $(dangerAlert).remove();
                        });
                    }
                });
            }

            // collapse or expand
            $(easyTree).delegate('li.parent_li > span', 'click', function (e) {
                var children = $(this).parent('li.parent_li').find(' > ul > li');
                if (children.is(':visible')) {
                    children.hide('fast');
                    $(this).attr('title', options.i18n.expandTip)
                        .find(' > span.glyphicon')
                        .addClass('glyphicon-folder-close')
                        .removeClass('glyphicon-folder-open');
                } else {
                    children.show('fast');
                    $(this).attr('title', options.i18n.collapseTip)
                        .find(' > span.glyphicon')
                        .addClass('glyphicon-folder-open')
                        .removeClass('glyphicon-folder-close');
                }
                e.stopPropagation();
            });

            // selectable, only single select
            if (options.selectable) {
                $(easyTree).find('li > span > a').attr('title', options.i18n.selectTip);
                $(easyTree).find('li > span > a').click(function (e) {
                    var li = $(this).parent().parent();
                    if (li.hasClass('li_selected')) {
                        $(this).attr('title', options.i18n.selectTip);
                        $(li).removeClass('li_selected');
                    }
                    else {
                        $(easyTree).find('li.li_selected').removeClass('li_selected');
                        $(this).attr('title', options.i18n.unselectTip);
                        $(li).addClass('li_selected');
                    }

                    if (options.deletable || options.editable || options.addable) {
                        var selected = getSelectedItems();
                        if (options.editable) {
                            if (selected.length <= 0 || selected.length > 1)
                                $(easyTree).find('.easy-tree-toolbar .edit > button').addClass('disabled');
                            else
                                $(easyTree).find('.easy-tree-toolbar .edit > button').removeClass('disabled');
                        }

                        if (options.deletable) {
                            if (selected.length <= 0 || selected.length > 1)
                                $(easyTree).find('.easy-tree-toolbar .remove > button').addClass('disabled');
                            else
                                $(easyTree).find('.easy-tree-toolbar .remove > button').removeClass('disabled');
                        }

                    }

                    e.stopPropagation();

                });
            }

//             Get selected items
            var getSelectedItems = function () {
                return $(easyTree).find('li.li_selected');
                
            };
//      
           
           
        });
    };
})(jQuery);
