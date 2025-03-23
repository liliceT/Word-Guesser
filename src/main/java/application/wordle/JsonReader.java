package application.wordle;

import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JsonReader {
    private final List<String> wordList = new ArrayList<>();

    public JsonReader(String JsonFile) {
        loadWordsFromJson(JsonFile);
    }

    private void loadWordsFromJson(String jsonFileName) {
        try {
            // Load JSON file from resources
            InputStream inputStream = getClass().getResourceAsStream("/Json_Files/" + jsonFileName);

            if (inputStream != null) {
                // Parse JSON file
                JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream));
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                // Convert JSON array to List<String>
                for (JsonElement element : jsonArray) {
                    wordList.add(element.getAsString());
                }
                //For hvert element i jasonarray skal den add til wordList

            } else {
                System.err.println("JSON file not found: " + jsonFileName);
            }

        } catch (Exception e) {
            System.err.println("Error loading JSON file: " + e.getMessage());
        }
    }

    public List<String> getJsonFile() {
        return wordList;
    }

    // Får et tilfældigt ord fra den valgte Json filen
    public String getRandomWord() {
        if (wordList.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(wordList.size());
        return wordList.get(randomIndex);
    }


}
