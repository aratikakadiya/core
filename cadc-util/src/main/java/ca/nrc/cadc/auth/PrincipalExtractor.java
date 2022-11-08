/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2016.                         (c) 2016.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 *
 * @author jenkinsd
 * 4/20/12 - 11:05 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.auth;

import java.security.Principal;
import java.util.List;
import java.util.Set;


/**
 * Object to extract principals from a given source.  Implementors are expected
 * to know how to pull Principals from their context, and provide them to the
 * caller.
 */
public interface PrincipalExtractor {
    
    /**
     * System property to enable using the an HTTP header to get the client certificate.
     * This feature must only be enabled in a back end server that trusts the connecting
     * client to do SSL termination with client certificates; the back end server should
     * not be exposed to untrusted connections (e.g. the internet) as anyone could send in
     * a user identity (client cert) and impersonate that user. The value of this system
     * property is <em>{fully qualified class name of this class}.enableClientCertHeader</em>.
     */
    public static final String CERT_HEADER_ENABLE = PrincipalExtractor.class.getName() + ".enableClientCertHeader";
    
    /**
     * System property to enable using HTTP basic auth. This requires a custom AUthenticator
     * to validate the AuthorizationTokenPrincipal (e.g. perform the login).
     */
    public static final String ALLOW_BASIC = PrincipalExtractor.class.getName() + ".allowBasicATP";
    
    /**
     * HTTP Header value for getting the client certificate. 
     * The value is <em>X-Client-Certificate</em>.
     */
    public static final String CERT_HEADER_FIELD = "X-Client-Certificate";
    
    /**
     * Obtain a Collection of Principals from this extractor.  This should be
     * immutable.
     *
     * @return      Collection of Principal instances, or empty Collection.
     *              Never null.
     */
    Set<Principal> getPrincipals();

    /**
     * Create and return a certificate chain from the request.
     * 
     * @return an X509CertficateChain or null if not authenticated via SSL
     */
    X509CertificateChain getCertificateChain();
}
