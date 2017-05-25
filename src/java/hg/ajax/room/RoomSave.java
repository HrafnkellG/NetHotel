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
 * Save a new room.
 */
/* ========================================================================= */
public class RoomSave extends HttpServlet
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
            // User must be an admin.
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                return;
                }
            
            // Parameters
            String rno    = request.getParameter("room_no");
            String rsize  = request.getParameter("room_size");
            String rfloor = request.getParameter("room_floor");
            String rtype  = request.getParameter("room_type");
            
            if (!Util.StringOK(rno)) 
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
            
            DBR resCheckName = Delphi.Inst().RoomGet(rno);
            if (resCheckName.OK()) 
                {
                out.print(usr.Txt(Loc.ERR_NAMEINUSE) + " (" + rno + ")");
                return;
                }
            
            DBR res = Delphi.Inst().RoomSave(rno, irsize, irfloor, rtype);
            if (!res.OK()) 
                {
                out.print(usr.Txt(Loc.ERRSAVE));
                return;
                }
            
            // New room definition has been saved.
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
