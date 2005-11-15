    <div id="propertiesDiv" style="display:none" class="sventonPropertiesDiv">
      <br/>
        <table class="sventonPropertiesTable">
          <c:forEach items="${properties}" var="property">
            <tr>
              <td><b><c:out value="${property.key}"/>:&nbsp;</b></td>
              <td><c:out value="${property.value}"/></td>
            </tr>
          </c:forEach>
        </table>
    </div>
