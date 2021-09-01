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

import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.capmovie.models.MovieRegCast
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.capmovie.views.html.MovieCast
import uk.gov.hmrc.capmovie.views.html.MovieCastConfirmation
import uk.gov.hmrc.capmovie.controllers.predicates.Login

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieCastController @Inject()(repo: SessionRepo,
                                    mcc: MessagesControllerComponents,
                                    castPage: MovieCast,
                                    confirmPage: MovieCastConfirmation,
                                    login: Login
                                   )

  extends FrontendController(mcc) {

  def getMovieCast: Action[AnyContent] = Action async  { implicit request =>
    login.check { _ =>
      Future.successful(Ok(castPage(MovieRegCast.form.fill(MovieRegCast("")))))
    }
  }

  def getConfirmationPage: Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
    repo.readOne(request.session.get("adminId").getOrElse("")).map { x =>
     Ok(confirmPage(x.get.cast))
    }
  }}

  def submitMovieCast(): Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
    MovieRegCast.form.bindFromRequest().fold({
      formWithErrors => Future(BadRequest(castPage(formWithErrors)))
    }, { formData =>
      repo.addCast(id, formData.cast).map {
        case true =>   Redirect(routes.MovieCastController.getConfirmationPage())
        case false => Unauthorized("error")
      }.recover {
        case _ => InternalServerError
      }
    })
  }}
}
