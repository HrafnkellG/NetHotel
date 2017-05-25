package hg.cons;

/*  ======================================================================== */
/** Defines all the languages which are supported and all the styles which 
 *  are available.
 * 
 * @author hg
 */
/*  ======================================================================== */
public interface LangStyleConst 
    {
    /** Codes for supported languages. */
    String[] Languages = 
        {
        "en",
        "is"
        };
    
    /** Where to find the styles. */
    String[] StylePaths =
        {
        "styles/default/",      // 01
        "styles/YellowPages/",  // 02
        "styles/Titian/"        // 03
        };
    
    /** The text to display for each style. */
    String[] StyleDescriptions =
        {
        "Default",          // 01
        "The Yellow Pages", // 02
        "Titian"            // 03
        };
    }
