package hg.ajax;

import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.User;
import hg.util.AppLog;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* ========================================================================= */
/**
 * Save the configuration for the hotel. There are 9 parameters, of which 6
 * are from text fields. Only 1 of them must be error checked, the V.A.T,
 * the rest may be empty.<p>
 * The caller expects 'ok' as a response for success, else an error message 
 * for the user.
 */
/* ========================================================================= */
public class HCSave extends HttpServlet
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
        User usr = null;
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            // Only admins allowed.
            usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                return;
                }
            
            String hcName       = request.getParameter("hc_name");
            String hcAddress1   = request.getParameter("hc_addr1");
            String hcAddress2   = request.getParameter("hc_addr2");
            String hcCity       = request.getParameter("hc_city");
            String hcCountry    = request.getParameter("hc_country");
            String hcVAT        = request.getParameter("hc_vat");
            String hcTimezone   = request.getParameter("hc_timezone");
            String hcLocale     = request.getParameter("hc_locale");
            String hcOverbook   = request.getParameter("hc_overbook");
            boolean allowOverbooking = Boolean.parseBoolean(hcOverbook);
            
            // Test the V.A.T. parameter.
            Double dblVat;
            Boolean parseOK = true;
            dblVat = Util.StrToDouble(hcVAT, parseOK);
            if (! parseOK) 
                {
                out.print(usr.Txt(Loc.MISSINGNUMBER));
                return;
                }
            
            // Update the configuration.
            DBR res = Delphi.Inst().HotelUpdate(
                hcName,
                hcAddress1,
                hcAddress2,
                hcCity,
                hcCountry,
                dblVat,
                hcTimezone,
                hcLocale,
                allowOverbooking);
            if (! res.OK()) 
                {
                // Failed to save.
                out.print(usr.Txt(Loc.ERRSAVE));
                return;
                }
            
            // Store the hotel object with the new settings in the session.
            Hotel hot = (Hotel)res.Result();
            HttpSession session = request.getSession(false);
            session.setAttribute(Sess.HOTEL, hot);
            
            out.print("ok");
            }
        catch (RuntimeException ex) 
            {
            AppLog.Instance().Error("HCSave() " + ex.getMessage());
            if (usr != null) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
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
