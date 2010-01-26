<%@ page import="ca.uoguelph.ccs.portal.rest.Header" %>
<%@ page import="ca.uoguelph.ccs.ws.XMLHttpRequestObject" %>
<% 

XMLHttpRequestObject xhr = new XMLHttpRequestObject();
Header[] requestHeaders = Header.renderRequestHeaders;
for (int i = 0; i < requestHeaders.length; i++) {
  String name = requestHeaders[i].getName();
  xhr.setRequestHeader(name, request.getHeader(name));
}
xhr.open("GET", "http://localhost:7080/service/zimlet/_dev/gimlet/snoop.jsp", false);
xhr.send(request.getReader());
Header[] responseHeaders = Header.responseHeaders;
for (int i = 0; i < responseHeaders.length; i++) {
  String name = responseHeaders[i].getName();
  response.setHeader(name, xhr.getResponseHeader(name));
}


%>
