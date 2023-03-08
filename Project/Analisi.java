package Progetto_ROMANI;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

public class Analisi {
	//metodo che analizza il file iso e richiama il
	//metodo di estrazione dei file zip da esso
	public void analisiISO(String zip, String dir) {
		File path_zip = new File(zip);
		//crea un array che contiene tutti i file dello zip
		File[] lista_file_zip = path_zip.listFiles();
		
		if (lista_file_zip != null) {
			for (File file : lista_file_zip) {
				//il nome viene trasformato tutto in minuscolo
				//per evitare problemi di case sensitivity
				String nome = file.getName().toLowerCase();
					
				//se il file e' uno zip viene estratto
				if (file.isFile() && nome.endsWith(".zip")) {
					estraiZip(file, dir);
				} else if (file.isDirectory()) {
					//se il file e' una directory lo si
					//analizza
					analisiISO(file.getAbsolutePath(), dir);
				}
			}
		}
	}
		
	//metodo che estrae i file zip
	private void estraiZip(File file, String dir) {
		 byte[] buffer = new byte[2048];
	     try {
	    	 FileInputStream fis = new FileInputStream(file);
	         ZipInputStream zis = new ZipInputStream(fis);
	         ZipEntry entry = zis.getNextEntry();
	            
	         while(entry != null){
	             File nuovo_file = new File(dir + File.separator + entry.getName());
	             new File(nuovo_file.getParent()).mkdirs();

	             FileOutputStream fos = new FileOutputStream(nuovo_file);
	             int len;
	             while ((len = zis.read(buffer)) > 0) {
	            	 fos.write(buffer, 0, len);
	             }
	             
	             fos.close();
	             zis.closeEntry();
	             entry = zis.getNextEntry();
	         }
	         
	         zis.closeEntry();
	         zis.close();
	         fis.close();
	         
	    } catch (FileNotFoundException e) {
	    	System.out.println("ERRORE: il file " + 
	    			file.getName() + " non esiste");
	    } catch (IOException e) {
	    	System.out.println("ERRORE di I/O");
	    }
	}
	
	//metodo che recupera i soli file txt e ne analizza
	//lingua, encoding e ID univoco
	public void analisiFileTxt(File dir, 
		HashMap<Integer, Charset> testi, HashMap<Integer, File> libri) {
		//contiene tutti i file della directory dir
		File[] txt_in_dir = dir.listFiles();
		
		//calcola il numero di testi txt analizzati
		int contatore_txt = 0;
		for (File txt : txt_in_dir) {
			//se il file in analisi e' di testo il contatore 
			//viene aggiornato e si procede col cercarne
			//lingua, encoding e ID univoco
			if (txt.getName().toLowerCase().endsWith(".txt")) {
				contatore_txt++;
			
				try {
					BufferedReader reader = new BufferedReader(new FileReader(txt));
					String st = reader.readLine();
					
					//ferma il ciclo while nel momento in cui ha trovato tutto
					//cio' che occorre per definire il testo. Il controllo e'
					//stato inserito per velocizzare il processo di analisi
					boolean fine_controllo = false;
					
					//variabili che definiscono le tre caratteristiche
					//del libro che si vogliono trovare
					boolean english = false;
					int numero_id = 0;
					Charset codifica = null;
					
					//finche' non si raggiunge il pattern ***START
					//vengono analizzate le righe una ad una
					while (st != null && !fine_controllo) {
						//st viene sempre trasformata tutta in 
						//minuscolo per evitare errori di case sensitivity
						
						//controlla che la lingua sia inglese
						if (st.toLowerCase().contains("language:") 
								&& st.toLowerCase().contains("english")) {
							english = true;
						} 
						
						if (st.toLowerCase().contains("[ebook #")) {
							//controlla il numero del testo (ID univoco)
							int indice_hashtag = st.indexOf("#");
							int indice_parentesi = st.indexOf("]");
							numero_id = Integer.parseInt(st.substring(indice_hashtag + 1, indice_parentesi));
						}
						
						if (st.toLowerCase().contains("encoding:")) {
							//definisce l'encoding giusto per ogni testo
							int indice_due_punti = st.indexOf(":");
							String encoding = st.substring(indice_due_punti + 1,st.length()).trim();
							try {
								codifica = Charset.forName(encoding);
							//Exception e' generica in modo che includa
							//sottoclassi come IllegalCharsetNameException
							//e UnsupportedCharsetException
							} catch (Exception e) {
								codifica = Charset.defaultCharset();
							}
						} 
						
						if (st.toLowerCase().contains("***start")) {
							//la riga dopo ***START segna l'inizio del 
							//libro e la fine delle informazioni su
							//di esso
							fine_controllo = true;
						}
						
						st = reader.readLine();
					}
					
					reader.close(); 
					
					//se il testo e' in inglese viene aggiunto alla HashMap
					if (english) {
						//deve essere presente anche la codifica per non
						//incorrere in eccezioni
						if (codifica != null) {
							//se ci fossero due ID uguali verrebbe 
							//sovrascritto quello gia' presente. 
							//Cosi' si preserva solo una copia
							testi.put(numero_id, codifica);
							libri.put(numero_id, txt);
						}
					}
					
				} catch (FileNotFoundException e) {
					System.out.println("ERRORE: il file " + txt.getName() +  " non esiste");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("ERRORE DI I/O");
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Numero totale di file txt: " + contatore_txt);
		System.out.println("Numero di file txt in inglese da analizzare: " + testi.size());
	}
	
	//metodo che calcola il numero di parole contenute in tutti i file txt
	//dell'HashMap libri
	public void calcolaFrequenze(HashMap<Integer, Charset> testi, 
			HashMap<Integer, File> libri, HashMap<String, Integer> frequenze) {
		
		//numero delle parole
		int parole_totali = 0;
		//contatore per il recupero della codifica
		//di ogni testo
		int contatore = 0;
		
		for (File txt : libri.values()) {
			//controllo che considera solo il testo
			//in se' togliendo testa e coda
			boolean testa = false;
			boolean coda = false;
			//recupera la codifica per quel testo txt
			Charset codifica = testi.get(contatore);
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(txt, codifica));
				String st = reader.readLine();
				
				while (!testa && st != null) {
					//se trova la testa (***START) comincia il calcolo delle
					//frequenze delle parole
					if (st.toLowerCase().contains("***start")) {
						testa = true;
						//finche' non trova la coda (***END) calcola le frequenze
						//delle parole
						while (!coda && st != null) {
							if (st.toLowerCase().contains("***end")) {
								//se trova la coda si ferma
								coda = true;
							} else {
								//Pattern che elimina punteggiatura e caratteri
								//speciali dalle parole rilevate
								Pattern pattern = Pattern.compile("[a-zA-Z]+");
								Matcher matcher = pattern.matcher(st);
								
								//analizza le parole ad una ad una
								while (matcher.find()) {
									String parola = matcher.group().toLowerCase();
									if (!frequenze.containsKey(parola)) {
										//aggiorna il calcolo delle parole
										parole_totali++;
										//aggiunge la parola alla HashMap
										frequenze.put(parola, 1);
									} else {
										//aggiunge una unita' alla parola gia' presente
										frequenze.put(parola, frequenze.get(parola) + 1);
									}
								}
								
								st = reader.readLine();
							}
						}
						
					} else {
						st = reader.readLine();
					}
				}
				
				reader.close();
				
			} catch (Exception e) {
				System.out.println("ERRORE");
			}
			
			contatore++;
		}
		
		System.out.println("Numero totale di parole trovate: " + parole_totali);
		
		
	}
	
	//metodo che calcola le frequenze delle frequenze (cioe' quante parole sono
	//presenti con la stessa frequenza e con quale frequenza)
	public void calcolaFrequenzeDelleFrequenze(HashMap<String, Integer> frequenze,
			HashMap<Integer, Integer> frequenze_frequenze) {

		for (int freq : frequenze.values()) {
			if (!frequenze_frequenze.containsKey(freq)) {
				//se la frequenza non e' presente nella HashMap 
				//viene aggiunta
				frequenze_frequenze.put(freq, 1);
			} else {
				//se la frequenza e' gia' presente viene aggiornato
				//il numero di parole che occorrono con quella stessa
				//frequenza
				frequenze_frequenze.put(freq, frequenze_frequenze.get(freq) + 1);
			}
		}
	}
}