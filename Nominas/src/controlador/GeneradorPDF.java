/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import DAO.NominaDAOInterface;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Categorias;
import modelo.Empresas;
import modelo.Nomina;
import modelo.Trabajadorbbdd;


/**
 *
 * @author david
 */
public class GeneradorPDF {
    
    private final String ruta = "resources/nominas/"; 
    private PdfWriter writer;
    private PdfDocument pdfDoc;
    private Document  doc;
    private final DecimalFormat df = new DecimalFormat("######0.00");
    
    public GeneradorPDF(){
       
    }
    public void modificacionFinal (int m, int y) {
        System.out.println("Generamos el pDF");
        String nombre="DavidGonzalezGarcia_71464871F";
        File file = new File (ruta + nombre + ".pdf"); //Creamos el nuevo PDF
        try {
            writer = new PdfWriter (ruta + nombre + ".pdf");//Escribimos en el archivo
            pdfDoc=new PdfDocument (writer);
            doc = new Document (pdfDoc, PageSize.LETTER);
            DatosBBDD db = new DatosBBDD ();
            List <Categorias> c=db.getAllCategorias();
            List <Trabajadorbbdd> t=db.getAllTrabajadores();
            
            int [] repeticionesCategoria = new int [c.size()];
            for (Trabajadorbbdd aux:t) {
                Categorias cat=aux.getCategorias();
                if ( c.contains(cat) ) {
                    repeticionesCategoria [c.indexOf(cat)] +=1;
                    //c.indexOf(c)
                }              
            }
            int posMax=0;
            int posMin=0;
            int max = 0;
            int min = Integer.MAX_VALUE;
            for (int i=0;i<repeticionesCategoria.length;i++) {
                if (repeticionesCategoria[i]>max) {
                    posMax=i;
                    max = repeticionesCategoria[i];
                } else if(repeticionesCategoria[i]<min) {
                    posMin=i;
                    min = repeticionesCategoria[i];
                }
            }
            
            Table tabla = new Table (1);
            tabla.setWidth(500);
            tabla.setMarginTop(15);
            
            Cell c1 = new Cell();
            c1.setTextAlignment(TextAlignment.LEFT);
            c1.add(new Paragraph("Categoria con más trabajadores es: " + c.get(posMax).getNombreCategoria() + " con " + max + " trabajadores"));
            
            Cell c2 = new Cell();
            c2.setTextAlignment(TextAlignment.LEFT);
            c2.add(new Paragraph("Categoria con menos trabajadores es: " + c.get(posMin).getNombreCategoria() + " con " + min + " trabajadores"));
            
            tabla.addCell(c1);
            tabla.addCell(c2);
            doc.add(tabla);
            
            System.out.println("La categoria con más repeticiones es " + c.get(posMax).getNombreCategoria() + " con " + max);
            System.out.println("La categoria con menos repeticiones es " + c.get(posMin).getNombreCategoria() + " con " + min);
            
            //APARTADO C
            Nomina n = db.getNominaMenorLiquido(m, y);
            
            tabla = new Table (2);
            tabla.setWidth(500);
            tabla.setMarginTop(15);
            tabla.setBorder(Border.NO_BORDER);
            c1= new Cell();
            c1.add(new Paragraph("ID: " + n.getIdNomina()));
            c1.add(new Paragraph("Nombre: " + n.getTrabajadorbbdd().getNombre()));
            c1.add(new Paragraph("Apellido: " +n.getTrabajadorbbdd().getApellido1() + " " +n.getTrabajadorbbdd().getApellido2()));
            c1.add(new Paragraph("Categoria: " +n.getTrabajadorbbdd().getCategorias().getNombreCategoria()));
            c1.add(new Paragraph("Empresa: " + n.getTrabajadorbbdd().getEmpresas().getNombre()));
            c1.add(new Paragraph("Liquido: " + n.getLiquidoNomina()));

            
            doc.add(c1);
            
            tabla = new Table (2);
            tabla.setWidth(500);
            tabla.setMarginTop(15);
            tabla.setBorder(Border.NO_BORDER);
            c1 = new Cell();
            c1.add(new Paragraph("Sentencia HQL: "));
            c1.add(new Paragraph("SELECT n FROM Nomina n WHERE n.mes=:mes AND n.anio=:anio  ORDER BY n.liquidoNomina asc"));
            tabla.addCell(c1);
            doc.add(tabla);
            doc.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GeneradorPDF.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al abrir el PDF " + ex.getMessage());
        }
       
    }
    
    public void nuevoPDF (boolean extra, Nomina n, int m, int y) {
         Trabajadorbbdd t = n.getTrabajadorbbdd();
         if (t.getApellido2() == null) {
             t.setApellido2("");
         }
         try {
            String nombre = t.getNifnie() + t.getNombre() + t.getApellido1() + t.getApellido2() + mes(m) + y;
            if (extra) {
                nombre = nombre.concat("EXTRA");
            }
            File file = new File(ruta + nombre + ".pdf"); //Creamos el archivo
            
            writer = new PdfWriter (ruta + nombre + ".pdf");//Escribimos en el archivo
            pdfDoc=new PdfDocument (writer);
            doc = new Document (pdfDoc, PageSize.LETTER);
            
            cuadroEmpresa(doc, n);
            cuadroDestinatario(doc, t);
            cuadroNomina(doc,n,extra);
            cuadroBase(doc,n);
            cuadroEmpresario(doc,n);
            cuadroFinal(doc,n);

            doc.close();
        } catch (Exception ex) {
            System.out.println("Error al escribir en el PDF " + ex.getMessage());
        }
        
    }
    
    private void cuadroFinal (Document d, Nomina n) {
        Table t= new Table (2);
        t.setWidth(500);
        t.setMarginTop(15);

        t.setBorder(new SolidBorder(4));
        
        Cell c1 = new Cell();
        c1.setBorder(Border.NO_BORDER);
        c1.setTextAlignment(TextAlignment.LEFT);
        
        c1.add(new Paragraph ("COSTE TOTAL TRABAJADOR").setBold().setFontColor(new DeviceRgb(254,000,000)));
        t.addCell(c1);
        
        Cell c2 = new Cell();
        c2.setBorder(Border.NO_BORDER);
        c2.setTextAlignment(TextAlignment.RIGHT);
        
        c2.add(new Paragraph (df.format(n.getCosteTotalEmpresario())).setBold().setFontColor(new DeviceRgb(254,000,000)));
        t.addCell(c2);
        
        doc.add(t);
    }
    
    private void cuadroEmpresario (Document d, Nomina n) {
        Table t= new Table (2);
        t.setWidth(500);
        t.setMarginTop(15);
        t.setFontColor(new DeviceRgb(156,156,156));
        t.setBorder(new SolidBorder(new DeviceRgb(156,156,156), 1));
        t.setBorderLeft(Border.NO_BORDER);
        t.setBorderRight(Border.NO_BORDER);
        t.setBorderTop(Border.NO_BORDER);
        
        Cell c1 = new Cell();
        c1.setBorder(Border.NO_BORDER);
        c1.setTextAlignment(TextAlignment.LEFT);
       
        c1.add(new Paragraph ("Contingencias comunes empresario: " + df.format(n.getSeguridadSocialEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c1.add(new Paragraph ("Desempleo: " + df.format(n.getDesempleoEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c1.add(new Paragraph ("Formación: " + df.format(n.getFormacionEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c1.add(new Paragraph ("Accidentes de trabajo " + df.format(n.getAccidentesTrabajoEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c1.add(new Paragraph ("FOGASA: " + df.format(n.getFogasaempresario())).setFontColor(new DeviceRgb(156,156,156)));
        t.addCell(c1);
        
        Cell c2 = new Cell();
        c2.setBorder(Border.NO_BORDER);
        c2.setTextAlignment(TextAlignment.RIGHT);
        c2.add(new Paragraph (df.format(n.getImporteSeguridadSocialEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c2.add(new Paragraph (df.format(n.getImporteDesempleoEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c2.add(new Paragraph (df.format(n.getImporteFormacionEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c2.add(new Paragraph (df.format(n.getImporteAccidentesTrabajoEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        c2.add(new Paragraph (df.format(n.getImporteFogasaempresario())).setFontColor(new DeviceRgb(156,156,156)));
        t.addCell(c2);
        
        Table t2= new Table (2);
        t2.setWidth(500);
        t2.setFontColor(new DeviceRgb(156,156,156));
        t2.setBorder(new SolidBorder(new DeviceRgb(156,156,156), 1));
        t2.setBorderLeft(Border.NO_BORDER);
        t2.setBorderRight(Border.NO_BORDER);
        t2.setBorderTop(Border.NO_BORDER);
        
        c1 = new Cell();
        c1.setBorder(Border.NO_BORDER);
        c1.setTextAlignment(TextAlignment.LEFT);
        c1.add(new Paragraph ("Total empresario ").setFontColor(new DeviceRgb(156,156,156)));
        t2.addCell(c1);
        c2= new Cell();
        c2.setBorder(Border.NO_BORDER);
        c2.setTextAlignment(TextAlignment.RIGHT);
        double sumaGasto = n.getImporteSeguridadSocialEmpresario()+n.getImporteFormacionEmpresario()
                +n.getImporteDesempleoEmpresario()+n.getImporteAccidentesTrabajoEmpresario()+n.getImporteFogasaempresario();
        c2.add(new Paragraph (df.format(sumaGasto)).setFontColor(new DeviceRgb(156,156,156)));
        t2.addCell(c2);
        doc.add(t);
        doc.add(t2);
        
    }
    
    private void cuadroBase (Document d, Nomina n) {
        Table t= new Table (2);
        t.setWidth(500);
        t.setMarginTop(15);
        t.setFontColor(new DeviceRgb(156,156,156));
        t.setBorder(new SolidBorder(new DeviceRgb(156,156,156), 1));
        t.setBorderLeft(Border.NO_BORDER);
        t.setBorderRight(Border.NO_BORDER);
        
        Cell c1 = new Cell();
        c1.setBorder(Border.NO_BORDER);
        c1.setTextAlignment(TextAlignment.LEFT);
        c1.add(new Paragraph ("Calculo empresario: BASE").setFontColor(new DeviceRgb(156,156,156)));
        t.addCell(c1);
        
        Cell c2 = new Cell();
        c2.setBorder(Border.NO_BORDER);
        c2.setTextAlignment(TextAlignment.RIGHT);
        c2.add(new Paragraph (df.format(n.getBaseEmpresario())).setFontColor(new DeviceRgb(156,156,156)));
        t.addCell(c2);
        
        doc.add(t);
        
    }
    
    private void cuadroNomina (Document d, Nomina n, boolean extra) {
        Paragraph empty = new Paragraph("Vacio");
        
        Table fechaN = new Table (1);
        fechaN.setWidth(500);
        fechaN.setMarginTop(10);
        
        String string = ("Fecha Nómina: " + mes(n.getMes()) + " de " + n.getAnio());
        if (extra) {
            string=string.concat(" EXTRA ");
            
        }
        Paragraph fecha = new Paragraph (string);
        fecha.setBold();
        Cell c = new Cell ();
        c.setTextAlignment(TextAlignment.LEFT);
        c.add(fecha);
        c.setBorder(Border.NO_BORDER);
        fechaN.addCell(c);
        doc.add(fechaN);
        
        
        
        Table contenido = new Table (4);
        contenido.setWidth(500);
        contenido.setBorderTop(new SolidBorder (1));
        contenido.setBorderLeft(Border.NO_BORDER);
        contenido.setBorderRight(Border.NO_BORDER);

        Paragraph p = new Paragraph ("pruebas");
        
        
        Cell conceptos = new Cell();
        conceptos.setBorder(Border.NO_BORDER);
        //conceptos.setWidth(50);
        conceptos.add(new Paragraph ("Conceptos").setBold().setBorderBottom(new SolidBorder(1)));
        
        conceptos.add(new Paragraph ("Salario Base"));
        conceptos.add(new Paragraph ("Prorrateo"));
        conceptos.add(new Paragraph ("Complemento"));
        conceptos.add(new Paragraph ("Antiguedad"));
        conceptos.add(new Paragraph ("Contingencias generales"));
        conceptos.add(new Paragraph ("Desempleo"));
        conceptos.add(new Paragraph ("Cuota de formación"));
        conceptos.add(new Paragraph ("IRPF").setBorderBottom(new SolidBorder(2)));
        conceptos.add(new Paragraph ("Total deducciones"));
        conceptos.add(new Paragraph ("Total devengos").setBorderBottom(new SolidBorder(1)));
        conceptos.add(new Paragraph ("Líquido a percibir").setBold());
        
        contenido.addCell(conceptos);
        
        Cell cantidad = new Cell();
        cantidad.add(new Paragraph ("Cantidad").setBold().setBorderBottom(new SolidBorder(1)));
        cantidad.setBorder(Border.NO_BORDER);
        cantidad.add(new Paragraph(" 30 Días "));
        cantidad.add(new Paragraph(" 30 Días "));
        cantidad.add(new Paragraph(" 30 Días "));
        cantidad.add(new Paragraph(n.getNumeroTrienios() + " trienios"));
        
        cantidad.add(new Paragraph(n.getSeguridadSocialTrabajador() + "% de " + df.format(n.getBaseEmpresario())));
        cantidad.add(new Paragraph(n.getDesempleoTrabajador() + "% de " + df.format(n.getBaseEmpresario())));
        cantidad.add(new Paragraph(n.getFormacionTrabajador() + "% de " + df.format(n.getBaseEmpresario())));
        cantidad.add(new Paragraph(n.getIrpf()+ "% de " + df.format(n.getBrutoNomina())).setBorderBottom(new SolidBorder(2)));
        cantidad.add(new Paragraph ("\n"));
        cantidad.add(new Paragraph ("\n").setBorderBottom(new SolidBorder(1)));
        cantidad.add(new Paragraph (df.format(n.getLiquidoNomina())).setBold());
        
        contenido.addCell(cantidad);
        
        Cell devengo = new Cell();
        devengo.add(new Paragraph ("Devengo").setBold().setBorderBottom(new SolidBorder(1)));
        devengo.setBorder(Border.NO_BORDER);
        devengo.add(new Paragraph(df.format(n.getImporteSalarioMes())));
        devengo.add(new Paragraph(df.format(n.getValorProrrateo())));
        devengo.add(new Paragraph(df.format(n.getImporteComplementoMes())));
        devengo.add(new Paragraph(df.format(n.getImporteTrienios())));
        devengo.add(new Paragraph ("\n"));
        devengo.add(new Paragraph ("\n"));
        devengo.add(new Paragraph ("\n"));
        devengo.add(new Paragraph ("\n").setBorderBottom(new SolidBorder(2)));
        devengo.add(new Paragraph ("\n"));
        devengo.add(new Paragraph (df.format(n.getBrutoNomina())).setBorderBottom(new SolidBorder(1)));
        devengo.add(new Paragraph ("\n"));
        contenido.addCell(devengo);
        
        Cell deduccion = new Cell();
        deduccion.add(new Paragraph ("Deducciones").setBold().setBorderBottom(new SolidBorder(1)));
        deduccion.setBorder(Border.NO_BORDER);
        deduccion.add(new Paragraph("\n"));
        deduccion.add(new Paragraph("\n"));
        deduccion.add(new Paragraph("\n"));
        deduccion.add(new Paragraph("\n"));
        
        deduccion.add(new Paragraph(df.format(n.getImporteSeguridadSocialTrabajador())));
        deduccion.add(new Paragraph(df.format(n.getImporteDesempleoTrabajador())));
        deduccion.add(new Paragraph(df.format(n.getImporteFormacionTrabajador())));
        deduccion.add(new Paragraph(df.format(n.getImporteIrpf())).setBorderBottom(new SolidBorder(2)));
        deduccion.add(new Paragraph(df.format(n.getBrutoNomina()-n.getLiquidoNomina())));
        deduccion.add(new Paragraph("\n").setBorderBottom(new SolidBorder(1)));
        deduccion.add(new Paragraph("\n"));
        contenido.addCell(deduccion);
                
        doc.add(contenido);
       
    }
    
    private void cuadroDestinatario (Document d, Trabajadorbbdd t) {
        Paragraph empty = new Paragraph("");
        Table tabla = new Table(2);
        tabla.setWidth(500);
        //tabla.setMarginTop(10);
        Paragraph nombre = new Paragraph ("Nombre: " + t.getNombre() + " " + t.getApellido1() + " " + t.getApellido2());
        Paragraph dni = new Paragraph ("DNI: " + t.getNifnie());
        Paragraph email = new Paragraph("Email: " + t.getEmail());
        Paragraph dir1 = new Paragraph("Avenida de la facultad - 6");
        Paragraph dir2 = new Paragraph("24001 León");
        Paragraph destinatario = new Paragraph ("Destinatario");
        destinatario.setBold();
        
        
        Cell cell3 = new Cell();
        cell3.setBorder(Border.NO_BORDER);
        cell3.setPaddingLeft(23);
        cell3.setPaddingTop(20);
        cell3.setWidth(250);
        
        tabla.addCell(cell3);
        
        Cell cell = new Cell();
        cell.setBorder(new SolidBorder(1));
        cell.setTextAlignment(TextAlignment.LEFT);
        cell.setPadding(10);
        
        cell.add(destinatario);
        cell.add(nombre);
        cell.add(dni);
        //cell.add(email);
        cell.add(dir1);
        cell.add(dir2);
        tabla.addCell(cell);
        
        doc.add(tabla);
        
    }
    
    private void cuadroEmpresa (Document d, Nomina n) {
        Trabajadorbbdd t = n.getTrabajadorbbdd();
        String fechaAlta = t.getFechaAlta().getDate() + "/"+(t.getFechaAlta().getMonth()+1)+"/"+(t.getFechaAlta().getYear()+1900);

        Paragraph empty = new Paragraph("");
        Table tabla1 = new Table(2);
        tabla1.setWidth(500);

        Paragraph nom = new Paragraph("NOMBRE: " + t.getEmpresas().getNombre());
        Paragraph cif = new Paragraph("CIF: " + t.getEmpresas().getCif());

        Paragraph dir1 = new Paragraph("Avenida de la facultad - 6");
        Paragraph dir2 = new Paragraph("24001 León");

        Cell cell1 = new Cell();
        cell1.setBorder(new SolidBorder(1));
        cell1.setWidth(250);
        cell1.setTextAlignment(TextAlignment.CENTER);

        cell1.add(nom);
        cell1.add(cif);
        cell1.add(dir1);
        cell1.add(dir2);
        tabla1.addCell(cell1);

        Cell cell2 = new Cell();
        cell2.setBorder(Border.NO_BORDER);
        cell2.setPadding(10);
        cell2.setTextAlignment(TextAlignment.RIGHT);
        cell2.add(new Paragraph("IBAN: " + t.getIban()));
        cell2.add(new Paragraph("Bruto anual: " + df.format(n.getBrutoAnual())));
        cell2.add(new Paragraph("Categoría: " + t.getCategorias().getNombreCategoria()));
        cell2.add(new Paragraph("Fecha de alta: "+ fechaAlta));
        tabla1.addCell(cell2);

        doc.add(tabla1);
        
    }
    
    private String mes (int n) {
        switch (n) {
            case 1: return "Enero";
            case 2: return "Febrero";
            case 3: return "Marzo";
            case 4: return "Abril";
            case 5: return "Mayo";
            case 6: return "Junio";
            case 7: return "Julio";
            case 8: return "Agosto";
            case 9: return "Septiembre";
            case 10: return "Octubre";
            case 11: return "Noviembre";
            case 12: return "Diciembre";
        }
        return "Nada";
    }
}
