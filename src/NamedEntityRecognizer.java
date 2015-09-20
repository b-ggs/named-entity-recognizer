import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by boggs on 9/12/15.
 */

public class NamedEntityRecognizer {
    public Reader reader = new Reader();
    public Writer writer = new Writer();

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

        Classifier nameClassifier = new Classifier("Name", nameRegex, writer);
        counter = 0;
        for(String line : lines) {
            System.out.println("Name - Classifying line " + (counter + 1) + " of " + lines.size() + ".");
            nameClassifier.classify(line);
            counter++;
        }

        Classifier locationClassifier = new Classifier("Location", locationRegex, writer);
        counter = 0;
        for(String line : lines) {
            System.out.println("Location - Classifying line " + (counter + 1) + " of " + lines.size() + ".");
            locationClassifier.classify(line);
            counter++;
        }

        Classifier dateClassifier = new Classifier("Date", dateRegex, writer);
        counter = 0;
        for(String line : lines) {
            System.out.println("Date - Classifying line " + (counter + 1) + " of " + lines.size() + ".");
            dateClassifier.classify(line);
            counter++;
        }


        nameClassifier.writeCounter();
        locationClassifier.writeCounter();
        dateClassifier.writeCounter();

        nameClassifier.verification(0.01);
        locationClassifier.verification(0.01);
        dateClassifier.verification(0.01);

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

//        resetFiles();
//        initializeWriters();
//        populateClassifier();
//
//        ArrayList<File> files = new ArrayList<File>();
//        listFiles("xml", files);
//
//        for(int i = 0; i < files.size(); i++) {
//            System.out.println("Reading from file " + i + " of " + files.size() + ": " + files.get(i).toString() + ".");
//            getNodes(files.get(i));
//        }
//
//        generateReport();
//        closeWriters();
    }

    public static void main(String[] args) {
        NamedEntityRecognizer m = new NamedEntityRecognizer();
        m.init();
    }
}
