# QA Automation Tests: SauceDemo (UI) + Restful Booker (API)

Automated UI tests for [SauceDemo](https://www.saucedemo.com/) and API tests for
[Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html).

| Area      | Tools                                                                    |
|-----------|--------------------------------------------------------------------------|
| Language  | Java 17                                                                  |
| Build     | Maven 3.9 via the Maven Wrapper (`./mvnw`), no local Maven needed        |
| Test runner | JUnit 5 (Jupiter), parameterized tests, parallel execution             |
| UI        | Selenide 7 (Selenium WebDriver underneath), Chrome                       |
| API       | REST Assured 5, Jackson, JSON Schema validation                          |
| Assertions | Selenide conditions (UI, they wait), AssertJ (data, soft assertions)    |
| Reporting | Allure (steps, screenshots on failure, HTTP request/response attachments) |
| Container | Docker (JDK + Maven + Chromium + chromedriver in one image)              |
| CI        | GitHub Actions: builds the image, runs the tests, uploads reports         |

## What is covered

| Test class | Scenarios |
|------------|-----------|
| `ui/LoginTest` | **Test 1**: standard user logs in, lands on Products, the page lists products (each with a name and a price). Additionally: 4 rejected logins (locked user, wrong password, empty username, empty password) with the exact error message; Products page is not reachable without login. |
| `ui/ProductCartTest` | **Test 2**, parameterized over all 4 sort options: log in, sort, check the order (and that no product was lost), add the product that the sort put first, check the cart badge, open the cart and check name, price and quantity. |
| `api/BookingApiTest` | **Test 3**: `POST /booking` (status, generated id, echoed data), then `GET /booking/{id}` (status, JSON schema, every field: first/last name, total price, deposit paid, booking dates, additional needs). |
| `api/BookingNegativeApiTest` | Missing required field (5 cases), malformed JSON, deleted booking is not found, delete with an invalid token is refused and the booking is unchanged, auth with a wrong password returns no token. |

20 test executions in total; the whole suite runs in about 20-30 seconds.

## Project structure

```
.
├── Dockerfile                 # test runner image
├── run-tests.sh               # container entrypoint: tests -> Allure report -> exit code
├── pom.xml                    # dependencies and plugins (versions in <properties>)
├── mvnw, .mvn/                # Maven Wrapper (downloads the pinned Maven version)
├── .github/workflows/tests.yml
└── src/test
    ├── java/com/upwind/qa
    │   ├── config/            # TestConfig (property/env/file lookup), Credentials
    │   ├── ui
    │   │   ├── BaseUiTest     # @Tag("ui"), Allure listener, fresh browser per test
    │   │   ├── LoginTest, ProductCartTest
    │   │   ├── pages/         # LoginPage, ProductsPage, CartPage (page objects)
    │   │   ├── components/    # Header, ProductList, ProductItem (reused by pages)
    │   │   └── model/         # Product, SortOption
    │   └── api
    │       ├── BaseApiTest    # @Tag("api"), clients, cleanup of created bookings
    │       ├── BookingApiTest, BookingNegativeApiTest
    │       ├── client/        # ApiSpecs (shared REST Assured spec), BookingClient, AuthClient
    │       ├── model/         # Booking, BookingDates, CreatedBooking, AuthResponse (records)
    │       └── data/          # BookingTestData (unique test data)
    └── resources
        ├── test.properties    # URLs and credentials (defaults)
        ├── selenide.properties# browser settings (Selenide reads it itself)
        ├── junit-platform.properties # parallel execution
        ├── allure.properties, simplelogger.properties
        └── schemas/booking.schema.json
```

## Prerequisites

**Docker only** (recommended): Docker 20+ and internet access. Nothing else.

**Local run**:
- JDK 17 or newer (`java -version`)
- Google Chrome (Selenium Manager downloads a matching chromedriver automatically)
- Maven is **not** required: the `./mvnw` wrapper downloads Maven 3.9.16 on first use
- Internet access (both applications are public websites)

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Local execution

```bash
./mvnw test                  # all tests
./mvnw test -Dgroups=ui      # UI tests only
./mvnw test -Dgroups=api     # API tests only

./mvnw test -Dtest=LoginTest                       # one class
./mvnw test -Dgroups=ui -Dselenide.headless=false  # watch the browser
```

Tests are selected by JUnit 5 tags: `BaseUiTest` is tagged `ui`, `BaseApiTest` is tagged `api`,
and every test class inherits the tag.

### Report

```bash
./mvnw allure:serve     # builds the report and opens it in the browser
./mvnw allure:report    # writes target/site/allure-maven-plugin/index.html (single file)
```

The report shows each test as readable steps (page object and API client methods are Allure `@Step`s),
every HTTP request/response, and a screenshot for a failed UI step.
Plain JUnit XML reports are in `target/surefire-reports`.

## Docker execution

```bash
docker build -t qa-automation-tests .
docker run --rm qa-automation-tests
```

Any argument after the image name is passed to Maven:

```bash
docker run --rm qa-automation-tests -Dgroups=ui
docker run --rm qa-automation-tests -Dgroups=api
docker run --rm qa-automation-tests -Dtest=BookingApiTest
```

The container exits with Maven's exit code, so a failed test fails the command (and CI).

### Getting the reports out of the container

Mount a folder at `/reports`; the container copies the Allure report (`allure-report.html`,
a single self-contained file), the JUnit XML reports and, if a UI test failed, Selenide screenshots there:

```bash
mkdir -p reports
docker build --build-arg UID="$(id -u)" -t qa-automation-tests .
docker run --rm -v "$PWD/reports:/reports" qa-automation-tests
open reports/allure-report.html     # macOS; xdg-open on Linux
```

The container runs as a non-root user. `--build-arg UID="$(id -u)"` gives it your user id so it can
write into the mounted folder (the default `1000` matches most single-user Linux machines and
Docker Desktop on macOS/Windows does not need it). Create the folder before mounting it, otherwise
Docker creates it owned by root.

### How the image is built

- Base image `maven:3.9.16-amazoncorretto-17-debian-trixie`: JDK 17 and the same Maven version as the wrapper.
- Chromium and chromedriver are installed from Debian packages, so their versions always match,
  nothing is downloaded at test time, and the image works on amd64 and arm64 (Apple Silicon).
- Layers are ordered for caching: `pom.xml` → dependencies (`dependency:go-offline`, Allure CLI),
  then sources → compile. Changing a test does not download dependencies again.
- `MAVEN_ARGS` points Selenide to the Chromium binary and chromedriver and adds `--no-sandbox`
  (Chrome's sandbox cannot start inside a container). Locally none of this is needed.

## Configuration

| What | Where | Override |
|------|-------|----------|
| SauceDemo URL, browser, headless, timeout, window size | `selenide.properties` | `-Dselenide.baseUrl=...`, `-Dselenide.browser=firefox`, `-Dselenide.headless=false`, `-Dselenide.timeout=10000` |
| UI credentials | `test.properties` (`ui.username`, `ui.password`) | `-Dui.username=...` or env `UI_USERNAME` |
| API URL and admin credentials | `test.properties` (`api.baseUrl`, `api.username`, `api.password`) | `-Dapi.baseUrl=...` or env `API_BASE_URL` |
| Parallelism | `junit-platform.properties` (4 threads) | `-Djunit.jupiter.execution.parallel.config.fixed.parallelism=2`, or `-Djunit.jupiter.execution.parallel.enabled=false` |

Lookup order for `test.properties` keys: system property → environment variable → file
(see `TestConfig`). The credentials are the public demo credentials published by both sites;
in a real project they would come from environment variables / a secret store only.

## Test approach

### Locators

SauceDemo puts a dedicated test attribute on its elements (`data-test="username"`,
`data-test="inventory-item"`, ...). All locators use it through Selenide's `byTestId(...)`
(`selenide.test-id.attribute=data-test`). Such attributes exist only for tests, so they do not change
when styling or layout changes, unlike CSS classes (`.btn_primary`) or positional XPath.

Products are found **by their visible name**, not by index or by product-specific ids
(`add-to-cart-sauce-labs-backpack`): `ProductList.item(name)` finds the name element and goes up to
its card. The test does not need to know the internal id of a product it picked at run time.

### Synchronization

There is no `sleep` anywhere. Selenide conditions retry until they pass or the timeout
(`selenide.timeout`, 8 s) expires:

- every page has `shouldBeOpened()` (URL + title + content) and page transitions return the next
  page object only after it is open, so the next step never runs on the old page;
- assertions on the page are Selenide conditions (`shouldHave(exactText(...))`,
  `shouldHave(exactTexts(...))`, `sizeGreaterThan(0)`), which wait as well;
- after sorting, `sortBy()` waits until the "active option" label shows the new option.
  The label and the list are rendered together, so after that the order can be read as plain data
  (`ProductList.snapshot()`) and checked with AssertJ `isSortedAccordingTo(...)`.
  Reading data without waiting is done only after such a synchronization point.

### Code structure

**UI**: page objects (`LoginPage`, `ProductsPage`, `CartPage`) hold locators and user actions;
components (`Header`, `ProductList`, `ProductItem`) are shared by pages instead of being copied.
Actions return the page the user ends up on (`loginAs()` → `ProductsPage`, `openCart()` → `CartPage`),
which makes tests read like the scenario. Page objects expose elements and actions; the expected
values and the checks live in the tests. `SortOption` keeps each option's value, label and expected
order together, so one parameterized test covers all four.

**API**: `ApiSpecs` holds the one REST Assured request specification (base URI, JSON headers,
Jackson mapper for `LocalDate`, Allure filter, log-on-failure). `BookingClient` and `AuthClient` only
send requests and return the `Response`; tests decide what is expected, so the same client is used by
positive and negative tests. Requests and responses are Java records (`Booking`, `CreatedBooking`, ...)
with `@JsonProperty` mapping Java names to the API's names. The main test checks status codes and
content type with REST Assured, the response structure with a JSON schema, and the data with AssertJ
soft assertions (one run reports every wrong field).

### Test isolation and parallel execution

- UI: each test gets a fresh browser (`closeWebDriver()` after each test), so no session or cart
  state is shared. Selenide keeps one browser per thread, which makes parallel runs safe.
- API: every booking is new and has a unique last name; bookings created by a test are deleted
  after it (`BaseApiTest`). Tests never depend on data created by other tests or by other users of
  the public API.
- Test classes and methods run in parallel (4 threads, `junit-platform.properties`).

### Assumptions and limitations

- Both systems are public demo services shared with other users. A test can fail because a service
  is down or slow; the tests do not retry, to avoid hiding real problems.
- Restful Booker behaves differently from common REST practice; the negative tests assert what the
  API actually returns (checked against the live API) and say so in a comment:
  - a booking with a missing required field gets **500** (a 400 would be expected);
  - a successful `DELETE` returns **201**;
  - `POST /auth` with wrong credentials returns **200** with `{"reason": "Bad credentials"}`;
  - an `Accept` header other than plain `application/json` gets **418 I'm a Teapot**, which is why the
    spec sets it explicitly instead of REST Assured's `ContentType.JSON` (that one lists several types).
  In a real project the first three would be raised as bugs/contract questions.
- `POST /booking` accepts some invalid values (for example `"totalprice": "abc"` is stored as `null`);
  this is not covered because the expected behaviour is not specified.
- API → UI test-data integration (an optional bonus) is not applicable: SauceDemo and Restful Booker
  are unrelated systems, and SauceDemo has no API to prepare data through.
- UI tests run on Chrome locally and on Chromium in Docker. Other browsers can be selected with
  `-Dselenide.browser=firefox`, but only Chrome/Chromium was verified.
- The `@Step` annotations need the AspectJ agent configured in Surefire. When a test is started from
  an IDE without Maven, it still runs, only the report has fewer steps.

## CI

`.github/workflows/tests.yml` runs on every push to `main`, on pull requests and manually: it builds
the Docker image, runs all tests in it and uploads the Allure report and JUnit XML files as the
`test-reports` artifact (also when tests fail).
