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
import uk.gov.hmrc.capmovie.controllers.MovieGenresController
import uk.gov.hmrc.capmovie.models.MovieReg
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.{MovieGenres, MovieGenresConfirmation}
import uk.gov.hmrc.capmovie.controllers.predicates.Login

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class MovieGenresControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val genresPage: MovieGenres = app.injector.instanceOf[MovieGenres]
  val genresConfirmationPage: MovieGenresConfirmation = app.injector.instanceOf[MovieGenresConfirmation]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MovieGenresController(repo, Helpers.stubMessagesControllerComponents(), genresPage, genresConfirmationPage, login)

  "getMovieGenres" should {
    "load the genres page when called" in {
      val result = controller.getMovieGenres(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }
  "submitMovieGenres" should {
    "return a form value" when {
      "the form is submitted with valid details" in {
        when(repo.addGenres(any(), any())).thenReturn(Future(true))
        val result = controller.submitMovieGenres().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> "TestGenre"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "return a bad request" when {
      "no form value is submitted" in {
        val result = controller.submitMovieGenres().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "return Unauthorized" when {
      "adminId not valid" in {
        when(repo.addGenres(any(), any())).thenReturn(Future(false))
        val result = controller.submitMovieGenres().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> "TestGenre"))
        status(result) shouldBe UNAUTHORIZED
      }
    }
    "return InternalServerError" in {
        when(repo.addGenres(any(), any())).thenReturn(Future.failed(new RuntimeException))
        val result = controller.submitMovieGenres().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> "TestGenre"))
        status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getConfirmationPage" should {
    "load genres confirmation page" in {
      when(repo.readOne(any()))
        .thenReturn(Future.successful(Some(MovieReg("testId", genres = List("genre1", "genre2")))))
      val result = controller.getConfirmationPage(FakeRequest("GET", "/").withSession("adminId" -> "testId"))
      status(result) shouldBe OK
    }
  }


}
