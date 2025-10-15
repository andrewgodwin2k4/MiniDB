package com.minidb;

import java.io.File;

public class StorageManager {
	
	private static final String data_dir = "data";
	private static final String catalog_dir = data_dir + File.separator + "catalog";
	private static final String table_dir = data_dir + File.separator + "tables";
	
	//Used to create dir if they don't exist
	public static void initializeStorage() {
		
		createDirectory(data_dir);
		createDirectory(catalog_dir);
		createDirectory(table_dir);
		//System.out.println("Storage directories are initialized!");
		
	}
	
	private static void createDirectory(String path) {
		
		File dir = new File(path);
		if(!dir.exists()) {
			if(dir.mkdirs())
				System.out.println(Color.GREEN + "Created: "+ path + Color.RESET);
			else
				System.err.println(Color.RED + "Failed to create: "+ path + Color.RESET);
		}
			
	}
	
	
}
