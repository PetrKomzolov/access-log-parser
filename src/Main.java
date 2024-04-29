import java.io.*;
import java.util.Scanner;

public class Main {
    private static final int MAX_LINE_LENGTH = 1024;

    public static void main(String[] args) {
        int fileCount = 0;
        FileReader fileReader = null;

        while (true) {
            System.out.println("Введите путь к файлу: ");

            String path = new Scanner(System.in).nextLine();
            File file = new File(path);

            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if(!fileExists || isDirectory) {
                System.out.println("Файл " + path + " не существует или путь является путем к папке");
                continue;
            }
            if(fileExists) {
                System.out.println("Путь " + path + " указан верно");
                fileCount++;
                System.out.println("Это файл номер " + fileCount);

                try {
                    fileReader = new FileReader(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BufferedReader reader =
                        new BufferedReader(fileReader);
                try {
                    String currentLine;
                    int currentLineLength;
                    int linesCount = 1, longestLineLength = 0, shortestLineLength = 0;

                    while ((currentLine = reader.readLine()) != null) {
                        currentLineLength = currentLine.length();

                        if (currentLineLength > MAX_LINE_LENGTH)
                            throw new TooLongLineException(linesCount, currentLineLength, MAX_LINE_LENGTH);

                        if (linesCount == 1)
                            shortestLineLength = longestLineLength = currentLineLength;

                        linesCount++;
                        shortestLineLength = Math.min(currentLineLength, shortestLineLength);
                        longestLineLength = Math.max(currentLineLength, longestLineLength);
                    }

                    System.out.println("Общее количество строк в файле: " + linesCount);
                    System.out.println("Длина самой длинной строки в файле: " + longestLineLength);
                    System.out.println("Длина самой короткой строки в файле: " + shortestLineLength);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
