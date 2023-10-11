package ae01;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Classe principal que proporciona funcions per gestionar fitxers de text.
 */
public class Principal {

    /**
     * Obté la data de l'última modificació d'un fitxer i la retorna com una cadena de text formateada.
     *
     * @param fitxer Fitxer del qual volem obtenir la data de modificació.
     * @return Una cadena de text que representa la data de modificació.
     */
    private static String data(File fitxer) {
        long ms = fitxer.lastModified();

        Date d = new Date(ms);
        Calendar c = new GregorianCalendar();
        c.setTime(d);

        String dia, mes, any, hora, minut;

        dia = Integer.toString(c.get(Calendar.DATE));
        mes = Integer.toString(c.get(Calendar.MONTH));
        any = Integer.toString(c.get(Calendar.YEAR));
        hora = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        minut = Integer.toString(c.get(Calendar.MINUTE));
        
        return dia + "/" + mes + "/" + any + "  " + hora + ":" + minut;
    }

    /**
     * Llista i retorna una col·lecció de fitxers de text (amb extensió .txt) continguts en una carpeta especificada.
     *
     * @param directori La ruta de la carpeta de la qual es volen llistar els fitxers de text.
     * @param ordenarPer L'ordre pel qual es volen ordenar els fitxers (podeu utilitzar "nom", "data" o "grandària").
     * @param ascendent Cert si es vol ordenar en ordre ascendent, fals si es vol en ordre descendent.
     * @return Una llista de fitxers de text ordenada segons els paràmetres especificats.
     */
	public static List<File> LlistarFitxersTxt(String directori, String ordenarPer, boolean ascendent) {
        List<File> fitxersTxt = new ArrayList<>();
        File directory = new File(directori);
        File[] fitxers = directory.listFiles();

        if (fitxers != null) {
            for (File fitxer : fitxers) {
                if (fitxer.isFile() && fitxer.getName().toLowerCase().endsWith(".txt")) {
                    fitxersTxt.add(fitxer);
                }
            }
        }


        fitxersTxt = OrdenarArxius(fitxersTxt, ordenarPer, ascendent);


        return fitxersTxt;
    }

	/**
	 * Mostra la llista de fitxers en un JTextArea donat, amb informació detallada sobre cada fitxer.
	 *
	 * @param fitxers Una llista de fitxers que es vol mostrar en el JTextArea.
	 * @param fileListTextArea L'àrea de text on es mostrarà la llista de fitxers.
	 */
    public static void MostrarLlistaFitxers(List<File> fitxers, JTextArea fileListTextArea) {
        fileListTextArea.setText("");
        for (File fitxer : fitxers) {
            String fileInfo = String.format("Nom: %s, Extensió: %s, Grandària: %d bytes, Última Modificació: %s \n",
                    fitxer.getName(), ObtenirExtensio(fitxer), fitxer.length(), data(fitxer));
            fileListTextArea.append(fileInfo);
        }
    }

    /**
     * Cerca una cadena de text específica en un fitxer de text i retorna el nombre de vegades que es troba.
     *
     * @param fitxerTxt El fitxer de text en el qual es vol realitzar la cerca.
     * @param buscarString La cadena de text que es vol cercar en el fitxer.
     * @return El nombre de vegades que la cadena de text es troba en el fitxer.
     */
    public static int CercarEnFitxer(File fitxerTxt, String buscarString) {
        int count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fitxerTxt));
            String line;
            while ((line = reader.readLine()) != null) {
                
                int index = line.indexOf(buscarString);
                while (index != -1) {
                    count++;
                    index = line.indexOf(buscarString, index + 1);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Obté l'extensió d'un fitxer donat.
     *
     * @param fitxer El fitxer del qual es vol obtenir l'extensió.
     * @return L'extensió del fitxer o una cadena buida si no en té.
     */
    private static String ObtenirExtensio(File fitxer) {
        String nomFitxer = fitxer.getName();
        int lastDotIndex = nomFitxer.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return nomFitxer.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * Fusiona una llista de fitxers de text en un nou fitxer.
     *
     * @param arxius Llista de fitxers que volem fusionar.
     * @param nomNouArxiu Nom del nou fitxer creat després de la fusió.
     * @param directori Camí del directori on es guardarà el nou fitxer.
     */
    public static void FusionarArxius(List<File> arxius, String nomNouArxiu, String directori) {
        File nuevoArchivo = new File(directori, nomNouArxiu + ".txt");

        if (nuevoArchivo.exists()) {
            int opcion = JOptionPane.showConfirmDialog(null, "El arxiu ja existeix. Vols sobreescriure-lo?", "Confirmació", JOptionPane.YES_NO_OPTION);

            if (opcion != JOptionPane.YES_OPTION) {
                return; 
            }
        }

        try (FileWriter fw = new FileWriter(nuevoArchivo);
                BufferedWriter bw = new BufferedWriter(fw)) {
                for (File arxiu : arxius) {
                    try (FileReader fr = new FileReader(arxiu);
                        BufferedReader br = new BufferedReader(fr)) {
                        String linia;
                        while ((linia = br.readLine()) != null) {
                            bw.write(linia);
                            bw.newLine(); 
                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error al fusionar arxius: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
    }

    /**
     * Ordena una llista de fitxers segons els criteris especificats.
     *
     * @param arxius Llista de fitxers que volem ordenar.
     * @param ordenarPer El paràmetre pel qual volem ordenar els fitxers (Nom, Grandaria, Data Modificacio).
     * @param ascendent Cert si volem ordenar de forma ascendent, Fals si volem ordenar de forma descendent.
     * @return Una llista de fitxers ordenada segons els criteris especificats.
     */
    public static List<File> OrdenarArxius(List<File> arxius, String ordenarPer, boolean ascendent) {
        if ("Nom".equals(ordenarPer)) {
            arxius.sort((f1, f2) -> {
                return ascendent ? f1.getName().compareTo(f2.getName()) : f2.getName().compareTo(f1.getName());
            });
        } else if ("Grandaria".equals(ordenarPer)) {
            arxius.sort((f1, f2) -> {
                return ascendent ? Long.compare(f1.length(), f2.length()) : Long.compare(f2.length(), f1.length());
            });
        } else if ("Data Modificacio".equals(ordenarPer)) {
            arxius.sort((f1, f2) -> {
                return ascendent ? Long.compare(f1.lastModified(), f2.lastModified()) : Long.compare(f2.lastModified(), f1.lastModified());
            });
        } else {
            // Default ordering by name in ascending order
            arxius.sort((f1, f2) -> {
                return ascendent ? f1.getName().compareTo(f2.getName()) : f2.getName().compareTo(f1.getName());
            });
        }

        return arxius;
    }

}
