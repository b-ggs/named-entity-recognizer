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
    public String regexPattern = "[A-Z][a-z]+";
    public File logFile = new File("log");
    public File classificationsFile = new File("classifications");
    public File classifiedFile =  new File("classified");
    public PrintWriter classifiedWriter;
    public HashMap<String, String> classificationsMap = new HashMap<String, String>();
    public HashMap<String, String> classifiedMap = new HashMap<String, String>();
    public HashMap<String, Integer> classificationsCounter = new HashMap<String, Integer>();
    public String[] nodeTags = {"title", "author", "month", "body"};

    public void populateClassifier() {
        try (BufferedReader br = new BufferedReader(new FileReader(classificationsFile))) {
            System.out.println("Populating classifier.");
            String line;
            String currentValue = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("=")) {
                    currentValue = line.split("=")[1];
//                    System.out.println("Current value: " + currentValue + ".");
                }
                else {
                    classificationsMap.put(line, currentValue);
//                    System.out.println("Added " + line + ", " + currentValue + ".");
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void resetFiles() {
        try {
            logFile.createNewFile();
            classifiedFile.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeWriters() {
        try {
            classifiedWriter = new PrintWriter(classifiedFile);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void closeWriters() {
        classifiedWriter.flush();
        classifiedWriter.close();
    }

    public void writeLine(PrintWriter writer, String line){
        try {
            writer.println(line);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void writeHeadersToFile() {
        writeLine(classifiedWriter, "GENERATED REPORT:");
        writeLine(classifiedWriter, "By: Aldanese, Soriano - INTRNLP S19");
        writeLine(classifiedWriter, "RegEx: " + regexPattern);
        writeLine(classifiedWriter, "");
    }

    public void writeClassificationsToFile() {
        Object[] keys = classificationsCounter.keySet().toArray();
        writeLine(classifiedWriter, "Classifications:");
        for(Object o : keys) {
            String s = (String) o;
            writeLine(classifiedWriter, s + ": " + classificationsCounter.get(s));
        }
        writeLine(classifiedWriter, "");
    }

    public void writeClassifiedToFile() {
        Object[] keys = classifiedMap.keySet().toArray();
        writeLine(classifiedWriter, "Classified names:");
        int counter = 0;
        for(Object o : keys) {
            String s = (String) o;
            writeLine(classifiedWriter, s + ", " + classifiedMap.get(s));
        }
        writeLine(classifiedWriter, "");
    }

    public void generateReport() {
        writeHeadersToFile();
        writeClassificationsToFile();
        writeClassifiedToFile();
    }

    public void classify(String line) {
        if(!classifiedMap.containsKey(line)) {
            String classification = "";
            if (classificationsMap.containsKey(line)) {
                classification = classificationsMap.get(line);
            }
            else {
                classification = "Name";
            }
            classifiedMap.put(line, classification);
            addClassificationsCounter(classification);
        }
    }

    public void addClassificationsCounter(String key) {
        if(classificationsCounter.containsKey(key)) {
            int value = classificationsCounter.get(key);
            classificationsCounter.put(key, value + 1);
        }
        else {
            classificationsCounter.put(key, 1);
        }
    }

    public void getNames(String line) {
        String[] tokens = line.split(" ");
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher;

        for(int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("&quot;","");
            tokens[i] = tokens[i].replaceAll("&nbsp;","");
            tokens[i] = tokens[i].replaceAll("&amp;","");
            tokens[i] = tokens[i].replaceAll("[()&-+.^:,;?!]", "");
            matcher = pattern.matcher(tokens[i]);
            if(matcher.matches())
                classify(tokens[i]);
        }
    }

    public void getNodes(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        NodeList nodeList = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            nodeList = document.getElementsByTagName("article");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                for(int j = 0; j < nodeTags.length; j++) {
                    try {
//                        System.out.println(nodeTags[j] + ": " + element.getElementsByTagName(nodeTags[j]).item(0).getTextContent());
                        getNames(element.getElementsByTagName(nodeTags[j]).item(0).getTextContent());
                    } catch(NullPointerException e) {
                    }
                }
            }
        }
    }

    public void listFiles(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listFiles(file.getAbsolutePath(), files);
            }
        }
    }

    public void init() {
        resetFiles();
        initializeWriters();
        populateClassifier();

        ArrayList<File> files = new ArrayList<File>();
        listFiles("xml", files);

        for(int i = 0; i < files.size(); i++) {
            System.out.println("Reading from file " + i + " of " + files.size() + ": " + files.get(i).toString() + ".");
            getNodes(files.get(i));
        }

        generateReport();
        closeWriters();
    }

    public static void main(String[] args) {
        NamedEntityRecognizer m = new NamedEntityRecognizer();
        m.init();
    }
}
