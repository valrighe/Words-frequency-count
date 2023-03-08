package Progetto_ROMANI;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;

public class MainClass {
	public static void main(String[] args) {
		
		//file zip di partenza
		String file_zip = "C:\\Users\\valli\\OneDrive\\Desktop\\iso";
		//directory in cui il file viene unzippato
		String dir_dest = "C:\\Users\\valli\\OneDrive\\Desktop\\Unzip";
		//directory in cui vengono inseriti i calcoli delle frequenze
		String dir_risultato = "C:\\Users\\valli\\OneDrive\\Desktop\\Output";
		
		//Integer e' l'ID univoco del libro, Charset e' la codifica
		HashMap<Integer, Charset> testi = new HashMap<>();
		//Integer e' l'ID univoco del libro, File e' il libro stesso
		HashMap<Integer, File> libri = new HashMap<>();
		//String e' la parola, Integer e' il numero di volte
		//che appare nei testi
		HashMap<String, Integer> frequenze = new HashMap<>();
		//la key Integer e' il numero di parole che compaiono
		//lo stesso numero di volte (frequenze), 
		//il value Integer e' il numero di volte (frequenze delle frequenze)
		HashMap<Integer, Integer> frequenze_frequenze = new HashMap<>();
		
		Analisi analisi = new Analisi();
		Output output = new Output();
		
		File directory = new File(dir_dest);
		//se la directory non esiste viene creata e vi vengono
		//inseriti i file estratti
		if (!directory.exists()) {
			//estrae i file dallo zip
			System.out.println("UNZIP DEL FILE ISO...");
			analisi.analisiISO(file_zip, dir_dest);
		}
			
		System.out.println();
		//analizza i file estratti
		System.out.println("ANALISI DEI FILE DI TESTO...");
		analisi.analisiFileTxt(directory, testi, libri);
		
		System.out.println();
		//calcola le frequenze delle parole e le frequenze
		//delle frequenze (quante parole appaiono lo stesso
		//numero di volte)
		System.out.println("CALCOLO DELLE FREQUENZE...");	
		analisi.calcolaFrequenze(testi, libri, frequenze);
		analisi.calcolaFrequenzeDelleFrequenze(frequenze, frequenze_frequenze);
		
		System.out.println();
		//genera i due file csv che contengono i risultati 
		//dei calcoli delle frequenze
		System.out.println("GENERAZIONE DEI FILE CSV...");	
		output.scriviFileFrequenze(dir_risultato, frequenze);
		output.scriviFileFrequenzeFrequenze(dir_risultato, frequenze_frequenze);
		
		System.out.println();
		System.out.println("OPERAZIONE COMPLETATA CON SUCCESSO");
		System.out.println();
		System.out.println("I file con i risultati si trovano nella cartella " 
				+ dir_risultato);	
	}
}