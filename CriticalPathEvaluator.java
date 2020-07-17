import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.io.*;

public class CriticalPathEvaluator {
    private float elapsedTime;
    private float criticalPathTime;
    private float remoteTotalTime;
    private String criticalPathString;
    private RemoteMetrics remoteMetrics;
    private List<CriticalPathEvent> criticalPathEvents;

    public CriticalPathEvaluator(
        float time,
        String criticalPath) {
        elapsedTime = time;
        criticalPathString = criticalPath;
        criticalPathEvents = CriticalPathEvent.getCriticalEvents(criticalPath);

        Matcher remoteTimeMatcher = Pattern.compile(".* ([\\d\\.\\d]*)s, Remote \\((.*)% of the time\\).*\\[(.*)\\]").matcher(criticalPath.split("\n")[0]);
        if(remoteTimeMatcher.matches()){
            criticalPathTime = Float.parseFloat(remoteTimeMatcher.group(1));
            remoteTotalTime = criticalPathTime*Float.parseFloat(remoteTimeMatcher.group(2))/100;
            remoteMetrics = RemoteMetrics.createFromString(criticalPathTime, remoteTimeMatcher.group(3));
        } else {
            System.out.println("Summary remote metrics matching error");
        }
    }

    public static CriticalPathEvaluator createFromJsonReader(JsonReader jsonReader) throws IOException {
        JsonToken nextToken = jsonReader.peek();
        jsonReader.beginObject(); // "BEGIN_OBJECT"

        while(jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("buildToolLogs")) {
                jsonReader.beginObject();

                while(jsonReader.hasNext()) {
                    name = jsonReader.nextName();
                    if(name.equals("log")) {
                        jsonReader.beginArray();

                        // now in log array

                        String decodedElapsedTimeString = "";

                        jsonReader.beginObject();
                        while(jsonReader.hasNext()) {
                            name = jsonReader.nextName();
                            if(name.equals("contents")) {
                                String encodedElapsedTime = jsonReader.nextString();
                                byte[] decodedElapsedTimeBytes = Base64.getDecoder().decode(encodedElapsedTime);
                                decodedElapsedTimeString = new String(decodedElapsedTimeBytes);
                                break;
                            } else {
                                jsonReader.skipValue();
                            }
                        }

                        jsonReader.endObject();

                        jsonReader.beginObject();

                        while(jsonReader.hasNext()) {
                            name = jsonReader.nextName();
                            if(name.equals("contents")) {
                                String encodedCriticalPathString = jsonReader.nextString();
                                byte[] decodedCriticalPathBytes = Base64.getDecoder().decode(encodedCriticalPathString);
                                String decodedCriticalPathString = new String(decodedCriticalPathBytes);
                                // System.out.println(decodedCriticalPathString);
                                return new CriticalPathEvaluator(Float.parseFloat(decodedElapsedTimeString), decodedCriticalPathString);
                            } else {
                                jsonReader.skipValue();
                            }
                        }

                    } else {
                        jsonReader.skipValue();
                    }
                }

                
            } else {
                jsonReader.skipValue();
            }
        }   
        return new CriticalPathEvaluator(0.0f, "");
    }

    public int totalSteps() {
        return this.criticalPathEvents.size();
    }

    public long remoteTotalSteps() {
        long count = this.criticalPathEvents.stream().filter(x->x.isRemote()).count();
        return count;
    }

    public float elapsedTime() {
        return this.elapsedTime;
    }

    public float criticalPathTime() {
        return this.criticalPathTime;
    }

    public float remoteTotalTime() {
        return this.remoteTotalTime;
    }

    public float parseTotalTime() {
        float parseTime = 0.0f;
        for (CriticalPathEvent event:this.criticalPathEvents) {
            if (event.isRemote()) {
                parseTime += event.remoteMetrics().parseTime();
            }
        }
        return parseTime;
    }

    public float parseTimePercentage() {
        float parseTime = this.parseTotalTime();
        return parseTime / this.elapsedTime;
    }
        
} 