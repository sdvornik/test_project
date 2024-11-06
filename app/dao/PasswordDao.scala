package dao

import akka.Done
import cats.implicits._
import com.google.inject.{Inject, Singleton}
import dao.model.{DbPasswordRow, DbPasswordRowUpdate}
import play.api.db.slick.DatabaseConfigProvider
import shared.{PasswordRowKey, UtcInstant}

import scala.concurrent.ExecutionContext

@Singleton
class PasswordDao @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(override implicit val executionContext: ExecutionContext)
    extends BaseDao[PasswordRowKey, DbPasswordRow, DbPasswordRowUpdate] {

  import profile.api._

  override protected val updater
      : (DbPasswordRow, DbPasswordRowUpdate) => DbPasswordRow =
    (prev, update) =>
      DbPasswordRow(
        id = prev.id,
        name = update.name.getOrElse(prev.name),
        password = update.password.getOrElse(prev.password),
        comment = update.comment.orElse(prev.comment),
        created = prev.created,
        deleted = prev.deleted
      )

  override protected def createQuery(
      row: DbPasswordRow
  ): DBIO[PasswordRowKey] = {
    (passwords += row).map(_ => row.id)
  }

  override protected def createListQuery(
      rows: Seq[DbPasswordRow]
  ): DBIO[Done] = {
    (passwords ++= rows).map(_ => Done)
  }

  override def updateQuery(update: DbPasswordRowUpdate): DBIO[DbPasswordRow] = {
    for {
      prev <- passwords.filter(_.p.id === update.id).result.head
      updated = updater(prev, update)
      _ <- passwords.filter(_.p.id === update.id).update(updated)
    } yield {
      updated
    }
  }

  override protected def deleteQuery(id: PasswordRowKey): DBIO[Done] = {
    passwords
      .filter(_.p.id === id)
      .map(_.p.deleted)
      .update(UtcInstant.now.some)
      .map(_ => Done)
  }

  override protected def findByIdQuery(
      id: PasswordRowKey
  ): DBIO[Option[DbPasswordRow]] = {
    passwords
      .filter(t => t.p.id === id && t.p.deleted.nonEmpty)
      .result
      .headOption
  }

  override protected def findByNameQuery(
      name: String
  ): DBIO[Seq[DbPasswordRow]] = {
    passwords
      .filter(t => t.p.name.ilike(name))
      .result
  }
}
