# Security checklist (Section 9)

Verified against the codebase at commit time. Re-run the grep commands yourself after any DAO
change — this file is a snapshot, not a substitute for checking.

- [x] **Every query parameterized.**
  `grep -rn "Statement)" src/main --include="*.java" | grep -v PreparedStatement` returns nothing —
  every `Statement)` occurrence in `src/main` is a `PreparedStatement`. `grep -rn "createStatement\|DriverManager.getConnection" src/main`
  returns only a comment (no executable usage) in `DataSourceListener`.

- [x] **Passwords bcrypt-hashed, never logged.**
  `util/PasswordUtil` wraps jBCrypt (`BCrypt.hashpw`/`BCrypt.checkpw`) exclusively; no MD5/SHA1
  anywhere. `dto/UserResponseDTO` (the only user shape ever serialized back to a client or logged)
  has no `passwordHash` field — see rule below.

- [x] **All protected servlets enforce session checks via AuthFilter.**
  `filter/AuthFilter` gates every non-public path (`/api/v1/cart*`, `/api/v1/orders*`,
  `/api/v1/admin*`, `/api/v1/seller*`, `/api/v1/products` writes, `/api/v1/reviews` writes, and the
  matching JSP pages) behind a session check, registered first in the filter chain via `web.xml`.
  Role-specific checks (`SELLER`/`ADMIN`) additionally happen in each servlet via
  `BaseServlet.requireRole`. Covered by `AuthServletTest`/`CartServletTest`
  (`src/test/.../controller`).

- [x] **User-supplied input escaped before rendering.**
  Server-rendered JSP content (session name, role) goes through JSTL `<c:out>`/EL escaping in
  `WEB-INF/jspf/header.jspf`. Client-rendered content (product names, descriptions, review
  comments, usernames) is inserted via DOM `textContent`/`createTextNode` in `js/app.js` — never
  via `innerHTML` string concatenation with user data. The one `innerHTML` use in `app.js` (`el()`
  helper's `html` attr) is never called with user-supplied strings, only our own static markup.

- [x] **File upload validates type/size; never trusts client filename.**
  Not applicable — the spec's `products.image_url` is a plain string column (a URL), not a binary
  upload endpoint, so there is no file-upload surface in this build.

- [x] **Error pages do not expose stack traces.**
  `web.xml` maps 404/500/`java.lang.Exception` to `404.jsp`/`500.jsp`, both marked
  `isErrorPage="true"` and neither one references the `exception` implicit object — verified live
  by hitting a 500 and confirming only the generic message renders (see README §7 / smoke test).

- [x] **Database credentials excluded from version control.**
  `config.properties` and `.env` are listed in `.gitignore`; only `config.properties.example` /
  `.env.example` are committed, and those contain no real secrets.

## Additional rules verified (mandatory engineering rules, Section 2)

- [x] Session id regenerated on login — `AuthServlet.login` invalidates any prior session before
  creating a new one.
- [x] Explicit session timeout — `web.xml` sets `<session-timeout>30</session-timeout>`;
  `AuthServlet.login` additionally sets `session.setMaxInactiveInterval(30 * 60)`.
- [x] Connection pool owned by a single `ServletContextListener` — `DataSourceListener` is the only
  place `HikariDataSource`/`DriverManager` is touched.
- [x] Try-with-resources for every `Connection`/`PreparedStatement`/`ResultSet` — used throughout
  `dao/*Impl.java`; the one exception (`OrderDAOImpl.placeOrder`'s manual `commit()`/`rollback()`)
  still opens the `Connection` in a try-with-resources block, with per-statement
  `PreparedStatement`s each in their own nested try-with-resources.
