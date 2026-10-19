# Arquitectura de NexusMarket

La explicación de cada clase está en [CLASES.md](CLASES.md). El comportamiento de los 33 servicios está en [SERVICIOS.md](SERVICIOS.md).

El dominio no importa Spring ni MongoDB. Los paquetes, clases, enums y rutas están en inglés. Los mensajes de la API y los códigos de error (`REGLA_NEGOCIO`, `NO_ENCONTRADO`, `ACCESO_DENEGADO`, `NO_AUTENTICADO`) siguen en español.

El actor de cada operación viaja en `X-User-Id`. Al arrancar se crea el administrador `user-admin` si la base está vacía. MongoDB escucha en `mongodb://localhost:27017/nexusmarket`. Hace falta Java 21.
