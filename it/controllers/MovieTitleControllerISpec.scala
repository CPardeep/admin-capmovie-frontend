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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, SEE_OTHER, UNAUTHORIZED}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.controllers.MovieTitleController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieTitle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieTitleControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val namePage: MovieTitle = app.injector.instanceOf[MovieTitle]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MovieTitleController(repo, Helpers.stubMessagesControllerComponents(), namePage, login)

  "getMovieTitle" should {
    "load the page when called" in {
      when(repo.create(any())).thenReturn(Future(true))
      val result = controller.getMovieTitle(FakeRequest("GET", "/")
        .withSession("adminId" -> "1001"))
      status(result) shouldBe OK
    }
  }
  "submitMovieTitle" should {
    "return a form value" when {
      "the form is submitted" in {
        //when(repo.create(any())).thenReturn(Future(true))
        when(repo.addTitle(any(), any())).thenReturn(Future(true))
        val result = controller.submitMovieTitle().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("title" -> "testTitle"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "return a bad request" when {
      "the form is submitted with errors" in {
        val result = controller.submitMovieTitle().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("title" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }

    "return UnAuthorised" in {
      when(repo.addTitle(any(), any())).thenReturn(Future(false))
      val result = controller.submitMovieTitle().apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("title" -> "testTitle"))
      status(result) shouldBe UNAUTHORIZED
    }

    "returns InternalServerError" in {
      when(repo.addTitle(any(), any())).thenReturn(Future.failed(new RuntimeException))
      val result = controller.submitMovieTitle().apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("title" -> "testTitle"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
