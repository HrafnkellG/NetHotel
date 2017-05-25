/* ********************************************************************** 
 * Script for the Pricelists page. Creates 2 dialogs: one to create a new
 * price-list, and a second one to edit an existing price-list. Handles the
 * edit of individual price-points in the tables.
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      nh.js
 * 
 * CSS IDs of elements used by the script:
 *      pledit        - jQuery dialog, edit price-list
 *      plnew         - jQuery dialog, define new price-list
 *      plname        - Descriptive text for the price-list
 *      dpperiodstart - CSS id for datepicker, starting date
 *      dpperiodend   - CSS id for datepicker, ending date
 *      
 * Variables created on the server side:
 *      del
 *      pltitlenew
 *      pltitleedit
 *      user_lang
 *      period_start  <-- type of Date
 *      period_end    <-- type of Date
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
jQuery(document).ready(InitPL);

/* Arrays whose indexes are the text on the dialog-buttons and the
 * values are references to the call-back functions for the buttons.
 * Initalized in the InitPL() function. */
var _ButtonsPLEdit = {};
var _ButtonsPLNew = {};

var _currentCell;
var _cellOriginalContent;
var _cellIsBeingEdited = false;

/* These 2 variables identify a cell for the database so it can be 
 * updated on the server side.  _cellPLItem is the id for a record in the
 * pitems table, _cellIndex tells which column to update. */
var _cellPLItem;
var _cellIndex;

/* These 3 variables hold the start date, end date and the db id for a pricelist 
 * that is being edited. Before the edit dialog can be shown the dates must be 
 * initialized so the datepickers can be correctly set. */
var _editPStart;
var _editPEnd;
var _editdbid;

/* ----------------------------------------------------------------------
 * 
 * Page has loaded, run initializers.
 *
 * ---------------------------------------------------------------------- */
function InitPL() {
    UPInit(); /* From userpreferences.js */
    jQuery.datepicker.setDefaults({dateFormat: "yy-mm-dd"});
    jQuery("#accordion").accordion({ collapsible: true, active: false });
    jQuery('#pledit').dialog({autoOpen: false, modal: true});
    jQuery('#plnew').dialog({autoOpen: false, modal: true});
    
    // Initialize the arrays which control how many buttons will be on
    // each dialog, what text will be on them and which function will be
    // called when the user clicks on them. These arrays are then fed to
    // the "dialog()" jQuery function.
    // The variables referenced inside the brackets are created on the server.
    // They hold the localized text for the buttons.
    _ButtonsPLEdit[help  ] = ButtonEditHelp;
    _ButtonsPLEdit[del   ] = ButtonEditDelete;
    _ButtonsPLEdit[cancel] = ButtonEditCancel;
    _ButtonsPLEdit[save  ] = ButtonEditSave;
    
    _ButtonsPLNew[help  ] = ButtonNewHelp;
    _ButtonsPLNew[cancel] = ButtonNewCancel;
    _ButtonsPLNew[save  ] = ButtonNewSave;
}

/* ----------------------------------------------------------------------
 * 
 * Create a price-list.
 *
 * ---------------------------------------------------------------------- */
function PLNew() {
    jQuery.ajax({
        url: "PLNew",
        cache: false,
        
        /* Query succeeded. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            jQuery('#plnew').html(data);
            jQuery('#plnew').dialog("option", "title", pltitlenew);
            jQuery('#plnew').dialog("option", "buttons", _ButtonsPLNew);
            jQuery('#plnew').dialog('open');
            
            // Datepickers: set language and starting dates.
            jQuery('#dpperiodstart').datepicker(jQuery.datepicker.regional[user_lang]);
            jQuery('#dpperiodstart').datepicker("option", "dateFormat", "yy-mm-dd");
            jQuery('#dpperiodstart').datepicker('setDate', period_start);
            
            jQuery('#dpperiodend').datepicker(jQuery.datepicker.regional[user_lang]);
            jQuery('#dpperiodend').datepicker("option", "dateFormat", "yy-mm-dd");
            jQuery('#dpperiodend').datepicker('setDate', period_end);
        },
        
        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Edit a price-list. Before we can show the dialog we must get the period
 * from the server, so we will chain the ajax calls.
 *
 * ---------------------------------------------------------------------- */
function PLEdit(priceListDatabaseID) {
    var params = "pl_databaseid=" + priceListDatabaseID;
    
    // Make the ajax call.
    jQuery.ajax({ 
        url: "PLGetPeriod",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data !== 'error') {
                // We have the dates for start and end as one string, the 
                // format is YYYYMMDDYYYYMMDD
                var pstart = data.substring(0, 8);
                var pend   = data.substring(8, 16);
                
                _editPStart = nhStringToDate(pstart);
                _editPEnd   = nhStringToDate(pend);
                    
                // We are ready to make that second ajax call.
                PLEditShowDialog(priceListDatabaseID);
            }
            // Something went wrong.
            else {
                alert('Could not get data from the server.');
            }
        },

        error: CallbackError
    });
    
//    // Start by getting the period for the pricelist from the database.
//    if (InitPeriodForPricelist(priceListDatabaseID) !== true) {
//        return;
//    }
    
}

/* ----------------------------------------------------------------------
 * 
 * This shows the dialog to edit a pricelist, it will use 2 variables which
 * were initialized in PLEdit().
 *
 * ---------------------------------------------------------------------- */
function PLEditShowDialog(plDBID) {
    _editdbid = plDBID;
    var params = "pl_databaseid=" + plDBID;
    
    jQuery.ajax({
        url: "PLEdit",
        cache: false,
        data: params,
        
        /* Query succeeded. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            $('#pledit').html(data);
            $('#pledit').dialog("option", "title", pltitleedit);
            $('#pledit').dialog("option", "buttons", _ButtonsPLEdit);
            $('#pledit').dialog('open');
            
            // Datepickers: set language and starting dates.
            $('#dpperiodstart').datepicker($.datepicker.regional[user_lang]);
            $('#dpperiodstart').datepicker("option", "dateFormat", "yy-mm-dd");
            $('#dpperiodstart').datepicker('setDate', _editPStart);
            
            $('#dpperiodend').datepicker($.datepicker.regional[user_lang]);
            $('#dpperiodend').datepicker("option", "dateFormat", "yy-mm-dd");
            $('#dpperiodend').datepicker('setDate', _editPEnd);
        },
        
        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Create a new price-list, but use the price info from an old one.
 * (C)opy(T)o(N)ew
 *
 * ---------------------------------------------------------------------- */
function PLEditCTN(priceListDatabaseID) {
    alert('TODO');
}

/* ----------------------------------------------------------------------
 * 
 * Edit the price stored in a cell.
 * cellID   = The CSS id for the td element.
 * itemDBID = The database identifier for the price-list item.
 * cellNo   = The cell index number, they go from 0 to 7.
 *
 * ---------------------------------------------------------------------- */
function EditCell(cellID, itemDBID, cellNo) {
    _cellIsBeingEdited = true;
    _cellPLItem        = itemDBID;
    _cellIndex         = cellNo;
    
    // Make the cell an input field with its own id.
    _currentCell = $('#'+cellID);
    _cellOriginalContent = _currentCell.text();
    _currentCell.html("<input id='edit_cell_id' type='text' value='" + _cellOriginalContent + "' />");
    _currentCell.children().first().focus();
    _currentCell.children().first().select();
    _currentCell.children().first().blur(CellLostFocus);
    _currentCell.children().first().keypress(CellKeypress);
}

/* ----------------------------------------------------------------------
 * 
 * When a cell looses focus but is still being edited, that is interpredet
 * as a cancel operation so the original content is restored.
 *
 * ---------------------------------------------------------------------- */
function CellLostFocus() {
    // Delete the event handlers for the cell.
    _currentCell.children().first().blur(null);
    _currentCell.children().first().keypress(null);
    
    if (_cellIsBeingEdited === false) {
        return;
    }
    
    // Cell is being edited so restore its contents.
    _currentCell.html(_cellOriginalContent);
    _cellIsBeingEdited = false;
}

/* ----------------------------------------------------------------------
 * 
 * If the user has pressed the 'Enter' key we attempt to update the database.
 *
 * ---------------------------------------------------------------------- */
function CellKeypress(e) {
    if (e.which === 13) {
        // Do nothing if the content of the cell is unchanged.
        var inputField = document.getElementById("edit_cell_id");
        var currentContent = inputField.value;
        if (_cellOriginalContent === currentContent) {
            return;
        }
        
        // Create the parameters.
        var params = 
              "item_id="     + _cellPLItem
            + "&cell_index=" + _cellIndex
            + "&cell_value=" + currentContent;
    
        // Make allowance for non-ASCII characters.
        params = encodeURI(params);
        
        // Make the ajax call.
        jQuery.ajax({
            url: "PLItemCellUpdate",
            cache: false,
            data: params,

            /* Callback for success. */
            success: function(data)
                {
                if (data !== 'error')
                    {
                    _cellIsBeingEdited = false;
                    _currentCell.html(data);
                    }
                // Something went wrong, show the error returned.
                else
                    {
                    alert(data);
                    }
                },

            error: CallbackError
        });
    }
}

/* ----------------------------------------------------------------------
 * 
 * Show help for the pricelist dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditHelp() {
    Help(150);
}

/* ----------------------------------------------------------------------
 * 
 * Delete a pricelist.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditDelete() {
    var params = "pl_db_id=" + _editdbid;
    
    // Make the ajax call.
    jQuery.ajax({
        url: "PLDelete",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data === 'ok') {
                window.location.reload(true);
            }
            // Something went wrong, show the error returned.
            else {
                alert(data);
            }
        },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Close the edit pricelist dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditCancel() {
    $('#pledit').dialog('close');
}

/* ----------------------------------------------------------------------
 * 
 * Save changes made to the pricelist.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditSave() {
    var plName = document.getElementById("plname").value;
    var datePerStart = $("#dpperiodstart").datepicker("getDate");
    var datePerEnd   = $("#dpperiodend").datepicker("getDate");
    var strStart = nhDateToString(datePerStart);
    var strEnd   = nhDateToString(datePerEnd);
    
    var params = 
          "pl_db_id="         + _editdbid
        + "&pl_name="         + plName
        + "&pl_period_start=" + strStart
        + "&pl_period_end="   + strEnd;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    jQuery.ajax({
        url: "PLEditSave",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data === 'ok') {
                window.location.reload(true);
            }
            // Something went wrong, show the error returned.
            else {
                alert(data);
            }
        },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Show help for the pricelist dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewHelp() {
    Help(150);
}

/* ----------------------------------------------------------------------
 * 
 * Close the new pricelist dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewCancel() {
    $('#plnew').dialog('close');
}

/* ----------------------------------------------------------------------
 * 
 * Create a new price list
 *
 * ---------------------------------------------------------------------- */
function ButtonNewSave() {
    var plName = document.getElementById("plname").value;
    var datePerStart = $("#dpperiodstart").datepicker("getDate");
    var datePerEnd   = $("#dpperiodend").datepicker("getDate");
    var strStart = nhDateToString(datePerStart);
    var strEnd   = nhDateToString(datePerEnd);
    
    var params = 
          "pl_name="          + plName
        + "&pl_period_start=" + strStart
        + "&pl_period_end="   + strEnd;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    jQuery.ajax({
        url: "PLCreate",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data === 'ok') {
                window.location.reload(true);
            }
            // Something went wrong, show the error returned.
            else {
                alert(data);
            }
        },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert(errTxt + ":    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}