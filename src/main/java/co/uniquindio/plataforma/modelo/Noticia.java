package co.uniquindio.plataforma.modelo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
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
                ", titulo='" + titulo + '\'' +
                ", autor=" + autor +
                ", fecha='" + fecha + '\'' +
                ", contenido='" + contenido + '\'' +
                '}';
    }
}