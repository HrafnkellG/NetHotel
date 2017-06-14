package hg.html5;

import hg.util.Util;



/* ========================================================================= */
/**
 * Create a single jQuery Datepicker widget, with a label in front with 
 * text describing its purpose, usually just 'Date'.
 * 
 * @author hg
 */
/* ========================================================================= */
public class JQDatePicker extends Widget
    {
    private String _label = ".";
    private String _cssClass;
    private String _cssID;
    
    // *********************************************************************
    /**
     * Constructor.
     * 
     * @param labelText The text which will be put in front of the datepicker.
     * @param cssClass The CSS class.
     * @param cssID The CSS ID, used by jQuery to create the datepicker.
     */
    public JQDatePicker(String labelText, String cssClass, String cssID) 
        {
        if (Util.StringOK(labelText)) 
            {
            _label = labelText;
            }
        if (!Util.StringOK(cssClass) || !Util.StringOK(cssID)) 
            {
            throw new UnsupportedOperationException("Bad input for JQDatePicker constructor.");
            }
        _cssClass = cssClass;
        _cssID = cssID;
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    @Override
    public String Render()
        {
        StringBuilder sb = new StringBuilder();
        sb.append("<div><pre>").append(_label).append("  <input class=\"").append(_cssClass).append("\" id=\"").append(_cssID).append("\" type=\"text\"></pre></div>\n");
        return sb.toString();
        }
    
    }
