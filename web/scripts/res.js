/* ********************************************************************** 
 * Script for the Reservation page. 
 * Responsibilities:
 *    1. Handle date-pickers from and to
 *    2. Info fields for number of nights, guests and rooms
 *    3. User clicks the save button
 *    4. Dynamic responses: when a new date is selected, make an ajax
 *       query about the available rooms using the new period and update
 *       the info about number of nights
 *    4a Dynamic responses: when a room is selected update the info about
 *       the number of guests and rooms
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      nh.js
 * 
 * CSS IDs of elements used by the script:
 *      idresarrive     - Datepicker for the from date
 *      idresdepart     - Datepicker for the to date
 *      idresnguests    - For number of guests
 *      idresnnights    - For number of nights
 *      idresnrooms     - For number of rooms
 *      idrescust       - Dropdown for customer id
 *      panrestab       - Div element which holds the table of 
 *                        available rooms (N.B. this is a class, not an ID)
 *      
 * Variables created on the server side:
 *      user_lang       - The preferred language of the user, for the datepickers
 *      period_start    - Value for the from date, has the type 'Date'
 *      period_end      - Value for the to date, has the type 'Date'
 *      err_input       - Localized text
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
$(document).ready(InitRes);

var _selectedRooms = new Array();

/* ----------------------------------------------------------------------
 * 
 * Page has loaded, run initializers.
 *
 * ---------------------------------------------------------------------- */
function InitRes() {
    UPInit(); /* From userpreferences.js */
    
    // Datepickers: set language and starting dates.
    $('#idresarrive').datepicker($.datepicker.regional[user_lang]);
    $('#idresarrive').datepicker("option", "dateFormat", "yy-mm-dd");
    $('#idresarrive').datepicker('setDate', period_start);

    $('#idresdepart').datepicker($.datepicker.regional[user_lang]);
    $('#idresdepart').datepicker("option", "dateFormat", "yy-mm-dd");
    $('#idresdepart').datepicker('setDate', period_end); 
    
    // Handlers on the input fields for the datepickers, when the dates 
    // change we can catch it.
    $('#idresarrive').bind('change', DateChangedArrive);
    $('#idresdepart').bind('change', DateChangedDepart);
}


/* ----------------------------------------------------------------------
 * 
 * User clicked the 'Save' button.
 *
 * ---------------------------------------------------------------------- */
function ResSave() {
    var dateArrive = $("#idresarrive").datepicker("getDate");
    var dateDepart = $("#idresdepart").datepicker("getDate");
    
    // Make sure some rooms have been selected.
    if (_selectedRooms.length === 0) {
        alert(err_input);
        return;
    }
    var csvRooms = getListOfRooms();
    
    // A customer must also be selected.
    var selObj  = document.getElementById("idrescust");
    var index   = selObj.selectedIndex;
    var selCust = selObj.options[index].value;
    if (selCust === null) {
        alert(err_input);
        return;
    }
    
    // Create the parameters.
    var numGuests = parseInt(document.getElementById("idresnguests").innerHTML);
    var strArrive = nhDateToString(dateArrive);
    var strDepart = nhDateToString(dateDepart);
    var params = 
          "resdate_arrive="  + strArrive
        + "&resdate_depart=" + strDepart
        + "&resrooms="       + csvRooms
        + "&rescustomer="    + selCust
        + "&resnum_guests="  + numGuests;

    // Make the ajax call.
    jQuery.ajax({
        url: "ResCreate",
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
            // Something went wrong, display error message from server
            else if (data !== 'ok') {
                alert(data);
            }
            // The reservation was created, retrieve its id and redirect.
            else {
                getIDandRedirect();
            }
        },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * The reservation was successfully created, get the id and redirect to
 * the edit page for reservations.
 *
 * ---------------------------------------------------------------------- */
function getIDandRedirect() {
    jQuery.ajax({
        url: "ResGetMaxID",
        cache: false,

        /* Callback for success. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            // Something went wrong.
            else if (data === 'error') {
                alert("Could not retrieve reservation ID.");
            }
            // We have the id.
            else {
                window.location = "ResEdit?res_id=" + data;
            }
        },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Create a string of (C)omma(S)eperated(V)alues from the list of selected rooms.
 *
 * ---------------------------------------------------------------------- */
function getListOfRooms() {
    var csvList = null;
    for (var i = 0; i < _selectedRooms.length; i++) {
        if (csvList === null) {
            csvList = _selectedRooms[i];
        }
        else {
            csvList = csvList + ',' + _selectedRooms[i];
        }
    }
    return csvList;
}



/* ----------------------------------------------------------------------
 * 
 * The date of the arrival has changed. If the departure is before, or on, 
 * the same date, we adjust the departure date, taking into account the 
 * presently selected number of nights.
 *
 * ---------------------------------------------------------------------- */
function DateChangedArrive() {
    var dateResStart = $("#idresarrive").datepicker("getDate");
    var dateResEnd   = $("#idresdepart").datepicker("getDate");
    var numNights    = parseInt(document.getElementById('idresnnights').innerHTML);
    
    if (dateResEnd <= dateResStart) {
        dateResEnd.setTime(dateResStart.getTime() + (numNights * 86400000));
        $('#idresdepart').datepicker('setDate', dateResEnd);
    }
    else {
        numNights = nhNumOfDays(dateResStart, dateResEnd);
        document.getElementById("idresnnights").innerHTML = numNights;
    }
    DatesChanged();
}


/* ----------------------------------------------------------------------
 * 
 * The date of the departure has changed. If the arrival is after, or on, 
 * the same date, we adjust the arrival date, taking into account the presently 
 * selected number of nights.
 *
 * ---------------------------------------------------------------------- */
function DateChangedDepart() {
    var dateResStart = $("#idresarrive").datepicker("getDate");
    var dateResEnd   = $("#idresdepart").datepicker("getDate");
    var numNights    = parseInt(document.getElementById('idresnnights').innerHTML);
    
    if (dateResEnd <= dateResStart) {
        dateResStart.setTime(dateResEnd.getTime() - (numNights * 86400000));
        $('#idresarrive').datepicker('setDate', dateResStart);
    }
    else {
        numNights = nhNumOfDays(dateResStart, dateResEnd);
        document.getElementById("idresnnights").innerHTML = numNights;
    }
    DatesChanged();
}


/* ----------------------------------------------------------------------
 * 
 * One of the dates has changed. We make an ajax call to update the
 * list of available rooms and then reset the counters.
 *
 * ---------------------------------------------------------------------- */
function DatesChanged() {
    var dateResStart = $("#idresarrive").datepicker("getDate");
    var dateResEnd   = $("#idresdepart").datepicker("getDate");
    
    // Convert the dates into strings.
    var strArrive = nhDateToString(dateResStart);
    var strDepart = nhDateToString(dateResEnd);
    var params =
        "resdate_arrive="  + strArrive +
        "&resdate_depart=" + strDepart;

    // Make the ajax call.
    jQuery.ajax({
        url: "ResAvailRooms",
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
            $(".panrestab").html(data);
            ResetRoomlistAndTotals();
        },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Set the counters for guests and rooms to zero and clear the list of rooms.
 *
 * ---------------------------------------------------------------------- */
function ResetRoomlistAndTotals() {
    _selectedRooms = new Array();
    document.getElementById('idresnrooms').innerHTML = 0;
    document.getElementById('idresnguests').innerHTML = 0;
}



/* ----------------------------------------------------------------------
 * 
 * The user has clicked on a room. He is either selecting or de-selecting,
 * the css class 'row_selected' determines which. The room number must also
 * be added or deleted from the collection of selected rooms.
 *
 * ---------------------------------------------------------------------- */
function RoomClicked(clickedRow, roomNum, roomSize) {
    if ($(clickedRow).hasClass('row_selected')) {
        RemoveRoom(roomNum, roomSize);
        $(clickedRow).removeClass('row_selected');
    }
    else {
        AddRoom(roomNum, roomSize);
        $(clickedRow).addClass('row_selected');
    }
}


/* ----------------------------------------------------------------------
 * 
 * Add a room to the collection of selected rooms.
 *
 * ---------------------------------------------------------------------- */
function AddRoom(roomNum, roomSize) {
    var targetIndex = _selectedRooms.length;
    _selectedRooms[targetIndex] = roomNum;
    
    // Increment the room counter.
    var roomCount = parseInt(document.getElementById('idresnrooms').innerHTML);
    roomCount++;
    document.getElementById('idresnrooms').innerHTML = roomCount;
    
    // Calculate the total number of guests for all selected rooms.
    var currentTotal = parseInt(document.getElementById('idresnguests').innerHTML);
    currentTotal = currentTotal + roomSize;
    document.getElementById('idresnguests').innerHTML = currentTotal;
}


/* ----------------------------------------------------------------------
 * 
 * Remove a room from the collection of selected rooms.
 *
 * ---------------------------------------------------------------------- */
function RemoveRoom(roomNum, roomSize) {
    var targetIndex = null;
    for (var i=0; i < _selectedRooms.length; i++) {
        var rn = _selectedRooms[i];
        if (rn === roomNum) {
            targetIndex = i;
            break;
        }
    }
    if (targetIndex !== null) {
        _selectedRooms.splice(targetIndex, 1);
        
        // Decrement the room counter.
        var roomCount = parseInt(document.getElementById('idresnrooms').innerHTML);
        roomCount--;
        document.getElementById('idresnrooms').innerHTML = roomCount;
        
        // Calculate the total number of guests for all selected rooms.
        var currentTotal = parseInt(document.getElementById('idresnguests').innerHTML);
        currentTotal = currentTotal - roomSize;
        if (currentTotal < 0) { currentTotal = 0; }
        document.getElementById('idresnguests').innerHTML = currentTotal;
    }
}


/* ----------------------------------------------------------------------
 * 
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert(errTxt + ":    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}