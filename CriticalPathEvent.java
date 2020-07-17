import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CriticalPathEvent {
    private boolean isRemote;
    private final String action;
    private Float totalTime;
    private Float remoteTime;
    private RemoteMetrics remoteMetrics;

    public CriticalPathEvent(
        String act,
        boolean rm,
        Float total,
        String metrics,
        Float remote        
    ) {
        isRemote = rm;
        action = act;
        totalTime = total;
        if (isRemote) {
            remoteMetrics = RemoteMetrics.createFromString(totalTime, metrics);
        }
    }

    public static CriticalPathEvent createFromString(String str) {
        boolean remote = false;
        Float total = 0.0f;
        String act = "";
        float pct = 0.0f;
        String remoteMetricsString = "";

        Matcher remoteTimeMatcher = Pattern.compile(".* ([\\d\\.\\d]*)s, Remote \\((.*)% of the time\\).*\\[(.*)\\] (.*)").matcher(str);
        
        if (remoteTimeMatcher.matches()) {
            remote = true;
            total = Float.parseFloat(remoteTimeMatcher.group(1));
            pct = Float.parseFloat(remoteTimeMatcher.group(2));
            remoteMetricsString = remoteTimeMatcher.group(3);
            act = remoteTimeMatcher.group(4);
        } else {
            Matcher local_time_matcher = Pattern.compile(".* ([\\d\\.\\d]*)s (.*)").matcher(str);
            if(local_time_matcher.matches()){
                total = Float.parseFloat(local_time_matcher.group(1));
                act = local_time_matcher.group(2);
            } else {
                System.out.println(str);
                System.err.println("Pattern Matching Error");
            }
        }

        return new CriticalPathEvent(act, remote, total, remoteMetricsString, total*pct/100);
    }

    public static List<CriticalPathEvent> getCriticalEvents(String str) {
        List<CriticalPathEvent> events = new ArrayList<>();
        String split[]= str.split("\n");
        for (int i = 1; i < split.length; i++) {
            events.add(createFromString(split[i]));
        }        
        return events;
    }

    public boolean isRemote() {
        return this.isRemote;
    }

    public Float totalTime() {
        return this.totalTime;
    }

    public RemoteMetrics remoteMetrics() {
        return this.remoteMetrics;
    }

    public void print() {
        System.out.println("ACTION:");
        System.out.println(this.action);
        System.out.println("Is Remote?");
        System.out.println(this.isRemote);
        System.out.println("Total Time:");
        System.out.println(this.totalTime);
        System.out.println();
    }
      
}