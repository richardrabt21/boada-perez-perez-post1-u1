package com.patrones.u1;

import java.util.List;

public class CitaService {
    private final CalculadoraCostoConsulta calculadora;
    private final RepositorioCitas repositorio;
    private final NotificadorPaciente notificador;
    private final TarifaConsultaStrategy tarifaStrategy;

    // Inyección por constructor — DIP aplicado
    public CitaService(CalculadoraCostoConsulta calculadora,
                        RepositorioCitas repositorio,
                        NotificadorPaciente notificador,
                        TarifaConsultaStrategy tarifaStrategy) {
        this.calculadora = calculadora;
        this.repositorio = repositorio;
        this.notificador = notificador;
        this.tarifaStrategy = tarifaStrategy;
    }

    public void agendarCita(String citaId, String email, List<Double> procedimientos) {
        double total = calculadora.calcularTotal(procedimientos);
        double totalConTarifa = tarifaStrategy.aplicar(total);
        repositorio.guardar(citaId, totalConTarifa);
        notificador.enviarRecordatorio(email, citaId);
    }
}