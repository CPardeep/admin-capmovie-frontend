// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/conf/prod.routes
// @DATE:Tue Aug 17 08:50:55 BST 2021

package com.kenshoo.play.metrics;

import prod.RoutesPrefix;

public class routes {
  
  public static final com.kenshoo.play.metrics.ReverseMetricsController MetricsController = new com.kenshoo.play.metrics.ReverseMetricsController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final com.kenshoo.play.metrics.javascript.ReverseMetricsController MetricsController = new com.kenshoo.play.metrics.javascript.ReverseMetricsController(RoutesPrefix.byNamePrefix());
  }

}
