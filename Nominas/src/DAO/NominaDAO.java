/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.ArrayList;
import java.util.Date;
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
        this.session = s;
    }
    
    @Override
    public void insert(List <Nomina> n) {
        List<Nomina> all = readAll();
        Transaction trans;
        int id=0;
        try {
            trans = session.beginTransaction(); //Comenzamos la transacción
            for (Nomina n1: n) {
                boolean existe =false;
                for (Nomina n2: all) {
                    if (n2.getIdNomina() == id) {
                        id++;
                    }
                    if (n1.getAnio()==n2.getAnio() && n1.getMes() == n2.getMes() 
                        && Double.compare(n1.getBrutoNomina(), n2.getBrutoNomina()) ==0
                        && Double.compare(n1.getLiquidoNomina(),n2.getLiquidoNomina()) == 0
                        && n1.getTrabajadorbbdd().equals(n2.getTrabajadorbbdd())) {
                        //LA NOMINA YA EXISTE => ACTUALIZAMOS
                        n2.setAccidentesTrabajoEmpresario(n1.getAccidentesTrabajoEmpresario());
                        n2.setBaseEmpresario(n1.getBaseEmpresario());
                        n2.setBrutoAnual(n1.getBrutoAnual());
                        n2.setCosteTotalEmpresario(n1.getCosteTotalEmpresario());
                        n2.setDesempleoEmpresario(n1.getDesempleoEmpresario());
                        n2.setDesempleoTrabajador(n1.getDesempleoTrabajador());
                        n2.setFogasaempresario(n1.getFogasaempresario());
                        n2.setFormacionEmpresario(n1.getFormacionEmpresario());
                        n2.setFormacionTrabajador(n1.getFormacionTrabajador());
                        n2.setImporteAccidentesTrabajoEmpresario(n1.getImporteAccidentesTrabajoEmpresario());
                        n2.setImporteComplementoMes(n1.getImporteComplementoMes());
                        n2.setImporteDesempleoEmpresario(n1.getImporteDesempleoEmpresario());
                        n2.setImporteDesempleoTrabajador(n1.getImporteDesempleoTrabajador());
                        n2.setImporteFogasaempresario(n1.getImporteFogasaempresario());
                        n2.setImporteFormacionEmpresario(n1.getImporteFormacionEmpresario());
                        n2.setImporteFormacionTrabajador(n1.getImporteFormacionTrabajador());
                        n2.setImporteIrpf(n1.getImporteIrpf());
                        n2.setImporteSalarioMes(n1.getImporteSalarioMes());
                        n2.setImporteSeguridadSocialEmpresario(n1.getImporteSeguridadSocialEmpresario());
                        n2.setImporteSeguridadSocialTrabajador(n1.getImporteSeguridadSocialTrabajador());
                        n2.setImporteTrienios(n1.getImporteTrienios());
                        n2.setIrpf(n1.getIrpf());
                        n2.setNumeroTrienios(n1.getNumeroTrienios());
                        n2.setSeguridadSocialEmpresario(n1.getSeguridadSocialEmpresario());
                        n2.setSeguridadSocialTrabajador(n1.getSeguridadSocialTrabajador());
                        n2.setValorProrrateo(n1.getValorProrrateo());
                        session.saveOrUpdate(n2);
                        existe=true;
                        break;
                    }
                }
                if (!existe) { //NO EXISTE LA EMPRESA LUEGO LA INTRODUCIMOS
                    n1.setIdNomina(id);
                    id++;
                    session.save(n1);
                }
            }
            //Acabamos de cargar todas las 
            try {
                trans.commit();
                System.out.println("Actualización tabla nominas exitosa!!");
            } catch (Exception ex) {
                System.out.println("Se ha producido un error en el commit de la transacción "+ ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println("Error al iniciar la transacción de nominas" + ex.getMessage());
        }
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
        //openSession();

        System.out.println("Borrando las nómina de " + t.getNombre() + " " + t.getApellido1() 
        + "(NIF/NIE)=" + t.getNifnie());
        Transaction trans;
        //BORRAMOS TODAS LAS NOMINAS PARA EL TRABAJADOR
        try {
            trans=session.beginTransaction();            
            consulta = "DELETE Nomina n WHERE n.trabajadorbbdd = :trabajador";
            query=session.createQuery(consulta);
            query.setParameter("trabajador",t);
        try {
            query.executeUpdate(); 
            trans.commit();
        } catch (Exception e) {
            System.out.println("Error en el borrado " + e.getMessage());
        }
        } catch (Exception e) {
            System.out.println("Error al construir la query " + e.getMessage());
        }

            

        //closeSession();
    }

    @Override
    public Nomina read(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List <Nomina> readAll() {
        consulta="FROM Nomina";
        try {
            query = session.createQuery(consulta);
            try {
                List <Nomina> listaResultado = query.list();
                return listaResultado;
            } catch (Exception e) {
                System.out.println("Error al ejecutar la query " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error al crear la query " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Nomina getNominaMenorLiquido(int m, int y) {
        try {
        int mes = m;
        int year = y;
        System.out.println(m + " " + y);
        consulta = "SELECT n FROM Nomina n WHERE n.mes=:mes AND n.anio=:anio AND n.liquidoNomina=( SELECT n2.liquidoNomina FROM Nomina n2 WHERE n2.mes=:mes AND n2.anio=:anio)";
        Query q = session.createQuery(consulta);
        q.setParameter("mes",mes);
        q.setParameter("anio",year);
        return (Nomina) q.list().get(0);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
        return null;
    }
}
