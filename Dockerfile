# Self-contained test runner: JDK 17, Maven, Chromium and chromedriver.
# Nothing but Docker is needed on the host.
FROM maven:3.9.16-amazoncorretto-17-debian-trixie

# Chromium and chromedriver come from the same Debian repository, so their versions always match,
# no driver is downloaded at test time, and the image builds on amd64 and arm64 (Apple Silicon).
RUN apt-get update \
    && apt-get install -y --no-install-recommends chromium chromium-driver fonts-liberation \
    && rm -rf /var/lib/apt/lists/*

# Run as a non-root user. Pass --build-arg UID=$(id -u) so files written to a mounted
# reports folder belong to you.
ARG UID=1000
RUN useradd --create-home --uid "${UID}" tester
USER tester
WORKDIR /home/tester/app

# Dependencies first: this layer is rebuilt only when pom.xml changes.
COPY --chown=tester pom.xml ./
RUN mvn -B -q dependency:go-offline allure:install

# Compile and warm up the test runner without running any test, so `docker run` starts testing at once.
COPY --chown=tester src ./src
RUN mvn -B -q test -Dtest=NoTestsDuringBuild -Dsurefire.failIfNoSpecifiedTests=false

COPY --chown=tester --chmod=755 run-tests.sh ./

# Applied to every mvn call in the container (Maven reads MAVEN_ARGS) and forwarded to the tests:
# use the Debian Chromium + chromedriver; Chrome's sandbox does not work inside a container.
ENV MAVEN_ARGS="-Dselenide.browserBinary=/usr/bin/chromium -Dwebdriver.chrome.driver=/usr/bin/chromedriver -Dchromeoptions.args=--no-sandbox"

# Extra arguments are passed to Maven, e.g.: docker run --rm qa-automation-tests -Dgroups=api
ENTRYPOINT ["./run-tests.sh"]
