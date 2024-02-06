# Release 3: REST-API

## Arkitektur

_Minesweeper_-appen vår har utviklet seg betydelig og har nå en mer struktur med fire hovedmoduler: **core**, **coverage**, **rest** og **ui**. I utviklingen av _release 3_ fant vi ut at det var smart å kombinere core- og storage-modulene til en større, mer funksjonell core-modul. I denne prosessen introduserte vi også en helt ny rest-modul, som har tatt over rollen som hovedlager for data i appen, ved hjelp av REST-API. Dette APIet bruker _Jackson_-biblioteket for å organisere og lagre brukernes poengsummer i en _JSON_-fil, som håndteres av rest-serveren.

Dette nye oppsettet gjør slik at alt det som tidligere hørte til storage nå er en del av core, og det nye er at _REST-API_ utgjør kjernen i lagring til server. Core-modulen er fremdeles ansvarlig for selve spillets logikk, og sørger for at spillet følger Minesweeper-reglene. Etter å ha vunnet et spill, og etter å ha lagt inn ditt navn i feltet som kommer opp og klikket 'OK', er det _REST-APIets_ jobb å sende din `UserScore` til _JSON_-filen som _rest-serveren_ har, og dette viderefører vår tradisjon med å bruke dokumentmetaforen for lagring.

Det nye i vår arkitektur inkluderer introduksjonen av et grensesnitt kalt `TileReadable`. Dette grensesnittet er spesifikt designet for situasjoner hvor kun _"getter"_-metoder i Tile er nødvendige. For eksempel, i `GameEngine`, returneres en liste med `Tile` objekter som har blitt oppdatert og trenger å oppdateres på skjermen. Brukergrensesnittet (_UI_) behøver kun tilgang til disse _getter_-metodene. Ved å bruke `TileReadable`-grensesnittet, sørger vi for at _UI_-komponenten ikke modifiserer `Tile`-objektet, noe som bidrar til bedre lesbarhet og en mer effektiv utviklingsprosess. Dette reduserer også risikoen for feilaktig bruk av `Tile`, som potensielt kan endre spilltilstanden ved et uhell.

## Arbeidsvaner

I tråd med våre tidligere erfaringer fra **Release 2**, har vi fortsatt med å bruke GitLab-Issues for å organisere oppgavene våre. For hver oppgave som har blitt fullført, har vi opprettet en tilsvarende branch. Vi har videreført bruk av labels på disse merge requests og issues for å angi hvor mye de haster, noe som har gjort det enklere for oss å finne ut hva som må prioriteres under sprintene. Noen ganger har det vært nødvendig å kombinere flere issues i en enkelt branch, ofte fordi oppgavene var små og naturlig overlappet hverandre. Vi anså dette som en god løsning.

Vi har også foretatt en liten endring i vårt system for navngivning av branches. Istedenfor å bruke issue-nummer som vi gjorde tidligere, har vi begynt å bruke release-nummeret som en del av branch-navnet for å bedre skille mellom forskjellige versjoner. Det var litt forvirring rundt denne nye praksisen i starten, og det var enkelte i gruppen som fortsatte å bruke det gamle systemet, men vi føler at det nye systemet gir en klarere organisering av arbeidet vårt.

Som en ny vri i **Release 3**, har vi også flyttet fokus fra parprogrammering til kodevurderinger. Det har vi gjort ved å tildele noen som ikke har jobbet med koden til å gjennomgå og kommentere på en ferdigstilt branch i en merge-request. Etter at tilbakemeldingene har blitt sett på og blitt utbedret av den som står bak branchen, er det _"reviewer"_ sin jobb å godkjenne og utføre sammenslåingen med master-branchen. Dette har vist seg å være et verdifullt skritt for å opprettholde høy kodekvalitet.

## Dokumentasjon

Vi har utarbeidet en omfattende dokumentasjon for hver modul i vår kodebase, for å sikre enkel navigering og forståelse for brukerne. Denne dokumentasjonen inkluderer detaljerte `README`-filer for hver modul som forklarer modulens hensikt, gir instruksjoner for bruk, og beskriver funksjonene til de forskjellige klassene. For å gi en visuell representasjon av modulstrukturen og klassene, har vi også integrert flere **PlantUML**-diagrammer.

Videre har vi dokumentert bruken av forskjellige kodekvalitetsverktøy som **Checkstyle**, **Spotbugs** og **Java-formatter**. For hvert verktøy finner man nå en detaljert beskrivelse av bruken og en lenke til verktøyets hjemmeside. Spesielt for _Checkstyle_, har vi valgt å følge `google_checks.xml`-formateringsreglene, med kildeinformasjon tilgjengelig i den tilhørende `README`-filen.

For å gjøre det enklere å vurdere testdekningen, har vi inkludert skjermbilder av Jacoco-rapporten i hver modul. Dette tilbyr en umiddelbar visuell indikasjon på testdekningsgraden, og for de som ønsker å generere disse rapportene selv, er det fortsatt inkludert en detaljert oppskrift i `minesweeper/README.md`.

I tillegg har vi i vår rot-`README` inkludert spilleregler for minesweeper og prosjektinformasjon. Dette sikrer at alle brukere har tilgang til kritisk informasjon fra starten. For å forenkle navigasjonen ytterligere, har vi lagt til en oversikt over alle moduler i `minesweeper/README`, komplett med hyperlenker til den tilsvarende modulens `README`. Dette gjør det raskt og enkelt å finne relevant informasjon.

I pakken springboot-package finner man dokumentasjonen `REST_DOCUMENTATION.md`, som omhandler REST-tjenester. Denne dokumentasjonen inneholder detaljert informasjon om hvordan man utfører **GET**-, **POST**- og **DELETE**-forespørsler. I tillegg gir den en oversikt over endepunktet og filformatet som brukes. Det er også lagt til en lenke til denne dokumentasjonen i `README`-filen til den aktuelle modulen, for enkel tilgang og referanse.

## Funksjonalitet

For **Release 3** tok vi et bevisst valg om å bygge ut funksjonaliteten i stedet for å gjenoppbygge klientsiden med ny teknologi. Vi hadde allerede et solid brukergrensesnitt fra **Release 2** og et ønske om å oppfylle flere av kravene som ble nevnt i _brukerhistoriene_. Vi fant det derfor mer verdt å legge til ny funksjonalitet, i stedet for å investere tid i å veksle teknologi, noe som ville medført betydelig tidsbruk uten garanterte fordeler.

I den siste oppdateringen har vi også rullet ut et nytt **REST-API**. Dette kjører nå i en modul som tidligere var kjent som _storage_, men som har fått det nye, mer beskrivende navnet rest. Denne modulen definerer nå tydelig hvilke _HTTP_-metoder vi støtter og har blitt det dedikerte kontaktpunktet for all _REST-API_-relatert kode. Tidligere kode for userscores og filbehandling av highscores har vi flyttet til 'core'-modulen for å forenkle og forbedre arkitekturen (se detaljer under [Arkitektur](#arkitektur)).

På funksjonalitetssiden har vi også lyktes i å implementere flere nye funksjoner direkte fra brukerhistorien, som chording – en funksjon detaljert i [Game Rules](../../README.md#game-rules-📜) seksjonen av vår _rot-README_ – samt innføring av forskjellige vanskelighetsgrader, temavalg for spillmiljøet (som mørk og lys modus), og separate highscore-lister for hver vanskelighetsgrad, sammen med et oppdatert brukergrensesnitt.

Fra den tidligere utgivelsen har vi i tillegg integrert **Spotbugs** som et supplement til **Checkstyle** og **Java-formatter** for å forbedre kvalitetssjekkene på vår kode. Dette nye verktøyet har hatt en merkbar positiv effekt, for det har hjulpet oss i å identifisere og rette flere feil, for eksempel tilfeller hvor lister ble returnert uten å bli kopiert først, og situasjoner der objekter ble opprettet gjentatte ganger uten at det var nødvendig. Takket være Spotbugs har vi blant annet klart å redusere kjøretiden til spillet ved å unngå gjentatt opprettelse av `Random`-objekter.

## Testing av funksjonalitet:

I utviklingen av Minesweeper før **release 2** prioriterte vi å skape et fungerende produkt fremfor å oppnå høy testdekningsgrad. Fra den tid har vi imidlertid rettet fokuset mer mot å forbedre koden ved å øke testdekningsgraden og å forbedre dokumentasjonen. Dette har resultert i en betydelig økning i testdekningsgraden for **core**-modulen, fra 50% ved release 2 til full 100% i den nåværende utgaven, selv etter å ha introdusert ny funksjonalitet.

Vi oppnådde denne økningen gjennom flere tiltak. I **rest**-modulen benyttet vi `@SpringBootTest` for å implementere integrasjonstester, som lar oss teste server-baserte komponenter som `HighscoreRestController` og `HighscoreService`. **MockMvc** ble brukt for å etterligne serverinteraksjoner, slik at vi fikk testet kontrollerens håndtering av HTTP-forespørsler.

For **ui**- og **core**-modulene fokuserte vi på tester utløst av brukerinteraksjoner, og supplerte med direkte tester i **core**-modulen for å dekke situasjoner utenfor rekkevidden av ui, som for eksempel spesifikke tilfeller i `Stopwatch`-klassen som ikke er praktisk å vente på i sanntid.

Selv om vi har jobbet utrettelig for å dekke hver eneste krik og krok av koden vår med tester, er det noen sjeldne unntak vi har valgt å la være å teste. For eksempel så har vi gått bort fra validering av ugyldige input av vannskelighets grad, da vi har benyttet **enum**-klasser for å garantere for lovlige input. Valgmulighetene er tross alt låst til `EASY`, `MEDIUM` og `HARD`. Videre har vi tatt en strategisk beslutning om ikke å jage etter full branch coverage som _JaCoCo_ foreslår, spesielt når det betyr å lage tester for høyst usannsynlige hendelser – som det absurde scenariet hvor man skulle vinne og tape i Minesweeper samtidig. Vi tenker kvaliteten på koden blir best dersom vi ikke fokuserer på full 100% coverage, og heller fokuserer på å lage gode, realistiske tester av sannsynlige yttertilfeller.
