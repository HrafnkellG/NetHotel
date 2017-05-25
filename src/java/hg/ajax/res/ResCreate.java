package hg.ajax.res;

import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.Res;
import hg.db.User;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/* ========================================================================= */
/**
 * Create a new reservation.
 */
/* ========================================================================= */
public class ResCreate extends HttpServlet
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
            // If a user is not logged in, redirect.
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                response.sendRedirect(Paths.LOGIN);
                return;
                }
            HttpSession sess = request.getSession(false);
            Hotel hot = (Hotel)sess.getAttribute(Sess.HOTEL);
            
            // Get parameters.
            String sArrive    = request.getParameter("resdate_arrive");
            String sDepart    = request.getParameter("resdate_depart");
            String sRooms     = request.getParameter("resrooms");
            String sCustID    = request.getParameter("rescustomer");
            String snumGuests = request.getParameter("resnum_guests");
            
            // Convert parameters.
            int inumGuests = Integer.parseInt(snumGuests);
            int iCustID    = Integer.parseInt(sCustID);
            GregorianCalendar gcArrive = Util.packedDateToGregorian(sArrive);
            GregorianCalendar gcDepart = Util.packedDateToGregorian(sDepart);
            Date dateArrive = gcArrive.getTime();
            Date dateDepart = gcDepart.getTime();
            String[] rooms = sRooms.split(",");
            
            // When the reservation was created.
            GregorianCalendar gcCreationDate = hot.getDateTime();
            
            DBR dbrRes = Delphi.Inst().ReservationCreate(
                usr, 
                gcCreationDate, 
                iCustID,
                inumGuests,
                dateArrive, 
                dateDepart, 
                rooms);
            if (!dbrRes.OK()) 
                {
                out.print(dbrRes.ErrorMessage());
                return;
                }
            Res reservation = (Res)dbrRes.Result();
            
            // We have created the reservation, now it needs an invoice.
            DBR dbrInvoice = Delphi.Inst().InvoiceCreate(reservation);
            if (! dbrInvoice.OK()) 
                {
                // Failed, we must erase the reservation too.
                Delphi.Inst().ReservationDelete(reservation.getDBID());
                out.print(dbrInvoice.ErrorMessage());
                return;
                }
            
            // Success, both the reservation and its invoice were created.
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
