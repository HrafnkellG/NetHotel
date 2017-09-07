package hg.db;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.html5.Dialog;
import hg.html5.IDropDown;
import hg.html5.TableRow;
import hg.util.Country;
import hg.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/* ========================================================================= */
/**
 * A guest in the reservation.
 */
/* ========================================================================= */
public class ResGuest
    {
    int     _rgid;
    int     _resid;
    String  _name;
    /** Holds the ISO code for a country. */
    String  _country;
    String  _passpid;
    int     _gender;
    boolean _checkedin;
    String  _roomno = "";
    Date    _arr;
    Date    _dep;

    /* --------------------------------------------------------------------- */
    /**
     * Display information about this guest in a row in a table. The row has
     * 5 columns: name - identifier - nationality - sex - room no.
     * 
     * @param row The target.
     * @param usr Needed for localized text.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRow(TableRow row, User usr)
        {
        // 1. NAME
        row.setCell(0, CSS.CCELL_NAME, _name);
        
        // 2. IDENTITFIER
        row.setCell(1, CSS.CCELL_ID, _passpid);
        
        // 3. NATIONALITY
        row.setCell(2, CSS.CCELL_NAT, _country);
        
        // 4. SEX
        String genderText = "";
        if (_gender == 1) 
            {
            genderText = usr.Txt(Loc.MALE);
            }
        else if (_gender == 2) 
            {
            genderText = usr.Txt(Loc.FEMALE);
            }
        else if (_gender == 3) 
            {
            genderText = usr.Txt(Loc.CHILD);
            }
        row.setCell(3, CSS.CCELL_SEX, genderText);
        
        // 5. ROOM NO.
        row.setCell(4, CSS.CCELL_RNO, _roomno);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Display the guest on the tab "Arriving today" on the main page.
     * 5 columns: 
     *    reservation id
     *    name
     *    room no.
     *    arrive
     *    depart
     * 
     * @param row Target
     * @param todaysDate Only the date, h:m:s are set to 0.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRowArriveTab(TableRow row, Date todaysDate) 
        {
        // If the guest is overdue, the date-arrives cell is highlighted.
        String cssCellArrives = CSS.CCELL_DATE;
        if (! _arr.equals(todaysDate)) 
            {
            cssCellArrives = cssCellArrives + " " + CSS.COVERDUE;
            }
        
        // 1. Reservation id
        row.setCell(0, CSS.CCELL_RESNO, Integer.toString(_resid));
        // 2. Name
        row.setCell(1, CSS.CCELL_NAME, _name);
        // 3. Room number
        row.setCell(2, CSS.CCELL_RNO, _roomno);
        // 4. Guest arrives for checkin
        row.setCell(3, cssCellArrives, Util.DateToIso(_arr));
        // 5. Guest checks out
        row.setCell(4, CSS.CCELL_DATE, Util.DateToIso(_dep));
        }

    /* --------------------------------------------------------------------- */
    /**
     * Return the database id for the guest.
     */
    /* --------------------------------------------------------------------- */
    public int getID()
        {
        return _rgid;
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * Return the database id for the reservation.
     */
    /* --------------------------------------------------------------------- */
    public int getReservationID() 
        {
        return _resid;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show this guest in a dialog.
     * 
     * @param dlg Display here.
     * @param usr Localized text.
     * @param cs Localized country names for the drop-down.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInDialog(Dialog dlg, User usr, Country[] cs)
        {
        // 1. NAME
        dlg.AddTextInput(usr.Txt(Loc.NAME), _name, "idres_guest_name", 50);
        
        // 2. COUNTRY
        IDropDown dd = dlg.AddDropDown(usr.Txt(Loc.NATIONALITY), "idres_nat");
        dd.AddItem("", "", false);
        for (int i = 0; i < cs.length; i++) 
            {
            boolean selected = false;
            if (cs[i]._countryISOCode.compareTo(_country) == 0) 
                {
                selected = true;
                }
            dd.AddItem(cs[i]._countryName, cs[i]._countryISOCode, selected);
            }
        
        // 3. Passport or other identifier
        dlg.AddTextInput(usr.Txt(Loc.IDENTIFIER), _passpid, "idres_identifier", 50);
        
        // 4. Gender
        IDropDown ddGender = dlg.AddDropDown(usr.Txt(Loc.SEX), "idres_sex");
        
        boolean gSelected = (_gender == 0);
        ddGender.AddItem("", "0", gSelected);
        
        gSelected = (_gender == 1);
        ddGender.AddItem(usr.Txt(Loc.MALE), "1", gSelected);
        
        gSelected = (_gender == 2);
        ddGender.AddItem(usr.Txt(Loc.FEMALE), "2", gSelected);
        
        gSelected = (_gender == 3);
        ddGender.AddItem(usr.Txt(Loc.CHILD), "3", gSelected);
        
        // 5. Rooms
        DBR res = Delphi.Inst().Reservation(_resid);
        Res reserv = (Res)res.Result();
        GregorianCalendar pstart = Util.DateToGregorian(reserv._periodstart);
        GregorianCalendar pend   = Util.DateToGregorian(reserv._periodend);
        ArrayList<Room> available = Res.getRoomsNotReserved(pstart, pend, _resid);
        
        IDropDown ddRooms = dlg.AddDropDown(usr.Txt(Loc.ROOMS), "idres_rooms");
        for (Room availr : available) 
            {
            boolean selected = (availr._roomNo.compareTo(_roomno) == 0);
            ddRooms.AddItem(availr._roomNo, availr._roomNo, selected);
            }
        }
    /* --------------------------------------------------------------------- */
    /**
     * True if the guest is checked in.
     */
    /* --------------------------------------------------------------------- */
    public boolean IsCheckedIn() 
        {
        return _checkedin;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Display in the Checkin page. There are 5 columns, but the first one is
     * not used by this method.
     * 
     * @param row Target
     */
    /* --------------------------------------------------------------------- */
    public void ShowInCheckin(TableRow row)
        {
        // 2nd. column NAME
        row.setCell(1, CSS.CCELL_NAME, _name);
        
        // 3rd. column ROOM NO.
        row.setCell(2, CSS.CCELL_RNO, _roomno);
        
        // 4th. column DATE ARRIVES
        row.setCell(3, CSS.CCELL_DATE, Util.DateToIso(_arr));
        
        // 5th. column DATE DEPARTS
        row.setCell(4, CSS.CCELL_DATE, Util.DateToIso(_dep));
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show the guest in the tab "Checked in" on the main page.
     * 7 columns
     *    name
     *    room no.
     *    room size
     *    room type
     *    floor
     *    arrive
     *    depart
     * 
     * @param row Target
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRowCheckedinTab(TableRow row)
        {
        row.setCell(0, CSS.CCELL_NAME, _name);
        row.setCell(1, CSS.CCELL_RNO, _roomno);
        row.setCell(5, CSS.CCELL_DATE, Util.DateToIso(_arr));
        row.setCell(6, CSS.CCELL_DATE, Util.DateToIso(_dep));
        }

    /* --------------------------------------------------------------------- */
    /**
     * Display the guest on the tab "Departing today" on the main page.
     * 5 columns: 
     *    reservation id
     *    name
     *    room no.
     *    arrive
     *    depart
     * 
     * @param row Target
     * @param todaysDate Only the date, h:m:s are set to 0.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRowDepartTab(TableRow row, Date todaysDate)
        {
        // If the guest is overdue, the date-departs cell is highlighted.
        String cssCellDeparts = CSS.CCELL_DATE;
        if (! _dep.equals(todaysDate)) 
            {
            cssCellDeparts = cssCellDeparts + " " + CSS.COVERDUE;
            }
        
        // 1. Reservation id
        row.setCell(0, CSS.CCELL_RESNO, Integer.toString(_resid));
        // 2. Name
        row.setCell(1, CSS.CCELL_NAME, _name);
        // 3. Room number
        row.setCell(2, CSS.CCELL_RNO, _roomno);
        // 4. Guest arrives for checkin
        row.setCell(3, CSS.CCELL_DATE, Util.DateToIso(_arr));
        // 5. Guest checks out
        row.setCell(4, cssCellDeparts, Util.DateToIso(_dep));
        }

    public String getRoomNo() 
        {
        return _roomno;
        }
    
    public Date getArriveDate()
        {
        return _arr;
        }
    
    public Date getDepartDate() 
        {
        return _dep;
        }

    }
