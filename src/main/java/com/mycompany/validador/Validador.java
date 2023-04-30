/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.validador;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author neo_0
 */
public class Validador {

    private static Scanner sc = new Scanner(System.in).useDelimiter("\n");

    public static Conexion conexion = new Conexion();

    public static void main(String[] args) throws SQLException, JsonProcessingException, IOException {

        boolean salir = false;

        while (!salir) {
            System.out.println("Elija una opción:");
            System.out.println("1. Agregar un nuevo alumno");
            System.out.println("2. Agregar una nueva materia");
            System.out.println("3. Inscribir un alumno a una materia");
            System.out.println("4. Ingresar nueva materia aprobada");
            System.out.println("5. Salir");

            int opcion = sc.nextInt();

            switch (opcion) {
                case 1 ->
                    Alumno.crearAlumno(sc);
                case 2 ->
                    Materia.crearMateria(sc);
                case 3 ->
                    Inscripcion.inscribirAlumno(sc);
                case 4 ->
                    Alumno.ingresarMateriasAprobadas(sc);
                case 5 ->
                    salir = true;

                default ->
                    System.out.println("Opción inválida");
            }
        }
    }
}
