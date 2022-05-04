FROM nccu/red-build as build-stage

FROM nccu/jdk11 as production-stage

ARG MODULE_NAME
ARG PORT

COPY --from=build-stage /app/$MODULE_NAME/build/libs/    /app/
COPY deploy/config/$MODULE_NAME/ /app/

EXPOSE $PORT

ENV ARTIFACT_NAME=$MODULE_NAME.jar

CMD cd /app && java -jar $ARTIFACT_NAME
