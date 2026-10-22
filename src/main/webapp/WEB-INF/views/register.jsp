<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jspf" %>
<div class="row justify-content-center"><div class="col-md-5"><div class="card shadow-sm"><div class="card-body p-4">
  <h4 class="mb-3">Member Registration</h4>
  <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${ctx}/register">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <div class="mb-3"><label class="form-label">Name</label><input name="name" class="form-control" maxlength="100" required></div>
    <div class="mb-3"><label class="form-label">Email</label><input type="email" name="email" class="form-control" required></div>
    <div class="mb-3"><label class="form-label">Password (min 8 chars)</label><input type="password" name="password" minlength="8" class="form-control" required></div>
    <button class="btn btn-success w-100">Register</button>
  </form>
  <p class="mt-3 mb-0 text-center"><a href="${ctx}/login">Back to login</a></p>
</div></div></div></div>
<%@ include file="footer.jspf" %>
