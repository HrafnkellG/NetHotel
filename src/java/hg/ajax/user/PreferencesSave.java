package hg.ajax.user;

import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.User;
import hg.util.Country;
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
 * Save the preferences for the currently logged in user. The parameters
 * are as follows:
 *      user_pass
 *      user_lang
 *      user_style
 * Returns 3 possible results:
 *      "ok"         = write to database succeeded
 *      "error text" = write failed, text describes error
 *      ""           = empty string, no user is logged in
 */
/* ========================================================================= */
public class PreferencesSave extends HttpServlet
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
                return;
                }
            
            String usrPass  = request.getParameter("user_pass");
            String usrLang  = request.getParameter("user_lang");
            String usrStyle = request.getParameter("user_style");
            
            // Check the password, the others are selected from drop-downs.
            if (!Util.StringOK(usrPass)) 
                {
                out.print(usr.Txt(Loc.MISS_PASSWORD));
                return;
                }
            
            DBR res = Delphi.Inst().UserUpdate(usr, usrPass, usrLang, usrStyle);
            if (!res.OK()) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
                return;
                }
            
            // Update the session with the changed user and list of countries.
            User u = (User)res.Result();
            HttpSession session = request.getSession(false);
            session.setAttribute(Sess.USER, u);
            Country[] cs = Util.CreateCountryList(u.getLocale());
            session.setAttribute(Sess.COUNTRIES, cs);
            
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
