<%@ include file="/init.jsp" %>

<%
	CommerceAccount commerceAccount = (CommerceAccount) request.getAttribute("commerceAccount");

%>
<p>
	Account verification required for <%= commerceAccount.getName()%>;
</p>
