/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.ArrayList;
import java.util.List;
import modelo.Categorias;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author david
 */
public class CategoriasDAO implements CategoriasDAOInterface {

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
    public void insert(Categorias c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Categorias c) {
        consulta = (" UPDATE Categorias c set c.salarioBaseCategoria = c.salarioBaseCategoria + 200 where c.idCategoria = :id");
        
        int id= c.getIdCategoria();
        try {
        query= session.createQuery (consulta);                        
        query.setParameter("id", id);                        
        query.executeUpdate();
        } catch (Exception e) {
            System.out.println("Se ha producido un error con la Query " + e.getMessage());
        }
    }
    
    @Override
    public void updateAllBut (Categorias c) {
        List<Categorias> allCat = readAll();
        try {
        trans = session.beginTransaction(); //Comenzamos la transacción
        
        for (Categorias cat:allCat) {
            if (cat.getIdCategoria()!=c.getIdCategoria()) {
                
                update(cat);
            }
        }
        try {
            trans.commit();
            System.out.println("Actualización exitosa!!");
        } catch (Exception e) {
            System.out.println("Se ha producido un error en el commit de la transacción "+ e.getMessage());
        }
        } catch (Exception e) {
            System.out.println("Error al iniciar la transacción" + e.getMessage());
        }
    } 

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Categorias read(int id) {
        openSession();
        
        consulta="Select n FROM Categorias n WHERE n.idCategoria=:param1";
        try {
            query = session.createQuery(consulta);
            query.setParameter("param1",id); // Añadimos los parametros
            try {
                Categorias c=(Categorias) query.uniqueResult();
                return c;
            } catch (Exception e) {
                System.out.println("Se ha producido una excepción al leer el id"
                        + " de la categoria" + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error al crear la query " + e.getMessage());
        }
        
        closeSession();
        return null;
    }

    @Override
    public List<Categorias> readAll() {
        
       List <Categorias> lista;
        
        try {
            openSession();
            consulta="FROM Categorias";
            
            query= session.createQuery(consulta);
            
            try {
                List listaResultado= query.list();
                
               return listaResultado;
            } catch (Exception e) {
                System.out.println("Error al leer las categorias " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Error al conectarse con la base de datos: " + e.getMessage());
        }
        
        
        return null;
    }
    
    
}
