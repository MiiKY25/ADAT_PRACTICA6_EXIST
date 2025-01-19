package org.mikel.ADAT_PRACTICA6_EXIST;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;

import java.io.File;
import java.nio.file.Files;

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

            String xqueryIntermedio = generarXQueryIntermedioCuotaAdicional();
            ejecutarYSubirConsulta(gymCollection, xqueryIntermedio, "cuotas_adicionales.xml");

            String xqueryFinal = generarXQueryFinalTotal();
            ejecutarYSubirConsulta(gymCollection, xqueryFinal, "cuotas_finales.xml");

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

        // Verificar si la colección ya existe
        Collection existingCollection = DatabaseManager.getCollection(uri + "/db/" + collectionName, usuario, password);
        if (existingCollection != null) {
            System.out.println("La colección " + collectionName + " ya existe.");
            return existingCollection;  // Retorna la colección existente
        }

        // Crear la colección si no existe
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

    private static String generarXQueryIntermedioCuotaAdicional() {
        return """
                let $socios := doc('socios_gim.xml')/SOCIOS_GIM/fila_socios
                let $actividades := doc('actividades_gim.xml')/ACTIVIDADES_GIM/fila_actividades
                let $uso_gimnasio := doc('uso_gimnasio.xml')/USO_GIMNASIO/fila_uso
                for $uso in $uso_gimnasio
                let $socio := $socios[COD = $uso/CODSOCIO][1]
                let $actividad := $actividades[@cod = $uso/CODACTIV][1]
                let $horas := xs:integer($uso/HORAFINAL) - xs:integer($uso/HORAINICIO)
                let $cuota_adicional :=
                    if ($actividad/@tipo = '1') then 0
                    else if ($actividad/@tipo = '2') then $horas * 2
                    else if ($actividad/@tipo = '3') then $horas * 4
                    else 0
                return
                    <datos>
                        <COD>{data($uso/CODSOCIO)}</COD>
                        <NOMBRESOCIO>{data($socio/NOMBRE)}</NOMBRESOCIO>
                        <CODACTIV>{data($uso/CODACTIV)}</CODACTIV>
                        <NOMBREACTIVIDAD>{data($actividad/NOMBRE)}</NOMBREACTIVIDAD>
                        <horas>{$horas}</horas>
                        <tipoact>{data($actividad/@tipo)}</tipoact>
                        <cuota_adicional>{$cuota_adicional}</cuota_adicional>
                    </datos>
            """;
    }

    private static String generarXQueryFinalTotal() {
        return """
            let $socios := doc('socios_gim.xml')/SOCIOS_GIM/fila_socios
                let $cuotas := doc('cuotas_adicionales.xml')/datos
                for $socio in $socios
                let $suma_cuota_adicional := sum($cuotas[COD = $socio/COD]/cuota_adicional)
                let $cuota_total := $suma_cuota_adicional + xs:decimal($socio/CUOTA_FIJA)
                return
                    <datos>
                        <COD>{data($socio/COD)}</COD>
                        <NOMBRESOCIO>{data($socio/NOMBRE)}</NOMBRESOCIO>
                        <CUOTA_FIJA>{data($socio/CUOTA_FIJA)}</CUOTA_FIJA>
                        <suma_cuota_adic>{$suma_cuota_adicional}</suma_cuota_adic>
                        <cuota_total>{$cuota_total}</cuota_total>
                    </datos>
            """;
    }

    private static void ejecutarYSubirConsulta(Collection coleccion, String consultaXQuery, String nombreArchivo) throws Exception {
        XQueryService servicioXQuery = (XQueryService) coleccion.getService("XQueryService", "1.0");
        ResourceSet conjuntoResultados = servicioXQuery.query(consultaXQuery);

        // Crear el recurso XML para guardar los resultados
        XMLResource recursoXML = (XMLResource) coleccion.createResource(nombreArchivo, "XMLResource");
        StringBuilder contenido = new StringBuilder("<result>");
        ResourceIterator iteradorRecursos = conjuntoResultados.getIterator();

        // Imprimir y almacenar los resultados
        System.out.println("Resultados de la consulta " + nombreArchivo + ":");
        while (iteradorRecursos.hasMoreResources()) {
            Resource recurso = iteradorRecursos.nextResource();
            String resultado = (String) recurso.getContent();

            // Imprimir cada resultado en la consola
            System.out.println(resultado);

            // Agregar el resultado al contenido a ser almacenado
            contenido.append(resultado);
        }
        contenido.append("</result>");

        // Establecer el contenido del recurso XML
        recursoXML.setContent(contenido.toString());
        coleccion.storeResource(recursoXML);

        System.out.println("Documento guardado: " + nombreArchivo);
    }


}
