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

package ca.nrc.cadc.auth;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;

import org.apache.log4j.Logger;

/**
 * Simple X509KeyManager implementation that delegates to the default
 * X509KeyManager where possible but uses a pre-set alias to pick the
 * certificate and private key to use.
 */
public class BasicX509KeyManager implements X509KeyManager {
    private static Logger log = Logger.getLogger(BasicX509KeyManager.class);

    private X509KeyManager keyManager;
    private String alias;

    /**
     * Constructor.
     *
     * @param km    underlying KeyManager this class delegates to
     * @param alias the alias of the X509 certificate we always use
     */
    public BasicX509KeyManager(X509KeyManager km, String alias) {
        log.debug("BasicX509KeyManager");
        this.keyManager = km;
        this.alias = alias;
    }

    public String chooseClientAlias(String[] strings, Principal[] prncpls, Socket socket) {
        String ret = keyManager.chooseClientAlias(strings, prncpls, socket);
        log.debug("chooseClientAlias: looking for alias by delegating... found " + ret);
        // note sure if the above should work or not... probably not
        if (ret == null) {
            ret = this.alias;
        }
        
        log.debug("chooseClientAlias: " + ret);
        return ret;
    }

    public String chooseServerAlias(String string, Principal[] prncpls, Socket socket) {
        String ret = keyManager.chooseServerAlias(string, prncpls, socket);
        log.debug("chooseServerAlias: " + ret);
        return ret;
    }

    public X509Certificate[] getCertificateChain(String alias) {
        log.debug("getCertificateChain: " + alias);
        X509Certificate[] ret = keyManager.getCertificateChain(alias);
        if (ret != null) {
            log.debug("looking for certificate chain by delegating... found " + ret.length);
            for (int i = 0; i < ret.length; i++) {
                log.debug("getCertificateChain: " + ret[i].getSubjectDN());
            }
            
            return ret;
        }
        log.debug("looking for certificate chain by delegating... not found");
        return null;
    }

    public String[] getClientAliases(String keyType, Principal[] prncpls) {
        log.debug("getClientAliases: " + keyType);
        String[] ret = keyManager.getClientAliases(keyType, prncpls);
        log.debug("getClientAliases found: " + ret.length);
        return ret;
    }

    public PrivateKey getPrivateKey(String alias) {
        PrivateKey pk = keyManager.getPrivateKey(alias);
        log.debug("getPrivateKey for " + alias + ": " + (pk != null)); // true or false
        return pk;
    }

    public String[] getServerAliases(String keyType, Principal[] prncpls) {
        log.debug("getServerAliases: " + keyType);
        String[] ret = keyManager.getServerAliases(keyType, prncpls);
        log.debug("getServerAliases found: " + ret.length);
        return ret;
    }

}
