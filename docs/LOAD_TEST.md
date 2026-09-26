# Load test (Section 9)

**Spec target:** concurrent user handling, minimum 10 concurrent users, 60 seconds, via
Apache JMeter or `ab`.

Neither was available in this build environment (the `apt` mirror returned 404s for
`apache2-utils`, and no `ab`/JMeter binary was preinstalled), so this run uses a small
pure-Python concurrent client instead — [`scripts/load_test.py`](../scripts/load_test.py).
Swap in real JMeter/`ab` against a deployed instance if you want an independently-verified
number; the methodology (10 threads, 60s, mixed read traffic) is the same either way.

## Methodology

10 daemon threads, each looping continuously for 60 seconds against a locally deployed
Tomcat 9 instance (`mvn clean package` → `target/adharshmart.war` → Tomcat 9.0.98,
embedded H2), hitting a read-heavy mix representative of real browsing traffic:

- `GET /api/v1/products` (full catalog)
- `GET /api/v1/products?keyword=coat` (search)
- `GET /api/v1/products/1` (product detail)
- `GET /api/v1/health`

## Results

```
Concurrent users:     10
Duration:             60.0s
Total requests:       115128
Throughput:           1918.2 req/s
Errors:               0 (0.00%)
Latency avg:          5.2 ms
Latency p50:          5.0 ms
Latency p95:          8.3 ms
Latency p99:          10.5 ms
```

Zero errors, zero `SEVERE` entries in `catalina.out`/`localhost.*.log` during the run.
p99 latency stayed under 11ms — comfortably inside what a single small VM (the reference
deployment target in README §6) should sustain for 10 concurrent users.

## Caveats

- Single-VM, embedded H2 (no network hop to a separate DB server) — a server-mode H2
  deployment (per README §6) will add some latency, though not materially at this
  concurrency level.
- Read-heavy only; write-path throughput (checkout's transactional stock decrement) wasn't
  separately load-tested. That transaction holds row locks briefly on `products`/`orders`,
  so concurrent checkouts on the *same* product would serialize — expected and correct
  behavior for stock-decrement correctness, not a bug.
- Re-run `python3 scripts/load_test.py` against any freshly deployed instance to reproduce.
