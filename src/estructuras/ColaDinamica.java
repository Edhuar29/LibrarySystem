package estructuras;
import modelos.SolicitudPrestamo;

/**
 * Estructura de datos dinámica: Cola (Queue - FIFO: First In, First Out).
 * Se encarga de gestionar la fila de espera de estudiantes que desean solicitar un libro,
 * garantizando que sean atendidos en el orden exacto de llegada.
 */
public class ColaDinamica {
    /** Puntero al primer nodo de la cola (el próximo a ser atendido). */
    public NodoCola frente;
    
    /** Puntero al último nodo de la cola (el recién ingresado). */
    public NodoCola finalCola;

    /**
     * Constructor por defecto. Inicializa una cola vacía.
     */
    public ColaDinamica() {
        this.frente = null;
        this.finalCola = null;
    }

    /**
     * Inserta una nueva solicitud al final de la fila de espera.
     * Operación con complejidad O(1).
     *
     * @param solicitud La información del préstamo y estudiante a encolar.
     */
    public void encolar(SolicitudPrestamo solicitud) {
        NodoCola nuevoNodo = new NodoCola(solicitud);
        if (this.finalCola == null) {
            this.frente = this.finalCola = nuevoNodo;
            return;
        }
        this.finalCola.siguiente = nuevoNodo;
        this.finalCola = nuevoNodo;
    }

    /**
     * Extrae y retorna la solicitud que se encuentra al frente de la fila.
     * Operación con complejidad O(1).
     *
     * @return La solicitud extraída, o null si la cola estaba vacía.
     */
    public SolicitudPrestamo desencolar() {
        if (this.frente == null) return null;
        
        NodoCola nodoExtraido = this.frente;
        this.frente = this.frente.siguiente;
        
        if (this.frente == null) {
            this.finalCola = null;
        }
        return nodoExtraido.solicitud;
    }

    /**
     * Recorre la cola sin alterarla y genera una representación textual de todos los turnos.
     * Útil para actualizar la interfaz gráfica.
     *
     * @return Una cadena formateada con todos los turnos, o un mensaje si está vacía.
     */
    public String obtenerTextoCola() {
        StringBuilder stringBuilderContenedor = new StringBuilder();
        NodoCola actual = frente;
        int contadorUbicacion = 1;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        while (actual != null) {
            String horaRegistro = sdf.format(new java.util.Date(actual.solicitud.timestamp));
            stringBuilderContenedor.append(" Turno #").append(contadorUbicacion)
                                   .append("  |  Libro Cód: ").append(actual.solicitud.codigoLibro)
                                   .append("  |  Solicitante: ").append(actual.solicitud.idEstudiante)
                                   .append("  |  C.I.: ").append(actual.solicitud.cedula)
                                   .append("  |  Carrera: ").append(actual.solicitud.carrera)
                                   .append("  |  Hora: ").append(horaRegistro).append("\n");
            actual = actual.siguiente;
            contadorUbicacion++;
        }
        if (stringBuilderContenedor.length() == 0) return "La cola de préstamos está completamente vacía.";
        return stringBuilderContenedor.toString();
    }
}
