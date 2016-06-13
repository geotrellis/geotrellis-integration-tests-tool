package geotrellis.util

import geotrellis.util.Colors._
import org.apache.log4j.{Logger, PatternLayout, WriterAppender}

import scala.collection.mutable
import java.io.{PrintWriter, StringWriter}

object LoggingSummary {
  @transient val speedMetricsBuffer = mutable.Map[String, mutable.ListBuffer[String]]()
  @transient val logBuffer          = mutable.Map[String, mutable.ListBuffer[String]]()

  def getStringAppender: (StringWriter, WriterAppender) = {
    val writer   = new StringWriter()
    val appender = new WriterAppender(new PatternLayout(), writer)
    (writer, appender)
  }

  def printSummary(logger: Logger, summaryName: String, filter: Option[String]) = {
    val buffer = filter.fold(logBuffer)(f => logBuffer filter (_._1.split("\\.").head == f))
    logger.info(green(s"\n${summaryName}:\n") +
      (buffer map { case (key, log) =>
        s"${grey(s"${key}:")}\n ${log mkString " "}"
      } mkString ""))
  }

  def stackTraceToString(e: Throwable): String = {
    val sw = new StringWriter()
    val pw = new PrintWriter(sw)
    e.printStackTrace(pw)
    sw.toString
  }
}

trait LoggingSummary {
  import LoggingSummary._

  @transient val logger: Logger

  def appendBuffer(id: String, line: String, buffer: mutable.Map[String, mutable.ListBuffer[String]]) = {
    val (writer, appender) = getStringAppender
    logger.addAppender(appender)
    logger.info(line)
    writer.flush()
    logger.removeAppender(appender)
    buffer.get(id).fold(
      buffer.update(id, mutable.ListBuffer(writer.toString))
    )(_ += writer.toString)
  }

  def withSpeedMetrics[T](id: String, append: Boolean = true)(f: => T): T = {
    val s = System.currentTimeMillis
    val result = f
    val e = System.currentTimeMillis
    val t = "%,d".format(e - s)

    if(append) {
      appendBuffer(id, cyan(s"Run completed in ${t} milliseconds"), speedMetricsBuffer)
      appendBuffer(id, cyan(s"Run completed in ${t} milliseconds"), logBuffer)
    }

    result
  }

  def appendLog(id: String, color: String => String = green(_), append: Boolean = true)(line: String) = appendBuffer(id, color(line), logBuffer)

  private def printBuffer(id: String, buffer: mutable.Map[String, mutable.ListBuffer[String]]) =
    buffer.get(id) foreach { _.foreach { line => logger.info(line) } }

  private def printBuffer(buffer: mutable.Map[String, mutable.ListBuffer[String]], prependString: String = "") =
    logger.info(s"${prependString}\n" + (buffer.toMap map { case (_, list) => list.mkString("\n") } mkString "\n"))

  def printSpeedMetricsSummary = if(speedMetricsBuffer.nonEmpty) printBuffer(speedMetricsBuffer, cyan("SpeedMetricsSummary:"))

  def printLoggingSummary = if(logBuffer.nonEmpty) printBuffer(logBuffer, green("LoggingSummary:"))

  def printSummary(summaryName: String = "Test Summary", filter: Option[String] = None) = LoggingSummary.printSummary(logger, summaryName, filter)
}
