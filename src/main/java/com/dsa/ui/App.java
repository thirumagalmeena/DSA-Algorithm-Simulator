package com.dsa.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mongodb.client.MongoDatabase;

import java.io.File;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Connecting to MongoDB Atlas...");
        VBox root = new VBox(label);
        root.setSpacing(10);
        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.setTitle("DSA Learning Kit + MongoDB");
        stage.show();

        // Run DB connection & JSON import in a background thread
        new Thread(() -> {
            try {
                // Connect to MongoDB Atlas
                MongoDatabase db = MongoConnector.connect();
                Platform.runLater(() -> label.setText("✅ Connected to MongoDB Atlas!"));

                // Create importer
                MongoJsonImporter importer = new MongoJsonImporter(db);

                // Import all JSON files from the folder
                File folder = new File("src/main/resources");
                if (folder.exists() && folder.isDirectory()) {
                    for (File file : folder.listFiles((d, name) -> name.endsWith(".json"))) {
                        importer.importJsonFile("algorithms", file.getPath());
                    }
                }

                Platform.runLater(() -> label.setText("✅ JSON import complete!"));

            } catch (Exception e) {
                Platform.runLater(() -> label.setText("❌ Connection or import failed: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
