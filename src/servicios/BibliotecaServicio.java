
package servicios;

import modelos.Libro;
import modelos.SolicitudPrestamo;
import estructuras.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servicio central del sistema (Controlador).
 * Orquesta la interacción entre las estructuras de datos (Árbol, Cola, Listas)
 * y la persistencia física en archivos de texto.
 */
public class BibliotecaServicio {
    /** Estructura lineal para recorrer el inventario. */
    public ListaCatalogo catalogo;   
    
    /** Historial de transacciones de préstamos. */
    public ListaBitacora bitacora;   
    
    /** Motor de búsqueda e índice principal O(log n). */
    public ArbolAVL indiceArbol;           
    
    /** Fila de espera de estudiantes. */
    public ColaDinamica colaPrestamos;     
    
    /** Ruta relativa o absoluta del archivo de base de datos. */
    private String rutaBaseDatosActual = "base_datos.txt"; 

    private Libro[] catálogoOrdenadoTemporal;

    private String[] rankingCodigos = new String[100];
    private int[] rankingConteos = new int[100];
    private int totalRankings = 0;

    public BibliotecaServicio() {
        this.catalogo = new ListaCatalogo();
        this.bitacora = new ListaBitacora();
        this.indiceArbol = new ArbolAVL();
        this.colaPrestamos = new ColaDinamica();
        this.catálogoOrdenadoTemporal = new Libro[0];
    }

    public boolean registrarLibro(Libro libro) {
        if (indiceArbol.buscar(libro.codigo) != null) return false;

        catalogo.agregar(libro);
        indiceArbol.insertar(libro);
        reescribirBaseDatosFisica();
        registrarAccionEnBitacora("Libro registrado: " + libro.codigo + " (" + libro.titulo + ")");
        return true;
    }

    public boolean eliminarLibro(String codigo) {
        if (indiceArbol.buscar(codigo) == null) {
            return false; 
        }

        catalogo.eliminar(codigo);
        indiceArbol.eliminar(codigo);
        reescribirBaseDatosFisica();
        registrarAccionEnBitacora("Libro eliminado del catálogo: " + codigo);
        return true; 
    }

    private void reescribirBaseDatosFisica() {
        try {
            FileWriter escritorArchivo = new FileWriter(rutaBaseDatosActual, false);
            PrintWriter escritorTexto = new PrintWriter(escritorArchivo);
            ElementoLista actual = catalogo.cabeza;
            while (actual != null) {
                escritorTexto.println(actual.libro.codigo + "|" + actual.libro.titulo + "|" + actual.libro.autor + "|" + actual.libro.anioPublicacion + "|" + actual.libro.estado + "|" + actual.libro.materia);
                actual = actual.siguiente;
            }
            escritorTexto.close();
        } catch (IOException e) {
            registrarAccionEnBitacora("ERROR CRÍTICO: No se pudo guardar la base de datos.");
        }
    }

    public boolean cargarBaseDatos(File archivoSeleccionado) {
        try {
            if (archivoSeleccionado == null || !archivoSeleccionado.exists()) {
                return false;
            }
            
            this.rutaBaseDatosActual = archivoSeleccionado.getAbsolutePath();
            BufferedReader lectorArchivo = new BufferedReader(new FileReader(archivoSeleccionado));
            String lineaFila;
            
            while ((lineaFila = lectorArchivo.readLine()) != null) {
                if (lineaFila.trim().isEmpty()) continue;
                
                String[] datosLibro = lineaFila.split("\\|");
                if (datosLibro.length >= 5) { 
                    String materiaAsignada = (datosLibro.length >= 6) ? datosLibro[5] : "General";
                    Libro libroCargado = new Libro(datosLibro[0], datosLibro[1], datosLibro[2], Integer.parseInt(datosLibro[3]), materiaAsignada);
                    libroCargado.estado = datosLibro[4];
                    
                    if (indiceArbol.buscar(libroCargado.codigo) == null) {
                        catalogo.agregar(libroCargado);
                        indiceArbol.insertar(libroCargado);
                    }
                }
            }
            lectorArchivo.close();
            
            registrarAccionEnBitacora("Archivo origen cargado y fusionado correctamente: " + archivoSeleccionado.getName());
            return true;
        } catch (IOException e) {
            registrarAccionEnBitacora("ERROR CRÍTICO: No se pudo leer el archivo de base de datos.");
            return false;
        } catch (NumberFormatException e) {
            registrarAccionEnBitacora("ERROR CRÍTICO: Archivo corrupto. Formato de número inválido.");
            return false;
        } catch (Exception e) {
            registrarAccionEnBitacora("ERROR CRÍTICO: Fallo general al cargar base de datos.");
            return false;
        }
    }

    public Libro buscarLibroPorArbol(String codigo) {
        return indiceArbol.buscar(codigo);
    }

    public String obtenerEstructuraArbolTexto() {
        StringBuilder sb = new StringBuilder();
        indiceArbol.mostrarInOrder(sb);
        return sb.toString();
    }

    public void encolarSolicitudPrestamo(SolicitudPrestamo solicitud) {
        colaPrestamos.encolar(solicitud);
        registrarAccionEnBitacora("Solicitud en cola: Usuario ID " + solicitud.idEstudiante + " - Libro Cód. " + solicitud.codigoLibro);
    }

    public String atenderSiguientePrestamo() {
        SolicitudPrestamo sol = colaPrestamos.desencolar();
        if (sol == null) return "No existen solicitudes pendientes en la cola de espera.";
        
        Libro lib = indiceArbol.buscar(sol.codigoLibro);
        if (lib == null) return "El libro solicitado no se encuentra registrado.";
        
        if (lib.estado.equals("Prestado")) return "El libro seleccionado se encuentra actualmente bajo préstamo.";
        
        lib.estado = "Prestado";
        reescribirBaseDatosFisica();
        registrarEnRanking(lib.codigo);
        registrarAccionEnBitacora("Préstamo procesado: Usuario ID " + sol.idEstudiante + " - Libro Cód. " + lib.codigo);
        return "Préstamo registrado: '" + lib.titulo + "' asignado a " + sol.idEstudiante;
    }

    private void registrarEnRanking(String codigo) {
        for (int i = 0; i < totalRankings; i++) {
            if (rankingCodigos[i].equals(codigo)) {
                rankingConteos[i]++;
                return;
            }
        }
        rankingCodigos[totalRankings] = codigo;
        rankingConteos[totalRankings] = 1;
        totalRankings++;
    }

    public String obtenerTextoColaEspera() {
        return colaPrestamos.obtenerTextoCola();
    }

    public void registrarAccionEnBitacora(String accion) {
        String hora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        bitacora.agregar("[" + hora + "] " + accion);
    }

    public String obtenerTextoBitacora() {
        StringBuilder sb = new StringBuilder();
        ElementoLista actual = bitacora.cabeza;
        while (actual != null) {
            sb.append(actual.accionBitacora).append("\n");
            actual = actual.siguiente;
        }
        return sb.toString();
    }

    /**
     * Exporta todo el contenido actual de la bitácora a un archivo de texto físico.
     *
     * @param rutaDestino La ruta donde se guardará el archivo (ej. "bitacora.txt").
     * @return true si se exportó con éxito, false en caso contrario.
     */
    public boolean exportarBitacora(String rutaDestino) {
        try {
            FileWriter escritorArchivo = new FileWriter(rutaDestino, false);
            PrintWriter escritorTexto = new PrintWriter(escritorArchivo);
            ElementoLista actual = bitacora.cabeza;
            while (actual != null) {
                escritorTexto.println(actual.accionBitacora);
                actual = actual.siguiente;
            }
            escritorTexto.close();
            return true;
        } catch (IOException e) {
            registrarAccionEnBitacora("ERROR CRÍTICO: No se pudo exportar la bitácora a " + rutaDestino);
            return false;
        }
    }

    public String ordenarPorBubbleSort() {
        catálogoOrdenadoTemporal = mapearAArreglo();
        int longitud = catálogoOrdenadoTemporal.length; 
        int contadorComparaciones = 0;
        long tiempoInicio = System.nanoTime();
        
        for (int i = 0; i < longitud - 1; i++) {
            for (int j = 0; j < longitud - i - 1; j++) {
                contadorComparaciones++;
                if (catálogoOrdenadoTemporal[j].codigo.compareTo(catálogoOrdenadoTemporal[j+1].codigo) > 0) {
                    Libro variableAuxiliar = catálogoOrdenadoTemporal[j]; 
                    catálogoOrdenadoTemporal[j] = catálogoOrdenadoTemporal[j+1]; 
                    catálogoOrdenadoTemporal[j+1] = variableAuxiliar;
                }
            }
        }
        double milisegundos = (System.nanoTime() - tiempoInicio) / 1000000.0;
        String tiempoStr = String.format("%.4f", milisegundos).replace(',', '.');
        return "Método BubbleSort (por Código) Finalizado.\n- Pasos internos realizados (Comparaciones): " + contadorComparaciones + "\n- Tiempo de respuesta: " + tiempoStr + " milisegundos";
    }

    public String ordenarPorTituloBurbuja() {
        catálogoOrdenadoTemporal = mapearAArreglo();
        int longitud = catálogoOrdenadoTemporal.length; 
        int contadorComparaciones = 0;
        long tiempoInicio = System.nanoTime();
        
        for (int i = 0; i < longitud - 1; i++) {
            for (int j = 0; j < longitud - i - 1; j++) {
                contadorComparaciones++;
                if (catálogoOrdenadoTemporal[j].titulo.compareToIgnoreCase(catálogoOrdenadoTemporal[j+1].titulo) > 0) {
                    Libro variableAuxiliar = catálogoOrdenadoTemporal[j]; 
                    catálogoOrdenadoTemporal[j] = catálogoOrdenadoTemporal[j+1]; 
                    catálogoOrdenadoTemporal[j+1] = variableAuxiliar;
                }
            }
        }
        double milisegundos = (System.nanoTime() - tiempoInicio) / 1000000.0;
        String tiempoStr = String.format("%.4f", milisegundos).replace(',', '.');
        return "Método BubbleSort (por Título) Finalizado.\n- Pasos internos realizados (Comparaciones): " + contadorComparaciones + "\n- Tiempo de respuesta: " + tiempoStr + " milisegundos";
    }

    public String ordenarPorQuickSort() {
        catálogoOrdenadoTemporal = mapearAArreglo(); 
        int[] contadorComparaciones = {0};
        long tiempoInicio = System.nanoTime();
        
        ejecutarQuickSortRecursivo(catálogoOrdenadoTemporal, 0, catálogoOrdenadoTemporal.length - 1, contadorComparaciones, false);
        double milisegundos = (System.nanoTime() - tiempoInicio) / 1000000.0;
        String tiempoStr = String.format("%.4f", milisegundos).replace(',', '.');
        return "Método QuickSort (por Código) Finalizado.\n- Pasos internos realizados (Comparaciones): " + contadorComparaciones[0] + "\n- Tiempo de respuesta: " + tiempoStr + " milisegundos";
    }

    public String ordenarPorTituloQuickSort() {
        catálogoOrdenadoTemporal = mapearAArreglo(); 
        int[] contadorComparaciones = {0};
        long tiempoInicio = System.nanoTime();
        
        ejecutarQuickSortRecursivo(catálogoOrdenadoTemporal, 0, catálogoOrdenadoTemporal.length - 1, contadorComparaciones, true);
        double milisegundos = (System.nanoTime() - tiempoInicio) / 1000000.0;
        String tiempoStr = String.format("%.4f", milisegundos).replace(',', '.');
        return "Método QuickSort (por Título) Finalizado.\n- Pasos internos realizados (Comparaciones): " + contadorComparaciones[0] + "\n- Tiempo de respuesta: " + tiempoStr + " milisegundos";
    }

    private void ejecutarQuickSortRecursivo(Libro[] arr, int indiceBajo, int indiceAlto, int[] m, boolean porTitulo) {
        if (indiceBajo < indiceAlto) {
            int indicePivote = particionarQuickSort(arr, indiceBajo, indiceAlto, m, porTitulo);
            ejecutarQuickSortRecursivo(arr, indiceBajo, indicePivote - 1, m, porTitulo);
            ejecutarQuickSortRecursivo(arr, indicePivote + 1, indiceAlto, m, porTitulo);
        }
    }

    private int particionarQuickSort(Libro[] arr, int indiceBajo, int indiceAlto, int[] m, boolean porTitulo) {
        String pivote = porTitulo ? arr[indiceAlto].titulo.toLowerCase() : arr[indiceAlto].codigo; 
        int i = (indiceBajo - 1);
        for (int j = indiceBajo; j < indiceAlto; j++) {
            m[0]++;
            String compStr = porTitulo ? arr[j].titulo.toLowerCase() : arr[j].codigo;
            if (compStr.compareTo(pivote) < 0) {
                i++; 
                Libro temp = arr[i]; 
                arr[i] = arr[j]; 
                arr[j] = temp;
            }
        }
        Libro temp = arr[i + 1]; 
        arr[i + 1] = arr[indiceAlto]; 
        arr[indiceAlto] = temp;
        return i + 1;
    }

    public String ejecutarBusquedaLineal(String codigoBuscado) {
        long tiempoInicio = System.nanoTime(); 
        ElementoLista actual = catalogo.cabeza; 
        Libro libroEncontrado = null;
        
        while (actual != null) { 
            if (actual.libro.codigo.equals(codigoBuscado)) { 
                libroEncontrado = actual.libro; 
                break; 
            } 
            actual = actual.siguiente; 
        }
        double milisegundos = (System.nanoTime() - tiempoInicio) / 1000000.0;
        String tiempoStr = String.format("%.4f", milisegundos).replace(',', '.');
        return (libroEncontrado != null ? "Resultado: '" + libroEncontrado.titulo + "' Encontrado " : "Resultado: Código no registrado. ") + "\n|| Tiempo de respuesta de la Búsqueda Lineal: " + tiempoStr + " milisegundos";
    }

    public String ejecutarBusquedaBinaria(String codigoBuscado) {
        Libro[] arregloAuxiliar = mapearAArreglo(); 
        ejecutarQuickSortRecursivo(arregloAuxiliar, 0, arregloAuxiliar.length - 1, new int[]{0}, false);
        
        long tiempoInicio = System.nanoTime(); 
        int indiceIzquierdo = 0, indiceDerecho = arregloAuxiliar.length - 1; 
        Libro libroEncontrado = null;
        
        while (indiceIzquierdo <= indiceDerecho) {
            int pivoteMedio = indiceIzquierdo + (indiceDerecho - indiceIzquierdo) / 2; 
            int resultadoComparacion = codigoBuscado.compareTo(arregloAuxiliar[pivoteMedio].codigo);
            
            if (resultadoComparacion == 0) { 
                libroEncontrado = arregloAuxiliar[pivoteMedio]; 
                break; 
            } 
            if (resultadoComparacion > 0) {
                indiceIzquierdo = pivoteMedio + 1; 
            } else {
                indiceDerecho = pivoteMedio - 1;
            }
        }
        double milisegundos = (System.nanoTime() - tiempoInicio) / 1000000.0;
        String tiempoStr = String.format("%.4f", milisegundos).replace(',', '.');
        return (libroEncontrado != null ? "Resultado: '" + libroEncontrado.titulo + "' Encontrado " : "Resultado: Código no registrado. ") + "\n|| Tiempo de respuesta de la Búsqueda Binaria: " + tiempoStr + " milisegundos";
    }

    private Libro[] mapearAArreglo() {
        Libro[] arregloLibros = new Libro[catalogo.tamaño];
        ElementoLista actual = catalogo.cabeza; 
        int indice = 0;
        while (actual != null) { 
            arregloLibros[indice++] = actual.libro; 
            actual = actual.siguiente; 
        }
        return arregloLibros;
    }

    public String generarReporteListadoExistencias() { 
        StringBuilder sb = new StringBuilder("=========================================\n");
        sb.append("      INFORME GENERAL DE EXISTENCIAS      \n");
        sb.append("=========================================\n\n");
        ElementoLista actual = catalogo.cabeza;
        while (actual != null) { 
            sb.append(actual.libro.toString()).append("\n"); 
            actual = actual.siguiente; 
        }
        return sb.toString();
    } 

    public String generarReportePorAutor(String autor) { 
        StringBuilder sb = new StringBuilder("=========================================\n");
        sb.append("     REPORTE FILTRADO POR AUTOR: ").append(autor.toUpperCase()).append("\n");
        sb.append("=========================================\n\n");
        ElementoLista actual = catalogo.cabeza;
        while (actual != null) {
            if (actual.libro.autor.equalsIgnoreCase(autor)) {
                sb.append(actual.libro.toString()).append("\n");
            }
            actual = actual.siguiente;
        }
        if (sb.toString().contains("Autor: " + autor)) return sb.toString();
        else return sb.append("No se encontraron libros para este autor.\n").toString();
    } 

    public String generarReporteRankingMasPedidos() { 
        StringBuilder sb = new StringBuilder("=========================================\n");
        sb.append("      RANKING DE EJEMPLARES SOLICITADOS   \n");
        sb.append("=========================================\n\n");
        for (int i = 0; i < totalRankings; i++) {
            Libro lib = indiceArbol.buscar(rankingCodigos[i]);
            sb.append("- ").append(lib != null ? lib.titulo : "Código: " + rankingCodigos[i]).append(" -> Frecuencia: ").append(rankingConteos[i]).append(" veces.\n");
        }
        return sb.toString();
    }

    public String generarReporteRankingPorMateria() { 
        StringBuilder sb = new StringBuilder("=========================================\n");
        sb.append("      RANKING DE LIBROS POR MATERIA       \n");
        sb.append("=========================================\n\n");
        
        String[] materias = new String[100];
        int[] frecuenciasMateria = new int[100];
        int totalMaterias = 0;

        for (int i = 0; i < totalRankings; i++) {
            Libro lib = indiceArbol.buscar(rankingCodigos[i]);
            if (lib != null) {
                boolean encontrada = false;
                for (int j = 0; j < totalMaterias; j++) {
                    if (materias[j].equalsIgnoreCase(lib.materia)) {
                        frecuenciasMateria[j] += rankingConteos[i];
                        encontrada = true;
                        break;
                    }
                }
                if (!encontrada) {
                    materias[totalMaterias] = lib.materia;
                    frecuenciasMateria[totalMaterias] = rankingConteos[i];
                    totalMaterias++;
                }
            }
        }
        
        for (int i = 0; i < totalMaterias - 1; i++) {
            for (int j = 0; j < totalMaterias - i - 1; j++) {
                if (frecuenciasMateria[j] < frecuenciasMateria[j+1]) {
                    int frecuenciaTemporal = frecuenciasMateria[j];
                    frecuenciasMateria[j] = frecuenciasMateria[j+1];
                    frecuenciasMateria[j+1] = frecuenciaTemporal;
                    
                    String materiaTemporal = materias[j];
                    materias[j] = materias[j+1];
                    materias[j+1] = materiaTemporal;
                }
            }
        }

        if (totalMaterias == 0) {
            sb.append("No hay registros de préstamos todavía.\n");
        }

        for (int i = 0; i < totalMaterias; i++) {
            sb.append("- Materia: ").append(materias[i]).append(" -> Total Préstamos: ").append(frecuenciasMateria[i]).append("\n");
        }
        return sb.toString();
    }

    public String[][] obtenerMatrizCatalogo() {
        String[][] matriz = new String[catalogo.tamaño][6];
        ElementoLista actual = catalogo.cabeza; 
        int i = 0;
        while (actual != null) {
            matriz[i][0] = actual.libro.codigo; 
            matriz[i][1] = actual.libro.titulo;
            matriz[i][2] = actual.libro.autor; 
            matriz[i][3] = String.valueOf(actual.libro.anioPublicacion);
            matriz[i][4] = actual.libro.estado; 
            matriz[i][5] = actual.libro.materia; 
            i++; 
            actual = actual.siguiente;
        }
        return matriz;
    }

    private String[][] convertirArregloAMatriz(Libro[] arreglo) {
        String[][] matriz = new String[arreglo.length][6];
        for (int i = 0; i < arreglo.length; i++) {
            matriz[i][0] = arreglo[i].codigo;
            matriz[i][1] = arreglo[i].titulo;
            matriz[i][2] = arreglo[i].autor;
            matriz[i][3] = String.valueOf(arreglo[i].anioPublicacion);
            matriz[i][4] = arreglo[i].estado;
            matriz[i][5] = arreglo[i].materia;
        }
        return matriz;
    }

    public Object[][] obtenerMatrizCatalogoOrdenadoTemporal() {
        if (catálogoOrdenadoTemporal == null || catálogoOrdenadoTemporal.length == 0) {
            return obtenerMatrizCatalogo();
        }
        return convertirArregloAMatriz(catálogoOrdenadoTemporal); 
    }
}
