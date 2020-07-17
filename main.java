import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;

public class main {
    public static void main(String[] args) throws Exception  
    {
        Scanner user = new Scanner( System.in ); 
        String input;
        System.out.print("Input File Name: ");
        input = user.nextLine().trim();

        BufferedReader inputFile = new BufferedReader(new FileReader(input));
        String last = ""; 
        String line;

        while ((line = inputFile.readLine()) != null) { 
            last = line;
        }
        JsonReader jsonReader = new JsonReader(new StringReader(last));
        
        CriticalPathEvaluator CPE = CriticalPathEvaluator.createFromJsonReader(jsonReader);
        System.out.println("\n==== SUMMARY =====\n");
        System.out.println("Elapsed Time = " + Float.toString(CPE.elapsedTime()) + "s");
        System.out.println("Critical Path Time = " + Float.toString(CPE.criticalPathTime()) + "s");
        System.out.println("Total Number of Steps = " + Integer.toString(CPE.totalSteps()));
        System.out.println("Total Number of Remote Steps = " + Long.toString(CPE.remoteTotalSteps()));
        
        System.out.println("\n==== ALL STEPS =====\n");
        
        CPE.printAllSteps();

        System.out.println("\n==== QUERY =====\n");

        while (true) {

            System.out.println("1: Query Remote Metrics Summary");
            System.out.println("2: Query Detail of Single Event");
            input = user.nextLine().trim();

            if (input.equals("1")) {
                System.out.print("Input Target Metric Key: ");
                input = user.nextLine().trim();
                CPE.printRemoteTimePercentage(input);
                System.out.println();
            } else if (input.equals("2")) {
                System.out.print("Input Target Event Index: ");
                input = user.nextLine().trim();
                CPE.printEventDetails(Integer.parseInt(input));
            }
        }
    }   
}