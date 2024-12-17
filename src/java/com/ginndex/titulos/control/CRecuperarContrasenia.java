/*
    Document   : Controler de Recuperar Contraseña
    Created on : 1/03/2023, 03:59:30 PM
    Author     : Ricardo Reyna
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import com.ginndex.titulos.modelo.CorreoInstitucional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Random;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Usuario
 */
public class CRecuperarContrasenia {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String correo;
    private String usuario;
    Bitacora bitacora;
    CBitacora cBitacora;
    CConexionPool conector;
    private int id_Usuario;
    private String carpeta;

    public int getId_Usuario() {
        return id_Usuario;
    }

    public void setId_Usuario(int id_Usuario) {
        this.id_Usuario = id_Usuario;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Bitacora getBitacora() {
        return bitacora;
    }

    public void setBitacora(Bitacora bitacora) {
        this.bitacora = bitacora;
    }

    public CBitacora getcBitacora() {
        return cBitacora;
    }

    public void setcBitacora(CBitacora cBitacora) {
        this.cBitacora = cBitacora;
    }

    public CConexionPool getConector() {
        return conector;
    }

    public void setConector(CConexionPool conector) {
        this.conector = conector;
    }

    public String mandarCorreo() {
        String respuesta = "";
        try {
            request.setCharacterEncoding("UTF-8");
            request.getRequestURI();
            conector = new CConexionPool(request);

            HttpSession sessionOk = request.getSession();
            //ESte seria la variable dentro de la url
            String user = request.getParameter("txtLogUsuario");
            String pass = request.getParameter("txtLogPass");
            this.carpeta = request.getParameter("txtCarpeta");
            String corr = this.request.getParameter("correo"); //POR SI NOS MANDAN ESPACIOS

            this.correo = corr.trim();

            if (verificarCorreo(correo)) {

                if (enviarCorreo()) {
                    respuesta = "0"; // 0 Significa que se ha enviado el correo exitosamente
                } else {
                    respuesta = "2"; // 2 Significa que ha ocurrido el error 2, el cual no se ha enviado el correo
                }
            } else {
                respuesta = "1";  // 1 Significa que ha ocurrido el error 1, el cual no ha encontrado el correo que han puesto
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Excepcion en MandarCorreo");
            
        }
        
        return respuesta;
    }

    private boolean verificarCorreo(String correo) {

        boolean bandera = false;
        PreparedStatement ps = null;
        try {
            ResultSet rs;
            
            rs = conector.consulta("select NombreUsuario, Email, UPI.Id_UsuarioPersona,UPI.Id_Persona, UPI.Id_Usuario from Usuario_Persona_Intermedia UPI LEFT join usuario U on U.Id_Usuario= UPI.Id_Usuario "
                    + "LEFT JOIN persona P on UPI.Id_Persona= P.Id_Persona "
                    + "where Email='" + correo + "' COLLATE SQL_Latin1_General_CP1_CI_AS");
            if (rs.next()) {
                bandera = true;
                this.id_Usuario = rs.getInt("Id_Usuario");
                this.usuario= rs.getString("NombreUsuario");
                System.out.println("SI EXISTE EL USUARIO "+ usuario+" CON ID " + id_Usuario);
            } else {
                bandera = false;
                System.out.println("N0 EXISTE EL USUARIO");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Excepcion en verificarCorreo");
        }
        return bandera;
    }

    public boolean enviarCorreo() throws Exception {
        boolean respuesta = false;

        String destinatario =this.usuario;

        try {
            //Se genera una contraseña random para que el usuario la ingrese en el sistema y pueda restablecer la suya
            String nuevaContraseña = generarContraseña();

            ResultSet rs;

            rs = conector.consulta("select Id_Correo, Correo, Puerto, host, contrasena, usarSSL from correos"); //Extraemos los datos del correo donde vamos enviar correos        

            CorreoInstitucional objCorreoEmpresa = new CorreoInstitucional(); // Se hace un objeto de tipo correo
            if (rs.next()) {

                objCorreoEmpresa.setId_Correo(rs.getInt("Id_Correo"));
                objCorreoEmpresa.setCorreo(rs.getString("Correo"));
                objCorreoEmpresa.setPuerto(rs.getString("Puerto"));
                objCorreoEmpresa.setHost(rs.getString("Host"));
                objCorreoEmpresa.setContrasena(rs.getString("contrasena"));
                objCorreoEmpresa.setSSL(rs.getString("usarSSL"));
            }

            final String correoI = objCorreoEmpresa.getCorreo();

            final String contI = objCorreoEmpresa.getContrasena().trim();

            String port = objCorreoEmpresa.getPuerto().trim();

            String Host = objCorreoEmpresa.getHost().trim();

            String ssl = objCorreoEmpresa.getSSL().trim();

            //Actualizamos la contraseña del usuario para que pueda ingresar con la generada
            rs = conector.consulta("update usuario set Contrasena = '" + nuevaContraseña + "' where id_Usuario = " + id_Usuario + ";");
         

            String mensaje = "<body link=\"#00a5b5\" vlink=\"#00a5b5\" alink=\"#00a5b5\">\n"
                    + "	<table class=\" main contenttable\" align=\"center\" style=\"font-weight: normal;border-collapse: collapse;border: 0;margin-left: auto;margin-right: auto;padding: 0;font-family: Arial, sans-serif;color: #555559;background-color: white;font-size: 16px;line-height: 26px;width: 600px;\">\n"
                    + "		<tr>\n"
                    + "			<td class=\"border\" style=\"border-collapse: collapse;border: 1px solid #eeeff0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\">\n"
                    + "				<table style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;\">\n"
                    + "					<tr>\n"
                    + "						<td colspan=\"4\" valign=\"top\" class=\"image-section\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;background-color: #fff;border-bottom: 4px solid #00a5b5\">\n"
                    + "							<a href=\"https://digisep.com/"+carpeta+"/Vista/Generales/LogIn.jsp\"><img class=\"top-image\" src=\"https://digisep.com/images/Digi-Sep.png;\" alt=\"digi sep\" width=\"100\" height=\"100\"></a>\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr>\n"
                    + "						<td valign=\"top\" class=\"side title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 20px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;vertical-align: top;background-color: white;border-top: none;\">\n"
                    + "							<table style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;\">\n"
                    + "								<tr>\n"
                    + "									<td class=\"head-title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 28px;line-height: 34px;font-weight: bold; text-align: center;\">\n"
                    + "										<div class=\"mktEditable\" id=\"main_title\">\n"
                    + "											Recuperación de Contraseña\n"
                    + "										</div>\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"sub-title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;padding-top:5px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 18px;line-height: 29px;font-weight: bold;text-align: center;\">\n"
                    + "										<div class=\"mktEditable\" id=\"intro_title\">\n"
                    + "											Se ha recibido una solicitud de recuperación de contraseña para el usuario: " + destinatario
                    + "										</div>\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"top-padding\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 5px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\"></td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"grey-block\" style=\"border-collapse: collapse;border: 0;margin: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;background-color: #fff; text-align:center;\">\n"
                    + "										<div class=\"mktEditable\" id=\"cta\">\n"
                    + "											<img class=\"top-image\" src=\"https://tituloelectronicosep.com/wp-content/uploads/2019/04/proceso-para-titulos-electronicos-Digi-sep-1400x633.png\" width=\"180\" height=\"100\" /><br><br>\n"
                    + "											<strong>Contraseña nueva: </strong> " + nuevaContraseña + "<br><br>\n"
                    + "											<a style=\"color:#ffffff; background-color: #00a5b5;  border: 10px solid #00a5b5; border-radius: 3px; text-decoration:none;\" href=\"https://digisep.com/"+carpeta+"/Vista/Generales/LogIn.jsp\">Ir a Digi-SEP</a>\n"
                    + "										</div>\n"
                    + "                                                                         <p>Ingrese a la plataforma con su usuario y su nueva contraseña. </p>"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"top-padding\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 15px 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 21px;\">\n"
                    + "										<hr size=\"1\" color=\"#eeeff0\">\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "								<tr>\n"
                    + "									<td class=\"text\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\">\n"
                    + "										<div class=\"mktEditable\" id=\"main_text\">\n"
                    + "											Si usted no ha realizado esta acción ignore el mensaje.\n"
                    + "										</div>\n"
                    + "									</td>\n"
                    + "								</tr>\n"
                    + "							</table>\n"
                    + "						</td>\n"
                    + "					</tr>\n"
                    + "					<tr bgcolor=\"#fff\" style=\"border-top: 4px solid #00a5b5;\">\n"
                    + "					</tr>\n"
                    + "				</table>\n"
                    + "</body>\n"
                    + "<style type='text/css'>"
                    + "@media only screen and (max-width: 600px) {\n"
                    + "		.main {\n"
                    + "			width: 320px !important;\n"
                    + "		}\n"
                    + "		.inside-footer {\n"
                    + "			width: 320px !important;\n"
                    + "		}\n"
                    + "		table[class=\"contenttable\"] { \n"
                    + "            width: 320px !important;\n"
                    + "            text-align: left !important;\n"
                    + "        }\n"
                    + "        td[class=\"force-col\"] {\n"
                    + "	        display: block !important;\n"
                    + "	    }\n"
                    + "	     td[class=\"rm-col\"] {\n"
                    + "	        display: none !important;\n"
                    + "	    }\n"
                    + "		.mt {\n"
                    + "			margin-top: 15px !important;\n"
                    + "		}\n"
                    + "		*[class].width300 {width: 255px !important;}\n"
                    + "		*[class].block {display:block !important;}\n"
                    + "		*[class].blockcol {display:none !important;}\n"
                    + "		.emailButton{\n"
                    + "            width: 100% !important;\n"
                    + "        }\n"
                    + "        .emailButton a {\n"
                    + "            display:block !important;\n"
                    + "            font-size:18px !important;\n"
                    + "        }\n"
                    + "	}"
                    + " </style>";

            Properties props = new Properties();
          
            props.put(
                    "mail.smtp.auth", "true");
            props.put(
                    "mail.smtp.starttls.enable", "true");
            props.put(
                    "mail.smtp.host", Host);
            props.put(
                    "mail.smtp.port", port);

            props.put("mail.smtp.ssl.trust", Host);

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoI, contI);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(correoI));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(this.correo));
            message.setSubject("DIGI-SEP Generación de nueva contraseña");
            message.setContent(mensaje, "text/html; charset=utf-8");
            Transport.send(message);

            rs.close();

            respuesta = true;
            //****************************************************** Fin metodo enviar correo *********************************************************
        } catch (Exception e) {
            respuesta = false;
            System.out.println("Excepcion en Enviar correo completo");
            throw new RuntimeException(e);
            
        }

        return respuesta;
    }

    // se genera una cobntraseña random
    public String generarContraseña() {
        String RESP = "";
        Random rnd = new Random();
        String[] elementos = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a",
            "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n ", "-", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "_"};

        for (int i = 0; i < 10; i++) {
            int el = rnd.nextInt(27);
            RESP += elementos[el];
        }
        return RESP;
    }

}
