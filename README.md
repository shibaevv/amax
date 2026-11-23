# amax
    sdk list java
    sdk install java 11.0.31-tem
    sdk use java 11.0.31-tem

    mvn -v
    mvn clean install -Dtest -DfailIfNoTests=false

    mvn eclipse:clean eclipse:eclipse
    mvn dependency:sources
    mvn dependency:tree -DoutputFile=dependency.txt
    