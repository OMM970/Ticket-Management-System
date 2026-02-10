FROM ubuntu:latest
LABEL authors="omnar"

ENTRYPOINT ["top", "-b"]