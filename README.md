# webapp08
# Aplicación web de subastas "PujaHoy"
## Integrantes
### Jorge Andrés Echevarría j.andres.2022@alumnos.urjc.es jae9104
### Arturo Enrique Gutierrez Mirandona ae.gutierrez.2022@alumnos.urjc.es arturox2500
### Iván Gutiérrez González i.gutierrez.2022@alumnos.urjc.es IvanGutierrrez
### Víctor Bartolomé Letosa v.bartolome.2022@alumnos.urjc.es victorino2324
## Aspectos principales
### Entidades:
#### Usuario
Guarda el rol, el id y si es usuario registrado guardará su reputación, su método de pago y sus productos públicados.
#### Producto
Guarda todas las ofertas que se realizan sobre el, la transacción final, la hora inicial y final, el precio inicial, los datos del producto (imagen, nombre, etc) y el estado de este (sin verificar, verificado, terminado y vetado).
#### Oferta
Guardará el precio de la puja, la hora a la que se realiza, el usuario que la efectua y el identificador del producto.
#### Transacción
Guarda el precio final, el usuario que vende el producto y el que lo compra y el identificador del producto.
### Tipos de Usuarios:
#### Usuario no registrado
Visualiza los productos, los perfiles de usuarios y el historial de las ofertas.
#### Usuario registrado
Puede publicar productos para subasta, hacer ofertas, realizar transacciones (compra o venta) y valorar a otros usuarios.
#### Administrador
Valida productos y banear o desbanear usuarios y productos.
### Permisos de los usuarios:
Los usuarios registrados pueden ver sus productos publicados (vendidos o no), sus datos personales, los productos sobre los que ha realizado una o varias ofertas y ver el precio de estas.
Y los administradores tendrán acceso a los productos publicados y a las ofertas realizadas de todos los usuarios registrados.
### Imágenes:
Los usuarios pueden subir una o varias imágenes sobre los productos que publican.
### Gráficos:
Los usuarios podrán consultar los gráficos que muestran el historial de ofertas de cada producto publicado.
### Tecnología complementaria:
La aplicación web utilizará Auth0 para la gestión de autentificación y autorización.
### Algoritmo o consulta avanzada:
Al ver los productos publicados se mostrarán en orden según la valoración que tenga el usuario que lo publica.
