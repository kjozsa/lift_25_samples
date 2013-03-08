java -Xmx2048m -Xss2m -XX:MaxPermSize=1024m -XX:+CMSClassUnloadingEnabled -jar `dirname $0`/sbt-launch.jar "$@"
