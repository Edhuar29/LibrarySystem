# Sistema Avanzado de Gestion de Biblioteca

Un sistema de gestion de bibliotecas de alto rendimiento, desarrollado completamente en Java utilizando Programacion Orientada a Objetos (POO), interfaces graficas nativas (Swing/AWT) y la implementacion de Estructuras de Datos Dinamicas personalizadas desde cero.

Este proyecto ha sido disenado con un enfoque estricto en la eficiencia algoritmica, la integridad referencial de los datos y la escalabilidad, garantizando tiempos de respuesta minimos en las operaciones de busqueda, asi como un control riguroso de concurrencia y validacion de entradas.

---

## 1. Arquitectura del Sistema

El sistema sigue un patron arquitectonico modular que separa claramente la logica de presentacion de la logica de negocio y el almacenamiento de datos. Esta estructurado en los siguientes paquetes principales:

*   **modelos:** Contiene las clases de dominio puro (`Libro`, `SolicitudPrestamo`) que representan las entidades fundamentales del sistema y sus atributos.
*   **estructuras:** Alberga todas las implementaciones de estructuras de datos dinamicas creadas desde cero (`ArbolAVL`, `ColaDinamica`, `ListaCatalogo`, `ListaBitacora`), incluyendo sus respectivos nodos. No se hace uso del framework de colecciones estandar de Java (`java.util.*`), demostrando un control total sobre la gestion de memoria y los algoritmos subyacentes.
*   **servicios:** Contiene `BibliotecaServicio`, que actua como el orquestador principal (controlador/servicio). Es el intermediario que procesa las peticiones de la interfaz y delega el trabajo pesado a las estructuras de datos, ademas de manejar la lectura y escritura en el disco duro.
*   **interfaz:** Gestiona la capa visual (`VentanaPrincipal`, `Main`) construida sobre Java Swing, reaccionando a los eventos del usuario y renderizando los datos entregados por los servicios.

---

## 2. Detalle de Estructuras de Datos Implementadas

Para garantizar un rendimiento optimo segun el tipo de operacion a realizar, el sistema emplea multiples estructuras de datos especializadas:

### 2.1 Arbol Binario de Busqueda Balanceado (AVL)
*   **Clase:** `ArbolAVL` y `ElementoArbol`.
*   **Proposito:** Almacenamiento primario y motor de busqueda del inventario de libros.
*   **Implementacion:** Se utiliza el "Codigo del Libro" como clave (key) para la indexacion. Tras cada insercion, el algoritmo evalua el factor de equilibrio de los nodos. Si el factor excede el umbral permitido (-1, 0, 1), el arbol ejecuta de manera autonoma rotaciones simples o dobles (Izquierda-Izquierda, Derecha-Derecha, Izquierda-Derecha o Derecha-Izquierda) para mantener su altura logaritmica.
*   **Complejidad:** Garantiza un tiempo de ejecucion en el peor de los casos de O(log n) para busquedas, inserciones y validacion de unicidad (evitando codigos duplicados).

### 2.2 Cola Dinamica (FIFO - First In, First Out)
*   **Clase:** `ColaDinamica` y `NodoCola`.
*   **Proposito:** Gestion de la "Cola de Espera" para los prestamos de libros.
*   **Implementacion:** Estructura lineal unidireccional con punteros al "frente" y al "final". Cuando un estudiante solicita un libro, se instancia un objeto complejo `SolicitudPrestamo` que encapsula el codigo del libro, identificador del estudiante, cedula, carrera y una marca de tiempo absoluta generada por el sistema.
*   **Complejidad:** Las operaciones de encolar (enqueue) y desencolar (dequeue) se realizan en un tiempo constante O(1), asegurando un despacho eficiente y estrictamente cronologico.

### 2.3 Lista Enlazada Simple (Catalogo Historico)
*   **Clase:** `ListaCatalogo` y `ElementoLista`.
*   **Proposito:** Registro lineal e historico de todos los libros ingresados.
*   **Implementacion:** Cada vez que un libro supera las validaciones e ingresa al Arbol AVL, una referencia secundaria de ese libro es inyectada al final de esta lista enlazada. 
*   **Complejidad:** Permite la aplicacion de algoritmos de ordenamiento convencionales sobre iteradores lineales sin tener que aplanar el arbol AVL, lo cual es ideal para generar reportes estructurados a O(n).

### 2.4 Pila / Lista LIFO (Bitacora de Eventos)
*   **Clase:** `ListaBitacora`.
*   **Proposito:** Sistema interno de auditoria e historial de transacciones.
*   **Implementacion:** Almacena registros inmutables de los prestamos efectuados. Cuando se atiende el frente de la cola de prestamos, el registro se empuja (push) al inicio de esta bitacora, permitiendo recuperar los eventos mas recientes rapidamente de forma O(1).

---

## 3. Motor de Validacion y Reglas de Negocio

El sistema incluye una capa de seguridad y validacion estricta antes de impactar los datos en memoria o en disco, apoyada fuertemente por Expresiones Regulares (Regex):

1.  **Validacion de Cedula Ecuatoriana:** Exige el ingreso de exactamente 10 digitos numericos, bloqueando cualquier intento de ingreso de cadenas alfanumericas o longitudes incorrectas (`\\d{10}`).
2.  **Limpieza de Cadenas (Sanitizacion):** Los campos de Titulo, Autor, Materia y Carrera son estrictamente filtrados para rechazar numeros y caracteres especiales indeseados (`[a-zA-ZaeiouAEIOUnN ]+`).
3.  **Integridad de Fechas:** El ano de publicacion debe cumplir con ser un numero de cuatro digitos verificable.
4.  **Prevencion de Duplicidad Absoluta:** Antes de registrar un nuevo libro, la capa de servicio delega una busqueda O(log n) al arbol AVL. Si el codigo ya existe, la transaccion es abortada y se reporta una excepcion visual al operador, protegiendo la integridad de la llave primaria.

---

## 4. Persistencia Hibrida y Manejo de Archivos

El sistema no depende de motores de base de datos externos (SQL/NoSQL), sino que gestiona su propio almacenamiento binario-textual para maxima portabilidad.

*   **Archivo Objetivo:** `base_datos.txt`
*   **Serializacion Personalizada:** Los objetos en memoria son aplanados utilizando el caracter delimitador tubería (`|`). El esquema estandarizado es: `Codigo|Titulo|Autor|Ano|Estado|Materia`.
*   **Sincronizacion en Tiempo Real:** Cualquier mutacion de estado (creacion de un nuevo libro o sustraccion de inventario) se escribe inmediatamente en el archivo fisico.
*   **Arranque en Frio (Cold Start):** Al iniciar, el programa lee el archivo secuencialmente, hidrata los objetos `Libro` y reconstruye simultaneamente el Arbol AVL y la Lista Catalogo en memoria RAM, estando listo para operar en milisegundos.

---

## 5. Requisitos Tecnicos y Dependencias

*   **Plataforma Base:** Java Development Kit (JDK) 9 o superior.
*   **Framework Grafico:** Java Foundation Classes (JFC), especificamente Swing y AWT.
*   **Sistema de Modulos:** Se hace uso estricto del patron de modulos de Java 9+ (`module-info.java`), requiriendo de forma explicita el paquete `java.desktop` para el renderizado grafico de los componentes de la interfaz. No existen dependencias de terceros ni archivos JAR externos que deban acoplarse al classpath.
*   **Entornos de Desarrollo Soportados:** Eclipse IDE, IntelliJ IDEA, Apache NetBeans.

---

## 6. Instrucciones de Compilacion y Despliegue

1. Efectue el clonado del repositorio hacia el almacenamiento local del equipo anfitrion.
2. Proceda con la importacion del proyecto en el Entorno de Desarrollo Integrado (IDE) de preferencia seleccionando el archivo `.project` o declarando un nuevo proyecto desde codigo fuente existente.
3. Asegurese de que la carpeta `src` este mapeada de manera explicita como el *Source Folder* dentro del *Java Build Path* del entorno.
4. Debido a las posibles discrepancias en la tabla de indices del compilador local (especialmente en Eclipse), se recomienda ejecutar una purga y recompilacion total seleccionando la opcion **Project > Clean...** antes de la primera ejecucion.
5. Inicie el ciclo de ejecucion disparando la clase controladora ubicada en `src/interfaz/Main.java`.

---

## 7. Informacion Adicional del Proyecto

Este software ha sido disenado como una arquitectura de codigo cerrado orientada al cumplimiento academico y profesional en algoritmia. Se priorizo la construccion manual de los arboles e indexadores por encima del uso de bibliotecas de facil uso, demostrando solidez en logica de programacion nativa.
