package estructuras;
import modelos.SolicitudPrestamo;

public class ColaDinamica {
    public NodoCola frente;
    public NodoCola finalCola;

    public ColaDinamica() {
        this.frente = null;
        this.finalCola = null;
    }

    public void encolar(SolicitudPrestamo solicitud) {
        NodoCola nuevoNodo = new NodoCola(solicitud);
        if (this.finalCola == null) {
            this.frente = this.finalCola = nuevoNodo;
            return;
        }
        this.finalCola.siguiente = nuevoNodo;
        this.finalCola = nuevoNodo;
    }

    public SolicitudPrestamo desencolar() {
        if (this.frente == null) return null;
        
        NodoCola nodoExtraido = this.frente;
        this.frente = this.frente.siguiente;
        
        if (this.frente == null) {
            this.finalCola = null;
        }
        return nodoExtraido.solicitud;
    }

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
