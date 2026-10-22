<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jspf" %>
<h4 class="mb-3">My Borrowing History</h4>
<div class="table-responsive"><table class="table bg-white align-middle">
  <thead><tr><th>Book</th><th>Issued</th><th>Due</th><th>Returned</th><th>Fine</th><th>Status</th></tr></thead><tbody>
  <c:forEach var="i" items="${history}">
    <tr class="${i.overdue ? 'table-danger' : ''}">
      <td><c:out value="${i.bookTitle}"/></td>
      <td><fmt:formatDate value="${i.issueDate}" pattern="dd MMM yyyy"/></td>
      <td><fmt:formatDate value="${i.dueDate}" pattern="dd MMM yyyy"/></td>
      <td><c:choose><c:when test="${i.returned}"><fmt:formatDate value="${i.returnDate}" pattern="dd MMM yyyy"/></c:when><c:otherwise>-</c:otherwise></c:choose></td>
      <td>${i.currentFine > 0 ? 'Rs. '.concat(i.currentFine) : '-'}</td>
      <td><c:choose>
        <c:when test="${i.returned}"><span class="badge bg-secondary">Returned</span></c:when>
        <c:when test="${i.overdue}"><span class="badge bg-danger">Overdue ${i.lateDays}d</span></c:when>
        <c:otherwise><span class="badge bg-primary">Issued</span></c:otherwise></c:choose></td>
    </tr>
  </c:forEach>
  <c:if test="${empty history}"><tr><td colspan="6" class="text-center text-muted">You haven't borrowed any books yet.</td></tr></c:if>
</tbody></table></div>
<%@ include file="footer.jspf" %>
