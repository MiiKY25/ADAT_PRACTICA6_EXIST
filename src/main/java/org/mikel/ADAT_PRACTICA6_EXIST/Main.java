package org.mikel.ADAT_PRACTICA6_EXIST;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String uri = "xmldb:exist://localhost:8080/exist/xmlrpc";
        String collectionName = "GIMNASIO";
        String usuario = "admin";  // Usuario de eXist-db
        String password = "";  // Contraseña de eXist-db
        String rutaArchivo1 = "src/main/resources/xml/actividades_gim.xml";
        String rutaArchivo2 = "src/main/resources/xml/socios_gim.xml";
        String rutaArchivo3 = "src/main/resources/xml/uso_gimnasio.xml";

        try {
            // Crear la colección en eXist-db
            Collection gymCollection = crearColeccion(uri, collectionName, usuario, password);

            // Subir el archivo XML a la colección
            subirArchivoXML(gymCollection, new File(rutaArchivo1));
            subirArchivoXML(gymCollection, new File(rutaArchivo2));
            subirArchivoXML(gymCollection, new File(rutaArchivo3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo para crear la colección
    public static Collection crearColeccion(String uri, String collectionName, String usuario, String password) throws Exception {
        // Inicializar el driver de eXist-db
        Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
        Database database = (Database) cl.getDeclaredConstructor().newInstance();
        DatabaseManager.registerDatabase(database);

        // Conectar a la base de datos
        Collection rootCollection = DatabaseManager.getCollection(uri + "/db", usuario, password);
        if (rootCollection == null) {
            System.out.println("No se pudo conectar a la base de datos.");
            return null;
        }

        // Crear la colección GIMNASIO
        CollectionManagementService mgtService =
                (CollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
        Collection gymCollection = mgtService.createCollection(collectionName);

        System.out.println("Colección " + collectionName + " creada con éxito.");
        return gymCollection;
    }

    // Metodo para subir un archivo XML
    public static void subirArchivoXML(Collection collection, File file) throws Exception {
        if (file.exists() && file.getName().endsWith(".xml")) {
            // Crear el recurso XML para el archivo
            XMLResource resource = (XMLResource) collection.createResource(file.getName(), XMLResource.RESOURCE_TYPE);

            // Leer el archivo XML y cargarlo en el recurso
            byte[] fileContent = Files.readAllBytes(file.toPath());
            resource.setContent(fileContent);

            // Guardar el recurso en la colección
            collection.storeResource(resource);

            System.out.println("Archivo " + file.getName() + " subido con éxito.");
        } else {
            System.out.println("El archivo no existe o no es un archivo XML válido.");
        }
    }
}
