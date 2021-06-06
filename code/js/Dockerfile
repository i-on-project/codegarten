FROM node:16-alpine

WORKDIR /app

COPY ./node_modules ./node_modules
COPY ./public ./public
COPY secrets ./secrets
COPY ./dist ./dist
COPY ./views ./views

ENV CG_WEB_CIPHER_KEY_PATH=secrets/cipher-key.txt

EXPOSE 80

ENTRYPOINT [ "node", "./dist/index.js" ]