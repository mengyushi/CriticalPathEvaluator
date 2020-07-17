import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

        System.out.println("Elapsed Time = " + Float.toString(CPE.elapsedTime()) + "s");
        System.out.println("Critical Path Time = " + Float.toString(CPE.criticalPathTime()) + "s");
        System.out.println("Total Number of Steps = " + Integer.toString(CPE.totalSteps()));
        System.out.println("Total Remote of Steps = " + Long.toString(CPE.remoteTotalSteps()));
        System.out.println("Total Parse Time = " + Float.toString(CPE.parseTotalTime()));
        System.out.println("Parse Time / Elapsed Time = " + Float.toString(CPE.parseTimePercentage() * 100) + "%");

    }   
}