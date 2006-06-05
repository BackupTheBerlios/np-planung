/*
 * RelationLehrerGegenstand.java
 *
 * Created on 15. Mai 2006, 09:36
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package at.htlpinkafeld.np.model;

import java.util.*;

import at.htlpinkafeld.np.util.*;
/**
 * Die Klasse RelationLehrerGegenstand speichert je einen 
 * Lehrer und einen Gegenstand, die miteinander verknüpft 
 * sind. Wenn nun ein Lehrer einen Gegenstand hat, der 
 * geprüft werden soll, wird eine Relation erstellt.
 *
 * @author Marc Schermann
 */
public class RelationLehrerGegenstand implements SQLizable {    
    private Gegenstand gegenstand;
    private Lehrer lehrer;
    
    /**
     * Erstellt eine neue Gegenstand-Lehrer Relation, 
     * mit dem Gegenstände zu Lehrern zugeteilt 
     * werden können.
     *
     * @param gegenstand Der Gegenstand, mit dem diese Relation verknüpft werden soll
     * @param lehrer Der Lehrer, mit dem diese Relation verknüpft werden soll 
     **/
    public RelationLehrerGegenstand( Gegenstand gegenstand, Lehrer lehrer) {
        this.gegenstand = gegenstand;
        this.lehrer = lehrer;
    }
    
    /**
     * Prüft, ob in einem Vektor von Relationen bereits 
     * eine Relation von einem Lehrer zu einem Gegenstand 
     * besteht. Diese Funktion ist dann hilfreich, wenn 
     * man herausfinden will, ob für einen Gegenstand zusätzlich 
     * eine Relation mit einem anderen Lehrer angelegt 
     * werden soll (Wichtig bei getrennten Gegenständen, 
     * zum Beispiel PRRU, Programmieren oder Englisch.
     *
     * @param relationen Vektor mit Relationen, die durchsucht werden
     * @param gegenstand Der Gegenstand der Relation
     * @param lehrer Der Lehrer der Relation
     * @return true, wenn der gegebene Gegenstand mit dem Lehrer in Relation steht, false wenn nicht
     **/
    public static boolean relationExists( Vector<RelationLehrerGegenstand> relationen, Gegenstand gegenstand, Lehrer lehrer)
    {
        for( int i=0; i<relationen.size(); i++)
        {
            RelationLehrerGegenstand r = relationen.get(i);
            
            if( gegenstand.equalsIgnoreGruppe( r.getGegenstand()) && lehrer.equals( r.getLehrer()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Liefert eine Liste von Lehrern, die zu einer bestimmten 
     * Klasse in einem bestimmten Gegenstand passen. Wenn dieser 
     * Gegenstand NICHT in dieser Klasse gelehrt wird, liefert 
     * diese Funktion einen leeren Lehrer-Vektor zurück. Wenn 
     * der Gegenstand nur von EINEM Lehrer unterrichtet wird, 
     * liefert diese Funktion einen Vektor mit dem einzelnen 
     * Lehrer zurück. Wenn der Gegenstand von mehreren Lehrern 
     * (zB Gruppenteilung) unterrichtet wird, dann liefert
     * diese Funktion einen Vektor mit allen Lehrern zurück, 
     * die diesen Gegenstand unterrichten.
     *
     * @param relationen Die Relationen, die zu durchsuchen sind (als Vektor)
     * @param gegenstand Der Gegenstand, für den die Lehrer zu suchen sind
     * @param klasse Die Klasse, für die die Lehrer zu suchen sind
     * @return Vektor von Lehrern, die für diesen Gegenstand + Klasse zutreffen
     **/
    public static Vector<Lehrer> getLehrerByGegenstandKlasse( Vector <RelationGegenstandLehrerKlasse> relationen, Gegenstand gegenstand, Klasse klasse)
    {
        Vector<Lehrer> lehrer = new Vector<Lehrer>();
        
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGegenstandLehrerKlasse r = relationen.get(i);
            
            if( gegenstand.equalsIgnoreGruppe( r.getGegenstand()) && klasse.equals( r.getKlasse()))
            {
                lehrer.add( r.getLehrer());
            }
        }
        
        return lehrer;
    }   
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten Gegenstands 
     * geholt werden kann.
     *
     * @return UID des Gegenstands dieser Relation
     **/
    public int getGegenstandUid() {
        return gegenstand.getUid();
    }
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten Lehrers 
     * geholt werden kann.
     *
     * @return UID des Lehrers dieser Relation
     **/
    public int getLehrerUid() {
        return lehrer.getUid();
    }
    
    
    /**
     * Wandelt diese Relation in einen String um. Dieser String 
     * ist einfach eine Umwandlung der Objekte (Gegenstand, Lehrer, 
     * Klasse) in einen String, verknüpft mit "-".
     *
     * @return String im Format "(Gegenstand)-(Lehrer)"
     **/
    public String toString() {
        return "(" + gegenstand + ")-(" + lehrer + ")";
    }
    
    // Es folgen die Methoden, die automatisch erzeugt wurden (getter und setter)

    public Gegenstand getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(Gegenstand gegenstand) {
        this.gegenstand = gegenstand;
    }

    public Lehrer getLehrer() {
        return lehrer;
    }

    public void setLehrer(Lehrer lehrer) {
        this.lehrer = lehrer;
    }
    
    /**
     * Wandelt dieses Relationen-Objekt um in einen SQL 
     * "INSERT INTO" - Befehl, mit dem diese Relation 
     * in die Datenbank geschrieben werden kann.
     *
     * @return SQL "INSERT INTO" Befehl, mit dem dieses Objekt in die Datenbank geschrieben werden kann
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.getTableName( DatabaseMetadata.LEHRER_GEGENSTAND);
      
        String geg = gegenstand.getName();
        String leh = lehrer.getName();
        
        return "INSERT INTO " + table + " (Lehrer, Gegenstand) " +
                    "VALUES ('" + leh + "', '" + geg + "')";
    }
}
