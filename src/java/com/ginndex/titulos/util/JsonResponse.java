/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonResponse {
    public static JsonArray successJson(JsonObject data) {
        JsonArray array = new JsonArray();
        array.add(data);
        return array;
    }

    public static JsonArray errorJson(String message, int statusCode) {
        JsonArray array = new JsonArray();
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("error", true);
        errorJson.addProperty("status", statusCode);
        errorJson.addProperty("message", message);
        array.add(errorJson);
        return array;
    }
    
    public static JsonArray infoJson(String message, int statusCode) {
        JsonArray array = new JsonArray();
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("info", true);
        errorJson.addProperty("status", statusCode);
        errorJson.addProperty("message", message);
        array.add(errorJson);
        return array;
    }
}
