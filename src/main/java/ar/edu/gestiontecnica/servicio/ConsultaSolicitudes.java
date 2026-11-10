package ar.edu.gestiontecnica.servicio;

import ar.edu.gestiontecnica.modelo.Solicitud;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConsultaSolicitudes {
    public static List<Solicitud> buscarYOrdenar(List<Solicitud> solicitudes, String filtro) {
        String buscado = normalizar(filtro).trim();
        List<Solicitud> coincidencias = new ArrayList<>();
        for (Solicitud solicitud : solicitudes) {
            if (normalizar(solicitud.getCliente()).contains(buscado)
                    || normalizar(solicitud.getServicio()).contains(buscado)
                    || Integer.toString(solicitud.getId()).contains(buscado)) {
                coincidencias.add(solicitud);
            }
        }
        Solicitud[] ordenadas = coincidencias.toArray(new Solicitud[0]);
        for (int indice = 1; indice < ordenadas.length; indice++) {
            Solicitud actual = ordenadas[indice];
            int anterior = indice - 1;
            while (anterior >= 0 && ordenadas[anterior].getId() < actual.getId()) {
                ordenadas[anterior + 1] = ordenadas[anterior];
                anterior--;
            }
            ordenadas[anterior + 1] = actual;
        }
        List<Solicitud> resultado = new ArrayList<>();
        for (Solicitud solicitud : ordenadas) {
            resultado.add(solicitud);
        }
        return resultado;
    }

    private static String normalizar(String texto) {
        if (texto == null) { return ""; }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
