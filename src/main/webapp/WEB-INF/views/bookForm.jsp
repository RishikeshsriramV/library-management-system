<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jspf" %>
<div class="row justify-content-center"><div class="col-md-6"><div class="card shadow-sm"><div class="card-body p-4">
  <h4 class="mb-3">${empty book ? 'Add Book' : 'Edit Book'}</h4>
  <form method="post" action="${ctx}/admin/book/save">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="id" value="${empty book ? 0 : book.id}">
    <div class="mb-3"><label class="form-label">Title</label><input name="title" class="form-control" value="<c:out value='${book.title}'/>" required></div>
    <div class="mb-3"><label class="form-label">Author</label><input name="author" class="form-control" value="<c:out value='${book.author}'/>" required></div>
    <div class="mb-3"><label class="form-label">Category</label><input name="category" class="form-control" value="<c:out value='${book.category}'/>" required></div>
    <div class="mb-3"><label class="form-label">ISBN</label><input name="isbn" class="form-control" value="<c:out value='${book.isbn}'/>"></div>
    <div class="mb-3"><label class="form-label">Total Copies</label><input type="number" min="0" name="total" class="form-control" value="${empty book ? 1 : book.totalCopies}" required></div>
    <button class="btn btn-primary">Save</button> <a class="btn btn-secondary" href="${ctx}/books">Cancel</a>
  </form>
</div></div></div></div>
<%@ include file="footer.jspf" %>
