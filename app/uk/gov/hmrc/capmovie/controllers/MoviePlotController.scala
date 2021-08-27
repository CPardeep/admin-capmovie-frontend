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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.capmovie.models.MovieRegPlot
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MoviePlot
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MoviePlotController @Inject()(repo: SessionRepo,
                                    mcc: MessagesControllerComponents,
                                    plotPage: MoviePlot)
  extends FrontendController(mcc) {

  def getMoviePlot: Action[AnyContent] = Action { implicit request =>
    // repo.create(MovieReg(adminId = request.session.get("adminId").getOrElse("")))
    Ok(plotPage(MovieRegPlot.form.fill(MovieRegPlot(""))))
  }

  def submitMoviePlot: Action[AnyContent] = Action.async { implicit request =>
    MovieRegPlot.form.bindFromRequest().fold({ formWithErrors =>
      Future(BadRequest(plotPage(formWithErrors)))
    }, { formData =>
      repo.addPlot(request.session.get("adminId").getOrElse(""), formData.plot).map {
        case true => Redirect(routes.MoviePosterController.getMoviePoster())
        case false => Unauthorized("error")
      }.recover {
        case _ => InternalServerError
      }
    })
  }


}
