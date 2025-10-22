package com.dsa.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.io.IOException;

public class MongoJsonImporter {

    private final MongoDatabase database;

    public MongoJsonImporter(MongoDatabase db) {
        this.database = db;
    }

    public void importJsonFile(String collectionName, String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(filePath));

            MongoCollection<Document> collection = database.getCollection(collectionName);

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    Document doc = Document.parse(node.toString());
                    collection.insertOne(doc);
                }
            } else {
                Document doc = Document.parse(rootNode.toString());
                collection.insertOne(doc);
            }

            System.out.println("✅ Imported JSON file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
