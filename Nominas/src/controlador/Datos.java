/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import static controlador.GenerarCorreo.generaCorreos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author david
 */
public class Datos {
   
    private static HashMap <String, Integer> baseCategoria = new HashMap <String, Integer> ();
    private static HashMap <String, Integer> complementoCategoria = new HashMap <String, Integer> ();
    private static HashMap <Integer, Double> retencion = new HashMap <Integer, Double> ();
    private static HashMap <String, Double> cuotas = new HashMap <String, Double> ();
    private static HashMap <Integer, Integer> trienios = new HashMap <Integer, Integer> ();
    
   public Datos ()  {
      //System.out.println("Cargando");
      cargar();
    }
    
    private void cargar () {
        //System.out.println("Cargando");
        String nombre="SistemasInformacionII.xlsx"; 
        String ruta="resources/" + nombre;
        String hoja="Hoja1";
        DataFormatter formatter = new DataFormatter(); 
        try (FileInputStream excel = new FileInputStream(new File(ruta))) {             
            XSSFWorkbook workBook = new XSSFWorkbook(excel);           
            // Elegimos la hoja que se pasa por parámetro.
            XSSFSheet sheet = workBook.getSheetAt(1); 
            
            int rows = sheet.getLastRowNum();
            //System.out.println(rows);
            int i=0;
            //CARGA BASE CATEGORIA
            try {
            for (i=1; i<rows+1;i++) {
                Row row = sheet.getRow(i);
                if (row.getCell(0)!=null && row.getCell(1)!=null && row.getCell(2)!=null) {
                    //System.out.println(row.getCell(0).getStringCellValue());
                    baseCategoria.put(row.getCell(0).getStringCellValue(),(int) row.getCell(1).getNumericCellValue());
                    complementoCategoria.put(row.getCell(0).getStringCellValue(), (int) row.getCell(2).getNumericCellValue());
                }
            }  
            } catch (Exception e) {
                System.out.println("Error al cargar los salarios (Fila: "+ i + ")"+ e.getMessage());
            }
            //CARGA RETENCION
            sheet = workBook.getSheetAt(2);
            rows = sheet.getLastRowNum();
            try {
            for (i=1;i<rows+1;i++) {
                Row row= sheet.getRow(i);
                if (row.getCell(0)!=null && row.getCell(1)!=null) {
                    //System.out.println(row.getCell(0).getNumericCellValue());
                    retencion.put((int) row.getCell(0).getNumericCellValue(), row.getCell(1).getNumericCellValue());
                }                 
            }
            } catch (Exception e) {
                System.out.println("Error al cargar las retenciones (Fila: "+ i + ")"+ e.getMessage());
            }
            //CARGA CUOTAS
            try{
            sheet = workBook.getSheetAt(3);
            rows = sheet.getLastRowNum();
            
            for (i=0;i<rows+1;i++) {
                Row row= sheet.getRow(i);
                if (row!=null) {
                if (row.getCell(0)!=null && row.getCell(1)!=null) {
                    //System.out.println(row.getCell(0).getStringCellValue());
                    cuotas.put( row.getCell(0).getStringCellValue(), row.getCell(1).getNumericCellValue());
                }    
                }
            }
            } catch (Exception e) {
                System.out.println("Error al cargar las cuotas (Fila: "+ i + ") "+ e.getMessage());
            }
            //CARGA TRIENIOS
            
            sheet = workBook.getSheetAt(4);
            rows = sheet.getLastRowNum();
            try {
            for (i=1;i<rows+1;i++) {
                Row row= sheet.getRow(i);
                if (row.getCell(0)!=null && row.getCell(1)!=null) {
                    //System.out.println(row.getCell(0).getNumericCellValue());
                    trienios.put( (int) row.getCell(0).getNumericCellValue(), (int) row.getCell(1).getNumericCellValue());
                }                 
            }
            } catch (Exception e) {
                System.out.println("Error al cargar los trienios (Fila: "+ i + ")"+ e.getMessage());
            }
        } catch (Exception e) {
            System.out.println ("Ha ocurrido una excepción al abrir el excell " + e.getMessage());
        }
    }

    public HashMap<String, Integer> getBaseCategoria() {
        return baseCategoria;
    }

    public HashMap<String, Integer> getComplementoCategoria() {
        return complementoCategoria;
    }

    public HashMap<Integer, Double> getRetencion() {
        return retencion;
    }

    public HashMap<String, Double> getCuotas() {
        return cuotas;
    }

    public HashMap<Integer, Integer> getTrienios() {
        return trienios;
    }

    public void setBaseCategoria(HashMap<String, Integer> baseCategoria) {
        this.baseCategoria = baseCategoria;
    }

    public void setComplementoCategoria(HashMap<String, Integer> complementoCategoria) {
        this.complementoCategoria = complementoCategoria;
    }

    public void setRetencion(HashMap<Integer, Double> retencion) {
        this.retencion = retencion;
    }

    public void setCuotas(HashMap<String, Double> cutoas) {
        this.cuotas = cutoas;
    }

    public void setTrienios(HashMap<Integer, Integer> trienios) {
        this.trienios = trienios;
    }

    @Override
    public String toString() {
        return "CargaDatos{\n" + "baseCategoria=" + baseCategoria + ",\n"
                + " complementoCategoria=" + complementoCategoria + ",\n"
                + " retencion=" + retencion + ",\n cuotas=" + cuotas + ",\n"
                + " trienios=" + trienios + "\n}";
    }
    
}