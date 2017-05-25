package hg.ajax.pl;

import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
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
 * Update a single price-point of an item in the PITEMS table in the database.
 * Returns a string which is the formatted value for the selected locale in
 * the hotel configuration. If something goes wrong the string is "error".
 */
/* ========================================================================= */
public class PLItemCellUpdate extends HttpServlet
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
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            
            // Get the 3 parameters we need.
            String plitemID    = request.getParameter("item_id");
            String columnIndex = request.getParameter("cell_index");
            String cellValue   = request.getParameter("cell_value");
            
            // The first 2 should represent integers.
            if (Util.IntOK(plitemID)    == false ||
                Util.IntOK(columnIndex) == false) 
                {
                out.print("error");
                return;
                }
            
            // The 3rd. parameter should represent a monetary value in the locale
            // chosen in the hotel configuration.
            hgBoolean moneyOK = new hgBoolean();
            long longValue = hot.StringToMoney(cellValue, moneyOK);
            if (! moneyOK.getValue()) 
                {
                out.print("error");
                return;
                }
            
            // Error checking is done, update the database.
            int pliDBID   = Integer.parseInt(plitemID);
            int pliColumn = Integer.parseInt(columnIndex);
            DBR res = Delphi.Inst().PricelistCellUpdate(pliDBID, pliColumn, longValue);
            if (! res.OK()) 
                {
                out.print("error");
                return;
                }
            
            out.print(hot.MoneyToString(longValue));
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
