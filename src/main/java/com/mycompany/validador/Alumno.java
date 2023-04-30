/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.validador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.mycompany.validador.Validador.conexion;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author neo_0
 */
public class Alumno {

    String nombre;
    int legajo;
    ArrayList<String> materiasAprobadas = new ArrayList<>();

    public Alumno(String nombre, int legajo) {
        this.nombre = nombre;
        this.legajo = legajo;
    }

    public Alumno() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getLegajo() {
        return legajo;
    }

    public void setLegajo(int legajo) {
        this.legajo = legajo;
    }

    public ArrayList<String> getMateriasAprobadas() {
        return materiasAprobadas;
    }

    public void setMateriasAprobadas(ArrayList<String> materiasAprobadas) {
        this.materiasAprobadas = materiasAprobadas;
    }

    @Override
    public String toString() {
        return "Alumno{" + "nombre=" + nombre + ", legajo=" + legajo + ", materiasAprobadas=" + materiasAprobadas + '}';
    }

    public static void crearAlumno(Scanner sc) throws SQLException, JsonProcessingException, IOException {

        Alumno alumno = new Alumno();

        // Ingresar nombre del alumno
        System.out.println("Ingrese el nombre del alumno:");
        String nombre = sc.next();
        alumno.setNombre(nombre);

        // Ingresar legajo del alumno con limitacion de 5 numeros.
        String legajo;
        Pattern pattern = Pattern.compile("\\d{5}");
        Matcher matcher;
        do {
            System.out.println("Ingrese el número de legajo del alumno:");
            legajo = sc.next();
            matcher = pattern.matcher(legajo);
            if (!matcher.matches()) {
                System.out.println("Formato de legajo inválido. Debe tener exactamente 5 dígitos.");
            }
        } while (!matcher.matches());
        alumno.setLegajo(Integer.parseInt(legajo));

        // Ingresar materias aprobadas del alumno
        List<String> materiasAprobadas = new ArrayList<>();
        String materiaAprobada;
        do {
            System.out.println("Ingrese una materia aprobada por el alumno (o escriba 'FIN' para finalizar):");
            materiaAprobada = sc.next();
            if (!materiaAprobada.equalsIgnoreCase("FIN")) {
                materiasAprobadas.add(materiaAprobada);
            }
        } while (!materiaAprobada.equalsIgnoreCase("FIN"));
        alumno.setMateriasAprobadas((ArrayList<String>) materiasAprobadas);

        // Preguntar si se desea crear el alumno
        System.out.println("¿Desea crear el alumno? (S/N)");
        String opcion = sc.next();
        if (opcion.equalsIgnoreCase("S")) {
            // Insertar el nuevo alumno en la base de datos
            ObjectMapper objectMapper = new ObjectMapper();
            String alumnoJson = objectMapper.writeValueAsString(alumno);
            conexion.estableceConexion();
            Statement stmt = conexion.conectar.createStatement();
            stmt.executeUpdate("INSERT INTO alumnos (legajo, nombre, aprobadas) VALUES (" + alumno.getLegajo() + ", '" + alumno.getNombre() + "', '" + objectMapper.writeValueAsString(alumno.getMateriasAprobadas()) + "');");
            stmt.close();
            conexion.cerrarConnection();
            System.out.println("Alumno creado exitosamente");
        } else {
            System.out.println("Carga cancelada");
        }
    }
    
    //Ingresar NUEVAS materias aprobdas
    
    public static void ingresarMateriasAprobadas(Scanner sc) throws SQLException, JsonProcessingException, IOException {
        int legajo;
        while (true) {
            // Obtener el número de legajo del alumno
            System.out.println("Ingrese el número de legajo del alumno:");
            legajo = sc.nextInt();

            // Verificar si el número de legajo existe en la base de datos
            conexion.estableceConexion();
            Statement stmt = conexion.conectar.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM alumnos WHERE legajo = " + legajo + ";");
            if (!rs.next()) {
                System.out.println("El número de legajo ingresado no existe en la base de datos");
            } else {
                String materiasAprobadasJson = rs.getString("aprobadas");
                rs.close();
                stmt.close();

                // Convertir el formato JSON a un ArrayList de Strings
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayList<String> materiasAprobadas;
                if (materiasAprobadasJson == null || materiasAprobadasJson.isEmpty()) {
                    materiasAprobadas = new ArrayList<>();
                } else {
                    materiasAprobadas = objectMapper.readValue(materiasAprobadasJson, new TypeReference<ArrayList<String>>() {
                    });
                }

                // Solicitar al usuario ingresar las nuevas materias aprobadas
                System.out.println("Ingrese las nuevas materias aprobadas por el alumno separadas por coma (ej: materia1,materia2,materia3):");
                String materiasAprobadasInput = sc.next();

                // Convertir la entrada de materias aprobadas a un ArrayList de Strings
                ArrayList<String> nuevasMateriasAprobadas = new ArrayList<>(Arrays.asList(materiasAprobadasInput.split(",")));

                // Agregar las nuevas materias aprobadas a las existentes
                materiasAprobadas.addAll(nuevasMateriasAprobadas);

                // Convertir el ArrayList a formato JSON
                materiasAprobadasJson = objectMapper.writeValueAsString(materiasAprobadas);

                // Actualizar la base de datos con las materias aprobadas en formato JSON
                conexion.estableceConexion();
                stmt = conexion.conectar.createStatement();
                stmt.executeUpdate("UPDATE alumnos SET aprobadas = '" + materiasAprobadasJson + "' WHERE legajo = " + legajo + ";");
                stmt.close();
                conexion.cerrarConnection();

                System.out.println("Materias aprobadas ingresadas exitosamente");
                break; // Salir del bucle cuando se encuentra un legajo válido
            }
        }
    }

    private static ArrayList<String> obtenerMateriasAprobadasJson(int legajo) throws SQLException, JsonProcessingException, IOException {
        conexion.estableceConexion();
        Statement stmt = conexion.conectar.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT aprobadas FROM alumnos WHERE legajo = " + legajo + ";");
        String materiasAprobadasJson = "";
        if (rs.next()) {
            materiasAprobadasJson = rs.getString("aprobadas");
        }
        rs.close();
        stmt.close();
        conexion.cerrarConnection();

        ArrayList<String> materiasAprobadas = new ArrayList<>();
        if (materiasAprobadasJson != null && !materiasAprobadasJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            materiasAprobadas = objectMapper.readValue(materiasAprobadasJson, new TypeReference<ArrayList<String>>() {
            });
        }
        return materiasAprobadas;
    }
}
