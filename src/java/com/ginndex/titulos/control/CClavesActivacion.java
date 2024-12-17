package com.ginndex.titulos.control;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.ssl.Base64;

/**
 *
 * @author Paola Alonso
 * @since 15/02/2019
 */
public class CClavesActivacion {

    public static String encriptaDos(String texto) {
        String RESP = "";
        String secretKey = "fgrupopinndexav"; //llave para encriptar datos
        String base64EncryptedString = "";

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] plainTextBytes = texto.getBytes("utf-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            byte[] base64Bytes = Base64.encodeBase64(buf);
            base64EncryptedString = new String(base64Bytes);
            RESP = base64EncryptedString;

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP = "error";
        }
        return RESP;
    }

    public String GenerarClaveTimbres(String numTimbres) {
        String RESP = "";
        byte[] bytesEncoded = null;

        try {
            bytesEncoded = Base64.encodeBase64(numTimbres.getBytes());
            RESP = encriptaDos(new String(bytesEncoded));

        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        }

        return RESP;
    }

    public int leeClaveTimbre(String clavesRestantes) {
        int timbresPasados = 0;

        String RESP = "";
        try {

            String claveDesencriptaUno = desencripta(clavesRestantes.trim());

            if (!claveDesencriptaUno.contains("error")) {
                RESP = new String(Base64.decodeBase64(claveDesencriptaUno.getBytes()));
                timbresPasados = Integer.valueOf(RESP);
            } else {
                RESP = "error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            RESP = "error";
        }

        return timbresPasados;
    }

    public String desencripta(String textoEncriptado) {
        String secretKey = "fgrupopinndexav"; //llave para desenciptar datos
        String base64EncryptedString = "";

        try {
            byte[] message = Base64.decodeBase64(textoEncriptado.getBytes("utf-8"));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");

            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);

            byte[] plainText = decipher.doFinal(message);

            base64EncryptedString = new String(plainText, "UTF-8");

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CClavesActivacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CClavesActivacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CClavesActivacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CClavesActivacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CClavesActivacion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return base64EncryptedString;
    }
}
