package co.uniquindio.plataforma.socket;

import co.uniquindio.plataforma.modelo.*;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log
public class HiloCliente implements Runnable {
    private final Socket socket;
    private final PlataformaServidor plataforma;

    private ProcesadorXML procesador = new ProcesadorXML();

    private File directorioPublicadores = new File("C:\\Users\\hulbe\\OneDrive\\Documentos\\ArticulosSocios");

    public HiloCliente(Socket socket, PlataformaServidor plataforma) {
        this.socket = socket;
        this.plataforma = plataforma;
    }

    @Override
    public void run() {
        try {
            // Se crean flujos de datos de entrada y salida para comunicarse a través del socket
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            // Se lee el mensaje enviado por el cliente
            Mensaje mensaje = (Mensaje) in.readObject();

            // Se captura el tipo de mensaje
            String tipo = mensaje.getTipo();

            // Se captura el contenido del mensaje
            Object contenido = mensaje.getContenido();

            // Según el tipo de mensaje se invoca el método correspondiente
            switch (tipo) {
                case "agregarNoticia":
                    agregarNoticia((Noticia) contenido, out);
                    break;

                case "listarNoticias":
                    listarNoticias(out);
                    break;

                case "registrarCliente":
                    registrarCliente((Cliente) contenido, out);
                    break;
                case "listarClientes":
                    listarClientes(out);
                    break;

                case "cargarNoticias":
                    cargarNoticias(out);
                    break;
                case "registrarSocioPublicador":
                    registrarSocioPublicador((SocioPublicador) contenido, out);
                    break;
                case "listarSocioPublicador":
                    listarSocioPublicador(out);
                    break;
                case "existeSocioPublicador":
                    existeSocioPublicador((SocioPublicador) contenido, out);
                    break;
                case "getSocioPublicador":
                    getSocioPublicador(out);
                    break;
                case "convertirNoticias":
                    convertirNoticias(out);
                    break;
                case "procesarNoticiasCsv":
                    procesarNoticiasCsv(out);
                    break;
                }

            // Se cierra la conexión del socket para liberar los recursos asociados
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void procesarNoticiasCsv(ObjectOutputStream out) throws IOException {
        out.writeObject(plataforma.procesarArchivosCSV());
    }

    private void listarSocioPublicador(ObjectOutputStream out) throws IOException {
        out.writeObject(plataforma.listarSociosPublicadores());
    }

    private void registrarCliente(Cliente cliente, ObjectOutputStream out) throws IOException {
        try{
            plataforma.agregarCliente(cliente.getIdCliente(), cliente.getNombre(), cliente.getRutaArticulos(), cliente.getRutaFotos());
            out.writeObject("Cliente agregado correctamente");
        } catch (Exception e){
            out.writeObject(e.getMessage());
        }
    }

    private void registrarSocioPublicador(SocioPublicador socioPublicador, ObjectOutputStream out) throws IOException {
        try{
            plataforma.agregarSocioPublicador(socioPublicador.getId(), socioPublicador.getNombre(), socioPublicador.getRutaArticulos(), socioPublicador.getNoticias());
            out.writeObject("Socio/Publicador agregado correctamente");
        } catch (Exception e){
            out.writeObject(e.getMessage());
        }
    }

    private void existeSocioPublicador(SocioPublicador socioPublicador, ObjectOutputStream out) throws IOException {
        try{
            boolean estado = plataforma.existeSocioPublicador(socioPublicador.getId(), socioPublicador.getNombre());
            out.writeObject(new Mensaje("Exito", estado));
        } catch (Exception e) {
            out.writeObject(new Mensaje("Error", e.getMessage()));
        }
    }

    public void listarClientes (ObjectOutputStream out) throws IOException {
        out.writeObject(plataforma.listarClientes());
    }
    public void agregarNoticia(Noticia noticia, ObjectOutputStream out) throws IOException {
        try{
            plataforma.agregarNoticia(noticia.getTitulo(), noticia.getContenido(), noticia.getAutor(), noticia.getFecha());
            out.writeObject("Noticia agregado correctamente");
        }catch (Exception e){
            out.writeObject(e.getMessage());
        }
    }
    public void listarNoticias (ObjectOutputStream out) throws IOException {
        out.writeObject(plataforma.listarNoticias());
    }

    public void cargarNoticias(ObjectOutputStream out) throws IOException {
        out.writeObject(plataforma.cargarNoticiasEnClientes());
    }

    public void getSocioPublicador(ObjectOutputStream out) throws IOException {
        out.writeObject(plataforma.getSocioPublicadorAutenticado());
    }

    public void convertirNoticias(ObjectOutputStream out) throws IOException {
        out.writeObject(procesador.procesarDirectorioPublicadores(procesador, directorioPublicadores));
    }
}
