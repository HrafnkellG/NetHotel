package hg.ajax.res;

import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.ResGuest;
import hg.db.User;
import hg.html5.Dialog;
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
 * Show information about a guest in a dialog.
 */
/* ========================================================================= */
public class ResGuestShowDlg extends HttpServlet
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
            
            String sgid = request.getParameter("guest_id");
            if (! Util.IntOK(sgid)) { return; }
            int gid = Integer.parseInt(sgid);
            
            DBR resGuest = Delphi.Inst().ReservationGuest(gid);
            if (! resGuest.OK()) { return; }
            ResGuest rg = (ResGuest)resGuest.Result();
            
            HttpSession session = request.getSession(false);
            Country[] cs = (Country[])session.getAttribute(Sess.COUNTRIES);
            
            Dialog dlg = new Dialog();
            rg.ShowInDialog(dlg, usr, cs);
            
            out.print(dlg.Render());
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
