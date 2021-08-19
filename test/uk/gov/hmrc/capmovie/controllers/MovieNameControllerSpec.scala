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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieName

class MovieNameControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val namePage: MovieName = app.injector.instanceOf[MovieName]
  val controller = new MovieNameController(repo, Helpers.stubMessagesControllerComponents(), namePage)

  "getMovieName" should {
    "load the page when called" in {
      val result = controller.getMovieName(FakeRequest("GET", "/"))
      status(result) shouldBe OK
    }
  }
  "submitMovieName" should {
    "return a form value" when {
      "the form is submitted" in {
        val result = controller.submitMovieName().apply(FakeRequest("POST", "/").withFormUrlEncodedBody("name" -> "testName"))
        status(result) shouldBe OK
      }

    }
    "return a bad request" when {
      "the form is submitted" in {
        val result = controller.submitMovieName().apply(FakeRequest("POST", "/").withFormUrlEncodedBody("name" -> ""))
        status(result) shouldBe BAD_REQUEST
      }

    }
  }
}
