package hg.html5;

import hg.util.Util;

/* ========================================================================= */
/**
 * Define a caption for the content of the page.
 */
/* ========================================================================= */
public class ConCaption extends Widget
    {
    private String _caption = "";
    
    public ConCaption(String caption) 
        {
        if (Util.StringOK(caption)) 
            {
            _caption = caption;
            }
        }
    
    @Override
    public String Render()
        {
        StringBuilder cap = new StringBuilder();
        cap.append("<div class=\"pagetitle\">").append(_caption).append("</div>\n");
        return cap.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
