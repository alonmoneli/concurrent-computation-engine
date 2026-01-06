package spl.lae;
import java.io.IOException;
import parser.*;



public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) 
            return;
        int numThreads = Integer.parseInt(args[0]);
        String inputPath = args[1];
        String outputPath = args[2];
        InputParser parser = new InputParser();
        LinearAlgebraEngine lae = new LinearAlgebraEngine(numThreads);

        try {
            ComputationNode initialNode = parser.parse(inputPath);
            ComputationNode resultNode = lae.run(initialNode);
            OutputWriter.write(resultNode.getMatrix(), outputPath);
            System.out.println(lae.getWorkerReport());
        } 
        catch (Exception e) {
            OutputWriter.write(e.getMessage(), outputPath);
        }
    }
}
