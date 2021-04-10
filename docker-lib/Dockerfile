FROM pontusvisiongdpr/pontus-jpostal-lib:1.13.2 as jpostal
FROM pontusvisiongdpr/pontus-natty-lib:1.13.2 as natty

FROM maven:3.6-jdk-8-alpine as builder
#RUN apk add git


COPY --from=jpostal /root/.m2 /root/.m2
COPY --from=natty /root/.m2 /root/.m2
COPY ./pom.xml /pontus-odb-graph/pom.xml

WORKDIR /pontus-odb-graph

#RUN git clone  --depth 1 --single-branch  --branch master https://github.com/pontusvision/pontus-odb-graph.git && \
RUN mvn dependency:resolve

COPY . /pontus-odb-graph
RUN mvn clean package install -DskipTests

RUN cd /root/.m2/repository && for i in *; do if [[ "com" == $i || "pv-gdpr" == $i ]]; then echo skip ; else rm -r $i; fi; done

FROM scratch

COPY --from=builder /root/.m2/ /root/.m2/


