import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoteMetrics {

    private List<Float> remoteMetrics;
    private String summary;
    HashMap<String, Integer> index;
    
    public RemoteMetrics(Float total, List<Float> times, String str){
        remoteMetrics = new ArrayList<>();
        remoteMetrics.add(total);
        for (int i = 0; i < 10; i++) {
            remoteMetrics.add(times.get(i));
        }

        index = new HashMap<String, Integer>();
        index.put("total", 0);
        index.put("parse", 1);
        index.put("queue", 2);
        index.put("network", 3);
        index.put("upload", 4);
        index.put("setup", 5);
        index.put("process", 6);
        index.put("fetch", 7);
        index.put("retry", 8);
        index.put("processOutputs", 9);
        index.put("other", 10);

        summary = str;
    }

    public static RemoteMetrics createFromString(Float remoteTotalTime, String str) {
        String[] keys = {"parse", "queue", "network", "upload", "setup", "process", "fetch", "retry", "processOutputs", "other"};
        String pattern = "";
        List<Float> times = new ArrayList<>();;
        for (String key:keys) {
            pattern += key + ": ([\\d\\.\\d]*)%.*";
        }

        Matcher remoteTimeMatcher = Pattern.compile(pattern).matcher(str);
        if(remoteTimeMatcher.matches()){
            for (int i = 0; i < keys.length; i++){
                float pct = Float.parseFloat(remoteTimeMatcher.group(i+1));
                times.add(remoteTotalTime * pct / 100.0f);
            }
        } else {
            System.out.println("remote metrics matching error:");
            System.out.println(str);
        }
        return new RemoteMetrics(remoteTotalTime, times, str);
    }

    public Float getTime(String key) {
        return remoteMetrics.get(index.get(key));
    }

    public void printSummary() {
        System.out.println(this.summary);
    }

    public static void main(String[] args) throws Exception  
    {
        Float t = 10000.0f;
        String str = "parse: 0.06%, queue: 10.20%, network: 6.54%, upload: 0.00%, setup: 8.73%, process: 73.72%, fetch: 0.00%, retry: 0.00%, processOutputs: 0.61%, other: 0.04%, input files: 23987, input bytes: 768787483, memory bytes: 0";
        
        RemoteMetrics rMetrics = createFromString(t, str);
        System.out.println(rMetrics.getTime("parse"));
        System.out.println(rMetrics.getTime("other"));

    }    
}