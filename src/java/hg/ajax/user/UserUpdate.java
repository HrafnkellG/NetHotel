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
 * Update inforamtion about an existing user.
 */
/* ========================================================================= */
public class UserUpdate extends HttpServlet
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
                out.print("Not logged in.");
                return;
                }
            
            // Parameters
            String uid   = request.getParameter("user_id");
            String uname = request.getParameter("user_name");
            String upass = request.getParameter("user_pass");
            String uacc  = request.getParameter("user_access");
            String ulang = request.getParameter("user_lang");
            String ustyl = request.getParameter("user_style");
            
            // Convert id and access level.
            int targetUserID = Integer.parseInt(uid);
            boolean targetUserIsAdmin = false;
            if (uacc.compareTo("2") == 0) 
                {
                targetUserIsAdmin = true;
                }
            
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
            
            // Name change: don't raise error of duplicate name if the user
            // is changing himself.
            boolean nameExists = Delphi.Inst().UserNameExists(uname);
            if (nameExists) 
                {
                DBR resUser = Delphi.Inst().UserGet(targetUserID);
                User targetUser = (User)resUser.Result();
                
                // User is not changing himself.
                if (usr.getIdentifier() != targetUserID) 
                    {
                    if (targetUser.getName().compareTo(uname) != 0) 
                        {
                        out.print(usr.Txt(Loc.ERR_NAMEINUSE));
                        return;
                        }
                    }
                // User is changing his own name.
                else if (usr.getName().compareTo(uname) != 0) 
                    {
                    out.print(usr.Txt(Loc.ERR_NAMEINUSE));
                    return;
                    }
                }
            
            // Access change: a user cannot change his own access.
            if (usr.getIdentifier() == targetUserID) 
                {
                if (!targetUserIsAdmin) 
                    {
                    out.print(usr.Txt(Loc.ERR_ACCESSCHANGE));
                    return;
                    }
                }
            
            DBR res = Delphi.Inst().UserUpdate(targetUserID, uname, upass, targetUserIsAdmin, ulang, ustyl);
            if (!res.OK()) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
                return;
                }
            
            // User has been updated.
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
