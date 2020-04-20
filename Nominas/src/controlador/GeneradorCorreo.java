/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import static controlador.UtilsPOI.isRowEmpty;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang3.StringUtils;
/**
 *
 * @author david
 */


public class GeneradorCorreo {
    
    
    
    public static void main (String [] args) {
        String nombre="SistemasInformacionII.xlsx"; 
        String ruta="C:\\Users\\david\\Documents\\NetBeansProjects\\Nominas\\resources\\" + nombre;
        String hoja="Hoja1";
        
        try (FileInputStream excel = new FileInputStream(new File(ruta))) {             
            XSSFWorkbook workBook = new XSSFWorkbook(excel);           
            // Elegimos la hoja que se pasa por parámetro.
            XSSFSheet sheet = workBook.getSheetAt(0);  
            try {
            generaCorreos(sheet);
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error al realizar la generación"
                        + "de los correos " + e);
            }
            try (FileOutputStream fileOut = new FileOutputStream(ruta)) {
                workBook.write(fileOut);
                fileOut.close();
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error al escribir "+e);
            }
        } catch (Exception e) {
            System.out.println ("Ha ocurrido una excepción al abrir el excell " + e.getMessage());
        }
        
    }
    /*
    * Nombre => Columna 4
    * Apellido1 = Columna 5
    * Apellido 2= Columna 6
    * Email = Columna 8
    */
    public static void generaCorreos (XSSFSheet sheet) {
        ArrayList <String> correos = new ArrayList <> (); 
        int rows = 1;
        //Hacemos una primera pasada para meter en la lista todos los correos que ya existan
        boolean isLast=false;
        //Además, contamos cual es la ultima fila del excell, ya que el metodo getLastRow no 
        //funciona como se espera
        while (!isLast) {
            //System.out.println (rows);
            try {
                if (!isRowEmpty(sheet.getRow(rows))) {
                   Row row = sheet.getRow(rows);
                   Cell cell = row.getCell(8);
                   if (cell != null && !"".equals(cell.getStringCellValue())) {
                       //System.out.println("Correo existente");
                       correos.add(cell.getStringCellValue()); //añadimos el correo
                   }
                }
                rows++;
            } catch (Exception e) {
                isLast=true;
            }
        }
       // System.out.println(rows);
        //System.out.println("Hemos recogido los correos ya existentes");
       
        for (int i=1;i<rows;i++) {
            if (!isRowEmpty(sheet.getRow(i))) {
                //System.out.println("Fila "+ (i+1));
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(4);
                if (cell != null && !"".equals(cell.getStringCellValue())) {
                    //System.out.println("Existe un trabajador");
                    cell= row.getCell(8); //chekeamos que no tiene correo
                    if (cell == null || cell.getStringCellValue().equals("")) {
                        //System.out.println("El trabajador no tiene correo");
                        boolean valido = false;
                        String empresa = row.getCell(1).getStringCellValue();
                        String nombre=row.getCell(4).getStringCellValue();
                        String a1=row.getCell(5).getStringCellValue();
                        String a2="";
                        if (row.getCell(6) != null) {
                            a2=row.getCell(6).getStringCellValue();
                        } else {
                            a2="";
                        }
                        int n=0;
                        
                        do {
                        //System.out.println("Asignamos un nuevo correo para " + nombre + " " + a1 + " " +a2);
                        String correo = nombreCorreo (nombre, a1, a2, n, empresa)
                                .concat("@").concat(empresa.toLowerCase()).concat(".es");
                        //comprobamos que no existe
                        if (!correos.contains(correo) && correo!=null && !correo.equals("")) {
                           // System.out.println("El correo generado es "+correo);
                            //añadimos el correo a la lista
                            correos.add(correo); 
                            //escribimos el correo
                            try {
                            row.createCell(8);
                            row.getCell(8).setCellValue(correo);
                            } catch (Exception e) {
                                System.out.println("Ha ocurrido un error al setear el"
                                        + "valor de la celda " + (i+1) + "x" + "I => " + e);
                            }
                            valido=true;
                        } else { //Ya existe
                            n++;
                        }
 
                        } while (!valido);
                        //System.out.println("Correo generado con éxito");
                    } else {
                        //System.out.println("Ya existe un correo para el trabajador " + i);
                    }
                } else {
                    //System.out.println("La celda " + i + " es nula / está vacía");
                }
             }
        }
      
    }

    public static String nombreCorreo (String nombre, String a1, String a2, int n, String empresa) {
        DecimalFormat df = new DecimalFormat ("00"); 
        String out = "";
        String [] aux;
        aux= StringUtils.stripAccents(a1).split(""); //Apellido 1
        out = out.concat(aux[0].toLowerCase());
        aux= StringUtils.stripAccents(a2).split(""); //Apellido 2
        out = out.concat(aux[0].toLowerCase());
        aux = StringUtils.stripAccents(nombre).split(""); //Nombre
        out = out.concat(aux[0].toLowerCase());
        out=out.concat(df.format(n)); //Numero en formato 00
        return out;
    }
    
}
