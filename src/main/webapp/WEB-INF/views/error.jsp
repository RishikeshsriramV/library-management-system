<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ include file="header.jspf" %>
<div class="text-center py-5"><h3>Something went wrong</h3>
<p class="text-muted">The request could not be completed (error ${pageContext.errorData.statusCode}).</p>
<a class="btn btn-primary" href="${ctx}/books">Back to books</a></div>
<%@ include file="footer.jspf" %>
