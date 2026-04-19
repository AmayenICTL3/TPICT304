# API bancaire JavaScript

Version JavaScript simple du projet bancaire, basee sur le module `http` natif de Node.js.

## Lancement

```bash
cd api-javascript
node server.js 8003
```

ou

```bash
cd api-javascript
npm start
```

## Accès

- API : `http://127.0.0.1:8003`
- Swagger : `http://127.0.0.1:8003/docs`

## Endpoints

- `GET /`
- `POST /accounts`
- `GET /accounts`
- `GET /accounts/{account_id}`
- `POST /accounts/{account_id}/deposit`
- `POST /accounts/{account_id}/withdraw`
- `GET /accounts/{account_id}/transactions`

## Limite

Les donnees sont conservees en memoire pendant l'execution du serveur.
