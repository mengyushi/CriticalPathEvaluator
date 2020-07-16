import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.io.*;

public class CriticalPathEvaluator {
    private float ElapsedTime;
    private String CriticalPathString;
    private String RemoteMetrics;
    private List<CriticalPathEvent> criticalPathEvents;

    public CriticalPathEvaluator(
        float et,
        String cp) {
        ElapsedTime = et;
        CriticalPathString = cp;
        criticalPathEvents = CriticalPathEvent.getCriticalEvents(cp);
        
    }

    public static CriticalPathEvaluator create(
        String et,
        String cp) {
  
        return new CriticalPathEvaluator(Float.parseFloat(et), cp);
    }


    public static CriticalPathEvaluator createFromJsonReader(JsonReader jsonReader) throws IOException {
        JsonToken nextToken = jsonReader.peek();
        jsonReader.beginObject(); // "BEGIN_OBJECT"

        while(jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("buildToolLogs")) {
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
                                return CriticalPathEvaluator.create(decodedElapsedTimeString, decodedCriticalPathString);
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
        return CriticalPathEvaluator.create("", "");
    }

    public int totalSteps() {
        return this.criticalPathEvents.size();
    }

    public long totalRemoteSteps() {
        long count = this.criticalPathEvents.stream().filter(x->x.isRemote()).count();
        return count;
    }
        
} 