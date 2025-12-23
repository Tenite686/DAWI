# 🛒 AlquilaPE - Sistema de Gestión de Alquiler de vehículos

## 📋 Descripción

**AlquilaPE** es una API REST desarrollada para la gestión de alquiler de vehículos livianos. Permite administrar alquileres, vhículos, pagos, clientes, usuarios y roles

---

## 👨‍💻 Información del Proyecto

- **Proyecto**: Alquiler vehículos livianos
- 
---

## 🚀 Tecnologías Utilizadas

### **Backend**
| Tecnología | Versión | Descripción |
|-----------|---------|-------------|
| **Java** | 21      | Lenguaje de programación |
| **Spring Boot** | 4.0.0   | Framework principal |
| **Spring Data JPA** | 4.0.0   | Persistencia de datos |
| **Spring Web** | 4.0.0   | API REST |
| **Lombok** | 1.18.42 | Reducción de código boilerplate |

### **Base de Datos**
| Tecnología | Versión | Uso                      |
|-----------|--------|--------------------------|
| **MySQL** | 8.x | Base de datos de preuba  |

### **Documentación**
| Tecnología | Versión | Descripción |
|-----------|---------|-------------|
| **SpringDoc OpenAPI** | 2.8.14  | Documentación Swagger/OpenAPI |


### **Build Tool**
| Tecnología | Versión |
|-----------|---------|
| **Maven** | 3.9.x |

---

## ⚙️ Configuración e Instalación

### **Prerrequisitos**

- ✅ Java 21 o superior
- ✅ Maven 3.9.x o superior
- ✅ MySQL 8.x
- ✅ IDE (IntelliJ IDEA)

### **2. Configurar la Base de Datos**

#### **2.1. Crear la base de datos en MySQL:**

```sql
-- Ejecutar el script SQL proporcionado
source bdtiendita.sql;
```

#### **2.2. Configurar credenciales en `application.properties`:**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_alquilape
spring.datasource.username=admin
spring.datasource.password=12
```

### **3. Compilar el proyecto**

```bash
mvn clean install
```

### **4. Ejecutar la aplicación**

```bash
mvn spring-boot:run
```

O ejecutar el JAR generado:

```bash
java -jar target/alquilape-1.0.0.jar
```

### **5. Verificar que funciona**

Abre tu navegador en:
```
http://localhost:8080/swagger-ui.html
```

---

## 📚 Documentación con Swagger

### **Acceder a Swagger UI:**

```
http://localhost:8080/swagger-ui.html
```

O también:
```
http://localhost:8080/swagger-ui/index.html
```

### **API Docs (JSON):**
```
http://localhost:8080/api-docs
```

### **API Docs (YAML):**
```
http://localhost:8080/api-docs.yaml
```
---

## 📊 Probar con Postman

### **Importar colección:**

1. Descarga el archivo de la carpera raiz del proyecto `API Sistema de Alquiler de Vehículos.json`
2. Abre Postman
3. Click en **Import**
4. Selecciona el archivo JSON
5. ¡Listo! Todos los endpoints estarán disponibles



---

## 📌 Notas Importantes

✅ **Base de datos**: Ejecutar el script `db_alquilape.sql` antes de iniciar

✅ **Java 21**: Requerido obligatoriamente

✅ **Swagger**: Disponible en `/swagger-ui.html` después de iniciar

✅ **Tests**: Usan H2 en memoria, no afectan la BD de desarrollo

✅ **Puerto**: Por defecto 8080, modificable en `application.properties`

---

**Versión**: 1.0.0  
**Fecha**: Diciembre 2025