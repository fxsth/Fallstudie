./gradlew downloadApolloSchema \
  -Pcom.apollographql.apollo.endpoint=http://roman.technology:8080/v1/graphql --header 'X-Hasura-Admin-Secret: Passwort'\
  -Pcom.apollographql.apollo.schema=src/main/graphql/com/example/schema.json