# DronePizza Applikation

## Introduktion

Dette repository indeholder en fuldt funktionel applikation til DronePizza, som kombinerer en database, et REST API og en simpel front-end. Applikationen er designet til at håndtere dronebaserede pizzaleveringer i København.

Applikationen er opdelt i tre dele:
1. **Database med JPA og H2/MySQL** – Modellering af droner, pizzaer, stationer og leveringer.
2. **REST API** – Koordinering og styring af droner og leveringer.
3. **Front-end** – Et simpelt HTML/JavaScript-interface til at vise og håndtere leveringer.

## Funktioner

### Database (Delopgave 1)
Applikationen benytter JPA og H2/MySQL til at modellere følgende:
- **Drone**:
  - Offentligt UUID (ikke primærnøgle).
  - Status: `i drift`, `ude af drift` eller `udfaset`.
- **Pizza**:
  - Pizzaer med fast titel og pris.
- **Station**:
  - Stationer med GPS-koordinater (breddegrad og længdegrad).
- **Levering**:
  - Leveringer med adresse, forventet og faktisk leveringstid.

**Initial data**:
- Tre stationer tæt på Københavns centrum (55,41°N, 12,34°Ø).
- Fem pizzaer på menuen.
- Data indsættes automatisk i databasen ved applikationsstart.

### REST API (Delopgave 2)
Applikationen tilbyder et REST API til håndtering af droner og leveringer. Følgende endpoints er implementeret:

#### Droner
- `GET /drones` – Returnerer en liste over alle droner med UUID, status og station.
- `POST /drones/add` – Opretter en ny drone og tildeler den til den station med færrest droner. Nye droner har status `i drift`.
- `POST /drones/enable` – Skifter en drones status til `i drift`.
- `POST /drones/disable` – Skifter en drones status til `ude af drift`.
- `POST /drones/retire` – Skifter en drones status til `udfaset`.

#### Leveringer
- `GET /deliveries` – Returnerer en liste over leveringer, der ikke er leveret.
- `POST /deliveries/add` – Tilføjer en ny levering af en bestemt pizza. Forventet leveringstid sættes til 30 minutter fra oprettelsen.
- `GET /deliveries/queue` – Returnerer en liste over leveringer, der mangler en drone.
- `POST /deliveries/schedule` – Tildeler en drone til en levering, der mangler en drone. Fejler, hvis dronen ikke er `i drift`, eller leveringen allerede er i gang.
- `POST /deliveries/finish` – Markerer en levering som færdig. Fejler, hvis leveringen ikke har en drone.

**Fejlhåndtering**:
- Fejl behandles med relevante HTTP-statuskoder og detaljerede fejlbeskeder, fx:
  - **400 Bad Request**: Ved ugyldige handlinger.
  - **404 Not Found**: Hvis ressourcen ikke findes.

**Test**:
- Endpoints er testet manuelt med Postman og programmatisk med JUnit.

### Front-end (Delopgave 3 - Se DronePizza_Frontend repository)
Applikationen inkluderer en front-end bygget med HTML, CSS og JavaScript.

**Funktioner**:
- Viser en liste over alle leveringer, der ikke er afsluttet, sorteret efter ældste levering først.
- Angiver, om leveringer mangler en drone, eller blot mangler at blive leveret.
- Opdaterer listen automatisk hvert 60. sekund.
- Knapper til:
  - Tildeling af en drone til leveringer, der mangler en drone.
  - Oprettelse af nye droner.

**Simulation**:
For at understøtte demonstrationer tilbyder front-end'en også:
- Knap til at simulere, at en kunde opretter en levering.
- Knap til at simulere, at en drone afslutter en levering.

## Teknologier
- **Backend**: Spring Boot, JPA, H2/MySQL.
- **Frontend**: HTML, CSS, JavaScript.
- **Test**: JUnit og Postman.
