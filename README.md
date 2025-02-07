# **ExistDB - Ejercicios**

## **Descripción del Proyecto:**
Este ejercicio consiste en crear un programa que maneje información relacionada con un gimnasio utilizando **ExistDB**. El programa deberá crear una colección llamada `GIMNASIO` y cargar documentos XML específicos que contienen información sobre los socios, actividades y el uso del gimnasio durante el año. A partir de estos documentos, el programa debe calcular las cuotas a pagar por cada socio, sumando la cuota fija y las cuotas adicionales derivadas de las actividades realizadas.

## **Documentos de Entrada:**

El programa debe procesar los siguientes documentos XML que se encuentran en la carpeta `ColeccionGimnasio`:

1. **socios_gim.xml**: Contiene información sobre los socios del gimnasio.
2. **actividades_gim.xml**: Contiene información sobre las actividades disponibles en el gimnasio, clasificadas en tres tipos:
   - Actividades de **libre horario**: No tienen costo adicional (ej. aparatos, piscina).
   - Actividades de **grupo**: Cuota adicional de 2€ por cada hora (ej. aerobic, pilates).
   - Actividades con **alquiler de espacio**: Cuota adicional de 4€ por cada hora (ej. padel, tenis).
3. **uso_gimnasio.xml**: Contiene las actividades que realizan los socios durante el año, con la fecha, hora de inicio y hora de finalización.

## **Objetivo del Programa:**

1. Crear una colección `GIMNASIO` en **ExistDB**.
2. Subir los documentos XML a la colección `GIMNASIO`.
3. Crear un método para calcular la **CUOTA_FINAL** de cada socio, que incluye:
   - **CUOTA_FIJA**: Valor base que el socio paga.
   - **CUOTAS_ADICIONALES**: Dependientes de las actividades realizadas.
4. Crear un documento XML intermedio con la información detallada de cada actividad realizada por los socios, incluyendo:
   - **COD** (Código del socio).
   - **NOMBRESOCIO** (Nombre del socio).
   - **CODACTIV** (Código de la actividad).
   - **NOMBREACTIVIDAD** (Nombre de la actividad).
   - **horas** (Número de horas dedicadas).
   - **tipoact** (Tipo de actividad).
   - **cuota_adicional** (Cuota adicional generada por la actividad).
5. Agregar el documento intermedio a la colección `GIMNASIO`.
6. Obtener la **CUOTA_TOTAL** por socio, que es la suma de la cuota fija más las cuotas adicionales:
   - **COD** (Código del socio).
   - **NOMBRESOCIO** (Nombre del socio).
   - **CUOTA_FIJA** (Cuota fija del socio).
   - **suma_cuota_adic** (Suma de las cuotas adicionales).
   - **cuota_total** (Cuota final total).

## **Estructura de los Archivos XML:**

### **Documento Intermedio por Actividad:**

Cada actividad realizada por un socio se deberá guardar en un documento XML con la siguiente estructura:

```xml
<datos>
  <COD>xxxxx</COD>
  <NOMBRESOCIO>xxxxxxxx</NOMBRESOCIO>
  <CODACTIV>xxxxxxx</CODACTIV>
  <NOMBREACTIVIDAD>xxxxxxxxx</NOMBREACTIVIDAD>
  <horas>xxxx</horas>
  <tipoact>xxxxx</tipoact>
  <cuota_adicional>xxxxx</cuota_adicional>
</datos>
