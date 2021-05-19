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

package ca.nrc.cadc.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Date conversion utility.
 * <p>
 * WARNING: The underlying SimpleDateFormat instances are NOT thread safe.
 * </p>
 * 
 * @version $Version$
 * @author pdowler
 */
public class DateUtil {
    /**
     * Pseudo-ISO8601 datetime format with milliseconds. This is nice for display as
     * it leaves a space between the date and time parts.
     */
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * Pseudo-ISO8601 datetime format with milliseconds and explicit timezone. This
     * is nice for display as it leaves a space between the date and time parts.
     */
    public static final String ISO_DATE_FORMAT_TZ = "yyyy-MM-dd HH:mm:ss.SSSZ";

    /**
     * IVOA standard datetime format string with milliseconds. Apparently the IVOA
     * went rogue (with respect to ISO8601) by mandating UTC only and then dropping
     * the Z timezone descriptor from the format string.
     */
    public static final String IVOA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    // public static final String IVOA_DATE_FORMAT = ISO8601_DATE_FORMAT_MSZ;

    /**
     * ISO8601 UTC datetime format without milliseconds.
     */
    public static final String ISO8601_DATE_FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * ISO8601 UTC datetime format with milliseconds.
     */
    public static final String ISO8601_DATE_FORMAT_MSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * ISO8601 local datetime format without milliseconds.
     */
    public static final String ISO8601_DATE_FORMAT_LOCAL = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * ISO8601 local datetime format with milliseconds.
     */
    public static final String ISO8601_DATE_FORMAT_MSLOCAL = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    
    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static final TimeZone LOCAL = TimeZone.getDefault();

    /**
     * Create a new DateFormat object with the specified format and timezone. If the
     * format is null it defaults to ISO format (without required TZ). If the time
     * zone is null it defaults to UTC for ISO8601_DATE_FORMAT_Z,
     * ISO8601_DATE_FORMAT_MSZ, and IVOA_DATE_FORMAT and must be UTC for the first
     * two of these.
     * <p>
     * WARNING: The underlying SimpleDateFormat instance is NOT thread safe.
     * </p>
     * 
     * @param format
     * @param tz
     * @return
     */
    public static DateFormat getDateFormat(String format, TimeZone tz) {
        if (format == null) {
            format = ISO_DATE_FORMAT; // best display format
        }

        if (format.equals(ISO8601_DATE_FORMAT_Z) || format.equals(ISO8601_DATE_FORMAT_MSZ)) {
            if (tz == null) {
                tz = UTC;
            } else if (!UTC.equals(tz)) {
                throw new IllegalArgumentException("Cannot use format " + format + " with non-UTC timezone");
            }
        }
        // NOTE: since the IVOA_DATE_FORMAT is identical to ISO8601_DATE_FORMAT_MSLOCAL
        // we cannot enforce the correct
        // timezone
        // else if ( format.equals(ISO8601_DATE_FORMAT_LOCAL) ||
        // format.equals(ISO8601_DATE_FORMAT_MSLOCAL) )
        // {
        // if (tz == null)
        // tz = LOCAL;
        // else if ( !LOCAL.equals(tz) )
        // throw new IllegalArgumentException("Cannot use format " + format + " with
        // non-LOCAL timezone");
        // }

        // Explicitly set the formatting locale in SimpleDateFormat.
        // The HTTP_DATE_FORMAT string throws an 'Unparseable date' error in Java 11.
        // The error was not thrown in Java 8.
        // Setting the locale in the SimpleDateFormat constructor to CANADA
        // or CANADA_FRENCH throws a parse error.
        // Setting the locale to ENGLISH, US, or UK, does not.
        SimpleDateFormat ret = new SimpleDateFormat(format, Locale.ENGLISH);
        ret.setLenient(false);
        if (tz != null) {
            ret.setTimeZone(tz);
        }
        
        return ret;
    }

    /**
     * Sloppy parsing. This method makes several attempts to parse the supplied date
     * string before giving up. if the initial parse fails, it tries to append 0
     * milliseconds to the time, then a time of 0:0:0, then 0:0:0.0, and the it
     * tries setting the DateFormat to lenient.
     *
     * @param s   string representation of the date
     * @param fmt the DateFormat to use
     * @return the Date
     * @throws java.text.ParseException the argument cannot be parsed after all
     *                                  attempts
     */
    public static Date flexToDate(String s, DateFormat fmt) throws ParseException {
        // try to parse
        ParseException orig = null;
        NumberFormatException origN = null;
        try {
            return fmt.parse(s);
        } catch (ParseException pex) {
            orig = pex;
        } catch (NumberFormatException nex) {
            origN = nex;
        }

        // missing milliseconds?
        try {
            return fmt.parse(s + ".0");
        } catch (ParseException ignore) {
            // do nothing
        } catch (NumberFormatException ignore) {
            // do nothing
        }

        // missing time?
        try {
            return fmt.parse(s + "T0:0:0.0");
        } catch (ParseException ignore) {
            // do nothing
        } catch (NumberFormatException ignore) {
            //do nothing
        }
        
        try {
            return fmt.parse(s + " 0:0:0.0");
        } catch (ParseException ignore) {
            // do nothing
        } catch (NumberFormatException ignore) {
            // do nothing
        }

        if (orig != null) {
            throw orig;
        }
        
        throw new ParseException("failed to parse '" + s + "': " + origN, 0);
    }

    /**
     * Convert the argument date string to a vanilla java.util.Date. The input
     * object can be a java.sql.Date (or subclass) or java.sql.Timestamp (or
     * subclass).
     *
     * @param date the original date
     * @return a Date
     * @throws UnsupportedOperationException
     */
    public static Date toDate(Object date) {
        if (date == null) {
            return null;
        }
        
        if (date instanceof java.sql.Timestamp) {
            java.sql.Timestamp ts = (java.sql.Timestamp) date;
            long millis = ts.getTime();

            // NOTE: On DB2 the millis is only to the second and all the fractional
            // part is in the nanos
            // int nanos = ts.getNanos();
            // millis += (long) (nanos / 1000000);

            return new Date(millis);
        }
        if (date instanceof java.sql.Date) {
            java.sql.Date sd = (java.sql.Date) date;
            return new Date(sd.getTime());
        }
        if (date instanceof Date) {
            return (Date) date;
        }
        throw new UnsupportedOperationException(
                "failed to convert " + date.getClass().getName() + " to java.util.Date");
    }

    /**
     * Convert a Modified Julian Date to a date in the UTC timezone.
     *
     * @param mjd the MJD value
     * @return a Date in the UTC timezone
     */
    public static Date toDate(double mjd) {
        final int[] ymd = slaDjcl(mjd);

        // fraction of a day
        double frac = mjd - ((double) (long) mjd);
        int hh = (int) (frac * 24);
        // fraction of an hour
        frac = frac * 24.0 - hh;
        int mm = (int) (frac * 60);
        // fraction of a minute
        frac = frac * 60.0 - mm;
        int ss = (int) (frac * 60);
        // fraction of a second
        frac = frac * 60.0 - ss;
        int ms = (int) (frac * 1000);
        // frac = frac*1000.0 - ms;

        Calendar cal = Calendar.getInstance(UTC);
        cal.set(Calendar.YEAR, ymd[0]);
        cal.set(Calendar.MONTH, ymd[1] - 1); // Calendar is 0-based

        cal.set(Calendar.DAY_OF_MONTH, ymd[2]);
        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mm);
        cal.set(Calendar.SECOND, ss);
        cal.set(Calendar.MILLISECOND, ms);

        return cal.getTime();
    }

    /**
     * Obtain the Date from the given DMF seconds.
     *
     * @param dmfSeconds The DMF Seconds value.
     * @return Date object from the dmf Seconds.
     */
    public static Date toDate(final long dmfSeconds) {
        return new Date((dmfSeconds * 1000L) + getDMFEpoch().getTime());
    }

    /**
     * Convert a date in the UTC timezone to Modified Julian Date. Note that the
     * double datatype for MJD does not have enough digits for successful round-trip
     * conversuion of all possible Date values (i.e. it has less that microsecond
     * precision).
     *
     * @param date a date in the UTC timezone
     * @return the MJD value
     */
    public static double toModifiedJulianDate(Date date) {
        return toModifiedJulianDate(date, UTC);
    }

    /**
     * Convert a date in the specified timezone to Modified Julian Date.
     *
     * @param date
     * @param timezone
     * @return number of days
     */
    public static double toModifiedJulianDate(Date date, TimeZone timezone) {
        Calendar cal = Calendar.getInstance(timezone);
        cal.clear();
        cal.setTime(date);
        int yr = cal.get(Calendar.YEAR);
        int mo = cal.get(Calendar.MONTH) + 1; // Calendar is 0-based

        int dy = cal.get(Calendar.DAY_OF_MONTH);
        double days = slaCldj(yr, mo, dy);

        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        int ss = cal.get(Calendar.SECOND);
        int ms = cal.get(Calendar.MILLISECOND);
        double seconds = hh * 3600.0 + mm * 60.0 + ss + ms / 1000.0;

        return days + seconds / 86400.0;
    }

    /* Month lengths in days */
    private static int[] mtab = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    // private static double slaCldj( int iy, int im, int id, double *djm, int *j )
    private static double slaCldj(int iy, int im, int id) {
        /*
         ** - - - - - - - - s l a C l d j - - - - - - - -
         **
         ** Gregorian calendar to Modified Julian Date.
         **
         ** Given: iy,im,id int year, month, day in Gregorian calendar
         **
         ** Returned: *djm double Modified Julian Date (JD-2400000.5) for 0 hrs *j int
         * status: 0 = OK 1 = bad year (MJD not computed) 2 = bad month (MJD not
         * computed) 3 = bad day (MJD computed)
         **
         ** The year must be -4699 (i.e. 4700BC) or later.
         **
         ** The algorithm is derived from that of Hatcher 1984 (QJRAS 25, 53-55).
         **
         ** Last revision: 29 August 1994
         **
         ** Copyright P.T.Wallace. All rights reserved.
         */
        // System.out.println("[slaCldj] " + iy + ", " + im + ", " + id);

        /* Validate year */
        // if ( iy < -4699 ) { *j = 1; return; }
        if (iy < -4699) {
            throw new IllegalArgumentException("bad year");
        }

        /* Validate month */
        // if ( ( im < 1 ) || ( im > 12 ) ) { *j = 2; return; }
        if ((im < 1) || (im > 12)) {
            throw new IllegalArgumentException("bad month");
        }

        /* Allow for leap year */
        mtab[1] = (((iy % 4) == 0) && (((iy % 100) != 0) || ((iy % 400) == 0))) ? 29 : 28;

        /* Validate day */
        // *j = ( id < 1 || id > mtab[im-1] ) ? 3 : 0;
        if (id < 1 || id > mtab[im - 1]) {
            throw new IllegalArgumentException("bad day");

            /* Lengthen year and month numbers to avoid overflow */
        }
        long iyL = (long) iy;
        long imL = (long) im;

        /* Perform the conversion */
        return (double) ((1461L * (iyL - (12L - imL) / 10L + 4712L)) / 4L + (306L * ((imL + 9L) % 12L) + 5L) / 10L
                - (3L * ((iyL - (12L - imL) / 10L + 4900L) / 100L)) / 4L + (long) id - 2399904L);
    }

    // void slaDjcl ( double djm, int *iy, int *im, int *id, double *fd, int *j)
    private static int[] slaDjcl(double djm) {
        /*
         ** - - - - - - - - s l a D j c l - - - - - - - -
         **
         ** Modified Julian Date to Gregorian year, month, day, and fraction of a day.
         **
         ** Given: djm double Modified Julian Date (JD-2400000.5)
         **
         ** Returned: *iy int year *im int month *id int day *fd double fraction of day
         ** *j int status: -1 = unacceptable date (before 4701BC March 1)
         **
         ** The algorithm is derived from that of Hatcher 1984 (QJRAS 25, 53-55).
         **
         ** Defined in slamac.h: dmod
         **
         ** Last revision: 12 March 1998
         **
         ** Copyright P.T.Wallace. All rights reserved.
         */
        // System.out.println("[slaDjcl] " + djm);
        // double f, d;
        // double f;

        /* Check if date is acceptable */
        if ((djm <= -2395520.0) || (djm >= 1e9)) {
            // {
            // *j = -1;
            // return;
            throw new IllegalArgumentException("MJD out of valid range");
            // }
            // else
            // {
            // *j = 0;

            /* Separate day and fraction */
            // f = dmod ( djm, 1.0 );
            // if ( f < 0.0 ) f += 1.0;
            // d = djm - f;
            // d = dnint ( d );
        }
        long ld = (long) djm;

        /* Express day in Gregorian calendar */
        // jd = (long) dnint ( d ) + 2400001;
        long jd = ld + 2400001L;
        long n4; 
        n4 = 4L * (jd + ((6L * ((4L * jd - 17918L) / 146097L)) / 4L + 1L) / 2L - 37L);
        long nd10 = 10L * (((n4 - 237L) % 1461L) / 4L) + 5L;
        // *iy = (int) (n4/1461L-4712L);
        // *im = (int) (((nd10/306L+2L)%12L)+1L);
        // *id = (int) ((nd10%306L)/10L+1L);
        // *fd = f;
        int[] ret = new int[3];
        ret[0] = (int) (n4 / 1461L - 4712L);
        ret[1] = (int) (((nd10 / 306L + 2L) % 12L) + 1L);
        ret[2] = (int) ((nd10 % 306L) / 10L + 1L);

        // *j = 0;
        return ret;
        // }
    }

    /**
     * Obtain the Date object representing the DMF Epoch of January 1st, 1980.
     *
     * @return Date object.
     */
    public static Date getDMFEpoch() {
        final Calendar cal = Calendar.getInstance(UTC);
        cal.set(1980, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
}
