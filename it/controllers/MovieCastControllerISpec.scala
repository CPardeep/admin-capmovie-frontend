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
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.controllers.MovieCastController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.MovieReg
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieCast
import uk.gov.hmrc.capmovie.views.html.MovieCastConfirmation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieCastControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val castPage: MovieCast = app.injector.instanceOf[MovieCast]
  val confirmPage: MovieCastConfirmation = app.injector.instanceOf[MovieCastConfirmation]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MovieCastController(repo, Helpers.stubMessagesControllerComponents(), castPage, confirmPage, login)

  "getMovieCast" should {
    "load the page when called" in {
      val result = controller.getMovieCast(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }
  "submitMovieCast" should {
    "return a form value" when {
      "the form is submitted" in {
        when(repo.addCast(any(), any())).thenReturn(Future(true))
        val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("cast" -> "testCast"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "return a bad request" when {
      "the form is submitted" in {
        val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("cast" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "return Unauthorised" in {
      when(repo.addCast(any(), any())).thenReturn(Future(false))
      val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("cast" -> "testCast"))
      status(result) shouldBe UNAUTHORIZED
    }

    "return InternalServerError" in {
      when(repo.addCast(any(), any())).thenReturn(Future.failed(new RuntimeException))
      val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("cast" -> "testCast"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
    "getConfirmationPage" should {
      "load MovieCastConfirmation Page" in {
        when(repo.readOne(any()))
          .thenReturn(Future.successful(Some(MovieReg("testId", cast =  List("actor1", "actor2")))))
        val result = controller.getConfirmationPage(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
        status(result) shouldBe OK
      }
    }
    "deleteCast" should {
      "redirect if cast members are successfully removed" when {
        "cast list not empty" in {
          when(repo.removeCast(any(), any()))
            .thenReturn(Future(true))
          when(repo.readOne(any()))
            .thenReturn(Future.successful(Some(MovieReg("testId", cast = List("actor1", "actor2")))))
          val result = controller.deleteCast("actor1").apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).get shouldBe "/capmovie/movie-cast/confirmation"
        }
        "cast list is empty" in {
          when(repo.removeCast(any(), any()))
            .thenReturn(Future(true))
          when(repo.readOne(any()))
            .thenReturn(Future.successful(Some(MovieReg("testId", cast = List()))))
          val result = controller.deleteCast("actor1").apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).get shouldBe "/capmovie/movie-cast"
        }
      }
      "return bad request when admin attempts to delete invalid cast member" in {
        when(repo.removeCast(any(), any()))
          .thenReturn(Future(false))
        when(repo.readOne(any()))
          .thenReturn(Future.successful(None))
        val result = controller.deleteCast("").apply(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
  }
}
