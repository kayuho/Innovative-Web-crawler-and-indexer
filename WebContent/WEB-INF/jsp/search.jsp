<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="domain.GenericPosting,java.util.List,domain.search.QueryProcessor,technical.helpers.BenchmarkRow"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search results for <%=request.getAttribute("query")%></title>
</head>
<body><center>
<jsp:include page="searchForm.jsp" />
<script>

</script>
<div id="results">
<jsp:include page="results.jsp" />
</div>
</body>
</html>