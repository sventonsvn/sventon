/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */

// function to handle action submissions in repo browser view
// Note: requires the global variable 'contextPath'
function doAction(form, name, path) {
  var input = $(form)['actionSelect'];
  var selectedValue = $F(input);

  // If no option value is selected, no action is taken.
  if (selectedValue == '') {
    return false;
  }

  // Check which action to execute
  if (selectedValue == 'thumb') {
    // One or more entries must be checked
    if (getCheckedCount(form) > 0) {
      form.action = contextPath + '/repos/' + name + '/showthumbnails' + path;
      return true;
    }
  } else if (selectedValue == 'diff') {
    // Exactly two entries must be checked
    if (getCheckedCount(form) == 2) {
      form.method = 'get';
      form.action = contextPath + '/repos/' + name + '/diff' + path;
      return true;
    } else {
      alert('Two entries must be selected');
    }
  } else if (selectedValue == 'export') {
    // One or more entries must be checked
    if (getCheckedCount(form) > 0) {
      form.action = contextPath + '/repos/' + name + '/export' + path;
      return true;
    }
  }
  return false;
}

function goto(form) {
  prepareGotoForm(form);
  form.submit();
}

// sets the value of the revision input text field to 'HEAD'
function goToHeadRevision(form) {
  prepareGotoForm(form);
  form.elements['revisionInput'].value = 'HEAD';
  form.submit();
}

// Note: requires the global variable 'contextPath'
function prepareGotoForm(form) {
  var path = form.elements['pathInput'].value;
  var name = form.elements['nameInput'].value;
  form.action = contextPath + '/repos/' + name + '/goto' + path;
}

// function to handle search submission
// Note: requires the global variable 'contextPath'
function doSearch(form, name, path) {
  var input = $(form)['searchString'];
  var searchStr = $F(input);

  // If no search string is entered, no action is taken.
  if (searchStr == '') {
    return false;
  } else {
    if (getCheckedValue(form.elements['searchMode']) == 'entries') {
      form.action = contextPath + '/repos/' + name + '/searchentries' + path;
    } else {
      form.action = contextPath + '/repos/' + name + '/searchlogs' + path;
    }
    if (searchStr.length < 3) {
      return confirm("Given search string is short. The result will potentially be very large.\nDo you want to continue anyway?");
    } else {
      return true;
    }
  }
}

// function to handle flatten submissions
// Note: requires the global variable 'contextPath'
function doFlatten(path, name) {
  var result = true;
  if (path == '/') {
    result = confirm("Flattening on root level is not recommended.\nThe result will potentially be very large.\nDo you want to continue anyway?");
  }
  if (result) {
    location.href = contextPath + '/repos/' + name + '/flatten' + path;
    return true;
  }
  return false;
}

// function to validate url during repository configuration submisson
function validateUrl(form) {
  var input = $(form)['repositoryUrl'];
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
function doDiff(form) {
  // Check if any entry is checked
  var checkedEntry = 0;
  for (var i = 0; i < form.entries.length; i++) {
    if (form.entries[i].type == 'checkbox' && form.entries[i].checked) {
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
  for (var i = 0; i < form.entries.length; i++) {
    if (form.entries[i].type == 'checkbox' && form.entries[i].checked) {
      if (first == null && form.entries[i] != checkbox) {
        first = form.entries[i];
      }
      count += 1;
    }
  }

  if (count > 2) {
    first.checked = false;
    count -= 1;
  }
}

// Toggles line wrap mode between normal and nowrap
function toggleWrap() {
  var classNames = new Array(['src'], ['srcChg'], ['srcAdd'], ['srcDel']);

  try {
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
  } catch(ex) {
    //
  }
}

// Requests directory contents in given path
// Note: requires the global variable 'contextPath'
function listFiles(rowNumber, name, path) {
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
    var url = contextPath + '/ajax/' + name + '/listfiles' + path;
    var urlParams = 'revision=head&rowNumber=' + rowNumber;
    var elementId = 'dir' + rowNumber;
    new Ajax.Updater({success: elementId}, url, {
      method: 'post', parameters: urlParams, onFailure: reportAjaxError, insertion:Insertion.After, onComplete:
        function(response) {
          iconElement.src = 'images/icon_folder.png';
          iconElement.className = 'minus';
          // TODO: Optimize this - now we will re-create Draggables for already visible entries.
          var entries = document.getElementsByClassName('entries');
          for (var i = 0; i < entries.length; i++) {
            new Draggable(entries[i].id, {revert:true})
          }
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
// Note: requires the global variable 'contextPath'
function getLatestRevisions(name, count) {
  // Do the ajax call
  var url = contextPath + '/ajax/' + name + '/latestrevisions';
  var urlParams = 'revision=head&revcount=' + count;

  new Ajax.Updater({success: $('latestCommitInfoDiv')}, url, {
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

// Note: requires the global variable 'contextPath'
function getHelpText(id) {
  var url = contextPath + '/ajax/static';
  var urlParams = 'id=' + id;
  var divName = 'help' + id + 'Div';

  new Ajax.Request(url, {
    method: 'post',
    parameters: urlParams,
    onFailure: reportAjaxError,
    onSuccess: function(transport) {
      Element.update(divName, transport.responseText);
    }});
  return Tip('<div id=\"' + divName + '\" style=\"width: 350px;\"><img src=\"images/spinner.gif\" alt=\"spinner\"></div>', TITLE, 'Help', BORDERCOLOR, '#3e647e', BGCOLOR, '#ffffff', STICKY, 1, CLICKCLOSE, true);
}

// Gets the log message for given revision.
// Note: requires the global variable 'contextPath'
function getLogMessage(revision, name, date) {
  var url = contextPath + '/ajax/' + name + '/getmessage';
  var urlParams = 'revision=' + revision;
  var divName = 'msg' + revision + 'Div';

  new Ajax.Request(url, {
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

function removeEntryFromTray(removeEntryUrl) {
  new Ajax.Updater({success: $('entryTray')}, removeEntryUrl, {
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

// Note: requires the global variable 'contextPath'
function getFileHistory(name, path, revision, archivedEntry) {
  var url = contextPath + '/ajax/' + name + '/filehistory' + path;
  var urlParams = 'revision=' + revision;
  if (archivedEntry != '') {
    urlParams = urlParams + '&archivedEntry=' + archivedEntry;
  }

  new Ajax.Updater({success: $('fileHistoryContainerDiv')}, url, {
    method: 'post', parameters: urlParams, onFailure: reportAjaxError});
}

function updateCharsetParameter(charset, queryString) {
  var charsetParameter = '&charset=';
  var index = queryString.indexOf(charsetParameter);
  if (index > -1) {
    queryString = queryString.substring(0, index);
  }
  document.location.href = document.location.pathname + '?' + queryString + charsetParameter + charset;
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
function toggleEntryFields(form) {
  for (var i = 0; i < form.length; i++) {
    var fieldObj = form.elements[i];
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
function getCheckedCount(form) {
  var undefined;
  var checkedEntriesCount = 0;

  // Check if only one entry exists - and whether it's checked
  if (form.entries.length == undefined) {
    checkedEntriesCount = form.entries.checked ? 1 : 0;
  } else {
    // More than one entry exists - Check how many are checked
    for (var i = 0; i < form.entries.length; i++) {
      if (form.entries[i].checked) {
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
