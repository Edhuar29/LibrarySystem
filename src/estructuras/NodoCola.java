package estructuras;
import modelos.SolicitudPrestamo;

/**
 * Nodo que representa un elemento dentro de la Cola Dinámica (FIFO).
 * Contiene la solicitud de préstamo de un estudiante y un puntero al siguiente nodo.
 */
public class NodoCola {
    /** La entidad que almacena los detalles de la solicitud de préstamo. */
    public SolicitudPrestamo solicitud;
    
    /** Puntero o referencia al siguiente nodo en la fila de espera. */
    public NodoCola siguiente;

    /**
     * Constructor para instanciar un nuevo nodo en la cola.
     * Al crearse, se asume que estará al final, por lo que el siguiente es nulo.
     *
     * @param solicitud La solicitud de préstamo que ocupará esta posición.
     */
    public NodoCola(SolicitudPrestamo solicitud) {
        this.solicitud = solicitud;
        this.siguiente = null;
    }
}
