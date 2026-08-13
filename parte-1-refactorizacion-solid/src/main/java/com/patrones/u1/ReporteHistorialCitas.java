package com.patrones.u1;

import java.util.List;

public class ReporteHistorialCitas {
    public void imprimir(List<String> citas) {
        System.out.println("=== Historial de Citas Médicas ===");
        citas.forEach(c -> System.out.println(" " + c));
    }
}
