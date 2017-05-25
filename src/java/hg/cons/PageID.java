package hg.cons;

/*  ======================================================================== */
/** Identifiers for the pages and dialogs in the application.<p>
 *  Each page and dialog is assigned a number, enabling e.g. the help-code 
 *  to generate the correct help-text when requested.<p>
 *  NOTA BENE: If any of these values are changed, they must be changed also 
 *  in the javascript files.
 * 
 */
/*  ======================================================================== */
public interface PageID 
    {
    int MAINPAGE        = 10; 
    int USERPREFS       = 20;
    int USERNEW         = 30;
    int USEREDIT        = 40;
    int USERSPAGE       = 50;
    int ROOMSPAGE       = 80;
    int PRICELISTSPAGE  = 90;
    int HOTELCONFIG     = 110;
    int CUSTOMERS       = 120;
    int ROOMNEW         = 130;
    int ROOMEDIT        = 140;
    int CHECKIN         = 150;
    int CHECKOUT        = 190;
    int INVLIST         = 200;
    
    /** Pricelist dialog. */
    int PL_DIALOG       = 150;
    int RESERVATION     = 160;
    
    int RESERVATION_EDIT = 170;
    int RESERVATION_LIST = 180;
    }
