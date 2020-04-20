/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;


import DAO.CategoriasDAO;
import DAO.CategoriasDAOInterface;
import DAO.EmpresasDAO;
import DAO.EmpresasDAOInterface;
import java.util.Scanner;
import DAO.HibernateUtil;
import DAO.NominaDAO;
import DAO.NominaDAOInterface;
import DAO.TrabajadorbbddDAO;
import DAO.TrabajadorbbddDAOInterface;
import modelo.Categorias;
import modelo.Empresas;
import modelo.Trabajadorbbdd;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author david
 */
public class Nominas {

    //private static Session sesion;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
       
        //sesion = HibernateUtil.getSessionFactory().openSession(); //Abrimos la sesion
        String nif="";
        System.out.println("Para salir, escribe Exit");
        TrabajadorbbddDAOInterface tDAO;
        CategoriasDAOInterface cDAO;
        NominaDAOInterface nDAO;
        EmpresasDAOInterface eDAO ;
                         
        do {             
        System.out.print("Introduce el NIF/NIE de un trabajador:");
        Scanner s= new Scanner(System.in); //Create Scanner
        nif=s.nextLine(); //Read User Input

        if (!nif.equalsIgnoreCase("EXIT")) {
            
        tDAO = new TrabajadorbbddDAO (); //Creamos el DAO con el que accederemos a la base de datos
        cDAO = new CategoriasDAO();
        nDAO = new NominaDAO();
        eDAO = new EmpresasDAO();
        
        //Recuperamos al trabajador indicado
        Trabajadorbbdd t;
        t = tDAO.readNIF(nif);
        //List listaResultado=query.list(); //lanzamos la query y guardamos el resultado
        if(t!=null) {
        System.out.println("Obteniendo información del trabajador con nif:"+ t.getNifnie());
        System.out.println("Nombre: " + t.getNombre() + " " +t.getApellido1()+ " "+ t.getApellido2());
        System.out.println("NIF: 2" + t.getNifnie());
        System.out.println("Categoria: "+ t.getCategorias().getNombreCategoria());
        System.out.println("Empresa: "+ t.getEmpresas().getNombre());

         //Incrementamos salarios
        String in;
        do {
            System.out.print("¿Quieres incrementar los salarios? (Y/N): ");
            in = s.nextLine();
            if (!"Y".equalsIgnoreCase(in) && !"N".equalsIgnoreCase(in)) {
                System.out.println("Carácter no válido");
            }
        } while (!"Y".equalsIgnoreCase(in) && !"N".equalsIgnoreCase(in));
        
        if ("Y".equalsIgnoreCase(in)) {
            System.out.println("Estamos aumentando los salarios en 200€...");
            Categorias c= t.getCategorias();
            cDAO.updateAllBut(c);
        }
        
        do {
            System.out.print("¿Quieres eliminar todos los trabajadores de la empresa? (Y/N): ");
            in = s.nextLine();
            if (!"Y".equalsIgnoreCase(in) && !"N".equalsIgnoreCase(in)) {
                System.out.println("Carácter no válido");
            }
            } while (!"Y".equalsIgnoreCase(in) && !"N".equalsIgnoreCase(in));
            
             if ("Y".equalsIgnoreCase(in)) {
                 System.out.println("Estamos borrando todos los trabajadores pertenecientes a " + t.getEmpresas().getNombre());
                 Empresas company= t.getEmpresas();
                 tDAO.deleteAllFromCompany(company);
             }
      
            
       }
        } else {
            System.out.println("No existe el trabajador");
        }
        } while (!nif.equalsIgnoreCase("EXIT"));
        
        System.out.println("Gracias por utilizarme!!!!");
        System.exit(0);
    }    
}
