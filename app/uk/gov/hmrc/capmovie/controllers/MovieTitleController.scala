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
import uk.gov.hmrc.capmovie.models.{MovieReg, MovieRegTitle}
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieTitle
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieTitleController @Inject()(repo: SessionRepo,
                                     mcc: MessagesControllerComponents,
                                     titlePage: MovieTitle)
  extends FrontendController(mcc) {

  def getMovieTitle: Action[AnyContent] = Action { implicit request =>
    repo.create(MovieReg(adminId = request.session.get("adminId").getOrElse("")))
    Ok(titlePage(MovieRegTitle.form.fill(MovieRegTitle(""))))
  }

  def submitMovieTitle(): Action[AnyContent] = Action.async { implicit request =>
    MovieRegTitle.form.bindFromRequest().fold({
      formWithErrors =>
        Future(BadRequest(titlePage(formWithErrors)))
    }, { formData =>
      repo.addTitle(request.session.get("adminId").get, formData.title).map {
        case true => Redirect(routes.MoviePlotController.getMoviePlot())
        case false => Unauthorized("error")
      }.recover {
        case _ => InternalServerError
      }
    })
  }
}
