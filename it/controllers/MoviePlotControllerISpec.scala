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
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, SEE_OTHER, UNAUTHORIZED}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.controllers.MoviePlotController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MoviePlot

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MoviePlotControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val plotPage: MoviePlot = app.injector.instanceOf[MoviePlot]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MoviePlotController(repo, Helpers.stubMessagesControllerComponents(), plotPage, login)

  "getMoviePlot" should {
    "load the page when called" in {
      val result = controller.getMoviePlot(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }

  "submitMoviePlot" should {
    "return Ok" when {
      "the form value is submitted" in {
        when(repo.addPlot(any(), any())).thenReturn(Future(true))
        val result = controller.submitMoviePlot().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("plot" -> "Test Plot"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "return a bad request" when {
      "no form value is submitted" in {
        val result = controller.submitMoviePlot().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("plot" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "return Unauthorised" in {
      when(repo.addPlot(any(), any())).thenReturn(Future(false))
      val result = controller.submitMoviePlot().apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("plot" -> "Test Plot"))
      status(result) shouldBe UNAUTHORIZED
    }

    "return InternalServerError" in {
      when(repo.addPlot(any(), any())).thenReturn(Future.failed(new RuntimeException))
      val result = controller.submitMoviePlot().apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("plot" -> "Test Plot"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
