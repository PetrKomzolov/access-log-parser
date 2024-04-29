public class TooLongLineException extends Exception {
    public TooLongLineException(int linesCount, int currentLineLength, int maxLineLength) {
        super("Количество символов в строке " + linesCount + " (" + currentLineLength + ") превышает допустимое (" + maxLineLength + ')');
    }
}
