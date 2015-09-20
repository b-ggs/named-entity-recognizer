import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by boggs on 9/19/15.
 */
public class Reader {
    public String[] nodeTags = {"title", "author", "month", "body"};

    public ArrayList<File> getFiles() {
        ArrayList<File> files = new ArrayList<File>();
        listFiles("xml", files);
        return files;
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

    public ArrayList<String> getNodes(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        NodeList nodeList = null;
        ArrayList<String> lines = new ArrayList<String>();

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
                        lines.add(element.getElementsByTagName(nodeTags[j]).item(0).getTextContent());
                    } catch(NullPointerException e) {
                    }
                }
            }
        }
        return lines;
    }
}
