package pacman.entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and managing level files for the Pacman game.
 * This class provides functionality to load level data from files and manage available levels.
 */
public class LevelReader {

    /** Directory path where level files are stored */
    private static final String LEVELS_DIRECTORY = "src/main/resources/pacman/levels";
    
    /**
     * Gets a list of all available level files.
     * @return List of level file names
     */
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

    /**
     * Reads level data from a specified file.
     * @param levelFileName Name of the level file to read
     * @return a char matrix containing the level data
     */
    public char[][] readLevelData(String levelFileName) {
        File levelFile = new File(LEVELS_DIRECTORY, levelFileName);
        List<char[]> levelLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(levelFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                levelLines.add(line.toCharArray());
            }
        } catch (IOException e) {
            System.out.println("Error reading level file: " + e.getMessage());
        }

        return levelLines.toArray(new char[0][]);
    }

}