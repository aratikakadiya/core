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
 *  $Revision: 5 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.net;

import ca.nrc.cadc.util.Log4jInit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;


/**
 * @author pdowler
 */
public class NetUtilTest {
    private static Logger log = Logger.getLogger(NetUtilTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Log4jInit.setLevel("ca.nrc.cadc.net", Level.INFO);
    }

    @Test
    public void testPlainString()
        throws Exception {
        try {
            String s1 = "abcdefghijklmnopqrstuvwxyz0123456789";
            String s2 = NetUtil.encode(s1);
            String s3 = NetUtil.decode(s2);
            log.debug(s1 + " -> " + s2 + " -> " + s3);
            Assert.assertEquals(s1, s2);
            Assert.assertEquals(s1, s3);
        } catch (Exception unexpected) {
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    @Test
    public void testPlainURL()
        throws Exception {
        try {
            String s1 = "http://www.example.com/foobar";
            String s2 = NetUtil.encode(s1);
            String s3 = NetUtil.decode(s2);
            log.debug(s1 + " -> " + s2 + " -> " + s3);
            Assert.assertNotSame(s1, s2);
            Assert.assertEquals(s1, s3);
        } catch (Exception unexpected) {
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    @Test
    public void testAllSpecialChars()
        throws Exception {
        try {
            String s1 = "`~!@#$%^&*()-_=+[]{};':\",./<>?";
            String s2 = NetUtil.encode(s1);
            String s3 = NetUtil.decode(s2);
            log.debug(s1 + " -> " + s2 + " -> " + s3);
            Assert.assertNotSame(s1, s2);
            Assert.assertEquals(s1, s3);
        } catch (Exception unexpected) {
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    @Test
    public void testURIWithPlusInFilename()
        throws Exception {
        try {
            String s1 = "scheme://authority/path/file+name.extension";
            String s2 = NetUtil.encode(s1);
            String s3 = NetUtil.decode(s2);
            log.debug(s1 + " -> " + s2 + " -> " + s3);
            Assert.assertNotSame(s1, s2);
            Assert.assertEquals(s1, s3);
        } catch (Exception unexpected) {
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    @Test
    public void testURIWithSpaceInFilename()
        throws Exception {
        try {
            String s1 = "scheme://authority/path/file name.extension";
            String s2 = NetUtil.encode(s1);
            String s3 = NetUtil.decode(s2);
            log.debug(s1 + " -> " + s2 + " -> " + s3);
            Assert.assertNotSame(s1, s2);
            Assert.assertEquals(s1, s3);
        } catch (Exception unexpected) {
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    @Test
    public void getClientIPForwarded() throws Exception {
        final HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);

        EasyMock.expect(mockRequest.getHeader(NetUtil.FORWARDED_FOR_CLIENT_IP_HEADER))
                .andReturn("192.168.33.44, 192.168.0.3, 192.169.5.5")
                .once();

        EasyMock.replay(mockRequest);

        Assert.assertEquals("Wrong Client IP.", "192.168.33.44", NetUtil.getClientIP(mockRequest));

        EasyMock.verify(mockRequest);

        // Reset to re-use.
        EasyMock.reset(mockRequest);

        EasyMock.expect(mockRequest.getHeader(NetUtil.FORWARDED_FOR_CLIENT_IP_HEADER)).andReturn("192.169.5.5").once();

        EasyMock.replay(mockRequest);

        Assert.assertEquals("Wrong Client IP.", "192.169.5.5", NetUtil.getClientIP(mockRequest));

        EasyMock.verify(mockRequest);
    }

    @Test
    public void getClientIPPlain() throws Exception {
        final HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);

        EasyMock.expect(mockRequest.getHeader(NetUtil.FORWARDED_FOR_CLIENT_IP_HEADER)).andReturn(null).once();
        EasyMock.expect(mockRequest.getRemoteAddr()).andReturn("192.169.5.5").once();

        EasyMock.replay(mockRequest);

        Assert.assertEquals("Wrong Client IP.", "192.169.5.5", NetUtil.getClientIP(mockRequest));

        EasyMock.verify(mockRequest);
    }

    @Test
    public void getDomainName() throws Exception {
        final String domainName1 =
            NetUtil.getDomainName(new URL("http://www.google.com/my/path"));
        Assert.assertEquals("Domain Name should be google.com",
                            "google.com", domainName1);

        final String domainName2 = NetUtil.getDomainName(new URL("http://cadc.ca"));
        Assert.assertEquals("Domain Name should be cadc.ca", "cadc.ca",
                            domainName2);

        final String domainName3 =
            NetUtil.getDomainName(new URL("http://user:pass@cadc.ca/path"));
        Assert.assertEquals("Domain Name should be cadc.ca", "cadc.ca",
                            domainName3);

        final String domainName4 =
            NetUtil.getDomainName(new URL("http://gimli.cadc.dao.nrc.ca"));
        Assert.assertEquals("Domain Name should be cadc.dao.nrc.ca",
                            "cadc.dao.nrc.ca", domainName4);

    }
}
