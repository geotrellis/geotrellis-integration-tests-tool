# [GeoTrellis](github.com/geotrellis/geotrellis) integration tests tool

```bash
spark-submit ${PWD}/target/scala-2.10/geotrellis-integration-tests-assembly-0.1.0-SNAPSHOT.jar \
             --datasets "file://${PWD}/conf/datasets.json" \
             --credentials "file://${PWD}/conf/credentials.json"
```

## Build

To build you can just run 

```bash
./sbt assembly
```

## Args

```bash
geotrellis-integration-tests 0.1.0-SNAPSHOT
Usage: geotrellis-integration-tests [options]

  --datasets <value>
        datasets is a non-empty String property
  --credentials <value>
        credentials is a non-empty String property
  --help
        prints this usage text
```

## License

* Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
