package estructuras;
import modelos.Libro;

/**
 * Nodo de una Lista Enlazada Simple.
 * Debido a que se usa tanto para el Catálogo (guardar libros) como para la 
 * Bitácora (guardar textos), posee constructores sobrecargados.
 */
public class ElementoLista {
    /** Instancia del libro si este nodo se usa en el Catálogo Histórico. */
    public Libro libro;
    
    /** Cadena de texto del evento si este nodo se usa en la Bitácora. */
    public String accionBitacora; 
    
    /** Puntero al siguiente elemento de la lista. */
    public ElementoLista siguiente;

    /**
     * Constructor para nodos de Catálogo.
     *
     * @param libro El libro a registrar.
     */
    public ElementoLista(Libro libro) {
        this.libro = libro;
        this.siguiente = null;
    }

    /**
     * Constructor para nodos de Bitácora.
     *
     * @param accionBitacora El texto que describe la transacción efectuada.
     */
    public ElementoLista(String accionBitacora) {
        this.accionBitacora = accionBitacora;
        this.siguiente = null;
    }
}
