/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */

var isAjaxRequestSent = false;

// function to toggle the entry checkboxes
function toggleEntryFields(formName) {
  for (var i = 0; i < formName.length; i++) {
    fieldObj = formName.elements[i];
    if (fieldObj.type == 'checkbox') {
      fieldObj.checked = !fieldObj.checked;
    }
  }
}

// function to handle action submissions in repo browser view
function doAction(formName) {

  // If no option value is selected, no action is taken.
  if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == '') {
    return false;
  }

  // Check which action to execute
  if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == 'thumb') {
    // One or more entries must be checked
    if (getCheckedCount(formName) > 1) {
      formName.action = 'showthumbs.svn'
      return true;
    }
  } else if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == 'diff') {
    // Exactly two entries must be checked
    if (getCheckedCount(formName) == 2)
    {
      formName.action = 'diff.svn'
      return true;
    } else {
      alert('Two entries must be selected');
    }
  } else if (formName.actionSelect.options[formName.actionSelect.selectedIndex].value == 'export') {
    // One or more entries must be checked
    if (getCheckedCount(formName) > 1) {
      formName.action = 'export.svn'
      return true;
    }
  }
  return false;
}

// sets the value of the revision input text field to 'HEAD'
function setHeadRevision() {
  document.getElementById('revisionInput').value = 'HEAD'
}

// function to handle search submission
function doSearch(formName) {
  // If no search string is entered, no action is taken.
  if (formName.searchString.value == '') {
    return false;
  } else {

    if (getCheckedValue(formName.elements['searchMode']) == 'entries') {
      formName.action = 'searchentries.svn';
    } else {
      formName.action = 'searchlogs.svn';
    }
    if (formName.searchString.value.length < 3) {
      return searchWarning();
    } else {
      return true;
    }
  }
}

// function to handle flatten submissions
function doFlatten(url, instanceName) {
  var flattenURL = 'flatten.svn?name=' + instanceName + '&path='
  var result = true;
  if (url == '/') {
    result = flatteningWarning();
  }
  if (result) {
    location.href = flattenURL + url;
  } else {
    return false;
  }
  return true;
}

// function to hide/show div layers
function toggleDivVisibility(theId) {
  var object = document.getElementById(theId);
  if (object.style.visibility == 'visible') {
    object.style.visibility = 'hidden';
  } else {
    object.style.visibility = 'visible';
  }
}

// function to hide/show extended revision log information
function toggleElementVisibility(theId) {
  var object = document.getElementById(theId);
  if (object.style.display == '') {
    object.style.display = 'none';
  } else {
    object.style.display = '';
  }
}

// function to change link text between 'more' and 'less' on log entry rows
function changeLessMoreDisplay(theId) {
  var object = document.getElementById(theId);
  if (object.innerHTML == 'more') {
    object.innerHTML = 'less';
  } else {
    object.innerHTML = 'more';
  }
}

// function to change link text between 'show' and 'hide'
function changeHideShowDisplay(theId) {
  var object = document.getElementById(theId);
  if (object.innerHTML == 'show') {
    object.innerHTML = 'hide';
  } else {
    object.innerHTML = 'show';
  }
}

// function to handle diff submissions
function doDiff(formName) {

  // Check if any entry is checked
  var checkedEntry = 0;
  for (i = 0; i < formName.entry.length; i++) {
    if (formName.entry[i].type == 'checkbox' && formName.entry[i].checked) {
      checkedEntry++;
    }
  }

  // Two boxes must be checked else no action is taken.
  return checkedEntry == 2;
}

// function to verify that not more than two checkboxes are checked.
function verifyCheckBox(checkbox) {
  var count = 0;
  var first = null;
  var form = checkbox.form;
  for (i = 0; i < form.entry.length; i++) {
    if (form.entry[i].type == 'checkbox' && form.entry[i].checked) {
      if (first == null && form.entry[i] != checkbox) {
        first = form.entry[i];
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

// returns the value of the radio button that is checked
// returns an empty string if none are checked, or there are no radio buttons
function getCheckedValue(radioObj) {
  if (!radioObj) {
    return "";
  }
  var radioLength = radioObj.length;
  if (radioLength == undefined) {
    if (radioObj.checked) {
      return radioObj.value;
    } else {
      return "";
    }
  }
  for (var i = 0; i < radioLength; i++) {
    if (radioObj[i].checked) {
      return radioObj[i].value;
    }
  }
  return "";
}

// returns number of checked entries.
function getCheckedCount(formName) {
  var undefined;
  var checkedEntriesCount = 0;

  // Check if only one entry exists - and whether it's checked
  if (formName.entry.length == undefined) {
    checkedEntriesCount = formName.entry.checked ? 1 : 0;
  } else {
    // More than one entry exists - Check how many are checked
    for (i = 0; i < formName.entry.length; i++) {
      if (formName.entry[i].checked) {
        checkedEntriesCount++;
      }
    }
  }
  return checkedEntriesCount;
}

// Toggles line wrap mode between normal and nowrap
function toggleWrap() {
  var classNames = new Array(['src'], ['srcChg'], ['srcAdd'], ['srcDel']);
  var tags = document.getElementById('diffTable').getElementsByTagName('td');

  for (var i = 0; i < tags.length; i++) {
    if (classNames.contains(tags[i].className)) {
      if (tags[i].style.whiteSpace == '') {
        tags[i].style.whiteSpace = 'nowrap';
      } else {
        tags[i].style.whiteSpace = '';
      }
    }
  }
}

// Requests directory contents in given path
// Uses the global variable 'isAjaxRequestSent'
function listFiles(rowNumber, path, name) {
  if (isAjaxRequestSent) {
    return false;
  }

  var iconElement = document.getElementById('dirIcon' + rowNumber);

  if (iconElement.className == 'minus') {
    // Files are already listed - hide them instead
    var elements = document.getElementsByClassName('expandedDir' + rowNumber);
    for (var i = 0; i < elements.length; i++) {
      Element.remove(elements[i])
    }
    iconElement.src = 'images/icon_dir_plus.gif';
    iconElement.className = 'plus';
  } else {
    // Do the ajax call
    var url = 'listfiles.ajax';
    var urlParams = 'path=' + path + '&revision=head&name=' + name + "&rowNumber=" + rowNumber;
    var elementId = 'dir' + rowNumber;
    var ajax = new Ajax.Updater({success: elementId}, url,
    {method: 'get', parameters: urlParams, onSuccess: ajaxSuccess, onFailure: reportAjaxError, insertion:Insertion.After});
    iconElement.src = 'images/icon_dir_minus.gif';
    iconElement.className = 'minus';
    Element.show(document.getElementById('spinner'));
    isAjaxRequestSent = true;
  }
  return false;
}

// General ajax error alert method
function ajaxSuccess(request) {
  isAjaxRequestSent = false;
  Element.hide(document.getElementById('spinner'));
}

// General ajax error alert method
function reportAjaxError(request) {
  isAjaxRequestSent = false;
  Element.hide(document.getElementById('spinner'));
  alert('An error occured during asynchronous request.');
}

Array.prototype.contains = function (element) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == element || element.constructor == String && this[i] == String(element)) {
      return true;
    }
  }
  return false
};