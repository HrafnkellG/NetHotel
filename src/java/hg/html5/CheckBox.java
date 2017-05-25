package hg.html5;

import hg.util.Util;

/* ========================================================================= */
/**
 * A stand alone check box, wrapped in a div.
 */
/* ========================================================================= */
public class CheckBox extends Widget
    {
    private String  _classes    = null;
    private String  _id         = null;
    private String  _jsCallback = null;
    private boolean _state      = true;
    private String  _label      = null;
    
    /* --------------------------------------------------------------------- */
    /**
     * Create the check box.
     * @param cssClass One or more classes for the enclosing div.
     * @param cssID An ID for the checkbox.
     * @param jsCallback Javascript event handler when state changes.
     * @param initialState Does the box start checked? True = checked.
     * @param label Text shown to the user.
     */
    /* --------------------------------------------------------------------- */
    public CheckBox(
    String  cssClass, 
    String  cssID, 
    String  jsCallback, 
    boolean initialState, 
    String  label) 
        {
        // Must have an ID.
        if (! Util.StringOK(cssID)) 
            {
            throw new IllegalArgumentException("CSS ID missing.");
            }
        
        _classes    = cssClass;
        _id         = cssID;
        _jsCallback = jsCallback;
        _state      = initialState;
        _label      = label;
        if (_label == null) 
            {
            _label = "";
            }
        }
    
    @Override
    public String Render()
        {
        StringBuilder sb = new StringBuilder();
        
        // Open div.
        sb.append("<div");
        if (Util.StringOK(_classes)) 
            {
            sb.append(" class=\"").append(_classes).append("\"");
            }
        sb.append(">\n");
        
        // Construct check box.
        sb.append("<input type=\"checkbox\" id=\"").append(_id).append("\"");
        if (Util.StringOK(_jsCallback)) 
            {
            sb.append(" onclick=\"").append(_jsCallback).append("\"");
            }
        if (_state) 
            {
            sb.append(" checked");
            }
        sb.append(" /><label>").append(_label).append("</label>\n");
        
        // Close div
        sb.append("</div>\n");
        
        return sb.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
