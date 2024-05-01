import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final int MAX_LINE_LENGTH = 1024;

    public static void main(String[] args) {
        int fileCount = 0;
        FileReader fileReader = null;

        String lineSplitRegex =
                "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"
                        + "[- ]{3,5}"
                        + "\\[([0-9A-Za-z: +].*)\\] "
                        + "\"([A-Z]{3,}.*)\" "
                        + "([0-9]{3} )"
                        + "([0-9]+) "
                        + "\"(.*)\" "
                        + "\"(.*)\"";
        Pattern lineSplitPattern = Pattern.compile(lineSplitRegex);
        Matcher lineSplitMatcher;

        while (true) {
            System.out.println("Введите путь к файлу: ");

            String path = new Scanner(System.in).nextLine();
            File file = new File(path);

            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists || isDirectory) {
                System.out.println("Файл " + path + " не существует или путь является путем к папке");
                continue;
            }
            if (fileExists) {
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
                    int linesCount = 1;

                    int yandexBotCount = 0, googleBotCount = 0;

                    HashMap<Integer, String> ipsMap = new HashMap<>(); //IP-адрес клиента, который сделал запрос к серверу
                    HashMap<Integer, String> dateTimeMap = new HashMap<>(); //Дата и время запроса в квадратных скобках
                    HashMap<Integer, String> requestPathMap = new HashMap<>(); //Метод запроса и путь, по которому сделан запрос
                    HashMap<Integer, String> responseCodeMap = new HashMap<>(); //Код HTTP-ответа
                    HashMap<Integer, String> responseSizeMap = new HashMap<>(); //Размер отданных данных в байтах
                    HashMap<Integer, String> refererPathMap = new HashMap<>(); //Путь к странице, с которой перешли на текущую страницу, — referer
                    HashMap<Integer, String> userAgentMap = new HashMap<>();//User-Agent — информация о браузере или другом клиенте, который выполнил запрос

                    Statistics statistics = new Statistics();

                    while ((currentLine = reader.readLine()) != null) {
                        currentLineLength = currentLine.length();

                        if (currentLineLength > MAX_LINE_LENGTH)
                            throw new TooLongLineException(linesCount, currentLineLength, MAX_LINE_LENGTH);

                        lineSplitMatcher = lineSplitPattern.matcher(currentLine);

                        if (lineSplitMatcher.find()) {
                            ipsMap.put(linesCount, lineSplitMatcher.group(1));
                            dateTimeMap.put(linesCount, lineSplitMatcher.group(2));
                            requestPathMap.put(linesCount, lineSplitMatcher.group(3));
                            responseCodeMap.put(linesCount, lineSplitMatcher.group(4));
                            responseSizeMap.put(linesCount, lineSplitMatcher.group(5));
                            refererPathMap.put(linesCount, lineSplitMatcher.group(6));
                            userAgentMap.put(linesCount, lineSplitMatcher.group(7));
                        }

                        linesCount++;

                        LogEntry logEntry = new LogEntry(currentLine);
                        statistics.addEntry(logEntry);
                    }

                    //Поиск GoogleBot или YandexBot внутри User-Agent (в первых скобках)
                    for (Map.Entry<Integer, String> entry : userAgentMap.entrySet()) {
                        if (!(entry.getValue().matches("-"))) {
                            Pattern firstBracketsPattern = Pattern.compile("\\((.*?)\\)");
                            Matcher firstBracketsMatcher = firstBracketsPattern.matcher(entry.getValue());
                            if (firstBracketsMatcher.find()) {
                                String firstBrackets = firstBracketsMatcher.group(1);
                                String[] parts = firstBrackets.split(";");
                                for (int i = 0; i < parts.length; i++) {
                                    parts[i] = parts[i].trim();
                                }
                                if (parts.length >= 2) {
                                    String fragment = parts[1];
                                    String[] splittedFragment = fragment.split("/");
                                    if (splittedFragment[0].equalsIgnoreCase("YandexBot")) yandexBotCount++;
                                    if (splittedFragment[0].equalsIgnoreCase("Googlebot")) googleBotCount++;
                                }
                            }
                        }
                    }
                    System.out.println("Общее количество строк в файле: " + linesCount);

                    System.out.println("Количество запросов от YandexBot: " + yandexBotCount);
                    System.out.println("Количество запросов от Googlebot: " + googleBotCount);

                    System.out.println("Доля запросов от YandexBot от общего количества: "
                            + (double) yandexBotCount * 100 / linesCount);
                    System.out.println("Доля запросов от Googlebot от общего количества: "
                            + (double) googleBotCount * 100 / linesCount);

                    System.out.println("Время самого раннего запроса: " + statistics.getMinTime());
                    System.out.println("Время самого позднего запроса: " + statistics.getMaxTime());
                    System.out.println("Объем часового трафика: " + statistics.getTrafficRate(statistics.getMinTime(), statistics.getMaxTime()));

                    System.out.println("Список всех существующих страниц: " + statistics.getExistingPages());
                    System.out.println("Пропорция по операционным системам: " + statistics.getOsStatisticsProportion());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
