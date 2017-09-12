package hg.ajax.tabs;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Res;
import hg.db.ResGuest;
import hg.db.Room;
import hg.db.User;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.util.Util;
import java.util.ArrayList;
import java.util.Date;

class graphTable
    {
    /**
     * Create a table with info about the state of reservations over period 
     * of one week.
     * 
     * @param db Access to the database.
     * @param usr User for the session, for localized text.
     * @param startDate The first day of the period.
     */
    static Table makeGraphTable(Delphi db, User usr, Date startDate) 
        {
        Date endDate = Util.DateAddDays(startDate, 6);
        
        DBR resultRooms = db.Rooms();
        if (! resultRooms.OK()) { return null; }
        ArrayList<Room> rooms = (ArrayList)resultRooms.Result();
        
        DBR result = db.Reservations(startDate, endDate);
        if (! result.OK()) { return null; }
        ArrayList<Res> resList = (ArrayList)result.Result();
        
        // Create the column headers.
        String[] colh = new String[8];
        colh[0] = usr.Txt(Loc.ROOMS);
        Date dateCounter = startDate;
        for (int i=1; i<=7; i++) 
            {
            // Get the day of the month.
            String isoDate = Util.DateToIsoPacked(dateCounter);
            colh[i] = isoDate.substring(6);
            dateCounter = Util.DateAddDays(dateCounter, 1);
            }
        
        // Create the table.
        Table t = new Table(colh, CSS.CGRAPHWRAP); 
        TableHead th = t.getHead();
        th.setCellCSS(0, CSS.CCOL_GRROOMNO);
        th.setCellCSS(1, CSS.CCOL_GRWEEKDAY);
        th.setCellCSS(2, CSS.CCOL_GRWEEKDAY);
        th.setCellCSS(3, CSS.CCOL_GRWEEKDAY);
        th.setCellCSS(4, CSS.CCOL_GRWEEKDAY);
        th.setCellCSS(5, CSS.CCOL_GRWEEKDAY);
        th.setCellCSS(6, CSS.CCOL_GRWEEKDAY);
        th.setCellCSS(7, CSS.CCOL_GRWEEKDAY);
        
        TableBody tb = t.getBody();
        
        boolean rowIsEven = true;
        String rowEvenOdd;
        // Double loop.
        // The outer loop goes through all the rooms in the hotel.
        // The inner loop goes through the 7 days of a week.
        // For each day we check whether the room is reserved and by whom.
        for (Room r : rooms) 
            {
            rowIsEven = ! rowIsEven;
            rowEvenOdd = rowIsEven ? CSS.CROW_EVEN : CSS.CROW_ODD;
            
            Date currentDate = startDate;
            
            TableRow tr = tb.AddRow();
            tr.setCell(0, CSS.CCELL_GRROOMNO, r.getRoomNo());
            tr.AddCSSClass(rowEvenOdd);
            tr.AddCSSClass(CSS.CROW_NOTHOVER);
            
            for (int i=1; i<=7; i++) 
                {
                String cellID = r.getRoomNo() + Util.DateToIsoPacked(currentDate);
                tr.setCellID(i, cellID);
                
                ResGuest foundGuestForRoomAndDay = null;
                Res reservationOfGuest = null;
                for (Res resv : resList) 
                    {
                    ArrayList<ResGuest> guestList = resv.getGuestList();
                    for (ResGuest g : guestList) 
                        {
                        String rn = g.getRoomNo();
                        if (!rn.equals(r.getRoomNo())) 
                            { continue; }
                        
                        // This guest is booked for this room during the week,
                        // check whether his reservation falls on this day.
                        Date garrive = g.getArriveDate();
                        Date gdepart = g.getDepartDate();
                        if (garrive.getTime() <= currentDate.getTime() &&
                            gdepart.getTime() >  currentDate.getTime()) 
                            {
                            // This guest is booked for this room on this day,
                            // our search is over.
                            foundGuestForRoomAndDay = g;
                            reservationOfGuest = resv;
                            break;
                            }
                        }
                    if (foundGuestForRoomAndDay != null) 
                        {
                        break;
                        }
                    }//END-OF for (Res resv : resList)
                
                if (foundGuestForRoomAndDay != null) 
                    {
                    String custName = reservationOfGuest.getCustomerName();
                    String cellDisplayName = null;
                    if (currentDate.getTime() == foundGuestForRoomAndDay.getArriveDate().getTime()) 
                        {
                        // We show the name of the customer in the cell which
                        // represents the first day of the guests' stay.
                        if (custName.length() > 12) 
                            {
                            cellDisplayName = custName.substring(0, 12);
                            }
                        else 
                            {
                            cellDisplayName = custName;
                            }
                        }
                    tr.setCell(i, CSS.CCELL_GRRES + " " + CSS.CCELL_GRRESERVATION, cellDisplayName);
                    
                    ArrayList<ResGuest> guestsInRoom = findAllGuests(foundGuestForRoomAndDay);
                    String titleText = custName;
                    for (ResGuest gir : guestsInRoom) 
                        {
                        titleText += "\n" + gir.getName();
                        }
                    tr.setCellTitle(i, titleText);
                    }
                else 
                    {
                    tr.setCell(i, CSS.CCELL_GRRES, null);
                    tr.setCellClickEvent(i, "TabGraphCellClickFree(this, '" + cellID + "')");
                    }
                currentDate = Util.DateAddDays(currentDate, 1);
                }//END-OF for (int i=1; i<=7; i++)
            
            }//END-OF for (Room r : rooms) 
        
        return t;
        }

    private static ArrayList<ResGuest> findAllGuests(ResGuest targetGuest)
        {
        ArrayList<ResGuest> returnValue = new ArrayList();
        String targetRoom = targetGuest.getRoomNo();
        DBR resultOfQuery = Delphi.Inst().Reservation(targetGuest.getReservationID());
        Res guestRes = (Res)resultOfQuery.Result();
        ArrayList<ResGuest> guestList = guestRes.getGuestList();
        
        for  (ResGuest g : guestList) 
            {
            if (targetRoom.compareTo(g.getRoomNo()) == 0) 
                {
                returnValue.add(g);
                }
            }
        
        return returnValue;
        }
    }
