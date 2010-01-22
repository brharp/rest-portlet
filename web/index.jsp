

<B> If you are viewing this page in a web browser, it means you have
successfully set up a REST portlet. Congratulations!</B>

<P>What next?</P>

<P>Presumably you have installed this package because you are
interested in developing a REST Portlet.</P>

<P>For those in need of an introduction to REST portlets, 
please read the
<A HREF="<%=request.getContextPath()%>intro.html">introduction</A>.

<P>If you need a refresher on the REST portlet protocol, 
check out the included protocol
<A HREF="<%=request.getContextPath()%>rest-portlet.html">specification</A>.


<P>When you are ready to develop your own portlet, you will want to
edit this portlet`s configuration and change the endpoint (the URL of
the portlet) to your own page.</P>

<P>To edit this portlet`s endpoint, edit:
<BR/><BR/><CODE>
<%=application.getRealPath("WEB-INF/portlet.xml")%>
</CODE><BR/><BR/>
 and set the `endpoint` parameter.</P>

<P>To see an example REST portlet in action, follow the 
directions above setting the endpoint parameter to
<BLOCKQUOTE>
<%=request.getContextPath()%>snoop.jsp
</BLOCKQUOTE>
The source code for the "snoop" portlet (in 
<%=application.getRealPath("snoop.jsp")%>)
demonstrates how to use nearly all REST portlet features.

