package hr.algebra.Model;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    public static final String FILENAME = "./xml/game_moves.xml";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static void saveGameMoveToXml(GameMove gameMove) {
        List<GameMove> gameMoveList = readGameMovesFromXmlFile();

        gameMoveList.add(gameMove);
        //List<GameMove> gameMoveList = new ArrayList<>();

        try {
            Document document = createDocument("gameMoves");

            for(GameMove gameMoveXmlNode : gameMoveList) {

                Element gameMoveElement = document.createElement("gameMove");
                document.getDocumentElement().appendChild(gameMoveElement);

                gameMoveElement.appendChild(createElement(document, "player", gameMoveXmlNode.getPlayer()));
                gameMoveElement.appendChild(createElement(document, "xLocation", gameMoveXmlNode.getLocation().getX().toString()));
                gameMoveElement.appendChild(createElement(document, "yLocation", gameMoveXmlNode.getLocation().getY().toString()));
                gameMoveElement.appendChild(createElement(document, "dateTime", gameMoveXmlNode.getDateTime().format(formatter)));
            }

            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException ex) {
            ex.printStackTrace();
        }
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        DocumentType documentType = domImplementation.createDocumentType("DOCTYPE", null, "employees.dtd");
        return domImplementation.createDocument(null, element, documentType);
    }
    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }
    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());
        transformer.transform(new DOMSource(document), new StreamResult(new File(fileName)));
    }

    public static List<GameMove> readGameMovesFromXmlFile() {
        List<GameMove> gameMoveList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(FILENAME));

            Node node = document.getDocumentElement();

            NodeList childNodes = node.getChildNodes();
            int numberOfNodes=childNodes.getLength();
            for(int i=0;i<numberOfNodes;i++){
                Node parentNode= childNodes.item(i);
                if(parentNode.getNodeType()==Node.ELEMENT_NODE) {

                    NodeList gameMoveNodes=parentNode.getChildNodes();

                    String playerTurn = "Player1";
                    String yLocation="";
                    String xLocation="";
                    String localDateTimeString="";

                    for (int n = 0; n < gameMoveNodes.getLength(); n++) {
                        Node moveNode = gameMoveNodes.item(n);

                        if(moveNode.getNodeType()==Node.ELEMENT_NODE) {

                            switch (moveNode.getNodeType()) {
                                case Node.ELEMENT_NODE:
                                    Element nodeElement = (Element) moveNode;
                                    String nodeName = nodeElement.getNodeName();
                                    if (nodeName.equals("player")) {
                                        String nodeValue = nodeElement.getTextContent();
                                        if (nodeValue.equals("Player2")) {
                                            playerTurn = "Player2";
                                        }
                                    }
                                    else if(nodeName.equals("xLocation")){
                                        xLocation = nodeElement.getTextContent();
                                    }
                                    else if(nodeName.equals("yLocation")){
                                        yLocation = nodeElement.getTextContent();
                                    }
                                    else if(nodeName.equals("dateTime")){
                                        localDateTimeString=nodeElement.getTextContent();
                                    }
                                    break;
                                case Node.TEXT_NODE:
                                    break;
                                case Node.CDATA_SECTION_NODE:
                                    break;
                            }


                        }
                    }
                    int xLocationInteger=Integer.parseInt(xLocation);
                    int yLocationInteger=Integer.parseInt(yLocation);
                    MoveLocation newMoveLocation= new MoveLocation(xLocationInteger,yLocationInteger);
                    LocalDateTime dateTime = LocalDateTime.parse(localDateTimeString, formatter);
                    GameMove gameMove = new GameMove(playerTurn,newMoveLocation,dateTime);
                    gameMoveList.add(gameMove);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace();
        }
        return gameMoveList;
    }    }
