function GimletHandlerObject() {}
GimletHandlerObject.prototype = new ZmZimletBase();
GimletHandlerObject.prototype.constructor = GimletHandlerObject;

GimletHandlerObject.prototype.singleClicked =
function(canvas) {
  // Set up the dialog.
  var view = new DwtComposite(this.getShell());
  view.setSize(320, 480);
  var title = this.getMessage("gimletName");
  canvas = this._createDialog({view: view, title: title});
  canvas.view = view;
  // Get content.
  var req = new XMLHttpRequest();
  req.open("GET", this.getResource("snoop.jsp"), false);
  req.setRequestHeader("X-Portlet-Method", "render");
  req.setRequestHeader("X-Portlet-Auth-Type", "password");
  req.setRequestHeader("X-Portlet-Context-Path", this.getResource(""));
  req.setRequestHeader("X-Portlet-Locale", navigator.language);
  req.setRequestHeader("X-Portlet-Namespace", "");
  req.setRequestHeader("X-Portlet-Portlet-Mode", "view");
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
  req.setRequestHeader("X-Portlet-Window-State", "normal");
  req.send("");
  if (req.readyState == 4 && req.status == 200) {
    var div = document.createElement("div");
    div.style.width = 320;
    div.style.height = 480;
    div.style.overflow = 'scroll';
    view.getHtmlElement().appendChild(div);
    div.innerHTML = req.responseText;
  }
  canvas.popup(); 
  // Display content.
//  var view = new DwtComposite(this.getShell());
//  view.setSize(Dwt.DEFAULT, Dwt.DEFAULT);
//  var title = this.getMessage("gimletName");
//  canvas = this._createDialog({view: view, title: title});
//  canvas.view = view;
//  canvas.popup(); 
  return canvas;
};

