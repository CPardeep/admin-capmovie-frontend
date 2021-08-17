
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
/*1.2*/import uk.gov.hmrc.agentsregfrontend.config.AppConfig
/*2.2*/import views.html.helper.CSPNonce

/**/
class Layout @javax.inject.Inject() /*4.6*/(
        appConfig: AppConfig,
        govukLayout: GovukLayout,
        hmrcHead: HmrcHead,
        hmrcStandardHeader: HmrcStandardHeader,
        hmrcStandardFooter: HmrcStandardFooter,
        hmrcScripts: HmrcScripts,
        hmrcLanguageSelectHelper: HmrcLanguageSelectHelper
) extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template6[Option[String],Option[Html],Option[Html],Html,Request[_$1] forSome { 
   type _$1
},Messages,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*13.2*/(pageTitle: Option[String] = None,
  headBlock: Option[Html] = None,
  scriptsBlock: Option[Html] = None
)(contentBlock: Html)(implicit request: Request[_], messages: Messages):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*17.1*/("""
"""),_display_(/*18.2*/govukLayout(
    pageTitle = pageTitle,
    headBlock = Some(hmrcHead(headBlock = headBlock, nonce = CSPNonce.get)),
    headerBlock = Some(hmrcStandardHeader()),
    scriptsBlock = Some(hmrcScripts(scriptsBlock = scriptsBlock, nonce = CSPNonce.get)),
    beforeContentBlock = if(appConfig.welshLanguageSupportEnabled) Some(hmrcLanguageSelectHelper()) else None,
    footerBlock = Some(hmrcStandardFooter())
)/*25.2*/(contentBlock)),format.raw/*25.16*/("""
"""))
      }
    }
  }

  def render(pageTitle:Option[String],headBlock:Option[Html],scriptsBlock:Option[Html],contentBlock:Html,request:Request[_$1] forSome { 
   type _$1
},messages:Messages): play.twirl.api.HtmlFormat.Appendable = apply(pageTitle,headBlock,scriptsBlock)(contentBlock)(request,messages)

  def f:((Option[String],Option[Html],Option[Html]) => (Html) => (Request[_$1] forSome { 
   type _$1
},Messages) => play.twirl.api.HtmlFormat.Appendable) = (pageTitle,headBlock,scriptsBlock) => (contentBlock) => (request,messages) => apply(pageTitle,headBlock,scriptsBlock)(contentBlock)(request,messages)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: 2021-08-17T08:50:56.174289
                  SOURCE: /Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/app/uk/gov/hmrc/agentsregfrontend/views/Layout.scala.html
                  HASH: 6fde801aa9d7d0e6712c60609445230ff59a09bd
                  MATRIX: 682->1|743->56|826->96|1488->382|1759->559|1787->561|2204->970|2239->984
                  LINES: 21->1|22->2|25->4|38->13|46->17|47->18|54->25|54->25
                  -- GENERATED --
              */
          