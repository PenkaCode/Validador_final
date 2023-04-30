/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.validador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author neo_0
 */
public class Conexion {

    Connection conectar = null;

    public Conexion() {
    }

    String usuario = "root";
    String contraseña = "root";
    String bd = "validador";
    String ip = "localhost";
    String puerto = "3306";

    String ruta = "jdbc:mysql://" + ip + ":" + puerto + "/" + bd;

    public Connection estableceConexion() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            conectar = DriverManager.getConnection(ruta, usuario, contraseña);

            System.out.println("Se conecto");

            //JOptionPane.showMessageDialog(null, "Se conecto correctamente");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "NO se conecto correctamente" + e);
        }

        return conectar;

    }

    public void cerrarConnection() throws SQLException {
        try {
            conectar.close();
        } catch (SQLException e) {
        }
    }
}
