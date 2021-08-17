// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/conf/testOnlyDoNotUseInAppConf.routes
// @DATE:Tue Aug 17 08:50:55 BST 2021

package testOnlyDoNotUseInAppConf

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:13
  prod_Routes_0: prod.Routes,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:13
    prod_Routes_0: prod.Routes
  ) = this(errorHandler, prod_Routes_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    testOnlyDoNotUseInAppConf.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, prod_Routes_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    prefixed_prod_Routes_0_0.router.documentation,
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:13
  private[this] val prefixed_prod_Routes_0_0 = Include(prod_Routes_0.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + ""))


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:13
    case prefixed_prod_Routes_0_0(handler) => handler
  }
}
