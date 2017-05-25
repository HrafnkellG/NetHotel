package hg.html5;

import hg.util.Util;

/* ========================================================================= */
/**
 * An element with two input fields which the jQuery lib will use to create
 * date-picker objects.
 */
/* ========================================================================= */
public class Period extends Widget
    {
    private String _cssClass = "";
    private String _cssID_From = "";
    private String _cssID_To = "";
    private String _locFrom = "";
    private String _locTo = "";
    
    /* --------------------------------------------------------------------- */
    /**
     * Create the element.
     * 
     * @param cssClass A CSS class for the whole element.
     * @param cssIDFrom A CSS id for the input element holding the FROM date.
     * @param cssIDTo A CSS id for the input element holding the TO date.
     * @param locFrom Localized text for the 'Period from' phrase.
     * @param locTo Localized text for the 'to' word.
     */
    /* --------------------------------------------------------------------- */
    public Period(
    String cssClass, 
    String cssIDFrom, 
    String cssIDTo,
    String locFrom,
    String locTo) 
        {
        if (Util.StringOK(cssClass ) &&
            Util.StringOK(cssIDFrom) &&
            Util.StringOK(cssIDTo  )) 
            {
            _cssClass   = cssClass;
            _cssID_From = cssIDFrom;
            _cssID_To   = cssIDTo;
            }
        else 
            {
            throw new IllegalArgumentException("Missing CSS class or id.");
            }
        if (Util.StringOK(locFrom)) { _locFrom = locFrom; }
        if (Util.StringOK(locTo  )) { _locTo = locTo; }
        }

    @Override
    public String Render()
        {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"").append(_cssClass).append("\">\n");
        
        // The from field
        sb.append("<span>").append(_locFrom).append("</span>");
        sb.append("<span><input id=\"").append(_cssID_From).append("\" class=\"dpinput\" type=\"text\" /></span>\n");
        
        // The to field.
        sb.append("<span>").append(_locTo).append("</span>");
        sb.append("<span><input id=\"").append(_cssID_To).append("\" class=\"dpinput\" type=\"text\" /></span>\n");
        
        sb.append("</div>\n");
        
        return sb.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
