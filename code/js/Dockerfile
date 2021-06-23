FROM node:16-alpine

WORKDIR /app

COPY ./node_modules ./node_modules
COPY ./public ./public
COPY ./dist ./dist
COPY ./views ./views

EXPOSE 80

ENTRYPOINT [ "node", "./dist/index.js" ]