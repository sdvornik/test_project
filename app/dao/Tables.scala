package dao

import dao.model.DbPasswordRow
import dao.model.lifted.LiftedDbPasswordRow
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import shared.{PasswordRowKey, UtcInstant}
import slick.jdbc.JdbcProfile
import slick.lifted

import java.sql.Timestamp
import scala.concurrent.ExecutionContext

trait Tables extends HasDatabaseConfigProvider[JdbcProfile] {
  protected val dbConfigProvider: DatabaseConfigProvider
  implicit val executionContext: ExecutionContext

  import profile.api._

  implicit val timeMapping: BaseColumnType[UtcInstant] =
    MappedColumnType.base[UtcInstant, Timestamp](
      t => new Timestamp(t.value),
      ts => UtcInstant(ts.getTime)
    )

  implicit val rowKeyMapping: BaseColumnType[PasswordRowKey] =
    MappedColumnType.base[PasswordRowKey, String](_.value, PasswordRowKey(_))

  implicit object PasswordRowShape
      extends CaseClassShape[
        Product,
        (
            Rep[PasswordRowKey],
            Rep[String],
            Rep[String],
            Rep[Option[String]],
            Rep[UtcInstant],
            Rep[Option[UtcInstant]]
        ),
        LiftedDbPasswordRow,
        (
            PasswordRowKey,
            String,
            String,
            Option[String],
            UtcInstant,
            Option[UtcInstant]
        ),
        DbPasswordRow
      ](LiftedDbPasswordRow.tupled, DbPasswordRow.tupled)

  implicit class ILikeImpl(private val s: Rep[String]) {
    def ilike(p: Rep[String]): Rep[Boolean] = {
      val expr = SimpleExpression.binary[String, String, Boolean] {
        (s, p, qb) =>
          qb.expr(s)
          qb.sqlBuilder += " ILIKE "
          qb.expr(p)
      }
      expr.apply(s, p)
    }
  }

  class Passwords(tag: Tag) extends Table[DbPasswordRow](tag, "passwords") {
    def p: LiftedDbPasswordRow = LiftedDbPasswordRow(
      id = column[PasswordRowKey]("id", O.PrimaryKey),
      name = column[String]("name"),
      password = column[String]("password"),
      comment = column[String]("password").?,
      created = column[UtcInstant]("created"),
      deleted = column[UtcInstant]("created").?
    )

    def * : lifted.ProvenShape[DbPasswordRow] = p
  }

  val passwords = lifted.TableQuery[Passwords]
}
