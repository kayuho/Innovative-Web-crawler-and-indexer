<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="domain.collection.documents.domain.collection.documents.GenericDocument,java.util.List,domain.search.QueryProcessor,technical.helpers.BenchmarkRow"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	BenchmarkRow timer = new BenchmarkRow(null);
	timer.start();
	if (((Integer) request.getAttribute("result-count")) == 0) {
%>
	<h2>Sorry, no results have been found for <%=request.getAttribute("query")%></h2>
	
<%
		} else {
	%>

I found <%=request.getAttribute("result-count")%> results in <%=request.getAttribute("time-to-match")%> ms.

	<%
		while(QueryProcessor.hasNext()) { 
			GenericDocument a = QueryProcessor.next().getResult(); if (a==null) continue;
	%>
		<h3><a href="document/view/?d=<%=a.getId()%>&q=<%= request.getAttribute("query") %>"><%= a.getTitle() %></a></h3>
	<% 
	 	response.flushBuffer();
		}
}%>

<% timer.stop();  %>
<hr>Page generated in <%= timer.getDuration()/1000.0 %> seconds.