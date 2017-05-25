package hg.ajax.tabs;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.ResGuest;
import hg.db.Room;
import hg.db.User;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.util.AppLog;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Supply a table for the "Available Rooms" tab on the main page.
 */
/* ========================================================================= */
public class TabAvailableRooms extends HttpServlet
    {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
        {
        AppLog.Instance().Info("TabAvailableRooms.processRequest() - Entered");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                AppLog.Instance().Info("TabAvailableRooms.processRequest() - No user");
                return;
                }
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            Date  todaysDate = hot.getDate();
            
            DBR resRoomList = Delphi.Inst().Rooms();
            if (! resRoomList.OK()) 
                {
                out.write("<p>" + resRoomList.ErrorMessage() + "</p>");
                return;
                }
            ArrayList<Room> roomList = (ArrayList)resRoomList.Result();
            
            // The table has 5 columns, provide the header texts.
            String[] colh = new String[5];
            colh[0] = usr.Txt(Loc.ROOMNO);
            colh[1] = usr.Txt(Loc.ROOMSIZE);
            colh[2] = usr.Txt(Loc.TYPE);
            colh[3] = usr.Txt(Loc.FLOOR);
            colh[4] = usr.Txt(Loc.DAYS);
            
            // Create table.
            Table tableAvailable = new Table(colh, null);
            tableAvailable.setCaption(usr.Txt(Loc.AVAILABLEROOMS));
            
            // Assign  CSS to the columns.
            TableHead th = tableAvailable.getHead();
            th.setCellCSS(0, CSS.CCOL_RNO);
            th.setCellCSS(1, CSS.CCOL_RSIZE);
            th.setCellCSS(2, CSS.CCOL_RTYPE);
            th.setCellCSS(3, CSS.CCOL_FLOOR);
            th.setCellCSS(4, CSS.CCOL_NUMBER);
            
            // Supply the rows.
            boolean isOdd = true;
            TableBody bod = tableAvailable.getBody();
            for (Room room : roomList) 
                {
                // Occupied rooms are of no interest.
                if (room.IsOccupied()) 
                    {
                    continue;
                    }
                DBR resultGuests = Delphi.Inst().GuestsReservedForRoom(room.getRoomNo(), todaysDate);
                if (! resultGuests.OK()) 
                    {
                    out.write(resultGuests.ErrorMessage());
                    return;
                    }
                ArrayList<ResGuest> guestList = (ArrayList)resultGuests.Result();
                ResGuest rg = guestList.isEmpty() ? null : guestList.get(0);
                if (rg == null) 
                    {
                    TableRow row = bod.AddRow();
                    room.ShowInRow(row);
                    row.setCell(4, CSS.CCELL_NUMBER, "∞");
                    if (isOdd) 
                        { row.AddCSSClass(CSS.CROW_ODD); }
                    else
                        { row.AddCSSClass(CSS.CROW_EVEN); }
                    isOdd = !isOdd;
                    }
                else 
                    {
                    // There are guests in reservations for this room. If the date
                    // of arrival of the first guest is after today, then at
                    // least 1 day is free for the room.
                    if (todaysDate.before(rg.getArriveDate())) 
                        {
                        int freeDays = Util.DifferenceInDays(todaysDate, rg.getArriveDate());
                        TableRow row = bod.AddRow();
                        room.ShowInRow(row);
                        row.setCell(4, CSS.CCELL_NUMBER, Integer.toString(freeDays));
                        if (isOdd) 
                            { row.AddCSSClass(CSS.CROW_ODD); }
                        else
                            { row.AddCSSClass(CSS.CROW_EVEN); }
                        isOdd = !isOdd;
                        }
                    }
                }//ENDOF for
            
            if (bod.RowCount() == 0) 
                {
                out.write("<p>" + usr.Txt(Loc.ALLROOMSOCCUPIED) + "</p>");
                }
            else 
                {
                out.write(tableAvailable.Render());
                }
            }
        finally
            {
            out.close();
            }
        }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
        {
        processRequest(request, response);
        }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
        {
        processRequest(request, response);
        }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
        {
        return "Short description";
        }// </editor-fold>

    }
