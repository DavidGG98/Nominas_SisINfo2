/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.Date;
import java.util.List;
import modelo.Nomina;
import modelo.Trabajadorbbdd;
import org.hibernate.Session;

/**
 *
 * @author david
 */
public interface NominaDAOInterface {
    public void insert (Nomina n);
    public void insert (List <Nomina> n);
    public void update (Nomina n);
    public void delete(int id);
    public void deleteAllFromWorker (Trabajadorbbdd t);
    public Nomina read(int id);
    public List <Nomina> readAll();
    public void setSession(Session s);
    public Nomina getNominaMenorLiquido(int m, int y);
}
