/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */

var isAjaxRequestSent = false;


// function to handle action submissions in repo browser view
function doAction(formName) {
  var input = $(formName)['actionSelect'];
  var selectedValue = $F(input);

  // If no option value is selected, no action is taken.
  if (selectedValue == '') {
    return false;
  }

  // Check which action to execute
  if (selectedValue == 'thumb') {
    // One or more entries must be checked
    if (getCheckedCount(formName) > 0) {
      formName.action = 'showthumbs.svn'
      return true;
    }
  } else if (selectedValue == 'diff') {
    // Exactly two entries must be checked
    if (getCheckedCount(formName) == 2) {
      formName.action = 'diff.svn'
      return true;
    } else {
      alert('Two entries must be selected');
    }
  } else if (selectedValue == 'export') {
    // One or more entries must be checked
    if (getCheckedCount(formName) > 0) {
      formName.action = 'export.svn'
      return true;
    }
  }
  return false;
}

// sets the value of the revision input text field to 'HEAD'
function setHeadRevision() {
  $('revisionInput').value = 'HEAD'
}

// function to handle search submission
function doSearch(formName) {

  var input = $(formName)['searchString'];
  var searchStr = $F(input);

  // If no search string is entered, no action is taken.
  if (searchStr == '') {
    return false;
  } else {

    if (getCheckedValue(formName.elements['searchMode']) == 'entries') {
      formName.action = 'searchentries.svn';
    } else {
      formName.action = 'searchlogs.svn';
    }
    if (searchStr.length < 3) {
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

// function to validate url during instance configuration submisson
function validateUrl(formName) {
  var input = $(formName)['repositoryUrl'];
  var url = $F(input).toLocaleLowerCase();
  if (url.indexOf('trunk') > -1
      || url.indexOf('tags') > -1
      || url.indexOf('branches') > -1) {
    return confirm('The URL entered must be the root of the repository!\n' +
                   'Is [' + $F(input) + '] really the subversion root?\n');
  }
  return true;
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
  for (var i = 0; i < form.entry.length; i++) {
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

// Toggles line wrap mode between normal and nowrap
function toggleWrap() {
  var classNames = new Array(['src'], ['srcChg'], ['srcAdd'], ['srcDel']);
  var tags = $('diffTable').getElementsByTagName('td');

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

  var iconElement = $('dirIcon' + rowNumber);

  if (iconElement.className == 'minus') {
    // Files are already listed - hide them instead
    var elements = document.getElementsByClassName('expandedDir' + rowNumber);
    for (var i = 0; i < elements.length; i++) {
      Element.remove(elements[i])
    }
    iconElement.src = 'images/icon_folder_go.png';
    iconElement.className = 'plus';
  } else {
    // Do the ajax call
    var url = 'listfiles.ajax';
    var urlParams = 'path=' + path + '&revision=head&name=' + name + "&rowNumber=" + rowNumber;
    var elementId = 'dir' + rowNumber;
    var ajax = new Ajax.Updater({success: elementId}, url,
    {method: 'post', parameters: urlParams, onSuccess: ajaxSuccess, onFailure: reportAjaxError, insertion:Insertion.After});
    iconElement.src = 'images/icon_folder.png';
    iconElement.className = 'minus';
    Element.show('spinner');
    isAjaxRequestSent = true;
  }
  return false;
}

function hideLatestRevisions() {
  if (isLatestRevisionsVisible()) {
    var infoDiv = $('latestCommitInfoDiv');
    // Hide details
    Element.hide(infoDiv);
    Element.update(infoDiv, '');
    toggleInnerHTML('latestCommitLink', 'show', 'hide');
  }
}

function isLatestRevisionsVisible() {
  return $('latestCommitInfoDiv').style.display == '';
}

// Requests the N latest revisions
// Uses the global variable 'isAjaxRequestSent'
function getLatestRevisions(name, count) {
  if (isAjaxRequestSent) {
    return false;
  }

  // Do the ajax call
  var url = 'latestrevisions.ajax';
  var urlParams = 'path=/&revision=head&name=' + name + "&revcount=" + count;

  var ajax = new Ajax.Updater({success: $('latestCommitInfoDiv')}, url,
  {method: 'post', parameters: urlParams, onSuccess: showLatestRevisionsDiv, onFailure: reportAjaxError});
  Element.show('spinner');
  isAjaxRequestSent = true;
  return false;
}

// Function called when ajax request in 'getLatestRevisions' is finished.
function showLatestRevisionsDiv(request) {
  Element.show('latestCommitInfoDiv');
  Element.update('latestCommitLink', 'hide');
  ajaxSuccess(request)
}

// General ajax success method
function ajaxSuccess(request) {
  isAjaxRequestSent = false;
  Element.hide('spinner');
}

// General ajax error alert method
function reportAjaxError(request) {
  isAjaxRequestSent = false;
  Element.hide('spinner');
  alert('An error occured during asynchronous request.');
}

function showHideHelp(helpDiv, id) {
  if (isAjaxRequestSent) {
    return;
  }

  if (helpDiv.style.display == '') {
    Element.hide(helpDiv);
  } else {
    // Do the ajax call
    var url = 'static.ajax';
    var urlParams = 'id=' + id;

    var ajax = new Ajax.Updater({success: $(helpDiv)}, url,
    {method: 'post', parameters: urlParams, onSuccess: ajaxSuccess, onFailure: reportAjaxError});
    Element.show('spinner');
    Element.show(helpDiv);
    isAjaxRequestSent = true;
  }
}

// Gets the log message for given revision.
function getLogMessage(revision, instanceName, date) {
  var url = 'getmessage.ajax';
  var urlParams = 'revision=' + revision + '&name=' + instanceName;
  var divName = 'msg' + revision + 'Div';
  
  var ajax = new Ajax.Request(url, {
    method: 'get',
    parameters: urlParams,
    onSuccess: function(transport) {
      Element.update(divName, transport.responseText);
    },
    onFailure: reportAjaxError
  });
  return Tip('<div id=\"' + divName + '\" style=\"width: 350px;\"><img src=\"images/spinner.gif\" alt=\"spinner\"/></div>', TITLE, 'Log message: ' + date, BORDERCOLOR, '#3e647e', BGCOLOR, '#ffffff', ABOVE, true);
}

function highlightBlameRev(revision) {
  var tags = $('blameTable').getElementsByTagName('td');
  setBackgroundColor(tags, 'blameRev_' + revision, '#dddddd');
}

function restoreBlameRev(revision) {
  var tags = $('blameTable').getElementsByTagName('td');
  setBackgroundColor(tags, 'blameRev_' + revision, '#ffffff');
}

function addEntryToTray(element, dropon, event) {
  var ajax = new Ajax.Updater({success: $('entryTray')}, element.id,
  {method: 'post', onSuccess: ajaxSuccess, onFailure: reportAjaxError});
  Element.show('spinner');
  isAjaxRequestSent = true;
}

function removeEntryFromTray(removeEntryUrl) {
  var ajax = new Ajax.Updater({success: $('entryTray')}, removeEntryUrl,
  {method: 'post', onSuccess: ajaxSuccess, onFailure: reportAjaxError});
  Element.show('spinner');
  isAjaxRequestSent = true;
}

function showHideEntryTray() {
  if ($('entryTrayWrapper').style.display == '') {
    Effect.SlideUp('entryTrayWrapper');
  } else {
    Effect.SlideDown('entryTrayWrapper');
  }
}

// ===============================================================================================
// Utility functions
// ===============================================================================================

function setBackgroundColor(tags, cssName, color) {
  for (var i = 0; i < tags.length; i++) {
    if (tags[i].className == cssName) {
        tags[i].style.backgroundColor = color;
    }
  }
}

function toggleInnerHTML(id, text1, text2) {
  var object = $(id);
  if (object.innerHTML == text1) {
    object.innerHTML = text2;
  } else {
    object.innerHTML = text1;
  }
}

// function to toggle the entry checkboxes
function toggleEntryFields(formName) {
  for (var i = 0; i < formName.length; i++) {
    var fieldObj = formName.elements[i];
    if (fieldObj.type == 'checkbox') {
      fieldObj.checked = !fieldObj.checked;
    }
  }
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
    for (var i = 0; i < formName.entry.length; i++) {
      if (formName.entry[i].checked) {
        checkedEntriesCount++;
      }
    }
  }
  return checkedEntriesCount;
}

// ===============================================================================================
// Extensions
// ===============================================================================================

Array.prototype.contains = function (element) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == element || element.constructor == String && this[i] == String(element)) {
      return true;
    }
  }
  return false
};
