package hg.ajax.res;

import hg.cons.Loc;
import hg.cons.Paths;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Res;
import hg.db.ResGuest;
import hg.db.User;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Delete a guest from a reservation.
 */
/* ========================================================================= */
public class ResGuestDelete extends HttpServlet
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
            // Need a logged in user.
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                out.print("Not logged in.");
                return;
                }
            
            // Check the passed in parameter.
            String sDBID = request.getParameter("idres_guest");
            if (! Util.IntOK(sDBID)) 
                {
                out.print("Bad ID for guest.");
                return;
                }
            int iDBID = Integer.parseInt(sDBID);
            
            // Prevent deletion if this is the last guest in the reservation.
            DBR qrGuest = Delphi.Inst().ReservationGuest(iDBID);
            if (! qrGuest.OK()) 
                {
                out.print("Guest not found.");
                return;
                }
            ResGuest theGuest = (ResGuest)qrGuest.Result();
            DBR qrRes = Delphi.Inst().Reservation(theGuest.getReservationID());
            Res theReservation = (Res)qrRes.Result();
            ArrayList guestList = theReservation.getGuests();
            if (guestList.size() == 1) 
                {
                out.print(usr.Txt(Loc.ERRLASTGUEST));
                return;
                }
            
            // Perform delete.
            DBR qr = Delphi.Inst().ReservationGuestDelete(iDBID);
            if (! qr.OK()) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
                return;
                }
            
            out.print("ok");
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
