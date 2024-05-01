import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ip;
    private final LocalDateTime dateTime;
    private final EHTTPMethod httpMethod;
    private final String requestPath;
    private final int responseCode;
    private final int responseSize;
    private final String refererPath;
    private final UserAgent userAgent;

    private final String lineSplitRegex =
            "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"
                    + "[- ]{3,5}"
                    + "\\[([0-9A-Za-z: +].*)\\] "
                    + "\"([A-Z]{3,}) "
                    + "(/.*) "
                    + "([0-9]{3}) "
                    + "([0-9]+) "
                    + "\"(.*)\" "
                    + "\"(.*)\"";
    private final Pattern lineSplitPattern = Pattern.compile(lineSplitRegex);
    private final Matcher lineSplitMatcher;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z"); //25/Sep/2022:06:25:06 +0300

    public LogEntry(String currentLine) {
        this.lineSplitMatcher = lineSplitPattern.matcher(currentLine);
        lineSplitMatcher.find();
        this.ip = lineSplitMatcher.group(1);
        this.dateTime = LocalDateTime.parse(lineSplitMatcher.group(2), formatter);
        this.httpMethod = EHTTPMethod.valueOf(lineSplitMatcher.group(3));
        this.requestPath = lineSplitMatcher.group(4);
        this.responseCode = Integer.parseInt(lineSplitMatcher.group(5));
        this.responseSize = Integer.parseInt(lineSplitMatcher.group(6));
        this.refererPath = lineSplitMatcher.group(7);
        this.userAgent = new UserAgent(lineSplitMatcher.group(8));
    }

    public String getIp() {
        return ip;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public EHTTPMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public String getRefererPath() {
        return refererPath;
    }
}
