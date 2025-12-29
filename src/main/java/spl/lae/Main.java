package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main

      //Useful For Later
      int numberOfThreads;
      String inputFilePath, outputFilePath;

      if (args.length == 3) {      
        numberOfThreads = Integer.parseInt(args[0]);
        inputFilePath = args[1];
        outputFilePath = args[2];
      }
    }
}