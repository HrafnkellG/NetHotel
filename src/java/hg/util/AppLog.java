package hg.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*  ======================================================================== */
/** Provides general logging for the application. This includes errors,
 *  warnings and activities. <p>
 *  Follows the singleton pattern.
 * 
 * @author hg
 */
/*  ======================================================================== */
public class AppLog
    {
    /** The only allowed instance of AppLog. */
    private static final AppLog _logger = new AppLog();

    /** The name of the log-file. */
    private static String _logName = "NetHotel.log";

    /** Formats the timestamp used in the messages. */
    SimpleDateFormat _dateFormat;
    
    /*  -------------------------------------------------------------------- */
    /** A private constructor for this singleton.
     * 
     */
    /*  -------------------------------------------------------------------- */
    private AppLog()
        {
        String propDir  = System.getProperty("user.dir");
        _logName = propDir + File.separator + _logName;
        _dateFormat = new SimpleDateFormat("'['yyyy.MM.dd HH:mm:ss']'");

        // We record that logging has started.
        //
        LogMessage("====== LOGGING ENABLED ======");
        }
    
    /*  -------------------------------------------------------------------- */
    /** Fetch an instance of the AppLog class.
     * 
     * @return The only allowed instance for this class.
     */
    /*  -------------------------------------------------------------------- */
    public static AppLog Instance()
        {
        return _logger;
        }
    
    /*  -------------------------------------------------------------------- */
    /** Writes a message to the log-file.
     * 
     * @param message The text which explains the error.
     */
    /*  -------------------------------------------------------------------- */
    private String LogMessage(String message)
        {
        // Create a time stamp.
        // The form is: [yyyy.MM.dd HH:mm:ss]
        //
        Date dNow = new Date();
        String stamp = _dateFormat.format(dNow);
        
        String errMsg = "";
        // Now append stamp & message to the log-file.
        //
        try
            {
            FileWriter fw = new FileWriter(_logName, true);
            BufferedWriter buffer = new BufferedWriter(fw);
            buffer.write(stamp + " " + message);
            buffer.newLine();
            buffer.close();
            }
        catch (Exception ex)
            {
            errMsg = ex.getMessage();
            }
        return errMsg;
        }


    /*  -------------------------------------------------------------------- */
    /** Writes an error message to the log.
     *
     * @param errMsg The error to be logged.
     */
    /*  -------------------------------------------------------------------- */
    public void Error(String errMsg)
        {
        LogMessage("ERROR: " + errMsg);
        }

    /*  -------------------------------------------------------------------- */
    /** Log info about some activity which is neither an error or a warning.
     *
     * @param infoMsg Description of event or activity.
     */
    /*  -------------------------------------------------------------------- */
    public String Info(String infoMsg)
        {
        // TODO: Comment out for release version, prevents the log file from
        // filling with activity messages.
        return LogMessage("INFO:  " + infoMsg);
        }


    /*  -------------------------------------------------------------------- */
    /** Write a warning message to the log.
     *
     * @param warningMsg Description of warning.
     */
    /*  -------------------------------------------------------------------- */
    public void Warning(String warningMsg)
        {
        // TODO: Comment out for release version, prevents the log file from
        // filling with warning messages.
        LogMessage("WARNING:  " + warningMsg);
        }



    }
