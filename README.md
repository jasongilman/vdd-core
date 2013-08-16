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

## Development

TODO

  * how to run tests
  * how to start the repl
  * how to refresh code
  * how to start the clojurescript repl
    * require phantomjs installed. Uses [Austin](https://github.com/cemerick/austin) for clojurescript repl.
    * ```(austin-exec)```
    * I can't seem to reference defined clojure code so I need to manually evaluate it into the repl with ctrl+shift+, then f for file
    * ```:cljs/quit``` to exit
    * run clojurescript tests

## License

Copyright © 2013 Jason Gilman and [element 84](http://www.element84.com).

Distributed under the MIT License.
