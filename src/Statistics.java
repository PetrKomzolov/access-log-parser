import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Statistics {
    private long totalTraffic;

    private LocalDateTime minTime, maxTime;

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

    void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getResponseSize();
        if(logEntry.getDateTime().isBefore(minTime)) minTime = logEntry.getDateTime();
        if(logEntry.getDateTime().isAfter(maxTime)) maxTime = logEntry.getDateTime();
    }

    long getTrafficRate(LocalDateTime minTime, LocalDateTime maxTime) {
        long hourlyTimeDiff = minTime.until(maxTime, ChronoUnit.HOURS);
        System.out.println("Максимальная разница между запросами, в часах: " + hourlyTimeDiff);
        System.out.println("Объем всего трафика: " + totalTraffic);
        long hourlyTraffic = totalTraffic/hourlyTimeDiff;
        return hourlyTraffic;
    }
}
