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

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.controllers.MoviePlotController
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MoviePlot

class MoviePlotControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val plotPage: MoviePlot = app.injector.instanceOf[MoviePlot]
  val controller = new MoviePlotController(repo, Helpers.stubMessagesControllerComponents(), plotPage)

  "getMoviePlot" should {
    "load the page when called" in {
      val result = controller.getMoviePlot(FakeRequest("GET", "/"))
      status(result) shouldBe OK
    }
  }

  "submitMoviePlot" should {
    "return Ok" when {
      "the form value is submitted" in {
        val result = controller.submitMoviePlot().apply(FakeRequest("POST", "/")
          .withFormUrlEncodedBody("plot" -> "Test Plot"))
        status(result) shouldBe OK
      }
    }
    "return a bad request" when {
      "no form value is submitted" in {
        val result = controller.submitMoviePlot().apply(FakeRequest("POST", "/")
          .withFormUrlEncodedBody("plot" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }
  }
}
