package hg.ajax.pl;

import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Pricelist;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Get the period for a specific pricelist. We get 1 parameter which is the
 * id for the list. We return the from and to dates as 1 string which has
 * the format YYYYMMDDYYYYMMDD. If there is an error we return "error".
 */
/* ========================================================================= */
public class PLGetPeriod extends HttpServlet
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
            // We don't bother with a logged in user, not needed.
            
            String strID = request.getParameter("pl_databaseid");
            if (! Util.IntOK(strID)) 
                {
                out.print("error");
                return;
                }
            
            int dbID = Integer.parseInt(strID);
            DBR res = Delphi.Inst().Pricelist(dbID);
            if (! res.OK()) 
                {
                out.print("error");
                return;
                }
            
            Pricelist pl = (Pricelist)res.Result();
            String perPacked = pl.getPeriodPacked();
            out.print(perPacked);
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
