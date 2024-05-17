package co.uniquindio.plataforma.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa un cliente que recibe contenido de cualquier tipo.
 */
public class Cliente implements Serializable {

    // Miembros tipo variable.

    /**
     * Identificador único del cliente.
     */
    public long idCliente;

    /**
     * Nombre del cliente.
     */
    public String nombre;

    /**
     * Ruta de la carpeta donde se entregan los artículos al cliente.
     */
    public String rutaArticulos;

    /**
     * Ruta de la carpeta donde se entregan las fotos al cliente.
     */
    public String rutaFotos;


    // Miembros tipo método (Constructor)

    /**
     * Constructor
     *
     * @param idCliente Identificador único del cliente.
     * @param nombre Nombre del cliente.
     * @param rutaArticulos Ruta donde se entregan los artículos.
     * @param rutaFotos Ruta donde se entregan las fotos.
     */
    public Cliente(long idCliente, String nombre, String rutaArticulos, String rutaFotos) {
        super();
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.rutaArticulos = rutaArticulos;
        this.rutaFotos = rutaFotos;
    }

    public Cliente() {
    }

    // Miembros tipo método (Accesores)

    /**
     * @return the idCliente
     */
    public long getIdCliente() {
        return idCliente;
    }

    /**
     * @param idCliente the idCliente to set
     */
    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the rutaArticulos
     */
    public String getRutaArticulos() {
        return rutaArticulos;
    }

    /**
     * @param rutaArticulos the rutaArticulos to set
     */
    public void setRutaArticulos(String rutaArticulos) {
        this.rutaArticulos = rutaArticulos;
    }

    /**
     * @return the rutaFotos
     */
    public String getRutaFotos() {
        return rutaFotos;
    }

    /**
     * @param rutaFotos the rutaFotos to set
     */
    public void setRutaFotos(String rutaFotos) {
        this.rutaFotos = rutaFotos;
    }


    // Miembros tipo método (hascode e equals)

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cliente other = (Cliente) obj;
        return Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", rutaArticulos='" + rutaArticulos + '\'' +
                ", rutaFotos='" + rutaFotos + '\'' +
                '}';
    }
}
