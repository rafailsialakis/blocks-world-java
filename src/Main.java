import javax.swing.text.html.parser.Parser;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        long time1,time2,time3,time4;
        Scanner in = new Scanner(System.in);

        System.out.print("Please select the algorithm you want to use: ");
        String algo = in.next();

        //System.out.print("Please insert the name of the output file: ");
        //String output = in.next();
        String output = "out.txt"; //Temporary

        PDDLParser parser = new PDDLParser("probBLOCKS-3-0.pddl");

        time1 = System.currentTimeMillis();
        Problem problem = parser.parseInputFile();
        problem.getInit().printState();
        problem.getFinal().printState();
        time2 = System.currentTimeMillis();

        System.out.println("\nProblem parsed in: " + (time2-time1)/1000.0 + " seconds.\n");

        time3 = System.currentTimeMillis();
        Search.SelectSearch(problem, algo, output);
        time4 = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (time4-time3)/1000.0 + " seconds.");
    }
}