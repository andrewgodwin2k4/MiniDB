package com.minidb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class MetaManagerTest {

    private static final String testTable = "junitTable";

    @BeforeAll
    static void setup() {
        StorageManager.initializeStorage();
    }

    @AfterEach
    void clear() {
        File metaFile = new File("data/catalog/" + testTable + ".meta");
        if (metaFile.exists()) 
            metaFile.delete();
    }

    @Test
    void testCreateTableSuccess() {
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        columns.put("id", "INT");
        columns.put("name", "TEXT");

        boolean isCreated = MetaManager.createTable(testTable, columns);
        assertTrue(isCreated, "Table should be created successfully");

        File metaFile = new File("data/catalog/" + testTable + ".meta");
        assertTrue(metaFile.exists(), "The .meta file should be physically created");
    }

    @Test
    void testCreateTableAlreadyExists() {
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        columns.put("id", "INT");
        MetaManager.createTable(testTable, columns);

        boolean isCreatedSecondTime = MetaManager.createTable(testTable, columns);
        assertFalse(isCreatedSecondTime, "Should not be able to create a table that already exists");
    }
}