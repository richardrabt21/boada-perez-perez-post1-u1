package com.patrones.u1;

public class TarifaSeguroPrivado implements TarifaConsultaStrategy {
    public double aplicar(double total) {
        return total * 0.80; // seguro cubre el 20%
    }
}