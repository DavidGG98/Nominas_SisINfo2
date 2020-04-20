/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.ArrayList;
import java.util.List;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author david
 */
public class NominaDAO implements NominaDAOInterface {

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
    public void insert(Nomina n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Nomina n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void deleteAllFromWorker (Trabajadorbbdd t) {    
        openSession();

        System.out.println("Borrando las nómina de " + t.getNombre() + " " + t.getApellido1() 
        + "(NIF/NIE)=" + t.getNifnie());
        /*
        try {
            //Recogemos todas las nóminas del trabajador
           consulta="FROM Nomina t WHERE t.trabajadorbbdd = :trabajador";
           query=session.createQuery(consulta);
           query.setParameter("trabajador", t);        
           //System.out.println("Hemos recogido todas las nominas del trabajador");
           
           List <Nomina> lista = query.list();
           System.out.println("Las nominas de este trabajador son: ");
           for (Nomina n:lista) {
               System.out.println(n.getIdNomina());
           }
        
         } catch (Exception e) {
            System.out.println("Error al acceder a las normias del trabajador "+ e.getMessage());
        }
*/
        //BORRAMOS TODAS LAS NOMINAS PARA EL TRABAJADOR
        try {
        trans=session.beginTransaction();            
        consulta = "DELETE Nomina n WHERE n.trabajadorbbdd = :trabajador";
        query=session.createQuery(consulta);
        query.setParameter("trabajador",t);
        try {
        query.executeUpdate();           
        } catch (Exception e) {
            System.out.println("Error en el borrado " + e.getMessage());
        }
        } catch (Exception e) {
            System.out.println("Error al construir la query " + e.getMessage());
        }
        try {
        trans.commit();
        } catch (Exception e) {
            System.out.println("Error en la transacción " + e.getMessage());
        }
        closeSession();
    }

    @Override
    public Nomina read(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
