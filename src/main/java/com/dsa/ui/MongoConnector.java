package com.dsa.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;
import io.github.cdimascio.dotenv.Dotenv;

public class MongoConnector {
    private static Dotenv dotenv;
    
    // Static initializer to load .env file
    static {
        dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }
    
    public static MongoDatabase connect() {
        try {
            // Get credentials from .env file
            String uri = dotenv.get("MONGODB_URI");
            String databaseName = dotenv.get("DATABASE_NAME");
            
            // Validate that we got the values
            if (uri == null || uri.isEmpty()) {
                throw new IllegalStateException("MONGODB_URI not found in .env file");
            }
            if (databaseName == null || databaseName.isEmpty()) {
                throw new IllegalStateException("DATABASE_NAME not found in .env file");
            }
            
            // Create connection and get database
            MongoClient client = MongoClients.create(uri);
            MongoDatabase db = client.getDatabase(databaseName);
            
            // Test the connection
            db.listCollectionNames().first(); // This will throw if connection fails
            
            System.out.println("✅ Connected to MongoDB Atlas!");
            return db;
            
        } catch (MongoException e) {
            System.err.println("❌ MongoDB connection failed: " + e.getMessage());
            throw new RuntimeException("Failed to connect to MongoDB", e);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw new RuntimeException("Configuration error", e);
        }
    }
}
