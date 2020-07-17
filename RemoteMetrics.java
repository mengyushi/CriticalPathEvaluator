import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

public class RemoteMetrics {
    private Float totalTime;
    private Float parseTime;
    private Float queueTime;
    private Float networkTime;
    private Float uploadTime;
    private Float setupTime;
    private Float processTime;
    private Float fetchTime;
    private Float retryTime;
    private Float processOutputsTime;
    private Float otherTime;
    
    public RemoteMetrics(Float total_t, List<Float> times){
        totalTime = total_t;
        parseTime = times.get(0);
        queueTime = times.get(1);
        networkTime = times.get(2);
        uploadTime = times.get(3);
        setupTime = times.get(4);
        processTime = times.get(5);
        fetchTime = times.get(6);
        retryTime = times.get(7);
        processOutputsTime = times.get(8);
        otherTime = times.get(9);
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
        return new RemoteMetrics(remoteTotalTime, times);
    }

    public Float parseTime() {
        return this.parseTime;
    }

    public void test() {
        System.out.println("totalTime:");
        System.out.println(totalTime);
        System.out.println("parseTime:");
        System.out.println(parseTime);
    }

    public static void main(String[] args) throws Exception  
    {
        Float t = 10000.0f;
        String str = "parse: 0.06%, queue: 10.20%, network: 6.54%, upload: 0.00%, setup: 8.73%, process: 73.72%, fetch: 0.00%, retry: 0.00%, processOutputs: 0.61%, other: 0.04%, input files: 23987, input bytes: 768787483, memory bytes: 0";
        
        RemoteMetrics rMetrics = createFromString(t, str);

    }    
}