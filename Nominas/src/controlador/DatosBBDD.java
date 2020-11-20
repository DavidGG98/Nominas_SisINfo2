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
import DAO.NominaDAO;
import DAO.NominaDAOInterface;
import DAO.TrabajadorbbddDAO;
import DAO.TrabajadorbbddDAOInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import modelo.Categorias;
import modelo.Empresas;
import modelo.Nomina;
import modelo.Trabajadorbbdd;

/**
 *
 * @author david
 */
public class DatosBBDD {
    //Listas que contienen todos los elementos de la base de datos
    
    private List <Empresas> listaEmpresas;
    private List <Trabajadorbbdd> listaTrabajadores;
    private List <Categorias> listaCategorias;
    private List <Nomina> listaNominas;
    
    //listas de candidatos a entrar
    private List <Empresas> empresas ;
    private List <Trabajadorbbdd> trabajadores;
    private List <Categorias> categorias;
    private List <Nomina> nominas;
    
    TrabajadorbbddDAOInterface tDAO;
    CategoriasDAOInterface cDAO;
    NominaDAOInterface nDAO;
    EmpresasDAOInterface eDAO;
    
    SessionManager sm;
    
    public DatosBBDD () {
        try {
            sm=new SessionManager();
            sm.openSession(); //Abrimos la sesion
        } catch (Exception e) {
            System.out.println ("Error al iniciar el SessionManager " + e.getMessage());
        }
        eDAO = new EmpresasDAO();
        cDAO = new CategoriasDAO();
        tDAO = new TrabajadorbbddDAO ();
        nDAO = new NominaDAO();
        empresas = new ArrayList ();
        nominas = new ArrayList();
        trabajadores = new ArrayList();
        categorias = new ArrayList();
        //Asignamos la sesion a los DAO
        try {          
            eDAO.setSession(sm.getSession()); 
            cDAO.setSession(sm.getSession());
            nDAO.setSession(sm.getSession());
            tDAO.setSession(sm.getSession());
        } catch (Exception e) {
            System.out.println("Error al asignar la sesión a los DAO " + e.getMessage());
        }


    }
    
    public void openSession() {
        try {
            sm.openSession();
        } catch (Exception e) {
            System.out.println("Session manager exception: Error al abrir la sesion " + e.getMessage());
        }
    }
    
    public void closeSession() {
        try {
            sm.closeSession();
        } catch (Exception e) {
            System.out.println("Session manager exception: Error al cerrar la sesion " + e.getMessage());
        }
    }
    
    public void insertaDatos () {
        openSession();
        try {
            eDAO.insert(empresas);
        } catch (Exception e) {
            System.out.println("Error al introducir las empresas en la base de datos " + e.getMessage());
        }
        try {
            cDAO.insert(categorias);
        } catch (Exception e) {
            System.out.println("Error al introducir las categorias en la base de datos " + e.getMessage());
        }
        try {
            cargaTrabajadores();
            tDAO.insert(trabajadores);
        } catch (Exception e) {
            System.out.println("Error al introducir los empleados en la base de datos "+ e.getMessage());
        }
        try {
            cargaNominas();
            nDAO.insert(nominas);
        } catch (Exception e) {
            System.out.println("Error al introducir las nominas en la base de datos " + e.getMessage());
        }
        closeSession();
    }
    
    private void cargaNominas() {
        listaTrabajadores = tDAO.readAll();
        for (Nomina n: nominas) {
            Trabajadorbbdd t= n.getTrabajadorbbdd();
            for (Trabajadorbbdd aux:listaTrabajadores) {
                if (t.getNombre().equalsIgnoreCase(aux.getNombre()) && t.getNifnie().equalsIgnoreCase(aux.getNifnie()) && t.getFechaAlta().equals(aux.getFechaAlta())) {
                    //Es el mismo trabajador
                    n.setTrabajadorbbdd(aux);
                    break;
                }
            }
        }
    }
    
    private void cargaTrabajadores () {
        listaEmpresas = eDAO.readAll(); //Recuperamos todas las empresas
        listaCategorias = cDAO.readAll(); //Recuperamos todas las categorias
        for (Trabajadorbbdd t:trabajadores) {
            Empresas e = t.getEmpresas();
            for (Empresas aux:listaEmpresas) {
                if (aux.getCif().equalsIgnoreCase(e.getCif())) {
                    //Es la misma empresa
                    t.setEmpresas(aux);
                    break;
                }
            }
            
            Categorias c=t.getCategorias();
            for (Categorias aux:listaCategorias) {
                if (c.getNombreCategoria().equalsIgnoreCase(aux.getNombreCategoria())) {
                    t.setCategorias(aux);
                    break;
                }
            }
            //System.out.println("Cargado empresas y categorias para el trabajador " + t.getNifnie());
        }
    }

    /*
    *Estos metodos añaden el objeto a la lista si no está duplicado
    */
    public void addNomina (Nomina n) {
        //Comprobamos que no existe un objeto igual en la lista 
        boolean existe = false;
        //System.out.println("Añadiendo nomina para " + n.getTrabajadorbbdd().getNifnie());
        for (Nomina aux: nominas) {
            if(n.getMes()==aux.getMes() && n.getAnio() == aux.getAnio() 
                    && Double.compare(n.getBrutoNomina(), aux.getBrutoNomina()) ==0
                    && Double.compare(n.getLiquidoNomina(),aux.getLiquidoNomina()) == 0
                    && mismoTrabajador (n,aux)) {
                existe = true;
                // System.out.println("Liquido n1 = " + n.getLiquidoNomina() + " Bruto = " + n.getBrutoNomina());
                //System.out.println("Liquido n2 = " + aux.getLiquidoNomina() + " Bruto = " + aux.getBrutoNomina());
                // System.out.println("La nomina ya existe");
                nominas.remove(aux);
                nominas.add(n);
                break;
            }
        }
        
        if (!existe) {
            //System.out.println("La nomina se ha añadido");
            nominas.add(n);
        }
        
    }
    
    private boolean mismoTrabajador (Nomina n,Nomina aux) {
        Trabajadorbbdd t1 = n.getTrabajadorbbdd();
        Trabajadorbbdd t2= aux.getTrabajadorbbdd();
        return t1.getNifnie().equalsIgnoreCase(t2.getNifnie()) && t1.getNombre().equalsIgnoreCase(t2.getNombre()) && t1.getFechaAlta().equals(t2.getFechaAlta());
    }
    
    public void addTrabajador (Trabajadorbbdd t) {
        boolean existe=false;
        for (Trabajadorbbdd aux: trabajadores) {
            if (t.getNombre().equalsIgnoreCase(aux.getNombre()) && t.getNifnie().equalsIgnoreCase(aux.getNifnie()) && t.getFechaAlta().equals(aux.getFechaAlta())) {
                trabajadores.remove(aux);
                trabajadores.add(t);
                existe=true;
                break;
            }
        }
        if (!existe) {
            trabajadores.add(t);
        }
    }
    
    public void addEmpresa (Empresas e) {      
        try {
            boolean existe =false;
            for (Empresas aux: empresas) {
                if (e.getCif().equalsIgnoreCase(aux.getCif())) {
                    //System.out.println("La empresa ya existe");
                    existe = true;
                    aux=e; //Actualizamos los datos de la empresa por la más reciente
                    break;
                }
            }
            if (!existe) {
                empresas.add(e);
            }
        } catch (Exception ex) {
            System.out.println("Error al añadir una empresa a la lista " + ex.getMessage());
        }
    }
    
    public void addCategoria (Categorias c) {
        boolean existe = false;
        for (Categorias aux: categorias) {
            if (aux.getNombreCategoria().equalsIgnoreCase(c.getNombreCategoria())) {
                existe=true;
                break;
            }
        }
        if (!existe) {
            categorias.add(c);
        }
    }

    public List<Empresas> getEmpresas() {
        return empresas;
    }

    public List<Trabajadorbbdd> getTrabajadores() {
        return trabajadores;
    }

    public List<Categorias> getCategorias() {
        return categorias;
    }

    public List<Nomina> getNominas() {
        return nominas;
    }

    public TrabajadorbbddDAOInterface gettDAO() {
        return tDAO;
    }

    public CategoriasDAOInterface getcDAO() {
        return cDAO;
    }

    public NominaDAOInterface getnDAO() {
        return nDAO;
    }

    public EmpresasDAOInterface geteDAO() {
        return eDAO;
    }

    public List <Categorias> getAllCategorias () {
        return cDAO.readAll();
    }
    
    public List <Trabajadorbbdd> getAllTrabajadores () {
        return tDAO.readAll();
    }
    
    public Nomina getNominaMenorLiquido (int m, int y) {
        return nDAO.getNominaMenorLiquido(m,y);
    }
    
}
