/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encriptacion {
    public String desencriptar(String palabraEncriptada) throws Exception {
        String key = "nqJleI2erWIXgvFuM8gD3mVr6WP1laAX"; // Clave utilizada en Dart/Flutter
        byte[] encryptedBytes = Base64.getDecoder().decode(palabraEncriptada); // Decodifica la cadena Base64 en un arreglo de bytes

        // Crea una clave secreta usando la clave proporcionada y el algoritmo AES
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

        // Crea una instancia del algoritmo de cifrado AES en modo CBC con relleno PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]); // Define un IV de 16 bytes lleno de ceros.

        // Inicializa el cifrado con la clave y el IV
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        // Realiza la operación de desencriptación sobre los bytes encriptados
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Convierte los bytes desencriptados a una cadena de texto
        String decryptedText = new String(decryptedBytes);

        // Devuelve el texto desencriptado
        return decryptedText;
    }

    public String encriptar(String palabra) throws Exception {
        if(palabra!=null){
            String key = "nqJleI2erWIXgvFuM8gD3mVr6WP1laAX"; // Clave utilizada en Dart/Flutter

        // Convierte la cadena de texto en bytes
        byte[] plaintextBytes = palabra.getBytes();

        // Crea una clave secreta usando la clave proporcionada y el algoritmo AES
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

        // Crea una instancia del algoritmo de cifrado AES en modo CBC con relleno PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]); // Define un IV de 16 bytes lleno de ceros.

        // Inicializa el cifrado con la clave y el IV para operación de encriptación
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        // Realiza la operación de encriptación sobre los bytes de la cadena de texto
        byte[] encryptedBytes = cipher.doFinal(plaintextBytes);

        // Convierte los bytes encriptados a una cadena Base64
        String encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);

        // Devuelve la cadena encriptada en formato Base64
        return encryptedString;
        }else{
            return "";
        }
    }
}
