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

public class main {
    public static void main(String[] args) throws Exception  
    {
        BufferedReader input = new BufferedReader(new FileReader("output.json"));
        String last = ""; 
        String line;

        while ((line = input.readLine()) != null) { 
            last = line;
        }

        JsonReader jsonReader = new JsonReader(new StringReader(last));
        CriticalPathEvaluator CPE = CriticalPathEvaluator.createFromJsonReader(jsonReader);

        System.out.println("Total Number of Steps = " + Integer.toString(CPE.totalSteps()));
        System.out.println("Total Remote of Steps = " + Long.toString(CPE.totalRemoteSteps()));

    }   
}