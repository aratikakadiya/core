/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *
 ************************************************************************
 */

package ca.nrc.cadc.xml;

import ca.nrc.cadc.util.Log4jInit;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;

import org.jdom2.Namespace;
import org.jdom2.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


public class JsonOutputterTest
{
    private static final Logger log = Logger.getLogger(JsonOutputterTest.class);
    
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.xml", Level.INFO);
    }
    
    private void doit(String tname, Document document, JSONObject expected)
        throws IOException, JSONException
    {
        final JsonOutputter jsonOut = new JsonOutputter();
        jsonOut.getListElementNames().add("items");
        jsonOut.getStringElementNames().add("item");
        
        final Writer writer = new StringWriter();

        jsonOut.output(document, writer);
        String actual = writer.toString();
        log.debug(tname + ":\n" + actual);
        
        // verify expected json format
        final JSONObject result = new JSONObject(actual);
        JSONAssert.assertEquals(expected, result, true);
        
        // verify round-trip through outputter and inputter
        
        // JsonInputter does not handle namespaces correctly so disable this
        //Document doc2 = jsonIn.input(actual);
        //Writer writer2 = new StringWriter();
        //jsonOut.output(doc2, writer2);
        //String actual2 = writer2.toString();
        //log.debug(tname + ": (round-trip)\n" + actual2);
        
        //final JSONObject result2 = new JSONObject(actual2);
        //JSONAssert.assertEquals(expected, result2, true);
    }

    @Test
    public void writeEmptyList() throws Exception
    {
        final Element root = new Element("root");
        final Element itemsElement = new Element("items");
        final Element metaElement = new Element("meta");
        metaElement.addContent(new Text("META"));
        root.addContent(metaElement);
        root.addContent(itemsElement);
        Document document = new Document();
        document.setRootElement(root);

        final JSONObject expected = new JSONObject("{\"root\" : {"
                                                   + "\"meta\" : {\"$\": \"META\"},"
                                                   + "\"items\" : {"
                                                   + "\"$\" : ["
                                                   + "] } } }");

        doit("writeEmptyList", document, expected);
    }



    @Test
    public void writeMultiObject() throws Exception
    {
        final Element root = new Element("root");

        // Array of five items.
        List<Integer> items = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));

        // converter array item -> Element
        ContentConverter<Element, Integer> cc = new ContentConverter<Element, Integer>() {
            public Element convert(Integer item) {
                final Element itemElement = new Element("item");
                itemElement.getAttributes().add(new Attribute("i", item.toString()));
                itemElement.addContent(new Text(item.toString()));
                return itemElement;
            }
        };

        IterableContent<Element, Integer> ic =
                new IterableContent<Element, Integer>("items", null, items.iterator(), cc);

        final Element metaElement = new Element("meta");
        metaElement.addContent(new Text("META"));

        root.addContent(metaElement);
        root.addContent(ic);

        final Document document = new Document();
        document.setRootElement(root);

        final JSONObject expected = new JSONObject("{\"root\" : {"
                                                   + "\"meta\" : {\"$\": \"META\"},"
                                                   + "\"items\" : {"
                                                   + "\"$\" : ["
                                                   + "{\r\n"
                                                   + "\"item\" : {\"@i\" : \"0\", \"$\" : \"0\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"@i\" : \"1\", \"$\" : \"1\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"@i\" : \"2\", \"$\" : \"2\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"@i\" : \"3\", \"$\" : \"3\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"@i\" : \"4\", \"$\" : \"4\"}"
                                                   + "}\r\n"
                                                   + "] } } }");

        doit("writeMultiObject", document, expected);
    }

    @Test
    public void writeRootArray() throws Exception
    {
        final Element itemsElement = new Element("items");

        // Array of five items. The outputter also works with list of items in membory besides element iterators
        for (int i = 0; i < 5; i++)
        {
            final Element itemElement = new Element("item");
            itemElement.addContent(new Text(Integer.toString(i)));

            itemsElement.addContent(itemElement);
        }


        final Document document = new Document();

        document.setRootElement(itemsElement);

        final JSONObject expected = new JSONObject("{\"items\" : {"
                                                   + "\"$\" : ["
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"0\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"1\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"2\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"3\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"4\"}"
                                                   + "}\r\n"
                                                   + "] } }");

        doit("writeRootArray", document, expected);
    }

    @Test
    public void writeNamespacePrefix() throws Exception
    {
        // no prefix
        Namespace ns = Namespace.getNamespace("nsi", "http://ns.items.com");

        // Array of five items.
        List<Integer> items = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));

        // converter array item -> Element
        ContentConverter<Element, Integer> cc = new ContentConverter<Element, Integer>() {
            public Element convert(Integer item) {
                Element itemElement = new Element("item");
                itemElement.addContent(new Text(item.toString()));
                return itemElement;
            }
        };

        IterableContent<Element, Integer> ic =
                new IterableContent<Element, Integer>("items", ns, items.iterator(), cc);

        final Document document = new Document();
        document.setRootElement(ic);

        final JSONObject expected = new JSONObject("{\r\n"
                                                   + "\"nsi:items\" : {"
                                                   + "\"@xmlns:nsi\": \"http://ns.items.com\","
                                                   + "\"$\": ["
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"0\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"1\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"2\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"3\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"4\"}"
                                                   + "}\r\n"
                                                   + "] } }");

        doit("writeNamespacePrefix", document, expected);
    }

    @Test
    public void writeNamespaceNoPrefix() throws Exception
    {
        Namespace ns = Namespace.getNamespace("http://ns.items.com");

        // Array of five items.
        List<Integer> items = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));

        // converter array item -> Element
        ContentConverter<Element, Integer> cc = new ContentConverter<Element, Integer>() {
            public Element convert(Integer item) {
                Element itemElement = new Element("item");
                itemElement.addContent(new Text(item.toString()));
                return itemElement;
            }
        };

        IterableContent<Element, Integer> ic =
                new IterableContent<Element, Integer>("items", ns, items.iterator(), cc);

        final Document document = new Document();
        document.setRootElement(ic);

        final JSONObject expected = new JSONObject("{\r\n"
                                                   + "\"items\" : {"
                                                   + "\"@xmlns\": \"http://ns.items.com\","
                                                   + "\"$\": ["
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"0\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"1\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"2\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"3\"}"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : {\"$\" : \"4\"}"
                                                   + "}\r\n"
                                                   + "] } }");

        doit("writeNamespaceNoPrefix", document, expected);
    }

    @Test
    public void testNamespaceOnMultipleBranches() throws Exception
    {
        Namespace ns = Namespace.getNamespace("nsi", "http://ns.items.com");
        Namespace otherNS = Namespace.getNamespace("nso", "http://ns.items.com/other");

        // Array of five items.
        List<Integer> items = new ArrayList<Integer>(Arrays.asList(0, 1));

        // converter array item -> Element
        ContentConverter<Element, Integer> cc = new ContentConverter<Element, Integer>() {
            public Element convert(Integer item) {
                Element itemElement = new Element("item");
                Element child = new Element("other", otherNS);
                child.addContent("stuff"+item.toString());
                itemElement.addContent(child);
                return itemElement;
            }
        };

        IterableContent<Element, Integer> ic =
                new IterableContent<Element, Integer>("items", ns, items.iterator(), cc);

        final Document document = new Document();
        document.setRootElement(ic);

        final JSONObject expected = new JSONObject("{"
                                                   + "\"nsi:items\" : {"
                                                   + "\"@xmlns:nsi\": \"http://ns.items.com\","
                                                   + "\"$\": ["
                                                   + "{\r\n"
                                                   + "\"item\" : { \"nso:other\" : {"
                                                   + "  \"@xmlns:nso\" : \"http://ns.items.com/other\","
                                                   + "  \"$\" : \"stuff0\" } }"
                                                   + "},\r\n"
                                                   + "{\r\n"
                                                   + "\"item\" : { \"nso:other\" : {"
                                                   + "  \"@xmlns:nso\" : \"http://ns.items.com/other\","
                                                   + "  \"$\" : \"stuff1\" } }"
                                                   + "}\r\n"
                                                   + "] } }");
        
        doit("testNamespaceOnMultipleBranches", document, expected);
    }
}
