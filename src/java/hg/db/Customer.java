package hg.db;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.html5.Dialog;
import hg.html5.IDropDown;
import hg.html5.TableRow;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * A customer to the hotel, usually with some privileges like a discount.
 */
/* ========================================================================= */
public class Customer
    {
    
    /* --------------------------------------------------------------------- */
    /**
     * Fill a dialog with empty fields, used when defining a new customer.
     * 
     * @param dlg The target dialog.
     * @param usr Supports localized text.
     */
    /* --------------------------------------------------------------------- */
    public static void EmptyDialog(Dialog dlg, User usr)
        {
        // NAME
        dlg.AddTextInput(usr.Txt(Loc.NAME), null, "cust_name", 50);
        
        // ADDRESS LINE 1
        dlg.AddTextInput(usr.Txt(Loc.ADDRESS), null, "cust_address1", 50);
        
        // ADDRESS LINE 2
        dlg.AddTextInput(null, null, "cust_address2", 50);
        
        // POSTAL CODE
        dlg.AddTextInput(usr.Txt(Loc.POSTALCODE), null, "cust_postal", 50);
        
        // CITY
        dlg.AddTextInput(usr.Txt(Loc.CITY), null, "cust_city", 50);
        
        // COUNTRY
        dlg.AddTextInput(usr.Txt(Loc.COUNTRY), null, "cust_country", 50);
        
        // PRICE LIST
        IDropDown pldd = dlg.AddDropDown(usr.Txt(Loc.PRICELIST), "cust_pricelist");
        
        // Drop down must have one empty item.
        pldd.AddItem(null, null, true);
        
        DBR resPL = Delphi.Inst().Pricelists();
        ArrayList<Pricelist> pls = (ArrayList)resPL.Result();
        if (! pls.isEmpty()) 
            {
            for (Pricelist p : pls) 
                {
                pldd.AddItem(p.getName(), p.getDBID(), false);
                }
            }
        
        // DISCOUNT
        dlg.AddTextInput(usr.Txt(Loc.DISCOUNT), null, "cust_disc", 50);
        }
    
    
    /** Database identifier. */
    int    _id = -1;
    private String _firstname = "";
    private String _lastname = "";
    String _address1;
    String _address2;
    String _postalcode;
    String _city;
    String _country;
    String _email;
    String _phone;
    int    _pricelist = -1;
    double _discount;
    
    public void setFirstName(String name) 
        {
        _firstname = (name != null) ? name : "";
        }
    
    public void setLastName(String name) 
        {
        _lastname = (name != null) ? name : "";
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * The name of the customer.
     */
    /* --------------------------------------------------------------------- */
    public String getName() 
        {
        return _firstname + " " + _lastname;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the database identifier for this customer.
     */
    /* --------------------------------------------------------------------- */
    public String getDBIdentifier()
        {
        return Integer.toString(_id);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Display this customer in a row in a table.
     * 
     * @param row The target, it has 4 columns.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRow(TableRow row)
        {
        // Column 1: The customers name. We just use the first name.
        row.setCell(0, CSS.CCELL_NAME, _firstname);
        
        // Column 2: The address. Like with the name just use address line 1.
        row.setCell(1, CSS.CCELL_NAME, getAddress1());
        
        // Column 3: The name of the price list.
        String plName = "";
        if (_pricelist >= 0) 
            {
            DBR resPL = Delphi.Inst().Pricelist(_pricelist);
            Pricelist pl = (Pricelist)resPL.Result();
            plName = pl.getName();
            }
        row.setCell(2, CSS.CCELL_NAME, plName);
        
        // Column 4: The discount.
        String disc = "";
        if (_discount > 0) 
            {
            disc = Double.toString(_discount);
            }
        row.setCell(3, CSS.CCELL_NUMBER, disc);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show the customer in the edit dialog.
     * 
     * @param dlg Target dialog.
     * @param usr Supplies localized text.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInDialog(Dialog dlg, User usr)
        {
        // NAME
        dlg.AddTextInput(usr.Txt(Loc.NAME), _firstname, "cust_name", 50);
        
        // ADDRESS LINE 1
        dlg.AddTextInput(usr.Txt(Loc.ADDRESS), getAddress1(), "cust_address1", 50);
        
        // ADDRESS LINE 2
        dlg.AddTextInput(null, getAddress2(), "cust_address2", 50);
        
        // POSTAL CODE
        dlg.AddTextInput(usr.Txt(Loc.POSTALCODE), getPostalcode(), "cust_postal", 50);
        
        // CITY
        dlg.AddTextInput(usr.Txt(Loc.CITY), getCity(), "cust_city", 50);
        
        // COUNTRY
        dlg.AddTextInput(usr.Txt(Loc.COUNTRY), getCountry(), "cust_country", 50);
        
        // PRICE LIST
        IDropDown pldd = dlg.AddDropDown(usr.Txt(Loc.PRICELIST), "cust_pricelist");
        DBR resPL = Delphi.Inst().Pricelists();
        ArrayList<Pricelist> pls = (ArrayList)resPL.Result();
        if (! pls.isEmpty()) 
            {
            // Drop down must have one empty item.
            pldd.AddItem(null, null, false);
            for (Pricelist p : pls) 
                {
                boolean isSelected = false;
                int pldbid = Integer.parseInt(p.getDBID());
                if (_pricelist == pldbid) 
                    {
                    isSelected = true;
                    }
                pldd.AddItem(p.getName(), p.getDBID(), isSelected);
                }
            }
        else 
            {
            // Drop down must have one empty item.
            pldd.AddItem(null, null, true);
            }
        
        // DISCOUNT
        dlg.AddTextInput(usr.Txt(Loc.DISCOUNT), Double.toString(_discount), "cust_disc", 50);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show this customer in a drop-down.
     * @param idd Target drop-donw.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInDropDown(IDropDown idd)
        {
        idd.AddItem(_firstname, Integer.toString(_id), false);
        }

    public String getAddress1()
        {
        return _address1;
        }

    public String getAddress2()
        {
        return _address2;
        }

    public String getPostalcode()
        {
        return _postalcode;
        }

    public String getCity()
        {
        return _city;
        }

    public String getCountry()
        {
        return _country;
        }
    
    }
