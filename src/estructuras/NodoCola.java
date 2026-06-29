package estructuras;
import modelos.SolicitudPrestamo;

public class NodoCola {
    public SolicitudPrestamo solicitud;
    public NodoCola siguiente;

    public NodoCola(SolicitudPrestamo solicitud) {
        this.solicitud = solicitud;
        this.siguiente = null;
    }
}
