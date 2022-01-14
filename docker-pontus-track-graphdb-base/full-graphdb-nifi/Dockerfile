FROM pontusvisiongdpr/pontus-track-graphdb-base as builder

FROM openjdk:8-jre-alpine as final

# needed for fonts:
ENV LANG en_GB.UTF-8
RUN apk upgrade -q --no-cache && \
    apk add -q --no-cache \
      curl \
      bash \
      gettext \
      unzip \
      zip \ 
      ttf-dejavu   # fonts...



#RUN apt-get upgrade -q --no-cache 
#RUN apt-get add -q --no-cache \
#      bash \
#      gettext \
#      unzip \
#      zip  

RUN mkdir -p /opt/pontus-graphdb/graphdb-current/lib && \
    addgroup -g 1000 -S pontus && \
    adduser -u 1000  -S -G pontus pontus && \
    chown -R pontus /opt/pontus-graphdb/ 

COPY --from=builder --chown=pontus /opt/pontus-graphdb/graphdb-current/datadir /opt/pontus-graphdb/graphdb-current/datadir
#COPY --from=builder --chown=pontus /root/.m2/repository/pv-gdpr/pontus-gdpr-graph/1.2.0/pontus-gdpr-graph-1.2.0.jar /opt/pontus-graphdb/graphdb-current/lib/
COPY --from=builder --chown=pontus /opt/pontus-graphdb/graphdb-current/bin /opt/pontus-graphdb/graphdb-current/bin
COPY --from=builder --chown=pontus /opt/pontus-graphdb/graphdb-current/lib/log4j.properties /opt/pontus-graphdb/graphdb-current/lib/
COPY  --from=pontusvisiongdpr/pontus-graphdb-conf    /pontus-gdpr-graph/conf /opt/pontus-graphdb/graphdb-current/conf

RUN ln -s /tmp/libpostal.so /tmp/libpostal.so.1 || true && \
    ln -s /tmp/libjpostal_parser.so  /tmp/libjpostal_parser.so.1 || true  

RUN rm /opt/pontus-graphdb/graphdb-current/conf/i18n*
#COPY bin /opt/pontus-graphdb/graphdb-current/bin

#COPY conf /opt/pontus-graphdb/graphdb-current/conf
USER pontus
WORKDIR /opt/pontus-graphdb/graphdb-current
RUN mkdir /opt/pontus-graphdb/graphdb-current/db
CMD LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/tmp /opt/pontus-graphdb/graphdb-current/bin/pv-gdpr-graphdb.sh /opt/pontus-graphdb/graphdb-current/conf/gremlin-server-nifi.yml

VOLUME /opt/pontus-graphdb/graphdb-current/db
