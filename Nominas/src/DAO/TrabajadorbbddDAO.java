/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.List;
import modelo.Empresas;
import modelo.Trabajadorbbdd;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author david
 */
public class TrabajadorbbddDAO implements TrabajadorbbddDAOInterface {

    private final SessionFactory sf = HibernateUtil.getSessionFactory();
    private static Session session;
    private static String consulta;
    private static Query query;
    private static Transaction trans;
    

    private void openSession () {
         session=sf.openSession();
    }
    
    private void closeSession() {
        session.close();
    }
    
    @Override
    public void insert(Trabajadorbbdd t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Trabajadorbbdd t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete (int id) {
        openSession();
        //Para borrar a un trabajador, debemos primero eliminar sus nominas
        Trabajadorbbdd t= read (id); //Recuperamos el trabajador que vamos a borrar
        try {
            openSession();
            trans=session.beginTransaction();
            NominaDAOInterface nDAO = new NominaDAO();
            //Eliminamos todaslas nominas del trabajador
            nDAO.deleteAllFromWorker(t); 
            //Eliminamos el propio trabajador
            consulta="DELETE Trabajadorbbdd t WHERE t = :trabajador";
            query = session.createQuery(consulta);
            query.setParameter("trabajador",t);
            query.executeUpdate();
            trans.commit();       
            closeSession();
        } catch (Exception e) {
            System.out.println("Error en la transacción "+ e.getMessage());
        }
        
    }
    
    @Override
    public void deleteAllFromCompany (Empresas e) {
        
        try {
            openSession();
            //Obtenemos los trabajadores de la empresa
            consulta = "SELECT t FROM Trabajadorbbdd t WHERE t.empresas = :empresa";
            query = session.createQuery(consulta);
            query.setParameter("empresa",e);
            List <Trabajadorbbdd> listaTrabajadores = query.list(); //Recibimos todos los trabajadores           
            try {
                trans=session.beginTransaction();
               
                //Borramos todas las nominas de todos los trabajadores
                for(Trabajadorbbdd t:listaTrabajadores) {  
                    
                     NominaDAOInterface nDAO = new NominaDAO();
                     nDAO.deleteAllFromWorker(t);                      
                }
                trans.commit();
            } catch (Exception ex) {
              System.out.println("Error durante el borrado de nóminas "+ex.getMessage());
            } 
            try {
                //Borramos todos los trabajadores de la empres
                trans=session.beginTransaction();
                System.out.println("Borrando los trabajadores");
                consulta= "DELETE Trabajadorbbdd t WHERE t.empresas = :empresa";
                query = session.createQuery(consulta);
                query.setParameter("empresa",e);
                query.executeUpdate();
                
                trans.commit();
               
            } catch (Exception ex) {
                System.out.println("Error al realizar el borrado " + ex.getMessage());
            }
            
            closeSession();
        
    } catch (Exception ex) {
       System.out.println("Error al recoger los trabajadores de la emrpesa "+ ex.getMessage());
    }
        
}
    
    
    @Override
    public Trabajadorbbdd read (int id) {
        openSession();
        
        consulta="SELECT n FROM Trabajadorbbdd n WHERE n.idTrabajador=:param1";
        try {
            query = session.createQuery(consulta);
            query.setParameter("param1",id); // Añadimos los parametros
            try {
                Trabajadorbbdd t=(Trabajadorbbdd) query.uniqueResult();
                return t;
            } catch (Exception e) {
                System.out.println("Se ha producido una excepción al retirar al trabajador"
                        + " de la base de datos" + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error al crear la query " + e.getMessage());
        }
        
        closeSession();
        return null;
    }

    @Override
    public Trabajadorbbdd readNIF (String id) {
        openSession();
        
        consulta="SELECT n FROM Trabajadorbbdd n WHERE n.nifnie=:param1";
        try {
            query = session.createQuery(consulta);
            query.setParameter("param1",id); // Añadimos los parametros
            try {
                Trabajadorbbdd t=(Trabajadorbbdd) query.uniqueResult();
                return t;
            } catch (Exception e) {
                System.out.println("Se ha producido una excepción al retirar al trabajador"
                        + " de la base de datos" + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error al crear la query " + e.getMessage());
        }
        
        closeSession();
        return null;
    }
    
    
    @Override
    public List<Trabajadorbbdd> readAll() {
          List <Trabajadorbbdd> lista;
        
        try {
            openSession();
            consulta="FROM Trabajadorbbdd";
            
            query= session.createQuery(consulta);
            
            try {
                List listaResultado= query.list();
                
               return listaResultado;
            } catch (Exception e) {
                System.out.println("Error al leer los trabajadores " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Error al conectarse con la base de datos: " + e.getMessage());
        }
        
        
        return null;
    }
    
    
}
