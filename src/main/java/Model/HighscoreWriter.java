package Model;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class HighscoreWriter {

    private String filePath;
    private FileWriter fileWriter;
    private FileReader fileReader;
    private BufferedReader reader;
    private PrintWriter writer;
    private File file;

    public HighscoreWriter() {
        filePath = "src/main/resources/Files/Highscore.txt";
        file = new File(filePath);
        try {
            fileReader = new FileReader(file);
            fileWriter = new FileWriter(file, true);
            writer = new PrintWriter(fileWriter);
        } catch (IOException e) {
            System.out.println("HighscoreSaver Error!");
        }
    }

    public int getHighscore() {
        try{
            reader = new BufferedReader(fileReader);
            List<String> highscores = reader.lines().collect(Collectors.toList());
            return Integer.parseInt(highscores.get(highscores.size() - 1));
        } catch (Exception e) {
            System.out.println("Couldn't read the Highscore");
        }
        return -1;
    }

    public void setHighscore(int highscore) {

        //write new Highscore
        writer.write(highscore + "\n");
        writer.close();
}
}
