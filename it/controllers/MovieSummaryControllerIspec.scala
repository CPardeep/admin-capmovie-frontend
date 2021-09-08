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

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.connectors.MovieConnector
import uk.gov.hmrc.capmovie.controllers.MovieSummaryController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.MovieReg
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.{MovieSummary, SubmissionConfirmation}

import scala.concurrent.Future

class MovieSummaryControllerIspec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val login: Login = app.injector.instanceOf[Login]
  val summaryPage: MovieSummary = app.injector.instanceOf[MovieSummary]
  val movieConfirmationPage: SubmissionConfirmation = app.injector.instanceOf[SubmissionConfirmation]
  val connector: MovieConnector = mock[MovieConnector]
  val controller = new MovieSummaryController(repo, Helpers.stubMessagesControllerComponents(), summaryPage, connector, login, movieConfirmationPage)
  val movieReg: MovieReg = MovieReg(
    adminId = "TESTID",
    plot = Some("Test plot"),
    genres = List(
      "testGenre1",
      "testGenre2"),
    rated = Some("testRating"),
    cast = List(
      "testPerson",
      "TestPerson"),
    poster = Some("testURL"),
    title = Some("testTitle"))

  "getSummary" should {
    "load the page when called" in {
      when(repo.readOne(any())).thenReturn(Future.successful(Some(movieReg)))
      val result = controller.getSummary(isSessionUpdate = false)(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }
  "submitSummary" should {
    "return redirect" in {
      when(repo.readOne(any())).thenReturn(Future.successful(Some(movieReg)))
      when(connector.create(any())).thenReturn(Future.successful(true))
      val result = controller.submitSummary(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe SEE_OTHER
    }
    "return a bad request" in {
      when(repo.readOne(any())).thenReturn(Future.successful(Some(movieReg)))
      when(connector.create(any())).thenReturn(Future.successful(false))
      val result = controller.submitSummary(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe BAD_REQUEST
    }
  }

  "confirmation" should {
    "return ok" in {
      val result = controller.confirmation(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }
}
