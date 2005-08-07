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
  // If no value is selected, no action is taken.
  if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == '') return false;

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

// function to hide/show extended revision log information
function toggleLogInfo(theId) {
  var object = document.getElementById(theId);
  if (object.style.display == '') {
    object.style.display = 'none';
  } else {
    object.style.display = '';
  }
  return;
}
