package com.dsa.ui;

import javafx.scene.Node;
import javafx.scene.web.WebView;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MermaidRenderer {

    public static String extractMermaidCode(String content) {
        // Extract mermaid code from ```mermaid ... ``` blocks
        Pattern pattern = Pattern.compile("```mermaid\\s*(.*?)\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    
    public static Node createMermaidWebView(String mermaidCode) {
        WebView webView = new WebView();
        webView.setPrefSize(600, 400);
        webView.setMinHeight(300);
        
        String html = createMermaidHTML(mermaidCode);
        webView.getEngine().loadContent(html);
        
        return webView;
    }
    
    private static String createMermaidHTML(String mermaidCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/mermaid@9.3.0/dist/mermaid.min.js"></script>
                <style>
                    body { 
                        margin: 0; 
                        padding: 10px; 
                        font-family: Arial, sans-serif;
                    }
                    .mermaid { 
                        text-align: center;
                    }
                    .error {
                        color: red;
                        padding: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="mermaid">
                    %s
                </div>
                <script>
                    try {
                        mermaid.initialize({ 
                            startOnLoad: true, 
                            theme: 'default',
                            flowchart: {
                                useMaxWidth: true,
                                htmlLabels: true
                            }
                        });
                    } catch (error) {
                        document.body.innerHTML = '<div class="error">Failed to render flowchart: ' + error.message + '</div>';
                    }
                </script>
            </body>
            </html>
            """.formatted(mermaidCode);
    }
}