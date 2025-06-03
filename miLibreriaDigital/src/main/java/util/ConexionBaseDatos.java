package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {
    private static final String URL_CONEXION = "jdbc:h2:./miLibreriaDigital";
    private static final String USUARIO_BD = "sa";
    private static final String CONTRASENA_BD = "";
    private static final String CONTROLADOR_JDBC = "org.h2.Driver";

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName(CONTROLADOR_JDBC);
        } catch (ClassNotFoundException e) {
            System.err.println("Error crítico: No se encontró el controlador JDBC de H2: " + e.getMessage());
            throw new SQLException("Controlador JDBC no encontrado: " + CONTROLADOR_JDBC, e);
        }
        return DriverManager.getConnection(URL_CONEXION, USUARIO_BD, CONTRASENA_BD);
    }
}