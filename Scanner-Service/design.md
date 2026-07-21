# Scanner Service - Flow Design

## Current State (what exists now)

```
┌──────────┐     POST /api/scanner      ┌────────────────┐     ┌────────────┐
│  Client   │ ──────────────────────────►│ ScanTarget     │────►│ PostgreSQL │
│           │ ◄──────────────────────────│ Controller     │     │            │
│           │     201 + ScanTargetResponse└────────────────┘     └────────────┘
│           │                                                    ┌────────────┐
│           │     POST /api/scanner/results ────────────────────►│ scan_      │
│           │     {scanner, target, severity, ...}               │ targets    │
└──────────┘                                                    │ scan_      │
                                                                 │ results    │
                                                                 └────────────┘

Problem: No scanning happens. Just a data store.
```

## Target Flow (what you described)

```
                                SCANNER SERVICE
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│  ① POST /api/scanner                                                    │
│     { url: "https://target.com" }                                       │
│              │                                                          │
│              ▼                                                          │
│     ┌─────────────────┐     ┌──────────────┐                           │
│     │ UrlValidator     │────►│ Save to DB   │                           │
│     │ (normalize+valid)│     │ status=ARRIVED│──► RabbitMQ ──┐         │
│     └─────────────────┘     └──────────────┘               │         │
│                                                             ▼         │
│                                              ┌──────────────────────┐ │
│                                              │ scan.requested queue  │ │
│                                              └──────────┬───────────┘ │
└─────────────────────────────────────────────────────────┼─────────────┘
                                                          │
                    ┌─────────────────────────────────────┘
                    ▼
         ┌──────────────────────────────┐
         │   SECURITY SCANNER SERVICE   │
         │  (Nuclei, Nikto, Subfinder)  │
         │                              │
         │  ② Receive URL               │
         │  ③ Execute tools             │
         │  ④ Parse results             │
         │  ⑤ Publish scan.results      │
         └──────────────┬───────────────┘
                        │
                        ▼
                    RabbitMQ
              scan.results queue
                        │
┌───────────────────────┼─────────────────────────────────────────────────┐
│  SCANNER SERVICE      │                                                 │
│                       ▼                                                 │
│  ⑥ @RabbitListener on scan.results                                      │
│              │                                                          │
│              ▼                                                          │
│     ┌──────────────────────┐                                            │
│     │ Save ScanResult to DB│                                            │
│     │ Update status=FINISHED│                                           │
│     └──────────┬───────────┘                                            │
│                │                                                        │
│                ▼                                                        │
│  ⑦ GET /api/scanner/results/target/{targetUrl}                          │
│     ──► Returns all findings to user                                     │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## Sequence Diagram

```
 User/Client        Scanner Service        RabbitMQ         Security Scanner
      │                    │                   │                    │
      │  POST /scanner     │                   │                    │
      │  { url }           │                   │                    │
      │───────────────────►│                   │                    │
      │                    │                   │                    │
      │                    │─ validate URL     │                    │
      │                    │─ save DB (ARRIVED)│                    │
      │                    │                   │                    │
      │                    │── publish ────────►                    │
      │                    │   scan.requested   │                    │
      │  201 Created       │                   │                    │
      │◄───────────────────│                   │                    │
      │                    │                   │                    │
      │                    │                   │── deliver ────────►│
      │                    │                   │                    │
      │                    │                   │    ② execute tools │
      │                    │                   │    ③ parse output  │
      │                    │                   │                    │
      │                    │                   │◄── publish ────────│
      │                    │                   │    scan.results    │
      │                    │                   │                    │
      │                    │◄── deliver ───────│                    │
      │                    │                   │                    │
      │                    │─ save ScanResult  │                    │
      │                    │─ status=FINISHED  │                    │
      │                    │                   │                    │
      │  GET /scanner      │                   │                    │
      │  /results/target   │                   │                    │
      │  /{url}            │                   │                    │
      │───────────────────►│                   │                    │
      │                    │─ query DB         │                    │
      │  200 + results[]   │                   │                    │
      │◄───────────────────│                   │                    │
```

## Status Lifecycle

```
ARRIVED ──► PENDING ──► FINISHED
  │            │            │
  │  (RabbitMQ │  (results  │
  │   sent)    │   received)│
  ▼            ▼            ▼
 POST /scan  publish     @RabbitListener
 created     to queue    saves results
```

## RabbitMQ Topology

```
Exchange: scanner.exchange (topic)
│
├── Queue: scan.requested
│   Binding: scan.requested
│   Producer: Scanner Service (on POST /api/scanner)
│   Consumer: Security Scanner Service
│
└── Queue: scan.results
    Binding: scan.results
    Producer: Security Scanner Service
    Consumer: Scanner Service (@RabbitListener)
```

## Message Format

### scan.requested (Scanner -> Security Scanner)

```json
{
  "eventId": "uuid",
  "eventType": "SCAN_REQUESTED",
  "timestamp": "2026-07-16T10:30:00Z",
  "correlationId": "scan-target-123",
  "payload": {
    "targetId": 1,
    "url": "https://target.com"
  }
}
```

### scan.results (Security Scanner -> Scanner)

```json
{
  "eventId": "uuid",
  "eventType": "SCAN_COMPLETED",
  "timestamp": "2026-07-16T10:35:00Z",
  "correlationId": "scan-target-123",
  "payload": {
    "targetId": 1,
    "url": "https://target.com",
    "results": [
      {
        "scanner": "nuclei",
        "target": "https://target.com",
        "severity": "HIGH",
        "title": "XSS Vulnerability",
        "description": "Cross-site scripting detected in input parameter.",
        "recommendation": "Sanitize user input and encode output.",
        "evidence": "<script>alert(1)</script>",
        "cwe": "CWE-79",
        "cvss": 6.1
      }
    ]
  }
}
```

## What Needs to Be Added to Scanner Service

| Layer | What | File |
|---|---|---|
| **pom.xml** | `spring-boot-starter-amqp` dependency | pom.xml |
| **Config** | RabbitMQ connection config | application.properties |
| **Config** | Exchange + Queue declarations | `config/RabbitMQConfig.java` |
| **Event** | Message envelope record | `event/ScanEvent.java` |
| **Producer** | Publish URL on create | `messaging/ScanEventPublisher.java` |
| **Consumer** | Listen for results | `messaging/ScanResultListener.java` |
| **Service** | Handle incoming results, update status | `service/ScanResultConsumer.java` |

## MLD (Modele Logique de Donnees)

### Diagramme Relationnel

```
┌──────────────────────────────────────────────┐
│              scan_targets                    │
├──────────────────────────────────────────────┤
│ PK  id              UUID (auto-generated)    │
│     url             VARCHAR(2048) NOT NULL    │
│     status          VARCHAR(20) NOT NULL      │
│     created_at      TIMESTAMP NOT NULL        │
│     updated_at      TIMESTAMP                 │
├──────────────────────────────────────────────┤
│ UNIQUE (url)                                 │
│ INDEX  (status)                              │
└─────────────┬────────────────────────────────┘
              │
              │ 1
              │
              │ N  (target = scan_targets.url)
              │
┌─────────────▼────────────────────────────────┐
│              scan_results                    │
├──────────────────────────────────────────────┤
│ PK  id              UUID (auto-generated)    │
│     target          VARCHAR(2048) NOT NULL    │
│     scanner         VARCHAR(100) NOT NULL     │
│     severity        VARCHAR(20) NOT NULL      │
│     title           VARCHAR(255) NOT NULL     │
│     description     TEXT                      │
│     recommendation  TEXT                      │
│     evidence        TEXT                      │
│     cwe             VARCHAR(20)               │
│     cvss            DECIMAL(3,1)              │
│     created_at      TIMESTAMP NOT NULL        │
├──────────────────────────────────────────────┤
│ INDEX  (target)                              │
│ INDEX  (scanner)                             │
│ INDEX  (severity)                            │
└──────────────────────────────────────────────┘
```

### Dictionnaire de Donnees

#### Table: `scan_targets`

| Champ | Type | Contrainte | Description |
|---|---|---|---|
| `id` | UUID | PK, AUTO (JPA `GenerationType.UUID`) | Identifiant unique de la cible (ex: `550e8400-e29b-41d4-a716-446655440000`) |
| `url` | VARCHAR(2048) | NOT NULL, UNIQUE | URL normalisee de la cible a scanner |
| `status` | VARCHAR(20) | NOT NULL | Etat du scan: `ARRIVED` / `PENDING` / `FINISHED` |
| `created_at` | TIMESTAMP | NOT NULL | Date de creation (auto) |
| `updated_at` | TIMESTAMP | | Date de derniere mise a jour (auto via @PreUpdate) |

#### Table: `scan_results`

| Champ | Type | Contrainte | Description |
|---|---|---|---|
| `id` | UUID | PK, AUTO (JPA `GenerationType.UUID`) | Identifiant unique du resultat (ex: `550e8400-e29b-41d4-a716-446655440001`) |
| `target` | VARCHAR(2048) | NOT NULL, INDEX | URL de la cible scannee (reference logique vers scan_targets.url) |
| `scanner` | VARCHAR(100) | NOT NULL, INDEX | Nom du scanner utilise (nuclei, nikto, etc.) |
| `severity` | VARCHAR(20) | NOT NULL, INDEX | Niveau de severite: `CRITICAL` / `HIGH` / `MEDIUM` / `LOW` / `INFO` |
| `title` | VARCHAR(255) | NOT NULL | Titre de la vulnerabilite |
| `description` | TEXT | | Description detaillee |
| `recommendation` | TEXT | | Recommandation de remediation |
| `evidence` | TEXT | | Preuve technique de la vulnerabilite |
| `cwe` | VARCHAR(20) | | Identifiant CWE (ex: CWE-79) |
| `cvss` | DECIMAL(3,1) | | Score CVSS (0.0 - 10.0) |
| `created_at` | TIMESTAMP | NOT NULL | Date de creation du resultat |

### Relations

```
scan_targets (1) ──────────── (N) scan_results
        │                              │
        │  relation logique            │
        │  par URL                     │
        │                              │
        └── scan_targets.url ◄─────────┘ scan_results.target
            (pas de FK formelle, lien par valeur)
```

> **Note:** La relation entre `scan_targets` et `scan_results` est un lien **logique par valeur** (URL), pas une FK physique. Cela permet aux resultats d'arriver de services externes sans contrainte d'integrite referentielle sur la table scan_targets.

### Enums

#### `ScanStatus`

| Valeur | Description |
|---|---|
| `ARRIVED` | URL recue, en attente d'envoi au scanner |
| `PENDING` | Envoye au scanner via RabbitMQ, en cours de traitement |
| `FINISHED` | Resultats recus et sauvegardes en base |
