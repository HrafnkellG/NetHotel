/* ********************************************************************** 
 * Script for the ResList page. 
 * Responsibilities:
 *    1. When a date is changed fetch and display those invoices which
 *       intersect the new period.
 *    3. When a date is changed so that the period is no longer
 *       valid (start > end), shift the opposite date to correct it.
 * 
 * Scripts referenced: 
 *      userpreferences.js
 * 
 * CSS IDs of elements used by the script:
 *      cbOnlyOpen - The checkbox.
 *      
 * Variables created on the server side:
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
jQuery(document).ready(InitIL);

/* ----------------------------------------------------------------------
 * 
 * Page has loaded, run initializers.
 *
 * ---------------------------------------------------------------------- */
function InitIL() {
    UPInit(); /* From userpreferences.js */
    
    // Datepickers: set language and starting dates.
    $('#dpfrom').datepicker($.datepicker.regional[user_lang]);
    $('#dpfrom').datepicker("option", "dateFormat", "yy-mm-dd");
    $('#dpfrom').datepicker('setDate', period_start);

    $('#dpto').datepicker($.datepicker.regional[user_lang]);
    $('#dpto').datepicker("option", "dateFormat", "yy-mm-dd");
    $('#dpto').datepicker('setDate', period_end);
    
    // Handlers on the input fields for the datepickers, when the dates 
    // change we can catch it.
    $('#dpfrom').bind('change', DateChangedStart);
    $('#dpto').bind('change', DateChangedEnd);
}

/* ----------------------------------------------------------------------
 * 
 * The start date has changed.
 *
 * ---------------------------------------------------------------------- */
function DateChangedStart() {
    var perStart = $("#dpfrom").datepicker("getDate");
    var perEnd   = $("#dpto").datepicker("getDate");
    
    if (perStart > perEnd) {
        $('#dpto').datepicker('setDate', perStart);
    }
    GetInvoicesForPeriod();
}

/* ----------------------------------------------------------------------
 * 
 * The end date has changed.
 *
 * ---------------------------------------------------------------------- */
function DateChangedEnd() {
    var perStart = $("#dpfrom").datepicker("getDate");
    var perEnd   = $("#dpto").datepicker("getDate");
    
    if (perStart > perEnd) {
        $('#dpfrom').datepicker('setDate', perEnd);
    }
    GetInvoicesForPeriod();
}


/* ----------------------------------------------------------------------
 * 
 * Get the period and fetch those invoices which intersect it.
 *
 * ---------------------------------------------------------------------- */
function GetInvoicesForPeriod() {
    var perStart = $("#dpfrom").datepicker("getDate");
    var perEnd   = $("#dpto").datepicker("getDate");
    
    // Convert the dates into strings.
    var strStart = nhDateToString(perStart);
    var strEnd   = nhDateToString(perEnd);
    var params =
        "period_start=" + strStart +
        "&period_end="  + strEnd;

    // Make the ajax call.
    jQuery.ajax({
        url: "InvForPeriod",
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