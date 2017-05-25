package hg.db;

import hg.cons.Loc;
import hg.html5.IDropDown;
import hg.html5.Panel;
import hg.util.AppLog;
import hg.util.Util;
import hg.util.hgBoolean;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

/* ========================================================================= */
/**
 * The  hotel settings.
 */
/* ========================================================================= */
public class Hotel
    {
    String  _name;
    String  _addr1;
    String  _addr2;
    String  _city;
    String  _country;
    double  _vat;
    String  _localeStr;
    boolean _overbook;
    Locale  _locale;
    String  _timezone;
    String  _decimalSeparator;
    String  _thousandSeparator;
    
    /* Does the localized currency use fractional values? */
    private boolean _useFractionalValues = false;
    
    /* The ratio between the whole number part and the fractional part. 
       This will nearly always be 0 or 100. */
    private int _ratio = 0;
    
    /* Use to create a localized version of a number. */
    private NumberFormat _formatter;
    
    /* Format date&time to the selected timezone. */
    private SimpleDateFormat _dtformat;
    
    /* The timezone to use. */
    private TimeZone _objTimezone;
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the date & time for the selected timezone.
     */
    /* --------------------------------------------------------------------- */
    public GregorianCalendar getDateTime() 
        {
        return new GregorianCalendar(_objTimezone);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the current date adjusted for timezone and with h:m:s set all to zero.
     */
    /* --------------------------------------------------------------------- */
    public Date getDate() 
        {
        Calendar cal = Calendar.getInstance(); 
        cal.setTimeZone(_objTimezone);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String iso = sdf.format(cal.getTime());
        Date retd = null;
        try { retd = sdf.parse(iso); } catch (ParseException ex) {}
        return retd;
        }
    
    public boolean AllowOverbooking() 
        {
        return _overbook;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Create a formatter for date & time for the supplied timezone.
     */
    /* --------------------------------------------------------------------- */
    void setTimezone(String tmz) 
        {
        if (Util.StringOK(tmz)) 
            {
            _timezone = tmz;
            _objTimezone = TimeZone.getTimeZone(tmz);
            _dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            _dtformat.setTimeZone(_objTimezone);
            }
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show info about the hotel in 2 panels.
     * 
     * @param leftpan Show address information in this panel.
     * @param rightpan The settings go here.
     * @param usr Used for loacalized text.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInPanels(Panel leftpan, Panel rightpan, User usr)
        {
        // The left panel.
        // ------------------------------
        leftpan.AddTextInput(usr.Txt(Loc.NAME), getName(), "hcname", 50);
        leftpan.AddTextInput(usr.Txt(Loc.ADDRESS), getAddr1(), "hcaddr1", 50);
        leftpan.AddTextInput(null, getAddr2(), "hcaddr2", 50);
        leftpan.AddTextInput(usr.Txt(Loc.CITY), getCity(), "hccity", 50);
        leftpan.AddTextInput(usr.Txt(Loc.COUNTRY), getCountry(), "hccountry", 50);
        
        // The right panel.
        // ------------------------------
        rightpan.AddTextInput(usr.Txt(Loc.VAT) + " %", Double.toString(_vat), "hcvat", 0);
        
        // Timezones
        String[] arrTimezoneIds = TimeZone.getAvailableIDs();
        Arrays.sort(arrTimezoneIds);
        int indexSelectedTZ = Arrays.binarySearch(arrTimezoneIds, _timezone);
        IDropDown ddtz = rightpan.AddDropDown(usr.Txt(Loc.TIMEZONE), "hctimezone");
        
        try 
            {
            for (int index = 0; index < arrTimezoneIds.length; index++) 
                {
                boolean selected = false;
                if (indexSelectedTZ == index) 
                    {
                    selected = true;
                    }
                ddtz.AddItem(arrTimezoneIds[index], arrTimezoneIds[index], selected);
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Hotel.ShowInPanles() = " + ex.getMessage());
            }
        
        // Locales
        LocaleCompare lcomp = new LocaleCompare(usr.getLocale());
        Locale[] locs = Locale.getAvailableLocales();
        Arrays.sort(locs, lcomp);
        IDropDown ddloc = rightpan.AddDropDown(usr.Txt(Loc.LOCALE), "hclocale");
        for (Locale l : locs) 
            {
            // Only use those locales which are supported by the Currency class.
            try 
                {
                Currency notUsed = Currency.getInstance(l);
                } 
            catch (Exception ex) 
                {
                continue;
                }
            
            String value = l.getLanguage() + "_" + l.getCountry() + "_" + l.getVariant();
            boolean selected = false;
            if (value.compareTo(_localeStr) == 0) 
                {
                selected = true;
                }
            ddloc.AddItem(l.getDisplayName(usr.getLocale()), value, selected);
            }
        
        // Overbooking
        rightpan.AddCheckbox(usr.Txt(Loc.OVERBOOKING), "hcoverbook", _overbook);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Convert a number representing monetary value into a string.
     * 
     * @param mv The monetary value to convert.
     * @return The value formatted for the locale chosen by the hotel settings.
     */
    /* --------------------------------------------------------------------- */
    public String MoneyToString(long mv) 
        {
        double mvdbl = mv;
        if (_useFractionalValues) 
            {
            mvdbl /= _ratio;
            }
        
        return _formatter.format(mvdbl);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Set the locale and create some objects to support it.
     * @param string The locale description.
     */
    /* --------------------------------------------------------------------- */
    void setLocale(String locDesc)
        {
        _localeStr = locDesc;
        StringTokenizer tokens = new StringTokenizer(locDesc, "_");
        
        String language = tokens.nextToken();
        String country = null;
        String variant = null;
        if (tokens.hasMoreTokens()) 
            {
            country = tokens.nextToken();
            }
        if (tokens.hasMoreTokens()) 
            {
            variant = tokens.nextToken();
            }
        
        // 3 ways a Locale object can be defined.
        // We assume the first parameter, language, will always be present.
        if (country != null && variant != null) 
            {
            _locale = new Locale(language, country, variant);
            }
        else if (country != null) 
            {
            _locale = new Locale(language, country);
            }
        else 
            {
            _locale = new Locale(language);
            }
        
        // Use the locale to create our currency formatter.
        _formatter = NumberFormat.getInstance(_locale);
        
        // Info about the currency for the locale.
        Currency curr = null;
        try 
            {
            curr = Currency.getInstance(_locale);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("Currency.getInstance() " + ex.getMessage());
            }
        int numFractionDigits = curr.getDefaultFractionDigits();
        if (numFractionDigits > 0) 
            {
            _useFractionalValues = true;
            if (numFractionDigits == 1) 
                {
                _ratio = 10;
                }
            else if (numFractionDigits == 2) 
                {
                _ratio = 100;
                }
            else
                {
                _ratio = 1000;
                }
            _formatter.setMinimumFractionDigits(numFractionDigits);
            }
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(_locale);
        _decimalSeparator = String.valueOf(dfs.getDecimalSeparator());
        _thousandSeparator = String.valueOf(dfs.getGroupingSeparator());
        }

    /* --------------------------------------------------------------------- */
    /**
     * Convert a string into a number.
     * 
     * @param moneyValue A string representing a monetary value.
     * @param moneyOK Will be set to true if the conversion can be made.
     * @return The monetary value as an integer.
     */
    /* --------------------------------------------------------------------- */
    public long StringToMoney(String moneyValue, hgBoolean moneyOK)
        {
        moneyOK.setValue(true);
        long result = 0;
        try 
            {
            if (_useFractionalValues) 
                {
                // The escape character must be used in front of the decimal
                // separator char. Reason: split() takes regular expressions,
                // the dot char means "any char". So if the decimal separator
                // is a dot it must be escaped.
                String[] wholeFraction = moneyValue.split("\\" + _decimalSeparator);
                // Then all grouping characters (thousand) are removed from
                // the whole part.
                String swhole = wholeFraction[0].replaceAll("\\" + _thousandSeparator, "");
                long whole = Long.parseLong(swhole);
                whole = whole * _ratio;
                // Is there a fraction?
                if (wholeFraction.length == 2) 
                    {
                    long fraction = Long.parseLong(wholeFraction[1]);
                    whole = whole + fraction;
                    }
                result = whole;
                }
            else 
                {
                // Not using fractional values, so convert straight to long.
                String mv = moneyValue.replaceAll("\\" + _thousandSeparator, "");
                result = Long.parseLong(mv);
                }
            } 
        catch (Exception ex) 
            {
            moneyOK.setValue(false);
            }
        
        return result;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Format a date to the locale chosen for the hotel, does not include the time.
     * 
     * @param target Date to format.
     * @return Date formatted for the chosen locale.
     */
    /* --------------------------------------------------------------------- */
    public String DateToString(Date target)
        {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, _locale);
        return df.format(target);
        }

    public String getName()
        {
        return _name;
        }

    public String getAddr1()
        {
        return _addr1;
        }

    public String getAddr2()
        {
        return _addr2;
        }

    public String getCity()
        {
        return _city;
        }

    public String getCountry()
        {
        return _country;
        }

    public String getVatAsString()
        {
        return _formatter.format(_vat);
        }
    
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    /**
     * Used in 'ShowInPanels()' for the Arrays.Sort() function.
     */
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    private class LocaleCompare implements Comparator 
        {
        // Locale to use in the comparison.
        private Locale _loc;
        
        /* CONSTRUCTOR */
        public LocaleCompare(Locale loc) 
            {
            _loc = loc;
            }

        @Override
        public int compare(Object o1, Object o2)
            {
            Locale loc1 = (Locale)o1;
            Locale loc2 = (Locale)o2;
            
            String strIDOne = loc1.getDisplayName(_loc);
            String strIDTwo = loc2.getDisplayName(_loc);
            
            return strIDOne.compareTo(strIDTwo);
            }
        }
    
    }//END-OF-CLASS Hotel
