package estructuras;
import modelos.Libro;

/**
 * Estructura de datos: Árbol Binario de Búsqueda Balanceado (AVL).
 * Se encarga de almacenar y gestionar el inventario de libros de manera eficiente
 * manteniendo un balance automático O(log n) para búsquedas e inserciones.
 */
public class ArbolAVL {
    /** Nodo raíz principal del árbol. */
    public ElementoArbol raiz;

    /**
     * Constructor por defecto. Inicializa un árbol vacío.
     */
    public ArbolAVL() {
        this.raiz = null;
    }

    private int obtenerAltura(ElementoArbol elemento) {
        return (elemento == null) ? 0 : elemento.altura;
    }

    private int obtenerFactorBalance(ElementoArbol elemento) {
        return (elemento == null) ? 0 : obtenerAltura(elemento.izquierdo) - obtenerAltura(elemento.derecho);
    }

    /**
     * Calcula el valor máximo entre dos números enteros.
     * Utilizado para recalcular la altura de los nodos tras inserciones o rotaciones.
     *
     * @param valorA Primer valor a comparar.
     * @param valorB Segundo valor a comparar.
     * @return El número mayor entre valorA y valorB.
     */
    private int maximo(int valorA, int valorB) {
        return (valorA > valorB) ? valorA : valorB;
    }

    private ElementoArbol rotarDerecha(ElementoArbol elementoDesbalanceado) {
        ElementoArbol nuevoElementoArriba = elementoDesbalanceado.izquierdo;
        ElementoArbol subArbolDesplazado = nuevoElementoArriba.derecho;

        nuevoElementoArriba.derecho = elementoDesbalanceado;
        elementoDesbalanceado.izquierdo = subArbolDesplazado;

        elementoDesbalanceado.altura = maximo(obtenerAltura(elementoDesbalanceado.izquierdo), obtenerAltura(elementoDesbalanceado.derecho)) + 1;
        nuevoElementoArriba.altura = maximo(obtenerAltura(nuevoElementoArriba.izquierdo), obtenerAltura(nuevoElementoArriba.derecho)) + 1;

        return nuevoElementoArriba;
    }

    private ElementoArbol rotarIzquierda(ElementoArbol elementoDesbalanceado) {
        ElementoArbol nuevoElementoArriba = elementoDesbalanceado.derecho;
        ElementoArbol subArbolDesplazado = nuevoElementoArriba.izquierdo;

        nuevoElementoArriba.izquierdo = elementoDesbalanceado;
        elementoDesbalanceado.derecho = subArbolDesplazado;

        elementoDesbalanceado.altura = maximo(obtenerAltura(elementoDesbalanceado.izquierdo), obtenerAltura(elementoDesbalanceado.derecho)) + 1;
        nuevoElementoArriba.altura = maximo(obtenerAltura(nuevoElementoArriba.izquierdo), obtenerAltura(nuevoElementoArriba.derecho)) + 1;

        return nuevoElementoArriba;
    }

    /**
     * Inserta un nuevo libro en el árbol AVL de forma ordenada según su código.
     * Posteriormente, se encarga de rebalancear el árbol si la inserción causa un desequilibrio.
     *
     * @param libro El libro instanciado a insertar.
     */
    public void insertar(Libro libro) {
        this.raiz = insertarRecursivo(this.raiz, libro);
    }

    private ElementoArbol insertarRecursivo(ElementoArbol elementoActual, Libro libro) {
        if (elementoActual == null) return new ElementoArbol(libro);
        
        if (libro.codigo.compareTo(elementoActual.libro.codigo) < 0) {
            elementoActual.izquierdo = insertarRecursivo(elementoActual.izquierdo, libro);
        } else if (libro.codigo.compareTo(elementoActual.libro.codigo) > 0) {
            elementoActual.derecho = insertarRecursivo(elementoActual.derecho, libro);
        } else {
            return elementoActual; 
        }

        elementoActual.altura = 1 + maximo(obtenerAltura(elementoActual.izquierdo), obtenerAltura(elementoActual.derecho));
        int balance = obtenerFactorBalance(elementoActual);

        if (balance > 1 && libro.codigo.compareTo(elementoActual.izquierdo.libro.codigo) < 0) {
            return rotarDerecha(elementoActual);
        }
        if (balance < -1 && libro.codigo.compareTo(elementoActual.derecho.libro.codigo) > 0) {
            return rotarIzquierda(elementoActual);
        }
        if (balance > 1 && libro.codigo.compareTo(elementoActual.izquierdo.libro.codigo) > 0) {
            elementoActual.izquierdo = rotarIzquierda(elementoActual.izquierdo);
            return rotarDerecha(elementoActual);
        }
        if (balance < -1 && libro.codigo.compareTo(elementoActual.derecho.libro.codigo) < 0) {
            elementoActual.derecho = rotarDerecha(elementoActual.derecho);
            return rotarIzquierda(elementoActual);
        }
        
        return elementoActual;
    }

    /**
     * Elimina un libro del árbol utilizando su código como clave de búsqueda.
     * Garantiza que el árbol se reestructure y mantenga sus propiedades AVL.
     *
     * @param codigo El código único del libro a eliminar.
     */
    public void eliminar(String codigo) {
        this.raiz = eliminarRecursivo(this.raiz, codigo);
    }

    private ElementoArbol eliminarRecursivo(ElementoArbol elementoActual, String codigo) {
        if (elementoActual == null) return elementoActual;

        if (codigo.compareTo(elementoActual.libro.codigo) < 0) {
            elementoActual.izquierdo = eliminarRecursivo(elementoActual.izquierdo, codigo);
        } else if (codigo.compareTo(elementoActual.libro.codigo) > 0) {
            elementoActual.derecho = eliminarRecursivo(elementoActual.derecho, codigo);
        } else {
            if ((elementoActual.izquierdo == null) || (elementoActual.derecho == null)) {
                ElementoArbol elementoTemporal = (elementoActual.izquierdo != null) ? elementoActual.izquierdo : elementoActual.derecho;
                if (elementoTemporal == null) {
                    elementoTemporal = elementoActual;
                    elementoActual = null;
                } else {
                    elementoActual = elementoTemporal;
                }
            } else {
                ElementoArbol elementoMinimoDerecho = obtenerValorMinimo(elementoActual.derecho);
                elementoActual.libro = elementoMinimoDerecho.libro;
                elementoActual.derecho = eliminarRecursivo(elementoActual.derecho, elementoMinimoDerecho.libro.codigo);
            }
        }

        if (elementoActual == null) return elementoActual;

        elementoActual.altura = maximo(obtenerAltura(elementoActual.izquierdo), obtenerAltura(elementoActual.derecho)) + 1;
        int balance = obtenerFactorBalance(elementoActual);

        if (balance > 1 && obtenerFactorBalance(elementoActual.izquierdo) >= 0) return rotarDerecha(elementoActual);
        if (balance > 1 && obtenerFactorBalance(elementoActual.izquierdo) < 0) {
            elementoActual.izquierdo = rotarIzquierda(elementoActual.izquierdo);
            return rotarDerecha(elementoActual);
        }
        if (balance < -1 && obtenerFactorBalance(elementoActual.derecho) <= 0) return rotarIzquierda(elementoActual);
        if (balance < -1 && obtenerFactorBalance(elementoActual.derecho) > 0) {
            elementoActual.derecho = rotarDerecha(elementoActual.derecho);
            return rotarIzquierda(elementoActual);
        }
        
        return elementoActual;
    }

    private ElementoArbol obtenerValorMinimo(ElementoArbol elementoRaizBlock) {
        ElementoArbol auxiliarActual = elementoRaizBlock;
        while (auxiliarActual.izquierdo != null) {
            auxiliarActual = auxiliarActual.izquierdo;
        }
        return auxiliarActual;
    }

    /**
     * Busca un libro dentro del árbol utilizando una búsqueda binaria eficiente O(log n).
     *
     * @param codigo El código del libro a buscar.
     * @return El objeto Libro si es encontrado, o null en caso contrario.
     */
    public Libro buscar(String codigo) {
        return buscarRecursivo(this.raiz, codigo);
    }

    private Libro buscarRecursivo(ElementoArbol elementoActual, String codigo) {
        if (elementoActual == null) return null;
        if (codigo.equals(elementoActual.libro.codigo)) return elementoActual.libro;
        
        if (codigo.compareTo(elementoActual.libro.codigo) < 0) {
            return buscarRecursivo(elementoActual.izquierdo, codigo);
        }
        return buscarRecursivo(elementoActual.derecho, codigo);
    }

    public void mostrarInOrder(StringBuilder stringBuilderContenedor) {
        mostrarInOrderRecursivo(this.raiz, stringBuilderContenedor);
    }

    private void mostrarInOrderRecursivo(ElementoArbol elementoActual, StringBuilder stringBuilderContenedor) {
        if (elementoActual != null) {
            mostrarInOrderRecursivo(elementoActual.izquierdo, stringBuilderContenedor);
            stringBuilderContenedor.append(elementoActual.libro.toString()).append("\n");
            mostrarInOrderRecursivo(elementoActual.derecho, stringBuilderContenedor);
        }
    }
}
