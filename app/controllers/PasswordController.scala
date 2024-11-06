package controllers

import akka.Done
import akka.util.ByteString
import cats.data.EitherT
import cats.implicits._
import com.github.tototoshi.csv.CSVReader
import controllers.model.{WebPasswordRowCreate, WebPasswordRowUpdate}
import play.api.http.Writeable
import play.api.libs.json._

import javax.inject._
import play.api.mvc._
import services.PasswordService
import shared.PasswordRowKey

import scala.concurrent.{ExecutionContext, Future}
import play.api.http.DefaultWriteables
import play.api.libs.Files
import play.mvc.Http.MimeTypes

import java.nio.file.{Path, Paths}

@Singleton
class PasswordController @Inject() (
    bodyParser: PlayBodyParsers,
    passwordService: PasswordService
)(implicit ec: ExecutionContext)
    extends InjectedController {

  implicit def writable[A: Writes]: Writeable[A] = new Writeable(
    a => ByteString(Json.stringify(Json.toJson(a))),
    MimeTypes.JSON.some
  )
  implicit val doneWritable: Writeable[Done] =
    new Writeable(_ => ByteString("Ok"), MimeTypes.JSON.some)

  def create: Action[WebPasswordRowCreate] =
    Action.async(bodyParser.json[WebPasswordRowCreate]) { r =>
      EitherT(passwordService.create(r.body))
        .fold(t => BadRequest(t.getMessage), Ok(_))
    }

  def createList: Action[Seq[WebPasswordRowCreate]] =
    Action.async(bodyParser.json[Seq[WebPasswordRowCreate]]) { r =>
      EitherT(passwordService.createList(r.body))
        .fold(t => BadRequest(t.getMessage), Ok(_))
    }

  def update: Action[WebPasswordRowUpdate] =
    Action.async(bodyParser.json[WebPasswordRowUpdate]) { r =>
      EitherT(passwordService.update(r.body))
        .fold(t => BadRequest(t.getMessage), Ok(_))
    }

  def delete(id: String): Action[AnyContent] = Action.async { _ =>
    EitherT(passwordService.delete(PasswordRowKey(id)))
      .fold(t => BadRequest(t.getMessage), Ok(_))
  }

  def findById(id: String): Action[AnyContent] = Action.async { _ =>
    EitherT(passwordService.findById(PasswordRowKey(id)))
      .fold(t => BadRequest(t.getMessage), Ok(_))
  }

  def findByName(name: String): Action[AnyContent] = Action.async { _ =>
    EitherT(passwordService.findByName(name))
      .fold(t => BadRequest(t.getMessage), Ok(_))
  }

  private def pathToFile(filename: Path): Path =
    Paths.get(s"/tmp/scv_files/$filename")

  def upload: Action[MultipartFormData[Files.TemporaryFile]] =
    Action(parse.multipartFormData).async { request =>
      (for {
        path <- EitherT.fromEither[Future](
          request.body
            .file("csv_file")
            .map { f =>
              val filename = Paths.get(f.filename).getFileName
              f.ref.copyTo(pathToFile(filename), replace = true)
              filename
            }
            .toRight(new NoSuchElementException())
        )
        rows = CSVReader
          .open(pathToFile(path).toFile)
          .allWithHeaders()
          .flatMap(WebPasswordRowCreate(_))
        _ <- EitherT(passwordService.createList(rows))
      } yield {
        Done
      }).fold(t => BadRequest(t.getMessage), Ok(_))
    }
}
