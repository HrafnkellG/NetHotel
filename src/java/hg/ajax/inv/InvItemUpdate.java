package hg.ajax.inv;

import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.InvItem;
import hg.db.User;
import hg.util.Util;
import hg.util.hgBoolean;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/* ========================================================================= */
/**
 * Update an invoice item.
 * 
 * @author hg
 */
/* ========================================================================= */
public class InvItemUpdate extends HttpServlet
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
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            
            // Get and check parameters.
            String sitemAmount = request.getParameter("inv_itemamount");
            hgBoolean moneyOK = new hgBoolean();
            long itemAmount = hot.StringToMoney(sitemAmount, moneyOK);
            if (! moneyOK.getValue())
                {
                out.print(usr.Txt(Loc.MISSINGNUMBER));
                return;
                }
            
            String sitemID = request.getParameter("inv_itemid");
            if (! Util.IntOK(sitemID)) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
                return;
                }
            int itemID = Integer.parseInt(sitemID);
            
            // The parameters are ok, update the item.
            DBR dbrItem = Delphi.Inst().InvoiceItem(itemID);
            if (!dbrItem.OK()) 
                {
                out.print(dbrItem.ErrorMessage());
                return;
                }
            InvItem item = (InvItem)dbrItem.Result();
            
            // Change price pr. item, then update database.
            item.setPricePerItem(itemAmount);
            
            DBR dbrResult = Delphi.Inst().InvoiceItemUpdate(item);
            if (! dbrResult.OK()) 
                {
                out.print(usr.Txt(Loc.ERRSAVE) + " " + dbrResult.ErrorMessage());
                return;
                }
            
            // Success
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
