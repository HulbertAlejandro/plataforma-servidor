package co.uniquindio.plataforma.modelo;

import com.opencsv.CSVWriter;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProcesadorXML {

    PlataformaServidor plataformaServidor = PlataformaServidor.getInstance();
    public void procesarArchivoXMLyAgregarACSV(File archivoXML) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(archivoXML);

            Element root = document.getDocumentElement();
            String titulo = obtenerValorElemento(root, "title");
            String fechaStr = obtenerValorElemento(root, "date.issue");
            String publicador = obtenerValorElemento(root, "doc.copyright");
            String contenido = obtenerValorElemento(root, "body.content");

            // Verificación adicional para debug
            System.out.println("Archivo XML: " + archivoXML.getName());
            System.out.println("Título: " + titulo);
            System.out.println("Fecha: " + fechaStr);
            System.out.println("Publicador: " + publicador);
            System.out.println("Contenido: " + contenido);

            if (fechaStr == null || fechaStr.isEmpty()) {
                System.err.println("La fecha está vacía o no se encontró en el archivo XML: " + archivoXML.getName());
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fecha = LocalDate.parse(fechaStr, formatter);

            String rutaCarpetaSalida = "C:\\Users\\hulbe\\OneDrive\\Documentos\\Noticias";
            Noticia noticia = new Noticia(titulo, contenido, publicador, fecha);
            guardarDatosCSV(rutaCarpetaSalida, noticia);

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
            return nodo.getTextContent().trim(); // Trim para eliminar espacios en blanco
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

    public void descomprimirYProcesarZip(File archivoZip) {
        String rutaCarpetaSalida = "C:\\Users\\hulbe\\OneDrive\\Documentos\\Noticias";
        File dirSalida = new File(rutaCarpetaSalida);
        if (!dirSalida.exists()) {
            dirSalida.mkdirs(); // Crear la carpeta de salida si no existe
        }

        boolean contieneXML = false;
        ZipInputStream zis = null;

        try {
            zis = new ZipInputStream(new FileInputStream(archivoZip));
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File nuevoArchivo = new File(dirSalida, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!nuevoArchivo.isDirectory() && !nuevoArchivo.mkdirs()) {
                        throw new IOException("No se pudo crear el directorio " + nuevoArchivo);
                    }
                } else {
                    // Crear todos los directorios padres
                    File parent = nuevoArchivo.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("No se pudo crear el directorio " + parent);
                    }

                    // Escribir el contenido del archivo
                    try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();

                // Procesar el nuevo archivo descomprimido
                if (nuevoArchivo.isDirectory()) {
                    procesarDirectorioPublicadores(this, nuevoArchivo);
                } else if (nuevoArchivo.getName().endsWith(".xml")) {
                    procesarArchivoXMLyAgregarACSV(nuevoArchivo);
                    contieneXML = true;
                } else if (nuevoArchivo.getName().endsWith(".png") || nuevoArchivo.getName().endsWith(".jpg") || nuevoArchivo.getName().endsWith(".jpeg")) {
                    procesarFotoYAgregarACSV(nuevoArchivo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cerrar el recurso ZipInputStream
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Eliminar el archivo ZIP después de procesarlo
            archivoZip.delete();

            if (!contieneXML) {
                System.out.println("El archivo ZIP " + archivoZip.getName() + " no contiene archivos XML.");
            }
        }
    }



    public void procesarFotoYAgregarACSV(File archivoFoto) {
        try {
            String rutaCarpetaSalida = "C:\\Users\\hulbe\\OneDrive\\Documentos\\Noticias";
            guardarDatosFotoCSV(rutaCarpetaSalida, archivoFoto.getName(), LocalDate.now());
            archivoFoto.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para guardar los datos de la foto en un archivo CSV
    private void guardarDatosFotoCSV(String rutaCarpetaSalida, String nombreFoto, LocalDate fechaProcesamiento) {
        File carpetaSalida = new File(rutaCarpetaSalida);
        if (!carpetaSalida.exists()) {
            carpetaSalida.mkdirs(); // Crear la carpeta de salida si no existe
        }

        String nombreArchivoCSV = "datos_fotos.csv";
        File archivoCSV = new File(carpetaSalida, nombreArchivoCSV);

        try (CSVWriter writer = new CSVWriter(new FileWriter(archivoCSV, true))) {
            // Escribir una nueva línea en el archivo CSV con los datos de la foto
            String[] datosFoto = { nombreFoto, fechaProcesamiento.toString() };
            writer.writeNext(datosFoto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object procesarDirectorioPublicadores(ProcesadorXML procesador, File directorio) {
        procesarArchivosEnDirectorio(procesador, directorio);
        return null;
    }

    private void procesarArchivosEnDirectorio(ProcesadorXML procesador, File directorio) {
        File[] archivos = directorio.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    procesarDirectorioPublicadores(procesador, archivo); // Recursivamente procesar subdirectorios
                } else {
                    procesarArchivo(procesador, archivo);
                }
            }
        }
    }

    private void procesarArchivo(ProcesadorXML procesador, File archivo) {
        String nombreArchivo = archivo.getName();
        if (nombreArchivo.endsWith(".xml")) {
            procesador.procesarArchivoXMLyAgregarACSV(archivo); // Procesar archivo XML de noticia
        } else if (nombreArchivo.endsWith(".png") || nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")) {
            procesador.procesarFotoYAgregarACSV(archivo); // Procesar fotos
        } else if (nombreArchivo.endsWith(".zip")) {
            procesador.descomprimirYProcesarZip(archivo); // Procesar archivos ZIP
        }
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
            // Crear archivo XML y escribir contenido
            File archivoXML = new File(directorioDestino, nombreArchivo);
            try (PrintWriter writer = new PrintWriter(archivoXML)) {
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
                writer.println("<noticia>");
                writer.println("<titulo>" + titulo + "</titulo>");
                writer.println("<contenido>" + contenido + "</contenido>");
                writer.println("<autor>" + publicador + "</autor>");
                writer.println("<fecha>" + fecha + "</fecha>");
                writer.println("</noticia>");
            }
            System.out.println("Archivo XML creado exitosamente: " + archivoXML.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
