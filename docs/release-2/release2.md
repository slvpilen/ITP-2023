# Release 2: Modularisering

## Prosjekmoduler

- **core**: Denne modulen innkapsler spill-logikken og tilbyr funksjoner som generering av Minesweeper-brett og beregning av tid og poeng.

- **storage**: Ansvarlig for persistent datalagring, med bruk av JSON-filformat. Den håndterer lesing fra og skriving til en fil som heter `highscore.json` i appdata-mappen.

- **ui**: Implementerer front-end ved hjelp av JavaFX.

Hver modul har en egen POM-fil (`pom.xml`), som spesifiserer modul-spesifikke avhengigheter og plugins. I tillegg brukes en "parent"-POM for å håndtere "dependencies" og "plugins" som er felles for alle moduler.

## Arkitektur

For lagring brukes Jackson-biblioteket for å enkelt kunne håndtere lagring av score-objekter i en JSON-fil. I appen skjer lagringen eksplisitt etter at du har vunnet et spill, skrevet inn navnet ditt og trykket på en "OK"-knapp. Med andre ord har det blitt benyttet dokumentmetafor for lagring. Dette er på grunn av at vi tenkte det var hensiktsmessig for brukeren å velge om de vil sende "scoren" sin til leaderboard etter at de har vunnet et spill. Dessuten gir det ikke mening å kontinuerlig lagre et spill underveis, da dette kan føre til juks på tidsbruken. Derfor blir ikke implisitt lagring brukt i prosjektet vårt.

## Kodekvalitet

Vi har lastet inn og definert en Checkstyle XML for å standardisere og sjekke koden for feil. Dette inkluderer regler angående "tab"-innrykk og maksimale linjelengder, blant annet. Slike standarder legger til rette for at man enkelt kan rette opp i brudd på regler (bruk: Ctrl+Shift+F). Enhetstester har blitt skrevet for alle moduler, inkludert klikktester i "ui", som indirekte også verifiserer funksjonaliteten til underliggende klasser. Målet vårt var å oppnå en 80 % testdekningsgrad på all kode. Testene avdekket noen avvik i forventet oppførsel, som deretter ble rettet. Kodedekning vurderes ved hjelp av JaCoCo-plugin, som bekrefter en 80 % dekning over alle moduler.

## Dokumentasjon

Alle README-filer er oppdatert. README-filen på rotnivå inneholder nå en lenke til kjøring med Eclipse Che. Et PlantUML-diagram er opprettet for å gi en oversikt over innholdet i "storage"-modulen. Parprogrammering er dokumentert i "commit"-meldinger ved bruk av "Co-authored-by:"-footeren. Videre har hver "merge request" fra en "branch" til "master" blitt gjennomgått og godkjent av et tilfeldig utvalgt gruppemedlem (flere har også fått lov til å gjennomgå).

## Arbeidsvaner

Vi har brukt GitLab-Issues for å holde styr på kodingsoppgaver. Hvert løst "issue" tilsvarer en ny "branch". Disse har en tilhørende "label", som forteller oss hvor mye den haster. Det gjør det enkelt for oss å vurdere prioritetsgraden i en "sprint". Parprogrammering ble brukt, spesielt under utvikling av tester. Dette hadde betydning for tidsbruken vår, men vi så verdien i å ha en ekstra makker som kan dobbeltsjekke koden underveis og komme med innspill.