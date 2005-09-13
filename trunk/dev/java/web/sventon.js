// Sventon javascript include file.


// function to toggle the entry checkboxes
function toggleEntryFields(formName) {
	for ( var i = 0 ; i < formName.length ; i++ ) {
		fieldObj = formName.elements[i];
		if ( fieldObj.type == 'checkbox' ) {
			fieldObj.checked = ( fieldObj.checked ) ? false : true ;
		}
	}
}

// function to handle action submissions in repo browser view
function doAction(formName) {

  // Check if any entry is checked
  var checkedEntry = false;
  for (i = 0; i < formName.entry.length; i++) {
    if (formName.entry[i].checked == true) {
      checkedEntry = true;
      break;
    }
  }

  // If no value is selected or no entries selected, no action is taken.
  if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == '' || !checkedEntry) return false;

  if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == 'thumb') {
    alert('Thumbnail view not yet supported');
    //TODO:Change action to url for the showFileController and return true
    //formName.action = 'zip'
    return false;
  } else if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == 'zip' ) {
    //TODO:Change action to url for the zipController and return true
    //formName.action = 'zip'
    alert('zip not yet supported');
    return false;
  }
  return false;
}

// function to handle search submission
function doSearch(formName) {
  // If no search string is entered, no action is taken.
  if (formName.sventonSearchString.value == '') {
    return false;
  } else {
    return true;
  }
}

// function to hide/show div layers
function toggleDivVisibility(theId) {
  var object = document.getElementById(theId);
  if (object.style.visibility == 'visible') {
    object.style.visibility = 'hidden';
  } else {
    object.style.visibility = 'visible';
  }
  return;
}

// function to hide/show extended revision log information
function toggleElementVisibility(theId) {
  var object = document.getElementById(theId);
  if (object.style.display == '') {
    object.style.display = 'none';
  } else {
    object.style.display = '';
  }
  return;
}

// function to handle diff submissions
function doDiff(formName) {

  // Check if any entry is checked
  var checkedEntry = false;
  for (i = 0; i < formName.rev.length; i++) {
    if (formName.rev[i].type == 'checkbox' && formName.rev[i].checked == true) {
      checkedEntry = true;
      break;
    }
  }

  // If no entries selected, no action is taken.
  if (!checkedEntry) {
    return false;
  }

  //TODO - validate and make sure two boxes are checked.
  return true;
}

// function to verify that not more than two checkboxes are checked.
function verifyCheckBox(checkbox) {
  var count = 0;
  var first = null;
  var form = checkbox.form;
  for (i = 0 ; i < form.rev.length ; i++) {
    if (form.rev[i].type == 'checkbox' && form.rev[i].checked) {
      if (first == null && form.rev[i] != checkbox) {
        first = form.rev[i];
      }
     count += 1;
    }
  }
  
  if (count > 2) {
    first.checked = false;
    count -= 1;
  }
}
