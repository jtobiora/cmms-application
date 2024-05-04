package ng.upperlink.nibss.cmms.util.emandate.soap.transformer.soap;

import ng.upperlink.nibss.cmms.util.emandate.soap.enums.SoapTransformerMode;
import ng.upperlink.nibss.cmms.util.emandate.soap.enums.SoapTransformerTarget;
import ng.upperlink.nibss.cmms.util.emandate.soap.transformer.xml.XmlTransformer;
import org.w3c.dom.Node;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Nduka on 02/10/2018.
 *
 */
public class SoapTransformer<T> extends XmlTransformer<T> {

    public SoapTransformer() {
    }

    public SoapTransformer(String... arrayTags) {
        super(arrayTags);
    }

    public Map<String,Object> transformHeader(String xmlString, String baseXmlTag){

        return transformHeader(xmlString, baseXmlTag, SoapTransformerMode.DEFAULT);
    }

    public Map<String,Object> transformHeader(String xmlString, String baseXmlTag, SoapTransformerMode mode){

        try {
            transformTarget(SoapTransformerTarget.HEADER, xmlString, baseXmlTag, mode);
        } catch (Exception e) { return null; }

        return baseMap;
    }

    public T transformHeader(String xmlString, Class<T> classOfT){

        return transformHeader(xmlString, classOfT, SoapTransformerMode.DEFAULT);
    }

    public T transformHeader(String xmlString, Class<T> classOfT, SoapTransformerMode mode){

        try {
            transformTarget(SoapTransformerTarget.HEADER, xmlString, classOfT.getSimpleName(), mode);
        } catch (Exception e) { return null; }

        return cast(classOfT);
    }

    public Map<String,Object> transformBody(String xmlString, String baseXmlTag){

        return transformBody(xmlString, baseXmlTag, SoapTransformerMode.DEFAULT);
    }

    public Map<String,Object> transformBody(String xmlString, String baseXmlTag, SoapTransformerMode mode){

        try {
            transformTarget(SoapTransformerTarget.BODY, xmlString, baseXmlTag, mode);
        } catch (Exception e) { return null; }

        return baseMap;
    }

    public T transformBody(String xmlString, Class<T> classOfT) {

        return transformBody(xmlString, classOfT, SoapTransformerMode.DEFAULT);
    }

    public T transformBody(String xmlString, Class<T> classOfT, SoapTransformerMode mode){

        try {
            transformTarget(SoapTransformerTarget.BODY, xmlString, classOfT.getSimpleName(), mode);
        } catch (Exception e) { return null; }

        return cast(classOfT);
    }

    private void transformTarget(SoapTransformerTarget target, String xmlString, String baseXmlTag, SoapTransformerMode mode) throws Exception {

        baseClassSimpleName = baseXmlTag;

        try {

            if(SoapTransformerTarget.BODY == target) transformNode(getSoapMessage(xmlString).getSOAPBody(), mode);
            else if(SoapTransformerTarget.HEADER == target) transformNode(getSoapMessage(xmlString).getSOAPHeader(), mode);

        } catch (SOAPException | IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    private static SOAPMessage getSoapMessage(String xmlString) throws SOAPException, IOException {
        InputStream is = new ByteArrayInputStream(xmlString.getBytes());
        return MessageFactory.newInstance().createMessage(null, is);
    }

    private void transformNode(Node node, SoapTransformerMode mode){
        if(mode == SoapTransformerMode.ROOT) recursiveNodeParser(node.getParentNode());
        else if(mode == SoapTransformerMode.HEAD) recursiveNodeParser(node);
        else childNodesReader(node.getChildNodes(), baseMap);
    }

    public T transform (String xml, Class<T> c) {
        return transformBody(xml,c, SoapTransformerMode.ROOT);
    }

    @Override
    protected String getNodeName(Node node){
        return node.getLocalName();
    }

    public static void testXml(String body) throws Exception {
        getSoapMessage(body);
    }

}
