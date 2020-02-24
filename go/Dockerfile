FROM golang:alpine

RUN apk add tzdata
ENV TZ=Asia/Tokyo

USER root
  
ADD . /go/src/
 
EXPOSE 8080
CMD ["/usr/local/go/bin/go", "run", "/go/src/server.go"]
