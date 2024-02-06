# Release 1: Innledende støtte og funksjonalitet
I denne første utgivelsen får brukeren muligheten til å starte Minesweeper-spillet. Skjermen viser spillets grensesnitt, og det er mulig å klikke på ruter for å se om ruten er en bombe eller ikke, men utenom dette, er det foreløpig begrenset med spillfunksjonalitet.

## Kjernefunksjonalitet og Utviklingsstatus
Det meste av spillets logikk er allerede på plass, men brukergrensesnittet er ikke fullstendig implementert ennå. Dette vil bli introdusert i en fremtidig release.
Lagring og innlesing fra fil er implementert for spillets highscore-liste. Selv om denne funksjonen fungerer, vil den sannsynligvis endres ettersom spillogikken blir utviklet videre. På dette stadiet kan man for eksempel ikke legge inn nye navn i highscore-listen, siden man ikke får spilt spillet.

## Dokumentasjon
Vi har laget README-filer i både rotnivåmappen og i Minesweeper-mappen. Disse filene gir en kort oversikt over prosjektet og instruksjoner for hvordan man kjører programmet.
For å forbedre brukeropplevelsen har vi også lagt til emojis i README-filene.
Det finnes en lenke til en brukerhistorie fra personasen “Truls” i README. Denne har tjent som inspirasjon for mange "issues" i vårt GitLab-repository.


## Testing
Enkle tester er utviklet for å bekrefte at en tekststreng blir skrevet ut til terminalen når en knapp blir trykket på. Selv om testen i seg selv ikke gir mye verdi, bekrefter den at test-oppsette fungerer som forventet.
Vi genererer også en Jacoco-rapport hver gang testene kjøres, noe som gir oss mulighet til å vurdere testdekningen. For vår nåværende, enkle kontroller, er testdekningen på litt under 40%. For resten av filene er testdekningen rundt 0%.

## Hvorfor ble ikke JSON implementert i første release?
Problemet med tittelen "JSON for fil-lagring" ble opprinnelig merket med en grønn etikett og var assosiert med milestone Release 1.
Ifølge vårt label-system indikerer en grønn label at problemet er valgfritt å implementere. 
Da vi tildelte den grønne etiketten, var vår hensikt at denne funksjonen kunne implementeres hvis det var tilstrekkelig med tid.

Dessverre hadde vi ikke ekstra tid til å implementere JSON fil-lagring før fristen for Release 1.
Som et resultat flyttet vi dette problemet til Release 2 og oppgraderte prioriteten til medium, siden problemet ble et krav for den utgivelsen.

I ettertid innser vi at det kanskje hadde vært mer hensiktsmessig å ikke tilordne dette valgfrie problemet til Release 1 i det hele tatt.
I stedet kunne vi ha tildelt det til en milestone når det faktisk var fullført.

For å klargjøre situasjonen ytterligere, og for å unngå fremtidige misforståelser, har vi valgt å inkludere en seksjon i README-filen på rotnivå som detaljert forklarer vårt label-system.
