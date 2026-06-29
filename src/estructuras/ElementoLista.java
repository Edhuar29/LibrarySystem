package estructuras;
import modelos.Libro;

public class ElementoLista {
    public Libro libro;
    public String accionBitacora; 
    public ElementoLista siguiente;

    public ElementoLista(Libro libro) {
        this.libro = libro;
        this.siguiente = null;
    }

    public ElementoLista(String accionBitacora) {
        this.accionBitacora = accionBitacora;
        this.siguiente = null;
    }
}
