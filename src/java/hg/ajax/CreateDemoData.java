package hg.ajax;

import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.User;
import hg.util.Country;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/* ========================================================================= */
/**
 * Create demonstration data for the hotel.
 */
/* ========================================================================= */
public class CreateDemoData extends HttpServlet
    {
    private SimpleDateFormat _DtoISO = new SimpleDateFormat("yyyy-MM-dd");
    private int _reservationID;
    private int _invoiceID = 0;
    private String _resDateCreated;

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
        _reservationID = 1;
        ResCreationDate();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            Delphi db = Delphi.Inst();
            db.sqlExecuteInserUpdateDelete(_sqlWipeAndCreateDB);
            db.sqlExecuteInserUpdateDelete(Occupied101());
            db.sqlExecuteInserUpdateDelete(Res1045());
            db.sqlExecuteInserUpdateDelete(Res106());
            db.sqlExecuteInserUpdateDelete(Res107());
            db.sqlExecuteInserUpdateDelete(Res202345());
            db.sqlExecuteInserUpdateDelete(ResArrive1056());
            db.sqlExecuteInserUpdateDelete(ResArrive108());
            db.sqlExecuteInserUpdateDelete(ResArrive201());
            db.sqlExecuteInserUpdateDelete(ResArrive20234());
            
            // Must recreate the objects stored in the session object.
            DBR res = db.UserGet("all", "open");
            User usr = (User)res.Result();
            DBR resHot = db.HotelGet();
            Hotel hot = (Hotel)resHot.Result();
            Country[] cs = Util.CreateCountryList(usr.getLocale());
            
            HttpSession session = request.getSession();
            session.setAttribute(Sess.USER, usr);
            session.setAttribute(Sess.HOTEL, hot);
            session.setAttribute(Sess.COUNTRIES, cs);
            
            out.print("ok");
            }
        catch (Exception ex) 
            {
            out.print("Demo creation failed.");
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

    // <editor-fold defaultstate="collapsed" desc="Wipe and create">
    // Copied from DB_CREATE.sql
    private String _sqlWipeAndCreateDB = 
        "DELETE FROM customer;" +
        "DELETE FROM hotel;" +
        "DELETE FROM invitems;" +
        "DELETE FROM invoice;" +
        "DELETE FROM pitems;" +
        "DELETE FROM price;" +
        "DELETE FROM res;" +
        "DELETE FROM resg;" +
        "DELETE FROM room;" +
        "DELETE FROM user WHERE user.id <> 0;" +
        "" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('101', 1, 1, 'MIN', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('102', 1, 1, 'MIN', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('103', 1, 1, 'MIN', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('104', 1, 1, 'MID', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('105', 1, 1, 'MID', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('106', 1, 1, 'MID', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('107', 1, 1, '',    'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('108', 1, 1, 'MAX', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('201', 1, 2, 'MID', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('202', 1, 2, 'MID', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('203', 2, 2, 'MAX', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('204', 2, 2, 'MID', 'false', 'true');" +
        "INSERT INTO Room (roomno, size, floor, type, occupied, clean) VALUES ('205', 2, 2, 'MAX', 'false', 'true');" +
        "" +
        "INSERT INTO HOTEL (" +
        "  name," +
        "  addr1," +
        "  addr2, " +
        "  city," +
        "  country," +
        "  vat," +
        "  timezone," +
        "  locale," +
        "  overbook" +
        ") VALUES (" +
        "  'HOTEL'," +
        "  'n/a', " +
        "  'n/a', " +
        "  'n/a', " +
        "  'n/a'," +
        "  20," +
        "  'GB'," +
        "  'en_GB_'," +
        "  'false');" +
        "" +
        "INSERT INTO PRICE (" +
        " id, usercreated, whencreated,  name,      periodstart,  periodend) " +
        "VALUES (" +
        " 1,  'all',       '2015-01-01', 'General', '2015-01-01', '2115-12-31');" +
        "" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '101', 'MIN', 1, 2000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '102', 'MIN', 1, 2000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '103', 'MIN', 1, 2000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '104', 'MID', 1, 3000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '105', 'MID', 1, 3000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '106', 'MID', 1, 3000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '107', '',    1, 3000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '108', 'MAX', 1, 4000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '201', 'MIN', 1, 2000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (1, '202', 'MIN', 1, 2000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, guest2, extracot) VALUES (1, '203', 'MIN', 2, 3000, 4000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, guest2, extracot) VALUES (1, '204', 'MIN', 2, 3000, 4000, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, guest2, extracot) VALUES (1, '205', 'MIN', 2, 3000, 4000, 1000);" +
        "" +
        "INSERT INTO PRICE (" +
        " id, usercreated, whencreated,  name,        periodstart,  periodend) " +
        "VALUES (" +
        " 2,  'all',       '2015-01-01', 'Artisanal', '2015-01-01', '2025-12-31');" +
        "" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '101', 'MIN', 1, 1600, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '102', 'MIN', 1, 1600, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '103', 'MIN', 1, 1600, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '104', 'MID', 1, 2400, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '105', 'MID', 1, 2400, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '106', 'MID', 1, 2400, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '107', '',    1, 2400, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '108', 'MAX', 1, 3200, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '201', 'MIN', 1, 1600, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, extracot) VALUES (2, '202', 'MIN', 1, 1600, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, guest2, extracot) VALUES (2, '203', 'MIN', 2, 2400, 3200, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, guest2, extracot) VALUES (2, '204', 'MIN', 2, 2400, 3200, 1000);" +
        "INSERT INTO PITEMS (priceid, roomno, roomtype, roomsize, guest1, guest2, extracot) VALUES (2, '205', 'MIN', 2, 2400, 3200, 1000);" +
        "" +
        "INSERT INTO CUSTOMER (" +
        " firstname,              address1,                          address2,                   pricelist) " +
        "VALUES (" +
        " 'Cash',                 '',                                '',                         1);" +
        "INSERT INTO CUSTOMER (" +
        " firstname,              address1,                          address2,                   pricelist) " +
        "VALUES (" +
        " 'Artisanal Tours Inc.', 'Alperton House Bridgewater Road', 'Alperton Wembley HA0 1EH', 2);" +
        "INSERT INTO CUSTOMER (" +
        "firstname,           address1,           postalcode, city,        country,   pricelist) " +
        "VALUES (" +
        "'Iceland Mountains', 'Stekkjastígur 13', '765',      'Reykjavik', 'Iceland', 1);" +
        "";// </editor-fold>

    // Reservation for 101 covers 3 days
    private String Occupied101()
        {
        GregorianCalendar c = new GregorianCalendar();
        c.add(GregorianCalendar.DAY_OF_YEAR, -1);
        String yesterday = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, 2);
        String tomorrow = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "1,"
            + "1,"
            + "'" + yesterday + "',"
            + "'" + tomorrow + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Humphrey Bogart',"
            + "'US',"
            + "null,"
            + "1,"
            + "true,"
            + "'101',"
            + "'" + yesterday + "',"
            + "'" + tomorrow + "'"
            + ");"
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + yesterday + "',"
            + "1,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'101',"
            + "1,"
            + "'" + yesterday + "',"
            + "'" + tomorrow + "',"
            + "2000,"
            + "4000"
            + ");"
            // Mark room as occupied.
            + "update room set occupied = true where roomno = '101';";
        
        _reservationID++;
        _invoiceID++;
        return sqlComm;
        }

    private void ResCreationDate()
        {
        // A week ago.
        GregorianCalendar c = new GregorianCalendar();
        c.add(GregorianCalendar.DAY_OF_YEAR, -7);
        _resDateCreated = _DtoISO.format(c.getTime());
        }

    /* Reservation for rooms 104/105 departing */
    private String Res1045()
        {
        GregorianCalendar c = new GregorianCalendar();
        String today = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, -1);
        String yesterday = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "2,"
            + "2,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Ingrid Bergman',"
            + "'SW',"
            + "null,"
            + "2,"
            + "true,"
            + "'104',"
            + "'" + yesterday + "',"
            + "'" + today + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Paul Henreid',"
            + "'IT',"
            + "null,"
            + "1,"
            + "true,"
            + "'105',"
            + "'" + yesterday + "',"
            + "'" + today + "'"
            + ");"
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + yesterday + "',"
            + "2,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'104',"
            + "1,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + "3000,"
            + "3000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'105',"
            + "1,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + "3000,"
            + "3000"
            + ");"
            + "update room set occupied = true where (roomno = '104' or roomno = '105');";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }

    /* Reservation for room 106 departing */
    private String Res106()
        {
        GregorianCalendar c = new GregorianCalendar();
        String today = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, -2);
        String twodaysago = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "1,"
            + "1,"
            + "'" + twodaysago + "',"
            + "'" + today + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Claude Rains',"
            + "'GB',"
            + "null,"
            + "1,"
            + "true,"
            + "'106',"
            + "'" + twodaysago + "',"
            + "'" + today + "'"
            + ");"
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + twodaysago + "',"
            + "0,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'106',"
            + "1,"
            + "'" + twodaysago + "',"
            + "'" + today + "',"
            + "3000,"
            + "6000"
            + ");"
            + "update room set occupied = true where roomno = '106';";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }

    /* Room 107 departing */
    private String Res107()
        {
        GregorianCalendar c = new GregorianCalendar();
        String today = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, -3);
        String threedaysago = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "1,"
            + "1,"
            + "'" + threedaysago + "',"
            + "'" + today + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Conrad Veidt',"
            + "'DE',"
            + "null,"
            + "1,"
            + "true,"
            + "'107',"
            + "'" + threedaysago + "',"
            + "'" + today + "'"
            + ");"
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + threedaysago + "',"
            + "1,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'107',"
            + "1,"
            + "'" + threedaysago + "',"
            + "'" + today + "',"
            + "3000,"
            + "9000"
            + ");"
            + "update room set occupied = true where roomno = '107';";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }

    /* Rooms 202/203/204/205 departing only 204 has 2 guests */
    private String Res202345()
        {
        GregorianCalendar c = new GregorianCalendar();
        String today = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, -1);
        String yesterday = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "4,"
            + "5,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Sydney Greenstreet',"
            + "'GB',"
            + "null,"
            + "1,"
            + "true,"
            + "'202',"
            + "'" + yesterday + "',"
            + "'" + today + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Peter Lorre',"
            + "'US',"
            + "null,"
            + "1,"
            + "true,"
            + "'203',"
            + "'" + yesterday + "',"
            + "'" + today + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'S. Z. Sakall',"
            + "'US',"
            + "null,"
            + "1,"
            + "true,"
            + "'204',"
            + "'" + yesterday + "',"
            + "'" + today + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Giza Grossner',"
            + "'US',"
            + "null,"
            + "2,"
            + "true,"
            + "'204',"
            + "'" + yesterday + "',"
            + "'" + today + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Madeleine Lebeau',"
            + "'US',"
            + "null,"
            + "2,"
            + "true,"
            + "'205',"
            + "'" + yesterday + "',"
            + "'" + today + "'"
            + ");"
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + yesterday + "',"
            + "1,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'202',"
            + "1,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + "2000,"
            + "2000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'203',"
            + "2,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + "3000,"
            + "3000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'204',"
            + "2,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + "4000,"
            + "4000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'205',"
            + "2,"
            + "'" + yesterday + "',"
            + "'" + today + "',"
            + "3000,"
            + "3000"
            + ");"
            + "update room set occupied = true where (roomno = '202' or roomno = '203' or roomno = '204' or roomno = '205');";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }

    /* Rooms 105/106 arriving */
    private String ResArrive1056()
        {
        GregorianCalendar c = new GregorianCalendar();
        String today = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, 3);
        String threedaysfromnow = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "2,"
            + "2,"
            + "'" + today + "',"
            + "'" + threedaysfromnow+ "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Dooley Wilson',"
            + "'US',"
            + "null,"
            + "1,"
            + "false,"
            + "'105',"
            + "'" + today + "',"
            + "'" + threedaysfromnow + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Joy Page',"
            + "'US',"
            + "null,"
            + "2,"
            + "false,"
            + "'106',"
            + "'" + today + "',"
            + "'" + threedaysfromnow + "'"
            + ");"
            
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + today + "',"
            + "0,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'105',"
            + "1,"
            + "'" + today + "',"
            + "'" + threedaysfromnow + "',"
            + "3000,"
            + "9000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'106',"
            + "1,"
            + "'" + today + "',"
            + "'" + threedaysfromnow + "',"
            + "3000,"
            + "9000"
            + ");";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }

    /* Room 108 arriving (make it overdue by 1 day) */
    private String ResArrive108()
        {
        GregorianCalendar c = new GregorianCalendar();
        c.add(GregorianCalendar.DAY_OF_YEAR, -1);
        String yesterday = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, 2);
        String tomorrow = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "1,"
            + "1,"
            + "'" + yesterday + "',"
            + "'" + tomorrow + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'John Qualen',"
            + "'US',"
            + "null,"
            + "1,"
            + "false,"
            + "'108',"
            + "'" + yesterday + "',"
            + "'" + tomorrow + "'"
            + ");"
            
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + yesterday + "',"
            + "0,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'108',"
            + "1,"
            + "'" + yesterday + "',"
            + "'" + tomorrow + "',"
            + "3000,"
            + "6000"
            + ");";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }

    /* Room 201 arriving */
    private String ResArrive201()
        {
        GregorianCalendar c = new GregorianCalendar();
        String today = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, 1);
        String tomorrow = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "1,"
            + "1,"
            + "'" + today + "',"
            + "'" + tomorrow + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Leonid Kinskey',"
            + "'US',"
            + "null,"
            + "1,"
            + "false,"
            + "'201',"
            + "'" + today + "',"
            + "'" + tomorrow + "'"
            + ");"
            
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + today + "',"
            + "0,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'201',"
            + "1,"
            + "'" + today + "',"
            + "'" + tomorrow + "',"
            + "3000,"
            + "3000"
            + ");";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }

    /* Rooms 202/203/204 arriving 5 guests */
    private String ResArrive20234()
        {
        GregorianCalendar c = new GregorianCalendar();
        String today = _DtoISO.format(c.getTime());
        c.add(GregorianCalendar.DAY_OF_YEAR, 2);
        String twodaysfromnow = _DtoISO.format(c.getTime());
        
        String sqlComm = "insert into RES (id, made, user, custid, nor, nog, periodstart, periodend, invid) values "
            + "("
            + _reservationID + ","
            + "'" + _resDateCreated + "',"
            + "'all',"
            + "0,"
            + "3,"
            + "5,"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "',"
            + _invoiceID
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Curt Bois',"
            + "'US',"
            + "null,"
            + "1,"
            + "false,"
            + "'202',"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Charles Halton',"
            + "'US',"
            + "null,"
            + "1,"
            + "false,"
            + "'203',"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Mary Astor',"
            + "'US',"
            + "null,"
            + "2,"
            + "false,"
            + "'203',"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Victor Sen Yung',"
            + "'US',"
            + "null,"
            + "1,"
            + "false,"
            + "'204',"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "'"
            + ");"
            + "insert into RESG (resid, name, country, passpid, gender, checkedin, roomno, arr, dep) values "
            + "("
            + _reservationID + ","
            + "'Roland Got',"
            + "'US',"
            + "null,"
            + "1,"
            + "false,"
            + "'204',"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "'"
            + ");"
            
            + "insert into INVOICE (user, created, customerid, isclosed) values" 
            + "("
            + "'all',"
            + "'" + today + "',"
            + "0,"
            + "false"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'202',"
            + "1,"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "',"
            + "3000,"
            + "6000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'203',"
            + "2,"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "',"
            + "3000,"
            + "6000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'203',"
            + "2,"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "',"
            + "3000,"
            + "6000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'204',"
            + "2,"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "',"
            + "3000,"
            + "6000"
            + ");"
            + "insert into INVITEMS (invid, roomno, roomsize, arrive, depart, priceprnight, total) values "
            + "("
            + _invoiceID + ","
            + "'204',"
            + "2,"
            + "'" + today + "',"
            + "'" + twodaysfromnow + "',"
            + "3000,"
            + "6000"
            + ");";
            
        _invoiceID++;
        _reservationID++;
        return sqlComm;
        }
    }
