package pacman.entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelReader {

    private static final String LEVELS_DIRECTORY = "src/main/resources/pacman/levels";
    private static final String[] GHOST_IMAGES = {
        "/pacman/images/ghosts/red.png",
        "/pacman/images/ghosts/blue.png",
        "/pacman/images/ghosts/pink.png",
        "/pacman/images/ghosts/orange.png"
    };

    public List<String> getAvailableLevels() {
        List<String> levels = new ArrayList<>();
        File directory = new File(LEVELS_DIRECTORY);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    levels.add(file.getName());
                }
            }
        }
        return levels;
    }

    public char[][] readLevelData(String levelFileName) {
        File levelFile = new File(LEVELS_DIRECTORY, levelFileName);
        List<char[]> levelLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(levelFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                levelLines.add(line.toCharArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return levelLines.toArray(new char[0][]);
    }

    public String getRandomGhostImage() {
        Random random = new Random();
        int index = random.nextInt(GHOST_IMAGES.length);
        return GHOST_IMAGES[index];
    }
}