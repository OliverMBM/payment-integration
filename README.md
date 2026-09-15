# Payment Integration Service

Servicio desarrollado con Java y Spring Boot para recibir pagos en JSON, persistirlos y transformarlos a XML para simular la integración con un sistema core legado.

## Tecnologías

- Java 17
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Spring Data JPA
- Spring Security
- H2
- Jackson XML
- OpenAPI / Swagger
- JUnit 5
- Python 3

## Requisitos

- JDK 17
- Python 3

No es necesario instalar Maven globalmente, ya que el proyecto incluye Maven Wrapper.

## Ejecución

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

La API estará disponible en:

```text
http://localhost:8080
```

Para ejecutar los tests:

```bash
./mvnw clean test
```

## Autenticación

Los endpoints utilizan HTTP Basic Authentication.

```text
Username: integration-user
Password: integration-pass
```

Se eligió Basic Authentication para mantener la solución sencilla dentro del tiempo disponible. En un ambiente productivo se podría utilizar OAuth2/JWT y un gestor de secretos.

## API

### Crear pago

```http
POST /payments
```

Ejemplo:

```json
{
  "id": "PAY-001",
  "customerId": "CUS-100",
  "amount": 150.75,
  "currency": "USD",
  "timestamp": "2026-09-14T20:30:00-06:00"
}
```

Respuesta:

```json
{
  "id": "PAY-001",
  "customerId": "CUS-100",
  "amount": 150.75,
  "currency": "USD",
  "timestamp": "2026-09-14T20:30:00-06:00",
  "status": "PROCESSED"
}
```

Se validan campos obligatorios, monto positivo, moneda y timestamp.

Si se intenta procesar un `id` ya existente, se devuelve `409 Conflict`.

### Consultar pagos

```http
GET /payments
```

Permite consultar todos los pagos o filtrar por cliente y rango de fechas.

Ejemplos:

```http
GET /payments?customerId=CUS-100
```

```http
GET /payments?from=2026-09-01T00:00:00Z&to=2026-09-30T23:59:59Z
```

```http
GET /payments?customerId=CUS-100&from=2026-09-01T00:00:00Z&to=2026-09-30T23:59:59Z
```

Los parámetros `from` y `to` deben enviarse juntos.

## Integración XML

Cada pago aceptado se transforma a XML mediante Jackson XML.

Ejemplo:

```xml
<payment>
    <id>PAY-001</id>
    <customerId>CUS-100</customerId>
    <amount>150.75</amount>
    <currency>USD</currency>
    <timestamp>2026-09-14T20:30:00-06:00</timestamp>
    <status>PROCESSED</status>
</payment>
```

Como no se proporcionó un XSD o contrato XML específico, se utilizó una estructura simple con los datos disponibles.

El envío al sistema core se simula guardando los archivos XML en:

```text
outbox/
```

Ejemplo:

```text
outbox/payment-PAY-001.xml
```

## Persistencia

La aplicación utiliza H2 en memoria:

```properties
spring.datasource.url=jdbc:h2:mem:paymentsdb
```

Los datos existen únicamente mientras la aplicación está ejecutándose.

## Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

La especificación exportada se encuentra en:

```text
docs/openapi.json
```

## Tests

Ejecutar:

```bash
./mvnw clean test
```

Se incluye un test unitario para validar la transformación de `Payment` a XML.

## SQL y PL/SQL

Los scripts se encuentran en:

```text
sql/
├── indexes.sql
├── process_pending_payments.sql
└── top_customers.sql
```

Incluyen:

- consulta de los 10 clientes con mayor monto pagado en los últimos 30 días
- cantidad de pagos y ticket promedio
- índice para mejorar la consulta
- procedimiento para procesar pagos `PENDING`
- actualización a estado `PROCESSED`
- registro de errores en `PAYMENT_PROCESS_LOG`

Para el reporte se asumió que únicamente los pagos `PROCESSED` representan pagos efectivamente realizados.

No es necesario levantar una instancia Oracle para revisar los scripts.

## Reporte Python

El script se encuentra en:

```text
python/payments_report.py
```

Consume `GET /payments` utilizando Basic Authentication y genera:

```text
payments_summary.csv
```

con las columnas:

```text
customerId
totalAmount
paymentCount
averageAmount
```

Para ejecutarlo:

```bash
cd python
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python payments_report.py
```

El script maneja errores de conexión, timeout y autenticación.

## Estructura principal

```text
payment-integration/
├── docs/
│   └── openapi.json
├── python/
│   ├── payments_report.py
│   └── requirements.txt
├── sql/
│   ├── indexes.sql
│   ├── process_pending_payments.sql
│   └── top_customers.sql
├── src/
│   ├── main/
│   └── test/
├── pom.xml
└── README.md
```

## Decisiones técnicas

- Java 17 como versión LTS.
- Maven Wrapper para facilitar la ejecución.
- H2 para evitar depender de una base externa.
- DTOs separados de la entidad JPA.
- `BigDecimal` para valores monetarios.
- `OffsetDateTime` para conservar el offset del timestamp.
- Basic Authentication para reducir complejidad.
- Jackson XML para la transformación.
- Archivos XML en disco para simular el envío al core.

## Limitaciones

La escritura del XML y la persistencia en H2 son operaciones independientes, por lo que no existe una transacción distribuida entre ambos recursos.

Para el alcance de la prueba se aceptó esta simplificación.

También se verifica que un identificador de pago no haya sido procesado previamente.

## Siguiente iteración

Con más tiempo se podrían agregar:

- OAuth2/JWT
- gestión de secretos
- base de datos persistente
- Flyway o Liquibase
- mayor cobertura de tests
- retries e idempotencia
- integración real mediante HTTP/JMS/mensajería
- paginación
- logging y observabilidad
- validación mediante XSD

## Ejecución rápida

Levantar aplicación:

```bash
./mvnw spring-boot:run
```

Crear pago:

```bash
curl -u integration-user:integration-pass \
  -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "id": "PAY-001",
    "customerId": "CUS-100",
    "amount": 150.75,
    "currency": "USD",
    "timestamp": "2026-09-14T20:30:00-06:00"
  }'
```

Consultar pagos:

```bash
curl -u integration-user:integration-pass \
  http://localhost:8080/payments
```

Ejecutar tests:

```bash
./mvnw clean test
```

Generar reporte:

```bash
cd python
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python payments_report.py
```