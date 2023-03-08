package Progetto_ROMANI;

import java.util.*;
import java.io.*; 

public class Output {
	//metodo che scrive un file excel con le frequenze
	//di ogni parola trovata
	public void scriviFileFrequenze(String dir, HashMap<String, Integer> frequenze) {
		//crea la cartella che conterra' 
		//i file csv con i risultati
		File directory = new File(dir);
		if (!directory.exists()) {
			directory.mkdirs();
		}	
		
		//crea il file csv che conterra' i risultati
		File tabella = new File(dir + "\\Frequenza_parole.csv");
		
		try {
			FileWriter writer = new FileWriter(tabella);
			//HashMap nella quale saranno scritti i valori delle frequenze
			//ordinati
			HashMap<String, Integer> hashmap_ordinata = new LinkedHashMap<String, Integer>();
			//ordinamento, in senso descrescente, delle frequenze
			frequenze.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator
					.reverseOrder())).forEachOrdered(x -> hashmap_ordinata.put(x.getKey(), 
							x.getValue()));
			
			//scrittura dei titoli delle due colonne del file csv
			writer.append("Parola").append(";").append("Frequenza").append("\n");

			//scrittura delle frequenze nel file csv
			for (HashMap.Entry<String, Integer> entry : hashmap_ordinata.entrySet()){
				writer.append(entry.getKey())
		          .append(';')
		          .append(entry.getValue().toString())
		          .append("\n");
			}
			
			writer.flush();
			writer.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("ERRORE: il file non esiste");
		} catch (IOException e) {
			System.out.println("ERRORE di I/O");
		}
	}
	
	//metodo che scrive in un file csv le frequenze 
	//delle frequenze delle parole
	public void scriviFileFrequenzeFrequenze(String dir, HashMap<Integer, 
			Integer> frequenze_frequenze) {
		//crea il file csv che conterra' i risultati
		File tabella = new File(dir + "\\Frequenza_frequenze.csv");
		
		try {
			FileWriter writer = new FileWriter(tabella);
			//HashMap nella quale vengono scritti i valori delle frequenze
			//ordinati in ordine decrescente
			HashMap<Integer, Integer> hashmap_ordinata = new LinkedHashMap<Integer, Integer>();
			//ordinamento descrescente delle frequenze delle frequenze
			frequenze_frequenze.entrySet().stream().sorted(Map.Entry
					.comparingByKey(Comparator.reverseOrder())).forEachOrdered(x -> 
						hashmap_ordinata.put(x.getKey(), x.getValue()));
			
			//scrittura dei titoli delle due colonne del file csv
			writer.append("Frequenze delle frequenze").append(";").append("Frequenze").append("\n");
			
			//scrittura delle frequenze delle frequenze sul file
			for (Map.Entry<Integer, Integer> entry : hashmap_ordinata.entrySet()){
				writer.append(entry.getKey().toString()).append(';') 
					.append(entry.getValue().toString()).append("\n");
			}
			
			writer.flush();
			writer.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("ERRORE: il file non esiste");
		} catch (IOException e) {
			System.out.println("ERRORE di I/O");
		}
	}
}