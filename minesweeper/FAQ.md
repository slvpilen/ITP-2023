# FAQ

### What is Checkstyle?

Checkstyle is a development tool that helps Java developers adhere to certain coding standards. When integrated in the POM it can be configured to not build the project unleess it's formated with the defined standard (google_checks.xml).

### How do i standarize my code to pass checkstyle?

1. Open the Java file you want to clean.
2. In windows press 'alt + shift + f' or open command palette (Ctrl+Shift+P) and search for "format document". This will format your code according to /.vscode/java-formatter rules. This should match the checkstyle

### How do i change java-formatter settings?

1. Open command palette (Ctrl+Shift+P) and select "java: Open Java Formatter Settings with Preview"
2. Modify the settings according to your preferences.
3. Save the settings. Note: After saving changes, each file needs to be reformatted to apply the new rules.
