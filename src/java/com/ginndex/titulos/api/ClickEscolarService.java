
package com.ginndex.titulos.api;

import com.ginndex.titulos.control.CCarrerasCarga;
import com.ginndex.titulos.control.CConexion;
import com.ginndex.titulos.control.HttpClientFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.HttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.ginndex.titulos.util.*;
import com.google.gson.JsonElement;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class ClickEscolarService {
    
    //private static final String URL_SERVICE = "http://localhost:8084/CESAR/api/acceso";
    private static final String URL_SERVICE = "https://click-escolar.com/CESAR/api/acceso";
    private static final String URL_SERVICE_TEST_CONEXION = "/digisep/test-conexion";
    private static final String URL_SERVICE_CARRERAS = "/digisep/descargar-carreras";
    private static final String URL_SERVICE_ASIGNATURAS = "/digisep/descargar-asignaturas";
    private static final String URL_SERVICE_ALUMNO_INDIVIDUAL = "/digisep/descargar-alumno-individual";
    private static final String URL_SERVICE_ALUMNOS_POR_CARRERA = "/digisep/descargar-alumnos-por-carrera";
    private static final String URL_SERVICE_CALIFICACIONES_INDIVIDUAL = "/digisep/descargar-calificaciones-individual";
    private static final String URL_SERVICE_CALIFICACIONES_POR_CARRERA = "/digisep/descargar-calificaciones-por-carrera";
    
    private HttpServletRequest request;
    private JsonObject respuestaConsumo;
    CConexion conexion;
    
    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public JsonObject getRespuestaConsumo() {
        return respuestaConsumo;
    }

    public void setRespuestaConsumo(JsonObject respuestaConsumo) {
        this.respuestaConsumo = respuestaConsumo;
    }
    
    public JsonArray servicioRealizarTestConexion(String claveInstitucion, String usuario, String contrasena) throws Exception {
        String responseBody = "";
        JsonArray resultado = new JsonArray();
        Encriptacion encryp = new Encriptacion();
        
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("claveInstitucion", encryp.encriptar(claveInstitucion)));
            params.add(new BasicNameValuePair("usuario", encryp.encriptar(usuario)));
            params.add(new BasicNameValuePair("contrasena", encryp.encriptar(contrasena)));
            params.add(new BasicNameValuePair("rol", encryp.encriptar("admin")));

            HttpPost httpPost = new HttpPost(URL_SERVICE + URL_SERVICE_TEST_CONEXION);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            //HttpClient httpClient = HttpClientBuilder.create().build();
            HttpClient httpClient = HttpClientFactory.createUnsafeHttpClient();

            
            try (CloseableHttpResponse responseHandler = (CloseableHttpResponse) httpClient.execute(httpPost)) {
                StatusLine statusLine = responseHandler.getStatusLine();
                responseBody = EntityUtils.toString(responseHandler.getEntity(), StandardCharsets.UTF_8);

                if (statusLine.getStatusCode() == 404) {
                    return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br>El recurso solicitado no está disponible en este momento.", 404);
                } else if (statusLine.getStatusCode() != 200) {
                    return JsonResponse.errorJson(responseBody, statusLine.getStatusCode());
                } else {
                    JsonParser parser = new JsonParser();
                    JsonObject respuestaJson = parser.parse(responseBody).getAsJsonObject();
                    
                    String mensajeLimpio = respuestaJson.get("mensaje").toString().substring(1, respuestaJson.get("mensaje").toString().length() - 1);
                    String mensaje = encryp.desencriptar(mensajeLimpio);
                    
                    if(mensaje.contains("(1)")){
                        return JsonResponse.successJson(respuestaJson);
                    }
                    if(mensaje.contains("(2)") || mensaje.contains("(5)")){
                        return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br>El procesó no pudo ser completado.", 500);
                    }
                    if(mensaje.contains("(3)")){
                        return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br>No se encontró el usuario ingresado.", 500);
                    }
                    if(mensaje.contains("(4)")){
                        return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br>La clave de la institución es incorrecta.", 500);
                    }
                    return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br>Error desconocido.", 500);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return JsonResponse.errorJson(ex.getMessage(), 500);
        }
    }

    public JsonArray servicioDescargarCarreras(String idCarrera) throws Exception {
        String responseBody = "";
        Encriptacion encryp = new Encriptacion();
        String nombreBD = "";
        
        try{
            String respuestaConsultaBD = consultarNombreBaseDatos();
            String[] partes = respuestaConsultaBD.split("\\|");

            if (!partes[0].equalsIgnoreCase("success")) {
                throw new Exception(partes[1]);
            } else {
                nombreBD = partes[1];
            }
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("idCarrera", encryp.encriptar(idCarrera)));
            params.add(new BasicNameValuePair("nombreBD", nombreBD));
            
            HttpPost httpPost = new HttpPost(URL_SERVICE + URL_SERVICE_CARRERAS);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            //HttpClient httpClient = HttpClientBuilder.create().build();
            HttpClient httpClient = HttpClientFactory.createUnsafeHttpClient();
            
            try (CloseableHttpResponse responseHandler = (CloseableHttpResponse) httpClient.execute(httpPost)) {
                StatusLine statusLine = responseHandler.getStatusLine();
                responseBody = EntityUtils.toString(responseHandler.getEntity(), StandardCharsets.UTF_8);
                
                JsonParser parser = new JsonParser();
                JsonElement parsedElement = parser.parse(responseBody);
                if (parsedElement.isJsonArray()) {
                    JsonArray respuestaArray = parsedElement.getAsJsonArray();
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("La carrera seleccionada no tiene materias registradas.<br><br><small>Por favor, verifica que hayan materias registradas en Click Escolar</small>", 500);
                    }
                } else if (parsedElement.isJsonObject()) {
                    JsonObject obj = parsedElement.getAsJsonObject();
                    if (obj.has("mensaje")) {
                        return JsonResponse.infoJson(obj.get("mensaje").getAsString(), 500);
                    } else {
                        return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                    }
                }
                return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
            }
        }catch (Exception ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return JsonResponse.errorJson(ex.getMessage(), 500);
        }
    }

    public JsonArray servicioDescargarAlumnoIndividual(String matricula) throws Exception {
        String responseBody = "";
        Encriptacion encryp = new Encriptacion();
        String nombreBD = "";
        
        try{
            String respuestaConsultaBD = consultarNombreBaseDatos();
            String[] partes = respuestaConsultaBD.split("\\|");

            if (!partes[0].equalsIgnoreCase("success")) {
                throw new Exception(partes[1]);
            } else {
                nombreBD = partes[1];
            }
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("matricula", encryp.encriptar(matricula)));
            params.add(new BasicNameValuePair("nombreBD", nombreBD));
            
            HttpPost httpPost = new HttpPost(URL_SERVICE + URL_SERVICE_ALUMNO_INDIVIDUAL);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            //HttpClient httpClient = HttpClientBuilder.create().build();
            HttpClient httpClient = HttpClientFactory.createUnsafeHttpClient();
            
            try (CloseableHttpResponse responseHandler = (CloseableHttpResponse) httpClient.execute(httpPost)) {
                StatusLine statusLine = responseHandler.getStatusLine();
                responseBody = EntityUtils.toString(responseHandler.getEntity(), StandardCharsets.UTF_8);
                
                JsonParser parser = new JsonParser();
                JsonElement parsedElement = parser.parse(responseBody);
                if (parsedElement.isJsonArray()) {
                    JsonArray respuestaArray = parsedElement.getAsJsonArray();
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("No se encontraron registros del alumno.<br><br><small>Por favor, verifica que la matrícula sea correcta y vuelve a intentarlo. Si el problema persiste, contacta con soporta</small>", 500);
                    }
                } else if (parsedElement.isJsonObject()) {
                    JsonObject obj = parsedElement.getAsJsonObject();
                    if (obj.has("mensaje")) {
                        return JsonResponse.infoJson(obj.get("mensaje").getAsString(), 500);
                    } else {
                        return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                    }
                }
                return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                
                /*if (statusLine.getStatusCode() == 404) {
                    return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br><br>El recurso solicitado no está disponible en este momento o no existe.", 404);
                } else if (statusLine.getStatusCode() != 200) {
                    return JsonResponse.errorJson(responseBody, statusLine.getStatusCode());
                } else {
                    JsonParser parser = new JsonParser();
                    JsonArray respuestaArray = parser.parse(responseBody).getAsJsonArray();
                    
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("No se encontró ningún alumno con la matrícula ingresada.<br><br><small>Verifique que la matrícula sea correcta. El sistema no encontró registros asociados a ese valor.</small>", 500);
                    }
                }*/
            }
        }catch (Exception ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return JsonResponse.errorJson(ex.getMessage(), 500);
        }
    }
    
    public JsonArray servicioDescargarAlumnoPorCarrera(String idCarrera) throws Exception {
        String responseBody = "";
        Encriptacion encryp = new Encriptacion();
        String nombreBD = "";
        
        try{
            String respuestaConsultaBD = consultarNombreBaseDatos();
            String[] partes = respuestaConsultaBD.split("\\|");

            if (!partes[0].equalsIgnoreCase("success")) {
                throw new Exception(partes[1]);
            } else {
                nombreBD = partes[1];
            }
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("idCarrera", encryp.encriptar(idCarrera)));
            params.add(new BasicNameValuePair("nombreBD", nombreBD));
            
            HttpPost httpPost = new HttpPost(URL_SERVICE + URL_SERVICE_ALUMNOS_POR_CARRERA);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            //HttpClient httpClient = HttpClientBuilder.create().build();
            HttpClient httpClient = HttpClientFactory.createUnsafeHttpClient();
            
            try (CloseableHttpResponse responseHandler = (CloseableHttpResponse) httpClient.execute(httpPost)) {
                StatusLine statusLine = responseHandler.getStatusLine();
                responseBody = EntityUtils.toString(responseHandler.getEntity(), StandardCharsets.UTF_8);
                
                JsonParser parser = new JsonParser();
                JsonElement parsedElement = parser.parse(responseBody);
                if (parsedElement.isJsonArray()) {
                    JsonArray respuestaArray = parsedElement.getAsJsonArray();
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("No se encontraron registros de alumnos asociados a la carrera en Click Escolar.<br><br><small>Por favor, verifica que la carrera esté correctamente configurada en la plataforma y que existan alumnos registrados. Si el problema persiste, contacta con soporte.</small>", 500);
                    }
                } else if (parsedElement.isJsonObject()) {
                    JsonObject obj = parsedElement.getAsJsonObject();
                    if (obj.has("mensaje")) {
                        return JsonResponse.infoJson(obj.get("mensaje").getAsString(), 500);
                    } else {
                        return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                    }
                }
                return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
            }
        }catch (Exception ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return JsonResponse.errorJson(ex.getMessage(), 500);
        }
    }
    
    public JsonArray servicioDescargarCalificacionesAlumnoIndividual(String matricula, String idCarrera) throws Exception {
        String responseBody = "";
        Encriptacion encryp = new Encriptacion();
        String nombreBD = "";
        
        try{
            String respuestaConsultaBD = consultarNombreBaseDatos();
            String[] partes = respuestaConsultaBD.split("\\|");

            if (!partes[0].equalsIgnoreCase("success")) {
                throw new Exception(partes[1]);
            } else {
                nombreBD = partes[1];
            }
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("matricula", encryp.encriptar(matricula)));
            params.add(new BasicNameValuePair("idCarrera", encryp.encriptar(idCarrera)));
            params.add(new BasicNameValuePair("nombreBD", nombreBD));
            
            HttpPost httpPost = new HttpPost(URL_SERVICE + URL_SERVICE_CALIFICACIONES_INDIVIDUAL);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            
            //HttpClient httpClient = HttpClientBuilder.create().build();
            HttpClient httpClient = HttpClientFactory.createUnsafeHttpClient();
            
            try (CloseableHttpResponse responseHandler = (CloseableHttpResponse) httpClient.execute(httpPost)) {
                StatusLine statusLine = responseHandler.getStatusLine();
                responseBody = EntityUtils.toString(responseHandler.getEntity(), StandardCharsets.UTF_8);
                
                JsonParser parser = new JsonParser();
                JsonElement parsedElement = parser.parse(responseBody);
                if (parsedElement.isJsonArray()) {
                    JsonArray respuestaArray = parsedElement.getAsJsonArray();
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("No se encontraron registros de calificaciones asociados al alumno.<br><br><small>Por favor, verifica que existan calificaciones registradas. Si el problema persiste, contacta con soporte.</small>", 500);
                    }
                } else if (parsedElement.isJsonObject()) {
                    JsonObject obj = parsedElement.getAsJsonObject();
                    if (obj.has("mensaje")) {
                        return JsonResponse.infoJson(obj.get("mensaje").getAsString(), 500);
                    } else {
                        return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                    }
                }
                return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                
                
                /*if (statusLine.getStatusCode() == 404) {
                    return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br><br>El recurso solicitado no está disponible en este momento o no existe.", 404);
                } else if (statusLine.getStatusCode() != 200) {
                    return JsonResponse.errorJson(responseBody, statusLine.getStatusCode());
                } else {
                    JsonParser parser = new JsonParser();
                    JsonArray respuestaArray = parser.parse(responseBody).getAsJsonArray();
                    
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("No se encontró ninguna calificación para el alumno ingresado.<br><br><small>El sistema no encontró registros asociados a ese valor.</small>", 500);
                    }
                }*/
            }
        }catch (Exception ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return JsonResponse.errorJson(ex.getMessage(), 500);
        }
    }
    
    public JsonArray servicioDescargarCalificacionesAlumnoPorCarrera(String idCarrera) throws Exception {
        String responseBody = "";
        Encriptacion encryp = new Encriptacion();
        String nombreBD = "";
        
        try{
            String respuestaConsultaBD = consultarNombreBaseDatos();
            String[] partes = respuestaConsultaBD.split("\\|");

            if (!partes[0].equalsIgnoreCase("success")) {
                throw new Exception(partes[1]);
            } else {
                nombreBD = partes[1];
            }
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("idCarrera", encryp.encriptar(idCarrera)));
            params.add(new BasicNameValuePair("nombreBD", nombreBD));
            
            HttpPost httpPost = new HttpPost(URL_SERVICE + URL_SERVICE_CALIFICACIONES_POR_CARRERA);
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            
            //HttpClient httpClient = HttpClientBuilder.create().build();
            HttpClient httpClient = HttpClientFactory.createUnsafeHttpClient();
            
            try (CloseableHttpResponse responseHandler = (CloseableHttpResponse) httpClient.execute(httpPost)) {
                StatusLine statusLine = responseHandler.getStatusLine();
                responseBody = EntityUtils.toString(responseHandler.getEntity(), StandardCharsets.UTF_8);
                
                JsonParser parser = new JsonParser();
                JsonElement parsedElement = parser.parse(responseBody);
                if (parsedElement.isJsonArray()) {
                    JsonArray respuestaArray = parsedElement.getAsJsonArray();
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("No se encontraron registros de calificaciones asociados a los alumnos en Click Escolar.<br><br><small>Por favor, verifica que la carrera esté correctamente configurada en la plataforma y que existan calificaciones registradas.. Si el problema persiste, contacta con soporte.</small>", 500);
                    }
                } else if (parsedElement.isJsonObject()) {
                    JsonObject obj = parsedElement.getAsJsonObject();
                    if (obj.has("mensaje")) {
                        return JsonResponse.infoJson(obj.get("mensaje").getAsString(), 500);
                    } else {
                        return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                    }
                }
                return JsonResponse.errorJson("La respuesta del servidor no es válida.", 500);
                /*if (statusLine.getStatusCode() == 404) {
                    return JsonResponse.errorJson("No fue posible establecer conexión con el sistema:<br><br>El recurso solicitado no está disponible en este momento o no existe.", 404);
                } else if (statusLine.getStatusCode() != 200) {
                    return JsonResponse.errorJson(responseBody, statusLine.getStatusCode());
                } else {
                    JsonParser parser = new JsonParser();
                    JsonArray respuestaArray = parser.parse(responseBody).getAsJsonArray();
                    
                    if (respuestaArray.size() > 0) {
                        return respuestaArray;
                    } else {
                        return JsonResponse.infoJson("No se encontró ninguna calificación en la carrera seleccionada.<br><br><small>El sistema no encontró registros asociados a ese valor.</small>", 500);
                    }
                }*/
            }
        }catch (Exception ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return JsonResponse.errorJson(ex.getMessage(), 500);
        }
    }
    
    public String consultarNombreBaseDatos() throws UnsupportedEncodingException, SQLException{
        HttpServletRequest requestProvisional = request;
        requestProvisional.setCharacterEncoding("UTF-8");
        HttpSession sessionOk = request.getSession();
        conexion = new CConexion();
        conexion.setRequest(request);
        
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            String Query = "SELECT Nombre_BD FROM Configuracion_Click_Escolar";
            con = conexion.GetconexionInSite();
            pstmt = con.prepareStatement(Query);    
            rs = pstmt.executeQuery();
            
            if(rs.next()){
                return "success|" + rs.getString("Nombre_BD");
            }else{
                return "sinConfiguracion|No se encontró ninguna configuración con Click Escolar.<br><br><small>Por favor, verifica que la conexión con Click Escolar haya sido realizada desde el módulo de Configuración inicial.</small>";
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error al consultar la conexión con click escolar: " + ex.getMessage();
        } catch (Exception ex) {
            Logger.getLogger(ClickEscolarService.class.getName()).log(Level.SEVERE, null, ex);
            return "error|Error al consultar la conexión con click escolar: " + ex.getMessage();
        } finally {
            if (con != null && !con.isClosed()) {
                con.close();
            }

            if (pstmt != null && !pstmt.isClosed()) {
                pstmt.close();
            }

            if (rs != null && !rs.isClosed()) {
                rs.close();
            }

            if (conexion != null && !conexion.GetconexionInSite().isClosed()) {
                conexion.GetconexionInSite().close();
            }
        }
    }
    
}
