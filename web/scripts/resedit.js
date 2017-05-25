/* ********************************************************************** 
 * Script for the ResEdit page. 
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      nh.js
 * 
 * CSS IDs of elements used by the script:
 *      res_edit_guest   - Div for the 'Edit' dialog
 *      idres_guest_name - Text input for name 
 *      idres_nat        - Drop-down for country
 *      idres_identifier - Input for passport or other id
 *      idres_sex        - Drop-down for gender
 *      idres_rooms      - Drop-down for available rooms
 *      
 * Variables created on the server side:
 *      reservation_id   - Database id for the reservation
 *      save             - Localized text
 *      cancel           - Localized text
 *      del              - Localized text
 *      help             - Localized text
 *      guest_edit_title - Localized text
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
jQuery(document).ready(InitResEdit);

/* Array whose indexes are the text on the dialog-buttons and the
 * values are references to the call-back functions for the buttons.
 * Initalized in the InitResEdit() function. */
var _ButtonsGuestEdit = {};

/* The id of the current guest being edited. */
var _currGuest;

/* ----------------------------------------------------------------------
 * 
 * Page has loaded, run initializers.
 *
 * ---------------------------------------------------------------------- */
function InitResEdit() {
    UPInit(); /* From userpreferences.js */
    jQuery('#res_edit_guest').dialog({autoOpen: false, modal: true});
    
    // Initialize the arrays which control how many buttons will be on
    // each dialog, what text will be on them and which function will be
    // called when the user clicks on them. These arrays are then fed to
    // the "dialog()" jQuery function.
    // The variables referenced inside the brackets are created on the server.
    // They hold the localized text for the buttons.
    _ButtonsGuestEdit[help  ] = ButtonEditHelp;
    _ButtonsGuestEdit[del   ] = ButtonEditDelete;
    _ButtonsGuestEdit[cancel] = ButtonEditCancel;
    _ButtonsGuestEdit[save  ] = ButtonEditSave;
}

/* ----------------------------------------------------------------------
 * 
 * Delete a guest from the reservation.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditDelete() { 
    var params = "idres_guest=" + _currGuest;
    
    jQuery.ajax({
        url: "ResGuestDelete",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            // Something went wrong.
            if (data !== 'ok') {
                alert(data);
            }
            // Success, refresh page
            else {
                window.location.reload(true);
            }
        },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Save changes to the guest.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditSave() {
    // Name of the guest
    var name = document.getElementById('idres_guest_name').value;
    
    // Nationality of the guest (country)
    var selObj = document.getElementById('idres_nat');
    var index = selObj.selectedIndex;
    var country = selObj.options[index].value;
    
    // Passport no. or identifier for the guest
    var ident = document.getElementById('idres_identifier').value;
    
    // Gender of the guest
    var gender = document.getElementById('idres_sex').value;
    
    // Number of the room
    selObj = document.getElementById('idres_rooms');
    index = selObj.selectedIndex;
    var room = selObj.options[index].value;
    
    var params = 
         "resg_guest_db_id=" + _currGuest
       + "&resg_name="       + name
       + "&resg_nat="        + country
       + "&resg_ident="      + ident
       + "&resg_gender="     + gender
       + "&resg_roomno="     + room;
       
    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call
    jQuery.ajax({
        url: "ResGuestUpdate",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            // Something went wrong.
            if (data !== 'ok') {
                alert(data);
            }
            // Success, refresh page
            else {
                window.location.reload(true);
            }
        },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * User clicked on the 'delete' button.
 *
 * ---------------------------------------------------------------------- */
function DeleteReservation() {
    var params = "res_id=" + reservation_id;
    jQuery.ajax({
        url: "ResDelete",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            // Something went wrong.
            if (data !== 'ok') {
                alert(data);
            }
            // Success, relocate to the new reservation page.
            else {
                window.location = "Reservation";
            }
        },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * User clicked on a guest in the table, display in dialog.
 *
 * ---------------------------------------------------------------------- */
function EditGuest(guestID) {
    _currGuest = guestID;
    var params = "guest_id=" + guestID;
    
    jQuery.ajax({
        url: "ResGuestShowDlg",
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
            jQuery('#res_edit_guest').html(data);
            jQuery('#res_edit_guest').dialog("option", "title", guest_edit_title);
            jQuery('#res_edit_guest').dialog("option", "buttons", _ButtonsGuestEdit);
            jQuery('#res_edit_guest').dialog('open');
        },
        
        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Close the edit dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditCancel() {
    jQuery('#res_edit_guest').dialog('close'); 
}

function ButtonEditHelp() { alert('TO-DO'); }




/* ----------------------------------------------------------------------
 * 
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert(errTxt + ":    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}