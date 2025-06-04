package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdministradorBaseDatos extends ProcesadorGenerico{
    private static final Logger logger = LogManager.getLogger(AdministradorBaseDatos.class);

    public void inicializarEsquema() {
        logger.info("Verificando e inicializando el esquema de la base de datos...");
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             Statement declaracion = conexion.createStatement()) {

            String sqlCrearTablaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                                         + "id INT AUTO_INCREMENT PRIMARY KEY, "
                                         + "nombre VARCHAR(255) NOT NULL, "
                                         + "email VARCHAR(255) NOT NULL UNIQUE)";
            declaracion.execute(sqlCrearTablaUsuarios);
            logger.debug("Tabla 'usuarios' verificada/creada exitosamente.");

            String sqlCrearTablaLibros = "CREATE TABLE IF NOT EXISTS libros ("
                                       + "id INT AUTO_INCREMENT PRIMARY KEY, "
                                       + "titulo VARCHAR(255) NOT NULL, "
                                       + "autor VARCHAR(255) NOT NULL, "
                                       + "id_usuario INT, "
                                       + "FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE)";
            declaracion.execute(sqlCrearTablaLibros);
            logger.debug("Tabla 'libros' verificada/creada exitosamente.");
            
            logger.info("Esquema de base de datos inicializado correctamente.");

        } catch (SQLException e) {
            logger.error("Error crítico durante la inicialización del esquema de la base de datos: {}", e.getMessage(), e);
            System.err.println("No se pudo inicializar el esquema de la base de datos. La aplicación podría no funcionar como se espera.");
        }
    }
    }

