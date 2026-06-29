package modelos;

public class SolicitudPrestamo {
    public String codigoLibro;
    public String idEstudiante;
    public String cedula;
    public String carrera;
    public long timestamp; 

    public SolicitudPrestamo(String codigoLibro, String idEstudiante, String cedula, String carrera) {
        this.codigoLibro = codigoLibro;
        this.idEstudiante = idEstudiante;
        this.cedula = cedula;
        this.carrera = carrera;
        this.timestamp = System.currentTimeMillis(); 
    }
}
