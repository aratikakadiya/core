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

import java.net.URI;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

/**
 * Implementation of IdentityManager that uses the X500Principal as the
 * definitive identifying object in a subject. Use this class if you want to
 * store the X509 distinguished name and be able to reconstruct the subject from
 * it later. Other principals and credentials in the callers Subject will not be
 * saved and restored: only a single X500Principal (the first one found).
 * 
 * @author pdowler
 */
public class X500IdentityManager implements IdentityManager {

    
    @Override
    public Subject validate(Subject subject) throws NotAuthenticatedException {
        return subject;
    }

    @Override
    public Subject augment(Subject subject) {
        return subject;
    }

    @Override
    public String toDisplayString(Subject subject) {
        if (subject != null) {
            Set<X500Principal> principals = subject.getPrincipals(X500Principal.class);
            for (X500Principal principal : principals) {
                return AuthenticationUtil.canonizeDistinguishedName(principal.getName());
            }
        }
        return null;
    }

    @Override
    public Object toOwner(Subject subject) {
        return toDisplayString(subject);
    }

    @Override
    public Subject toSubject(Object owner) {
        String str = (String) owner;
        X500Principal p = new X500Principal(str);
        Set<Principal> pset = new HashSet<>();
        pset.add(p);
        return new Subject(false, pset, new HashSet(), new HashSet());
    }

    @Override
    public Set<URI> getSecurityMethods() {
        Set<URI> ret = new TreeSet<>();
        ret.add(URI.create("ivo://ivoa.net/sso#tls-with-certificate"));
        ret.add(URI.create("ivo://ivoa.net/sso#anon"));
        return ret;
    }
}
