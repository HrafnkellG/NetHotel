package hg.ajax.tabs;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.db.DBR;
import hg.db.Delphi;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Tab on the main page showing a list of guests which are currently checked in.
 */
/* ========================================================================= */
public class TabCheckedin extends HttpServlet
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
        AppLog.Instance().Info("TabCheckedin.processRequest() - Entered");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                AppLog.Instance().Info("TabCheckedin.processRequest() - No user");
                return;
                }
            
            // List of those reservations with guests checked in.
            DBR resCheckedin = Delphi.Inst().ReservationsCheckedin();
            if (!resCheckedin.OK()) 
                {
                out.print(resCheckedin.ErrorMessage());
                return;
                }
            ArrayList<Res> checkedIn = (ArrayList)resCheckedin.Result();
            if (checkedIn.isEmpty()) 
                {
                out.print("<p>Nothing found</p>");
                return;
                }
            
            // The table has 7 columns, provide the header texts.
            String[] colh = new String[7];
            colh[0] = usr.Txt(Loc.NAME);
            colh[1] = usr.Txt(Loc.ROOMNO);
            colh[2] = usr.Txt(Loc.ROOMSIZE);
            colh[3] = usr.Txt(Loc.TYPE);
            colh[4] = usr.Txt(Loc.FLOOR);
            colh[5] = usr.Txt(Loc.ARRIVING);
            colh[6] = usr.Txt(Loc.DEPARTING);
            
            Table tableCheckedin = new Table(colh, null);
            tableCheckedin.setCaption(usr.Txt(Loc.CHECKEDIN));
            
            // Assign  CSS to the columns.
            TableHead th = tableCheckedin.getHead();
            th.setCellCSS(0, CSS.CCOL_NAME);
            th.setCellCSS(1, CSS.CCOL_RNO);
            th.setCellCSS(2, CSS.CCOL_RSIZE);
            th.setCellCSS(3, CSS.CCOL_RTYPE);
            th.setCellCSS(4, CSS.CCOL_FLOOR);
            th.setCellCSS(5, CSS.CCOL_DATE);
            th.setCellCSS(6, CSS.CCOL_DATE);
            
            // Add 1 row to the table for each guest which is checked in.
            boolean isOdd = true;
            TableBody bod = tableCheckedin.getBody();
            for (Res reservation : checkedIn) 
                {
                ArrayList<ResGuest> guestList = reservation.getGuests();
                for (ResGuest guest : guestList)
                    {
                    // Only show guests which ARE checked in.
                    if (! guest.IsCheckedIn()) 
                        {
                        continue;
                        }
                    TableRow row = bod.AddRow();
                    guest.ShowInRowCheckedinTab(row);
                    if (isOdd) 
                        { row.AddCSSClass(CSS.CROW_ODD); }
                    else
                        { row.AddCSSClass(CSS.CROW_EVEN); }
                    isOdd = !isOdd;
                    
                    // Javascript to handle row selection.
                    row.setEventOnclick("TabCheckedinRowSelect(" + reservation.getDBID() + ")");
                    }
                }
            out.print(tableCheckedin.Render());
            }//END-OF-TRY
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
