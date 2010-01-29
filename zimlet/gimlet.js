function GimletHandlerObject() {}
GimletHandlerObject.prototype = new ZmZimletBase();
GimletHandlerObject.prototype.constructor = GimletHandlerObject;

baseURI = "baseURI:";

GimletHandlerObject.prototype.resolveURI =
function  (relativeURI, baseURI, formId) {
  if (relativeURI.indexOf(":") < 0) {
    uri = baseURI + relativeURI;
  } else {
    uri = relativeURI;
  }
  if (uri.indexOf("portlet:") == 0) {
    return this.createPortletURL(uri, formId);
  } else {
    return uri;
  }
}

function PortletMode () {}
PortletMode.VIEW = "view";
PortletMode.EDIT = "edit";
PortletMode.HELP = "help";

function WindowState () {}
WindowState.MAXIMIZED = "maximized";
WindowState.MINIMIZED = "minimized";
WindowState.NORMAL    = "normal";

// Creates a URI from a portlet: scheme URI.
// In a Zimlet, portlet: scheme URIs are implemented
// as javascript: URIs that call one of render() or 
// or processAction.
GimletHandlerObject.prototype.createPortletURL =
function (uri, formId) {
  var id = this.id;
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
    }
  }
  var query = parts.length > 1 ? parts[1] : "";
  return this.createURL(method,mode,state,secure,query,formId);
}

GimletHandlerObject.prototype.createURL =
function (method, mode, state, secure, params, formId) {
  var id = this.id;
  return ("javascript:" + method + "(" +  id + "," +
          "'" + mode   + "'" + "," + "'" + state  + "'" + "," +
          "'" + secure + "'" + "," + "'" + params + "'" + "," +
          "'" + formId + "'" + ")");
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
  this.render(this.id, "view", "normal", true, "");
  canvas.popup(); 
  return canvas;
};

GimletHandlerObject.prototype.update =
function (mode, state) {
  this.mode = mode;
  this.state = state;
};

GimletHandlerObject.prototype.render =
function (mode,state,secure,params) {
  this.update(mode, state);
  this.doProxy("GET", params);
};

GimletHandlerObject.prototype.processAction =
function (mode,state,secure,params) {
  var req = new XMLHttpRequest();
  var url = this.getResource("snoop.jsp");
  req.open("POST", params.length > 0 ? url+"?"+params : url , false);
  req.setRequestHeader("X-Portlet-Method", "processAction");
  req.setRequestHeader("X-Portlet-Auth-Type", "password");
  req.setRequestHeader("X-Portlet-Context-Path", this.getResource(""));
  req.setRequestHeader("X-Portlet-Locale", navigator.language);
  req.setRequestHeader("X-Portlet-Namespace", "");
  req.setRequestHeader("X-Portlet-Portlet-Mode", this.mode);
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
  req.setRequestHeader("X-Portlet-Window-State", this.state);
  req.send("");
  if (req.readyState == 4 && req.status == 200) {
    // Process headers.
    var setprefs = req.getResponseHeader("X-Portlet-Set-Preference");
    alert(setprefs);
    if (setprefs && setprefs.length > 0) {
      var prefs = setprefs.split(";");
      for (var i = 0; i < prefs.length; i++) {
        var pair = prefs[i].trim().split("=");
        if (pair.length == 2) {
          this.setUserProperty(pair[0], pair[1], false);
        }
        else if (pair.length == 1) {
          this.setUserProperty(pair[0], null, false);
        }
      }
      this.saveUserProperties();
    }
  }
  // X-Portlet-Set-Portlet-Mode
  // X-Portlet-Set-Title
  // X-Portlet-Set-Window-State
  // X-Portlet-Set-Property
  // X-Portlet-Add-Property
  // X-Portlet-Send-Redirect
  // X-Portlet-Set-Render-Parameter
  this.render(mode,state,secure,params);
};

GimletHandlerObject.prototype.doProxy =
function (method, params) {  
  // Get content.
  var req = new XMLHttpRequest();
  var url = this.getResource("snoop.jsp");
  req.open(method, params.length > 0 ? url+"?"+params : url , false);
  req.setRequestHeader("X-Portlet-Method", "render");
  req.setRequestHeader("X-Portlet-Auth-Type", "password");
  req.setRequestHeader("X-Portlet-Context-Path", this.getResource(""));
  req.setRequestHeader("X-Portlet-Locale", navigator.language);
  req.setRequestHeader("X-Portlet-Namespace", "");
  req.setRequestHeader("X-Portlet-Portlet-Mode", this.mode);
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
  req.setRequestHeader("X-Portlet-Window-State", this.state);
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
            e.setAttribute(attrs[k], 
                 this.resolveURI(a, 'http://',  'test'));
          }
        }
      }
    }
    // Process forms.
    var forms = div.getElementsByTagName("FORM");
    for (var i = 0; i < forms.length; i++) {
      var form = forms[i];
      if (!form.hasAttribute("ID"))
        form.setAttribute("ID", this.namespace+"_form_"+i);
      var id = form.getAttribute("ID");
      if (form.hasAttribute("ACTION")) {
        var action = form.getAttribute("ACTION");
        form.setAttribute("ONSUBMIT", this.resolveURI(action, 'http://', id));
      }
    }
  }
}

GimletHandlerObject.prototype.init = 
function () {
  this.id = GimletHandlerObject.nextId++;
  GimletHandlerObject.registry[this.id] = this;
};

GimletHandlerObject.nextId = 0;
GimletHandlerObject.registry = [];

function render (id,mode,state,secure,params,formId) {
  if (formId) {
    var data = serialize_form(formId);
    if (params.length > 0)
      params += "&";
    params += data;
  }
  GimletHandlerObject.registry[id].render(mode,state,secure,params);
}

function processAction (id,mode,state,secure,params,formId) {
  if (formId) {
    var data = serialize_form(formId);
    if (params.length > 0)
      params += "&";
    params += data;
  }
  GimletHandlerObject.registry[id].processAction(mode,state,secure,params);
}

function serialize_form(id) {
  var form = document.getElementById(id);
  var inputs = form.getElementsByTagName("INPUT");
  var params = [];
  for (var i = 0; i < inputs.length; i++) {
    var input = inputs[i];
    var name = input.name;
    var value = input.value;
    params.push(name + "=" + value);
  }
  return params.join("&");
}
