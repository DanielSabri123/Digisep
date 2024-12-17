/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import com.ginndex.titulos.modelo.Bitacora;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.commons.ssl.Base64.encodeBase64;
import org.apache.commons.ssl.PKCS8Key;

/**
 *
 * @author BSorcia
 */
public class CGeneracionSello {

    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    String sql;
    String nombreInstitucion;
    String idUsuario;

    public CGeneracionSello(Connection con, String nombreInstitucion, String idUsuario) {
        this.con = con;
        this.nombreInstitucion = nombreInstitucion;
        this.idUsuario = idUsuario;
    }

    public void verificarCadenaySello(String idTitulo) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rsRecord = null;
        int contFirmantes = 0;
        String folioControl = "";
        try {
            sql = "SELECT folioControl FROM TETituloElectronico WHERE Id_Titulo = " + idTitulo;
            rsRecord = con.createStatement().executeQuery(sql);
            if (rsRecord.next()) {
                folioControl = rsRecord.getString("folioControl");
            }

            sql = "SELECT * FROM Titulo_Firmante_Intermedia WHERE Id_Titulo = ?";

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, idTitulo);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String idFirmante = rs.getString("Id_Firmante");
                String cadenaOriginal = rs.getString("cadenaOriginal");
                String sello = rs.getString("sello");

                if (cadenaOriginal == null || sello == null) {
                    //Vamos a obtener la cadena original de los firmantes relacionados al titulo.
                    //Posterior a esto generaremos el sello correspondiente.
                    sql = "SELECT TOP 1 cadenaOriginal FROM Texml WHERE folioControl = ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, folioControl);
                    rsRecord = ps.executeQuery();
                    rsRecord.next();
                    cadenaOriginal = rsRecord.getString("cadenaOriginal");
                    String[] arrayCadenaOriginal = cadenaOriginal.split("~");
                    sello = generarSello(idFirmante, arrayCadenaOriginal[contFirmantes]);
                    insertarCadenaYSello(arrayCadenaOriginal[contFirmantes], sello, idFirmante, idTitulo);
                    ps.close();
                    rsRecord.close();
                }
                contFirmantes++;
            }
        } catch (SQLException ex) {

        } finally {
            rs.close();
            pstmt.close();
        }
    }

    private String generarSello(String idFirmante, String cadenaOriginal) {
        sql = "SELECT * FROM TEFirmantes WHERE id_firmante = " + idFirmante + " ;";
        ResultSet rsRecord = null;
        String sello_firmante = "";
        try {
            rsRecord = con.createStatement().executeQuery(sql);
            String contrasenia = "";
            String llave = "";
            while (rsRecord.next()) {
                //sello_firmante = rs.getString("Sello");
                contrasenia = rsRecord.getString("contrasenia");
                llave = rsRecord.getString("llave");
            }
            File key = new File(System.getProperty("user.dir") + "\\webapps\\Instituciones\\" + nombreInstitucion.trim() + "\\KEY\\" + llave);
            PrivateKey llavePrivada;
            //llavePrivada = getPrivateKey(key, "12345678a");
            try {
                llavePrivada = getPrivateKey(key, contrasenia);
                sello_firmante = generarSelloDigital(llavePrivada, cadenaOriginal);
            } catch (GeneralSecurityException e) {
                Logger.getLogger(CGeneracionSello.class.getName()).log(Level.SEVERE, "Ocurrió un error al leer la llave privada {0}", e);
                sello_firmante = "noSuchPassword";
            }
        } catch (SQLException ex) {

        } catch (IOException ex) {
            Logger.getLogger(CGeneracionSello.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sello_firmante;
    }

    private PrivateKey getPrivateKey(File keyFile, String password)
            throws java.security.GeneralSecurityException, IOException {
        FileInputStream in = new FileInputStream(keyFile);
        PKCS8Key pkcs8 = new PKCS8Key(in, password.toCharArray());
        byte[] decrypted = pkcs8.getDecryptedBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypted);
        PrivateKey pk = null;
        if (pkcs8.isDSA()) {
            pk = KeyFactory.getInstance("DSA").generatePrivate(spec);
        } else if (pkcs8.isRSA()) {
            pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
        pk = pkcs8.getPrivateKey();
        return pk;
    }

    private String generarSelloDigital(PrivateKey key, String cadenaOriginal)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        byte[] bytesCadenaOriginal = cadenaOriginal.getBytes("UTF-8");
        signature.update(bytesCadenaOriginal);
        byte[] bytesSigned = signature.sign();
        //Base64 b64 = new Base64(-1);
        byte[] bytesEncoded = encodeBase64(bytesSigned);
        return new String(bytesEncoded);
    }

    private void insertarCadenaYSello(String cadenaOriginal, String selloDigital, String idFirmante, String idTitulo) {
        try {
            Bitacora bitacora = new Bitacora();
            CBitacora controlBitacora;
            bitacora.setId_Usuario(idUsuario);
            bitacora.setModulo("Titulos");
            bitacora.setMovimiento("Insertar Cadena y Sello");
            sql = "UPDATE Titulo_Firmante_Intermedia SET cadenaOriginal = '" + cadenaOriginal + "', sello = '" + selloDigital + "' "
                    + "WHERE ID_Firmante = " + idFirmante + " AND ID_Titulo = " + idTitulo;
            bitacora.setInformacion("Cadena y Sello||CadenaOriginal:" + cadenaOriginal + "||SelloDigital:" + selloDigital + "||Firmante:" + idFirmante + "||Titulo:" + idTitulo);
            controlBitacora = new CBitacora(bitacora,con);
            controlBitacora.addBitacoraGeneral();
            con.createStatement().execute(sql);
        } catch (SQLException ex) {

        }
    }
}
