<?php
/**
 * PHP REST Portlet (Snoop) implementation.
 */

//edit|view|help
$mode = $_SERVER['HTTP_X_PORTLET_PORTLET_MODE'];
//init|render|processAction|destroy
$method = $_SERVER['HTTP_X_PORTLET_METHOD'];
$prefs = Array();

//Parse out the preferences string
if(isset($_SERVER['HTTP_X_PORTLET_PREFERENCE'])){
	$prefHeader = $_SERVER['HTTP_X_PORTLET_PREFERENCE'];
	foreach(split(';', $prefHeader) as $pref){
		$kv = split('=', $pref);
		$prefs[trim($kv[0])] = trim($kv[1]);
	}
}

function getPrefValue($key,$default=null){
	global $prefs;
	if(isset($prefs[$key])) return $prefs[$key];
	return $default;
}


//Retrieve the preferred colour, default to black
$hColour = getPrefValue("colour", "black");

/** 
 * Retrieve a value from a form submit, and use
 * a response header to have the portal remember the value as a preference.
 * Built for php 4.1.0 and later ($_SERVER used to be a global called $HTTP_SERVER_VARS)
 */
$prefPairs = "";
if(isset($_REQUEST['pname']) and isset($_REQUEST['pvalue'])){
	$prefPairs .= "{$_REQUEST['pname']}={$_REQUEST['pvalue']};";
}
header("X-Portlet-Set-Preference:$prefPairs");

if(isset($_SERVER['QUERY_STRING'])){
	echo "<h1>Request Info</h2>\n";
	echo "Query String: {$_SERVER['QUERY_STRING']}\n";
}

//Prints out all of the HTTP headers, Note: PHP replaces - with _ and capitalizes keynames
echo "<h1>Portlet Headers</h1>\n";
echo "<div style=\"color:$hColour;\">\n";
foreach($_SERVER as $key=>$val){
	if(preg_match("/^HTTP_.*/", $key)){
		echo "$key: $val<br />\n";
	}
}
echo "</div>\n";
?>

<h1>Preferences</h1>
<!-- causes a preference to be stored by the portal, see PHP code -->
<form method="post" action="portlet:processAction">
	<label for='pname'>Name</label>
	<input name='pname'></input><br/>
	<label for='pvalue'>Value</label>
	<input name='pvalue'></input><br/>
	<input type='submit' value='Set'/>
</form>

<h1>Render Parameters</h1>

<form method="post" action="portlet:processAction">
	<label for='rname'>Name</label>
	<input name='rname'></input><br/>
	<label for='rvalue'>Value</label>
	<input name='rvalue'></input><br/>
	<input type='submit' value='Set'/>
</form>

<h1>Portlet URLs</h1>

<a href="portlet:render?foo=bar">renderURL</a>
<a href="portlet:processAction?foo=bar">actionURL</a>

<h1>Window State</h1>
<a href="portlet:render;state=maximized">Maximize</a>
<a href="portlet:render;state=minimized">Minimize</a>
<a href="portlet:render;state=normal">Normalize</a>

<h1>Portlet Mode</h1>
<a href="portlet:render;mode=view">View</a>
<a href="portlet:render;mode=edit">Edit</a>
<a href="portlet:render;mode=help">Help</a>

<h1>Title</h1>
<form action="portlet:processAction">
	<label for='title'>Title</label>
	<input name='title'></input><br/>
	<input type='submit' value='Set'/>
</form>

<h1>Images</h1>
<!-- The path is relative to this file, not the portal -->
<img src='onintelligence.jpg'></img> 
