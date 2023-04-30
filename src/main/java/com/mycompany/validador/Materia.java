/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.validador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.mycompany.validador.Validador.conexion;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author neo_0
 */
public class Materia {

    String nombre;

    ArrayList<String> correlativas = new ArrayList<>();

    public Materia(String nombre) {
        this.nombre = nombre;
    }

    public Materia() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<String> getCorrelativas() {
        return correlativas;
    }

    public void setCorrelativas(ArrayList<String> correlativas) {
        this.correlativas = correlativas;
    }

    @Override
    public String toString() {
        return "Materia{" + "nombre=" + nombre + ", correlativas=" + correlativas + '}';
    }
    ////---CREACION DE MATERIAS---

    public static void crearMateria(Scanner sc) throws SQLException, JsonProcessingException {

        Materia materia = new Materia();

        System.out.println("Ingrese el nombre de la materia");
        String nombre = sc.next();
        materia.setNombre(nombre);

        System.out.println("Cuantas correlativas tiene?");
        int numero = sc.nextInt();

        System.out.println("Ingrese los nombres de las correlativas");
        ArrayList<String> correlativas = new ArrayList<>();

        String input;

        for (int i = 0; i < numero; i++) {
            input = sc.next();
            correlativas.add(input);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String correlativasJson = objectMapper.writeValueAsString(correlativas);

        conexion.estableceConexion();
        Statement stmt = conexion.conectar.createStatement();
        stmt.executeUpdate("INSERT INTO materias_final (nombre, correlativas) VALUES ('" + nombre + "', '" + correlativasJson + "');");
        stmt.close();
        conexion.cerrarConnection();

        System.out.println("Materia creada exitosamente");

    }
}
