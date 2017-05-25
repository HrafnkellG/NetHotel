package hg.db;

import hg.cons.CSS;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/* ========================================================================= */
/**
 * A reservation in the hotel.
 */
/* ========================================================================= */
public class Res
    {
    int     _id;
    Date    _made;
    String  _user;
    int     _custid;
    int     _nor;
    int     _nog;
    Date    _periodstart;
    Date    _periodend;
    int     _invid;
    
    private ArrayList<ResGuest> _guestList = new ArrayList();

    public ArrayList<ResGuest> getGuestList()
        {
        return _guestList;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * A utility function which tells us how many rooms are available over
     * a given period. The rule is this: if a reservation has booked a room and
     * that reservation intersects the period, then that room is marked as taken
     * and will not be part of the group of available rooms.
     * 
     * @param avail A table to be filled with those rooms which are available.
     * @param fromDate The starting day of the period.
     * @param toDate The last day of the period.
     * @param allowOverbooking NOT USED AT THE MOMENT If true then all 
     * the rooms will be shown in the table but those who are booked will be marked.
     */
    /* --------------------------------------------------------------------- */
    public static void showInTableAvailableRooms(
    Table             avail, 
    GregorianCalendar fromDate, 
    GregorianCalendar toDate, 
    boolean           allowOverbooking) 
        {
        ArrayList<Room> available = getRoomsNotReserved(fromDate, toDate);
        if (available == null) 
            {
            return;
            }
        
        TableHead th = avail.getHead();
        th.setCellCSS(0, CSS.CCOL_RNO);
        th.setCellCSS(1, CSS.CCOL_RSIZE);
        th.setCellCSS(2, CSS.CCOL_RTYPE);
        th.setCellCSS(3, CSS.CCOL_FLOOR);
        
        TableBody body = avail.getBody();
        
        String rowClass;
        boolean isOdd = true;
        for (Room r : available) 
            {
            rowClass = (isOdd) ? CSS.CROW_ODD : CSS.CROW_EVEN ;
            isOdd = ! isOdd;
            TableRow row = body.AddRow();
            row.AddCSSClass(rowClass);
            row.setEventOnclick("RoomClicked(this, '" + r._roomNo + "', " + r._size + ")");
            r.ShowInRow(row);
            }
        }
    /* --------------------------------------------------------------------- */
    /**
     * Utility function which fills a table with those reservations which 
     * intersect the given period.
     * @param reservs The table to fill, it has 5 columns.
     * @param startDate Start date of the period.
     * @param endDate End date of the period.
     */
    /* --------------------------------------------------------------------- */
    public static void showInTableReservations(
    Table reservs,
    GregorianCalendar startDate,
    GregorianCalendar endDate) 
        {
        DBR resReservations = Delphi.Inst().Reservations(startDate.getTime(), endDate.getTime());
        if (!resReservations.OK()) 
            {
            return;
            }
        ArrayList<Res> resList = (ArrayList)resReservations.Result();
        if (resList.isEmpty()) 
            {
            return;
            }
        
        // Set CSS for the columns.
        TableHead th = reservs.getHead();
        th.setCellCSS(0, CSS.CCOL_DATE);
        th.setCellCSS(1, CSS.CCOL_DATE);
        th.setCellCSS(2, CSS.CCOL_NAME);
        th.setCellCSS(3, CSS.CCOL_NUMBER);
        th.setCellCSS(4, CSS.CCOL_NUMBER);
        TableBody tbody = reservs.getBody();
        
        String rowClass;
        boolean isOdd = true;
        for (Res r : resList) 
            {
            rowClass = (isOdd) ? CSS.CROW_ODD : CSS.CROW_EVEN ;
            isOdd = !isOdd;
            TableRow row = tbody.AddRow();
            row.AddCSSClass(rowClass);
            row.setEventOnclick("window.location = 'ResEdit?res_id=" + r._id + "'");
            r.ShowInRow(row);
            }
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * Get a list of those rooms which are not part of any reservation which
     * intersects the given period.
     * 
     * @param fromDate Start of the period.
     * @param toDate End of the period.
     */
    /* --------------------------------------------------------------------- */
    public static ArrayList<Room> getRoomsNotReserved(
    GregorianCalendar fromDate, 
    GregorianCalendar toDate) 
        {
        DBR resRooms = Delphi.Inst().Rooms();
        if (! resRooms.OK()) { return null; }
        ArrayList<Room> rooms = (ArrayList)resRooms.Result();
        
        Date fd = fromDate.getTime();
        Date td = toDate.getTime();
        DBR resReservations = Delphi.Inst().Reservations(fd, td);
        if (! resReservations.OK()) { return null; }
        ArrayList<Res> reservations = (ArrayList)resReservations.Result();
        
        ArrayList<Room> nonReserved = new ArrayList();
        if (reservations.isEmpty()) 
            {
            // No reservations intersect the period so we return all the rooms
            return rooms;
            }
        else 
            {
            // There are reservations which intersect the period.
            // Walk through them and pick out the rooms which are occupied.
            HashMap occupiedRooms = new HashMap();
            for (Res resv : reservations) 
                {
                for (ResGuest resg : resv._guestList) 
                    {
                    String roomNum = resg._roomno;
                    if (! occupiedRooms.containsKey(roomNum)) 
                        {
                        occupiedRooms.put(roomNum, true);
                        }
                    }
                }
            
            // Pick out those rooms which are not reserved.
            for (Room r : rooms) 
                {
                if (! occupiedRooms.containsKey(r._roomNo)) 
                    {
                    nonReserved.add(r);
                    }
                }
            }//END-OF if-else
        
        return nonReserved;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get a list of those rooms which are not part of any reservation which
     * intersects the given period, but include those rooms which are in a
     * specific reservation.
     * 
     * @param fromDate Start of the period.
     * @param toDate End of the period.
     * @param resID The id of the reservation to include.
     */
    /* --------------------------------------------------------------------- */
    public static ArrayList<Room> getRoomsNotReserved(
    GregorianCalendar fromDate, 
    GregorianCalendar toDate,
    int               resID) 
        {
        ArrayList<Room> avail = getRoomsNotReserved(fromDate, toDate);
        
        DBR qr = Delphi.Inst().Reservation(resID);
        if (! qr.OK()) 
            {
            return avail;
            }
        Res theReservation = (Res)qr.Result();
        HashMap roomsInRes = new HashMap();
        for (ResGuest rg : theReservation._guestList) 
            {
            if (! roomsInRes.containsKey(rg._roomno)) 
                {
                roomsInRes.put(rg._roomno, true);
                }
            }
        Iterator it = roomsInRes.entrySet().iterator();
        while (it.hasNext()) 
            {
            Entry e = (Entry)it.next();
            Room r = (Room)Delphi.Inst().RoomGet((String)e.getKey()).Result();
            avail.add(r);
            }
        
        return avail;
        }
    

    /* --------------------------------------------------------------------- */
    /**
     * Add a guest to the reservation.
     */
    /* --------------------------------------------------------------------- */
    void addGuest(ResGuest resg)
        {
        if (resg != null) 
            {
            _guestList.add(resg);
            }
        }

    /* --------------------------------------------------------------------- */
    /**
     * Return the name of the customer which owns this reservation.
     */
    /* --------------------------------------------------------------------- */
    public String getCustomerName()
        {
        String result = "-?-";
        DBR dbqRes = Delphi.Inst().Customer(_custid);
        if (dbqRes.OK()) 
            {
            result = ((Customer)dbqRes.Result()).getName();
            }
        
        return result;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get a list of guests registered in this reservation.
     */
    /* --------------------------------------------------------------------- */
    public ArrayList<ResGuest> getGuests()
        {
        return _guestList;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Return the database identifier.
     */
    /* --------------------------------------------------------------------- */
    public int getDBID()
        {
        return _id;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show the reservation in a row in a table. The table will have 5 columns:
     *    1. Start date
     *    2. End date
     *    3. Name of the customer
     *    4. No. of rooms in the reservation
     *    5. No. of guests
     * 
     * @param row Target
     */
    /* --------------------------------------------------------------------- */
    public void ShowInRow(TableRow row)
        {
        DBR resCustomer = Delphi.Inst().Customer(_custid);
        String custName = resCustomer.OK() ? ((Customer)resCustomer.Result()).getName() : "?";
        
        row.setCell(0, CSS.CCELL_DATE, Util.DateToIso(_periodstart));
        row.setCell(1, CSS.CCELL_DATE, Util.DateToIso(_periodend));
        row.setCell(2, CSS.CCELL_NAME, custName);
        row.setCell(3, CSS.CCELL_NUMBER, Integer.toString(_nor));
        row.setCell(4, CSS.CCELL_NUMBER, Integer.toString(_nog));
        }

    /* --------------------------------------------------------------------- */
    /**
     * Count the number of guests per room.
     * @return The key/value is: room number is the key, number of guests the value.
     */
    /* --------------------------------------------------------------------- */
    public HashMap getGuestsPerRoom()
        {
        HashMap numGuests = new HashMap();
        for (ResGuest g : _guestList) 
            {
            if (numGuests.containsKey(g._roomno)) 
                {
                numGuests.put(g._roomno, ((Integer)numGuests.get(g._roomno)) + 1);
                }
            else 
                {
                numGuests.put(g._roomno, 1);
                }
            }
        
        return numGuests;
        }

    public String getInvoiceID()
        {
        return Integer.toString(_invid);
        }
    }
