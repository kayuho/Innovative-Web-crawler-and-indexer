<form method="get" action="/InformationRetrieval/search" id="searchForm">
	<input id="q" name="q" type="text" size=41  value="<%= (request.getParameter("q") != null) ? request.getParameter("q") : ""%>" /><input type="submit" value="search" />
</form>

<hr/>