/* ********************************************************************** 
 * Script for the ResList page. 
 * Responsibilities:
 *    1. Handle date-pickers from and to
 *    2. Dynamic repsponse: when a date is changed fetch reservations 
 *       intersecting the new period
 *    3. Dynamic reponse: when a date is changed so that the period is no longer
 *       valid (start > end), shift the opposite date to correct it.
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      nh.js
 * 
 * CSS IDs of elements used by the script:
 *      idresstart      - Datepicker for the start date
 *      idresend        - Datepicker for the end date
 *      pantable        - Div enclosing the table of reservations
 *      
 * Variables created on the server side:
 *      user_lang       - The preferred language of the user, for the datepickers
 *      period_start    - Value for the from date, has the type 'Date'
 *      period_end      - Value for the to date, has the type 'Date'
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
$(document).ready(InitResList);

/* ----------------------------------------------------------------------
 * 
 * Page has loaded, run initializers.
 *
 * ---------------------------------------------------------------------- */
function InitResList() {
    UPInit(); /* From userpreferences.js */
    
    // Datepickers: set language and starting dates.
    $('#idresstart').datepicker($.datepicker.regional[user_lang]);
    $('#idresstart').datepicker("option", "dateFormat", "yy-mm-dd");
    $('#idresstart').datepicker('setDate', period_start);

    $('#idresend').datepicker($.datepicker.regional[user_lang]);
    $('#idresend').datepicker("option", "dateFormat", "yy-mm-dd");
    $('#idresend').datepicker('setDate', period_end); 
    
    // Handlers on the input fields for the datepickers, when the dates 
    // change we can catch it.
    $('#idresstart').bind('change', DateChangedStart);
    $('#idresend').bind('change', DateChangedEnd);
}

/* ----------------------------------------------------------------------
 * 
 * The start date has changed.
 *
 * ---------------------------------------------------------------------- */
function DateChangedStart() {
    var dateResStart = $("#idresstart").datepicker("getDate");
    var dateResEnd   = $("#idresend").datepicker("getDate");
    
    if (dateResStart > dateResEnd) {
        $('#idresend').datepicker('setDate', dateResStart);
    }
    PeriodChanged();
}

/* ----------------------------------------------------------------------
 * 
 * The end date has changed.
 *
 * ---------------------------------------------------------------------- */
function DateChangedEnd() {
    var dateResStart = $("#idresstart").datepicker("getDate");
    var dateResEnd   = $("#idresend").datepicker("getDate");
    
    if (dateResStart > dateResEnd) {
        $('#idresstart').datepicker('setDate', dateResEnd);
    }
    PeriodChanged();
}

/* ----------------------------------------------------------------------
 * 
 * The period has changed, fetch reservations which intersect it.
 *
 * ---------------------------------------------------------------------- */
function PeriodChanged() {
    var dateResStart = $("#idresstart").datepicker("getDate");
    var dateResEnd   = $("#idresend").datepicker("getDate");
    
    // Convert the dates into strings.
    var strStart = nhDateToString(dateResStart);
    var strEnd   = nhDateToString(dateResEnd);
    var params =
        "resdate_start="  + strStart +
        "&resdate_end=" + strEnd;

    // Make the ajax call.
    jQuery.ajax({
        url: "ResPeriod",
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
            $(".pantable").html(data);
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