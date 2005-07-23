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

