// WARNING: DO NOT EDIT!
// THIS FILE WAS GENERATED BY SHADOW-CLJS AND WILL BE OVERWRITTEN!

var ALL = {};
ALL["@grame/faustwasm"] = require("@grame/faustwasm");
global.shadow$bridge = function shadow$bridge(name) {
  var ret = ALL[name];

  if (ret === undefined) {
     throw new Error("Dependency: " + name + " not provided by external JS. Do you maybe need a recompile?");
  }

  return ret;
};

shadow$bridge.ALL = ALL;
