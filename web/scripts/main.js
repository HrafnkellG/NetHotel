/* ====================================================================== 
 * Variables created by the server:
 *      currentDateDefinedByHotel
 *      lastSelectedTab
 * 
 * ====================================================================== */

// Wait for the page to load.
jQuery(document).ready(InitMain);


// For the graph-table, holds those cells which the user has selected to
// be part of a new reservation. Each cell represents a date 
// for a particular room.
var _selectedCells = new Array();



/* ----------------------------------------------------------------------
 * 
 * Page has loaded, initialize.
 *
 * ---------------------------------------------------------------------- */
function InitMain() {
    try {
        UPInit();
        jQuery('#jqtabs').tabs({ activate: trackTabActivation });
        jQuery('#jqtabs').tabs({ load: trackTabLoad });
        
        // This variable, lastSelectedTab, is defined on the server.
        jQuery('#jqtabs').tabs({ active: lastSelectedTab });
    }
    catch (e) {
        alert("error = " + e.message);
    }
}


/* ----------------------------------------------------------------------
 * 
 * When a tab has finished loading we check whether it is the graph tab,
 * if so we initialize the datepicker, clear the selected cells collection.
 *
 * ---------------------------------------------------------------------- */
function trackTabLoad(event, ui) {
    var newTI = $( "#jqtabs" ).tabs( "option", "active" );
    if (newTI === 0) {
        $('#dpgraph').datepicker($.datepicker.regional[""]);
        $('#dpgraph').datepicker("option", "dateFormat", "yy-mm-dd");
        $('#dpgraph').datepicker('setDate', currentDateDefinedByHotel);
        
        // Set the handler on the input field, only way 
        // to catch changes to the date.
        $("#dpgraph").bind("change", GraphDateChanged);
        
        _selectedCells = new Array();
    }
}


/* ----------------------------------------------------------------------
 * 
 * Each time the user selects a new tab it is reported to the server.
 *
 * ---------------------------------------------------------------------- */
function trackTabActivation(event, ui) {
    var newTI = $( "#jqtabs" ).tabs( "option", "active" );
    var params = 'newTabIndex=' + newTI;
    jQuery.ajax({
        url: "TabNewIndex",
        cache: false,
        data: params,

        /* Callback for success. */
        success:  function(data)
            {
            // We do not do anything with the returned data.
            // If an error occurred, ignore it.
            },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * (C)reate(D)emo(D)ata
 *
 * ---------------------------------------------------------------------- */
function CDD() {
    jQuery.ajax({
        url: "CreateDemoData",
        cache: false,

        /* Callback for success. */
        success:  function(data)
            {
            if (data === 'ok')
                {
                window.location.reload(true);
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


/* ----------------------------------------------------------------------
 * 
 * A row selected in the "Room status" tab. This flips the "clean" status
 * for the room.
 *
 * ---------------------------------------------------------------------- */
function TabRoomsRowSelect(roomNo) {
    var params = 'roomNum=' + roomNo;
    
    jQuery.ajax({
        url: "TabFlipRoomStatus",
        cache: false,
        data: params,

        /* Callback for success. */
        success:  function(data)
            {
            if (data === 'ok')
                {
                window.location.reload(true);
                }
            // If something went wrong, do nothing.
            },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * A row has been selected in the "Guests arriving today" tab.
 * Redirect to the check-in page with the reservation id for 
 * the selected guest.
 *
 * ---------------------------------------------------------------------- */
function TabArriveRowSelect(reservationID) {
    window.location = "Checkin?resID=" + reservationID;
}

/* ----------------------------------------------------------------------
 * 
 * A row has been selected in the "Guests departing today" tab.
 * Redirect to the check-out page with the reservation id for 
 * the selected guest.
 *
 * ---------------------------------------------------------------------- */
function TabDepartRowSelect(reservationID) {
    window.location = "Checkout?resID=" + reservationID;
}

/* ----------------------------------------------------------------------
 * 
 * User selected an invoice in the "Open invoices" tab.
 *
 * ---------------------------------------------------------------------- */
function TabInvoicesRowSelect(invoiceNo) {
    window.location = "Invoice?invID=" + invoiceNo;
}


/* ----------------------------------------------------------------------
 * 
 * User selected a guest in the "Checked in " tab.
 *
 * ---------------------------------------------------------------------- */
function TabCheckedinRowSelect(reservationID) {
    window.location = "Checkout?resID=" + reservationID;
}



/* ----------------------------------------------------------------------
 * 
 * The date for the graph has changed, reload the table showing the week.
 *
 * ---------------------------------------------------------------------- */
function GraphDateChanged() {
    var newGraphDate = $("#dpgraph").datepicker("getDate");
    // nhDateToString is in nh.js
    var dateCompactString = nhDateToString(newGraphDate);
    var params = "newGraphDate=" + dateCompactString;
    
    // Make the ajax call.
    jQuery.ajax({
        url: "TabGraphNewDate",
        cache: false,
        data: params,

        /* Callback for success. */
        success:  function(data)
            {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            // If the first letter is a capital e then it is an error message.
            var firstChar = data.substring(0, 1);
            if (firstChar === "E") {
                alert(data);
                return;
            }
            // Put the table into the div which wraps it
            // and clear the cell collection.
            $(".graphwrap").html(data);
            _selectedCells = new Array();
            },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * A cell in the graph has been clicked. If the cell was selected previously
 * it will be removed from the collection of selected cells.
 *
 * ---------------------------------------------------------------------- */
function TabGraphCellClickFree(clickedCell, cellID) {
    // We use a css class to mark those cells which have been selected.
    if ($(clickedCell).hasClass('cell_grselected')) {
       RemoveCellFromCollection(cellID);
       $(clickedCell).removeClass('cell_grselected');
    }
    else {
        AddCellToCollection(cellID);
        $(clickedCell).addClass('cell_grselected');
    }
}

/* ----------------------------------------------------------------------
 * 
 * Remove this cell from the collection of selected cells.
 *
 * ---------------------------------------------------------------------- */
function RemoveCellFromCollection(cellID) {
    for (var i=0; i < _selectedCells.length; i++) {
        var storedCellID = _selectedCells[i];
        if (storedCellID === cellID) {
            _selectedCells.splice(i, 1);
            break;
        }
    }
}

/* ----------------------------------------------------------------------
 * 
 * Add this cell to the collection of selected cells.
 *
 * ---------------------------------------------------------------------- */
function AddCellToCollection(cellID) {
    var newCellIndex = _selectedCells.length;
    _selectedCells[newCellIndex] = cellID;
}

/* ----------------------------------------------------------------------
 * 
 * Create a new reservation from the selected cells in the graph.
 *
 * ---------------------------------------------------------------------- */
function TabGraphNewReservation() {
    if (_selectedCells.length === 0) { 
        return; 
    }
    if (NoGap() !== true) {
        alert('There is a gap.');
        return;
    }
    alert('No gap found.');
}


/* ----------------------------------------------------------------------
 * 
 * Examine the selected cells collection, there must be no gaps in selected
 * days for a given room.
 *
 * ---------------------------------------------------------------------- */
function NoGap() {
    if (_selectedCells.length === 1) {
        return true;
    }
    // At this point we know the collection is >= 2
    _selectedCells.sort();
    var previousCell = _selectedCells[0];
    var noGapFound = true;
    for (var i=1; i < _selectedCells.length; i++) {
        var currentCell = _selectedCells[i];
        var currentRoom  = currentCell.substr(0, currentCell.length - 8);
        var previousRoom = previousCell.substr(0, previousCell.length - 8);
        if (previousRoom !== currentRoom) {
            previousCell = currentCell;
            continue;
        }
        // We are tracking the same room, extract the days.
        var previousDay = parseInt(previousCell.substr(previousCell.length - 2, 2));
        var currentDay  = parseInt(currentCell.substr(currentCell.length   - 2, 2));
        if ((currentDay - previousDay) === 1) {
            // Not a gap between these two selected days.
            previousCell = currentCell;
            continue;
        }
        // There may not be a gap if we are transitioning between months.
        if (currentDay !== 1) {
            // Not transitioning, so a gap.
            noGapFound = false;
            break;
        }
        var previousMonth = parseInt(previousCell.substr(previousCell.length - 4, 2));
        var currentMonth  = parseInt(currentCell.substr(currentCell.length   - 4, 2));
        // First we check the edge case of going from december to january.
        if (previousMonth === 12 &&
            previousDay   === 31 &&
            currentMonth  === 1  &&
            currentDay    === 1) {
            // Not a gap between the two selected days.
            previousCell = currentCell;
            continue;
        }
        // Then we check those months with 30 days.
        if (previousMonth === 4 ||
            previousMonth === 6 ||
            previousMonth === 9 ||
            previousMonth === 11
            &&
            previousDay === 30) {
            // Not a gap between the two selected days.
            previousCell = currentCell;
            continue;
        }
        // Next we check those months with 31 days.
        if (previousMonth !== 2 &&
            previousDay === 31) {
            // Not a gap between the two selected days.
            previousCell = currentCell;
            continue;
        }
        // Only february is left.
        if (previousDay !== 28 && previousDay !== 29) {
            noGapFound = false;
            break;
        }
        // Did not find a gap.
        previousCell = currentCell;
    }
    
    return noGapFound;
}

/* ----------------------------------------------------------------------
 * 
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert("Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}