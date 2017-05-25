package hg.ajax.inv;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.Hotel;
import hg.db.Inv;
import hg.db.User;
import hg.html5.Table;
import hg.html5.TableHead;
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
 * Get invoices which intersect a given period. This servlet takes
 * 2 parameters to define the period.
 * @author hg
 */
/* ========================================================================= */
public class InvForPeriod extends HttpServlet
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
            String sstart = request.getParameter("period_start");
            String send   = request.getParameter("period_end");
            if ((! Util.StringOK(sstart)) ||
                (! Util.StringOK(send  )) ||
                (sstart.length() != 8   ) ||
                (send.length()   != 8   )) 
                {
                return;
                }
            GregorianCalendar perStart = Util.packedDateToGregorian(sstart);
            GregorianCalendar perEnd = Util.packedDateToGregorian(send);
            
            // A table with invoices intersecting the period.
            String[] colHeaders = new String[4];
            colHeaders[0] = usr.Txt(Loc.NUMBER_SHORTENED);
            colHeaders[1] = usr.Txt(Loc.DATE);
            colHeaders[2] = usr.Txt(Loc.CUSTOMER);
            colHeaders[3] = usr.Txt(Loc.AMOUNT);
            Table invoices = new Table(colHeaders, null);
            invoices.setCaption(usr.Txt(Loc.INVOICES));
            // Set CSS for the columns.
            TableHead th = invoices.getHead();
            th.setCellCSS(0, CSS.CCOL_NUMBER);
            th.setCellCSS(1, CSS.CCOL_DATE);
            th.setCellCSS(2, CSS.CCOL_NAME);
            th.setCellCSS(3, CSS.CCOL_NUMBER);
            Inv.ShowPeriodInTable(invoices, perStart, perEnd, hot);
            
            out.print(invoices.Render());
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
