package hg.db;

import hg.intf.IPriceItem;
import hg.util.AppLog;
import hg.util.Util;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Exception;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/* ========================================================================= */
/**
 * Access to the database goes through this class.
 */
/* ========================================================================= */
public class Delphi
    {
    /** The only allowed instance of the Delphi class.
     */
    private static final Delphi _instance = new Delphi();
    
    /** The connection to the NetHotel database.
     */
    private Connection _hsqldbConnection;
    
    /** Convert a floating-point number to a string, with no thousand separators
     * and the decimal point is a period.
     */
    private DecimalFormat _FtoStr = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));
    
    /** Date to string in the ISO standard. 
     */
    private SimpleDateFormat _DtoISO = new SimpleDateFormat("yyyy-MM-dd");
    
    /* --------------------------------------------------------------------- */
    /**
     * Replace all instances of a single-quote(') with two.
     */
    /* --------------------------------------------------------------------- */
    private String Apos(String s) 
        {
        return s.replaceAll("'", "''");
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Convert a GregorianCalendar date into a ISO date.
     */
    /* --------------------------------------------------------------------- */
    private String _GCtoISO(GregorianCalendar srcDate) 
        {
        int year   = srcDate.get(GregorianCalendar.YEAR);
        int month  = srcDate.get(GregorianCalendar.MONTH) + 1;
        int day    = srcDate.get(GregorianCalendar.DAY_OF_MONTH);
        int hour   = srcDate.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = srcDate.get(GregorianCalendar.MINUTE);
        int second = srcDate.get(GregorianCalendar.SECOND);
        
        String syear   = Integer.toString(year);
        String smonth  = Integer.toString(month);
        String sday    = Integer.toString(day);
        String shour   = Integer.toString(hour);
        String sminute = Integer.toString(minute);
        String ssecond = Integer.toString(second);
        
        if (smonth.length()  == 1) { smonth  = "0" + smonth; }
        if (sday.length()    == 1) { sday    = "0" + sday; }
        if (shour.length()   == 1) { shour   = "0" + shour; }
        if (sminute.length() == 1) { sminute = "0" + sminute; }
        if (ssecond.length() == 1) { ssecond = "0" + ssecond; }
        
        return syear + "-" + smonth + "-" + sday + " " + shour + ":" + sminute + ":" + ssecond;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the currently highest id used by a reservation.
     */
    /* --------------------------------------------------------------------- */
    public int getResMaxID() 
        {
        int result = -1;
        String query = "select max(id) as maxid from res;";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rset = stmt.executeQuery(query);
            rset.next();
            result = rset.getInt("maxid");
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("_getResMaxID() " + ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {}
            }
        
        return result;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the currently highest id used by an invoice.
     */
    /* --------------------------------------------------------------------- */
    public int getInvMaxID() 
        {
        int result = -1;
        String query = "select max(id) as maxid from invoice;";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rset = stmt.executeQuery(query);
            rset.next();
            result = rset.getInt("maxid");
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("_getResMaxID() " + ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {}
            }
        
        return result;
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * A private constructor.
     */
    /* --------------------------------------------------------------------- */
    private Delphi() 
        {
        try
            {
            //String propHome = System.getProperty("user.home");
            String propDir  = System.getProperty("user.dir");
            String con_str  = "jdbc:hsqldb:" + propDir + File.separator + "dbfiles" + File.separator + "nethoteldb";

            AppLog.Instance().Info("Delphi.Delphi() DB connection string=" + con_str);
            
            Class.forName("org.hsqldb.jdbcDriver");
            _hsqldbConnection = DriverManager.getConnection(con_str, "sa", "");
            }
        catch (Exception ex)
            {
            _hsqldbConnection = null;
            AppLog.Instance().Error("Delphi.Delphi() " + ex.getMessage());
            }
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * Get an object of this class.
     */
    /* --------------------------------------------------------------------- */
    public static Delphi Inst() 
        {
        return _instance;
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * Get a user matching the user-name and password.
     * 
     * @param name The user-name.
     * @param passw The password.
     * @return Returns null if a user has not been defined for 
     * the name/pass combination.
     */
    /* --------------------------------------------------------------------- */
    public DBR UserGet(String name, String passw) 
        {
        User              userOK   = null;
        PreparedStatement readStmt = null;
        DBR               r        = new DBR();
        
        String query = "select * from user where name = ? and key = ?";
        try 
            {
            readStmt = _hsqldbConnection.prepareStatement(query);
            readStmt.setString(1, name);
            readStmt.setString(2, passw);
            ResultSet res = readStmt.executeQuery();
            
            if (res != null && res.next()) 
                {
                // Read the values from the record.
                //
                userOK = new User(res.getString("lang"));
                userOK._identifier  = res.getInt("id");
                userOK._name        = res.getString("name");
                userOK._key         = res.getString("key");
                userOK._isAdmin     = res.getBoolean("admin");
                userOK._style       = res.getString("style");
                r.setResult(userOK);
                }
            else 
                {
                // name/key combo not defined.
                //
                r.Failed("User is not defined.");
                return r;
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("UserGet() " + ex.getMessage());
            r.Failed(ex.getMessage());
            } 
        finally 
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                // Ignore.
                }
            }
        
        return r;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Updates a users' password, language and style.
     * 
     * @param usr The user to change.
     * @param usrPass 
     * @param usrLang
     * @param usrStyle
     * @return An instance of the changed user.
     */
    /* --------------------------------------------------------------------- */
    public DBR UserUpdate(User usr, String usrPass, String usrLang, String usrStyle)
        {
        DBR res = new DBR();
        
        // Update user with the supplied info.
        String sqlCommand = "update user set "
            + "key = ?, " // Index 1 password
            + "lang = '"  + usrLang  + "', "
            + "style = '" + usrStyle + "' "
            + "where "
            + "id = " + Integer.toString(usr._identifier);

        PreparedStatement updateStmt = null;
        try
            {
            updateStmt = _hsqldbConnection.prepareStatement(sqlCommand);
            updateStmt.setString(1, usrPass);
            updateStmt.executeUpdate();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("UserUpdate() " + ex.getMessage());
            res.Failed("");
            }
        finally
            {
            try
                {
                updateStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        if (res.OK()) 
            {
            // Get an instance of the changed user.
            DBR intermediateRes = UserGet(usr._name, usrPass);
            res.setResult(intermediateRes.Result());
            }
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get a list of all defined users.
     */
    /* --------------------------------------------------------------------- */
    public DBR Users()
        {
        ArrayList<User> list = new ArrayList();
        DBR dbres = new DBR();
        
        String query = "select * from user order by name;";

        Statement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet res = readStmt.executeQuery(query);

            while (res.next())
                {
                String lang = res.getString("lang");
                User usr = new User(lang);
                usr._identifier = res.getInt("id");
                usr._name       = res.getString("name");
                usr._key        = res.getString("key");
                usr._isAdmin    = res.getBoolean("admin");
                usr._style      = res.getString("style");
                
                list.add(usr);
                }
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("Delphi.Users() " + ex.getMessage());
            dbres.Failed("Could not read user list.");
            }
        finally
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }

        dbres.setResult(list);
        return dbres;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Create a new user.
     * 
     * @param name His name.
     * @param pass Password.
     * @param acc Access level.
     * @param lang Language.
     * @param style Style.
     */
    /* --------------------------------------------------------------------- */
    public DBR UserNew(
    String name, 
    String pass, 
    String acc, 
    String language, 
    String style)
        {
        DBR res = new DBR();
        boolean isAdministrator = false;
        if (acc.compareTo("2") == 0) 
            {
            isAdministrator = true;
            }
        
        String insertCom = "insert into user "
            + "("
            + "name,"
            + "key,"
            + "admin,"
            + "lang,"
            + "style"
            + ") "
            + "values "
            + "("
            + "?," // Index 1 name
            + "?," // Index 2 password
            + "'" + isAdministrator + "',"
            + "'" + language        + "',"
            + "'" + style           + "'"
            + ")";
        
        PreparedStatement insertStmt = null;
        try
            {
            insertStmt = _hsqldbConnection.prepareStatement(insertCom);
            insertStmt.setString(1, name);
            insertStmt.setString(2, pass);
            insertStmt.executeUpdate();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("UserNew() " + ex.getMessage());
            res.Failed(ex.getMessage());
            }
        finally
            {
            try
                {
                insertStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Check whether a name is already defined.
     * @param name Name to check.
     * @return True if the name is defined.
     */
    /* --------------------------------------------------------------------- */
    public boolean UserNameExists(String name) 
        {
        boolean nameExists = false;
        
        String query = "select * from user where name = ?";
        PreparedStatement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.prepareStatement(query);
            readStmt.setString(1, name);
            ResultSet res = readStmt.executeQuery();
            nameExists = res.next();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("UserNameExists() " + ex.getMessage());
            }
        finally
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return nameExists;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get a user with the supplied ID.
     * 
     */
    /* --------------------------------------------------------------------- */
    public DBR UserGet(int userID)
        {
        User      userOK   = null;
        Statement readStmt = null;
        DBR       r        = new DBR();
        
        String query = "select * from user where id = " + userID + ";";
        try 
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet res = readStmt.executeQuery(query);
            
            if (res != null && res.next()) 
                {
                // Read the values from the record.
                //
                userOK = new User(res.getString("lang"));
                userOK._identifier  = res.getInt("id");
                userOK._name        = res.getString("name");
                userOK._key         = res.getString("key");
                userOK._isAdmin     = res.getBoolean("admin");
                userOK._style       = res.getString("style");
                r.setResult(userOK);
                }
            else 
                {
                // id not found
                //
                r.Failed("User is not defined.");
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("UserGet(int) " + ex.getMessage());
            r.Failed("Error accessing database.");
            } 
        finally 
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                // Ignore.
                }
            }
        
        return r;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Update all the fields of a specific user.
     * 
     * @param userID Identifier
     * @param name Name
     * @param pass Password
     * @param access Access level
     * @param language Chosen language
     * @param style Style
     */
    /* --------------------------------------------------------------------- */
    public DBR UserUpdate(
    int     userID, 
    String  name, 
    String  pass, 
    boolean access, 
    String  language, 
    String  style)
        {
        DBR res = new DBR();
        
        // Update user with the supplied info.
        String sqlCommand = "update user set "
            + "name = ?," // Index 1 name
            + "key = ?, " // Index 2 password
            + "admin = " + access + ", "
            + "lang = '"  + language  + "', "
            + "style = '" + style + "' "
            + "where "
            + "id = " + userID + ";";
        
        PreparedStatement updateStmt = null;
        try
            {
            updateStmt = _hsqldbConnection.prepareStatement(sqlCommand);
            updateStmt.setString(1, name);
            updateStmt.setString(2, pass);
            updateStmt.executeUpdate();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("UserUpdate(int) " + ex.getMessage());
            res.Failed("");
            }
        finally
            {
            try
                {
                updateStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Delete a user.
     */
    /* --------------------------------------------------------------------- */
    public DBR UserDelete(int userID)
        {
        DBR res = new DBR();
        String query = "delete from user where id = " + userID + ";";
        
        Statement delStmt = null;
        try
            {
            delStmt = _hsqldbConnection.createStatement();
            int rowCount = delStmt.executeUpdate(query);
            if (rowCount != 1) 
                { 
                throw new Exception("This command failed: '" + query + "'"); 
                }
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("UserDelete() " + ex.getMessage());
            res.Failed("Could not delete user.");
            }
        finally
            {
            try
                {
                delStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }
    
    /*  -------------------------------------------------------------------- */
    /** Get an instance of a single room.
     *
     * @param roomNo Which room to retrieve.
     * @return Null if room is not defined or an error occurred.
     */
    /*  -------------------------------------------------------------------- */
    public DBR RoomGet(String roomNo) 
        {
        DBR    dbRes = new DBR();
        Room   r     = new Room();
        String query = "select * from room where roomno = ?";

        PreparedStatement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.prepareStatement(query);
            readStmt.setString(1, roomNo);
            ResultSet res = readStmt.executeQuery();
            if (res != null && res.next()) 
                {
                // Read the values from the record.
                r._roomNo   = res.getString("roomno");
                r._size     = res.getInt("size");
                r._floor    = res.getInt("floor");
                r._type     = res.getString("type");
                r._occupied = res.getBoolean("occupied");
                r._clean    = res.getBoolean("clean");
                
                dbRes.setResult(r);
                }
            else 
                {
                // Room not defined.
                dbRes.Failed("Room " + roomNo + " is not defined.");
                }
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("RoomGet() " + ex.getMessage());
            dbRes.Failed("Could not read from database.");
            }
        finally
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }

        return dbRes;
        }
    
    /*  -------------------------------------------------------------------- */
    /** Get a list of all defined rooms in the hotel.
     *
     * @return The list is empty if no rooms have been defined.
     */
    /*  -------------------------------------------------------------------- */
    public DBR Rooms()
        {
        DBR             queryResult = new DBR();
        ArrayList<Room> list        = new ArrayList();
        String          query       = "select * from room order by roomno";

        Statement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet res = readStmt.executeQuery(query);

            while (res.next())
                {
                Room r = new Room();
                r._roomNo   = res.getString("roomno");
                r._size     = res.getInt("size");
                r._floor    = res.getInt("floor");
                r._type     = res.getString("type");
                r._occupied = res.getBoolean("occupied");
                r._clean    = res.getBoolean("clean");

                list.add(r);
                }
            queryResult.setResult(list);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("Rooms() " + ex.getMessage());
            queryResult.Failed("Could not read from database.");
            }
        finally
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return queryResult;
        }

    
    /*  -------------------------------------------------------------------- */
    /** Create a new room.
     *
     * @param roomNo The room's number.
     * @param size How many beds are in the room.
     * @param floor On which floor it is.
     * @param type User defined, 10 chars.
     * @return An instance of the new room, null if an error occured.
     */
    /*  -------------------------------------------------------------------- */
    public DBR RoomSave(String roomNo, int size, int floor, String type)
        {
        DBR res = new DBR();
        
        String comm = "insert into room "
            + "("
            + "roomno,"
            + "size,"
            + "floor,"
            + "type"
            + ") "
            + "values "
            + "("
            + "?," // Index 1
            + size  + ","
            + floor + ","
            + "?" // Index 2
            + ")";

        PreparedStatement insertStmt = null;
        try
            {
            insertStmt = _hsqldbConnection.prepareStatement(comm);
            insertStmt.setString(1, roomNo);
            insertStmt.setString(2, type);
            insertStmt.executeUpdate();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("RoomSave() " + ex.getMessage());
            res.Failed("Write op failed.");
            }
        finally
            {
            try
                {
                insertStmt.close();
                }
            catch (Exception ex)
                {
                }
            }

        if (res.OK()) 
            {
            // Return an instance of the room just created.
            res.setResult(RoomGet(roomNo));
            }
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Update info about a room.
     * 
     * @param rnoOld Room no.
     * @param rnoNew New room no.
     * @param irsize Size of the room.
     * @param irfloor On which floor.
     * @param rtype Type of room.
     * @return True if success.
     */
    /* --------------------------------------------------------------------- */
    public DBR RoomUpdate(String rnoOld, String rnoNew, int irsize, int irfloor, String rtype)
        {
        DBR res = new DBR();
        String query = "update room set "
            + "roomno = ?, " // index 1
            + "size = "  + Integer.toString(irsize) + ", "
            + "floor = " + Integer.toString(irfloor) + ", "
            + "type = ? "    // index 2
            + "where roomno = '" + rnoOld + "';";
        
        PreparedStatement updateStmt = null;
        try
            {
            updateStmt = _hsqldbConnection.prepareStatement(query);
            updateStmt.setString(1, rnoNew);
            updateStmt.setString(2, rtype);
            updateStmt.executeUpdate();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("RoomUpdate() " + ex.getMessage());
            res.Failed("Write op failed.");
            }
        finally
            {
            try
                {
                updateStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Delete a room.
     * 
     * @param roomNo The room no.
     * @return True if successful.
     */
    /* --------------------------------------------------------------------- */
    public DBR RoomDelete(String roomNo)
        {
        DBR res = new DBR();
        String query = "delete from room where roomno = '" + roomNo + "';";
        
        Statement delStmt = null;
        try
            {
            delStmt = _hsqldbConnection.createStatement();
            int rowCount = delStmt.executeUpdate(query);
            if (rowCount != 1) 
                { 
                throw new Exception("This command failed: '" + query + "'"); 
                }
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("RoomDelete() " + ex.getMessage());
            res.Failed("Could not delete room.");
            }
        finally
            {
            try
                {
                delStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get all defined price-lists.
     * 
     * @return A list of Pricelist objects.
     */
    /* --------------------------------------------------------------------- */
    public DBR Pricelists()
        {
        DBR                  res   = new DBR();
        ArrayList<Pricelist> list  = new ArrayList();
        String               query = "select * from price, pitems where price.id = pitems.priceid order by price.id, pitems.roomno;";
        
        Statement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet rSet = readStmt.executeQuery(query);

            Pricelist plist = null;
            int currentPL = -1;
            while (rSet.next())
                {
                int plID = rSet.getInt("id");
                if (plID != currentPL) 
                    {
                    // Starting to read a new price-list.
                    currentPL = plID;
                    if (plist != null) 
                        {
                        list.add(plist);
                        }
                    plist = new Pricelist();
                    plist._id           = plID;
                    plist._usercreated  = rSet.getString("usercreated");
                    plist._whencreated  = rSet.getTimestamp("whencreated");
                    plist._name         = rSet.getString("name");
                    plist._periodstart  = rSet.getDate("periodstart");
                    plist._periodend    = rSet.getDate("periodend");
                    }
                
                // Read an item in the price-list.
                PricelistItem item = new PricelistItem();
                item._id        = rSet.getInt("piid");
                item._priceid   = rSet.getInt("priceid");
                item._roomno    = rSet.getString("roomno");
                item._roomtype  = rSet.getString("roomtype");
                item._roomsize  = rSet.getInt("roomsize");
                item._guest1    = rSet.getLong("guest1");
                item._guest2    = rSet.getLong("guest2");
                item._guest3    = rSet.getLong("guest3");
                item._guest4    = rSet.getLong("guest4");
                item._over4     = rSet.getLong("over4");
                item._extracot  = rSet.getLong("extracot");
                plist.AddItem(item);
                }
            // Store the last read price-list.
            if (plist != null) 
                {
                list.add(plist);
                }
            
            res.setResult(list);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("Pricelists() " + ex.getMessage());
            res.Failed("Could not read from database.");
            }
        finally
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * The hotel settings.
     */
    /* --------------------------------------------------------------------- */
    public DBR HotelGet()
        {
        Hotel     hotel    = new Hotel();
        Statement readStmt = null;
        DBR       qRes     = new DBR();
        
        String query = "select * from hotel;";
        try 
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet res = readStmt.executeQuery(query);
            
            if (res != null && res.next()) 
                {
                hotel._name     = res.getString("name");
                hotel._addr1    = res.getString("addr1");
                hotel._addr2    = res.getString("addr2");
                hotel._city     = res.getString("city");
                hotel._country  = res.getString("country");
                hotel._vat      = res.getDouble("vat");
                hotel._overbook = res.getBoolean("overbook");
                hotel.setLocale(res.getString("locale"));
                hotel.setTimezone(res.getString("timezone"));
                
                qRes.setResult(hotel);
                }
            else 
                {
                qRes.Failed("Database error, could not read hotel-configuration data.");
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("HotelGet() " + ex.getMessage());
            qRes.Failed("Error accessing database.");
            } 
        finally 
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                // Ignore.
                }
            }
        
        return qRes;
        }

    /* --------------------------------------------------------------------- */
    /**
     * The configurations for the hotel.
     * 
     * @param hcName User text input.
     * @param hcAddress1 User text input.
     * @param hcAddress2 User text input.
     * @param hcCity User text input.
     * @param hcCountry User text input.
     * @param hcVAT Has been error checked.
     * @param hcTimezone From a drop-down.
     * @param hcLocale From a drop-down.
     * @param allowOverbooking From a check-box.
     * @return If successful an object with the new settings.
     */
    /* --------------------------------------------------------------------- */
    public DBR HotelUpdate(
    String hcName, 
    String hcAddress1, 
    String hcAddress2, 
    String hcCity, 
    String hcCountry, 
    Double hcVAT, 
    String hcTimezone, 
    String hcLocale, 
    boolean allowOverbooking)
        {
        DBR res = new DBR();
        String query = "update hotel set "
            + "name = ?, "    // index 1
            + "addr1 = ?, "   // index 2
            + "addr2 = ?, "   // index 3
            + "city = ?, "    // index 4
            + "country = ?, " // index 5
            + "vat = "       + _FtoStr.format(hcVAT) + ", "
            + "timezone = '" + hcTimezone            + "', "
            + "locale = '"   + hcLocale              + "', "
            + "overbook = "  + allowOverbooking      + ";";
        
        PreparedStatement updateStmt = null;
        try
            {
            updateStmt = _hsqldbConnection.prepareStatement(query);
            updateStmt.setString(1, hcName);
            updateStmt.setString(2, hcAddress1);
            updateStmt.setString(3, hcAddress2);
            updateStmt.setString(4, hcCity);
            updateStmt.setString(5, hcCountry);
            updateStmt.executeUpdate();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("RoomUpdate() " + ex.getMessage());
            res.Failed("Write op failed.");
            }
        finally
            {
            try
                {
                updateStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        if (res.OK()) 
            {
            // Success, so we get an object with the new settings.
            DBR resNewSettings = HotelGet();
            res.setResult(resNewSettings.Result());
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Set the value of a price-point in a record in the PITEMS table.
     * 
     * @param pliDBID ID for a record in the PITEMS table.
     * @param pliColumn Which column is the target.
     * @param longValue The value to set.
     * @return True if successful.
     */
    /* --------------------------------------------------------------------- */
    public DBR PricelistCellUpdate(int pliDBID, int pliColumn, long longValue)
        {
        DBR res = new DBR();
        
        // Determine the name of the column to update. There are 6 columns
        // which can be changed, numbered 2-7, anything else is an error.
        String colName;
        switch (pliColumn) 
            {
            case 2  : colName = "guest1";   break;
            case 3  : colName = "guest2";   break;
            case 4  : colName = "guest3";   break;
            case 5  : colName = "guest4";   break;
            case 6  : colName = "over4";    break;
            case 7  : colName = "extracot"; break;
            default : 
                {
                res.Failed("Index out of range.");
                return res;
                }
            }
        
        String query = "update pitems set "
            + colName + " = " + longValue + " "
            + "where "
            + "piid = " + pliDBID;
        
        Statement updateStmt = null;
        try
            {
            updateStmt = _hsqldbConnection.createStatement();
            updateStmt.executeUpdate(query);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("PricelistCellUpdate() " + ex.getMessage());
            res.Failed("Write op failed.");
            }
        finally
            {
            try
                {
                updateStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Create a new price list. This means creating a record in the PRICE table
     * and then 1 record pr. defined room in the PITEMS table.
     * 
     * @param strPLName The name of the new price list.
     * @param oStart The date when the list becomes active, ISO format YYYY-MM-DD.
     * @param oEnd The date when the list becomes inactive, ISO format YYYY-MM-DD.
     * @param userName Which user is creating the list.
     * 
     * @return True if successful.
     */
    /* --------------------------------------------------------------------- */
    public DBR PricelistCreate(
    String            strPLName, 
    GregorianCalendar gcStart, 
    GregorianCalendar gcEnd, 
    String            userName)
        {
        DBR res = new DBR();
        
        // Get a list of all defined rooms, use it to create the price-items
        // for the price-list.
        DBR resRooms = Rooms();
        if (! resRooms.OK()) 
            {
            res.Failed("DB read failed.");
            return res;
            }
        ArrayList<Room> listRooms = (ArrayList)resRooms.Result();
        
        String isoStart = _GCtoISO(gcStart);
        String isoEnd   = _GCtoISO(gcEnd);
        
        PreparedStatement stmtPrice = null;
        Statement         stmtItems = null;
        Statement         iden      = null;
        try 
            {
            // This starts our transaction for the operation.
            _hsqldbConnection.setAutoCommit(false);
            
            // Step 1: Create the id of the record in the PRICE table.
            String query = "select max(id) as maxid from price;";
            iden = _hsqldbConnection.createStatement();
            ResultSet resID = iden.executeQuery(query);
            resID.next();
            int pricelistID = resID.getInt("maxid");
            pricelistID++;
            
            // Step 2: Create the record in the PRICE table.
            String queryPrice = "INSERT INTO price "  
                + "(" 
                + "id, "
                + "usercreated, " 
                + "whencreated, " 
                + "name, " 
                + "periodstart, " 
                + "periodend" 
                + ") " 
                + "VALUES " 
                + "(" 
                + pricelistID                      + ", "
                + "'" + userName                   + "', "
                + "'" + _DtoISO.format(new Date()) + "', "
                + "?, "                            // INDEX 1
                + "'" + isoStart                   + "', "
                + "'" + isoEnd                     + "'"
                + ");";
            stmtPrice = _hsqldbConnection.prepareStatement(queryPrice);
            stmtPrice.setString(1, strPLName);
            stmtPrice.executeUpdate();
            
            // Step 3: create a price-item for each defined room.
            StringBuilder sbItems = new StringBuilder();
            for (Room r : listRooms) 
                {
                String s = "INSERT INTO pitems "
                    + "("
                    + "priceid, "
                    + "roomno, "
                    + "roomtype, "
                    + "roomsize, "
                    + "guest1, "
                    + "guest2, "
                    + "guest3, "
                    + "guest4, "
                    + "over4, "
                    + "extracot"
                    + ") VALUES "
                    + "("
                    + pricelistID + ", "
                    + "'" + r._roomNo + "', "
                    + "'" + r._type   + "', "
                    + r._size         + ", "
                    + "0, "
                    + "0, "
                    + "0, "
                    + "0, "
                    + "0, "
                    + "0"
                    + ");";
                sbItems.append(s);
                }
            stmtItems = _hsqldbConnection.createStatement();
            stmtItems.executeQuery(sbItems.toString());
            
            _hsqldbConnection.commit();
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("PricelistCreate() " + ex.getMessage());
            res.Failed("DB write failed.");
            try
                {
                _hsqldbConnection.rollback();
                }
            catch (Exception ex2)
                {
                AppLog.Instance().Error("rollback() " + ex2.getMessage());
                }
            } 
        finally 
            {            
            try 
                {
                // Stop the transaction.
                _hsqldbConnection.setAutoCommit(true);
                
                if (stmtItems != null) stmtItems.close();
                if (stmtPrice != null) stmtPrice.close();
                if (iden      != null) iden.close();
                } 
            catch (Exception ex3) 
                {} // Ignore
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get a pricelist which has the specified database ID.
     */
    /* --------------------------------------------------------------------- */
    public DBR Pricelist(long dbID)
        {
        DBR res = new DBR();
        String query = 
              "select * from "
            + "price, pitems "
            + "where "
            + "price.id = " + dbID + " and "
            + "price.id = pitems.priceid "
            + "order by price.id, pitems.roomno;";
        
        Statement readStmt = null;
        try 
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet rSet = readStmt.executeQuery(query);
            
            Pricelist plist = new Pricelist();
            boolean firstRecord = true;
            while (rSet.next()) 
                {
                if (firstRecord) 
                    {
                    plist._id           = rSet.getInt("id");
                    plist._usercreated  = rSet.getString("usercreated");
                    plist._whencreated  = rSet.getTimestamp("whencreated");
                    plist._name         = rSet.getString("name");
                    plist._periodstart  = rSet.getDate("periodstart");
                    plist._periodend    = rSet.getDate("periodend");
                    firstRecord = false;
                    }
                
                // Read an item in the price-list.
                PricelistItem item = new PricelistItem();
                item._id        = rSet.getInt("piid");
                item._priceid   = rSet.getInt("priceid");
                item._roomno    = rSet.getString("roomno");
                item._roomtype  = rSet.getString("roomtype");
                item._roomsize  = rSet.getInt("roomsize");
                item._guest1    = rSet.getLong("guest1");
                item._guest2    = rSet.getLong("guest2");
                item._guest3    = rSet.getLong("guest3");
                item._guest4    = rSet.getLong("guest4");
                item._over4     = rSet.getLong("over4");
                item._extracot  = rSet.getLong("extracot");
                plist.AddItem(item);
                }
            
            res.setResult(plist);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Pricelist() " + ex.getMessage());
            res.Failed("DB read failed.");
            } 
        finally 
            {
            try 
                {
                if (readStmt != null) readStmt.close();
                }
            catch (Exception ex) 
                {}
            }
        
        return res;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get a pricelist which has the specified id and covers the date.
     * 
     * @param pricelID DB identity of the list.
     * @param targetDate Must cover this date.
     */
    /* --------------------------------------------------------------------- */
    public DBR Pricelist(int pricelID, Date targetDate) 
        {
        DBR res = new DBR();
        String query = 
              "select * from "
            + "price, pitems "
            + "where "
            + "price.id = " + pricelID + " and "
            + "price.periodstart <= '" + _DtoISO.format(targetDate) + "' and "
            + "price.periodend > '" + _DtoISO.format(targetDate) + "' and "
            + "price.id = pitems.priceid "
            + "order by price.id, pitems.roomno;";
        
        Statement readStmt = null;
        try 
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet rSet = readStmt.executeQuery(query);
            
            Pricelist plist = new Pricelist();
            boolean firstRecord = true;
            while (rSet.next()) 
                {
                if (firstRecord) 
                    {
                    plist._id           = rSet.getInt("id");
                    plist._usercreated  = rSet.getString("usercreated");
                    plist._whencreated  = rSet.getTimestamp("whencreated");
                    plist._name         = rSet.getString("name");
                    plist._periodstart  = rSet.getDate("periodstart");
                    plist._periodend    = rSet.getDate("periodend");
                    firstRecord = false;
                    }
                
                // Read an item in the price-list.
                PricelistItem item = new PricelistItem();
                item._id        = rSet.getInt("piid");
                item._priceid   = rSet.getInt("priceid");
                item._roomno    = rSet.getString("roomno");
                item._roomtype  = rSet.getString("roomtype");
                item._roomsize  = rSet.getInt("roomsize");
                item._guest1    = rSet.getLong("guest1");
                item._guest2    = rSet.getLong("guest2");
                item._guest3    = rSet.getLong("guest3");
                item._guest4    = rSet.getLong("guest4");
                item._over4     = rSet.getLong("over4");
                item._extracot  = rSet.getLong("extracot");
                plist.AddItem(item);
                }
            
            if (plist._items.size() > 0) 
                {
                res.setResult(plist);
                }
            else 
                {
                // We got nothing.
                res.Failed("No match.");
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Pricelist(int,date) " + ex.getMessage());
            res.Failed("DB read failed.");
            } 
        finally 
            {
            try 
                {
                if (readStmt != null) readStmt.close();
                }
            catch (Exception ex) 
                {}
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Update a pricelist.
     * 
     * @param iDBID The target list.
     * @param strPLName Descriptive text for the list.
     * @param oStart When list becomes active, in ready-to-use format.
     * @param oEnd When list becomes inactive, in ready-to-use format.
     */
    /* --------------------------------------------------------------------- */
    public DBR PricelistUpdate(int iDBID, String strPLName, String isoStart, String isoEnd)
        {
        DBR res = new DBR();
        
        String query = "update price set "
            + "name = ?"                    + ", "  // INDEX 1
            + "periodstart = '" + isoStart  + "', "
            + "periodend = '"   + isoEnd    + "' "
            + "where "
            + "id = " + iDBID + ";";
        
        PreparedStatement stmt = null;
        try 
            {
            stmt  = _hsqldbConnection.prepareStatement(query);
            stmt.setString(1, strPLName);
            stmt.executeUpdate();
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("PricelistUpdate() " + ex.getMessage());
            res.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) stmt.close();
                } 
            catch (Exception ex2) 
                {}
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Delete a pricelist from the db. This means delete records from both
     * the PRICE and PTIEMS tables.
     * @param idbid The identifier for the pricelist.
     */
    /* --------------------------------------------------------------------- */
    public DBR PricelistDelete(int idbid)
        {
        DBR res = new DBR();
        
        String query = 
              "delete from price where price.id = "        + idbid + "; "
            + "delete from pitems where pitems.priceid = " + idbid + ";";
        
        Statement delStmt = null;
        try
            {
            delStmt = _hsqldbConnection.createStatement();
            delStmt.executeUpdate(query);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("PricelistDelete() " + ex.getMessage());
            res.Failed("Could not delete pricelist.");
            }
        finally
            {
            try
                {
                if (delStmt != null) delStmt.close();
                }
            catch (Exception ex)
                {}
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get all defined customers for the hotel.
     * 
     * @return A list of the customers, the list is emtpy if no customers are defined.
     */
    /* --------------------------------------------------------------------- */
    public DBR Customers()
        {
        DBR res = new DBR();
        ArrayList<Customer> clist = new ArrayList();
        
        String query = "select * from customer order by firstname;";
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet setCust = stmt.executeQuery(query);
            
            while (setCust.next()) 
                {
                Customer c = new Customer();
                c._id           = setCust.getInt("id");
                c.setFirstName(setCust.getString("firstname"));
                c.setLastName(setCust.getString("lastname"));
                c._address1     = setCust.getString("address1");
                c._address2     = setCust.getString("address2");
                c._postalcode   = setCust.getString("postalcode");
                c._city         = setCust.getString("city");
                c._country      = setCust.getString("country");
                c._email        = setCust.getString("email");
                c._phone        = setCust.getString("phone");
                c._pricelist    = setCust.getInt("pricelist");
                c._discount     = setCust.getDouble("discount");
                clist.add(c);
                }
            
            res.setResult(clist);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Customers() " + ex.getMessage());
            res.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) stmt.close();
                } 
            catch (Exception ex2) 
                {}
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Create a new customer in the database.
     * 
     * @param name His name.
     * @param addr1 Address line 1
     * @param addr2 Address line 2
     * @param post Postal code
     * @param city Name of the city
     * @param count Name of the country
     * @param plid Price list identifier
     * @param disc Percent discount
     */
    /* --------------------------------------------------------------------- */
    public DBR CustomerSave(
    String name, 
    String addr1, 
    String addr2, 
    String post, 
    String city, 
    String count, 
    int plid, 
    double disc)
        {
        DBR res = new DBR();
        
        String query = "INSERT INTO customer "
            + "("
            + "firstname, "
            + "address1, "
            + "address2, "
            + "postalcode, "
            + "city, "
            + "country, "
            + "pricelist, "
            + "discount"
            + ") "
            + "VALUES "
            + "("
            + "?, " // INDEX 1
            + "?, " // INDEX 2
            + "?, " // INDEX 3
            + "?, " // INDEX 4
            + "?, " // INDEX 5
            + "?, " // INDEX 6
            + plid + ", "
            + _FtoStr.format(disc)
            + ");";
        
        PreparedStatement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, addr1);
            stmt.setString(3, addr2);
            stmt.setString(4, post);
            stmt.setString(5, city);
            stmt.setString(6, count);
            
            stmt.executeUpdate();
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("CustomerSave() " + ex.getMessage());
            res.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) stmt.close();
                } 
            catch (Exception ex2) 
                {}
            }
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Fetch a particular customer.
     */
    /* --------------------------------------------------------------------- */
    public DBR Customer(int customerID)
        {
        DBR res = new DBR();
        
        String query = "select * from customer where id = " + customerID + ";";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rset = stmt.executeQuery(query);
            if (rset.next()) 
                {
                Customer c = new Customer();
                c._id           = rset.getInt("id");
                c.setFirstName(rset.getString("firstname"));
                c.setLastName(rset.getString("lastname"));
                c._address1     = rset.getString("address1");
                c._address2     = rset.getString("address2");
                c._postalcode   = rset.getString("postalcode");
                c._city         = rset.getString("city");
                c._country      = rset.getString("country");
                c._pricelist    = rset.getInt("pricelist");
                c._discount     = rset.getDouble("discount");
                
                res.setResult(c);
                }
            else 
                {
                // No customer has this id.
                res.Failed("Customer not defined.");
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Customer() " + ex.getMessage());
            res.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) stmt.close();
                } 
            catch (Exception ex2) 
                {}
            }
        
        return res;
        }

    
    /* --------------------------------------------------------------------- */
    /**
     * Save changes made to a customer.
     * 
     * @param icustomerID Database id.
     * @param name His name
     * @param addr1 Address line 1
     * @param addr2 Address line2
     * @param post Postal code
     * @param city His city
     * @param count His country
     * @param iplid Price list database id
     * @param ddisc Percentage discount
     */
    /* --------------------------------------------------------------------- */
    public DBR CustomerUpdate(
    int     icustomerID, 
    String  name, 
    String  addr1, 
    String  addr2, 
    String  post, 
    String  city, 
    String  count, 
    int     iplid, 
    double  ddisc)
        {
        DBR res = new DBR();
        
        String query = "UPDATE customer SET "
            + "firstname = ?, "   // INDEX 1 firstname
            + "address1 = ?, "    // INDEX 2 address1
            + "address2 = ?, "    // INDEX 3 address2
            + "postalcode = ?, "  // INDEX 4 postalcode
            + "city = ?, "        // INDEX 5 city
            + "country = ?, "     // INDEX 6 country
            + "pricelist = " + iplid + ", "
            + "discount = " + _FtoStr.format(ddisc) + " "
            + "where "
            + "id = " + icustomerID + ";";
        
        PreparedStatement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, addr1);
            stmt.setString(3, addr2);
            stmt.setString(4, post);
            stmt.setString(5, city);
            stmt.setString(6, count);
            
            stmt.executeUpdate();
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("CustomerUpdate() " + ex.getMessage());
            res.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) stmt.close();
                } 
            catch (Exception ex2) 
                {}
            }
        
        return res;
        }

    
    /* --------------------------------------------------------------------- */
    /**
     * Delete a customer from the database.
     * 
     * @param custID Database id for the customer.
     */
    /* --------------------------------------------------------------------- */
    public DBR CustomerDelete(int custID)
        {
        DBR res = new DBR();
        
        String query = "delete from customer where id = " + custID + ";";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            stmt.executeQuery(query);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("CustomerDelete() " + ex.getMessage());
            res.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) stmt.close();
                } 
            catch (Exception ex2) 
                {}
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get all reservations which intersect the given period.
     * 
     * @param fd The first day of the period.
     * @param ld The last day of the period.
     * @return List of intersecting reservations.
     */
    /* --------------------------------------------------------------------- */
    public DBR Reservations(Date fd, Date ld)
        {
        DBR res = new DBR();
        ArrayList<Res> listOfReservations = new ArrayList();
        
        String query = "select * from res, resg "
            + "where "
            + "res.id = resg.resid and "
            + "res.periodend > '"    + _DtoISO.format(fd) + "' and "
            + "res.periodstart <= '" + _DtoISO.format(ld) + "'"
            + "order by res.periodstart, res.id;";
        
        Statement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet rSet = readStmt.executeQuery(query);

            Res currentRes = null;
            int currentResID = -1;
            while (rSet.next()) 
                {
                int resID = rSet.getInt("id");
                if (resID != currentResID) 
                    {
                    currentResID = resID;
                    if (currentRes != null) 
                        {
                        listOfReservations.add(currentRes);
                        }
                    currentRes = new Res();
                    currentRes._id          = resID;
                    currentRes._made        = rSet.getDate("made");
                    currentRes._user        = rSet.getString("user");
                    currentRes._custid      = rSet.getInt("custid");
                    currentRes._nor         = rSet.getInt("nor");
                    currentRes._nog         = rSet.getInt("nog");
                    currentRes._periodstart = rSet.getDate("periodstart");
                    currentRes._periodend   = rSet.getDate("periodend");
                    currentRes._invid       = rSet.getInt("invid");
                    }
                ResGuest resg = new ResGuest();
                resg._rgid      = rSet.getInt("rgid");
                resg._resid     = resID;
                resg._name      = rSet.getString("name");
                resg._country   = rSet.getString("country");
                resg._passpid   = rSet.getString("passpid");
                resg._gender    = rSet.getInt("gender");
                resg._checkedin = rSet.getBoolean("checkedin");
                resg._roomno    = rSet.getString("roomno");
                currentRes.addGuest(resg);
                }
            // Store the last read reservation
            if (currentRes != null) 
                {
                listOfReservations.add(currentRes);
                }
            res.setResult(listOfReservations);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("Reservations() " + ex.getMessage());
            res.Failed("Could not read from database.");
            }
        finally
            {
            try
                {
                if (readStmt != null) readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    
    /* --------------------------------------------------------------------- */
    /**
     * Create a new reservation. First create 1 record in the 'Res' table.
     * Then create a record for every guest in the 'Resg' table.
     * 
     * @param usr The user making the reservation.
     * @param gcCreationDate When it was created.
     * @param custID Which customer owns the reservation.
     * @param numGuests How many guests are in the reservation.
     * @param dArrive The date the guests will arrive.
     * @param dDepart The date the guests will depart.
     * @param selectedRooms The rooms the user selected for the reservation.
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationCreate(
    User usr, 
    GregorianCalendar gcCreationDate, 
    int custID,
    int numGuests,
    Date dArrive, 
    Date dDepart, 
    String[] selectedRooms)
        {
        DBR result = new DBR();
        DBR resRooms = Rooms();
        ArrayList<Room> listRooms = (ArrayList)resRooms.Result();
        
        Statement stmtRes  = null;
        Statement stmtResg = null;
        try 
            {
            // START transaction.
            _hsqldbConnection.setAutoCommit(false);
            
            // 1. Create the identity for the reservation.
            int reservationID = getResMaxID();
            reservationID++;
            
            // 2. Create the record in the RES table.
            String qres = "INSERT INTO res "
                + "("
                + "id, "
                + "made, "
                + "user, "
                + "custid, "
                + "nor, "
                + "nog, "
                + "periodstart, "
                + "periodend"
                + ")"
                + "VALUES "
                + "("
                + reservationID                  + ", "
                + "'" + _GCtoISO(gcCreationDate) + "', "
                + "'" + usr._name                + "', "
                +       custID                   + ", "
                +       selectedRooms.length     + ", "
                +       numGuests                + ", "
                + "'" + _DtoISO.format(dArrive)  + "', "
                + "'" + _DtoISO.format(dDepart)  + "'"
                + ");";
            stmtRes = _hsqldbConnection.createStatement();
            stmtRes.executeQuery(qres);
            
            // 3. Create anonymous guests for all selected rooms.
            StringBuilder sbAnon = new StringBuilder();
            for (String roomNum : selectedRooms) 
                {
                int numGuestsInRoom = 0;
                for (Room r : listRooms) 
                    {
                    if (r._roomNo.compareTo(roomNum) == 0) 
                        {
                        numGuestsInRoom = r._size;
                        break;
                        }
                    }
                for (int i = 1; i <= numGuestsInRoom; i++) 
                    {
                    String s = "INSERT INTO resg "
                        + "("
                        + "resid,"
                        + "checkedin,"
                        + "roomno, "
                        + "arr, "
                        + "dep"
                        + ") "
                        + "VALUES "
                        + "("
                        + reservationID + ", "
                        + false + ", "
                        + "'" + roomNum + "', "
                        + "'" + _DtoISO.format(dArrive) + "', "
                        + "'" + _DtoISO.format(dDepart) + "'"
                        + ");";
                    sbAnon.append(s);
                    }
                }//END-OF for (String roomNum : selectedRooms)
            stmtResg = _hsqldbConnection.createStatement();
            stmtResg.executeQuery(sbAnon.toString());
            
            _hsqldbConnection.commit();
            
            // Return the just created reservation.
            DBR dbrResevation = Reservation(reservationID);
            result.setResult(dbrResevation.Result());
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("ReservationCreate() " + ex.getMessage());
            result.Failed(ex.getMessage());
            try
                {
                _hsqldbConnection.rollback();
                }
            catch (Exception ex2)
                {
                AppLog.Instance().Error("rollback() " + ex2.getMessage());
                }
            } 
        finally 
            {
            try 
                {
                // STOP transaction.
                _hsqldbConnection.setAutoCommit(true);
                
                if (stmtRes  != null) { stmtRes.close(); }
                if (stmtResg != null) { stmtResg.close(); }
                } 
            catch (Exception ex3) 
                {}// Ignore
            }
        
        return result;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Fetch a single reservation.
     * @param resID The database id for the reservation.
     * @return Object of class Res
     */
    /* --------------------------------------------------------------------- */
    public DBR Reservation(int resID)
        {
        DBR result = new DBR();
        String query = "select * from res, resg "
            + "where "
            + "res.id = " + resID + " and "
            + "res.id = resg.resid "
            + "order by resg.roomno;";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rset = stmt.executeQuery(query);
            
            Res theReservation = null;
            while (rset.next()) 
                {
                // First time through we create the reservation object.
                if (theReservation == null) 
                    {
                    theReservation = new Res();
                    theReservation._id          = rset.getInt("id");
                    theReservation._made        = rset.getDate("made");
                    theReservation._user        = rset.getString("user");
                    theReservation._custid      = rset.getInt("custid");
                    theReservation._nor         = rset.getInt("nor");
                    theReservation._nog         = rset.getInt("nog");
                    theReservation._periodstart = rset.getDate("periodstart");
                    theReservation._periodend   = rset.getDate("periodend");
                    theReservation._invid       = rset.getInt("invid");
                    }
                
                ResGuest rguest = new ResGuest();
                rguest._rgid        = rset.getInt("rgid");
                rguest._resid       = rset.getInt("resid");
                rguest._name        = rset.getString("name");
                rguest._country     = rset.getString("country");
                rguest._passpid     = rset.getString("passpid");
                rguest._gender      = rset.getInt("gender");
                rguest._checkedin   = rset.getBoolean("checkedin");
                rguest._roomno      = rset.getString("roomno");
                rguest._arr         = rset.getDate("arr");
                rguest._dep         = rset.getDate("dep");
                theReservation.addGuest(rguest);
                }
            result.setResult(theReservation);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Reservation() " + ex.getMessage());
            result.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {}//Ignore
            }
        
        return result;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Delete a reservation.
     * @param rid The target.
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationDelete(int rid)
        {
        DBR result = new DBR();
        
        Statement stmt = null;
        try 
            {
            // START transaction
            _hsqldbConnection.setAutoCommit(false);
            String query = 
                  "delete from res where id = "     + rid + "; "
                + "delete from resg where resid = " + rid + ";";
            stmt = _hsqldbConnection.createStatement();
            stmt.executeQuery(query);
            _hsqldbConnection.commit();
            } 
        catch (Exception ex) 
            {
            try { _hsqldbConnection.rollback(); } catch (Exception ex3) {}
            AppLog.Instance().Error("ReservationDelete() " + ex.getMessage());
            result.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                // STOP transaction
                _hsqldbConnection.setAutoCommit(true);
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {}
            }
        
        return result;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Fetch a single guest from a reservation.
     * 
     * @param gid The target guest.
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationGuest(int gid)
        {
        DBR result = new DBR();
        
        Statement stmt = null;
        try 
            {
            String query = "select * from resg where rgid = " + gid + ";";
            stmt = _hsqldbConnection.createStatement();
            ResultSet r = stmt.executeQuery(query);
            r.next();
            
            ResGuest guest = new ResGuest();
            guest._rgid         = r.getInt("rgid");
            guest._resid        = r.getInt("resid");
            guest._name         = r.getString("name");
            guest._country      = r.getString("country");
            guest._passpid      = r.getString("passpid");
            guest._gender       = r.getInt("gender");
            guest._checkedin    = r.getBoolean("checkedin");
            guest._roomno       = r.getString("roomno");
            guest._arr          = r.getDate("arr");
            guest._dep          = r.getDate("dep");
            
            result.setResult(guest);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("ReservationGuest() " + ex.getMessage());
            result.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {}
            }
        
        return result;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Update information about a guest in a reservation.
     * 
     * @param dbID Database id for the guest.
     * @param name Name of the guest.
     * @param isoCountry Country of origin.
     * @param ident Identifier for the guest.
     * @param gender Sex.
     * @param roomNo Room number.
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationGuestUpdate(
    int     dbID, 
    String  name, 
    String  isoCountry, 
    String  ident, 
    int     gender, 
    String  roomNo)
        {
        DBR result = new DBR();
        name = Apos(name);
        String query = "update resg set "
            + "name = ?, "  // INDEX 1
            + "country = '" + isoCountry + "', "
            + "passpid = '" + ident      + "', "
            + "gender = "   + gender     + ", "
            + "roomno = '"  + roomNo     + "' "
            + "where "
            + "rgid = " + dbID + ";";
        
        PreparedStatement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.executeUpdate();
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("ReservationGuestUpdate() " + ex.getMessage());
            result.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {}
            }
        
        return result;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Delete a guest from a reservation.
     * @param guestID The database id for the guest.
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationGuestDelete(int guestID)
        {
        DBR result = new DBR();
        
        Statement stmt = null;
        try 
            {
            String query = "delete from resg where rgid = " + guestID + ";";
            stmt = _hsqldbConnection.createStatement();
            stmt.executeUpdate(query);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("ReservationGuestDelete() " + ex.getMessage());
            result.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {
                }
            }
        
        return result;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Execute the supplied sql, returns true if successful.
     */
    /* --------------------------------------------------------------------- */
    public boolean sqlExecuteInserUpdateDelete(String sqlComm) 
        {
        AppLog.Instance().Info("Delphi.sqlExecuteInserUpdateDelete: sql size = " + sqlComm.length());
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            stmt.executeUpdate(sqlComm);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Delphi.sqlExecuteInserUpdateDelete: err=" + ex.getMessage() + " sql=" + sqlComm);
            return false;
            }
        finally 
            {
            if (stmt != null) 
                {
                try { stmt.close(); } catch (Exception ex) { /*IGNORE*/ }
                }
            }
        
        return true;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Return a list of those reservations which contain guests which will
     * arrive today.
     * 
     * @return ArrayList<Res>
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationsArriving()
        {
        // Start by getting a list of reservations with guests arriving today
        // AND who have not been checked in. Use this 2 step method because
        // all the guests in a reservation might not have the same chekcin status.
        DBR result = new DBR();
        ArrayList<Integer> idList = getResIDWithGuestsArriving();
        if (idList.isEmpty()) 
            {
            result.Failed("Nothing found");
            return result; 
            }
        
        // Collect the reservations found in the previous step.
        ArrayList<Res> reservationsList = new ArrayList();
        for (Integer reservationID : idList) 
            {
            DBR resultRes = Reservation(reservationID);
            if (resultRes.OK()) 
                {
                reservationsList.add((Res)resultRes.Result());
                }
            }
        result.setResult(reservationsList);
        return result;
        }

    
    /* --------------------------------------------------------------------- */
    /**
     * Return a list of those reservations which contain guests which will
     * depart today.
     * 
     * @return ArrayList<Res>
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationsDeparting()
        {
        // Start by getting a list of reservations with guests departing today
        // AND who are checked in. Use this 2 step method because
        // all the guests in a reservation might not have the same chekcin status.
        DBR result = new DBR();
        ArrayList<Integer> idList = getResIDWithGuestsDeparting();
        if (idList.isEmpty()) 
            {
            result.Failed("Nothing found");
            return result; 
            }
        
        // Collect the reservations found in the previous step.
        ArrayList<Res> reservationsList = new ArrayList();
        for (Integer reservationID : idList) 
            {
            DBR resultRes = Reservation(reservationID);
            if (resultRes.OK()) 
                {
                reservationsList.add((Res)resultRes.Result());
                }
            }
        result.setResult(reservationsList);
        return result;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the ID of those reservations which have guests departing today.
     * 
     * @return A list of reservation IDs.
     */
    /* --------------------------------------------------------------------- */
    private ArrayList<Integer> getResIDWithGuestsDeparting() 
        {
        ArrayList<Integer> idList = new ArrayList();
        
        GregorianCalendar cal = new GregorianCalendar();
        String isoToday = _DtoISO.format(cal.getTime());
        String query = "select distinct resid " 
            + "from resg " 
            + "where " 
            + "checkedin = true and " 
            + "arr < '" + isoToday + "' and " 
            + "dep = '" + isoToday + "';";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) 
                {
                idList.add(rs.getInt("resid"));
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("getResIDWithGuestsArriving() : " + ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) {/*IGNORE*/}
            }
        
        return idList;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get the ID of those reservations which have guests arriving today.
     * 
     * @return A list of reservation IDs.
     */
    /* --------------------------------------------------------------------- */
    private ArrayList<Integer> getResIDWithGuestsArriving() 
        {
        ArrayList<Integer> idList = new ArrayList();
        
        GregorianCalendar cal = new GregorianCalendar();
        String isoToday = _DtoISO.format(cal.getTime());
        String query = "select distinct resid " 
            + "from resg " 
            + "where " 
            + "checkedin = false and " 
            + "arr <= '" + isoToday + "' and " 
            + "dep > '"  + isoToday + "';";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) 
                {
                idList.add(rs.getInt("resid"));
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("getResIDWithGuestsArriving() : " + ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) {/*IGNORE*/}
            }
        
        return idList;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get a list of reservations with guests which have checked in.
     * 
     * @return A list of reservation IDs.
     */
    /* --------------------------------------------------------------------- */
    private ArrayList<Integer> getResIDWithCheckedinGuests() 
        {
        ArrayList<Integer> idList = new ArrayList();
        String query = "select distinct resid from resg "
            + "where "
            + "checkedin = true;";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) 
                {
                idList.add(rs.getInt("resid"));
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("getResIDWithCheckedinGuests() : " + ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) {/*IGNORE*/}
            }
        
        return idList;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Mark guests as checked out.
     * @param guestID An array of IDs  of those guests which are to be checked out.
     * @return True if successful.
     */
    /* --------------------------------------------------------------------- */
    @SuppressWarnings("UseSpecificCatch")
    public DBR GuestCheckOut(String[] guestID)
        {
        DBR res = new DBR();
        Statement updateStmt = null;
        class RoomCount { String rno; int guestCount; }
        HashMap<String, RoomCount> roomCounters = new HashMap();
        try
            {
            StringBuilder sqlStatements = new StringBuilder();
            // This starts our transaction for the operation.
            _hsqldbConnection.setAutoCommit(false);
            
            updateStmt = _hsqldbConnection.createStatement();
            for (String gid : guestID) 
                {
                DBR   resultg = Guest(Integer.parseInt(gid));
                Guest g       = (Guest)resultg.Result();
                DBR   resultr = RoomGet(g._roomno);
                Room  r       = (Room)resultr.Result();
                int countGuestsInRoom = 1;
                if (r._size > 1) 
                    {
                    ArrayList<Guest> guestList = GuestsCheckedIntoRoom(g._roomno);
                    if (guestList.size() > 1) 
                        {
                        countGuestsInRoom = guestList.size();
                        }
                    }
                
                String sqlQuery = "update RESG set checkedin = false where rgid = " + gid + ";";
                sqlStatements.append(sqlQuery);
                
                if (roomCounters.containsKey(g._roomno)) 
                    {
                    RoomCount rc = roomCounters.get(g._roomno);
                    rc.guestCount--;
                    roomCounters.put(g._roomno, rc);
                    }
                else 
                    {
                    countGuestsInRoom--;
                    RoomCount rc = new RoomCount();
                    rc.rno = g._roomno;
                    rc.guestCount = countGuestsInRoom;
                    roomCounters.put(g._roomno, rc);
                    }
                }
            
            // At this point all the guests have been marked as checked out and 
            // we have a list of the rooms they were in. If any of those rooms
            // are now empty they must be marked as not occupied and not clean.
            Collection<RoomCount> collCounters = roomCounters.values();
            for (RoomCount rc : collCounters) 
                {
                if (rc.guestCount == 0) 
                    {
                    String sqlQuery = "update ROOM set occupied = false, clean = false where roomno = '" + rc.rno + "';";
                    sqlStatements.append(sqlQuery);
                    }
                }
            
            updateStmt.execute(sqlStatements.toString());
            _hsqldbConnection.commit();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("GuestCheckOut() " + ex.getMessage());
            res.Failed(ex.getMessage());
            try
                {
                _hsqldbConnection.rollback();
                }
            catch (Exception ex2)
                {
                AppLog.Instance().Error("rollback() " + ex2.getMessage());
                }
            }
        finally
            {
            try 
                {
                // Stop the transaction.
                _hsqldbConnection.setAutoCommit(true);
                
                if (updateStmt != null) { updateStmt.close(); }
                } 
            catch (Exception ex2) 
                {
                /*IGNORE*/
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get those reservations which have guests checked in.
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationsCheckedin()
        {
        DBR result = new DBR();
        ArrayList<Integer> idList = getResIDWithCheckedinGuests();
        if (idList.isEmpty()) 
            {
            result.Failed("Nothing found");
            return result; 
            }
        
        // Collect the reservations found in the previous step.
        ArrayList<Res> reservationsList = new ArrayList();
        for (Integer reservationID : idList) 
            {
            DBR resultRes = Reservation(reservationID);
            if (resultRes.OK()) 
                {
                reservationsList.add((Res)resultRes.Result());
                }
            }
        result.setResult(reservationsList);
        
        return result;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get a list of all guests which are in a reservation for a room.
     * 
     * @param roomNo The target room.
     * @param cutoffDate Only guests which will depart after this date.
     * @return A list of "ResGuest" objects.
     */
    /* --------------------------------------------------------------------- */
    public DBR GuestsReservedForRoom(String roomNo, Date cutoffDate)
        {
        DBR resultList = new DBR();
        String query = "select * from resg "
            + "where "
            + "resg.roomno = '" + roomNo + "' and "
            + "resg.dep > '" + _DtoISO.format(cutoffDate) + "' "
            + "order by resg.arr asc;" ;
        ArrayList<ResGuest> guestList = new ArrayList();
        
        Statement readStmt = null;
        try 
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet res = readStmt.executeQuery(query);

            while (res.next())
                {
                ResGuest rg     = new ResGuest();
                rg._rgid        = res.getInt("rgid");
                rg._resid       = res.getInt("resid");
                rg._name        = res.getString("name");
                rg._country     = res.getString("country");
                rg._passpid     = res.getString("passpid");
                rg._gender      = res.getInt("gender");
                rg._checkedin   = res.getBoolean("checkedin");
                rg._roomno      = res.getString("roomno");
                rg._arr         = res.getDate("arr");
                rg._dep         = res.getDate("dep");
                guestList.add(rg);
                }
            resultList.setResult(guestList);
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("GuestsReservedForRoom() " + ex.getMessage());
            resultList.Failed("Could not read from database.");
            } 
        finally 
            {
            if (readStmt != null) 
                {
                try { readStmt.close(); } catch (Exception ex2) { /*IGNORE*/ }
                }
            }
        
        return resultList;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get a single guest.
     * @param rgid Guest identity in the RESG table.
     * @return A Guest object.
     */
    /* --------------------------------------------------------------------- */
    public DBR Guest(int rgid)
        {
        Guest     guestOK  = null;
        Statement readStmt = null;
        DBR       r        = new DBR();
        
        String query = "select * from resg where rgid = " + rgid + ";";
        try 
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet res = readStmt.executeQuery(query);
            
            if (res != null && res.next()) 
                {
                // Read the values from the record.
                //
                guestOK = new Guest();
                guestOK._rgid       = res.getInt("rgid");
                guestOK._resid      = res.getInt("resid");
                guestOK._name       = res.getString("name");
                guestOK._country    = res.getString("country");
                guestOK._passpid    = res.getString("passpid");
                guestOK._gender     = res.getInt("gender");
                guestOK._checkedin  = res.getBoolean("checkedin");
                guestOK._roomno     = res.getString("roomno");
                guestOK._arr        = res.getDate("arr");
                guestOK._dep        = res.getDate("dep");
                r.setResult(guestOK);
                }
            else 
                {
                // id not found
                //
                r.Failed("Guest ID does not exist.");
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Guest(int) " + ex.getMessage());
            r.Failed("Error accessing database.");
            } 
        finally 
            {
            try
                {
                readStmt.close();
                }
            catch (Exception ex)
                {
                // Ignore.
                }
            }
        
        return r;
        }

    
    /** Return a list of those guests which are checked into the specified room. */
    private ArrayList<Guest> GuestsCheckedIntoRoom(String roomno)
        {
        ArrayList<Guest> glist = new ArrayList();
        Statement readStmt = null;
        try 
            {
            String query = 
                "select * from RESG "
                + "where roomno = '" + roomno + "' and "
                + "checkedin = true;";
            
            readStmt = _hsqldbConnection.createStatement();
            ResultSet res = readStmt.executeQuery(query);
            while (res.next()) 
                {
                Guest guestOK = new Guest();
                guestOK._rgid       = res.getInt("rgid");
                guestOK._resid      = res.getInt("resid");
                guestOK._name       = res.getString("name");
                guestOK._country    = res.getString("country");
                guestOK._passpid    = res.getString("passpid");
                guestOK._gender     = res.getInt("gender");
                guestOK._checkedin  = res.getBoolean("checkedin");
                guestOK._roomno     = res.getString("roomno");
                guestOK._arr        = res.getDate("arr");
                guestOK._dep        = res.getDate("dep");
                glist.add(guestOK);
                }
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("GuestsCheckedIntoRoom() " + ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (readStmt != null) { readStmt.close(); }
                } 
            catch (Exception ex2) 
                {
                /*IGNORE*/
                }
            }
        return glist;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Check-in guest and update the room status accordingly.
     * @param guestID IDs of the guests to be checked in.
     * @return True if success, otherwise an error message.
     */
    /* --------------------------------------------------------------------- */
    @SuppressWarnings("UseSpecificCatch")
    public DBR GuestCheckIn(String[] guestID)
        {
        DBR       res          = new DBR();
        Statement updateStmt   = null;
        //class RoomCount { String rno; int guestCount; }
        HashMap<String, String> rooms = new HashMap();
        try
            {
            StringBuilder sqlStatements = new StringBuilder();
            // This starts our transaction for the operation.
            _hsqldbConnection.setAutoCommit(false);
            
            updateStmt = _hsqldbConnection.createStatement();
            for (String gid : guestID) 
                {
                DBR   resultg = Guest(Integer.parseInt(gid));
                Guest g       = (Guest)resultg.Result();
                DBR   resultr = RoomGet(g._roomno);
                Room  r       = (Room)resultr.Result();
                
                String sqlQuery = "update RESG set checkedin = true where rgid = " + gid + ";";
                sqlStatements.append(sqlQuery);
                rooms.put(g._roomno, g._roomno);
                }
            
            // At this point all the guests have been marked as checked in and 
            // we have a list of the rooms they are in. We now walk through the
            // list and mark the rooms as occupied, we leave clean status alone.
            Collection<String> collRooms = rooms.values();
            for (String roomno : collRooms) 
                {
                String sqlQuery = "update ROOM set occupied = true where roomno = '" + roomno + "';";
                sqlStatements.append(sqlQuery);
                }
            
            updateStmt.execute(sqlStatements.toString());
            _hsqldbConnection.commit();
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("GuestCheckIn() " + ex.getMessage());
            res.Failed(ex.getMessage());
            try
                {
                _hsqldbConnection.rollback();
                }
            catch (Exception ex2)
                {
                AppLog.Instance().Error("rollback() " + ex2.getMessage());
                }
            }
        finally
            {
            try 
                {
                // Stop the transaction.
                _hsqldbConnection.setAutoCommit(true);
                
                if (updateStmt != null) { updateStmt.close(); }
                } 
            catch (Exception ex2) 
                {
                /*IGNORE*/
                }
            }
        
        return res;
        }

    
    /* --------------------------------------------------------------------- */
    /**
     * Return the reservation which has any guests checked into the specified room.
     * If no reservation matches then a null value is returned.
     * @param roomNo Room to check.
     */
    /* --------------------------------------------------------------------- */
    public DBR ReservationCheckedinRoom(String roomNo)
        {
        DBR result = new DBR();
        String query = "select resg.resid from resg "
            + "where "
            + "resg.roomno = '" + roomNo + "' and "
            + "resg.checkedin = true;";
        
        Statement stmt = null;
        try 
            {
            stmt = _hsqldbConnection.createStatement();
            ResultSet rset = stmt.executeQuery(query);
            
            if (rset.next() == false) 
                {
                return result;
                }
            int resID = rset.getInt("resid");
            DBR dbrReservation = Delphi.Inst().Reservation(resID);
            result.setResult(dbrReservation.Result());
            } 
        catch (Exception ex) 
            {
            AppLog.Instance().Error("Reservation() " + ex.getMessage());
            result.Failed(ex.getMessage());
            } 
        finally 
            {
            try 
                {
                if (stmt != null) { stmt.close(); }
                } 
            catch (Exception ex2) 
                {}//Ignore
            }
        
        return result;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Get all invoices which are still open.
     */
    /* --------------------------------------------------------------------- */
    public DBR InvoicesOpen() 
        {
        DBR res = new DBR();
        ArrayList<Inv> listOfInvoices = new ArrayList();
        
        String query = "select * from invoice, invitems, customer "
            + "where "
            + "invoice.id = invitems.invid and "
            + "customer.id = invoice.customerid and "
            + "invoice.isclosed = false "
            + "order by invoice.id;";
        
        Statement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet rSet = readStmt.executeQuery(query);

            Inv currentInv = null;
            int currentInvID = -1;
            while (rSet.next()) 
                {
                int invID = rSet.getInt("id");
                if (invID != currentInvID) 
                    {
                    currentInvID = invID;
                    if (currentInv != null) 
                        {
                        listOfInvoices.add(currentInv);
                        }
                    currentInv = new Inv();
                    currentInv._id           = invID;
                    currentInv._user         = rSet.getString("user");
                    currentInv._created      = rSet.getDate("created");
                    currentInv._customer     = rSet.getInt("customerid");
                    currentInv._customerName = rSet.getString("firstname");
                    currentInv._closed       = rSet.getBoolean("isClosed");
                    }
                InvItem item = new InvItem();
                item._itemid        = rSet.getInt("itemid");
                item._invid         = rSet.getInt("invid");
                item._roomno        = rSet.getString("roomno");
                item._roomsize      = rSet.getInt("roomsize");
                item._arrive        = rSet.getDate("arrive");
                item._depart        = rSet.getDate("depart");
                item._pricePrNight  = rSet.getLong("priceprnight");
                item._total         = rSet.getLong("total");
                currentInv.addItem(item);
                }
            // Store the last read invoice.
            if (currentInv != null) 
                {
                listOfInvoices.add(currentInv);
                }
            res.setResult(listOfInvoices);
            AppLog.Instance().Info("Delphi.InvoicesOpen() count = " + listOfInvoices.size());
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("InvoicesOpen() " + ex.getMessage());
            res.Failed("Could not read from database.");
            }
        finally
            {
            try
                {
                if (readStmt != null) readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    
    /* --------------------------------------------------------------------- */
    /**
     * Get those invoices which intersect the given period.
     * @param perStart Period start
     * @param perEnd   Period end
     */
    /* --------------------------------------------------------------------- */
    public DBR InvoicesPeriod(GregorianCalendar perStart, GregorianCalendar perEnd)
        {
        DBR res = new DBR();
        ArrayList<Inv> listOfInvoices = new ArrayList();
        String sStart = _GCtoISO(perStart);
        String sEnd   = _GCtoISO(perEnd);
        
        String query = "select * from invoice, invitems, customer "
            + "where "
            + "invoice.id = invitems.invid and "
            + "customer.id = invoice.customerid and "
            + "invoice.created >= '" + sStart + "' and "
            + "invoice.created <= '" + sEnd   + "' "
            + "order by invoice.id;";
        
        Statement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet rSet = readStmt.executeQuery(query);

            Inv currentInv = null;
            int currentInvID = -1;
            while (rSet.next()) 
                {
                int invID = rSet.getInt("id");
                if (invID != currentInvID) 
                    {
                    currentInvID = invID;
                    if (currentInv != null) 
                        {
                        listOfInvoices.add(currentInv);
                        }
                    currentInv = new Inv();
                    currentInv._id           = invID;
                    currentInv._user         = rSet.getString("user");
                    currentInv._created      = rSet.getDate("created");
                    currentInv._customer     = rSet.getInt("customerid");
                    currentInv._customerName = rSet.getString("firstname");
                    currentInv._closed       = rSet.getBoolean("isClosed");
                    }
                InvItem item = new InvItem();
                item._itemid        = rSet.getInt("itemid");
                item._invid         = rSet.getInt("invid");
                item._roomno        = rSet.getString("roomno");
                item._roomsize      = rSet.getInt("roomsize");
                item._arrive        = rSet.getDate("arrive");
                item._depart        = rSet.getDate("depart");
                item._pricePrNight  = rSet.getLong("priceprnight");
                item._total         = rSet.getLong("total");
                currentInv.addItem(item);
                }
            // Store the last read invoice.
            if (currentInv != null) 
                {
                listOfInvoices.add(currentInv);
                }
            res.setResult(listOfInvoices);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("InvoicesPeriod() " + ex.getMessage());
            res.Failed("Could not read from database.");
            }
        finally
            {
            try
                {
                if (readStmt != null) readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get a specific invoice.
     * 
     * @param invNumber The invoice number. 
     */
    /* --------------------------------------------------------------------- */
    public DBR Invoice(int invNumber)
        {
        DBR res = new DBR();
        
        String query = "select * from invoice, invitems, customer "
            + "where "
            + "invoice.id = " + Integer.toString(invNumber) + " and "
            + "invitems.invid = invoice.id and "
            + "customer.id = invoice.customerid;";
        
        Statement readStmt = null;
        try
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet rSet = readStmt.executeQuery(query);

            Inv theInvoice = null;
            while (rSet.next()) 
                {
                if (theInvoice == null) 
                    {
                    theInvoice = new Inv();
                    theInvoice._id           = rSet.getInt("id");
                    theInvoice._user         = rSet.getString("user");
                    theInvoice._created      = rSet.getDate("created");
                    theInvoice._customer     = rSet.getInt("customerid");
                    theInvoice._customerName = rSet.getString("firstname");
                    theInvoice._closed       = rSet.getBoolean("isClosed");
                    }
                InvItem item = new InvItem();
                item._itemid        = rSet.getInt("itemid");
                item._invid         = rSet.getInt("invid");
                item._roomno        = rSet.getString("roomno");
                item._roomsize      = rSet.getInt("roomsize");
                item._arrive        = rSet.getDate("arrive");
                item._depart        = rSet.getDate("depart");
                item._pricePrNight  = rSet.getLong("priceprnight");
                item._total         = rSet.getLong("total");
                theInvoice.addItem(item);
                }
            res.setResult(theInvoice);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("Invoice(" + Integer.toString(invNumber) + ") " + ex.getMessage());
            res.Failed("Could not read from database.");
            }
        finally
            {
            try
                {
                if (readStmt != null) readStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return res;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Return an invoice item.
     * 
     * @param itemID A unique identifier for the item.
     */
    /* --------------------------------------------------------------------- */
    public DBR InvoiceItem(int itemID)
        {
        DBR dbrResult = new DBR();
        String query = "select * from invitems where itemid = " + itemID + ";";
        Statement readStmt = null;
        
        try 
            {
            readStmt = _hsqldbConnection.createStatement();
            ResultSet result = readStmt.executeQuery(query);
            
            InvItem item = new InvItem();
            result.next();
            item._itemid        = itemID;
            item._invid         = result.getInt("invid");
            item._roomno        = result.getString("roomno");
            item._roomsize      = result.getInt("roomsize");
            item._arrive        = result.getDate("arrive");
            item._depart        = result.getDate("depart");
            item._pricePrNight  = result.getLong("priceprnight");
            item._total         = result.getLong("total");
            dbrResult.setResult(item);
            } 
        catch (Exception e)
            {
            AppLog.Instance().Error("InvoiceItem() " + e.getMessage());
            dbrResult.Failed(e.getMessage());
            } 
        finally 
            {
            try 
                {
                if (readStmt != null) readStmt.close();
                } 
            catch (Exception ex) 
                {
                }
            }
        
        return dbrResult;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Update an invoice item. For now only the price per item and the total 
     * are updated.
     * 
     * @param item The invoice item.
     */
    /* --------------------------------------------------------------------- */
    public DBR InvoiceItemUpdate(InvItem item)
        {
        DBR dbr = new DBR();
        String query = "update invitems set "
            + "priceprnight = " + item._pricePrNight + ", "
            + "total = " + item._total + " "
            + "where "
            + "itemid = " + item._itemid + ";";
        
        Statement updateStmt = null;
        try
            {
            updateStmt = _hsqldbConnection.createStatement();
            updateStmt.execute(query);
            }
        catch (Exception ex)
            {
            AppLog.Instance().Error("InvoiceItemUpdate() " + ex.getMessage());
            dbr.Failed(ex.getMessage());
            }
        finally
            {
            try
                {
                if (updateStmt != null) updateStmt.close();
                }
            catch (Exception ex)
                {
                }
            }
        
        return dbr;
        }

    /* --------------------------------------------------------------------- */
    /**
     * Create an invoice for a reservation.
     * 
     * @param reservation Needs an invoice.
     */
    /* --------------------------------------------------------------------- */
    public DBR InvoiceCreate(Res reservation)
        {
        DBR result = new DBR();
        
        // <editor-fold defaultstate="collapsed" desc="Gathering info.">
        Customer cust = (Customer)Customer(reservation._custid).Result();
        DBR dbrPricelist = Pricelist(cust._pricelist, reservation._periodstart);
        Pricelist pl = null;
        if (dbrPricelist.OK()) { pl = (Pricelist)dbrPricelist.Result(); }
        
        HashMap numGuestsPerRoom = reservation.getGuestsPerRoom();
        Set<String> setRooms = numGuestsPerRoom.keySet();
        ArrayList<InvItem> itemsForSQL = new ArrayList();
        for (String room : setRooms) 
            {
            int ginroom = (Integer)numGuestsPerRoom.get(room);
            long priceOfRoom = 0;
            if (dbrPricelist.OK()) 
                {
                for (IPriceItem item : pl._items) 
                    {
                    if (room.compareTo(item.roomNo()) == 0) 
                        {
                        // Located the room in the pricelist. Determine the
                        // price for the number of guests.
                        switch (ginroom) 
                            {
                            case 1 : priceOfRoom = item.guest1(); break;
                            case 2 : priceOfRoom = item.guest2(); break;
                            case 3 : priceOfRoom = item.guest3(); break;
                            case 4 : priceOfRoom = item.guest4(); break;
                            case 5 : priceOfRoom = item.guest5(); break;
                            default: priceOfRoom = 0;
                            }
                        break;
                        }
                    }
                }//END-OF if (dbrPricelist.OK())
            
            // We have the price for the room for 1 night.
            // Now we get the number of nights, multiply by the price
            // and we have enough info to create an InvItems object which will
            // be used later to construct the SQL string.
            InvItem invoiceItem = new InvItem();
            for (ResGuest guest : reservation.getGuestList()) 
                {
                if (room.compareTo(guest._roomno) == 0) 
                    {
                    int numOfNights = Util.DifferenceInDays(guest._arr, guest._dep);
                    invoiceItem._roomno = room;
                    invoiceItem._arrive = guest._arr;
                    invoiceItem._depart = guest._dep;
                    invoiceItem._pricePrNight = priceOfRoom;
                    invoiceItem._total = numOfNights * priceOfRoom;
                    break;
                    }
                }
            itemsForSQL.add(invoiceItem);
            }//END-OF for (String room : setRooms)
        // </editor-fold>
        
        // We have enough info to create the invoice and its items.
        String query = "insert into invoice "
            + "("
            + "user, "
            + "created, "
            + "customerid, "
            + "isclosed"
            + ") values "
            + "("
            + "'" + reservation._user                 + "', "
            + "'" + _DtoISO.format(reservation._made) + "', "
            + reservation._custid                     + ", "
            + "false"
            + ");";
        boolean invoiceOK = sqlExecuteInserUpdateDelete(query);
        if (! invoiceOK) 
            {
            result.Failed("Could not create invoice.");
            return result;
            }
        int invoiceID = getInvMaxID();
        
        // Create the invoice items.
        StringBuilder sbQuery = new StringBuilder();
        for (InvItem item : itemsForSQL) 
            {
            String isoArrive = Util.DateToIso(item._arrive);
            String isoDepart = Util.DateToIso(item._depart);
            sbQuery.append("insert into invitems (");
            sbQuery.append(
                  "invid, "
                + "roomno, "
                + "arrive, "
                + "depart, "
                + "priceprnight, "
                + "total");
            sbQuery.append(") values (");
            sbQuery.append(
                  invoiceID          + ", "
                + "'" + item._roomno + "', "
                + "'" + isoArrive    + "', "
                + "'" + isoDepart    + "', "
                + item._pricePrNight + ", "
                + item._total);
            sbQuery.append("); ");
            }
        boolean itemsOK = sqlExecuteInserUpdateDelete(sbQuery.toString());
        if (! itemsOK) 
            {
            result.Failed("Could not create invoice.");
            // Have to clean up the invoice alreay created.
            String delInvoice = "delete from inv where inv.id = " + invoiceID + ";";
            sqlExecuteInserUpdateDelete(delInvoice);
            }
        
        // Finally we link the invoice to the reservation.
        String linkQuery = "update res set "
            + "res.invid = " + invoiceID + " "
            + "where res.id = " + reservation._id + ";";
        sqlExecuteInserUpdateDelete(linkQuery);
        
        return result;
        }
    
    
    /* --------------------------------------------------------------------- */
    /**
     * Mark an invoice as closed, meaning it can no longer be altered.
     * 
     * @param invID Target
     * @return True if no errors.
     */
    /* --------------------------------------------------------------------- */
    public boolean InvoiceClose(int invID) 
        {
        String query = "update invoice set "
            + "invoice.isClosed = true "
            + "where "
            + "invoice.id = " + invID + ";";
        
        return sqlExecuteInserUpdateDelete(query);
        }
    
    }//END-OF-CLASS Delphi
