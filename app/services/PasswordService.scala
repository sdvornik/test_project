package services

import akka.Done
import cats.data.EitherT
import com.google.inject.{Inject, Singleton}
import controllers.model.{
  WebPasswordRow,
  WebPasswordRowCreate,
  WebPasswordRowUpdate
}
import dao.PasswordDao
import shared.PasswordRowKey

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PasswordService @Inject() (dao: PasswordDao)(implicit
    ec: ExecutionContext
) {
  def create(
      row: WebPasswordRowCreate
  ): Future[Either[Throwable, PasswordRowKey]] = {
    dao.create(row.toDb)
  }

  def createList(
      rows: Seq[WebPasswordRowCreate]
  ): Future[Either[Throwable, Done]] = {
    dao.createList(rows.map(_.toDb))
  }

  def update(
      update: WebPasswordRowUpdate
  ): Future[Either[Throwable, WebPasswordRow]] = {
    EitherT(dao.update(update.toDb)).map(WebPasswordRow(_)).value
  }

  def delete(id: PasswordRowKey): Future[Either[Throwable, Done]] = {
    dao.delete(id)
  }

  def findById(
      id: PasswordRowKey
  ): Future[Either[Throwable, Option[WebPasswordRow]]] = {
    EitherT(dao.findById(id)).map(_.map(WebPasswordRow(_))).value
  }

  def findByName(
      name: String
  ): Future[Either[Throwable, Seq[WebPasswordRow]]] = {
    EitherT(dao.findByName(name)).map(_.map(WebPasswordRow(_))).value
  }
}
