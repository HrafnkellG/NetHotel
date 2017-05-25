package hg.html5;

import hg.cons.CSS;
import hg.util.Util;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * Actions available to the user.
 * 
 * @author hg
 */
/* ========================================================================= */
public class Actions extends Widget
    {
    private String _actionsCSSClass = CSS.CACTIONS;
    private ArrayList<Action> _actions = new ArrayList();
    
    public Actions(String cssClass) 
        {
        if (Util.StringOK(cssClass)) 
            {
            _actionsCSSClass = cssClass;
            }
        }

    @Override
    public String Render()
        {
        // Must have something to show the user.
        if (_actions.isEmpty())
            {
            throw new UnsupportedOperationException();
            }
        
        // Enclosing div element.
        StringBuilder sb = new StringBuilder("<div class=\"");
        sb.append(_actionsCSSClass);
        sb.append("\">\n");
        
        // Insert all the actions.
        for (Action a : _actions)
            {
            sb.append(a.Render());
            }
        
        // Close div.
        sb.append("</div>\n");
        
        return sb.toString();
        }

    
    public void AddAction(String value, String action)
        {
        // Both parameters must be valid.
        if ((! Util.StringOK(value)) || (! Util.StringOK(action)))
            {
            throw new UnsupportedOperationException("Value and action must both be valid.");
            }
        
        Action a = new Action(value, action);
        _actions.add(a);
        }


    public void AddAction(String value, String action, String cssClass)
        {
        if ((! Util.StringOK(value)) || (! Util.StringOK(action)) || (! Util.StringOK(cssClass)))
            {
            throw new UnsupportedOperationException("Value, action and class must all be valid.");
            }
        
        Action a = new Action(value, action, cssClass);
        _actions.add(a);
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    
    /* ========================================================================= */
    /**
     * Reresents a single action.
     */
    /* ========================================================================= */
    private class Action extends Widget 
        {
        private String _value;
        private String _action;
        private String _cssClass = CSS.CACTION;
        
        Action(String v, String a)
            {
            _value = v;
            _action = a;
            }

        Action(String v, String a, String c)
            {
            _value = v;
            _action = a;
            if (Util.StringOK(c))
                {
                _cssClass = c;
                }
            }

        @Override
        public String Render()
            {
            StringBuilder sb = new StringBuilder("<a class=\"");
            sb.append(_cssClass);
            sb.append("\" href=\"");
            sb.append(_action);
            sb.append("\">");
            sb.append(_value);
            sb.append("</a>\n");
            
            return sb.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
    
    }
