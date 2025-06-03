package dao;

import modelo.Usuario;
import util.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UsuarioDAO { 
    private static final Logger logger = LogManager.getLogger(UsuarioDAO.class);

    private static final String CONSULTA_INSERTAR = "INSERT INTO usuarios (nombre, email) VALUES (?, ?)";
    private static final String CONSULTA_BUSCAR_POR_ID = "SELECT id, nombre, email FROM usuarios WHERE id = ?";
    private static final String CONSULTA_LISTAR_TODOS = "SELECT id, nombre, email FROM usuarios";
    private static final String CONSULTA_ACTUALIZAR = "UPDATE usuarios SET nombre = ?, email = ? WHERE id = ?";
    private static final String CONSULTA_ELIMINAR = "DELETE FROM usuarios WHERE id = ?";

    public void crear(Usuario usuario) throws SQLException {
        logger.debug("Intentando crear usuario: {}", usuario.getEmail());
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            
            declaracion.setString(1, usuario.getNombre());
            declaracion.setString(2, usuario.getEmail());
            int filasAfectadas = declaracion.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet clavesGeneradas = declaracion.getGeneratedKeys()) {
                    if (clavesGeneradas.next()) {
                        usuario.setId(clavesGeneradas.getInt(1));
                        logger.info("Usuario creado exitosamente con ID: {}", usuario.getId());
                    } else {
                        logger.warn("Se creó el usuario pero no se pudo obtener el ID generado.");
                    }
                }
            } else {
                 logger.warn("No se insertó ninguna fila para el usuario: {}", usuario.getEmail());
            }
        } catch (SQLException e) {
            logger.error("Error al crear usuario '{}': {}", usuario.getEmail(), e.getMessage(), e);
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) { 
                 throw new SQLException("El email '" + usuario.getEmail() + "' ya está registrado.", e.getSQLState(), e);
            }
            throw e;
        }
    }

    public Optional<Usuario> buscarPorId(int id) throws SQLException {
        logger.debug("Buscando usuario con ID: {}", id);
        Usuario usuario = null;
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_BUSCAR_POR_ID)) {
            declaracion.setInt(1, id);
            try (ResultSet resultados = declaracion.executeQuery()) {
                if (resultados.next()) {
                    usuario = new Usuario(
                            resultados.getInt("id"),
                            resultados.getString("nombre"),
                            resultados.getString("email")
                    );
                    logger.info("Usuario encontrado: {}", usuario);
                } else {
                    logger.info("No se encontró usuario con ID: {}", id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
        return Optional.ofNullable(usuario);
    }

    public List<Usuario> listarTodos() throws SQLException {
        logger.debug("Listando todos los usuarios.");
        List<Usuario> listaDeUsuarios = new ArrayList<>();
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             Statement declaracion = conexion.createStatement();
             ResultSet resultados = declaracion.executeQuery(CONSULTA_LISTAR_TODOS)) {
            while (resultados.next()) {
                listaDeUsuarios.add(new Usuario(
                        resultados.getInt("id"),
                        resultados.getString("nombre"),
                        resultados.getString("email")
                ));
            }
            logger.info("Se encontraron {} usuarios.", listaDeUsuarios.size());
        } catch (SQLException e) {
            logger.error("Error al listar todos los usuarios: {}", e.getMessage(), e);
            throw e;
        }
        return listaDeUsuarios;
    }

    public boolean actualizar(Usuario usuario) throws SQLException {
        logger.debug("Actualizando usuario: {}", usuario);
        int filasAfectadas = 0;
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_ACTUALIZAR)) {
            declaracion.setString(1, usuario.getNombre());
            declaracion.setString(2, usuario.getEmail());
            declaracion.setInt(3, usuario.getId());
            filasAfectadas = declaracion.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Usuario con ID {} actualizado exitosamente.", usuario.getId());
            } else {
                logger.warn("No se encontró o no se actualizó el usuario con ID {}.", usuario.getId());
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar usuario ID {}: {}", usuario.getId(), e.getMessage(), e);
             if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                 throw new SQLException("El email '" + usuario.getEmail() + "' ya está registrado por otro usuario.", e.getSQLState(), e);
            }
            throw e;
        }
        return filasAfectadas > 0;
    }

    public boolean eliminar(int id) throws SQLException {
        logger.debug("Eliminando usuario con ID: {}", id);
        int filasAfectadas = 0;
        try (Connection conexion = ConexionBaseDatos.obtenerConexion();
             PreparedStatement declaracion = conexion.prepareStatement(CONSULTA_ELIMINAR)) {
            declaracion.setInt(1, id);
            filasAfectadas = declaracion.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Usuario con ID {} eliminado exitosamente.", id);
            } else {
                logger.warn("No se encontró o no se eliminó el usuario con ID {}.", id);
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar usuario ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
        return filasAfectadas > 0;
    }
}
