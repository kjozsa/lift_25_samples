java -Xmx1024m -Xss2m -XX:MaxPermSize=512m -XX:+CMSClassUnloadingEnabled -jar `dirname $0`/sbt-launch.jar "$@"
