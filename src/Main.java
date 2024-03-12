import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int fileCount = 0;

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
            }
        }
    }
}
