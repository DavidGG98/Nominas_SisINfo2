/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;


import controlador.CalculoNominas;
import controlador.Datos;
import controlador.GenerarCorreo;
import controlador.GenerarIban;
import controlador.GenerarNifNie;
import java.util.Scanner;

/**
 *
 * @author david
 */
public class Nominas {
    
     public static void main(String[] args) {
         boolean salir=false;
         boolean valido=false;
         Scanner s= new Scanner(System.in);
         String input;
         System.out.println("Bienvenido a la aplicación de nóminas");

         generarDatos(); //Comprobamos NIF/NIE, IBAN y generamos correos
         
        do {
            lineaRayas();
            System.out.println("Introduce una fecha para generar nóminas (Formato mm/aaaa)");
            input=s.nextLine();
            if (input.length()!=7 || input.charAt(2)!=('/')) {
                  System.out.println("La entrada no es correcta (Formato mm/aaaa)");
            } else {
               valido=true;
               String [] date = input.split("/"); //La entrada debe tener un formato mm/yyyy
               int month = Integer.parseInt(date[0]);
               int year = Integer.parseInt(date[1]);
               if (month >12 || month < 1) {
                   System.out.println("El mes introducido no existe");
               } else {
                   System.out.println("Calculo de nóminas hasta el " + month + " de " + year);
                   CalculoNominas c = new CalculoNominas();
                   c.generarNominas(month,year);
               }
            }
           
        } while (!valido);

        lineaRayas();
        System.out.println("¡Hasta pronto!");
        System.exit(0);
     }
     
     public static void lineaRayas () {
         System.out.println("-----------------------------");
     }
     public static void generarDatos() {
          GenerarNifNie.main();
          GenerarCorreo.main();
          GenerarIban.main();
     }
}
