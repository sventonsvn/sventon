  <p class="sventonHeader">
  Blame - <b><c:out value="${command.target}"/></b>&nbsp;<a href="javascript:toggleElementVisibility('propertiesDiv');">[show/hide properties]</a>
  </p>

    <div id="propertiesDiv" style="display:none" class="sventonPropertiesDiv">
        <table class="sventonPropertiesTable">
          <c:forEach items="${properties}" var="property">
            <tr>
              <td><b><c:out value="${property.key}"/></b></td>
              <td><c:out value="${property.value}"/></td>
            </tr>
          </c:forEach>
        </table>
    </div>
