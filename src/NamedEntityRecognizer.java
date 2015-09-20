import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by boggs on 9/12/15.
 */

public class NamedEntityRecognizer {
    public Reader reader = new Reader();
    public Writer writer = new Writer();
    public Scanner scanner = new Scanner(System.in);

    public void init() {
        ArrayList<File> files = reader.getFiles();
        ArrayList<String> lines = new ArrayList<String>();

        for(int i = 0; i < files.size(); i++) {
            System.out.println("Reading from file " + (i + 1) + " of " + files.size() + ": " + files.get(i).toString() + ".");
            lines.addAll(reader.getNodes(files.get(i)));
        }

        String nameRegex = "(.*?)(sina|kina|kay|nina|si)(\\s)([A-Z][a-z]+[\\.]*(\\s[A-Z][a-z]+)*)(.*?)";
        String locationRegex = "(.*?)(mula\\ssa|sa)(\\s)([A-Z][a-z]+[\\,]*(\\s[A-Z][a-z]+)*)(.*?)";
        String dateRegex = "(.*?)(noong|mula)(\\s)([A-Z][a-z]+(\\s[0-9]+)*(\\s[0-9]+)*)(.*?)";
        int counter = 0;

        Classifier nameClassifier = new Classifier("Name", nameRegex, writer, scanner);
        counter = 0;
        for(String line : lines) {
            System.out.println("Name - Classifying line " + (counter + 1) + " of " + lines.size() + ".");
            nameClassifier.classify(line);
            counter++;
        }

        Classifier locationClassifier = new Classifier("Location", locationRegex, writer, scanner);
        counter = 0;
        for(String line : lines) {
            System.out.println("Location - Classifying line " + (counter + 1) + " of " + lines.size() + ".");
            locationClassifier.classify(line);
            counter++;
        }

        Classifier dateClassifier = new Classifier("Date", dateRegex, writer, scanner);
        counter = 0;
        for(String line : lines) {
            System.out.println("Date - Classifying line " + (counter + 1) + " of " + lines.size() + ".");
            dateClassifier.classify(line);
            counter++;
        }


        nameClassifier.writeCounter();
        locationClassifier.writeCounter();
        dateClassifier.writeCounter();

        nameClassifier.verification(0.005);
        locationClassifier.verification(0.005);
        dateClassifier.verification(0.005);

        nameClassifier.writeVerificationCounter();
        locationClassifier.writeVerificationCounter();
        dateClassifier.writeVerificationCounter();

        nameClassifier.write();
        locationClassifier.write();
        dateClassifier.write();

        nameClassifier.writeVerification();
        locationClassifier.writeVerification();
        dateClassifier.writeVerification();

        writer.closeWriters();
        scanner.close();
    }

    public static void main(String[] args) {
        NamedEntityRecognizer m = new NamedEntityRecognizer();
        m.init();
    }
}
