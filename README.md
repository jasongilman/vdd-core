# vdd-core

FIXME

## Usage

```bash
% lein run -- --help
Usage:

 Switches           Default  Desc
 --------           -------  ----
 -i, --ip                    The ip address to bind to
 -p, --port                  Port to listen
 -t, --thread                Http worker thread count
 --no-help, --help  false    Print this help
```

Run with application defaults:

```bash
% lein run
```
...then point browser to [localhost:8080](http://localhost:8080)

Application defaults are supplied by environment-based configuration
found within `./resources-dev/config.clj`.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
