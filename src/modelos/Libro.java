package modelos;

public class Libro {
    public String codigo;
    public String titulo;
    public String autor;
    public int anioPublicacion;
    public String materia; 
    public String estado; 

    public Libro(String codigo, String titulo, String autor, int anioPublicacion, String materia) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.materia = materia;
        this.estado = "Disponible"; 
    }

    public String toString() {
        return "Código: " + codigo + " | Título: " + titulo + " | Autor: " + autor + " | Año: " + anioPublicacion + " | Materia: " + materia + " | Estado: " + estado;
    }
}
