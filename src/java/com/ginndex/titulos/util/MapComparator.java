/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.util;

/**
 *
 * @author BSorcia
 */
import java.util.Comparator;
import java.util.Map;

public class MapComparator implements Comparator<Map> {

    String field;
    short opt;

    /**
     * Inicializamos la clase.
     *
     * @param field Nombre del campo a obtener de los objetos a comparar
     * @param opt  <ul><ol>1:Comparar cadenas de texto</ol><ol>2:Comparar
     * enteros</ol></ul>
     */
    public MapComparator(String field, short opt) {
        this.field = field;
        this.opt = opt;
    }

    @Override
    public int compare(Map o1, Map o2) {
        int compare;
        switch (opt) {
            case 1:
                compare = o1.get(field).toString().compareTo(o2.get(field).toString());
                break;
            case 2:
                int num1 = Integer.parseInt(o1.get(field).toString());
                int num2 = Integer.parseInt(o2.get(field).toString());
                compare = num1 < num2 ? -1 : (num1 == num2 ? 0 : 1);
                break;
            default:
                compare = 0;
                break;

        }
        return compare;
    }
}
