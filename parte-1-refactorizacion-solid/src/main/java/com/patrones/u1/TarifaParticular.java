package com.patrones.u1;

public class TarifaParticular implements TarifaConsultaStrategy {
    public double aplicar(double total) {
        return total; // paga el 100%
    }
}