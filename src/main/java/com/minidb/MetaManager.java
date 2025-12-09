package com.minidb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

public class MetaManager {

	private static final String catalog_dir = "data" + File.separator + "catalog";
	
	//Storing the metadata of the tables
	public static boolean createTable(String tableName, LinkedHashMap<String,String> columns) {
		
		File metaFile = new File(catalog_dir + File.separator + tableName + ".meta");
		if(metaFile.exists()) {
			System.err.println(Color.RED + "Error: Table '" + tableName + "' already exists!" + Color.RESET);
			return false;
		}
		
		try(FileWriter writer = new FileWriter(metaFile)) {
			for(Map.Entry<String, String> entry: columns.entrySet()) {
				writer.write(entry.getKey() + " " + entry.getValue() + "\n");
			}
			System.out.println(Color.GREEN + "Table '" + tableName + "' created!" + Color.RESET);
			return true;
		}
		catch(IOException e) {
			System.err.println(Color.RED + "Error creating the table: " + e.getMessage() + Color.RESET);
			return false;
		}
	}
	
	
	
	//Loading the metadata of the tables
	public static LinkedHashMap<String,String> loadTable(String tableName) {
		
		File metaFile = new File(catalog_dir + File.separator + tableName + ".meta");
		
		if(!metaFile.exists()) {
			System.err.println(Color.RED + "\nError: Table '" + tableName + "' not exists!" + Color.RESET);
			return null;
		}
		
		LinkedHashMap<String,String> columns = new LinkedHashMap<>();
		try(Scanner scanner = new Scanner(metaFile)) {
			
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if(line.isEmpty()) continue;
				
				String[] parts = line.split("\\s+");
				if(parts.length == 2) 
					columns.put(parts[0], parts[1]);  //columnName and columnType
			}
			//System.out.println("Loaded schema for table: " + tableName);
		}	
		catch(Exception e) {
			System.err.println(Color.RED + "\nError reading the table schema: " + e.getMessage() + Color.RESET);
		}
		return columns;
	}
	
	
	
	//listing all the tables in the catalog folder
	public static List<String> showTables() {
		List<String> tables = new ArrayList<>();
		File catalogDir = new File("data" + File.separator + "catalog");
		
		File[] files = catalogDir.listFiles(); //listFiles() will give all the files so filter it!
		if(files!=null) {
			for(File file: files) {
				if(file.getName().endsWith(".meta")) {
					String tableName = file.getName().replace(".meta","");
					tables.add(tableName);			
				}
			}
		}
		return tables;
	}
	
	
	
	//listing the columns in the table
	public static void describeTable(String tableName) {
		File metaFile = new File("data" + File.separator + "catalog" + File.separator + tableName + ".meta");
		
		if(!metaFile.exists()) {
			System.err.println(Color.RED + "Error: Table '" + tableName + "' does not exist!" + Color.RESET);
			return;
		}
		
		
		List<String[]> rows = new ArrayList<>();
		try(Scanner scanner = new Scanner(metaFile)){
			while(scanner.hasNextLine()) {
				
				String col = scanner.nextLine().trim();
				if(col.isEmpty())
					continue;
				
				String[] parts = col.split("\\s+");
				rows.add(parts);
			}
			
			int col1Length = 11; //"COLUMN NAME" = 11
			int col2Length = 11; //"COLUMN TYPE" = 11
			for(String[] row: rows) {
				col1Length = Math.max(col1Length, row[0].length());
				col2Length = Math.max(col2Length, row[1].length());
			}
			
			System.out.println(Color.CYAN + "Table: " + tableName + Color.RESET);
			printBorder(col1Length,col2Length);
			System.out.printf("| %-" + col1Length + "s | %-" + col2Length + "s |\n", "COLUMN NAME", "COLUMN TYPE");
			printBorder(col1Length,col2Length);
			for(String[] row: rows)
				System.out.printf("| %-" + col1Length + "s | %-" + col2Length + "s |\n", row[0], row[1]);
			printBorder(col1Length,col2Length);
			
		}
		catch(Exception e) {
			System.err.println(Color.RED + "Error reading table schema: " + e.getMessage() + Color.RESET);
		}
		
	}
	private static void printBorder(int col1Length, int col2Length) {
		
		System.out.print("+");
		for(int i=0;i<col1Length+2;i++)
			System.out.print("-");
		System.out.print("+");
		for(int i=0;i<col2Length+2;i++)
			System.out.print("-");
		System.out.println("+");
		
	}
}

