package com.patrones.u1;

public class NotificadorPaciente {
    public void enviarRecordatorio(String email, String citaId) {
        System.out.println("[EMAIL] Enviando a " + email
                + " recordatorio de la cita " + citaId);
    }
}
