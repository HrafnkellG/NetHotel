package hg.ajax.user;

import hg.cons.Loc;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.User;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Create a new user.
 */
/* ========================================================================= */
public class UserSave extends HttpServlet
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
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                return;
                }
            
            // GET PARAMETERS
            String uname = request.getParameter("user_name");
            String upass = request.getParameter("user_pass");
            String uacc  = request.getParameter("user_access");
            String ulang = request.getParameter("user_lang");
            String ustyl = request.getParameter("user_style");
            
            // CHECK THE NAME AND PASSWORD, THE OTHERS ARE FROM DROP-DOWNS
            if (!Util.StringOK(uname)) 
                {
                out.print(usr.Txt(Loc.MISS_NAME));
                return;
                }
            if (!Util.StringOK(upass)) 
                {
                out.print(usr.Txt(Loc.MISS_PASSWORD));
                return;
                }
            
            boolean nameDefined = Delphi.Inst().UserNameExists(uname);
            if (nameDefined) 
                {
                out.print(usr.Txt(Loc.ERR_NAMEINUSE));
                return;
                }
            
            DBR res = Delphi.Inst().UserNew(uname, upass, uacc, ulang, ustyl);
            if (!res.OK()) 
                {
                out.print("User " + uname + "could not be stored.");
                return;
                }
            
            // New user has been saved.
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
