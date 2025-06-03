package principal;

import dao.UsuarioDAO; 
import dao.LibroDAO;   
import modelo.Usuario;
import modelo.Libro;
import util.AdministradorBaseDatos;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClasePrincipal {
    private static final Scanner teclado = new Scanner(System.in);
    private static final UsuarioDAO daoUsuario = new UsuarioDAO();
    private static final LibroDAO daoLibro = new LibroDAO();
    private static final Logger logger = LogManager.getLogger(ClasePrincipal.class);
    private static final AdministradorBaseDatos adminBD = new AdministradorBaseDatos();

    public static void main(String[] args) {
        logger.info("Iniciando la aplicación Gestor de Biblioteca Digital...");
        adminBD.inicializarEsquema(); 
        mostrarMenuPrincipal();
        teclado.close();
        logger.info("Aplicación finalizada.");
    }

    private static int leerOpcionDelMenu(String mensajeSolicitud) {
        System.out.print(mensajeSolicitud);
        while (true) {
            try {
                String lineaIngresada = teclado.nextLine().trim();
                if (lineaIngresada.isEmpty()) {
                    System.out.print("La opción no puede estar vacía. " + mensajeSolicitud);
                    continue;
                }
                return Integer.parseInt(lineaIngresada);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, ingrese un número. " + mensajeSolicitud);
            }
        }
    }

    private static int leerId(String entidad, String accion) {
        return leerOpcionDelMenu("Ingrese el ID del " + entidad + " a " + accion + ": ");
    }

    private static String leerTexto(String mensajeSolicitud, boolean permitirVacio) {
        System.out.print(mensajeSolicitud);
        String textoIngresado = teclado.nextLine().trim();
        while (!permitirVacio && textoIngresado.isEmpty()) {
            System.out.print("Este campo no puede estar vacío. " + mensajeSolicitud);
            textoIngresado = teclado.nextLine().trim();
        }
        return textoIngresado;
    }

    private static void mostrarMenuPrincipal() {
        int opcionSeleccionada;
        do {
            System.out.println("\n╔═════════════════════════════════════╗");
            System.out.println("║     GESTOR DE BIBLIOTECA DIGITAL    ║");
            System.out.println("╠═════════════════════════════════════╣");
            System.out.println("║ 1. Gestionar Usuarios               ║");
            System.out.println("║ 2. Gestionar Libros                 ║");
            System.out.println("║ 0. Salir de la Aplicación           ║");
            System.out.println("╚═════════════════════════════════════╝");
            opcionSeleccionada = leerOpcionDelMenu("Seleccione una opción del menú: ");

            switch (opcionSeleccionada) {
                case 1:
                    mostrarMenuUsuarios();
                    break;
                case 2:
                    mostrarMenuLibros();
                    break;
                case 0:
                    System.out.println("Gracias por usar el Gestor de Biblioteca Digital. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción no reconocida. Por favor, intente de nuevo.");
            }
        } while (opcionSeleccionada != 0);
    }

    private static void mostrarMenuUsuarios() {
        int opcionSeleccionada;
        do {
            System.out.println("\n--- Gestión de Usuarios ---");
            System.out.println("1. Registrar Nuevo Usuario");
            System.out.println("2. Listar Todos los Usuarios");
            System.out.println("3. Consultar Usuario por ID");
            System.out.println("4. Modificar Datos de Usuario");
            System.out.println("5. Eliminar Usuario (¡CUIDADO! También elimina sus libros)");
            System.out.println("0. Volver al Menú Principal");
            opcionSeleccionada = leerOpcionDelMenu("Seleccione una opción para usuarios: ");

            switch (opcionSeleccionada) {
                case 1: registrarNuevoUsuario(); break;
                case 2: listarTodosLosUsuarios(); break;
                case 3: consultarUsuarioPorId(); break;
                case 4: modificarDatosDeUsuario(); break;
                case 5: eliminarUsuarioConLibros(); break;
                case 0: break; 
                default: System.out.println("Opción de usuario no válida.");
            }
        } while (opcionSeleccionada != 0);
    }

    private static void registrarNuevoUsuario() {
        try {
            System.out.println("\n--- Registrar Nuevo Usuario ---");
            String nombre = leerTexto("Nombre completo del usuario: ", false);
            String email = leerTexto("Correo electrónico del usuario: ", false);
            Usuario nuevoUsuario = new Usuario(0, nombre, email);
            daoUsuario.crear(nuevoUsuario);
            if (nuevoUsuario.getId() > 0) {
                 System.out.println("✅ Usuario registrado exitosamente con ID: " + nuevoUsuario.getId());
            } else {
                 System.out.println("⚠️  El usuario fue registrado, pero no se pudo confirmar el ID (revise logs).");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar el nuevo usuario: " + e.getMessage());
            logger.warn("Fallo al registrar usuario: {}", e.getMessage());
        }
    }

    private static void listarTodosLosUsuarios() {
        try {
            System.out.println("\n--- Listado de Todos los Usuarios ---");
            List<Usuario> listaDeUsuarios = daoUsuario.listarTodos();
            if (listaDeUsuarios.isEmpty()) {
                System.out.println("ℹ️ No hay usuarios registrados en el sistema.");
            } else {
                for (Usuario u : listaDeUsuarios) {
                    System.out.println(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener el listado de usuarios: " + e.getMessage());
            logger.warn("Fallo al listar usuarios.", e);
        }
    }

    private static void consultarUsuarioPorId() {
        try {
            System.out.println("\n--- Consultar Usuario por ID ---");
            int idUsuario = leerId("usuario", "consultar");
            Optional<Usuario> optUsuario = daoUsuario.buscarPorId(idUsuario);
            if (optUsuario.isPresent()) {
                System.out.println("ℹ️ Usuario encontrado: " + optUsuario.get());
            } else {
                System.out.println("⚠️ No se encontró ningún usuario con el ID: " + idUsuario);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar el usuario: " + e.getMessage());
            logger.warn("Fallo al buscar usuario por ID.", e);
        }
    }

    private static void modificarDatosDeUsuario() {
        try {
            System.out.println("\n--- Modificar Datos de Usuario ---");
            int idUsuario = leerId("usuario", "modificar");
            Optional<Usuario> optUsuario = daoUsuario.buscarPorId(idUsuario);
            if (optUsuario.isEmpty()) {
                System.out.println("⚠️ No se encontró ningún usuario con el ID: " + idUsuario + ". No se puede modificar.");
                return;
            }
            
            Usuario usuarioAModificar = optUsuario.get();
            System.out.println("Datos actuales del usuario: " + usuarioAModificar);
            String nuevoNombre = leerTexto("Nuevo nombre (deje vacío si no desea cambiar el actual: '" + usuarioAModificar.getNombre() + "'): ", true);
            String nuevoEmail = leerTexto("Nuevo email (deje vacío si no desea cambiar el actual: '" + usuarioAModificar.getEmail() + "'): ", true);

            boolean seRealizaronCambios = false;
            if (!nuevoNombre.isEmpty()) {
                usuarioAModificar.setNombre(nuevoNombre);
                seRealizaronCambios = true;
            }
            if (!nuevoEmail.isEmpty()) {
                usuarioAModificar.setEmail(nuevoEmail);
                seRealizaronCambios = true;
            }
            
            if (seRealizaronCambios) {
                if (daoUsuario.actualizar(usuarioAModificar)) {
                    System.out.println("✅ Usuario actualizado correctamente.");
                } else {
                    System.out.println("⚠️ No se pudo actualizar el usuario. Verifique si el ID aún existe o si el email ya está en uso.");
                }
            } else {
                System.out.println("ℹ️ No se especificaron cambios para el usuario.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al modificar el usuario: " + e.getMessage());
            logger.warn("Fallo al actualizar usuario: {}", e.getMessage());
        }
    }

    private static void eliminarUsuarioConLibros() {
        try {
            System.out.println("\n--- Eliminar Usuario ---");
            int idUsuario = leerId("usuario", "eliminar");
            Optional<Usuario> optUsuario = daoUsuario.buscarPorId(idUsuario);
            if (optUsuario.isEmpty()) {
                System.out.println("⚠️ No se encontró ningún usuario con el ID: " + idUsuario + ".");
                return;
            }
            
            System.out.println("ATENCIÓN: Se eliminará el usuario: " + optUsuario.get());
            System.out.println("Esto también eliminará TODOS los libros asociados a este usuario debido a la configuración 'ON DELETE CASCADE'.");
            String confirmacion = leerTexto("¿Está completamente seguro de que desea eliminar este usuario y sus libros? (escriba 'si' para confirmar): ", false);
            
            if (confirmacion.equalsIgnoreCase("si")) {
                if (daoUsuario.eliminar(idUsuario)) {
                    System.out.println("✅ Usuario y sus libros asociados han sido eliminados exitosamente.");
                } else {
                    System.out.println("⚠️ No se pudo eliminar el usuario (podría haber sido eliminado por otra operación).");
                }
            } else {
                System.out.println("ℹ️ Eliminación del usuario cancelada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar el usuario: " + e.getMessage());
            logger.warn("Fallo al eliminar usuario.", e);
        }
    }

    private static void mostrarMenuLibros() {
        int opcionSeleccionada;
        do {
            System.out.println("\n--- Gestión de Libros ---");
            System.out.println("1. Registrar Nuevo Libro");
            System.out.println("2. Listar Todos los Libros");
            System.out.println("3. Consultar Libro por ID");
            System.out.println("4. Listar Libros de un Usuario Específico");
            System.out.println("5. Modificar Datos de Libro");
            System.out.println("6. Eliminar Libro");
            System.out.println("0. Volver al Menú Principal");
            opcionSeleccionada = leerOpcionDelMenu("Seleccione una opción para libros: ");

            switch (opcionSeleccionada) {
                case 1: registrarNuevoLibro(); break;
                case 2: listarTodosLosLibros(); break;
                case 3: consultarLibroPorId(); break;
                case 4: listarLibrosDeUnUsuario(); break;
                case 5: modificarDatosDeLibro(); break;
                case 6: eliminarLibroPorId(); break;
                case 0: break; 
                default: System.out.println("Opción de libro no válida.");
            }
        } while (opcionSeleccionada != 0);
    }

    private static void registrarNuevoLibro() {
        try {
            System.out.println("\n--- Registrar Nuevo Libro ---");
            String titulo = leerTexto("Título del libro: ", false);
            String autor = leerTexto("Autor del libro: ", false);

            System.out.println("Usuarios disponibles para asignar el libro:");
            listarTodosLosUsuarios(); 
            List<Usuario> usuarios = daoUsuario.listarTodos(); 
            if (usuarios.isEmpty()) {
                System.out.println("⚠️ No hay usuarios registrados. Debe registrar un usuario antes de añadir un libro.");
                return;
            }

            int idDelUsuario = leerId("usuario", "asignar al libro");
            if (daoUsuario.buscarPorId(idDelUsuario).isEmpty()) {
                System.out.println("⚠️ El usuario con ID " + idDelUsuario + " no existe. No se puede registrar el libro.");
                return;
            }

            Libro nuevoLibro = new Libro(0, titulo, autor, idDelUsuario);
            daoLibro.crear(nuevoLibro);
            if (nuevoLibro.getId() > 0) {
                System.out.println("✅ Libro registrado exitosamente con ID: " + nuevoLibro.getId());
            } else {
                 System.out.println("⚠️  El libro fue registrado, pero no se pudo confirmar el ID (revise logs).");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar el nuevo libro: " + e.getMessage());
            logger.warn("Fallo al registrar libro: {}", e.getMessage());
        }
    }

    private static void listarTodosLosLibros() {
        try {
            System.out.println("\n--- Listado de Todos los Libros ---");
            List<Libro> listaDeLibros = daoLibro.listarTodos();
            if (listaDeLibros.isEmpty()) {
                System.out.println("ℹ️ No hay libros registrados en el sistema.");
            } else {
                for (Libro l : listaDeLibros) {
                    Optional<Usuario> dueno = daoUsuario.buscarPorId(l.getIdUsuario());
                    String nombreDueno = dueno.map(Usuario::getNombre).orElse("Desconocido (ID Usuario: " + l.getIdUsuario() + ")");
                    System.out.println(l + " - Dueño: " + nombreDueno);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener el listado de libros: " + e.getMessage());
            logger.warn("Fallo al listar libros.", e);
        }
    }

    private static void consultarLibroPorId() {
        try {
            System.out.println("\n--- Consultar Libro por ID ---");
            int idLibro = leerId("libro", "consultar");
            Optional<Libro> optLibro = daoLibro.buscarPorId(idLibro);
            if (optLibro.isPresent()) {
                Libro libro = optLibro.get();
                Optional<Usuario> dueno = daoUsuario.buscarPorId(libro.getIdUsuario());
                String nombreDueno = dueno.map(Usuario::getNombre).orElse("Desconocido (ID Usuario: " + libro.getIdUsuario() + ")");
                System.out.println("ℹ️ Libro encontrado: " + libro + " - Dueño: " + nombreDueno);
            } else {
                System.out.println("⚠️ No se encontró ningún libro con el ID: " + idLibro);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar el libro: " + e.getMessage());
            logger.warn("Fallo al buscar libro por ID.", e);
        }
    }

    private static void listarLibrosDeUnUsuario() {
        try {
            System.out.println("\n--- Listar Libros de un Usuario Específico ---");
            int idDelUsuario = leerId("usuario", "cuyos libros desea listar");
            Optional<Usuario> optUsuario = daoUsuario.buscarPorId(idDelUsuario);
            if (optUsuario.isEmpty()) {
                System.out.println("⚠️ El usuario con ID " + idDelUsuario + " no existe.");
                return;
            }

            List<Libro> librosDelUsuario = daoLibro.listarPorIdUsuario(idDelUsuario);
            if (librosDelUsuario.isEmpty()) {
                System.out.println("ℹ️ El usuario '" + optUsuario.get().getNombre() + "' no tiene libros registrados.");
            } else {
                System.out.println("Libros de '" + optUsuario.get().getNombre() + "':");
                for (Libro l : librosDelUsuario) {
                    System.out.println(l);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar los libros del usuario: " + e.getMessage());
            logger.warn("Fallo al listar libros de un usuario.", e);
        }
    }

    private static void modificarDatosDeLibro() {
        try {
            System.out.println("\n--- Modificar Datos de Libro ---");
            int idLibro = leerId("libro", "modificar");
            Optional<Libro> optLibro = daoLibro.buscarPorId(idLibro);
            if (optLibro.isEmpty()) {
                System.out.println("⚠️ No se encontró ningún libro con el ID: " + idLibro + ". No se puede modificar.");
                return;
            }

            Libro libroAModificar = optLibro.get();
            System.out.println("Datos actuales del libro: " + libroAModificar);
            String nuevoTitulo = leerTexto("Nuevo título (deje vacío si no desea cambiar el actual: '" + libroAModificar.getTitulo() + "'): ", true);
            String nuevoAutor = leerTexto("Nuevo autor (deje vacío si no desea cambiar el actual: '" + libroAModificar.getAutor() + "'): ", true);
            
            System.out.println("ID de Usuario actual del libro: " + libroAModificar.getIdUsuario());
            listarTodosLosUsuarios(); 
            String nuevoIdUsuarioStr = leerTexto("Nuevo ID de Usuario para el libro (deje vacío si no desea cambiar): ", true);

            boolean seRealizaronCambios = false;
            if (!nuevoTitulo.isEmpty()) {
                libroAModificar.setTitulo(nuevoTitulo);
                seRealizaronCambios = true;
            }
            if (!nuevoAutor.isEmpty()) {
                libroAModificar.setAutor(nuevoAutor);
                seRealizaronCambios = true;
            }
            if (!nuevoIdUsuarioStr.isEmpty()) {
                try {
                    int nuevoIdDueno = Integer.parseInt(nuevoIdUsuarioStr);
                    if (daoUsuario.buscarPorId(nuevoIdDueno).isPresent()) { 
                        libroAModificar.setIdUsuario(nuevoIdDueno);
                        seRealizaronCambios = true;
                    } else {
                        System.out.println("⚠️ El ID de usuario " + nuevoIdDueno + " no existe. No se cambiará el dueño del libro.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ ID de usuario ingresado no es un número válido. No se cambiará el dueño.");
                }
            }

            if (seRealizaronCambios) {
                if (daoLibro.actualizar(libroAModificar)) {
                    System.out.println("✅ Libro actualizado correctamente.");
                } else {
                    System.out.println("⚠️ No se pudo actualizar el libro. Verifique si el ID aún existe o si el ID de usuario es válido.");
                }
            } else {
                System.out.println("ℹ️ No se especificaron cambios para el libro.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al modificar el libro: " + e.getMessage());
            logger.warn("Fallo al actualizar libro: {}", e.getMessage());
        }
    }

    private static void eliminarLibroPorId() {
        try {
            System.out.println("\n--- Eliminar Libro ---");
            int idLibro = leerId("libro", "eliminar");
            Optional<Libro> optLibro = daoLibro.buscarPorId(idLibro);
            if (optLibro.isEmpty()) {
                System.out.println("⚠️ No se encontró ningún libro con el ID: " + idLibro + ".");
                return;
            }
            
            System.out.println("Se eliminará el libro: " + optLibro.get());
            String confirmacion = leerTexto("¿Está seguro de que desea eliminar este libro? (escriba 'si' para confirmar): ", false);

            if (confirmacion.equalsIgnoreCase("si")) {
                if (daoLibro.eliminar(idLibro)) {
                    System.out.println("✅ Libro eliminado exitosamente.");
                } else {
                     System.out.println("⚠️ No se pudo eliminar el libro (podría haber sido eliminado por otra operación).");
                }
            } else {
                System.out.println("ℹ️ Eliminación del libro cancelada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar el libro: " + e.getMessage());
            logger.warn("Fallo al eliminar libro.", e);
        }
    }
}