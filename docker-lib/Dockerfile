FROM pontusvisiongdpr/pontus-jpostal-lib:1.13.2 as jpostal
FROM pontusvisiongdpr/pontus-natty-lib:1.13.2 as natty

FROM maven:3.6-jdk-8-alpine as builder
#RUN apk add git

COPY --from=jpostal /pontus-jpostal-datadir /pontus-odb-graph/jpostal/
COPY --from=jpostal /root/.m2 /root/.m2
COPY --from=natty /root/.m2 /root/.m2
COPY ./pom.xml /pontus-odb-graph/pom.xml

WORKDIR /pontus-odb-graph

#RUN git clone  --depth 1 --single-branch  --branch master https://github.com/pontusvision/pontus-odb-graph.git && \
RUN mvn dependency:resolve

COPY . /pontus-odb-graph/
ENV PATH /orientdb/bin:/home/lmartins/go/bin:/home/lmartins/.local/bin:/home/lmartins/go/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/mnt/c/Program Files/Common Files/Oracle/Java/javapath:/mnt/c/Windows/system32:/mnt/c/Windows:/mnt/c/Windows/System32/Wbem:/mnt/c/Windows/System32/WindowsPowerShell/v1.0/:/mnt/c/Windows/System32/OpenSSH/:/mnt/c/Program Files (x86)/GnuPG/bin:/mnt/c/Program Files/Git/cmd:/mnt/c/Program Files (x86)/NVIDIA Corporation/PhysX/Common:/mnt/c/Program Files/Docker/Docker/resources/bin:/mnt/c/ProgramData/DockerDesktop/version-bin:/mnt/c/Users/lmart/AppData/Local/Microsoft/WindowsApps:/mnt/c/Program Files/JetBrains/IntelliJ IDEA 2020.3.2/bin:/mnt/c/Program Files/sfdx/bin:/snap/bin:/home/lmartins/.local/bin:/home/lmartins/.yarn/bin:/mnt/c/Users/LeonardoMartins/go/bin/:/home/lmartins/go/src/github.com/lexicality/wsl-relay/scripts:/home/lmartins/.local/bin:/home/lmartins/.yarn/bin:/mnt/c/Users/LeonardoMartins/go/bin/:/home/lmartins/go/src/github.com/lexicality/wsl-relay/scripts
ENV LD_LIBRARY_PATH=:/tmp
#RUN mvn clean package install
#RUN mvn compile  dependency:copy-dependencies  -DincludeScope=runtime
RUN ln -s /tmp/libpostal.so /tmp/libpostal.so.1 || true && \
    ln -s /tmp/libjpostal_parser.so  /tmp/libjpostal_parser.so.1 || true && \
    ln -s /pontus-odb-graph /orientdb && \
    mvn install

RUN cd /root/.m2/repository && for i in *; do if [[ "com" == $i || "pv-gdpr" == $i ]]; then echo skip ; else rm -r $i; fi; done

FROM scratch

COPY --from=builder /pontus-odb-graph/target/classes/ /pontus-odb-graph/
COPY --from=builder /pontus-odb-graph/target/dependency/* /pontus-odb-graph/lib/
COPY --from=builder /root/.m2/ /root/.m2/


