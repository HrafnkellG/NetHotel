package hg.ajax.tabs;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.db.Room;
import hg.db.User;
import hg.html5.Table;
import hg.html5.TableHead;
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
 * The status of all the rooms, are they occupied and have they been cleaned.
 */
/* ========================================================================= */
public class TabRoomStatus extends HttpServlet
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
        AppLog.Instance().Info("TabRoomStatus.processRequest() - Entered");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            // Need a logged in user.
            User usr = Util.getUserForSession(request);
            if (usr == null) 
                {
                AppLog.Instance().Info("TabRoomStatus.processRequest() - No user");
                return;
                }
            
            // Create a table with 6 columns.
            String[] colHeaders = new String[6];
            colHeaders[0] = usr.Txt(Loc.ROOMNO);
            colHeaders[1] = usr.Txt(Loc.SIZE);
            colHeaders[2] = usr.Txt(Loc.TYPE);
            colHeaders[3] = usr.Txt(Loc.FLOOR);
            colHeaders[4] = usr.Txt(Loc.OCCUPIED);
            colHeaders[5] = usr.Txt(Loc.CLEAN);
            Table status = new Table(colHeaders, "superfluous");
            TableHead th = status.getHead();
            th.setCellCSS(0, CSS.CCOL_RNO);
            th.setCellCSS(1, CSS.CCOL_RSIZE);
            th.setCellCSS(2, CSS.CCOL_RTYPE);
            th.setCellCSS(3, CSS.CCOL_FLOOR);
            th.setCellCSS(4, CSS.CCOL_RNO);
            th.setCellCSS(5, CSS.CCOL_RNO);
            
            status.setCaption(usr.Txt(Loc.ROOMSTATUS));
            Room.ShowInTableStatus(status, usr);
            
            out.write(status.Render());
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
