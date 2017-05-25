package hg.ajax.res;

import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.Hotel;
import hg.db.Res;
import hg.db.User;
import hg.html5.Table;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Get reservations which intersect the given period. This servlet takes
 * 2 parameters to define the period.
 */
/* ========================================================================= */
public class ResPeriod extends HttpServlet
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
                return;
                }
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            
            // Get our dates.
            String sarrive = request.getParameter("resdate_start");
            String sdepart = request.getParameter("resdate_end");
            if ((! Util.StringOK(sarrive)) ||
                (! Util.StringOK(sdepart)) ||
                (sarrive.length() != 8   ) ||
                (sdepart.length() != 8   )) 
                {
                return;
                }
            GregorianCalendar perStart = Util.packedDateToGregorian(sarrive);
            GregorianCalendar perEnd = Util.packedDateToGregorian(sdepart);
            
            // A table with the reservations intersecting the period.
            String[] colHeaders = new String[5];
            colHeaders[0] = usr.Txt(Loc.ARRIVING);
            colHeaders[1] = usr.Txt(Loc.DEPARTING);
            colHeaders[2] = usr.Txt(Loc.CUSTOMER);
            colHeaders[3] = usr.Txt(Loc.NOOFROOMS);
            colHeaders[4] = usr.Txt(Loc.NOOFGUESTS);
            Table reservations = new Table(colHeaders, "superfluous");
            reservations.setCaption(usr.Txt(Loc.RESERVATION));
            Res.showInTableReservations(reservations, perStart, perEnd);
            
            out.print(reservations.Render());
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
