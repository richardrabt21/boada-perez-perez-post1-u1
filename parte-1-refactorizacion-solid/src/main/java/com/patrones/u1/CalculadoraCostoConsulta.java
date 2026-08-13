package com.patrones.u1;

import java.util.List;

public class CalculadoraCostoConsulta {
    private final double tarifaBase;

    public CalculadoraCostoConsulta(double tarifaBase) {
        this.tarifaBase = tarifaBase;
    }

    public double calcularTotal(List<Double> procedimientos) {
        double subtotal = tarifaBase;
        for (double p : procedimientos) subtotal += p;
        return subtotal;
    }
}