package fr.vannes.notes.openAI;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Classe pour effectuer une requête à l'API OpenAI et extraire la réponse
 */
public class OpenAIRequest {

    public interface ChatRequestListener {
        void onRequestComplete(String response);
    }

    public static void performAsyncChatRequest(String keywords, ChatRequestListener listener) {
        new ChatRequestTask(listener).execute(keywords);
    }

    private static class ChatRequestTask extends AsyncTask<String, Void, String> {
        private final ChatRequestListener listener;

        public ChatRequestTask(ChatRequestListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... keywords) {
            try {
                return performChatRequest(keywords[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (listener != null) {
                listener.onRequestComplete(response);
            }
        }
    }

    /**
     * Méthode pour effectuer une requête à l'API OpenAI et extraire la réponse
     */
    public static String performChatRequest(String keywords) throws IOException {

        // Définir les paramètres de la requête
        String apiKey = "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";
        String endpoint = "https://api.openai.com/v1/chat/completions";

        // Ouvrir la connexion HttpURLConnection
        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        // Construire le corps de la requête
        String requestBody = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"Vous êtes mon assistant.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"Génére moi un suggestion de note avec cette phrase: " + keywords + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Définir les paramètres de la requête
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Authorization", "Bearer " + apiKey);

        // Envoyer la requête
        urlConnection.setDoOutput(true);
        urlConnection.getOutputStream().write(requestBody.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        StringBuilder responseContent = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }

        // Parser la réponse JSON avec Gson
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonResponse = jsonParser.parse(responseContent.toString()).getAsJsonObject();

        // Extraire le contenu de la première choice
        JsonArray choices = jsonResponse.getAsJsonArray("choices");
        if (!choices.isEmpty()) {
            JsonElement firstChoice = choices.get(0);
            String assistantResponse = firstChoice.getAsJsonObject()
                    .getAsJsonObject("message")
                    .getAsJsonPrimitive("content")
                    .getAsString();

            // Retourner le contenu extrait
            return assistantResponse;
        }

        // Retourner une chaîne vide si la réponse ne peut pas être extraite
        return "";
    }
}
