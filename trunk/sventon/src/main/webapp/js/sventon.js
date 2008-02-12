/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */

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
function goToHeadRevision(form) {
  form.elements['revisionInput'].value = 'HEAD';
  form.submit();
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
function listFiles(rowNumber, path, name) {
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
    var ajax = new Ajax.Updater({success: elementId}, url, {
      method: 'post', parameters: urlParams, onFailure: reportAjaxError, insertion:Insertion.After, onComplete:
        function(response) {
          iconElement.src = 'images/icon_folder.png';
          iconElement.className = 'minus';
          Element.hide('spinner');
        }
    });
    iconElement.src = 'images/spinner.gif';
    Element.show('spinner');
  }
  return false;
}

function hideLatestRevisions() {
  var infoDiv = $('latestCommitInfoDiv');
  if (infoDiv.visible()) {
    // Hide details
    Element.hide(infoDiv);
    Element.update(infoDiv, '');
    toggleInnerHTML('latestCommitLink', 'show', 'hide');
  }
}

// Requests the N latest revisions
function getLatestRevisions(name, count) {
  // Do the ajax call
  var url = 'latestrevisions.ajax';
  var urlParams = 'path=/&revision=head&name=' + name + "&revcount=" + count;

  var ajax = new Ajax.Updater({success: $('latestCommitInfoDiv')}, url, {
    method: 'post', parameters: urlParams, onFailure: reportAjaxError, onSuccess: function(request) {
    Element.show('latestCommitInfoDiv');
    Element.update('latestCommitLink', 'hide');
  }, onComplete: function() {
    Element.hide('spinner');
  }});
  Element.show('spinner');
  return false;
}

// General ajax error alert method
function reportAjaxError(request) {
  alert('An error occured during asynchronous request.');
}

function getHelpText(id) {
  var url = 'static.ajax';
  var urlParams = 'id=' + id;
  var divName = 'help' + id + 'Div';

  var ajax = new Ajax.Request(url, {
    method: 'post',
    parameters: urlParams,
    onFailure: reportAjaxError,
    onSuccess: function(transport) {
      Element.update(divName, transport.responseText);
    }});
  return Tip('<div id=\"' + divName + '\" style=\"width: 350px;\"><img src=\"images/spinner.gif\" alt=\"spinner\"></div>', TITLE, 'Help', BORDERCOLOR, '#3e647e', BGCOLOR, '#ffffff', STICKY, 1, CLICKCLOSE, true);
}

// Gets the log message for given revision.
function getLogMessage(revision, instanceName, date) {
  var url = 'getmessage.ajax';
  var urlParams = 'revision=' + revision + '&name=' + instanceName;
  var divName = 'msg' + revision + 'Div';

  var ajax = new Ajax.Request(url, {
    method: 'post',
    parameters: urlParams,
    onFailure: reportAjaxError,
    onSuccess: function(transport) {
      Element.update(divName, transport.responseText);
    }
  });
  return Tip('<div id=\"' + divName + '\" style=\"width: 350px;\"><img src=\"images/spinner.gif\" alt=\"spinner\"></div>', TITLE, 'Log message: ' + date, BORDERCOLOR, '#3e647e', BGCOLOR, '#ffffff', ABOVE, true);
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
  var ajax = new Ajax.Updater({success: $('entryTray')}, element.id, {
    method: 'post', onFailure: reportAjaxError, onComplete: function(request) {
    Element.hide('spinner');
  }});
  Element.show('spinner');
}

function removeEntryFromTray(removeEntryUrl) {
  var ajax = new Ajax.Updater({success: $('entryTray')}, removeEntryUrl, {
    method: 'post', onFailure: reportAjaxError, onComplete: function(request) {
    Element.hide('spinner');
  }});
  Element.show('spinner');
}

function showHideEntryTray() {
  if ($('entryTrayWrapper').visible()) {
    Effect.SlideUp('entryTrayWrapper', {duration:0.3});
  } else {
    Effect.SlideDown('entryTrayWrapper', {duration:0.3});
  }
}

function getFileHistory(path, revision, name, archivedEntry) {
  var url = 'filehistory.ajax';
  var urlParams = 'path=' + path + '&revision=' + revision + '&name=' + name;
  if (archivedEntry != '') {
    urlParams = urlParams + '&archivedEntry=' + archivedEntry;
  }

  var ajax = new Ajax.Updater({success: $('fileHistoryContainerDiv')}, url, {
    method: 'post', parameters: urlParams, onFailure: reportAjaxError});
}

function updateCharsetParameter(charset, queryString) {
  var charsetParameter = '&charset=';
  var index = queryString.indexOf(charsetParameter);
  if (index > -1) {
    queryString = queryString.substring(0, index);
  }
  var newUrl = document.location.pathname + '?' + queryString + charsetParameter + charset;
  document.location.href = newUrl;
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
