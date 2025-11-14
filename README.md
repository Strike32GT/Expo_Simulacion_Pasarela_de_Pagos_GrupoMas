# API Simulada de Pagos (Tarjeta, Yape, PayPal)

Proyecto demo con Spring Boot para simular pagos con foco en Seguridad (JWT, roles, CORS, firmas) y AOP (auditoría, logging, validaciones previas). Incluye una UI simple con Thymeleaf y JS para consumir la API.

## Estado de Cumplimiento
- Seguridad: Sí
  - Autenticación JWT: Sí
  - Control de roles (ADMIN/CLIENTE): Sí
  - Firmas digitales SHA-256: Sí (prefijo `SHA256-`)
  - CORS: Sí (configurado en seguridad)
  - Prevención de ataques (recursos estáticos permitidos, rutas protegidas, sesiones stateless): Sí
- AOP: Parcial / Pendiente
  - Auditoría de operaciones críticas: Pendiente
  - Log de métodos: Pendiente
  - Validación antes de ejecutar pagos (vía Advice): Pendiente
  - Manejo global de errores (Controller Advice): Pendiente

> Nota: La funcionalidad central de pagos, seguridad JWT y firma digital está implementada. Las piezas AOP (aspects) y un `@ControllerAdvice` global se planifican como siguiente fase.

## Características
- Registro e inicio de sesión con JWT.
- Creación de pagos (Tarjeta, Yape, PayPal) con selección de usuario origen/destino.
- Firma digital SHA-256 del pago generada al crear el registro y persistida.
- UI con Thymeleaf + JS (validaciones y autoformato de inputs en Tarjeta, Yape y PayPal).

## Endpoints principales
- Auth
  - `POST /auth/signup`
  - `POST /auth/login` -> devuelve JWT
- Usuarios
  - `GET /usuarios` -> listado para selects
- Pagos
  - `POST /pagos` -> crea un pago (requiere JWT)

## Estructura (archivos relevantes)

- Controladores
  - `src/main/java/.../controller/AuthController.java`
  - `src/main/java/.../controller/PagoController.java`
  - `src/main/java/.../controller/UsuarioController.java`

- Servicio de dominio
  - `src/main/java/.../service/PagoService.java` (lógica de creación de pagos y firma SHA-256)

- Seguridad
  - `src/main/java/.../security/SecurityConfig.java` (filtro de seguridad, CORS, rutas públicas/privadas, recursos estáticos)
  - (JWT utilidades y filtros se asumen según configuración del proyecto; agregar aquí si están en otro paquete)

- Modelo/Entidades
  - `src/main/java/.../model/Usuario.java`
  - `src/main/java/.../model/Pago.java`

- Repositorios
  - `src/main/java/.../repository/UsuarioRepository.java`
  - `src/main/java/.../repository/PagoRepository.java` (si aplica)

- DTOs
  - `src/main/java/.../dto/PagoRequest.java`
  - `src/main/java/.../dto/PagoResponse.java`
  - `src/main/java/.../dto/UsuarioResponse.java`

- Vistas (Thymeleaf)
  - `src/main/resources/templates/login.html`
  - `src/main/resources/templates/crear_cuenta.html`
  - `src/main/resources/templates/index.html`

- Recursos estáticos (Frontend)
  - `src/main/resources/static/app.css`
  - `src/main/resources/static/index.js`
  - `src/main/resources/static/login.js`
  - `src/main/resources/static/crear_cuenta.js`

## Seguridad (detalle)
- JWT stateless
  - El cliente almacena el token y lo envía en `Authorization: Bearer <token>`.
- Roles
  - ADMIN y CLIENTE protegidos desde `SecurityConfig` en rutas según corresponda.
- CORS
  - Configurado para permitir el acceso del frontend embebido.
- Firmas digitales
  - En `PagoService` se calcula `SHA-256` de un payload crítico con prefijo `SHA256-` y se persiste en `pago.firma_digital`.

## AOP (plan de implementación)
- Aspectos propuestos
  - `@Around`/`@Before` para logging de métodos del dominio.
  - `@Before` para validaciones previas a `crearPago` (por ejemplo, evitar pagos a uno mismo o montos inválidos).
  - `@AfterReturning` para auditar operaciones (persistir en una entidad `AuditoriaOperacion`).
- Manejo global de errores
  - `@ControllerAdvice` para mapear excepciones a respuestas JSON claras y trazables.

## Cómo ejecutar
1. Requisitos
   - Java 21
   - Maven 3.9+
   - MySQL en ejecución (config en `application.properties`)
2. Configurar propiedades (ejemplo)
   - `spring.datasource.url=jdbc:mysql://localhost:3306/pagos_db`
   - `spring.datasource.username=...`
   - `spring.datasource.password=...`
   - `jwt.secret=...` (256 bits)
3. Compilar y ejecutar
   - `mvn spring-boot:run`
4. Acceso
   - UI: `http://localhost:8080/` (redirige a Login si no hay token)

## Uso rápido
1. Crear cuenta en `/crear_cuenta` o via `POST /auth/signup`.
2. Login en `/login` -> copia el token JWT en localStorage automáticamente.
3. Ir a `/` (index):
   - Selecciona Origen/Destino.
   - Tarjeta: completa número (16 dígitos), titular, vencimiento `MM/AA`, CVV 3/4 dígitos y monto.
   - Yape: teléfono 9 dígitos (empieza con 9) y monto.
   - PayPal: email válido y monto.
   - En todos los casos, se envía `POST /pagos` con el método elegido.

## Notas técnicas
- El campo `fecha` del `Pago` se genera desde la BD; el servicio recarga la entidad tras guardar para incluir la fecha en la respuesta.
- Validaciones de UI y autoformato implementados en `index.js`.

## Roadmap (pendiente)
- Implementar aspectos AOP (logging, validaciones previas, auditoría persistente).
- `@ControllerAdvice` para manejo global de errores y respuestas consistentes.
- Registrar detalles específicos por método de pago (teléfono Yape, email PayPal) en entidad separada.
- Tests unitarios e integración.
