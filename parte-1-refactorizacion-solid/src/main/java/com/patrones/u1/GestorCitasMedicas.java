package com.patrones.u1;

import java.util.List;
import java.util.ArrayList;

// CLASE GOD OBJECT — no modificar en este paso
public class GestorCitasMedicas {

    private List<String> citas = new ArrayList<>();
    private double tarifaBase = 50000.0; // tarifa base de consulta en COP

    // Responsabilidad 1: lógica de negocio (cálculo de costo de consulta)
    public double calcularCostoConsulta(List<Double> procedimientos) {
        double subtotal = tarifaBase;
        for (double p : procedimientos) subtotal += p;
        return subtotal;
    }

    // Responsabilidad 2: tarifas según tipo de paciente (segundo algoritmo en la misma clase)
    public double aplicarTarifaPaciente(double total, String tipoPaciente) {
        if (tipoPaciente.equals("EPS")) return total * 0.60; // EPS cubre el 40%
        if (tipoPaciente.equals("SEGURO_PRIVADO")) return total * 0.80;
        return total; // PARTICULAR paga el 100%
    }

    // Responsabilidad 3: persistencia
    public void guardarCita(String citaId, double total) {
        citas.add(citaId + ":" + total);
        System.out.println("[DB] Cita guardada: " + citaId);
    }

    // Responsabilidad 4: notificación
    public void enviarRecordatorio(String email, String citaId) {
        System.out.println("[EMAIL] Enviando a " + email
                + " recordatorio de la cita " + citaId);
    }

    // Responsabilidad 5: reporte / presentación
    public void imprimirHistorialCitas() {
        System.out.println("=== Historial de Citas Médicas ===");
        for (String c : citas) System.out.println(" " + c);
    }
}