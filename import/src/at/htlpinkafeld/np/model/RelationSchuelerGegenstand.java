/*
 * RelationSchuelerGegenstand.java
 *
 * Created on 01. Juni 2005, 17:05
 */

package at.htlpinkafeld.np.model;

/**
 * Die Klasse RelationSchuelerGegenstand speichert je einen 
 * Sch�ler und einen Gegenstand, die miteinander verkn�pft 
 * sind. Wenn nun ein Sch�ler einen Gegenstand hat, der 
 * gepr�ft werden soll, wird eine Relation erstellt.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationSchuelerGegenstand {
    private Schueler schueler;
    private Gegenstand gegenstand;
    
    /**
     * Erstellt eine neue Sch�ler-Gegenstand Relation, mit der 
     * einem Sch�ler die Pr�fungen per Gegenstand zugeteilt werden.
     *
     * @param schueler Der Sch�ler, mit dem diese Relation verkn�pft werden soll
     * @param gegenstand Der Gegenstand, mit dem diese Relation verkn�pft werden soll
     **/
    public RelationSchuelerGegenstand( Schueler schueler, Gegenstand gegenstand) {
        this.schueler = schueler;
        this.gegenstand = gegenstand;
    }

    public Schueler getSchueler() {
        return schueler;
    }

    public void setSchueler(Schueler schueler) {
        this.schueler = schueler;
    }

    public Gegenstand getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(Gegenstand gegenstand) {
        this.gegenstand = gegenstand;
    }
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten 
     * Sch�lers abgerufen werden kann.
     *
     * @return UID-Nummer des Sch�lers
     **/
    public int getSchuelerUid() {
        return schueler.getUid();
    }
    
    /**
     * Hilfsfunktion, mit der die UID des gespeicherten 
     * Gegenstands abgerufen werden kann.
     *
     * @return UID-Nummer des Gegenstands
     **/
    public int getGegenstandUid() {
        return gegenstand.getUid();
    }

}
