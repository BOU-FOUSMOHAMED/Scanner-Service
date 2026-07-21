# Scanner Service - Description Métier

## 1. Vue d'ensemble

Le **Scanner Service** est le registre central des scans de sécurité. Il reçoit les URLs à scanner, les enregistre, et stocke les résultats de vulnérabilités retournés par les outils de sécurité externes (Nuclei, Nikto, Subfinder, etc.).

### Objectifs métier

- **Enregistrer des cibles** : toute URL à analyser est soumise via une API REST
- **Suivre le cycle de vie** d'un scan : `ARRIVED` → `PENDING` → `FINISHED`
- **Consulter les résultats** : filtrer par cible, scanner, sévérité, statut
- **S'intégrer avec des scanners externes** via RabbitMQ (architecture cible)

---

## 2. Cycle de Vie d'un Scan

```mermaid
stateDiagram-v2
    [*] --> ARRIVED : URL reçue et validée
    ARRIVED --> PENDING : URL envoyée au scanner
    PENDING --> FINISHED : Résultats reçus et sauvegardés
    FINISHED --> [*]
```

| Statut | Description |
|---|---|
| `ARRIVED` | URL reçue, validée, en attente d'envoi au scanner |
| `PENDING` | Envoyée au scanner, en cours de traitement |
| `FINISHED` | Résultats reçus et sauvegardés |

---

## 3. Diagrammes de Séquence

### 3.1 Création d'une Cible de Scan

```mermaid
sequenceDiagram
    actor Client
    participant API as Scanner Service
    participant VAL as UrlValidator
    participant DB as PostgreSQL

    Client->>API: POST /api/scanner\n{ "url": "https://target.com" }
    API->>VAL: Valider et normaliser l'URL
    VAL-->>API: URL valide
    API->>API: Vérifier unicité
    API->>DB: Enregistrer cible (status=ARRIVED)
    DB-->>API: OK
    API-->>Client: 201 Created + ScanTargetResponse
```

### 3.2 Soumission d'un Résultat de Scan

```mermaid
sequenceDiagram
    actor Client (Scanner Externe)
    participant API as Scanner Service
    participant DB as PostgreSQL

    Client->>API: POST /api/scanner/results\n{ scanner, target, severity, ... }
    API->>API: Valider les données
    API->>DB: Enregistrer le résultat
    DB-->>API: OK
    API-->>Client: 201 Created + ScanResultResponse
```

### 3.3 Consultation des Résultats

```mermaid
sequenceDiagram
    actor Client
    participant API as Scanner Service
    participant DB as PostgreSQL

    Client->>API: GET /api/scanner/results/target/{url}
    API->>DB: SELECT scan_results WHERE target = ?
    DB-->>API: [résultats...]
    API-->>Client: 200 OK + résultats[]
```

### 3.4 Mise à jour d'une Cible

```mermaid
sequenceDiagram
    actor Client
    participant API as Scanner Service
    participant DB as PostgreSQL

    Client->>API: PUT /api/scanner/{id}\n{ "url": "https://new-url.com" }
    API->>DB: findById(id)
    DB-->>API: ScanTarget existant
    API->>API: Valider + vérifier unicité
    API->>DB: UPDATE url
    DB-->>API: OK
    API-->>Client: 200 OK
```

---

## 4. Diagrammes d'Activité

### 4.1 Création d'une Cible

```mermaid
flowchart TD
    A([Saisir URL]) --> B{URL valide ?}
    B -->|Non| C[Rejet : URL invalide]
    B -->|Oui| D{URL déjà existante ?}
    D -->|Oui| E[Rejet : doublon]
    D -->|Non| F[Enregistrer cible\nstatus = ARRIVED]
    F --> G[Confirmation 201]
```

### 4.2 Soumission d'un Résultat

```mermaid
flowchart TD
    A([Scanner externe envoie résultat]) --> B{Données valides ?}
    B -->|Non| C[Rejet 400]
    B -->|Oui| D[Sauvegarder le résultat]
    D --> E[Confirmation 201]
```

### 4.3 Consultation des Cibles

```mermaid
flowchart LR
    A([Consulter /api/scanner]) --> B{Avec filtre ?}
    B -->|Aucun| C[Toutes les cibles]
    B -->|Par statut| D[Filtrer par statut]
    B -->|Par ID| E[Cible spécifique]
```

---

## 5. Architecture Cible (avec intégration RabbitMQ)

```mermaid
flowchart LR
    subgraph "Scanner Service"
        API[REST API]
        DB[(Base de données)]
        PUB[Publisher scan.requested]
        SUB[Consumer scan.results]
    end

    subgraph "RabbitMQ"
        EX{scanner.exchange}
        Q1[Queue: scan.requested]
        Q2[Queue: scan.results]
    end

    subgraph "Security Scanner"
        TOOLS[Nuclei / Nikto / Subfinder]
    end

    USER[Développeur] -->|POST URL| API
    API --> DB
    API --> PUB --> Q1
    Q1 --> TOOLS
    TOOLS --> Q2
    Q2 --> SUB --> DB
    USER -->|GET résultats| API
```

### Flux des échanges

| Étape | Quoi | Comment |
|---|---|---|
| 1 | Soumettre URL | `POST /api/scanner` |
| 2 | Publier scan.requested | RabbitMQ → Security Scanner |
| 3 | Exécuter les outils | Nuclei, Nikto, Subfinder |
| 4 | Publier scan.results | Security Scanner → RabbitMQ |
| 5 | Sauvegarder résultats | Scanner Service → DB |
| 6 | Consulter résultats | `GET /api/scanner/results/...` |

---

## 6. Règles de Gestion

| Règle | Description |
|---|---|
| RG-01 | Une URL doit être valide (format HTTP/HTTPS) avant d'être enregistrée |
| RG-02 | Une URL ne peut être enregistrée qu'une seule fois (unicité) |
| RG-03 | Un résultat de scan doit avoir un scanner, une cible, une sévérité et un titre |
| RG-04 | Le score CVSS doit être compris entre 0.0 et 10.0 |
| RG-05 | La sévérité doit être parmi : CRITICAL, HIGH, MEDIUM, LOW, INFO |
| RG-06 | Le statut est insensible à la casse (ARRIVED, PENDING, FINISHED) |
