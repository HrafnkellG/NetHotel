

function FocusID() {
    var nameBox = document.getElementById("nameBox");
    nameBox.focus();
}


function KeypressOnName(event) {
    if (event.keyCode === 13) {
        var passwBox = document.getElementById("passwBox");
        passwBox.focus();
        return false;
    }
    return true;
}


function KeypressOnPassword(event) {
    if (event.keyCode === 13) {
        document.forms["formLogin"].submit();
        return false;
    }
    return true;
}

//var errNoContact = "No contact.\nEngin tenging.";
//
//var enNew = "<p style=\"font-size: x-large; color: white;\">Just a moment, creating demo data...</p>";
//var isNew = "<p style=\"font-size: x-large; color: white;\">Augnablik, bý til sýndargögn...</p>";
//
//function InitiateDemoCreation() {
//    try 
//    {
//    jQuery("#enTxt").replaceWith(enNew);
//    jQuery("#isTxt").replaceWith(isNew);
//    
//    // Make the call.
//    jQuery.ajax({
//        url: "CreateDemoData",
//        cache: false,
//
//        /* Callback: the call was ok so we display the dialog. */
//        success: function(data)
//            {
//            // The jQuery code returns success even though it could not
//            // contact the server, so we check for an empty dataset.
//            if (data.length === 0) {
//                alert(errNoContact);
//                return;
//            }
//            var scrubbedInput = data;
//            scrubbedInput = scrubbedInput.replace("\r", "");
//            scrubbedInput = scrubbedInput.replace("\n", "");
//            if (scrubbedInput == 'OK') {
//                document.forms["formLogin"].submit();
//            }
//            else {
//                alert('Error: ' + data);
//            }
//            },
//        
//        /* Default error handling. */
//        error: CallbackError
//    });
//    }
//    catch (e) {
//        alert("error = " + e.message);
//    }
//}
//
//function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
//    alert("ERROR:    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
//}