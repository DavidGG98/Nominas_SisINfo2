/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import modelo.Nomina;
import modelo.Trabajadorbbdd;

/**
 *
 * @author david
 */
public interface NominaDAOInterface {
    public void insert (Nomina n);
    public void update (Nomina n);
    public void delete(int id);
    public void deleteAllFromWorker (Trabajadorbbdd t);
    public Nomina read(int id);
}
