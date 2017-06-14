// Wait for the page to load.
jQuery(document).ready(InitMain);



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
        
        // This variable is defined on the server.
        jQuery('#jqtabs').tabs({ active: lastSelectedTab });
    }
    catch (e) {
        alert("error = " + e.message);
    }
}


/* ----------------------------------------------------------------------
 * 
 * When a tab has finished loading we check whether it is the graph tab,
 * if so we initialize the datepicker.
 *
 * ---------------------------------------------------------------------- */
function trackTabLoad(event, ui) {
    var newTI = $( "#jqtabs" ).tabs( "option", "active" );
    if (newTI === 0) {
        var the_date = new Date(2017,5,13);
        $('#dpgraph').datepicker($.datepicker.regional[""]);
        $('#dpgraph').datepicker("option", "dateFormat", "yy-mm-dd");
        $('#dpgraph').datepicker('setDate', the_date);
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
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert("Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}