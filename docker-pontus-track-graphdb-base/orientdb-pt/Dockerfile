############################################################
# Dockerfile to run an OrientDB (Graph) Container
############################################################
ARG TAG=latest
FROM pontusvisiongdpr/pontus-track-graphdb-odb:1.13.2

MAINTAINER Pontus Vision LTD (info@pontusvision.com)

COPY --chown=1000:1000   --from=pontusvisiongdpr/pontus-i18n:latest /*.json  /orientdb/conf/

