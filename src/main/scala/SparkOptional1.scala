import org.apache.spark.{SparkConf, SparkContext}
import scala.util.matching.Regex
import org.apache.spark.sql.SparkSession

object LogAnalyzer {
  def main(args:Array[String]):Unit={
    val spark = SparkSession.builder()
        .appName("Log Analyzer")
        .master("local[*]")
        .config("spark.driver.bindAddress", "127.0.0.1")  
        .config("spark.driver.port", "7077")              
        .config("spark.ui.port", "4040")
        .getOrCreate()

    val logFile = "/Users/ronak/Downloads/access_log.txt"
    val logData = spark.read.textFile(logFile).rdd

    val logPattern: Regex = """^(\S+) - - \[\S+ \S+\] "\S+ (\S+) \S+" (\d{3}) (\d+|-)""".r
    

    val parsedLogs = logData.flatMap{
      case logPattern(ip, url, status, bytes) => Some((ip, url, status.toInt, if(bytes == "-") 0 else bytes.toInt))
      case _ => None
    }

    val uniqueIps = parsedLogs.map(_._1).distinct().collect()
    println("Unique Ips:")
    uniqueIps.foreach(println)

    val urlStatus200 = parsedLogs.filter(_._3 == 200).map(log => (log._1, 1)).reduceByKey(_ + _).collect()
    println("\nURLs with number of 200 status:")
    urlStatus200.foreach(println)

    // 3. Number of 4xx responses
    val response4xx = parsedLogs.filter { case (_, _, status, _) => status >= 400 && status < 500 }.count()
    println(s"Number of 4xx responses: $response4xx")

    // 4. Number of requests that sent more than 5000 bytes as response
    val largeResponses = parsedLogs.filter { case (_, _, _, bytes) => bytes > 5000 }.count()
    println(s"Number of requests with more than 5000 bytes sent: $largeResponses")

     // 5. URL with the most number of requests
    val mostRequestedUrl = parsedLogs.map(_._2).countByValue().maxBy(_._2)
    println(s"URL with the most number of requests: ${mostRequestedUrl._1} (${mostRequestedUrl._2})")

    // 6. URL with the most number of 404 responses
    val most404Url = parsedLogs.filter(_._3 == 404).map(_._2).countByValue().maxBy(_._2)
    println(s"URL with the most number of 404 responses: ${most404Url._1} (${most404Url._2})")


    spark.stop()
  }
}