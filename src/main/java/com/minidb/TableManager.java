package com.minidb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TableManager {

	private static final String table_dir = "data" + File.separator + "tables";
	
	public static boolean createTableFile(String tableName) {
		
		File tableFile = new File(table_dir + File.separator + tableName + ".tbl");
		
		if(tableFile.exists()) {
			 System.err.println(Color.RED + "\nError: Table file for '" + tableName + "' already exists." + Color.RESET);
			 return false;
		}
		
		try {
			if(tableFile.createNewFile()) {
				//System.out.println("Created table file at: " + tableFile.getPath());
				return true;
			}
			else {
				System.err.println(Color.RED + "\nFailed to create table file for " + tableName + Color.RESET);
				return false;
			}
		}
		catch(IOException e) {
			System.err.println(Color.RED + "\nError creating table file: " + e.getMessage() + Color.RESET);
			return false;
		}
		
	}
	
	
	
	
	public static boolean insertRow(String tableName, List<String> values) {
		
		LinkedHashMap<String,String> schema = MetaManager.loadTable(tableName);
		if(schema == null)
			return false;
		
		//check column count is equal
		if(values.size() != schema.size()) {
			System.err.println(Color.RED + "\nError: Column count mismatch. Expected " + schema.size() + " values" + Color.RESET);
			return false;
		}
		
		//Type validation
		int i = 0;
		for(Map.Entry<String,String> entry: schema.entrySet()) {
			String colType = entry.getValue();
			String value = values.get(i).trim();
			
			if(colType.equalsIgnoreCase("INT")) {
				try {
					Integer.parseInt(value);
				}
				catch(NumberFormatException e) {
					System.err.println(Color.RED + "\nError: Column '" + entry.getKey() + "' expects INT, got '" + value + "'" + Color.RESET);
					return false;
				}
			}
			else if(colType.equalsIgnoreCase("TEXT")) {
				if((value.startsWith("'")) && value.endsWith("'") ||
						value.startsWith("\"") && value.endsWith("\"")) {
					values.set(i, value.substring(1, value.length()-1));
				}
			}
			i++;
		}
		
		
		//Now insert the row in .tbl
		String row = String.join("|",values);
		File tableFile = new File(table_dir + File.separator + tableName + ".tbl");
		
		try(FileWriter writer = new FileWriter(tableFile,true)) {
			writer.write(row + "\n");
			System.out.println(Color.GREEN + "\nRow inserted in '" + tableName + "' successfully" + Color.RESET);
			return true;
		}
		catch(IOException e) {
			System.err.println(Color.RED + "\nError writing to table file: " + e.getMessage() + Color.RESET);
			return false;
		}
	}

	
	
	
	//Select operation
	public static void displayTable(List<String> headers, List<List<String>> rows) {
		
		int[] colLengths = new int[headers.size()];
		
		for(int i=0;i<headers.size();i++)
			colLengths[i] = headers.get(i).length();
		for(List<String> row: rows) {
			for(int i=0;i<headers.size();i++)
				colLengths[i] = Math.max(colLengths[i], row.get(i).length());
		}
		
		printBorder(colLengths);
		printRow(headers, colLengths);
		printBorder(colLengths);
		for(List<String> row: rows)
			printRow(row, colLengths);
		printBorder(colLengths);

	}
	private static void printBorder(int[] colLengths) {
		
		System.out.print("+");
		for(int len: colLengths) {
			for(int i=0; i<len+2; i++)
				System.out.print("-");
			System.out.print("+");
		}
		System.out.println();
	}
	private static void printRow(List<String> row, int[] colLengths) {
		
		System.out.print("|");
		for(int i=0;i<row.size();i++)
			System.out.printf(" %-" + colLengths[i] + "s |", row.get(i));
		System.out.println();
		
	}
	public static List<List<String>> readAllRows(String tableName) {
		
		List<List<String>> rows = new ArrayList<>();
		File tableFile = new File(table_dir + File.separator + tableName + ".tbl");
		
		if(!tableFile.exists()) {
			 System.err.println(Color.RED + "Error: Table file for '" + tableName + "' does not exist." + Color.RESET);
			 return rows;
		}
		
		try(Scanner scanner = new Scanner(tableFile)) {
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if(line.isEmpty()) continue;
				
				String[] values = line.split("\\|");
				List<String> row = new ArrayList<>();
				for(String val: values)
					row.add(val);
				rows.add(row);
			}
		}
		catch(Exception e) {
			System.err.println(Color.RED + "Error reading the table file: " + e.getMessage() + Color.RESET);
		}
		
		return rows;
	}
	
	
	
	
	public static boolean dropTable(String tableName) {

		File tableFile = new File(table_dir + File.separator + tableName + ".tbl");
		File metaFile = new File("data" + File.separator + "catalog" + File.separator + tableName + ".meta");
		boolean tableDropped = true, metaDropped = true;
		
		if(tableFile.exists())
			tableDropped = tableFile.delete();
		if(metaFile.exists())
			metaDropped = metaFile.delete();
		
		return tableDropped && metaDropped;
	}
	
	
	
	
	public static void overWriteTable(String tableName, List<List<String>> rows) {
		
		File tableFile = new File(table_dir + File.separator + tableName + ".tbl");
		try(FileWriter writer = new FileWriter(tableFile,false)) {
			for(List<String> row : rows) 
				writer.write(String.join("|",row) + "\n");
		}
		catch(IOException e) {
			System.err.println(Color.RED + "Error: " + e.getMessage() + Color.RESET);
		}
		
	}
	
}
