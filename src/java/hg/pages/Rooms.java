package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.Room;
import hg.db.User;
import hg.html5.Actions;
import hg.html5.Div;
import hg.html5.Page;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* ========================================================================= */
/**
 * Page where an administrator can create, edit and delete rooms.
 */
/* ========================================================================= */
public class Rooms extends HttpServlet
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
            // Must be an admin to view this page.
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                response.sendRedirect(Paths.LOGIN);
                return;
                }
            HttpSession theSession = request.getSession(false);
            Hotel hot = (Hotel)theSession.getAttribute(Sess.HOTEL);
            
            Page p = new Page(usr.Txt(Loc.ROOMS));
            Util.CommonElementsOnPages(p, usr, hot, PageID.ROOMSPAGE);
            
            p.AddJSLink("scripts/rooms.js");
            
            // Action to create a new room.
            Actions acts = p.getActions();
            acts.AddAction(usr.Txt(Loc.ROOMNEW), "javascript:RoomNew()");
            
            AddTable(p, usr);
            
            // Add js variables for localized text.
            p.AddJSCode("var roomtitlenew = '"  + usr.Txt(Loc.ROOMNEW)        + "';\n");
            p.AddJSCode("var roomtitleedit = '" + usr.Txt(Loc.ROOMEDIT)       + "';\n");
            
            // Elements for jQuery so it can create the dialogs.
            p.AddDiv("roomedit");
            p.AddDiv("roomnew");
            
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_rooms", null);
            p.AddElement(extraDiv);
            
            out.print(p.Render());
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

    /* --------------------------------------------------------------------- */
    /**
     * Add a table of all defined rooms to the page.
     * @param p
     * @param usr 
     */
    /* --------------------------------------------------------------------- */
    private void AddTable(Page p, User usr)
        {
        DBR res = Delphi.Inst().Rooms();
        if (!res.OK()) 
            {
            return;
            }
        ArrayList<Room> roomsList = (ArrayList)res.Result();
        
        String[] headers = new String[4];
        headers[0] = usr.Txt(Loc.ROOMNO);
        headers[1] = usr.Txt(Loc.SIZE);
        headers[2] = usr.Txt(Loc.TYPE);
        headers[3] = usr.Txt(Loc.FLOOR);
        
        Table tbl = p.AddTable(headers, CSS.CGRID);
        tbl.setCaption(usr.Txt(Loc.DEFINEDROOMS));
        
        // Set the style of the headers.
        TableHead head = tbl.getHead();
        head.setCellCSS(0, CSS.CCOL_RNO);
        head.setCellCSS(1, CSS.CCOL_RSIZE);
        head.setCellCSS(2, CSS.CCOL_RTYPE);
        head.setCellCSS(3, CSS.CCOL_FLOOR);
        
        TableBody tbody = tbl.getBody();
        boolean isOdd = true;
        for (Room r : roomsList) 
            {
            TableRow row = tbody.AddRow();
            row.setEventOnclick("RoomEdit('" + r.getRoomNo() + "')");
            if (isOdd) 
                {
                row.AddCSSClass(CSS.CROW_ODD);
                isOdd = false;
                }
            else 
                {
                row.AddCSSClass(CSS.CROW_EVEN);
                isOdd = true;
                }
            r.ShowInRow(row);
            }
        }

    }
