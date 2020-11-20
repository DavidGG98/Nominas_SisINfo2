/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.ArrayList;
import java.util.List;
import modelo.Categorias;
import org.hibernate.Session;

/**
 *
 * @author david
 */
public interface CategoriasDAOInterface {
    public void insert (Categorias c);
    public void insert (List <Categorias> c);
    public void update (Categorias c);
    public void delete(int id);
    public Categorias read(int id);
    public List<Categorias> readAll ();
    public void updateAllBut (Categorias c);
    public void setSession (Session s);
}
