FROM alpine as builder
RUN apk add git

#RUN git clone  --depth 1 --single-branch  --branch master https://github.com/pontus-vision/pontus-odb-graph.git
COPY . /pontus-odb-graph


FROM scratch

COPY --from=builder  /pontus-odb-graph/conf /pontus-odb-graph/conf
COPY --from=builder  /pontus-odb-graph/bin /pontus-odb-graph/bin


