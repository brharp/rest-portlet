<%@ page import="ca.uoguelph.ccs.portal.rest.Header" %>
<%
String method = request.getHeader("X-Portlet-Method");

if ("processAction".equals(method)) {
    if (request.getParameter("pname") != null) {
        response.setHeader("X-Portlet-Set-Preference",
                request.getParameter("pname") + "=" +
                request.getParameter("pvalue") + "; ");
    }
    if (request.getParameter("rname") != null) {
        response.setHeader("X-Portlet-Set-Render-Parameter",
                request.getParameter("rname") + "=" +
                request.getParameter("rvalue") + "; ");
    }
    if (request.getParameter("lmesg") != null) {
  	response.setHeader("X-Portlet-Log",
		request.getParameter("lmesg"));
    }
    if (request.getParameter("title") != null) {
        response.setHeader("X-Portlet-Set-Render-Parameter",
		"title=" + 
		request.getParameter("title") + "; ");
    }
}

if ("render".equals(method)) {
    if (request.getParameter("title") != null) {
  	response.setHeader("X-Portlet-Set-Title",
		request.getParameter("title"));
	response.setHeader("X-Portlet-Set-Render-Parameter",
		"title=" +
		request.getParameter("title") + "; ");
    }
}
%>
<%!
public String filter (String s)
{
  StringBuffer b = new StringBuffer();
  for (int i = 0; i < s.length; i++) {
     char c = s.charAt(i);
     switch (c) {
       case '>': b.append("&gt;"); break;
       case '<': b.append("&lt;"); break;
       case '&': b.append("&amp;"); break;
       case '"': b.append("&dquot;"); break;
       default: b.append(c); break;
     }
  }
  return b.toString();
}
%>

<h1>Request Info</h1>

Query String: 
<% out.print(filter(request.getQueryString())); %><br/>

<h1>Portlet Headers</h1>

<% for (int i = 0; i < Header.renderRequestHeaders.length; i++) { %>
  <% String h = Header.renderRequestHeaders[i].getName(); %>
  <% out.print(filter(h)); %>: 
  <% out.print(filter(request.getHeader(h))); %><BR>
<% } %>


<% boolean isPortlet = request.getHeader("X-Portlet-Method") != null; %>

<h1>Preferences</h1>

<form method="post" action="<%=isPortlet?"portlet:processAction":"#"%>">
  <label for='pname'>Name</label>
  <input name='pname'></input><br/>
  <label for='pvalue'>Value</label>
  <input name='pvalue'></input><br/>
  <input type='submit' value='Set'/>
</form>

<h1>Render Parameters</h1>

<form method="post" action="<%=isPortlet?"portlet:processAction":"#"%>">
  <label for='rname'>Name</label>
  <input name='rname'></input><br/>
  <label for='rvalue'>Value</label>
  <input name='rvalue'></input><br/>
  <input type='submit' value='Set'/>
</form>

<h1>Logging</h1>

<form method="post" action="<%=isPortlet?"portlet:processAction":"#"%>">
  <label for='lmesg'>Message</label>
  <input name='lmesg'></input><br/>
  <input type='submit' value='Log'/>
</form>

<h1>Portlet URLs</h1>

<a href="<%=isPortlet?"portlet:render":""%>?foo=bar">renderURL</a>
<a href="<%=isPortlet?"portlet:processAction":""%>?foo=bar">actionURL</a>

<h1>Window State</h1>

<a href="<%=isPortlet?"portlet:render;state=maximized":""%>"
  >Maximize</a>

<a href="<%=isPortlet?"portlet:render;state=minimized":""%>"
  >Minimize</a>

<a href="<%=isPortlet?"portlet:render;state=normal":""%>"
  >Normalize</a>

<h1>Portlet Mode</h1>

<a href="<%=isPortlet?"portlet:render;mode=view":""%>"
  >View</a>

<a href="<%=isPortlet?"portlet:render;mode=edit":""%>"
  >Edit</a>

<a href="<%=isPortlet?"portlet:render;mode=help":""%>"
  >Help</a>

<h1>Title</h1>

<form action="<%=isPortlet?"portlet:processAction":"#"%>">
  <label for='title'>Title</label>
  <input name='title'></input><br/>
  <input type='submit' value='Set'/>
</form>

<h1>Images</h1>

<img src='onintelligence.jpg'></img>

