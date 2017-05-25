package hg.html5;

import hg.util.Util;


/* --------------------------------------------------------------------- */
/**
 * The 'thead' element in a table.
 * @author hg
 */
/* --------------------------------------------------------------------- */
public class TableHead extends Widget
    {
    private Cell[] _colHeaders;
    
    /* --------------------------------------------------------------------- */
    /**
     * Create an instance.
     * @param colNames The column names.
     */
    /* --------------------------------------------------------------------- */
    TableHead(String[] colNames)
        {
        _colHeaders = new Cell[colNames.length];
        for (int i = 0; i < colNames.length; i++)
            {
            _colHeaders[i] = new Cell(colNames[i]);
            }
        }

    @Override
    public String Render()
        {
        // Opening tags 'thead' and 'tr'.
        StringBuilder sb = new StringBuilder("<thead>\n<tr>\n");
        
        // Insert cells.
        for (int i = 0; i < _colHeaders.length; i++)
            {
            sb.append(_colHeaders[i].Render());
            }
        
        // Closing tags 'thead' and 'tr'.
        sb.append("</tr>\n</thead>\n");
        
        return sb.toString();
        }

    public void setCellCSS(int cellNum, String cssClass)
        {
        if (Util.StringOK(cssClass))
            {
            _colHeaders[cellNum].setCSSClass(cssClass);
            }
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    /**
     * Embedded class representing a cell in the row.
     */
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    private class Cell extends Widget
        {
        private String _cssClass;
        private String _value;
        
        /* --------------------------------------------------------------------- */
        /**
         * On creation the cell receives its value.
         * @param value Content of cell.
         */
        /* --------------------------------------------------------------------- */
        Cell(String value)
            {
            _value = value;
            }
        
        public void setCSSClass(String cssClass)
            {
            if (Util.StringOK(cssClass))
                {
                _cssClass = cssClass;
                }
            }

        @Override
        public String Render()
            {
            // Opening tag 'th'.
            StringBuilder sb = new StringBuilder("<th");
            if (_cssClass != null)
                {
                sb.append(" class=\"");
                sb.append(_cssClass);
                sb.append("\"");
                }
            sb.append(">");
            
            sb.append(_value);
            
            // Closing tag 'th'.
            sb.append("</th>\n");
            
            return sb.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
    }
