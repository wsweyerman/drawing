package org.sam.christmasdrawing.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.sam.christmasdrawing.util.Person;
import org.sam.christmasdrawing.util.RandomPerm;

/**
 * Reads a list of people in from a flat file including those that they are not
 * allowed to give a gift to. Then a random drawing is generated to denote who
 * is giving gifts to whom. This drawing satisfies three rules: a person cannot
 * draw themself, two people cannot draw each other, and a person cannot draw a
 * person on their disallowed list.
 * 
 * The format of the input file is:
 *
 * person name disallowed 1 disallowed 2 ... disallowed n
 *
 * person name disallowed 1 ...
 *
 * The format of the output file is:
 *
 * giver 1 -> receiver 1 giver 2 -> receiver 2 ...
 *
 * @author smakked
 */
public class DrawingHat {
  private final LinkedHashMap<String, Person> familyMap;
  private final Person[] family;
  private RandomPerm permuter;

  public DrawingHat(RandomPerm permuter, BufferedReader inFile, List<BufferedReader> prevFiles) throws IOException {
    this.permuter = permuter;
    familyMap = readFamilyMap(inFile);
    updateAllowance(prevFiles);
    family = familyMap.values().toArray(new Person[familyMap.size()]);
  }

  private void updateAllowance(List<BufferedReader> prevFiles) throws IOException {
    for (BufferedReader prevFile : prevFiles) {
      while (prevFile.ready()) {
        // This file is of the same format as an output file. When reading we parse as
        // ${family name}->${family not allowed}
        String line = prevFile.readLine();
        String[] families = line.split("->");
        if (families.length == 2) {
          Person updatePerson = familyMap.get(families[0].trim());
          if (updatePerson != null) {
            updatePerson.addNotAllowed(families[1].trim());
          }
        }
      }
    }
  }

  private LinkedHashMap<String, Person> readFamilyMap(BufferedReader inFile) throws IOException {
    LinkedHashMap<String, Person> tmpFamilyMap = new LinkedHashMap<>();
    while (inFile.ready()) {
      String familyName = inFile.readLine();
      if (familyName.length() > 0) {
        Person person = new Person(familyName);
        while (inFile.ready()) {
          String notAllowed = inFile.readLine();
          if (notAllowed.length() == 0)
            break;
          person.addNotAllowed(notAllowed);
        }
        tmpFamilyMap.put(familyName, person);
      }
    }
    return tmpFamilyMap;
  }

  private boolean isPermutationAllowed(int[] perm) {
    int n = familyMap.size();
    if (n != perm.length)
      throw new IllegalArgumentException("Family set is size " + n + ", but permutation is size "
              + perm.length);

    for (int i = 0; i < n; i++) {
      // don't allow someone to draw their own name, the name of someone who is
      // disallowed or have a cycle of length two
      if (i == perm[perm[i]]) {
        System.out.println("Not allowing circular (" + i + "," + perm[i] + "): " 
            + family[i].getName() + " <-> " + family[perm[i]].getName());
        return false;
      }
      if (!family[i].isAllowed(family[perm[i]].getName())) {
        System.out.println("Not allowing (" + i + "," + perm[i] + "): " + family[i].getName()
                + " -> " + family[perm[i]].getName());
        return false;
      }
    }
    return true;
  }

  public List<Integer> getDrawing() {
    int[] perm = permuter.getRandomPerm(familyMap.size());
    while (!isPermutationAllowed(perm)) {
      for (int i = 0; i < familyMap.size(); i++) {
        System.out.print(perm[i] + ",");
      }
      System.out.println("Trying another permutation....");
      perm = permuter.getRandomPerm(familyMap.size());
    }
    List<Integer> drawing = new ArrayList<>();
    for (int p : perm) {
      drawing.add(p);
    }
    return drawing;
  }

  public void writeDrawing(FileWriter outFile, List<Integer> perm)
          throws IOException {
    int n = familyMap.size();
    if (n != perm.size())
      throw new IllegalArgumentException("Family set is size " + n + ", but permutation is size "
              + perm.size());

    for (int i = 0; i < n; i++) {
      String from = family[i].getName();
      String to = family[perm.get(i)].getName();
      System.out.println(from + " -> " + to);
      outFile.write(from + " -> " + to + "\n");
    }
  }

  private static final String INPUT_ARG = "input";
  private static final String OUTPUT_ARG = "output";
  private static final String PREV_ARG = "prev";
  @SuppressWarnings("static-access")
  private static Options buildCli() {
    Options cli = new Options();

    cli.addOption(OptionBuilder
        .withArgName(INPUT_ARG)
        .withLongOpt(INPUT_ARG)
        .hasArg()
        .withDescription("Input file with people to include in drawing and disallowed pairings.")
        .isRequired()
        .create());
    cli.addOption(OptionBuilder
        .withArgName(OUTPUT_ARG)
        .withLongOpt(OUTPUT_ARG)
        .hasArg()
        .withDescription("Filename to write the output of the drawing.")
        .isRequired()
        .create());
    cli.addOption(OptionBuilder
        .withArgName(PREV_ARG)
        .withLongOpt(PREV_ARG)
        .hasArgs()
        .withDescription("Previous output files to read as disallowed pairings.")
        .create());
    return cli;
  }

  public static void main(String[] args) throws IOException {
    Options cliOptions = buildCli();
    CommandLineParser parser = new BasicParser();
    CommandLine line = null;
    try {
      line = parser.parse(cliOptions, args);
    } catch (ParseException e) {
      System.err.println("Command line parsing failed: " + e.getMessage());
      HelpFormatter help = new HelpFormatter();
      help.printHelp("DrawingHat", cliOptions);
      System.exit(1);
    }
    String inFileName = line.getOptionValue(INPUT_ARG);
    String outFileName = line.getOptionValue(OUTPUT_ARG);
    String[] prevFileNames = line.getOptionValues(PREV_ARG);
    FileWriter outFile = null;
    BufferedReader inFile = null;
    List<BufferedReader> prevFiles = new ArrayList<>();
    try {
      inFile = new BufferedReader(new FileReader(inFileName));
      if (prevFileNames != null) {
        for (String prevFileName : prevFileNames) {
          prevFiles.add(new BufferedReader(new FileReader(prevFileName)));
        }
      }
      DrawingHat drawingHat = new DrawingHat(new RandomPerm(new Random()), inFile, prevFiles);
      List<Integer> drawing = drawingHat.getDrawing();
 
      outFile = new FileWriter(outFileName);
      drawingHat.writeDrawing(outFile, drawing);
    } finally {
      if (outFile != null) {
        outFile.close();
      }
      if (inFile != null) {
        inFile.close();
      }
    }
  }
}
