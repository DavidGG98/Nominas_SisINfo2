/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import modelo.Trabajadorbbdd;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author david
 */
public class GeneradorDOM {

    private String ruta = "resources/";
    private Document doc;
    private Element element;

    public GeneradorDOM(int n) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.newDocument();
        switch (n) {
            case 0: //Errores trabajadores
                element = doc.createElement("Trabajadores");
                doc.appendChild(element);
                break;
            case 1: //Errores Cuentas Banco
                element = doc.createElement("Cuentas");
                doc.appendChild(element);
                break;
        }
    }

    //Rellenamos el doc con la información que queramos
    public void addTrabajador(Trabajadorbbdd t, int c) {
        Element trabajador = doc.createElement("trabajador");
        try {
            trabajador.setAttribute("Columna", Integer.toString(c));
        } catch (Exception e) {
            System.out.println("Error al introducir el id (Columna) " + e);
        }
        element.appendChild(trabajador);
        try {
            Element nombre = doc.createElement("Nombre");
            nombre.setTextContent(t.getNombre());
            trabajador.appendChild(nombre);
        } catch (Exception e) {
            System.out.println("Error con el nombre " + e);
        }
        try {
            Element apellido1 = doc.createElement("Apellido_1");
            //apellido1.setNodeValue(t.getApellido1());
            apellido1.setTextContent(t.getApellido1());
            trabajador.appendChild(apellido1);
        } catch (Exception e) {
            System.out.println("Error con Apellido 1 " + t.getApellido1() + "=>" + e);
        }
        try {
            Element apellido2 = doc.createElement("Apellido_2");
            apellido2.setTextContent(t.getApellido2());
            trabajador.appendChild(apellido2);
        } catch (Exception e) {
            System.out.println("Error con Apellido 2 " + t.getApellido2() + "=>" + e);
        }
        try {
            Element empresa = doc.createElement("Empresa");
            empresa.setTextContent(t.getEmpresas().getNombre());
            trabajador.appendChild(empresa);
        } catch (Exception e) {
            System.out.println("Error con la empresa " + e);
        }
        try {
            Element categoria = doc.createElement("Categoria");
            categoria.setTextContent(t.getCategorias().getNombreCategoria());
            trabajador.appendChild(categoria);
        } catch (Exception e) {
            System.out.println("Error con categoria " + e);
        }

    }

    public void addCuenta(Trabajadorbbdd t, String CC, String IBAN, int c) {
        Element cuenta = doc.createElement("cuenta");
        try {
            cuenta.setAttribute("Columna", Integer.toString(c));
        } catch (Exception e) {
            System.out.println("Error al introducir el id (Columna) " + e);
        }
        element.appendChild(cuenta);
        try {
            Element nombre = doc.createElement("Nombre");
            nombre.setTextContent(t.getNombre());
            cuenta.appendChild(nombre);
        } catch (Exception e) {
            System.out.println("Error con el nombre " + e);
        }
        try {
            Element apellido1 = doc.createElement("Apellido_1");
            //apellido1.setNodeValue(t.getApellido1());
            apellido1.setTextContent(t.getApellido1());
            cuenta.appendChild(apellido1);
        } catch (Exception e) {
            System.out.println("Error con Apellido 1 " + t.getApellido1() + "=>" + e);
        }
        try {
            Element apellido2 = doc.createElement("Apellido_2");
            apellido2.setTextContent(t.getApellido2());
            cuenta.appendChild(apellido2);
        } catch (Exception e) {
            System.out.println("Error con Apellido 2 " + t.getApellido2() + "=>" + e);
        }
        try {
            Element empresa = doc.createElement("Empresa");
            empresa.setTextContent(t.getEmpresas().getNombre());
            cuenta.appendChild(empresa);
        } catch (Exception e) {
            System.out.println("Error con la empresa " + e);
        }
        try {
            Element cc = doc.createElement("Codigo_Erroneo");

            cc.setTextContent(CC);
            cuenta.appendChild(cc);
        } catch (Exception e) {
            System.out.println("Error con el codigo de cuenta" + e);
        }
        try {
            Element iban = doc.createElement("Codigo_IBAN");
            iban.setTextContent(IBAN);
            cuenta.appendChild(iban);
        } catch (Exception e) {
            System.out.println("Error con el IBAN " + e);
        }
    }

    public void generaXML(String nombre) throws TransformerConfigurationException, IOException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        try {
            Source source = new DOMSource(doc);
            File file = new File(ruta + nombre + ".xml");
            FileWriter fw = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);
            Result result = new StreamResult(pw);

            transformer.transform(source, result);

        } catch (Exception e) {
            System.out.println("Se ha producido una excepcion al crear el XML " + e);
        }
    }
}
