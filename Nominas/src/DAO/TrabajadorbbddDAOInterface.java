/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.List;
import modelo.Empresas;
import modelo.Trabajadorbbdd;

/**
 *
 * @author david
 */
public interface TrabajadorbbddDAOInterface {
    
    public void insert (Trabajadorbbdd t);
    public void update (Trabajadorbbdd t);
    public void delete (int id);
    public void deleteAllFromCompany (Empresas e);
    public Trabajadorbbdd read(int id);
    public Trabajadorbbdd readNIF(String id);
    public List<Trabajadorbbdd> readAll ();
    
}
