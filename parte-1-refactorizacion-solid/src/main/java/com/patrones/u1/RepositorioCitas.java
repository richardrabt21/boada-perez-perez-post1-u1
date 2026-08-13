package com.patrones.u1;

import java.util.List;
import java.util.ArrayList;

public class RepositorioCitas {
    private final List<String> citas = new ArrayList<>();

    public void guardar(String citaId, double total) {
        citas.add(citaId + ":" + total);
        System.out.println("[DB] Cita guardada: " + citaId);
    }

    public List<String> obtenerTodas() {
        return List.copyOf(citas);
    }
}
