import sbt._

class ContainerManagedDispatchers(info: ProjectInfo) extends DefaultProject(info) with AkkaProject{


	lazy val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
        lazy val scala_tools_releases = "scala-tools.releases" at "http://scala-tools.org/repo-releases"
        lazy val scala_tools_snapshots = "scala-tools.snapshots" at "http://scala-tools.org/repo-snapshots"
        lazy val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
        lazy val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"


	lazy val containerManagedDispatchers = project("container-managed-dispatchers", "container-managed-dispatchers", new ContainerManagedDispathersCore(_))   
        lazy val glassfishManagedDispatchersExample = project("glassfish-managed-dispatchers-example", "glassfish-managed-dispatchers-example", new GlassfishExample(_), containerManagedDispatchers)

 
        class ContainerManagedDispathersCore(info: ProjectInfo) extends DefaultProject(info) with AkkaProject{
  		lazy val specs_2_8_0 = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test"
		lazy val spring_3_0_5 = "org.springframework" % "spring-tx" % "3.0.5.RELEASE"
		lazy val jee6_api = "javax" % "javaee-api" % "6.0" % "provided"		
	}

	class GlassfishExample(info: ProjectInfo) extends DefaultWebProject(info) with AkkaProject{
                lazy val scalatra = "org.scalatra" % "scalatra_2.8.0" % "2.0.0.M1"
        }

}