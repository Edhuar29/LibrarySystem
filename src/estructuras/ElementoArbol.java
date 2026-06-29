package estructuras;
import modelos.Libro;

/**
 * Nodo fundamental para la construcción del Árbol Binario de Búsqueda (AVL).
 * Almacena un libro y las referencias a sus nodos hijos (izquierdo y derecho).
 */
public class ElementoArbol {
    /** La entidad de datos (Libro) almacenada en este nodo. */
    public Libro libro;
    
    /** Referencia al hijo izquierdo en la estructura arbórea. */
    public ElementoArbol izquierdo;
    
    /** Referencia al hijo derecho en la estructura arbórea. */
    public ElementoArbol derecho;
    
    /** 
     * Altura actual del nodo, utilizada para calcular el factor de equilibrio
     * y mantener el árbol AVL balanceado.
     */
    public int altura; 

    /**
     * Constructor para inicializar un nuevo nodo hoja.
     *
     * @param libro El libro que será insertado en el árbol.
     */
    public ElementoArbol(Libro libro) {
        this.libro = libro;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1;
    }
}
