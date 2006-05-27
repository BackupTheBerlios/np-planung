/*
 * DataExchange.java
 *
 * Created on 16. Juni 2005, 14:07
 */

/*

npImport - Einlesen-Programm für Nachprüfungsplanung
Copyright (c) 2005 Thomas Perl <thp@perli.net>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/


package at.htlpinkafeld.np.frontend.util;

import java.util.*;
import java.lang.ref.*;
import java.lang.reflect.*;

import at.htlpinkafeld.np.devel.*;

/**
 * Diese Klasse (übernommen aus dem Package 
 * net.perli.util.reflection) bietet die Möglichkeit,
 * eine Java-Bean (getter und setter Methoden) mit 
 * einem Dialog (JFrame/JDialog) zu verknüpfen und 
 * damit Daten zu lesen und zu schreiben, ohne 
 * viel Aufwand. Dabei muss einfach das Eingabefeld
 * (JTextField) den gleichen Namen wie das Property
 * haben, und das JTextField muss public deklariert 
 * sein.
 *
 * Diese Klasse entstammt einer Übung 
 * in Programmieren (Idee: Prof. Ritter).
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class DataExchange {
    private java.awt.Window window;
    private Object bean;
    
    private Vector<String> properties_get = new Vector<String>();
    private Vector<String> properties_set = new Vector<String>();
    
    private int mode = 0;
    private static final int READ = 1;
    private static final int WRITE = 2;
    private static final int CLEAR = 3;
    
    /**
     * Erstellt ein neues DataExchange-Objekt.
     *
     * @param window Das Fenster, das die Eingabefelder beinhaltet
     * @param bean Das Objekt, von dem die Daten gelesen/geschrieben werden
     **/
    public DataExchange( java.awt.Window window, Object bean) {
        this.window = window;
        this.bean = bean;
        
        getPropertiesFromObject();
    }
    
    /**
     * Holt eine Liste aller Eigenschaften (getter/setter Methoden)
     * von einem Objekt und speichert diese intern ab.
     **/
    private void getPropertiesFromObject()
    {
        Class c = bean.getClass();
        Method [] m = c.getDeclaredMethods();
        
        for( int i=0; i<m.length; i++)
        {
            String name = m[i].getName();
            String adding = name.substring( 3, 4).toLowerCase() + name.substring( 4);
            
            if( name.startsWith( "get"))
            {
                properties_get.add( adding);
                Logger.debug( this, "found property (gettable): " + adding);
            }
            if( name.startsWith( "set"))
            {
                properties_set.add( adding);
                Logger.debug( this, "found property (settable): " + adding);
            }
        }
    }
    
    /**
     * Schreibt die Daten vom Dialog (JFrame/JDialog) in 
     * das Bean-Objekt (für alle Typen, wo dies möglich ist).
     **/
    public void writeObject()
    {
        mode = WRITE;
        iterateOverFields();
    }
    
    /**
     * Liest Daten vom Objekt und schreibt diese Daten 
     * in den Dialog (für alle Typen, wo dies möglich ist).
     **/
    public void readObject()
    {
        mode = READ;
        iterateOverFields();
    }
    
    /**
     * Versucht, alle Eingabefelder im Fenster zurückzusetzen.
     **/
    public void clearForm()
    {
        mode = CLEAR;
        iterateOverFields();
    }
    
    /**
     * Diese Funktion durchläuft alle Felder des Fensters 
     * und wenn ein passendes Feld gefunden wurde, wird dies 
     * verarbeitet (diese Funktion wird von writeObject() und 
     * readObject() verwendet. Diese Funktion verhält sich 
     * anders, je nachdem auf welchen Wert die Variable "mode" 
     * gesetzt wurde.
     **/
    private void iterateOverFields()
    {
        Class c = window.getClass();
        Field [] f = c.getDeclaredFields();
        
        for( int i=0; i<f.length; i++)
        {
            String name = f[i].getName();
            Logger.debug( this, "found field: " + name);
            if( isProperty( name))
            {
                Logger.debug( this, name + " => IS PROPERTY!");
                Object field;
                try
                {
                    field = f[i].get( window);
                }
                catch( Exception e)
                {
                    Logger.debug( this, "Error: " + e.toString());
                    return;
                }
                
                // Löschen aller Felder
                if( mode == CLEAR)
                {
                    if( field.getClass().equals( javax.swing.JTextField.class))
                    {
                        ((javax.swing.JTextField)field).setText( "");
                    }
                }
                
                // Alle Fehler lesen
                if( mode == READ)
                {
                    Object read_object = readProperty( name);

                    if( field.getClass().equals( javax.swing.JTextField.class))
                    {
                        if( read_object.getClass().equals( String.class))
                        {
                            ((javax.swing.JTextField)field).setText( (String)read_object);
                        }
                        if( read_object.getClass().equals( Integer.class))
                        {
                            ((javax.swing.JTextField)field).setText( ((Integer)read_object).toString());
                        }
                    }
                }
                
                // Alle Felder schreiben
                if( mode == WRITE)
                {
                    Object write_object = null;
                    if( field.getClass().equals( javax.swing.JTextField.class))
                    {
                        write_object = ((javax.swing.JTextField)field).getText();
                    }
                    writeProperty( name, write_object);
                }
            }
        }
    }

    /**
     * Prüft, ob ein bestimmter Name eine 
     * Eigenschaft des mit diesem DataExchange-Objekts 
     * verknüpften Java Bean-Objekts ist.
     *
     * @param name Der Name einer Eigenschaft
     * @return true, wenn das Java-Bean Objekt diese Eigenschaft hat, sonst false
     **/
    private boolean isProperty( String name)
    {
        // Standardmäßig wird das Property gelesen
        Vector v = properties_get;
        
        // Wenn es aber geschrieben werden soll, verwenden wir den anderen Vektor
        if( mode == WRITE)
            v = properties_set;
        
        for( int i=0; i<v.size(); i++)
        {
            if( name.equals( (String)v.get(i)))
                return true;
        }
        
        return false;
    }

    /**
     * Liest einer Wert vom Objekt.
     *
     * @param name Der Java-Bean Eingenschaftenname des Objekts
     * @return Das gelesene Objekt, oder null, wenn nicht erfolgreich
     **/
    private Object readProperty( String name)
    {
        Class c = bean.getClass();
        
        Method [] m = c.getDeclaredMethods();
        
        for( int i=0; i<m.length; i++)
        {
            Method method = m[i];
            if( method.getName().equals( "get" + name.substring( 0, 1).toUpperCase() + name.substring( 1)))
            {
                Class return_class = method.getReturnType();
                try
                {
                    return return_class.cast( method.invoke( bean));
                }
                catch( Exception e)
                {
                    Logger.debug( this, "Fehler: " + e.toString());
                    return null;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Schreibt eine Eingenschaft in das Objekt.
     *
     * @param name Der Java-Bean Eigenschaftsname
     * @param value Der Wert, auf den die Eigenschaft gesetzt wird
     **/
    private void writeProperty( String name, Object value)
    {
        Class c = bean.getClass();
        
        Method [] m = c.getDeclaredMethods();
        
        for( int i=0; i<m.length; i++)
        {
            Method method = m[i];
            Class [] parameters = m[i].getParameterTypes();
            if( method.getName().equals( "set" + name.substring( 0, 1).toUpperCase() + name.substring( 1)))
            {
                try
                {
                    if( name.getClass().equals( parameters[0]))
                    {
                        method.invoke( bean, parameters[0].cast( value));
                    }
                    else
                    {
                        method.invoke( bean, parameters[0].cast( shadowCaster( value, parameters[0])));
                    }
                }
                catch( Exception e)
                {
                    Logger.debug( this, "Fehler bei writeProperty: " + e.toString());
                }
            }
        }
    }

    /**
     * Versucht im Hintergrund ein Objekt auf eine 
     * bestimmte Klasse zu casten, auch wenn das 
     * ursprünglich nicht (direkt) möglich ist.
     * (Der Name dieser Klasse ist eine Referenz auf 
     * das Spiel ShadowCaster, da mir kein besserer 
     * Name für eine Casting-Funktion eingefallen ist, 
     * außerdem ist die Bezeichnung an sich ja gar nicht 
     * so falsch).
     *
     * @param from Objekt, das zu casten ist
     * @param to Klasse, auf die dieses Objekt zu bringen ist
     * @return Objekt auf die "to" Klasse gecastet, oder null wenn Fehler oder nicht möglich
     **/
    private Object shadowCaster( Object from, Class to)
    {
        if( from.getClass().equals( String.class))
        {
            if( to.equals( Integer.class))
            {
                int i;
                try
                {
                    i = Integer.parseInt( (String)from);
                }
                catch( Exception e)
                {
                    Logger.debug( this, "Fehler: " + e.toString());
                    return null;
                }
                return new Integer( i);
            }
        }
        return null;
    }
} 
