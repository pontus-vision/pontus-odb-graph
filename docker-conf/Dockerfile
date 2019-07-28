FROM alpine as builder
RUN apk add git

RUN git clone  --depth 1 --single-branch  --branch master https://github.com/pontusvision/pontus-gdpr-graph.git


FROM alpine

COPY --from=builder  /pontus-gdpr-graph/conf /pontus-gdpr-graph/conf
COPY --from=builder  /pontus-gdpr-graph/bin /pontus-gdpr-graph/bin


