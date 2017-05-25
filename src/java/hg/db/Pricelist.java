package hg.db;

import hg.cons.Loc;
import hg.html5.Dialog;
import hg.intf.IPriceItem;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

/* ========================================================================= */
/**
 * A price list for the hotel.
 */
/* ========================================================================= */
public class Pricelist
    {
    int         _id;
    String      _usercreated;
    Timestamp   _whencreated;
    String      _name;
    Date        _periodstart;
    Date        _periodend;
    ArrayList<IPriceItem> _items = new ArrayList();

    /* --------------------------------------------------------------------- */
    /**
     * Get the database unique identifier.
     */
    /* --------------------------------------------------------------------- */
    public String getDBID()
        {
        return Integer.toString(_id);
        }

    public ArrayList<IPriceItem> getItems()
        {
        return _items;
        }

    /* --------------------------------------------------------------------- */
    /**
     * The caption is the period over which it is active followed by the name.
     */
    /* --------------------------------------------------------------------- */
    public String getCaption()
        {
        return _periodstart.toString() + " ► " + _periodend.toString() + "     " + _name;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the name of the list.
     */
    /* --------------------------------------------------------------------- */
    public String getName() 
        {
        return _name;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Add an item to the price-list.
     * @param item The item to add.
     */
    /* --------------------------------------------------------------------- */
    void AddItem(PricelistItem item)
        {
        _items.add(item);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Fill a dialog with fields for a price-list that have default values.
     * Thus, the name is empty, the start date is today and the end date is 
     * tomorrow.
     * 
     * @param dlg Target dialog.
     * @param usr Logged in user, for localized text.
     */
    /* --------------------------------------------------------------------- */
    public void EmptyDialog(Dialog dlg, User usr)
        {
        // Name
        dlg.AddTextInput(usr.Txt(Loc.NAME), null, "plname", 50);
        
        // Active period, start
        dlg.AddJQDPInput(usr.Txt(Loc.ACTIVEFROM), "", "dpperiodstart");
        dlg.AddJQDPInput(usr.Txt(Loc.ACTIVETO), "", "dpperiodend");
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Show the list in a dialog for editing.
     * 
     * @param dlg The target dialog.
     * @param usr The user requesting this, nedded for localized text.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInDialog(Dialog dlg, User usr)
        {
        // Name
        dlg.AddTextInput(usr.Txt(Loc.NAME), _name, "plname", 50);
        
        // Active period, start
        dlg.AddJQDPInput(usr.Txt(Loc.ACTIVEFROM), "", "dpperiodstart");
        dlg.AddJQDPInput(usr.Txt(Loc.ACTIVETO), "", "dpperiodend");
        }

    /* --------------------------------------------------------------------- */
    /**
     * Return the period for the list as a packed string: YYYYMMDDYYYYMMDD
     */
    /* --------------------------------------------------------------------- */
    public String getPeriodPacked()
        {
        Calendar ps = Calendar.getInstance();
        ps.setTime(_periodstart);
        
        Calendar pe = Calendar.getInstance();
        pe.setTime(_periodend);
        
        String sy = Integer.toString(ps.get(Calendar.YEAR));
        int ism = ps.get(Calendar.MONTH); ism++;
        String sm = Integer.toString(ism);
        String sd = Integer.toString(ps.get(Calendar.DAY_OF_MONTH));
        
        String ey = Integer.toString(pe.get(Calendar.YEAR));
        int iem = pe.get(Calendar.MONTH); iem++;
        String em = Integer.toString(iem);
        String ed = Integer.toString(pe.get(Calendar.DAY_OF_MONTH));
        
        if (sm.length() == 1) { sm = "0" + sm; }
        if (sd.length() == 1) { sd = "0" + sd; }
        if (em.length() == 1) { em = "0" + em; }
        if (ed.length() == 1) { ed = "0" + ed; }
        
        return sy + sm + sd + ey + em + ed;
        }
    
    }
