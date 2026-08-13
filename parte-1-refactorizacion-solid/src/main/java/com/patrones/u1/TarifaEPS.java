package com.patrones.u1;

public class TarifaEPS implements TarifaConsultaStrategy {
    public double aplicar(double total) {
        return total * 0.60; // EPS cubre el 40%
    }
}