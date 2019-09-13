/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2019.                            (c) 2019.
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
 * @author adriand
 * 
 * @version $Revision$
 * 
 * 
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;


/**
 * Utility class that initializes the logger for an application.
 * <h3>Usage</h3>
 * <pre><br>public class HelloWorld<br>{</pre>
 * <pre>&nbsp;&nbsp;&nbsp; 
 * static Logger logger = Logger.getLogger(HelloWorld.class);
 * <br><br>&nbsp;&nbsp;&nbsp; public static void main(String[] args)
 * <br>&nbsp;&nbsp;&nbsp; {
 * <br>
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; try
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {
 * <br>&nbsp;&nbsp;  &nbsp;&nbsp;  &nbsp;&nbsp; &nbsp;&nbsp;  LogArgUtil.initialize(Test.class.getPackage().getName(), args);
 * <br>
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; logger.debug("Debug message");
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; logger.info("Info message");
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp; &nbsp;&nbsp; logger.warn("Warning message");
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; logger.error("Error message");
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; logger.fatal("Fatal message");
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; }
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; catch (IOException e)
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; {
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; System.err.println("Can't create the log file");
 * <br>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; }
 * <br>
 * <br>&nbsp;&nbsp;&nbsp; }
 * <br>}
 * </pre>
 *  
 * <h4> Description </h4>
 * 
 * <code>LogArgUtil</code> is supposed to be called once per VM, typically in 
 * the main method. Subsequent calls lead to duplication of the logging messages.
 * Clients can call <code>isInitialized()</code> to check whether 
 * <code>LogArgUtil</code> has been intialized already.<p></p>
 * Logging configuration can be dynamically changed at runtime using a 
 * log4j configuration file. The name of the file is 
 * <code><b><i>cfgRootName</i>.log4j.config</b></code> where 
 * <code><b><i>cfgRootName</i></b></code> is
 * usually the name of the application (project) and should reside in the same
 * directory with other config files (<code><b>$DEFAULT_CONFIG_DIR</b></code>)
 * and should be set with <code>setCfgRootName()</code> before calling 
 * <code>initialize()</code>.
 * 
 * <h4> Logging Flags </h4>
 * LogArgUtil can process the following command line:
 * <tt><b>
 * [ -q|--quiet ] [ -v|--verbose ] [ -d|--debug ] [ --logfile=filename ]
 * </b></tt>
 * <br>where:
 * <ul>
 * <li><tt><b>-q|--quiet</b></tt>: Quiet mode. Print only the error messages to 
 * user's console.
 * <li><tt><b>-v|--verbose</b></tt>: Verbose mode. Print progress information 
 * and error messages to user's console.
 * <li><tt><b>-d|--debug</b></tt>: Debug mode. Print error messages and extra 
 * debugging information to user's console.
 * <li><tt><b>--logfile=filename</b></tt>: Specifies a destination file for all 
 * the logging information. Progress information and error messages are always 
 * logged in the logging file regardless of the logging flags in the command 
 * line. When the application is invoked in the debug mode (-d flag) debug 
 * messages are also logged in the logging file.
 * </ul>
 * 
 *  
 */
public class LogArgUtil {
    // logging related flags in the command line
    public static final String VERBOSE_SHORT = "v";
    public static final String VERBOSE_LONG = "verbose";
    public static final String DEBUG_SHORT = "d";
    public static final String DEBUG_LONG = "debug";
    public static final String QUIET_SHORT = "q";
    public static final String QUIET_LONG = "quiet";
    public static final String LOG = "log";

    public static final Level DEFAULT_CONSOLE_LOGGING_LEVEL = Level.WARN;
    public static final Level DEFAULT_FILE_LOGGING_LEVEL = Level.INFO;

    /**
     * Returns the logging related command line flags and their description for 
     * the clients to use it in their usage methods.
     * @return Array of Strings where the first entry represents the logging 
     *     related command line flags ("<tt>[-q|--quiet] [-v|--verbose] [-d|--debug] 
     *     [--log=log file]</tt>") and the subsequent entries are the description 
     *     of these flags.
     */
    public static String[] getUsage() {
        return new String[] {
            "[-q|--quiet] [-v|--verbose] [-d|--debug] [--log=<log file>]",
            "-q|--quiet   : Quiet mode prints only error messages",
            "-v|--verbose : Verbose mode prints progress and error messages",
            "-d|--debug   : Debug mode prints all the logging messages",
            "--log        : Log all the messages to <log file>"
        };
    }

    /**
     * Initializes the log4j for logging. This method is
     * to be called by an application at the beginning of the execution. 
     * According to the CADC guidelines it sets the overall logging level to 
     * WARN and logging level of the logger specified by loggerName to the level 
     * specified by the command line arguments:<ul>
     *   <li>-d or -debug -&gt; DEBUG level</li>
     *   <li>-v or -verbose -&gt; INFO level</li>
     *   <li>empty or null -&gt; WARN level</li>
     *   <li>-q or -quiet -&gt; ERROR level</li>
     * </ul>
     * This method also defines the formats of the message. If --log=fileName
     * option is present in the args, logging messages are sent to both the
     * console and the specified log file, otherwise the console is the only
     * logging destination. 
     * 
     * <p>Note: The most verbose flag takes precedence when multiple
     * conflicting options are present in the command line args. 
     * @param loggerName the name of the logger that the args options apply
     *     to. This is usually the name of a class, package or the name of any 
     *     ancestor of a class or package. It can't be null.
     * @param args command line arguments as presented in the description of
     *     the class. Can be null if there are no logging command line flags.
     * @exception IOException problems when trying to log to a file.
     * @throws UsageException if there is a disagreement with the way the file
     *                        is handled.
     */
    public static void initialize(
        String loggerName,
        String[] args)
        throws IOException, UsageException {
        String[] loggerNames = { loggerName };
        initialize(loggerNames, args);
    }

    /**
     * Similar to initialize(String, String[]) method above except that 
     * this method applies the argument logging options to a list of loggers
     * specified in the loggerNames array
     * @param loggerNames names of the loggers to apply the logging options of
     *     in the args to.
     * @param args command line argument options as presented in the 
     *     description of the class. 
     * @throws IOException problems with the log file
     * @throws UsageException if there is a disagreement with the way the file
     *                        is handled.
     */
    public static void initialize(
        String[] loggerNames,
        String[] args)
        throws IOException, UsageException {
        if (args == null) {
            // no flags
            args = new String[0]; // empty string to satisfy argMap
        }

        ArgumentMap argMap = new ArgumentMap(args);

        if (argMap.isSet(LOG)) {
            // we need to log to a file as well
            initialize(loggerNames, args, null);
            // file name in args
        } else {
            initConsoleLogging(loggerNames, argMap);
        }
    }

    /**
     * <p>
     * Initializes the log4j for logging to the console and to a specified file. 
     * This method is to be called by an application at the beginning of the 
     * execution. According to the CADC guidelines it sets the overall logging 
     * level to WARN and defines the formats of the messages.</p> 
     * 
     * <p>At the console, messages from the logger specified by logName are
     * logged according to the flags in the arguments list:<ul>
     *   <li>-d or -debug -&gt; DEBUG level</li>
     *   <li>-v or -verbose -&gt; INFO level</li>
     *   <li>empty or null -&gt; WARN level</li>
     *   <li>-q or -quiet -&gt; ERROR level</li>
     * </ul>
     * However, in the log file, messages from the logName logger are logged at 
     * the DEBUG level when -d or -debug flags are present or at the INFO level 
     * otherwise.  
     * 
     * <p>Note1: If another log file is specified through the args 
     * (--log=logFile option), then this file is used instead of the fileName
     * argument one. 
     * 
     * <p>Note2: The most verbose flag takes precedence when multiple
     * conflicting options are present in the command line args. 
     * @param loggerName the name of the logger that the args options apply
     *     to. This is usually the name of a class, package or the name of any 
     *     ancestor of a class or package. It can't be null.
     * @param args command line arguments as presented in the description of
     *     the class. Can be null if there are no logging command line flags.
     * @param fileName the name of the log file. If another file is specified
     *     in the args through --log option, then fileName is ignored.
     * @throws IOException problems with the log file
     * @throws UsageException if there is a disagreement with the way the file
     *                        is handled.
     */
    public static void initialize(
        String loggerName,
        String[] args,
        String fileName)
        throws IOException, UsageException {
        String[] loggerNames = { loggerName };
        initialize(loggerNames, args, fileName);
    }

    /**
     * Similar to initialize(String, String[], String) method above except that 
     * this method applies the argument logging options to a list of loggers
     * specified in the loggerNames array.
     * 
     * @param loggerNames names of the loggers to apply the logging options of
     *     in the args to.
     * @param args command line argument options as presented in the 
     *     description of the class.
     * @param fileName      The logging file name.
     * @throws IOException problems with the log file
     * @throws UsageException if there is a disagreement with the way the file
     *                        is handled. 
     */
    public static synchronized void initialize(String[] loggerNames, String[] args, String fileName) throws IOException, UsageException {
        if (args == null) {
            // no flags
            args = new String[0]; // empty string to satisfy argMap
        }
        
        ArgumentMap argMap = new ArgumentMap(args);

        if (argMap.isSet(LOG)) {
            // override the file log file name
            fileName = argMap.getValue(LOG);
            if (fileName.equals("true") || fileName.length() < 1) {
                // no valid file name provided
                throw new UsageException("Illegal log file name option");
            }
        }

        // Implementation approach
        // Because messages to the log file are logged at DEBUG and INFO levels
        // only, the debug level of the loggerName logger can only be at these
        // two levels. To simulate the other higher levels for the log messages
        // directed to the console, the console appender filters messages 
        // according to the application flags. For example, if the -q flag is
        // present, the minimum level accepted by the console is ERROR and all
        // the lower messages are thrown away. 

        Level newLevel = null;
        if (argMap.isSet(VERBOSE_SHORT)
            || argMap.isSet(VERBOSE_LONG)
            || argMap.isSet(DEBUG_SHORT)
            || argMap.isSet(DEBUG_LONG)) {
            newLevel = initConsoleLogging(loggerNames, argMap);
        } else {
            newLevel = Level.INFO;
            for (String loggerName : loggerNames) {
                Log4jInit.setLevel(loggerName, newLevel);
            }
        }

        // this is just a convenient way to create the file even if it is on non-exist directory.
        // e.g. /tmp/non/exist/directory/the/log/file
        new FileAppender(new PatternLayout(), fileName);

        boolean append = true;
        Writer fileWriter = new FileWriter(fileName, append);
        for (String loggerName : loggerNames) {
            Log4jInit.setLevel(loggerName, newLevel, fileWriter);
        }
    }

    /**
     * Configures logging to either the console or a file, but not both.
     * 
     * <p>If the ArgumentMap contains a key name 'log', a file logger named with the
     * value of 'log' is created. If the value of 'log' is null or an empty string,
     * a file logger will not be created. If the ArgumentMap doesn't contain
     * a 'log' key, a console logger is created.
     *
     * <p>The log level is set using log level arguments in the ArgumentMap. If the
     * ArgumentMap doesn't contain log level arguments, the default log level
     * for the console logger is WARN, and the default log level for
     * the file logger is INFO.
     *
     * @param loggerNames names of the loggers to apply the logging options of
     *     in the argsMap to.
     * @param argMap command line argument options.
     * @throws IOException problems with the log file
     * @throws UsageException if there is a disagreement with the way the file
     *                        is handled.
     */
    public static void initialize(String[] loggerNames, ArgumentMap argMap)
        throws IOException, UsageException {
        if (argMap.isSet(LOG)) {
            String filename = argMap.getValue(LOG);
            if (filename.equals("true") || filename.isEmpty()) {
                if (filename.isEmpty()) {
                    filename = "empty or zero-length string";
                }
                
                throw new UsageException("Illegal log file name option: " + filename);
            }

            initFileLogging(loggerNames, argMap, filename);
        } else {
            initConsoleLogging(loggerNames, argMap);
        }
    }

    /**
      Returns the name of the log file from a given nameRoot and it can be used
      as the name of the destination log file.
     *
     * @param   nameRoot        The root name. 
      @return name of the logging file in the form: nameRootyyyMMddHHmmss.log
     */ 
    public static String getTimestampLogName(String nameRoot) {
        Date now = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return (nameRoot + "-" + formatter.format(now) + ".log");
    }

    /**
     * Initializes logging to the console.
     * @param loggerNames the names of the loggers (usually names of packages 
     *     or classes or ancestors of packages or classes). Can't be null.
     * @param argMap command line arguments.
     */
    private static synchronized Level initConsoleLogging(String[] loggerNames, ArgumentMap argMap) {
        Level level = DEFAULT_CONSOLE_LOGGING_LEVEL;
        if (argMap.isSet(QUIET_SHORT) || argMap.isSet(QUIET_LONG)) {
            level = Level.ERROR;
        }
        
        if (argMap.isSet(VERBOSE_SHORT) || argMap.isSet(VERBOSE_LONG)) {
            level = Level.INFO;
        }
        
        if (argMap.isSet(DEBUG_SHORT) || argMap.isSet(DEBUG_LONG)) {
            level = Level.DEBUG;
        }

        for (int i = 0; i < loggerNames.length; i++) {
            Log4jInit.setLevel(loggerNames[i], level);
        }

        return level;
    }

    /**
     * Initializes logging to a file.
     * @param loggerNames the names of the loggers (usually names of packages
     *     or classes or ancestors of packages or classes). Can't be null.
     * @param argMap command line arguments.
     */
    private static synchronized void initFileLogging(String[] loggerNames,
            ArgumentMap argMap, String filename)
        throws IOException {
        Level level = DEFAULT_FILE_LOGGING_LEVEL;
        if (argMap.isSet(QUIET_SHORT) || argMap.isSet(QUIET_LONG)) {
            level = Level.ERROR;
        }
        
        if (argMap.isSet(VERBOSE_SHORT) || argMap.isSet(VERBOSE_LONG)) {
            level = Level.INFO;
        }
        
        if (argMap.isSet(DEBUG_SHORT) || argMap.isSet(DEBUG_LONG)) {
            level = Level.DEBUG;
        }

        FileAppender fileAppender = new FileAppender(new PatternLayout(), filename);

        boolean append = true;
        Writer fileWriter = new FileWriter(filename, append);
        for (String loggerName : loggerNames) {
            Log4jInit.setLevel(loggerName, level, fileWriter);
        }
    }
}
