FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER conttononline@outlook.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /ttiot-example

WORKDIR /ttiot-example

EXPOSE 8729

ADD ./ttiot-example/target/ttiot-example.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar ttiot-example.jar