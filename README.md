# Post-contenido — Unidad 1: Fundamentos de Patrones de Diseño y Buenas Prácticas

## Descripción
Repositorio del post-contenido de la Unidad 1 de Patrones de Diseño de Software. Contiene dos partes: refactorización SOLID de un God Object (parte-1-refactorizacion-solid/, dominio de citas médicas) y análisis de patrones GoF en Spring Framework (parte-2-analisis-gof-spring/).

## Análisis de Violaciones SOLID

| Principio | Método/Sección afectada | Descripción de la violación |
|-----------|--------------------------|------------------------------|
| SRP | calcularCostoConsulta + aplicarTarifaPaciente + guardarCita + enviarRecordatorio + imprimirHistorialCitas | La clase GestorCitasMedicas concentra cinco responsabilidades distintas (cálculo de costos, reglas de tarifas, persistencia, notificación y reporte) en un solo lugar, violando el Principio de Responsabilidad Única: cualquier cambio en alguna de estas áreas obliga a modificar la misma clase. |
| OCP | aplicarTarifaPaciente (if/else sobre tipoPaciente) | Agregar un nuevo tipo de paciente (por ejemplo, "PLAN_CORPORATIVO") requiere modificar directamente el código existente en lugar de extenderlo, violando el Principio de Abierto/Cerrado. |
| DIP | Toda la clase (dependencias internas sin abstracciones) | GestorCitasMedicas no depende de interfaces ni abstracciones; su lógica de negocio, persistencia y notificación están acopladas directamente dentro de la misma clase, sin posibilidad de inyectar implementaciones alternativas. |