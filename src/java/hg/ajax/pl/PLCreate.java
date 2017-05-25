package hg.ajax.pl;

import hg.cons.Loc;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.User;
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
 * Create a new price list.
 */
/* ========================================================================= */
public class PLCreate extends HttpServlet
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
            // Only available to admins.
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                out.print("error");
                return;
                }
            
            String strPLName = request.getParameter("pl_name");
            String pstart    = request.getParameter("pl_period_start");
            String pend      = request.getParameter("pl_period_end");
            
            // Check what we have, all must be strings and dates must be 8 chars long.
            if (! Util.StringOK(strPLName)) 
                {
                out.print(usr.Txt(Loc.MISS_NAME));
                return;
                }
            if (! Util.StringOK(pstart) ||
                ! Util.StringOK(pend  ) || 
                pstart.length() != 8    ||
                pend.length()   != 8      )
                {
                out.print("Bad input");
                return;
                }
            
            GregorianCalendar gcStart = Util.packedDateToGregorian(pstart);
            GregorianCalendar gcEnd   = Util.packedDateToGregorian(pend);
//            String isoStart = pstart.substring(0, 4) + "-" + pstart.substring(4, 6) + "-" + pstart.substring(6, 8);
//            String isoEnd   = pend.substring(0, 4)   + "-" + pend.substring(4, 6)   + "-" + pend.substring(6, 8);
            
            DBR res = Delphi.Inst().PricelistCreate(strPLName, gcStart, gcEnd, usr.getName());
            if (! res.OK()) 
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
