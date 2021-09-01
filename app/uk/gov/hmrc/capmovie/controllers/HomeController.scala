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
import uk.gov.hmrc.capmovie.connectors.MovieConnector
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.capmovie.views.html.HomePage
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class HomeController @Inject()(mcc: MessagesControllerComponents,
                               home: HomePage,
                               connector: MovieConnector,
                               login: Login)
  extends FrontendController(mcc) {

  def homePage: Action[AnyContent] = Action async { implicit request =>
    login.check { _ => connector.readAll().map(x => Ok(home(x)))
    }
  }

}
