package consulo.php.impl.xdebug.connection;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DbgpResponse {
    private final Document myDocument;
    private final Element myRoot;

    private DbgpResponse(@Nonnull Document document) {
        myDocument = document;
        myRoot = document.getDocumentElement();
    }

    @Nonnull
    public static DbgpResponse parse(@Nonnull String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return new DbgpResponse(doc);
    }

    @Nonnull
    public String getRootName() {
        return myRoot.getLocalName();
    }

    @Nullable
    public String getAttribute(@Nonnull String name) {
        return myRoot.hasAttribute(name) ? myRoot.getAttribute(name) : null;
    }

    @Nullable
    public String getCommand() {
        return getAttribute("command");
    }

    @Nullable
    public String getTransactionId() {
        return getAttribute("transaction_id");
    }

    @Nullable
    public String getStatus() {
        return getAttribute("status");
    }

    @Nullable
    public String getReason() {
        return getAttribute("reason");
    }

    public boolean isInit() {
        return "init".equals(getRootName());
    }

    public boolean isStream() {
        return "stream".equals(getRootName());
    }

    public boolean hasError() {
        return getErrorElement() != null;
    }

    @Nullable
    public Element getErrorElement() {
        NodeList errors = myRoot.getElementsByTagName("error");
        return errors.getLength() > 0 ? (Element) errors.item(0) : null;
    }

    @Nullable
    public String getErrorMessage() {
        Element error = getErrorElement();
        if (error == null) {
            return null;
        }
        NodeList messages = error.getElementsByTagName("message");
        if (messages.getLength() > 0) {
            return messages.item(0).getTextContent();
        }
        return "Error code: " + error.getAttribute("code");
    }

    @Nonnull
    public Element getRoot() {
        return myRoot;
    }

    @Nonnull
    public List<Element> getChildElements(@Nonnull String tagName) {
        List<Element> result = new ArrayList<>();
        NodeList nodes = myRoot.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            result.add((Element) nodes.item(i));
        }
        return result;
    }

    @Nullable
    public Element getFirstChildElement(@Nonnull String tagName) {
        NodeList nodes = myRoot.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? (Element) nodes.item(0) : null;
    }

    @Nullable
    public String getStreamData() {
        if (!isStream()) {
            return null;
        }
        String encoded = myRoot.getTextContent();
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }
        String encoding = myRoot.getAttribute("encoding");
        if ("base64".equals(encoding)) {
            return new String(Base64.getDecoder().decode(encoded.trim()), StandardCharsets.UTF_8);
        }
        return encoded;
    }

    @Nullable
    public String getBreakpointId() {
        return getAttribute("id");
    }

    @Nonnull
    public List<Element> getStackElements() {
        return getChildElements("stack");
    }

    @Nonnull
    public List<Element> getPropertyElements() {
        return getChildElements("property");
    }

    @Nonnull
    public List<Element> getContextElements() {
        return getChildElements("context");
    }

    @Nullable
    public String getFeatureValue() {
        return myRoot.getTextContent();
    }

    @Override
    public String toString() {
        return "DbgpResponse{root=" + getRootName() + ", command=" + getCommand() + ", status=" + getStatus() + "}";
    }
}
