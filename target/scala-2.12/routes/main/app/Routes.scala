// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/conf/app.routes
// @DATE:Tue Aug 17 08:50:55 BST 2021

package app

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:3
  govuk_Routes_1: govuk.Routes,
  // @LINE:4
  hmrcfrontend_Routes_0: hmrcfrontend.Routes,
  // @LINE:6
  Assets_0: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:3
    govuk_Routes_1: govuk.Routes,
    // @LINE:4
    hmrcfrontend_Routes_0: hmrcfrontend.Routes,
    // @LINE:6
    Assets_0: controllers.Assets
  ) = this(errorHandler, govuk_Routes_1, hmrcfrontend_Routes_0, Assets_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    app.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, govuk_Routes_1, hmrcfrontend_Routes_0, Assets_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    prefixed_govuk_Routes_1_0.router.documentation,
    prefixed_hmrcfrontend_Routes_0_1.router.documentation,
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/""" + "$" + """file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:3
  private[this] val prefixed_govuk_Routes_1_0 = Include(govuk_Routes_1.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + "govuk-frontend"))

  // @LINE:4
  private[this] val prefixed_hmrcfrontend_Routes_0_1 = Include(hmrcfrontend_Routes_0.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + "hmrc-frontend"))

  // @LINE:6
  private[this] lazy val controllers_Assets_versioned2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned2_invoker = createInvoker(
    Assets_0.versioned(fakeValue[String], fakeValue[Asset]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "app",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      this.prefix + """assets/""" + "$" + """file<.+>""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:3
    case prefixed_govuk_Routes_1_0(handler) => handler
  
    // @LINE:4
    case prefixed_hmrcfrontend_Routes_0_1(handler) => handler
  
    // @LINE:6
    case controllers_Assets_versioned2_route(params@_) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned2_invoker.call(Assets_0.versioned(path, file))
      }
  }
}
