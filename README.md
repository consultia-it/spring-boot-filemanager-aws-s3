## Arquitectura de la aplicación

![Spring Boot and Amazon S3 - Arquitectura](https://user-images.githubusercontent.com/115224035/196198458-b3ed687f-702e-4c54-9ed9-64202603e10a.png)

## Configuración

Toda la configuración necesaria para la correcta ejecución de esta aplicación se concentra en el fichero *application.yml*. 
En él, es necesario indicar: 
* El nombre del bucket S3 con el que se trabajará (previamente creado desde la consola de AWS S3), en la propiedad ``document.bucket-name``
* La región específica en la que se ha creado el *bucket*, propiedad ``cloud.aws.region.static``
* Las credenciales de acceso, tanto la **clave de acceso** como el **secret**, en las propiedades ``cloud.aws.credentials.access-key`` y ``cloud.aws.credentials.secret-key`` respectivamente

```yml
document:
  bucket-name: spring-boot-aws-s3-poc
cloud:
  aws:
    region:
      auto: false
      static: eu-west-3
    credentials:
      access-key: AKIAUND4E7ESIOKUCP3J
      secret-key: XXXXXXX
```

## Arranque y ejecución

```
mvnw spring-boot:run
```

La aplicación levanta el API REST en el puerto 8080. 
Se puede hacer uso de la [colección Postman](https://github.com/consultia-it/spring-boot-filemanager-aws-s3/blob/main/Amazon%20S3%20tests.postman_collection.json) incluída en el propio repositorio.

