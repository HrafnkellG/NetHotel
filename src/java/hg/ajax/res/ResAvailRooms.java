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
 * Returns a list of available rooms for a given period. Takes 2 parameters,
 * the arrival date and departure date.
 */
/* ========================================================================= */
public class ResAvailRooms extends HttpServlet
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
            String sarrive = request.getParameter("resdate_arrive");
            String sdepart = request.getParameter("resdate_depart");
            if ((! Util.StringOK(sarrive)) ||
                (! Util.StringOK(sdepart)) ||
                (sarrive.length() != 8   ) ||
                (sdepart.length() != 8   )) 
                {
                return;
                }
            GregorianCalendar gcArrive = Util.packedDateToGregorian(sarrive);
            GregorianCalendar gcDepart = Util.packedDateToGregorian(sdepart);
            
            // Create a table with 4 columns.
            String[] colHeaders = new String[4];
            colHeaders[0] = usr.Txt(Loc.ROOMNO);
            colHeaders[1] = usr.Txt(Loc.SIZE);
            colHeaders[2] = usr.Txt(Loc.TYPE);
            colHeaders[3] = usr.Txt(Loc.FLOOR);
            Table avail = new Table(colHeaders, "superfluous");
            avail.setCaption(usr.Txt(Loc.AVAILABLEROOMS));
            
            // Fill the table with available rooms.
            Res.showInTableAvailableRooms(avail, gcArrive, gcDepart, hot.AllowOverbooking());
            out.print(avail.Render());
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
