function GimletHandlerObject() {}
GimletHandlerObject.prototype = new ZmZimletBase();
GimletHandlerObject.prototype.constructor = GimletHandlerObject;

baseURI = "baseURI:";

function resolveURI (relativeURI, baseURI) {
  if (relativeURI.indexOf(":") < 0) {
    uri = baseURI + relativeURI;
  } else {
    uri = relativeURI;
  }
  if (uri.indexOf("portlet:") == 0) {
    return createPortletURL(uri);
  } else {
    return uri;
  }
}


function createURL (method, mode, state, secure, params) {
  return "javascript:"+method+"(\""+mode+"\",\""+state+"\",\""+secure+"\","
             +params+")";
}

function PortletMode () {}
PortletMode.VIEW = "view";
PortletMode.EDIT = "edit";
PortletMode.HELP = "help";

function WindowState () {}
WindowState.MAXIMIZED = "maximized";
WindowState.MINIMIZED = "minimized";
WindowState.NORMAL = "normal";

// Creates a URI from a portlet: scheme URI.
// In a Zimlet, portlet: scheme URIs are implemented
// as javascript: URIs that call one of render() or 
// or processAction.
GimletHandlerObject
function createPortletURL (uri) {
  var method;
  var mode = PortletMode.VIEW;
  var state = WindowState.NORMAL;
  var secure = true;
  var parameters = [];
  parts = uri.substr(8).split("?", 2);
  if (parts.length > 0) {
    var pathComp = parts[0];
    var path = pathComp.split(";", 2);
    method = path[0];
    if (path.length > 1) {
      var params = path[1].split(";");
      for (var i = 0; i < params.length; i++) {
        var nv = params[i].split("=");
        if (nv.length == 2) {
          var name = nv[0].toLowerCase();
          var value = nv[1].toLowerCase();
          if ("mode" == name)
            { mode = value; }
          else if ("state" == name)
            { state = value; }
          else if ("secure" == name)
            { secure = value; }
        }
      }
    } else { // there was nothing after portlet:
      method = "render";
    }
  }
  if (parts.length == 2) {
    query = parts[1];
    pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++){ 
      var nv = pairs[i].split("=");
      if (nv.length = 2) {
        var name = nv[0];
        var value = nv[1];
        parameters.push("\""+name+"\":\""+value+"\"");
      }
    }
  }
  return createURL(method,mode,state,secure,"{"+parameters.join(",")+"}");
}

GimletHandlerObject.prototype.singleClicked =
function (canvas) {
  // Set up the dialog.
  var view = new DwtComposite(this.getShell());
  view.setSize(320, 480);
  this.content = document.createElement("div");
  this.content.style.width = 320;
  this.content.style.height = 480;
  this.content.style.overflow = 'scroll';
  view.getHtmlElement().appendChild(this.content);
  var title = this.getMessage("gimletName");
  canvas = this._createDialog({view: view, title: title});
  canvas.view = view;
  this.render("view", "normal", true, {});
  canvas.popup(); 
  return canvas;
};

GimletHandlerObject.prototype.render =
function (mode,state,secure,params) {
  // Get content.
  var req = new XMLHttpRequest();
  req.open("GET", this.getResource("snoop.jsp"), false);
  req.setRequestHeader("X-Portlet-Method", "render");
  req.setRequestHeader("X-Portlet-Auth-Type", "password");
  req.setRequestHeader("X-Portlet-Context-Path", this.getResource(""));
  req.setRequestHeader("X-Portlet-Locale", navigator.language);
  req.setRequestHeader("X-Portlet-Namespace", "");
  req.setRequestHeader("X-Portlet-Portlet-Mode", mode);
  req.setRequestHeader("X-Portlet-Parameter", "");
  var hv = "";
  var props = this.xmlObj().userProperties;
  for (var i = 0; i < props.length; i++) {
    hv += props[i].name + "=" + props[i].value + ";";
  }
  req.setRequestHeader("X-Portlet-Preference", hv);
  // ZmZimletBase.getConfigProperty() not implemented in 5.0.20
  req.setRequestHeader("X-Portlet-Property", "");
  req.setRequestHeader("X-Portlet-Remote-User", this.getUsername());
  req.setRequestHeader("X-Portlet-Response-Content-Type", "");
  req.setRequestHeader("X-Portlet-Response-Content-Types", "");
  req.setRequestHeader("X-Portlet-Secure", "");
  req.setRequestHeader("X-Portlet-Session-Id", "");
  req.setRequestHeader("X-Portlet-Server-Name", "");
  req.setRequestHeader("X-Portlet-Server-Port", "");
  req.setRequestHeader("X-Portlet-Scheme", "");
  req.setRequestHeader("X-Portlet-User-Info", "");
  req.setRequestHeader("X-Portlet-Window-State", state);
  req.send("");
  if (req.readyState == 4 && req.status == 200) {
    var div = this.content;
    div.innerHTML = req.responseText;
    // Process links.
    var names=["a"];
    var attrs=["href"];
    for (var i = 0; i < names.length; i++) {
      var name = names[i];
      var elts = div.getElementsByTagName(name);
      for (var j = 0; j < elts.length; j++) {
        var e = elts[j];
        for (var k = 0; k < attrs.length; k++) {
          if (e.hasAttribute(attrs[k])) {
            var a = e.getAttribute(attrs[k]);
            e.setAttribute(attrs[k], resolveURI(a));
          }
        }
      }
    }
  }
}

GimletHandlerObject.prototype.init =
function () {
  this.namespace = "G" + (Math.random() * 10);
  GimletHandlerObject[this.namespace] = this;
}
