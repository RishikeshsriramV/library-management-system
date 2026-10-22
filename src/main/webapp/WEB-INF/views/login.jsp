<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="header.jspf" %>
<div class="row justify-content-center"><div class="col-md-5"><div class="card shadow-sm"><div class="card-body p-4">
  <h4 class="mb-3">Login</h4>
  <c:if test="${param.registered == '1'}"><div class="alert alert-success">Registered! Please login.</div></c:if>
  <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${ctx}/login">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <div class="mb-3"><label class="form-label">Email</label><input type="email" name="email" class="form-control" required></div>
    <div class="mb-3"><label class="form-label">Password</label><input type="password" name="password" class="form-control" required></div>
    <button class="btn btn-primary w-100">Login</button>
  </form>
  <p class="mt-3 mb-0 text-center">New member? <a href="${ctx}/register">Register</a></p>
</div></div></div></div>
<%@ include file="footer.jspf" %>
