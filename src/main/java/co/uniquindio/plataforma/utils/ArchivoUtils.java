package co.uniquindio.plataforma.utils;

import co.uniquindio.plataforma.modelo.Cliente;
import co.uniquindio.plataforma.modelo.SocioPublicador;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class ArchivoUtils {

    /**
     * Serializa un objeto en un archivo en formato XML
     * @param ruta Ruta del archivo donde se va a serializar el objeto
     * @param objeto Objeto a serializar
     * @throws FileNotFoundException Si la ruta del archivo no es válida
     */
    public static void serializarObjetoXML(String ruta, Object objeto) throws FileNotFoundException {
        XMLEncoder encoder = new XMLEncoder(new FileOutputStream(ruta));
        encoder.writeObject(objeto);
        encoder.close();
    }

    /**
     * Deserializa un objeto desde un archivo XML
     * @param ruta Ruta del archivo a deserializar
     * @return Objeto deserializado, o null si el archivo está vacío o no contiene un objeto válido
     * @throws FileNotFoundException Si el archivo no existe
     * @throws IOException Si ocurre un error de E/S al leer el archivo
     */
    public static Object deserializarObjetoXML(String ruta) throws IOException {
        Object objeto = null;
        try (XMLDecoder decoder = new XMLDecoder(new FileInputStream(ruta))) {
            try {
                objeto = decoder.readObject();
                if (objeto == null) {
                    System.out.println("Advertencia: El archivo XML está vacío o no contiene un objeto válido.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Error: El archivo XML está vacío o no contiene un objeto válido.");
            }
        } catch (FileNotFoundException e) {
            // Manejar el caso cuando el archivo no existe
            System.err.println("Error: El archivo no se encontró.");
            throw e;
        } catch (IOException e) {
            // Manejar otros errores de E/S
            System.err.println("Error: Problema al leer el archivo.");
            throw e;
        } catch (Exception e) {
            // Manejar cualquier otro error inesperado
            System.err.println("Error: Ocurrió un problema inesperado al deserializar el objeto.");
            throw e;
        }
        return objeto;
    }

    public static void crearCarpetaCliente(Cliente cliente) {
        // Crear la carpeta en la ruta especificada para los artículos
        File carpetaArticulos = new File(cliente.getRutaArticulos()+"/"+cliente.getNombre());
        if (!carpetaArticulos.exists()) {
            if (carpetaArticulos.mkdirs()) {
                System.out.println("Carpeta de artículos creada para el cliente: " + cliente.getNombre());
            } else {
                System.err.println("Error al crear la carpeta de artículos para el cliente: " + cliente.getNombre());
            }
        } else {
            System.out.println("La carpeta de artículos para el cliente " + cliente.getNombre() + " ya existe.");
        }

        // Crear la carpeta en la ruta especificada para las fotos
        File carpetaFotos = new File(cliente.getRutaFotos()+"/"+cliente.getNombre());
        if (!carpetaFotos.exists()) {
            if (carpetaFotos.mkdirs()) {
                System.out.println("Carpeta de fotos creada para el cliente: " + cliente.getNombre());
            } else {
                System.err.println("Error al crear la carpeta de fotos para el cliente: " + cliente.getNombre());
            }
        } else {
            System.out.println("La carpeta de fotos para el cliente " + cliente.getNombre() + " ya existe.");
        }
    }

    public static void crearCarpetaSocio(SocioPublicador socioPublicador) {
        // Crear la carpeta en la ruta especificada para los artículos
        File carpetaArticulos = new File(socioPublicador.getRutaArticulos()+"/"+socioPublicador.getId());
        if (!carpetaArticulos.exists()) {
            if (carpetaArticulos.mkdirs()) {
                System.out.println("Carpeta de artículos creada para el socio/publicador: " + socioPublicador.getNombre());
            } else {
                System.err.println("Error al crear la carpeta de artículos para el socio/publicador: " + socioPublicador.getNombre());
            }
        } else {
            System.out.println("La carpeta de artículos para el socio/publicador " + socioPublicador.getNombre() + " ya existe.");
        }
    }
}
