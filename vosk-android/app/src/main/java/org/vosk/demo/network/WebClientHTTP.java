package org.vosk.demo.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebClientHTTP {


    public static void sendGetInBackground(String url) {
        // Création d'un nouveau thread pour l'appel réseau
        new Thread(() -> {
            // Appel de la méthode sendGet dans le thread
            try {
                //sendGet(url);
                get(url);
            } catch (IOException e) {
                System.out.println(e);
            }
        }).start();
    }


    public static String get(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        if (inputStream == null) {
            // Nothing to do.
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            return null;
        }

        String stringResult = buffer.toString();
        return stringResult;
    }
}
