# Sistema de Gestión de Biblioteca 

Un sistema de gestión de biblioteca desarrollado en Java utilizando programación orientada a objetos (POO), interfaces gráficas (Swing/AWT) y estructuras de datos dinámicas personalizadas.

## Características Principales 

*   **Interfaz Gráfica de Usuario (GUI):** Diseñada con Java Swing para una experiencia amigable e intuitiva.
*   **Gestión de Inventario (Árbol AVL):** Búsqueda ultrarrápida y prevención de libros duplicados usando un Árbol Binario de Búsqueda Balanceado (AVL).
*   **Catálogo Histórico (Lista Enlazada):** Registro en orden de todos los libros ingresados históricamente al sistema.
*   **Sistema de Préstamos (Cola Dinámica):** Gestión de turnos de préstamos mediante estructuras FIFO (First In, First Out).
*   **Bitácora de Eventos (Pila Dinámica):** Registro de auditoría para rastrear quién atendió qué solicitud y a qué hora.
*   **Persistencia de Datos:** Guardado y carga automática desde archivos de texto plano (`base_datos.txt`) para no perder la información al cerrar el programa.
*   **Validaciones Estrictas:** Control de datos mediante Expresiones Regulares (Regex) para evitar el ingreso de datos erróneos (ej: cédulas incompletas, años inválidos).

## Estructuras de Datos Utilizadas 

El proyecto fue construido desde cero sin usar las librerías estándar de colecciones de Java (`java.util.List`, `java.util.Queue`, etc.), implementando las siguientes estructuras propias:

*   `ArbolAVL`: Para optimizar el tiempo de búsqueda de libros por su código.
*   `ColaDinamica`: Para manejar equitativamente el orden de llegada de los estudiantes solicitantes.
*   `ListaCatalogo`: Para almacenar libros.
*   `ListaBitacora`: Para el historial de préstamos.

## Requisitos del Sistema 

*   Java Development Kit (JDK) 9 o superior.
*   Eclipse IDE (o cualquier otro IDE compatible con Java).

## Cómo Ejecutar el Proyecto ️

1. Clona este repositorio en tu máquina local.
2. Abre tu IDE (Eclipse recomendado) e importa el proyecto.
3. Si estás en Eclipse, haz clic derecho en el proyecto -> **Refresh**.
4. Dirígete a la ruta `src/interfaz/Main.java`.
5. Ejecuta la clase `Main.java` como una *Java Application*.


