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

package uk.gov.hmrc.capmovie.repo

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.Updates.{addToSet, pull, set}
import org.mongodb.scala.model.{Filters, IndexModel, IndexOptions}
import uk.gov.hmrc.capmovie.models.MovieReg
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionRepo @Inject()(mongoComponent: MongoComponent) extends PlayMongoRepository[MovieReg](
  collectionName = "SessionRepo",
  mongoComponent = mongoComponent,
  domainFormat = MovieReg.format,
  indexes = Seq(IndexModel(ascending("adminId"), IndexOptions().unique(true)))
) {

  def create(movieReg: MovieReg): Future[Boolean] = collection.insertOne(movieReg).toFuture().map {
    response => response.wasAcknowledged && !response.getInsertedId.isNull
  } recover { case _ => false }

  def addTitle(id: String, title: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal("adminId", id),
      set("title", title)
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }


  def addPoster(id: String, poster: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal("adminId", id),
      set("poster", poster)
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

  def addPlot(id: String, plot: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal("adminId", id),
      set("plot", plot)
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

  def addAgeRating(id: String, rating: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal("adminId", id),
      set("rated", rating)
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged()

    )
  }

  def clearSession(id: String): Future[Boolean] = {
    collection.deleteOne(
      Filters.equal("adminId", id)
    ).toFuture().map {
      response => response.wasAcknowledged && response.getDeletedCount == 1
    }
  }

  def addGenres(id: String, genre: String): Future[Boolean] = {
    collection.updateOne(Filters.equal("adminId", id), addToSet("genres", genre))
      .toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

  def removeGenre(id: String, genre: String): Future[Boolean] = {
    collection.updateOne(Filters.equal("adminId", id), pull("genres", genre))
      .toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

  def addCast(id: String, cast: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal("adminId", id),
      addToSet("cast", cast)
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

  def readOne(id: String): Future[Option[MovieReg]] = collection.find(equal("adminId", id)).headOption

  def removeCast(id: String, cast: String): Future[Boolean] = {
    collection.updateOne(
      Filters.equal("adminId", id),
      pull("cast", cast)
    ).toFuture().map(result => result.getModifiedCount == 1 && result.wasAcknowledged())
  }

}