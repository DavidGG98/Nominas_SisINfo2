/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.List;
import modelo.Empresas;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author david
 */
public class EmpresasDAO implements EmpresasDAOInterface{

    private final SessionFactory sf = HibernateUtil.getSessionFactory();
    private Session session;
    private static String consulta;
    private static Query query;
    
    /*
    private void openSession () {
         session=sf.openSession();
    }
    
    private void closeSession() {
        session.close();
    }
    */
    @Override
    public void setSession (Session s) {
        this.session=s;
    }
    @Override
    public void insert(Empresas e) {
    
    }

    @Override
    public void update(Empresas e) {
        consulta = (" UPDATE Empresas em set em = e where em.idEmpresa = :id");
        
        int id= e.getIdEmpresa();
        try {
            query= session.createQuery (consulta);                        
            query.setParameter("id", id);                        
            //query.executeUpdate();
            System.out.println("Empresa actualizada con exito");
        } catch (Exception ex) {
            System.out.println("Se ha producido un error con la Query " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Empresas read(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Empresas> readAll() {
        consulta="FROM Empresas";
        //System.out.println(consulta);
        try {
            query=session.createQuery(consulta);
            try {
                List <Empresas> listaResultado = query.list();
                return listaResultado;
            } catch (Exception e) {
                System.out.println("Error al ejecutar la query " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error al crear la query " + e.getMessage());
        }
        System.out.println("No hay nada");
        return null;
    }

    @Override
    public void insert(List<Empresas> e) {
        //session=sf.openSession();
        List<Empresas> all = readAll();

        Transaction trans;
        //System.out.println("LISTA :" +all.toString());
        try {
            int id=0;
            trans = session.beginTransaction(); //Comenzamos la transacción
            for (Empresas e1: e) {
                //System.out.println("Introduciendo empresa " + e1.getNombre());               
                boolean existe =false;
                for (Empresas e2: all) {
                    //System.out.println("Comparando con empresa " + e2.getNombre());
                    //System.out.println(e2.getNombre() + " " + e2.getIdEmpresa());
                    if (e2.getIdEmpresa() == id) {
                        //System.out.println("Aumentamos id");
                        id++;
                    }
                    if (e1.getCif().equalsIgnoreCase(e2.getCif())) {
                        //LA empresa ya existe
                        if (!e1.getNombre().equalsIgnoreCase(e2.getNombre())) {
                            //NOMBRE DISTINTO = ACTUALIZAMOS
                            e2.setNombre(e1.getNombre());
                            try {
                                session.saveOrUpdate(e2);
                            } catch (Exception ex) {
                                System.out.println(ex.getStackTrace() + " " + ex.getMessage());
                            }
                           
                        }
                        existe=true;
                        break;
                    }
                }
                if (!existe) { //NO EXISTE LA EMPRESA LUEGO LA INTRODUCIMOS
                    try {
                        e1.setIdEmpresa(id);
                        //System.out.println(e1.getNombre() + " " + e1.getCif() + " " + e1.getIdEmpresa());
                        session.save(e1);
                        id++;
                    } catch (Exception ex) {
                        System.out.println("Error al cargar una nueva empresa en la base de datos " + ex.getMessage());
                    }
                }
            }
            //Acabamos de cargar todas las 
            try {
                trans.commit();
                System.out.println("Actualización tabla empresas exitosa!!");
            } catch (Exception ex) {
                System.out.println("Se ha producido un error en el commit de la transacción "+ ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println("Error al iniciar la transacción de empresas" + ex.getMessage());
        }
        //session.close();
    }
    
}
