package estructuras;

/**
 * Lista enlazada simple adaptada para funcionar como una bitácora o log de eventos.
 * Almacena el historial de transacciones (préstamos despachados) en formato de texto.
 */
public class ListaBitacora {
    /** Puntero al primer evento de la bitácora. */
    public ElementoLista cabeza;

    /**
     * Constructor por defecto. Inicializa la bitácora vacía.
     */
    public ListaBitacora() {
        this.cabeza = null;
    }

    /**
     * Añade un nuevo registro de transacción al final de la bitácora.
     * Complejidad O(N).
     *
     * @param accion Texto descriptivo de la acción realizada (ej. préstamo completado).
     */
    public void agregar(String accion) {
        ElementoLista nuevo = new ElementoLista(accion);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            ElementoLista actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }
}
