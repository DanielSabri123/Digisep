/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ginndex.titulos.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 *
 * @author Célula de desarrollo
 * @description Clase java de Conexión Pool donde se almacena la configuración
 * hacia la conexión
 */
public class CConexionPool {

    DataSource dataSource;
    public ResultSet rsRecord = null;
    public Statement smtConsulta = null;
    public Connection conexion = null;
    HttpServletRequest request;
    HttpServletResponse response;
    private int errorCode;
    private String messageError;
    private Exception ex;

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessageError() {
        return messageError;
    }

    public Exception getEx() {
        return ex;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public CConexionPool(HttpServletRequest request) throws IOException {
        this.request = request;
        if (dataSource == null) {
            inicializaDataSource();
        }

    }

    public ResultSet consulta(String pStrConsulta) throws SQLException {
        conexion = dataSource.getConnection();
        smtConsulta = conexion.createStatement(rsRecord.TYPE_SCROLL_SENSITIVE, rsRecord.CONCUR_UPDATABLE);
        try {

            if (pStrConsulta.toUpperCase().contains("UPDATE") || pStrConsulta.toUpperCase().contains("DELETE")) {

                smtConsulta = conexion.createStatement();
                smtConsulta.execute(pStrConsulta);
            } else {

                smtConsulta = conexion.createStatement();
                rsRecord = smtConsulta.executeQuery(pStrConsulta);

            }
        } catch (SQLException sqlE) {

            if (sqlE.getErrorCode() != 0) {
                System.out.println("Ocurrió un error: " + sqlE.getMessage() + " Código: " + sqlE.getErrorCode());
                errorCode = sqlE.getErrorCode();
                messageError = sqlE.getMessage();
                ex = sqlE;
            }
        }
        return rsRecord;
    }

    public void cerrarConexion() {
        try {
            if (smtConsulta != null) {
                smtConsulta.close();
            }
            conexion.close();
            System.out.println("Conexión cerrada");
        } catch (SQLException ex) {
            Logger.getLogger(CConexionPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void inicializaDataSource() throws FileNotFoundException, IOException {
        String carpeta = "";
        if (request != null) {
            carpeta = request.getParameter("txtCarpeta") + "";
            if (carpeta.equalsIgnoreCase("") || carpeta.equalsIgnoreCase("null")) {
                carpeta = getRequest().getSession().getAttribute("txtCarpeta") + "";
            }
            //System.out.println(carpeta);
        }
        File fileConexion = new File(System.getProperty("user.dir") + "\\webapps\\" + carpeta + "\\Conexion.txt");
        //System.out.println(fileConexion.getAbsolutePath());
        BufferedReader br = new BufferedReader(new FileReader(fileConexion));
        String st;
        String[] datosConexion = new String[3];
        int i = 0;
        while ((st = br.readLine()) != null) {
            datosConexion[i] = st.split("\t")[1];
            ++i;
        }
        br.close();
        PoolProperties p = new PoolProperties();

        p.setUrl(datosConexion[0] + "useLOBs=false;");
        p.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        //p.setUsername("titulo_demo");
        p.setUsername(datosConexion[1]);
        p.setPassword(datosConexion[2]);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(1000);
        p.setInitialSize(1);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(1000);
        p.setMaxIdle(1000);
        p.setLogAbandoned(false);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        dataSource = new DataSource();
        dataSource.setPoolProperties(p);

    }

    private void inicializaDataSourceRESPALDO() {

        PoolProperties p = new PoolProperties();
        /**
         * INSTANCIAS LOCALES EN EL SERVIDOR
         */
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Titulos_Demo;instance=sqlexpress;");
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Cuihac;instance=sqlexpress;");
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Titulos;instance=sqlexpress;");
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Univer;instance=sqlexpress;");
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Cundi;instance=sqlexpress;");
        p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Quetzalcoatl;instance=sqlexpress;");
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/DelPrado;instance=sqlexpress;");
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Concordia;instance=sqlexpress;");

        /*
        INSTANCIAS LOCALES EN MI EQUIPO (BRAULIO)
         */
        //p.setUrl("jdbc:jtds:sqlserver://localhost:1433/Quetzalcoatl;");
        p.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        //p.setUsername("titulo_demo");
        p.setUsername("titulos");
        p.setPassword("titulos");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(1000);
        p.setInitialSize(1);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(1000);
        p.setMaxIdle(1000);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;" + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
        dataSource = new DataSource();
        dataSource.setPoolProperties(p);

    }
}
