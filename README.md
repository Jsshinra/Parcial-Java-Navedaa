# 📚 Sistema de Gestión de Biblioteca Personal

## 📌 Descripción General

Este es un proyecto hecho en **Java** para gestionar una biblioteca personal o chica, donde podés registrar usuarios (los dueños de libros) y los libros que tiene cada uno.  
Todo queda guardado gracias a una **base de datos H2 embebida**, así que no se pierde nada aunque cierres el programa.

Tiene una **interfaz por consola** interactiva y usa **Log4j2** para registrar lo que pasa internamente (errores, operaciones exitosas, etc.).  
Es un proyecto pensado para practicar **Programación Orientada a Objetos**, manejo de bases de datos con JDBC y buenas prácticas de organización del código.

---

## ✨ Funcionalidades Principales

### 👤 Gestión de Usuarios
- Alta de usuarios (nombre y correo).
- Modificación de datos (nombre o correo).
- Eliminación de usuarios (y también se eliminan los libros que tenían).
- Listado completo de usuarios.
- Búsqueda por ID.

### 📘 Gestión de Libros
- Alta de libros (título, autor, dueño).
- Modificación (título, autor o dueño).
- Eliminación individual.
- Listado completo.
- Búsqueda por ID.
- Ver todos los libros de un usuario.

---

## 🛠 Tecnologías Utilizadas

| Tecnología   | Descripción                                  |
|--------------|----------------------------------------------|
| ☕ Java 17    | Lenguaje principal del proyecto              |
| 🗄️ H2 DB      | Base de datos embebida, persistente          |
| 🔌 JDBC       | Conexión y operaciones con la base de datos |
| 🖥️ Swing      | Interfaz gráfica básica con `JOptionPane`    |
| 🧩 Gradle     | Manejo de dependencias y compilación        |
| 📋 Log4j2     | Sistema de logging (registros de acciones)  |

---

## 🗂️ Organización del Proyecto

El proyecto está dividido en paquetes para mantener todo ordenado:

- `modelo`: Clases `Usuario` y `Libro`, con sus atributos y métodos.
- `dao`: Clases DAO para manejar la lógica de acceso a datos (insertar, buscar, eliminar, etc.).
- `util`: Utilidades como la conexión a la base de datos y creación de tablas.
- `principal`: Contiene la clase `ClasePrincipal`, que inicia el programa y maneja los menús.

---

## 🚀 ¿Cómo Empezar?

### ✅ Requisitos Previos
- Tener instalado **Java JDK 17 o superior**
- Tener un **IDE** (recomendado: IntelliJ IDEA o NetBeans)
- Tener **Gradle** (opcional si no usás un IDE que lo gestione)

### 📦 Dependencias Necesarias
Estas se pueden descargar automáticamente si usás Gradle, o podés agregarlas manualmente:

- `h2-*.jar`
- `log4j-api-*.jar` y `log4j-core-*.jar`

---

## 🗃️ Sobre la Base de Datos

- Se genera automáticamente un archivo llamado `miLibreriaDigital.mv.db` en la carpeta raíz.
- No se requiere instalar ningún gestor de bases de datos externo.
- Usuario: `sa` | Contraseña: *(vacía)*
- Tablas creadas:
  - `usuarios`
  - `libros`

---

## ✍️ Autor

- **Nombre:** Juan Segura  
- **Materia:** Programación II - UTN FRM  
- **Año:** 2025
