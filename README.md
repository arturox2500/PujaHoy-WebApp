# PujaHoy – Aplicación Web de Subastas

---

# 📌 Descripción

## Entidades de la Aplicación

### Usuario
Representa a una persona registrada. Contiene información como ID, nombre, nombre visible, email, descripción, contraseña encriptada, foto de perfil, código postal, reputación y estado activo.

### Producto
Representa un artículo en subasta. Incluye ID, nombre, descripción, valor inicial, ID del vendedor, tiempo de inicio y fin, estado e imagen.

### Oferta
Representa una puja realizada por un usuario. Incluye ID, cantidad ofrecida, hora, ID del producto y del usuario.

### Transacción
Representa la compra final de un producto subastado. Incluye ID, coste, ID del comprador, producto y vendedor.

### Valoración
Representa puntuaciones que los compradores dan a los vendedores. Contiene ID, puntuación, ID del producto y del vendedor.

---

## Roles de Usuario y Permisos

### Usuario No Registrado
- Ver subastas activas
- Ver productos subastados
- Navegar por perfiles
- Ver historial de pujas de un producto

### Usuario Registrado
- Acceder a subastas activas
- Ver y editar sus datos
- Subastar productos
- Realizar pujas
- Completar transacciones
- Ver productos ganados
- Valorar usuarios

### Administrador
- Acceso a todos los productos
- Banear/desbanear usuarios o productos

---

## Imágenes
- Usuarios pueden subir imágenes de sus productos
- También pueden tener foto de perfil

## Gráficas
- Línea de tiempo con historial de pujas por producto

## Tecnología Complementaria
- API de mapas para ver la localización de los productos.

## Algoritmo Avanzado
- Los productos se ordenan según la valoración del vendedor.

---

## Páginas Principales

- **Productos Destacados (Index)**  
- **Registro**  
- **Inicio de Sesión**  
- **Detalles de Cuenta**  
- **Tus Pujas Ganadoras**  
- **Tus Productos**  
- **Editar Perfil**  
- **Nueva Subasta**  
- **Página del Producto**  

## Instrucciones para Ejecutar

### Requisitos
- Java 21, Maven 4, Spring Boot 3.4.3
- MySQL 8.0.33, Workbench
- Docker + Docker Compose
- Node.js y Angular CLI
- VSCode o IDE preferido

### Manualmente
1. Descargar ZIP y extraer
2. Configurar BD MySQL (Password0])
3. Ejecutar la app
4. Acceder a `https://localhost:8443`

### Con Docker
```bash
cd backend
docker compose up
```

### Crear Imagen Docker
```bash
chmod +x create_image.sh && ./create_image.sh
chmod +x publish_image.sh && ./publish_image.sh
```

### Ejecutar Frontend Angular
```bash
cd ./frontend
npm install -g @angular/cli@17.3.14
npm install
ng serve
```
Ir a `localhost:4200`

# 👥 Colaboradores
- **Jorge Andrés Echevarría**
- **Arturo Enrique Gutierrez Mirandona** 
- **Iván Gutiérrez González**
- **Víctor Bartolomé Letosa**
- **Miguel Pradillo Bartolomé**
