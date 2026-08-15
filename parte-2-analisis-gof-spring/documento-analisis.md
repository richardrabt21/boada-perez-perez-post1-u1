# Análisis de Patrones GoF en Spring Framework

**Nombre:** Richard Boada, Jesús Pérez, Jaireth Pérez
**Código:** 02230132006, 02230132003, 02240131072
**Curso:** Patrones de Diseño de Software
**Unidad:** 1
**Fecha:** 18 de agosto de 2026


## Introducción
 
Spring Framework es uno de los motores principales del ecosistema Java empresarial. Su núcleo (spring-core, spring-beans, spring-context), junto a módulos como spring-aop y spring-jdbc, refleja muy bien cómo se aplican los patrones de diseño del Gang of Four (GoF) descritos originalmente por Gamma et al. (1994) en un entorno de producción real. Al estar construido sobre esta base, Spring Boot hereda toda esa arquitectura en mecanismos cotidianos como la inyección de dependencias, AOP o el acceso a datos (VMware/Broadcom, 2026).
 
En este documento se analizan tres patrones GoF de distintas categorías (Creacional, Estructural y de Comportamiento) dentro del código fuente de Spring (Spring Framework source code, 2026). Para cada patrón se identifica la clase concreta donde se implementa, el problema específico que resuelve y su relación con los principios SOLID, tomando como referencia de catálogo la clasificación de Gamma et al. (1994) y su versión divulgativa en Refactoring Guru (s.f.). Además, se incluye un análisis contrafactual para evaluar el impacto y el costo de diseño que habría tenido Spring de no haber utilizado estas soluciones..


## Análisis de Patrón 1: Singleton (Creacional)
 
### Patrón y categoría
El patrón **Singleton** pertenece a la categoría **Creacional** del catálogo GoF (Gamma et al., 1994). Según Refactoring Guru (s.f.), su propósito general es garantizar que una clase tenga una única instancia en un contexto dado y proporcionar un punto de acceso global y controlado a ella, evitando la creación descoordinada de múltiples objetos que deberían ser compartidos.
 
### Dónde aparece en Spring Framework
El patrón se materializa en la clase `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry`, ubicada en el módulo **spring-beans** (Spring Framework source code, 2026). Esta clase actúa como registro base para `AbstractBeanFactory` y `DefaultListableBeanFactory`, y es la responsable de que un bean declarado con el *scope* por defecto (`singleton`) tenga una única instancia por contenedor de Spring (`ApplicationContext`), tal como lo define la documentación oficial de Spring Boot (VMware/Broadcom, 2026) respecto al ciclo de vida de los beans gestionados por el contenedor IoC.
 
### Problema que resuelve
En una aplicación Spring Boot típica existen decenas de beans (servicios, repositorios, componentes de configuración) que no deben duplicarse: un `DataSource`, un `ObjectMapper` o un servicio de negocio sin estado deben compartir la misma instancia en toda la aplicación para evitar el desperdicio de recursos (por ejemplo, múltiples *pools* de conexión) y para asegurar consistencia de estado compartido entre los componentes que los inyectan. Si Spring no aplicara este patrón, cada `@Autowired` generaría una nueva instancia del bean, lo que multiplicaría el consumo de memoria y podría romper la coherencia del estado de la aplicación (por ejemplo, una caché en memoria dejaría de ser realmente compartida). A diferencia de que cada desarrollador implemente manualmente el patrón Singleton clásico (constructor privado, instancia estática) tal como lo describen Gamma et al. (1994), Spring lo centraliza en el contenedor IoC, de modo que el propio ciclo de vida del framework —y no el código del desarrollador— controla la unicidad.
 
### Evidencia de código
`DefaultSingletonBeanRegistry` mantiene internamente una caché de objetos ya creados y expone un método de registro que verifica que no exista previamente una instancia con el mismo nombre antes de guardarla, lanzando una excepción si se intenta sobrescribir un singleton ya registrado (Spring Framework source code, 2026). De forma simplificada, la lógica equivalente puede resumirse así:
 
```java
	protected void addSingleton(String beanName, Object singletonObject) {
		Object oldObject = this.singletonObjects.putIfAbsent(beanName, singletonObject);
		if (oldObject != null) {
			throw new IllegalStateException("Could not register object [" + singletonObject +
					"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
		}
		this.singletonFactories.remove(beanName);
		this.earlySingletonObjects.remove(beanName);
		this.registeredSingletons.add(beanName);

		Consumer<Object> callback = this.singletonCallbacks.get(beanName);
		if (callback != null) {
			callback.accept(singletonObject);
		}
	}
    	protected void addSingleton(String beanName, Object singletonObject) {
		Object oldObject = this.singletonObjects.putIfAbsent(beanName, singletonObject);
		if (oldObject != null) {
			throw new IllegalStateException("Could not register object [" + singletonObject +
					"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
		}
		this.singletonFactories.remove(beanName);
		this.earlySingletonObjects.remove(beanName);
		this.registeredSingletons.add(beanName);

		Consumer<Object> callback = this.singletonCallbacks.get(beanName);
		if (callback != null) {
			callback.accept(singletonObject);
		}
	}

```
 
La instrucción `putIfAbsent()` constituye la parte fundamental de esta lógica, ya que evita registrar una nueva instancia cuando ya existe un objeto asociado al mismo `beanName`. De esta forma, Spring gestiona la instancia singleton desde el contenedor IoC y no requiere que las clases de la aplicación implementen por sí mismas mecanismos como constructores privados o instancias estáticas (Gamma et al., 1994; Spring Framework source code, 2026).

### Principio SOLID asociado

El patrón se relaciona principalmente con el **Principio de Responsabilidad Única (SRP)**, ya que `DefaultSingletonBeanRegistry` se encarga de gestionar el registro y la unicidad de las instancias singleton dentro del contenedor de Spring, manteniendo esta responsabilidad separada de la lógica de negocio de los beans. De esta manera, las clases de la aplicación no necesitan implementar directamente mecanismos para controlar la creación y reutilización de sus propias instancias (Gamma et al., 1994; Spring Framework source code, 2026).

Además, esta gestión centralizada contribuye al **Principio de Inversión de Dependencias (DIP)**, ya que los componentes pueden recibir sus dependencias administradas por el contenedor de Spring en lugar de encargarse directamente de crearlas. Esto reduce el acoplamiento entre las clases de negocio y la forma concreta en que se construyen y gestionan sus dependencias (Gamma et al., 1994; VMware/Broadcom, 2026).
 
---
 
## Análisis de Patrón 2: Proxy (Estructural)
 
### Patrón y categoría
El patrón **Proxy** pertenece a la categoría **Estructural** (Gamma et al., 1994). Su propósito, según Refactoring Guru (s.f.), es proporcionar un objeto sustituto o intermediario que controla el acceso a otro objeto, permitiendo añadir comportamiento adicional (control de acceso, registro, transacciones, etc.) sin modificar la clase original.

### Dónde aparece en Spring Framework
El patrón se implementa en la clase `org.springframework.aop.framework.JdkDynamicAopProxy`, ubicada en el módulo **spring-aop** (Spring Framework source code, 2026). Esta clase implementa la interfaz `AopProxy` y la interfaz estándar de Java `java.lang.reflect.InvocationHandler`, y es la responsable de generar proxies dinámicos basados en interfaces para los beans interceptados por Spring AOP (por ejemplo, los beans anotados con `@Transactional` o `@Cacheable`), un mecanismo descrito en la documentación oficial de Spring Boot como parte central de la programación orientada a aspectos del framework (VMware/Broadcom, 2026).
 
### Problema que resuelve
Spring Boot necesita aplicar comportamiento transversal —gestión de transacciones, seguridad, caché, registro de auditoría— a métodos de negocio sin obligar al desarrollador a escribir ese código repetidamente dentro de cada clase de servicio. La alternativa directa sería incluir manualmente en cada método el código de apertura/cierre de transacción o de verificación de permisos, lo cual viola la separación de responsabilidades y genera duplicación masiva. `JdkDynamicAopProxy` resuelve esto interceptando las llamadas a los métodos del bean real: cuando un cliente invoca un método sobre el bean proxy, la invocación pasa primero por la cadena de *advices* configurada (por ejemplo, el interceptor transaccional) antes de delegar en el objeto real (`target`) (Spring Framework source code, 2026). El bean de negocio permanece limpio, ignorando por completo que está siendo interceptado, lo que coincide con la intención original del patrón Proxy definida por Gamma et al. (1994).
 
### Evidencia de código
`JdkDynamicAopProxy` construye un proxy dinámico utilizando la API estándar de Java mediante `Proxy.newProxyInstance()`. El método `getProxy()` determina el cargador de clases y las interfaces que serán utilizadas por el proxy, mientras que la propia instancia de JdkDynamicAopProxy actúa como controlador de las invocaciones mediante InvocationHandler (Spring Framework source code, 2026).
 
```java 
	@Override
	public Object getProxy() {
		return getProxy(ClassUtils.getDefaultClassLoader());
	}

	@Override
	public Object getProxy(@Nullable ClassLoader classLoader) {
		if (logger.isTraceEnabled()) {
			logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
		}
		return Proxy.newProxyInstance(determineClassLoader(classLoader), this.cache.proxiedInterfaces, this);
	}

```
 
La instrucción `Proxy.newProxyInstance()` constituye la evidencia principal del uso del patrón Proxy, ya que crea un objeto intermediario que puede recibir las llamadas dirigidas al objeto real y aplicar el comportamiento configurado por Spring AOP antes de delegarlas. Además, `JdkDynamicAopProxy` implementa InvocationHandler, lo que permite centralizar el procesamiento de las invocaciones realizadas sobre el proxy (Spring Framework source code, 2026).

El `DefaultAopProxyFactory` participa en este mecanismo seleccionando el tipo de proxy que debe utilizarse según las características y configuración del objeto. Cuando corresponde utilizar un proxy basado en interfaces, se emplea `JdkDynamicAopProxy`, mientras que en otros casos Spring puede utilizar `CglibAopProxy` (Spring Framework source code, 2026). Esta colaboración muestra que el patrón Proxy puede combinarse con mecanismos de creación y selección dentro de la arquitectura de Spring.

### Principio SOLID asociado

El patrón se relaciona principalmente con el **Principio de Responsabilidad Única (SRP)**, ya que `JdbcTemplate` concentra la responsabilidad de proporcionar la estructura común para las operaciones JDBC, mientras que el `StatementCallback` define la operación específica que debe ejecutarse sobre el `Statement`. Esta separación permite mantener diferenciadas la lógica general de acceso a datos y la lógica específica de cada operación, reduciendo la duplicación de código (Gamma et al., 1994).

También favorece el **Principio de Abierto/Cerrado (OCP)**, porque es posible proporcionar diferentes implementaciones de `StatementCallback` para realizar distintas operaciones sin modificar la estructura general de `JdbcTemplate`. De esta manera, el comportamiento específico puede extenderse mediante nuevos callbacks mientras se mantiene estable la lógica común del template (Gamma et al., 1994; Spring Framework source code, 2026).


## Análisis de Patrón 3: Template Method (Comportamiento)


### Patrón y categoría
El patrón **Template Method** pertenece a la categoría **de Comportamiento** (Gamma et al., 1994). Su propósito, de acuerdo con Refactoring Guru (s.f.), es definir el esqueleto general de un algoritmo en un método de una clase base, delegando en subclases o en objetos colaboradores (callbacks) únicamente los pasos variables, mientras la estructura invariable del algoritmo permanece fija.
 
### Dónde aparece en Spring Framework
El patrón se observa en la clase `org.springframework.jdbc.core.JdbcTemplate`, ubicada en el módulo **spring-jdbc** (Spring Framework source code, 2026). Sus métodos `execute(...)`, `query(...)` y `update(...)` implementan el flujo fijo de acceso a datos JDBC, delegando en interfaces de callback como `StatementCallback`, `PreparedStatementCallback` o `RowMapper` la parte específica de cada operación, un enfoque que la documentación oficial de Spring Boot presenta como la forma recomendada de simplificar el acceso a datos JDBC dentro del framework (VMware/Broadcom, 2026).
 
### Problema que resuelve
El acceso a datos con JDBC puro obliga a repetir, en cada operación, el mismo bloque de código: obtener una conexión, crear el `Statement`, manejar excepciones (`SQLException`), cerrar el `ResultSet`, cerrar el `Statement` y liberar la conexión —habitualmente dentro de bloques `try/catch/finally` anidados. Ese código repetitivo es propenso a fugas de recursos si se omite el cierre en algún punto. `JdbcTemplate` resuelve el problema fijando ese flujo invariable (apertura, ejecución, manejo de errores, liberación de recursos) dentro de su propia implementación, y exponiendo únicamente un punto de extensión —el callback— donde el desarrollador coloca la lógica específica de su consulta (Spring Framework source code, 2026). De esta manera, el desarrollador de Spring Boot solo escribe la sentencia SQL y el mapeo de resultados, sin preocuparse por el ciclo de vida de los recursos JDBC.
 
### Evidencia de código
El método execute de JdbcTemplate utiliza una estructura de ejecución común y permite delegar la operación específica sobre el Statement mediante un objeto que implementa StatementCallback. En el fragmento encontrado, JdbcTemplate crea un ExecuteStatementCallback que encapsula la operación concreta que debe realizarse sobre el Statement y posteriormente lo pasa al método execute, reutilizando así la lógica general definida por la plantilla (Spring Framework source code, 2026).
```java
	@Override
	public <T extends @Nullable Object> T execute(StatementCallback<T> action) throws DataAccessException {
		return execute(action, true);
	}

	@Override
	public void execute(String sql) throws DataAccessException {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing SQL statement [" + sql + "]");
		}

		// Callback to execute the statement.
		class ExecuteStatementCallback implements StatementCallback<@Nullable Object>, SqlProvider {
			@Override
			public @Nullable Object doInStatement(Statement stmt) throws SQLException {
				stmt.execute(sql);
				return null;
			}
			@Override
			public String getSql() {
				return sql;
			}
		}

		execute(new ExecuteStatementCallback(), true);
	}
```
 
En este fragmento, StatementCallback permite encapsular la operación específica que se realizará sobre el Statement, mientras que `JdbcTemplate` proporciona el mecanismo general para ejecutar dicha operación. La implementación de `doInStatement()` representa el paso variable del algoritmo, ya que contiene la acción concreta que debe realizarse (stmt.execute(sql)). Esta separación permite reutilizar la estructura general de ejecución con diferentes operaciones, característica relacionada con la intención del patrón Template Method (Gamma et al., 1994; Spring Framework source code, 2026).

Los métodos de más alto nivel de `JdbcTemplate`, como las operaciones de consulta y actualización, pueden reutilizar esta estructura de ejecución mediante callbacks, evitando que cada operación tenga que implementar nuevamente toda la lógica común de acceso a datos (Spring Framework source code, 2026).

### Principio SOLID asociado
El patrón se relaciona principalmente con el Principio de Responsabilidad Única (SRP), ya que `JdbcTemplate` concentra la responsabilidad de proporcionar la estructura común para las operaciones `JDBC`, mientras que el `StatementCallback` define la operación específica que debe ejecutarse sobre el Statement. Esta separación permite mantener diferenciadas la lógica general de acceso a datos y la lógica específica de cada operación, reduciendo la duplicación de código (Gamma et al., 1994).

También favorece el Principio de Abierto/Cerrado (OCP), porque es posible proporcionar diferentes implementaciones de `StatementCallback` para realizar distintas operaciones sin modificar la estructura general de `JdbcTemplate`. De esta manera, el comportamiento específico puede extenderse mediante nuevos callbacks mientras se mantiene estable la lógica común del template (Gamma et al., 1994; Spring Framework source code, 2026).
 

 
## Conclusiones
 
El análisis de `DefaultSingletonBeanRegistry`, `JdkDynamicAopProxy` y `JdbcTemplate` muestra que Spring Framework no aplica los patrones GoF descritos por Gamma et al. (1994) como ejercicio académico, sino como respuesta directa a problemas concretos de gestión de instancias, interceptación transversal y repetición de código de infraestructura (Spring Framework source code, 2026). En los tres casos, el patrón permite que el código de negocio del desarrollador —el bean de servicio, la clase anotada con `@Transactional`, la consulta SQL— permanezca simple y enfocado en su propósito, mientras el framework absorbe la complejidad estructural mediante un punto de extensión bien definido (registro de instancias, cadena de interceptores o callback), coherente con la arquitectura descrita en la documentación oficial de Spring Boot (VMware/Broadcom, 2026). La lección para el diseño propio es que un patrón GoF rara vez se aplica de forma aislada: en Spring, Singleton, Proxy y Factory Method conviven en la creación de un mismo bean interceptado, lo que sugiere que el valor real de estos patrones está en su combinación disciplinada para sostener principios SOLID como SRP y OCP a gran escala.
 

 
## Referencias
 
Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design patterns: Elements of reusable object-oriented software*. Addison-Wesley.
 
Refactoring Guru. (s.f.). *Design patterns catalog*. https://refactoring.guru/design-patterns
 
Spring Framework source code. (2026). *DefaultSingletonBeanRegistry.java, JdkDynamicAopProxy.java, JdbcTemplate.java*. GitHub. https://github.com/spring-projects/spring-framework
 
VMware/Broadcom. (2026). *Spring Boot reference documentation*. Spring.io. https://docs.spring.io/spring-boot/reference/
 