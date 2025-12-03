# MiniDB 

MiniDB is a lightweight, file-based relational database management system written in Java.  
It supports basic SQL-like operations including creating and dropping tables, inserting rows, selecting data, updating and deleting rows, and listing table metadata.

This project demonstrates core database concepts such as storage management, schema handling, and command parsing in a console-based interface.

---

## Features 

- **CREATE TABLE** – Define a new table with column names and types (INT or TEXT)  
- **INSERT INTO** – Insert rows into a table with type validation  
- **SELECT** – Retrieve data with support for specific columns and multi-condition WHERE clause (AND & OR)  
- **UPDATE** – Update specific rows based on WHERE conditions  
- **DELETE FROM** – Delete rows based on WHERE conditions  
- **DROP TABLE** – Remove a table completely  
- **SHOW TABLES** – List all existing tables  
- **DESC tableName** – Display column metadata for a table  
- **Terminal UI** – Color-coded, user-friendly REPL interface using ANSI colors  

---

## Future Improvements [Advanced] 

- Subqueries  
- Joins  
- Filtering using operators  
- Aggregate functions  
- Sorting and grouping  

---

## How to Run 

1. Make sure Docker is installed and running on your system.
2. Pull the MiniDB Docker image from Docker Hub:
   docker pull andrew2k4/minidb:latest  
3. Run MiniDB in an interactive terminal:
   docker run -it --rm --name minidb andrew2k4/minidb
4. MiniDB will open successfully. Try out some queries! 

