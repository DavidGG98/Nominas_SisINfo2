/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import static controlador.GenerarNifNie.isRowEmpty;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.text.DecimalFormat;
import modelo.Categorias;
import modelo.Empresas;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
/**
 *
 * @author david
 */
public class CalculoNominas {
    
    private String nombre="SistemasInformacionII.xlsx"; 
    private String ruta="C:\\Users\\david\\Documents\\NetBeansProjects\\Nominas\\resources\\" + nombre;
    private String hoja="Hoja1";
    private XSSFSheet sheet;
    private Datos d;
    private final DecimalFormat df = new DecimalFormat("######0.00");
    
    private Categorias categoria;
    private Trabajadorbbdd trabajador;
    private Empresas empresa;
    private Nomina nomina;
    private DatosBBDD dbbdd;
    private GeneradorPDF pdf;
    
    public void generarNominas (int m, int y) {        
        try (FileInputStream excel = new FileInputStream(new File(ruta))) {             
            XSSFWorkbook workBook = new XSSFWorkbook(excel);           
            // Elegimos la hoja que se pasa por parámetro.
            sheet = workBook.getSheetAt(0);     
            int rows=sheet.getLastRowNum();
            d = new Datos ();
            pdf= new GeneradorPDF();
            dbbdd=new DatosBBDD();
            for (int i=1;i<=rows;i++) {
                try {
                    if (tieneNomina(i,m,y)) {
                        //System.out.println("El trabajador " + (i+1) +" tiene nómina");
                        System.out.println("Generando nomina para la fila " + (i));
                        calculaNomina(i,m,y);
                    } else {
                        System.out.println("El trabajador " + (i) +" NO tiene nómina");
                    }
                } catch (Exception e) {
                    //System.out.println("Ha ocurrido un error en la fila "+ (i+1) + " " + e.getMessage());
                }
            }
            
            try {
                dbbdd.insertaDatos();
                pdf.modificacionFinal(m,y);
            } catch (Exception e) {
                System.out.println("ERROR AL INTRODUCIR LOS DATOS EN LA BASE DE DATOS " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println ("Ha ocurrido una excepción al abrir el excell " + e.getMessage());
        }
         
    }
    
    /*
    * Devuelve una lista con con la posicion en la tabla excell de los trabajadores
    * para los que debemos generar una nomina
    */
    
    private boolean tieneNomina (int r, int m, int y) {
        if (isRowEmpty(sheet.getRow(r))) {
            //System.out.println("La fila " + r+1 + " está vacia");
            return false;
        } else {
            Row row = sheet.getRow(r);
            Cell cell = row.getCell(3);
            if (cell== null || cell.getCellType() == CellType.BLANK) {
                System.out.println("La fila " + (r) + " no fecha de alta");
                //FECHA DE ALTA VACIA
                return false;
            } else  if (row.getCell(7)==null ||row.getCell(7).getStringCellValue() == ""){
                //No tiene DNI
                System.out.println("La fila " + (r) + " no tiene DNI");
                return false;
            } else {
                Date date = cell.getDateCellValue();
                int month = date.getMonth();
                int year = date.getYear();
                return (year < (y-1900) || (year == (y-1900) && month <= (m-1)));
            }
        }
    }
    
    private void calculaNomina (int i, int m, int y) {
        try {
            Row row = sheet.getRow(i);
            //System.out.println("FECHA NÓMINA: "+m+"/"+y);
            //DECLARACION DE VARIABLES
            trabajador= new Trabajadorbbdd();
            empresa = new Empresas();
            nomina = new Nomina();
            categoria = new Categorias();
            
            trabajador.setEmpresas(empresa);
            trabajador.setCategorias(categoria);
            nomina.setTrabajadorbbdd(trabajador);
            nomina.setMes(m);
            nomina.setAnio(y);
            //Categoria del trabajador
            //Fecha de alta del trabajador
            Date alta= row.getCell(3).getDateCellValue(); 
            double brutoAnual;
            double complementoAnual;
            double salarioAnual; //salario que percibe durante el año especificado
            double salarioMes; //salario mensual
            double baseMes;
            double complementoMes;
            double prorrateoMes=0;
            int trienios=0;
            double antiguedadMes=0;
            double antiguedadAno=0;
            double irpf;
            double ss;
            double formacion;
            double desempleo;
            double liquido; //Salario despues de aplicar los descuentos
            double ssE;
            double formacionE;
            double desempleoE;
            double fogasa;
            double accidentes;
            double costeEmpresario;
            double gastoEmpresa;
            
            boolean cambioT=false;
            double costoMensual;
            //Prorrateo
            boolean prorrateo = false;            
            if (row.getCell(12).getStringCellValue().equalsIgnoreCase("si")) {
                //System.out.println("EL trabajador tiene la nomina prorrateada");
                prorrateo=true;
            } 
            //FIN DECLARACION VARIABLES   
                //Cargamos los objetos 
                cargaObjetos(row);
              
                brutoAnual=categoria.getSalarioBaseCategoria();
                complementoAnual = categoria.getComplementoCategoria();
                //Calcula total a desembolsar por antiguedad en todo el año
                antiguedadAno=calculaAntiguedad(m,y,alta);
                //System.out.println(antiguedadAno);
                if (prorrateo) {
                    //Comprobamos si se produce cambio para añadir la diferencia al salario final
                    int t = calculaTrienio(12,y,alta); //Calculamos en que trienio estamos en diciembre                   
                    int t2 = calculaTrienio(6,y+1,alta); //Calculamos en que trienio estamos en diciembre
                    //System.out.println(t);
                    //System.out.println(t2);
                    //System.out.println("Trienio diciembre " + y + "  = " +t + " trienio Junio de "+(y+1)+" = "+t2);
                    if (t!=t2) {
                        //System.out.println("CAMBIO DE TRIENIO A LA VISTA");
                        cambioT=true;
                        //System.out.println("HOLA, hay un cambio de trienio en JUNIO del año que viene");
                        double a1=calculaAntiguedadMes(m,y,alta);
                        //System.out.println(a1);
                        double a2=calculaAntiguedadMes(6,y+1,alta);
                        //System.out.println(a2);
                        antiguedadAno=antiguedadAno + a2/6 - a1/6; //Añadimos 1/6 del prorrateo de diciembre
                    }
                } 
                
                //Salario bruto anual
                //salarioAnual=calculaSalarioAnual(brutoAnual, complementoAnual, y,m, prorrateo ,alta)+antiguedadAno;
                nomina.setBrutoAnual(calculaSalarioAnual(brutoAnual, complementoAnual, y,m, prorrateo ,alta)+antiguedadAno);              
                // = calculaIRPF(salarioAnual); //IRPF
                nomina.setIrpf(calculaIRPF(nomina.getBrutoAnual()));
                //baseMes=; //Salario base mensual   
                nomina.setImporteSalarioMes(brutoAnual/14); //Salario base bruto mensual
                //complementoMes=complementoAnual/14; //Complemento base mensual
                nomina.setImporteComplementoMes(complementoAnual/14);
                //Calcula la antiguedad para ese mes
               // antiguedadMes=calculaAntiguedadMes(m,y,alta);
                nomina.setNumeroTrienios(calculaTrienio(m,y,alta));
                nomina.setImporteTrienios(calculaAntiguedadMes(m,y,alta));
                
                //SI HAY CAMBIO DE TRIENIO SE AÑADE EN DICIEMBRE 1/6 de la nueva antiguedad
                if (prorrateo) {
                    if (!cambioT) {
                        nomina.setValorProrrateo(nomina.getImporteSalarioMes()/6 + nomina.getImporteComplementoMes()/6 + calculaAntiguedadMes(6,y,alta)/6);
                    } else {
                        if (m!=12) { //NO DICIEMBRE
                            nomina.setValorProrrateo(nomina.getImporteSalarioMes()/6 + nomina.getImporteComplementoMes()/6 + calculaAntiguedadMes(6,y,alta)/6);
                        } else {
                            nomina.setValorProrrateo(nomina.getImporteSalarioMes()/6 + nomina.getImporteComplementoMes()/6 + calculaAntiguedadMes(6,y+1,alta)/6);
                        }
                    }
                } else {
                    nomina.setValorProrrateo(0.0);
                }
                /*
                System.out.println("Salario bruto anual = " + df.format(nomina.getBrutoAnual()) + " ( " + df.format(antiguedadAno) +" de antiguedad)");
                System.out.println("Salario mes= " + df.format(nomina.getImporteSalarioMes()));
                System.out.println("Complemento mes= " + df.format(nomina.getImporteComplementoMes()));
                System.out.println("Prorrateo mes = " + df.format(nomina.getValorProrrateo()));
                System.out.println("Antiguedad mes = " + nomina.getNumeroTrienios() + "Trienios = " + df.format(nomina.getImporteTrienios()));
                */              
                nomina.setBrutoNomina(nomina.getImporteSalarioMes()+nomina.getImporteComplementoMes()
                        +nomina.getImporteTrienios()+ nomina.getValorProrrateo());
                if (prorrateo) {
                    nomina.setBaseEmpresario(nomina.getBrutoNomina());
                } else {
                     nomina.setBaseEmpresario(nomina.getBrutoNomina()*14/12);
                }
                /*
                System.out.println("------------------------");
                System.out.println("Bruto mes= " + df.format(nomina.getBrutoNomina()) );               
                System.out.println("------------------------");
                System.out.println("DESCUENTOS:");
                */
                nomina.setImporteSeguridadSocialTrabajador(calculaSS(nomina.getBaseEmpresario(), 0));
                nomina.setImporteFormacionTrabajador(calculaFormacion(nomina.getBaseEmpresario(), 0));
                nomina.setImporteDesempleoTrabajador(calculaDesempleo(nomina.getBaseEmpresario(), 0));
                nomina.setImporteIrpf(nomina.getBrutoNomina()*(nomina.getIrpf()/100));
                /*
                System.out.println("Seguridad Social(4,70%) de " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteSeguridadSocialTrabajador()));
                System.out.println("Formación (0,10%) de " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteFormacionTrabajador()));
                System.out.println("Desempleo (1,60%) de " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteDesempleoTrabajador()));
                System.out.println("IRPF ("+nomina.getIrpf()+") de " + df.format(nomina.getBrutoNomina()) + " = " + df.format(nomina.getImporteIrpf()) );
                */
                //liquido = salarioMes - ss - formacion - desempleo - (salarioMes*(irpf/100));
                nomina.setLiquidoNomina(nomina.getBrutoNomina()-nomina.getImporteSeguridadSocialTrabajador()
                        -nomina.getImporteDesempleoTrabajador()-nomina.getImporteFormacionTrabajador()-nomina.getImporteIrpf());
                /*
                System.out.println("------------------------");
                System.out.println("Líquido nómina = " + df.format(nomina.getLiquidoNomina()));
                System.out.println("------------------------");
                System.out.println("Gastos empresario:");
                */
                nomina.setImporteSeguridadSocialEmpresario(calculaSS(nomina.getBaseEmpresario(),1));
                nomina.setImporteDesempleoEmpresario(calculaDesempleo(nomina.getBaseEmpresario(),1));
                nomina.setImporteFormacionEmpresario(calculaFormacion(nomina.getBaseEmpresario(),1));
                nomina.setImporteAccidentesTrabajoEmpresario(calculaAccidentes(nomina.getBaseEmpresario()));
                nomina.setImporteFogasaempresario(calculaFOGASA(nomina.getBaseEmpresario()));
                /*
                System.out.println("Seguridad Social Empresario (23,60%) " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteSeguridadSocialEmpresario()));
                System.out.println("Formación Empresario (0,60%) " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteFormacionEmpresario()));
                System.out.println("Desempleo Empresario (6,70%) " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteDesempleoEmpresario()));
                System.out.println("Accidentes (1,00%) " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteAccidentesTrabajoEmpresario()));
                System.out.println("FOGASA (0,20%) " + df.format(nomina.getBaseEmpresario()) + " = " + df.format(nomina.getImporteFogasaempresario()));
                */
                //LO QUE PAGA EL EMPRESARIO POR ESA NOMINA
                costeEmpresario = nomina.getImporteSeguridadSocialEmpresario()+nomina.getImporteFormacionEmpresario()
                +nomina.getImporteDesempleoEmpresario()+nomina.getImporteAccidentesTrabajoEmpresario()+nomina.getImporteFogasaempresario();
                
                /*
                System.out.println("-------------");
                System.out.println("Total aportación del empresario = " + df.format(nomina.getCosteTotalEmpresario()));
                System.out.println("-------------");
                */
                //COSTE TOTAL PARA LA EMPRESA
                nomina.setCosteTotalEmpresario(costeEmpresario + nomina.getBrutoNomina()) ;
                /*
                System.out.println("Coste empresa = " + df.format(gastoEmpresa));
                //GENERAMOS EL PDF CON LOS DATOS QUE HEMOS CALCULADO
                System.out.println("-------------------------------------");
                System.out.println("CREANDO PDF DE LA NOMINA.......");
                */
                try {
                    //TODO
                    //pdf.nuevoPDF( false, nomina, m, y);                   
                }catch (Exception e) {
                    System.out.println("Error al generar el PDF" + e.getMessage());
                }
                try {
                    dbbdd.addCategoria(categoria);
                    dbbdd.addEmpresa(empresa);
                    dbbdd.addNomina(nomina);
                    dbbdd.addTrabajador(trabajador);
                } catch (Exception e) {
                    System.out.println("Error al cargar la clase de inserccion en base de datos  "+ e.getMessage());
                }
                //PAGA EXTRA SI NO ESTA PRORRATEADA (SI PROCEDE)
                if (!prorrateo) {   
                    if (m==6 || m==12) { //Calculamos extra de Junio o Diciembre
                        //System.out.println("Calculamos extra");
                        getPagaExtra(alta,y,m,nomina.getBrutoNomina(),nomina.getIrpf());
                    } else {
                        //NO TOCA EXTRA 
                        //System.out.println("ESTE MES NO HAY EXTRA");
                    }
                }              
            //System.out.println("------------------------");
            //AÑADIMOS LA INFORMACIÓN AL CONTROLADOR DE BBD
            
        } catch (Exception e) {
            System.out.println("Error al generar la nomina para la columna " + (i+1) + " "+ e.getMessage());
        }
       
    }
    
    //METODO PAGA EXTRA
    private void getPagaExtra (Date alta, int y, int m,  double salarioMes, double irpf) {
        /*
        System.out.println("------------------------");
        System.out.println("Calculo de paga extra");
        System.out.println("------------------------");
        */        
        Nomina n= new Nomina();
        
        n.setMes(m);
        n.setAnio(y);
        n.setBrutoAnual(nomina.getBrutoAnual());
        n.setImporteSalarioMes(nomina.getImporteSalarioMes());
        n.setImporteComplementoMes(nomina.getImporteComplementoMes());
        n.setImporteTrienios(nomina.getImporteTrienios());
        n.setDesempleoEmpresario(nomina.getDesempleoEmpresario());
        n.setDesempleoTrabajador(nomina.getDesempleoTrabajador());
        n.setSeguridadSocialEmpresario(nomina.getSeguridadSocialEmpresario());
        n.setSeguridadSocialTrabajador(nomina.getSeguridadSocialTrabajador());
        n.setFormacionEmpresario(nomina.getFormacionEmpresario());
        n.setFormacionTrabajador(nomina.getFormacionTrabajador());
        n.setFogasaempresario(nomina.getFogasaempresario());
        n.setAccidentesTrabajoEmpresario(nomina.getAccidentesTrabajoEmpresario());
        n.setImporteSeguridadSocialTrabajador(0.0);
        n.setImporteDesempleoTrabajador(0.0);
        n.setImporteFormacionTrabajador(0.0);
        n.setImporteAccidentesTrabajoEmpresario(0.0);        
        n.setImporteDesempleoEmpresario(0.0);
        n.setImporteSeguridadSocialEmpresario(0.0);
        n.setImporteFormacionEmpresario(0.0);
        n.setImporteFogasaempresario(0.0);
        n.setImporteIrpf(nomina.getImporteIrpf());
        n.setIrpf(nomina.getIrpf());
        n.setTrabajadorbbdd(trabajador);
        
        n.setNumeroTrienios(nomina.getNumeroTrienios());
        n.setValorProrrateo(nomina.getValorProrrateo());
        
        double pagaExtra=0;
        if(alta.getYear()+1900 == y) { //Entro este mismo año => Puede que no cobre toda la extra
            if (m==6) { //
                pagaExtra= salarioMes * ((6 -(alta.getMonth()+1)) /6.0) - (salarioMes*(irpf/100));

                //System.out.println("La extra de JUNIO es de " + df.format(pagaExtra));

            } else if (m==12) {
                if (alta.getMonth()+1 < 6) {
                    pagaExtra=salarioMes - (salarioMes*(irpf/100));
                    //System.out.println("La extra de DICIEMBRE es de " + df.format(pagaExtra));
                } else {
                    pagaExtra=salarioMes * ((11 -(alta.getMonth()+1)) /6.0) - (salarioMes*(irpf/100));
                    //System.out.println("La extra de DICIEMBRE es de: " + df.format(pagaExtra));
                }
            }                         
        } else { //Ha trabajado todos los meses de la extra            
            pagaExtra= salarioMes - (salarioMes*irpf/100);


            //Gasto empresario = salarioMes;
        }  
        n.setLiquidoNomina(pagaExtra);
        n.setBrutoNomina(salarioMes);
        n.setBaseEmpresario(0.0);
        n.setCosteTotalEmpresario(salarioMes);
        /*
        System.out.println("------------------------");
        System.out.println("Gastos empresario EXTRA");
        System.out.println("------------------------");
        System.out.println("Aportación empresario = 0");
        System.out.println("------------------------");
        System.out.println("Coste total paga EXTRA = "+df.format(salarioMes));
        */
        try {
            //TODO
            //pdf.nuevoPDF( true, n, m, y);
            dbbdd.addNomina(n);

        } catch (Exception e) {
            System.out.println("Error al crear el PDF " + e.getMessage());
        }
    }

    // Calcula el salario bruto anual a percibir
    // en funcion de los meses que se trabajaría el año de la nomina
    private double calculaSalarioAnual (double base, double complemento, int y, int m, boolean p, Date alta) {
        double out=0; 
        boolean mismoAño = alta.getYear()+1900 == y;        
        if (!mismoAño) {
             //Trabaja todo el año
            out = base + complemento;
        } else {
            double n=12-(alta.getMonth()); //nº de meses trabajados   
            //System.out.println("El trabajador trabaja " + n + " meses en el año " + y);
            if (p) {
                //prorrateo si
                //out = (base + complemento) / 12 * meses trabajados
                out= ((base + complemento) / 12 ) * n;
            } else {
                //prorrateo no
                //out = (base + complemento)/ 14 * meses trabajados + (base + complemento)/ 14 * pagas extra
                //Calculo extras
                double e; //nº de pagas extra
                if ((alta.getMonth()+1)<6) {
                    //Si ha entrado antes de junio, tendrá derecho a la extra de diciembre entera
                    //sumamos la extra de diciembre
                    n=n+1;
                    // % de la extra de junio
                    e = (double) (6-(alta.getMonth()+1)) / 6;

                    out= ((base + complemento) / 14)* n + ((base + complemento)/14)* e;
                    //System.out.println("Salario = (("+base+" + "+complemento+")/14) *"+n+" + (("
                    //+ base +" + "+complemento+")/14) *" +e);
                } else {
                    //No tiene derecho a la extra de diciembre entera
                    //No tiene extra de junio
                    // % Extra de diciembre
                    e=(11-(alta.getMonth()+1))/6;

                    out= ((base + complemento) / 14)* n + ((base + complemento)/14)* e;
                    
                }                
            }
        } 
       
        //System.out.println("Salario bruto anual = " + out );
        //Calculamos ahora cuantas extras corresponden
        return out;
    }
    
    //Calcula el desembolso por antiguedad a lo largo de todo año laboral
    private double calculaAntiguedad (int  m, int y, Date alta) {
        double out=0;
        for (int i =1;i<=12;i++) { //De enero a diciembre + las extras
            out=out+calculaAntiguedadMes(i,y,alta);
            if(i==6 || i == 12) { //EXTRA
                out+=calculaAntiguedadMes(i,y,alta);
            }
        }
        return out;
    }
    
    //Calcula antiguedad a pagar en el mes especificado
    private double calculaAntiguedadMes ( int  m, int y, Date alta) {
        double out=0;
        int t=calculaTrienio(m,y,alta);
        //System.out.println("Trienio = " + t);
        for (Integer i:d.getTrienios().keySet()) {
            if (t >= i) {
                //System.out.println(i+" trienios = " + d.getTrienios().get(i));
                out = d.getTrienios().get(i);                
            } else {
                break;
            }
            
        }
        //System.out.println("Mes " + m +" = " + out);
        return out;
    }
    
    //Calcula que en que trienio esta en el mes especificado
    private int calculaTrienio (int m, int y, Date alta) {

        int yAlta = alta.getYear()+1900;
        int mAlta = alta.getMonth()+1;
        int difM =m-mAlta;
        int trienios = Math.floorDiv(y-yAlta, 3);

        if ((y-yAlta)%3 == 0) {
            //ES TRIENIO JUSTO           
            trienios = Math.floorDiv(y-yAlta, 3);
            if (difM <= 0) {
                    trienios-=1; //Aun no se ha actualizado el trienio
                }
        } else {
            trienios = Math.floorDiv(y-yAlta, 3);
        }
        
        //System.out.println("En el " + m + "/" + y +" han pasado " + trienios + " trienios");
        return trienios;
    }
    
    //Salario bruto mensual
    private double calculaSalarioMes (double base, double complemento, boolean prorrateo, double IRPF) {
        if (prorrateo) {
            return (base + complemento)/12;
        } else {
            return (base + complemento)/14;
        }
    }
    
    //IRPF a aplicar sobre el salario bruto anual
    private double calculaIRPF (double salario) {        
        double out=26.22; //IRPF maximo
        int n=0;    
        try {
            List <Integer> tramos = new ArrayList <> (d.getRetencion().keySet());
            Collections.sort(tramos);
            for (int i=0;i<tramos.size();i++) {
                //System.out.println(tramos.get(i)+ " => " +d.getRetencion().get(tramos.get(i)));
                if (salario>tramos.get(i)){
                    out=0;
                } else {
                    out=d.getRetencion().get(tramos.get(i));
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar el IRPF en la estructura de datos " + e.getMessage());
            return 0;
        }
        //System.out.println("El IRPF para " + salario + " será de " + out);
        return out;
    }
    //t=0 => trabajador t=1 => empresario
    private double calculaSS(double salario, int t) {
        
        try {
        switch (t) {
            case 0:
                return salario * (d.getCuotas().get("Cuota obrera general TRABAJADOR")/100); //4,70% del salario
            case 1:
                return salario * (d.getCuotas().get("Contingencias comunes EMPRESARIO")/100); // 23,69%
            default:
                System.out.println("Error, parametro t debe ser 0 (trabajador) o 1 (empresario)");
                return 0;
        }
        } catch (Exception e) {
            System.out.println("Error al buscar la cuota de Seguridad social en la estructura de datos " + e.getMessage());
            return 0;
        }
    }
    
    private double calculaFormacion (double salario, int t) {
        try {
        switch (t) {
            case 0:
                return salario* (d.getCuotas().get("Cuota formación TRABAJADOR")/100); // 0.10% salario
            case 1:
                return salario *(d.getCuotas().get("Formacion EMPRESARIO")/100); // 0,60%
            default:
                System.out.println("Error, parametro t debe ser 0 (trabajador) o 1 (empresario)");
                return 0;
        }
        } catch (Exception e) {
            System.out.println("Error al buscar la cuota de Formacion en la estructura de datos " + e.getMessage());
            return 0;
        }
    }
    
    private double calculaDesempleo (double salario, int t) {
        try {
            switch (t) {
                case 0:
                    return salario* (d.getCuotas().get("Cuota desempleo TRABAJADOR")/100); //1.60% del salario
                case 1:
                    return salario *(d.getCuotas().get("Desempleo EMPRESARIO")/100); // 6.70%
                default:
                    System.out.println("Error, parametro t debe ser 0 (trabajador) o 1 (empresario)");
                    return 0;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar la cuota de Desempleo en la estructura de datos " + e.getMessage());
            return 0;
        }
        
    }
    
    private double calculaAccidentes (double salario) {
        try {
            return salario * (d.getCuotas().get("Accidentes trabajo EMPRESARIO")/100); // 1% del salario
        } catch (Exception e) {
            System.out.println("Error al buscar la cuota de Accidentes en la estructura de datos " + e.getMessage());
            return 0;
        }
    }
    
    private double calculaFOGASA (double salario) {
        try {
            return salario * (d.getCuotas().get("Fogasa EMPRESARIO")/100); //0.20% del salario
        } catch (Exception e) {
            System.out.println("Error al buscar la cuota de FOGASA en la estructura de datos " + e.getMessage());
            return 0;
        }
    }
    

    
    //Cargamos los objetos trabajador, empresa y nomina e imprimimos la información
    private void cargaObjetos (Row row) {
        
        
        empresa.setCif(row.getCell(0).getStringCellValue());
        empresa.setNombre(row.getCell(1).getStringCellValue());
        
        trabajador.setFechaAlta(row.getCell(3).getDateCellValue());
        trabajador.setNombre(row.getCell(4).getStringCellValue());
        trabajador.setApellido1(row.getCell(5).getStringCellValue());
        if (row.getCell(6)!= null) {
            trabajador.setApellido2(row.getCell(6).getStringCellValue());
        } else {
            trabajador.setApellido2("");
        }
        trabajador.setNifnie(row.getCell(7).getStringCellValue());   
        trabajador.setEmail(row.getCell(8).getStringCellValue());
        trabajador.setCodigoCuenta(row.getCell(9).getStringCellValue());     
        trabajador.setIban(row.getCell(11).getStringCellValue());
                       
        categoria.setNombreCategoria( row.getCell(2).getStringCellValue());
        categoria.setSalarioBaseCategoria(d.getBaseCategoria().get(categoria.getNombreCategoria()));
        categoria.setComplementoCategoria(d.getComplementoCategoria().get(categoria.getNombreCategoria()));
        
        nomina.setAccidentesTrabajoEmpresario(d.getCuotas().get("Accidentes trabajo EMPRESARIO"));
        nomina.setDesempleoEmpresario(d.getCuotas().get("Desempleo EMPRESARIO"));
        nomina.setSeguridadSocialEmpresario(d.getCuotas().get("Contingencias comunes EMPRESARIO"));
        nomina.setFogasaempresario(d.getCuotas().get("Fogasa EMPRESARIO"));
        nomina.setFormacionEmpresario(d.getCuotas().get("Formacion EMPRESARIO"));
        
        nomina.setFormacionTrabajador(d.getCuotas().get("Cuota formación TRABAJADOR"));
        nomina.setSeguridadSocialTrabajador(d.getCuotas().get("Cuota obrera general TRABAJADOR"));
        nomina.setDesempleoTrabajador(d.getCuotas().get("Cuota desempleo TRABAJADOR"));
               
        /*
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Empresa: "+ empresa.getNombre() + " (CIF = " + empresa.getCif() + ")");
        System.out.println("Trabajador: " + trabajador.getNombre() + " " + trabajador.getApellido1() + " " + trabajador.getApellido2());
        System.out.println("NIF/NIE: " + trabajador.getNifnie());
        System.out.println("Email: " + trabajador.getEmail());
        System.out.println("IBAN: " + trabajador.getIban());
        System.out.println("Fecha de alta: " + trabajador.getFechaAlta().toString());
        System.out.println("Categoria: " + categoria.getNombreCategoria());
        System.out.println("--------------------------------------------------------------------");
        */
    }
    
    
}
   

