import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime, maxTime;

    private HashSet<String> existingPages = new HashSet<>();
    private HashSet<String> nonExistingPages = new HashSet<>();

    private HashMap<String, Integer> osStatistics = new HashMap<>();
    private HashMap<String, Integer> browserStatistics = new HashMap<>();

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
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
    }

    long getTrafficRate(LocalDateTime minTime, LocalDateTime maxTime) {
        long hourlyTimeDiff = minTime.until(maxTime, ChronoUnit.HOURS);
        System.out.println("Максимальная разница между запросами, в часах: " + hourlyTimeDiff);
        System.out.println("Объем всего трафика: " + totalTraffic);
        long hourlyTraffic = totalTraffic/hourlyTimeDiff;
        return hourlyTraffic;
    }
}
