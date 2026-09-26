# Contributing

## From `git clone` to a running local instance

```bash
git clone <repo-url> adharshmart
cd adharshmart
mvn clean package
```

That's it for local dev — the default `config.properties.example` values point at an embedded
H2 in-memory database, and `DataSourceListener` runs `schema.sql`/`seed.sql` automatically on
first boot. Deploy `target/adharshmart.war` to a local Tomcat 9, or run `mvn test` to exercise
the DAO/service layers directly without a container.

To point at a real config file instead of relying on defaults:

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
# edit config.properties — never commit it, it's gitignored
mvn clean package
```

## Workflow

1. **Intake** — open a GitHub Issue (Feature/Change Request or Bug report template).
2. **Impact analysis** — 3-4 lines on the issue: DB migration required? API shape changed?
3. **Branch** — `feature/<name>` off `main`. `main` is always deployable.
4. **Commit style** — Conventional Commits: `feat:`, `fix:`, `test:`, `docs:`.
5. **PR** — describe what changed, why, and how it was tested. Checklist: tests added, docs
   updated, migration script included if the schema changed.
6. **Definition of Done** (every feature, before merge):
   - Compiles with no Checkstyle/SpotBugs major warnings (`mvn clean verify`).
   - Unit/DAO tests written and passing.
   - Self-reviewed.
   - Migration script included if schema changed (`db/migrations/V{n}__description.sql`).
   - Verified against the deployed URL, not only localhost.
   - README/API docs updated if behavior changed.
   - Merged to `main` only when CI is green.

## Database migrations

Every schema change is a new, numbered file in `db/migrations/` —
`V{n}__description.sql`. Never hand-edit a live table. Seed/demo data stays in `seed.sql`,
separate from schema migrations.
