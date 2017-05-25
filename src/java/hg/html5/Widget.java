package hg.html5;

/* ========================================================================= */
/**
 * The base class for the web page suite.
 * 
 * @author hg
 */
/* ========================================================================= */
abstract class Widget
    {
    
    /* --------------------------------------------------------------------- */
    /**
     * The browser will display the supplied text when the mouse is hovered
     * over this element.
     * 
     * @param titleText The pop-up text to display.
     */
    /* --------------------------------------------------------------------- */
    public abstract void TitlePopupText(String titleText);
    
    /* --------------------------------------------------------------------- */
    /**
     * Turn this widget into a web page element.
     * @return HTML5 markup for the element this widget represents.
     */
    /* --------------------------------------------------------------------- */
    public abstract String Render();
    }
