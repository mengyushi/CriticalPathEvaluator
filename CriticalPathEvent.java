import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CriticalPathEvent {
    private boolean isRemote;
    private final String action;
    private Duration total_time;
    private String remote_metrics;
    private Duration remote_time;

    public CriticalPathEvent(
        String act,
        boolean remote,
        Duration total_t,
        String remote_m,
        Duration remote_t        
    ) {
        isRemote = remote;
        action = act;
        total_time = total_t;
        remote_metrics = remote_m;
        remote_time = remote_t;
    }

    public static CriticalPathEvent createFromString(String str) {
        boolean remote = false;
        Float t = 0.0f;
        String act = "";
        float pct = 0.0f;
        String remote_metrics = "";

        Matcher remote_time_matcher = Pattern.compile("  ([\\d\\.\\d]*)s, Remote \\((.*)% of the time\\).*\\[(.*)\\] (.*)").matcher(str);
        
        if(remote_time_matcher.matches()){
            remote = true;
            t = Float.parseFloat(remote_time_matcher.group(1));
            pct = Float.parseFloat(remote_time_matcher.group(2));
            remote_metrics = remote_time_matcher.group(3);
            act = remote_time_matcher.group(4);
        } else {
            Matcher local_time_matcher = Pattern.compile("  ([\\d\\.\\d]*)s (.*)").matcher(str);
            if(local_time_matcher.matches()){
                t = Float.parseFloat(local_time_matcher.group(1));
                act = local_time_matcher.group(2);
            } else {
                System.err.println("Pattern Matching Error");
            }
        }

        Duration total_t = Duration.ofNanos((long)(t*1000000000));
        Duration remote_t = Duration.ofNanos((long)(t*pct*10000000));

        return new CriticalPathEvent(act, remote, total_t, remote_metrics, remote_t);
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

    public void test() {
        System.out.println("ACTION:");
        System.out.println(this.action);
        System.out.println("Is Remote?");
        System.out.println(this.isRemote);
        System.out.println("Total Time:");
        System.out.println(this.total_time.toSeconds());
        System.out.println();
    }
      
}