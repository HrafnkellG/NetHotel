package hg.ajax.room;

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
 * Update info about a room.
 */
/* ========================================================================= */
public class RoomUpdate extends HttpServlet
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
            // Must be an admin.
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                out.print("Not logged in.");
                return;
                }
            
            // Parameters.
            String rnoOld = request.getParameter("room_no_old");
            String rnoNew = request.getParameter("room_no_new");
            String rsize  = request.getParameter("room_size");
            String rfloor = request.getParameter("room_floor");
            String rtype  = request.getParameter("room_type");
            
            if (!Util.StringOK(rnoOld)) 
                {
                out.print(usr.Txt(Loc.MISS_ROOMNO));
                return;
                }
            if (!Util.IntOK(rsize)) 
                {
                out.print(usr.Txt(Loc.MISS_ROOMSIZE));
                return;
                }
            if (!Util.IntOK(rfloor)) 
                {
                out.print(usr.Txt(Loc.MISS_ROOMFLOOR));
                return;
                }
            if (!Util.StringOK(rtype)) 
                {
                rtype = "";
                }
            int irsize  = Integer.parseInt(rsize);
            int irfloor = Integer.parseInt(rfloor);
            
            boolean okToUpdate = true;
            if (rnoNew.compareTo(rnoOld) != 0) 
                {
                // The room no. has changed, make sure the new is not defined.
                DBR resCheckNo = Delphi.Inst().RoomGet(rnoNew);
                if (resCheckNo.OK()) 
                    {
                    okToUpdate = false;
                    }
                }
            if (!okToUpdate) 
                {
                out.print(usr.Txt(Loc.ERR_NAMEINUSE));
                return;
                }
            
            DBR res = Delphi.Inst().RoomUpdate(rnoOld, rnoNew, irsize, irfloor, rtype);
            if (!res.OK()) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
                return;
                }
            
            // Update succeeded.
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
