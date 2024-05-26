package co.uniquindio.plataforma.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Noticia implements Serializable {
    private String titulo;
    private String contenido;
    private String autor;
    private LocalDate fecha;

    @Override
    public String toString() {
        return "Noticia{" +
                "titulo='" + titulo + '\'' +
                ", contenido='" + contenido + '\'' +
                ", autor='" + autor + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
