/*
 * MyFileFilter.java
 *
 * Created on 20. Februar 2006, 10:34
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package at.htlpinkafeld.np.util;
/**
 * Diese Klasse erstellt einen neuen FileFilter.
 * Der FileFilter wird beim Speichern und Öffnen von Dateien
 * (Log-Datei, Konfigurationsdatei) verwendet, um nur die
 * benutzten Dateitypen anzuzeigen.
 *
 * @author Marc Schermann
 */
import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.filechooser.*;

public class MyFileFilter extends FileFilter {

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";

    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    /**
     * Erstellt einen FileFilter. 
     * Sind keine Filter hinzugefügt, werden alle Dateien akzeptiert.
     */
    public MyFileFilter() {
	this.filters = new Hashtable();
    }

    /**
     * Erstellt einen FileFilter, der Dateien mit der angegebenen Erweiterung akzeptiert. <br>
     * Beispiel: new MyFileFilter("jpg");
     */
    public MyFileFilter(String extension) {
	this(extension,null);
    }

    /**
     * Erstellt einen FileFilter, der den angegebenen Dateityp akzeptiert. <br>
     * Beispiel: new MyFileFilter("jpg", "JPEG Image Images");
     * <br><br>
     * Beachte, dass der "." vor der Erweiterung nicht benötigt wird.
     * Wenn er dennoch angegeben wird, wird er ignoriert.
     */
    public MyFileFilter(String extension, String description) {
	this();
	if(extension!=null) addExtension(extension);
 	if(description!=null) setDescription(description);
    }

    /**
     * Erstellt einen FileFilter aus einem gegebenen String-Feld.
     * Beispiel: new MyFileFilter(String {"gif", "jpg"});
     * <br><br>
     * Beachte, dass der "." vor der Erweiterung nicht benötigt wird.
     * Wenn er dennoch angegeben wird, wird er ignoriert.
     */
    public MyFileFilter(String[] filters) {
	this(filters, null);
    }

    /**
     * Erstellt einen FileFilter aus einem gegebenen String-Feld mit Beschreibung.
     * Beispiel: new MyFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
     * <br><br>
     * Beachte, dass der "." vor der Erweiterung nicht benötigt wird.
     * Wenn er dennoch angegeben wird, wird er ignoriert.
     */
    public MyFileFilter(String[] filters, String description) {
	this();
	for (int i = 0; i < filters.length; i++) {
	    // fügt Filter nach der Reihe hinzu
	    addExtension(filters[i]);
	}
 	if(description!=null) setDescription(description);
    }

    /**
     * Schickt true zurück, wenn die Datei im directory pane angezeigt werden soll,
     * anderfalls false.
     *<br><br>
     * Dateien, die mit "." beginnen, werden ignoriert.
     */
    public boolean accept(File f) {
	if(f != null) {
	    if(f.isDirectory()) {
		return true;
	    }
	    String extension = getExtension(f);
	    if(extension != null && filters.get(getExtension(f)) != null) {
		return true;
	    };
	}
	return false;
    }

    /**
     * Schickt die Erweiterung des Dateinamens zurück.
     */
     public String getExtension(File f) {
	if(f != null) {
	    String filename = f.getName();
	    int i = filename.lastIndexOf('.');
	    if(i>0 && i<filename.length()-1) {
		return filename.substring(i+1).toLowerCase();
	    };
	}
	return null;
    }

    /**
     * Fügt eine Erweiterung dem Filter hinzu.
     * <br>
     * Beispiel: Der folgende Code erzeugt einen FileFilter, der nur Dateien
     * mit den Endungen ".jpg" und ".tif" anzeigt:
     * <p>
     *   MyFileFilter filter = new MyFileFilter(); <br>
     *   filter.addExtension("jpg"); <br>
     *   filter.addExtension("tif"); <br>
     * <p>
     * Beachte, dass der "." vor der Erweiterung nicht benötigt wird.
     * Wenn er dennoch angegeben wird, wird er ignoriert.
     */
    public void addExtension(String extension) {
	if(filters == null) {
	    filters = new Hashtable(5);
	}
	filters.put(extension.toLowerCase(), this);
	fullDescription = null;
    }


    /**
     * Schickt die Beschreibung des Filters zurück. <br>
     * Beispiel: "JPEG and GIF Image Files (*.jpg, *gif)"
     */
    public String getDescription() {
	if(fullDescription == null) {
	    if(description == null || isExtensionListInDescription()) {
 		fullDescription = description==null ? "(" : description + " (";
		// build the description from the extension list
		Enumeration extensions = filters.keys();
		if(extensions != null) {
		    fullDescription += "." + (String) extensions.nextElement();
		    while (extensions.hasMoreElements()) {
			fullDescription += ", ." + (String) extensions.nextElement();
		    }
		}
		fullDescription += ")";
	    } else {
		fullDescription = description;
	    }
	}
	return fullDescription;
    }

    /**
     * Setzt eine Beschreibung für den Filter. <br>
     * Beispiel: filter.setDescription("Gif and JPG Images");
     */
    public void setDescription(String description) {
	this.description = description;
	fullDescription = null;
    }

    /**
     * Entscheidet, ob die Erweiterungsliste (.jpg, .gif, etc) in der
     * Beschreibung angezeigt werden soll.
     * <br><br>
     * Dies ist nur relevant, wenn eine Beschreibung im Konstruktor oder
     * mittels setDescription(); hinzugefügt wurde.
     */
    public void setExtensionListInDescription(boolean b) {
	useExtensionsInDescription = b;
	fullDescription = null;
    }

    /**
     * Schickt true zurück, wenn die Erweiterungsliste (.jpg, .gif, etc)
     * in der Beschreibung angezeigt werden soll.
     * <br><br>
     * Dies ist nur relevant, wenn eine Beschreibung im Konstruktor oder
     * mittels setDescription(); hinzugefügt wurde.
     */
    public boolean isExtensionListInDescription() {
	return useExtensionsInDescription;
    }
}

