package modelos;

/**
 * Entidad que encapsula los datos de una solicitud de préstamo de libro.
 * Se utiliza para almacenar la información del estudiante en la Cola Dinámica.
 */
public class SolicitudPrestamo {
    /** Código único del libro que se desea prestar. */
    public String codigoLibro;
    
    /** Nombre o identificador del estudiante que solicita el préstamo. */
    public String idEstudiante;
    
    /** Cédula de identidad del estudiante (10 dígitos). */
    public String cedula;
    
    /** Carrera universitaria a la que pertenece el estudiante. */
    public String carrera;
    
    /** Marca de tiempo exacta (en milisegundos) en la que se generó la solicitud. */
    public long timestamp; 

    /**
     * Constructor para crear una nueva solicitud de préstamo.
     * La marca de tiempo (timestamp) se asigna automáticamente al momento de la instanciación.
     *
     * @param codigoLibro   Código del libro solicitado.
     * @param idEstudiante  Nombre del estudiante.
     * @param cedula        Cédula de identidad.
     * @param carrera       Carrera del estudiante.
     */
    public SolicitudPrestamo(String codigoLibro, String idEstudiante, String cedula, String carrera) {
        this.codigoLibro = codigoLibro;
        this.idEstudiante = idEstudiante;
        this.cedula = cedula;
        this.carrera = carrera;
        this.timestamp = System.currentTimeMillis(); 
    }
}
