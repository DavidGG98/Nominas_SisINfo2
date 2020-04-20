/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import modelo.Empresas;


/**
 *
 * @author david
 */
public interface EmpresasDAOInterface {
    public void insert (Empresas e);
    public void update (Empresas e);
    public void delete(int id);
   // public void deleteAllWorkers(int id);
    public Empresas read(int id);
}
