FROM node:14 as build-stage

COPY . /app

RUN cd /app && \
    npm install && \
    npm run build

FROM nginx:latest as production-stage

COPY --from=build-stage /app/build /usr/share/nginx/html

EXPOSE 80
EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]
