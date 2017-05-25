package hg.html5;

import hg.util.Util;


/* ========================================================================= */
/**
 * The "p" tag in HTML
 * @author hg
 */
/* ========================================================================= */
public class Paragraph extends Widget
    {
    private String _text = "";
    
    public Paragraph(String txt) 
        {
        if (Util.StringOK(txt)) 
            {
            _text = txt;
            }
        }
    
    @Override
    public String Render()
        {
        return "<p>" + _text + "</p>";
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }// END-OF class Paragraph
