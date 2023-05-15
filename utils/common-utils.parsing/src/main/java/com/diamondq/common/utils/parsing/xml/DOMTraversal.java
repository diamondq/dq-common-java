package com.diamondq.common.utils.parsing.xml;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DOMTraversal {

  /**
   * Traverses from a given node down the arguments to a set of possible result nodes
   *
   * @param pStartingNode the starting node (NOTE: Unless it's an Element or Document, there are no real children)
   * @param pClass the target node type
   * @param pTraverseArgs the arguments. Each is a two part portion (ie. namespace URI/local name)
   * @return the resulting set
   */
  public static <X extends Node> List<X> traverse(Node pStartingNode, Class<X> pClass,
    @Nullable String... pTraverseArgs) {
    List<X> results = new ArrayList<>();

    Element elem;
    if (pStartingNode instanceof Element) elem = (Element) pStartingNode;
    else if (pStartingNode instanceof Document) elem = ((Document) pStartingNode).getDocumentElement();
    else return results;

    /* Recursively handle the arguments */

    List<X> elementResults = new ArrayList<>();
    if (pTraverseArgs.length > 0)
      internalTraverse(elem, pStartingNode instanceof Document, elementResults, pClass, 0, pTraverseArgs);
    else {
      if (pClass.isInstance(elem)) {
        @SuppressWarnings("unchecked") X xe = (X) elem;
        elementResults.add(xe);
      }
    }

    return elementResults;
  }

  private static <X extends Node> void internalTraverse(Element pParent, boolean pParentWasDocument, List<X> pResults,
    Class<X> pClass, int pOffset, @Nullable String... pTraverseArgs) {
    String namespaceURI = pTraverseArgs[pOffset];
    String localName = pTraverseArgs[pOffset + 1];

    if (pParentWasDocument == true) {
      String testNamespaceURI = pParent.getNamespaceURI();
      if (Objects.equals(namespaceURI, testNamespaceURI) == true) {
        String testLocalName = pParent.getLocalName();
        if (Objects.equals(localName, testLocalName) == true) {

          /* If there are more arguments, then recurse */

          if (pOffset < (pTraverseArgs.length - 2))
            internalTraverse(pParent, false, pResults, pClass, pOffset + 2, pTraverseArgs);
          else {

            /* Otherwise, add this as a result */

            @SuppressWarnings("unchecked") X xe = (X) pParent;
            pResults.add(xe);
          }
        }
      }
    } else {
      boolean isLast = pOffset >= (pTraverseArgs.length - 2);
      if ((isLast == false) || ((isLast == true) && (Element.class.isAssignableFrom(pClass)))) {
        Node child = pParent.getFirstChild();
        while (child != null) {
          if ((child instanceof Element) == true) {
            Element e = (Element) child;
            String testNamespaceURI = e.getNamespaceURI();
            if (Objects.equals(namespaceURI, testNamespaceURI) == true) {
              String testLocalName = e.getLocalName();
              if (Objects.equals(localName, testLocalName) == true) {

                /* If there are more arguments, then recurse */

                if (pOffset < (pTraverseArgs.length - 2))
                  internalTraverse(e, false, pResults, pClass, pOffset + 2, pTraverseArgs);
                else {

                  /* Otherwise, add this as a result */

                  @SuppressWarnings("unchecked") X xe = (X) e;
                  pResults.add(xe);
                }
              }
            }
          }
          child = child.getNextSibling();
        }
      } else if (isLast == true) {
        if (Attr.class.isAssignableFrom(pClass)) {
          Attr attr = pParent.getAttributeNodeNS(namespaceURI, localName);
          if (attr != null) {
            @SuppressWarnings("unchecked") X xe = (X) attr;
            pResults.add(xe);
          }
        } else throw new UnsupportedOperationException();
      }
    }
  }

  public static <X extends Node> @Nullable X traverseSingle(Node pStartingNode, Class<X> pClass,
    @Nullable String... pTraverseArgs) {
    List<X> resultList = traverse(pStartingNode, pClass, pTraverseArgs);
    if (resultList.isEmpty()) return null;
    X result = resultList.iterator().next();
    return result;
  }

  public static <X extends Node> X traverseRequiredSingle(Node pStartingNode, Class<X> pClass,
    @Nullable String... pTraverseArgs) {
    List<X> resultList = traverse(pStartingNode, pClass, pTraverseArgs);
    if (resultList.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Unable to find ");
      boolean first = true;
      for (String s : pTraverseArgs) {
        if (s == null) continue;
        if (first == true) first = false;
        else sb.append(", ");
        sb.append(s);
      }
      sb.append(" within the document");
      throw new RuntimeException(sb.toString());
    }
    X result = resultList.iterator().next();
    return result;
  }

  /**
   * Return's the String value of the node. For an Attr, it's the Attributes value, for an Element, it's the
   * concatenation of all Text children. For all other node types, it's just the Node Value.
   *
   * @param pNode
   * @return the value
   */
  public static <X extends Node> String getValue(X pNode) {
    if (pNode instanceof Attr) return ((Attr) pNode).getValue();
    else if (pNode instanceof Element) {
      Element e = (Element) pNode;
      StringBuilder sb = new StringBuilder();
      Node child = e.getFirstChild();
      while (child != null) {
        if (child instanceof Text) {
          Text t = (Text) child;
          sb.append(t.getData());
        }
        child = child.getNextSibling();
      }
      return sb.toString();
    } else {
      String v = pNode.getNodeValue();
      if (v == null) v = "";
      return v;
    }
  }

  public static @Nullable String getAttributeValue(Element pElement, @Nullable String pNamespace, String pAttrName) {
    return pElement.getAttributeNS(pNamespace, pAttrName);
  }
}
