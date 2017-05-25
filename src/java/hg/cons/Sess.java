package hg.cons;

/* --------------------------------------------------------------------- */
/**
 * Session indexes for the objects created for the user.
 */
/* --------------------------------------------------------------------- */
public interface Sess
    {
    /** The logged in user. Object with the type 'User' */
    String USER = "user";
    
    /** Hotel settings. Object with the type 'Hotel' */
    String HOTEL = "hotel";
    
    /** Countries in alphabetical order, array of hg.util.Country objects. */
    String COUNTRIES = "countries";
    
    /** Which tab on the main page is active. */
    String ACTIVE_TAB = "activetab";
    }
