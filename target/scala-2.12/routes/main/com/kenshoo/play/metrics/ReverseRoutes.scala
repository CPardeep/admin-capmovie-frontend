// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/conf/prod.routes
// @DATE:Tue Aug 17 08:50:55 BST 2021

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:5
package com.kenshoo.play.metrics {

  // @LINE:5
  class ReverseMetricsController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:5
    def metrics(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "admin/metrics")
    }
  
  }


}
