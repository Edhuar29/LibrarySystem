package modelos;

/**
 * Entidad principal que representa un libro dentro de la biblioteca.
 * Contiene toda la información bibliográfica y su estado actual de disponibilidad.
 */
public class Libro {
    /** Código único de identificación del libro. */
    public String codigo;
    
    /** Título de la obra. */
    public String titulo;
    
    /** Autor principal de la obra. */
    public String autor;
    
    /** Año en el que fue publicado el libro. */
    public int anioPublicacion;
    
    /** Área de conocimiento o categoría (Ej. Ciencias, Literatura). */
    public String materia; 
    
    /** Estado del préstamo: "Disponible" o "Prestado". */
    public String estado; 

    /**
     * Constructor para inicializar un nuevo libro en el inventario.
     * El estado por defecto siempre será "Disponible" al momento del registro.
     *
     * @param codigo          Código único del libro.
     * @param titulo          Título del libro.
     * @param autor           Autor del libro.
     * @param anioPublicacion Año de publicación.
     * @param materia         Categoría o materia del libro.
     */
    public Libro(String codigo, String titulo, String autor, int anioPublicacion, String materia) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.materia = materia;
        this.estado = "Disponible"; 
    }

    /**
     * Retorna una representación en texto de todos los atributos del libro.
     * Útil para reportes y almacenamiento en texto plano.
     *
     * @return Cadena formateada con la información del libro.
     */
    public String toString() {
        return "Código: " + codigo + " | Título: " + titulo + " | Autor: " + autor + " | Año: " + anioPublicacion + " | Materia: " + materia + " | Estado: " + estado;
    }
}
