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
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.controllers.MovieRatingController
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieRating

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieRatingControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val ageRatingPage: MovieRating = app.injector.instanceOf[MovieRating]
  val controller = new MovieRatingController(repo, Helpers.stubMessagesControllerComponents(), ageRatingPage)

  "getMovieAgeRating" should {
    "load the page when called" in {
      val result = controller.getAgeRating(FakeRequest("GET", "/"))
      status(result) shouldBe OK
    }
  }
  "submitMovieAgeRating" should {
    "return a form value" when {
      "the form is submitted" in {
        when(repo.addAgeRating(any(), any())).thenReturn(Future(true))
        val result = controller.submitAgeRating().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("rated" -> "testRating"))
        status(result) shouldBe SEE_OTHER
      }
    }
  }
  "return a bad request" when {
    "the form is submitted" in {
      val result = controller.submitAgeRating().apply(FakeRequest("POST", "/")
        .withFormUrlEncodedBody("rated" -> ""))
      status(result) shouldBe BAD_REQUEST
    }
  }
  "return Unauthorised" in {
    when(repo.addAgeRating(any(), any())).thenReturn(Future(false))
    val result = controller.submitAgeRating().apply(FakeRequest("POST", "/")
      .withSession("adminId" -> "TESTID")
      .withFormUrlEncodedBody("rated" -> "testRating"))
    status(result) shouldBe UNAUTHORIZED
  }
  "return InternalServerError" in {
    when(repo.addAgeRating(any(), any())).thenReturn(Future.failed(new RuntimeException))
    val result = controller.submitAgeRating().apply(FakeRequest("POST", "/")
      .withSession("adminId" -> "TESTID")
      .withFormUrlEncodedBody("rated" -> "testRating"))
    status(result) shouldBe INTERNAL_SERVER_ERROR
  }
}
