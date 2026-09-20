#!/usr/bin/env python3
"""
Concurrent load test — Section 9's "Load" row (minimum 10 concurrent users, 60 seconds).
Apache JMeter / ab were unavailable in this environment (apt mirror 404s, no ab binary),
so this substitutes a small pure-Python concurrent client against the real deployed WAR.
"""
import json
import statistics
import threading
import time
import urllib.request
import urllib.error

BASE = "http://localhost:8080/adharshmart"
CONCURRENT_USERS = 10
DURATION_SECONDS = 60

results_lock = threading.Lock()
latencies = []
errors = 0
requests_made = 0
stop_flag = threading.Event()


def hit_endpoint(path, method="GET", body=None):
    url = BASE + path
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, method=method,
                                  headers={"Content-Type": "application/json"} if data else {})
    start = time.monotonic()
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            resp.read()
            ok = 200 <= resp.status < 300
    except urllib.error.HTTPError as e:
        e.read()
        ok = 200 <= e.code < 300  # 4xx from an unauth cart call is still a "handled" response
    except Exception:
        ok = False
    elapsed_ms = (time.monotonic() - start) * 1000
    return ok, elapsed_ms


def worker(worker_id):
    global errors, requests_made
    while not stop_flag.is_set():
        # Mixed read-heavy traffic: browse, search, product detail, health.
        for path in ("/api/v1/products", "/api/v1/products?keyword=coat",
                     "/api/v1/products/1", "/api/v1/health"):
            if stop_flag.is_set():
                return
            ok, ms = hit_endpoint(path)
            with results_lock:
                requests_made += 1
                latencies.append(ms)
                if not ok:
                    errors += 1


def main():
    threads = [threading.Thread(target=worker, args=(i,), daemon=True) for i in range(CONCURRENT_USERS)]
    start = time.monotonic()
    for t in threads:
        t.start()
    time.sleep(DURATION_SECONDS)
    stop_flag.set()
    for t in threads:
        t.join(timeout=5)
    total_time = time.monotonic() - start

    with results_lock:
        n = len(latencies)
        sorted_lat = sorted(latencies)
        p50 = sorted_lat[int(n * 0.50)] if n else 0
        p95 = sorted_lat[int(n * 0.95)] if n else 0
        p99 = sorted_lat[min(int(n * 0.99), n - 1)] if n else 0
        avg = statistics.mean(latencies) if latencies else 0
        err_count = errors
        total_requests = requests_made

    print("=" * 60)
    print(f"Concurrent users:     {CONCURRENT_USERS}")
    print(f"Duration:             {total_time:.1f}s")
    print(f"Total requests:       {total_requests}")
    print(f"Throughput:           {total_requests / total_time:.1f} req/s")
    print(f"Errors:               {err_count} ({(err_count / total_requests * 100) if total_requests else 0:.2f}%)")
    print(f"Latency avg:          {avg:.1f} ms")
    print(f"Latency p50:          {p50:.1f} ms")
    print(f"Latency p95:          {p95:.1f} ms")
    print(f"Latency p99:          {p99:.1f} ms")
    print("=" * 60)


if __name__ == "__main__":
    main()
