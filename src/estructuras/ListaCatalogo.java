package estructuras;
import modelos.Libro;

/**
 * Lista enlazada simple utilizada para almacenar el historial (Catálogo) 
 * de todos los libros en el orden cronológico en que fueron ingresados.
 * Permite recorridos O(N) para la generación de reportes y algoritmos de ordenamiento.
 */
public class ListaCatalogo {
    /** Puntero al inicio de la lista. */
    public ElementoLista cabeza;
    
    /** Contador del número total de elementos almacenados actualmente. */
    public int tamaño;

    /**
     * Constructor por defecto. Inicializa una lista vacía.
     */
    public ListaCatalogo() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    /**
     * Inserta un nuevo libro al final de la lista.
     * Complejidad O(N).
     *
     * @param libro El objeto libro a insertar.
     */
    public void agregar(Libro libro) {
        ElementoLista nuevo = new ElementoLista(libro);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            ElementoLista actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamaño++;
    }

    /**
     * Elimina la primera aparición de un libro dado su código.
     * Reorganiza los punteros para no romper la cadena.
     *
     * @param codigo Código del libro a eliminar.
     * @return true si el libro fue encontrado y eliminado, false en caso contrario.
     */
    public boolean eliminar(String codigo) {
        if (cabeza == null) return false;
        
        if (cabeza.libro.codigo.equals(codigo)) {
            cabeza = cabeza.siguiente;
            tamaño--;
            return true;
        }
        
        ElementoLista actual = cabeza;
        while (actual.siguiente != null && !actual.siguiente.libro.codigo.equals(codigo)) {
            actual = actual.siguiente;
        }
        
        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            tamaño--;
            return true;
        }
        return false;
    }
    
    /**
     * Recupera un libro de la lista utilizando un índice basado en 0.
     * Símil a un acceso de array, útil para matrices y tablas.
     *
     * @param indice La posición del elemento (0-indexed).
     * @return El libro en el índice especificado, o null si está fuera de rango.
     */
    public Libro obtener(int indice) {
        ElementoLista actual = cabeza;
        int contador = 0;
        while(actual != null) {
            if (contador == indice) return actual.libro;
            contador++;
            actual = actual.siguiente;
        }
        return null;
    }
}
