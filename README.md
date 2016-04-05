# GeoTrellis integration tests tool

```bash
spark-submit ${PWD}/target/scala-2.10/geotrellis-integration-tests-assembly-0.1.0-SNAPSHOT.jar \
             --datasets "file:///${PWD}/conf/datasets.json" \
             --credentials "file:///${PWD}/conf/credentials.json"
```
