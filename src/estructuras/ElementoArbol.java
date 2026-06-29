package estructuras;
import modelos.Libro;

public class ElementoArbol {
    public Libro libro;
    public ElementoArbol izquierdo;
    public ElementoArbol derecho;
    public int altura; 

    public ElementoArbol(Libro libro) {
        this.libro = libro;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1;
    }
}
