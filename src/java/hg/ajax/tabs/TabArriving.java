package hg.ajax.tabs;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.Loch;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.Res;
import hg.db.ResGuest;
import hg.db.User;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.util.AppLog;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Called by jQuery lib to fill the tab on the main page for guests arriving today.
 */
/* ========================================================================= */
public class TabArriving extends HttpServlet
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
        AppLog.Instance().Info("TabArriving.processRequest() - Entered");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                AppLog.Instance().Info("TabArriving.processRequest() - No user");
                return;
                }
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            Date  todaysDate = hot.getDate();
            
            // List of those reservations which have guests arriving today.
            DBR resultArriving = Delphi.Inst().ReservationsArriving();
            if (! resultArriving.OK()) 
                {
                out.print("<p>" + resultArriving.ErrorMessage() + "</p>");
                return;
                }
            ArrayList<Res> arriving = (ArrayList)resultArriving.Result();
            if (arriving.isEmpty()) 
                {
                out.print("<p>" + usr.Txt(Loc.NOGUESTARRIVE) + "</p>");
                return;
                }
            
            // The table has 5 columns, provide the header texts.
            String[] colh = new String[5];
            colh[0] = usr.Txt(Loc.RESERVATION);
            colh[1] = usr.Txt(Loc.NAME);
            colh[2] = usr.Txt(Loc.ROOMNO);
            colh[3] = usr.Txt(Loc.ARRIVING);
            colh[4] = usr.Txt(Loc.DEPARTING);
            
            // Create table.
            Table tableArrive = new Table(colh, null);
            tableArrive.setCaption(usr.Txt(Loc.GCHECKINTODAY));
            
            // Assign  CSS to the columns.
            TableHead th = tableArrive.getHead();
            th.setCellCSS(0, CSS.CCOL_RESNO);
            th.setCellCSS(1, CSS.CCOL_NAME);
            th.setCellCSS(2, CSS.CCOL_RNO);
            th.setCellCSS(3, CSS.CCOL_DATE);
            th.setCellCSS(4, CSS.CCOL_DATE);
            
            // Supply the rows.
            boolean isOdd = true;
            TableBody bod = tableArrive.getBody();
            for (Res reservation : arriving) 
                {
                ArrayList<ResGuest> gs = reservation.getGuests();
                for (ResGuest g : gs) 
                    {
                    // Only show guests which have NOT checked in.
                    if (g.IsCheckedIn()) 
                        {
                        continue;
                        }
                    
                    TableRow r = bod.AddRow();
                    r.TitlePopupText(usr.Hlp(Loch.POP_ROW_ARRIVING));
                    g.ShowInRowArriveTab(r, todaysDate);
                    if (isOdd) 
                        { r.AddCSSClass(CSS.CROW_ODD); }
                    else
                        { r.AddCSSClass(CSS.CROW_EVEN); }
                    isOdd = !isOdd;
                    
                    // Hook up the javascript which handles row selection.
                    r.setEventOnclick("TabArriveRowSelect(" + reservation.getDBID() + ")");
                    }
                }
            
            out.print(tableArrive.Render());
            }// END-OF-TRY
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
