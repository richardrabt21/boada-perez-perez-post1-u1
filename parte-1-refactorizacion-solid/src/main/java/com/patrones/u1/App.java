package com.patrones.u1;

import java.util.List;

public class App {
    public static void main(String[] args) {
        RepositorioCitas repo = new RepositorioCitas();
        NotificadorPaciente notificador = new NotificadorPaciente();
        CalculadoraCostoConsulta calculadora = new CalculadoraCostoConsulta(50000.0);
        ReporteHistorialCitas reporte = new ReporteHistorialCitas();

        // Cita con paciente EPS
        CitaService servicioEPS = new CitaService(
                calculadora, repo, notificador, new TarifaEPS());
        servicioEPS.agendarCita("CITA-001", "paciente1@mail.com",
                List.of(20000.0, 15000.0));

        // Cita con paciente Seguro Privado
        CitaService servicioSeguro = new CitaService(
                calculadora, repo, notificador, new TarifaSeguroPrivado());
        servicioSeguro.agendarCita("CITA-002", "paciente2@mail.com",
                List.of(30000.0));

        // Cita con paciente Particular
        CitaService servicioParticular = new CitaService(
                calculadora, repo, notificador, new TarifaParticular());
        servicioParticular.agendarCita("CITA-003", "paciente3@mail.com",
                List.of(10000.0, 5000.0));

        reporte.imprimir(repo.obtenerTodas());
    }
}