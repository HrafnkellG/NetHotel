package hg.db;

import java.util.Date;

/* ========================================================================= */
/**
 * A defined guest in the hotel (from the RESG table).
 */
/* ========================================================================= */
public class Guest
    {
    int     _rgid       = -1;
    int     _resid      = -1;
    String  _name       = "-?-";
    String  _country    = "";
    String  _passpid    = "";
    int     _gender     = 0;
    boolean _checkedin  = false;
    String  _roomno     = "";
    Date    _arr        = null;
    Date    _dep        = null;
    
    public String getRoomNo() 
        {
        return _roomno;
        }
    
    public Res getReservation() 
        {
        DBR reserv = Delphi.Inst().Reservation(_resid);
        return (Res)reserv.Result();
        }
    
    }//END-OF class Guest
