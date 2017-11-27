# Google App Engine

## Serve locally

```
docker run --rm -it -h localhost -v ~/.m2:/root/.m2 -v $(pwd):/usr/src/app -v ~/.config/gcloud:/root/.config/gcloud -w /usr/src/app -p 8080:8080 zenika/alpine-appengine-java
```

## Deploy

```
docker run --rm -it -h localhost -v ~/.m2:/root/.m2 -v $(pwd):/usr/src/app -v ~/.config/gcloud:/root/.config/gcloud -w /usr/src/app -p 8080:8080 zenika/alpine-appengine-java mvn -D.app.version=v5 appengine:deploy
```

URL: `https://imt-2017-11-clement.appspot.com`

