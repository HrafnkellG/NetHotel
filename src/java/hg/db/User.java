package hg.db;

import hg.cons.CSS;
import hg.cons.LangStyleConst;
import hg.cons.Loc;
import hg.cons.Paths;
import hg.html5.Dialog;
import hg.html5.IDropDown;
import hg.html5.TableRow;
import hg.util.AppLog;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/* ========================================================================= */
/**
 * An administrator or employee of the hotel.
 */
/* ========================================================================= */
public class User
    {
    String  _lang;
    int     _identifier;
    String  _name;
    String  _key;
    boolean _isAdmin;
    String  _style;
    
    private ResourceBundle _locText;
    private ResourceBundle _locHelp;
    
    /* --------------------------------------------------------------------- */
    /**
     * CONSTRUCTOR
     * @param lang Which language the user prefers. 
     */
    /* --------------------------------------------------------------------- */
    User(String lang) 
        {
        _lang = lang;
        Locale chosenLanguage = new Locale(_lang);
        _locText = ResourceBundle.getBundle(Paths.LOCALIZED_TEXT, chosenLanguage);
        _locHelp = ResourceBundle.getBundle(Paths.LOCALIZED_HELP, chosenLanguage);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * The path to the chosen style.
     */
    /* --------------------------------------------------------------------- */
    public String getStyle() 
        {
        return _style;
        }
    
    public Locale getLocale() 
        {
        return new Locale(_lang);
        }
    
    public int getIdentifier() 
        {
        return _identifier;
        }
    
    public boolean isAdmin() 
        {
        return _isAdmin;
        }
    
    /* --------------------------------------------------------------------- */
    /** Retrieve a localized text-string.
     * 
     * @param index An index into the resource-bundle which holds the strings.
     * @return The localized string. Has "[?]" if nothing could be found for 
     * the given index.
     */
    /* --------------------------------------------------------------------- */
    public String Txt(String index) 
        {
        String res;
        try 
            {
            res = _locText.getString(index);
            }
        catch (MissingResourceException ms) 
            {
            AppLog.Instance().Error(ms.getMessage());
            res = "[?]";
            }
        return res;
        }
    
    
    /* --------------------------------------------------------------------- */
    /** Retrieve a localized text-string from the help file.
     * 
     * @param index An index into the resource-bundle which holds the strings.
     * @return The localized string. Has "[?]" if nothing could be found for 
     * the given index.
     */
    /* --------------------------------------------------------------------- */
    public String Hlp(String index) 
        {
        String res;
        try 
            {
            res = _locHelp.getString(index);
            }
        catch (MissingResourceException ms) 
            {
            AppLog.Instance().Error(ms.getMessage());
            res = "[?]";
            }
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show the user in a dialog. Users can not change their name or their
     * access level, only an admin can do that.
     * @param dlg The dialog to display in.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInDialog(Dialog dlg)
        {
        // 1st. row: PASSWORD
        dlg.AddTextInput(Txt(Loc.PASSWORD), _key, CSS.IUPPASS, 50);
        
        // 2nd. row: LANGUAGE
        IDropDown ddm = dlg.AddDropDown(Txt(Loc.LANGUAGE), CSS.IUPLANG);
        for (String lang_code : LangStyleConst.Languages)
            {
            Locale lang_locale = new Locale(lang_code);
            String lang_name = ResourceBundle.getBundle(Paths.LOCALIZED_TEXT, lang_locale).getString(Loc.LANGUAGENAME);
            Boolean is_selected = false;
            if (lang_code.compareTo(_lang) == 0)
                {
                is_selected = true;
                }
            ddm.AddItem(lang_name, lang_code, is_selected);
            }
        
        // 3rd. row: STYLE
        IDropDown ddmStyle = dlg.AddDropDown(Txt(Loc.STYLE), CSS.IUPSTYLE);
        for (int Index = 0; Index <= LangStyleConst.StylePaths.length - 1; Index++)
            {
            String path = LangStyleConst.StylePaths[Index];
            String text = LangStyleConst.StyleDescriptions[Index];
            Boolean is_selected = false;
            if (_style.compareTo(path) == 0)
                {
                is_selected = true;
                }
            ddmStyle.AddItem(text, path, is_selected);
            }
        }

    /* --------------------------------------------------------------------- */
    /**
     * Display the user in grid. There are 5 columns.
     * @param row Target row.
     * @param accessLevel Descriptive text for the access leve.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRow(TableRow row, String accessLevel)
        {
        // Strip path info from the style string.
        StringTokenizer strTok = new StringTokenizer(_style, "/");
        int tokCount = strTok.countTokens();
        for (int Index = 1; Index < tokCount; Index++)
            {
            strTok.nextToken();
            }
        String style = strTok.nextToken();
        
        row.setCell(0, CSS.CCELL_NAME, _name);
        row.setCell(1, CSS.CCELL_PASSW, _key);
        row.setCell(2, CSS.CCELL_ACCESS, accessLevel);
        row.setCell(3, CSS.CCELL_LANG, _lang);
        row.setCell(4, CSS.CCELL_STYLE, style);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Fill a dialog with un-initialized fields.
     * @param dlg Target dialog.
     */
    /* --------------------------------------------------------------------- */
    public void EmptyDialog(Dialog dlg)
        {
        // Row 1. name
        dlg.AddTextInput(Txt(Loc.NAME), null, "username", 50);
        
        // Row 2. password
        dlg.AddTextInput(Txt(Loc.PASSWORD), null, "userpass", 50);
        
        // Row 3. access level
        IDropDown ddm = dlg.AddDropDown(Txt(Loc.ACCESS), "useraccess");
        ddm.AddItem(Txt(Loc.USER),          "1", false);
        ddm.AddItem(Txt(Loc.ADMINISTRATOR), "2", false);
        
        // Row 4. language
        IDropDown ddmLang = dlg.AddDropDown(Txt(Loc.LANGUAGE), "userlang");
        for (String lang_code : LangStyleConst.Languages)
            {
            Locale lang_locale = new Locale(lang_code);
            String lang_name = ResourceBundle.getBundle(Paths.LOCALIZED_TEXT, lang_locale).getString(Loc.LANGUAGENAME);
            ddmLang.AddItem(lang_name, lang_code, false);
            }
        
        // Row 5. style
        IDropDown ddmStyle = dlg.AddDropDown(Txt(Loc.STYLE), "userstyle");
        for (int Index = 0; Index <= LangStyleConst.StylePaths.length - 1; Index++)
            {
            String path = LangStyleConst.StylePaths[Index];
            String text = LangStyleConst.StyleDescriptions[Index];
            ddmStyle.AddItem(text, path, false);
            }
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show the user in a dialog where all the fields can be edited.
     * 
     * @param dlg Target dialog.
     * @param admin The administrator requesting the edit. Some texts must
     * be localized to his language, which can be different from the target user.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInDialogEdit(Dialog dlg, User admin)
        {
        // Row 1. name
        dlg.AddTextInput(admin.Txt(Loc.NAME), _name, "username", 50);
        
        // Row 2. password
        dlg.AddTextInput(admin.Txt(Loc.PASSWORD), _key, "userpass", 50);
        
        // Row 3. access level
        IDropDown ddm = dlg.AddDropDown(admin.Txt(Loc.ACCESS), "useraccess");
        boolean selected = false;
        if (!_isAdmin) { selected = true; }
        ddm.AddItem(admin.Txt(Loc.USER),          "1", selected);
        selected = false;
        if (_isAdmin) { selected = true; }
        ddm.AddItem(admin.Txt(Loc.ADMINISTRATOR), "2", selected);
        
        // Row 4. language
        IDropDown ddmLang = dlg.AddDropDown(admin.Txt(Loc.LANGUAGE), "userlang");
        for (String lang_code : LangStyleConst.Languages)
            {
            Locale lang_locale = new Locale(lang_code);
            String lang_name = ResourceBundle.getBundle(Paths.LOCALIZED_TEXT, lang_locale).getString(Loc.LANGUAGENAME);
            boolean isSelected = false;
            if (lang_name.compareTo(_lang) == 0) 
                {
                isSelected = true;
                }
            ddmLang.AddItem(lang_name, lang_code, isSelected);
            }
        
        // Row 5. style
        IDropDown ddmStyle = dlg.AddDropDown(admin.Txt(Loc.STYLE), "userstyle");
        for (int Index = 0; Index <= LangStyleConst.StylePaths.length - 1; Index++)
            {
            String path = LangStyleConst.StylePaths[Index];
            String text = LangStyleConst.StyleDescriptions[Index];
            boolean isSelected = false;
            if (path.compareTo(_style) == 0) 
                {
                isSelected = true;
                }
            ddmStyle.AddItem(text, path, isSelected);
            }
        }

    /* --------------------------------------------------------------------- */
    /**
     * The name of the user.
     */
    /* --------------------------------------------------------------------- */
    public String getName()
        {
        return _name;
        }
    
    }//END-OF-CLASS User
