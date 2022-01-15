FROM alpine as final
RUN apk upgrade -q --no-cache
RUN apk add -q --no-cache \
      bash \
      gettext \
      git \
      maven \
      openjdk8 \
      openjdk8-jre-base \
      openssl \
      unzip \
      zip 

RUN mkdir -p /opt/pontus-graphdb/graphdb-current/lib 
RUN mkdir -p /opt/pontus-graphdb/graphdb-current/bin 
RUN mkdir -p /opt/pontus-graphdb/graphdb-current/conf 
COPY --from=pontusvisiongdpr/pontus-jpostal-lib:1.13.2 /pontus-jpostal-datadir /opt/pontus-graphdb/graphdb-current/datadir
COPY --from=pontusvisiongdpr/pontus-graphdb-odb-lib:1.13.2 /root/.m2/ /root/.m2/
COPY --from=pontusvisiongdpr/pontus-graphdb-odb-conf:1.13.2  /pontus-odb-graph/bin /opt/pontus-graphdb/graphdb-current/bin
COPY log4j.properties /opt/pontus-graphdb/graphdb-current/lib
COPY --from=pontusvisiongdpr/pontus-graphdb-odb-conf:1.13.2    /pontus-odb-graph/conf /opt/pontus-graphdb/graphdb-current/conf

