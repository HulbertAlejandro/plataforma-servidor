package co.uniquindio.plataforma.modelo;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SocioPublicador implements Serializable {

    private long id;
    private String nombre;
    private String rutaArticulos;
    private ArrayList<Noticia> noticias;

}
