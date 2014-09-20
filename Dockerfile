FROM ruby:2.1.2
MAINTAINER Tobias Gesellchen <tobias@gesellix.de>

RUN gem install travis --no-rdoc --no-ri

VOLUME /repo
WORKDIR /repo

CMD ["/usr/local/bin/travis"]
