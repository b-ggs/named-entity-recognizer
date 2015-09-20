import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by boggs on 9/19/15.
 */
public class Classifier {
    public String label;
    public String regex;
    public Writer writer;
    public Scanner scanner;
    public int counter = 0;
    public int trueCounter = 0;
    public HashMap<String, String> map = new HashMap<String, String>();
    public HashMap<String, Boolean> verifyMap = new HashMap<String, Boolean>();

    public Classifier(String label, String regex, Writer writer, Scanner scanner) {
        this.label = label;
        this.regex = regex;
        this.writer = writer;
        this.scanner = scanner;
    }

    public void classify(String line) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;

        matcher = pattern.matcher(line);
        if(matcher.matches() && !map.containsKey(line)) {
            System.out.println("match: " + matcher.group(4));
//            for(int i = 0; i < matcher.groupCount(); i++)
//                System.out.println(i + ": " + matcher.group(i));
            map.put(matcher.group(4).replaceAll("[()&-+.^:,;?!]", ""), label);
            counter++;
        }
    }

    public void write() {
        writer.writeLine("");
        Object[] keys = map.keySet().toArray();
        writer.writeLine("Classification: " + label + " (" + counter + " results)");
        for(Object o : keys) {
            String s = (String) o;
            writer.writeLine(s + ", " + map.get(s));
        }
    }

    public void writeCounter() {
        writer.writeLine("Classification: " + label + ", " + counter + " results");
    }

    public void verification(double percentage) {
        int iterations = (int) (percentage * counter) + 1;
        int index = 0;
        Object[] keys = map.keySet().toArray();
        String value = "";
        for(int i = 0; i < iterations; i++) {
            do {
                Random r = new Random();
                index = r.nextInt(keys.length);
                value = map.get(keys[index]);
            } while (verifyMap.containsKey(keys[index]));
            ask((String) keys[index], value, (i + 1) + " of " + iterations);
        }
    }

    public void ask(String key, String value, String progress) {
        System.out.println(label + " " + progress + " - Is " + key + " an instance of " + value + "? (y/n): ");
        String in = scanner.nextLine();
        boolean response = in.equals("y");
        System.out.println(response);
        verifyMap.put(key, response);
    }

    public void countVerification() {
        Object[] keys = verifyMap.keySet().toArray();
        for(Object o : keys) {
            String key = (String) o;
            if(verifyMap.get(key))
                trueCounter++;
        }
    }

    public void writeVerification() {
        writer.writeLine("");
        Object[] keys = verifyMap.keySet().toArray();
        ArrayList<String> lines = new ArrayList<String>();
        trueCounter = 0;
        countVerification();
        writer.writeLine("Verification: " + label + " (" + trueCounter  + " correct of " + keys.length + ")");
        for(Object o : keys) {
            String key = (String) o;
            writer.writeLine(key + ", " + label + " - " + verifyMap.get(key));
        }
    }

    public void writeVerificationCounter() {
        countVerification();
        int max = verifyMap.keySet().toArray().length;
        writer.writeLine("Verification: " + label + ", " + trueCounter + " correct of " + max + " (" + (trueCounter * 1.00 / max) * 100 + "%)");
    }
}
