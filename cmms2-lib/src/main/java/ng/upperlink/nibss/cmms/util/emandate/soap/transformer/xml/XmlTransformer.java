package ng.upperlink.nibss.cmms.util.emandate.soap.transformer.xml;

import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class XmlTransformer<T> {

    private Boolean beginMapping = false;

    protected String baseClassSimpleName;

    protected Map<String,Object> baseMap = new LinkedHashMap<>();

    private int beginMappingDepth = 0, decent = 0;

    private Collection<String> arrayTags = new ArrayList<>();

    public XmlTransformer() {
    }

    public XmlTransformer(String... arrayTags) {
        this.arrayTags = Arrays.asList(arrayTags);
    }

    public Map<String,Object> transformXml(String xmlString){

        try {
            transformDocument(xmlString, null);
        } catch (Exception e) { return null; }

        return baseMap;
    }

    public Map<String,Object> transformXml(String xmlString, String baseXmlTag){

        try {
            transformDocument(xmlString, baseXmlTag);
        } catch (Exception e) { return null; }

        return baseMap;
    }

    public T transformXml(String xmlString, Class<T> classOfT){

        try {
            transformDocument(xmlString, classOfT.getSimpleName());
        } catch (Exception e) { return null; }

        return cast(classOfT);
    }

    private void transformDocument(String xmlString, String baseXmlTag) throws Exception {

        baseClassSimpleName = baseXmlTag;

        try {

            if(baseClassSimpleName == null){
                beginMapping = true;
                beginMappingDepth = 1;
            }

            recursiveNodeParser(getRootNode(xmlString));

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    private static Node getRootNode(String xmlString) throws ParserConfigurationException, IOException, SAXException {

        InputStream is = new ByteArrayInputStream(xmlString.getBytes());

        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    }

    private NodeList getDocumentNodes(String xmlString) throws ParserConfigurationException, IOException, SAXException {

        InputStream is = new ByteArrayInputStream(xmlString.getBytes());

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

        return document.getChildNodes();
    }

    protected String getNodeName(Node node){
        return node.getNodeName();
    }

    protected Object recursiveNodeParser(Node node){

        //Node is empty
        if(node.getFirstChild() == null) return node.getTextContent();

        //Node has not child
        if(node.getFirstChild().getNodeType() == 3) return node.getTextContent();

        //"Node has " + node.getChildNodes().getLength()+" children");
        return childNodesReader(node.getChildNodes(), new LinkedHashMap<>());
    }

    protected Map<String, Object> childNodesReader(NodeList nodeList, Map<String,Object> map){

        decent++;
        for(int i=0; i < nodeList.getLength(); i++){

            if(beginMappingDepth > decent) break;

            Node node = nodeList.item(i);

            if(!beginMapping){
                beginMapping = baseClassSimpleName.equals(getNodeName(node));
                if(beginMapping) beginMappingDepth = decent;
                recursiveNodeParser(node);
            }
            else{
                if((beginMappingDepth + 1) == decent){
//                    System.out.println("@Root Element Found at "+ getNodeName(node));
                    add(baseMap,node,recursiveNodeParser(node));
                }
                else add(map,node,recursiveNodeParser(node));
            }

        }
        decent--;

        return map;
    }

    protected void add(Map<String, Object> map,Node node,Object newValue){

        String key = getNodeName(node);

        if(map.containsKey(key)){
            Object value = map.get(key);
            if(value instanceof Collection){
                Collection<Object> collection = (Collection) value;
                collection.add(newValue);

                map.put(key, collection);
            }
            else{
                Collection<Object> collection = new LinkedList<>();
                collection.add(value);
                collection.add(newValue);

                map.put(key, collection);
            }

        } else if(arrayTags.contains(key)){
            Collection<Object> collection = new LinkedList<>();
            collection.add(newValue);

            map.put(key, collection);
        }
        else map.put(key, newValue);
    }

    protected T cast(Class<T> classOfT){
        Gson gson = new Gson();

        return gson.fromJson(gson.toJsonTree(baseMap), classOfT);
    }

    public static void testXml(String body) throws Exception {
        getRootNode(body);
    }
}
