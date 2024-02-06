# Coverage module

## Overview

This module is used to generate a overall jacoco rapport. It leverages the capabilities of the JaCoCo tool to generate comprehensive reports on code coverage.

## Table of contents 📚

- [Configuration-setup ⚙️](#configuration-setup-⚙️)
- [Generate Coverage Raport 🧪](#generate-coverage-raport-🧪)

## Configuration-setup ⚙️

This module contains a POM.xml-file, no code. The POM is configured to import all reports from all modules and generate a combined one. This will appear in the target-folder in this module.

## Generate Coverage Raport 🧪

1. **Navigate to the minesweeper directory (relative to root folder)**

```cmd
cd minesweeper
```

2. **Run the tests**

```cmd
mvn clean test
```

3. **Generate Combined JaCoCo Report**

- A JaCoCo report is automatically generated each time tests are run.
- To create a combined test coverage report across all modules, ensure that all tests are executed and then run:

```cmd
mvn verify
```

4. **Viewing the Combined Test Results**

- Navigate to **coverage/target/site/jacoco-aggregate** directory in your project folder.
- Locate the **index.html** file. You can open this file in your preferred web browser to view the test coverage results.
- _Optionally, if you have a live server extension in your code editor (such as Visual Studio Code), you can right-click on the index.html file and select "Open with Live Server" to view the results._
- _Optionally, if you want its possible to view test result in the jacoco.csv and jacoco.xml as well._
