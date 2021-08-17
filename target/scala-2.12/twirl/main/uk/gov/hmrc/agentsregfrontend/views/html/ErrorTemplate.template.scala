
package uk.gov.hmrc.agentsregfrontend.views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.helpers._
import uk.gov.hmrc.hmrcfrontend.views.html.components._
import uk.gov.hmrc.hmrcfrontend.views.html.helpers._

/**/
class ErrorTemplate @javax.inject.Inject() /*1.6*/(layout: Layout) extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template5[String,String,String,Request[_$1] forSome { 
   type _$1
},Messages,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*3.2*/(pageTitle: String, heading: String, message: String)(implicit request: Request[_], messages: Messages):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*4.2*/layout(pageTitle = Some(pageTitle))/*4.37*/ {_display_(Seq[Any](format.raw/*4.39*/("""
    """),format.raw/*5.5*/("""<h1 class="govuk-heading-xl">"""),_display_(/*5.35*/{Text(heading).asHtml}),format.raw/*5.57*/("""</h1>
    <p class="govuk-body">"""),_display_(/*6.28*/{Text(message).asHtml}),format.raw/*6.50*/("""</p>
""")))}),format.raw/*7.2*/("""
"""))
      }
    }
  }

  def render(pageTitle:String,heading:String,message:String,request:Request[_$1] forSome { 
   type _$1
},messages:Messages): play.twirl.api.HtmlFormat.Appendable = apply(pageTitle,heading,message)(request,messages)

  def f:((String,String,String) => (Request[_$1] forSome { 
   type _$1
},Messages) => play.twirl.api.HtmlFormat.Appendable) = (pageTitle,heading,message) => (request,messages) => apply(pageTitle,heading,message)(request,messages)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2021-08-17T08:50:56.193392
                  SOURCE: /Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/app/uk/gov/hmrc/agentsregfrontend/views/ErrorTemplate.scala.html
                  HASH: 5f1a3ea670a559af2cb5526f157c0dce9cefcd7a
                  MATRIX: 731->5|1099->24|1296->129|1339->164|1378->166|1409->171|1465->201|1507->223|1566->256|1608->278|1643->284
                  LINES: 23->1|28->3|33->4|33->4|33->4|34->5|34->5|34->5|35->6|35->6|36->7
                  -- GENERATED --
              */
          