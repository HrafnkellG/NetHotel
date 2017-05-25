package hg.ajax.inv;

import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.InvItem;
import hg.db.User;
import hg.html5.Dialog;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/* ========================================================================= */
/**
 * Get an invoice item and display it in a jQuery dialog.
 * @author hg
 */
/* ========================================================================= */
public class InvItemEdit extends HttpServlet
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
                return;
                }
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            
            // Get our parameter for the item.
            String sitemID = request.getParameter("inv_itemid");
            if (! Util.IntOK(sitemID)) 
                {
                // We return nothing if this is not an integer.
                return;
                }
            int itemID = Integer.parseInt(sitemID);
            DBR dbrItem = Delphi.Inst().InvoiceItem(itemID);
            if (! dbrItem.OK()) 
                {
                String errString = "<p>" + dbrItem.ErrorMessage() + "</p>";
                out.print(errString);
                return;
                }
            InvItem item = (InvItem)dbrItem.Result();
            Dialog dlg = new Dialog();
            item.showInEditDialog(dlg, usr, hot);
            
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
