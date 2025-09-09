/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author Usuario
 */
public class HttpClientFactory {
     public static CloseableHttpClient createUnsafeHttpClient() throws Exception {
        // Crea un TrustManager que no valida certificados
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }
        };

        // Inicializa el contexto SSL con nuestro TrustManager inseguro
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Crea un socket que no verifica el nombre del host
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
            sslContext,
            NoopHostnameVerifier.INSTANCE
        );

        return HttpClients.custom()
            .setSSLSocketFactory(socketFactory)
            .build();
    }
}
