package modelo;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private int idUsuario; 

    public Libro() {
    }

    public Libro(int id, String titulo, String autor, int idUsuario) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Libro [ID=" + id + ", Título='" + titulo + "', Autor='" + autor + "', ID Usuario=" + idUsuario + "]";
    }
}