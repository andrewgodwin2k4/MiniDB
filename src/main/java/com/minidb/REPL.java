package com.minidb;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


public class REPL {
	
    public void run() {
  
    	try {
    		
    		Terminal terminal = TerminalBuilder.builder().system(true).build();
    		LineReader reader = LineReaderBuilder.builder().terminal(terminal).history(new DefaultHistory()).build();
    		
    		System.out.println(Color.YELLOW + "╔══════════════════════════════════════════════╗" + Color.RESET);
    		System.out.println(Color.YELLOW + "║                   MiniDB                     ║" + Color.RESET);
    		System.out.println(Color.YELLOW + "║                                              ║" + Color.RESET);
    		System.out.println(Color.YELLOW + "║  Copyrights Reserved (2025) Andrew Godwin J  ║" + Color.RESET);
    		System.out.println(Color.YELLOW + "╚══════════════════════════════════════════════╝" + Color.RESET);
            System.out.println();
    		
    		
    		System.out.println(Color.CYAN + "MiniDB started... Type 'exit' to quit!" + Color.RESET);
    		System.out.println();
    		while(true) {
    			String line = reader.readLine("minidb> ").trim();
    			//Jline echoes the input commands due to the running in dumb terminal.
    			//if we don't want it to echo, better use scanner. but the history feature will be missed
    			if(line.equalsIgnoreCase("exit"))
    				break;
    			if(line.isEmpty())
    				continue;
    			
    			try {
    				executeCommand(line);
    			}
    			catch(Exception e) {
    				System.err.println(Color.RED + "Error: " + e.getMessage() + Color.RESET);
    			}
    			System.out.println();
    		}
    		System.out.println(Color.YELLOW + "MiniDB stopped, Bye!" + Color.RESET);
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "Error: " + e.getMessage() + Color.RESET);
    	}
    	 
    }
    
    
    
    
    private static void executeCommand(String line) {
    	
    	if(line.endsWith(";"))
    		line = line.substring(0,line.length()-1);
    	
    	if(line.toUpperCase().startsWith("CREATE TABLE"))
    		handleCreateTable(line);
    	else if(line.toUpperCase().startsWith("INSERT INTO"))
    		handleInsert(line);
    	else if(line.toUpperCase().startsWith("SELECT"))
    		handleSelect(line);
    	else if(line.toUpperCase().startsWith("DROP TABLE"))
    		handleDropTable(line);
    	else if(line.equalsIgnoreCase("SHOW TABLES"))
    		handleShowTables();
    	else if(line.toUpperCase().startsWith("DESC"))
    		handleDescribeTable(line);
    	else if(line.toUpperCase().startsWith("DELETE FROM"))
    		handleDelete(line);
    	else if(line.toUpperCase().startsWith("UPDATE"))
    		handleUpdate(line);
    	else
    		System.err.println(Color.RED + "Unrecognized Command!" + Color.RESET);
    	
    }
    
    
    
    
    //CREATE TABLE tableName (col1 col1Type, col2 col2Type, ...);
    private static void handleCreateTable(String line) {
    	try {
    		
    		Pattern tp = Pattern.compile("(?i)CREATE\\s+TABLE\\s+([^(\\s]+)");
    		Matcher tm = tp.matcher(line);
    		if(!tm.find()) {
    			System.err.println(Color.RED + "\nSyntax Error: Missing Table Name" + Color.RESET);
    			return;
    		}
    		String tableName = tm.group(1).trim();
    		
    		Pattern p = Pattern.compile("\\((.*?)\\)");
    		Matcher m = p.matcher(line);
    		if(!m.find()) {
    			System.err.println(Color.RED + "\nSyntax Error: Missing Column Names" + Color.RESET);
    			return;
    		}
    		
    		String part = m.group(1);
    		String[] defns = part.split(",");
    		
    		LinkedHashMap<String,String> columns = new LinkedHashMap<>();
    		for(String defn : defns) {
    			String[] part2 = defn.trim().split("\\s+");
    			if(part2.length != 2) {
    				System.err.println(Color.RED + "\nSyntax Error in Column Definition: " + defn + Color.RESET);
    				return;
    			}
    			columns.put(part2[0].trim(),part2[1].trim().toUpperCase());
    		}
    		
    		//actually creating the table
    		if(MetaManager.createTable(tableName, columns)) {
    			TableManager.createTableFile(tableName);
    			System.out.println(Color.GREEN + "\nTable '" + tableName + "' created successfully!" + Color.RESET);
    		}
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "\nError creating the table: " + e.getMessage() + Color.RESET);
    	}
    }
    
    
    
    
    //INSERT INTO tableName VALUES (col1val, col2val,...);
    private static void handleInsert(String line) {
    	try {
    		
    		String[] parts = line.split("\\s+",4);
    		String tableName = parts[2];
    		
    		Pattern p = Pattern.compile("\\((.*?)\\)");
    		Matcher m = p.matcher(line);
    		if(!m.find()) {
    			System.err.println(Color.RED + "\nSyntax Error: Missing VALUES part" + Color.RESET);
    			return;
    		}
    		
    		String part = m.group(1);
    		String[] vals = part.split(",");
    		
    		List<String> values = new ArrayList<>();
    		for(String x: vals) 
    			values.add(x.trim());
    		
    		TableManager.insertRow(tableName, values);
  
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "\nError inserting row: " + e.getMessage() + Color.RESET);
    	}
    }
    
    
    
    
    //SELECT * FROM tableName;
    //WHERE colName = colValue 
    private static void handleSelect(String line) {
    	try {
    		
    		Pattern p = Pattern.compile("(?i)SELECT\\s+(.+)\\s+FROM\\s+(\\S+)(?:\\s+WHERE\\s+(.+))?");
    		Matcher m = p.matcher(line);
    		if(!m.find()) {
    			System.err.println(Color.RED + "Syntax Error, Expected: SELECT * FROM tableName [WHERE colName = colValue]" + Color.RESET);
    			return;
    		}
    		
    		String cols = m.group(1).trim();
    		String tableName = m.group(2).trim();
    		String where;
    		if(m.group(3)!=null)
    			where = m.group(3).trim();
    		else
    			where = null;
    		
    		List<List<String>> rows = TableManager.readAllRows(tableName);
    		if(rows.isEmpty()) {
    			System.err.println(Color.RED + "No rows found!" + Color.RESET);
    			return;
    		}
    		
    		LinkedHashMap<String,String> schema = MetaManager.loadTable(tableName);
    		if(schema == null)
    			return;
    		
    		List<String> headers = new ArrayList<>(schema.keySet());
    		if(where!=null) 
    			rows = filterRows(rows, headers, where);
    		
    		if(!rows.isEmpty()) {
    			List<String> newCols = new ArrayList<>();
    			if(cols.equals("*"))
					newCols.addAll(headers);
				else {
					String[] sepCols = cols.split(",");
					for(String sepCol: sepCols) {
						if(!headers.contains(sepCol.trim())) {
							System.err.println(Color.RED + "Column '" + sepCol + "' does not exist in table '" + tableName + "'!" + Color.RESET);
		                    return;
						}
						newCols.add(sepCol);
					}
				}
    			
    			List<Integer> colIndex = new ArrayList<>();
    			for(String newCol: newCols) 
    				colIndex.add(headers.indexOf(newCol));
    			
    			List<List<String>> newRows = new ArrayList<>();
    			for(List<String> row: rows) {
    				List<String> newRow = new ArrayList<>();
    				for(int index: colIndex) {
    					newRow.add(row.get(index));
    				}
    				newRows.add(newRow);
    			}
    			
    			TableManager.displayTable(newCols, newRows);
    		}
    		else
    			System.err.println(Color.RED + "No rows selected for WHERE " + where + Color.RESET);
    		
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "Error selecting the data: " + e.getMessage() + Color.RESET);
    	}
    }
    
    
    
    
    //Filtering rows based on some condition
    private static List<List<String>> filterRows(List<List<String>> rows, List<String> headers, String where){
    	
    	List<List<String>> filteredRows = new ArrayList<>();
    	try {
    		
    		String[] orParts = where.split("(?i)\\s+OR\\s+");
    		for(List<String> row: rows) {
    			
    			boolean orOkay = false;
    			for(String orPart: orParts) {
    				
    				String[] andParts = orPart.split("(?i)\\s+AND\\s+");
    				boolean andOkay = true;
    				
    				for(String andPart: andParts) {
    					
    					String[] condition = andPart.split("=");
    					if (condition.length != 2) {
                            System.err.println(Color.RED + "Syntax Error in WHERE condition: " + andPart + Color.RESET);
                            return filteredRows;
                        }
    					
    					String col = condition[0].trim();
    					String val = condition[1].trim();
    					if ((val.startsWith("'") && val.endsWith("'")) || (val.startsWith("\"") && val.endsWith("\""))) {
                            val = val.substring(1, val.length() - 1);
                        }
    					
    					int colIndex = headers.indexOf(col);
    					 if (colIndex == -1) {
    	                        System.err.println(Color.RED + "No rows returned due to column mismatch!" + Color.RESET);
    	                        return filteredRows;
    	                 }
    					 
    					 if(!row.get(colIndex).equals(val)) {
    						 andOkay = false;
    						 break;
    					 }
    					
    				}
    				
    				if(andOkay) {
    					orOkay = true;
    					break;
    				}
    				
    			}
    			
    			if(orOkay)
    				filteredRows.add(row);
    		}
    		
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "Error: " + e.getMessage() + Color.RESET);
    	}
    	return filteredRows;
    }
    
    
    
    
    
    //DROP TABLE tableName;
    private static void handleDropTable(String line) {
    	try {
    		
    		String[] parts = line.split("\\s+");
    		if(parts.length < 3) {
    			System.err.println(Color.RED + "Syntax Error! Expected: DROP TABLE tableName;" + Color.RESET);
    			return;
    		}
    		
    		String tableName = parts[2].trim();
    		if(TableManager.dropTable(tableName)) {
                System.out.println(Color.GREEN + "Table '" + tableName + "' dropped successfully!" + Color.RESET);
            } else {
                System.err.println(Color.RED + "Error: Table '" + tableName + "' does not exist or could not be deleted." + Color.RESET);
            }
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "Error dropping table: " + e.getMessage() + Color.RESET);
    	}
    }
    
    
    
    
    //SHOW TABLES;
    private static void handleShowTables() {
    	try {
    		
    		List<String> tables = MetaManager.showTables();
    		if(tables.isEmpty()) {
    			System.err.println(Color.RED + "No Tables Found!" + Color.RESET);
    			return;
    		}
    		
    		int colLength = 6; //"Tables" = 6
    		for(String table: tables) 
    			colLength = Math.max(colLength, table.length());
    			
    		printBorder(colLength);
    		System.out.printf("| %-" + colLength + "s |\n", "TABLES");
    		printBorder(colLength);
    		for(String tableName: tables)
    			System.out.printf("| %-" + colLength + "s |\n", tableName );
    		printBorder(colLength);
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "Error showing tables: " + e.getMessage() + Color.RESET);
    	}
    }
    private static void printBorder(int colLength) {
    	
    	System.out.print("+");
    	for(int i=0;i<colLength+2;i++)
    		System.out.print("-");
    	System.out.println("+");
    	
    }
    
    
    
    
    //DESC tableName;
    private static void handleDescribeTable(String line) {
    	try {
    		
    		String[] parts = line.split("\\s+");
    		if(parts.length<2) {
    			System.err.println(Color.RED + "Syntax Error! Expected: DESC tableName;" + Color.RESET);
    			return;
    		}
    		String tableName = parts[1];
    		MetaManager.describeTable(tableName);
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "Error describing table: " + e.getMessage() + Color.RESET);
    	}
    	
    }
    
    
    
    
    //DELETE FROM tableName WHERE colName = colValue;
    private static void handleDelete(String line) {
    	try {
    		
    		Pattern p = Pattern.compile("(?i)DELETE\\s+FROM\\s+(\\S+)\\s+WHERE\\s+(.+)");
    		Matcher m = p.matcher(line);
    		if(!m.find()) {
    			System.err.println(Color.RED + "Syntax Error, Expected: DELETE FROM tableName WHERE colName = colValue;" + Color.RESET);
    			return;
    		}
    		
    		String tableName = m.group(1).trim();
    		String where = m.group(2).trim();
    		
    		LinkedHashMap<String,String> schema = MetaManager.loadTable(tableName);
    		if(schema == null)
    			return;
    		
    		List<String> headers = new ArrayList<>(schema.keySet());
    		List<List<String>> rows = TableManager.readAllRows(tableName);
    		
    		List<List<String>> filteredRows = filterRows(rows,headers,where);
    		if(filteredRows.isEmpty()) 
    			System.err.println(Color.RED + "No rows selected for WHERE " + where + Color.RESET);
    		else {
    			rows.removeAll(filteredRows);
    			TableManager.overWriteTable(tableName, rows);
    			System.out.println(filteredRows.size() + " row(s) deleted from table: '" + tableName + "'");
    		}
    		
    	}
    	catch(Exception e) {
    		System.err.println(Color.RED + "Error deleting row(s): " + e.getMessage() + Color.RESET);
    	}
    }
    
    
    
    
    //UPDATE tableName SET col1=val1,col2=val2,... WHERE col=val;
    private static void handleUpdate(String line) {
    	try {
    		
    		Pattern p = Pattern.compile("(?i)UPDATE\\s+(\\S+)\\s+SET\\s(.+?)\\sWHERE\\s(.+)");
    		Matcher m = p.matcher(line);
    		if (!m.find()) {
                System.err.println(Color.RED + "Syntax Error, Expected: UPDATE tableName SET col1=val1, col2=val2 WHERE col=val;" + Color.RESET);
                return;
            }
    		
    		String tableName = m.group(1).trim();
    		String set = m.group(2).trim();
    		String where = m.group(3).trim();
    		
    		LinkedHashMap<String,String> schema = MetaManager.loadTable(tableName);
    		if (schema == null) {
                System.err.println(Color.RED + "Table '" + tableName + "' does not exist." + Color.RESET);
                return;
            }
    		
    		List<String> headers = new ArrayList<>(schema.keySet());
    		List<List<String>> rows = TableManager.readAllRows(tableName);
    		
    		List<List<String>> filteredRows = filterRows(rows, headers, where);
    		if (filteredRows.isEmpty()) {
    			System.err.println(Color.RED + "No rows selected for WHERE " + where + Color.RESET);
                return;
            }
    		
    		String[] setParts = set.split(",");
    		for(List<String> row: filteredRows) {
    			for(String setPart: setParts) {
    				
    				String[] setPartParts = setPart.split("=");
    				if (setPartParts.length != 2) {
                        System.err.println(Color.RED + "Invalid SET clause: " + setPart.trim() + Color.RESET);
                        return;
                    }
    				
    				String col = setPartParts[0].trim();
    				String val = setPartParts[1].trim();
    				if(val.startsWith("'") && val.endsWith("'") || val.startsWith("\"") && val.endsWith("\""))
    					val = val.substring(1,val.length()-1);
    				int colIndex = headers.indexOf(col);
    				if (colIndex == -1) {
                        System.err.println(Color.RED + "Column '" + col + "' does not exist in table '" + tableName + "'." + Color.RESET);
                        return;
                    }
  
    				for(List<String> r: rows) {
    					if(r.equals(row))
    						r.set(colIndex, val);
    				}
    			}
    		}
    		
    		TableManager.overWriteTable(tableName, rows);
    		System.out.println(Color.GREEN + filteredRows.size() + " row(s) updated successfully in table '" + tableName + "'!" + Color.RESET);
    		
    	}
    	catch (Exception e) {
            System.err.println(Color.RED + "Error when updating : " + e.getMessage() + Color.RESET);
        }
    	
    }
    
    
    
    
    
}