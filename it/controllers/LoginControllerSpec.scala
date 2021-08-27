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

package controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, SEE_OTHER, UNAUTHORIZED}
import play.api.test.Helpers.{defaultAwaitTimeout, session, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.connectors.MovieConnector
import uk.gov.hmrc.capmovie.controllers.LoginController
import uk.gov.hmrc.capmovie.views.html.LoginPage

import scala.concurrent.Future

class LoginControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val login: LoginPage = app.injector.instanceOf[LoginPage]
  val connector: MovieConnector = mock[MovieConnector]
  val controller = new LoginController(Helpers.stubMessagesControllerComponents(), connector, login)


  "getLoginPage" should {
    "load the page when called" in {
      val result = controller.getLoginPage(FakeRequest("GET", "/"))
      status(result) shouldBe OK
    }
  }

  "submitLoginPage" should {
    "return redirect if login is successful" in {
      when(connector.login(any())).thenReturn(Future.successful(200))
      val testLogin = FakeRequest("POST", "/").withFormUrlEncodedBody("id" -> "TESTID", "password" -> "TESTPASS")
      val result = controller.submitLogin(testLogin)
      status(result) shouldBe SEE_OTHER
      session(result).get("adminId") shouldBe Some("TESTID")
    }
    "return Unauthorized if the login details are incorrect" in {
      when(connector.login(any())).thenReturn(Future.successful(401))
      val testLogin = FakeRequest("POST", "/").withFormUrlEncodedBody("id" -> "testId", "password" -> "wrongPass")
      val result = controller.submitLogin(testLogin)
      status(result) shouldBe UNAUTHORIZED
    }
    "return BadRequest if the fields are left empty" in {
      val testLogin = FakeRequest("POST", "/").withFormUrlEncodedBody("id" -> "", "password" -> "")
      val result = controller.submitLogin(testLogin)
      status(result) shouldBe BAD_REQUEST
    }
    "return InternalServerError if it cannot reach the database" in {
      when(connector.login(any())).thenReturn(Future.failed(new RuntimeException))
      val testLogin = FakeRequest("POST", "/").withFormUrlEncodedBody("id" -> "testId", "password" -> "wrongPass")
      val result = controller.submitLogin(testLogin)
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

}
