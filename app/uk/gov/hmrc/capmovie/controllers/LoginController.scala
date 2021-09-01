/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.capmovie.controllers

import play.api.data.Form
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.capmovie.connectors.MovieConnector
import uk.gov.hmrc.capmovie.models.{Admin, AdminForm}
import uk.gov.hmrc.capmovie.views.html.LoginPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController @Inject()(mcc: MessagesControllerComponents,
                                movieConnector: MovieConnector,
                                login: LoginPage)
  extends FrontendController(mcc) {

  def getLoginPage: Action[AnyContent] = Action { implicit request =>
    val form: Form[Admin] = AdminForm.form.fill(Admin(id = "", password = ""))
    Ok(login(form))
  }

  def submitLogin: Action[AnyContent] = Action.async { implicit request =>
    AdminForm.form.bindFromRequest().fold({formWithErrors =>
      Future.successful(BadRequest(login(formWithErrors)))
    }, success => {
      movieConnector.login(success).map {
        case 200 => Redirect(routes.HomeController.homePage()).withSession("adminId" -> success.id)
        case 401 => Unauthorized(login(AdminForm.form.fill(Admin("", ""))))
      }.recover {
        case _ =>  InternalServerError
      }
    })
  }

  def logout(): Action[AnyContent] = Action {
    Redirect(routes.LoginController.getLoginPage()).withNewSession
  }


}
