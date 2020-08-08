package com.pkmnleague.game;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/*
 * This class implements a singleton pattern.
 * This class loads and distributes static game data from CSVs into dictionaries.
 * 
 * */
public class Dataset {

		// This is the only instance of Dataset allowed to exist
		private static Dataset datasetInstance = null;
		
		private ArrayList<HashMap> pokemonDB;
		
		private Dataset() {
			//TODO: INIT HERE
			//Load pokemon.csv into pokemonDB
			this.pokemonDB = loadCSV("assets/data/pokemon.csv");
		}
		
		public static Dataset getDataset() {
			if(datasetInstance == null) {
				datasetInstance = new Dataset();
			}
			return datasetInstance;
		}
		
		public HashMap<String,String> lookupPokemon(String name){
			for(int i=0;i<pokemonDB.size();i++) {
				String pokemonName = (String) pokemonDB.get(i).get("name");
				if(pokemonName.equalsIgnoreCase(name)) {
					return pokemonDB.get(i);
				}
			}
			return null;
		}
		
		public HashMap<String,String> lookupPokemon(int id){
			for(int i=0;i<pokemonDB.size();i++) {
				Integer pokemonId = Integer.parseInt((String)pokemonDB.get(i).get("id"));
				if(pokemonId == id) {
					return pokemonDB.get(i);
				}
			}
			return null;
		}
		
		/*
		 * Loads each row of a CSV into a hashmap. These rows are stored in an ArrayList.
		 * The resulting ArrayList<HashMap> is stored in a pointer passed as a param
		 */
		public static ArrayList<HashMap> loadCSV(String filePath) {
			
			ArrayList<HashMap> targetObject = new ArrayList<HashMap>();
			Scanner csvReader = null;
			
			try {
				
				File csv = new File(filePath);
				csvReader = new Scanner(csv);

				String[] header = csvReader.nextLine().split(",",-1);
				while(csvReader.hasNextLine()) {
					String[] line = csvReader.nextLine().split(",",-1);
					
					HashMap<String,String> dict = new HashMap<String,String>();
					for(int i=0;i<header.length;i++) {
						dict.put(header[i],line[i]);
					}
					targetObject.add(dict);
				}
				
				
			} catch (FileNotFoundException e) {
				System.out.printf("FILE '%s' NOT FOUND IN DIRECTORY '%s'!\n",filePath,System.getProperty("user.dir"));
			} finally {
				if (csvReader!=null)
					csvReader.close();
			}
			return targetObject;
		}
	
}
