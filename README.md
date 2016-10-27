# [GeoTrellis](github.com/geotrellis/geotrellis) integration tests tool

[![Build Status](https://api.travis-ci.org/geotrellis/geotrellis-integration-tests-tool.svg)](http://travis-ci.org/geotrellis/geotrellis-integration-tests-tool) [![Join the chat at https://gitter.im/geotrellis/geotrellis](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/geotrellis/geotrellis?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Simple run:

```bash
spark-submit ${PWD}/target/scala-2.11/geotrellis-integration-tests-assembly-1.0.0-SNAPSHOT.jar \
             --datasets "file://${PWD}/conf/datasets.json" \
             --backend-profiles "file://${PWD}/conf/backend-profiles.json"
```

Run with custom environment variables (in this case with S3Appender)

```bash
spark-submit --conf spark.driver.extraJavaOptions="-Dlog4j.configuration=file://${PWD}/conf/log4j.properties" \
             --conf spark.executor.extraJavaOptions="-Dlog4j.configuration=file://${PWD}/conf/log4j.properties" \
             ${PWD}/target/scala-2.11/geotrellis-integration-tests-assembly-1.0.0-SNAPSHOT.jar \
               --datasets "file://${PWD}/conf/datasets.json" \
               --backend-profiles "file://${PWD}/conf/backend-profiles.json"
```

## Build

```bash
./sbt assembly
```

## Args

```bash
geotrellis-integration-tests 1.0.0-SNAPSHOT
Usage: geotrellis-integration-tests [options]

  --datasets <value>
        datasets is a non-empty String property
  --backend-profiles <value>
        backend-profiles is a non-empty String property
  --help
        prints this usage text
```

## License

* Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
