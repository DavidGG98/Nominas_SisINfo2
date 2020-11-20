/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import DAO.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author david
 */
public class SessionManager {
    
    private final SessionFactory sf = HibernateUtil.getSessionFactory();
    private static Session session;
    
    public void sessionManager () {
      
    }
    
    public void openSession() {
        session=sf.openSession();
    }
    public void closeSession() {
        session.close();
    }
    public Session getSession() {
        return this.session;
    }
}
