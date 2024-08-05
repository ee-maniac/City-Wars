package inventory;

public class MatchInfo {
    String date;
    String result;
    String hostName;
    String rivalName;
    String rivalLevel;
    String aftermath;

    public MatchInfo(String date, String result, String hostName, String rivalName, String rivalLevel, String aftermath) {
        this.date = date;
        this.result = result;
        this.hostName = hostName;
        this.rivalName = rivalName;
        this.rivalLevel = rivalLevel;
        this.aftermath = aftermath;
    }

    public String getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public String getHostName() {
        return hostName;
    }

    public String getRivalName() {
        return rivalName;
    }

    public String getRivalLevel() {
        return rivalLevel;
    }

    public String getAftermath() {
        return aftermath;
    }
}