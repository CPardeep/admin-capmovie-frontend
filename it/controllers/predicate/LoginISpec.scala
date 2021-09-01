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

package controllers.predicate

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc.Results.Ok
import play.api.mvc.{AnyContent, MessagesControllerComponents, MessagesRequest, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import scala.concurrent.Future

class LoginISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val messages = app.injector.instanceOf[MessagesControllerComponents]
  private val controller = new Login(messages)

  def fakefunc(request: Request[AnyContent]) : Future[Result] = {
    controller.check(_ => Future.successful(Ok("test")))(new MessagesRequest[AnyContent](request, messages.messagesApi))
  }

  "check" should {
    "returns a function if agent is signed in" in {
      val result = fakefunc(FakeRequest("GET", "/").withSession("adminId" -> "test"))
      status(result) shouldBe OK
      contentAsString(result) shouldBe "test"
    }
    "return a redirect if agent is not signed in" in {
      val result = fakefunc(FakeRequest("GET", "/"))
      status(result) shouldBe SEE_OTHER
    }
  }

}
