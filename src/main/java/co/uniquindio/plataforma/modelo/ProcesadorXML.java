package co.uniquindio.plataforma.modelo;

import com.opencsv.CSVWriter;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProcesadorXML {

    public void procesarArchivoXML(File archivoXML) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(archivoXML);

            Element root = document.getDocumentElement();
            String titulo = obtenerValorElemento(root, "title");
            String fechaStr = obtenerValorElemento(root, "date.issue");
            String publicador = obtenerValorElemento(root, "doc.copyright");
            String contenido = obtenerValorElemento(root, "body.content");

            // Mostrar los datos extraídos por consola (opcional)
            System.out.println("Título: " + titulo);
            System.out.println("Fecha: " + fechaStr);
            System.out.println("Publicador: " + publicador);
            System.out.println("Contenido: " + contenido);

            // Convertir la fecha de String a LocalDate utilizando un DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fecha = LocalDate.parse(fechaStr, formatter);

            // Especificar la ruta de la carpeta de salida para los archivos CSV
            String rutaCarpetaSalida = "C:\\Users\\hulbe\\OneDrive\\Documentos\\Noticias";

            // Crear una instancia de Noticia con los datos extraídos
            Noticia noticia = new Noticia(titulo, contenido, publicador, fecha);

            // Llamar al método para guardar la noticia en un archivo CSV
            guardarDatosCSV(rutaCarpetaSalida, noticia);

            // Borrar el archivo XML después de procesarlo
            archivoXML.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para obtener el valor de un elemento del documento XML por nombre de etiqueta
    private String obtenerValorElemento(Element elementoPadre, String nombreElemento) {
        NodeList nodeList = elementoPadre.getElementsByTagName(nombreElemento);
        if (nodeList.getLength() > 0) {
            Node nodo = nodeList.item(0);
            return nodo.getTextContent();
        }
        return "";
    }

    // Método para guardar los datos de la noticia en un archivo CSV
    private void guardarDatosCSV(String rutaCarpetaSalida, Noticia noticia) {
        File carpetaSalida = new File(rutaCarpetaSalida);
        if (!carpetaSalida.exists()) {
            carpetaSalida.mkdirs(); // Crear la carpeta de salida si no existe
        }

        String nombreArchivoCSV = "datos_noticias.csv";
        File archivoCSV = new File(carpetaSalida, nombreArchivoCSV);

        try (CSVWriter writer = new CSVWriter(new FileWriter(archivoCSV, true))) {
            // Escribir una nueva línea en el archivo CSV con los datos de la noticia
            String[] datosNoticia = { noticia.getTitulo(), noticia.getFecha().toString(),
                    noticia.getAutor(), noticia.getContenido() };
            writer.writeNext(datosNoticia);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object procesarDirectorioPublicadores(ProcesadorXML procesador, File directorio) {
        File[] archivos = directorio.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    procesarDirectorioPublicadores(procesador, archivo); // Recursivamente procesar subdirectorios
                } else if (archivo.getName().endsWith(".xml")) {
                    procesador.procesarArchivoXML(archivo); // Procesar archivo XML de noticia
                }
            }
        }
        return null;
    }

    public static String[] parseCSVLine(String line) {
        // Método básico para dividir una línea de CSV en sus componentes
        String[] parts = line.split("\",\"");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("^\"|\"$", ""); // Remover comillas de inicio y fin
        }
        return parts;
    }

    public static void crearArchivoXML(String rutaDestino, String titulo, String fecha, String publicador, String contenido) {
        try {
            // Generar un nombre de archivo válido
            String nombreArchivo = titulo.replaceAll("[^a-zA-Z0-9]", "_") + ".xml";
            // Asegurar que la ruta destino existe
            File directorioDestino = new File(rutaDestino);
            if (!directorioDestino.exists()) {
                directorioDestino.mkdirs(); // Crear directorio si no existe
            }
            // Crear un objeto File para representar el archivo
            File archivoXML = new File(rutaDestino, nombreArchivo);

            // Crear el FileWriter para escribir en el archivo
            try (FileWriter writer = new FileWriter(archivoXML)) {
                // Escribir el contenido en formato XML
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<nitf xmlns=\"\" xmlns:xsi=\"\" xsi:schemaLocation=\"\">\n");
                writer.write("  <head>\n");
                writer.write("    <title>" + titulo + "</title>\n");
                writer.write("    <docdata>\n");
                writer.write("      <date.issue>" + fecha + "</date.issue>\n");
                writer.write("      <doc.copyright>" + publicador + "</doc.copyright>\n");
                writer.write("    </docdata>\n");
                writer.write("  </head>\n");
                writer.write("  <body>\n");
                writer.write("    <body.content>" + contenido + "</body.content>\n");
                writer.write("  </body>\n");
                writer.write("</nitf>\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
