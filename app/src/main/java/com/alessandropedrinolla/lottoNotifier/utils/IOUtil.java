package com.alessandropedrinolla.lottoNotifier.utils;

import android.content.Context;
import com.google.gson.Gson;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.alessandropedrinolla.lottoNotifier.models.ScrapeData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class IOUtil {
    private final String SCRAPE_DATA_FOLDER = "scrape_data";
    private final String GAME_FOLDER = "games";

    private static Gson mGson = new Gson();
    private static String mFilePath;

    public IOUtil(Context context) {
        mFilePath = context.getFilesDir().getPath();
    }

    public void persistGames(ArrayList<Game> games) {
        Gson gson = new Gson();

        String filename = String.format("%s/%s/%s.json", mFilePath, GAME_FOLDER, UUID.randomUUID().toString());

        for (Game g : games) {
            try {
                FileWriter fileWriter = new FileWriter(filename);
                fileWriter.write(gson.toJson(g));
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                return;
            }
        }
    }

    public void persistScrapeData(ScrapeData scrapeData) {
        String date = scrapeData.getDate();
        String scrapeDataPath = String.format("%s/%s/%s", mFilePath, SCRAPE_DATA_FOLDER, date);

        File scrapeDataFolder = new File(scrapeDataPath);

        if (!scrapeDataFolder.exists()) {
            scrapeDataFolder.mkdir();
        }

        Set<Integer> keys = scrapeData.getExtractions().keySet();

        for (Integer key : keys) {
            File scrapeDataFile = new File(String.format("%s/%s.json", scrapeDataFolder, key));

            try (FileWriter fw = new FileWriter(scrapeDataFile)) {
                fw.write(mGson.toJson(scrapeData.getExtractions().get(key)));
                fw.flush();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                return;
            }
        }
    }

    public void loadGames(ArrayList<Game> games) {
        File[] gameFiles = new File(String.format("%s/%s", mFilePath, GAME_FOLDER)).listFiles();

        games.clear();

        for (File f : gameFiles) {
            if (f.isFile()) {
                try {
                    FileReader fileReader = new FileReader(f);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        //stringBuilder.append("\n");
                    }
                    fileReader.close();

                    Game game = mGson.fromJson(stringBuilder.toString(), Game.class);
                    games.add(game);
                } catch (IOException fnfEx) {
                    fnfEx.printStackTrace();
                }
            }
        }
    }
}
