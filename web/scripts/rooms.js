/* ********************************************************************** 
 * Script for the Rooms page. Handles selection of a room from the table
 * and 2 scripts: edit an existing room and create a new one.
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      nh.js
 * 
 * CSS IDs of elements used by the script:
 *      roomedit    - jQuery dialog, edit room
 *      roomnew     - jQuery dialog, define new room
 *      roomno      - text input
 *      roomsize    - text input
 *      roomfloor   - text input
 *      roomtype    - text input
 * 
 * Variables created on the server side:
 *      missrno
 *      missrsize
 *      missrfloor
 *      del
 *      roomtitlenew
 *      roomtitleedit
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
jQuery(document).ready(InitRooms);

/* Arrays whose indexes are the text on the dialog-buttons and the
 * values are references to the call-back functions for the buttons.
 * Initalized in the InitRooms() function. */
var _ButtonsRoomEdit = {};
var _ButtonsRoomNew = {};

var _roomNoForEdit;

/* ----------------------------------------------------------------------
 * 
 * The page has loaded, run initializers, etc.
 *
 * ---------------------------------------------------------------------- */
function InitRooms() {
    UPInit(); /* From userpreferences.js */
    jQuery('#roomedit').dialog({autoOpen: false, modal: true});
    jQuery('#roomnew').dialog({autoOpen: false, modal: true});
    
    // Initialize the arrays which control how many buttons will be on
    // each dialog, what text will be on them and which function will be
    // called when the user clicks on them. These arrays are then fed to
    // the "dialog()" jQuery function.
    // The variables referenced inside the brackets are created on the server.
    // They hold the localized text for the buttons.
    _ButtonsRoomEdit[help  ] = ButtonEditHelp;
    _ButtonsRoomEdit[del   ] = ButtonEditDelete;
    _ButtonsRoomEdit[cancel] = ButtonEditCancel;
    _ButtonsRoomEdit[save  ] = ButtonEditSave;
    
    _ButtonsRoomNew[help  ] = ButtonNewHelp;
    _ButtonsRoomNew[cancel] = ButtonNewCancel;
    _ButtonsRoomNew[save  ] = ButtonNewSave;
}


/* ----------------------------------------------------------------------
 * 
 * Create a new room.
 *
 * ---------------------------------------------------------------------- */
function RoomNew() {
    jQuery.ajax({
        url: "RoomEmpty",
        cache: false,
        
        /* Query succeeded. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            jQuery('#roomnew').html(data);
            jQuery('#roomnew').dialog("option", "title", roomtitlenew);
            jQuery('#roomnew').dialog("option", "buttons", _ButtonsRoomNew);
            jQuery('#roomnew').dialog('open');
        },
        
        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Edit an already defined room.
 *
 * ---------------------------------------------------------------------- */
function RoomEdit(roomNo) {
    _roomNoForEdit = roomNo;
    var params = "room_no=" + roomNo;
    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call
    jQuery.ajax({
        url: "RoomEdit",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data){
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            jQuery('#roomedit').html(data);
            jQuery('#roomedit').dialog("option", "title", roomtitleedit);
            jQuery('#roomedit').dialog("option", "buttons", _ButtonsRoomEdit);
            jQuery('#roomedit').dialog('open');
        },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Open help for edit dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditHelp() {
    Help(140);
}

/* ----------------------------------------------------------------------
 * 
 * Delete the room.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditDelete() {
    var params = "room_no=" + _roomNoForEdit;
    
    // Make the ajax call.
    jQuery.ajax({
        url: "RoomDelete",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
            {
            if (data == 'ok')
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
 * Close the edit dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditCancel() {
    jQuery("#roomedit").dialog('close');
}

/* ----------------------------------------------------------------------
 * 
 * Update info about the room.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditSave() {
    // Collect the information in the dialog.
    var rno     = document.getElementById('roomno').value;
    var rsize   = document.getElementById('roomsize').value;
    var rfloor  = document.getElementById('roomfloor').value;
    var rtype   = document.getElementById('roomtype').value;
    
    // Compose the parameter string.
    var params = 
          "room_no_old="  + _roomNoForEdit
        + "&room_no_new=" + rno 
        + "&room_size="   + rsize
        + "&room_floor="  + rfloor
        + "&room_type="   + rtype;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    jQuery.ajax({
        url: "RoomUpdate",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
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
 * Open help for new dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewHelp() {
    Help(130);
}

/* ----------------------------------------------------------------------
 * 
 * Close the new dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewCancel() {
    jQuery("#roomnew").dialog('close');
}

/* ----------------------------------------------------------------------
 * 
 * Save the new room.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewSave() {
    // Collect the information in the dialog.
    var rno     = document.getElementById('roomno').value;
    var rsize   = document.getElementById('roomsize').value;
    var rfloor  = document.getElementById('roomfloor').value;
    var rtype   = document.getElementById('roomtype').value;
    
    // Compose the parameter string.
    var params = 
          "room_no="     + rno
        + "&room_size="  + rsize
        + "&room_floor=" + rfloor
        + "&room_type="  + rtype;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    jQuery.ajax({
        url: "RoomSave",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
            {
            if (data == 'ok')
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
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert(errTxt + ":    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}