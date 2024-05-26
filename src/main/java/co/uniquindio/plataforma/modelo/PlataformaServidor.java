package co.uniquindio.plataforma.modelo;

import co.uniquindio.plataforma.exceptions.AtributoVacioException;
import co.uniquindio.plataforma.exceptions.InformacionRepetidaException;
import co.uniquindio.plataforma.utils.ArchivoUtils;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

@Log
public class PlataformaServidor {
    private ArrayList<Noticia> noticias;

    public ArrayList<Cliente> clientes;

    private ArrayList<SocioPublicador> sociosPublicadores;

    private String rutaClientes;

    private String rutaNoticias;

    private String rutaSociosPublicadores;

    private SocioPublicador socioPublicadorAutenticado;

    private static PlataformaServidor plataformaServidor;

    private static ProcesadorXML procesadorXML;

    // Constructor para inicializar la lista de noticias
    public PlataformaServidor() {
        inicializarLogger();
        log.info("Se crea una nueva instancia de la Plataforma");

        this.rutaClientes = "src/main/resources/persistencia/clientes.txt";
        this.clientes = new ArrayList<>();
        leerCliente();
        for (Cliente cliente : clientes) {
            System.out.println(cliente);
        }

        this.rutaNoticias = "src/main/resources/persistencia/noticias.txt";
        this.noticias = new ArrayList<>();
        leerNoticias();
        for (Noticia noticia : noticias) {
            System.out.println(noticia);
        }

        this.rutaSociosPublicadores = "src/main/resources/persistencia/sociosPublicadores.txt";
        this.sociosPublicadores = new ArrayList<>();
        leerSociosPublicadores();
        for (SocioPublicador socioPublicador : sociosPublicadores){
            System.out.println(socioPublicador);
        }
    }

    private void inicializarLogger() {
        try{
            FileHandler fh = new FileHandler("logs.log", true);
            fh.setFormatter(new SimpleFormatter());
            log.addHandler(fh);
        }catch (IOException e){
            log.severe(e.getMessage());
        }
    }

    public static PlataformaServidor getInstance(){
        if(plataformaServidor == null){
            plataformaServidor = new PlataformaServidor();
        }
        return plataformaServidor;
    }

    public Cliente agregarCliente(long idCliente, String nombre, String rutaArticulos, String rutaFotos) throws AtributoVacioException, InformacionRepetidaException {

        // Validar que el nombre no esté vacío
        if(nombre == null || nombre.isBlank()){
            throw new AtributoVacioException("El nombre es obligatorio");
        }

        // Validar que la identificación no esté repetida
        if(obtenerCliente(idCliente) != null ){
            throw new InformacionRepetidaException("El id "+idCliente+" ya está registrado");
        }

        // Validar que la ruta de los artículos no esté vacía
        if(rutaArticulos == null || rutaArticulos.isBlank()){
            throw new AtributoVacioException("La ruta de los artículos es obligatoria");
        }

        // Validar que la ruta de las fotos no esté vacía
        if(rutaFotos == null || rutaFotos.isBlank()){
            throw new AtributoVacioException("La ruta de las fotos es obligatoria");
        }

        // Construir el cliente con los atributos proporcionados
        Cliente cliente = new Cliente(idCliente, nombre, rutaArticulos, rutaFotos);

        // Agregar el cliente a la lista de clientes
        clientes.add(cliente);

        ArchivoUtils.crearCarpetaCliente(cliente);

        // Escribir el cliente en algún medio de persistencia (archivo, base de datos, etc.)
        escribirCliente();

        // Loggear el registro del cliente
        log.info("Se ha registrado un nuevo cliente con el nombre: "+nombre);

        return cliente;
    }

    private Cliente obtenerCliente(long idCliente) {
        for (Cliente cliente : clientes) {
            if (cliente.getIdCliente() == idCliente) {
                return cliente;
            }
        }
        return null;
    }

    private void escribirCliente() {
        try{
            ArchivoUtils.serializarObjetoXML(rutaClientes, clientes);
        }catch (Exception e){
            log.severe(e.getMessage());
        }
    }

    private void leerCliente() {
        try {
            ArrayList<Cliente> lista = (ArrayList<Cliente>) ArchivoUtils.deserializarObjetoXML(rutaClientes);
            if (lista != null) {
                this.clientes = lista;
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }


    public Noticia agregarNoticia(String titulo, String contenido, String autor, LocalDate fecha) {
        if (titulo == null || titulo.isEmpty()) {
            throw new IllegalArgumentException("El título de la noticia no puede estar vacío");
        }
        if (contenido == null || contenido.isEmpty()) {
            throw new IllegalArgumentException("El contenido de la noticia no puede estar vacío");
        }
        if (autor == null || autor.isEmpty()) {
            throw new IllegalArgumentException("El autor de la noticia no puede estar vacío");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la noticia no puede ser nula");
        }

        Noticia noticia = new Noticia(titulo, contenido, autor, fecha);
        ArrayList<Noticia> noticias = socioPublicadorAutenticado.getNoticias();
        noticias.add(noticia);

        escribirNoticia();
        escribirSocioPublicador();

        log.info("Se ha registrado una nueva noticia");

        return noticia;
    }

    private void escribirNoticia() {
        try{
            ArchivoUtils.serializarObjetoXML(rutaNoticias, noticias);
        }catch (Exception e){
            log.severe(e.getMessage());
        }
    }

    private void leerNoticias() {
        try {
            ArrayList<Noticia> lista = (ArrayList<Noticia>) ArchivoUtils.deserializarObjetoXML(rutaNoticias);
            if (lista != null) {
                this.noticias = lista;
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    public ArrayList<Noticia> listarNoticias() {
        return noticias;
    }

    public ArrayList<Cliente> listarClientes() {
        return clientes;
    }

    public ArrayList<SocioPublicador> listarSociosPublicadores() {
        return sociosPublicadores;
    }

    public String cargarNoticiasEnClientes() {
        for (Cliente cliente : clientes) {
            String rutaCarpetaArticulos = cliente.getRutaArticulos() + File.separator + cliente.getNombre();

            // Verificar y crear la carpeta del cliente si no existe
            crearCarpeta(rutaCarpetaArticulos);

            // Limpiar la carpeta de artículos del cliente
            limpiarCarpeta(rutaCarpetaArticulos+"/"+cliente.getNombre());

            // Guardar las noticias en la carpeta del cliente
            for (Noticia noticia : noticias) {
                guardarNoticiaEnCarpeta(noticia, rutaCarpetaArticulos);
            }
        }
        return null;
    }

    private void crearCarpeta(String rutaCarpeta) {
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) {
            if (carpeta.mkdirs()) {
                System.out.println("Carpeta creada correctamente: " + rutaCarpeta);
            } else {
                System.err.println("Error al crear la carpeta: " + rutaCarpeta);
            }
        }
    }

    public void limpiarListaSociosPublicadores() {
        for (SocioPublicador socio : sociosPublicadores) {
            socio.getNoticias().clear();
        }
        escribirSocioPublicador();
    }

    public void limpiarCarpeta(String rutaCarpeta) {
        try {
            Path carpeta = Paths.get(rutaCarpeta);
            Files.walk(carpeta)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.err.println("Error al limpiar la carpeta: " + e.getMessage());
        }
    }


    public void guardarNoticiaEnCarpeta(Noticia noticia, String rutaCarpeta) {
        try {
            File archivoNoticia = new File(rutaCarpeta + File.separator + noticia.getTitulo() + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("noticia");
            doc.appendChild(rootElement);

            Element titulo = doc.createElement("titulo");
            titulo.appendChild(doc.createTextNode(noticia.getTitulo()));
            rootElement.appendChild(titulo);

            Element contenido = doc.createElement("contenido");
            contenido.appendChild(doc.createTextNode(noticia.getContenido()));
            rootElement.appendChild(contenido);

            Element autor = doc.createElement("autor");
            autor.appendChild(doc.createTextNode(noticia.getAutor()));
            rootElement.appendChild(autor);

            Element fecha = doc.createElement("fecha");
            fecha.appendChild(doc.createTextNode(noticia.getFecha().toString()));
            rootElement.appendChild(fecha);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivoNoticia);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void limpiarCarpetaSocios() {
        String rutaBase = "C:\\Users\\hulbe\\OneDrive\\Documentos\\ArticulosSocios";
        File carpetaBase = new File(rutaBase);
        if (carpetaBase.exists() && carpetaBase.isDirectory()) {
            File[] carpetasSocios = carpetaBase.listFiles();
            if (carpetasSocios != null) {
                for (File carpetaSocio : carpetasSocios) {
                    if (carpetaSocio.isDirectory()) {
                        limpiarCarpeta(carpetaSocio);
                    }
                }
            }
        } else {
            System.out.println("La carpeta base de los socios no existe o no es una carpeta válida.");
        }
    }

    private void limpiarCarpeta(File carpeta) {
        File[] archivos = carpeta.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    limpiarCarpeta(archivo);
                } else {
                    archivo.delete();
                }
            }
        }
    }

    public SocioPublicador agregarSocioPublicador(long idSocio, String nombre, String rutaArticulos, ArrayList<Noticia> noticias) throws AtributoVacioException, InformacionRepetidaException {

        // Validar que el nombre no esté vacío
        if(nombre == null || nombre.isBlank()){
            throw new AtributoVacioException("El nombre es obligatorio");
        }

        // Validar que la identificación no esté repetida
        if(obtenerSocioPublicador(idSocio) != null ){
            throw new InformacionRepetidaException("El id "+idSocio+" ya está registrado");
        }

        // Validar que la ruta de los artículos no esté vacía
        if(rutaArticulos == null || rutaArticulos.isBlank()){
            throw new AtributoVacioException("La ruta de los artículos es obligatoria");
        }

        // Construir el cliente con los atributos proporcionados
        SocioPublicador socioPublicador = new SocioPublicador(idSocio, nombre, rutaArticulos, noticias);

        // Agregar el socio/publicador a la lista
        sociosPublicadores.add(socioPublicador);

        ArchivoUtils.crearCarpetaSocio(socioPublicador);

        // Escribir el cliente en algún medio de persistencia (archivo, base de datos, etc.)
        escribirSocioPublicador();

        // Loggear el registro del cliente
        log.info("Se ha registrado un nuevo socio/publicador con el nombre: "+nombre);

        return socioPublicador;
    }

    private void escribirSocioPublicador() {
        try{
            ArchivoUtils.serializarObjetoXML(rutaSociosPublicadores, sociosPublicadores);
        }catch (Exception e){
            log.severe(e.getMessage());
        }
    }

    private SocioPublicador obtenerSocioPublicador(long idSocio) {
        for (SocioPublicador socioPublicador : sociosPublicadores) {
            if (socioPublicador.getId() == idSocio) {
                return socioPublicador;
            }
        }
        return null;
    }

    private void leerSociosPublicadores() {
        try {
            ArrayList<SocioPublicador> lista = (ArrayList<SocioPublicador>) ArchivoUtils.deserializarObjetoXML(rutaSociosPublicadores);
            if (lista != null) {
                this.sociosPublicadores = lista;
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    // Método para verificar si un SocioPublicador específico existe en un ArrayList
    public boolean existeSocioPublicador(long id, String nombre) {
        for (SocioPublicador sp : sociosPublicadores) {
            // Compara el id y nombre del SocioPublicador actual con el socio dado
            if (sp.getId() == id && nombre.equals(sp.getNombre())) {
                this.socioPublicadorAutenticado = sp;
                return true; // Si encuentra coincidencia, devuelve true
            }
        }
        return false; // Si no encuentra coincidencia, devuelve false
    }

    public SocioPublicador getSocioPublicadorAutenticado(){
        return socioPublicadorAutenticado;
    }

    private String rutaCSV = "C:\\Users\\hulbe\\OneDrive\\Documentos\\Noticias";
    public Object procesarArchivosCSV() throws IOException {
        File carpeta = new File(rutaCSV);
        File[] archivosCSV = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (archivosCSV != null) {
            for (File archivoCSV : archivosCSV) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        // Suponemos que cada línea es una noticia en formato CSV
                        String[] datos = procesadorXML.parseCSVLine(linea);
                        if (datos.length == 4) {
                            String titulo = datos[0];
                            String fecha = datos[1];
                            String publicador = datos[2];
                            String contenido = datos[3];

                            // Crear archivo XML por cada noticia para cada cliente
                            for (Cliente cliente : clientes) {
                                procesadorXML.crearArchivoXML(cliente.getRutaArticulos()+"/"+cliente.getNombre(), titulo, fecha, publicador, contenido);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
