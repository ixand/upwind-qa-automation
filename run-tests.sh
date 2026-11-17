#!/bin/sh
# Container entrypoint: runs the tests, always builds the Allure report,
# and exits with the test result (so CI fails when a test fails).
#
# Arguments are passed to Maven:   ./run-tests.sh -Dgroups=ui
# If a folder is mounted at /reports, the reports are copied there.

mvn -B test "$@"
test_status=$?

mvn -B -q allure:report || echo "WARNING: Allure report was not generated"

copy_report() {
    # $1 = source, $2 = destination; missing sources are skipped (e.g. no screenshots when nothing failed)
    [ -e "$1" ] || return 0
    cp -r "$1" "$2" || copy_failed=1
}

if [ -d /reports ]; then
    copy_failed=0
    copy_report target/site/allure-maven-plugin/index.html /reports/allure-report.html
    copy_report target/surefire-reports /reports/
    copy_report target/selenide-reports /reports/
    if [ "$copy_failed" -eq 0 ]; then
        echo "Reports copied to /reports:"
        ls -l /reports
    else
        echo "WARNING: could not copy reports to /reports (running as uid $(id -u))." \
             "Create the folder before mounting it, or rebuild with --build-arg UID=\$(id -u)"
    fi
fi

exit $test_status
