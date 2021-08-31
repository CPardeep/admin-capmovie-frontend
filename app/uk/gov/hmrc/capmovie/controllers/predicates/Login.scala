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

package uk.gov.hmrc.capmovie.controllers.predicates

import play.api.mvc.{AnyContent, MessagesControllerComponents, MessagesRequest, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.capmovie.controllers.routes
import scala.util.{Failure, Success, Try}

class Login @Inject()(mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  def check(func: (String) => Future[Result])(implicit request: MessagesRequest[AnyContent]): Future[Result] = {
    Try {
      request.session.get("adminId").get
    } match {
      case Success(value) => func(value)
      case Failure(_) => Future(Redirect(routes.LoginController.getLoginPage()))
    }

  }

}


