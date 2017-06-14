package hg.util;

import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.Hotel;
import hg.db.User;
import hg.html5.Menu;
import hg.html5.MenuItem;
import hg.html5.Page;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/* ========================================================================= */
/**
 * Static functions to make life a little easier.
 */
/* ========================================================================= */
public class Util
    {
    private static NumberFormat     _nfdDot   = NumberFormat.getInstance(Locale.US);
    private static NumberFormat     _nfdComma = NumberFormat.getInstance(Locale.FRANCE);
    private static SimpleDateFormat _DtoISO   = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat _DtoISOpacked = new SimpleDateFormat("yyyyMMdd");
    
    /* --------------------------------------------------------------------- */
    /**
     * Add days to a date.
     * 
     * @param target The date to alter.
     * @param noDays How many days to add (or subtract if negative).
     * @return A new instance of Date.
     */
    /* --------------------------------------------------------------------- */
    static public Date DateAddDays(Date target, int noDays) 
        {
        GregorianCalendar gc = DateToGregorian(target);
        gc.add(GregorianCalendar.DAY_OF_MONTH, noDays);
        return gc.getTime();
        }
    
    
    /*  -------------------------------------------------------------------- */
    /** 
     * Gets a session from the supplied request, and from the session
     *  a logged-in user.
     * 
     * @param request The request to be examined.
     * @return Null if a user could not be found for the session.
     */
    /*  -------------------------------------------------------------------- */
    static public User getUserForSession(HttpServletRequest request)
        {
        HttpSession sess = request.getSession(false);
        if (sess == null)
            {
            return null;
            }
        
        return (User)sess.getAttribute(Sess.USER);
        }
    
    
    /*  -------------------------------------------------------------------- */
    /** 
     * Get a user for the session which also has administrator access.
     *
     * @param request The request to be examined.
     * @return Null if the user which logged in is not an administrator.
     */
    /*  -------------------------------------------------------------------- */
    static public User getAdminForSession(HttpServletRequest request) 
        {
        User usr = getUserForSession(request);

        if ((usr == null   ) ||
            (!usr.isAdmin()))
            {
            return null;
            }
        return usr;
        }
    
    /*  -------------------------------------------------------------------- */
    /** 
     * Make sure that a string is neither null nor empty.
     *
     * @param s The string to examine.
     * @return True if the string is not null and not empty.
     */
    /*  -------------------------------------------------------------------- */
    static public boolean StringOK(String s)
        {
        boolean result = true;
        if ((s == null  ) ||
            (s.isEmpty()))
            {
            result = false;
            }

        return result;
        }
    
    /*  -------------------------------------------------------------------- */
    /** 
     * Make sure that the string can be parsed as an integer.
     *
     * @param s The string to parse.
     * @return True if the string represents an integer.
     */
    /*  -------------------------------------------------------------------- */
    static public boolean IntOK(String s)
        {
        // Make sure we actually have a string.
        if (!StringOK(s)) 
            { 
            return false; 
            }

        boolean result = true;
        try
            {
            Integer.parseInt(s);
            }
        catch (NumberFormatException discard)
            {
            result = false;
            }

        return result;
        }
    

    /* --------------------------------------------------------------------- */
    /**
     * Adds elements which are common to all pages. These are the caption, 
     * the menu and the user preferences dialog. Will add the javascripts to
     * support the dialog and some javascript variables.
     * @param p The page to add to.
     * @param usr A logged in user.
     * @param hot The hotel configuration, for the timezone.
     * @param pID Which page is being created.
     */
    /* --------------------------------------------------------------------- */
    static public void CommonElementsOnPages(Page p, User usr, Hotel hot, int pID) 
        {
        // CSS links
        String cssPath = usr.getStyle();
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(cssPath + "jq/jquery-ui.min.css");
        p.AddCSSLink(cssPath + "nh.css");
        
        // Javascript links
        p.AddJSLink("scripts/jq/jquery-2.1.1.min.js");
        p.AddJSLink("scripts/jq/jquery-ui.min.js");
        p.AddJSLink("scripts/nh.js");
        p.AddJSLink("scripts/userpreferences.js");
        
        // Caption and date+time
        Locale loc = usr.getLocale();
        GregorianCalendar greg = hot.getDateTime();
        
        int gcY = greg.get(GregorianCalendar.YEAR);
        int gcM = greg.get(GregorianCalendar.MONTH);
        int gcD = greg.get(GregorianCalendar.DAY_OF_MONTH);
        int gch = greg.get(GregorianCalendar.HOUR_OF_DAY);
        int gcm = greg.get(GregorianCalendar.MINUTE);
        int gcs = greg.get(GregorianCalendar.SECOND);
        GregorianCalendar greg2 = new GregorianCalendar(gcY, gcM, gcD, gch, gcm, gcs);
        
        Date current = greg2.getTime();
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, loc);
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.MEDIUM, loc);
        String dt_formatted = df.format(current) + " " + tf.format(current);
        p.setCaption(usr.Txt(Loc.APPTITLELONG) + "   " + dt_formatted);
        
        // --- THE MENU ---
        // USER
        Menu m = p.getMenu();
        MenuItem mUser = m.AddItem(usr.Txt(Loc.USER), null);
        mUser.AddSubItem(usr.Txt(Loc.PREFERENCES), "javascript:OpenPreferences()");
        mUser.AddSubItem(usr.Txt(Loc.LOGOUT), Paths.LOGOUT);
        
        // ADMIN
        if (usr.isAdmin()) 
            {
            MenuItem mAdmin = m.AddItem(usr.Txt(Loc.MANAGER), null);
            if (pID != PageID.PRICELISTSPAGE) 
                {
                mAdmin.AddSubItem(usr.Txt(Loc.PRICELISTS), Paths.PRICELISTS);
                }
            if (pID != PageID.CUSTOMERS) 
                {
                mAdmin.AddSubItem(usr.Txt(Loc.CUSTOMERS), Paths.CUSTOMERS);
                }
            if (pID != PageID.ROOMSPAGE) 
                {
                mAdmin.AddSubItem(usr.Txt(Loc.ROOMS), Paths.ROOMS);
                }
            if (pID != PageID.USERSPAGE) 
                {
                mAdmin.AddSubItem(usr.Txt(Loc.USERS), Paths.USERS);
                }
            if (pID != PageID.HOTELCONFIG) 
                {
                mAdmin.AddSubItem(usr.Txt(Loc.HCONFIG), Paths.HOTELCONFIG);
                }
            }
        
        // BACK TO MAINPAGE
        if (pID != PageID.MAINPAGE) 
            {
            m.AddItem(usr.Txt(Loc.MAINPAGE), Paths.MAIN);
            }
        
        // HELP
        m.AddItem(usr.Txt(Loc.HELP), "javascript:Help(" + pID + ")");
        
        // JAVASCRIPT VARIABLES FOR WIDELY USED LOCALIZED TEXT
        p.AddJSCode("var del = '"           + usr.Txt(Loc.DELETE)        + "';\n");
        p.AddJSCode("var cancel = '"        + usr.Txt(Loc.CANCEL)        + "';\n");
        p.AddJSCode("var save = '"          + usr.Txt(Loc.SAVE)          + "';\n");
        p.AddJSCode("var help = '"          + usr.Txt(Loc.HELP)          + "';\n");
        p.AddJSCode("var err = '"           + usr.Txt(Loc.ERROR)         + "';\n");
        p.AddJSCode("var errSave = '"       + usr.Txt(Loc.ERRSAVE)       + "';\n");
        p.AddJSCode("var errTxt = '"        + usr.Txt(Loc.ERROR)         + "';\n");
        p.AddJSCode("var errNoContact = '"  + usr.Txt(Loc.ERR_NOCONTACT) + "';\n");
        p.AddJSCode("var dtup = '"          + usr.Txt(Loc.PREFERENCES)   + "';\n");
        
        // Add <div> so jquery can create the dialog.
        p.AddDiv("upform");
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * Convert a string into a double. 
     * 
     * @param src String to convert. Decimal seperator can be '.' or ','. If
     * there are thousand seperators present this WILL reuturn a nonsense value.
     *
     * @param srcOK This will be set to true if the string could be converted.
     * 
     * @return The value of the string.
     */
    /* --------------------------------------------------------------------- */
    static public double StrToDouble(String src, Boolean srcOK) 
        {
        if (! StringOK(src)) 
            {
            srcOK = false;
            return 0;
            }
        
        // . or ,
        int dotIndex   = src.indexOf('.');
        int commaIndex = src.indexOf(',');
        
        srcOK = true;
        double val = 0;
        try
            {
            if (dotIndex > -1) 
                {
                val = (_nfdDot.parse(src)).doubleValue();
                }
            else if (commaIndex > -1) 
                {
                val = (_nfdComma.parse(src)).doubleValue();
                }
            // No decimal seperator found.
            else 
                {
                val = (_nfdDot.parse(src)).doubleValue();
                }
            }
        catch (ParseException ex)
            {
            srcOK = false;
            }
        
        return val;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Takes a packed date and converts it into a GregorianCalendar object.
     * 
     * @param pd The packed date. The format is yyyymmdd.
     */
    /* --------------------------------------------------------------------- */
    static public GregorianCalendar packedDateToGregorian(String pd) 
        {
        String syear = pd.substring(0, 4);
        String smon  = pd.substring(4, 6);
        String sday  = pd.substring(6, 8);
        int iyear = Integer.parseInt(syear);
        int imon  = Integer.parseInt(smon);
        int iday  = Integer.parseInt(sday);
        // The Date object index is zero based, thus jan. = 0.
        imon--;
        
        return new GregorianCalendar(iyear, imon, iday);
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * Create a list of countries, suitable for displaying in a drop-down.
     * 
     * @param userLocale Used to localize the country names.
     * @return List of countries in alphabetical order.
     */
    /* --------------------------------------------------------------------- */
    static public Country[] CreateCountryList(Locale userLocale) 
        {
        String[] isoCodes = Locale.getISOCountries();
        Country[] countries = new Country[isoCodes.length];
        
        for (int i = 0; i < isoCodes.length; i++) 
            {
            Country c  = new Country();
            Locale loc = new Locale("", isoCodes[i]);
            
            c._countryName    = loc.getDisplayCountry(userLocale);
            c._countryISOCode = isoCodes[i];
            
            countries[i] = c;
            }
        
        CountryCompare ccomp = new CountryCompare();
        Arrays.sort(countries, ccomp);
        
        return countries;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Convert a Date object into a GregorianCalendar.
     * 
     * @param dateSrc Date to convert.
     * @return GregorianCalendar object.
     */
    /* --------------------------------------------------------------------- */
    public static GregorianCalendar DateToGregorian(Date dateSrc)
        {
        GregorianCalendar result = new GregorianCalendar();
        result.setTime(dateSrc);
        return result;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Convert a Date object into a string representing an ISO date, 
     * i.e. year-month-day
     */
    /* --------------------------------------------------------------------- */
    public static String DateToIso(Date dsrc) 
        {
        return _DtoISO.format(dsrc);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Convert a Date into packed ISO, i.e. no hyphens.
     */
    /* --------------------------------------------------------------------- */
    public static String DateToIsoPacked(Date dsrc) 
        {
        return _DtoISOpacked.format(dsrc);
        }

    public static int DifferenceInDays(Date d1, Date d2)
        {
        GregorianCalendar greg1 = DateToGregorian(d1);
        GregorianCalendar greg2 = DateToGregorian(d2);
        long millisecDiff = Math.abs(greg2.getTimeInMillis() - greg1.getTimeInMillis());
        return (int) (millisecDiff / 86400000);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Returns the localization file for the jQuery Datepicker.
     */
    /* --------------------------------------------------------------------- */
    public static String jqDatepickerLocalizationFile(String lang)
        {
        String jqFile = "";
        if (lang.compareTo("is") == 0) 
            {
            jqFile = "datepicker-is.js";
            }
        return jqFile;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Create the parameters needed for the "new Date()" call in javascript.
     * @param theDate The date we extract the parameters from.
     * @return A string of the form "year,month,day".
     */
    /* --------------------------------------------------------------------- */
    public static String jqParamsForDate(GregorianCalendar theDate) 
        {
        String year  = Integer.toString(theDate.get(GregorianCalendar.YEAR));
        String month = Integer.toString(theDate.get(GregorianCalendar.MONTH));
        String day   = Integer.toString(theDate.get(GregorianCalendar.DAY_OF_MONTH));
        String params = year + "," + month + "," + day;
        return params;
        }
    
    }//END-OF-CLASS Util


/* --------------------------------------------------------------------- */
/* --------------------------------------------------------------------- */
/**
 * Used by the 'CreateCountryList()' method to sort the countries.
 */
/* --------------------------------------------------------------------- */
/* --------------------------------------------------------------------- */
class CountryCompare implements Comparator 
    {

    @Override
    public int compare(Object o1, Object o2)
        {
        Country c1 = (Country)o1;
        Country c2 = (Country)o2;

        return c1._countryName.compareTo(c2._countryName);
        }
    }