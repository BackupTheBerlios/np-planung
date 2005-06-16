/*
 * RelationGruppenteilung.java
 *
 * Created on 14. Juni 2005, 12:25
 */

package at.htlpinkafeld.np.model;

import java.util.*;

import at.htlpinkafeld.np.model.*;

/**
 * Diese Klasse beinhaltet alle Relationen, die 
 * bei einer Gruppenteilung auftreten, sprich:
 * Je eine Klasse und einen Gegenstand und dazu 
 * einen Vektor von Lehrern, die diesen Gegenstand 
 * in einer Klasse unterrichten.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationGruppenteilung {
    private Klasse klasse;
    private Gegenstand gegenstand;
    private Vector<Lehrer> lehrer;
    
    /**
     * Erstellt eine neue Relation für die Gruppenteilung.
     *
     * @param klasse Die Klasse, die zur Relation gehört
     * @param gegenstand Der Gegenstand, der zur Relation gehört
     * @param lehrer Ein Vektor von Lehrern, die den genannten Gegenstand in der genannten Klasse unterrichten
     **/
    public RelationGruppenteilung( Klasse klasse, Gegenstand gegenstand, Vector<Lehrer> lehrer)
    {
        this.klasse = klasse;
        this.gegenstand = gegenstand;
        this.lehrer = lehrer;
    }
    
    public static boolean relationExists( Vector<RelationGruppenteilung> relationen, Klasse klasse, Gegenstand gegenstand)
    {
        for( int i=0; i<relationen.size(); i++)
        {
            RelationGruppenteilung relation = relationen.get( i);
            
            if( relation.getKlasse().equals( klasse) && relation.getGegenstand().equalsIgnoreGruppe( gegenstand))
                return true;
        }
        
        return false;
    }

    public Klasse getKlasse() {
        return klasse;
    }

    public void setKlasse(Klasse klasse) {
        this.klasse = klasse;
    }

    public Gegenstand getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(Gegenstand gegenstand) {
        this.gegenstand = gegenstand;
    }

    public Vector<Lehrer> getLehrer() {
        return lehrer;
    }

    public void setLehrer(Vector<Lehrer> lehrer) {
        this.lehrer = lehrer;
    }
    
}
