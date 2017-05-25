package hg.ajax.customers;

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
 * Save info for a new user.
 */
/* ========================================================================= */
public class CustomerSaveNew extends HttpServlet
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
            // Just admins...
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                return;
                }
            
            // Our one requirement: a name.
            String cname = request.getParameter("cust_name");
            if (! Util.StringOK(cname)) 
                {
                out.print(usr.Txt(Loc.MISS_NAME));
                return;
                }
            
            // Get the rest of the parameters.
            String caddr1 = request.getParameter("cust_address1");
            String caddr2 = request.getParameter("cust_address2");
            String cpost  = request.getParameter("cust_postal");
            String ccity  = request.getParameter("cust_city");
            String ccount = request.getParameter("cust_country");
            String sdisc  = request.getParameter("cust_disc");
            String splid  = request.getParameter("cust_pricelist");
            
            if (! Util.StringOK(caddr1)) { caddr1 = ""; }
            if (! Util.StringOK(caddr2)) { caddr2 = ""; }
            if (! Util.StringOK(cpost))  { cpost  = ""; }
            if (! Util.StringOK(ccity))  { ccity  = ""; }
            if (! Util.StringOK(ccount)) { ccount = ""; }
            
            int iplid = -1;
            if (Util.IntOK(splid)) { iplid = Integer.parseInt(splid); }
            
            boolean dblConvertOK = true;
            double ddisc = Util.StrToDouble(sdisc, dblConvertOK);
            if (! dblConvertOK) { ddisc = 0; }
            
            DBR res = Delphi.Inst().CustomerSave(
                cname,
                caddr1,
                caddr2,
                cpost,
                ccity,
                ccount,
                iplid,
                ddisc);
            if (! res.OK()) 
                {
                out.print("Error: " + res.ErrorMessage());
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
