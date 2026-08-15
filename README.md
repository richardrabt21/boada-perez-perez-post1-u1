# Post-contenido — Unidad 1: Fundamentos de Patrones de Diseño y Buenas Prácticas

## Descripción
Repositorio del post-contenido de la Unidad 1 de Patrones de Diseño de Software. Contiene dos partes: refactorización SOLID de un God Object (parte-1-refactorizacion-solid/, dominio de citas médicas) y análisis de patrones GoF en Spring Framework (parte-2-analisis-gof-spring/).

## Análisis de Violaciones SOLID

| Principio | Método/Sección afectada | Descripción de la violación |
|-----------|--------------------------|------------------------------|
| SRP | calcularCostoConsulta + aplicarTarifaPaciente + guardarCita + enviarRecordatorio + imprimirHistorialCitas | La clase GestorCitasMedicas concentra cinco responsabilidades distintas (cálculo de costos, reglas de tarifas, persistencia, notificación y reporte) en un solo lugar, violando el Principio de Responsabilidad Única: cualquier cambio en alguna de estas áreas obliga a modificar la misma clase. |
| OCP | aplicarTarifaPaciente (if/else sobre tipoPaciente) | Agregar un nuevo tipo de paciente (por ejemplo, "PLAN_CORPORATIVO") requiere modificar directamente el código existente en lugar de extenderlo, violando el Principio de Abierto/Cerrado. |
| DIP | Toda la clase (dependencias internas sin abstracciones) | GestorCitasMedicas no depende de interfaces ni abstracciones; su lógica de negocio, persistencia y notificación están acopladas directamente dentro de la misma clase, sin posibilidad de inyectar implementaciones alternativas. |



## Parte 2 — Análisis de Patrones GoF en Spring

| # | Patrón | Categoría | Clase en Spring |
|---|--------|-----------|-----------------|
| 1 | Singleton | Creacional | `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry` |
| 2 | Proxy | Estructural | `org.springframework.aop.framework.JdkDynamicAopProxy` |
| 3 | Template Method | Comportamiento | `org.springframework.jdbc.core.JdbcTemplate` |

Ver [parte-2-analisis-gof-spring/documento-analisis.md](parte-2-analisis-gof-spring/documento-analisis.md).

## Herramientas utilizadas

- Java 17
- Apache Maven
- VS Code
- Git
- GitHub
- Código fuente de Spring Framework (investigación)

## Conclusiones

## Conclusiones

La investigación permitió identificar cómo Spring Framework utiliza patrones GoF para resolver problemas de diseño. Se analizaron **Singleton**, **Proxy** y **Template Method**, observando su aplicación en código real. Estos patrones favorecen la reutilización, separación de responsabilidades y mantenibilidad del software.
