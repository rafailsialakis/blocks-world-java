import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        long time1,time2;
        Scanner in = new Scanner(System.in);

        Block A = new Block("A");
        Block B = new Block("B");
        Block C = new Block("C");
        Block D = new Block("D");
        Block E = new Block("E");
        Block F = new Block("F");

        Substack s1 = new Substack(C);
        Substack s2 = new Substack(B);
        Substack s3 = new Substack(A);
        Substack s4 = new Substack(E);
        Substack s5 = new Substack(F);
        Substack s6 = new Substack(D);

        Substack s7 = new Substack();
        Substack s8 = new Substack();
        Substack s9 = new Substack(C,B,A,E,F,D);
        Substack s10 = new Substack();
        Substack s11 = new Substack();
        Substack s12 = new Substack();


        State state1 = new State(s1, s2, s3,s4, s5, s6);
        State state2 = new State(s7, s8, s9,s10, s11, s12);

        //System.out.println(state1.equals(state2));
        //System.exit(1);

        System.out.print("Select the algorithm you want to use: ");
        String algo = in.next();

        //System.out.println("Select the name of the file: ");
        //String name = in.next();

        Problem problem = new Problem(state1,state2);


        time1 = System.currentTimeMillis();
        Search.SelectSearch(problem, algo);
        time2 = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (time2-time1)/1000.0);

    }
}