public class UserAgent {
    private final String browserName;
    private final String osTypeName;

    public UserAgent(String userAgent) {
        if (userAgent.contains("Windows")) this.osTypeName = "Windows";
        else if (userAgent.contains("Mac OS")) this.osTypeName = "MacOS";
        else if (userAgent.contains("Linux")) this.osTypeName = "Linux";
        else this.osTypeName = "Other";

        if (userAgent.contains("Edg/"))
            this.browserName = "Edge";
        else if (userAgent.contains("Firefox/")
                && !(userAgent.contains("Seamonkey/")))
            this.browserName = "Firefox";
        else if (userAgent.contains("KHTML, like Gecko")
                && userAgent.contains("Chrome/")
                && !(userAgent.contains("Chromium/"))
                && !(userAgent.contains("Edg")))
            this.browserName = "Chrome";
        else if (userAgent.contains("OPR/")
                || userAgent.contains("Opera/"))
            this.browserName = "Opera";
        else
            this.browserName = "Other";
    }

    public String getBrowserName() {
        return browserName;
    }

    public String getOsTypeName() {
        return osTypeName;
    }

    @Override
    public String toString() {
        return "UserAgent{" +
                "browserName='" + browserName + '\'' +
                ", osTypeName='" + osTypeName + '\'' +
                '}';
    }
}
