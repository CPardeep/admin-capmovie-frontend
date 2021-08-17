// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/conf/testOnlyDoNotUseInAppConf.routes
// @DATE:Tue Aug 17 08:50:55 BST 2021


package testOnlyDoNotUseInAppConf {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
