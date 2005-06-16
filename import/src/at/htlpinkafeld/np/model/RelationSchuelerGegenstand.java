/*
 * RelationSchuelerGegenstand.java
 *
 * Created on 01. Juni 2005, 17:05
 */

package at.htlpinkafeld.np.model;

/**
 * Die Klasse RelationSchuelerGegenstand speichert je einen 
 * Schüler und einen Gegenstand, die miteinander verknüpft 
 * sind. Wenn nun ein Schüler einen Gegenstand hat, der 
 * geprüft werden soll, wird eine Relation erstellt.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationSchuelerGegenstand {
    private Schueler schueler;
    private Gegenstand gegenstand;
    
    /**
     * Erstellt eine neue Schüler-Gegenstand Relation, mit der 
     * einem Schüler die Prüfungen per Gegenstand zugeteilt werden.
     *
     * @param schueler Der Schüler, mit dem diese Relation verknüpft werden soll
     * @param gegenstand Der Gegenstand, mit dem diese Relation verknüpft werden soll
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
     * Schülers abgerufen werden kann.
     *
     * @return UID-Nummer des Schülers
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
