package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
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
 * Here an administrator can see all defined users, create new ones and
 * edit old ones.
 */
/* ========================================================================= */
public class Users extends HttpServlet
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
        User usr = Util.getAdminForSession(request);
        if (usr == null) 
            {
            response.sendRedirect(Paths.LOGIN);
            return;
            }
        HttpSession theSession = request.getSession(false);
        Hotel hot = (Hotel)theSession.getAttribute(Sess.HOTEL);
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            Page users = new Page(usr.Txt(Loc.MANAGEUSERS));
            Util.CommonElementsOnPages(users, usr, hot, PageID.USERSPAGE);
            users.AddJSLink("scripts/users.js");
            
            Actions acts = users.getActions();
            acts.AddAction(usr.Txt(Loc.NEWUSER), "javascript:NewUser()");
            
            AddTable(users, usr);
            
            // Divs for jQuery dialogs.
            users.AddDiv("useredit");
            users.AddDiv("usernew");
            
            // Localized text used by the scripts.
            users.AddJSCode("var usertitlenew = '"  + usr.Txt(Loc.DEFNEWUSR) + "';\n");
            users.AddJSCode("var usertitleedit = '" + usr.Txt(Loc.EDITUSR)   + "';\n");
            
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_users", null);
            users.AddElement(extraDiv);
            
            out.print(users.Render());
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
     * Add a table of defined users. There are 5 columns with the following information:
     *      user name
     *      password
     *      access level
     *      language
     *      styling
     * 
     * @param p Target page.
     * @param usr Logged in user.
     */
    /* --------------------------------------------------------------------- */
    private void AddTable(Page p, User usr)
        {
        DBR res = Delphi.Inst().Users();
        if (!res.OK()) 
            {
            return;
            }
        ArrayList<User> usersList = (ArrayList)res.Result();
        
        String[] headers = new String[5];
        headers[0] = usr.Txt(Loc.NAME);
        headers[1] = usr.Txt(Loc.PASSWORD);
        headers[2] = usr.Txt(Loc.ACCESS);
        headers[3] = usr.Txt(Loc.LANGUAGE);
        headers[4] = usr.Txt(Loc.STYLE);
        
        Table tbl = p.AddTable(headers, CSS.CGRID);
        tbl.setCaption(usr.Txt(Loc.DEFINEDUSERS));
        
        // Set the style of the headers.
        TableHead head = tbl.getHead();
        head.setCellCSS(0, CSS.CCOL_NAME);
        head.setCellCSS(1, CSS.CCOL_PASSW);
        head.setCellCSS(2, CSS.CCOL_ACCESS);
        head.setCellCSS(3, CSS.CCOL_LANG);
        head.setCellCSS(4, CSS.CCOL_STYLE);
        
        TableBody tbody = tbl.getBody();
        boolean isOdd = true;
        for (User u : usersList) 
            {
            TableRow row = tbody.AddRow();
            
            // DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO
            // For the demo we do not allow the user to edit the "all" user.
            // His id = 0.
            if (u.getIdentifier() != 0) 
                {
                row.setEventOnclick("UserEdit(" + u.getIdentifier() + ")");
                }
            // DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO DEMO
            
            //row.setEventOnclick("UserEdit(" + u.getIdentifier() + ")");
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
            
            // Access level, descriptive text for the administrator.
            String access = usr.Txt(Loc.USER);
            if (u.isAdmin()) 
                {
                access = usr.Txt(Loc.ADMINISTRATOR);
                }
            u.ShowInRow(row, access);
            }
        }

    }
