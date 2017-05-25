package hg.pages;

import hg.cons.Loc;
import hg.cons.Loch;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.db.User;
import hg.html5.HelpPage;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Create the help page.
 */
/* ========================================================================= */
public class Help extends HttpServlet
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
            User usr = Util.getUserForSession(request);
            if (usr == null) 
                {
                response.sendRedirect(Paths.LOGIN);
                return;
                }
            
            String par = request.getParameter("help_id");
            int helpID = Integer.parseInt(par);
            
            HelpPage hp;
            switch (helpID) 
                {
                case PageID.MAINPAGE        : hp = Main(usr); break;
                case PageID.USERNEW         : hp = UserNew(usr); break;
                case PageID.USEREDIT        : hp = UserEdit(usr); break;
                case PageID.USERPREFS       : hp = UserPrefs(usr); break;
                case PageID.USERSPAGE       : hp = UsersPage(usr); break;
                case PageID.ROOMSPAGE       : hp = RoomsPage(usr); break;
                case PageID.ROOMEDIT        : hp = RoomEdit(usr); break;
                case PageID.ROOMNEW         : hp = RoomNew(usr); break;
                case PageID.HOTELCONFIG     : hp = HotelConfig(usr); break;
                case PageID.PRICELISTSPAGE  : hp = PriceLists(usr); break;
                case PageID.CUSTOMERS       : hp = Customers(usr); break;
                case PageID.PL_DIALOG       : hp = PLDialog(usr); break;
                default : throw new IllegalArgumentException();
                }
            
            out.print(hp.Render());
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

    private HelpPage Main(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.MAINPAGE));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(null, usr.Hlp(Loch.MAINPAGE));
        p.AddSection(usr.Hlp(Loch.THETITLE), usr.Hlp(Loch.THETITLETXT));
        p.AddSection(usr.Hlp(Loch.THEMENU), usr.Hlp(Loch.THEMENUTXT));
        p.AddSection(usr.Hlp(Loch.ACTIONBUTTONS), usr.Hlp(Loch.ACTIONBUTTONSTXT));
        p.AddSection("Tabs", "TODO");
        
        return p;
        }

    private HelpPage UserNew(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.NEWUSER));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(usr.Txt(Loc.NAME), usr.Hlp(Loch.USERNAME));
        p.AddSection(usr.Txt(Loc.PASSWORD), usr.Hlp(Loch.PASSWORD));
        p.AddSection(usr.Txt(Loc.ACCESS), usr.Hlp(Loch.ACCESS));
        p.AddSection(usr.Txt(Loc.LANGUAGE), usr.Hlp(Loch.LANGUAGE));
        p.AddSection(usr.Txt(Loc.STYLE), usr.Hlp(Loch.STYLE));
        
        return p;
        }

    private HelpPage UserEdit(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.EDITUSR));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(usr.Txt(Loc.NAME), usr.Hlp(Loch.USERNAME));
        p.AddSection(usr.Txt(Loc.PASSWORD), usr.Hlp(Loch.PASSWORD));
        p.AddSection(usr.Txt(Loc.ACCESS), usr.Hlp(Loch.ACCESS));
        p.AddSection(usr.Txt(Loc.LANGUAGE), usr.Hlp(Loch.LANGUAGE));
        p.AddSection(usr.Txt(Loc.STYLE), usr.Hlp(Loch.STYLE));
        p.AddSection(usr.Txt(Loc.DELETE), usr.Hlp(Loch.USERDELETE));
        
        return p;
        }

    private HelpPage UserPrefs(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.PREFERENCES));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(usr.Txt(Loc.PASSWORD), usr.Hlp(Loch.PASSWORD));
        p.AddSection(usr.Txt(Loc.LANGUAGE), usr.Hlp(Loch.LANGUAGE));
        p.AddSection(usr.Txt(Loc.STYLE), usr.Hlp(Loch.STYLE));
        
        return p;
        }

    private HelpPage UsersPage(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.USERS));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(null, usr.Hlp(Loch.USERSPAGE));
        
        return p;
        }

    private HelpPage RoomsPage(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.ROOMS));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(null, usr.Hlp(Loch.ROOMSPAGE));
        
        return p;
        }

    private HelpPage RoomEdit(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.ROOMEDIT));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(usr.Txt(Loc.ROOMNO), usr.Hlp(Loch.ROOMNO));
        p.AddSection(usr.Txt(Loc.SIZE), usr.Hlp(Loch.ROOMSIZE));
        p.AddSection(usr.Txt(Loc.TYPE), usr.Hlp(Loch.ROOMTYPE));
        p.AddSection(usr.Txt(Loc.FLOOR), usr.Hlp(Loch.ROOMFLOOR));
        p.AddSection(usr.Txt(Loc.DELETE), usr.Hlp(Loch.ROOMDELETE));
        
        return p;
        }

    private HelpPage RoomNew(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.ROOMNEW));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(usr.Txt(Loc.ROOMNO), usr.Hlp(Loch.ROOMNO));
        p.AddSection(usr.Txt(Loc.SIZE), usr.Hlp(Loch.ROOMSIZE));
        p.AddSection(usr.Txt(Loc.TYPE), usr.Hlp(Loch.ROOMTYPE));
        p.AddSection(usr.Txt(Loc.FLOOR), usr.Hlp(Loch.ROOMFLOOR));
        
        return p;
        }

    private HelpPage HotelConfig(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.HCONFIG));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(null, usr.Hlp(Loch.HCONF_DESCRIPTION));
        p.AddSection(usr.Hlp(Loch.HCONF_ADDRES), usr.Hlp(Loch.HCONF_ADDRESTXT));
        p.AddSection(usr.Txt(Loc.VAT), usr.Hlp(Loch.HCONF_VATTXT));
        p.AddSection(usr.Txt(Loc.TIMEZONE), usr.Hlp(Loch.HCONF_TIMEZONETXT));
        p.AddSection(usr.Txt(Loc.LOCALE), usr.Hlp(Loch.HCONF_LOCALETXT));
        p.AddSection(usr.Txt(Loc.OVERBOOKING), usr.Hlp(Loch.HCONF_OVERBOOKTXT));
        
        return p;
        }

    
    private HelpPage PriceLists(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.PRICELISTS));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(null, usr.Hlp(Loch.PL_GENERAL));
        return p;
        }

    private HelpPage PLDialog(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.PRICELISTS));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(null, usr.Hlp(Loch.PL_DLG_GEN));
        p.AddSection(usr.Txt(Loc.NAME), usr.Hlp(Loch.PL_DLG_NAME));
        p.AddSection(usr.Txt(Loc.ACTIVEFROM), usr.Hlp(Loch.PL_DLG_FIRSTDATE));
        p.AddSection(usr.Txt(Loc.ACTIVETO), usr.Hlp(Loch.PL_DLG_LASTTDATE));
        
        return p;
        }

    private HelpPage Customers(User usr)
        {
        HelpPage p = new HelpPage(usr.Txt(Loc.HELP), usr.Txt(Loc.CUSTOMERS));
        p.AddCSSLink("styles/reset.css");
        p.AddCSSLink(usr.getStyle() + "help.css");
        
        p.AddSection(null, usr.Hlp(Loch.CUST_GENERAL));
        
        return p;
        }

    }
