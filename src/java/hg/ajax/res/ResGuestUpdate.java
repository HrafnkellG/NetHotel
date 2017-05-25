package hg.ajax.res;

import hg.cons.Loc;
import hg.cons.Paths;
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
 * Update information about a specific guest.
 */
/* ========================================================================= */
public class ResGuestUpdate extends HttpServlet
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
            // If a user is not logged in, do nothing.
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                out.print("Not logged in.");
                return;
                }
            
            // Get raw parameters.
            String sDbID        = request.getParameter("resg_guest_db_id");
            String sName        = request.getParameter("resg_name");
            String sISOCountry  = request.getParameter("resg_nat");
            String sIdentifier  = request.getParameter("resg_ident");
            String sGender      = request.getParameter("resg_gender");
            String sRoomNo      = request.getParameter("resg_roomno");
            
            // Check & convert parameters.
            if (! Util.IntOK(sDbID)) 
                {
                out.print("Bad database ID for guest.");
                return;
                }
            int dbID = Integer.parseInt(sDbID);
            
            int gender = 0;
            if (Util.IntOK(sGender)) 
                {
                gender = Integer.parseInt(sGender);
                if (gender < 0 || gender > 3)
                    {
                    gender = 0;
                    }
                }
            
            // Perform update.
            DBR qr = Delphi.Inst().ReservationGuestUpdate(
                dbID,
                sName,
                sISOCountry,
                sIdentifier,
                gender,
                sRoomNo
                );
            if (! qr.OK()) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
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
