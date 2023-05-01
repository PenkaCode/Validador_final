/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.validador;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.mycompany.validador.Validador.conexion;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author neo_0
 */
public class Inscripcion {

    Materia materia;

    Alumno alumno;

    Date fecha = new Date();

    public Inscripcion() {
    }

    public Inscripcion(Materia materia, Alumno alumno) {
        this.materia = materia;
        this.alumno = alumno;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Inscripcion{" + "materia=" + materia + ", alumno=" + alumno + ", fecha=" + fecha + '}';
    }

    public static void inscribirAlumno(Scanner sc) throws SQLException, IOException {
    //Ingresar numero de legajo
    int legajo = 0;
    while (true) {
        System.out.println("Ingrese el número de legajo del alumno:");
        String input = sc.next();
        try {
            legajo = Integer.parseInt(input);
            if (input.length() != 5) {
                System.out.println("El número de legajo debe tener 5 dígitos");
                continue;
            }
            break;
        } catch (NumberFormatException e) {
            System.out.println("El número de legajo debe ser numérico");
        }
    }

    // Obtener el nombre del alumno correspondiente al legajo ingresado
    conexion.estableceConexion();
    Statement stmt = conexion.conectar.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT nombre FROM alumnos WHERE legajo=" + legajo);

    String nombreAlumno = "";
    if (rs.next()) {
        nombreAlumno = rs.getString("nombre");
    } else {
        System.out.println("El alumno con el legajo ingresado no existe");
        stmt.close();
        conexion.cerrarConnection();
        return;
    }
    stmt.close();
    conexion.cerrarConnection();

        //Ingresar materia a inscribirse
        
        System.out.println("Ingrese el nombre de la materia a la que desea inscribir al alumno:");
        String nombreMateria = sc.next();

        // Verificar si la materia existe en la tabla Materia
        conexion.estableceConexion();
        stmt = conexion.conectar.createStatement();
        ResultSet rsMateria = stmt.executeQuery("SELECT * FROM materias_final WHERE nombre='" + nombreMateria + "'");
        if (!rsMateria.next()) {
            System.out.println("La materia ingresada no existe");
            stmt.close();
            conexion.cerrarConnection();
            return;
        }
        stmt.close();
        conexion.cerrarConnection();

        // Verificar si el alumno ya está inscripto en la materia
        conexion.estableceConexion();
        stmt = conexion.conectar.createStatement();
        ResultSet rsInscripcion = stmt.executeQuery("SELECT * FROM inscripciones WHERE legajo=" + legajo + " AND materia='" + nombreMateria + "'");
        if (rsInscripcion.next()) {
            System.out.println("El alumno ya está inscripto en la materia " + nombreMateria);
            stmt.close();
            conexion.cerrarConnection();
            return;
        }
        stmt.close();
        conexion.cerrarConnection();

        // Validar las materias correlativas
        if (!validadorCorrelativas(legajo, nombreMateria)) {
            return;
        }

        // Inscribir al alumno en la materia
        conexion.estableceConexion();
        stmt = conexion.conectar.createStatement();
        stmt.executeUpdate("INSERT INTO inscripciones (legajo, alumno, materia, fecha_inscripcion) VALUES (" + legajo + ", '" + nombreAlumno + "', '" + nombreMateria + "', NOW())");
        stmt.close();
        conexion.cerrarConnection();

        System.out.println("Alumno " + nombreAlumno + " inscripto exitosamente en la materia " + nombreMateria + " el día de hoy");

    }

    public static boolean validadorCorrelativas(int legajo, String nombreMateria) throws SQLException, IOException {
        // Obtener las materias correlativas de la materia a la que se quiere inscribir al alumno
        ArrayList<String> correlativas = new ArrayList<>();
        conexion.estableceConexion();
        Statement stmt = conexion.conectar.createStatement();
        ResultSet rsCorrelativas = stmt.executeQuery("SELECT correlativas FROM materias_final WHERE nombre='" + nombreMateria + "'");
        if (rsCorrelativas.next()) {
            String correlativasStr = rsCorrelativas.getString("correlativas");
            // Parsear el JSON a un ArrayList
            ObjectMapper mapper = new ObjectMapper();
            correlativas = mapper.readValue(correlativasStr, new TypeReference<ArrayList<String>>() {
            });
        }
        stmt.close();
        conexion.cerrarConnection();

        // Obtener las materias aprobadas del alumno
        conexion.estableceConexion();
        stmt = conexion.conectar.createStatement();
        ResultSet rsMateriasAprobadas = stmt.executeQuery("SELECT aprobadas FROM alumnos WHERE legajo=" + legajo);
        List<String> materiasAprobadas = new ArrayList<>();
        if (rsMateriasAprobadas.next()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = rsMateriasAprobadas.getString("aprobadas");
            materiasAprobadas = objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        }
        stmt.close();
        conexion.cerrarConnection();

        // Verificar si el alumno aprobó las materias correlativas
        for (String correlativa : correlativas) {
            if (!materiasAprobadas.contains(correlativa)) {
                System.out.println("El alumno no ha aprobado la materia correlativa " + correlativa);
                return false;
            }
        }
        return true;
    }
}
