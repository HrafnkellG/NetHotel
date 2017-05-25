package hg.ajax;

import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Guest;
import hg.db.Hotel;
import hg.db.Res;
import hg.db.Room;
import hg.db.User;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Check-in guests. Returns 'ok' if successful, otherwise an error-string.
 */
/* ========================================================================= */
public class CheckinGuests extends HttpServlet
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            User usr = Util.getUserForSession(request);
            if (usr == null) 
                {
                out.print("user not logged in");
                return;
                }
            
            String opResult = "ok";
            
            String par = request.getParameter("guests");
            String[] guestID = par.split(",");
            
            if (! CheckinAllowed(guestID)) 
                {
                out.print(usr.Txt(Loc.ROOMSOCCUPIED));
                return;
                }
            
            DBR qresult = Delphi.Inst().GuestCheckIn(guestID);
            if (! qresult.OK()) 
                {
                opResult = qresult.ErrorMessage();
                }
            
            out.print(opResult);
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

    /* --------------------------------------------------------------------- */
    /**
     * Guests can only be checked into rooms which are not occupied, 
     * OR
     * the guest in the room and the guest to be checked in are both in
     * the same reservation.
     * 
     * @param guestID The guests which are to be checked in.
     * 
     * @return True if all the guests can be checked in.
     */
    /* --------------------------------------------------------------------- */
    private boolean CheckinAllowed(String[] guestID)
        {
        for (String gid : guestID) 
            {
            DBR dbRes = Delphi.Inst().Guest(Integer.parseInt(gid));
            Guest g = (Guest)dbRes.Result();
            DBR   resultr = Delphi.Inst().RoomGet(g.getRoomNo());
            Room  r       = (Room)resultr.Result();
            if (r.IsOccupied()) 
                {
                DBR dbrCheckedin      = Delphi.Inst().ReservationCheckedinRoom(r.getRoomNo());
                Res resWithLockOnRoom = (Res)dbrCheckedin.Result();
                Res resWithGuest      = g.getReservation();
                if (resWithLockOnRoom.getDBID() != resWithGuest.getDBID()) 
                    {
                    // The guest which is already checked into the room is not 
                    // in the same reservation as the guest we are trying to 
                    // check in.
                    return false;
                    }
                }
            }
        
        // None of the rooms are occupied, or the guests which are already 
        // checked in are also in the same reservation.
        // Either way we can allow these guests to be checked in.
        return true;
        }

    }
