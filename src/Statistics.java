import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime, maxTime;

    private HashSet<String> existingPages = new HashSet<>();
    private HashSet<String> nonExistingPages = new HashSet<>();

    private HashMap<String, Integer> osStatistics = new HashMap<>();
    private HashMap<String, Integer> browserStatistics = new HashMap<>();

    private int nonBotsCounter;
    private int failedRequestsCounter;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.nonBotsCounter = 0;
        this.failedRequestsCounter = 0;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public HashSet<String> getExistingPages() {
        return existingPages;
    }

    public HashSet<String> getNonExistingPages() {
        return nonExistingPages;
    }

    public HashMap<String, Double> getOsStatisticsProportion() {
        HashMap<String, Double> osStatisticsProportion = new HashMap<>();
        double osSum = 0.0;
        for(String os : osStatistics.keySet()) osSum+=osStatistics.get(os);
        for(String os : osStatistics.keySet())
            osStatisticsProportion.put(os, Double.valueOf(osStatistics.get(os))/osSum);
        return osStatisticsProportion;
    }

    public HashMap<String, Double> getBrowserStatisticsProportion() {
        HashMap<String, Double> browserStatisticsProportion = new HashMap<>();
        double browserSum = 0.0;
        for(String browser : browserStatistics.keySet()) browserSum+=browserStatistics.get(browser);
        for(String browser : browserStatistics.keySet())
            browserStatisticsProportion.put(browser, Double.valueOf(browserStatistics.get(browser))/browserSum);
        return browserStatisticsProportion;
    }

    void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getResponseSize();
        if(logEntry.getDateTime().isBefore(minTime)) minTime = logEntry.getDateTime();
        if(logEntry.getDateTime().isAfter(maxTime)) maxTime = logEntry.getDateTime();

        if((logEntry.getResponseCode() == 200) && !(existingPages.contains(logEntry.getRequestPath())))
            existingPages.add(logEntry.getRequestPath());
        if((logEntry.getResponseCode() == 404) && !(nonExistingPages.contains(logEntry.getRequestPath())))
            nonExistingPages.add(logEntry.getRequestPath());

        if(!(logEntry.getUserAgent().getOsTypeName()).equals("Other")) {
            osStatistics.merge(logEntry.getUserAgent().getOsTypeName(), 1, Integer::sum);
        }
        browserStatistics.merge(logEntry.getUserAgent().getBrowserName(), 1, Integer::sum);

        if(!(logEntry.getUserAgent().isBot())) nonBotsCounter++;
        if((logEntry.getResponseCode() >= 400) && (logEntry.getResponseCode() <= 599)) failedRequestsCounter++;
    }

    long getTrafficRate(LocalDateTime minTime, LocalDateTime maxTime) {
        long hourlyTimeDiff = minTime.until(maxTime, ChronoUnit.HOURS);
        System.out.println("Максимальная разница между запросами, в часах: " + hourlyTimeDiff);
        System.out.println("Объем всего трафика: " + totalTraffic);
        long hourlyTraffic = totalTraffic/hourlyTimeDiff;
        return hourlyTraffic;
    }

    public long getAverageSiteVisitsPerHour(List<LogEntry> entriesList) {
        LocalDateTime streamMinTime = entriesList
                .stream()
                .min(Comparator.comparing(LogEntry::getDateTime))
                .orElseThrow(NoSuchElementException::new)
                .getDateTime();

        LocalDateTime streamMaxTime = entriesList
                .stream()
                .max(Comparator.comparing(LogEntry::getDateTime))
                .orElseThrow(NoSuchElementException::new)
                .getDateTime();

        return nonBotsCounter / streamMinTime.until(streamMaxTime, ChronoUnit.HOURS);
    }

    public long getAverageFailedRequestsPerHour(List<LogEntry> entriesList) {
        LocalDateTime streamMinTime = entriesList
                .stream()
                .min(Comparator.comparing(LogEntry::getDateTime))
                .orElseThrow(NoSuchElementException::new)
                .getDateTime();

        LocalDateTime streamMaxTime = entriesList
                .stream()
                .max(Comparator.comparing(LogEntry::getDateTime))
                .orElseThrow(NoSuchElementException::new)
                .getDateTime();

        return failedRequestsCounter / streamMinTime.until(streamMaxTime, ChronoUnit.HOURS);
    }

    public long getAverageVisitsPerUser(List<LogEntry> entriesList) {
        long uniqueUserIPAddressCounter = entriesList
                .stream()
                .filter(u -> !(u.getUserAgent().isBot()))
                .map(LogEntry::getIp)
                .distinct()
                .count();

        return nonBotsCounter / uniqueUserIPAddressCounter;
    }

    public long getPeakVisitsBySecond(List<LogEntry> entriesList) {
        Map<LocalDateTime, Long> allVisitsBySecond = entriesList
                .stream()
                .filter(u -> !(u.getUserAgent().isBot()))
                .collect(groupingBy(LogEntry::getDateTime, counting()));

        return allVisitsBySecond
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getValue();
    }

    public HashSet<String> getReferringDomains(List<LogEntry> entriesList) {
        return entriesList
                .stream()
                .map(LogEntry::getRefererPath)
                .filter(Pattern.compile("^https?:\\/\\/(.*?)(?:[\\/?&#]|$)").asPredicate())
                .map(e -> e.replaceAll("http(s)?://|www\\.|/.*", ""))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public long getMaxVisitsByUser(List<LogEntry> entriesList) {
        Map<String, Long> uniqueUsersVisitsCounter = entriesList
                .stream()
                .filter(u -> !(u.getUserAgent().isBot()))
                .collect(groupingBy(LogEntry::getIp, counting()));

        return uniqueUsersVisitsCounter
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getValue();
    }
}
