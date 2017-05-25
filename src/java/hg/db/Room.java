package hg.db;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.html5.Dialog;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableRow;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * A room in the hotel.
 */
/* ========================================================================= */
public class Room
    {
    String  _roomNo     = "";
    int     _size       = 0;
    int     _floor      = 0;
    String  _type       = "";
    boolean _occupied   = false;
    boolean _clean      = true;
    
    /* --------------------------------------------------------------------- */
    /**
     * Fill a dialog with un-initialized fields.
     * 
     * @param usr Logged in user, needed for loaclized text.
     * @param dlg Target dialog.
     */
    /* --------------------------------------------------------------------- */
    public void EmptyDialog(User usr, Dialog dlg) 
        {
        // 1st. row - ROOM NUMBER
        dlg.AddTextInput(usr.Txt(Loc.ROOMNO), null, "roomno", 10);
        
        // 2nd. row - SIZE
        dlg.AddTextInput(usr.Txt(Loc.SIZE), null, "roomsize", 0);
        
        // 3rd. row - FLOOR
        dlg.AddTextInput(usr.Txt(Loc.FLOOR), null, "roomfloor", 0);
        
        // 4rth. row - TYPE
        dlg.AddTextInput(usr.Txt(Loc.TYPE), null, "roomtype", 10);
        }

    public String getRoomNo()
        {
        return _roomNo;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show info about this room in a row in a table. There are 4 cells.
     * @param row Target row.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRow(TableRow row)
        {
        // 1st. cell - The hotel room number
        row.setCell(0, CSS.CCELL_RNO, _roomNo);
        
        // 2nd. cell - The size of the room.
        row.setCell(1, CSS.CCELL_RSIZE, Integer.toString(_size));
        
        // 3rd. cell - The type of the room.
        row.setCell(2, CSS.CCELL_RTYPE, _type);
        
        // 4th. cell - The floor.
        row.setCell(3, CSS.CCELL_FLOOR, Integer.toString(_floor));
        }

    /* --------------------------------------------------------------------- */
    /**
     * Fill a dialog with info about this room.
     * 
     * @param usr Logged in user, needed for loaclized text.
     * @param dlg Target dialog.
     */
    /* --------------------------------------------------------------------- */
    public void ShowInDialog(User usr, Dialog dlg)
        {
        // 1st. row - ROOM NUMBER
        dlg.AddTextInput(usr.Txt(Loc.ROOMNO), _roomNo, "roomno", 10);
        
        // 2nd. row - SIZE
        dlg.AddTextInput(usr.Txt(Loc.SIZE), Integer.toString(_size), "roomsize", 0);
        
        // 3rd. row - FLOOR
        dlg.AddTextInput(usr.Txt(Loc.FLOOR), Integer.toString(_floor), "roomfloor", 0);
        
        // 4rth. row - TYPE
        dlg.AddTextInput(usr.Txt(Loc.TYPE), _type, "roomtype", 10);
        }

    public boolean IsOccupied()
        {
        return _occupied;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show the status of all rooms: occupied/clean
     * 
     * @param status The target table, it should have 6 columns.
     * @param usr Logged in user, for localized text.
     */
    /* --------------------------------------------------------------------- */
    public static void ShowInTableStatus(Table status, User usr)
        {
        DBR resRooms = Delphi.Inst().Rooms();
        ArrayList<Room> rooms = (ArrayList)resRooms.Result();
        
        TableBody tbody = status.getBody();
        boolean isOdd = true;
        for (Room room : rooms) 
            {
            TableRow row = tbody.AddRow();
            room.ShowInRow(row);
            String cssForCleanCell = (room._clean) ? CSS.CCELL_RNO : CSS.CCELL_RNO + " " + CSS.COVERDUE;
            String occupied = (room._occupied) ? usr.Txt(Loc.YES) : usr.Txt(Loc.NO);
            String clean    = (room._clean)    ? usr.Txt(Loc.YES) : usr.Txt(Loc.NO);
            row.setCell(4, CSS.CCELL_RNO, occupied);
            row.setCell(5, cssForCleanCell, clean);
            row.setEventOnclick("TabRoomsRowSelect(" + room.getRoomNo() + ")");
            
            String rowClass = (isOdd) ? CSS.CROW_ODD : CSS.CROW_EVEN ;
            row.AddCSSClass(rowClass);
            isOdd = !isOdd;
            }
        }

    public boolean getCleanStatus()
        {
        return _clean;
        }
    }
