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
    formName.action = 'showthumbs.svn'
    return true;
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
  } else if (formName.sventonSearchString.value.length < 3) {
    return searchWarning();
  } else {
    return true;
  }
}

// function to handle flatten submissions
function doFlatten(url) {
  var flattenURL = 'flatten.svn?path='
  var result = true;
  if (url == '/') {
    result = flatteningWarning();
  }
  if (result) {
    location.href = flattenURL + url;
  } else {
    return false;
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

// function to change link text between 'more' and 'less' on log entry rows
function changeLessMoreDisplay(theId) {
  var object = document.getElementById(theId);
  if (object.innerHTML == 'more') {
    object.innerHTML = 'less';
  } else {
    object.innerHTML = 'more';
  }
  return
}

// function to change link text between 'show' and 'hide'
function changeHideShowDisplay(theId) {
  var object = document.getElementById(theId);
  if (object.innerHTML == 'show') {
    object.innerHTML = 'hide';
  } else {
    object.innerHTML = 'show';
  }
  return
}


// function to handle diff submissions
function doDiff(formName) {

  // Check if any entry is checked
  var checkedEntry = 0;
  for (i = 0; i < formName.rev.length; i++) {
    if (formName.rev[i].type == 'checkbox' && formName.rev[i].checked == true) {
      checkedEntry++;
    }
  }

  // Two boxes must be checked else no action is taken.
  if (checkedEntry != '2') {
    return false;
  } else {
    return true
  }
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

// function to display warning in case user tries to flatten on
// root directory level.
function flatteningWarning() {
  return confirm("Flattening on root level is not recommended.\nThe result will potentially be very large.\nDo you want to continue anyway?");
}

// function to display warning in case search string is too short.
function searchWarning() {
  return confirm("Given search string is short. The result will potentially be very large.\nDo you want to continue anyway?");
}
