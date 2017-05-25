package hg.html5;

import java.util.ArrayList;

/* ========================================================================= */
/**
 * The 'tfoot' element in a table.
 * @author hg
 */
/* ========================================================================= */
public class TableFoot extends Widget
    {
    private ArrayList<TableRow> _rows = new ArrayList();
    private int _numCols;
    
    /* --------------------------------------------------------------------- */
    /**
     * When created the foot element must know how many columns are in the table.
     * @param numColumns Number of columns (i.e. cells).
     */
    /* --------------------------------------------------------------------- */
    TableFoot(int numColumns)
        {
        _numCols = numColumns;
        }

    @Override
    public String Render()
        {
        if (_rows.isEmpty())
            {
            // No row has been defined, return an empty row.
            // A 'tfoot' element must have at least 1 'tr', and the
            // 'tr' must have at least 1 'td'.
            return "<tfoot><tr><td></td></tr></tfoot>\n";
            }
        
        // Opening tag 'tfoot'.
        StringBuilder sb = new StringBuilder("<tfoot>\n");
        
        // Add the rows.
        for (TableRow r : _rows)
            {
            sb.append(r.Render());
            }
        
        // Closing tag 'tfoot'.
        sb.append("</tfoot>\n");
        
        return sb.toString();
        }

    public TableRow AddRow()
        {
        TableRow row = new TableRow(_numCols);
        _rows.add(row);
        return row;
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
