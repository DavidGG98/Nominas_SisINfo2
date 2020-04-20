/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import modelo.Categorias;
import modelo.Empresas;
import modelo.Trabajadorbbdd;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author david
 */
public class UtilsPOI {
    
    private static Character [] controlNIF= {'T','R','W','A','G','M','Y','F','P','D','X','B'
                        ,'N','J','Z','S','Q','V','H','L','C','K','E'};
    private static Character [] controlNIE= {'X','Y','Z'};
    
    final static int columnaNIF=7;
    
    public static void main (String [] args) {
        readExcelFile();
    }
    
     public static void readExcelFile(){
         
        String nombre="SistemasInformacionII.xlsx"; 
        String ruta="C:\\Users\\david\\Documents\\NetBeansProjects\\Nominas\\resources\\" + nombre;
        String hoja="Hoja1";
        
        
        DataFormatter format = new DataFormatter();
               
        //Cargamos el excell
        try (FileInputStream excel = new FileInputStream(new File(ruta))) {  
            
            XSSFWorkbook workBook = new XSSFWorkbook(excel);           
            // Elegimos la hoja que se pasa por parámetro.
            XSSFSheet sheet = workBook.getSheetAt(0);
            checkNIFCorrect(sheet, ruta, workBook);
            checkForDuplicates(sheet);
        } catch (Exception e) {
            System.out.println ("Ha ocurrido una excepción " + e.getMessage());
        }
        
        
     }
     //Metodo que busca NIF/NIE duplicados o vacios
     public static void checkForDuplicates (XSSFSheet sheet) throws ParserConfigurationException, IOException, TransformerException {
        int rows = sheet.getLastRowNum();
        int cols = sheet.getRow(0).getLastCellNum();
        String cellValue;
        ArrayList<String> nif = new ArrayList <> (); //Lista de NIFS/NIE
        ArrayList<Integer> errores = new ArrayList <> (); //Lista de posiciones
        
        for (int i=1;i<rows;i++) {
                if (!isRowEmpty(sheet.getRow(i))) {
                    Row row = sheet.getRow(i);

                        Cell cell = row.getCell(columnaNIF);
                        if (cell!=null) {                           
                            cellValue=cell.getStringCellValue();
                            if (cellValue!="") {
                               //Celda con nif, comprobamos que no se repita
                               if(!nif.contains(cellValue)) {
                                   //Añadimos el NIF a la lista
                                   nif.add(cellValue);
                               } else {
                                   //Añadimos la posicion a la lista de errores
                                   errores.add(i+1);
                               }
                            } else {
                                //System.out.println(i+".- NIF EN BLANCO");
                                //Añadimos la posicion a la lista de errores
                                errores.add(i+1);
                            }
                        } else {
                            //System.out.println(i+".- CELDA VACIA");
                            if (row.getCell(0)!=null) {
                                //System.out.println("Falta dni");
                                errores.add(i+1);
                            }
                        }
                    
                } else {
                    //System.out.println(i+" Fila Vacia ");
                }
        }
        GeneradorDOM g= new GeneradorDOM(0);
        System.out.println("Generamos XML");
        
        for(int i=0;i<errores.size();i++) {
            //System.out.println("Trabajador de columna= " + errores.get(i));
            try {
            Row row = sheet.getRow(errores.get(i));
            //System.out.println(errores.get(i));
            Trabajadorbbdd t= new Trabajadorbbdd();
            Categorias c = new Categorias();
            c.setNombreCategoria(row.getCell(2).getStringCellValue());
            Empresas e= new Empresas();           
            e.setNombre(row.getCell(1).getStringCellValue());
            t.setIdTrabajador(errores.get(i));
            t.setNombre(row.getCell(4).getStringCellValue());
            t.setApellido1(row.getCell(5).getStringCellValue());
            if (row.getCell(6)!= null && row.getCell(6).getStringCellValue() != "") {
            t.setApellido2(row.getCell(6).getStringCellValue());
            }
            t.setCategorias(c);
            t.setEmpresas(e);
            
            g.addTrabajador(t);

            //System.out.println("trabajador creado con exito");
            } catch (Exception e) {
                System.out.println("Error al crear trabajador" + e);
            }
        }
        //GeneradorDOM g= new GeneradorDOM();
        //g.generaDOC();
        g.generaXML("Errores");
     }
     
  
     
     public static void checkNIFCorrect (XSSFSheet sheet, String ruta, XSSFWorkbook workBook) {
                     // Representación del más alto nivel de la hoja excel.
            try {
            // Objeto que nos permite leer un fila de la hoja excel, y de aquí extraer el contenido de las celdas.
            //HSSFRow row;
            // Inicializo el objeto que leerá el valor de la celda
            //HSSFCell cell;                        
            // Obtengo el número de filas ocupadas en la hoja
            int rows = sheet.getLastRowNum();
            //System.out.println("Tenemos estas columnas: " + rows);
            // Obtengo el número de columnas ocupadas en la hoja
            int cols = sheet.getRow(0).getLastCellNum();            
            // Cadena que usamos para almacenar la lectura de la celda
            String cellValue;
            int r=0;
            //Comprobamos que las letras están bien 
            for (int i=1;i<rows;i++) {
                if (!isRowEmpty(sheet.getRow(i))) {
                    Row row = sheet.getRow(i);

                        Cell cell = row.getCell(columnaNIF);
                        if (cell!=null) {
                            if (cell.getStringCellValue()!="") {
                                cellValue=cell.getStringCellValue(); //Valor celda
                                //System.out.println(i+".- Chekeamos el NIF " + cellValue);
                                String nif=checkNIF(cellValue); //Valor correcto
                                                        
                                if (!nif.equals(cellValue)) {
                                    //System.out.println(i+".- NIF Corregido ");
                                    cell.setCellValue(nif);
                                    try (FileOutputStream fileOut = new FileOutputStream(ruta)) {
                                        workBook.write(fileOut);
                                        fileOut.close();
                                    } catch (Exception e) {
                                        System.out.println("Ha ocurrido un error al escribir "+e);
                                    }
                                    
                                }
                                //Corregimos el NIF en la hoja de calculo
                            } else {
                                //System.out.println(i+".- NIF EN BLANCO");
                            }
                        } else {
                            System.out.println(i+".- CELDA VACIA");
                        }
                    
                } else {
                    System.out.println(i+" Fila Vacia ");
                }

            }
            }catch(Exception e) {
                System.out.println("ECEPTION " + e);
            }
     }
     
     public static String checkNIF (String nif) {
         //System.out.println("Chekamos NIF " + nif);
         StringBuilder out = new StringBuilder(nif);
         Character c;
         //System.out.println("Comprobamos si el NIF o nie");
         if (isDigit(nif.charAt(0))) { //Primera posicion Numero
             //Algoritmo NIF
            //System.out.println("Es NIF");
            int n= Integer.parseInt(out.substring(0,(out.length()-1)));
            out.setCharAt(out.length()-1, controlNIF[n%23]);
            //System.out.println("Comprobamos letra " + controlNIF[n%23]);
            if (!out.toString().equals(nif)) {
                System.out.println("Corregimos NIF " + nif + " --> " + out.toString());
            }
         } else if (isLetter(nif.charAt(0))) { //Primera posicion Letra
            //System.out.println("Es NIE");
            //Algoritmo NIE
            c=nif.charAt(0); //Guardamos letra
            switch (c) {
                case 'X':
                    out.setCharAt(0, '0');
                    break;
                case 'Y':
                    out.setCharAt(0, '1');
                    break;
                case 'Z':
                    out.setCharAt(0, '2');
                    break;
            }

             
            int n= Integer.parseInt(out.substring(0,(out.length()-1)));
            out.setCharAt(out.length()-1, controlNIF[n%23]);
            //System.out.println("Comprobamos letra " + controlNIF[n%23]);
            out.setCharAt(0, c); //Devolvemos la letra de la primera pos
            if (!out.toString().equals(nif)) {
                System.out.println("Corregimos NIE " + nif + " --> " + out.toString());
            }
         }
         return out.toString();
     }
     
     public static boolean isRowEmpty (Row row) {
         for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
         if (cell != null)
                return false;
        }
            return true;
     }
    
}
