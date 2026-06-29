package estructuras;

public class ListaBitacora {
    public ElementoLista cabeza;

    public ListaBitacora() {
        this.cabeza = null;
    }

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
