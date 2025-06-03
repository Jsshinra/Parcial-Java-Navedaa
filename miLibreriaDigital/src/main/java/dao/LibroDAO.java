package dao;

import modelo.Libro;
import util.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LibroDAO {
    private static final Logger logger = LogManager.getLogger(LibroDAO.class);

    private static final String CONSULTA_INSERTAR = "INSERT INTO libros (titulo, autor, id_usuario) VALUES (?, ?, ?)";
    private static final String CONSULTA_BUSCAR_POR_ID = "SELECT id, titulo, autor, id_usuario FROM libros WHERE id = ?";
    private static final String CONSULTA_LISTAR_TODOS = "SELECT id, titulo, autor, id_usuario FROM libros";
    private static final String CONSULTA_ACTUALIZAR = "UPDATE libros SET titulo = ?, autor = ?, id_usuario = ? WHERE id = ?";
    private static final String CONSULTA_ELIMINAR = "DELETE FROM libros WHERE id = ?";
    private static final String CONSULTA_LISTAR_POR_ID_USUARIO = "SELECT id, titulo, autor, id_usuario FROM libros WHERE id_usuario = ?";
    private static final String CONSULTA_ELIMINAR_POR_ID_USUARIO = "DELETE FROM libros WHERE id_usuario = ?";

    public void crear(Libro libro) throws SQLException {
        logger.debug("Intentando crear libro: {}", libro.getTitulo());
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            declaracion.setString(1, libro.getTitulo());
            declaracion.setString(2, libro.getAutor());
            declaracion.setInt(3, libro.getIdUsuario());
            int filasAfectadas = declaracion.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet clavesGeneradas = declaracion.getGeneratedKeys()) {
                    if (clavesGeneradas.next()) {
                        libro.setId(clavesGeneradas.getInt(1));
                        logger.info("Libro creado exitosamente con ID: {}", libro.getId());
                    } else {
                         logger.warn("Se creó el libro pero no se pudo obtener el ID generado.");
                    }
                }
            } else {
                 logger.warn("No se insertó ninguna fila para el libro: {}", libro.getTitulo());
            }
        } catch (SQLException e) {
            logger.error("Error al crear libro '{}': {}", libro.getTitulo(), e.getMessage(), e);
            if (e.getSQLState() != null && e.getSQLState().equals("23503")) { 
                 throw new SQLException("El usuario con ID " + libro.getIdUsuario() + " no existe. No se puede asignar el libro.", e.getSQLState(), e);
            }
            throw e;
        }
    }

    public Optional<Libro> buscarPorId(int id) throws SQLException {
        logger.debug("Buscando libro con ID: {}", id);
        Libro libro = null;
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_BUSCAR_POR_ID)) {
            declaracion.setInt(1, id);
            try (ResultSet resultados = declaracion.executeQuery()) {
                if (resultados.next()) {
                    libro = new Libro(
                            resultados.getInt("id"),
                            resultados.getString("titulo"),
                            resultados.getString("autor"),
                            resultados.getInt("id_usuario")
                    );
                    logger.info("Libro encontrado: {}", libro);
                } else {
                    logger.info("No se encontró libro con ID: {}", id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar libro por ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
        return Optional.ofNullable(libro);
    }

    public List<Libro> listarTodos() throws SQLException {
        logger.debug("Listando todos los libros.");
        List<Libro> listaDeLibros = new ArrayList<>();
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             Statement declaracion = conexion.createStatement();
             ResultSet resultados = declaracion.executeQuery(CONSULTA_LISTAR_TODOS)) {
            while (resultados.next()) {
                listaDeLibros.add(new Libro(
                        resultados.getInt("id"),
                        resultados.getString("titulo"),
                        resultados.getString("autor"),
                        resultados.getInt("id_usuario")
                ));
            }
            logger.info("Se encontraron {} libros.", listaDeLibros.size());
        } catch (SQLException e) {
            logger.error("Error al listar todos los libros: {}", e.getMessage(), e);
            throw e;
        }
        return listaDeLibros;
    }

    public boolean actualizar(Libro libro) throws SQLException {
        logger.debug("Actualizando libro: {}", libro);
        int filasAfectadas = 0;
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_ACTUALIZAR)) {
            declaracion.setString(1, libro.getTitulo());
            declaracion.setString(2, libro.getAutor());
            declaracion.setInt(3, libro.getIdUsuario());
            declaracion.setInt(4, libro.getId());
            filasAfectadas = declaracion.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Libro con ID {} actualizado exitosamente.", libro.getId());
            } else {
                logger.warn("No se encontró o no se actualizó el libro con ID {}.", libro.getId());
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar libro ID {}: {}", libro.getId(), e.getMessage(), e);
            if (e.getSQLState() != null && e.getSQLState().equals("23503")) {
                 throw new SQLException("El usuario con ID " + libro.getIdUsuario() + " no existe. No se puede asignar el libro.", e.getSQLState(), e);
            }
            throw e;
        }
        return filasAfectadas > 0;
    }

    public boolean eliminar(int id) throws SQLException {
        logger.debug("Eliminando libro con ID: {}", id);
        int filasAfectadas = 0;
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_ELIMINAR)) {
            declaracion.setInt(1, id);
            filasAfectadas = declaracion.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Libro con ID {} eliminado exitosamente.", id);
            } else {
                logger.warn("No se encontró o no se eliminó el libro con ID {}.", id);
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar libro ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
        return filasAfectadas > 0;
    }

    public List<Libro> listarPorIdUsuario(int idUsuario) throws SQLException {
        logger.debug("Listando libros para el usuario con ID: {}", idUsuario);
        List<Libro> listaDeLibros = new ArrayList<>();
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_LISTAR_POR_ID_USUARIO)) {
            declaracion.setInt(1, idUsuario);
            try (ResultSet resultados = declaracion.executeQuery()) {
                while (resultados.next()) {
                    listaDeLibros.add(new Libro(
                            resultados.getInt("id"),
                            resultados.getString("titulo"),
                            resultados.getString("autor"),
                            resultados.getInt("id_usuario")
                    ));
                }
            }
            logger.info("Se encontraron {} libros para el usuario con ID {}.", listaDeLibros.size(), idUsuario);
        } catch (SQLException e) {
            logger.error("Error al listar libros por ID de usuario {}: {}", idUsuario, e.getMessage(), e);
            throw e;
        }
        return listaDeLibros;
    }

    public int eliminarPorIdUsuario(int idUsuario) throws SQLException {
        logger.debug("Eliminando libros del usuario con ID: {}", idUsuario);
        int filasAfectadas = 0;
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_ELIMINAR_POR_ID_USUARIO)) {
            declaracion.setInt(1, idUsuario);
            filasAfectadas = declaracion.executeUpdate();
            logger.info("Se eliminaron {} libros del usuario con ID {}.", filasAfectadas, idUsuario);
        } catch (SQLException e) {
            logger.error("Error al eliminar libros por ID de usuario {}: {}", idUsuario, e.getMessage(), e);
            throw e;
        }
        return filasAfectadas;
    }
}