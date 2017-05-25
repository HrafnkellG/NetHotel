package hg.ajax.tabs;

import hg.cons.Paths;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Room;
import hg.db.User;
import hg.util.AppLog;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Flip the clean status for the selected room. Return "ok" if success.
 * 
 * @author hg
 */
/* ========================================================================= */
public class TabFlipRoomStatus extends HttpServlet
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
        AppLog.Instance().Info("TabFlipRoomStatus.processRequest() - Entered");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                AppLog.Instance().Info("TabFlipRoomStatus.processRequest() - No user");
                return;
                }
            
            String roomNumber = request.getParameter("roomNum");
            DBR dbrRoom = Delphi.Inst().RoomGet(roomNumber);
            if (! dbrRoom.OK()) 
                {
                return;
                }
            Room r = (Room)dbrRoom.Result();
            boolean status = ! r.getCleanStatus();
            
            String query = "update room set "
                + "clean = " + status + " "
                + "where roomno = '" + roomNumber + "';";
            boolean flipOK = Delphi.Inst().sqlExecuteInserUpdateDelete(query);
            if (! flipOK) 
                {
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
