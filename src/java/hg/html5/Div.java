package hg.html5;

import hg.util.Util;

/* --------------------------------------------------------------------- */
/**
 * This is a wrapper for other elements, mainly to provide a container
 * for jQuery to manipulate.
 */
/* --------------------------------------------------------------------- */
public class Div extends Widget
    {
    private String _cssClass;
    private Widget _widget = null;
    
    public Div(String cssClass, Widget content) 
        {
        if (! Util.StringOK(cssClass)) 
            {
            throw new IllegalArgumentException("CSS ID missing.");
            }
        if (content != null) 
            {
            _widget = content;
            }
        _cssClass = cssClass;
        }
    
    
    @Override
    public String Render()
        {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"").append(_cssClass).append("\">\n");
        if (_widget != null) 
            {
            sb.append(_widget.Render());
            }
        sb.append("</div>\n");
        
        return sb.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
