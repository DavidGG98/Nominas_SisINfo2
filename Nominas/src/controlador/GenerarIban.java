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
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import modelo.Categorias;
import modelo.Empresas;
import modelo.Trabajadorbbdd;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author david
 */
public class GenerarIban {

    public static void main() {
        String nombre = "SistemasInformacionII.xlsx";
        String ruta = "C:\\Users\\david\\Documents\\NetBeansProjects\\Nominas\\resources\\" + nombre;
        String hoja = "Hoja1";

        try (FileInputStream excel = new FileInputStream(new File(ruta))) {
            XSSFWorkbook workBook = new XSSFWorkbook(excel);
            // Elegimos la hoja que se pasa por parámetro.
            XSSFSheet sheet = workBook.getSheetAt(0);
            try {
                corrigeCB(sheet);
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error al realizar la generación"
                        + "de los IBAN " + e);
            }

            try (FileOutputStream fileOut = new FileOutputStream(ruta)) {
                workBook.write(fileOut);
                fileOut.close();
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error al escribir " + e);
            }

            excel.close();
        } catch (Exception e) {
            System.out.println("Ha ocurrido una excepción al abrir el excell " + e.getMessage());
        }

    }

    public static void corrigeCB(XSSFSheet sheet) throws ParserConfigurationException, IOException {
        System.out.println("Generando codigo IBAN");
        GeneradorDOM g = new GeneradorDOM(1); //Generador del XML        
        int rows = sheet.getLastRowNum();

        for (int i = 1; i < rows+1; i++) {
            Row row = sheet.getRow(i);
            if (row.getCell(9) != null && !row.getCell(9).equals("")) {
                String codigo = row.getCell(9).getStringCellValue();
                //System.out.println(codigo);
                // COMPROBACIÓN DEL CODIGO DE CUENTA
                if (codigo.length() == 20) {
                    String checked = corrigeCodigo(codigo);
                    // System.out.println("Codigo Corregido");
                    String IBAN = generaIBAN(checked, row.getCell(10).getStringCellValue());
                    //System.out.println(checked);              
                    if (checked.equals(codigo)) {
                        //NADA                        
                    } else { //Se ha corregido                       
                        //Apuntar en XML
                        addErrorCuenta(g,sheet,i,codigo,IBAN);
                        //Corregir en excell
                        row.getCell(9).setCellValue(checked);
                        //System.out.println((i+1) + ".- El codigo " + codigo + " se ha corregido por " + checked);
                    }
                    //Apuntamos el IBAN Generado
                    try {
                        row.createCell(11);
                        row.getCell(11).setCellValue(IBAN);
                    } catch (Exception e) {
                        System.out.println("Ha ocurrido un error al setear el"
                                + "valor de la celda " + (i + 1) + "x" + "I => " + e);
                    }

                } else {
                    System.out.println("El CODIGO DEBE SER DE LONGITUD =20");
                }         
            }
        }
        try {
        g.generaXML("ErroresCCC");
        } catch (Exception e) {
            System.out.println ("Error al generar el XML " + e);
        }

    }
    private static void addErrorCuenta (GeneradorDOM g, XSSFSheet sheet, int i, String CC, String IBAN) {
        try {
            Row row = sheet.getRow(i);
            
            //Creamos un objeto trabajador que es el que es erroneo
            Trabajadorbbdd t= new Trabajadorbbdd();
            Empresas e= new Empresas();           
            e.setNombre(row.getCell(1).getStringCellValue());
            t.setIdTrabajador(i);
            t.setNombre(row.getCell(4).getStringCellValue());
            t.setApellido1(row.getCell(5).getStringCellValue());
            if (row.getCell(6)!= null && row.getCell(6).getStringCellValue() != "") {
            t.setApellido2(row.getCell(6).getStringCellValue());
            }
            t.setEmpresas(e);
            
            g.addCuenta(t, CC, IBAN, i+1);
        } catch (Exception e) {
            System.out.println("Error al añadir una cuenta al XML " + e);
        }
    }

    private static String generaIBAN(String codigo, String pais) {
        DecimalFormat df = new DecimalFormat("00");
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String out = "";
        String[] codigoPais = pais.split("");
        int breaker = 0;
        for (int i = 0; i < abc.length(); i++) {
            if (abc.charAt(i) == codigoPais[0].charAt(0)) {
                codigoPais[0] = Integer.toString(i + 10);
                breaker++;
            }
            if (abc.charAt(i) == codigoPais[1].charAt(0)) {
                codigoPais[1] = Integer.toString(i + 10);
                breaker++;
            }
            if (breaker == 2) {
                break;
            }
        }
        out = codigo.concat(codigoPais[0]).concat(codigoPais[1]).concat("00");

        BigInteger big = new BigInteger(out);
        BigInteger mod = new BigInteger("97");
        big = big.mod(mod); // Numero Modulo 97
        int n = big.intValue();
        n = 98 - n;
        
        out = pais.concat(df.format(n)).concat(codigo);

        return out;
    }
    
    //Comprueba si el código de la cuenta (CC) es correcto y lo corrige
    private static String corrigeCodigo(String codigo) {

        StringBuilder out = new StringBuilder();
        String[] cod = codigo.split("");
        String CE = ""; //Codigo entidad
        for (int i = 0; i < 4; i++) {
            CE = CE.concat(cod[i]);
        }
        String CO = ""; //Codigo Oficina
        for (int i = 4; i < 8; i++) {
            CO = CO.concat(cod[i]);
        }
        String CC = ""; //Codigo cuenta
        for (int i = 10; i < 20; i++) {
            CC = CC.concat(cod[i]);
        }
        String DC = primerDigito(CE, CO).concat(segundoDigito(CC));

        out = out.append(CE).append(CO).append(DC).append(CC);
        //System.out.println(CE + " - " + CO + " - " + DC + " - " + CC);
        return out.toString();
    }

    private static String primerDigito(String CE, String CO) {
        int out = 0;
        String code = "00".concat(CE).concat(CO); //Codigo a comprobar
        int suma = 0;
        int[] factores = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};

        for (int i = 0; i < 10; i++) {
            int n = Integer.parseInt(String.valueOf(code.charAt(i)));
            suma += n * factores[i];
        }
        suma = suma % 11;
        out = 11 - suma;
        if (out == 10) {
            out = 1;
        } else if (out == 11) {
            out = 0;
        }
        return Integer.toString(out);
    }

    private static String segundoDigito(String CC) {
        int out = 0;
        int suma = 0;
        int[] factores = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};
        for (int i = 0; i < 10; i++) {
            int n = Integer.parseInt(String.valueOf(CC.charAt(i)));
            suma += n * factores[i];
        }
        suma = suma % 11;
        out = 11 - suma;
        if (out == 10) {
            out = 1;
        } else if (out == 11) {
            out = 0;
        }
        return Integer.toString(out);
    }
}
