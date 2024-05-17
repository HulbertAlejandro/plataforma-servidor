package co.uniquindio.plataforma.socket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tipo;
    private Object contenido;


}

