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

package repo

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.capmovie.models.MovieReg
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

class SessionRepoISpec extends AnyWordSpec with Matchers with ScalaFutures with DefaultPlayMongoRepositorySupport[MovieReg] {

  override val repository = new SessionRepo(mongoComponent)

  "Create" should {
    "return true when valid details are submitted" in {
      await(repository.create(MovieReg("testId"))) shouldBe true
    }
    "return false when duplicate details are submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.create(MovieReg("testId"))) shouldBe false
    }
  }
  "addTitle" should {
    "return true when valid details are submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addTitle("testId", "TheMovie")) shouldBe true
    }
    "return false when wrong ID is submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addTitle("falseId", "TheMovie")) shouldBe false
    }
  }

  "addPoster" should {
    "return true when valid details are submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addPoster("testId", "posterURL")) shouldBe true
    }
    "return false when wrong ID is submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addPoster("falseId", "posterURL")) shouldBe false
    }
  }


  "addPlot" should {
    "return true when plot details have been submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addPlot("testId", "The Plot")) shouldBe true
    }
    "return false when the wrong ID is submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addPlot("falseId", "The Plot")) shouldBe false
    }
  }

  "addAgeRating" should {
    "return true when valid details are submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addAgeRating("testId", "testRating")) shouldBe true
    }
    "return false when wrong ID is submitted" in {
      await(repository.create(MovieReg("testId")))
      await(repository.addAgeRating("falseId", "testRating")) shouldBe false

    }

  }
}
