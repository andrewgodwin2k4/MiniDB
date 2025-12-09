package com.minidb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableManagerTest {

    private static final String testTable = "junitTable";

    @BeforeEach
    void setup() {
        StorageManager.initializeStorage();
        
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        columns.put("id", "INT");
        columns.put("username", "TEXT");
        
        MetaManager.createTable(testTable, columns);
        TableManager.createTableFile(testTable);
    }

    @AfterEach
    void clear() {
        new File("data/catalog/" + testTable + ".meta").delete();
        new File("data/tables/" + testTable + ".tbl").delete();
    }

    @Test
    void testInsertValidRow() {
        List<String> values = new ArrayList<>();
        values.add("1");
        values.add("'Andrew'"); 

        boolean success = TableManager.insertRow(testTable, values);
        assertTrue(success, "Row insertion should succeed");
    }

    @Test
    void testInsertInvalidRow() {
        List<String> values = new ArrayList<>();
        values.add("NOT_A_NUMBER"); 
        values.add("'Andrew'");

        boolean success = TableManager.insertRow(testTable, values);
        assertFalse(success, "Row insertion should fail because 'id' expects INT");
    }
    
    @Test
    void testReadRows() {
        List<String> values = new ArrayList<>();
        values.add("100");
        values.add("'TestUser'");
        TableManager.insertRow(testTable, values);

        List<List<String>> rows = TableManager.readAllRows(testTable);

        assertFalse(rows.isEmpty(), "Table should not be empty");
        assertEquals("100", rows.get(0).get(0));
        assertEquals("TestUser", rows.get(0).get(1)); 
    }

    @Test
    void testUpdateRow() {
        List<String> user1 = new ArrayList<>(); user1.add("1"); user1.add("'Andrew'");
        TableManager.insertRow(testTable, user1);

        List<List<String>> rows = TableManager.readAllRows(testTable);
        rows.get(0).set(1, "Andy"); 
        TableManager.overWriteTable(testTable, rows);

        List<List<String>> updatedRows = TableManager.readAllRows(testTable);
        assertEquals("Andy", updatedRows.get(0).get(1), "Name should be updated to Andy");
    }

    @Test
    void testDeleteRow() {
        List<String> user1 = new ArrayList<>(); user1.add("1"); user1.add("'Andrew'");
        List<String> user2 = new ArrayList<>(); user2.add("2"); user2.add("'Selena'");
        
        TableManager.insertRow(testTable, user1);
        TableManager.insertRow(testTable, user2);
        
        List<List<String>> currentRows = TableManager.readAllRows(testTable);
        currentRows.remove(0); 
        TableManager.overWriteTable(testTable, currentRows);

        List<List<String>> rowsAfterDelete = TableManager.readAllRows(testTable);
        assertEquals(1, rowsAfterDelete.size(), "Should have 1 row left");
        assertEquals("2", rowsAfterDelete.get(0).get(0), "Remaining row should be ID 2");
    }
}
