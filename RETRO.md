# Retro log

One line per sprint: what worked, what didn't, one change for next time.

## Sprint 0 — Scaffold & core MVP
- **Worked:** Building strictly to the DAO → Service → Servlet layering from the start kept the
  checkout transaction (order + line items + stock decrement) isolated to one DAO method instead
  of leaking across layers.
- **Didn't:** `schema.sql`/`seed.sql` living at the repo root (per spec) aren't on the classpath
  by default — the first deployed smoke test failed with "Table USERS not found" until the Maven
  `resources` block was taught to mirror them into `WEB-INF/classes`.
- **Change for next sprint:** Run a real Tomcat smoke test (not just `mvn test`) after every
  layer that touches bootstrapping/config, rather than only at the end.
