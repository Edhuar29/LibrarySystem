package estructuras;
import modelos.Libro;

public class ListaCatalogo {
    public ElementoLista cabeza;
    public int tamaño;

    public ListaCatalogo() {
        this.cabeza = null;
        this.tamaño = 0;
    }

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
    
    public Libro obtener(int indice) {
        ElementoLista actual = cabeza;
        int cont = 0;
        while(actual != null) {
            if (cont == indice) return actual.libro;
            cont++;
            actual = actual.siguiente;
        }
        return null;
    }
}
