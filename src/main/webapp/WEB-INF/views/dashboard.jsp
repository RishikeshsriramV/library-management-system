<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jspf" %>
<h4 class="mb-3">Admin Dashboard</h4>
<div class="row g-3 mb-4">
  <div class="col-6 col-md"><div class="card text-center"><div class="card-body"><div class="fs-3">${stats.books}</div>Titles</div></div></div>
  <div class="col-6 col-md"><div class="card text-center"><div class="card-body"><div class="fs-3">${stats.members}</div>Members</div></div></div>
  <div class="col-6 col-md"><div class="card text-center"><div class="card-body"><div class="fs-3">${stats.active}</div>Issued</div></div></div>
  <div class="col-6 col-md"><div class="card text-center text-danger"><div class="card-body"><div class="fs-3">${stats.overdue}</div>Overdue</div></div></div>
  <div class="col-6 col-md"><div class="card text-center"><div class="card-body"><div class="fs-3">Rs. ${stats.fines}</div>Fines</div></div></div>
</div>
<div class="card mb-4"><div class="card-body">
  <h5>Issue a Book</h5>
  <form class="row g-2" method="post" action="${ctx}/admin/issue">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <div class="col-md-5"><select name="userId" class="form-select" required><option value="">Select member</option>
      <c:forEach var="m" items="${members}"><option value="${m.id}"><c:out value="${m.name}"/> (<c:out value="${m.email}"/>)</option></c:forEach></select></div>
    <div class="col-md-5"><select name="bookId" class="form-select" required><option value="">Select book</option>
      <c:forEach var="b" items="${books}"><c:if test="${b.availableCopies > 0}"><option value="${b.id}"><c:out value="${b.title}"/> (${b.availableCopies} left)</option></c:if></c:forEach></select></div>
    <div class="col-md-2"><button class="btn btn-primary w-100">Issue</button></div>
  </form>
</div></div>
<h5>Currently Issued</h5>
<div class="table-responsive"><table class="table bg-white align-middle">
  <thead><tr><th>Book</th><th>Member</th><th>Issued</th><th>Due</th><th>Fine (so far)</th><th></th></tr></thead><tbody>
  <c:forEach var="i" items="${active}">
    <tr class="${i.overdue ? 'table-danger' : ''}">
      <td><c:out value="${i.bookTitle}"/></td><td><c:out value="${i.memberName}"/></td>
      <td><fmt:formatDate value="${i.issueDate}" pattern="dd MMM yyyy"/></td>
      <td><fmt:formatDate value="${i.dueDate}" pattern="dd MMM yyyy"/></td>
      <td>${i.currentFine > 0 ? 'Rs. '.concat(i.currentFine) : '-'}</td>
      <td><form method="post" action="${ctx}/admin/return">
        <input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="issueId" value="${i.id}">
        <button class="btn btn-sm btn-success">Return</button></form></td>
    </tr>
  </c:forEach>
  <c:if test="${empty active}"><tr><td colspan="6" class="text-center text-muted">No books currently issued.</td></tr></c:if>
</tbody></table></div>
<%@ include file="footer.jspf" %>
