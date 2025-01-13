package org.mikel.ADAT_PRACTICA6_EXIST;


import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.CollectionManagementService;

public class Main {

    public static void main(String[] args) {
        String uri = "xmldb:exist://localhost:8080/exist/xmlrpc";
        String collectionName = "GIMNASIO";
        String usuario = "admin";  // Usuario de eXist-db
        String password = "";  // Contraseña de eXist-db

        try {
            crearColeccion(uri, collectionName, usuario, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void crearColeccion(String uri, String collectionName, String usuario, String password) throws Exception {
        // Inicializar el driver de eXist-db
        Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
        Database database = (Database) cl.getDeclaredConstructor().newInstance();
        DatabaseManager.registerDatabase(database);

        // Conectar a la base de datos
        Collection rootCollection = DatabaseManager.getCollection(uri + "/db", usuario, password);
        if (rootCollection == null) {
            System.out.println("No se pudo conectar a la base de datos.");
            return;
        }

        // Crear la colección GIMNASIO
        CollectionManagementService mgtService =
                (CollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
        mgtService.createCollection(collectionName);

        System.out.println("Colección " + collectionName + " creada con éxito.");
    }
}
