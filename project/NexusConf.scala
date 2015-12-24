import sbt._
import Keys._

object NexusConf {
  val nexus_ip       = "121.199.26.84"
  val nexus_url      = s"http://$nexus_ip:8081/nexus/"
  val publishSetting = Option apply Seq(
    credentials += Credentials("Sonatype Nexus Repository Manager", nexus_ip, "deployment", "Wecash0509"),
    publishTo := Some("releases" at (nexus_url + "content/repositories/snapshots"))
  )
  val nexusResolvers = Option apply Seq(
    credentials += Credentials("Sonatype Nexus Repository Manager", nexus_ip, "deployment", "Wecash0509"),
    resolvers += "localhost" at s"$nexus_url/content/groups/public/"
  )

}