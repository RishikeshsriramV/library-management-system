<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jspf" %>
<div class="d-flex justify-content-between align-items-center mb-3">
  <h4 class="mb-0">Books</h4>
  <c:if test="${sessionScope.user.admin}"><a class="btn btn-primary" href="${ctx}/admin/book/new">+ Add Book</a></c:if>
</div>
<form class="row g-2 mb-3" method="get" action="${ctx}/books">
  <div class="col-md-3"><select name="f" class="form-select">
    <option value="title" ${f=='title'?'selected':''}>Title</option>
    <option value="author" ${f=='author'?'selected':''}>Author</option>
    <option value="category" ${f=='category'?'selected':''}>Category</option>
  </select></div>
  <div class="col-md-6"><input name="q" value="<c:out value='${q}'/>" class="form-control" placeholder="Search..."></div>
  <div class="col-md-3"><button class="btn btn-dark w-100">Search</button></div>
</form>
<div class="table-responsive"><table class="table table-striped bg-white align-middle">
  <thead><tr><th>Title</th><th>Author</th><th>Category</th><th>ISBN</th><th>Available</th>
    <c:if test="${sessionScope.user.admin}"><th>Actions</th></c:if></tr></thead>
  <tbody>
  <c:forEach var="b" items="${books}">
    <tr>
      <td><c:out value="${b.title}"/></td><td><c:out value="${b.author}"/></td>
      <td><c:out value="${b.category}"/></td><td><c:out value="${b.isbn}"/></td>
      <td><span class="badge ${b.availableCopies > 0 ? 'bg-success' : 'bg-danger'}">${b.availableCopies} / ${b.totalCopies}</span></td>
      <c:if test="${sessionScope.user.admin}"><td>
        <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/book/edit?id=${b.id}">Edit</a>
        <form method="post" action="${ctx}/admin/book/delete" class="d-inline" onsubmit="return confirm('Delete this book?')">
          <input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="id" value="${b.id}">
          <button class="btn btn-sm btn-outline-danger">Delete</button></form>
      </td></c:if>
    </tr>
  </c:forEach>
  <c:if test="${empty books}"><tr><td colspan="6" class="text-center text-muted">No books found.</td></tr></c:if>
  </tbody></table></div>
<%@ include file="footer.jspf" %>
