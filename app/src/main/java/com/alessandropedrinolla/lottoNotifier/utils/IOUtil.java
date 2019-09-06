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
        mFilePath = context.getFilesDir().getParentFile().getPath();
    }

    public void persistGames(ArrayList<Game> games) {
        for (Game g : games) {
            String randomFileName = UUID.randomUUID().toString();
            g.setUUID(randomFileName);

            persistGame(g);
        }
    }

    public void persistGame(Game game) {
        File gameFolder = new File(String.format("%s/%s", mFilePath, GAME_FOLDER));

        if(!gameFolder.exists())
            gameFolder.mkdir();

        if(game.getUUID() == null) {
            String randomFileName = UUID.randomUUID().toString();
            game.setUUID(randomFileName);
        }

        try {
            String filename = String.format("%s/%s/%s.json", mFilePath, GAME_FOLDER, game.getUUID());
            FileWriter fileWriter = new FileWriter(filename);
            fileWriter.write(mGson.toJson(game));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void persistScrapeData(ScrapeData scrapeData) {
        String date = scrapeData.getDate();
        String scrapeDataDatePath = String.format("%s/%s/%s", mFilePath, SCRAPE_DATA_FOLDER, date);

        File scrapeDataDateFolder = new File(scrapeDataDatePath);

        if (!scrapeDataDateFolder.exists()) {
            scrapeDataDateFolder.mkdirs();
        }

        Set<Integer> keys = scrapeData.getExtractions().keySet();

        for (Integer key : keys) {
            File scrapeDataFile = new File(String.format("%s/%s.json", scrapeDataDateFolder, key));

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

        if (gameFiles != null) {
            for (File f : gameFiles) {
                if (f.isFile()) {
                    try {
                        FileReader fileReader = new FileReader(f);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
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

    public void deleteGame(String gameUUID) {
        String filename = String.format("%s/%s/%s.json", mFilePath, GAME_FOLDER, gameUUID);
        File gameFile = new File(filename);

        if (gameFile.exists()) {
            gameFile.delete();
        }
    }
}
