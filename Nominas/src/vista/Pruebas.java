/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import DAO.HibernateUtil;
import controlador.DatosBBDD;
import modelo.Empresas;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author david
 */
public class Pruebas {
     public static void main(String[] args) {
          
         try{
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session;
            session=sf.openSession();
            Empresas e= new Empresas();
            e.setCif("P2418823C");
            e.setNombre("PRUEBA");
            DatosBBDD d = new DatosBBDD();
            d.addEmpresa(e);
            d.insertaDatos();
            
            session.close();
         }catch (Exception e) {
             System.out.println("Error fatal " + e.getMessage());
         }
         System.exit(0);
     }
}
